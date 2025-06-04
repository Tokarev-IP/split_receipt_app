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
import com.iliatokarev.receipt_splitter_app.receipt.presentation.views.screens.AllReceiptsScreenView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllReceiptsScreen(
    modifier: Modifier = Modifier,
    receiptViewModel: ReceiptViewModel,
    allReceiptViewModel: AllReceiptsViewModel,
    topAppBarState: TopAppBarState = rememberTopAppBarState(),
) {
    val limitExceededMessage = stringResource(R.string.exceed_info_message)
    var showAcceptDeletionReceiptDialog by rememberSaveable { mutableStateOf(false) }
    var receiptIdToDelete by rememberSaveable { mutableStateOf<Long?>(null) }

    val allReceiptsList by allReceiptViewModel.getAllReceiptsList().collectAsStateWithLifecycle()
    val isReceiptsAtMaxLimit by allReceiptViewModel.getIsReceiptsAtMaxLimit()
        .collectAsStateWithLifecycle()

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
                    if (isReceiptsAtMaxLimit == false)
                        receiptViewModel.setEvent(ReceiptEvent.OpenCreateReceiptScreen)
                    else
                        receiptViewModel.setEvent(ReceiptEvent.SetUiMessage(limitExceededMessage))
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
                receiptIdToDelete = receiptId
                showAcceptDeletionReceiptDialog = true
            },
            onEditReceiptClicked = { receiptId ->
                receiptViewModel.setEvent(
                    ReceiptEvent.OpenEditReceiptsScreen(
                        receiptId = receiptId
                    )
                )
            }
        )

        if (showAcceptDeletionReceiptDialog && receiptIdToDelete != null)
            receiptIdToDelete?.let { receiptId ->
                AcceptDeletionDialog(
                    infoText = stringResource(R.string.do_you_want_to_delete_this_receipt),
                    onDismissRequest = { showAcceptDeletionReceiptDialog = false },
                    onAcceptClicked = {
                        allReceiptViewModel.setEvent(
                            AllReceiptsEvent.DeleteSpecificReceipt(
                                receiptId
                            )
                        )
                        receiptIdToDelete = null
                        showAcceptDeletionReceiptDialog = false
                    }
                )
            }
    }
}