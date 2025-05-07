package com.example.receipt_splitter.receipt.presentation.views.screens

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil3.compose.AsyncImage
import com.example.receipt_splitter.R

@Composable
internal fun CreateReceiptScreenView(
    modifier: Modifier = Modifier,
    listOfUri: () -> List<Uri>,
    onChoosePhotoClicked: () -> Unit = {},
    onClearPhotoClicked: () -> Unit = {},
    onGetReceiptFromImageClicked: () -> Unit = {},
    onMakePhotoClicked: () -> Unit = {},
){
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ){
        CreateReceiptView(
            listOfUri = { listOfUri() },
            onChoosePhotoClicked = { onChoosePhotoClicked() },
            onClearPhotoClicked = { onClearPhotoClicked() },
            onGetReceiptFromImageClicked = { onGetReceiptFromImageClicked() },
            onMakePhotoClicked = { onMakePhotoClicked() },
        )
    }
}

@Composable
private fun CreateReceiptView(
    modifier: Modifier = Modifier,
    listOfUri: () -> List<Uri>,
    onChoosePhotoClicked: () -> Unit ,
    onClearPhotoClicked: () -> Unit,
    onGetReceiptFromImageClicked: () -> Unit,
    onMakePhotoClicked: () -> Unit,
) {
    val listOfUri = listOfUri()

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        if (listOfUri.isNotEmpty()) {
            if (listOfUri.size == 1) {
                ImageReceiptView(
                    onGetReceiptFromImageClicked = { onGetReceiptFromImageClicked() },
                    uri = { listOfUri[0] },
                    onClearPhotoClicked = { onClearPhotoClicked() },
                )
            } else
                ImageCarouselReceiptView(
                    listOfUri = { listOfUri },
                    onClearPhotoClicked = { onClearPhotoClicked() },
                    onGetReceiptFromImageClicked = { onGetReceiptFromImageClicked() },
                )
        } else {
            ChoosePhotoBoxView(
                onChoosePhotoClicked = { onChoosePhotoClicked() },
                onMakePhotoClicked = { onMakePhotoClicked() },
            )
        }
    }
}

@Composable
private fun ImageReceiptView(
    modifier: Modifier = Modifier,
    onGetReceiptFromImageClicked: () -> Unit,
    uri: () -> Uri,
    onClearPhotoClicked: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        PhotoBoxView(
            uri = { uri() },
            onClearPhotoClicked = { onClearPhotoClicked() }
        )
        Spacer(modifier = modifier.height(40.dp))
        OutlinedButton(
            onClick = { onGetReceiptFromImageClicked() }
        ) {
            Text(text = stringResource(id = R.string.split_the_receipt))
        }
    }
}

@Composable
private fun ImageCarouselReceiptView(
    modifier: Modifier = Modifier,
    listOfUri: () -> List<Uri>,
    onClearPhotoClicked: () -> Unit = {},
    onGetReceiptFromImageClicked: () -> Unit = {},
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        ImageCarouselBox(
            listOfUri = { listOfUri() },
            onClearPhotoClicked = { onClearPhotoClicked() },
        )
        Spacer(modifier = modifier.height(40.dp))
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
    listOfUri: () -> List<Uri>,
    onClearPhotoClicked: () -> Unit = {},
    height: Dp = 320.dp,
    preferredItemWidth: Dp = 240.dp,
) {
    val listOfUri = listOfUri()

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
    uri: () -> Uri,
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
                .padding(top = 40.dp)
                .align(Alignment.Center)
                .clip(RoundedCornerShape(40.dp)),
            model = uri(),
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
        Text(text = stringResource(id = R.string.receipt_photo_is_empty))
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

@Composable
@Preview(showBackground = true)
private fun ChoosePhotoScreenViewPreview() {
    CreateReceiptScreenView(
        listOfUri = { listOf("1234".toUri(), "1232345".toUri()) },
    )
}