@file:OptIn(ExperimentalMaterial3Api::class)

package com.fm.products.ui.screens.bottomsheet

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.fm.products.ui.models.ImageFilter
import com.fm.products.ui.utils.processors.FilterImageProcessor
import com.fm.products.R
import com.fm.products.ui.theme.PhotoEditorTheme

@Composable
fun SelectImageFilterBottomSheet(
    onDismissRequest: () -> Unit,
    targetImage: ImageBitmap,
    onSelectFilter: (ImageFilter) -> Unit,
) {
    val state = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        containerColor = MaterialTheme.colorScheme.background,
        sheetState = state,
        windowInsets = WindowInsets.ime
    ) {
        SelectImageFilterBottomSheetContent(
            onSelectFilter = {
                onSelectFilter(it)
                onDismissRequest()
            },
            targetImage = targetImage,
        )
    }
}

@Composable
private fun SelectImageFilterBottomSheetContent(
    onSelectFilter: (ImageFilter) -> Unit,
    targetImage: ImageBitmap,
) {

    val filters = ImageFilter.entries.toTypedArray()

    LazyVerticalGrid(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(bottom = 42.dp),
        contentPadding = PaddingValues(24.dp),
        columns = GridCells.Fixed(2)
    ) {

        items(filters) {
            SelectImageFilterItem(
                targetImage = targetImage,
                imageFilter = it,
                onClick = onSelectFilter,
            )
        }
    }
}

@Composable
private fun SelectImageFilterItem(
    targetImage: ImageBitmap,
    imageFilter: ImageFilter,
    onClick: (ImageFilter) -> Unit,
) {

    var image by remember { mutableStateOf(targetImage) }

    LaunchedEffect(imageFilter) {
        val processor = FilterImageProcessor(imageFilter)
        image = processor.process(targetImage)
    }

    Box(
        modifier = Modifier
            .border(2.dp, MaterialTheme.colorScheme.background)
            .clickable { onClick(imageFilter) },
    ) {

        Image(
            bitmap = image,
            contentDescription = "",
        )
    }
}

@PreviewLightDark
@Composable
private fun SelectToolsBottomSheetPreview() {
    val resource = LocalContext.current.resources
    val bitmap = BitmapFactory.decodeResource(resource, R.drawable.preview_img)
    PhotoEditorTheme {
        SelectImageFilterBottomSheetContent(
            onSelectFilter = { /* no-op */ },
            targetImage = bitmap.asImageBitmap()
        )
    }
}
