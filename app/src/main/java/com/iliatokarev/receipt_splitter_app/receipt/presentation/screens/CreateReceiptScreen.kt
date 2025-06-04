package com.iliatokarev.receipt_splitter_app.receipt.presentation.screens

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.iliatokarev.receipt_splitter_app.R
import com.iliatokarev.receipt_splitter_app.main.basic.BasicCircularLoadingUi
import com.iliatokarev.receipt_splitter_app.receipt.data.services.DataConstantsReceipt
import com.iliatokarev.receipt_splitter_app.receipt.presentation.ReceiptEvent
import com.iliatokarev.receipt_splitter_app.receipt.presentation.ReceiptViewModel
import com.iliatokarev.receipt_splitter_app.receipt.presentation.viewmodels.CreateReceiptEvent
import com.iliatokarev.receipt_splitter_app.receipt.presentation.viewmodels.CreateReceiptIntent
import com.iliatokarev.receipt_splitter_app.receipt.presentation.viewmodels.CreateReceiptUiMessage
import com.iliatokarev.receipt_splitter_app.receipt.presentation.viewmodels.CreateReceiptUiMessageIntent
import com.iliatokarev.receipt_splitter_app.receipt.presentation.viewmodels.CreateReceiptUiState
import com.iliatokarev.receipt_splitter_app.receipt.presentation.viewmodels.CreateReceiptViewModel
import com.iliatokarev.receipt_splitter_app.receipt.presentation.views.dialogs.ChooseLanguageDialog
import com.iliatokarev.receipt_splitter_app.receipt.presentation.views.screens.CreateReceiptScreenView
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.RESULT_FORMAT_JPEG
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.SCANNER_MODE_BASE
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateReceiptScreen(
    modifier: Modifier = Modifier,
    receiptViewModel: ReceiptViewModel,
    createReceiptViewModel: CreateReceiptViewModel,
    currentActivity: Activity? = LocalActivity.current,
) {
    val messageUiMap = mapOf<String, String>(
        CreateReceiptUiMessage.NETWORK_ERROR.message to stringResource(R.string.no_internet_connection),
        CreateReceiptUiMessage.INTERNAL_ERROR.message to stringResource(R.string.internal_error),
        CreateReceiptUiMessage.IMAGE_IS_INAPPROPRIATE.message to stringResource(R.string.image_is_inappropriate),
        CreateReceiptUiMessage.TOO_MANY_ATTEMPTS.message to stringResource(R.string.too_many_attempts),
        CreateReceiptUiMessage.LOGIN_REQUIRED.message to stringResource(R.string.sign_in_required),
        CreateReceiptUiMessage.ATTEMPTS_LEFT.message to stringResource(R.string.remaining_attempts),
        CreateReceiptUiMessage.RECEIPT_IS_TOO_BIG.message to stringResource(R.string.receipt_is_too_big)
    )
    val listOfImages by createReceiptViewModel.getReceiptImages().collectAsStateWithLifecycle()
    val uiState by createReceiptViewModel.getUiStateFlow().collectAsStateWithLifecycle()

    var showLanguageDialog by rememberSaveable { mutableStateOf(false) }
    var selectedLanguage by rememberSaveable { mutableStateOf<String?>(null) }
    var languageSwitchState by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(key1 = Unit) {
        createReceiptViewModel.getIntentFlow().collectLatest { createIntent ->
            when (createIntent) {
                is CreateReceiptIntent.NewReceiptIsCreated -> {
                    receiptViewModel.setEvent(ReceiptEvent.NewReceiptIsCreated(createIntent.receiptId))
                }

                is CreateReceiptIntent.UserIsEmpty -> {
                    receiptViewModel.setEvent(ReceiptEvent.SignOut)
                }
            }
        }
    }
    LaunchedEffect(key1 = Unit) {
        createReceiptViewModel.getUiMessageIntentFlow().collectLatest { messageIntent ->
            handleCreateReceiptUiMessages(messageIntent, messageUiMap, currentActivity)
        }
    }

    val choosePhotoLaunch = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = DataConstantsReceipt.MAXIMUM_AMOUNT_OF_IMAGES),
    ) { newListOfImages: List<Uri> ->
        createReceiptViewModel.setEvent(CreateReceiptEvent.PutImages(listOfImages = newListOfImages))
    }

    val scanningOptions = GmsDocumentScannerOptions.Builder()
        .setGalleryImportAllowed(false)
        .setPageLimit(DataConstantsReceipt.MAXIMUM_AMOUNT_OF_IMAGES)
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
                    listOfUri = listOfImages ?: emptyList(),
                    onChoosePhotoClicked = {
                        choosePhotoLaunch.launch(
                            PickVisualMediaRequest(
                                mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly,
                                maxItems = DataConstantsReceipt.MAXIMUM_AMOUNT_OF_IMAGES,
                            )
                        )
                    },
                    onClearPhotoClicked = {
                        createReceiptViewModel.setEvent(CreateReceiptEvent.PutImages(listOfImages = emptyList()))
                    },
                    onGetReceiptFromImageClicked = {
                        createReceiptViewModel.setEvent(
                            CreateReceiptEvent.CreateReceipt(selectedLanguage)
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
                    },
                    onSwitchCheckedChange = { value ->
                        languageSwitchState = value
                        if (value)
                            showLanguageDialog = true
                        else
                            selectedLanguage = null
                    },
                    languageSwitchState = languageSwitchState,
                    translatedLanguage = selectedLanguage,
                    onShowLanguageDialog = { showLanguageDialog = true }
                )
            }
        }

        if (showLanguageDialog)
            ChooseLanguageDialog(
                onDismissRequest = {
                    showLanguageDialog = false
                    if (selectedLanguage == null)
                        languageSwitchState = false
                },
                onLanguageSelected = { language ->
                    selectedLanguage = language
                    showLanguageDialog = false
                },
                selectedLanguage = selectedLanguage,
            )
    }
}

private fun handleCreateReceiptUiMessages(
    messageIntent: CreateReceiptUiMessageIntent,
    messageMap: Map<String, String>,
    currentActivity: Activity?,
) {
    when (messageIntent) {
        is CreateReceiptUiMessageIntent.SomeImagesAreInappropriate -> {
            Toast.makeText(
                currentActivity,
                messageMap[CreateReceiptUiMessage.IMAGE_IS_INAPPROPRIATE.message]
                    ?: CreateReceiptUiMessage.INTERNAL_ERROR.message,
                Toast.LENGTH_SHORT
            ).show()
        }

        is CreateReceiptUiMessageIntent.InternalError -> {
            Toast.makeText(
                currentActivity,
                messageMap[CreateReceiptUiMessage.INTERNAL_ERROR.message]
                    ?: CreateReceiptUiMessage.INTERNAL_ERROR.message,
                Toast.LENGTH_SHORT
            ).show()
        }

        is CreateReceiptUiMessageIntent.InternetConnectionError -> {
            Toast.makeText(
                currentActivity,
                messageMap[CreateReceiptUiMessage.NETWORK_ERROR.message]
                    ?: CreateReceiptUiMessage.INTERNAL_ERROR.message,
                Toast.LENGTH_SHORT
            ).show()
        }

        is CreateReceiptUiMessageIntent.TooManyAttempts -> {
            Toast.makeText(
                currentActivity,
                ("${messageMap[CreateReceiptUiMessage.TOO_MANY_ATTEMPTS.message]} ${messageIntent.resetTimeMin}"),
                Toast.LENGTH_LONG
            ).show()
        }

        is CreateReceiptUiMessageIntent.LoginRequired -> {
            Toast.makeText(
                currentActivity,
                messageMap[CreateReceiptUiMessage.LOGIN_REQUIRED.message]
                    ?: CreateReceiptUiMessage.INTERNAL_ERROR.message,
                Toast.LENGTH_LONG
            ).show()
        }

        is CreateReceiptUiMessageIntent.AttemptsLeft -> {
            Toast.makeText(
                currentActivity,
                ("${messageMap[CreateReceiptUiMessage.ATTEMPTS_LEFT.message]} ${messageIntent.attemptsLeft}"),
                Toast.LENGTH_LONG
            ).show()
        }

        is CreateReceiptUiMessageIntent.ReceiptIsTooBig -> {
            Toast.makeText(
                currentActivity,
                messageMap[CreateReceiptUiMessage.RECEIPT_IS_TOO_BIG.message]
                    ?: CreateReceiptUiMessage.INTERNAL_ERROR.message,
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}