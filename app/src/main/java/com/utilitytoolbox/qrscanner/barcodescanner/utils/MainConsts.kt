package com.utilitytoolbox.qrscanner.barcodescanner.utils

import android.content.Context
import com.utilitytoolbox.qrscanner.barcodescanner.model
.TypeOfQR
import com.utilitytoolbox.qrscanner.barcodescanner.model.schema
.BarcodeSchema
import com.google.zxing.BarcodeFormat
import com.utilitytoolbox.qrscanner.barcodescanner.R

object MainConsts {
    const val is_premium_const = "is_premium_pro"
    const val selected_camera = "selected_camera"
    const val selected_theme = "selected_theme"
    const val is_app_open_ad_restrict = "is_app_open_ad_restrict"
    const val is_interstitial_ad_showed = "is_interstitial_ad_showed"
    const val inter_ad_show_interval = "inter_ad_show_interval"
    const val is_language_screen_required = "is_language_screen_required"
    const val selected_language = "selected_language"
    const val inter_ad_click_counter = "inter_ad_click_counter"


    fun getQrCreateData(context: Context): MutableList<TypeOfQR> {
        val typeClipboard = TypeOfQR(
            BarcodeFormat.QR_CODE,
            BarcodeSchema.OTHER,
            "CLIPBOARD",
            context.getString(R.string.clipboard),
            R.drawable.ic_copy_
        )
        val typeText = TypeOfQR(
            BarcodeFormat.QR_CODE,
            BarcodeSchema.OTHER,
            "TEXT",
            context.getString(R.string.qr_code_text),
            R.drawable.ic_text_new
        )
        val typeUrl = TypeOfQR(
            BarcodeFormat.QR_CODE,
            BarcodeSchema.URL,
            "URL",
            context.getString(R.string.schema_url),
            R.drawable.ic_link_qr
        )
        val typeWifi = TypeOfQR(
            BarcodeFormat.QR_CODE,
            BarcodeSchema.WIFI,
            "WIFI",
            context.getString(R.string.schema_wifi),
            R.drawable.ic_wifi_qr
        )
        val typeLocation = TypeOfQR(
            BarcodeFormat.QR_CODE,
            BarcodeSchema.GEO,
            "LOCATION",
            context.getString(R.string.schema_geo),
            R.drawable.ic_location_qr
        )
        val typeContact = TypeOfQR(
            BarcodeFormat.QR_CODE,
            BarcodeSchema.MECARD,
            "CONTACT",
            context.getString(R.string.schema_me_card),
            R.drawable.ic_user_contact
        )
        val typePhone = TypeOfQR(
            BarcodeFormat.QR_CODE,
            BarcodeSchema.PHONE,
            "PHONE",
            context.getString(R.string.schema_phone),
            R.drawable.ic_phone_qr
        )
        val typeEmail = TypeOfQR(
            BarcodeFormat.QR_CODE,
            BarcodeSchema.EMAIL,
            "EMAIL",
            context.getString(R.string.schema_email),
            R.drawable.ic_email_qr_content
        )
        val typeSms = TypeOfQR(
            BarcodeFormat.QR_CODE,
            BarcodeSchema.SMS,
            "SMS",
            context.getString(R.string.schema_sms),
            R.drawable.ic_sms
        )
        val typeApps = TypeOfQR(
            BarcodeFormat.QR_CODE,
            BarcodeSchema.APP,
            "APPS",
            context.getString(R.string.schema_google_play),
            R.drawable.ic_apps
        )
        val typeEAN13 = TypeOfQR(
            BarcodeFormat.EAN_13,
            null,
            "EAN_13",
            context.getString(R.string.format_ean_13),
            R.drawable.ic_barcode_
        )
        val typeEAN8 = TypeOfQR(
            BarcodeFormat.EAN_8,
            null,
            "EAN_8",
            context.getString(R.string.format_ean_8),
            R.drawable.ic_barcode_
        )
        val typeUPCE = TypeOfQR(
            BarcodeFormat.UPC_E,
            null,
            "UPC_E",
            context.getString(R.string.format_upc_e),
            R.drawable.ic_barcode_
        )
        val typeUPCA = TypeOfQR(
            BarcodeFormat.UPC_A,
            null,
            "UPC_A",
            context.getString(R.string.format_upc_a),
            R.drawable.ic_barcode_
        )
        val typeCODE128 = TypeOfQR(
            BarcodeFormat.CODE_128,
            null,
            "CODE_128",
            context.getString(R.string.format_code_128),
            R.drawable.ic_barcode_
        )
        val typeCODE93 = TypeOfQR(
            BarcodeFormat.CODE_93,
            null,
            "CODE_93",
            context.getString(R.string.format_code_93),
            R.drawable.ic_barcode_
        )
        val typeCODE39 = TypeOfQR(
            BarcodeFormat.CODE_39,
            null,
            "CODE_39",
            context.getString(R.string.format_code_39),
            R.drawable.ic_barcode_
        )
        val typeCODABAR = TypeOfQR(
            BarcodeFormat.CODABAR,
            null,
            "CODA_BAR",
            context.getString(R.string.format_codabar),
            R.drawable.ic_barcode_
        )
        val typeITF = TypeOfQR(
            BarcodeFormat.ITF,
            null,
            "ITF",
            context.getString(R.string.format_itf_14),
            R.drawable.ic_barcode_
        )
        val typeDATAMATRIX = TypeOfQR(
            BarcodeFormat.DATA_MATRIX,
            null,
            "DATA_MATRIX",
            context.getString(R.string.format_data_matrix),
            R.drawable.ic_data_matrix_qr
        )
        val typeAZTEC = TypeOfQR(
            BarcodeFormat.AZTEC,
            null,
            "AZTEC",
            context.getString(R.string.format_aztec),
            R.drawable.ic_aztec
        )
        val typePDF417 = TypeOfQR(
            BarcodeFormat.PDF_417,
            null,
            "PDF_417",
            context.getString(R.string.format_pdf_417),
            R.drawable.ic_pdf417_qr
        )
        return mutableListOf(
            typeClipboard,
            typeText,
            typeUrl,
            typeWifi,
            typeLocation,
            typeContact,
            typePhone,
            typeEmail,
            typeSms,
            typeApps,
            typeEAN13,
            typeEAN8,
            typeUPCE,
            typeUPCA,
            typeCODE128,
            typeCODE93,
            typeCODE39,
            typeCODABAR,
            typeITF,
            typeDATAMATRIX,
            typeAZTEC,
            typePDF417
        )
    }
}