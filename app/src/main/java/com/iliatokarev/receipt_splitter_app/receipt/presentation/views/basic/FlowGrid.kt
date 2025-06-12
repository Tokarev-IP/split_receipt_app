package com.iliatokarev.receipt_splitter_app.receipt.presentation.views.basic

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun FlowGridLayout(
    modifier: Modifier = Modifier,
    horizontalSpacing: Dp = 8.dp,
    verticalSpacing: Dp = 8.dp,
    content: @Composable () -> Unit,
) {
    Layout(
        modifier = modifier,
        content = content
    ) { measurables, constraints ->

        val maxWidth = constraints.maxWidth
        val hSpacingPx = horizontalSpacing.roundToPx()
        val vSpacingPx = verticalSpacing.roundToPx()

        val placeables = measurables.map { it.measure(constraints) }

        val positions = mutableListOf<Pair<Int, Int>>()
        var x = 0
        var y = 0
        var rowHeight = 0
        var layoutHeight = 0

        placeables.forEach { placeable ->
            if (x + placeable.width > maxWidth) {
                x = 0
                y += rowHeight + vSpacingPx
                layoutHeight += rowHeight + vSpacingPx
                rowHeight = 0
            }

            positions.add(x to y)

            x += placeable.width + hSpacingPx
            rowHeight = maxOf(rowHeight, placeable.height)
        }

        layoutHeight += rowHeight

        layout(width = maxWidth, height = layoutHeight) {
            placeables.forEachIndexed { index, placeable ->
                val (px, py) = positions[index]
                placeable.placeRelative(px, py)
            }
        }
    }
}
