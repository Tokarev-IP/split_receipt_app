package com.iliatokarev.receipt_splitter_app.receipt.presentation.screens

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.iliatokarev.receipt_splitter_app.R
import com.iliatokarev.receipt_splitter_app.main.basic.icons.BasicTextIcon
import com.iliatokarev.receipt_splitter_app.main.basic.icons.LongTextIcon
import com.iliatokarev.receipt_splitter_app.main.basic.icons.ShortTextIcon
import com.iliatokarev.receipt_splitter_app.receipt.presentation.ReceiptEvent
import com.iliatokarev.receipt_splitter_app.receipt.presentation.ReceiptViewModel
import com.iliatokarev.receipt_splitter_app.receipt.presentation.viewmodels.ReceiptReports
import com.iliatokarev.receipt_splitter_app.receipt.presentation.views.basic.BackNavigationButton
import com.iliatokarev.receipt_splitter_app.receipt.presentation.views.screens.ShowReportsScreenView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ShowReportsScreen(
    modifier: Modifier = Modifier,
    receiptReports: ReceiptReports,
    receiptViewModel: ReceiptViewModel,
) {
    var selectedItem: ReportItem by remember { mutableStateOf(ReportItem.SHORT_REPORT) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {}

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(text = selectedItem.appBarTitle())
                },
                navigationIcon = {
                    BackNavigationButton { receiptViewModel.setEvent(ReceiptEvent.GoBack) }
                }
            )
        },
        bottomBar = {
            ReportsNavigationBar(
                selectedItem = selectedItem,
                onSelectedItemChange = { item: ReportItem ->
                    selectedItem = item
                }
            )
        }
    ) { innerPadding ->
        ShowReportsScreenView(
            modifier = modifier.padding(innerPadding),
            reportText = when (selectedItem) {
                ReportItem.SHORT_REPORT -> receiptReports.shortReport
                ReportItem.BASIC_REPORT -> receiptReports.basicReport
                ReportItem.LONG_REPORT -> receiptReports.longReport
            },
            onShareClicked = { orderReportText ->
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, orderReportText)
                }
                launcher.launch(Intent.createChooser(shareIntent, "Share order report"))
            }
        )
    }
}

private enum class ReportItem(
    val appBarTitle: @Composable () -> String,
    val icon: @Composable () -> Unit,
) {
    SHORT_REPORT(
        appBarTitle = { stringResource(R.string.short_report) },
        icon = {
            Icon(
                Icons.Filled.ShortTextIcon,
                stringResource(R.string.short_report_icon)
            )
        },
    ),
    BASIC_REPORT(
        appBarTitle = { stringResource(R.string.basic_report) },
        icon = {
            Icon(
                Icons.Filled.BasicTextIcon,
                stringResource(R.string.basic_report_icon)
            )
        },
    ),
    LONG_REPORT(
        appBarTitle = { stringResource(R.string.long_report) },
        icon = {
            Icon(
                Icons.Filled.LongTextIcon,
                stringResource(R.string.short_report_icon)
            )
        },
    )
}

@Composable
private fun ReportsNavigationBar(
    modifier: Modifier = Modifier,
    selectedItem: ReportItem,
    onSelectedItemChange: (ReportItem) -> Unit,
){
    NavigationBar(
        modifier = modifier.fillMaxWidth()
    ) {
        NavigationBarItem(
            selected = selectedItem == ReportItem.SHORT_REPORT,
            onClick = { onSelectedItemChange(ReportItem.SHORT_REPORT) },
            label = { Text(ReportItem.SHORT_REPORT.appBarTitle()) },
            icon = { ReportItem.SHORT_REPORT.icon() }
        )
        NavigationBarItem(
            selected = selectedItem == ReportItem.BASIC_REPORT,
            onClick = { onSelectedItemChange(ReportItem.BASIC_REPORT) },
            label = { Text(ReportItem.BASIC_REPORT.appBarTitle()) },
            icon = { ReportItem.BASIC_REPORT.icon() }
        )
        NavigationBarItem(
            selected = selectedItem == ReportItem.LONG_REPORT,
            onClick = { onSelectedItemChange(ReportItem.LONG_REPORT) },
            label = { Text(ReportItem.LONG_REPORT.appBarTitle()) },
            icon = { ReportItem.LONG_REPORT.icon() }
        )

    }
}