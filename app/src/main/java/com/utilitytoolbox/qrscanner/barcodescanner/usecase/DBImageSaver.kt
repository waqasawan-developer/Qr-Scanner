package com.utilitytoolbox.qrscanner.barcodescanner.usecase


import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore.Images
import androidx.core.content.FileProvider
import com.utilitytoolbox.qrscanner.barcodescanner.model.MainBarcodeParsedNew   
import com.utilitytoolbox.qrscanner.barcodescanner.model.MineBarCode
import io.reactivex.Completable
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream


object DBImageSaver {

    fun saveImageToCache(context: Context, image: Bitmap, barcode: MainBarcodeParsedNew ): Uri? {

        val imagesFolder = File(context.cacheDir, "qr_scanner_images")
        imagesFolder.mkdirs()

        val imageFileName = "${barcode.format}_${barcode.schema}_${barcode.date}.png"
        val imageFile = File(imagesFolder, imageFileName)

        FileOutputStream(imageFile).apply {
            image.compress(Bitmap.CompressFormat.PNG, 100, this)
            flush()
            close()
        }

        return FileProvider.getUriForFile(context, "com.appscafe.qrscanner.qrscanner.fileprovider", imageFile)
    }

    fun savePngImageToPublicDirectory(context: Context, image: Bitmap, MineBarCode: MineBarCode): Completable {
        return Completable.create { emitter ->
            try {
                saveToPublicDirectory(context, MineBarCode, "image/png") { outputStream ->
                    image.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                }
                emitter.onComplete()
            } catch (ex: Exception) {
                MainLogger.log(ex)
                emitter.onError(ex)
            }
        }
    }

    private fun saveToPublicDirectory(context: Context, MineBarCode: MineBarCode, mimeType:String, action: (OutputStream)-> Unit) {
        val contentResolver = context.contentResolver ?: return

        val imageTitle = "${MineBarCode.format}_${MineBarCode.schema}_${MineBarCode.datetime}"

        val values = ContentValues().apply {
            put(Images.Media.TITLE, imageTitle)
            put(Images.Media.DISPLAY_NAME, imageTitle)
            put(Images.Media.MIME_TYPE, mimeType)
            put(Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000)
        }

        val uri = contentResolver.insert(Images.Media.EXTERNAL_CONTENT_URI, values) ?: return
        contentResolver.openOutputStream(uri)?.apply {
            action(this)
            flush()
            close()
        }
    }
}