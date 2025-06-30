package com.iliatokarev.receipt_splitter_app.receipt.presentation.views.dialogs

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import com.iliatokarev.receipt_splitter_app.receipt.data.services.DataConstantsReceipt.MAXIMUM_FOLDER_NAME_TEXT_LENGTH
import com.iliatokarev.receipt_splitter_app.receipt.data.services.DataConstantsReceipt.ORDER_CONSUMER_NAME_DIVIDER
import com.iliatokarev.receipt_splitter_app.receipt.presentation.FolderData
import com.iliatokarev.receipt_splitter_app.receipt.presentation.views.basic.CancelSaveButtonView
import com.iliatokarev.receipt_splitter_app.receipt.presentation.views.basic.DialogCapView

@Composable
internal fun AddNewFolderDialog(
    onDismissRequest: () -> Unit,
    onSaveButtonClicked: (FolderData) -> Unit,
    allFoldersName: List<String>,
) {
    Dialog(
        onDismissRequest = { onDismissRequest() }
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp)
        ) {
            EditFolderDialogView(
                onDismissRequest = { onDismissRequest() },
                folderData = FolderData(id = 0, folderName = EMPTY_STRING),
                titleText = stringResource(R.string.add_new_folder_title),
                onSaveButtonClicked = { folderData ->
                    onSaveButtonClicked(folderData)
                },
                allFoldersName = allFoldersName,
            )
        }
    }
}

@Composable
internal fun EditFolderDialog(
    onDismissRequest: () -> Unit,
    onSaveButtonClicked: (FolderData) -> Unit,
    folderData: FolderData,
    allFoldersName: List<String>,
) {
    Dialog(
        onDismissRequest = { onDismissRequest() }
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp)
        ) {
            EditFolderDialogView(
                folderData = folderData,
                titleText = stringResource(R.string.edit_folder_title),
                onDismissRequest = { onDismissRequest() },
                onSaveButtonClicked = { folderData ->
                    onSaveButtonClicked(folderData)
                },
                allFoldersName = allFoldersName,
            )
        }
    }
}

@Composable
private fun EditFolderDialogView(
    modifier: Modifier = Modifier,
    titleText: String = EMPTY_STRING,
    onDismissRequest: () -> Unit = {},
    onSaveButtonClicked: (FolderData) -> Unit = {},
    folderData: FolderData,
    allFoldersName: List<String> = emptyList(),
) {
    var folderNameText by rememberSaveable { mutableStateOf(folderData.folderName) }
    var isFolderNameError by rememberSaveable { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            DialogCapView(text = titleText) { onDismissRequest() }
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = folderNameText,
                onValueChange = { value ->
                    isFolderNameError = false
                    if (value.length <= MAXIMUM_FOLDER_NAME_TEXT_LENGTH)
                        folderNameText = value
                },
                maxLines = MAXIMUM_LINES,
                label = { Text(text = stringResource(R.string.name)) },
                trailingIcon = {
                    if (folderNameText.isNotEmpty())
                        IconButton(
                            onClick = {
                                folderNameText = EMPTY_STRING
                                isFolderNameError = false
                            }
                        ) {
                            Icon(Icons.Filled.Clear, stringResource(R.string.clear_text_button))
                        }
                },
                isError = isFolderNameError,
                supportingText = {
                    if (isFolderNameError) {
                        if (folderNameText.trim().isEmpty())
                            Text(text = stringResource(R.string.field_is_empty))
                        else if (folderNameText.trim() in allFoldersName)
                            Text(text = stringResource(R.string.name_is_already_existed))
                        else if (CONSUMER_NAME_DIVIDER in folderNameText.trim()
                            || ORDER_CONSUMER_NAME_DIVIDER in folderNameText.trim()
                        ) Text(text = stringResource(R.string.inappropriate_symbols))
                    } else
                        Text(
                            text = stringResource(
                                R.string.maximum_letters,
                                folderNameText.length,
                                MAXIMUM_FOLDER_NAME_TEXT_LENGTH
                            )
                        )
                },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        isFolderNameValid(
                            folderNameText = folderNameText.trim(),
                            onFolderNameError = { isFolderNameError = true },
                            onSave = {
                                onSaveButtonClicked(
                                    folderData.copy(folderName = folderNameText)
                                )
                            },
                            allFoldersName = allFoldersName,
                        )
                    }
                ),
            )
            Spacer(modifier = Modifier.height(8.dp))

            CancelSaveButtonView(
                onSaveClicked = {
                    isFolderNameValid(
                        folderNameText = folderNameText.trim(),
                        onFolderNameError = { isFolderNameError = true },
                        onSave = {
                            onSaveButtonClicked(
                                folderData.copy(folderName = folderNameText)
                            )
                        },
                        allFoldersName = allFoldersName,
                    )
                },
                onCancelClicked = { onDismissRequest() }
            )
        }
    }
}

private fun isFolderNameValid(
    folderNameText: String,
    onFolderNameError: () -> Unit,
    onSave: () -> Unit,
    allFoldersName: List<String>,
) {
    if (folderNameText in allFoldersName) {
        onFolderNameError()
        return
    }
    if (folderNameText.isEmpty()) {
        onFolderNameError()
        return
    }
    if (CONSUMER_NAME_DIVIDER in folderNameText || ORDER_CONSUMER_NAME_DIVIDER in folderNameText) {
        onFolderNameError()
        return
    }
    if (folderNameText.length > MAXIMUM_FOLDER_NAME_TEXT_LENGTH) {
        onFolderNameError()
        return
    }
    onSave()
}

@Preview(showBackground = true)
@Composable
private fun EditFolderDialogViewPreview() {
    EditFolderDialogView(
        folderData = FolderData(id = 0),
        titleText = "title"
    )
}

private const val EMPTY_STRING = ""
private const val MAXIMUM_LINES = 5
