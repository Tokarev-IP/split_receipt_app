package com.iliatokarev.receipt_splitter_app.receipt.presentation.views.sheets

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iliatokarev.receipt_splitter_app.R
import com.iliatokarev.receipt_splitter_app.main.basic.icons.PersonAddIcon
import com.iliatokarev.receipt_splitter_app.receipt.data.services.DataConstantsReceipt.CONSUMER_NAME_DIVIDER
import com.iliatokarev.receipt_splitter_app.receipt.data.services.DataConstantsReceipt.MAXIMUM_AMOUNT_OF_CONSUMER_NAMES
import com.iliatokarev.receipt_splitter_app.receipt.data.services.DataConstantsReceipt.MAXIMUM_CONSUMER_NAME_TEXT_LENGTH
import com.iliatokarev.receipt_splitter_app.receipt.data.services.DataConstantsReceipt.ORDER_CONSUMER_NAME_DIVIDER
import com.iliatokarev.receipt_splitter_app.receipt.presentation.views.basic.FlowGridLayout

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SelectInitialConsumerNamesBottomSheet(
    modifier: Modifier = Modifier,
    allConsumerNamesList: List<String>,
    onDismissClick: () -> Unit,
    onSetSelectedNamesClick: (List<String>) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    ModalBottomSheet(
        modifier = modifier.fillMaxWidth(),
        onDismissRequest = { onDismissClick() },
        sheetState = sheetState,
    ) {
        SelectInitialConsumerNamesBottomView(
            allConsumerNamesList = allConsumerNamesList,
            onSetSelectedNamesClick = { names ->
                onSetSelectedNamesClick(names)
            },
        )
    }
}

@Composable
private fun SelectInitialConsumerNamesBottomView(
    modifier: Modifier = Modifier,
    allConsumerNamesList: List<String> = emptyList<String>(),
    onSetSelectedNamesClick: (List<String>) -> Unit = {},
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        item {
            SelectInitialConsumerNamesView(
                allConsumerNamesList = allConsumerNamesList,
                onSetSelectedNamesClick = { names ->
                    onSetSelectedNamesClick(names)
                },
            )
        }
    }
}

@Composable
private fun SelectInitialConsumerNamesView(
    modifier: Modifier = Modifier,
    allConsumerNamesList: List<String>,
    onSetSelectedNamesClick: (List<String>) -> Unit,
) {
    val chosenConsumerNamesList = remember { mutableStateListOf<String>() }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stringResource(R.string.select_consumer_names),
            fontSize = 20.sp,
            fontWeight = FontWeight.Normal,
        )

        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(8.dp))

        if (allConsumerNamesList.size >= MAXIMUM_AMOUNT_OF_CONSUMER_NAMES) {
            Text(
                textAlign = TextAlign.Center,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                text = stringResource(R.string.maximum_amount_of_names_is_reached)
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        AnimatedVisibility(
            visible = allConsumerNamesList.isNotEmpty()
        ) {
            ConsumerNamesGrid(
                allConsumerNamesList = allConsumerNamesList,
                onChooseConsumerNameClick = { name ->
                    chosenConsumerNamesList.add(name)
                    onSetSelectedNamesClick(chosenConsumerNamesList)
                },
                onRemoveConsumerNameClick = { name ->
                    chosenConsumerNamesList.remove(name)
                },
                chosenConsumerNamesList = chosenConsumerNamesList,
            )
        }
        Spacer(modifier = Modifier.height(12.dp))

        ConsumerNameTextFieldView(
            allConsumerNamesList = allConsumerNamesList,
            onAddNewConsumerNameClick = { name ->
                chosenConsumerNamesList.add(name)
                onSetSelectedNamesClick(chosenConsumerNamesList)
            }
        )
        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
private fun ConsumerNameTextFieldView(
    onAddNewConsumerNameClick: (String) -> Unit,
    allConsumerNamesList: List<String>,
) {
    var consumerNameText by rememberSaveable { mutableStateOf("") }
    var isConsumerNameErrorState by rememberSaveable { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        value = consumerNameText,
        onValueChange = { name ->
            isConsumerNameErrorState = false
            if (name.length <= MAXIMUM_CONSUMER_NAME_TEXT_LENGTH)
                consumerNameText = name
        },
        isError = isConsumerNameErrorState,
        label = { Text(text = stringResource(R.string.name)) },
        trailingIcon = {
            if (consumerNameText.isNotEmpty())
                IconButton(
                    onClick = {
                        consumerNameText = EMPTY_STRING
                        isConsumerNameErrorState = false
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
                addConsumerName(
                    consumerNameText = consumerNameText,
                    allConsumerNamesList = allConsumerNamesList,
                    onAddNewConsumerNameClick = { name ->
                        onAddNewConsumerNameClick(name)
                        consumerNameText = EMPTY_STRING
                    },
                    onConsumerNameErrorState = { state ->
                        isConsumerNameErrorState = state
                    }
                )
                focusManager.clearFocus()
            }
        ),
        leadingIcon = {
            IconButton(
                onClick = {
                    addConsumerName(
                        consumerNameText = consumerNameText,
                        allConsumerNamesList = allConsumerNamesList,
                        onAddNewConsumerNameClick = { name ->
                            onAddNewConsumerNameClick(name)
                            consumerNameText = EMPTY_STRING
                        },
                        onConsumerNameErrorState = { state ->
                            isConsumerNameErrorState = state
                        }
                    )
                    focusManager.clearFocus()
                },
                enabled = allConsumerNamesList.size < MAXIMUM_AMOUNT_OF_CONSUMER_NAMES,
            ) {
                Icon(Icons.Filled.PersonAddIcon, stringResource(R.string.add_name_button))
            }
        },
        enabled = allConsumerNamesList.size < MAXIMUM_AMOUNT_OF_CONSUMER_NAMES,
        singleLine = true,
    )
}

private fun addConsumerName(
    consumerNameText: String,
    allConsumerNamesList: List<String>,
    onAddNewConsumerNameClick: (String) -> Unit,
    onConsumerNameErrorState: (Boolean) -> Unit,
) {
    if (consumerNameText.trim()
            .isEmpty() || consumerNameText.length > MAXIMUM_CONSUMER_NAME_TEXT_LENGTH
    ) {
        onConsumerNameErrorState(true)
        return
    }
    if (CONSUMER_NAME_DIVIDER in consumerNameText.trim() ||
        ORDER_CONSUMER_NAME_DIVIDER in consumerNameText.trim()
    ) {
        onConsumerNameErrorState(true)
        return
    }

    if (consumerNameText.trim() in allConsumerNamesList) {
        onConsumerNameErrorState(true)
        return
    }

    onAddNewConsumerNameClick(consumerNameText.trim())
}

@Composable
private fun ConsumerNamesGrid(
    modifier: Modifier = Modifier,
    allConsumerNamesList: List<String>,
    chosenConsumerNamesList: List<String>,
    onChooseConsumerNameClick: (String) -> Unit,
    onRemoveConsumerNameClick: (String) -> Unit,
) {
    FlowGridLayout(
        horizontalSpacing = 8.dp,
        verticalSpacing = 4.dp,
    ) {
        repeat(allConsumerNamesList.size) { index ->
            val consumerName = allConsumerNamesList[index]
            OutlinedCard(
                onClick = { onRemoveConsumerNameClick(consumerName) },
                enabled = consumerName in chosenConsumerNamesList
            ) {
                Box(
                    modifier = modifier
                        .then(
                            if (consumerName !in chosenConsumerNamesList)
                                Modifier.clickable { onChooseConsumerNameClick(consumerName) }
                            else
                                Modifier
                        )
                ) {
                    Text(
                        modifier = modifier
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        text = consumerName,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Normal,
                        maxLines = MAXIMUM_LINE_IS_1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

private const val EMPTY_STRING = ""
private const val MAXIMUM_LINE_IS_1 = 1

@Preview(showBackground = true)
@Composable
private fun SetInitialConsumerNameBottomSheetViewPreview() {
    SelectInitialConsumerNamesBottomView(
        allConsumerNamesList = listOf(
            "consumer1",
            "consumer2",
            "consumer number 3",
            "consumer4",
            "consumer5",
            "consumer6",
            "consumer7",
            "consumer8",
            "consumer1",
            "consumer2",
            "consumer number 3",
            "consumer4",
            "consumer5",
            "consumer6",
            "consumer7",
            "consumer8",
            "consumer1",
        )
    )
}