package com.iliatokarev.receipt_splitter_app.receipt.presentation.views.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.iliatokarev.receipt_splitter_app.main.basic.icons.RemoveCircle
import com.iliatokarev.receipt_splitter_app.main.basic.icons.TurnedInLabel
import com.iliatokarev.receipt_splitter_app.main.basic.icons.TurnedInNotLabel
import com.iliatokarev.receipt_splitter_app.main.basic.shimmerBrush
import com.iliatokarev.receipt_splitter_app.receipt.data.services.DataConstantsReceipt.MAXIMUM_AMOUNT_OF_CONSUMER_NAMES
import com.iliatokarev.receipt_splitter_app.receipt.data.services.DataConstantsReceipt.MAXIMUM_AMOUNT_OF_RECEIPTS_IN_FOLDER
import com.iliatokarev.receipt_splitter_app.receipt.presentation.FolderData
import com.iliatokarev.receipt_splitter_app.receipt.presentation.ReceiptData
import com.iliatokarev.receipt_splitter_app.receipt.presentation.views.basic.FlowGridLayout

@Composable
internal fun FolderReceiptsScreenView(
    modifier: Modifier = Modifier,
    receiptDataList: List<ReceiptData>?,
    isSplitMode: Boolean,
    onReceiptClicked: (receiptId: Long) -> Unit,
    onCheckStateChanged: (receiptId: Long) -> Unit,
    onShareStateChanged: (ReceiptData) -> Unit,
    onRemoveReceiptFromFolderClicked: (ReceiptData) -> Unit,
    onEditReceiptClicked: (receiptId: Long) -> Unit,
    onDeleteReceiptClicked: (receiptId: Long) -> Unit,
    folderData: FolderData?,
    onAddNewConsumerNameClick: () -> Unit,
    onDeleteConsumerNameClick: (name: String) -> Unit,
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        receiptDataList?.let {
            FolderReceiptsView(
                receiptDataList = receiptDataList,
                isSplitMode = isSplitMode,
                onReceiptClicked = { receiptId ->
                    onReceiptClicked(receiptId)
                },
                onCheckStateChanged = { receiptId ->
                    onCheckStateChanged(receiptId)
                },
                onShareStateChanged = { receiptData ->
                    onShareStateChanged(receiptData)
                },
                onRemoveReceiptFromFolderClicked = { receiptId ->
                    onRemoveReceiptFromFolderClicked(receiptId)
                },
                onEditReceiptClicked = { receiptId ->
                    onEditReceiptClicked(receiptId)
                },
                onDeleteReceiptClicked = { receiptId ->
                    onDeleteReceiptClicked(receiptId)
                },
                folderData = folderData,
                onAddNewConsumerNameClick = { onAddNewConsumerNameClick() },
                onDeleteConsumerNameClick = { name ->
                    onDeleteConsumerNameClick(name)
                },
            )
        } ?: ShimmedFolderReceiptsView()
    }
}

@Composable
private fun FolderReceiptsView(
    modifier: Modifier = Modifier,
    receiptDataList: List<ReceiptData> = emptyList(),
    isSplitMode: Boolean,
    onReceiptClicked: (receiptId: Long) -> Unit = {},
    onCheckStateChanged: (receiptId: Long) -> Unit = {},
    onShareStateChanged: (ReceiptData) -> Unit = {},
    onRemoveReceiptFromFolderClicked: (ReceiptData) -> Unit = {},
    onEditReceiptClicked: (receiptId: Long) -> Unit = {},
    onDeleteReceiptClicked: (receiptId: Long) -> Unit = {},
    folderData: FolderData? = null,
    onAddNewConsumerNameClick: () -> Unit = {},
    onDeleteConsumerNameClick: (name: String) -> Unit = {},
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        item {
            folderData?.let { folder ->
                ConsumerNameGridView(
                    consumerNamesList = folder.consumerNamesList,
                    onAddNewConsumerNameClick = { onAddNewConsumerNameClick() },
                    onDeleteConsumerNameClick = { name ->
                        onDeleteConsumerNameClick(name)
                    },
                )
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
        item {
            AnimatedVisibility(
                visible = receiptDataList.isEmpty()
            ) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(R.string.no_receipts_found),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                )
            }
            AnimatedVisibility(
                visible = receiptDataList.size >= MAXIMUM_AMOUNT_OF_RECEIPTS_IN_FOLDER
            ) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(R.string.maximum_amount_of_receipts_reached),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
        items(receiptDataList.size) { index ->
            FolderReceiptCardView(
                receiptData = receiptDataList[index],
                isSplitMode = isSplitMode,
                onReceiptClicked = { receiptId ->
                    onReceiptClicked(receiptId)
                },
                onCheckStateChanged = { receiptId ->
                    onCheckStateChanged(receiptId)
                },
                onShareStateChanged = { receiptData ->
                    onShareStateChanged(receiptData)
                },
                onRemoveReceiptFromFolderClicked = { receiptData ->
                    onRemoveReceiptFromFolderClicked(receiptData)
                },
                onEditReceiptClicked = { receiptId ->
                    onEditReceiptClicked(receiptId)
                },
                onDeleteReceiptClicked = { receiptId ->
                    onDeleteReceiptClicked(receiptId)
                }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun FolderReceiptCardView(
    receiptData: ReceiptData,
    isSplitMode: Boolean,
    onReceiptClicked: (receiptId: Long) -> Unit,
    onCheckStateChanged: (receiptId: Long) -> Unit,
    onShareStateChanged: (ReceiptData) -> Unit,
    onRemoveReceiptFromFolderClicked: (ReceiptData) -> Unit,
    onEditReceiptClicked: (receiptId: Long) -> Unit,
    onDeleteReceiptClicked: (receiptId: Long) -> Unit
) {
    ElevatedCard(
        onClick = {
            if (isSplitMode)
                onCheckStateChanged(receiptData.id)
            else
                onReceiptClicked(receiptData.id)
        }
    ) {
        ReceiptItemView(
            receiptData = receiptData,
            onDeleteReceiptClicked = { onDeleteReceiptClicked(receiptData.id) },
            onEditReceiptClicked = { onEditReceiptClicked(receiptData.id) },
            onRemoveReceiptFromFolderClicked = { onRemoveReceiptFromFolderClicked(receiptData) },
            isSplitMode = isSplitMode,
            onCheckStateChanged = { onCheckStateChanged(receiptData.id) },
            onShareStateChanged = { onShareStateChanged(receiptData) },
        )
    }
}

@Composable
private fun ReceiptItemView(
    modifier: Modifier = Modifier,
    receiptData: ReceiptData,
    onDeleteReceiptClicked: () -> Unit,
    onEditReceiptClicked: () -> Unit,
    onRemoveReceiptFromFolderClicked: () -> Unit,
    isSplitMode: Boolean,
    onShareStateChanged: () -> Unit,
    onCheckStateChanged: () -> Unit,
) {
    Row(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = modifier
                .weight(2f)
                .padding(top = 8.dp, bottom = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            AnimatedContent(
                targetState = receiptData.isShared,
            ) { shared ->
                if (shared)
                    Icon(Icons.Filled.TurnedInLabel, null)
                else
                    Icon(Icons.Filled.TurnedInNotLabel, null)
            }

            AnimatedVisibility(
                visible = isSplitMode,
            ) {
                Checkbox(
                    checked = receiptData.isChecked,
                    onCheckedChange = { onCheckStateChanged() }
                )
            }
        }
        Column(
            modifier = modifier.weight(14f),
        ) {
            Row(
                modifier = modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
            ) {
                Column(
                    modifier = modifier.weight(12f),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.Start,
                ) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Normal,
                        text = receiptData.receiptName,
                    )
                    receiptData.translatedReceiptName?.let { translatedReceiptName ->
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = translatedReceiptName)
                    }
                }
                FolderReceiptsSubmenuBox(
                    modifier = modifier.weight(2f),
                    onDeleteReceiptClicked = { onDeleteReceiptClicked() },
                    onEditReceiptClicked = { onEditReceiptClicked() },
                    onRemoveReceiptFromFolderClicked = { onRemoveReceiptFromFolderClicked() },
                    isShared = receiptData.isShared,
                    onShareStateChanged = { onShareStateChanged() },
                    enabled = isSplitMode == false
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(end = 12.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    modifier = modifier.weight(1f),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    text = receiptData.date,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = MAXIMUM_LINES_IS_1,
                    textAlign = TextAlign.Start,
                )
                Text(
                    modifier = modifier.weight(1f),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    text = stringResource(R.string.total_of_receipt, receiptData.total),
                    maxLines = MAXIMUM_LINES_IS_1,
                    textAlign = TextAlign.End,
                )
            }
        }
    }
}

@Composable
private fun FolderReceiptsSubmenuBox(
    modifier: Modifier = Modifier,
    isShared: Boolean,
    onDeleteReceiptClicked: () -> Unit,
    onEditReceiptClicked: () -> Unit,
    onRemoveReceiptFromFolderClicked: () -> Unit,
    onShareStateChanged: () -> Unit,
    enabled: Boolean,
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    Box(modifier = modifier) {
        IconButton(
            onClick = { expanded = !expanded },
            enabled = enabled,
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
                    Text(text = stringResource(R.string.delete))
                },
                onClick = {
                    expanded = false
                    onDeleteReceiptClicked()
                },
                leadingIcon = {
                    Icon(
                        Icons.Outlined.Delete,
                        contentDescription = stringResource(R.string.delete_receipt_button)
                    )
                }
            )
            DropdownMenuItem(
                text = {
                    Text(text = stringResource(R.string.edit))
                },
                onClick = {
                    expanded = false
                    onEditReceiptClicked()
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
                    Text(text = stringResource(R.string.remove_from))
                },
                onClick = {
                    expanded = false
                    onRemoveReceiptFromFolderClicked()
                },
                leadingIcon = {
                    Icon(
                        Icons.Outlined.RemoveCircle,
                        contentDescription = stringResource(R.string.remove_receipt_from_folder_button)
                    )
                }
            )
            AnimatedContent(
                targetState = isShared,
            ) { shared ->
                if (shared)
                    DropdownMenuItem(
                        text = {
                            Text(text = stringResource(R.string.mark_as_unshared))
                        },
                        onClick = {
                            expanded = false
                            onShareStateChanged()
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Filled.TurnedInNotLabel,
                                contentDescription = stringResource(R.string.mark_receipt_as_unshared_button)
                            )
                        }
                    )
                else
                    DropdownMenuItem(
                        text = {
                            Text(text = stringResource(R.string.mark_as_shared))
                        },
                        onClick = {
                            expanded = false
                            onShareStateChanged()
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Filled.TurnedInLabel,
                                contentDescription = stringResource(R.string.mark_receipt_as_shared_button)
                            )
                        }
                    )
            }
        }
    }
}

@Composable
private fun ConsumerNameGridView(
    modifier: Modifier = Modifier,
    consumerNamesList: List<String>,
    onAddNewConsumerNameClick: () -> Unit,
    onDeleteConsumerNameClick: (name: String) -> Unit,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AnimatedContent(
            targetState = consumerNamesList.isEmpty(),
        ) { isEmpty ->
            if (isEmpty)
                Text(
                    text = stringResource(R.string.names_are_empty),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                )
            else
                FlowGridLayout {
                    consumerNamesList.forEach { consumerName ->
                        ConsumerNameItemView(
                            consumerName = consumerName,
                            onDeleteNameClick = { name ->
                                onDeleteConsumerNameClick(name)
                            }
                        )
                    }
                }
        }
        Spacer(modifier = Modifier.height(4.dp))

        AnimatedVisibility(
            visible = consumerNamesList.size >= MAXIMUM_AMOUNT_OF_CONSUMER_NAMES,
        ) {
            Text(
                text = stringResource(R.string.maximum_amount_of_names_is_reached),
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        TextButton(
            onClick = { onAddNewConsumerNameClick() },
            enabled = consumerNamesList.size < MAXIMUM_AMOUNT_OF_CONSUMER_NAMES,
        ) {
            Text(
                text = stringResource(R.string.add_person_name),
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
            )
        }
    }
}

@Composable
private fun ConsumerNameItemView(
    modifier: Modifier = Modifier,
    consumerName: String,
    onDeleteNameClick: (name: String) -> Unit,
) {
    ElevatedCard {
        Row(
            modifier = modifier.padding(start = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = consumerName,
                fontWeight = FontWeight.Normal,
                fontSize = 20.sp,
                maxLines = MAXIMUM_LINES_IS_1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.width(4.dp))
            IconButton(
                onClick = { onDeleteNameClick(consumerName) },
            ) {
                Icon(Icons.Outlined.Clear, stringResource(R.string.clear_consumer_name_button))
            }
        }
    }
}

@Composable
private fun ShimmedFolderReceiptsView(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        repeat(6) {
            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(brush = shimmerBrush(), shape = RoundedCornerShape(16.dp))
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

private const val MAXIMUM_LINES_IS_1 = 1

@Preview(showBackground = true)
@Composable
private fun FolderReceiptsViewPreview() {
    FolderReceiptsView(
        receiptDataList = listOf<ReceiptData>(
            ReceiptData(
                id = 1L,
                receiptName = "restaurant fhfghgfnvbncvnghfghfghd",
                date = "15/05/2023",
                total = 1000000.0f,
                isShared = true,
                isChecked = true,
            ),
            ReceiptData(
                id = 2L,
                receiptName = "restaurant",
                translatedReceiptName = "fdhgdhfdg",
                date = "04/10/2022",
                total = 10078.0f,
                isShared = false,
                isChecked = true,
            ),
            ReceiptData(
                id = 3L,
                receiptName = "restaurant",
                date = "01/01/2023",
                total = 57465.0f,
                isShared = true,
            ),
            ReceiptData(
                id = 4L,
                receiptName = "restaurant",
                translatedReceiptName = "fdgdfgfdgfji",
                date = "01/01/2023",
                total = 57465.0f,
                isShared = false,
            ),
            ReceiptData(
                id = 5L,
                receiptName = "restaurant fdg dfgdfgfd ffghfs hfds hsh fds fh gfhfd hfdh df d hh ",
                translatedReceiptName = "fdgdfk dsj sdfhsyeybcyus  fsdfdsgscha kvdjvdhvfh hjkj dvvh h  vhj dv",
                date = "01/01/2023",
                total = 57465.0f,
                isShared = true,
                isChecked = false,
            ),
            ReceiptData(
                id = 6L,
                receiptName = "restaurant",
                date = "01/01/2023",
                total = 57465.0f,
                isShared = false,
                isChecked = true,
            )
        ),
        isSplitMode = true,
        folderData = FolderData(
            id = 1L,
            folderName = "folder",
            consumerNamesList = listOf(
                "consumer1",
                "consumer2 df dsdf ",
                "consumer3",
                "consumer4",
                "consumer5",
                "consumer6",
                "consumer7",
                "consumer8",
                "consumer9",
                "consumer10"
            )
        )
    )
}