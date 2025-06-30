package com.iliatokarev.receipt_splitter_app.main.basic.icons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector

public val Icons.Outlined.ChecklistIcon: ImageVector
    get() {
        if (_outlined_checklist != null) {
            return _outlined_checklist!!
        }
        _outlined_checklist = materialIcon(name = "Outlined.ChecklistIcon") {
            materialPath {
                moveTo(22.0f, 7.0f)
                horizontalLineToRelative(-9.0f)
                verticalLineToRelative(2.0f)
                horizontalLineToRelative(9.0f)
                verticalLineTo(7.0f)
                close()
                moveTo(22.0f, 15.0f)
                horizontalLineToRelative(-9.0f)
                verticalLineToRelative(2.0f)
                horizontalLineToRelative(9.0f)
                verticalLineTo(15.0f)
                close()
                moveTo(5.54f, 11.0f)
                lineTo(2.0f, 7.46f)
                lineToRelative(1.41f, -1.41f)
                lineToRelative(2.12f, 2.12f)
                lineToRelative(4.24f, -4.24f)
                lineToRelative(1.41f, 1.41f)
                lineTo(5.54f, 11.0f)
                close()
                moveTo(5.54f, 19.0f)
                lineTo(2.0f, 15.46f)
                lineToRelative(1.41f, -1.41f)
                lineToRelative(2.12f, 2.12f)
                lineToRelative(4.24f, -4.24f)
                lineToRelative(1.41f, 1.41f)
                lineTo(5.54f, 19.0f)
                close()
            }
        }
        return _outlined_checklist!!
    }

private var _outlined_checklist: ImageVector? = null




public val Icons.Outlined.TwoLinesIcon: ImageVector
    get() {
        if (_twoLinesIcon != null) {
            return _twoLinesIcon!!
        }
        _twoLinesIcon = materialIcon(name = "Outlined.TwoLinesIcon") {
            materialPath {
                // First horizontal line (top)
                moveTo(2.0f, 7.0f)
                horizontalLineTo(22.0f)
                verticalLineToRelative(2.0f)
                horizontalLineTo(2.0f)
                verticalLineTo(7.0f)
                close()

                // Second horizontal line (bottom)
                moveTo(2.0f, 15.0f)
                horizontalLineTo(22.0f)
                verticalLineToRelative(2.0f)
                horizontalLineTo(2.0f)
                verticalLineTo(15.0f)
                close()
            }
        }
        return _twoLinesIcon!!
    }

private var _twoLinesIcon: ImageVector? = null

