package com.example.receipt_splitter.receipt.presentation.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.animation.with
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.receipt_splitter.R
import com.example.receipt_splitter.receipt.presentation.OrderData
import com.example.receipt_splitter.receipt.presentation.ReceiptEvent
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
) {
    var showDeleteOrderDialog by rememberSaveable { mutableStateOf(false) }
    var orderIdToDelete by rememberSaveable { mutableStateOf<Long?>(null) }

    val orderDataList by editReceiptViewModel.getOrderDataList().collectAsStateWithLifecycle()
    val receiptData by editReceiptViewModel.getReceiptData().collectAsStateWithLifecycle()

    var showEditReceiptDialog by rememberSaveable { mutableStateOf(false) }
    var showAddNewOrderDialog by rememberSaveable { mutableStateOf(false) }
    var showEditOrderDialog by rememberSaveable { mutableStateOf(false) }
    var orderId by rememberSaveable { mutableLongStateOf(0L) }

    val ordersListState = rememberLazyListState()
    val isOrderListAlmostAtBottom = remember {
        derivedStateOf {
            val lastVisibleItem = ordersListState.layoutInfo.visibleItemsInfo.lastOrNull()
            lastVisibleItem != null && lastVisibleItem.index >= ordersListState.layoutInfo.totalItemsCount - 5
        }
    }
    val isOrderListAtBottom = remember {
        derivedStateOf {
            val lastVisibleItem = ordersListState.layoutInfo.visibleItemsInfo.lastOrNull()
            lastVisibleItem != null && lastVisibleItem.index >= ordersListState.layoutInfo.totalItemsCount - 1
        }
    }

    if (showEditReceiptDialog) {
        receiptData?.let { receipt ->
            EditReceiptDialog(
                receiptData = { receipt },
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

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.edit_the_receipt))
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
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = isOrderListAlmostAtBottom.value,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut(),
            ) {
                Box(
                    contentAlignment = Alignment.BottomEnd,
                ) {
                    FloatingActionButton(
                        modifier = modifier
                            .padding(12.dp)
                            .animateContentSize(),
                        onClick = {
                            showAddNewOrderDialog = true
                        }
                    ) {
                        Box(
                            modifier = modifier.padding(12.dp),
                        ) {
                            if (isOrderListAtBottom.value)
                                Text(
                                    modifier = modifier.animateEnterExit(
                                        enter = fadeIn(),
                                        exit = slideOutHorizontally()
                                    ),
                                    text = stringResource(id = R.string.add_new_order)
                                )
                            else
                                Icon(
                                    imageVector = Icons.Filled.Add,
                                    contentDescription = stringResource(id = R.string.add_new_receipt_button),
                                    modifier = modifier.animateEnterExit(
                                        enter = fadeIn(),
                                        exit = fadeOut(),
                                    ),
                                )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        EditReceiptScreenView(
            modifier = modifier.padding(innerPadding),
            receiptData = { receiptData },
            orderDataList = { orderDataList },
            onEditOrderClicked = { id ->
                orderId = id
                showEditOrderDialog = true
            },
            onEditReceiptClicked = {
                showEditReceiptDialog = true
            },
            onDeleteOrderClicked = { id ->
                orderIdToDelete = id
                showDeleteOrderDialog = true
            },
            onSplitScreenClicked = { receiptId ->
                receiptViewModel.setEvent(ReceiptEvent.OpenSplitReceiptScreen(receiptId = receiptId))
            },
            ordersListState = ordersListState,
        )

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
                }
            )
    }
}