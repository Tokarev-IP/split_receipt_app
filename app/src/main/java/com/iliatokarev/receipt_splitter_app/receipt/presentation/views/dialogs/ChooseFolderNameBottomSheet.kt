package com.iliatokarev.receipt_splitter_app.receipt.presentation.views.dialogs

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iliatokarev.receipt_splitter_app.R
import com.iliatokarev.receipt_splitter_app.receipt.presentation.FolderData
import com.iliatokarev.receipt_splitter_app.receipt.presentation.FolderWithReceiptsData
import com.iliatokarev.receipt_splitter_app.receipt.presentation.views.basic.FlowGridLayout

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ChooseFolderNameBottomSheet(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    unarchivedFoldersWithReceipts: List<FolderWithReceiptsData>,
    onFolderIsChosen: (folderId: Long) -> Unit,
) {
    ModalBottomSheet(
        modifier = modifier.fillMaxWidth(),
        onDismissRequest = { onDismissRequest() }
    ) {
        ChooseFolderNameView(
            unarchivedFoldersWithReceipts = unarchivedFoldersWithReceipts,
            onFolderIsChosen = { folderId ->
                onFolderIsChosen(folderId)
            },
        )
    }
}

@Composable
private fun ChooseFolderNameView(
    modifier: Modifier = Modifier,
    unarchivedFoldersWithReceipts: List<FolderWithReceiptsData> = emptyList(),
    onFolderIsChosen: (folderId: Long) -> Unit = {},
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.choose_folder_name_title),
                fontSize = 20.sp,
                fontWeight = FontWeight.Normal,
            )
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))

            FlowGridLayout {
                for (folderData in unarchivedFoldersWithReceipts) {
                    FolderNameItemView(
                        folderData = folderData.folder,
                        onFolderIsChosen = { onFolderIsChosen(folderData.folder.id) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun FolderNameItemView(
    modifier: Modifier = Modifier,
    folderData: FolderData,
    onFolderIsChosen: () -> Unit,
) {
    OutlinedCard(
        onClick = { onFolderIsChosen() },
    ) {
        Text(
            modifier = modifier
                .padding(horizontal = 12.dp, vertical = 8.dp),
            text = folderData.folderName,
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            maxLines = MAXIMUM_LINES_IS_1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

private const val MAXIMUM_LINES_IS_1 = 1

@Preview(showBackground = true)
@Composable
private fun ChooseFolderNameViewPreview() {
    ChooseFolderNameView(
        unarchivedFoldersWithReceipts = listOf(
            FolderWithReceiptsData(
                folder = FolderData(
                    id = 1L,
                    folderName = "folder 123124 dfgdfg  dfgdsf gdfg dfs dfg ",
                    isArchived = false,
                ),
            ),
            FolderWithReceiptsData(
                folder = FolderData(
                    id = 1L,
                    folderName = "folder 123124 dfgdfg  dfgdsf gdfg dfs dfg ",
                    isArchived = false,
                ),
            ),
        ),
    )
}
