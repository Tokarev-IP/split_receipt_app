package com.iliatokarev.receipt_splitter_app.receipt.presentation.views.dialogs

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.iliatokarev.receipt_splitter_app.R
import com.iliatokarev.receipt_splitter_app.main.basic.convertMillisToDate
import com.iliatokarev.receipt_splitter_app.main.basic.icons.Calendar
import com.iliatokarev.receipt_splitter_app.receipt.data.services.DataConstantsReceipt.MAXIMUM_TEXT_LENGTH
import com.iliatokarev.receipt_splitter_app.receipt.data.services.DataConstantsReceipt.MAXIMUM_PERCENT
import com.iliatokarev.receipt_splitter_app.receipt.data.services.DataConstantsReceipt.MAXIMUM_SUM
import com.iliatokarev.receipt_splitter_app.receipt.presentation.ReceiptData
import com.iliatokarev.receipt_splitter_app.receipt.presentation.views.basic.CancelSaveButtonView
import com.iliatokarev.receipt_splitter_app.receipt.presentation.views.basic.DialogCapView

@Composable
internal fun EditReceiptDialog(
    receiptData: ReceiptData,
    onCancelButtonClicked: () -> Unit,
    onSaveButtonClicked: (receiptData: ReceiptData) -> Unit,
) {
    Dialog(
        onDismissRequest = { onCancelButtonClicked() },
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        EditReceiptDialogSurface(
            receiptData = receiptData,
            onCancelButtonClicked = {
                onCancelButtonClicked()
            },
            onSaveButtonClicked = { receiptData ->
                onSaveButtonClicked(receiptData)
            },
        )
    }
}

@Composable
private fun EditReceiptDialogSurface(
    modifier: Modifier = Modifier,
    receiptData: ReceiptData = ReceiptData(id = 0),
    onCancelButtonClicked: () -> Unit = {},
    onSaveButtonClicked: (receiptData: ReceiptData) -> Unit = {}
) {
    var showCalendarDialog by rememberSaveable { mutableStateOf(false) }
    var dateText: String by rememberSaveable { mutableStateOf(receiptData.date) }

    Surface(
        modifier = modifier.fillMaxSize(),
        shape = RoundedCornerShape(16.dp),
    ) {
        EditReceiptDialogView(
            receiptData = receiptData,
            onCancelButtonClicked = {
                onCancelButtonClicked()
            },
            onSaveButtonClicked = { receiptData ->
                onSaveButtonClicked(receiptData)
            },
            onShowCalendarDialog = {
                showCalendarDialog = true
            },
            dateText = { dateText },
            onDateTextChange = { date ->
                dateText = date
            },
        )
        if (showCalendarDialog)
            CalendarDialog(
                onDismissRequest = { showCalendarDialog = false },
                onSaveButtonClicked = { date ->
                    dateText = date
                    showCalendarDialog = false
                },
            )
    }
}

@Composable
private fun EditReceiptDialogView(
    modifier: Modifier = Modifier,
    receiptData: ReceiptData,
    onCancelButtonClicked: () -> Unit,
    onSaveButtonClicked: (receiptData: ReceiptData) -> Unit,
    onShowCalendarDialog: () -> Unit,
    dateText: () -> String,
    onDateTextChange: (String) -> Unit,
) {
    var nameText: String by rememberSaveable { mutableStateOf(receiptData.receiptName) }
    var translatedNameText: String by rememberSaveable {
        mutableStateOf(
            receiptData.translatedReceiptName ?: ""
        )
    }
    var totalSumText: String by rememberSaveable { mutableStateOf(receiptData.total.toString()) }
    var taxText: String by rememberSaveable { mutableStateOf(receiptData.tax?.toString() ?: "") }
    var discountText: String by rememberSaveable {
        mutableStateOf(
            receiptData.discount?.toString() ?: ""
        )
    }
    var tipText: String by rememberSaveable { mutableStateOf(receiptData.tip?.toString() ?: "") }

    var isNameError by rememberSaveable { mutableStateOf(false) }
    var isTranslatedNameError by rememberSaveable { mutableStateOf(false) }
    var isDateError by rememberSaveable { mutableStateOf(false) }
    var isTotalSumError by rememberSaveable { mutableStateOf(false) }
    var isTaxError by rememberSaveable { mutableStateOf(false) }
    var isDiscountError by rememberSaveable { mutableStateOf(false) }
    var isTipError by rememberSaveable { mutableStateOf(false) }
    var isTipSumError by rememberSaveable { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            DialogCapView(text = stringResource(R.string.edit_receipt_info_title)) { onCancelButtonClicked() }
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = nameText,
                onValueChange = { value ->
                    isNameError = false
                    if (value.length <= MAXIMUM_TEXT_LENGTH)
                        nameText = value
                },
                label = { Text(text = stringResource(R.string.receipt_name)) },
                trailingIcon = {
                    if (nameText.isNotEmpty())
                        IconButton(
                            onClick = {
                                nameText = EMPTY_STRING
                                isNameError = false
                            }
                        ) {
                            Icon(Icons.Filled.Clear, stringResource(R.string.clear_text_button))
                        }
                },
                maxLines = MAXIMUM_LINES,
                supportingText = {
                    if (isNameError && nameText.isEmpty())
                        Text(text = stringResource(R.string.field_is_empty))
                    else
                        Text(
                            text = stringResource(
                                R.string.maximum_letters,
                                nameText.length,
                                MAXIMUM_TEXT_LENGTH
                            )
                        )
                },
                isError = isNameError,
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = translatedNameText,
                onValueChange = { value ->
                    isTranslatedNameError = false
                    if (value.length <= MAXIMUM_TEXT_LENGTH)
                        translatedNameText = value
                },
                label = { Text(text = stringResource(R.string.translated_receipt_name)) },
                trailingIcon = {
                    if (nameText.isNotEmpty())
                        IconButton(
                            onClick = {
                                translatedNameText = EMPTY_STRING
                                isTranslatedNameError = false
                            }
                        ) {
                            Icon(Icons.Filled.Clear, stringResource(R.string.clear_text_button))
                        }
                },
                maxLines = MAXIMUM_LINES,
                supportingText = {
                    if (translatedNameText.isEmpty())
                        Text(text = stringResource(R.string.keep_this_field_empty))
                    else
                        Text(
                            text = stringResource(
                                R.string.maximum_letters,
                                translatedNameText.length,
                                MAXIMUM_TEXT_LENGTH
                            )
                        )
                },
                isError = isTranslatedNameError,
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = dateText(),
                onValueChange = { value ->
                    isDateError = false
                    if (value.length <= MAXIMUM_TEXT_LENGTH)
                        onDateTextChange(value)
                },
                singleLine = true,
                label = { Text(text = stringResource(R.string.date)) },
                trailingIcon = {
                    if (dateText().isNotEmpty())
                        IconButton(
                            onClick = {
                                onDateTextChange(EMPTY_STRING)
                                isDateError = false
                            }
                        ) {
                            Icon(Icons.Filled.Clear, stringResource(R.string.clear_text_button))
                        }
                },
                leadingIcon = {
                    IconButton(
                        onClick = {
                            onShowCalendarDialog()
                            isDateError = false
                        }
                    ) {
                        Icon(Icons.Filled.Calendar, stringResource(R.string.open_calendar_button))
                    }
                },
                supportingText = {
                    if (isDateError && dateText().isEmpty())
                        Text(text = stringResource(R.string.field_is_empty))
                    else if (isDateError)
                        Text(
                            text = stringResource(
                                R.string.maximum_letters,
                                dateText().length,
                                MAXIMUM_TEXT_LENGTH
                            )
                        )
                },
                isError = isDateError,
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = totalSumText,
                onValueChange = { value ->
                    isTotalSumError = false
                    if (value.length <= MAXIMUM_TEXT_LENGTH)
                        totalSumText = value.trim()
                },
                singleLine = true,
                label = { Text(text = stringResource(R.string.total)) },
                trailingIcon = {
                    if (totalSumText.isNotEmpty())
                        IconButton(
                            onClick = {
                                totalSumText = EMPTY_STRING
                                isTotalSumError = false
                            }
                        ) {
                            Icon(Icons.Filled.Clear, stringResource(R.string.clear_text_button))
                        }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                supportingText = {
                    if (isTotalSumError && totalSumText.isEmpty())
                        Text(text = stringResource(R.string.field_is_empty))
                    else
                        Text(
                            text = stringResource(
                                R.string.must_be_from_to,
                                MINIMUM_is_0,
                                MAXIMUM_SUM
                            )
                        )
                },
                isError = isTotalSumError,
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = taxText,
                onValueChange = { value ->
                    isTaxError = false
                    if (value.length <= MAXIMUM_TEXT_LENGTH)
                        taxText = value.trim()
                },
                label = { Text(text = stringResource(R.string.tax)) },
                singleLine = true,
                trailingIcon = {
                    if (taxText.isNotEmpty())
                        IconButton(
                            onClick = {
                                taxText = EMPTY_STRING
                                isTaxError = false
                            }
                        ) {
                            Icon(Icons.Filled.Clear, stringResource(R.string.clear_text_button))
                        }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                supportingText = {
                    if (taxText.isNotEmpty())
                        Text(
                            text = stringResource(
                                R.string.must_be_from_to_percent,
                                MINIMUM_is_0,
                                MAXIMUM_PERCENT,
                            )
                        )
                    else Text(text = stringResource(R.string.keep_this_field_empty))
                },
                isError = isTaxError,
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = discountText,
                onValueChange = { value ->
                    isDiscountError = false
                    if (value.length <= MAXIMUM_TEXT_LENGTH)
                        discountText = value.trim()
                },
                label = { Text(text = stringResource(R.string.discount)) },
                singleLine = true,
                trailingIcon = {
                    if (discountText.isNotEmpty())
                        IconButton(
                            onClick = {
                                discountText = EMPTY_STRING
                                isDiscountError = false
                            }
                        ) {
                            Icon(Icons.Filled.Clear, stringResource(R.string.clear_text_button))
                        }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                supportingText = {
                    if (discountText.isNotEmpty())
                        Text(
                            text = stringResource(
                                R.string.must_be_from_to_percent,
                                MINIMUM_is_0,
                                MAXIMUM_PERCENT,
                            )
                        )
                    else Text(text = stringResource(R.string.keep_this_field_empty))
                },
                isError = isDiscountError,
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = tipText,
                onValueChange = { value ->
                    isTipError = false
                    if (value.length <= MAXIMUM_TEXT_LENGTH)
                        tipText = value.trim()
                },
                label = { Text(text = stringResource(R.string.tip)) },
                trailingIcon = {
                    if (tipText.isNotEmpty())
                        IconButton(
                            onClick = {
                                tipText = EMPTY_STRING
                                isTipError = false
                            }
                        ) {
                            Icon(Icons.Filled.Clear, stringResource(R.string.clear_text_button))
                        }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                supportingText = {
                    if (tipText.isNotEmpty())
                        Text(
                            text = stringResource(
                                R.string.must_be_from_to_percent,
                                MINIMUM_is_0,
                                MAXIMUM_PERCENT,
                            )
                        )
                    else Text(text = stringResource(R.string.keep_this_field_empty))
                },
                isError = isTipError,
            )
            Spacer(modifier = Modifier.height(12.dp))

            CancelSaveButtonView(
                onCancelClicked = {
                    onCancelButtonClicked()
                },
                onSaveClicked = {
                    nameText = nameText.trim()
                    onDateTextChange(dateText().trim())

                    isNameError = nameText.trim().isEmpty()
                    isTranslatedNameError = translatedNameText.length > MAXIMUM_TEXT_LENGTH
                    isDateError = dateText().trim().isEmpty() || dateText().length > MAXIMUM_TEXT_LENGTH
                    totalSumText.toFloatOrNull()?.let { totalSum ->
                        if (totalSum < MINIMUM_is_0 || totalSum > MAXIMUM_SUM)
                            isTotalSumError = true
                    } ?: run { isTotalSumError = true }
                    taxText.toFloatOrNull()?.let { tax ->
                        if (tax < MINIMUM_is_0 || tax > MAXIMUM_PERCENT)
                            isTaxError = true
                    }
                    discountText.toFloatOrNull()?.let { discount ->
                        if (discount < MINIMUM_is_0 || discount > MAXIMUM_PERCENT)
                            isDiscountError = true
                    }
                    tipText.toFloatOrNull()?.let { tip ->
                        if (tip < MINIMUM_is_0 || tip > MAXIMUM_PERCENT)
                            isTipError = true
                    }

                    if (isNameError || isTranslatedNameError || isDateError || isTotalSumError || isTaxError || isDiscountError || isTipError || isTipSumError)
                        return@CancelSaveButtonView

                    val receiptData = receiptData.copy(
                        receiptName = nameText.trim(),
                        translatedReceiptName =
                            if (translatedNameText.isEmpty()) null else translatedNameText.trim(),
                        date = dateText().trim(),
                        total = totalSumText.trim().toFloat(),
                        tax = taxText.trim().toFloatOrNull(),
                        discount = discountText.trim().toFloatOrNull(),
                        tip = tipText.trim().toFloatOrNull(),
                    )
                    onSaveButtonClicked(receiptData)
                }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CalendarDialog(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    onSaveButtonClicked: (date: String) -> Unit
) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(
        modifier = modifier.fillMaxWidth(),
        onDismissRequest = { onDismissRequest() },
        confirmButton = {
            Button(
                onClick = {
                    val dateText: String? = datePickerState.selectedDateMillis?.run {
                        this.convertMillisToDate()
                    }
                    dateText?.let {
                        onSaveButtonClicked(dateText)
                    } ?: onDismissRequest()
                }
            ) { Text(text = stringResource(R.string.save)) }
        },
        dismissButton = {
            OutlinedButton(
                onClick = { onDismissRequest() }
            ) { Text(text = stringResource(R.string.cancel)) }
        },
    ) {
        DatePicker(
            state = datePickerState,
            showModeToggle = true
        )
    }
}

private const val MAXIMUM_LINES = 4
private const val MINIMUM_is_0 = 0
private const val EMPTY_STRING = ""

@Composable
@Preview(showBackground = true)
private fun EditReceiptDialogViewPreview() {
    EditReceiptDialogSurface(
        receiptData = ReceiptData(
            id = 5,
            receiptName = "good food soup with tomato",
            date = "september 2025",
            total = 48393.5f,
            discount = 10.4f,
            tip = 20f,
        )
    )
}