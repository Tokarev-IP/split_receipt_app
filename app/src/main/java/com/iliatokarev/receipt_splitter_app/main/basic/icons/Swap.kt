package com.iliatokarev.receipt_splitter_app.main.basic.icons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector

val Icons.Outlined.Swap: ImageVector
    get() {
        if (_swap != null) {
            return _swap!!
        }
        _swap = materialIcon(name = "Outlined.SwapUiModes") {
            materialPath {
                // Left Panel Box (top-left)
                moveTo(3.0f, 5.0f)
                horizontalLineTo(7.0f)
                verticalLineTo(9.0f)
                horizontalLineTo(3.0f)
                close()

                // Right Panel Box (bottom-right)
                moveTo(17.0f, 15.0f)
                horizontalLineTo(21.0f)
                verticalLineTo(19.0f)
                horizontalLineTo(17.0f)
                close()

                // Rightward Arrow (top right → bottom left)
                moveTo(11.0f, 6.0f)
                lineTo(16.0f, 6.0f)
                lineTo(13.5f, 3.5f)
                lineTo(15.0f, 2.0f)
                lineTo(20.0f, 7.0f)
                lineTo(15.0f, 12.0f)
                lineTo(13.5f, 10.5f)
                lineTo(16.0f, 8.0f)
                horizontalLineTo(11.0f)
                close()

                // Leftward Arrow (bottom left → top right)
                moveTo(13.0f, 18.0f)
                lineTo(8.0f, 18.0f)
                lineTo(10.5f, 20.5f)
                lineTo(9.0f, 22.0f)
                lineTo(4.0f, 17.0f)
                lineTo(9.0f, 12.0f)
                lineTo(10.5f, 13.5f)
                lineTo(8.0f, 16.0f)
                horizontalLineTo(13.0f)
                close()
            }
        }
        return _swap!!
    }

private var _swap: ImageVector? = null
