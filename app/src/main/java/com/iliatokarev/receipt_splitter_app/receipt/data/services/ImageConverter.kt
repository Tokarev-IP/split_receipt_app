package com.iliatokarev.receipt_splitter_app.receipt.data.services

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ImageConverter(private val appContext: Context) : ImageConverterInterface {

    override suspend fun convertImageFromUriToBitmap(imageUri: Uri): Bitmap {
        return withContext(Dispatchers.Default) {
            val inputStream = appContext.contentResolver.openInputStream(imageUri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()
            return@withContext bitmap
        }
    }

    override suspend fun convertImageListFromUriToBitmapList(listOfImages: List<Uri>): List<Bitmap> {
        return withContext(Dispatchers.Default) {
            listOfImages.map { imageUri ->
                val inputStream = appContext.contentResolver.openInputStream(imageUri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()
                bitmap
            }
        }
    }
}

interface ImageConverterInterface {
    suspend fun convertImageFromUriToBitmap(imageUri: Uri): Bitmap
    suspend fun convertImageListFromUriToBitmapList(listOfImages: List<Uri>): List<Bitmap>
}