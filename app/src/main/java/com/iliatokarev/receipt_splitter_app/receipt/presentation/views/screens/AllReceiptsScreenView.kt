package com.iliatokarev.receipt_splitter_app.receipt.presentation.views.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iliatokarev.receipt_splitter_app.R
import com.iliatokarev.receipt_splitter_app.main.basic.icons.Archive
import com.iliatokarev.receipt_splitter_app.main.basic.icons.CreateNewFolder
import com.iliatokarev.receipt_splitter_app.main.basic.icons.FileMove
import com.iliatokarev.receipt_splitter_app.main.basic.icons.Folder
import com.iliatokarev.receipt_splitter_app.main.basic.icons.Unarchive
import com.iliatokarev.receipt_splitter_app.main.basic.shimmerBrush
import com.iliatokarev.receipt_splitter_app.receipt.presentation.FolderData
import com.iliatokarev.receipt_splitter_app.receipt.presentation.ReceiptData

@Composable
internal fun AllReceiptsScreenView(
    modifier: Modifier = Modifier,
    allReceiptsList: List<ReceiptData>?,
    onReceiptClicked: (receiptId: Long) -> Unit,
    onDeleteReceiptClicked: (receiptId: Long) -> Unit,
    onEditReceiptClicked: (receiptId: Long) -> Unit,
    foldersListUnarchived: List<FolderData>?,
    foldersListArchived: List<FolderData>?,
    onFolderClick: (Long) -> Unit,
    onAddNewFolderClicked: () -> Unit,
    onMoveReceiptToClicked: (receiptId: Long) -> Unit,
    onArchiveFolderClicked: (FolderData) -> Unit,
    onUnarchiveFolderClicked: (FolderData) -> Unit,
    onEditFolderClicked: (Long) -> Unit,
) {
    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        allReceiptsList?.let { receipt ->
            AllReceiptsView(
                allReceiptsList = receipt,
                onReceiptClicked = { receiptId ->
                    onReceiptClicked(receiptId)
                },
                onDeleteReceiptClicked = { receiptId ->
                    onDeleteReceiptClicked(receiptId)
                },
                onEditReceiptClicked = { receiptId ->
                    onEditReceiptClicked(receiptId)
                },
                foldersListUnarchived = foldersListUnarchived,
                foldersListArchived = foldersListArchived,
                onFolderClick = { id ->
                    onFolderClick(id)
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
            )
        } ?: ShimmedAllReceiptsScreenView()
    }
}

@Composable
private fun AllReceiptsView(
    modifier: Modifier = Modifier,
    allReceiptsList: List<ReceiptData> = emptyList(),
    onReceiptClicked: (receiptId: Long) -> Unit = {},
    onDeleteReceiptClicked: (receiptId: Long) -> Unit = {},
    onEditReceiptClicked: (receiptId: Long) -> Unit = {},
    foldersListUnarchived: List<FolderData>? = emptyList(),
    foldersListArchived: List<FolderData>? = emptyList(),
    onFolderClick: (Long) -> Unit = {},
    onAddNewFolderClicked: () -> Unit = {},
    onMoveReceiptToClicked: (receiptId: Long) -> Unit = {},
    onArchiveFolderClicked: (FolderData) -> Unit = {},
    onUnarchiveFolderClicked: (FolderData) -> Unit = {},
    onEditFolderClicked: (folderId: Long) -> Unit = {},
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        item {
            if (foldersListUnarchived != null && foldersListArchived != null) {
                AllFoldersColumnView(
                    foldersListUnarchived = foldersListUnarchived,
                    foldersListArchived = foldersListArchived,
                    onFolderClick = { id ->
                        onFolderClick(id)
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
                )

                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
        item {
            if (allReceiptsList.isEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(R.string.no_receipts_found),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                )
            }
        }
        items(
            count = allReceiptsList.size,
            key = { index -> allReceiptsList[index].id }
        ) { index ->
            val receiptData = allReceiptsList[index]
            AllReceiptViewItem(
                receiptData = receiptData,
                onReceiptClicked = { onReceiptClicked(receiptData.id) },
                onDeleteReceiptClicked = { onDeleteReceiptClicked(receiptData.id) },
                onEditReceiptClicked = { onEditReceiptClicked(receiptData.id) },
                onMoveReceiptToClicked = { receiptId ->
                    onMoveReceiptToClicked(receiptId)
                },
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun AllReceiptViewItem(
    modifier: Modifier = Modifier,
    receiptData: ReceiptData,
    onReceiptClicked: () -> Unit,
    onDeleteReceiptClicked: () -> Unit,
    onEditReceiptClicked: () -> Unit,
    onMoveReceiptToClicked: (receiptId: Long) -> Unit,
) {
    OutlinedCard(
        onClick = { onReceiptClicked() },
    ) {
        Box {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 12.dp,
                        top = 8.dp,
                        bottom = 8.dp,
                        end = 12.dp,
                    ),
            ) {
                Text(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Normal,
                    modifier = modifier
                        .align(Alignment.Start)
                        .padding(end = 40.dp),
                    text = receiptData.receiptName,
                )
                receiptData.translatedReceiptName?.let { translatedReceiptName ->
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = translatedReceiptName)
                }

                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        fontSize = 16.sp,
                        text = receiptData.date
                    )
                    Text(
                        fontSize = 16.sp,
                        text = stringResource(R.string.total_of_receipt, receiptData.total)
                    )
                }
            }
            ReceiptSubmenuBox(
                modifier = Modifier.align(Alignment.TopEnd),
                onDeleteReceiptClicked = { onDeleteReceiptClicked() },
                onEditReceiptClicked = { onEditReceiptClicked() },
                onMoveReceiptToClicked = { onMoveReceiptToClicked(receiptData.id) },
                folderId = receiptData.folderId,
            )
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
                    onMoveReceiptToClicked()
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
                        onEditReceiptClicked()
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
    foldersListUnarchived: List<FolderData>,
    foldersListArchived: List<FolderData>,
    onFolderClick: (Long) -> Unit,
    onAddNewFolderClicked: () -> Unit,
    onArchiveFolderClicked: (FolderData) -> Unit,
    onUnarchiveFolderClicked: (FolderData) -> Unit,
    onEditFolderClicked: (folderId: Long) -> Unit,
) {
    var archivedFoldersExpanded by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        repeat(foldersListUnarchived.size) { index ->
            FolderItemView(
                folderData = foldersListUnarchived[index],
                onFolderClick = { id ->
                    onFolderClick(id)
                },
                onEditFolderClicked = {
                    onEditFolderClicked(foldersListUnarchived[index].id)
                },
                onArchiveFolderClicked = {
                    onArchiveFolderClicked(foldersListUnarchived[index])
                },
                onUnarchiveFolderClicked = {
                    onUnarchiveFolderClicked(foldersListUnarchived[index])
                },
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement =
                if (foldersListArchived.isNotEmpty()) Arrangement.SpaceBetween
                else Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedButton(
                onClick = { onAddNewFolderClicked() }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        Icons.Outlined.CreateNewFolder,
                        stringResource(R.string.add_new_folder_button)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = stringResource(R.string.add_new_folder),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }

            AnimatedVisibility(
                visible = foldersListArchived.isNotEmpty()
            ) {
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
        }
        AnimatedVisibility(
            visible = archivedFoldersExpanded
        ) {
            Column(
                modifier = modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))

                repeat(foldersListArchived.size) { index ->
                    FolderItemView(
                        folderData = foldersListArchived[index],
                        onFolderClick = { id ->
                            onFolderClick(id)
                        },
                        onEditFolderClicked = {
                            onEditFolderClicked(foldersListArchived[index].id)
                        },
                        onArchiveFolderClicked = {
                            onArchiveFolderClicked(foldersListArchived[index])
                        },
                        onUnarchiveFolderClicked = {
                            onUnarchiveFolderClicked(foldersListArchived[index])
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
    folderData: FolderData,
    onFolderClick: (Long) -> Unit,
    onEditFolderClicked: (FolderData) -> Unit,
    onArchiveFolderClicked: (FolderData) -> Unit,
    onUnarchiveFolderClicked: (FolderData) -> Unit,
) {
    OutlinedCard(
        onClick = { onFolderClick(folderData.id) },
        enabled = folderData.isArchived == false,
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .then(
                    if (folderData.isArchived == true)
                        Modifier.clickable { onFolderClick(folderData.id) }
                    else
                        Modifier
                )
        ) {
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    modifier = modifier.weight(2f),
                    imageVector = Icons.Outlined.Folder,
                    contentDescription = null
                )
                Text(
                    modifier = modifier.weight(10f),
                    text = folderData.folderName,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Normal,
                    maxLines = MAXIMUM_FOLDER_NAME_LINES,
                    overflow = TextOverflow.Ellipsis,
                )
                FolderSubmenuBox(
                    modifier = modifier.weight(2f),
                    onEditReceiptClicked = { onEditFolderClicked(folderData) },
                    onArchiveFolderClicked = { onArchiveFolderClicked(folderData) },
                    onUnarchiveFolderClicked = { onUnarchiveFolderClicked(folderData) },
                    isArchived = folderData.isArchived,
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
        allReceiptsList =
            listOf<ReceiptData>(
                ReceiptData(
                    id = 1L,
                    receiptName = "restaurant fhfghgfnvbncvnghfghfghd",
                    date = "15/05/2023",
                    total = 1000000.0f,
                ),
                ReceiptData(
                    id = 2L,
                    receiptName = "restaurant",
                    date = "04/10/2022",
                    total = 10078.0f,
                ),
                ReceiptData(
                    id = 3L,
                    receiptName = "restaurant",
                    date = "01/01/2023",
                    total = 57465.0f,
                ),
            ),
        foldersListUnarchived = listOf(
            FolderData(
                id = 1L,
                folderName = "folder 123124 dfgdfg  dfgdsf gdfg dfs dfg ",
                isArchived = false,
            ),
            FolderData(
                id = 3L,
                folderName = "folder",
                isArchived = false,
            ),
        ),
//        foldersListArchived = listOf(
//            FolderData(
//                id = 2L,
//                folderName = "folder gafd gsdf er wer erewr  cvcxqe fd",
//                isArchived = true,
//            ),
//            FolderData(
//                id = 4L,
//                folderName = "folder",
//                isArchived = true,
//            ),
//        )
    )
}

private const val MAXIMUM_FOLDER_NAME_LINES = 2