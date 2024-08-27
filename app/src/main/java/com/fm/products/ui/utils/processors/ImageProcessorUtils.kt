package com.fm.products.ui.utils.processors

import android.content.Context
import android.graphics.Bitmap
import com.fm.products.ui.models.GraphicTool
import com.fm.products.ui.models.OtherGraphicTool
import dev.eren.removebg.RemoveBg
import kotlinx.coroutines.flow.first

fun createImageProcessor(
    graphicTool: GraphicTool,
    context: Context,
): ImageProcessor {
    return when (graphicTool) {
        OtherGraphicTool.BackgroundRemover -> {
            RemoveBackgroundProcessor(context)
        }

        else -> {
            EmptyImageProcessor()
        }
    }
}

suspend fun removeBackground(bitmap: Bitmap, context: Context) : Bitmap? {
    val remover = RemoveBg(context)
    return remover.clearBackground(bitmap).first()
}
