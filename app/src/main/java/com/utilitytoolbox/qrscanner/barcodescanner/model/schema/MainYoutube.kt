package com.utilitytoolbox.qrscanner.barcodescanner.model.schema


import com.utilitytoolbox.qrscanner.barcodescanner.utils.startsWithAnyIgnoreCase


class YoutubeModel(val url: String) : Schema {

    companion object {
        private val PREFIXES = listOf("vnd.youtube://", "http://www.youtube.com", "https://www.youtube.com")

        fun parse(text: String): YoutubeModel? {
            if (text.startsWithAnyIgnoreCase(PREFIXES).not()) {
                return null
            }
            return YoutubeModel(text)
        }
    }

    override val schema = BarcodeSchema.YOUTUBE
    override fun toFormattedText(): String = url
    override fun toBarcodeText(): String = url
}