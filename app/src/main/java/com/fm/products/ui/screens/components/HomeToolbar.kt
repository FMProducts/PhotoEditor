package com.fm.products.ui.screens.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.fm.products.R
import com.fm.products.ui.components.ToolButton
import com.fm.products.ui.models.Tools
import com.fm.products.ui.theme.PurpleGrey80
import com.fm.products.ui.utils.buttonStateColor

@Composable
fun HomeToolbar(
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
