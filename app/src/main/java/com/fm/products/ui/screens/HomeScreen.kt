package com.fm.products.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toOffset
import androidx.compose.ui.unit.toSize
import com.fm.products.R
import com.fm.products.ui.components.ToolButton
import com.fm.products.ui.models.Tools
import com.fm.products.ui.theme.PhotoEditorTheme
import com.fm.products.ui.theme.PurpleGrey80
import com.fm.products.ui.utils.buttonStateColor
import com.fm.products.ui.utils.calculateDrawImageOffset
import com.fm.products.ui.utils.calculateDrawImageSize
import com.fm.products.ui.utils.dashStyle
import com.fm.products.ui.utils.leftBottomCircleOffset
import com.fm.products.ui.utils.leftTopCircleOffset
import com.fm.products.ui.utils.rightBottomCircleOffset
import com.fm.products.ui.utils.rightTopCircleOffset
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
        mutableStateOf(Tools.CircleSelection)
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
                ImageEditState(it)
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
private fun ImageEditState(imageUri: Uri) {
    val context = LocalContext.current
    val image = remember {
        imageUri.toImageBitmap(context)
    }

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .padding(4.dp),
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

        drawImage(
            image = image,
            dstSize = drawSize,
            dstOffset = drawOffset
        )

        drawRect(
            color = Color.LightGray,
            topLeft = drawOffset.toOffset(),
            style = dashStyle(),
            size = drawSize.toSize()
        )

        drawCircle(
            color = Color.Magenta,
            radius = 20f,
            center = leftTopCircleOffset(drawOffset)
        )


        drawCircle(
            color = Color.Magenta,
            radius = 20f,
            center = rightTopCircleOffset(drawOffset, drawSize)
        )

        drawCircle(
            color = Color.Magenta,
            radius = 20f,
            center = leftBottomCircleOffset(drawOffset, drawSize)
        )

        drawCircle(
            color = Color.Magenta,
            radius = 20f,
            center = rightBottomCircleOffset(drawOffset, drawSize)
        )
    }

}


@Composable
private fun HomeToolbar(
    selectedTool: Tools,
    onToolsChanged: (Tools) -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(12.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = PurpleGrey80,
        )
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = 12.dp,
                vertical = 12.dp
            ),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ToolButton(
                modifier = Modifier,
                icon = painterResource(R.drawable.ic_lasso_select),
                isEnable = selectedTool == Tools.LassoSelection,
                onClick = { onToolsChanged(Tools.LassoSelection) }
            )
            Spacer(Modifier.width(12.dp))
            ToolButton(
                modifier = Modifier,
                icon = painterResource(R.drawable.ic_select_square),
                isEnable = selectedTool == Tools.RectangleSelection,
                onClick = { onToolsChanged(Tools.RectangleSelection) }
            )
            Spacer(Modifier.width(12.dp))
            ToolButton(
                modifier = Modifier,
                icon = painterResource(R.drawable.ic_select_circle),
                isEnable = selectedTool == Tools.CircleSelection,
                onClick = { onToolsChanged(Tools.CircleSelection) }
            )

            Spacer(Modifier.width(12.dp))
            Button(
                onClick = { onToolsChanged(Tools.None) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Max),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = buttonStateColor(selectedTool == Tools.None),
                ),
            ) {
                Text(
                    text = "None",
                    color = Color.Black,
                )
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
