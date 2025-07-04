package com.iliatokarev.receipt_splitter_app.receipt.presentation.views.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
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
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iliatokarev.receipt_splitter_app.R
import com.iliatokarev.receipt_splitter_app.main.basic.icons.PersonAddIcon
import com.iliatokarev.receipt_splitter_app.main.basic.icons.Swap
import com.iliatokarev.receipt_splitter_app.main.basic.shimmerBrush
import com.iliatokarev.receipt_splitter_app.receipt.data.services.DataConstantsReceipt.CONSUMER_NAME_DIVIDER
import com.iliatokarev.receipt_splitter_app.receipt.data.services.DataConstantsReceipt.MAXIMUM_AMOUNT_OF_CONSUMER_NAMES
import com.iliatokarev.receipt_splitter_app.receipt.data.services.DataConstantsReceipt.MAXIMUM_CONSUMER_NAME_TEXT_LENGTH
import com.iliatokarev.receipt_splitter_app.receipt.data.services.DataConstantsReceipt.ORDER_CONSUMER_NAME_DIVIDER
import com.iliatokarev.receipt_splitter_app.receipt.presentation.OrderDataSplit
import com.iliatokarev.receipt_splitter_app.receipt.presentation.ReceiptData
import com.iliatokarev.receipt_splitter_app.receipt.presentation.views.basic.FlowGridLayout

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun SplitReceiptForAllScreenView(
    modifier: Modifier = Modifier,
    receiptData: ReceiptData?,
    orderDataSplitList: List<OrderDataSplit>,
    orderReportText: String?,
    onShareOrderReportClick: () -> Unit,
    onCheckStateChange: (Boolean, Int) -> Unit,
    onRemoveConsumerNameClick: (Int, String) -> Unit,
    onClearAllConsumerNamesClick: (Int) -> Unit,
    onAddConsumerNameForSpecificOrderClick: (Int, String) -> Unit,
    allConsumerNamesList: List<String>,
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
                onCheckStateChange = { state, position ->
                    onCheckStateChange(state, position)
                },
                onRemoveConsumerNameClick = { position, consumerName ->
                    onRemoveConsumerNameClick(position, consumerName)
                },
                onClearAllConsumerNamesClick = { position ->
                    onClearAllConsumerNamesClick(position)
                },
                onAddConsumerNameForSpecificOrderClick = { position, name ->
                    onAddConsumerNameForSpecificOrderClick(position, name)
                },
                allConsumerNamesList = allConsumerNamesList,
            )
        } ?: ShimmedShowReportsScreenView()
    }
}

@Composable
private fun SplitReceiptForAllView(
    modifier: Modifier = Modifier,
    receiptData: ReceiptData,
    orderDataSplitList: List<OrderDataSplit>,
    orderReportText: String?,
    onShareOrderReportClick: () -> Unit = {},
    onCheckStateChange: (Boolean, Int) -> Unit = { _, _ -> },
    onRemoveConsumerNameClick: (Int, String) -> Unit = { _, _ -> },
    onClearAllConsumerNamesClick: (Int) -> Unit = {},
    onAddConsumerNameForSpecificOrderClick: (Int, String) -> Unit = { _, _ -> },
    allConsumerNamesList: List<String> = emptyList(),
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp),
    ) {
        item {
            Spacer(modifier = Modifier.height(12.dp))
            ReceiptInfoView(receiptData = receiptData)
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))
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
                onClearAllConsumerNamesClick = { onClearAllConsumerNamesClick(index) },
                onAddConsumerForSpecificOrderNameClick = { name ->
                    onAddConsumerNameForSpecificOrderClick(index, name)
                },
                allConsumerNamesList = allConsumerNamesList,
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        item {
            ReceiptReportTextView(
                receiptReportText = orderReportText,
                onShareOrderReportClick = { onShareOrderReportClick() },
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
    onAddConsumerForSpecificOrderNameClick: (String) -> Unit,
    allConsumerNamesList: List<String>,
) {
    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .then(
                    if (orderDataSplit.consumerNamesList.isEmpty()) {
                        Modifier.clickable { onCheckedChange(!orderDataSplit.checked) }
                    } else Modifier
                ),
        ) {
            OrderDataSplitItem(
                orderDataSplit = orderDataSplit,
                onCheckedChange = { checked ->
                    onCheckedChange(checked)
                },
                onRemoveConsumerNameClick = { consumerName ->
                    onRemoveConsumerNameClick(consumerName)
                },
                onClearAllConsumerNamesClick = { onClearAllConsumerNamesClick() },
                onAddConsumerNameForSpecificOrderClick = { name ->
                    onAddConsumerForSpecificOrderNameClick(name)
                },
                allConsumerNamesList = allConsumerNamesList,
            )
        }
    }
}

@Composable
private fun OrderDataSplitItem(
    modifier: Modifier = Modifier,
    orderDataSplit: OrderDataSplit,
    onCheckedChange: (Boolean) -> Unit,
    onRemoveConsumerNameClick: (String) -> Unit,
    onClearAllConsumerNamesClick: () -> Unit,
    onAddConsumerNameForSpecificOrderClick: (String) -> Unit,
    allConsumerNamesList: List<String>,
) {
    var expandConsumerNames by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(key1 = orderDataSplit.consumerNamesList) {
        if (orderDataSplit.consumerNamesList.isEmpty())
            expandConsumerNames = false
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(end = 12.dp, bottom = 8.dp, top = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AnimatedContent(
            targetState = orderDataSplit.consumerNamesList.isEmpty(),
        ) { isEmpty ->
            if (isEmpty)
                Checkbox(
                    modifier = modifier.weight(2f),
                    checked = orderDataSplit.checked,
                    onCheckedChange = { checked ->
                        onCheckedChange(checked)
                    },
                )
            else
                Spacer(modifier = Modifier.width(12.dp))
        }

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
                expandConsumerNames = expandConsumerNames,
                onExpandConsumerNamesClick = { state: Boolean ->
                    expandConsumerNames = state
                },
                onRemoveConsumerNameClick = { consumerName ->
                    onRemoveConsumerNameClick(consumerName)
                },
                onClearAllConsumerNamesClick = { onClearAllConsumerNamesClick() },
                onAddConsumerNameClick = { name ->
                    onAddConsumerNameForSpecificOrderClick(name)
                },
                allConsumerNamesList = allConsumerNamesList,
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
    expandConsumerNames: Boolean,
    onExpandConsumerNamesClick: (Boolean) -> Unit,
    onRemoveConsumerNameClick: (String) -> Unit,
    onClearAllConsumerNamesClick: () -> Unit,
    onAddConsumerNameClick: (String) -> Unit,
    allConsumerNamesList: List<String>,
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
                    targetState = !expandConsumerNames,
                    modifier = modifier.weight(12f),
                ) { showNames ->
                    if (showNames)
                        ConsumerNamesRowView(consumerNamesList = consumerNamesList)
                    else
                        Box {
                            TextButton(
                                modifier = modifier.align(Alignment.Center),
                                onClick = { onClearAllConsumerNamesClick() }
                            ) {
                                Text(text = stringResource(R.string.clear_all_names))
                            }
                        }
                }

                AnimatedContent(
                    targetState = expandConsumerNames,
                    modifier = modifier.weight(2f),
                ) { expand ->
                    IconButton(
                        onClick = {
                            if (expandConsumerNames)
                                onExpandConsumerNamesClick(false)
                            else
                                onExpandConsumerNamesClick(true)
                        },
                    ) {
                        if (expand)
                            Icon(
                                Icons.Outlined.KeyboardArrowUp,
                                stringResource(R.string.narrow_down_consumer_names_button)
                            )
                        else
                            Icon(
                                Icons.Outlined.KeyboardArrowDown,
                                stringResource(R.string.expand_consumer_names_button)
                            )
                    }
                }
            }

            AnimatedVisibility(
                visible = expandConsumerNames,
                enter = fadeIn() + expandIn(),
                exit = fadeOut() + shrinkOut(),
            ) {
                EditConsumerNamesView(
                    consumerNamesList = consumerNamesList,
                    onRemoveConsumerNameClick = { consumerName ->
                        onRemoveConsumerNameClick(consumerName)
                    },
                    onAddConsumerNameClick = { name ->
                        onAddConsumerNameClick(name)
                    },
                    allConsumerNamesList = allConsumerNamesList,
                )
            }
        }
    }
}

@Composable
private fun ConsumerNamesRowView(
    modifier: Modifier = Modifier,
    consumerNamesList: List<String>,
) {
    LazyRow(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        item {
            if (consumerNamesList.joinToString(SEPARATOR).length > INFO_DISPLAY_CHAR_COUNT) {
                Text(
                    text = consumerNamesList.size.toString(),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    maxLines = MAXIMUM_AMOUNT_OF_LINES_IS_1,
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
        }
        items(consumerNamesList.size) { index ->
            OutlinedCard {
                Text(
                    modifier = modifier
                        .padding(vertical = 4.dp, horizontal = 8.dp),
                    text = consumerNamesList[index],
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Normal,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = MAXIMUM_AMOUNT_OF_LINES_IS_1,
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
        }
    }
}

@Composable
private fun EditConsumerNamesView(
    modifier: Modifier = Modifier,
    consumerNamesList: List<String>,
    onRemoveConsumerNameClick: (String) -> Unit,
    onAddConsumerNameClick: (String) -> Unit,
    allConsumerNamesList: List<String>,
) {
    var consumerNameText by rememberSaveable { mutableStateOf("") }
    var isConsumerNameErrorState by rememberSaveable { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ConsumerNamesGrid(
            allConsumerNamesList = allConsumerNamesList,
            consumerNamesList = consumerNamesList,
            onChooseConsumerNameClick = { name ->
                onAddConsumerNameClick(name)
            },
            onRemoveConsumerNameClick = { name ->
                onRemoveConsumerNameClick(name)
            },
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (allConsumerNamesList.size >= MAXIMUM_AMOUNT_OF_CONSUMER_NAMES) {
            Text(
                textAlign = TextAlign.Center,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                text = stringResource(R.string.maximum_amount_of_names_is_reached)
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        OutlinedTextField(
            value = consumerNameText,
            onValueChange = { name ->
                isConsumerNameErrorState = false
                if (name.length <= MAXIMUM_CONSUMER_NAME_TEXT_LENGTH)
                    consumerNameText = name
            },
            isError = isConsumerNameErrorState,
            singleLine = true,
            label = { Text(text = stringResource(R.string.name)) },
            trailingIcon = {
                if (consumerNameText.isNotEmpty())
                    IconButton(
                        onClick = {
                            consumerNameText = EMPTY_STRING
                            isConsumerNameErrorState = false
                        }
                    ) { Icon(Icons.Filled.Clear, stringResource(R.string.clear_text_button)) }
            },
            leadingIcon = {
                IconButton(
                    onClick = {
                        addConsumerName(
                            consumerNameText = consumerNameText.trim(),
                            consumerNamesList = consumerNamesList,
                            onConsumerNameErrorState = { state ->
                                isConsumerNameErrorState = state
                            },
                            onAddConsumerNameClick = { name ->
                                onAddConsumerNameClick(name)
                                consumerNameText = EMPTY_STRING
                            }
                        )
                        focusManager.clearFocus()
                    },
                    enabled = allConsumerNamesList.size < MAXIMUM_AMOUNT_OF_CONSUMER_NAMES
                ) {
                    Icon(Icons.Filled.PersonAddIcon, stringResource(R.string.add_name_button))
                }
            },
            supportingText = {
                if (isConsumerNameErrorState && consumerNameText.isEmpty())
                    Text(text = stringResource(R.string.field_is_empty))
                else if (isConsumerNameErrorState && consumerNameText.trim() in consumerNamesList)
                    Text(text = stringResource(R.string.name_is_already_existed))
                else if (isConsumerNameErrorState)
                    Text(text = stringResource(R.string.inappropriate_symbols))
                else if (consumerNameText.isNotEmpty())
                    Text(
                        text = stringResource(
                            R.string.maximum_letters,
                            consumerNameText.length,
                            MAXIMUM_CONSUMER_NAME_TEXT_LENGTH,
                        )
                    )
            },
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                imeAction = ImeAction.Done,
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    addConsumerName(
                        consumerNameText = consumerNameText.trim(),
                        consumerNamesList = consumerNamesList,
                        onConsumerNameErrorState = { state ->
                            isConsumerNameErrorState = state
                        },
                        onAddConsumerNameClick = { name ->
                            onAddConsumerNameClick(name)
                            consumerNameText = EMPTY_STRING
                        }
                    )
                    focusManager.clearFocus()
                }
            ),
            enabled = allConsumerNamesList.size < MAXIMUM_AMOUNT_OF_CONSUMER_NAMES,
        )
        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
private fun ConsumerNamesGrid(
    modifier: Modifier = Modifier,
    allConsumerNamesList: List<String>,
    consumerNamesList: List<String>,
    onChooseConsumerNameClick: (String) -> Unit,
    onRemoveConsumerNameClick: (String) -> Unit,
) {
    FlowGridLayout {
        repeat(allConsumerNamesList.size) { index ->
            val consumerName = allConsumerNamesList[index]
            OutlinedCard(
                onClick = { onRemoveConsumerNameClick(consumerName) },
                enabled = consumerName in consumerNamesList
            ) {
                Box(
                    modifier = modifier
                        .then(
                            if (consumerName !in consumerNamesList)
                                Modifier.clickable { onChooseConsumerNameClick(consumerName) }
                            else
                                Modifier
                        )
                ) {
                    Text(
                        modifier = modifier
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        text = consumerName,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Normal,
                        maxLines = MAXIMUM_AMOUNT_OF_LINES_IS_1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

private fun addConsumerName(
    consumerNameText: String,
    consumerNamesList: List<String>,
    onConsumerNameErrorState: (Boolean) -> Unit,
    onAddConsumerNameClick: (String) -> Unit,
) {
    if (consumerNameText
            .isEmpty() || consumerNameText.length > MAXIMUM_CONSUMER_NAME_TEXT_LENGTH
    ) {
        onConsumerNameErrorState(true)
        return
    }

    if (CONSUMER_NAME_DIVIDER in consumerNameText ||
        ORDER_CONSUMER_NAME_DIVIDER in consumerNameText
    ) {
        onConsumerNameErrorState(true)
        return
    }

    if (consumerNameText in consumerNamesList) {
        onConsumerNameErrorState(true)
        return
    }

    onAddConsumerNameClick(consumerNameText)
}

@Composable
private fun ReceiptReportTextView(
    modifier: Modifier = Modifier,
    receiptReportText: String?,
    onShareOrderReportClick: () -> Unit,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        HorizontalDivider()
        Spacer(modifier = Modifier.height(8.dp))

        AnimatedContent(
            targetState = receiptReportText == null
        ) { reportIsNull ->
            if (reportIsNull){
                Column(
                    modifier = modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        modifier = modifier.fillMaxWidth(),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Center,
                        text = stringResource(R.string.order_report_is_empty),
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider()
                }
            } else {
                Column(
                    modifier = modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        modifier = modifier.fillMaxWidth(),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Justify,
                        text = receiptReportText ?: EMPTY_STRING,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = { onShareOrderReportClick() }
                    ) {
                        Icon(
                            Icons.Outlined.Share,
                            stringResource(R.string.share_order_report_button)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = stringResource(R.string.share),
                            fontWeight = FontWeight.Normal,
                            fontSize = 16.sp,
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}


@Composable
private fun ShimmedShowReportsScreenView(
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
internal fun TopAppBarSplitReceipt(
    modifier: Modifier = Modifier,
    onEditReceiptClick: () -> Unit,
    onSwapUiModesClick: () -> Unit,
    onClearReportClick: () -> Unit,
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
                    Text(text = stringResource(R.string.swap_split_mode))
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
            DropdownMenuItem(
                text = {
                    Text(text = stringResource(R.string.clear_report))
                },
                onClick = {
                    expanded = false
                    onClearReportClick()
                },
                leadingIcon = {
                    Icon(
                        Icons.Outlined.Clear,
                        contentDescription = stringResource(R.string.clear_report_button)
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
                tax = 1.5F,
                discount = 0.0F,
                tip = 2.0F,
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
        allConsumerNamesList = listOf(
            "Dan",
            "John",
            "Abby",
            "Sian",
            "Alex",
            "Laura",
            "Vasya",
            "Oleg"
        ),
    )
}

private const val MAXIMUM_AMOUNT_OF_LINES_IS_1 = 1
private const val MAXIMUM_AMOUNT_OF_LINES_IS_2 = 2
private const val EMPTY_STRING = ""
private const val SEPARATOR = "__"
private const val INFO_DISPLAY_CHAR_COUNT = 20