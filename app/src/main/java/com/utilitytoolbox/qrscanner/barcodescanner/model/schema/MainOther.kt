package com.utilitytoolbox.qrscanner.barcodescanner.model.schema


class OtherModel(val text: String): Schema {
    override val schema = BarcodeSchema.OTHER
    override fun toFormattedText(): String = text
    override fun toBarcodeText(): String = text
}