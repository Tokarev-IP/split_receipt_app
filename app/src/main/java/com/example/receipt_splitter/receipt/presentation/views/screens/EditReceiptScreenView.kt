package com.example.receipt_splitter.receipt.presentation.views.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.receipt_splitter.R
import com.example.receipt_splitter.main.basic.shimmerBrush
import com.example.receipt_splitter.receipt.presentation.OrderData
import com.example.receipt_splitter.receipt.presentation.ReceiptData
import com.example.receipt_splitter.receipt.presentation.views.basic.OrderItemView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun EditReceiptScreenView(
    modifier: Modifier = Modifier,
    receiptData: ReceiptData?,
    orderDataList: List<OrderData>,
    onEditOrderClicked: (id: Long) -> Unit,
    onDeleteOrderClicked: (id: Long) -> Unit,
    onEditReceiptClicked: () -> Unit,
    onAddNewOrderClicked: () -> Unit,
) {
    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        receiptData?.let { receipt ->
            EditReceiptView(
                receiptData = receipt,
                orderDataList = orderDataList,
                onEditOrderClicked = { id -> onEditOrderClicked(id) },
                onDeleteOrderClicked = { id ->
                    onDeleteOrderClicked(id)
                },
                onEditReceiptClicked = { onEditReceiptClicked() },
                onAddNewOrderClicked = { onAddNewOrderClicked() },
            )
        } ?: ShimmedEditReceiptsScreenView()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditReceiptView(
    modifier: Modifier = Modifier,
    receiptData: ReceiptData = ReceiptData(id = 0),
    orderDataList: List<OrderData> = emptyList<OrderData>(),
    onEditOrderClicked: (id: Long) -> Unit = {},
    onDeleteOrderClicked: (id: Long) -> Unit = {},
    onEditReceiptClicked: () -> Unit = {},
    onAddNewOrderClicked: () -> Unit = {},
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp),
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            EditReceiptInfo(
                receiptData = receiptData,
                onEditReceiptClicked = { onEditReceiptClicked() },
            )
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(12.dp))
        }
        items(orderDataList.size) { index ->
            val orderData = orderDataList[index]
            OrderCardView(
                orderData = orderData,
                onDeleteOrderClicked = { onDeleteOrderClicked(orderData.id) },
                onEditOrderClicked = { onEditOrderClicked(orderData.id) },
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        item {
            Spacer(modifier = Modifier.height(8.dp))
            AddOrderView(
                onAddNewOrderClicked = { onAddNewOrderClicked() }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun EditReceiptInfo(
    modifier: Modifier = Modifier,
    receiptData: ReceiptData,
    onEditReceiptClicked: () -> Unit,
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = modifier
                    .weight(8f)
                    .animateContentSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                OutlinedTextField(
                    value = receiptData.receiptName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(text = stringResource(R.string.receipt_name)) }
                )
                Spacer(modifier = Modifier.height(8.dp))

                AnimatedVisibility(visible = expanded) {
                    Column {
                        OutlinedTextField(
                            value = receiptData.translatedReceiptName ?: "",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(text = stringResource(R.string.translated_receipt_name)) }
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = receiptData.date,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(text = stringResource(R.string.date)) }
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = receiptData.total.toString(),
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(text = stringResource(R.string.total)) }
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = receiptData.tax?.toString() ?: "",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(text = stringResource(R.string.tax)) }
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = receiptData.discount?.toString() ?: "",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(text = stringResource(R.string.discount)) }
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = receiptData.tip?.toString() ?: "",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(text = stringResource(R.string.tip)) }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            IconButton(
                modifier = modifier.weight(1f),
                onClick = { onEditReceiptClicked() },
            ) {
                Icon(Icons.Outlined.Edit, stringResource(R.string.edit_receipt_info_button))
            }
        }
        IconButton(
            onClick = { expanded = !expanded }
        ) {
            AnimatedContent(targetState = expanded) { expand ->
                if (expand)
                    Icon(
                        modifier = modifier.animateEnterExit(
                            enter = fadeIn(),
                            exit = fadeOut(),
                        ),
                        imageVector = Icons.Outlined.KeyboardArrowUp,
                        contentDescription = stringResource(R.string.expand_receipt_info_button),
                    )
                else
                    Icon(
                        modifier = modifier.animateEnterExit(
                            enter = fadeIn(),
                            exit = fadeOut(),
                        ),
                        imageVector = Icons.Outlined.KeyboardArrowDown,
                        contentDescription = stringResource(R.string.narrow_down_receipt_info_button)
                    )
            }
        }
    }
}

@Composable
private fun OrderCardView(
    modifier: Modifier = Modifier,
    orderData: OrderData,
    onDeleteOrderClicked: () -> Unit,
    onEditOrderClicked: () -> Unit,
) {
    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(
                onClick = { onDeleteOrderClicked() },
            ) {
                Icon(
                    Icons.Outlined.Delete,
                    contentDescription = stringResource(R.string.delete_order_button)
                )
            }
            IconButton(
                onClick = { onEditOrderClicked() },
            ) {
                Icon(
                    Icons.Outlined.Edit,
                    contentDescription = stringResource(R.string.edit_order_button)
                )
            }
        }
        Column(
            modifier = modifier.padding(horizontal = 12.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            OrderItemView(orderData = { orderData })
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun AddOrderView(
    modifier: Modifier = Modifier,
    onAddNewOrderClicked: () -> Unit,
) {
    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        OutlinedButton(
            onClick = { onAddNewOrderClicked() },
            modifier = modifier.align(Alignment.Center)
        ) {
            Icon(Icons.Outlined.Add, stringResource(R.string.add_new_order_button))
        }
    }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview(showBackground = true)
private fun EditReceiptViewPreview() {
    EditReceiptView(
        receiptData =
            ReceiptData(
                id = 1,
                receiptName = "restaurant",
                translatedReceiptName = "ресторан",
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
                    translatedName = "апаап апврара аврварапр паоокуасм иарар счмацйяссся сравнпоавл вавапвап ",
                    quantity = 3,
                    price = 30.0f,
                    receiptId = 1,
                ),
            ),
    )
}

