package com.utilitytoolbox.qrscanner.barcodescanner.model.schema

import com.utilitytoolbox.qrscanner.barcodescanner.utils.removePrefixIgnoreCase
import com.utilitytoolbox.qrscanner.barcodescanner.utils.startsWithAnyIgnoreCase
import com.utilitytoolbox.qrscanner.barcodescanner.utils.unsafeLazy

class MainApps(val url: String) : Schema {

    companion object {
        private val PREFIXES = listOf("https://play.google.com/store/apps/details?id=", "market://search", "http://play.google.com/", "https://play.google.com/")

        fun parse(text: String): MainApps? {
            if (text.startsWithAnyIgnoreCase(PREFIXES).not()) {
                return null
            }
            return MainApps(text)
        }

        fun fromPackage(packageName: String): MainApps {
            return MainApps(PREFIXES[0] + packageName)
        }
    }

    override val schema = BarcodeSchema.APP
    override fun toFormattedText(): String = url
    override fun toBarcodeText(): String = url

    val appPackage by unsafeLazy {
        url.removePrefixIgnoreCase(PREFIXES[0])
    }
}