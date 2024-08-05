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
import com.fm.products.ui.models.CircleSelectionPosition
import com.fm.products.ui.models.RectangleSelectionPosition
import com.fm.products.ui.models.SelectionTool
import com.fm.products.ui.models.Tools
import com.fm.products.ui.screens.components.HomeToolbar
import com.fm.products.ui.theme.PhotoEditorTheme
import com.fm.products.ui.utils.calculateDefaultCircleSelectionPosition
import com.fm.products.ui.utils.calculateDefaultRectangleSelectionPosition
import com.fm.products.ui.utils.calculateDrawImageOffset
import com.fm.products.ui.utils.calculateDrawImageSize
import com.fm.products.ui.utils.drawCircleSelection
import com.fm.products.ui.utils.drawRectangleSelection
import com.fm.products.ui.utils.emptyIntOffset
import com.fm.products.ui.utils.emptyIntSize
import com.fm.products.ui.utils.isEmpty
import com.fm.products.ui.utils.motions.CircleSelectionMotionHandler
import com.fm.products.ui.utils.motions.MotionHandler
import com.fm.products.ui.utils.motions.RectangleCropper
import com.fm.products.ui.utils.motions.RectangleSelectionMotionHandler
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
        mutableStateOf(Tools.RectangleSelection)
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
    selectedTool: Tools,
) {
    val context = LocalContext.current

    val image = remember { imageUri.toImageBitmap(context) }

    var outputImage: Bitmap? = remember { null }

    Box {
        HomeCanvas(
            selectedTool = selectedTool,
            image = image,
            drawOnOutputBitmap = { tools, canvasSize, imageOffset ->
                when(tools) {
                    is RectangleSelectionPosition -> {
                        outputImage = RectangleCropper(tools, image, canvasSize, imageOffset).crop()
                    }
                }
            }
        )

        ExportButton(
            selectedTool = selectedTool,
            onClick = {
                outputImage?.saveToDisk()?.let {
                    Toast.makeText(context, "Picture saved", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .padding(40.dp)
                .align(Alignment.BottomCenter),
        )
    }
}

@Composable
private fun HomeCanvas(
    selectedTool: Tools,
    image: ImageBitmap,
    drawOnOutputBitmap: (SelectionTool, Size, IntOffset) -> Unit,
) {
    var rectangleSelectionPos by remember { mutableStateOf(RectangleSelectionPosition()) }

    var circleSelectionPos by remember { mutableStateOf(CircleSelectionPosition()) }

    var imagePosition: IntOffset by remember { mutableStateOf(emptyIntOffset()) }

    var imageSize: IntSize by remember { mutableStateOf(emptyIntSize()) }

    val motionHandler: MotionHandler by remember(selectedTool) {
        val motionHandler = when (selectedTool) {
            Tools.CircleSelection -> {
                CircleSelectionMotionHandler(
                    circleSelectionPosition = circleSelectionPos,
                    onUpdateCircleSelectionPosition = { circleSelectionPos = it },
                    imagePosition = imagePosition,
                    imageSize = imageSize,
                )
            }

            else -> {
                RectangleSelectionMotionHandler(
                    rectangleSelectionPosition = rectangleSelectionPos,
                    onUpdateRectangleSelectionPosition = { rectangleSelectionPos = it },
                    imagePosition = imagePosition,
                    imageSize = imageSize,
                )
            }
        }
        mutableStateOf(motionHandler)
    }

    when (val handler = motionHandler) {
        is CircleSelectionMotionHandler -> {
            LaunchedEffect(circleSelectionPos, imagePosition, imageSize) {
                handler.circleSelectionPosition = circleSelectionPos
                handler.imageSize = imageSize
                handler.imagePosition = imagePosition
            }
        }

        is RectangleSelectionMotionHandler -> {
            LaunchedEffect(rectangleSelectionPos, imagePosition, imageSize) {
                handler.rectangleSelectionPosition = rectangleSelectionPos
                handler.imageSize = imageSize
                handler.imagePosition = imagePosition
            }
        }
    }

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .padding(4.dp)
            .pointerInteropFilter { motionHandler.handleMotion(it) },
    ) {

        val drawSize = calculateDrawImageSize(
            canvasWidth = size.width,
            canvasHeight = size.height,
            imageWidth = image.width.toFloat(),
            imageHeight = image.height.toFloat(),
        )

        val drawOffset = calculateDrawImageOffset(
            canvasWidth = size.width,
            canvasHeight = size.height,
            drawImageSize = drawSize
        )
        if (imagePosition.isEmpty()) {
            imagePosition = drawOffset
        }

        if (imageSize.isEmpty()) {
            imageSize = drawSize
        }

        if (rectangleSelectionPos.isEmpty()) {
            rectangleSelectionPos = calculateDefaultRectangleSelectionPosition(drawSize, drawOffset)
        }

        if (circleSelectionPos.isEmpty()) {
            circleSelectionPos = calculateDefaultCircleSelectionPosition(drawSize, center)
        }

        drawImage(
            image = image,
            dstSize = drawSize,
            dstOffset = drawOffset
        )

        when (selectedTool) {
            Tools.LassoSelection -> {
                /* no-op */
            }

            Tools.RectangleSelection -> {
                drawRectangleSelection(rectangleSelectionPos)
                drawOnOutputBitmap(rectangleSelectionPos, size, drawOffset)
            }

            Tools.CircleSelection -> {
                drawCircleSelection(circleSelectionPos)
                drawOnOutputBitmap(circleSelectionPos, size, drawOffset)
            }

            Tools.None -> {
                /* no-op */
            }
        }
    }
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
