package com.example.receipt_splitter.receipt.presentation.screens

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.receipt_splitter.R
import com.example.receipt_splitter.main.basic.isNotZero
import com.example.receipt_splitter.receipt.presentation.ReceiptUiEvent
import com.example.receipt_splitter.receipt.presentation.ReceiptViewModel
import com.example.receipt_splitter.receipt.presentation.SplitOrderData
import com.example.receipt_splitter.receipt.presentation.SplitReceiptData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SplitReceiptScreen(
    modifier: Modifier = Modifier,
    receiptViewModel: ReceiptViewModel,
) {
    val splitReceiptData by receiptViewModel.getReceiptData().collectAsState()
    val splitReceiptDataList by receiptViewModel.getSplitReceiptItems().collectAsState()
    val orderReportText by receiptViewModel.getOrderReportText().collectAsState()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {}

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        maxLines = 1,
                        text = splitReceiptData?.restaurant
                            ?: stringResource(R.string.split_the_receipt)
                    )
                },
                scrollBehavior = scrollBehavior,
            )
        }
    ) { innerPadding ->
        SplitReceiptView(
            modifier = modifier.padding(innerPadding),
            splitReceiptData = { splitReceiptData },
            splitOrderDataList = { splitReceiptDataList },
            orderReportText = { orderReportText },
            onSubtractOneQuantityClicked = { orderId ->
                receiptViewModel.setUiEvent(ReceiptUiEvent.SubtractQuantityToSplitOrderData(orderId))
            },
            onAddOneQuantityClicked = { orderId ->
                receiptViewModel.setUiEvent(ReceiptUiEvent.AddQuantityToSplitOrderData(orderId))
            },
            onShareOrderReportText = {
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, orderReportText)
                }
                launcher.launch(Intent.createChooser(shareIntent, "Share order report"))
            }
        )
    }
}

@Composable
private fun SplitReceiptView(
    modifier: Modifier = Modifier,
    splitReceiptData: () -> SplitReceiptData?,
    splitOrderDataList: () -> List<SplitOrderData>,
    orderReportText: () -> String?,
    onSubtractOneQuantityClicked: (orderId: Long) -> Unit = {},
    onAddOneQuantityClicked: (orderId: Long) -> Unit = {},
    onShareOrderReportText: () -> Unit = {},
) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        item {
            splitReceiptData()?.let { data ->
                ReceiptInfoView(splitReceiptData = { data })
            }
        }
        items(splitOrderDataList().size) { index ->
            SplitItemView(
                splitOrderData = { splitOrderDataList()[index] },
                onSubtractQuantityClicked = { onSubtractOneQuantityClicked(splitOrderDataList()[index].id) },
                onAddOneQuantityClicked = { onAddOneQuantityClicked(splitOrderDataList()[index].id) }
            )
        }
        item {
            ReportBottomSheetView(
                orderReportText = { orderReportText() },
                onShareOrderReportText = { onShareOrderReportText() },
            )
        }
    }
}

@Composable
private fun ReceiptInfoView(
    modifier: Modifier = Modifier,
    splitReceiptData: () -> SplitReceiptData,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            fontSize = 24.sp,
            text = splitReceiptData().restaurant
        )
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            fontSize = 20.sp,
            text = splitReceiptData().date
        )
        Spacer(modifier = Modifier.height(8.dp))

        splitReceiptData().subTotal?.let { subTotal ->
            Text(
                fontSize = 18.sp,
                text = stringResource(R.string.sub_total_sum, subTotal)
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        splitReceiptData().discount?.let { discount ->
            Text(
                fontSize = 18.sp,
                text = stringResource(R.string.discount, discount)
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        splitReceiptData().tax?.let { tax ->
            Text(
                fontSize = 18.sp,
                text = stringResource(R.string.tax, tax)
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        splitReceiptData().total?.let { total ->
            Text(
                fontSize = 18.sp,
                text = stringResource(R.string.total_sum, total)
            )
        }
    }
}

@Composable
private fun SplitItemView(
    modifier: Modifier = Modifier,
    splitOrderData: () -> SplitOrderData,
    onSubtractQuantityClicked: () -> Unit,
    onAddOneQuantityClicked: () -> Unit,
) {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 4.dp, start = 8.dp, end = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            OrderItemView(splitOrderData = { splitOrderData() })
            Spacer(modifier = modifier.height(4.dp))
            HorizontalDivider()
            Spacer(modifier = modifier.height(4.dp))
            SplitOrderItemView(
                onSubtractOrderClicked = { onSubtractQuantityClicked() },
                onAddOneQuantityClicked = { onAddOneQuantityClicked() },
                quantity = { splitOrderData().selectedQuantity },
                isAddButtonEnabled = { splitOrderData().selectedQuantity < splitOrderData().quantity },
                isSubtractButtonEnabled = { splitOrderData().selectedQuantity.isNotZero() },
            )
        }
    }
}

@Composable
private fun OrderItemView(
    modifier: Modifier = Modifier,
    splitOrderData: () -> SplitOrderData,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = modifier
                .padding(end = 8.dp)
                .weight(20f),
            text = splitOrderData().name,
            fontSize = 18.sp,
            textAlign = TextAlign.Left,
        )
        Text(
            modifier = modifier.weight(3f),
            text = stringResource(R.string.quantity, splitOrderData().quantity),
            fontSize = 18.sp,
            textAlign = TextAlign.Left,
        )
        Text(
            modifier = modifier.weight(7f),
            text = splitOrderData().price.toString(),
            fontSize = 18.sp,
            textAlign = TextAlign.End,
        )
    }
}

@Composable
private fun SplitOrderItemView(
    modifier: Modifier = Modifier,
    onSubtractOrderClicked: () -> Unit,
    onAddOneQuantityClicked: () -> Unit,
    quantity: () -> Int,
    isAddButtonEnabled: () -> Boolean,
    isSubtractButtonEnabled: () -> Boolean,
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(
            enabled = isSubtractButtonEnabled(),
            onClick = {
                onSubtractOrderClicked()
            }
        ) {
            Icon(
                Icons.Default.KeyboardArrowDown,
                contentDescription = stringResource(R.string.clear_quantity_button)
            )
        }

        Spacer(modifier = modifier.width(8.dp))
        Text(
            fontSize = 24.sp,
            fontWeight = FontWeight.Medium,
            text = quantity().toString()
        )
        Spacer(modifier = modifier.width(8.dp))

        IconButton(
            enabled = isAddButtonEnabled(),
            onClick = { onAddOneQuantityClicked() }
        ) {
            Icon(
                Icons.Default.KeyboardArrowUp,
                contentDescription = stringResource(R.string.add_one_quantity_button)
            )
        }
    }
}

@Composable
private fun ReportBottomSheetView(
    modifier: Modifier = Modifier,
    orderReportText: () -> String?,
    onShareOrderReportText: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        HorizontalDivider(modifier = modifier.fillMaxWidth())
        Spacer(modifier = modifier.height(12.dp))
        orderReportText()?.let { orderText ->
            Text(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                textAlign = TextAlign.Right,
                text = orderText,
            )
            Spacer(modifier = modifier.height(12.dp))
            HorizontalDivider(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            )
            Spacer(modifier = modifier.height(12.dp))
            OutlinedButton(onClick = { onShareOrderReportText() }) {
                Icon(
                    Icons.Filled.Share,
                    contentDescription = stringResource(R.string.share_order_report)
                )
                Spacer(modifier = modifier.width(8.dp))
                Text(
                    fontSize = 24.sp,
                    text = stringResource(R.string.share_order_report),
                )
            }
        } ?: run {
            Spacer(modifier = modifier.height(12.dp))
            Text(
                fontSize = 24.sp,
                text = stringResource(R.string.no_order_report),
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun SplitReceiptViewPreview() {
    SplitReceiptView(
        splitReceiptData = {
            SplitReceiptData(
                id = 1,
                restaurant = "restaurant",
                date = "18/03/2024",
                subTotal = 60.0f,
                total = 60.0f,
                tax = null,
                discount = null,
                orders = emptyList(),
                tip = null,
                tipSum = null,
            )
        },
        splitOrderDataList = {
            listOf(
                SplitOrderData(
                    id = 1,
                    name = "order1",
                    quantity = 79,
                    price = 100000.0f
                ),
                SplitOrderData(
                    id = 2,
                    name = "order2",
                    quantity = 2,
                    price = 20.0f
                ),
                SplitOrderData(
                    id = 3,
                    name = "order3 fdgdf dfgfdg dfgdfg erter xcxv sdfdsf sdfsdf asd jyhn vcvf erret fgdfg",
                    quantity = 3,
                    price = 30.0f
                ),
            )
        },
        orderReportText = { "orderReportText" }
    )
}