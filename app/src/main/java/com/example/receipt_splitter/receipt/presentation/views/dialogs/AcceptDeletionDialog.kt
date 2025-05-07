package com.example.receipt_splitter.receipt.presentation.views.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.receipt_splitter.receipt.presentation.views.CancelDeleteRowView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AcceptDeletionDialog(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    onAcceptClicked: () -> Unit,
    infoText: String,
) {
    BasicAlertDialog(
        modifier = modifier.fillMaxWidth(),
        onDismissRequest = { onDismissRequest() },
    ) {
        ElevatedCard {
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = infoText,
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp,
                )
                Spacer(modifier = modifier.height(20.dp))
                CancelDeleteRowView(
                    onCancelClicked = { onDismissRequest() },
                    onAcceptClicked = {
                        onAcceptClicked()
                    }
                )
            }
        }
    }
}