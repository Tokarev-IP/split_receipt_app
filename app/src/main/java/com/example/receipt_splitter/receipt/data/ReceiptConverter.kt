package com.example.receipt_splitter.receipt.data

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class ReceiptConverter(
    private val appContext: Context,
): ReceiptConverterInterface {

    // When using Latin script library
//    val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    // When using Chinese script library
//    val recognizer = TextRecognition.getClient(ChineseTextRecognizerOptions.Builder().build())

    // When using Devanagari script library
//    val recognizer = TextRecognition.getClient(DevanagariTextRecognizerOptions.Builder().build())

    // When using Japanese script library
//    val recognizer = TextRecognition.getClient(JapaneseTextRecognizerOptions.Builder().build())

    // When using Korean script library
//    val recognizer = TextRecognition.getClient(KoreanTextRecognizerOptions.Builder().build())

    override suspend fun convertReceiptImagesToText(listOfUri: List<Uri>): String {
        val receiptText = StringBuilder()
        for (uri in listOfUri){
            val text = convertReceiptImageToText(uri)
            receiptText.append(text)
        }
//        Log.d("TOKAR", "$receiptText")
        return receiptText.toString()
    }

    private suspend fun convertReceiptImageToText(imageUri: Uri): String{
        return suspendCancellableCoroutine { continuation ->
            val inputImage: InputImage = InputImage.fromFilePath(appContext, imageUri)
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            recognizer.process(inputImage)
                .addOnSuccessListener { result ->
                    val receiptText = StringBuilder()
                    for (block in result.textBlocks){
                        for (line in block.lines){
                            for (lineElement in line.elements){
                                receiptText.append(lineElement.text + " ")
                            }
                            receiptText.append("\n")
                        }
                    }
                    Log.d("TOKAR", "$receiptText")
                    val text = result.text
                    continuation.resume(text)
                }
                .addOnFailureListener { e: Exception ->
                    continuation.resumeWithException(e)
                }
        }
    }
}

interface ReceiptConverterInterface {
    suspend fun convertReceiptImagesToText(listOfUri: List<Uri>): String
}