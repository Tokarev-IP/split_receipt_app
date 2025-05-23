package com.example.receipt_splitter.receipt.presentation.views.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.receipt_splitter.R
import com.example.receipt_splitter.receipt.presentation.views.basic.CancelSaveButtonView

@Composable
internal fun ChooseLanguageDialog(
    onDismissRequest: () -> Unit,
    onLanguageSelected: (String) -> Unit,
    selectedLanguage: String?,
) {
    Dialog(
        onDismissRequest = { onDismissRequest() }
    ) {
        ChooseLanguageDialogView(
            onLanguageSelected = { language ->
                onLanguageSelected(language)
            },
            onDismissRequest = { onDismissRequest() },
            selectedLanguage = selectedLanguage,
        )
    }
}

@Composable
private fun ChooseLanguageDialogView(
    modifier: Modifier = Modifier,
    onLanguageSelected: (language: String) -> Unit = {},
    onDismissRequest: () -> Unit = {},
    selectedLanguage: String? = null,
) {
    val radioOptions = listOf(
        stringResource(id = R.string.english),
        stringResource(id = R.string.russian),
        stringResource(id = R.string.french),
        stringResource(id = R.string.portuguese),
        stringResource(id = R.string.italian),
        stringResource(id = R.string.spanish),
        stringResource(id = R.string.polish),
        stringResource(id = R.string.german),
        stringResource(id = R.string.dutch),
        stringResource(id = R.string.greek),
        stringResource(id = R.string.arabic),
        stringResource(id = R.string.turkish),
        stringResource(id = R.string.korean),
        stringResource(id = R.string.chinese),
        stringResource(id = R.string.japanese),
        stringResource(id = R.string.thai),
        stringResource(id = R.string.indonesian),
        stringResource(id = R.string.vietnamese),
    )
    var selectedOption by remember { mutableStateOf(selectedLanguage ?: radioOptions.first()) }

    Surface(
        shape = RoundedCornerShape(16.dp),
    ) {
        LazyColumn(
            modifier = modifier.padding(horizontal = 12.dp)
        ) {
            item {
                Spacer(modifier = modifier.padding(4.dp))
                Row(
                    modifier = modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        fontWeight = FontWeight.Normal,
                        fontSize = 20.sp,
                        text = stringResource(R.string.choose_language_title),
                        overflow = TextOverflow.Ellipsis,
                    )
                    IconButton(
                        onClick = { onDismissRequest() }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = stringResource(R.string.close_the_dialog),
                        )
                    }
                }
                Spacer(modifier = modifier.padding(4.dp))
                RadioButtonView(
                    selectedOption = { selectedOption },
                    onLanguageSelected = { option ->
                        selectedOption = option
                    },
                    radioOptions = radioOptions,
                )
                Spacer(modifier = modifier.padding(4.dp))
                CancelSaveButtonView(
                    onCancelClicked = { onDismissRequest() },
                    onSaveClicked = {
                        onLanguageSelected(selectedOption)
                    },
                )
                Spacer(modifier = modifier.padding(4.dp))
            }
        }
    }
}

@Composable
private fun RadioButtonView(
    modifier: Modifier = Modifier,
    onLanguageSelected: (language: String) -> Unit,
    selectedOption: () -> String?,
    radioOptions: List<String>,
) {
    Column(
        modifier
            .padding(20.dp)
            .selectableGroup()
    ) {
        radioOptions.forEach { language ->
            Spacer(modifier = modifier.height(4.dp))
            Row(
                modifier
                    .clip(RoundedCornerShape(16.dp))
                    .fillMaxWidth()
                    .selectable(
                        selected = (language == selectedOption()),
                        onClick = {
                            onLanguageSelected(language)
                        },
                        role = Role.RadioButton
                    )
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(text = language)
                RadioButton(
                    selected = (language == selectedOption()),
                    onClick = null
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ChooseTranslatedLanguagePreview() {
    ChooseLanguageDialogView()
}