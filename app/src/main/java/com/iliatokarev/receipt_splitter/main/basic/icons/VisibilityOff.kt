package com.iliatokarev.receipt_splitter.main.basic.icons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector

val Icons.Filled.VisibilityOff: ImageVector
    get() {
        if (_visibilityOff != null) {
            return _visibilityOff!!
        }
        _visibilityOff = materialIcon(name = "Filled.VisibilityOff") {
            materialPath {
                moveTo(12.0f, 6.5f)
                curveToRelative(3.31f, 0.0f, 6.31f, 1.71f, 8.1f, 4.5f)
                curveToRelative(-0.59f, 1.0f, -1.36f, 1.9f, -2.27f, 2.64f)
                lineToRelative(1.46f, 1.46f)
                curveToRelative(1.37f, -1.16f, 2.5f, -2.67f, 3.25f, -4.35f)
                curveTo(21.27f, 7.61f, 17.0f, 4.5f, 12.0f, 4.5f)
                curveToRelative(-1.45f, 0.0f, -2.84f, 0.29f, -4.11f, 0.8f)
                lineToRelative(1.56f, 1.56f)
                curveToRelative(0.79f, -0.23f, 1.63f, -0.36f, 2.55f, -0.36f)
                close()
                moveTo(2.0f, 4.27f)
                lineTo(4.28f, 6.55f)
                curveTo(2.94f, 7.83f, 1.93f, 9.4f, 1.0f, 12.0f)
                curveToRelative(1.73f, 4.39f, 6.0f, 7.5f, 11.0f, 7.5f)
                curveToRelative(2.01f, 0.0f, 3.89f, -0.5f, 5.56f, -1.38f)
                lineToRelative(1.67f, 1.67f)
                lineToRelative(1.41f, -1.41f)
                lineTo(3.41f, 2.86f)
                lineTo(2.0f, 4.27f)
                close()
                moveTo(8.43f, 10.7f)
                lineToRelative(1.51f, 1.51f)
                curveToRelative(0.02f, 0.92f, 0.77f, 1.66f, 1.69f, 1.66f)
                curveToRelative(0.21f, 0.0f, 0.41f, -0.04f, 0.6f, -0.1f)
                lineToRelative(1.51f, 1.51f)
                curveToRelative(-0.65f, 0.27f, -1.36f, 0.42f, -2.11f, 0.42f)
                curveToRelative(-2.21f, 0.0f, -4.0f, -1.79f, -4.0f, -4.0f)
                curveToRelative(0.0f, -0.75f, 0.2f, -1.46f, 0.55f, -2.07f)
                close()
            }
        }
        return _visibilityOff!!
    }

private var _visibilityOff: ImageVector? = null
