package com.fm.products.ui.utils.processors

import androidx.compose.ui.graphics.ImageBitmap

interface ImageProcessor {

    suspend fun process(image: ImageBitmap) : ImageBitmap?
}
