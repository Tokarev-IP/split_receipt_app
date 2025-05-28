package com.iliatokarev.receipt_splitter_app.settings.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iliatokarev.receipt_splitter_app.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SignOutDialog(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    onSignOutClicked: () -> Unit,
) {
    BasicAlertDialog(
        modifier = modifier.fillMaxWidth(),
        onDismissRequest = { onDismissRequest() },
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(R.string.sign_out_request),
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp,
                )
                Spacer(modifier = modifier.height(20.dp))
                CancelSignOutButtonView(
                    onCancelClicked = { onDismissRequest() },
                    onSignOutClicked = { onSignOutClicked() }
                )
            }
        }
    }
}

@Composable
private fun CancelSignOutButtonView(
    modifier: Modifier = Modifier,
    onCancelClicked: () -> Unit = {},
    onSignOutClicked: () -> Unit = {},
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
            onClick = { onSignOutClicked() },
        ) {
            Text(text = stringResource(R.string.sign_out))
        }
    }
}