package com.fm.products.ui.utils.helpers

import android.content.Context
import android.graphics.Bitmap
import dev.eren.removebg.RemoveBg
import kotlinx.coroutines.flow.first

object RemoveBackgroundHelper {

    suspend fun getResult(image: Bitmap, ctx: Context): Bitmap? {
        val remover = RemoveBg(ctx)
        return remover.clearBackground(image).first()
    }
}