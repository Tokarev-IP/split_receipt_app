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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
internal fun SplitReceiptForOneScreenView(
    modifier: Modifier = Modifier,
    receiptData: ReceiptData?,
    orderDataList: List<OrderData>,
    orderReportText: String?,
    onSubtractOneQuantityClicked: (orderId: Long) -> Unit,
    onAddOneQuantityClicked: (orderId: Long) -> Unit,
    onEditReportClicked: () -> Unit,
    onClearReportClicked: () -> Unit,
    onShareReportClicked: () -> Unit,
) {
    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        receiptData?.let { receipt ->
            SplitReceiptForOneView(
                receiptData = receipt,
                orderDataList = orderDataList,
                orderReportText = orderReportText,
                onSubtractOneQuantityClicked = { orderId ->
                    onSubtractOneQuantityClicked(orderId)
                },
                onAddOneQuantityClicked = { orderId ->
                    onAddOneQuantityClicked(orderId)
                },
                onEditReportClicked = { onEditReportClicked() },
                onClearReportClicked = { onClearReportClicked() },
                onShareReportClicked = { onShareReportClicked() },
            )
        } ?: ShimmedSplitReceiptScreenView()
    }
}

@Composable
private fun SplitReceiptForOneView(
    modifier: Modifier = Modifier,
    receiptData: ReceiptData,
    orderDataList: List<OrderData> = emptyList(),
    orderReportText: String? = null,
    onSubtractOneQuantityClicked: (orderId: Long) -> Unit = {},
    onAddOneQuantityClicked: (orderId: Long) -> Unit = {},
    onEditReportClicked: () -> Unit = {},
    onClearReportClicked: () -> Unit = {},
    onShareReportClicked: () -> Unit = {},
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp),
    ) {
        item {
            Spacer(modifier = Modifier.height(20.dp))
            ReceiptInfoView(receiptData = receiptData)
            Spacer(modifier = Modifier.height(20.dp))
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
            Spacer(modifier = Modifier.height(8.dp))
            ReportBottomSheetView(
                orderReportText = orderReportText,
                onEditReportClicked = { onEditReportClicked() },
                onClearReportClicked = { onClearReportClicked() },
                onShareReportClicked = { onShareReportClicked() },
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
internal fun ReceiptInfoView(
    modifier: Modifier = Modifier,
    receiptData: ReceiptData,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
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
            OrderItemView(orderData = orderData)
            Spacer(modifier = Modifier.height(4.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(4.dp))
            SubtractAddQuantityView(
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
private fun SubtractAddQuantityView(
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
    onClearReportClicked: () -> Unit,
    onShareReportClicked: () -> Unit,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        HorizontalDivider()
        Spacer(modifier = Modifier.height(8.dp))

        orderReportText?.let { orderReport ->
            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Center,
                ) {
                    IconButton(
                        onClick = { onEditReportClicked() }
                    ) {
                        Icon(Icons.Outlined.Edit, stringResource(R.string.edit_order_report_button))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    IconButton(
                        onClick = { onClearReportClicked() }
                    ) {
                        Icon(
                            Icons.Outlined.Clear,
                            stringResource(R.string.clear_order_report_button)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Left,
                    text = orderReport,
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = { onShareReportClicked() }
            ) {
                Icon(Icons.Outlined.Share, stringResource(R.string.share_order_report_button))
            }
            Spacer(modifier = Modifier.height(8.dp))
        } ?: run {
            Text(
                fontSize = 20.sp,
                fontWeight = FontWeight.Normal,
                text = stringResource(R.string.order_report_is_empty),
            )

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
        }
    }
}

@Composable
private fun ShimmedSplitReceiptScreenView(
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
private fun SplitReceiptViewPreview() {
    SplitReceiptForOneView(
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
        orderReportText = "Report is not empty fdg d df fd  h dh hdf hf gdf dfg d gdf gdf g",
    )
}