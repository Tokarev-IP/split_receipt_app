package com.example.receipt_splitter.receipt.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri

class ImageConverter(private val appContext: Context) : ImageConverterInterface {

    override suspend fun convertImageFromUriToBitmap(imageUri: Uri): Bitmap {
        val inputStream = appContext.contentResolver.openInputStream(imageUri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        inputStream?.close()
        return bitmap
    }
}

interface ImageConverterInterface {
    suspend fun convertImageFromUriToBitmap(imageUri: Uri): Bitmap
}