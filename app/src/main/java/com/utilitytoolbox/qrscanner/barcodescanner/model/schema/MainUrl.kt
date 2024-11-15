package com.utilitytoolbox.qrscanner.barcodescanner.model.schema


import com.utilitytoolbox.qrscanner.barcodescanner.utils.startsWithAnyIgnoreCase
import com.utilitytoolbox.qrscanner.barcodescanner.utils.startsWithIgnoreCase
import com.utilitytoolbox.qrscanner.barcodescanner.model.schema
.BarcodeSchema
import com.utilitytoolbox.qrscanner.barcodescanner.model.schema
.Schema

class UrlModel(val url: String) : Schema {

    companion object {
        private const val HTTP_PREFIX = "http://"
        private const val HTTPS_PREFIX = "https://"
        private const val WWW_PREFIX = "www."
        private const val F_DROID_REPOSITORY_PREFIX = "fdroidrepos://"
        private val PREFIXES = listOf(HTTP_PREFIX, HTTPS_PREFIX, WWW_PREFIX, F_DROID_REPOSITORY_PREFIX)

        fun parse(text: String): UrlModel? {
            if (text.startsWithAnyIgnoreCase(PREFIXES).not()) {
                return null
            }

            val url = when {
                text.startsWithIgnoreCase(WWW_PREFIX) -> "$HTTP_PREFIX$text"
                else -> text
            }

            return UrlModel(url)
        }
    }

    override val schema = BarcodeSchema.URL
    override fun toFormattedText(): String = url
    override fun toBarcodeText(): String = url
}