package com.iliatokarev.receipt_splitter_app.main.basic.icons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector

val Icons.Filled.FloppyDisk: ImageVector
    get() {
        if (_floppyDisk != null) {
            return _floppyDisk!!
        }
        _floppyDisk = materialIcon(name = "Filled.FloppyDisk") {
            materialPath {
                // Внешний контур дискеты
                moveTo(4f, 3f)
                lineTo(18f, 3f)
                lineTo(21f, 6f)
                lineTo(21f, 21f)
                lineTo(4f, 21f)
                close()

                // Вырез под метку (верхняя часть)
                moveTo(6f, 5f)
                lineTo(14f, 5f)
                verticalLineTo(9f)
                lineTo(6f, 9f)
                close()

                // Центральное отверстие (или кнопка)
                moveTo(10f, 13f)
                arcToRelative(2f, 2f, 0f, true, true, 4f, 0f)
                arcToRelative(2f, 2f, 0f, true, true, -4f, 0f)
                close()
            }
        }
        return _floppyDisk!!
    }

private var _floppyDisk: ImageVector? = null
