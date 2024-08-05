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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.fm.products.ui.components.ExportButton
import com.fm.products.ui.models.CircleSelectionState
import com.fm.products.ui.models.RectangleSelectionState
import com.fm.products.ui.models.SelectionTool
import com.fm.products.ui.screens.components.HomeToolbar
import com.fm.products.ui.theme.PhotoEditorTheme
import com.fm.products.ui.utils.calculateDefaultCircleSelectionPosition
import com.fm.products.ui.utils.calculateDefaultRectangleSelectionPosition
import com.fm.products.ui.utils.calculateDrawImageOffset
import com.fm.products.ui.utils.calculateDrawImageSize
import com.fm.products.ui.utils.createMotionHandler
import com.fm.products.ui.utils.cropper.RectangleCropper
import com.fm.products.ui.utils.drawCircleSelection
import com.fm.products.ui.utils.drawRectangleSelection
import com.fm.products.ui.utils.emptyIntOffset
import com.fm.products.ui.utils.emptyIntSize
import com.fm.products.ui.utils.emptySize
import com.fm.products.ui.utils.isEmpty
import com.fm.products.ui.utils.motions.CircleSelectionMotionHandler
import com.fm.products.ui.utils.motions.MotionHandler
import com.fm.products.ui.utils.motions.RectangleSelectionMotionHandler
import com.fm.products.ui.utils.orFalse
import com.fm.products.ui.utils.saveToDisk
import com.fm.products.ui.utils.toImageBitmap

@Composable
fun HomeScreen() {
    HomeScreenContent()
}

@Composable
private fun HomeScreenContent() {
    var image: Uri? by remember {
        mutableStateOf(null)
    }

    val picker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { image = it }
    )

    var selectedTool by remember {
        mutableStateOf(SelectionTool.RectangleSelection)
    }


    Column {
        HomeToolbar(
            selectedTool = selectedTool,
            onToolsChanged = { selectedTool = it },
        )

        if (image == null) {
            SelectImageState(
                onClickSelectImage = {
                    picker.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
            )
        } else {
            image?.let {
                ImageEditState(
                    imageUri = it,
                    selectedTool = selectedTool,
                )
            }
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
    var rectangleSelectionState by remember { mutableStateOf(RectangleSelectionState()) }

    var circleSelectionState by remember { mutableStateOf(CircleSelectionState()) }

    var imagePosition: IntOffset by remember { mutableStateOf(emptyIntOffset()) }

    var imageSize: IntSize by remember { mutableStateOf(emptyIntSize()) }

    var canvasSize: Size by remember { mutableStateOf(emptySize()) }

    val motionHandler: MotionHandler<*>? by remember(selectedTool) {
        val motionHandler = createMotionHandler(
            selectionTool = selectedTool,
            circleSelectionState = circleSelectionState,
            onUpdateCircleSelectionState = { circleSelectionState = it },
            rectangleSelectionState = rectangleSelectionState,
            onUpdateRectangleSelectionState = { rectangleSelectionState = it  },
            imageSize = imageSize,
            imagePosition = imagePosition,
        )
        mutableStateOf(motionHandler)
    }

    when (val handler = motionHandler) {
        is CircleSelectionMotionHandler -> {
            LaunchedEffect(circleSelectionState, imagePosition, imageSize) {
                handler.update(circleSelectionState, imageSize, imagePosition)
            }
        }

        is RectangleSelectionMotionHandler -> {
            LaunchedEffect(rectangleSelectionState, imagePosition, imageSize) {
                handler.update(rectangleSelectionState, imageSize, imagePosition)
            }
        }
    }


    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .padding(4.dp)
            .pointerInteropFilter { motionHandler?.handleMotion(it).orFalse() },
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

        if (rectangleSelectionState.isEmpty()) {
            rectangleSelectionState =
                calculateDefaultRectangleSelectionPosition(imageSize, imagePosition)
        }

        if (circleSelectionState.isEmpty()) {
            circleSelectionState = calculateDefaultCircleSelectionPosition(imageSize, center)
        }

        drawImage(
            image = image,
            dstSize = imageSize,
            dstOffset = imagePosition
        )

        when (selectedTool) {
            SelectionTool.RectangleSelection -> {
                drawRectangleSelection(rectangleSelectionState)
            }

            SelectionTool.CircleSelection -> {
                drawCircleSelection(circleSelectionState)
            }

            SelectionTool.None -> {
                /* no-op */
            }
        }
    }

    ExportButton(
        selectedTool = selectedTool,
        onClick = {
            when (selectedTool) {
                SelectionTool.RectangleSelection -> {
                    val bitmap = RectangleCropper(
                        rectangleSelectionState = rectangleSelectionState,
                        image = image,
                        canvasSize = canvasSize,
                        imageOffset = imagePosition
                    ).crop()
                    drawOnOutputBitmap(bitmap)
                }

                SelectionTool.CircleSelection -> {
                    /* no-op */
                }

                SelectionTool.None -> {
                    /* no-op */
                }
            }
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
