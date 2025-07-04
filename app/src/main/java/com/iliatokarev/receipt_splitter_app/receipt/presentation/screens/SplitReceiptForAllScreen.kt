package com.iliatokarev.receipt_splitter_app.receipt.presentation.screens

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
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
import com.iliatokarev.receipt_splitter_app.main.basic.icons.SaveIcon
import com.iliatokarev.receipt_splitter_app.receipt.presentation.ReceiptEvent
import com.iliatokarev.receipt_splitter_app.receipt.presentation.ReceiptViewModel
import com.iliatokarev.receipt_splitter_app.receipt.presentation.viewmodels.SplitReceiptForAllEvents
import com.iliatokarev.receipt_splitter_app.receipt.presentation.viewmodels.SplitReceiptForAllUiMessageIntent
import com.iliatokarev.receipt_splitter_app.receipt.presentation.viewmodels.SplitReceiptForAllViewModel
import com.iliatokarev.receipt_splitter_app.receipt.presentation.views.basic.BackNavigationButton
import com.iliatokarev.receipt_splitter_app.receipt.presentation.views.dialogs.AcceptClearingDialog
import com.iliatokarev.receipt_splitter_app.receipt.presentation.views.screens.SplitReceiptForAllScreenView
import com.iliatokarev.receipt_splitter_app.receipt.presentation.views.screens.TopAppBarSplitReceipt
import com.iliatokarev.receipt_splitter_app.receipt.presentation.views.sheets.SelectInitialConsumerNamesBottomSheet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, FlowPreview::class)
@Composable
fun SplitReceiptForAllScreen(
    modifier: Modifier = Modifier,
    receiptViewModel: ReceiptViewModel,
    splitReceiptForAllViewModel: SplitReceiptForAllViewModel,
    topAppBarState: TopAppBarState = rememberTopAppBarState(),
    localContext: Context = LocalContext.current,
    scope: CoroutineScope = rememberCoroutineScope(),
) {
    val internalErrorText = stringResource(R.string.internal_error)
    var doesDataNeedToSave by rememberSaveable { mutableStateOf(false) }
    var wasDataSaved by rememberSaveable { mutableStateOf(false) }

    val receiptData by splitReceiptForAllViewModel.getSplitReceiptData()
        .collectAsStateWithLifecycle()
    val orderDataSplitList by splitReceiptForAllViewModel.getOrderDataSplitList()
        .collectAsStateWithLifecycle()
    val allConsumerNamesList by splitReceiptForAllViewModel.getAllConsumerNamesList()
        .collectAsStateWithLifecycle()
    var orderReportText by rememberSaveable { mutableStateOf<String?>(null) }
    var isShownSelectConsumerNamesBottomSheet by rememberSaveable { mutableStateOf(false) }
    var isShownClearOrderReportDialog by rememberSaveable { mutableStateOf(false) }
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
                    scope.launch {
                        wasDataSaved = true
                        delay(1000)
                        doesDataNeedToSave = false
                        delay(300)
                        wasDataSaved = false
                    }
                }
            }
        }
    }

    LaunchedEffect(key1 = Unit) {
        splitReceiptForAllViewModel.getOrderReportText()
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
                        maxLines = ONE_LINE,
                        text = stringResource(R.string.split_the_receipt_for_all),
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                navigationIcon = {
                    BackNavigationButton { receiptViewModel.setEvent(ReceiptEvent.GoBack) }
                },
                actions = {
                    TopAppBarSplitReceipt(
                        onEditReceiptClick = {
                            receiptData?.id?.let { id ->
                                receiptViewModel.setEvent(
                                    ReceiptEvent.OpenEditReceiptsScreen(
                                        receiptId = id
                                    )
                                )
                            }
                        },
                        onSwapUiModesClick = {
                            receiptData?.id?.let { id ->
                                receiptViewModel.setEvent(
                                    ReceiptEvent.OpenSplitReceiptForOneScreen(
                                        receiptId = id
                                    )
                                )
                            }
                        },
                        onClearReportClick = { isShownClearOrderReportDialog = true },
                    )
                }
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = isCheckStateExisted,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                FloatingActionButton(
                    modifier = Modifier.padding(12.dp),
                    onClick = { isShownSelectConsumerNamesBottomSheet = true },
                ) {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = stringResource(R.string.set_consumer_name_button),
                    )
                }
            }
            AnimatedVisibility(
                modifier = Modifier.padding(12.dp),
                visible = isCheckStateExisted == false && doesDataNeedToSave,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                FloatingActionButton(
                    onClick = {
                        if (wasDataSaved == false)
                            splitReceiptForAllViewModel.setEvent(SplitReceiptForAllEvents.SaveOrderDataSplit)
                    }
                ) {
                    AnimatedContent(
                        targetState = wasDataSaved
                    ) { wasDataSaved ->
                        if (wasDataSaved)
                            Icon(Icons.Filled.Check, stringResource(R.string.save_button))
                        else
                            Icon(Icons.Filled.SaveIcon, stringResource(R.string.save_button))
                    }
                }
            }

        }
    ) { innerPadding ->
        SplitReceiptForAllScreenView(
            modifier = modifier.padding(innerPadding),
            receiptData = receiptData,
            orderDataSplitList = orderDataSplitList,
            orderReportText = orderReportText,
            onShareOrderReportClick = {
                receiptData?.let { receipt ->
                    splitReceiptForAllViewModel.setEvent(
                        SplitReceiptForAllEvents.SetIsSharedStateForReceipt(
                            receiptData = receipt
                        )
                    )
                }

                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, orderReportText)
                }
                launcher.launch(Intent.createChooser(shareIntent, "Share order report"))
            },
            onCheckStateChange = { state, position ->
                splitReceiptForAllViewModel.setEvent(
                    SplitReceiptForAllEvents.SetCheckState(
                        position = position,
                        state = state,
                    )
                )
            },
            onRemoveConsumerNameClick = { position, consumerName ->
                splitReceiptForAllViewModel.setEvent(
                    SplitReceiptForAllEvents.ClearConsumerNameForOrder(
                        position = position,
                        name = consumerName,
                    )
                )
                doesDataNeedToSave = true
            },
            onClearAllConsumerNamesClick = { position ->
                splitReceiptForAllViewModel.setEvent(
                    SplitReceiptForAllEvents.ClearAllConsumerNames(position = position)
                )
                doesDataNeedToSave = true
            },
            onAddConsumerNameForSpecificOrderClick = { position, name ->
                splitReceiptForAllViewModel.setEvent(
                    SplitReceiptForAllEvents.AddConsumerNameForSpecificOrder(
                        position = position,
                        name = name,
                    )
                )
                doesDataNeedToSave = true
            },
            allConsumerNamesList = allConsumerNamesList,
        )
    }

    if (isShownSelectConsumerNamesBottomSheet) {
        SelectInitialConsumerNamesBottomSheet(
            allConsumerNamesList = allConsumerNamesList,
            onDismissClick = { isShownSelectConsumerNamesBottomSheet = false },
            onSetSelectedNamesClick = { names ->
                splitReceiptForAllViewModel.setEvent(
                    SplitReceiptForAllEvents.SetInitialConsumerNamesForCheckedOrders(consumerNamesList = names)
                )
                doesDataNeedToSave = true
            },
        )
    }

    if (isShownClearOrderReportDialog) {
        AcceptClearingDialog(
            onDismissRequest = { isShownClearOrderReportDialog = false },
            onClearClicked = {
                splitReceiptForAllViewModel.setEvent(SplitReceiptForAllEvents.ClearOrderReport)
                isShownClearOrderReportDialog = false
                doesDataNeedToSave = true
            },
            infoText = stringResource(R.string.clear_report_text),
        )
    }
}

private const val ONE_LINE = 1