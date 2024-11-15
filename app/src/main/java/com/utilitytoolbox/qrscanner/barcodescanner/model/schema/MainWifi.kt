package com.utilitytoolbox.qrscanner.barcodescanner.model.schema


import com.utilitytoolbox.qrscanner.barcodescanner.utils.appendIfNotNullOrBlank
import com.utilitytoolbox.qrscanner.barcodescanner.utils.joinToStringNotNullOrBlankWithLineSeparator
import com.utilitytoolbox.qrscanner.barcodescanner.utils.startsWithIgnoreCase
import com.utilitytoolbox.qrscanner.barcodescanner.utils.unescape
import java.util.*

class WifiModel(
    val encryption: String? = null,
    val name: String? = null,
    val password: String? = null,
    val isHidden: Boolean? = null,
    val anonymousIdentity: String? = null,
    val identity: String? = null,
    val eapMethod: String? = null,
    val phase2Method: String? = null
) : Schema {

    companion object {
        private val WIFI_REGEX = """^WIFI:((?:.+?:(?:[^\\;]|\\.)*;)+);?$""".toRegex()
        private val PAIR_REGEX = """(.+?):((?:[^\\;]|\\.)*);""".toRegex()
        private const val SCHEMA_PREFIX = "WIFI:"
        private const val ENCRYPTION_PREFIX = "T:"
        private const val NAME_PREFIX = "S:"
        private const val PASSWORD_PREFIX = "P:"
        private const val IS_HIDDEN_PREFIX = "H:"
        private const val ANONYMOUS_IDENTITY_PREFIX = "AI:"
        private const val IDENTITY_PREFIX = "I:"
        private const val EAP_PREFIX = "E:"
        private const val PHASE2_PREFIX = "PH2:"
        private const val SEPARATOR = ";"

        fun parse(text: String): WifiModel? {
            if (text.startsWithIgnoreCase(SCHEMA_PREFIX).not()) {
                return null
            }

            val keysAndValuesSubstring = WIFI_REGEX.matchEntire(text)?.groupValues?.get(1) ?: return null
            val keysAndValues = PAIR_REGEX
                .findAll(keysAndValuesSubstring)
                .map { pair ->
                    "${pair.groupValues[1].toUpperCase(Locale.US)}:" to pair.groupValues[2]
                }
                .toMap()

            return WifiModel(
                keysAndValues[ENCRYPTION_PREFIX]?.unescape(),
                keysAndValues[NAME_PREFIX]?.unescape(),
                keysAndValues[PASSWORD_PREFIX]?.unescape(),
                keysAndValues[IS_HIDDEN_PREFIX].toBoolean(),
                keysAndValues[ANONYMOUS_IDENTITY_PREFIX]?.unescape(),
                keysAndValues[IDENTITY_PREFIX]?.unescape(),
                keysAndValues[EAP_PREFIX],
                keysAndValues[PHASE2_PREFIX]
            )
        }
    }

    override val schema = BarcodeSchema.WIFI

    override fun toFormattedText(): String {
        return listOf(name, encryption, password).joinToStringNotNullOrBlankWithLineSeparator()
    }

    override fun toBarcodeText(): String {
        return StringBuilder()
            .append(SCHEMA_PREFIX)
            .appendIfNotNullOrBlank(ENCRYPTION_PREFIX, encryption, SEPARATOR)
            .appendIfNotNullOrBlank(NAME_PREFIX, name, SEPARATOR)
            .appendIfNotNullOrBlank(PASSWORD_PREFIX, password, SEPARATOR)
            .appendIfNotNullOrBlank(IS_HIDDEN_PREFIX, isHidden?.toString(), SEPARATOR)
            .append(SEPARATOR)
            .toString()
    }
}