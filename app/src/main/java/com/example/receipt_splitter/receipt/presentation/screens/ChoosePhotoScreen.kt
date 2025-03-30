package com.example.receipt_splitter.receipt.presentation.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil3.compose.AsyncImage
import com.example.receipt_splitter.R
import com.example.receipt_splitter.receipt.presentation.ReceiptUiErrorIntent
import com.example.receipt_splitter.receipt.presentation.ReceiptUiEvent
import com.example.receipt_splitter.receipt.presentation.ReceiptUiState
import com.example.receipt_splitter.receipt.presentation.ReceiptViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChoosePhotoScreen(
    modifier: Modifier = Modifier,
    receiptViewModel: ReceiptViewModel,
) {
    val uiState by receiptViewModel.getUiStateFlow().collectAsState()
    val uiErrorIntent by receiptViewModel.getUiErrorIntentFlow().collectAsState(null)

    var receiptPhotoUri: Uri? by rememberSaveable { mutableStateOf(null) }

    val choosePhotoLaunch = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
    ) { newUri: Uri? ->
        receiptPhotoUri = newUri
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {}
    ) { innerPadding ->

        when (uiState) {
            ReceiptUiState.Loading -> {
                LinearProgressIndicator(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(innerPadding)
                )
            }
        }

        ChoosePhotoView(
            modifier = modifier.padding(innerPadding),
            uri = { receiptPhotoUri },
            onChoosePhotoClicked = {
                choosePhotoLaunch.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            },
            onClearPhotoClicked = { receiptPhotoUri = null },
            onGetReceiptFromImageClicked = {
                receiptPhotoUri?.let { photoUri ->
                    receiptViewModel.setUiEvent(ReceiptUiEvent.ConvertReceiptFromImage(photoUri))
                }
            }
        )

        when (uiErrorIntent) {
            is ReceiptUiErrorIntent.ImageIsInappropriate -> {
                Toast.makeText(
                    LocalContext.current,
                    stringResource(R.string.image_is_inappropriate),
                    Toast.LENGTH_SHORT
                ).show()
            }

            is ReceiptUiErrorIntent.ReceiptError -> {
                val msg = (uiErrorIntent as ReceiptUiErrorIntent.ReceiptError).msg
                Toast.makeText(LocalContext.current, msg, Toast.LENGTH_SHORT).show()
            }
        }
    }
}

@Composable
private fun ChoosePhotoView(
    modifier: Modifier = Modifier,
    uri: () -> Uri?,
    onChoosePhotoClicked: () -> Unit,
    onClearPhotoClicked: () -> Unit,
    onGetReceiptFromImageClicked: () -> Unit,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        uri()?.let { photoUri ->
            SplitReceiptView(
                uri = { photoUri },
                onClearPhotoClicked = { onClearPhotoClicked() },
                onGetReceiptFromImageClicked = { onGetReceiptFromImageClicked() }
            )
        } ?: run {
            ChoosePhotoBoxView(
                onChoosePhotoClicked = { onChoosePhotoClicked() }
            )
        }
    }
}

@Composable
private fun SplitReceiptView(
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
    }
}

@Composable
@Preview(showBackground = true)
private fun ChoosePhotoViewPreview() {
    ChoosePhotoView(
        uri = { "1234".toUri() },
        onChoosePhotoClicked = {},
        onClearPhotoClicked = {},
        onGetReceiptFromImageClicked = {}
    )
}