package com.utilitytoolbox.qrscanner.barcodescanner.utils

import android.content.Context
import com.google.zxing.BarcodeFormat
import com.utilitytoolbox.qrscanner.barcodescanner.R
import com.utilitytoolbox.qrscanner.barcodescanner.model
.QRMainType
import com.utilitytoolbox.qrscanner.barcodescanner.model.schema
.BarcodeSchema

object MainConstantsss {
    const val is_premium_const = "is_premium_pro"
    const val selected_camera = "selected_camera"
    const val selected_theme = "selected_theme"
    const val is_app_open_ad_restrict = "is_app_open_ad_restrict"
    const val is_interstitial_ad_showed = "is_interstitial_ad_showed"
    const val inter_ad_show_interval = "inter_ad_show_interval"
    const val is_language_screen_required = "is_language_screen_required"
    const val selected_language = "selected_language"
    const val inter_ad_click_counter = "inter_ad_click_counter"


    fun getQRCreateGeneral(context: Context): MutableList<QRMainType> {

        val typeWifi = QRMainType(
            BarcodeFormat.QR_CODE,
            BarcodeSchema.WIFI,
            "WIFI",
            context.getString(R.string.schema_wifi),
            R.drawable.ic_wifi_c,
            R.color.qrwifiBg
        )

        val typeContact = QRMainType(
            BarcodeFormat.QR_CODE,
            BarcodeSchema.MECARD,
            "CONTACT",
            context.getString(R.string.schema_me_card),
            R.drawable.ic_contact_c,
            R.color.qrcontactBg
        )

        val typeEmail = QRMainType(
            BarcodeFormat.QR_CODE,
            BarcodeSchema.EMAIL,
            "EMAIL",
            context.getString(R.string.schema_email),
            R.drawable.ic_email_c,
            R.color.qremailBg
        )

        val typeSms = QRMainType(
            BarcodeFormat.QR_CODE,
            BarcodeSchema.SMS,
            "SMS",
            context.getString(R.string.schema_sms),
            R.drawable.ic_sms_c,
            R.color.qrsmsBg
        )

        val typePhone = QRMainType(
            BarcodeFormat.QR_CODE,
            BarcodeSchema.PHONE,
            "PHONE",
            context.getString(R.string.schema_phone),
            R.drawable.ic_phone_c,
            R.color.qrphoneBg
        )

        val typeUrl = QRMainType(
            BarcodeFormat.QR_CODE,
            BarcodeSchema.URL,
            "URL",
            context.getString(R.string.schema_url),
            R.drawable.ic_url_c,
            R.color.qrurlBg
        )

        return mutableListOf(
            typeWifi,
            typeContact, typeEmail, typeSms,
            typePhone, typeUrl
        )
    }

    fun getQRCreateOther(context: Context): MutableList<QRMainType> {

        val typeLocation = QRMainType(
            BarcodeFormat.QR_CODE,
            BarcodeSchema.GEO,
            "LOCATION",
            context.getString(R.string.schema_geo),
            R.drawable.ic_location_c,
            R.color.qrlocationBg
        )

        val typeClipboard = QRMainType(
            BarcodeFormat.QR_CODE,
            BarcodeSchema.Clipboard,
            "CLIPBOARD",
            context.getString(R.string.clipboard),
            R.drawable.ic_clipbord_c,
            R.color.qrClipbordBg
        )

        val typeText = QRMainType(
            BarcodeFormat.QR_CODE,
            BarcodeSchema.OTHER,
            "TEXT",
            context.getString(R.string.qr_code_text),
            R.drawable.ic_text_c,
            R.color.qrtextBg
        )



        return mutableListOf(
            typeLocation,
            typeClipboard, typeText
        )
    }

    fun getBarcodeCreateGeneral(context: Context): MutableList<QRMainType> {

        val typeEAN13 = QRMainType(
            BarcodeFormat.EAN_13,
            null,
            "EAN_13",
            context.getString(R.string.format_ean_13),
            R.drawable.ic_barcode_ran13,
            R.color.barcode_ean13
        )
        val typeEAN8 = QRMainType(
            BarcodeFormat.EAN_8,
            null,
            "EAN_8",
            context.getString(R.string.format_ean_8),
            R.drawable.ic_barcode_ean8,
            R.color.barcode_ean8
        )
        val typeUPCE = QRMainType(
            BarcodeFormat.UPC_E,
            null,
            "UPC_E",
            context.getString(R.string.format_upc_e),
            R.drawable.ic_barcode_upc_e,
            R.color.barcode_upce
        )
        val typeUPCA = QRMainType(
            BarcodeFormat.UPC_A,
            null,
            "UPC_A",
            context.getString(R.string.format_upc_a),
            R.drawable.ic_barcode_upc_a,
            R.color.barcode_upca
        )
        val typeCODE93 = QRMainType(
            BarcodeFormat.CODE_93,
            null,
            "CODE_93",
            context.getString(R.string.format_code_93),
            R.drawable.ic_barcode_ran13,
            R.color.barcode_code93
        )
        val typeCODE39 = QRMainType(
            BarcodeFormat.CODE_39,
            null,
            "CODE_39",
            context.getString(R.string.format_code_39),
            R.drawable.ic_barcode_code39, R.color.barcode_code39
        )


        return mutableListOf(
            typeEAN8,
            typeEAN13, typeUPCE,typeUPCA,typeCODE39,typeCODE93
        )
    }

    fun getBarcodeCreateOther(context: Context): MutableList<QRMainType> {

        val typeCODE128 = QRMainType(
            BarcodeFormat.CODE_128,
            null,
            "CODE_128",
            context.getString(R.string.format_code_128),
            R.drawable.ic_barcode_code129,
            R.color.barcode_code128
        )

        val typeCODABAR = QRMainType(
            BarcodeFormat.CODABAR,
            null,
            "CODA_BAR",
            context.getString(R.string.format_codabar),
            R.drawable.ic_barcode_codebar,
            R.color.barcode_codabar
        )
        val typeITF = QRMainType(
            BarcodeFormat.ITF,
            null,
            "ITF",
            context.getString(R.string.format_itf_14),
            R.drawable.ic_barcode_itf,
            R.color.barcode_itf
        )
        val typeDATAMATRIX = QRMainType(
            BarcodeFormat.DATA_MATRIX,
            null,
            "DATA_MATRIX",
            context.getString(R.string.format_data_matrix),
            R.drawable.ic_barcode_matrix,
            R.color.barcode_matrix
        )
        val typeAZTEC = QRMainType(
            BarcodeFormat.AZTEC,
            null,
            "AZTEC",
            context.getString(R.string.format_aztec),
            R.drawable.ic_barcode_aztec,
            R.color.barcode_aztec
        )
        val typePDF417 = QRMainType(
            BarcodeFormat.PDF_417,
            null,
            "PDF_417",
            context.getString(R.string.format_pdf_417),
            R.drawable.ic_barcode_pdf417,
            R.color.barcode_pdf417
        )


        return mutableListOf(
            typeCODE128,
            typeITF, typePDF417,typeCODABAR,typeDATAMATRIX,typeAZTEC
        )


    }
}