package com.fm.products.ui.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.IntOffset
import com.fm.products.ui.models.CircleSelectionState
import com.fm.products.ui.models.LassoSelectionState
import com.fm.products.ui.models.RectangleSelectionState
import com.fm.products.ui.models.SelectionState
import com.fm.products.ui.utils.cropper.CircleCropper
import com.fm.products.ui.utils.cropper.ImageCropper
import com.fm.products.ui.utils.cropper.LassoCropper
import com.fm.products.ui.utils.cropper.RectangleCropper
import com.fm.products.ui.utils.selections.drawLassoSelection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

fun DrawScope.drawSelectionByState(selectionState: SelectionState) {
    when (selectionState) {
        is RectangleSelectionState -> {
            drawRectangleSelection(selectionState)
        }

        is CircleSelectionState -> {
            drawCircleSelection(selectionState)
        }

        is LassoSelectionState -> {
            drawLassoSelection(selectionState)
        }
    }
}

fun Bitmap.saveToDisk() {
    val file = File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
        "screenshot-${System.currentTimeMillis()}.png"
    )

    compress(Bitmap.CompressFormat.PNG, 100, FileOutputStream(file))
}


suspend fun Context.saveToDiskAndToast(
    image: Bitmap?,
): Unit = withContext(Dispatchers.Main) {
    image?.saveToDisk()?.let {
        Toast.makeText(this@saveToDiskAndToast, "Picture saved", Toast.LENGTH_SHORT).show()
    }
}

fun Uri.toImageBitmap(context: Context): ImageBitmap {
    val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, this))
    } else {
        @Suppress("DEPRECATION")
        MediaStore.Images.Media.getBitmap(context.contentResolver, this)
    }
    return bitmap.asImageBitmap()
}
