package com.hackathon_ieee.myapplication.core.network

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.provider.OpenableColumns
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import kotlin.math.max
import kotlin.math.roundToInt

private const val TARGET_UPLOAD_BYTES = 850 * 1024
private const val MAX_IMAGE_DIMENSION = 1_600
private const val MIN_IMAGE_DIMENSION = 640

internal data class PreparedPhotoUpload(
    val fileName: String,
    val contentType: String,
    val bytes: ByteArray
)

internal fun ContentResolver.preparePhotoUpload(uri: Uri): PreparedPhotoUpload {
    val originalName = displayName(uri).sanitizeFileName()
    val originalType = getType(uri) ?: "image/jpeg"
    val originalBytes = openInputStream(uri)?.use { it.readBytes() }
        ?: throw IllegalArgumentException("Selected photo cannot be opened.")

    if (originalBytes.size <= TARGET_UPLOAD_BYTES) {
        return PreparedPhotoUpload(
            fileName = originalName,
            contentType = originalType,
            bytes = originalBytes
        )
    }

    var bitmap = decodeScaledBitmap(originalBytes)
    bitmap = applyExifRotation(bitmap, originalBytes)

    var quality = 88
    repeat(5) {
        val compressed = bitmap.toJpeg(quality)
        if (compressed.size <= TARGET_UPLOAD_BYTES) {
            return PreparedPhotoUpload(
                fileName = originalName.asJpegName(),
                contentType = "image/jpeg",
                bytes = compressed
            )
        }
        quality -= 10
    }

    while (max(bitmap.width, bitmap.height) > MIN_IMAGE_DIMENSION) {
        val resized = Bitmap.createScaledBitmap(
            bitmap,
            (bitmap.width * 0.8f).roundToInt().coerceAtLeast(1),
            (bitmap.height * 0.8f).roundToInt().coerceAtLeast(1),
            true
        )
        if (resized !== bitmap) bitmap.recycle()
        bitmap = resized

        val compressed = bitmap.toJpeg(78)
        if (compressed.size <= TARGET_UPLOAD_BYTES) {
            return PreparedPhotoUpload(
                fileName = originalName.asJpegName(),
                contentType = "image/jpeg",
                bytes = compressed
            )
        }
    }

    return PreparedPhotoUpload(
        fileName = originalName.asJpegName(),
        contentType = "image/jpeg",
        bytes = bitmap.toJpeg(55)
    )
}

private fun decodeScaledBitmap(bytes: ByteArray): Bitmap {
    val bounds = BitmapFactory.Options().apply {
        inJustDecodeBounds = true
    }
    BitmapFactory.decodeByteArray(bytes, 0, bytes.size, bounds)

    var sampleSize = 1
    while (
        bounds.outWidth / sampleSize > MAX_IMAGE_DIMENSION * 2 ||
        bounds.outHeight / sampleSize > MAX_IMAGE_DIMENSION * 2
    ) {
        sampleSize *= 2
    }

    val options = BitmapFactory.Options().apply {
        inSampleSize = sampleSize
        inPreferredConfig = Bitmap.Config.ARGB_8888
    }
    val decoded = BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)
        ?: throw IllegalArgumentException("Selected photo format is not supported.")

    val longestSide = max(decoded.width, decoded.height)
    if (longestSide <= MAX_IMAGE_DIMENSION) return decoded

    val scale = MAX_IMAGE_DIMENSION.toFloat() / longestSide
    val scaled = Bitmap.createScaledBitmap(
        decoded,
        (decoded.width * scale).roundToInt().coerceAtLeast(1),
        (decoded.height * scale).roundToInt().coerceAtLeast(1),
        true
    )
    if (scaled !== decoded) decoded.recycle()
    return scaled
}

private fun applyExifRotation(bitmap: Bitmap, bytes: ByteArray): Bitmap {
    val orientation = runCatching {
        ExifInterface(ByteArrayInputStream(bytes)).getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )
    }.getOrDefault(ExifInterface.ORIENTATION_NORMAL)

    val rotation = when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> 90f
        ExifInterface.ORIENTATION_ROTATE_180 -> 180f
        ExifInterface.ORIENTATION_ROTATE_270 -> 270f
        else -> 0f
    }
    if (rotation == 0f) return bitmap

    val rotated = Bitmap.createBitmap(
        bitmap,
        0,
        0,
        bitmap.width,
        bitmap.height,
        Matrix().apply { postRotate(rotation) },
        true
    )
    if (rotated !== bitmap) bitmap.recycle()
    return rotated
}

private fun Bitmap.toJpeg(quality: Int): ByteArray =
    ByteArrayOutputStream().use { output ->
        if (!compress(Bitmap.CompressFormat.JPEG, quality, output)) {
            throw IllegalArgumentException("Selected photo could not be prepared.")
        }
        output.toByteArray()
    }

private fun String.asJpegName(): String =
    substringBeforeLast('.', this).ifBlank { "riverguard-report" } + ".jpg"

private fun ContentResolver.displayName(uri: Uri): String {
    query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (nameIndex >= 0 && cursor.moveToFirst()) {
            return cursor.getString(nameIndex)
        }
    }
    return "riverguard-report.jpg"
}

private fun String.sanitizeFileName(): String =
    replace("\r", "_").replace("\n", "_").replace("\"", "_")
