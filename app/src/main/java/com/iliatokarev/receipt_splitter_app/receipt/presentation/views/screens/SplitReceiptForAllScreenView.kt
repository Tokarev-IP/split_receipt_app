package com.iliatokarev.receipt_splitter_app.receipt.presentation.views.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
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
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iliatokarev.receipt_splitter_app.R
import com.iliatokarev.receipt_splitter_app.main.basic.icons.Swap
import com.iliatokarev.receipt_splitter_app.main.basic.shimmerBrush
import com.iliatokarev.receipt_splitter_app.receipt.presentation.OrderDataSplit
import com.iliatokarev.receipt_splitter_app.receipt.presentation.ReceiptData

@Composable
internal fun SplitReceiptForAllScreenView(
    modifier: Modifier = Modifier,
    receiptData: ReceiptData?,
    orderDataSplitList: List<OrderDataSplit>,
    orderReportText: String?,
    onShareOrderReportClick: () -> Unit,
    onClearOrderReportClick: () -> Unit,
    onCheckStateChange: (Boolean, Int) -> Unit,
    onRemoveConsumerNameClick: (Int, String) -> Unit,
    onSaveOrderDataSplitClick: () -> Unit,
    isSavedState: Boolean,
    onClearAllConsumerNamesClick: (Int) -> Unit,
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        receiptData?.let {
            SplitReceiptForAllView(
                receiptData = receiptData,
                orderDataSplitList = orderDataSplitList,
                orderReportText = orderReportText,
                onShareOrderReportClick = { onShareOrderReportClick() },
                onClearOrderReportClick = { onClearOrderReportClick() },
                onCheckStateChange = { state, position ->
                    onCheckStateChange(state, position)
                },
                onRemoveConsumerNameClick = { position, consumerName ->
                    onRemoveConsumerNameClick(position, consumerName)
                },
                onSaveOrderDataSplitClick = { onSaveOrderDataSplitClick() },
                isSavedState = isSavedState,
                onClearAllConsumerNamesClick = { position ->
                    onClearAllConsumerNamesClick(position)
                }
            )
        } ?: ShimmedSplitReceiptForAllScreenView()
    }
}

@Composable
private fun SplitReceiptForAllView(
    modifier: Modifier = Modifier,
    receiptData: ReceiptData,
    orderDataSplitList: List<OrderDataSplit>,
    orderReportText: String?,
    onShareOrderReportClick: () -> Unit = {},
    onClearOrderReportClick: () -> Unit = {},
    onCheckStateChange: (Boolean, Int) -> Unit = { _, _ -> },
    onRemoveConsumerNameClick: (Int, String) -> Unit = { _, _ -> },
    onSaveOrderDataSplitClick: () -> Unit = {},
    isSavedState: Boolean = false,
    onClearAllConsumerNamesClick: (Int) -> Unit = {},
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
        items(orderDataSplitList.size) { index ->
            val orderDataCheck = orderDataSplitList[index]
            OrderDataCheckCardItem(
                orderDataSplit = orderDataCheck,
                onCheckedChange = { state ->
                    onCheckStateChange(state, index)
                },
                onRemoveConsumerNameClick = { consumerName ->
                    onRemoveConsumerNameClick(index, consumerName)
                },
                onClearAllConsumerNamesClick = { onClearAllConsumerNamesClick(index) }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        item {
            ReceiptReportTextView(
                receiptReportText = orderReportText,
                onShareOrderReportClick = { onShareOrderReportClick() },
                onClearOrderReportClick = { onClearOrderReportClick() },
                onSaveOrderDataSplitClick = { onSaveOrderDataSplitClick() },
                isSavedState = isSavedState,
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun OrderDataCheckCardItem(
    modifier: Modifier = Modifier,
    orderDataSplit: OrderDataSplit,
    onCheckedChange: (Boolean) -> Unit,
    onRemoveConsumerNameClick: (String) -> Unit,
    onClearAllConsumerNamesClick: () -> Unit,
) {
    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        onClick = { onCheckedChange(!orderDataSplit.checked) },
    ) {
        OrderDataSplitItem(
            orderDataSplit = orderDataSplit,
            onCheckedChange = { checked ->
                onCheckedChange(checked)
            },
            onRemoveConsumerNameClick = { consumerName ->
                onRemoveConsumerNameClick(consumerName)
            },
            onClearAllConsumerNamesClick = { onClearAllConsumerNamesClick() }
        )
    }
}

@Composable
private fun OrderDataSplitItem(
    modifier: Modifier = Modifier,
    orderDataSplit: OrderDataSplit,
    onCheckedChange: (Boolean) -> Unit,
    onRemoveConsumerNameClick: (String) -> Unit,
    onClearAllConsumerNamesClick: () -> Unit,
) {
    var expandConsumerNames by rememberSaveable { mutableStateOf(false) }
    val consumerNamesText = orderDataSplit.consumerNamesList.joinToString(NAMES_DIVIDER)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(end = 12.dp, bottom = 8.dp, top = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(
            modifier = modifier.weight(2f),
            checked = orderDataSplit.checked,
            onCheckedChange = { checked ->
                onCheckedChange(checked)
            },
        )
        Column(
            modifier = modifier
                .weight(12f)
                .animateContentSize(),
        ) {
            OrderInfoView(
                orderDataSplit = orderDataSplit,
            )
            OrderConsumerNameView(
                consumerNamesList = orderDataSplit.consumerNamesList,
                consumerNamesText = consumerNamesText,
                expandConsumerNames = expandConsumerNames,
                onExpandConsumerNamesClick = { state: Boolean ->
                    expandConsumerNames = state
                },
                onRemoveConsumerNameClick = { consumerName ->
                    onRemoveConsumerNameClick(consumerName)
                },
                onClearAllConsumerNamesClick = { onClearAllConsumerNamesClick() }
            )
        }
    }
}

@Composable
private fun OrderInfoView(
    modifier: Modifier = Modifier,
    orderDataSplit: OrderDataSplit,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(
            modifier = modifier.weight(10f),
        ) {
            Text(
                text = orderDataSplit.name,
                fontSize = 20.sp,
                fontWeight = FontWeight.Normal,
                overflow = TextOverflow.Ellipsis,
                maxLines = MAXIMUM_AMOUNT_OF_LINES_IS_2,
            )
            orderDataSplit.translatedName?.let { translatedName ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = translatedName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = MAXIMUM_AMOUNT_OF_LINES_IS_2,
                )
            }
        }
        Text(
            modifier = modifier.weight(4f),
            textAlign = TextAlign.End,
            text = orderDataSplit.price.toString(),
            fontSize = 20.sp,
            fontWeight = FontWeight.Normal,
        )
    }
}

@Composable
private fun OrderConsumerNameView(
    modifier: Modifier = Modifier,
    consumerNamesList: List<String>,
    consumerNamesText: String,
    expandConsumerNames: Boolean,
    onExpandConsumerNamesClick: (Boolean) -> Unit,
    onRemoveConsumerNameClick: (String) -> Unit,
    onClearAllConsumerNamesClick: () -> Unit,
) {
    AnimatedVisibility(
        visible = consumerNamesList.isNotEmpty(),
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        Column(
            modifier = modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(4.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AnimatedContent(
                    targetState = expandConsumerNames == false || consumerNamesList.size <= MIN_ITEMS_TO_EXPAND,
                    modifier = modifier.weight(12f),
                ) { showNames ->
                    if (showNames)
                        Text(
                            textAlign = TextAlign.Center,
                            text = consumerNamesText,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = MAXIMUM_AMOUNT_OF_LINES_IS_1,
                        )
                    else
                        Box{
                            TextButton(
                                modifier = modifier.align(Alignment.Center),
                                onClick = { onClearAllConsumerNamesClick() }
                            ) {
                                Text(text = stringResource(R.string.clear_all_consumer_names_button))
                            }
                        }
                }

                AnimatedContent(
                    targetState = consumerNamesList.size > MIN_ITEMS_TO_EXPAND,
                    modifier = modifier.weight(2f)
                ) { moreThanMinAmount ->
                    ConsumerNameIconView(
                        consumerNamesList = consumerNamesList,
                        moreThanMinAmount = moreThanMinAmount,
                        expandConsumerNames = expandConsumerNames,
                        onExpandConsumerNamesClick = { state: Boolean ->
                            onExpandConsumerNamesClick(state)
                        },
                        onRemoveConsumerNameClick = { consumerName ->
                            onRemoveConsumerNameClick(consumerName)
                        }
                    )
                }
            }

            AnimatedVisibility(
                visible = expandConsumerNames && consumerNamesList.size > MIN_ITEMS_TO_EXPAND,
                enter = fadeIn() + expandIn(),
                exit = fadeOut() + shrinkOut(),
            ) {
                ConsumerNamesColumnView(
                    consumerNamesList = consumerNamesList,
                    onRemoveConsumerNameClick = { consumerName ->
                        onRemoveConsumerNameClick(consumerName)
                    },
                )
            }
        }
    }
}

@Composable
private fun ConsumerNameIconView(
    consumerNamesList: List<String>,
    moreThanMinAmount: Boolean,
    expandConsumerNames: Boolean,
    onExpandConsumerNamesClick: (Boolean) -> Unit,
    onRemoveConsumerNameClick: (String) -> Unit,
) {
    if (moreThanMinAmount)
        AnimatedContent(
            targetState = expandConsumerNames,
        ) { expand ->
            if (expand)
                IconButton(
                    onClick = { onExpandConsumerNamesClick(false) },
                ) {
                    Icon(
                        Icons.Outlined.KeyboardArrowUp,
                        stringResource(R.string.narrow_down_consumer_names_button)
                    )
                }
            else
                IconButton(
                    onClick = { onExpandConsumerNamesClick(true) }
                ) {
                    Icon(
                        Icons.Outlined.KeyboardArrowDown,
                        stringResource(R.string.expand_receipt_info_button)
                    )
                }
        }
    else
        IconButton(
            onClick = { onRemoveConsumerNameClick(consumerNamesList.firstOrNull() ?: EMPTY_STRING) }
        ) {
            Icon(
                Icons.Outlined.Clear,
                stringResource(R.string.clear_consumer_name_button)
            )
        }
}

@Composable
private fun ConsumerNamesColumnView(
    modifier: Modifier = Modifier,
    consumerNamesList: List<String>,
    onRemoveConsumerNameClick: (String) -> Unit,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        repeat(consumerNamesList.size) { index ->
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = modifier.weight(12f),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        textAlign = TextAlign.Left,
                        text = consumerNamesList[index],
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Normal,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = MAXIMUM_AMOUNT_OF_LINES_IS_3,
                    )
                }

                IconButton(
                    onClick = { onRemoveConsumerNameClick(consumerNamesList[index]) }
                ) {
                    Icon(
                        Icons.Outlined.Clear,
                        stringResource(R.string.clear_consumer_name_button)
                    )
                }
            }
        }
    }
}

@Composable
private fun ReceiptReportTextView(
    modifier: Modifier = Modifier,
    receiptReportText: String?,
    onShareOrderReportClick: () -> Unit,
    onClearOrderReportClick: () -> Unit,
    onSaveOrderDataSplitClick: () -> Unit,
    isSavedState: Boolean,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        OutlinedButton(
            onClick = { onSaveOrderDataSplitClick() },
            enabled = isSavedState == false,
        ) {
            AnimatedContent(
                targetState = isSavedState,
            ) { isSaved ->
                if (isSaved)
                    Row {
                        Icon(Icons.Outlined.Check, stringResource(R.string.data_was_saved))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = stringResource(R.string.saved))
                    }
                else
                    Text(text = stringResource(R.string.save))
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(8.dp))
        receiptReportText?.let { orderReport ->
            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(
                    onClick = { onClearOrderReportClick() }
                ) {
                    Icon(
                        Icons.Outlined.Clear,
                        stringResource(R.string.clear_order_report_button)
                    )
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
                onClick = { onShareOrderReportClick() }
            ) {
                Icon(Icons.Outlined.Share, stringResource(R.string.share_order_report_button))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = stringResource(R.string.share))
            }
            Spacer(modifier = Modifier.height(8.dp))
        } ?: run {
            Text(
                fontSize = 20.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Left,
                text = stringResource(R.string.order_report_is_empty),
            )
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
        }
    }
}


@Composable
private fun ShimmedSplitReceiptForAllScreenView(
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

        repeat(8) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .background(brush = shimmerBrush(), shape = RoundedCornerShape(16.dp))
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
internal fun SplitReceiptSubmenuBox(
    modifier: Modifier = Modifier,
    onEditReceiptClick: () -> Unit,
    onSwapUiModesClick: () -> Unit,
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    Box(modifier = modifier) {
        IconButton(
            onClick = { expanded = !expanded },
        ) {
            Icon(
                Icons.Outlined.MoreVert,
                contentDescription = stringResource(R.string.receipt_view_submenu_button)
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = {
                    Text(text = stringResource(R.string.swap))
                },
                onClick = {
                    expanded = false
                    onSwapUiModesClick()
                },
                leadingIcon = {
                    Icon(
                        Icons.Outlined.Swap,
                        contentDescription = stringResource(R.string.swap_ui_modes_receipt_button)
                    )
                }
            )

            DropdownMenuItem(
                text = {
                    Text(text = stringResource(R.string.edit))
                },
                onClick = {
                    expanded = false
                    onEditReceiptClick()
                },
                leadingIcon = {
                    Icon(
                        Icons.Outlined.Edit,
                        contentDescription = stringResource(R.string.edit_receipt_button)
                    )
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SplitReceiptForAllViewPreview() {
    SplitReceiptForAllView(
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
        orderDataSplitList =
            listOf(
                OrderDataSplit(
                    name = "order1",
                    translatedName = "заказ 1",
                    price = 999000.0f,
                    consumerNamesList = emptyList(),
                    checked = true,
                    orderDataId = 1,
                ),
                OrderDataSplit(
                    name = "order2",
                    price = 20.0f,
                    consumerNamesList = listOf("Alex"),
                    checked = true,
                    orderDataId = 1,
                ),
                OrderDataSplit(
                    name = "order3 fdgdf dfgfdg dfgdfg erter xcxv sdfdsf sdfsdf asd jyhn vcvf erret fgdfg",
                    translatedName = "перевод 1223 паошпов вошвоп вопшавоп ушегуре впргвр",
                    price = 30.0f,
                    consumerNamesList = emptyList(),
                    checked = false,
                    orderDataId = 1,
                ),
                OrderDataSplit(
                    name = "order3 fdgdf dfgfdg dfgdfg erter xcxv sdfdsf sdfsdf asd jyhn vcvf erret fgdfg",
                    price = 30.0f,
                    consumerNamesList = listOf("Dan", "John"),
                    checked = false,
                    orderDataId = 1,
                ),
                OrderDataSplit(
                    name = "order777",
                    price = 30.0f,
                    consumerNamesList = listOf("Dan", "John", "Abby", "Sian", "Alex", "Laura"),
                    checked = true,
                    orderDataId = 1,
                ),
            ),
        orderReportText = "Report 123 fjdfg kdgjdkfg df djfjg fdg fdjg j dflkjdfgj dfgjdfjg",
    )
}

private const val MAXIMUM_AMOUNT_OF_LINES_IS_1 = 1
private const val MAXIMUM_AMOUNT_OF_LINES_IS_2 = 2
private const val MAXIMUM_AMOUNT_OF_LINES_IS_3 = 3
private const val NAMES_DIVIDER = ", "
private const val MIN_ITEMS_TO_EXPAND = 1
private const val EMPTY_STRING = ""