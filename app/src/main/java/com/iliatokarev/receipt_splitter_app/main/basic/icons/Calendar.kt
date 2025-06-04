package com.iliatokarev.receipt_splitter_app.main.basic.icons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector

val Icons.Filled.Calendar: ImageVector
    get() {
        if (_calendar != null) {
            return _calendar!!
        }
        _calendar = materialIcon(name = "Filled.Calendar") {
            materialPath {
                // Outer calendar frame
                moveTo(4.0f, 5.0f)
                curveTo(3.45f, 5.0f, 3.0f, 5.45f, 3.0f, 6.0f)
                verticalLineTo(20.0f)
                curveTo(3.0f, 20.55f, 3.45f, 21.0f, 4.0f, 21.0f)
                horizontalLineTo(20.0f)
                curveTo(20.55f, 21.0f, 21.0f, 20.55f, 21.0f, 20.0f)
                verticalLineTo(6.0f)
                curveTo(21.0f, 5.45f, 20.55f, 5.0f, 20.0f, 5.0f)
                horizontalLineTo(17.0f)
                verticalLineTo(3.0f)
                horizontalLineTo(15.0f)
                verticalLineTo(5.0f)
                horizontalLineTo(9.0f)
                verticalLineTo(3.0f)
                horizontalLineTo(7.0f)
                verticalLineTo(5.0f)
                horizontalLineTo(4.0f)
                close()

                // Top header line
                moveTo(5.0f, 8.0f)
                horizontalLineTo(19.0f)
                verticalLineTo(9.5f)
                horizontalLineTo(5.0f)
                verticalLineTo(8.0f)
                close()

                // Calendar dots (days)
                fun drawDot(cx: Float, cy: Float, radius: Float) {
                    moveTo(cx + radius, cy)
                    arcToRelative(
                        radius, radius,
                        0f,
                        false, true,
                        -2 * radius, 0f
                    )
                    arcToRelative(
                        radius, radius,
                        0f,
                        false, true,
                        2 * radius, 0f
                    )
                }

                val dotRadius = 0.8f
                drawDot(7.5f, 12.0f, dotRadius)
                drawDot(12.0f, 12.0f, dotRadius)
                drawDot(16.5f, 12.0f, dotRadius)

                drawDot(7.5f, 16.0f, dotRadius)
                drawDot(12.0f, 16.0f, dotRadius)
                drawDot(16.5f, 16.0f, dotRadius)
            }
        }
        return _calendar!!
    }

private var _calendar: ImageVector? = null

