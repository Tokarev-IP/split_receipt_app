package com.iliatokarev.receipt_splitter_app.receipt.presentation.views.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iliatokarev.receipt_splitter_app.R
import com.iliatokarev.receipt_splitter_app.main.basic.icons.BasicTextIcon
import com.iliatokarev.receipt_splitter_app.main.basic.icons.LongTextIcon
import com.iliatokarev.receipt_splitter_app.main.basic.icons.ShortTextIcon
import com.iliatokarev.receipt_splitter_app.main.basic.shimmerBrush
import com.iliatokarev.receipt_splitter_app.receipt.presentation.viewmodels.ReceiptReports

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ReceiptReportBottomSheet(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    allReceiptsReport: ReceiptReports?,
    onShareReport: (String) -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = { onDismissRequest() },
    ) {
        allReceiptsReport?.let {
            ReceiptReportView(
                modifier = modifier,
                shortTextReport = allReceiptsReport.shortReport,
                basicTextReport = allReceiptsReport.basicReport,
                longTextReport = allReceiptsReport.longReport,
                onShareReport = { report ->
                    onShareReport(report)
                    onDismissRequest()
                },
            )
        } ?: run {
            ShimmedAllReceiptsScreenView()
        }
    }
}

@Composable
private fun ReceiptReportView(
    modifier: Modifier = Modifier,
    onShareReport: (String) -> Unit = {},
    shortTextReport: String,
    basicTextReport: String,
    longTextReport: String,
    items: List<Pair<String, @Composable () -> Unit>> = listOf(
        stringResource(R.string.short_report) to {
            Icon(
                Icons.Filled.ShortTextIcon,
                stringResource(R.string.short_report_icon)
            )
        },
        stringResource(R.string.basic_report) to {
            Icon(
                Icons.Filled.BasicTextIcon,
                stringResource(R.string.basic_report_icon)
            )
        },
        stringResource(R.string.long_report) to {
            Icon(
                Icons.Filled.LongTextIcon,
                stringResource(R.string.short_report_icon)
            )
        },
    )
) {
    var selectedItem by rememberSaveable { mutableStateOf(items[0].first) }

    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        item {
            NavigationBar(
                modifier = modifier.fillMaxWidth()
            ) {
                for (item in items) {
                    NavigationBarItem(
                        selected = selectedItem == item.first,
                        onClick = { selectedItem = item.first },
                        label = { Text(item.first) },
                        icon = { item.second() }
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            if (selectedItem == stringResource(R.string.short_report))
                Text(text = shortTextReport)
            else if (selectedItem == stringResource(R.string.basic_report))
                Text(text = basicTextReport)
            else if (selectedItem == stringResource(R.string.long_report))
                Text(text = longTextReport)

            Spacer(modifier = Modifier.height(12.dp))
            OutlinedButton(
                onClick = { onShareReport(selectedItem) },
            ) {
                Icon(Icons.Outlined.Share, stringResource(R.string.share_order_report_button))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = stringResource(R.string.share),
                    fontWeight = FontWeight.Normal,
                    fontSize = 20.sp,
                )
            }
        }
    }
}

@Composable
private fun ShimmedAllReceiptsScreenView(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        repeat(4) {
            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(brush = shimmerBrush(), shape = RoundedCornerShape(16.dp))
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ReceiptReportViewPreview() {
    ReceiptReportView(
        shortTextReport = "short short short short short ",
        basicTextReport = "normal normal normal normal normal normal normal normal normal normal ",
        longTextReport = "long long long long long long long long long long long long long long long long long long long long long long long "
    )
}