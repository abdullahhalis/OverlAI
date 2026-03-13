package com.abdullahhalis.overlai.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.media.Image
import android.os.Environment
import android.provider.MediaStore
import androidx.core.graphics.createBitmap

fun Image.toBitmap(width: Int, height: Int): Bitmap {
    val planes = this.planes
    val buffer = planes[0].buffer
    val pixelStride = planes[0].pixelStride
    val rowStride = planes[0].rowStride
    val rowPadding = rowStride - pixelStride * width

    val bitmap = createBitmap(width + rowPadding / pixelStride, height)
    bitmap.copyPixelsFromBuffer(buffer)

    return bitmap
}

fun Bitmap.saveToGallery(context: Context) {
    val fileName = "overlai_${System.currentTimeMillis()}.png"
    val contentValues = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
        put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/OverlAI")
    }

    val uri = context.contentResolver.insert(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        contentValues
    )

    uri?.let {
        context.contentResolver.openOutputStream(it)?.use { stream ->
            this.compress(Bitmap.CompressFormat.PNG, 100, stream)
        }
    }
}