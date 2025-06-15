package com.iliatokarev.receipt_splitter_app.receipt.presentation.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.iliatokarev.receipt_splitter_app.receipt.presentation.viewmodels.FolderReceiptsViewModel
import com.iliatokarev.receipt_splitter_app.receipt.presentation.views.screens.FolderReceiptsScreenView

@Composable
internal fun FolderReceiptScreen(
    modifier: Modifier = Modifier,
    folderReceiptsViewModel: FolderReceiptsViewModel,
){
    Scaffold() { innerPadding ->
        FolderReceiptsScreenView(
            modifier = modifier.padding(innerPadding)
        )
    }
}