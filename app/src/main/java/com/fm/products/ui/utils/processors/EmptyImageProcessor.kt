package com.fm.products.ui.utils.processors

import androidx.compose.ui.graphics.ImageBitmap

class EmptyImageProcessor : ImageProcessor {

    override suspend fun process(image: ImageBitmap): ImageBitmap? = null
}
