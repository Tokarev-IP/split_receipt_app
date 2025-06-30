package com.iliatokarev.receipt_splitter_app.receipt.presentation.views.basic

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.iliatokarev.receipt_splitter_app.R

@Composable
fun BackNavigationButton(
    onClick:() -> Unit,
){
    IconButton(
        onClick = { onClick() }
    ) {
        Icon(
            Icons.AutoMirrored.Rounded.ArrowBack,
            stringResource(R.string.go_back_button)
        )
    }
}