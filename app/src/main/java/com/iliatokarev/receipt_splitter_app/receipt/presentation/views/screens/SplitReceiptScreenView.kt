package com.iliatokarev.receipt_splitter_app.receipt.presentation.views.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iliatokarev.receipt_splitter_app.R
import com.iliatokarev.receipt_splitter_app.main.basic.isNotZero
import com.iliatokarev.receipt_splitter_app.main.basic.shimmerBrush
import com.iliatokarev.receipt_splitter_app.receipt.presentation.OrderData
import com.iliatokarev.receipt_splitter_app.receipt.presentation.ReceiptData
import com.iliatokarev.receipt_splitter_app.receipt.presentation.views.basic.OrderItemView

@Composable
internal fun SplitReceiptScreenView(
    modifier: Modifier = Modifier,
    receiptData: ReceiptData?,
    orderDataList: List<OrderData>,
    orderReportText: String?,
    onSubtractOneQuantityClicked: (orderId: Long) -> Unit,
    onAddOneQuantityClicked: (orderId: Long) -> Unit,
    orderListState: LazyListState,
    onEditReportClicked: () -> Unit,
) {
    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        receiptData?.let { receipt ->
            SplitReceiptView(
                receiptData = receipt,
                orderDataList = orderDataList,
                orderReportText = orderReportText,
                onSubtractOneQuantityClicked = { orderId ->
                    onSubtractOneQuantityClicked(orderId)
                },
                onAddOneQuantityClicked = { orderId ->
                    onAddOneQuantityClicked(orderId)
                },
                orderListState = orderListState,
                onEditReportClicked = { onEditReportClicked() }
            )
        } ?: ShimmedSplitReceiptsScreenView()
    }
}

@Composable
private fun SplitReceiptView(
    modifier: Modifier = Modifier,
    receiptData: ReceiptData,
    orderDataList: List<OrderData> = emptyList(),
    orderReportText: String? = null,
    onSubtractOneQuantityClicked: (orderId: Long) -> Unit = {},
    onAddOneQuantityClicked: (orderId: Long) -> Unit = {},
    orderListState: LazyListState = rememberLazyListState(),
    onEditReportClicked: () -> Unit = {},
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp),
        state = orderListState,
    ) {
        item {
            ReceiptInfoView(receiptData = receiptData)
        }
        items(orderDataList.size) { index ->
            val orderData = orderDataList[index]
            SplitItemView(
                orderData = orderData,
                onSubtractQuantityClicked = { onSubtractOneQuantityClicked(orderData.id) },
                onAddOneQuantityClicked = { onAddOneQuantityClicked(orderData.id) }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        item {
            ReportBottomSheetView(
                orderReportText = orderReportText,
                onEditReportClicked = { onEditReportClicked() }
            )
        }
    }
}

@Composable
private fun ReceiptInfoView(
    modifier: Modifier = Modifier,
    receiptData: ReceiptData,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            fontWeight = FontWeight.Medium,
            fontSize = 24.sp,
            text = receiptData.receiptName,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(4.dp))

        receiptData.translatedReceiptName?.let { translatedRestaurant ->
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                fontSize = 20.sp,
                text = translatedRestaurant,
                textAlign = TextAlign.Center,
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            fontWeight = FontWeight.Normal,
            fontSize = 20.sp,
            text = receiptData.date,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(8.dp))

        receiptData.discount?.let { discount ->
            Text(
                fontSize = 20.sp,
                text = stringResource(R.string.discount_number, discount),
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        receiptData.tip?.let { tip ->
            Text(
                fontSize = 20.sp,
                text = stringResource(R.string.tip_number, tip),
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        receiptData.tax?.let { tax ->
            Text(
                fontSize = 20.sp,
                text = stringResource(R.string.tax_number, tax),
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Text(
            fontSize = 20.sp,
            text = stringResource(R.string.total_sum, receiptData.total),
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun SplitItemView(
    modifier: Modifier = Modifier,
    orderData: OrderData,
    onSubtractQuantityClicked: () -> Unit,
    onAddOneQuantityClicked: () -> Unit,
) {
    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 4.dp, start = 8.dp, end = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            OrderItemView(orderData = { orderData })
            Spacer(modifier = Modifier.height(4.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(4.dp))
            SplitOrderItemView(
                onSubtractOrderClicked = { onSubtractQuantityClicked() },
                onAddOneQuantityClicked = { onAddOneQuantityClicked() },
                quantity = orderData.selectedQuantity,
                isAddButtonEnabled = orderData.selectedQuantity < orderData.quantity,
                isSubtractButtonEnabled = orderData.selectedQuantity.isNotZero(),
            )
        }
    }
}

@Composable
private fun SplitOrderItemView(
    modifier: Modifier = Modifier,
    onSubtractOrderClicked: () -> Unit,
    onAddOneQuantityClicked: () -> Unit,
    quantity: Int,
    isAddButtonEnabled: Boolean,
    isSubtractButtonEnabled: Boolean,
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(
            enabled = isSubtractButtonEnabled,
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

        AnimatedContent(
            targetState = quantity.toString(),
        ) { quantity ->
            Text(
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium,
                text = quantity,
            )
        }

        Spacer(modifier = modifier.width(8.dp))

        IconButton(
            enabled = isAddButtonEnabled,
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
    orderReportText: String?,
    onEditReportClicked: () -> Unit,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        orderReportText?.let { orderText ->
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(
                    onClick = { onEditReportClicked() }
                ) {
                    Icon(Icons.Outlined.Edit, stringResource(R.string.edit_order_report_button))
                }
                Text(
                    textAlign = TextAlign.Left,
                    text = orderText,
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))
        } ?: run {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                fontSize = 20.sp,
                fontWeight = FontWeight.Normal,
                text = stringResource(R.string.no_order_report),
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun ShimmedSplitReceiptsScreenView(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp)
                .background(brush = shimmerBrush(), shape = RoundedCornerShape(16.dp))
        )
        Spacer(modifier = Modifier.height(32.dp))

        repeat(4) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(brush = shimmerBrush(), shape = RoundedCornerShape(16.dp))
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun SplitReceiptScreenViewPreview() {
    SplitReceiptView(
        receiptData =
            ReceiptData(
                id = 1,
                receiptName = "restaurant abdi paluma kulupa group",
                date = "18/03/2024",
                total = 60.0f,
                tax = null,
                discount = null,
                tip = null,
            ),
        orderDataList =
            listOf(
                OrderData(
                    id = 1,
                    name = "order1",
                    translatedName = "заказ 1",
                    quantity = 79,
                    price = 100000.0f,
                    receiptId = 1,
                ),
                OrderData(
                    id = 2,
                    name = "order2",
                    quantity = 2,
                    price = 20.0f,
                    receiptId = 1,
                ),
                OrderData(
                    id = 3,
                    name = "order3 fdgdf dfgfdg dfgdfg erter xcxv sdfdsf sdfsdf asd jyhn vcvf erret fgdfg",
                    translatedName = "перевод 1223 паошпов вошвоп вопшавоп ушегуре впргвр",
                    quantity = 3,
                    price = 30.0f,
                    receiptId = 1,
                ),
            ),
        orderReportText = null,
    )
}