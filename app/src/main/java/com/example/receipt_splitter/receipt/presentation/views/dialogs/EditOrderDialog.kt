package com.example.receipt_splitter.receipt.presentation.views.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.outlined.Close
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.receipt_splitter.R
import com.example.receipt_splitter.receipt.presentation.OrderData
import com.example.receipt_splitter.receipt.presentation.views.basic.CancelSaveButtonView

@Composable
internal fun AddNewOrderDialog(
    receiptId: Long,
    onCancelButtonClicked: () -> Unit = {},
    onSaveButtonClicked: (orderData: OrderData) -> Unit = {},
) {
    Dialog(
        onDismissRequest = { onCancelButtonClicked() }
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp)
        ) {
            EditOrderDialogView(
                orderData = OrderData(
                    id = 0,
                    name = EMPTY_STRING,
                    translatedName = EMPTY_STRING,
                    receiptId = receiptId,
                ),
                onCancelButtonClicked = {
                    onCancelButtonClicked()
                },
                onSaveButtonClicked = { orderData ->
                    onSaveButtonClicked(orderData)
                },
                titleText = stringResource(R.string.add_new_order_dialog_title)
            )
        }
    }
}

@Composable
internal fun EditOrderDialog(
    orderData: OrderData,
    onCancelButtonClicked: () -> Unit = {},
    onSaveButtonClicked: (orderData: OrderData) -> Unit = {},
) {
    Dialog(
        onDismissRequest = { onCancelButtonClicked() }
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp)
        ) {
            EditOrderDialogView(
                orderData = orderData,
                onCancelButtonClicked = {
                    onCancelButtonClicked()
                },
                onSaveButtonClicked = { orderData ->
                    onSaveButtonClicked(orderData)
                },
                titleText = stringResource(R.string.edit_order_dialog_title)
            )
        }
    }
}

@Composable
private fun EditOrderDialogView(
    modifier: Modifier = Modifier,
    orderData: OrderData,
    onCancelButtonClicked: () -> Unit = {},
    onSaveButtonClicked: (orderData: OrderData) -> Unit = {},
    titleText: String = EMPTY_STRING,
) {
    var nameText by rememberSaveable { mutableStateOf(orderData.name) }
    var translatedNameText by rememberSaveable {
        mutableStateOf(
            orderData.translatedName?.toString() ?: ""
        )
    }
    var quantityText by rememberSaveable {
        mutableStateOf(if (orderData.quantity == 0) EMPTY_STRING else orderData.quantity.toString())
    }
    var priceText by rememberSaveable {
        mutableStateOf(orderData.price.toString())
    }

    var isNameError by rememberSaveable { mutableStateOf(false) }
    var isQuantityError by rememberSaveable { mutableStateOf(false) }
    var isPriceError by rememberSaveable { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = modifier.fillMaxWidth()
            ) {
                Text(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Normal,
                    text = titleText,
                )
                IconButton(
                    onClick = { onCancelButtonClicked() },
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = stringResource(R.string.close_the_dialog),
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = nameText,
                onValueChange = { value ->
                    isNameError = false
                    if (value.length <= MAXIMUM_TEXT_LENGTH)
                        nameText = value
                },
                maxLines = MAXIMUM_LINES,
                label = { Text(text = stringResource(R.string.name)) },
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
                isError = isNameError,
                supportingText = {
                    if (isNameError && nameText.isEmpty()) {
                        Text(text = stringResource(R.string.is_empty))
                    } else
                        Text(
                            text = stringResource(
                                R.string.maximum_letters,
                                nameText.length,
                                MAXIMUM_TEXT_LENGTH
                            )
                        )
                },
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = translatedNameText,
                onValueChange = { value ->
                    if (value.length <= MAXIMUM_TEXT_LENGTH)
                        translatedNameText = value
                },
                maxLines = MAXIMUM_LINES,
                label = { Text(text = stringResource(R.string.translated_name)) },
                trailingIcon = {
                    if (translatedNameText.isNotEmpty())
                        IconButton(onClick = { translatedNameText = EMPTY_STRING }) {
                            Icon(Icons.Filled.Clear, stringResource(R.string.clear_text_button))
                        }
                },
                supportingText = {
                    if (translatedNameText.isNotEmpty())
                        Text(
                            text = stringResource(
                                R.string.maximum_letters,
                                translatedNameText.length,
                                MAXIMUM_TEXT_LENGTH
                            )
                        )
                }
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = quantityText,
                onValueChange = { value ->
                    isQuantityError = false
                    if (value.length <= MAXIMUM_QUANTITY_LENGTH)
                        quantityText = value.trim()
                },
                singleLine = true,
                label = { Text(text = stringResource(R.string.quantity)) },
                trailingIcon = {
                    if (quantityText.isNotEmpty())
                        IconButton(
                            onClick = {
                                quantityText = EMPTY_STRING
                                isQuantityError = false
                            }
                        ) {
                            Icon(Icons.Filled.Clear, stringResource(R.string.clear_text_button))
                        }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = isQuantityError,
                supportingText = {
                    if (isQuantityError && quantityText.isEmpty()) {
                        Text(text = stringResource(R.string.is_empty))
                    } else
                        Text(
                            text = stringResource(
                                R.string.must_be_even_from_to,
                                MINIMUM_QUANTITY,
                                MAXIMUM_QUANTITY
                            )
                        )
                },
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = priceText,
                onValueChange = { value ->
                    isPriceError = false
                    if (value.length <= MAXIMUM_QUANTITY_LENGTH)
                        priceText = value.trim()
                },
                singleLine = true,
                label = { Text(text = stringResource(R.string.price)) },
                trailingIcon = {
                    if (priceText.isNotEmpty())
                        IconButton(
                            onClick = {
                                priceText = EMPTY_STRING
                                isPriceError = false
                            }
                        ) {
                            Icon(Icons.Filled.Clear, stringResource(R.string.clear_text_button))
                        }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                isError = isPriceError,
                supportingText = {
                    if (isPriceError && priceText.isEmpty()) {
                        Text(text = stringResource(R.string.is_empty))
                    } else
                        Text(
                            text = stringResource(
                                R.string.must_be_from_to,
                                MINIMUM_PRICE,
                                MAXIMUM_PRICE
                            )
                        )
                },
            )
            Spacer(modifier = Modifier.height(20.dp))

            CancelSaveButtonView(
                onCancelClicked = {
                    onCancelButtonClicked()
                },
                onSaveClicked = {
                    isNameError = nameText.isEmpty()
                    isQuantityError = quantityText.isEmpty()
                    isPriceError = priceText.isEmpty()
                    quantityText.toIntOrNull()?.let { quantity ->
                        if (quantity < MINIMUM_QUANTITY || quantity > MAXIMUM_QUANTITY)
                            isQuantityError = true
                    } ?: run { isQuantityError = true }
                    priceText.toFloatOrNull()?.let { price ->
                        if (price < MINIMUM_PRICE || price > MAXIMUM_PRICE)
                            isPriceError = true
                    } ?: run { isPriceError = true }
                    if (isNameError || isQuantityError || isPriceError)
                        return@CancelSaveButtonView
                    val orderData = orderData.copy(
                        name = nameText.trim(),
                        translatedName = if (translatedNameText.isEmpty()) null else translatedNameText.trim(),
                        quantity = quantityText.toIntOrNull() ?: orderData.quantity,
                        price = priceText.toFloatOrNull() ?: orderData.price,
                    )
                    onSaveButtonClicked(orderData)
                }
            )
            Spacer(modifier = modifier.height(12.dp))
        }
    }
}

private const val MAXIMUM_TEXT_LENGTH = 100
private const val MAXIMUM_QUANTITY_LENGTH = 10
private const val MAXIMUM_QUANTITY = 99
private const val MINIMUM_QUANTITY = 0
private const val MAXIMUM_PRICE = 999999
private const val MINIMUM_PRICE = 0
private const val EMPTY_STRING = ""
private const val MAXIMUM_LINES = 5

@Composable
@Preview(showBackground = true)
private fun EditOrderDialogViewPreview() {
    EditOrderDialogView(
        orderData = OrderData(
            id = 1,
            name = "sopu with tomato plants",
            selectedQuantity = 2,
            price = 200f,
            receiptId = 5,
        )
    )
}