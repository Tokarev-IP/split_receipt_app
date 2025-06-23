package com.iliatokarev.receipt_splitter_app.receipt.presentation.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.iliatokarev.receipt_splitter_app.R
import com.iliatokarev.receipt_splitter_app.main.basic.getOrZero
import com.iliatokarev.receipt_splitter_app.main.basic.icons.ChecklistIcon
import com.iliatokarev.receipt_splitter_app.main.basic.icons.ReceiptLong
import com.iliatokarev.receipt_splitter_app.main.basic.icons.TwoLinesIcon
import com.iliatokarev.receipt_splitter_app.receipt.data.services.DataConstantsReceipt.MAXIMUM_AMOUNT_OF_RECEIPTS_IN_FOLDER
import com.iliatokarev.receipt_splitter_app.receipt.presentation.ReceiptEvent
import com.iliatokarev.receipt_splitter_app.receipt.presentation.ReceiptViewModel
import com.iliatokarev.receipt_splitter_app.receipt.presentation.viewmodels.FolderReceiptsEvent
import com.iliatokarev.receipt_splitter_app.receipt.presentation.viewmodels.FolderReceiptsViewModel
import com.iliatokarev.receipt_splitter_app.receipt.presentation.views.dialogs.AcceptDeletionDialog
import com.iliatokarev.receipt_splitter_app.receipt.presentation.views.dialogs.AddFolderConsumerNameDialog
import com.iliatokarev.receipt_splitter_app.receipt.presentation.views.dialogs.ReceiptReportBottomSheet
import com.iliatokarev.receipt_splitter_app.receipt.presentation.views.screens.FolderReceiptsScreenView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FolderReceiptScreen(
    modifier: Modifier = Modifier,
    receiptViewModel: ReceiptViewModel,
    folderReceiptsViewModel: FolderReceiptsViewModel,
    folderId: Long,
    folderName: String,
) {
    val receiptDataList by folderReceiptsViewModel.getAllReceiptsList()
        .collectAsStateWithLifecycle()
    val isReportGenerationPending by folderReceiptsViewModel.getIsReportGenerationPendingState()
        .collectAsStateWithLifecycle()
    val folderData by folderReceiptsViewModel.getFolderDataState().collectAsStateWithLifecycle()
    val allReceiptsReport =
        folderReceiptsViewModel.getReceiptReportState().collectAsStateWithLifecycle()

    var isSplitMode by rememberSaveable { mutableStateOf(false) }
    var showAddFolderConsumerNameDialog by rememberSaveable { mutableStateOf(false) }
    var showDeleteConsumerNameDialog by rememberSaveable { mutableStateOf(false) }
    var showReceiptReportBottomSheet by rememberSaveable { mutableStateOf(false) }

    var consumerNameToDelete: String? = null

    BackHandler(enabled = isSplitMode) { isSplitMode = false }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = folderName,
                        maxLines = ONE_LINE,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { receiptViewModel.setEvent(ReceiptEvent.GoBack) }
                    ) {
                        Icon(
                            Icons.AutoMirrored.Rounded.ArrowBack,
                            stringResource(R.string.go_back_button)
                        )
                    }
                },
                actions = {
                    AnimatedVisibility(
                        visible = receiptDataList?.isEmpty() == false
                    ) {
                        AnimatedContent(
                            targetState = isSplitMode,
                        ) { splitMode ->
                            if (splitMode)
                                IconButton(
                                    onClick = {
                                        isSplitMode = false
                                        folderReceiptsViewModel.setEvent(
                                            FolderReceiptsEvent.TurnOffCheckStateForAllReceipts
                                        )
                                    }
                                ) {
                                    Icon(
                                        Icons.Outlined.TwoLinesIcon,
                                        stringResource(R.string.split_mode_off_button)
                                    )
                                }
                            else
                                IconButton(
                                    onClick = { isSplitMode = true }
                                ) {
                                    Icon(
                                        Icons.Outlined.ChecklistIcon,
                                        stringResource(R.string.split_mode_on_button)
                                    )
                                }
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                enter = fadeIn(),
                exit = fadeOut(),
                visible = isSplitMode && isReportGenerationPending
            ) {
                FloatingActionButton(
                    modifier = modifier.padding(12.dp),
                    onClick = {
                        folderReceiptsViewModel.setEvent(
                            FolderReceiptsEvent.CreateFullOrdersReport
                        )
                        showReceiptReportBottomSheet = true
                    },
                ) {
                    Icon(
                        Icons.Filled.ReceiptLong,
                        contentDescription = stringResource(id = R.string.split_the_receipt)
                    )
                }
            }
            AnimatedVisibility(
                enter = fadeIn(),
                exit = fadeOut(),
                visible = receiptDataList?.size.getOrZero() < MAXIMUM_AMOUNT_OF_RECEIPTS_IN_FOLDER
                        && isSplitMode == false
            ) {
                FloatingActionButton(
                    modifier = modifier.padding(12.dp),
                    onClick = {
                        receiptViewModel.setEvent(
                            ReceiptEvent.OpenCreateReceiptScreenFromFolder(folderId = folderId)
                        )
                    },
                ) {
                    Icon(
                        Icons.Filled.Add,
                        contentDescription = stringResource(id = R.string.add_new_receipt_button)
                    )
                }
            }
        }
    ) { innerPadding ->
        FolderReceiptsScreenView(
            modifier = modifier.padding(innerPadding),
            receiptDataList = receiptDataList,
            isSplitMode = isSplitMode,
            onReceiptClicked = { receiptId ->
                receiptViewModel.setEvent(
                    ReceiptEvent.OpenSplitReceiptScreen(
                        receiptId = receiptId,
                        assignedConsumerNamesList = folderData?.consumerNamesList ?: emptyList()
                    )
                )
            },
            onCheckStateChanged = { receiptId ->
                folderReceiptsViewModel.setEvent(
                    FolderReceiptsEvent.ChangeCheckStateForSpecificReceipt(receiptId = receiptId)
                )
            },
            onShareStateChanged = { receiptData ->
                folderReceiptsViewModel.setEvent(
                    FolderReceiptsEvent.ChangeShareStateForReceipt(receiptData = receiptData)
                )
            },
            onRemoveReceiptFromFolderClicked = { receiptData ->
                folderReceiptsViewModel.setEvent(
                    FolderReceiptsEvent.MoveReceiptOutOfFolder(receiptData = receiptData)
                )
            },
            onEditReceiptClicked = { receiptData ->
                receiptViewModel.setEvent(ReceiptEvent.OpenEditReceiptsScreen(receiptId = receiptData))
            },
            onDeleteReceiptClicked = { receiptId ->
                //todo
            },
            folderData = folderData,
            onAddNewConsumerNameClick = { showAddFolderConsumerNameDialog = true },
            onDeleteConsumerNameClick = { name ->
                consumerNameToDelete = name
                showDeleteConsumerNameDialog = true
            },
        )

        if (showAddFolderConsumerNameDialog)
            AddFolderConsumerNameDialog(
                onDismissRequest = { showAddFolderConsumerNameDialog = false },
                onSaveNewFolderConsumerName = { name ->
                    showAddFolderConsumerNameDialog = false
                    folderReceiptsViewModel.setEvent(
                        FolderReceiptsEvent.AddConsumerNameToFolder(consumerName = name)
                    )
                },
                allConsumerNamesList = folderData?.consumerNamesList ?: emptyList(),
            )

        if (showDeleteConsumerNameDialog) {
            consumerNameToDelete?.let { name ->
                AcceptDeletionDialog(
                    onDismissRequest = {
                        showDeleteConsumerNameDialog = false
                        consumerNameToDelete = null

                    },
                    onDeleteClicked = {
                        folderReceiptsViewModel.setEvent(
                            FolderReceiptsEvent.DeleteConsumerNameFromFolder(consumerName = name)
                        )
                        showDeleteConsumerNameDialog = false
                        consumerNameToDelete = null
                    },
                    infoText = stringResource(R.string.delete_consumer_name_text)
                )
            }
        }

        if (showReceiptReportBottomSheet) {
            allReceiptsReport
            ReceiptReportBottomSheet(
                onDismissRequest = { showReceiptReportBottomSheet = false },
                allReceiptsReport = allReceiptsReport.value,
                onShareReport = { report ->

                },
            )
        }
    }
}

private const val ONE_LINE = 1