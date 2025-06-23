package com.iliatokarev.receipt_splitter_app.main.basic.icons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector

public val Icons.Filled.ShortTextIcon: ImageVector
    get() {
        if (_filled_shortText != null) {
            return _filled_shortText!!
        }
        _filled_shortText = materialIcon(name = "Filled.ShortText", autoMirror = true) {
            materialPath {
                //first line
                moveTo(4.0f, 9.0f)
                horizontalLineToRelative(16.0f)
                verticalLineToRelative(2.0f)
                horizontalLineTo(4.0f)
                verticalLineTo(9.0f)
                close()

                //second line
                moveTo(4.0f, 13.0f)
                horizontalLineToRelative(10.0f)
                verticalLineToRelative(2.0f)
                horizontalLineTo(4.0f)
                verticalLineTo(13.0f)
                close()

                //third line
                moveTo(4.0f, 17.0f)
                horizontalLineToRelative(4.0f)
                verticalLineToRelative(2.0f)
                horizontalLineTo(4.0f)
                verticalLineTo(13.0f)
                close()
            }
        }
        return _filled_shortText!!
    }

private var _filled_shortText: ImageVector? = null




public val Icons.Filled.LongTextIcon: ImageVector
    get() {
        if (_filled_longText != null) {
            return _filled_longText!!
        }
        _filled_longText = materialIcon(name = "Filled.LongText", autoMirror = true) {
            materialPath {
                //first line
                moveTo(4.0f, 9.0f)
                horizontalLineToRelative(16.0f)
                verticalLineToRelative(2.0f)
                horizontalLineTo(4.0f)
                verticalLineTo(9.0f)
                close()

                //second line
                moveTo(4.0f, 13.0f)
                horizontalLineToRelative(14.0f)
                verticalLineToRelative(2.0f)
                horizontalLineTo(4.0f)
                verticalLineTo(13.0f)
                close()

                //third line
                moveTo(4.0f, 17.0f)
                horizontalLineToRelative(12.0f)
                verticalLineToRelative(2.0f)
                horizontalLineTo(4.0f)
                verticalLineTo(13.0f)
                close()
            }
        }
        return _filled_longText!!
    }

private var _filled_longText: ImageVector? = null




public val Icons.Filled.BasicTextIcon: ImageVector
    get() {
        if (_filled_basicText != null) {
            return _filled_basicText!!
        }
        _filled_basicText = materialIcon(name = "Filled.BasicText", autoMirror = true) {
            materialPath {
                //first line
                moveTo(4.0f, 9.0f)
                horizontalLineToRelative(16.0f)
                verticalLineToRelative(2.0f)
                horizontalLineTo(4.0f)
                verticalLineTo(9.0f)
                close()

                //second line
                moveTo(4.0f, 13.0f)
                horizontalLineToRelative(12.0f)
                verticalLineToRelative(2.0f)
                horizontalLineTo(4.0f)
                verticalLineTo(13.0f)
                close()

                //third line
                moveTo(4.0f, 17.0f)
                horizontalLineToRelative(8.0f)
                verticalLineToRelative(2.0f)
                horizontalLineTo(4.0f)
                verticalLineTo(13.0f)
                close()
            }
        }
        return _filled_basicText!!
    }

private var _filled_basicText: ImageVector? = null