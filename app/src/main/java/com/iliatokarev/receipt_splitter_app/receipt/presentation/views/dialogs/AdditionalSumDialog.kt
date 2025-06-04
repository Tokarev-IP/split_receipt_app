package com.iliatokarev.receipt_splitter_app.receipt.presentation.views.dialogs

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.iliatokarev.receipt_splitter_app.R
import com.iliatokarev.receipt_splitter_app.main.basic.icons.Minus
import com.iliatokarev.receipt_splitter_app.main.basic.icons.Plus
import com.iliatokarev.receipt_splitter_app.receipt.presentation.views.basic.DialogCap

@Composable
internal fun AdditionalSumDialog(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    onAddItemClicked: (Pair<String, Float>) -> Unit,
    onRemoveItemClicked: (Pair<String, Float>) -> Unit,
    additionalSumList: () -> List<Pair<String, Float>>,
) {
    Dialog(
        onDismissRequest = { onDismissRequest() }
    ) {
        Surface(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
        ) {
            AdditionalSumDialogView(
                onDismissRequest = { onDismissRequest() },
                onAddClicked = { pair ->
                    onAddItemClicked(pair)
                },
                additionalSumList = { additionalSumList() },
                onRemoveClicked = { pair ->
                    onRemoveItemClicked(pair)
                }
            )
        }
    }
}

@Composable
private fun AdditionalSumDialogView(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit = {},
    onAddClicked: (Pair<String, Float>) -> Unit = {},
    onRemoveClicked: (Pair<String, Float>) -> Unit = {},
    additionalSumList: () -> List<Pair<String, Float>> = { emptyList() },
) {
    LazyColumn(
        modifier = modifier
            .padding(horizontal = 12.dp)
            .fillMaxWidth()
            .animateContentSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        item {
            AddAdditionalSumView(
                onDismissRequest = onDismissRequest,
                onAddClicked = { pair ->
                    onAddClicked(pair)
                }
            )
            AdditionalSumView(
                additionalSumList = { additionalSumList() },
                onRemoveItemClicked = { pair ->
                    onRemoveClicked(pair)
                }
            )
        }
    }
}

@Composable
private fun AddAdditionalSumView(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit = {},
    onAddClicked: (Pair<String, Float>) -> Unit = {},
) {
    var nameText by rememberSaveable { mutableStateOf("") }
    var sumText by rememberSaveable { mutableStateOf("") }

    var isNameTextError by rememberSaveable { mutableStateOf(false) }
    var isSumTextError by rememberSaveable { mutableStateOf(false) }

    var useMinus by rememberSaveable { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Spacer(modifier = modifier.height(12.dp))
        DialogCap(text = stringResource(R.string.additional_sum_title)) { onDismissRequest() }
        Spacer(modifier = modifier.height(12.dp))

        OutlinedTextField(
            value = nameText,
            onValueChange = { value ->
                isNameTextError = false
                if (value.length <= MAXIMUM_LENGTH)
                    nameText = value
            },
            label = { Text(text = stringResource(R.string.name)) },
            trailingIcon = {
                if (nameText.isNotEmpty())
                    IconButton(
                        onClick = {
                            nameText = EMPTY_STRING
                            isNameTextError = false
                        }
                    ) {
                        Icon(Icons.Filled.Clear, stringResource(R.string.clear_text_button))
                    }
            },
            maxLines = MAXIMUM_LINES,
            supportingText = {
                if (isNameTextError && nameText.isEmpty())
                    Text(text = stringResource(R.string.field_is_empty))
                else
                    Text(
                        text = stringResource(
                            R.string.maximum_letters,
                            nameText.length,
                            MAXIMUM_LENGTH
                        )
                    )
            },
            isError = isNameTextError,
        )
        Spacer(modifier = modifier.height(8.dp))

        OutlinedTextField(
            value = sumText,
            onValueChange = { value ->
                isSumTextError = false
                if (value.length <= MAXIMUM_LENGTH)
                    sumText = value.trim()
            },
            singleLine = true,
            label = { Text(text = stringResource(R.string.sum)) },
            trailingIcon = {
                if (sumText.isNotEmpty())
                    IconButton(
                        onClick = {
                            sumText = EMPTY_STRING
                            isSumTextError = false
                        }
                    ) {
                        Icon(Icons.Filled.Clear, stringResource(R.string.clear_text_button))
                    }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            supportingText = {
                if (isSumTextError && sumText.isEmpty())
                    Text(text = stringResource(R.string.field_is_empty))
                else
                    Text(
                        text = stringResource(
                            R.string.must_be_from_to,
                            MINIMUM_TOTAL_SUM,
                            MAXIMUM_TOTAL_SUM
                        )
                    )
            },
            isError = isSumTextError,
            leadingIcon = {
                AnimatedContent(
                    targetState = useMinus,
                ) { minus ->
                    IconButton(
                        onClick = { useMinus = !useMinus }
                    ) {
                        if (minus)
                            Icon(
                                Icons.Filled.Minus,
                                stringResource(R.string.minus_button)
                            )
                        else
                            Icon(
                                Icons.Filled.Plus,
                                stringResource(R.string.plus_button)
                            )
                    }
                }
            }
        )
        Spacer(modifier = modifier.height(12.dp))

        OutlinedButton(
            onClick = {
                isNameTextError = nameText.isEmpty()
                isSumTextError = sumText.isEmpty()

                sumText.toFloatOrNull()?.let { sum ->
                    if (sum > MAXIMUM_TOTAL_SUM)
                        isSumTextError = true
                } ?: { isSumTextError = true }

                if (isNameTextError == false && isSumTextError == false) {
                    var sum = sumText.toFloatOrNull() ?: ZERO_F
                    if (useMinus)
                        sum *= -1
                    onAddClicked(Pair(nameText.trim(), sum))
                }
            }
        ) {
            Text(text = stringResource(R.string.add_additional_sum))
        }
        Spacer(modifier = modifier.height(12.dp))
    }
}

@Composable
private fun AdditionalSumView(
    additionalSumList: () -> List<Pair<String, Float>>,
    onRemoveItemClicked: (Pair<String, Float>) -> Unit,
) {
    val additionalList = additionalSumList()
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (additionalList.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))

            for (additionalSum in additionalList) {
                AdditionalSumItemView(
                    additionalSum = additionalSum,
                    onRemoveItemClicked = { pair ->
                        onRemoveItemClicked(pair)
                    }
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun AdditionalSumItemView(
    modifier: Modifier = Modifier,
    additionalSum: Pair<String, Float>,
    onRemoveItemClicked: (Pair<String, Float>) -> Unit,
) {
    OutlinedCard {
        Row(
            modifier = modifier
                .padding(horizontal = 8.dp, vertical = 4.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = additionalSum.first,
                modifier = modifier
                    .padding(end = 8.dp)
                    .weight(10f),
                fontWeight = FontWeight.Normal,
            )
            Text(
                modifier = modifier.weight(4f),
                text = additionalSum.second.toString(),
                fontWeight = FontWeight.Normal,
            )
            IconButton(
                modifier = modifier.weight(2f),
                onClick = { onRemoveItemClicked(additionalSum) },
            ) {
                Icon(Icons.Filled.Clear, stringResource(R.string.clear_additional_sum_button))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AdditionalSumDialogViewPreview() {
    AdditionalSumDialogView(
        additionalSumList = {
            listOf(
                Pair("Name 1 dkgdfgj 8f9 jfdgj 98 fjgjd 89d jnfgjnd gnd nfg ggftr", 187776.0f),
                Pair("Name 2", 2.0f),
                Pair("Name 3", 3.0f),
                Pair("Name 4", 4.0f),
                Pair("Name 5", 5.0f),
            )
        }
    )
}

private const val MAXIMUM_LINES = 5
private const val MAXIMUM_LENGTH = 100
private const val MINIMUM_TOTAL_SUM = -99_999_999
private const val EMPTY_STRING = ""
private const val MAXIMUM_TOTAL_SUM = 99_999_999
private const val ZERO_F = 0.0F