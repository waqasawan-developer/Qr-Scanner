package com.utilitytoolbox.qrscanner.barcodescanner.model.schema


import com.utilitytoolbox.qrscanner.barcodescanner.utils.joinToStringNotNullOrBlankWithLineSeparator
import com.utilitytoolbox.qrscanner.barcodescanner.utils.removePrefixIgnoreCase
import com.utilitytoolbox.qrscanner.barcodescanner.utils.startsWithIgnoreCase

data class SmsModel(
    val phone: String? = null,
    val message: String? = null
) : Schema {

    companion object {
        private const val PREFIX = "smsto:"
        private const val SEPARATOR = ":"

        fun parse(text: String): SmsModel? {
            if (text.startsWithIgnoreCase(PREFIX).not()) {
                return null
            }

            val parts = text.removePrefixIgnoreCase(PREFIX).split(SEPARATOR)

            return SmsModel(
                phone = parts.getOrNull(0),
                message = parts.getOrNull(1)
            )
        }
    }

    override val schema = BarcodeSchema.SMS

    override fun toFormattedText(): String {
        return listOf(phone, message).joinToStringNotNullOrBlankWithLineSeparator()
    }

    override fun toBarcodeText(): String {
        return PREFIX +
                phone.orEmpty() +
                "$SEPARATOR${message.orEmpty()}"
    }
}