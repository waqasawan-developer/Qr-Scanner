package com.utilitytoolbox.qrscanner.barcodescanner.interfaces

import android.util.Log
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.utilitytoolbox.qrscanner.barcodescanner.utils.getBarcodeFormat
import com.utilitytoolbox.qrscanner.barcodescanner.utils.onBatchScanningEnabled
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.google.zxing.Result

class MLKitAnalyzer(private val listener: ScanningResult) : ImageAnalysis.Analyzer {

    private var isScanning: Boolean = false

    @ExperimentalGetImage
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null && !isScanning) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            val scanner = BarcodeScanning.getClient()

            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    if (barcodes.isNotEmpty()) {
                        if (onBatchScanningEnabled) {
                            listener.onBatchScanning(barcodes)
                            android.os.Handler().postDelayed({
                                imageProxy.close()
                            }, 500)
                        } else {
                            isScanning = true
                            barcodes.firstOrNull().let { barcode ->
                                Log.e("TAG", "analyze: ${barcode?.toString()}")
                                val qrCodeData: String? = barcode?.rawValue
                                val qrCodeFormat: Int? = barcode?.format
                                val rawBytes: ByteArray? = barcode?.rawBytes

                                qrCodeFormat?.let {
                                    // Create a ZXing Result object
                                    val zxingResult = Result(
                                        qrCodeData,
                                        rawBytes,  // QR code raw bytes if available, can be obtained from mlKitBarcode.getRawBytes()
                                        null,  // ResultPoint[] if available
                                        getBarcodeFormat(qrCodeFormat)
                                    )

                                    listener.onScanned(zxingResult)
                                }
                            }
                            isScanning = false
                            imageProxy.close()
                        }
                    } else {
                        isScanning = false
                        imageProxy.close()
                    }
                }
                .addOnFailureListener {
                    isScanning = false
                    imageProxy.close()
                }
        }
    }


}