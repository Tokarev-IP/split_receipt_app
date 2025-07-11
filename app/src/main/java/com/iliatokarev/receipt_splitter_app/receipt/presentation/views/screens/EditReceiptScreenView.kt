package com.iliatokarev.receipt_splitter_app.receipt.presentation.views.screens

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iliatokarev.receipt_splitter_app.R
import com.iliatokarev.receipt_splitter_app.main.basic.icons.Receipt
import com.iliatokarev.receipt_splitter_app.main.basic.shimmerBrush
import com.iliatokarev.receipt_splitter_app.receipt.data.services.DataConstantsReceipt.MAXIMUM_AMOUNT_OF_DISHES
import com.iliatokarev.receipt_splitter_app.receipt.presentation.OrderData
import com.iliatokarev.receipt_splitter_app.receipt.presentation.ReceiptData
import com.iliatokarev.receipt_splitter_app.receipt.presentation.views.basic.OrderItemView

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
    goToSplitReceiptScreenClick: () -> Unit,
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
                goToSplitReceiptScreenClick = { goToSplitReceiptScreenClick() },
            )
        } ?: ShimmedEditReceiptsScreenView()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditReceiptView(
    modifier: Modifier = Modifier,
    receiptData: ReceiptData = ReceiptData(id = 0),
    orderDataList: List<OrderData> = emptyList(),
    onEditOrderClicked: (id: Long) -> Unit = {},
    onDeleteOrderClicked: (id: Long) -> Unit = {},
    onEditReceiptClicked: () -> Unit = {},
    onAddNewOrderClicked: () -> Unit = {},
    goToSplitReceiptScreenClick: () -> Unit = {},
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp),
    ) {
        item {
            Spacer(modifier = Modifier.height(12.dp))
            EditReceiptInfo(
                receiptData = receiptData,
                onEditReceiptClicked = { onEditReceiptClicked() },
            )
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))
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
            BottomActionsView(
                onAddNewOrderClicked = { onAddNewOrderClicked() },
                enabled = orderDataList.size < MAXIMUM_AMOUNT_OF_DISHES,
                goToSplitReceiptScreenClick = { goToSplitReceiptScreenClick() },
            )
            Spacer(modifier = Modifier.height(20.dp))
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
                Text(
                    text = stringResource(R.string.name_is, receiptData.receiptName),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(8.dp))

                AnimatedVisibility(visible = expanded) {
                    Column(
                        modifier = modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        receiptData.translatedReceiptName?.let { translatedName ->
                            Text(
                                text = stringResource(R.string.translated_name_is, translatedName),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Normal,
                                textAlign = TextAlign.Center,
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = stringResource(R.string.date_is, receiptData.date),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Center,
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = stringResource(R.string.total_is, receiptData.total),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Center,
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = stringResource(R.string.tax_is, receiptData.tax),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Center,
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = stringResource(R.string.discount_is, receiptData.discount),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Center,
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = stringResource(R.string.tip_is, receiptData.tip),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Center,
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
            OrderItemView(orderData = orderData)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun BottomActionsView(
    modifier: Modifier = Modifier,
    onAddNewOrderClicked: () -> Unit,
    enabled: Boolean,
    goToSplitReceiptScreenClick: () -> Unit,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AnimatedVisibility(
            visible = !enabled,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            Column(
                modifier = modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.maximum_amount_of_dishes_reached),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
        AddOrderButtonView(
            onAddNewOrderClicked = { onAddNewOrderClicked() },
            enabled = enabled,
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = { goToSplitReceiptScreenClick() },
            modifier = modifier.fillMaxWidth(),
        ) {
            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.go_to_split_receipt_screen),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center,
                )
                Icon(
                    imageVector = Icons.Filled.Receipt,
                    contentDescription = stringResource(R.string.go_to_split_receipt_screen_button),
                )
            }
        }
    }
}

@Composable
private fun AddOrderButtonView(
    modifier: Modifier = Modifier,
    onAddNewOrderClicked: () -> Unit,
    enabled: Boolean,
) {
    ElevatedCard(
        onClick = { onAddNewOrderClicked() },
        enabled = enabled,
    ) {
        Box(
            modifier = modifier.fillMaxSize().padding(vertical = 8.dp),
            contentAlignment = Alignment.Center,
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
                receiptName = "restaurant fhgf hgfh gfh gfh gfh gfh gf hgfhgf",
                translatedReceiptName = "ресторан пар апр апр апр парап р  папр апр ап",
                date = "18/03/2024",
                total = 60.0f,
                tax = 10.0f,
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

