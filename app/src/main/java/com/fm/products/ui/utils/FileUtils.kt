package com.fm.products.ui.utils

import android.graphics.Bitmap
import android.os.Environment
import java.io.File
import java.io.FileOutputStream

fun Bitmap.saveToDisk() {
    val file = File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
        "screenshot-${System.currentTimeMillis()}.png"
    )

    compress(Bitmap.CompressFormat.PNG, 100, FileOutputStream(file))
}
