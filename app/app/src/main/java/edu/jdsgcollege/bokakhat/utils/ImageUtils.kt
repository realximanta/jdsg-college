package edu.jdsgcollege.bokakhat.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

object ImageUtils {
    /**
     * Compresses an image from a URI to WebP format, scaling down if necessary.
     * Returns a File object in the app's cache directory.
     */
    fun compressImageToWebp(context: Context, uri: Uri): File? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val originalBitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            if (originalBitmap == null) return null

            // Scale down if larger than 2048px
            val maxDimension = 2048
            val width = originalBitmap.width
            val height = originalBitmap.height
            val scaledBitmap = if (width > maxDimension || height > maxDimension) {
                val ratio = width.toFloat() / height.toFloat()
                val newWidth = if (width > height) maxDimension else (maxDimension * ratio).toInt()
                val newHeight = if (height > width) maxDimension else (maxDimension / ratio).toInt()
                Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true)
            } else {
                originalBitmap
            }

            // Compress to WebP (Use WEBP_LOSSY for API 30+ to avoid deprecation)
            val format = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                Bitmap.CompressFormat.WEBP_LOSSY
            } else {
                @Suppress("DEPRECATION")
                Bitmap.CompressFormat.WEBP
            }

            val outputStream = ByteArrayOutputStream()
            scaledBitmap.compress(format, 80, outputStream)
            val byteArray = outputStream.toByteArray()

            // Save to cache file
            val cacheFile = File(context.cacheDir, "compressed_avatar.webp")
            val fileOutputStream = FileOutputStream(cacheFile)
            fileOutputStream.write(byteArray)
            fileOutputStream.close()

            // Clean up bitmaps
            if (scaledBitmap != originalBitmap) scaledBitmap.recycle()
            originalBitmap.recycle()

            cacheFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
