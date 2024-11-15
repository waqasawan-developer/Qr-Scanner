package com.utilitytoolbox.qrscanner.barcodescanner.model


import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.utilitytoolbox.qrscanner.barcodescanner.model.schema
.BarcodeSchema

class QRMainType(
    var barcodeFormat: BarcodeFormat,
    var barcodeSchema: BarcodeSchema? = null,
    var qrType: String,
    var content: String,
    var icon: Int,
    var color:Int
){}