package com.example.receipt_splitter.receipt.presentation.screens

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.receipt_splitter.R
import com.example.receipt_splitter.receipt.presentation.ReceiptEvent
import com.example.receipt_splitter.receipt.presentation.ReceiptViewModel
import com.example.receipt_splitter.receipt.presentation.viewmodels.SplitReceiptEvent
import com.example.receipt_splitter.receipt.presentation.viewmodels.SplitReceiptViewModel
import com.example.receipt_splitter.receipt.presentation.views.screens.SplitReceiptScreenView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SplitReceiptScreen(
    modifier: Modifier = Modifier,
    receiptViewModel: ReceiptViewModel,
    splitReceiptViewModel: SplitReceiptViewModel,
) {
    val receiptData by splitReceiptViewModel.getSplitReceiptData().collectAsStateWithLifecycle()
    val orderDataList by splitReceiptViewModel.getOrderDataList().collectAsStateWithLifecycle()
    val orderReportText by splitReceiptViewModel.getOrderReportText().collectAsStateWithLifecycle()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {}

    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

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

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        maxLines = 1,
                        text = receiptData?.restaurant
                            ?: stringResource(R.string.split_the_receipt),
                        overflow = TextOverflow.Ellipsis
                    )
                },
                scrollBehavior = scrollBehavior,
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
                enter = scaleIn(),
                exit = scaleOut(),

                ) {
                FloatingActionButton(
                    modifier = modifier
                        .padding(12.dp)
                        .animateContentSize(),
                    onClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, orderReportText)
                        }
                        launcher.launch(Intent.createChooser(shareIntent, "Share order report"))
                    }
                ) {
                    if (isOrderListAtBottom.value)
                        Text(
                            modifier = modifier.animateEnterExit(
                                enter = scaleIn(),
                                exit = scaleOut(),
                            ),
                            text = stringResource(R.string.share)
                        )
                    else
                        Icon(
                            modifier = modifier.animateEnterExit(
                                enter = scaleIn(),
                                exit = scaleOut(),
                            ),
                            imageVector = Icons.Outlined.Share,
                            contentDescription = stringResource(R.string.share_order_report)
                        )
                }
            }
        }
    ) { innerPadding ->
        SplitReceiptScreenView(
            modifier = modifier.padding(innerPadding),
            receiptData = { receiptData },
            orderDataList = { orderDataList },
            orderReportText = { orderReportText },
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
            orderListState = ordersListState,
        )
    }
}