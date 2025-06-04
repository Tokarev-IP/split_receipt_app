package com.iliatokarev.receipt_splitter_app.receipt.presentation.views.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import androidx.compose.ui.window.Dialog
import com.iliatokarev.receipt_splitter_app.R
import com.iliatokarev.receipt_splitter_app.receipt.data.services.DataConstantsReceipt.MAXIMUM_TEXT_LENGTH
import com.iliatokarev.receipt_splitter_app.receipt.presentation.views.basic.CancelSaveButtonView
import com.iliatokarev.receipt_splitter_app.receipt.presentation.views.basic.DialogCap

@Composable
internal fun SetConsumerNameDialog(
    modifier: Modifier = Modifier,
    consumerNamesList: List<String>,
    onDismissClick: () -> Unit,
    onNameSelectedClick: (String) -> Unit,
) {
    Dialog(
        onDismissRequest = { onDismissClick() }
    ) {
        Surface(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
        ) {
            SetConsumerNameDialogView(
                consumerNamesList = consumerNamesList,
                onDismissClick = onDismissClick,
                onNameSelectedClick = onNameSelectedClick,
            )
        }
    }
}

@Composable
private fun SetConsumerNameDialogView(
    modifier: Modifier = Modifier,
    consumerNamesList: List<String> = emptyList<String>(),
    onDismissClick: () -> Unit = {},
    onNameSelectedClick: (String) -> Unit = {},
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        item {
            SetConsumerNameView(
                consumerNamesList = consumerNamesList,
                onDismissClick = onDismissClick,
                onNameSelectedClick = onNameSelectedClick,
            )
        }
    }
}

@Composable
private fun SetConsumerNameView(
    modifier: Modifier = Modifier,
    consumerNamesList: List<String>,
    onDismissClick: () -> Unit,
    onNameSelectedClick: (String) -> Unit,
) {
    var consumerName by rememberSaveable { mutableStateOf("") }
    var isConsumerNameErrorState by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        DialogCap(text = stringResource(R.string.set_consumer_name)) { onDismissClick() }
        Spacer(modifier = Modifier.height(8.dp))

        if (consumerNamesList.isNotEmpty()) {
            HorizontalDivider()
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = stringResource(R.string.previous_names),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
            )
            Spacer(modifier = Modifier.height(4.dp))

            for (consumer in consumerNamesList) {
                OutlinedCard(
                    onClick = {
                        if (consumerName != consumer)
                            consumerName = consumer
                    },
                ) {
                    Text(
                        modifier = modifier
                            .padding(horizontal = 12.dp, vertical = 4.dp),
                        text = consumer,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
            }
            HorizontalDivider()
        }

        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = consumerName,
            onValueChange = { name ->
                isConsumerNameErrorState = false
                if (name.length <= MAXIMUM_TEXT_LENGTH)
                    consumerName = name
            },
            isError = isConsumerNameErrorState,
            singleLine = true,
            label = { Text(text = stringResource(R.string.name)) },
            trailingIcon = {
                if (consumerName.isNotEmpty())
                    IconButton(
                        onClick = {
                            consumerName = EMPTY_STRING
                            isConsumerNameErrorState = false
                        }
                    ) {
                        Icon(Icons.Filled.Clear, stringResource(R.string.clear_text_button))
                    }
            },
            supportingText = {
                if (isConsumerNameErrorState && consumerName.isEmpty())
                    Text(text = stringResource(R.string.field_is_empty))
                else
                    Text(
                        text = stringResource(
                            R.string.maximum_letters,
                            consumerName.length,
                            MAXIMUM_TEXT_LENGTH,
                        )
                    )
            }
        )

        Spacer(modifier = Modifier.height(12.dp))
        CancelSaveButtonView(
            onCancelClicked = { onDismissClick() },
            onSaveClicked = {
                if (consumerName.trim().isEmpty() || consumerName.length > MAXIMUM_TEXT_LENGTH) {
                    isConsumerNameErrorState = true
                    return@CancelSaveButtonView
                }
                if (isConsumerNameErrorState != true)
                    onNameSelectedClick(consumerName.trim())
            }
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}

private const val EMPTY_STRING = ""

@Preview(showBackground = true)
@Composable
private fun SetConsumerNameDialogViewPreview() {
    SetConsumerNameDialogView(
        consumerNamesList = listOf("consumer1", "consumer2", "consumer3")
    )
}