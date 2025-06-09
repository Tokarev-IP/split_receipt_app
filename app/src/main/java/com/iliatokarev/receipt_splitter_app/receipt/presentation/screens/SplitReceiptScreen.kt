package com.iliatokarev.receipt_splitter_app.receipt.presentation.screens

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.Person
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.iliatokarev.receipt_splitter_app.R
import com.iliatokarev.receipt_splitter_app.receipt.presentation.ReceiptEvent
import com.iliatokarev.receipt_splitter_app.receipt.presentation.ReceiptUIConstants
import com.iliatokarev.receipt_splitter_app.receipt.presentation.ReceiptViewModel
import com.iliatokarev.receipt_splitter_app.receipt.presentation.viewmodels.SplitReceiptForAllEvents
import com.iliatokarev.receipt_splitter_app.receipt.presentation.viewmodels.SplitReceiptForAllUiMessageIntent
import com.iliatokarev.receipt_splitter_app.receipt.presentation.viewmodels.SplitReceiptForAllViewModel
import com.iliatokarev.receipt_splitter_app.receipt.presentation.viewmodels.SplitReceiptForOneEvent
import com.iliatokarev.receipt_splitter_app.receipt.presentation.viewmodels.SplitReceiptForOneViewModel
import com.iliatokarev.receipt_splitter_app.receipt.presentation.views.dialogs.AcceptClearingDialog
import com.iliatokarev.receipt_splitter_app.receipt.presentation.views.dialogs.AdditionalSumDialog
import com.iliatokarev.receipt_splitter_app.receipt.presentation.views.dialogs.SetConsumerNameDialog
import com.iliatokarev.receipt_splitter_app.receipt.presentation.views.screens.SplitReceiptForAllScreenView
import com.iliatokarev.receipt_splitter_app.receipt.presentation.views.screens.SplitReceiptForOneScreenView
import com.iliatokarev.receipt_splitter_app.receipt.presentation.views.screens.SplitReceiptSubmenuBox
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce

@OptIn(ExperimentalMaterial3Api::class, FlowPreview::class)
@Composable
fun SplitReceiptScreen(
    modifier: Modifier = Modifier,
    receiptViewModel: ReceiptViewModel,
    splitReceiptForOneViewModel: SplitReceiptForOneViewModel,
    splitReceiptForAllViewModel: SplitReceiptForAllViewModel,
    topAppBarState: TopAppBarState = rememberTopAppBarState(),
    localContext: Context = LocalContext.current,
) {
    val internalErrorText = stringResource(R.string.internal_error)

    var isShowReceiptForAll by rememberSaveable { mutableStateOf(true) }
    var isDataSavedState by rememberSaveable { mutableStateOf(false) }

    //Receipt For One VM
    val receiptDataForOne by splitReceiptForOneViewModel.getSplitReceiptData()
        .collectAsStateWithLifecycle()
    val orderDataListForOne by splitReceiptForOneViewModel.getOrderDataList()
        .collectAsStateWithLifecycle()
    var orderReportTextForOne by rememberSaveable { mutableStateOf<String?>(null) }
    var showAdditionalSumDialog by rememberSaveable { mutableStateOf(false) }
    var showClearOrderReportForOneDialog by rememberSaveable { mutableStateOf(false) }

    //Receipt For All VM
    val receiptDataForAll by splitReceiptForAllViewModel.getSplitReceiptData()
        .collectAsStateWithLifecycle()
    val orderDataSplitListForAll by splitReceiptForAllViewModel.getOrderDataSplitList()
        .collectAsStateWithLifecycle()
    val consumerNameList by splitReceiptForAllViewModel.getConsumerNameList()
        .collectAsStateWithLifecycle()
    var orderReportTextForAll by rememberSaveable { mutableStateOf<String?>(null) }
    var showSetConsumerNameDialog by rememberSaveable { mutableStateOf(false) }
    var showClearOrderReportForAllDialog by rememberSaveable { mutableStateOf(false) }
    val isCheckStateExisted by splitReceiptForAllViewModel.getIsCheckStateExisted()
        .collectAsStateWithLifecycle()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {}

    val scrollBehavior =
        TopAppBarDefaults.enterAlwaysScrollBehavior(topAppBarState)

    LaunchedEffect(key1 = Unit) {
        splitReceiptForAllViewModel.getUiMessageIntentFlow().collect { uiMessageState ->
            when (uiMessageState) {
                is SplitReceiptForAllUiMessageIntent.InternalError -> {
                    Toast.makeText(localContext, internalErrorText, Toast.LENGTH_SHORT).show()
                }

                is SplitReceiptForAllUiMessageIntent.DataWasSaved -> {
                    isDataSavedState = true
                }
            }
        }
    }

    LaunchedEffect(key1 = Unit) {
        splitReceiptForOneViewModel.getOrderReportText()
            .debounce(500L)
            .collect { text ->
                orderReportTextForOne = text
            }
    }

    LaunchedEffect(key1 = Unit) {
        splitReceiptForAllViewModel.getOrderReportText()
            .collect { text ->
                orderReportTextForAll = text
            }
    }

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                scrollBehavior = scrollBehavior,
                title = {
                    AnimatedContent(
                        targetState = isShowReceiptForAll,
                    ) { showForAll ->
                        if (showForAll)
                            Text(
                                maxLines = ReceiptUIConstants.ONE_LINE,
                                text = stringResource(R.string.split_the_receipt_for_all),
                                overflow = TextOverflow.Ellipsis,
                            )
                        else
                            Text(
                                maxLines = ReceiptUIConstants.ONE_LINE,
                                text = stringResource(R.string.split_the_receipt_for_one),
                                overflow = TextOverflow.Ellipsis,
                            )
                    }
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
                    SplitReceiptSubmenuBox(
                        onEditReceiptClick = {
                            receiptDataForOne?.id?.let { id ->
                                receiptViewModel.setEvent(
                                    ReceiptEvent.OpenEditReceiptsScreen(
                                        receiptId = id
                                    )
                                )
                            }
                        },
                        onSwapUiModesClick = {
                            isShowReceiptForAll = !isShowReceiptForAll
                        }
                    )
                }
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = isCheckStateExisted && isShowReceiptForAll,
                enter = scaleIn(),
                exit = scaleOut(),
            ) {
                FloatingActionButton(
                    modifier = Modifier.padding(12.dp),
                    onClick = {
                        showSetConsumerNameDialog = true
                    },
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = stringResource(R.string.set_consumer_name_button),
                    )
                }
            }
        }
    ) { innerPadding ->
        AnimatedContent(
            targetState = isShowReceiptForAll,
        ) { showReceiptForAll ->
            if (showReceiptForAll) {
                SplitReceiptForAllScreenView(
                    modifier = modifier.padding(innerPadding),
                    receiptData = receiptDataForAll,
                    orderDataSplitList = orderDataSplitListForAll,
                    orderReportText = orderReportTextForAll,
                    onShareOrderReportClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, orderReportTextForAll)
                        }
                        launcher.launch(Intent.createChooser(shareIntent, "Share order report"))
                    },
                    onClearOrderReportClick = { showClearOrderReportForAllDialog = true },
                    onCheckStateChange = { state, position ->
                        splitReceiptForAllViewModel.setEvent(
                            SplitReceiptForAllEvents.SetCheckState(
                                position = position,
                                state = state,
                            )
                        )
                        isDataSavedState = false
                    },
                    onRemoveConsumerNameClick = { position, consumerName ->
                        splitReceiptForAllViewModel.setEvent(
                            SplitReceiptForAllEvents.ClearSpecificConsumerName(
                                position = position,
                                name = consumerName,
                            )
                        )
                        isDataSavedState = false
                    },
                    onSaveOrderDataSplitClick = {
                        splitReceiptForAllViewModel.setEvent(SplitReceiptForAllEvents.SaveOrderDataSplit)
                    },
                    isSavedState = isDataSavedState,
                    onClearAllConsumerNamesClick = { position ->
                        splitReceiptForAllViewModel.setEvent(
                            SplitReceiptForAllEvents.ClearAllConsumerNames(position = position)
                        )
                    }
                )
            } else {
                SplitReceiptForOneScreenView(
                    modifier = modifier.padding(innerPadding),
                    receiptData = receiptDataForOne,
                    orderDataList = orderDataListForOne,
                    orderReportText = orderReportTextForOne,
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
                    onEditReportClicked = { showAdditionalSumDialog = true },
                    onClearReportClicked = { showClearOrderReportForOneDialog = true },
                    onShareReportClicked = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, orderReportTextForOne)
                        }
                        launcher.launch(Intent.createChooser(shareIntent, "Share order report"))
                    },
                )
            }
        }

        if (showAdditionalSumDialog && isShowReceiptForAll == false) {
            AdditionalSumDialog(
                onDismissRequest = { showAdditionalSumDialog = false },
                onAddItemClicked = { pair ->
                    splitReceiptForOneViewModel.setEvent(
                        SplitReceiptForOneEvent.AddAdditionalSum(pair = pair)
                    )
                },
                additionalSumList = { receiptDataForOne?.additionalSumList ?: emptyList() },
                onRemoveItemClicked = { pair ->
                    splitReceiptForOneViewModel.setEvent(
                        SplitReceiptForOneEvent.RemoveAdditionalSum(pair = pair)
                    )
                }
            )
        }

        if (showClearOrderReportForOneDialog && isShowReceiptForAll == false) {
            AcceptClearingDialog(
                onDismissRequest = { showClearOrderReportForOneDialog = false },
                onAcceptClicked = {
                    splitReceiptForOneViewModel.setEvent(SplitReceiptForOneEvent.ClearOrderReport)
                    showClearOrderReportForOneDialog = false
                },
                infoText = stringResource(R.string.clear_order_report_text),
            )
        }

        if (showSetConsumerNameDialog && isShowReceiptForAll) {
            SetConsumerNameDialog(
                consumerNamesList = consumerNameList,
                onDismissClick = { showSetConsumerNameDialog = false },
                onNameSelectedClick = { name ->
                    splitReceiptForAllViewModel.setEvent(
                        SplitReceiptForAllEvents.SetConsumerName(name)
                    )
                    showSetConsumerNameDialog = false
                }
            )
        }

        if (showClearOrderReportForAllDialog && isShowReceiptForAll) {
            AcceptClearingDialog(
                onDismissRequest = { showClearOrderReportForAllDialog = false },
                onAcceptClicked = {
                    splitReceiptForAllViewModel.setEvent(SplitReceiptForAllEvents.ClearOrderReport)
                    showClearOrderReportForAllDialog = false
                },
                infoText = stringResource(R.string.clear_order_report_text),
            )
        }
    }
}