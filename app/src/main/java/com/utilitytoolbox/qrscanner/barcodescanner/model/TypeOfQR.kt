package com.utilitytoolbox.qrscanner.barcodescanner.model


import com.utilitytoolbox.qrscanner.barcodescanner.model.schema
.BarcodeSchema
import com.google.zxing.BarcodeFormat

class TypeOfQR(
    var barcodeFormat: BarcodeFormat,
    var barcodeSchema: BarcodeSchema? = null,
    var qrType: String,
    var content: String,
    var icon: Int
){}