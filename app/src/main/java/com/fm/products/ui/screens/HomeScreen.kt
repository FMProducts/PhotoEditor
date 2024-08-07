@file:OptIn(ExperimentalComposeUiApi::class)

package com.fm.products.ui.screens

import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fm.products.ui.components.ExportButton
import com.fm.products.ui.models.SelectionTool
import com.fm.products.ui.screens.components.HomeToolbar
import com.fm.products.ui.theme.PhotoEditorTheme
import com.fm.products.ui.utils.calculateDrawImageOffset
import com.fm.products.ui.utils.calculateDrawImageSize
import com.fm.products.ui.utils.createMotionHandler
import com.fm.products.ui.utils.cropByState
import com.fm.products.ui.utils.drawSelectionByState
import com.fm.products.ui.utils.emptyIntOffset
import com.fm.products.ui.utils.emptyIntSize
import com.fm.products.ui.utils.emptySize
import com.fm.products.ui.utils.isEmpty
import com.fm.products.ui.utils.motions.MotionHandler
import com.fm.products.ui.utils.saveToDisk
import com.fm.products.ui.utils.toImageBitmap

@Composable
fun HomeScreen() {
    HomeScreenContent()
}

@Composable
private fun HomeScreenContent(
    viewModel: HomeViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    val picker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = viewModel::setImageUri
    )

    Column {
        HomeToolbar(
            selectedTool = uiState.selectionTool,
            onToolsChanged = viewModel::updateSelectionTool,
        )

        val uri = uiState.imageUri
        if (uri == null) {
            SelectImageState(
                onClickSelectImage = {
                    picker.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
            )
        } else {
            ImageEditState(
                imageUri = uri,
                selectedTool = uiState.selectionTool,
            )
        }
    }
}

@Composable
private fun SelectImageState(
    onClickSelectImage: () -> Unit,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize(),
    ) {
        OutlinedButton(
            onClick = onClickSelectImage,
        ) {
            Text(text = "Select image")
        }
    }
}

@Composable
private fun ImageEditState(
    imageUri: Uri,
    selectedTool: SelectionTool,
) {
    val context = LocalContext.current

    val image = remember { imageUri.toImageBitmap(context) }

    Box {
        HomeCanvas(
            selectedTool = selectedTool,
            image = image,
            drawOnOutputBitmap = { outputImage ->
                outputImage.saveToDisk().let {
                    Toast.makeText(context, "Picture saved", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }
}

@Composable
private fun HomeCanvas(
    selectedTool: SelectionTool,
    image: ImageBitmap,
    drawOnOutputBitmap: (Bitmap) -> Unit,
) {
    var imagePosition: IntOffset by remember { mutableStateOf(emptyIntOffset()) }

    var imageSize: IntSize by remember { mutableStateOf(emptyIntSize()) }

    var canvasSize: Size by remember { mutableStateOf(emptySize()) }

    val motionHandler: MotionHandler by remember(selectedTool, imageSize) {
        val motionHandler = createMotionHandler(
            selectionTool = selectedTool,
            imageSize = imageSize,
            imagePosition = imagePosition,
            canvasCenter = canvasSize.center,
        )
        mutableStateOf(motionHandler)
    }

    val selectionState by motionHandler.selectionState.collectAsState()

    LaunchedEffect(imagePosition, imageSize) {
        motionHandler.update(imageSize, imagePosition)
    }

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .padding(4.dp)
            .pointerInteropFilter {
                motionHandler.handleMotion(it)
            },
    ) {


        canvasSize = size

        if (imageSize.isEmpty()) {
            imageSize = calculateDrawImageSize(
                canvasWidth = size.width,
                canvasHeight = size.height,
                imageWidth = image.width.toFloat(),
                imageHeight = image.height.toFloat(),
            )
        }

        if (imagePosition.isEmpty()) {
            imagePosition = calculateDrawImageOffset(
                canvasWidth = size.width,
                canvasHeight = size.height,
                drawImageSize = imageSize
            )
        }

        drawImage(
            image = image,
            dstSize = imageSize,
            dstOffset = imagePosition
        )

        drawSelectionByState(selectionState)
    }

    ExportButton(
        isVisible = selectedTool != SelectionTool.None,
        onClick = {
            val bitmap = image.cropByState(
                selectionState = selectionState,
                canvasSize = canvasSize,
                imagePosition = imagePosition,
            )
            bitmap?.let(drawOnOutputBitmap)
        },
        modifier = Modifier.padding(40.dp)
    )
}

@Composable
@Preview
private fun HomeScreenContentPreview() {
    PhotoEditorTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            HomeScreenContent()
        }
    }
}
