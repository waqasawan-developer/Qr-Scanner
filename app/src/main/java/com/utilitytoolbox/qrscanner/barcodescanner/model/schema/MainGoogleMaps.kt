package com.utilitytoolbox.qrscanner.barcodescanner.model.schema


import com.utilitytoolbox.qrscanner.barcodescanner.utils.startsWithAnyIgnoreCase

data class GoogleMapsModel(val url: String) : Schema {

    companion object {
        private val PREFIXES = listOf("http://maps.google.com/", "https://maps.google.com/")

        fun parse(text: String): GoogleMapsModel? {
            if (text.startsWithAnyIgnoreCase(PREFIXES).not()) {
                return null
            }
            return GoogleMapsModel(text)
        }
    }

    override val schema = BarcodeSchema.GOOGLE_MAPS
    override fun toFormattedText(): String = url
    override fun toBarcodeText(): String = url
}