package com.iliatokarev.receipt_splitter_app.receipt.presentation.screens

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.iliatokarev.receipt_splitter_app.R
import com.iliatokarev.receipt_splitter_app.receipt.presentation.ReceiptEvent
import com.iliatokarev.receipt_splitter_app.receipt.presentation.ReceiptUIConstants
import com.iliatokarev.receipt_splitter_app.receipt.presentation.ReceiptViewModel
import com.iliatokarev.receipt_splitter_app.receipt.presentation.viewmodels.SplitReceiptForOneEvent
import com.iliatokarev.receipt_splitter_app.receipt.presentation.viewmodels.SplitReceiptForOneViewModel
import com.iliatokarev.receipt_splitter_app.receipt.presentation.views.basic.BackNavigationButton
import com.iliatokarev.receipt_splitter_app.receipt.presentation.views.dialogs.AcceptClearingDialog
import com.iliatokarev.receipt_splitter_app.receipt.presentation.views.screens.SplitReceiptForOneScreenView
import com.iliatokarev.receipt_splitter_app.receipt.presentation.views.screens.TopAppBarSplitReceiptForOneSubmenuBox
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce

@OptIn(ExperimentalMaterial3Api::class, FlowPreview::class)
@Composable
internal fun SplitReceiptForOneScreen(
    modifier: Modifier = Modifier,
    receiptViewModel: ReceiptViewModel,
    splitReceiptForOneViewModel: SplitReceiptForOneViewModel,
    topAppBarState: TopAppBarState = rememberTopAppBarState(),
) {
    val receiptData by splitReceiptForOneViewModel.getSplitReceiptData()
        .collectAsStateWithLifecycle()
    val orderDataList by splitReceiptForOneViewModel.getOrderDataList()
        .collectAsStateWithLifecycle()
    var orderReportText by rememberSaveable { mutableStateOf<String?>(null) }
    var isShownAdditionalSumDialog by rememberSaveable { mutableStateOf(false) }
    var isShownClearOrderReportDialog by rememberSaveable { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {}

    val scrollBehavior =
        TopAppBarDefaults.enterAlwaysScrollBehavior(topAppBarState)

    LaunchedEffect(key1 = Unit) {
        splitReceiptForOneViewModel.getOrderReportText()
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
                        text = stringResource(R.string.split_the_receipt_for_one),
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                navigationIcon = {
                    BackNavigationButton { receiptViewModel.setEvent(ReceiptEvent.GoBack) }
                },
                actions = {
                    TopAppBarSplitReceiptForOneSubmenuBox(
                        onEditReceiptClick = {
                            receiptData?.id?.let { id ->
                                receiptViewModel.setEvent(
                                    ReceiptEvent.OpenEditReceiptsScreen(
                                        receiptId = id
                                    )
                                )
                            }
                        },
                        onClearReportClick = { isShownClearOrderReportDialog = true }
                    )
                },
            )
        },
        floatingActionButton = {},
    ) { innerPadding ->
        SplitReceiptForOneScreenView(
            modifier = modifier.padding(innerPadding),
            receiptData = receiptData,
            orderDataList = orderDataList,
            orderReportText = orderReportText,
            onSubtractOneQuantityClicked = { orderId ->
                splitReceiptForOneViewModel.setEvent(
                    SplitReceiptForOneEvent.RemoveOneQuantityToSpecificOrder(
                        orderId
                    )
                )
            },
            onAddOneQuantityClicked = { orderId ->
                splitReceiptForOneViewModel.setEvent(
                    SplitReceiptForOneEvent.AddOneQuantityToSpecificOrder(
                        orderId
                    )
                )
            },
            onShareReportClicked = {
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, orderReportText)
                }
                launcher.launch(Intent.createChooser(shareIntent, "Share order report"))
            },
        )
    }

    if (isShownClearOrderReportDialog) {
        AcceptClearingDialog(
            onDismissRequest = { isShownClearOrderReportDialog = false },
            onClearClicked = {
                splitReceiptForOneViewModel.setEvent(SplitReceiptForOneEvent.ClearOrderReport)
                isShownClearOrderReportDialog = false
            },
            infoText = stringResource(R.string.clear_report_text),
        )
    }
}