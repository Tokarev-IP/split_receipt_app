package com.iliatokarev.receipt_splitter_app.receipt.presentation.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.iliatokarev.receipt_splitter_app.R
import com.iliatokarev.receipt_splitter_app.main.basic.icons.Receipt
import com.iliatokarev.receipt_splitter_app.receipt.presentation.OrderData
import com.iliatokarev.receipt_splitter_app.receipt.presentation.ReceiptEvent
import com.iliatokarev.receipt_splitter_app.receipt.presentation.ReceiptUIConstants
import com.iliatokarev.receipt_splitter_app.receipt.presentation.ReceiptViewModel
import com.iliatokarev.receipt_splitter_app.receipt.presentation.viewmodels.EditReceiptEvent
import com.iliatokarev.receipt_splitter_app.receipt.presentation.viewmodels.EditReceiptViewModel
import com.iliatokarev.receipt_splitter_app.receipt.presentation.views.basic.BackNavigationButton
import com.iliatokarev.receipt_splitter_app.receipt.presentation.views.dialogs.AcceptDeletionDialog
import com.iliatokarev.receipt_splitter_app.receipt.presentation.views.dialogs.AddNewOrderDialog
import com.iliatokarev.receipt_splitter_app.receipt.presentation.views.dialogs.EditOrderDialog
import com.iliatokarev.receipt_splitter_app.receipt.presentation.views.dialogs.EditReceiptDialog
import com.iliatokarev.receipt_splitter_app.receipt.presentation.views.screens.EditReceiptScreenView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditReceiptScreen(
    modifier: Modifier = Modifier,
    editReceiptViewModel: EditReceiptViewModel,
    receiptViewModel: ReceiptViewModel,
    topAppBarState: TopAppBarState = rememberTopAppBarState(),
) {
    val limitExceededMessage = stringResource(R.string.exceed_info_message)

    var showDeleteOrderDialog by rememberSaveable { mutableStateOf(false) }
    var orderIdToDelete by rememberSaveable { mutableStateOf<Long?>(null) }

    val orderDataList by editReceiptViewModel.getOrderDataList().collectAsStateWithLifecycle()
    val receiptData by editReceiptViewModel.getReceiptData().collectAsStateWithLifecycle()
    val isOrderCountAtLimit by editReceiptViewModel.getIsOrderCountAtLimit()
        .collectAsStateWithLifecycle()

    var showEditReceiptDialog by rememberSaveable { mutableStateOf(false) }
    var showAddNewOrderDialog by rememberSaveable { mutableStateOf(false) }
    var showEditOrderDialog by rememberSaveable { mutableStateOf(false) }

    var orderId = 0L

    val scrollBehavior =
        TopAppBarDefaults.enterAlwaysScrollBehavior(topAppBarState)

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                scrollBehavior = scrollBehavior,
                title = {
                    Text(
                        maxLines = ReceiptUIConstants.ONE_LINE,
                        text = stringResource(R.string.edit_the_receipt),
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                navigationIcon = {
                    BackNavigationButton { receiptViewModel.setEvent(ReceiptEvent.GoBack) }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.padding(12.dp),
                onClick = {
                    receiptData?.id?.let { id ->
                        receiptViewModel.setEvent(
                            ReceiptEvent.OpenSplitReceiptForAllScreen(receiptId = id)
                        )
                    }
                },
            ) {
                Icon(
                    imageVector = Icons.Filled.Receipt,
                    contentDescription = stringResource(R.string.go_to_split_receipt_screen_button),
                )
            }
        }
    ) { innerPadding ->
        EditReceiptScreenView(
            modifier = modifier.padding(innerPadding),
            receiptData = receiptData,
            orderDataList = orderDataList,
            onEditOrderClicked = { id ->
                orderId = id
                showEditOrderDialog = true
            },
            onDeleteOrderClicked = { id ->
                orderIdToDelete = id
                showDeleteOrderDialog = true
            },
            onEditReceiptClicked = {
                showEditReceiptDialog = true
            },
            onAddNewOrderClicked = {
                if (isOrderCountAtLimit == false)
                    showAddNewOrderDialog = true
                else
                    receiptViewModel.setEvent(ReceiptEvent.SetUiMessage(limitExceededMessage))
            },
        )

        if (showEditReceiptDialog) {
            receiptData?.let { receipt ->
                EditReceiptDialog(
                    receiptData = receipt,
                    onCancelButtonClicked = {
                        showEditReceiptDialog = false
                    },
                    onSaveButtonClicked = { receiptData ->
                        showEditReceiptDialog = false
                        editReceiptViewModel.setEvent(EditReceiptEvent.EditReceipt(receipt = receiptData))
                    },
                )
            }
        }

        if (showAddNewOrderDialog) {
            receiptData?.let { receipt ->
                AddNewOrderDialog(
                    receiptId = receipt.id,
                    onCancelButtonClicked = {
                        showAddNewOrderDialog = false
                    },
                    onSaveButtonClicked = { orderData ->
                        showAddNewOrderDialog = false
                        editReceiptViewModel.setEvent(EditReceiptEvent.AddNewOrder(order = orderData))
                    },
                )
            }
        }

        if (showEditOrderDialog) {
            val specificOrderData: OrderData? = orderDataList.find { it.id == orderId }
            specificOrderData?.let { order ->
                EditOrderDialog(
                    orderData = order,
                    onCancelButtonClicked = {
                        showEditOrderDialog = false
                    },
                    onSaveButtonClicked = { orderData ->
                        showEditOrderDialog = false
                        editReceiptViewModel.setEvent(EditReceiptEvent.EditOrder(order = orderData))
                    },
                )
            }
        }

        if (showDeleteOrderDialog && orderIdToDelete != null)
            AcceptDeletionDialog(
                infoText = stringResource(R.string.do_you_want_to_delete_this_order),
                onDismissRequest = { showDeleteOrderDialog = false },
                onDeleteClicked = {
                    orderIdToDelete?.let { id ->
                        editReceiptViewModel.setEvent(EditReceiptEvent.DeleteOrder(orderId = id))
                    }
                    orderIdToDelete = null
                    showDeleteOrderDialog = false
                },
            )
    }
}