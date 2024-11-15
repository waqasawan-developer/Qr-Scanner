package com.utilitytoolbox.qrscanner.barcodescanner.usecase


import com.utilitytoolbox.qrscanner.barcodescanner.model.MineBarCode
import com.utilitytoolbox.qrscanner.barcodescanner.model.schema
.*
import com.google.zxing.BarcodeFormat
import com.google.zxing.Result
import com.google.zxing.ResultMetadataType
import com.utilitytoolbox.qrscanner.barcodescanner.model.schema.MainApps
import com.utilitytoolbox.qrscanner.barcodescanner.model.schema
.MainBoardingPass
import com.utilitytoolbox.qrscanner.barcodescanner.model.schema.MainBookmark
import com.utilitytoolbox.qrscanner.barcodescanner.model.schema
.CryptocurrencyModel
import com.utilitytoolbox.qrscanner.barcodescanner.model.schema
.EmailModel
import com.utilitytoolbox.qrscanner.barcodescanner.model.schema
.GeoModel
import com.utilitytoolbox.qrscanner.barcodescanner.model.schema
.GoogleMapsModel
import com.utilitytoolbox.qrscanner.barcodescanner.model.schema
.MeCardModel
import com.utilitytoolbox.qrscanner.barcodescanner.model.schema
.MmsModel
import com.utilitytoolbox.qrscanner.barcodescanner.model.schema
.NZCovidTracerModel
import com.utilitytoolbox.qrscanner.barcodescanner.model.schema
.OtherModel
import com.utilitytoolbox.qrscanner.barcodescanner.model.schema
.OtpAuthModel
import com.utilitytoolbox.qrscanner.barcodescanner.model.schema
.PhoneModel
import com.utilitytoolbox.qrscanner.barcodescanner.model.schema
.Schema
import com.utilitytoolbox.qrscanner.barcodescanner.model.schema
.SmsModel
import com.utilitytoolbox.qrscanner.barcodescanner.model.schema
.VCardModel
import com.utilitytoolbox.qrscanner.barcodescanner.model.schema
.VEventModel
import com.utilitytoolbox.qrscanner.barcodescanner.model.schema
.WifiModel
import com.utilitytoolbox.qrscanner.barcodescanner.model.schema
.YoutubeModel

object BarcodeParserNew{

    fun parseResult(result: Result): MineBarCode {
        var text = ""
        if (!result.text.isNullOrEmpty()) {
            text = result.text
        }
        val schema = parseSchema(result.barcodeFormat, text)
        return MineBarCode(
            text = text,
            formattedText = schema.toFormattedText(),
            format = result.barcodeFormat,
            schema = schema.schema,
            datetime = result.timestamp,
            correctionLevel = result.resultMetadata?.get(ResultMetadataType.ERROR_CORRECTION_LEVEL) as? String
        )
    }

    fun parseSchema(format: BarcodeFormat, text: String): Schema {
        if (format != BarcodeFormat.QR_CODE) {
            return MainBoardingPass.parse(text)
                   ?: OtherModel(text)
        }

        return MainApps.parse(text)
            ?: YoutubeModel.parse(text)
            ?: GoogleMapsModel.parse(text)
            ?: UrlModel.parse(text)
            ?: PhoneModel.parse(text)
            ?: GeoModel.parse(text)
            ?: MainBookmark.parse(text)
            ?: SmsModel.parse(text)
            ?: MmsModel.parse(text)
            ?: WifiModel.parse(text)
            ?: EmailModel.parse(text)
            ?: CryptocurrencyModel.parse(text)
            ?: VEventModel.parse(text)
            ?: MeCardModel.parse(text)
            ?: VCardModel.parse(text)
            ?: OtpAuthModel.parse(text)
            ?: NZCovidTracerModel.parse(text)
            ?: MainBoardingPass.parse(text)
            ?: OtherModel(text)
    }
}