package com.iliatokarev.receipt_splitter_app.receipt.presentation.views.basic

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.iliatokarev.receipt_splitter_app.R

@Composable
internal fun CancelSaveButtonView(
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
internal fun CancelDeleteButtonView(
    modifier: Modifier = Modifier,
    onCancelClicked: () -> Unit = {},
    onDeleteClicked: () -> Unit = {},
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
            onClick = { onDeleteClicked() },
        ) {
            Text(text = stringResource(R.string.delete))
        }
    }
}

@Composable
internal fun CancelClearButtonView(
    modifier: Modifier = Modifier,
    onCancelClicked: () -> Unit = {},
    onClearClicked: () -> Unit = {},
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
            onClick = { onClearClicked() },
        ) {
            Text(text = stringResource(R.string.clear))
        }
    }
}

@Composable
internal fun CancelAddButtonView(
    modifier: Modifier = Modifier,
    onCancelClicked: () -> Unit = {},
    onAddClicked: () -> Unit = {},
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
            onClick = { onAddClicked() },
        ) {
            Text(text = stringResource(R.string.add))
        }
    }
}