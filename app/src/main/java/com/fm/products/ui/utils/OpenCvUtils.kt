package com.fm.products.ui.utils

import android.graphics.Bitmap
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.opencv.core.MatOfPoint
import org.opencv.core.Point
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc


fun processToCanny(gray: Mat, kernelSize: Double = 15.0): Mat {
    val edges = Mat()
    Imgproc.Canny(gray, edges, 100.0, 200.0)
    val kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, Size(kernelSize, kernelSize))
    Imgproc.dilate(edges, edges, kernel, Point(), 1)
    return edges
}


fun processToGray(src: Mat, blurSize: Double = 7.0): Mat {
    val gray = Mat()
    Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGRA2GRAY)
    Imgproc.GaussianBlur(gray, gray, Size(blurSize, blurSize), 0.0)
    return gray
}


fun finContours(
    imgMat: Mat,
    chainApprox: Int = Imgproc.CHAIN_APPROX_NONE,
    mode: Int = Imgproc.RETR_EXTERNAL,
): ArrayList<MatOfPoint> {
    val contours = ArrayList<MatOfPoint>()
    val hierarchy = Mat()
    Imgproc.findContours(imgMat, contours, hierarchy, mode, chainApprox)
    return contours
}

fun Bitmap.toMat(): Mat {
    val src = Mat()
    Utils.bitmapToMat(this, src)
    return src
}

fun ArrayList<MatOfPoint>.findMaxByArea() : MatOfPoint = this.maxBy { Imgproc.contourArea(it) }