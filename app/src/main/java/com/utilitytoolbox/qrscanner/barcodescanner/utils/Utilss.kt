package com.utilitytoolbox.qrscanner.barcodescanner.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaScannerConnection
import android.net.ConnectivityManager
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Environment
import android.os.SystemClock
import android.os.VibrationEffect
import android.os.Vibrator
import android.provider.ContactsContract
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import android.view.Window
import android.view.WindowInsets
import android.widget.EditText
import android.widget.RadioButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.utilitytoolbox.qrscanner.barcodescanner.model
.MainBarcodeParsedNew
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import com.utilitytoolbox.qrscanner.barcodescanner.dialogs.ErrorFragDialog
import com.utilitytoolbox.qrscanner.barcodescanner.model.schema
.BarcodeSchema
import com.google.zxing.BarcodeFormat
import com.utilitytoolbox.qrscanner.barcodescanner.R
import com.utilitytoolbox.qrscanner.barcodescanner.model.MineBarCode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.apache.commons.codec.binary.Base32
import per.wsj.library.AndRatingBar
import java.io.File
import java.io.FileWriter
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private var mLastClickTime: Long = 0
fun rapidClick(): Boolean {

    // mis-clicking prevention, using threshold of 1000 ms
    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
        return false
    }
    mLastClickTime = SystemClock.elapsedRealtime()
    return true
}

var onInAppSubscription: (() -> Unit)? = null
var disableBatchScanningMode: (() -> Unit)? = null
var showBatchScanningFromHistory: (() -> Unit)? = null
var onBatchScanningEnabled: Boolean = true
var startScanFrag: ((Boolean) -> Unit)? = null
var createOrCancelQr: ((Boolean) -> Unit)? = null
var showBatchScanningFavHistory: ((Boolean, ArrayList<MineBarCode>) -> Unit)? = null


public fun showConfirmationDialog(context: Context) {
    // Inflate the custom layout for the dialog
    val dialogView = LayoutInflater.from(context).inflate(R.layout.rating_layout, null)

    // Find the views in the custom layout
    val yesButton = dialogView.findViewById<TextView>(R.id.tvCreate)
    val noButton = dialogView.findViewById<TextView>(R.id.tvCancel)
    val rattingBar = dialogView.findViewById<AndRatingBar>(R.id.rattingBar)

    // You can modify the message dynamically if needed

    // Create the dialog builder
    val builder = AlertDialog.Builder(context,R.style.CustomDialog)
        .setView(dialogView) // Set the custom layout for the dialog
        .setCancelable(false) // Optional: prevent dismissing the dialog by tapping outside

    // Create the dialog
    val dialog = builder.create()

    // Set the listeners for the buttons
    yesButton.setOnClickListener {
        //  handleYesButtonClick() // Handle Yes button click
        dialog.dismiss() // Close the dialog

        Log.d(
            "jhdjhd8fdkjfkd",
            "here is clicked on ratting -> ${rattingBar.rating} -> ${context.packageName}"
        )
    }

    //   rattingBar.setOnRatingChangeListener

    noButton.setOnClickListener {
        //  handleNoButtonClick() // Handle No button click
        dialog.dismiss() // Close the dialog
    }

    // Show the dialog
    dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

    dialog.show()
}


fun rateUs(context: Context) {
    val appPackageName = context.packageName // getPackageName() from Context or Activity object
    try {
        context.startActivity(
            Intent(
                Intent.ACTION_VIEW, Uri.parse(
                    "market://details?id=$appPackageName"
                )
            )
        )
    } catch (anfe: ActivityNotFoundException) {
        context.startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id=" + context.packageName)
            )
        )
    }

    // TinyDB.getInstance(mContext).putBoolean(Constants.RATEBAR_CHECK, true)

}

fun getHeaderListScan(
    scanDataEntities: List<MineBarCode>,
): ArrayList<MineBarCode> {
    val mSectionList = ArrayList<MineBarCode>()
    var lastHeader: String? = ""
    var header: String? = ""
    for (user in scanDataEntities) {
        user?.apply {
            isHeader = false
            header = (SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH).format((datetime)))

            Log.d(
                "kjsdf978sfkjsfs",
                "$lastHeader -> $header -> ${!TextUtils.equals(lastHeader, header)}"
            )

            if (!TextUtils.equals(lastHeader, header)) {
                lastHeader = header
                var scanDataEntity = MineBarCode(
                    0, name,
                    text,
                    formattedText,
                    format,
                    schema,
                    datetime,
                    isGenerated,
                    isFavorite,
                    correctionLevel,
                    isScanned,
                    true
                )
                mSectionList.add(scanDataEntity)
            }
            mSectionList.add(user)
        }
    }

    return (mSectionList)
}

fun Context.createFile(
    historyModel: List<MainBarcodeParsedNew>,
    type: String,
    isScan: Boolean?,
    isShare: Boolean,
    isTXTExport: Boolean
): Boolean {

    Log.d("jfshf7sfsf", "here is is Share - >$isShare")
    val calendar: Calendar = Calendar.getInstance()
    val time: Long = calendar.timeInMillis
    var fileName: String = "CSV_Data_$time.csv"
    var current = historyModel

    if (isTXTExport) {
        fileName = "${type}_$time.txt"
    } else {
        fileName = "${type}_$time.csv"
    }
    var myDir: File? = null
    var newFile: File? = null
    try {
        if (isShare) {
            myDir = this.filesDir
            newFile = File(myDir.absoluteFile, fileName)

        } else {
            val root = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS
            ).toString()
            myDir = File("$root/QR SCANNER BY WAQAS")
            if (!myDir.exists()) {
                myDir.mkdir()
            }
            newFile = File(myDir.absoluteFile, fileName)
            newFile.createNewFile()
        }

        if (isTXTExport) {
            val writer = FileWriter(newFile)

            historyModel.forEach {
                if (isScan == true) {
                    writer.append(it.formattedText + "\n")
                } else {
                    writer.append((it.formattedText + "\n"))
                }
            }

            writer.flush()
            writer.close()
            if (isShare) {
                CoroutineScope(Dispatchers.Main).launch {
                    this@createFile.shareFile(newFile, isTXTExport)
                }
            }
        } else {
            val csvWriter = csvWriter()
            csvWriter.open(newFile, append = false) {
                if (isScan == true) {
                    writeRow(
                        listOf(
                            "Scan Id",
                            "Scan Code",
                            "Scan Type",
                            "Timestamp",
                        )
                    )
                }

                if (isScan == true) {
                    historyModel.forEach {
                        writeRow(
                            listOf(
                                it.id,
                                it.formattedText,
                                it.schema,
                                it.date
                            )
                        )
                    }
                }

                if (isShare) {
                    CoroutineScope(Dispatchers.Main).launch {
                        this@createFile.shareFile(newFile, isTXTExport)
                    }
                }
                flush()
                close()
            }
        }


    } catch (e: Exception) {
        println(e.message)
        return false
    } finally {
        CoroutineScope(Dispatchers.Main).launch {
            MediaScannerConnection.scanFile(
                this@createFile,
                arrayOf<String>(newFile?.absolutePath!!),
                null,
                { path, uri -> })
        }
    }
    return true
}

private fun Context.shareFile(
    mFile: File,
    isTXTExport: Boolean
) {
    try {
        val intent = Intent()
        intent.action = Intent.ACTION_SEND
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.putExtra(
            Intent.EXTRA_SUBJECT,
            resources.getString(R.string.app_name)
        )
        intent.putExtra("share_our_app", true)
        if (isTXTExport) {
            intent.type = "text/plain"
        } else {
            intent.type = "text/csv"
        }
        val uri: Uri = FileProvider.getUriForFile(
            this,
            this.packageName + ".FileProvider",
            File(mFile.path)
        )
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        startActivity(Intent.createChooser(intent, "Share File"))
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun Context.initSubMenuDialog(
): Dialog? {
    val dialog = Dialog(this, R.style.CustomDialog)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
    dialog.setContentView(R.layout.layout_options_sub_menu)
    //  var selectedColor = TinyDB.getInstance(this).getInt(Constants.SELECTED_COLOR_SCHEME)
    val colorStateList = ColorStateList(
        arrayOf(
            intArrayOf(-android.R.attr.state_checked),
            intArrayOf(android.R.attr.state_checked)
        ), intArrayOf(
            ContextCompat.getColor(this, R.color.text_foreground_color),  // disabled
            R.color.color_accent // enabled
        )
    )
    dialog?.findViewById<RadioButton>(R.id.saveRadioBtn)?.buttonTintList =
        colorStateList // set the color tint list

    dialog?.findViewById<RadioButton>(R.id.saveRadioBtn)?.invalidate()

    dialog?.findViewById<RadioButton>(R.id.shareRadioBtn)?.buttonTintList =
        colorStateList // set the color tint list

    dialog?.findViewById<RadioButton>(R.id.shareRadioBtn)?.invalidate()

    return dialog
}

fun getBarcodeFormat(format: Int): BarcodeFormat {

    when (format) {
        com.google.mlkit.vision.barcode.common.Barcode.FORMAT_CODE_128 -> return BarcodeFormat.CODE_128
        com.google.mlkit.vision.barcode.common.Barcode.FORMAT_CODE_39 -> return BarcodeFormat.CODE_39
        com.google.mlkit.vision.barcode.common.Barcode.FORMAT_CODE_93 -> return BarcodeFormat.CODE_93
        com.google.mlkit.vision.barcode.common.Barcode.FORMAT_CODABAR -> return BarcodeFormat.CODABAR
        com.google.mlkit.vision.barcode.common.Barcode.FORMAT_DATA_MATRIX -> return BarcodeFormat.DATA_MATRIX
        com.google.mlkit.vision.barcode.common.Barcode.FORMAT_EAN_13 -> return BarcodeFormat.EAN_13
        com.google.mlkit.vision.barcode.common.Barcode.FORMAT_EAN_8 -> return BarcodeFormat.EAN_8
        com.google.mlkit.vision.barcode.common.Barcode.FORMAT_ITF -> return BarcodeFormat.ITF
        com.google.mlkit.vision.barcode.common.Barcode.FORMAT_QR_CODE -> return BarcodeFormat.QR_CODE
        com.google.mlkit.vision.barcode.common.Barcode.FORMAT_UPC_A -> return BarcodeFormat.UPC_A
        com.google.mlkit.vision.barcode.common.Barcode.FORMAT_UPC_E -> return BarcodeFormat.UPC_E
        com.google.mlkit.vision.barcode.common.Barcode.FORMAT_PDF417 -> return BarcodeFormat.PDF_417
        com.google.mlkit.vision.barcode.common.Barcode.FORMAT_AZTEC -> return BarcodeFormat.AZTEC
    }
    return BarcodeFormat.QR_CODE
}

fun BarcodeFormat.toStringId(): Int {
    return when (this) {
        BarcodeFormat.AZTEC -> R.string.format_aztec
        BarcodeFormat.CODABAR -> R.string.format_codabar
        BarcodeFormat.CODE_39 -> R.string.format_code_39
        BarcodeFormat.CODE_93 -> R.string.format_code_93
        BarcodeFormat.CODE_128 -> R.string.format_code_128
        BarcodeFormat.DATA_MATRIX -> R.string.format_data_matrix
        BarcodeFormat.EAN_8 -> R.string.format_ean_8
        BarcodeFormat.EAN_13 -> R.string.format_ean_13
        BarcodeFormat.ITF -> R.string.format_itf_14
        BarcodeFormat.PDF_417 -> R.string.format_pdf_417
        BarcodeFormat.QR_CODE -> R.string.format_qr_code
        BarcodeFormat.UPC_A -> R.string.format_upc_a
        BarcodeFormat.UPC_E -> R.string.format_upc_e
        else -> R.string.format_qr_code
    }
}


fun Context.getLanguageAgainstCodeNew(code: String): String {

    when (code) {
        "ar" -> return "Arabic (العربية)"
        "bn" -> return "Bangla (বাংলা)"
        "zh" -> return "Chinese (汉语)"
        "fr" -> return "French (française)"
        "de" -> return "German (Deutsch)"
        "hi" -> return "Hindi (हिंदी)"
        "in" -> return "Indonesia (Indonesian)"
        "it" -> return "Italian (Italiano)"
        "ms" -> return "Malaysian (Melayu)"
        "nl" -> return "Nederlands (Dutch)"
        "ru" -> return "Russian (Русский)"
        "ko" -> return "Korean (한국어)"
        "es" -> return "Spanish (Español)"
        "tr" -> return "Turkish (Türkçe)"
        "uk" -> return "Ukrainian (українська)"
        "en" -> return "English (USA)"
        "pt" -> return "Portuguese (Português)"
        "th" -> return "Thai (ไทย)"
        "ja" -> return "Japanese (日本語)"
        "vi" -> return "Vietnamese (Tiếng Việt)"
        else -> return "Default"


    }
}


fun BarcodeSchema.toImageId(): Int? {
    return when (this) {
        BarcodeSchema.BOOKMARK -> R.drawable.ic_bookmark_
        BarcodeSchema.EMAIL -> R.drawable.ic_email_qr_content
        BarcodeSchema.GEO -> R.drawable.ic_location_qr
        BarcodeSchema.APP -> R.drawable.ic_apps
        BarcodeSchema.MMS -> R.drawable.ic_mms_qr
        BarcodeSchema.MECARD -> R.drawable.ic_user_contact
        BarcodeSchema.PHONE -> R.drawable.ic_phone_qr
        BarcodeSchema.OTP_AUTH -> R.drawable.ic_otp_qr
        BarcodeSchema.SMS -> R.drawable.ic_sms
        BarcodeSchema.URL -> R.drawable.ic_link_qr
        BarcodeSchema.VEVENT -> R.drawable.ic_calendar
        BarcodeSchema.VCARD -> R.drawable.ic_user_contact
        BarcodeSchema.WIFI -> R.drawable.ic_wifi_qr
        BarcodeSchema.YOUTUBE -> R.drawable.ic_youtube_qr
        //BarcodeSchema.BOARDINGPASS -> R.drawable.ic_boardingpass
        else -> null
    }
}

val Fragment.packageManager: PackageManager
    get() = requireContext().packageManager

fun Fragment.showError(error: Throwable?) {
    val errorDialog = ErrorFragDialog.newInstance(requireContext(), error)
    errorDialog.show(childFragmentManager, "")
}

fun <T> unsafeLazy(initializer: () -> T): Lazy<T> = lazy(LazyThreadSafetyMode.NONE, initializer)
fun EditText.isNotBlank(): Boolean {
    return text.isNotBlank()
}

val EditText.textString: String
    get() = text.toString()

fun Int?.orZero(): Int {
    return this ?: 0
}

val Context.vibrator: Vibrator?
    get() = getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator

val Context.wifiManager: WifiManager?
    get() = applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager

val Context.clipboardManager: ClipboardManager?
    get() = getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager

val Context.currentLocale: Locale?
    get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        resources.configuration.locales.get(0)
    } else {
        resources.configuration.locale
    }

fun BarcodeSchema.toStringId(): Int? {
    return when (this) {
        BarcodeSchema.BOOKMARK -> R.string.schema_bookmark
        BarcodeSchema.CRYPTOCURRENCY -> R.string.schema_cryptocurrency
        BarcodeSchema.EMAIL -> R.string.schema_email
        BarcodeSchema.GEO -> R.string.schema_geo
        BarcodeSchema.APP -> R.string.schema_google_play
        BarcodeSchema.MMS -> R.string.schema_mms
        BarcodeSchema.MECARD -> R.string.schema_me_card
        BarcodeSchema.PHONE -> R.string.schema_phone
        BarcodeSchema.OTP_AUTH -> R.string.schema_otp
        BarcodeSchema.SMS -> R.string.schema_sms
        BarcodeSchema.URL -> R.string.schema_url
        BarcodeSchema.VEVENT -> R.string.schema_v_event
        BarcodeSchema.VCARD -> R.string.schema_v_card
        BarcodeSchema.WIFI -> R.string.schema_wifi
        BarcodeSchema.YOUTUBE -> R.string.schema_youtube
        BarcodeSchema.OTHER -> R.string.schema_other
        else -> null
    }
}

fun BarcodeFormat.toImageId(): Int {
    return when (this) {
        BarcodeFormat.QR_CODE -> R.drawable.ic_qr_code
        BarcodeFormat.DATA_MATRIX -> R.drawable.ic_data_matrix_qr
        BarcodeFormat.AZTEC -> R.drawable.ic_aztec
        BarcodeFormat.PDF_417 -> R.drawable.ic_pdf417_qr
        else -> R.drawable.ic_barcode_
    }
}

fun BarcodeFormat.toColorId(): Int {
    return when (this) {
        BarcodeFormat.QR_CODE -> R.color.blue3
        BarcodeFormat.DATA_MATRIX, BarcodeFormat.AZTEC, BarcodeFormat.PDF_417, BarcodeFormat.MAXICODE -> R.color.orange
        else -> R.color.green
    }
}

fun DateFormat.parseOrNull(date: String?): Date? {
    return try {
        parse(date.orEmpty())
    } catch (ex: Exception) {
        null
    }
}

fun List<DateFormat>.parseOrNull(date: String?): Date? {
    forEach { dateParser ->
        val parsedDate = dateParser.parseOrNull(date)
        if (parsedDate != null) {
            return parsedDate
        }
    }
    return null
}

fun DateFormat.formatOrNull(time: Long?): String? {
    return try {
        format(Date(time!!))
    } catch (ex: Exception) {
        null
    }
}

fun Boolean?.orFalse(): Boolean {
    return this ?: false
}


fun Activity.changeStatusBarClr(color: Int = R.color.white, isTransparent: Boolean) {
    window?.let {
        if (isTransparent) {
//            it.setFlags(
//                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
//                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
//            )
        } else {
            //it.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }
        it.statusBarColor = ContextCompat.getColor(this, color)
    }
}

fun isNetworkAvailable(activity: Context): Boolean {
    val networkInfo =
        (activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo
    return networkInfo != null && networkInfo.isConnected
}

fun setMargins(v: View, l: Int, t: Int, r: Int, b: Int) {
    if (v.getLayoutParams() is MarginLayoutParams) {
        val p = v.getLayoutParams() as MarginLayoutParams
        p.setMargins(l, t, r, b)
        v.requestLayout()
    }
}

fun Activity.changeStatusBarClr(color: Int = R.color.color_accent) {
    window?.let {
        it.statusBarColor = ContextCompat.getColor(this, color)
    }
}

fun Context.checkCameraPermission(): Boolean {
    val result =
        ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
    return result == PackageManager.PERMISSION_GRANTED
}


private val escapedRegex = """\\([\\;,":])""".toRegex()

fun String.unescape(): String {
    return replace(escapedRegex) { escaped ->
        escaped.groupValues[1]
    }
}

fun String?.decodeBase32(): ByteArray? {
    if (isNullOrBlank()) {
        return null
    }
    return Base32().decode(this)
}

fun String.toCaps(): String {
    return toUpperCase(Locale.ROOT)
}


fun String.removeStartAll(symbol: Char): String {
    var newStart = 0

    run loop@{
        forEachIndexed { index, c ->
            if (c == symbol) {
                newStart = index + 1
            } else {
                return@loop
            }
        }
    }

    return if (newStart >= length) {
        ""
    } else {
        substring(newStart)
    }
}

fun String.removePrefixIgnoreCase(prefix: String): String {
    return substring(prefix.length)
}

fun String.startsWithIgnoreCase(prefix: String): Boolean {
    return startsWith(prefix, true)
}

fun String.startsWithAnyIgnoreCase(prefixes: List<String>): Boolean {
    prefixes.forEach { prefix ->
        if (startsWith(prefix, true)) {
            return true
        }
    }
    return false
}

fun String.equalsAnyIgnoreCase(others: List<String>): Boolean {
    others.forEach { other ->
        if (equals(other, true)) {
            return true
        }
    }
    return false
}

fun String.endsWithIgnoreCase(prefix: String): Boolean {
    return endsWith(prefix, true)
}

fun List<String?>.joinToStringNotNullOrBlankWithLineSeparator(): String {
    return joinToStringNotNullOrBlank("\n")
}

fun List<String?>.joinToStringNotNullOrBlank(separator: String): String {
    return filter { it.isNullOrBlank().not() }.joinToString(separator)
}

fun String.toCountryEmoji(): String {
    if (this.length != 2) {
        return ""
    }

    val countryCodeCaps = toUpperCase(Locale.US)
    val firstLetter = Character.codePointAt(countryCodeCaps, 0) - 0x41 + 0x1F1E6
    val secondLetter = Character.codePointAt(countryCodeCaps, 1) - 0x41 + 0x1F1E6

    if (!countryCodeCaps[0].isLetter() || !countryCodeCaps[1].isLetter()) {
        return this
    }

    return String(Character.toChars(firstLetter)) + String(Character.toChars(secondLetter))
}

fun String.toEmailType(): Int? {
    return when (toUpperCase(Locale.US)) {
        "HOME" -> ContactsContract.CommonDataKinds.Email.TYPE_HOME
        "WORK" -> ContactsContract.CommonDataKinds.Email.TYPE_WORK
        "OTHER" -> ContactsContract.CommonDataKinds.Email.TYPE_OTHER
        "MOBILE" -> ContactsContract.CommonDataKinds.Email.TYPE_MOBILE
        else -> null
    }
}

fun String.toPhoneType(): Int? {
    return when (toUpperCase(Locale.US)) {
        "HOME" -> ContactsContract.CommonDataKinds.Phone.TYPE_HOME
        "MOBILE", "CELL" -> ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE
        "WORK" -> ContactsContract.CommonDataKinds.Phone.TYPE_WORK
        "FAX_WORK" -> ContactsContract.CommonDataKinds.Phone.TYPE_FAX_WORK
        "FAX_HOME" -> ContactsContract.CommonDataKinds.Phone.TYPE_FAX_HOME
        "PAGER" -> ContactsContract.CommonDataKinds.Phone.TYPE_PAGER
        "OTHER" -> ContactsContract.CommonDataKinds.Phone.TYPE_OTHER
        "CALLBACK" -> ContactsContract.CommonDataKinds.Phone.TYPE_CALLBACK
        "CAR" -> ContactsContract.CommonDataKinds.Phone.TYPE_CAR
        "COMPANY_MAIN" -> ContactsContract.CommonDataKinds.Phone.TYPE_COMPANY_MAIN
        "ISDN" -> ContactsContract.CommonDataKinds.Phone.TYPE_ISDN
        "MAIN" -> ContactsContract.CommonDataKinds.Phone.TYPE_MAIN
        "OTHER_FAX" -> ContactsContract.CommonDataKinds.Phone.TYPE_OTHER_FAX
        "RADIO" -> ContactsContract.CommonDataKinds.Phone.TYPE_RADIO
        "TELEX" -> ContactsContract.CommonDataKinds.Phone.TYPE_TELEX
        "TTY_TDD" -> ContactsContract.CommonDataKinds.Phone.TYPE_TTY_TDD
        "WORK_MOBILE" -> ContactsContract.CommonDataKinds.Phone.TYPE_WORK_MOBILE
        "WORK_PAGER" -> ContactsContract.CommonDataKinds.Phone.TYPE_WORK_PAGER
        "ASSISTANT" -> ContactsContract.CommonDataKinds.Phone.TYPE_ASSISTANT
        "MMS" -> ContactsContract.CommonDataKinds.Phone.TYPE_MMS
        else -> null
    }
}

fun View.applySystemWindowInsets(
    applyLeft: Boolean = false,
    applyTop: Boolean = false,
    applyRight: Boolean = false,
    applyBottom: Boolean = false
) {
    doOnApplyWindowInsets { view, insets, padding ->
        val left = if (applyLeft) insets.systemWindowInsetLeft else 0
        val top = if (applyTop) insets.systemWindowInsetTop else 0
        val right = if (applyRight) insets.systemWindowInsetRight else 0
        val bottom = if (applyBottom) insets.systemWindowInsetBottom else 0

        view.setPadding(
            padding.left + left,
            padding.top + top,
            padding.right + right,
            padding.bottom + bottom
        )
    }
}

@SuppressLint("RestrictedApi")
fun View.doOnApplyWindowInsets(f: (View, WindowInsets, InitialPadding) -> Unit) {
    // Create a snapshot of the view's padding state
    val initialPadding = recordInitialPaddingForView(this)
    // Set an actual OnApplyWindowInsetsListener which proxies to the given
    // lambda, also passing in the original padding state
    setOnApplyWindowInsetsListener { v, insets ->
        f(v, insets, initialPadding)
        // Always return the insets, so that children can also use them
        insets
    }
    // request some insets
    requestApplyInsetsWhenAttached()
}

data class InitialPadding(
    val left: Int, val top: Int,
    val right: Int, val bottom: Int
)

private fun recordInitialPaddingForView(view: View) = InitialPadding(
    view.paddingLeft, view.paddingTop, view.paddingRight, view.paddingBottom
)

fun AppCompatActivity.showError(error: Throwable?) {
    val errorDialog =
        ErrorFragDialog.newInstance(
            this,
            error
        )
    errorDialog.show(supportFragmentManager, "")
}

fun View.requestApplyInsetsWhenAttached() {
    if (isAttachedToWindow) {
        // We're already attached, just request as normal
        requestApplyInsets()
    } else {
        // We're not attached to the hierarchy, add a listener to
        // request when we are
        addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) {
                v.removeOnAttachStateChangeListener(this)
                v.requestApplyInsets()
            }

            override fun onViewDetachedFromWindow(v: View) = Unit
        })
    }
}

fun Vibrator.vibrateOnce(pattern: LongArray) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrate(VibrationEffect.createWaveform(pattern, -1))
    } else {
        vibrate(pattern, -1)
    }
}

fun Uri.Builder.appendQueryParameterIfNotNullOrBlank(key: String, value: String?): Uri.Builder {
    if (value.isNullOrBlank().not()) {
        appendQueryParameter(key, value)
    }
    return this
}

fun StringBuilder.appendIfNotNullOrBlank(
    prefix: String = "",
    value: String?,
    suffix: String = ""
): StringBuilder {
    if (value.isNullOrBlank().not()) {
        append(prefix)
        append(value)
        append(suffix)
    }
    return this
}

fun String.toAddressType(): Int? {
    return when (toUpperCase(Locale.US)) {
        "HOME" -> ContactsContract.CommonDataKinds.StructuredPostal.TYPE_HOME
        "WORK" -> ContactsContract.CommonDataKinds.StructuredPostal.TYPE_WORK
        "OTHER" -> ContactsContract.CommonDataKinds.StructuredPostal.TYPE_OTHER
        else -> null
    }
}


