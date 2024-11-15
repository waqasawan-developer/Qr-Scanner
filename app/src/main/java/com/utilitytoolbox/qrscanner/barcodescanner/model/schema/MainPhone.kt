package com.utilitytoolbox.qrscanner.barcodescanner.model.schema


import com.utilitytoolbox.qrscanner.barcodescanner.utils.removePrefixIgnoreCase
import com.utilitytoolbox.qrscanner.barcodescanner.utils.startsWithIgnoreCase

class PhoneModel(val phone: String) : Schema {

    companion object {
        private const val PREFIX = "tel:"

        fun parse(text: String): PhoneModel? {
            if (text.startsWithIgnoreCase(PREFIX).not()) {
                return null
            }

            val phone = text.removePrefixIgnoreCase(PREFIX)
            return PhoneModel(phone)
        }
    }

    override val schema = BarcodeSchema.PHONE
    override fun toFormattedText(): String = phone
    override fun toBarcodeText(): String = "$PREFIX$phone"
}