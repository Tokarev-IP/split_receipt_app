package com.iliatokarev.receipt_splitter_app.receipt.presentation.views.dialogs

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.iliatokarev.receipt_splitter_app.R
import com.iliatokarev.receipt_splitter_app.receipt.data.services.DataConstantsReceipt.CONSUMER_NAME_DIVIDER
import com.iliatokarev.receipt_splitter_app.receipt.data.services.DataConstantsReceipt.MAXIMUM_TEXT_LENGTH
import com.iliatokarev.receipt_splitter_app.receipt.data.services.DataConstantsReceipt.ORDER_CONSUMER_NAME_DIVIDER
import com.iliatokarev.receipt_splitter_app.receipt.presentation.FolderData
import com.iliatokarev.receipt_splitter_app.receipt.presentation.views.basic.CancelSaveButtonView
import com.iliatokarev.receipt_splitter_app.receipt.presentation.views.basic.DialogCap

@Composable
internal fun AddNewFolderDialog(
    onDismissRequest: () -> Unit,
    onSaveButtonClicked: (FolderData) -> Unit,
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
                }
            )
        }
    }
}

@Composable
internal fun EditFolderDialog(
    onDismissRequest: () -> Unit,
    onSaveButtonClicked: (FolderData) -> Unit,
    folderData: FolderData,
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
                }
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
            DialogCap(text = titleText) { onDismissRequest() }
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = folderNameText,
                onValueChange = { value ->
                    isFolderNameError = false
                    if (value.length <= MAXIMUM_TEXT_LENGTH)
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
                    if (isFolderNameError && folderNameText.isEmpty()) {
                        Text(text = stringResource(R.string.field_is_empty))
                    } else
                        Text(
                            text = stringResource(
                                R.string.maximum_letters,
                                folderNameText.length,
                                MAXIMUM_TEXT_LENGTH
                            )
                        )
                },
            )
            Spacer(modifier = Modifier.height(8.dp))

            CancelSaveButtonView(
                onSaveClicked = {
                    if (folderNameText.trim().isEmpty()) {
                        isFolderNameError = true
                        return@CancelSaveButtonView
                    }
                    if (CONSUMER_NAME_DIVIDER in folderNameText.trim()
                        || ORDER_CONSUMER_NAME_DIVIDER in folderNameText.trim()
                    ) {
                        isFolderNameError = true
                        return@CancelSaveButtonView
                    }
                    if (folderNameText.length > MAXIMUM_TEXT_LENGTH) {
                        isFolderNameError = true
                        return@CancelSaveButtonView
                    }

                    onSaveButtonClicked(
                        folderData.copy(folderName = folderNameText.trim())
                    )
                },
                onCancelClicked = { onDismissRequest() }
            )
        }
    }
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
private const val MAXIMUM_LINES = 1
