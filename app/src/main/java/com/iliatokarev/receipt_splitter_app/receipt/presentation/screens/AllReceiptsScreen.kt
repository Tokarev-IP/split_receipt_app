package com.iliatokarev.receipt_splitter_app.receipt.presentation.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.iliatokarev.receipt_splitter_app.R
import com.iliatokarev.receipt_splitter_app.receipt.presentation.ReceiptEvent
import com.iliatokarev.receipt_splitter_app.receipt.presentation.ReceiptViewModel
import com.iliatokarev.receipt_splitter_app.receipt.presentation.viewmodels.AllReceiptsEvent
import com.iliatokarev.receipt_splitter_app.receipt.presentation.viewmodels.AllReceiptsViewModel
import com.iliatokarev.receipt_splitter_app.receipt.presentation.views.dialogs.AcceptDeletionDialog
import com.iliatokarev.receipt_splitter_app.receipt.presentation.views.dialogs.AddNewFolderDialog
import com.iliatokarev.receipt_splitter_app.receipt.presentation.views.dialogs.EditFolderDialog
import com.iliatokarev.receipt_splitter_app.receipt.presentation.views.screens.AllReceiptsScreenView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllReceiptsScreen(
    modifier: Modifier = Modifier,
    receiptViewModel: ReceiptViewModel,
    allReceiptViewModel: AllReceiptsViewModel,
    topAppBarState: TopAppBarState = rememberTopAppBarState(),
) {
    var showAcceptDeletionReceiptDialog by rememberSaveable { mutableStateOf(false) }
    var showAddNewFolderDialog by rememberSaveable { mutableStateOf(false) }
    var showEditFolderDialog by rememberSaveable { mutableStateOf(false) }

    val allReceiptsList by allReceiptViewModel.getAllReceiptsList().collectAsStateWithLifecycle()
    val foldersListUnarchived by allReceiptViewModel.getFoldersListUnarchived().collectAsStateWithLifecycle()
    val foldersListArchived by allReceiptViewModel.getFoldersListArchived().collectAsStateWithLifecycle()

    var chosenReceiptIdToDelete: Long? = null
    var chosenFolderIdToEdit: Long? = null
    var chosenReceiptIdToMove: Long? = null

    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(topAppBarState)

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.receipts)) },
                scrollBehavior = scrollBehavior,
                actions = {
                    IconButton(
                        onClick = {
                            receiptViewModel.setEvent(ReceiptEvent.OpenSettings)
                        }
                    ) {
                        Icon(
                            Icons.Outlined.Settings,
                            contentDescription = stringResource(R.string.settings_button)
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.padding(12.dp),
                onClick = {
                    receiptViewModel.setEvent(ReceiptEvent.OpenCreateReceiptScreen)
                }
            ) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = stringResource(id = R.string.add_new_receipt_button)
                )
            }
        }
    ) { innerPadding ->
        AllReceiptsScreenView(
            modifier = modifier.padding(innerPadding),
            allReceiptsList = allReceiptsList,
            onReceiptClicked = { receiptId ->
                receiptViewModel.setEvent(
                    ReceiptEvent.OpenSplitReceiptScreen(receiptId = receiptId)
                )
            },
            onDeleteReceiptClicked = { receiptId ->
                chosenReceiptIdToDelete = receiptId
                showAcceptDeletionReceiptDialog = true
            },
            onEditReceiptClicked = { receiptId ->
                receiptViewModel.setEvent(
                    ReceiptEvent.OpenEditReceiptsScreen(
                        receiptId = receiptId
                    )
                )
            },
            foldersListUnarchived = foldersListUnarchived,
            foldersListArchived = foldersListArchived,
            onAddNewFolderClicked = { showAddNewFolderDialog = true },
            onEditFolderClicked = { folderId ->
                chosenFolderIdToEdit = folderId
                showEditFolderDialog = true
            },
            onArchiveFolderClicked = { folderData ->
                allReceiptViewModel.setEvent(AllReceiptsEvent.ArchiveFolder(folderData = folderData))
            },
            onUnarchiveFolderClicked = { folderData ->
                allReceiptViewModel.setEvent(AllReceiptsEvent.UnArchiveFolder(folderData = folderData))
            },
            onFolderClick = { folderId ->

            },
            onMoveReceiptToClicked = { receiptId ->
                chosenReceiptIdToMove = receiptId
            },
        )

        if (showAcceptDeletionReceiptDialog)
            chosenReceiptIdToDelete?.let { receiptId ->
                AcceptDeletionDialog(
                    infoText = stringResource(R.string.do_you_want_to_delete_this_receipt),
                    onDismissRequest = { showAcceptDeletionReceiptDialog = false },
                    onAcceptClicked = {
                        allReceiptViewModel.setEvent(
                            AllReceiptsEvent.DeleteSpecificReceipt(
                                receiptId
                            )
                        )
                        chosenReceiptIdToDelete = null
                        showAcceptDeletionReceiptDialog = false
                    }
                )
            }

        if (showEditFolderDialog) {
            chosenFolderIdToEdit?.let { folderId ->
                val folderData = foldersListUnarchived?.find { it.id == folderId }
                    ?: foldersListArchived?.find { it.id == folderId }

                folderData?.let { data ->
                    EditFolderDialog(
                        onDismissRequest = { showEditFolderDialog = false },
                        folderData = data,
                        onSaveButtonClicked = { folderData ->
                            allReceiptViewModel.setEvent(
                                AllReceiptsEvent.SaveFolder(folderData = folderData)
                            )
                            chosenFolderIdToEdit = null
                            showEditFolderDialog = false
                        }
                    )
                }
            }
        }

        if (showAddNewFolderDialog) {
            AddNewFolderDialog(
                onDismissRequest = { showAddNewFolderDialog = false },
                onSaveButtonClicked = { folderData ->
                    allReceiptViewModel.setEvent(
                        AllReceiptsEvent.SaveFolder(folderData = folderData)
                    )
                    showAddNewFolderDialog = false
                },
            )
        }
    }
}