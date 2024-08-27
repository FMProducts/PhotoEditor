package com.fm.products.ui.utils

import android.graphics.Bitmap
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.MatOfPoint
import org.opencv.core.Point
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc


/**
 *  https://fritz.ai/image-effects-for-android-using-opencv/
 *  https://github.com/ahmedfgad/AndroidOpenCVImageEffects
 */
private fun createLUT(numColors: Int): Mat {
    // When numColors=1 the LUT will only have 1 color which is black.
    if (numColors < 0 || numColors > 255) {
        throw IllegalArgumentException("Invalid Number of Colors. It must be between 0 and 256 inclusive.")
    }

    val lookupTable = Mat.zeros(Size(1.0, 256.0), CvType.CV_8UC1)
    var startIdx = 0

    var x = 0.0
    while (x < 256) {
        lookupTable.put(x.toInt(), 0, x)

        for (y in startIdx until x.toInt()) {
            if (lookupTable.get(y, 0)[0] == 0.0) {
                lookupTable.put(y, 0, *lookupTable.get(x.toInt(), 0))
            }
        }
        startIdx = x.toInt()

        x += 256.0 / numColors
    }
    return lookupTable
}

fun reduceColors(img: Mat, red: Int, green: Int, blue: Int): Mat {
    val redLut = createLUT(red)
    val greenLut = createLUT(green)
    val blueLut = createLUT(blue)

    val bgr = ArrayList<Mat>(3)
    Core.split(img, bgr)

    Core.LUT(bgr[0], redLut, bgr[0])
    Core.LUT(bgr[1], greenLut, bgr[1])
    Core.LUT(bgr[2], blueLut, bgr[2])

    Core.merge(bgr, img)
    return img
}

fun processToClearCanny(src: Mat): Mat {
    val gray = Mat()
    Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY)
    Imgproc.GaussianBlur(gray, gray, Size(13.0, 13.0), 0.0)

    val canny = Mat()
    Imgproc.Canny(gray, canny, 50.0, 100.0)
    return canny
}

fun processToSepia(src: Mat): Mat {
    val result = Mat()
    src.copyTo(result)

    // Fill sepia kernel
    val mSepiaKernel = Mat(4, 4, CvType.CV_32F)
    mSepiaKernel.put(0, 0,  /* R */0.189, 0.769, 0.393, 0.0)
    mSepiaKernel.put(1, 0,  /* G */0.168, 0.686, 0.349, 0.0)
    mSepiaKernel.put(2, 0,  /* B */0.131, 0.534, 0.272, 0.0)
    mSepiaKernel.put(3, 0,  /* A */0.000, 0.000, 0.000, 1.0)

    Core.transform(result, result, mSepiaKernel)
    return result
}

fun processToPixelize(src: Mat): Mat {
    val result = Mat()
    src.copyTo(result)
    Imgproc.resize(result, result, Size(), 0.1, 0.1, Imgproc.INTER_NEAREST)
    Imgproc.resize(result, result, src.size(), 0.0, 0.0, Imgproc.INTER_NEAREST)
    return result
}

fun processToGaussianBlur(src: Mat, blurSize: Double = 21.0): Mat {
    val blur = Mat()
    src.copyTo(blur)
    Imgproc.GaussianBlur(blur, blur, Size(blurSize, blurSize), 0.0)
    return blur
}

fun processToCartoon(src: Mat, numRed: Int = 80, numGreen: Int = 15, numBlue: Int = 10): Mat {
    Imgproc.cvtColor(src, src, Imgproc.COLOR_BGRA2BGR)
    val reducedColors = reduceColors(src, numRed, numGreen, numBlue)

    val result = Mat()
    Imgproc.cvtColor(src, result, Imgproc.COLOR_BGR2GRAY)
    Imgproc.medianBlur(result, result, 45)

    Imgproc.adaptiveThreshold(
        result,
        result,
        255.0,
        Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,
        Imgproc.THRESH_BINARY,
        15,
        2.0
    )

    Imgproc.cvtColor(result, result, Imgproc.COLOR_GRAY2BGR)

    Core.bitwise_and(reducedColors, result, result)
    return result
}

fun processToLight(src: Mat): Mat {
    val dst = Mat()
    src.copyTo(dst)
    Core.addWeighted(src, 1.0, dst, 0.5, 0.5, dst)
    return dst
}

fun processToColorMap(src: Mat, colorMap: Int): Mat {
    val dst = Mat()
    Imgproc.cvtColor(src, dst, Imgproc.COLOR_RGBA2BGR)
    Imgproc.applyColorMap(dst, dst, colorMap)
    return dst
}

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

fun Mat.toBitmap(): Bitmap {
    val bitmap = Bitmap.createBitmap(cols(), rows(), Bitmap.Config.ARGB_8888)
    Utils.matToBitmap(this, bitmap)
    return bitmap
}

fun ArrayList<MatOfPoint>.findMaxByArea(): MatOfPoint = this.maxBy { Imgproc.contourArea(it) }