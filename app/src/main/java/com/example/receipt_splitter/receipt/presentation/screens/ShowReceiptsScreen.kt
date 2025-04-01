package com.example.receipt_splitter.receipt.presentation.screens

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.receipt_splitter.R
import com.example.receipt_splitter.receipt.presentation.ReceiptUiEvent
import com.example.receipt_splitter.receipt.presentation.ReceiptViewModel
import com.example.receipt_splitter.receipt.presentation.SplitReceiptData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowReceiptsScreen(
    modifier: Modifier = Modifier,
    receiptViewModel: ReceiptViewModel,
) {
    val splitReceiptDataList by receiptViewModel.getAllReceiptsList().collectAsState()

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.history_of_receipts)) },
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                modifier = modifier.padding(20.dp),
                onClick = { receiptViewModel.setUiEvent(ReceiptUiEvent.AddNewReceipt) }
            ) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = stringResource(id = R.string.add_new_receipt_button)
                )
            }
        }
    ) { innerPadding ->
        if (splitReceiptDataList.isEmpty())
            Box(modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)) {
                Text(
                    modifier = modifier.align(Alignment.Center),
                    text = stringResource(R.string.no_receipts_found)
                )
            }
        else
            ShowReceiptsView(
                modifier = modifier.padding(innerPadding),
                splitReceiptDataList = { splitReceiptDataList },
                onReceiptClicked = { splitReceiptData ->
                    receiptViewModel.setUiEvent(
                        ReceiptUiEvent.OpenSplitReceiptScreen(
                            splitReceiptData
                        )
                    )
                },
                onDeleteReceiptClicked = { receiptId ->
                    receiptViewModel.setUiEvent(ReceiptUiEvent.ReceiptDeletion(receiptId))
                }
            )
    }
}

@Composable
private fun ShowReceiptsView(
    modifier: Modifier = Modifier,
    splitReceiptDataList: () -> List<SplitReceiptData>,
    onReceiptClicked: (splitReceiptData: SplitReceiptData) -> Unit = {},
    onDeleteReceiptClicked: (receiptId: Long) -> Unit = {},
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
    ) {
        items(
            count = splitReceiptDataList().size,
            key = { index -> splitReceiptDataList()[index].id }
        ) { index ->
            ReceiptViewItem(
                splitReceiptData = { splitReceiptDataList()[index] },
                onReceiptClicked = { onReceiptClicked(splitReceiptDataList()[index]) },
                onDeleteReceiptClicked = { onDeleteReceiptClicked(splitReceiptDataList()[index].id) },
            )
        }
    }
}

@Composable
private fun ReceiptViewItem(
    modifier: Modifier = Modifier,
    splitReceiptData: () -> SplitReceiptData,
    onReceiptClicked: () -> Unit = {},
    onDeleteReceiptClicked: () -> Unit = {},
) {
    OutlinedCard(
        onClick = { onReceiptClicked() },
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Box(modifier = modifier.fillMaxWidth()) {
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
            ) {
                Text(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Normal,
                    modifier = modifier
                        .align(Alignment.Start)
                        .padding(end = 40.dp),
                    text = splitReceiptData().restaurant,
                )
                Spacer(modifier = modifier.height(16.dp))
                Row(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(end = 40.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(text = splitReceiptData().date)
                    splitReceiptData().total?.let { total ->
                        Text(text = stringResource(R.string.total_of_receipt, total))
                    }
                }
            }
            ReceiptViewSubmenuBox(
                modifier = modifier.align(Alignment.TopEnd),
                onDeleteReceiptClicked = { onDeleteReceiptClicked() },
            )
        }
    }
}

@Composable
private fun ReceiptViewSubmenuBox(
    modifier: Modifier = Modifier,
    onDeleteReceiptClicked: () -> Unit = {},
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    Box(modifier = modifier) {
        IconButton(
            onClick = { expanded = !expanded },
        ) {
            Icon(
                Icons.Filled.MoreVert,
                contentDescription = stringResource(R.string.receipt_view_submenu_button)
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text(text = stringResource(R.string.delete)) },
                onClick = { onDeleteReceiptClicked() },
                leadingIcon = {
                    Icon(
                        Icons.Filled.Delete,
                        contentDescription = stringResource(R.string.delete)
                    )
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ShowReceiptViewPreview() {
    ShowReceiptsView(
        splitReceiptDataList = {
            listOf<SplitReceiptData>(
                SplitReceiptData(
                    id = 1L,
                    restaurant = "restaurant fhfghgfnvbncvnghfghfghd",
                    date = "15/05/2023",
                    total = 1000000.0f,
                    orders = emptyList(),
                ),
                SplitReceiptData(
                    id = 2L,
                    restaurant = "restaurant",
                    date = "04/10/2022",
                    total = 10078.0f,
                    orders = emptyList(),
                ),
                SplitReceiptData(
                    id = 3L,
                    restaurant = "restaurant",
                    date = "01/01/2023",
                    total = 57465.0f,
                    orders = emptyList(),
                ),
            )
        }
    )
}

