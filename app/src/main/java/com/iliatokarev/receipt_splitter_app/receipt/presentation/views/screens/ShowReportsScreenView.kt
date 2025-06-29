package com.iliatokarev.receipt_splitter_app.receipt.presentation.views.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iliatokarev.receipt_splitter_app.R

@Composable
internal fun ShowReportsScreenView(
    modifier: Modifier = Modifier,
    reportText: String,
    onShareClicked: (report: String) -> Unit,
) {
    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        ShowReportsView(
            reportText = reportText,
            onShareClicked = { report: String ->
                onShareClicked(report)
            },
        )
    }
}

@Composable
private fun ShowReportsView(
    modifier: Modifier = Modifier,
    reportText: String,
    onShareClicked: (report: String) -> Unit,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        item {
            TextReportView(
                reportText = reportText,
                onShareClicked = { onShareClicked(reportText) },
            )
        }
    }
}

@Composable
private fun TextReportView(
    modifier: Modifier = Modifier,
    reportText: String,
    onShareClicked: () -> Unit,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        SelectionContainer(
            modifier = modifier.fillMaxWidth()
        ) {
            Text(text = reportText)
        }
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = { onShareClicked() },
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