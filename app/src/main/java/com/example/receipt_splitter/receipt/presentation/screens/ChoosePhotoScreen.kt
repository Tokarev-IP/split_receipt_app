package com.example.receipt_splitter.receipt.presentation.screens

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil3.compose.AsyncImage
import com.example.receipt_splitter.R
import com.example.receipt_splitter.main.basic.BasicCircularLoadingUi
import com.example.receipt_splitter.receipt.data.DataConstantsReceipt
import com.example.receipt_splitter.receipt.presentation.ReceiptUiErrorIntent
import com.example.receipt_splitter.receipt.presentation.ReceiptUiEvent
import com.example.receipt_splitter.receipt.presentation.ReceiptUiState
import com.example.receipt_splitter.receipt.presentation.ReceiptViewModel
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.RESULT_FORMAT_JPEG
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.SCANNER_MODE_BASE
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChoosePhotoScreen(
    modifier: Modifier = Modifier,
    receiptViewModel: ReceiptViewModel,
    currentActivity: Activity? = LocalActivity.current,
) {
    val uiState by receiptViewModel.getUiStateFlow().collectAsState()
    val uiErrorIntent by receiptViewModel.getUiErrorIntentFlow().collectAsState(null)

    var listOfImages: List<Uri> by rememberSaveable { mutableStateOf(emptyList()) }

    val choosePhotoLaunch = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = DataConstantsReceipt.MAX_AMOUNT_OF_IMAGES),
    ) { newListOfImages: List<Uri> ->
        listOfImages = if (newListOfImages.size > DataConstantsReceipt.MAX_AMOUNT_OF_IMAGES)
            newListOfImages.subList(0, DataConstantsReceipt.MAX_AMOUNT_OF_IMAGES + 1)
        else
            newListOfImages
    }

    val options = GmsDocumentScannerOptions.Builder()
        .setGalleryImportAllowed(false)
        .setPageLimit(DataConstantsReceipt.MAX_AMOUNT_OF_IMAGES)
        .setResultFormats(RESULT_FORMAT_JPEG)
        .setScannerMode(SCANNER_MODE_BASE)
        .build()

    val scanner = GmsDocumentScanning.getClient(options)
    val scannerLaunch = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            val result =
                GmsDocumentScanningResult.fromActivityResultIntent(result.data)
            result?.pages?.let { pages ->
                listOfImages = pages.map { it.imageUri }
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(title = { Text(text = stringResource(id = R.string.select_receipt_photo)) })
        }
    ) { innerPadding ->

        when (uiState) {
            is ReceiptUiState.Loading -> {
                BasicCircularLoadingUi(
                    modifier = modifier
                        .padding(innerPadding)
                )
            }

            is ReceiptUiState.Show -> {
                ChoosePhotoView(
                    modifier = modifier.padding(innerPadding),
                    listOfUri = { listOfImages },
                    onChoosePhotoClicked = {
                        choosePhotoLaunch.launch(
                            PickVisualMediaRequest(
                                mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly,
                                maxItems = DataConstantsReceipt.MAX_AMOUNT_OF_IMAGES,
                            )
                        )
                    },
                    onClearPhotoClicked = { listOfImages = emptyList() },
                    onGetReceiptFromImageClicked = {
                        receiptViewModel.setUiEvent(
                            ReceiptUiEvent.ConvertImagesToReceipt(listOfImages = listOfImages)
                        )
                    },
                    onMakePhotoClicked = {
                        currentActivity?.let { myActivity ->
                            scanner.getStartScanIntent(myActivity)
                                .addOnSuccessListener { intentSender ->
                                    scannerLaunch.launch(
                                        IntentSenderRequest.Builder(intentSender).build()
                                    )
                                }
                                .addOnFailureListener { }
                        }
                    }
                )
            }
        }

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
    listOfUri: () -> List<Uri>,
    onChoosePhotoClicked: () -> Unit = {},
    onClearPhotoClicked: () -> Unit = {},
    onGetReceiptFromImageClicked: () -> Unit = {},
    onMakePhotoClicked: () -> Unit = {},
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        if (listOfUri().isNotEmpty()) {
            if (listOfUri().size == 1) {
                SplitReceiptView(
                    onGetReceiptFromImageClicked = { onGetReceiptFromImageClicked() },
                    uri = { listOfUri()[0] },
                    onClearPhotoClicked = { onClearPhotoClicked() },
                )
            } else
                ImageCarouselView(
                    listOfUri = { listOfUri() },
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
private fun ImageCarouselView(
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
    Box(modifier = modifier.padding(horizontal = 20.dp)) {
        HorizontalMultiBrowseCarousel(
            modifier = modifier
                .fillMaxWidth()
                .height(height)
                .padding(top = 60.dp),
            state = rememberCarouselState(itemCount = { listOfUri().size }),
            preferredItemWidth = preferredItemWidth,
            itemSpacing = 16.dp,
        ) { index ->
            AsyncImage(
                model = listOfUri()[index],
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
private fun ChoosePhotoViewPreview() {
    ChoosePhotoView(
        listOfUri = { listOf("1234".toUri(), "1232345".toUri()) },
    )
}