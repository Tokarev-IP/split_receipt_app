package com.example.receipt_splitter.receipt.data.services

import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabel
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class ImageLabelingKit() : ImageLabelingKitInterface {

    private fun getInputImageFromBitmap(bitmap: Bitmap) =
        InputImage.fromBitmap(bitmap, 0)

    override suspend fun getLabelsOfImage(bitmap: Bitmap): List<ImageLabel> {
        return suspendCancellableCoroutine { continuation ->
            val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)
            val inputImage = getInputImageFromBitmap(bitmap)

            labeler.process(inputImage)
                .addOnSuccessListener { labels ->
                    continuation.resume(labels)
                }
                .addOnFailureListener { e ->
                    continuation.resumeWithException(e)
                }
        }
    }
}

interface ImageLabelingKitInterface {
    suspend fun getLabelsOfImage(bitmap: Bitmap): List<ImageLabel>
}