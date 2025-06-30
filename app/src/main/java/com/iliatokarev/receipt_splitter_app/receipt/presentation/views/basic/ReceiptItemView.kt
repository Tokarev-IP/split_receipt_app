package com.iliatokarev.receipt_splitter_app.receipt.presentation.views.basic

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iliatokarev.receipt_splitter_app.R
import com.iliatokarev.receipt_splitter_app.main.basic.icons.Archive
import com.iliatokarev.receipt_splitter_app.receipt.presentation.ReceiptData

@Composable
internal fun ReceiptItemView(
    modifier: Modifier = Modifier,
    receiptData: ReceiptData,
    iconButtonCompose: @Composable (modifier: Modifier) -> Unit,
) {
    Column(
        modifier = modifier
//            .padding(
//                start = 12.dp,
//                top = 8.dp,
//                bottom = 8.dp,
//                end = 12.dp,
//            ),
    ) {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.Top,
        ) {
            Column(
                modifier = modifier.weight(12f),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start,
            ) {
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
            iconButtonCompose(Modifier.weight(2f))
        }

        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = modifier,
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
}

@Preview(showBackground = true)
@Composable
private fun ReceiptItemViewPreview() {
    ReceiptItemView(
        receiptData = ReceiptData(
            id = 1,
            receiptName = "Receipt 1",
            translatedReceiptName = "Рецепт 1"
        )
    ) { modifier ->
        IconButton(
            modifier = modifier,
            onClick = {}
        ) {
            Icon(Icons.Filled.Archive, null)
        }
    }
}