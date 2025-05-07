package com.example.receipt_splitter.receipt.presentation.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.receipt_splitter.R
import com.example.receipt_splitter.receipt.presentation.OrderData

@Composable
internal fun OrderItemView(
    modifier: Modifier = Modifier,
    orderData: () -> OrderData,
) {
    val orderData = orderData()

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.Top,
    ) {
        Column(
            modifier = modifier
                .padding(end = 16.dp)
                .weight(20f),
        ) {
            Text(
                text = orderData.name,
                fontSize = 20.sp,
                textAlign = TextAlign.Left,
            )
            orderData.translatedName?.let { translatedName ->
                Spacer(modifier = modifier.height(4.dp))
                Text(
                    text = translatedName,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Left,
                )
            }
        }
        Text(
            modifier = modifier.weight(4f),
            text = stringResource(R.string.quantity_x, orderData.quantity),
            fontSize = 20.sp,
            textAlign = TextAlign.Left,
        )
        Text(
            modifier = modifier.weight(8f),
            text = orderData.price.toString(),
            fontSize = 20.sp,
            textAlign = TextAlign.End,
        )
    }
}

@Composable
internal fun CancelSaveRowView(
    modifier: Modifier = Modifier,
    onCancelClicked: () -> Unit = {},
    onSaveClicked: () -> Unit = {},
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OutlinedButton(
            onClick = { onCancelClicked() },
        ) {
            Text(text = stringResource(R.string.cancel))
        }

        Button(
            onClick = { onSaveClicked() },
        ) {
            Text(text = stringResource(R.string.save))
        }
    }
}

@Composable
internal fun CancelDeleteRowView(
    modifier: Modifier = Modifier,
    onCancelClicked: () -> Unit = {},
    onAcceptClicked: () -> Unit = {},
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OutlinedButton(
            onClick = { onCancelClicked() },
        ) {
            Text(text = stringResource(R.string.cancel))
        }

        Button(
            onClick = { onAcceptClicked() },
        ) {
            Text(text = stringResource(R.string.delete))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun OrderItemViewPreview(){
    OrderItemView(
        orderData = { OrderData(id = 1, receiptId = 2)}
    )
}