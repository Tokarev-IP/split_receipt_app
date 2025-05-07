package com.example.receipt_splitter.receipt.presentation.views.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.receipt_splitter.R
import com.example.receipt_splitter.main.basic.shimmerBrush
import com.example.receipt_splitter.receipt.presentation.OrderData
import com.example.receipt_splitter.receipt.presentation.ReceiptData
import com.example.receipt_splitter.receipt.presentation.views.OrderItemView

@Composable
internal fun EditReceiptScreenView(
    modifier: Modifier = Modifier,
    receiptData: () -> ReceiptData?,
    orderDataList: () -> List<OrderData>,
    onEditOrderClicked: (id: Long) -> Unit,
    onEditReceiptClicked: () -> Unit,
    onDeleteOrderClicked: (id: Long) -> Unit,
    onSplitScreenClicked: (receiptId: Long) -> Unit,
    ordersListState: LazyListState,
) {
    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        receiptData()?.let { receipt ->
            EditReceiptView(
                receiptData = { receipt },
                orderDataList = { orderDataList() },
                onEditOrderClicked = { id -> onEditOrderClicked(id) },
                onEditReceiptClicked = { onEditReceiptClicked() },
                onDeleteOrderClicked = { id ->
                    onDeleteOrderClicked(id)
                },
                onSplitScreenClicked = { onSplitScreenClicked(receipt.id) },
                ordersListState = ordersListState,
            )
        } ?: ShimmedEditReceiptsScreenView()
    }
}

@Composable
private fun EditReceiptView(
    modifier: Modifier = Modifier,
    receiptData: () -> ReceiptData = { ReceiptData(id = 0) },
    orderDataList: () -> List<OrderData> = { emptyList<OrderData>() },
    onEditOrderClicked: (id: Long) -> Unit = {},
    onEditReceiptClicked: () -> Unit = {},
    onDeleteOrderClicked: (id: Long) -> Unit = {},
    onSplitScreenClicked: () -> Unit = {},
    ordersListState: LazyListState = rememberLazyListState(),
) {
    val receiptData = receiptData()
    val orderDataList = orderDataList()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        state = ordersListState,
    ) {
        item {
            Spacer(modifier = modifier.height(8.dp))
            EditReceiptInfo(
                receiptData = { receiptData },
                onEditReceiptClicked = { onEditReceiptClicked() }
            )
            Spacer(modifier = modifier.height(8.dp))
            HorizontalDivider(modifier = modifier.fillMaxWidth())
            Spacer(modifier = modifier.height(8.dp))
        }
        items(orderDataList.size) {
            val orderData = orderDataList[it]
            OrderCardView(
                orderData = { orderData },
                onDeleteOrderClicked = { onDeleteOrderClicked(orderData.id) },
                onEditOrderClicked = { onEditOrderClicked(orderData.id) },
            )
            Spacer(modifier = modifier.height(8.dp))
        }
        item {
            GoToSplitScreenButtonView(
                onSplitScreenClicked = { onSplitScreenClicked() }
            )
        }
    }
}

@Composable
private fun EditReceiptInfo(
    modifier: Modifier = Modifier,
    receiptData: () -> ReceiptData,
    onEditReceiptClicked: () -> Unit,
) {
    val receiptData = receiptData()

    Box(
        modifier = modifier.fillMaxWidth(),
    ) {
        EditReceiptInfoView(
            modifier = modifier.align(Alignment.TopCenter),
            receiptData = { receiptData }
        )
        IconButton(
            modifier = modifier.align(Alignment.TopEnd),
            onClick = { onEditReceiptClicked() },
        ) {
            Icon(
                Icons.Outlined.Edit,
                contentDescription = stringResource(R.string.edit_receipt_button)
            )
        }
    }
}

@Composable
private fun EditReceiptInfoView(
    modifier: Modifier = Modifier,
    receiptData: () -> ReceiptData,
) {
    val receiptData = receiptData()
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = modifier.height(36.dp))
        OutlinedTextField(
            value = receiptData.restaurant,
            onValueChange = {},
            readOnly = true,
            label = { Text(text = stringResource(R.string.restaurant_name)) }
        )
        Spacer(modifier = modifier.height(8.dp))

        OutlinedTextField(
            value = receiptData.translatedRestaurant ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text(text = stringResource(R.string.translated_restaurant_name)) }
        )
        Spacer(modifier = modifier.height(8.dp))

        OutlinedTextField(
            value = receiptData.date,
            onValueChange = {},
            readOnly = true,
            label = { Text(text = stringResource(R.string.date)) }
        )
        Spacer(modifier = modifier.height(8.dp))

        OutlinedTextField(
            value = receiptData.total.toString(),
            onValueChange = {},
            readOnly = true,
            label = { Text(text = stringResource(R.string.total)) }
        )
        Spacer(modifier = modifier.height(8.dp))

        OutlinedTextField(
            value = receiptData.tax?.toString() ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text(text = stringResource(R.string.tax)) }
        )
        Spacer(modifier = modifier.height(8.dp))

        OutlinedTextField(
            value = receiptData.discount?.toString() ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text(text = stringResource(R.string.discount)) }
        )
        Spacer(modifier = modifier.height(8.dp))

        OutlinedTextField(
            value = receiptData.tip?.toString() ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text(text = stringResource(R.string.tip)) }
        )
        Spacer(modifier = modifier.height(8.dp))
    }
}

@Composable
private fun OrderCardView(
    modifier: Modifier = Modifier,
    orderData: () -> OrderData,
    onDeleteOrderClicked: () -> Unit,
    onEditOrderClicked: () -> Unit,
) {
    val orderData = orderData()

    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
    ) {
        Box(
            modifier = modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = modifier
                    .padding(horizontal = 12.dp)
                    .align(Alignment.TopCenter),
            ) {
                Spacer(modifier = modifier.height(56.dp))
                OrderItemView(orderData = { orderData })
                Spacer(modifier = modifier.height(8.dp))
            }

            IconButton(
                modifier = modifier.align(Alignment.TopStart),
                onClick = { onDeleteOrderClicked() },
            ) {
                Icon(
                    Icons.Outlined.Delete,
                    contentDescription = stringResource(R.string.delete_order_button)
                )
            }

            IconButton(
                modifier = modifier.align(Alignment.TopEnd),
                onClick = { onEditOrderClicked() },
            ) {
                Icon(
                    Icons.Outlined.Edit,
                    contentDescription = stringResource(R.string.edit_order_button)
                )
            }
        }
    }
}

@Composable
private fun GoToSplitScreenButtonView(
    modifier: Modifier = Modifier,
    onSplitScreenClicked: () -> Unit,
) {
    Spacer(modifier = modifier.height(12.dp))
    Button(
        modifier = modifier.fillMaxWidth(),
        onClick = { onSplitScreenClicked() }
    ) {
        Text(text = stringResource(R.string.go_to_split_the_receipt))
    }
    Spacer(modifier = modifier.height(12.dp))
}

@Composable
private fun ShimmedEditReceiptsScreenView(
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
                .height(520.dp)
                .background(brush = shimmerBrush(), shape = RoundedCornerShape(16.dp))
        )
        Spacer(modifier = Modifier.height(32.dp))

        repeat(3) {
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
private fun EditReceiptViewPreview() {
    EditReceiptView(
        receiptData = {
            ReceiptData(
                id = 1,
                restaurant = "restaurant",
                translatedRestaurant = "ресторан",
                date = "18/03/2024",
                total = 60.0f,
                tax = null,
                discount = null,
                tip = null,
                tipSum = null,
            )
        },
        orderDataList = {
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
                    translatedName = "апаап апврара аврварапр паоокуасм иарар счмацйяссся сравнпоавл вавапвап ",
                    quantity = 3,
                    price = 30.0f,
                    receiptId = 1,
                ),
            )
        }
    )
}

