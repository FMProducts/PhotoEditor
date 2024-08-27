package com.fm.products.ui.screens.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.fm.products.R
import com.fm.products.ui.components.ToolButton
import com.fm.products.ui.models.GraphicTool
import com.fm.products.ui.models.SelectionGraphicTool
import com.fm.products.ui.theme.PhotoEditorTheme
import com.fm.products.ui.theme.PurpleGrey80
import com.fm.products.ui.utils.buttonStateColor

@Composable
fun HomeToolbar(
    selectedTool: GraphicTool,
    onClickMore: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(12.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = PurpleGrey80,
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 12.dp,
                    vertical = 12.dp
                ),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {

            Button(
                onClick = onClickMore,
                modifier = Modifier
                    .weight(1f)
                    .height(IntrinsicSize.Max),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = buttonStateColor(selectedTool != GraphicTool.None),
                ),
            ) {
                Row {
                    Icon(
                        painter = painterResource(id = selectedTool.iconRes),
                        contentDescription = "",
                        modifier = Modifier.size(22.dp),
                        tint = Color.Black,
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = stringResource(id = selectedTool.nameRes),
                        textAlign = TextAlign.Center,
                        color = Color.Black,
                    )
                }
            }

            Spacer(Modifier.width(12.dp))
            ToolButton(
                modifier = Modifier,
                icon = painterResource(R.drawable.ic_more),
                isEnable = false,
                onClick = onClickMore
            )
        }
    }
}


@PreviewLightDark
@Composable
private fun HomeToolbarPreview() {
    PhotoEditorTheme {
        HomeToolbar(
            selectedTool = SelectionGraphicTool.CircleSelection,
            onClickMore = { /* no-op */ },
        )
    }
}
