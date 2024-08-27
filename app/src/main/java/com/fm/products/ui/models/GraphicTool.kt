package com.fm.products.ui.models

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.fm.products.R

sealed class GraphicTool(
    @StringRes val nameRes: Int,
    @DrawableRes val iconRes: Int,
) {

    data object None : GraphicTool(
        nameRes = R.string.none_selection,
        iconRes = R.drawable.ic_more,
    )

    companion object {
        fun values(): List<GraphicTool> = buildList {
            addAll(SelectionGraphicTool.values())
            addAll(OtherGraphicTool.values())
            add(None)
        }
    }
}

sealed class SelectionGraphicTool(nameRes: Int, iconRes: Int) : GraphicTool(nameRes, iconRes) {

    data object RectangleSelection : SelectionGraphicTool(
        nameRes = R.string.rectangle_selection,
        iconRes = R.drawable.ic_select_square,
    )

    data object CircleSelection : SelectionGraphicTool(
        nameRes = R.string.circle_selection,
        iconRes = R.drawable.ic_select_circle,
    )

    data object LassoSelection : SelectionGraphicTool(
        nameRes = R.string.lasso_selection,
        iconRes = R.drawable.ic_lasso_select,
    )

    data object MagneticLassoSelection : SelectionGraphicTool(
        nameRes = R.string.magnetic_lasso_selection,
        iconRes = R.drawable.ic_magnetic_lasso_select,
    )

    companion object {
        fun values(): List<SelectionGraphicTool> = listOf(
            RectangleSelection,
            CircleSelection,
            LassoSelection,
            MagneticLassoSelection,
        )
    }
}

sealed class OtherGraphicTool(nameRes: Int, iconRes: Int) : GraphicTool(nameRes, iconRes) {

    data object BackgroundRemover : OtherGraphicTool(
        nameRes = R.string.remove_background,
        iconRes = R.drawable.ic_remove_outline,
    )

    data class PhotoFilter(val selectedFilter: ImageFilter = ImageFilter.None) : OtherGraphicTool(
        nameRes = R.string.photo_filter,
        iconRes = R.drawable.ic_photo_filter,
    )

    companion object {
        fun values(): List<OtherGraphicTool> = listOf(
            BackgroundRemover,
            PhotoFilter(),
        )
    }
}
