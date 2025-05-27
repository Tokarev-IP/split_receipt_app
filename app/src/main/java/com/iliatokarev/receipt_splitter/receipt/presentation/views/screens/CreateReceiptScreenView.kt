package com.iliatokarev.receipt_splitter.receipt.presentation.views.screens

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import coil3.compose.AsyncImage
import com.iliatokarev.receipt_splitter.R
import com.iliatokarev.receipt_splitter.receipt.data.services.DataConstantsReceipt
import com.iliatokarev.receipt_splitter.receipt.presentation.ReceiptUIConstants

@Composable
internal fun CreateReceiptScreenView(
    modifier: Modifier = Modifier,
    listOfUri: List<Uri>,
    onChoosePhotoClicked: () -> Unit = {},
    onClearPhotoClicked: () -> Unit = {},
    onGetReceiptFromImageClicked: () -> Unit = {},
    onMakePhotoClicked: () -> Unit = {},
    onSwitchCheckedChange: (Boolean) -> Unit = {},
    languageSwitchState: Boolean = false,
    translatedLanguage: String? = null,
    onShowLanguageDialog: () -> Unit = {},
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CreateReceiptView(
            listOfUri = listOfUri,
            onChoosePhotoClicked = { onChoosePhotoClicked() },
            onClearPhotoClicked = { onClearPhotoClicked() },
            onGetReceiptFromImageClicked = { onGetReceiptFromImageClicked() },
            onMakePhotoClicked = { onMakePhotoClicked() },
            onSwitchCheckedChange = { value ->
                onSwitchCheckedChange(value)
            },
            languageSwitchState = languageSwitchState,
            translatedLanguage = translatedLanguage,
            onShowLanguageDialog = { onShowLanguageDialog() },
        )
    }
}

@Composable
private fun CreateReceiptView(
    modifier: Modifier = Modifier,
    listOfUri: List<Uri>,
    onChoosePhotoClicked: () -> Unit,
    onClearPhotoClicked: () -> Unit,
    onGetReceiptFromImageClicked: () -> Unit,
    onMakePhotoClicked: () -> Unit,
    onSwitchCheckedChange: (Boolean) -> Unit,
    languageSwitchState: Boolean,
    translatedLanguage: String?,
    onShowLanguageDialog: () -> Unit,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        item {
            if (listOfUri.isEmpty()) {
                ChoosePhotoBoxView(
                    onChoosePhotoClicked = { onChoosePhotoClicked() },
                    onMakePhotoClicked = { onMakePhotoClicked() },
                )
            } else {
                ImagesAreSelectedView(
                    listOfUri = listOfUri,
                    onClearPhotoClicked = { onClearPhotoClicked() },
                    onGetReceiptFromImageClicked = { onGetReceiptFromImageClicked() },
                    onSwitchCheckedChange = { value ->
                        onSwitchCheckedChange(value)
                    },
                    languageSwitchState = languageSwitchState,
                    translatedLanguage = translatedLanguage,
                    onShowLanguageDialog = { onShowLanguageDialog() },
                )
            }
        }
    }
}

@Composable
private fun ImagesAreSelectedView(
    modifier: Modifier = Modifier,
    listOfUri: List<Uri>,
    onClearPhotoClicked: () -> Unit,
    onGetReceiptFromImageClicked: () -> Unit,
    onSwitchCheckedChange: (Boolean) -> Unit,
    languageSwitchState: Boolean,
    translatedLanguage: String?,
    onShowLanguageDialog: () -> Unit,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        if (listOfUri.size == ReceiptUIConstants.ONE_ELEMENT) {
            PhotoBoxView(
                uri = listOfUri.firstOrNull(),
                onClearPhotoClicked = { onClearPhotoClicked() }
            )
        } else
            ImageCarouselBox(
                listOfUri = listOfUri,
                onClearPhotoClicked = { onClearPhotoClicked() },
            )

        Spacer(modifier = Modifier.height(36.dp))

        ChooseTranslatedLanguageView(
            translatedLanguage = translatedLanguage,
            switchState = languageSwitchState,
            onSwitchCheckedChange = { value ->
                onSwitchCheckedChange(value)
            },
            onShowLanguageDialog = { onShowLanguageDialog() },
        )
        Spacer(modifier = Modifier.height(36.dp))
        Text(
            fontWeight = FontWeight.Light,
            fontSize = 12.sp,
            text = stringResource(id = R.string.there_is_limited_attempts)
        )

        Spacer(modifier = Modifier.height(12.dp))
        OutlinedButton(
            onClick = { onGetReceiptFromImageClicked() }
        ) {
            Text(text = stringResource(id = R.string.split_the_receipt))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ImageCarouselBox(
    modifier: Modifier = Modifier,
    listOfUri: List<Uri>,
    onClearPhotoClicked: () -> Unit = {},
    height: Dp = 320.dp,
    preferredItemWidth: Dp = 240.dp,
) {
    Box(modifier = modifier.padding(horizontal = 20.dp)) {
        HorizontalMultiBrowseCarousel(
            modifier = modifier
                .fillMaxWidth()
                .height(height)
                .padding(top = 60.dp),
            state = rememberCarouselState(itemCount = { listOfUri.size }),
            preferredItemWidth = preferredItemWidth,
            itemSpacing = 16.dp,
        ) { index ->
            AsyncImage(
                model = listOfUri[index],
                contentDescription = stringResource(R.string.receipt_photo),
                contentScale = ContentScale.Crop,
            )
        }
        IconButton(
            modifier = modifier.align(Alignment.TopEnd),
            onClick = { onClearPhotoClicked() }
        ) {
            Icon(
                Icons.Filled.Clear,
                contentDescription = stringResource(id = R.string.clear_receipt_photo)
            )
        }
    }
}

@Composable
private fun PhotoBoxView(
    modifier: Modifier = Modifier,
    uri: Uri?,
    onClearPhotoClicked: () -> Unit,
) {
    Box(
        modifier = modifier
            .height(320.dp)
            .width(320.dp)
    ) {
        AsyncImage(
            modifier = modifier
                .fillMaxSize()
                .padding(top = 60.dp)
                .align(Alignment.Center)
                .clip(RoundedCornerShape(20.dp)),
            model = uri,
            contentDescription = stringResource(R.string.receipt_photo),
            contentScale = ContentScale.Crop,
        )

        IconButton(
            modifier = modifier.align(Alignment.TopEnd),
            onClick = { onClearPhotoClicked() }
        ) {
            Icon(
                Icons.Filled.Clear,
                contentDescription = stringResource(id = R.string.clear_receipt_photo)
            )
        }
    }
}

@Composable
private fun ChoosePhotoBoxView(
    modifier: Modifier = Modifier,
    onChoosePhotoClicked: () -> Unit,
    onMakePhotoClicked: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(
                id = R.string.receipt_photo_is_empty,
                DataConstantsReceipt.MAXIMUM_AMOUNT_OF_IMAGES
            )
        )
        Spacer(modifier = modifier.height(40.dp))
        OutlinedButton(
            onClick = { onChoosePhotoClicked() }
        ) {
            Text(text = stringResource(id = R.string.select_receipt_photo))
        }
        Spacer(modifier = modifier.height(20.dp))
        OutlinedButton(
            onClick = { onMakePhotoClicked() }
        ) {
            Text(text = stringResource(id = R.string.make_photo))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChooseTranslatedLanguageView(
    modifier: Modifier = Modifier,
    translatedLanguage: String?,
    switchState: Boolean,
    onSwitchCheckedChange: (Boolean) -> Unit,
    onShowLanguageDialog: () -> Unit,
) {
    OutlinedCard(
        modifier = modifier
            .padding(horizontal = 36.dp),
        onClick = {
            if (switchState)
                onShowLanguageDialog()
        },
        enabled = switchState,
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = stringResource(id = R.string.translate))
            Text(text = translatedLanguage ?: "")
            Switch(
                checked = switchState,
                onCheckedChange = { value ->
                    onSwitchCheckedChange(value)
                }
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun ChoosePhotoScreenViewPreview() {
    CreateReceiptScreenView(
        listOfUri = listOf("1234".toUri(), "1232345".toUri()),
    )
}