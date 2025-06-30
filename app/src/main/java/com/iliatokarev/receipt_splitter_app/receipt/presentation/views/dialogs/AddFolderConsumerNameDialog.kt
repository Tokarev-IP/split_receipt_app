package com.iliatokarev.receipt_splitter_app.receipt.presentation.views.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.iliatokarev.receipt_splitter_app.R
import com.iliatokarev.receipt_splitter_app.receipt.data.services.DataConstantsReceipt.CONSUMER_NAME_DIVIDER
import com.iliatokarev.receipt_splitter_app.receipt.data.services.DataConstantsReceipt.MAXIMUM_CONSUMER_NAME_TEXT_LENGTH
import com.iliatokarev.receipt_splitter_app.receipt.data.services.DataConstantsReceipt.ORDER_CONSUMER_NAME_DIVIDER
import com.iliatokarev.receipt_splitter_app.receipt.presentation.views.basic.CancelAddButtonView
import com.iliatokarev.receipt_splitter_app.receipt.presentation.views.basic.DialogCapView

@Composable
internal fun AddFolderConsumerNameDialog(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    onSaveNewFolderConsumerName: (String) -> Unit,
    allConsumerNamesList: List<String>,
) {
    Dialog(
        onDismissRequest = { onDismissRequest() },
    ) {
        Surface(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
        ) {
            AddFolderConsumerNameView(
                onDismissRequest = { onDismissRequest() },
                onSaveNewFolderConsumerName = { name ->
                    onSaveNewFolderConsumerName(name)
                },
                allConsumerNamesList = allConsumerNamesList,
            )
        }
    }
}

@Composable
private fun AddFolderConsumerNameView(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit = {},
    allConsumerNamesList: List<String> = emptyList<String>(),
    onSaveNewFolderConsumerName: (String) -> Unit = {},
) {
    var consumerNameText by rememberSaveable { mutableStateOf("") }
    var isConsumerNameErrorState by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(12.dp))
        DialogCapView(
            text = stringResource(R.string.add_person_name),
            onDismissClick = { onDismissRequest() },
        )
        Spacer(modifier = Modifier.height(12.dp))

        ConsumerNameTextFieldView(
            consumerNameText = consumerNameText,
            onConsumerNameTextChange = { name ->
                consumerNameText = name
            },
            isConsumerNameErrorState = isConsumerNameErrorState,
            onConsumerNameErrorStateChange = { state ->
                isConsumerNameErrorState = state
            },
            onAddNameClicked = {
                addConsumerName(
                    consumerNameText = consumerNameText.trim(),
                    onSaveNewFolderConsumerName = { name ->
                        onSaveNewFolderConsumerName(name)
                    },
                    onConsumerNameErrorState = {
                        isConsumerNameErrorState = true
                    },
                    allConsumerNamesList = allConsumerNamesList,
                )
            }
        )
        Spacer(modifier = Modifier.height(12.dp))

        CancelAddButtonView(
            onCancelClicked = { onDismissRequest() },
            onAddClicked = {
                addConsumerName(
                    consumerNameText = consumerNameText.trim(),
                    onSaveNewFolderConsumerName = { name ->
                        onSaveNewFolderConsumerName(name)
                    },
                    onConsumerNameErrorState = {
                        isConsumerNameErrorState = true
                    },
                    allConsumerNamesList = allConsumerNamesList,
                )
            }
        )
        Spacer(modifier = Modifier.height(12.dp))
    }
}

private fun addConsumerName(
    consumerNameText: String,
    onSaveNewFolderConsumerName: (String) -> Unit,
    onConsumerNameErrorState: () -> Unit,
    allConsumerNamesList: List<String>,
) {
    if (consumerNameText.isEmpty() || consumerNameText.length > MAXIMUM_CONSUMER_NAME_TEXT_LENGTH
    ) {
        onConsumerNameErrorState()
        return
    }
    if (CONSUMER_NAME_DIVIDER in consumerNameText || ORDER_CONSUMER_NAME_DIVIDER in consumerNameText) {
        onConsumerNameErrorState()
        return
    }

    if (consumerNameText in allConsumerNamesList) {
        onConsumerNameErrorState()
        return
    }

    onSaveNewFolderConsumerName(consumerNameText)
}

@Composable
private fun ConsumerNameTextFieldView(
    consumerNameText: String,
    onConsumerNameTextChange: (String) -> Unit,
    isConsumerNameErrorState: Boolean,
    onConsumerNameErrorStateChange: (Boolean) -> Unit,
    onAddNameClicked: () -> Unit,
) {
    OutlinedTextField(
        value = consumerNameText,
        onValueChange = { name ->
            onConsumerNameErrorStateChange(false)
            if (name.length <= MAXIMUM_CONSUMER_NAME_TEXT_LENGTH)
                onConsumerNameTextChange(name)
        },
        isError = isConsumerNameErrorState,
        label = { Text(text = stringResource(R.string.name)) },
        trailingIcon = {
            if (consumerNameText.isNotEmpty())
                IconButton(
                    onClick = {
                        onConsumerNameTextChange(EMPTY_STRING)
                        onConsumerNameErrorStateChange(false)
                    }
                ) {
                    Icon(Icons.Filled.Clear, stringResource(R.string.clear_text_button))
                }
        },
        supportingText = {
            if (isConsumerNameErrorState && consumerNameText.isEmpty())
                Text(text = stringResource(R.string.field_is_empty))
            else if (CONSUMER_NAME_DIVIDER in consumerNameText.trim() ||
                ORDER_CONSUMER_NAME_DIVIDER in consumerNameText.trim()
            ) Text(text = stringResource(R.string.inappropriate_symbols))
            else if (isConsumerNameErrorState)
                Text(text = stringResource(R.string.name_is_already_existed))
            else if (consumerNameText.isNotEmpty())
                Text(
                    text = stringResource(
                        R.string.maximum_letters,
                        consumerNameText.length,
                        MAXIMUM_CONSUMER_NAME_TEXT_LENGTH,
                    )
                )
        },
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Sentences,
            imeAction = ImeAction.Done,
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                onAddNameClicked()
            }
        ),
        maxLines = MAXIMUM_LINES_IS_5,
    )
}

private const val EMPTY_STRING = ""
private const val MAXIMUM_LINES_IS_5 = 5

@Preview(showBackground = true)
@Composable
private fun AddFolderConsumerNameDialogPreview() {
    AddFolderConsumerNameView()
}