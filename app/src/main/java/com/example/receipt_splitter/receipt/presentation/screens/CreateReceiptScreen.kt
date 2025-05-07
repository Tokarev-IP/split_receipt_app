package com.example.receipt_splitter.receipt.presentation.screens

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.net.Uri
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.receipt_splitter.R
import com.example.receipt_splitter.main.basic.BasicCircularLoadingUi
import com.example.receipt_splitter.receipt.data.DataConstantsReceipt
import com.example.receipt_splitter.receipt.presentation.ReceiptEvent
import com.example.receipt_splitter.receipt.presentation.ReceiptViewModel
import com.example.receipt_splitter.receipt.presentation.viewmodels.CreateReceiptEvent
import com.example.receipt_splitter.receipt.presentation.viewmodels.CreateReceiptIntent
import com.example.receipt_splitter.receipt.presentation.viewmodels.CreateReceiptUiState
import com.example.receipt_splitter.receipt.presentation.viewmodels.CreateReceiptViewModel
import com.example.receipt_splitter.receipt.presentation.views.screens.CreateReceiptScreenView
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.RESULT_FORMAT_JPEG
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.SCANNER_MODE_BASE
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateReceiptScreen(
    modifier: Modifier = Modifier,
    receiptViewModel: ReceiptViewModel,
    createReceiptViewModel: CreateReceiptViewModel,
    currentActivity: Activity? = LocalActivity.current,
) {
    val listOfImages by createReceiptViewModel.getReceiptImages().collectAsStateWithLifecycle()
    val uiState by createReceiptViewModel.getUiStateFlow().collectAsStateWithLifecycle()

    LaunchedEffect(key1 = Unit) {
        createReceiptViewModel.getIntentFlow().collect { createIntent ->
            createIntent?.let { intent ->
                createReceiptViewModel.clearIntentFlow()
                when (intent) {
                    is CreateReceiptIntent.GoToEditReceiptScreen -> {
                        receiptViewModel.setEvent(
                            ReceiptEvent.OpenEditReceiptsScreen(
                                intent.receiptId
                            )
                        )
                    }
                }
            }
        }
    }

    val choosePhotoLaunch = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = DataConstantsReceipt.MAX_AMOUNT_OF_IMAGES),
    ) { newListOfImages: List<Uri> ->
        createReceiptViewModel.setEvent(CreateReceiptEvent.PutImages(listOfImages = newListOfImages))
    }

    val scanningOptions = GmsDocumentScannerOptions.Builder()
        .setGalleryImportAllowed(false)
        .setPageLimit(DataConstantsReceipt.MAX_AMOUNT_OF_IMAGES)
        .setResultFormats(RESULT_FORMAT_JPEG)
        .setScannerMode(SCANNER_MODE_BASE)
        .build()

    val scanner = GmsDocumentScanning.getClient(scanningOptions)
    val scannerLaunch = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            val result =
                GmsDocumentScanningResult.fromActivityResultIntent(result.data)
            result?.pages?.let { pages ->
                val newListOfImages = pages.map { it.imageUri }
                createReceiptViewModel.setEvent(CreateReceiptEvent.PutImages(listOfImages = newListOfImages))
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.select_receipt_photo)) },
                navigationIcon = {
                    IconButton(
                        onClick = { receiptViewModel.setEvent(ReceiptEvent.GoBack) }
                    ) {
                        Icon(
                            Icons.AutoMirrored.Rounded.ArrowBack,
                            stringResource(R.string.go_back_button)
                        )
                    }
                },
            )
        }
    ) { innerPadding ->

        when (uiState) {
            is CreateReceiptUiState.Loading -> {
                BasicCircularLoadingUi(
                    modifier = modifier.padding(innerPadding)
                )
            }

            is CreateReceiptUiState.Show -> {
                CreateReceiptScreenView(
                    modifier = modifier.padding(innerPadding),
                    listOfUri = { listOfImages ?: emptyList() },
                    onChoosePhotoClicked = {
                        choosePhotoLaunch.launch(
                            PickVisualMediaRequest(
                                mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly,
                                maxItems = DataConstantsReceipt.MAX_AMOUNT_OF_IMAGES,
                            )
                        )
                    },
                    onClearPhotoClicked = {
                        createReceiptViewModel.setEvent(CreateReceiptEvent.PutImages(listOfImages = emptyList()))
                    },
                    onGetReceiptFromImageClicked = {
                        createReceiptViewModel.setEvent(CreateReceiptEvent.CreateReceipt)
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

//        when (uiErrorIntent) {
//            is ReceiptUiMessageIntent.ImageIsInappropriate -> {
//                Toast.makeText(
//                    LocalContext.current,
//                    stringResource(R.string.image_is_inappropriate),
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//
//            is ReceiptUiMessageIntent.ReceiptMessage -> {
//                val msg = (uiErrorIntent as ReceiptUiMessageIntent.ReceiptMessage).msg
//                Toast.makeText(LocalContext.current, msg, Toast.LENGTH_SHORT).show()
//            }
//        }
    }
}