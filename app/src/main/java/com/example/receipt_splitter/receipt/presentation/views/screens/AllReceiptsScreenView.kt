package com.example.receipt_splitter.receipt.presentation.views.screens

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
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.receipt_splitter.R
import com.example.receipt_splitter.main.basic.shimmerBrush
import com.example.receipt_splitter.receipt.presentation.ReceiptData

@Composable
internal fun AllReceiptsScreenView(
    modifier: Modifier = Modifier,
    allReceiptsList: List<ReceiptData>? = emptyList<ReceiptData>(),
    onReceiptClicked: (receiptId: Long) -> Unit = {},
    onDeleteReceiptClicked: (receiptId: Long) -> Unit = {},
    onEditReceiptClicked: (receiptId: Long) -> Unit = {},
) {
    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        allReceiptsList?.let { receipt ->
            if (receipt.isEmpty())
                Text(
                    modifier = modifier.align(Alignment.Center),
                    text = stringResource(R.string.no_receipts_found),
                )

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
            )
        } ?: ShimmedAllReceiptsScreenView()
    }
}

@Composable
private fun AllReceiptsView(
    modifier: Modifier = Modifier,
    allReceiptsList: List<ReceiptData>,
    onReceiptClicked: (receiptId: Long) -> Unit,
    onDeleteReceiptClicked: (receiptId: Long) -> Unit,
    onEditReceiptClicked: (receiptId: Long) -> Unit,
) {
    LazyColumn(
        modifier = modifier
            .animateContentSize()
            .fillMaxSize()
            .padding(horizontal = 12.dp),
    ) {
        items(
            count = allReceiptsList.size,
            key = { index -> allReceiptsList[index].id }
        ) { index ->
            val receiptData = allReceiptsList[index]
            AllReceiptViewItem(
                receiptData = receiptData,
                onReceiptClicked = { onReceiptClicked(receiptData.id) },
                onDeleteReceiptClicked = { onDeleteReceiptClicked(receiptData.id) },
                onEditReceiptClicked = { onEditReceiptClicked(receiptData.id) }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        item {
            Spacer(modifier = Modifier.height(60.dp))
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
            ReceiptViewSubmenuBox(
                modifier = Modifier.align(Alignment.TopEnd),
                onDeleteReceiptClicked = { onDeleteReceiptClicked() },
                onEditReceiptClicked = { onEditReceiptClicked() }
            )
        }
    }
}

@Composable
private fun ReceiptViewSubmenuBox(
    modifier: Modifier = Modifier,
    onDeleteReceiptClicked: () -> Unit,
    onEditReceiptClicked: () -> Unit,
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
    AllReceiptsScreenView(
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
            )

    )
}