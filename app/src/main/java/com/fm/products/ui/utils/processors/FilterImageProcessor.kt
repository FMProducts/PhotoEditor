package com.fm.products.ui.utils.processors

import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.fm.products.ui.models.ImageFilter
import com.fm.products.ui.utils.processToCartoon
import com.fm.products.ui.utils.processToClearCanny
import com.fm.products.ui.utils.processToColorMap
import com.fm.products.ui.utils.processToGaussianBlur
import com.fm.products.ui.utils.processToGray
import com.fm.products.ui.utils.processToLight
import com.fm.products.ui.utils.processToPixelize
import com.fm.products.ui.utils.processToSepia
import com.fm.products.ui.utils.toBitmap
import com.fm.products.ui.utils.toMat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.opencv.android.OpenCVLoader
import org.opencv.imgproc.Imgproc

class FilterImageProcessor(
    private val imageFilter: ImageFilter,
) : ImageProcessor {

    init {
        OpenCVLoader.initLocal()
    }

    override suspend fun process(image: ImageBitmap): ImageBitmap =
        withContext(Dispatchers.IO) {
            val img = Bitmap.createScaledBitmap(
                image.asAndroidBitmap(),
                image.width / 2,
                image.height / 2,
                false
            ).asImageBitmap()

            when (imageFilter) {
                ImageFilter.None -> image
                ImageFilter.GaussianBlur -> processFilterGaussianBlur(img).asImageBitmap()
                ImageFilter.Gray -> processFilterGray(img).asImageBitmap()
                ImageFilter.Cartoon -> processFilterCartoon(img).asImageBitmap()
                ImageFilter.Light -> processFilterLight(img).asImageBitmap()
                ImageFilter.Canny -> processFilterCanny(img).asImageBitmap()
                ImageFilter.Sepia -> processFilterSepia(img).asImageBitmap()
                ImageFilter.Pixelize -> processFilterPixelize(img).asImageBitmap()
                ImageFilter.Summer -> processFilterSummer(img).asImageBitmap()
                ImageFilter.Turbo -> processFilterTurbo(img).asImageBitmap()
                ImageFilter.Ocean -> processFilterOcean(img).asImageBitmap()
                ImageFilter.Autumn -> processFilterAutumn(img).asImageBitmap()
                ImageFilter.Bone -> processFilterBone(img).asImageBitmap()
                ImageFilter.Hot -> processFilterHot(img).asImageBitmap()
                ImageFilter.Jet -> processFilterJet(img).asImageBitmap()
                ImageFilter.Inferno -> processFilterInferno(img).asImageBitmap()
                ImageFilter.Cool -> processFilterCool(img).asImageBitmap()
                ImageFilter.Plasma -> processFilterPlasma(img).asImageBitmap()
                ImageFilter.Rainbow -> processFilterRainbow(img).asImageBitmap()
                ImageFilter.Cividis -> processFilterCividis(img).asImageBitmap()
            }
        }

    private fun processFilterGray(image: ImageBitmap): Bitmap {
        val src = image.asAndroidBitmap().toMat()
        val gray = processToGray(src)
        return gray.toBitmap()
    }

    private fun processFilterGaussianBlur(image: ImageBitmap): Bitmap {
        val src = image.asAndroidBitmap().toMat()
        val blur = processToGaussianBlur(src)
        return blur.toBitmap()
    }

    private fun processFilterCartoon(image: ImageBitmap): Bitmap {
        val src = image.asAndroidBitmap().toMat()
        val cartoon = processToCartoon(src)
        return cartoon.toBitmap()
    }

    private fun processFilterLight(image: ImageBitmap): Bitmap {
        val src = image.asAndroidBitmap().toMat()
        val light = processToLight(src)
        return light.toBitmap()
    }

    private fun processFilterCanny(image: ImageBitmap): Bitmap {
        val src = image.asAndroidBitmap().toMat()
        val canny = processToClearCanny(src)
        return canny.toBitmap()
    }

    private fun processFilterSepia(image: ImageBitmap): Bitmap {
        val src = image.asAndroidBitmap().toMat()
        val sobel = processToSepia(src)
        return sobel.toBitmap()
    }

    private fun processFilterPixelize(image: ImageBitmap): Bitmap {
        val src = image.asAndroidBitmap().toMat()
        val pixelize = processToPixelize(src)
        return pixelize.toBitmap()
    }

    private fun processFilterSummer(image: ImageBitmap): Bitmap {
        val src = image.asAndroidBitmap().toMat()
        val result = processToColorMap(src, Imgproc.COLORMAP_SUMMER)
        return result.toBitmap()
    }

    private fun processFilterTurbo(image: ImageBitmap): Bitmap {
        val src = image.asAndroidBitmap().toMat()
        val result = processToColorMap(src, Imgproc.COLORMAP_TURBO)
        return result.toBitmap()
    }

    private fun processFilterOcean(image: ImageBitmap): Bitmap {
        val src = image.asAndroidBitmap().toMat()
        val result = processToColorMap(src, Imgproc.COLORMAP_OCEAN)
        return result.toBitmap()
    }

    private fun processFilterAutumn(image: ImageBitmap): Bitmap {
        val src = image.asAndroidBitmap().toMat()
        val result = processToColorMap(src, Imgproc.COLORMAP_AUTUMN)
        return result.toBitmap()
    }

    private fun processFilterBone(image: ImageBitmap): Bitmap {
        val src = image.asAndroidBitmap().toMat()
        val result = processToColorMap(src, Imgproc.COLORMAP_BONE)
        return result.toBitmap()
    }

    private fun processFilterHot(image: ImageBitmap): Bitmap {
        val src = image.asAndroidBitmap().toMat()
        val result = processToColorMap(src, Imgproc.COLORMAP_HOT)
        return result.toBitmap()
    }

    private fun processFilterJet(image: ImageBitmap): Bitmap {
        val src = image.asAndroidBitmap().toMat()
        val result = processToColorMap(src, Imgproc.COLORMAP_JET)
        return result.toBitmap()
    }

    private fun processFilterInferno(image: ImageBitmap): Bitmap {
        val src = image.asAndroidBitmap().toMat()
        val result = processToColorMap(src, Imgproc.COLORMAP_INFERNO)
        return result.toBitmap()
    }

    private fun processFilterCool(image: ImageBitmap): Bitmap {
        val src = image.asAndroidBitmap().toMat()
        val result = processToColorMap(src, Imgproc.COLORMAP_COOL)
        return result.toBitmap()
    }

    private fun processFilterPlasma(image: ImageBitmap): Bitmap {
        val src = image.asAndroidBitmap().toMat()
        val result = processToColorMap(src, Imgproc.COLORMAP_PLASMA)
        return result.toBitmap()
    }

    private fun processFilterRainbow(image: ImageBitmap): Bitmap {
        val src = image.asAndroidBitmap().toMat()
        val result = processToColorMap(src, Imgproc.COLORMAP_RAINBOW)
        return result.toBitmap()
    }

    private fun processFilterCividis(image: ImageBitmap): Bitmap {
        val src = image.asAndroidBitmap().toMat()
        val result = processToColorMap(src, Imgproc.COLORMAP_CIVIDIS)
        return result.toBitmap()
    }
}
