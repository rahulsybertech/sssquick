package com.ssspvtltd.quick_new.utils

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.OpenableColumns
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.Locale


object MediaUtils {

    private const val TAG = "MediaUtils"
    private const val MAX_FILE_SIZE: Long = 1500 * 1000 // 1500 KB = 1.5 MB
    private const val IMAGE_MAX_DIMENSION = 1024

    @JvmStatic
    fun getBlankImageUri(context: Context): Uri? {
        var imageUri: Uri? = null
        val imageFile = getImageFile(context)
        imageFile?.also {
            imageUri = FileProvider.getUriForFile(context, "${context.packageName}.provider", it)
        }
        return imageUri
    }

    @JvmStatic
    fun getImageFile(context: Context): File? {
        val picDirFile = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        if (picDirFile != null) {
            return File.createTempFile(
                "IMG_${System.currentTimeMillis()}", /* prefix */
                ".jpg", /* suffix */
                picDirFile /* directory */
            ).apply { deleteOnExit() }
        }
        return null
    }

    @JvmStatic
    fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        // Raw height and width of image
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2
            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while (halfHeight / inSampleSize >= reqHeight || halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

    @JvmStatic
    fun copyFileToInternalStorage(uri: Uri, context: Context): String? {
        try {
            val internalFile = File(
                context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                getFileName(uri, context.contentResolver)!!
            ).apply { deleteOnExit() }

            // Load Sampled Bitmap
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            var stream = context.contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(stream, null, options)
            stream?.close()
            val scale = calculateInSampleSize(options, IMAGE_MAX_DIMENSION, IMAGE_MAX_DIMENSION)
            options.inJustDecodeBounds = false
            options.inSampleSize = scale
            stream = context.contentResolver.openInputStream(uri)
            var resultBitmap: Bitmap? = BitmapFactory.decodeStream(stream, null, options)
            stream?.close()

            // Scale Bitmap Based On Resolution
            resultBitmap = resizeBitmapToResolution(resultBitmap)
            resultBitmap =
                compressBitmapToSize(resultBitmap, getMimeType(uri, context.contentResolver))
            if (resultBitmap == null) return null

            // Write bitmap to file
            val outputStream: OutputStream = FileOutputStream(internalFile)
            resultBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.close()
            return internalFile.absolutePath
        } catch (t: Throwable) {
            t.printStackTrace()
            return null
        }
    }

    @JvmStatic
    fun resizeBitmapToResolution(bitmap: Bitmap?, resolution: Int = IMAGE_MAX_DIMENSION): Bitmap? {
        try {
            if (bitmap == null) return null
            var width = bitmap.width
            var height = bitmap.height
            if (width > height && width > resolution) {
                val ratio = height.toFloat() / width
                width = resolution
                height = (width * ratio).toInt()
            } else if (width < height && height > resolution) {
                val ratio = width.toFloat() / height
                height = resolution
                width = (height * ratio).toInt()
            } else if (width > resolution) {
                val ratio = height.toFloat() / width
                width = resolution
                height = (width * ratio).toInt()
            }
            var scaledBitmap: Bitmap? = null
            try {
                scaledBitmap = Bitmap.createScaledBitmap(bitmap, width, height, true)
            } catch (exception: OutOfMemoryError) {
                exception.printStackTrace()
            }
            if (scaledBitmap != bitmap) {
                bitmap.recycle()
            }
            return scaledBitmap
        } catch (t: Throwable) {
            t.printStackTrace()
            return null
        }
    }

    @JvmStatic
    fun compressBitmapToSize(
        bitmap: Bitmap?,
        mimeType: String?,
        requiredFileSize: Long = MAX_FILE_SIZE
    ): Bitmap? {
        try {
            if (bitmap == null) return null
            if (mimeType == null) return bitmap
            var reduce = 0
            var isCompressed = true
            var stream: ByteArrayOutputStream? = null
            var fileSize: Int
            do {
                stream?.close()
                stream = ByteArrayOutputStream()
                if (mimeType.contains("jpg", true) || mimeType.contains("jpeg", true)) {
                    isCompressed = bitmap.compress(
                        Bitmap.CompressFormat.JPEG, 98 - (reduce * 10), stream
                    )
                } else if (mimeType.contains("png", true)) {
                    isCompressed = bitmap.compress(
                        Bitmap.CompressFormat.PNG, 98 - (reduce * 10), stream
                    )
                } else if (mimeType.contains("webp", true)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        isCompressed = bitmap.compress(
                            Bitmap.CompressFormat.WEBP_LOSSLESS, 98 - (reduce * 10), stream
                        )
                    } else {
                        isCompressed = bitmap.compress(
                            Bitmap.CompressFormat.WEBP, 98 - (reduce * 10), stream
                        )
                    }
                }
                fileSize = stream.size()
                Log.i(TAG, "size $fileSize")
                reduce++
            } while (fileSize > requiredFileSize && isCompressed)
            val resultBitmap =
                BitmapFactory.decodeStream(ByteArrayInputStream(stream?.toByteArray()))
            bitmap.recycle()
            stream?.close()
            return resultBitmap
        } catch (t: Throwable) {
            t.printStackTrace()
            return null
        }
    }

    @JvmStatic
    fun getFileName(uri: Uri, contentResolver: ContentResolver): String? {
        var result: String? = null
        if (uri.scheme == "content") {
            contentResolver.query(uri, null, null, null, null).use { cursor ->
                if (cursor != null && cursor.moveToFirst()) {
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    result = cursor.getString(nameIndex)
                }
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/')
            if (cut != null && cut != -1) {
                result = result?.substring(cut + 1)
            }
        }
        if (result == null) {
            result = "IMG_${System.currentTimeMillis()}.jpg"
        }
        return result
    }

    @JvmStatic
    private fun getMimeType(uri: Uri, contentResolver: ContentResolver): String? {
        val mimeType: String? = if ("content".equals(uri.scheme, ignoreCase = true)) {
            contentResolver.getType(uri)
        } else {
            val fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString())
            MimeTypeMap.getSingleton()
                .getMimeTypeFromExtension(fileExtension.lowercase(Locale.getDefault()))
        }
        return mimeType
    }

    @JvmStatic
    fun deleteFile(context: Context, filePath: String?) {
        try {
            val file = File(filePath!!)
            if (file.exists()) file.delete()
        } catch (t: Throwable) {
            t.printStackTrace()
        }
    }

    @JvmStatic
    fun deleteTempFiles(context: Context) {
        try {
            val picDirFile = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            if (picDirFile?.exists() == true) {
                val files = picDirFile.listFiles().orEmpty()
                for (file in files) {
                    if (file.exists()) file.delete()
                }
            }
        } catch (t: Throwable) {
            t.printStackTrace()
        }
    }
}