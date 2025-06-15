package com.iliatokarev.receipt_splitter_app.main.basic.icons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector

public val Icons.Outlined.FileMove: ImageVector
    get() {
        if (_fileMove != null) {
            return _fileMove!!
        }
        _fileMove = materialIcon(name = "AutoMirrored.Outlined.DriveFileMove", autoMirror =
            true) {
            materialPath {
                moveTo(20.0f, 6.0f)
                horizontalLineToRelative(-8.0f)
                lineToRelative(-2.0f, -2.0f)
                horizontalLineTo(4.0f)
                curveTo(2.9f, 4.0f, 2.0f, 4.9f, 2.0f, 6.0f)
                verticalLineToRelative(12.0f)
                curveToRelative(0.0f, 1.1f, 0.9f, 2.0f, 2.0f, 2.0f)
                horizontalLineToRelative(16.0f)
                curveToRelative(1.1f, 0.0f, 2.0f, -0.9f, 2.0f, -2.0f)
                verticalLineTo(8.0f)
                curveTo(22.0f, 6.9f, 21.1f, 6.0f, 20.0f, 6.0f)
                close()
                moveTo(20.0f, 18.0f)
                horizontalLineTo(4.0f)
                verticalLineTo(6.0f)
                horizontalLineToRelative(5.17f)
                lineToRelative(1.41f, 1.41f)
                lineTo(11.17f, 8.0f)
                horizontalLineTo(20.0f)
                verticalLineTo(18.0f)
                close()
                moveTo(12.16f, 12.0f)
                horizontalLineTo(8.0f)
                verticalLineToRelative(2.0f)
                horizontalLineToRelative(4.16f)
                lineToRelative(-1.59f, 1.59f)
                lineTo(11.99f, 17.0f)
                lineTo(16.0f, 13.01f)
                lineTo(11.99f, 9.0f)
                lineToRelative(-1.41f, 1.41f)
                lineTo(12.16f, 12.0f)
                close()
            }
        }
        return _fileMove!!
    }

private var _fileMove: ImageVector? = null