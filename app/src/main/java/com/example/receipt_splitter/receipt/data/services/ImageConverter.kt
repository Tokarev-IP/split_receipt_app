package com.example.receipt_splitter.receipt.data.services

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

    override suspend fun convertImageListFromUriToBitmapList(listOfImages: List<Uri>): List<Bitmap> {
        return listOfImages.map {  imageUri ->
            val inputStream = appContext.contentResolver.openInputStream(imageUri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()
            bitmap
        }
    }
}

interface ImageConverterInterface {
    suspend fun convertImageFromUriToBitmap(imageUri: Uri): Bitmap
    suspend fun convertImageListFromUriToBitmapList(listOfImages: List<Uri>): List<Bitmap>
}