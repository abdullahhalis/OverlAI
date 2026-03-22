package com.abdullahhalis.overlai.utils

import android.graphics.Bitmap
import android.media.Image
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
