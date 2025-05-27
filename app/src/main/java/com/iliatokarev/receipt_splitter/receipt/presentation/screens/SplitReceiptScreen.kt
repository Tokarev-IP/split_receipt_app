package com.iliatokarev.receipt_splitter.receipt.presentation.screens

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.Edit
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
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
import com.iliatokarev.receipt_splitter.R
import com.iliatokarev.receipt_splitter.receipt.presentation.ReceiptEvent
import com.iliatokarev.receipt_splitter.receipt.presentation.ReceiptUIConstants
import com.iliatokarev.receipt_splitter.receipt.presentation.ReceiptViewModel
import com.iliatokarev.receipt_splitter.receipt.presentation.viewmodels.SplitReceiptEvent
import com.iliatokarev.receipt_splitter.receipt.presentation.viewmodels.SplitReceiptViewModel
import com.iliatokarev.receipt_splitter.receipt.presentation.views.dialogs.AdditionalSumDialog
import com.iliatokarev.receipt_splitter.receipt.presentation.views.screens.SplitReceiptScreenView
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce

@OptIn(ExperimentalMaterial3Api::class, FlowPreview::class)
@Composable
fun SplitReceiptScreen(
    modifier: Modifier = Modifier,
    receiptViewModel: ReceiptViewModel,
    splitReceiptViewModel: SplitReceiptViewModel,
    topAppBarState: TopAppBarState = rememberTopAppBarState(),
    orderListState: LazyListState = rememberLazyListState(),
) {
    val receiptData by splitReceiptViewModel.getSplitReceiptData().collectAsStateWithLifecycle()
    val orderDataList by splitReceiptViewModel.getOrderDataList().collectAsStateWithLifecycle()

    var orderReportText by rememberSaveable { mutableStateOf<String?>(null) }
    var showAdditionalSumDialog by rememberSaveable { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {}

    val scrollBehavior =
        TopAppBarDefaults.enterAlwaysScrollBehavior(topAppBarState)

    val isOrderListAtBottom = remember {
        derivedStateOf {
            val lastVisibleItem = orderListState.layoutInfo.visibleItemsInfo.lastOrNull()
            lastVisibleItem != null &&
                    lastVisibleItem.index >= orderListState.layoutInfo.totalItemsCount - AMOUNT_OF_ELEMENTS
        }
    }

    LaunchedEffect(key1 = Unit) {
        splitReceiptViewModel.getOrderReportText()
            .debounce(500L)
            .collect { text ->
                orderReportText = text
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
                            ?: stringResource(R.string.split_the_receipt),
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
                                    ReceiptEvent.OpenEditReceiptsScreen(
                                        receiptId = id
                                    )
                                )
                            }
                        }
                    ) {
                        Icon(
                            Icons.Outlined.Edit,
                            stringResource(R.string.edit_receipt_button)
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = isOrderListAtBottom.value,
                enter = scaleIn(),
                exit = scaleOut(),
            ) {
                FloatingActionButton(
                    modifier = Modifier.padding(12.dp),
                    onClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, orderReportText)
                        }
                        launcher.launch(Intent.createChooser(shareIntent, "Share order report"))
                    },
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Share,
                        contentDescription = stringResource(R.string.share_order_report),
                    )
                }
            }
        }
    ) { innerPadding ->
        SplitReceiptScreenView(
            modifier = modifier.padding(innerPadding),
            receiptData = receiptData,
            orderDataList = orderDataList,
            orderReportText = orderReportText,
            onSubtractOneQuantityClicked = { orderId ->
                splitReceiptViewModel.setEvent(
                    SplitReceiptEvent.RemoveOneQuantityToSpecificOrder(
                        orderId
                    )
                )
            },
            onAddOneQuantityClicked = { orderId ->
                splitReceiptViewModel.setEvent(
                    SplitReceiptEvent.AddOneQuantityToSpecificOrder(
                        orderId
                    )
                )
            },
            orderListState = orderListState,
            onEditReportClicked = { showAdditionalSumDialog = true }
        )

        if (showAdditionalSumDialog) {
            AdditionalSumDialog(
                onDismissRequest = { showAdditionalSumDialog = false },
                onAddItemClicked = { pair ->
                    splitReceiptViewModel.setEvent(SplitReceiptEvent.AddAdditionalSum(pair))
                },
                additionalSumList = { receiptData?.additionalSumList ?: emptyList() },
                onRemoveItemClicked = { pair ->
                    splitReceiptViewModel.setEvent(SplitReceiptEvent.RemoveAdditionalSum(pair))
                }
            )
        }
    }
}

private const val AMOUNT_OF_ELEMENTS = 1