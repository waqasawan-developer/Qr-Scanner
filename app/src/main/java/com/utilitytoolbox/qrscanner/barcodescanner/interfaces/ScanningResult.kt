package com.utilitytoolbox.qrscanner.barcodescanner.interfaces


import com.google.mlkit.vision.barcode.common.Barcode
import com.google.zxing.Result

interface ScanningResult {
    fun onScanned(result: Result)
    fun onBatchScanning(barcodes: MutableList<Barcode>)
}