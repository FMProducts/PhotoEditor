@file:OptIn(ExperimentalComposeUiApi::class)

package com.fm.products.ui.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fm.products.ui.components.ExportButton
import com.fm.products.ui.models.GraphicTool
import com.fm.products.ui.screens.bottomsheet.SelectImageFilterBottomSheet
import com.fm.products.ui.screens.bottomsheet.SelectToolsBottomSheet
import com.fm.products.ui.screens.components.HomeToolbar
import com.fm.products.ui.theme.PhotoEditorTheme
import com.fm.products.ui.utils.calculateDrawImageOffset
import com.fm.products.ui.utils.calculateDrawImageSize
import com.fm.products.ui.utils.cropper.cropImage
import com.fm.products.ui.utils.drawSelectionByState
import com.fm.products.ui.utils.emptyIntOffset
import com.fm.products.ui.utils.emptyIntSize
import com.fm.products.ui.utils.emptySize
import com.fm.products.ui.utils.isEmpty
import com.fm.products.ui.utils.motions.MagneticLassoSelectionMotionHandler
import com.fm.products.ui.utils.motions.MotionHandler
import com.fm.products.ui.utils.motions.createMotionHandler
import com.fm.products.ui.utils.processors.ImageProcessor
import com.fm.products.ui.utils.processors.createImageProcessor
import com.fm.products.ui.utils.saveToDiskAndToast
import com.fm.products.ui.utils.toImageBitmap
import kotlinx.coroutines.launch

@Composable
fun HomeScreen() {
    HomeScreenContent()
}

@Composable
private fun HomeScreenContent(
    viewModel: HomeViewModel = viewModel(),
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    val picker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = viewModel::setImageUri
    )
    val sourceImage = uiState.imageUri?.toImageBitmap(context)

    Column {
        HomeToolbar(
            selectedTool = uiState.graphicTool,
            onClickMore = { viewModel.setSelectToolsBottomSheetVisibility(true) },
        )

        if (sourceImage == null) {
            SelectImageState(
                onClickSelectImage = {
                    picker.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
            )
        } else {
            ImageEditState(
                sourceImage = sourceImage,
                uiState = uiState,
                changeProgressState = viewModel::changeProgressState
            )
        }
    }

    if (uiState.isShowSelectToolsBottomSheet) {
        SelectToolsBottomSheet(
            onDismissRequest = { viewModel.setSelectToolsBottomSheetVisibility(false) },
            onSelectTool = viewModel::updateSelectionTool,
        )
    }

    if (uiState.isShowFilterBottomSheet && sourceImage != null) {
        SelectImageFilterBottomSheet(
            onDismissRequest = { viewModel.setFilterBottomSheetVisibility(false) },
            targetImage = sourceImage,
            onSelectFilter = viewModel::updateImageFilter,
        )
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
    sourceImage: ImageBitmap,
    uiState: HomeViewModel.UiState,
    changeProgressState: (Boolean) -> Unit,
) {

    Box(
        contentAlignment = Alignment.Center,
    ) {
        HomeCanvas(
            selectedTool = uiState.graphicTool,
            sourceImage = sourceImage,
            changeProgressState = changeProgressState,
        )
        if (uiState.isProgress) {
            HomeProgressBar()
        }
    }
}

@Composable
private fun HomeProgressBar() {

    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.5f),
        ),
    ) {
        Box(
            modifier = Modifier.padding(14.dp),
        ) {
            CircularProgressIndicator()
        }
    }
}

@Composable
private fun HomeCanvas(
    selectedTool: GraphicTool,
    sourceImage: ImageBitmap,
    changeProgressState: (Boolean) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    var image: ImageBitmap by remember {
        mutableStateOf(sourceImage)
    }

    var imagePosition: IntOffset by remember { mutableStateOf(emptyIntOffset()) }

    var imageSize: IntSize by remember { mutableStateOf(emptyIntSize()) }

    var canvasSize: Size by remember { mutableStateOf(emptySize()) }

    val motionHandler: MotionHandler by remember(selectedTool, imageSize) {
        val motionHandler = createMotionHandler(
            graphicTool = selectedTool,
            imageSize = imageSize,
            imagePosition = imagePosition,
            canvasSize = canvasSize,
            sourceImage = image,
            coroutineScope = coroutineScope,
            context = context,
        )
        mutableStateOf(motionHandler)
    }

    val imageProcessor: ImageProcessor by remember(selectedTool) {
        mutableStateOf(
            createImageProcessor(selectedTool, context)
        )
    }

    val selectionState by motionHandler.selectionState.collectAsState()

    LaunchedEffect(imagePosition, imageSize) {
        motionHandler.update(imageSize, imagePosition)
    }

    LaunchedEffect(motionHandler) {
        when (val handler = motionHandler) {
            is MagneticLassoSelectionMotionHandler -> {
                handler.changeProgressBarState = changeProgressState
            }
        }
    }

    LaunchedEffect(imageProcessor) {
        try {
            changeProgressState(true)
            val img = imageProcessor.process(sourceImage)
            img?.let { image = it }
            changeProgressState(false)
        } catch (e: Exception) {
            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Gray)
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
        isVisible = selectedTool != GraphicTool.None,
        onClick = {
            coroutineScope.launch {
                changeProgressState(true)
                val img = cropImage(image, selectedTool, selectionState, canvasSize, imagePosition)
                context.saveToDiskAndToast(img)
                changeProgressState(false)
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
