package com.iliatokarev.receipt_splitter_app.receipt.presentation.views.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
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
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedCard
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
import com.iliatokarev.receipt_splitter_app.main.basic.icons.Archive
import com.iliatokarev.receipt_splitter_app.main.basic.icons.FileMove
import com.iliatokarev.receipt_splitter_app.main.basic.icons.Folder
import com.iliatokarev.receipt_splitter_app.main.basic.icons.FolderDelete
import com.iliatokarev.receipt_splitter_app.main.basic.icons.Unarchive
import com.iliatokarev.receipt_splitter_app.main.basic.shimmerBrush
import com.iliatokarev.receipt_splitter_app.receipt.data.services.DataConstantsReceipt.MAXIMUM_AMOUNT_OF_FOLDERS
import com.iliatokarev.receipt_splitter_app.receipt.data.services.DataConstantsReceipt.MAXIMUM_AMOUNT_OF_RECEIPTS
import com.iliatokarev.receipt_splitter_app.receipt.presentation.FolderData
import com.iliatokarev.receipt_splitter_app.receipt.presentation.FolderWithReceiptsData
import com.iliatokarev.receipt_splitter_app.receipt.presentation.ReceiptData
import com.iliatokarev.receipt_splitter_app.receipt.presentation.ReceiptWithFolderData

@Composable
internal fun AllReceiptsScreenView(
    modifier: Modifier = Modifier,
    allReceiptsWithFolder: List<ReceiptWithFolderData>?,
    onReceiptClicked: (receiptId: Long) -> Unit,
    onDeleteReceiptClicked: (receiptId: Long) -> Unit,
    onEditReceiptClicked: (receiptId: Long) -> Unit,
    foldersWithReceiptsUnarchived: List<FolderWithReceiptsData>?,
    foldersWithReceiptsArchived: List<FolderWithReceiptsData>?,
    onFolderClick: (FolderData) -> Unit,
    onAddNewFolderClicked: () -> Unit,
    onMoveReceiptToClicked: (receiptId: Long) -> Unit,
    onArchiveFolderClicked: (FolderData) -> Unit,
    onUnarchiveFolderClicked: (FolderData) -> Unit,
    onEditFolderClicked: (Long) -> Unit,
    onDeleteFolderClicked: (Long) -> Unit,
) {
    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        allReceiptsWithFolder?.let { receiptsWithFolder ->
            AllReceiptsView(
                allReceiptsWithFolder = receiptsWithFolder,
                onReceiptClicked = { receiptId ->
                    onReceiptClicked(receiptId)
                },
                onDeleteReceiptClicked = { receiptId ->
                    onDeleteReceiptClicked(receiptId)
                },
                onEditReceiptClicked = { receiptId ->
                    onEditReceiptClicked(receiptId)
                },
                foldersWithReceiptsUnarchived = foldersWithReceiptsUnarchived,
                foldersWithReceiptsArchived = foldersWithReceiptsArchived,
                onFolderClick = { folderData ->
                    onFolderClick(folderData)
                },
                onAddNewFolderClicked = { onAddNewFolderClicked() },
                onMoveReceiptToClicked = { receiptId ->
                    onMoveReceiptToClicked(receiptId)
                },
                onArchiveFolderClicked = { folderData ->
                    onArchiveFolderClicked(folderData)
                },
                onUnarchiveFolderClicked = { folderData ->
                    onUnarchiveFolderClicked(folderData)
                },
                onEditFolderClicked = { folderId ->
                    onEditFolderClicked(folderId)
                },
                onDeleteFolderClicked = { folderId ->
                    onDeleteFolderClicked(folderId)
                },
            )
        } ?: ShimmedAllReceiptsScreenView()
    }
}

@Composable
private fun AllReceiptsView(
    modifier: Modifier = Modifier,
    allReceiptsWithFolder: List<ReceiptWithFolderData> = emptyList(),
    onReceiptClicked: (receiptId: Long) -> Unit = {},
    onDeleteReceiptClicked: (receiptId: Long) -> Unit = {},
    onEditReceiptClicked: (receiptId: Long) -> Unit = {},
    foldersWithReceiptsUnarchived: List<FolderWithReceiptsData>? = emptyList(),
    foldersWithReceiptsArchived: List<FolderWithReceiptsData>? = emptyList(),
    onFolderClick: (FolderData) -> Unit = {},
    onAddNewFolderClicked: () -> Unit = {},
    onMoveReceiptToClicked: (receiptId: Long) -> Unit = {},
    onArchiveFolderClicked: (FolderData) -> Unit = {},
    onUnarchiveFolderClicked: (FolderData) -> Unit = {},
    onEditFolderClicked: (folderId: Long) -> Unit = {},
    onDeleteFolderClicked: (folderId: Long) -> Unit = {},
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        item {
            if (foldersWithReceiptsUnarchived != null && foldersWithReceiptsArchived != null) {
                AllFoldersColumnView(
                    foldersWithReceiptsUnarchived = foldersWithReceiptsUnarchived,
                    foldersWithReceiptsArchived = foldersWithReceiptsArchived,
                    onFolderClick = { folderData ->
                        onFolderClick(folderData)
                    },
                    onAddNewFolderClicked = { onAddNewFolderClicked() },
                    onArchiveFolderClicked = { folderData ->
                        onArchiveFolderClicked(folderData)
                    },
                    onUnarchiveFolderClicked = { folderData ->
                        onUnarchiveFolderClicked(folderData)
                    },
                    onEditFolderClicked = { folderId ->
                        onEditFolderClicked(folderId)
                    },
                    onDeleteFolderClicked = { folderId ->
                        onDeleteFolderClicked(folderId)
                    },
                )

                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
        item {
            AnimatedVisibility(
                visible = allReceiptsWithFolder.isEmpty()
            ) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(R.string.no_receipts_found),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                )
            }
            AnimatedVisibility(
                visible = allReceiptsWithFolder.size >= MAXIMUM_AMOUNT_OF_RECEIPTS
            ) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(R.string.maximum_amount_of_folders_reached),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
        items(count = allReceiptsWithFolder.size) { index ->
            val receiptData = allReceiptsWithFolder[index].receipt
            val folderName = allReceiptsWithFolder[index].folderName
            ReceiptItemCardView(
                receiptData = receiptData,
                onReceiptClicked = { onReceiptClicked(receiptData.id) },
                onDeleteReceiptClicked = { onDeleteReceiptClicked(receiptData.id) },
                onEditReceiptClicked = { onEditReceiptClicked(receiptData.id) },
                onMoveReceiptToClicked = { receiptId ->
                    onMoveReceiptToClicked(receiptId)
                },
                folderName = folderName,
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ReceiptItemCardView(
    modifier: Modifier = Modifier,
    receiptData: ReceiptData,
    folderName: String?,
    onReceiptClicked: () -> Unit,
    onDeleteReceiptClicked: () -> Unit,
    onEditReceiptClicked: () -> Unit,
    onMoveReceiptToClicked: (receiptId: Long) -> Unit,
) {
    OutlinedCard(
        onClick = { onReceiptClicked() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        ReceiptItemView(
            modifier = modifier,
            receiptData = receiptData,
            onDeleteReceiptClicked = { onDeleteReceiptClicked() },
            onEditReceiptClicked = { onEditReceiptClicked() },
            onMoveReceiptToClicked = { receiptId ->
                onMoveReceiptToClicked(receiptId)
            },
            folderName = folderName,
        )
    }
}

@Composable
private fun ReceiptItemView(
    modifier: Modifier = Modifier,
    receiptData: ReceiptData,
    folderName: String?,
    onDeleteReceiptClicked: () -> Unit,
    onEditReceiptClicked: () -> Unit,
    onMoveReceiptToClicked: (receiptId: Long) -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 12.dp),
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
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    text = receiptData.receiptName,
                )
                receiptData.translatedReceiptName?.let { translatedReceiptName ->
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = translatedReceiptName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                    )
                }
            }
            Box(
                modifier = modifier.weight(2f),
            ) {
                ReceiptSubmenuBox(
                    modifier = modifier.align(Alignment.TopEnd),
                    onDeleteReceiptClicked = { onDeleteReceiptClicked() },
                    onEditReceiptClicked = { onEditReceiptClicked() },
                    onMoveReceiptToClicked = { onMoveReceiptToClicked(receiptData.id) },
                    folderId = receiptData.folderId,
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(end = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                modifier = modifier.weight(2f),
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                text = receiptData.date,
                overflow = TextOverflow.Ellipsis,
                maxLines = MAXIMUM_LINES_IS_1,
                textAlign = TextAlign.Start,
            )
            Text(
                modifier = modifier.weight(2f),
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                text = stringResource(R.string.total_of_receipt, receiptData.total),
                maxLines = MAXIMUM_LINES_IS_1,
                textAlign = TextAlign.End,
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        AnimatedVisibility(
            visible = folderName != null
        ) {
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(end = 12.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = modifier.weight(2f),
                ) {
                    Icon(
                        modifier = modifier.align(Alignment.CenterStart),
                        imageVector = Icons.Outlined.Folder,
                        contentDescription = stringResource(R.string.folder_icon)
                    )
                }
                Text(
                    modifier = modifier.weight(14f),
                    text = folderName ?: EMPTY_STRING,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    maxLines = MAXIMUM_LINES_IS_1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun ReceiptSubmenuBox(
    modifier: Modifier = Modifier,
    onDeleteReceiptClicked: () -> Unit,
    onEditReceiptClicked: () -> Unit,
    onMoveReceiptToClicked: () -> Unit,
    folderId: Long?,
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
            if (folderId == null)
                DropdownMenuItem(
                    text = {
                        Text(text = stringResource(R.string.move))
                    },
                    onClick = {
                        expanded = false
                        onMoveReceiptToClicked()
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Outlined.FileMove,
                            contentDescription = stringResource(R.string.receipt_move_to_folder_button)
                        )
                    }
                )
        }
    }
}

@Composable
private fun AllFoldersColumnView(
    modifier: Modifier = Modifier,
    foldersWithReceiptsUnarchived: List<FolderWithReceiptsData>,
    foldersWithReceiptsArchived: List<FolderWithReceiptsData>,
    onFolderClick: (FolderData) -> Unit,
    onAddNewFolderClicked: () -> Unit,
    onArchiveFolderClicked: (FolderData) -> Unit,
    onUnarchiveFolderClicked: (FolderData) -> Unit,
    onEditFolderClicked: (folderId: Long) -> Unit,
    onDeleteFolderClicked: (folderId: Long) -> Unit,
) {
    var archivedFoldersExpanded by rememberSaveable { mutableStateOf(false) }
    val sumOfFolders = foldersWithReceiptsUnarchived.size + foldersWithReceiptsArchived.size

    Column(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AnimatedVisibility(
            visible = sumOfFolders >= MAXIMUM_AMOUNT_OF_FOLDERS
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.maximum_amount_of_folders_reached),
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        repeat(foldersWithReceiptsUnarchived.size) { index ->
            FolderItemView(
                folderWithReceiptsData = foldersWithReceiptsUnarchived[index],
                onFolderClick = { folderData ->
                    onFolderClick(folderData)
                },
                onEditFolderClicked = { folderId ->
                    onEditFolderClicked(folderId)
                },
                onArchiveFolderClicked = { folderData ->
                    onArchiveFolderClicked(folderData)
                },
                onUnarchiveFolderClicked = { folderData ->
                    onUnarchiveFolderClicked(folderData)
                },
                onDeletedFolderClicked = { folderId ->
                  onDeleteFolderClicked(folderId)
                },
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement =
                if (foldersWithReceiptsArchived.isNotEmpty()) Arrangement.SpaceBetween
                else Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextButton(
                onClick = { onAddNewFolderClicked() },
                enabled = sumOfFolders < MAXIMUM_AMOUNT_OF_FOLDERS
            ) {
                Text(
                    text = stringResource(R.string.add_new_folder),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            if (foldersWithReceiptsArchived.isNotEmpty())
                AnimatedContent(
                    targetState = archivedFoldersExpanded,
                ) { expand ->
                    IconButton(
                        onClick = {
                            archivedFoldersExpanded = !archivedFoldersExpanded
                        },
                    ) {
                        if (expand)
                            Icon(
                                Icons.Outlined.KeyboardArrowUp,
                                stringResource(R.string.narrow_down_archive_folders_button)
                            )
                        else
                            Icon(
                                Icons.Outlined.KeyboardArrowDown,
                                stringResource(R.string.expand_archive_folders_button)
                            )
                    }
                }
        }
        AnimatedVisibility(
            visible = archivedFoldersExpanded && foldersWithReceiptsArchived.isNotEmpty()
        ) {
            Column(
                modifier = modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(R.string.archived_folders),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                )
                Spacer(modifier = Modifier.height(8.dp))

                repeat(foldersWithReceiptsArchived.size) { index ->
                    FolderItemView(
                        folderWithReceiptsData = foldersWithReceiptsArchived[index],
                        onFolderClick = { id ->
                            onFolderClick(id)
                        },
                        onEditFolderClicked = { folderId ->
                            onEditFolderClicked(folderId)
                        },
                        onArchiveFolderClicked = { folderData ->
                            onArchiveFolderClicked(folderData)
                        },
                        onUnarchiveFolderClicked = { folderData ->
                            onUnarchiveFolderClicked(folderData)
                        },
                        onDeletedFolderClicked = { folderId ->
                            onDeleteFolderClicked(folderId)
                        },
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
}

@Composable
private fun FolderItemView(
    modifier: Modifier = Modifier,
    folderWithReceiptsData: FolderWithReceiptsData,
    onFolderClick: (FolderData) -> Unit,
    onEditFolderClicked: (folderId: Long) -> Unit,
    onArchiveFolderClicked: (FolderData) -> Unit,
    onUnarchiveFolderClicked: (FolderData) -> Unit,
    onDeletedFolderClicked: (folderId: Long) -> Unit,
) {
    OutlinedCard(
        modifier = modifier.fillMaxWidth(),
        onClick = { onFolderClick(folderWithReceiptsData.folder) },
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(start = 12.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = modifier.weight(2f),
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Folder,
                        contentDescription = stringResource(R.string.folder_icon),
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        modifier = modifier.align(Alignment.CenterHorizontally),
                        text = folderWithReceiptsData.amountOfReceipts.toString(),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                    )
                }
            }
            Text(
                modifier = modifier.weight(12f),
                text = folderWithReceiptsData.folder.folderName,
                fontSize = 20.sp,
                fontWeight = FontWeight.Normal,
                maxLines = MAXIMUM_FOLDER_NAME_LINES,
                overflow = TextOverflow.Ellipsis,
            )
            Box(
                modifier = modifier.weight(2f),
            ) {
                FolderSubmenuBox(
                    modifier = modifier.align(Alignment.TopEnd),
                    onEditReceiptClicked = { onEditFolderClicked(folderWithReceiptsData.folder.id) },
                    onArchiveFolderClicked = { onArchiveFolderClicked(folderWithReceiptsData.folder) },
                    onUnarchiveFolderClicked = { onUnarchiveFolderClicked(folderWithReceiptsData.folder) },
                    isArchived = folderWithReceiptsData.folder.isArchived,
                    onDeleteFolderClicked = { onDeletedFolderClicked(folderWithReceiptsData.folder.id) },
                )
            }
        }
    }
}

@Composable
private fun FolderSubmenuBox(
    modifier: Modifier = Modifier,
    onEditReceiptClicked: () -> Unit,
    onArchiveFolderClicked: () -> Unit,
    onUnarchiveFolderClicked: () -> Unit,
    onDeleteFolderClicked: () -> Unit,
    isArchived: Boolean,
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
            if (isArchived) {
                DropdownMenuItem(
                    text = {
                        Text(text = stringResource(R.string.unarchive))
                    },
                    onClick = {
                        expanded = false
                        onUnarchiveFolderClicked()
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Outlined.Unarchive,
                            contentDescription = stringResource(R.string.unarchive_folder_button)
                        )
                    }
                )
            } else
                DropdownMenuItem(
                    text = {
                        Text(text = stringResource(R.string.archive))
                    },
                    onClick = {
                        expanded = false
                        onArchiveFolderClicked()
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Outlined.Archive,
                            contentDescription = stringResource(R.string.archive_folder_button)
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
                        contentDescription = stringResource(R.string.edit_folder_button)
                    )
                }
            )
            DropdownMenuItem(
                text = {
                    Text(text = stringResource(R.string.delete))
                },
                onClick = {
                    expanded = false
                    onDeleteFolderClicked()
                },
                leadingIcon = {
                    Icon(
                        Icons.Outlined.FolderDelete,
                        contentDescription = stringResource(R.string.delete)
                    )
                }
            )
        }
    }
}

@Composable
private fun ShimmedAllReceiptsScreenView(
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

@Preview(showBackground = true)
@Composable
private fun AllReceiptScreenViewPreview() {
    AllReceiptsView(
        allReceiptsWithFolder =
            listOf<ReceiptWithFolderData>(
                ReceiptWithFolderData(
                    receipt = ReceiptData(
                        id = 1L,
                        receiptName = "restaurant fhfghgfnvbncvnghfghfghd",
                        date = "15/05/2023",
                        total = 1000000.0f,
                    ),
                ),
                ReceiptWithFolderData(
                    receipt = ReceiptData(
                        id = 1L,
                        receiptName = "restaurant fhfghgfnvbncvnghfghfghd",
                        translatedReceiptName = "аовава авылатыв вылаь  ыватыв т ьывлаь  ыдваь ",
                        date = "15/05/2023",
                        total = 1000000.0f,
                    ),
                    folderName = "dgfdgdfg"
                ),
                ReceiptWithFolderData(
                    receipt = ReceiptData(
                        id = 1L,
                        receiptName = "restaurant fhfghgfnvbncvnghfghfghd",
                        date = "15/05/2023",
                        total = 1000000.0f,
                    ),
                ),
                ReceiptWithFolderData(
                    receipt = ReceiptData(
                        id = 1L,
                        receiptName = "restaurant fhfghgfnvbncvnghfghfghd",
                        date = "15/05/2023",
                        total = 1000000.0f,
                    ),
                    folderName = "dsfoerwokvc",
                ),
            ),
        foldersWithReceiptsUnarchived = listOf(
            FolderWithReceiptsData(
                folder = FolderData(
                    id = 1L,
                    folderName = "folder 123124 ",
                    isArchived = false,
                ),
                amountOfReceipts = 100,
            ),
            FolderWithReceiptsData(
                folder = FolderData(
                    id = 1L,
                    folderName = "folder 123124 dfgdfg  dfgdsf gdfg dfs dfg ",
                    isArchived = false,
                ),
                amountOfReceipts = 5,
            ),
        ),
        foldersWithReceiptsArchived = listOf(
            FolderWithReceiptsData(
                folder = FolderData(
                    id = 1L,
                    folderName = "folder 123124 dfgdfg  dfgdsf gdfg dfs dfg ",
                    isArchived = true,
                ),
            ),
            FolderWithReceiptsData(
                folder = FolderData(
                    id = 1L,
                    folderName = "folder 123124 dfgdfg  dfgdsf gdfg dfs dfg ",
                    isArchived = true,
                ),
            ),
        ),
    )
}

private const val MAXIMUM_LINES_IS_1 = 1
private const val MAXIMUM_FOLDER_NAME_LINES = 2
private const val EMPTY_STRING = ""