package com.iliatokarev.receipt_splitter_app.main.basic.icons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector

val Icons.Filled.Receipt: ImageVector
    get() {
        if (_receipt != null) {
            return _receipt!!
        }
        _receipt = materialIcon(name = "Filled.Receipt") {
            materialPath {
                moveTo(21.0f, 2.0f)
                lineTo(17.5f, 4.0f)
                lineTo(14.0f, 2.0f)
                lineTo(10.5f, 4.0f)
                lineTo(7.0f, 2.0f)
                lineTo(3.0f, 4.0f)
                verticalLineTo(22.0f)
                lineTo(7.0f, 20.0f)
                lineTo(10.5f, 22.0f)
                lineTo(14.0f, 20.0f)
                lineTo(17.5f, 22.0f)
                lineTo(21.0f, 20.0f)
                verticalLineTo(2.0f)
                close()
                moveTo(17.0f, 12.0f)
                horizontalLineTo(7.0f)
                verticalLineTo(10.0f)
                horizontalLineTo(17.0f)
                verticalLineTo(12.0f)
                close()
                moveTo(17.0f, 8.0f)
                horizontalLineTo(7.0f)
                verticalLineTo(6.0f)
                horizontalLineTo(17.0f)
                verticalLineTo(8.0f)
                close()
                moveTo(17.0f, 16.0f)
                horizontalLineTo(7.0f)
                verticalLineTo(14.0f)
                horizontalLineTo(17.0f)
                verticalLineTo(16.0f)
                close()
            }
        }
        return _receipt!!
    }

private var _receipt: ImageVector? = null
