package com.iliatokarev.receipt_splitter_app.receipt.presentation.viewmodels

import com.iliatokarev.receipt_splitter_app.main.basic.BasicEvent
import com.iliatokarev.receipt_splitter_app.main.basic.BasicIntent
import com.iliatokarev.receipt_splitter_app.main.basic.BasicUiMessageIntent
import com.iliatokarev.receipt_splitter_app.main.basic.BasicUiState
import com.iliatokarev.receipt_splitter_app.main.basic.BasicViewModel

class FolderReceiptsViewModel(

): BasicViewModel<FolderReceiptsUiState, FolderReceiptsIntent, FolderReceiptsEvent, FolderReceiptsUiMessageIntent>(
    FolderReceiptsUiState.Show

){
    override fun setEvent(newEvent: FolderReceiptsEvent) {
        when (newEvent){

            else -> {}
        }
    }

}

interface FolderReceiptsUiState: BasicUiState{
    object Show: FolderReceiptsUiState
    object Loading: FolderReceiptsUiState
}
interface FolderReceiptsIntent: BasicIntent
sealed interface FolderReceiptsEvent: BasicEvent
interface FolderReceiptsUiMessageIntent: BasicUiMessageIntent
