package com.example.receipt_splitter.receipt.presentation.views.dialogs

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.receipt_splitter.R
import com.example.receipt_splitter.main.basic.convertMillisToDate
import com.example.receipt_splitter.receipt.presentation.ReceiptData
import com.example.receipt_splitter.receipt.presentation.views.CancelSaveRowView

@Composable
internal fun EditReceiptDialog(
    modifier: Modifier = Modifier,
    receiptData: () -> ReceiptData,
    onCancelButtonClicked: () -> Unit,
    onSaveButtonClicked: (receiptData: ReceiptData) -> Unit,
) {
    Dialog(
        onDismissRequest = { onCancelButtonClicked() },
    ) {
        EditReceiptDialogSurface(
            receiptData = { receiptData() },
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
    receiptData: () -> ReceiptData = { ReceiptData(id = 0) },
    onCancelButtonClicked: () -> Unit = {},
    onSaveButtonClicked: (receiptData: ReceiptData) -> Unit = {}
) {
    var showCalendarDialog by rememberSaveable { mutableStateOf(false) }
    var dateText: String by rememberSaveable { mutableStateOf(receiptData().date) }

    Surface(
        modifier = modifier.fillMaxSize(),
    ) {
        Box {
            EditReceiptDialogView(
                receiptData = receiptData(),
                onCancelButtonClicked = {
                    onCancelButtonClicked()
                },
                onSaveButtonClicked = { receiptData ->
                    onSaveButtonClicked(receiptData)
                },
                onShowCalendarDialog = {
                    showCalendarDialog = true
                },
                dateText = { dateText }
            )
            if (showCalendarDialog)
                CalendarDialog(
                    modifier = modifier.align(Alignment.Center),
                    onDismissRequest = { showCalendarDialog = false },
                    onSaveButtonClicked = { date ->
                        dateText = date
                        showCalendarDialog = false
                    },
                )
        }
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
) {
    val interactionSourceDateField = remember { MutableInteractionSource() }
    val isPressed by interactionSourceDateField.collectIsPressedAsState()

    LaunchedEffect(key1 = isPressed) {
        if (isPressed)
            onShowCalendarDialog()
    }

    var restaurantNameText: String by rememberSaveable { mutableStateOf(receiptData.restaurant) }
    var translatedRestaurantNameText: String by rememberSaveable {
        mutableStateOf(
            receiptData.translatedRestaurant ?: ""
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
    var tipSumText: String by rememberSaveable {
        mutableStateOf(
            receiptData.tipSum?.toString() ?: ""
        )
    }

    var isRestaurantNameError by rememberSaveable { mutableStateOf(false) }
    var isTotalSumError by rememberSaveable { mutableStateOf(false) }
    var isTaxError by rememberSaveable { mutableStateOf(false) }
    var isDiscountError by rememberSaveable { mutableStateOf(false) }
    var isTipError by rememberSaveable { mutableStateOf(false) }
    var isTipSumError by rememberSaveable { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        item {
            Spacer(modifier = Modifier.height(60.dp))
//            Row() {
//                Text(text = stringResource(R.string.edit_receipt_info))
//                IconButton(
//                    modifier = modifier.align(Alignment.TopEnd),
//                    onClick = { onCancelButtonClicked() },
//                ) {
//                    Icon(
//                        imageVector = Icons.Filled.Clear,
//                        contentDescription = stringResource(R.string.close_the_dialog),
//                    )
//                }
//            }

            OutlinedTextField(
                value = restaurantNameText,
                onValueChange = { value ->
                    if (value.length <= MAXIMUM_LENGTH)
                        restaurantNameText = value
                },
                label = { Text(text = stringResource(R.string.restaurant_name)) },
                trailingIcon = {
                    if (restaurantNameText.isNotEmpty())
                        IconButton(
                            onClick = {
                                restaurantNameText = EMPTY_STRING
                                isRestaurantNameError = false
                            }
                        ) {
                            Icon(Icons.Filled.Clear, stringResource(R.string.clear_text_button))
                        }
                },
                maxLines = MAXIMUM_LINES,
                supportingText = {
                    if (isRestaurantNameError && restaurantNameText.isEmpty())
                        Text(text = stringResource(R.string.is_empty))
                    else
                        Text(
                            text = stringResource(
                                R.string.maximum_letters,
                                restaurantNameText.length,
                                MAXIMUM_LENGTH
                            )
                        )
                },
                isError = isRestaurantNameError,
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = translatedRestaurantNameText,
                onValueChange = { value ->
                    if (value.length <= MAXIMUM_LENGTH)
                        translatedRestaurantNameText = value
                },
                label = { Text(text = stringResource(R.string.translated_restaurant_name)) },
                trailingIcon = {
                    if (restaurantNameText.isNotEmpty())
                        IconButton(onClick = { translatedRestaurantNameText = EMPTY_STRING }) {
                            Icon(Icons.Filled.Clear, stringResource(R.string.clear_text_button))
                        }
                },
                maxLines = MAXIMUM_LINES,
                supportingText = {
                    if (translatedRestaurantNameText.isNotEmpty())
                        Text(
                            text = stringResource(
                                R.string.maximum_letters,
                                translatedRestaurantNameText.length,
                                MAXIMUM_LENGTH
                            )
                        )
                },
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = dateText(),
                onValueChange = {},
                readOnly = true,
                singleLine = true,
                label = { Text(text = stringResource(R.string.date)) },
                interactionSource = interactionSourceDateField,
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = totalSumText,
                onValueChange = { value ->
                    isTotalSumError = false
                    if (value.length <= MAXIMUM_LENGTH)
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
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                supportingText = {
                    if (isTotalSumError && totalSumText.isEmpty())
                        Text(text = stringResource(R.string.is_empty))
                    else
                        Text(
                            text = stringResource(
                                R.string.must_be_from_to,
                                MINIMUM_is_0,
                                MAXIMUM_TOTAL_SUM
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
                    if (value.length <= MAXIMUM_LENGTH)
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
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                supportingText = {
                    if (taxText.isNotEmpty())
                        Text(
                            text = stringResource(
                                R.string.must_be_from_to_percent,
                                MINIMUM_is_0,
                                MAXIMUM_PERCENT,
                            )
                        )
                },
                isError = isTaxError,
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = discountText,
                onValueChange = { value ->
                    isDiscountError = false
                    if (value.length <= MAXIMUM_LENGTH)
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
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                supportingText = {
                    if (discountText.isNotEmpty())
                        Text(
                            text = stringResource(
                                R.string.must_be_from_to_percent,
                                MINIMUM_is_0,
                                MAXIMUM_PERCENT,
                            )
                        )
                },
                isError = isDiscountError,
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = tipText,
                onValueChange = { value ->
                    isTipError = false
                    if (value.length <= MAXIMUM_LENGTH)
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
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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
                },
                isError = isTipError,
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = tipSumText,
                onValueChange = { value ->
                    isTipSumError = false
                    if (value.length <= MAXIMUM_LENGTH)
                        tipSumText = value.trim()
                },
                label = { Text(text = stringResource(R.string.tip_sum)) },
                trailingIcon = {
                    if (tipSumText.isNotEmpty())
                        IconButton(
                            onClick = {
                                tipSumText = EMPTY_STRING
                                isTipSumError = false
                            }
                        ) {
                            Icon(Icons.Filled.Clear, stringResource(R.string.clear_text_button))
                        }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                supportingText = {
                    if (tipSumText.isNotEmpty())
                        Text(
                            text = stringResource(
                                R.string.must_be_from_to,
                                MINIMUM_is_0,
                                MAXIMUM_TOTAL_SUM
                            )
                        )
                },
                isError = isTipSumError,
            )
            Spacer(modifier = Modifier.height(12.dp))

            CancelSaveRowView(
                onCancelClicked = {
                    onCancelButtonClicked()
                },
                onSaveClicked = {
                    isRestaurantNameError = restaurantNameText.isEmpty()
                    totalSumText.toFloatOrNull()?.let { totalSum ->
                        if (totalSum < MINIMUM_is_0 || totalSum > MAXIMUM_TOTAL_SUM)
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
                    tipSumText.toFloatOrNull()?.let { tipSum ->
                        if (tipSum < MINIMUM_is_0 || tipSum > MAXIMUM_TOTAL_SUM)
                            isTipSumError = true
                    }

                    if (isRestaurantNameError || isTotalSumError || isTaxError || isDiscountError || isTipError || isTipSumError)
                        return@CancelSaveRowView

                    val receiptData = ReceiptData(
                        id = receiptData.id,
                        restaurant = restaurantNameText.trim(),
                        translatedRestaurant =
                            if (translatedRestaurantNameText.isEmpty()) null else translatedRestaurantNameText.trim(),
                        date = dateText(),
                        total = totalSumText.trim().toFloat(),
                        tax = if (taxText.isEmpty()) null else taxText.trim().toFloat(),
                        discount = if (discountText.isEmpty()) null else discountText.trim()
                            .toFloat(),
                        tip = if (tipText.isEmpty()) null else tipText.trim().toFloat(),
                        tipSum = if (tipSumText.isEmpty()) null else tipSumText.trim().toFloat(),
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

private const val MAXIMUM_LINES = 5
private const val MAXIMUM_LENGTH = 100
private const val MINIMUM_is_0 = 0
private const val EMPTY_STRING = ""
private const val MAXIMUM_TOTAL_SUM = 99999999
private const val MAXIMUM_PERCENT = 100

@Composable
@Preview(showBackground = true)
private fun EditReceiptDialogViewPreview() {
    EditReceiptDialogSurface(
        receiptData = {
            ReceiptData(
                id = 5,
                restaurant = "good food soup with tomato",
                date = "september 2025",
                total = 48393.5f,
                discount = 10.4f,
                tip = 20f,
            )
        }
    )
}