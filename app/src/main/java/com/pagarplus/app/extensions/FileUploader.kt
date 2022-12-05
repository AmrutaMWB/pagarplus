package com.pagarplus.app.extensions

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import com.google.android.material.internal.ContextUtils
import com.pagarplus.app.appcomponents.di.MyApp
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream

object FileUploadHelper {

    @SuppressLint("Range")
    fun getFileName(file: Uri): String? {
        val cursor = MyApp.getInstance().contentResolver.query(file, null, null, null, null)
        cursor?.moveToNext()
        val fileName =
            cursor?.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
        cursor?.close()
        return fileName
    }

    fun getFileBytes(file: Uri): ByteArray? {
        val inputStream = MyApp.getInstance().contentResolver.openInputStream(file)
        return inputStream?.readBytes()
    }

    fun convertBytesToKB(byteArr: ByteArray): Int? {
        return (byteArr.size / 1000)
    }

    fun convertBytesToMB(byteArr: ByteArray): Int {
        val kb = convertBytesToKB(byteArr)
        return if (kb != null) {
            kb / 1000
        } else {
            0
        }
    }

    /*fun compressedImageFile(filePath: String): ByteArray {
        val ONE_KO = 1024
        val ONE_MO = ONE_KO * ONE_KO
        var quality = 80
        var inputStream: InputStream? = null
        if (filePath.isNotEmpty()) {
            var bufferSize = Integer.MAX_VALUE
            val byteArrayOutputStream = ByteArrayOutputStream()
            try {
                val bitmap = BitmapFactory.decodeFile(filePath)
                *//*do {*//*
                    if (bitmap != null) {
                        byteArrayOutputStream.reset()
                        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream)
                        bufferSize = byteArrayOutputStream.size()
                        Log.e("Quality","quality: $quality -> length: $bufferSize")
                        //quality -= 10
                    }
                *//*} while (bufferSize > ONE_MO)*//*
                inputStream = ByteArrayInputStream(byteArrayOutputStream.toByteArray())
                byteArrayOutputStream.close()
            } catch (e: Exception) {
                Log.e("Excepetion","Exception when compressing file image: ${e.message}")
            }
        }
        Log.e("quality",quality.toString())
        Log.e("Name",inputStream!!.readBytes().toString())
        return inputStream!!.readBytes()
    }*/

    @SuppressLint("RestrictedApi")
    fun compressedImageFile(filePath: Uri,context: Context): ByteArray {
        /*convert uir image to bitmap*/
        val fullBitmap = MediaStore.Images.Media.getBitmap(ContextUtils.getActivity(context)!!.getContentResolver(), filePath)

        val scaleWidth: Int = fullBitmap.width / 4
        val scaleHeight: Int = fullBitmap.height / 4
        val scaledBitmap = Bitmap.createScaledBitmap(fullBitmap, scaleWidth, scaleHeight, true)
        val baos = ByteArrayOutputStream()
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)

        return baos.toByteArray()
    }
}