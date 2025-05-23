package com.example.receipt_splitter.receipt.presentation.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Share
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.receipt_splitter.R
import com.example.receipt_splitter.receipt.presentation.OrderData
import com.example.receipt_splitter.receipt.presentation.ReceiptEvent
import com.example.receipt_splitter.receipt.presentation.ReceiptUIConstants
import com.example.receipt_splitter.receipt.presentation.ReceiptViewModel
import com.example.receipt_splitter.receipt.presentation.viewmodels.EditReceiptEvent
import com.example.receipt_splitter.receipt.presentation.viewmodels.EditReceiptViewModel
import com.example.receipt_splitter.receipt.presentation.views.dialogs.AcceptDeletionDialog
import com.example.receipt_splitter.receipt.presentation.views.dialogs.AddNewOrderDialog
import com.example.receipt_splitter.receipt.presentation.views.dialogs.EditOrderDialog
import com.example.receipt_splitter.receipt.presentation.views.dialogs.EditReceiptDialog
import com.example.receipt_splitter.receipt.presentation.views.screens.EditReceiptScreenView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditReceiptScreen(
    modifier: Modifier = Modifier,
    editReceiptViewModel: EditReceiptViewModel,
    receiptViewModel: ReceiptViewModel,
    topAppBarState: TopAppBarState = rememberTopAppBarState(),
    orderListState: LazyListState = rememberLazyListState(),
) {
    var showDeleteOrderDialog by rememberSaveable { mutableStateOf(false) }
    var orderIdToDelete by rememberSaveable { mutableStateOf<Long?>(null) }

    val orderDataList by editReceiptViewModel.getOrderDataList().collectAsStateWithLifecycle()
    val receiptData by editReceiptViewModel.getReceiptData().collectAsStateWithLifecycle()

    var showEditReceiptDialog by rememberSaveable { mutableStateOf(false) }
    var showAddNewOrderDialog by rememberSaveable { mutableStateOf(false) }
    var showEditOrderDialog by rememberSaveable { mutableStateOf(false) }
    var orderId by rememberSaveable { mutableLongStateOf(0L) }

    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(topAppBarState)

    val isOrderListAtTheBottom = remember {
        derivedStateOf {
            val lastVisibleItem = orderListState.layoutInfo.visibleItemsInfo.lastOrNull()
            lastVisibleItem != null &&
                    lastVisibleItem.index >= orderListState.layoutInfo.totalItemsCount - AMOUNT_OF_ELEMENTS
        }
    }

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                scrollBehavior = scrollBehavior,
                title = {
                    Text(
                        maxLines = ReceiptUIConstants.ONE_LINE,
                        text = receiptData?.receiptName
                            ?: stringResource(R.string.edit_the_receipt),
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
                    IconButton(
                        onClick = {
                            receiptData?.id?.let { id ->
                                receiptViewModel.setEvent(
                                    ReceiptEvent.OpenSplitReceiptScreen(
                                        receiptId = id
                                    )
                                )
                            }
                        },
                    ) {
                        Icon(
                            Icons.Outlined.Share,
                            contentDescription = stringResource(R.string.go_to_split_the_receipt_button)
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = isOrderListAtTheBottom.value,
                enter = scaleIn(),
                exit = scaleOut(),
            ) {
                FloatingActionButton(
                    modifier = Modifier.padding(12.dp),
                    onClick = { showAddNewOrderDialog = true }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Add,
                        contentDescription = stringResource(R.string.add_new_order_button),
                    )
                }
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
            orderListState = orderListState,
            onEditReceiptClicked = {
                showEditReceiptDialog = true
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
                onAcceptClicked = {
                    orderIdToDelete?.let { id ->
                        editReceiptViewModel.setEvent(EditReceiptEvent.DeleteOrder(orderId = id))
                    }
                    orderIdToDelete = null
                    showDeleteOrderDialog = false
                },
            )
    }
}

private const val AMOUNT_OF_ELEMENTS = 1