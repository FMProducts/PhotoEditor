package com.fm.products.ui.utils.processors

import android.content.Context
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap

class RemoveBackgroundProcessor(
    private val context: Context,
) : ImageProcessor {

    override suspend fun process(image: ImageBitmap): ImageBitmap {
        val bitmap = removeBackground(image.asAndroidBitmap(), context)
            ?: throwException()
        return bitmap.asImageBitmap()
    }

    private fun throwException(): Nothing {
        throw IllegalStateException("RemoveBackgroundProcessor result == null")
    }
}
