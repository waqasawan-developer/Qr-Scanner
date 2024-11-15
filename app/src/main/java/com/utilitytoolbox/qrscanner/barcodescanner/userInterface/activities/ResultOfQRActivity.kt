package com.utilitytoolbox.qrscanner.barcodescanner.userInterface.activities

import android.Manifest
import android.app.SearchManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.CalendarContract
import android.provider.ContactsContract
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.print.PrintHelper
import com.utilitytoolbox.qrscanner.barcodescanner.BaseActivity
import com.utilitytoolbox.qrscanner.barcodescanner.R
import com.utilitytoolbox.qrscanner.barcodescanner.databinding.ActivityResultOfScanBinding

import com.utilitytoolbox.qrscanner.barcodescanner.model
.MainBarcodeParsedNew 
import com.utilitytoolbox.qrscanner.barcodescanner.usecase
.BarcodeDatabase
import com.utilitytoolbox.qrscanner.barcodescanner.usecase.BarcodeGeneratorImage
import com.utilitytoolbox.qrscanner.barcodescanner.usecase
.DBImageSaver
import com.utilitytoolbox.qrscanner.barcodescanner.usecase
.MainPermissionsHelper
import com.utilitytoolbox.qrscanner.barcodescanner.usecase
.ConnectorWifi
import com.utilitytoolbox.qrscanner.barcodescanner.utils.applySystemWindowInsets
import com.utilitytoolbox.qrscanner.barcodescanner.utils.changeStatusBarClr
import com.utilitytoolbox.qrscanner.barcodescanner.utils.orFalse
import com.utilitytoolbox.qrscanner.barcodescanner.utils.showError
import com.utilitytoolbox.qrscanner.barcodescanner.utils.toEmailType
import com.utilitytoolbox.qrscanner.barcodescanner.utils.toPhoneType
import com.utilitytoolbox.qrscanner.barcodescanner.utils.toStringId
import com.utilitytoolbox.qrscanner.barcodescanner.utils.unsafeLazy
import com.utilitytoolbox.qrscanner.barcodescanner.dialogs.ConfirmationDeleteFragDialog
import com.utilitytoolbox.qrscanner.barcodescanner.dialogs.ShowMainDeleteHistoryDialog
import com.utilitytoolbox.qrscanner.barcodescanner.model.MineBarCode
import com.utilitytoolbox.qrscanner.barcodescanner.model
.SearchEngineMain
import com.utilitytoolbox.qrscanner.barcodescanner.model.schema
.BarcodeSchema
import com.utilitytoolbox.qrscanner.barcodescanner.usecase
.SettingsMainNew
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject


@AndroidEntryPoint
class ResultOfQRActivity : BaseActivity(), ConfirmationDeleteFragDialog.Listener{
    lateinit var binding: ActivityResultOfScanBinding

    var interAdValue: Long = 2

    @Inject
    lateinit var mainSettings: SettingsMainNew

    @Inject
    lateinit var barcodeDatabase: BarcodeDatabase

    private var primaryBaseActivity: Context? = null


    /* override fun attachBaseContext(base: Context) {
         super.attachBaseContext(ApplicationLevel.localeManager.setLocale(base))
         primaryBaseActivity = base
     }*/

    companion object {

        private const val REQUEST_PERMISSIONS_CODE = 101
        private val PERMISSIONS = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)

        private const val QRCODE_KEY = "QRCODE_KEY"
        private const val IS_CREATED_QR = "IS_CREATED_QR"

        fun start(context: Context, mMineBarCode: MineBarCode, isCreatedQr: Boolean = false) {
            val intent = Intent(context, ResultOfQRActivity::class.java).apply {
                putExtra(QRCODE_KEY, mMineBarCode)
                putExtra(IS_CREATED_QR, isCreatedQr)
//              addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            }
            context.startActivity(intent)
        }
    }

    private val compositeDisposable = CompositeDisposable()
    private val dateTimeFormatter = SimpleDateFormat("dd MMM, yyyy hh:mm a", Locale.ENGLISH)


    private val originalCode by unsafeLazy {
        intent?.getSerializableExtra(QRCODE_KEY) as? MineBarCode
            ?: throw IllegalArgumentException("No barcode")
    }

    private fun requestPermissions() {
        MainPermissionsHelper.requestPermissions(
            this,
            PERMISSIONS,
            REQUEST_PERMISSIONS_CODE
        )
    }

    private val isCreated by unsafeLazy {
        intent?.getBooleanExtra(IS_CREATED_QR, false).orFalse()
    }

    private val barcode by unsafeLazy {
        MainBarcodeParsedNew (originalCode)
    }

    private val clipboardManager by unsafeLazy {
        getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultOfScanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        applyUIChanges()
        onClickListeners()
        showQrDetails()
        setupActionButtons()

    }


    private fun applyUIChanges() {
        changeStatusBarClr(isTransparent = true)
        binding.rootView.applySystemWindowInsets(applyTop = true, applyBottom = true)

        if (mainSettings.copyToClipboard) {
            copyToClipboard(barcode.text)
        }

        if (mainSettings.openLinksAutomatically.not() || isCreated) {
            return
        }

        when (barcode.schema) {
            BarcodeSchema.APP -> openInAppMarket()
            BarcodeSchema.BOOKMARK -> saveBookmark()
            BarcodeSchema.CRYPTOCURRENCY -> openBitcoinUrl()
            BarcodeSchema.EMAIL -> sendEmail(barcode.email)
            BarcodeSchema.GEO -> showLocation()
            BarcodeSchema.GOOGLE_MAPS -> showLocation()
            BarcodeSchema.MMS -> sendSms(barcode.phone)
            BarcodeSchema.MECARD -> addToContacts()
            BarcodeSchema.OTP_AUTH -> openOtpInOtherApp()
            BarcodeSchema.PHONE -> dialPhone(barcode.phone)
            BarcodeSchema.SMS -> sendSms(barcode.phone)
            BarcodeSchema.URL -> openLink()
            BarcodeSchema.VEVENT -> addToCalendar()
            BarcodeSchema.VCARD -> addToContacts()
            BarcodeSchema.WIFI -> connectToWifi()
            BarcodeSchema.YOUTUBE -> openInYoutube()
            BarcodeSchema.NZCOVIDTRACER -> openLink()
            BarcodeSchema.BOARDINGPASS -> return
            else -> return
        }
    }

    private fun showQrDetails() {
        binding.toolbar.inflateMenu(R.menu.menu_result)
        binding.toolbar.menu.apply {
            findItem(R.id.item_print_code)?.isVisible = true
        }

        showIsFavorite(barcode.isFavorite)

        try {
            val bitmap = BarcodeGeneratorImage.generateBitmap(
                originalCode,
                2000,
                2000,
                0
                /*settings.barcodeContentColor,
                settings.codeBackgroundColor*/
            )
            binding.flQrImageBg.isVisible = true
            binding.ivQrcode.isVisible = true
            binding.ivQrcode.setImageBitmap(bitmap)
            binding.ivQrcode.setBackgroundColor(mainSettings.codeBackgroundColor)
            binding.flQrImageBg.setBackgroundColor(mainSettings.codeBackgroundColor)
            if (mainSettings.isDarkThemeEnabled.not()) {
                binding.flQrImageBg.setPadding(0, 0, 0, 0)
            }
        } catch (ex: Exception) {
            //MainLogger.log(ex)
            binding.ivQrcode.isVisible = false
        }

        binding.tvDate.text = dateTimeFormatter.format(barcode.date)

        val format = barcode.format.toStringId()
        binding.toolbar.setTitle(format)

        binding.tvQrName.isVisible = true
        binding.tvQrName.setText(barcode.format.toStringId())

        binding.tvQrText.text = if (isCreated) {
            barcode.text
        } else {
            barcode.formattedText
        }
    }

    override fun onDeleteConfirmed() {
        deleteBarcode()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

    private fun onClickListeners() {
        binding.toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.item_print_code -> printBarcode()
            }
            return@setOnMenuItemClickListener true
        }
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
        binding.ivDelete.setOnClickListener {
            showDeleteBarcodeConfirmationDialog()
        }
        binding.ivFavoriteQr.setOnClickListener { toggleIsFavorite() }
        // binding.ivAddToCalendar.setOnClickListener { addToCalendar() }
        //   binding.ivAddToContacts.setOnClickListener { addToContacts() }
        //    binding.ivShowLocation.setOnClickListener { showLocation() }
        //    binding.ivConnectToWifi.setOnClickListener { connectToWifi() }
        //    binding.ivOpenWifiSettings.setOnClickListener { openWifiSettings() }
        //    binding.ivCopyPassword.setOnClickListener { copyNetworkPasswordToClipboard() }
        //    binding.ivOpenApp.setOnClickListener { openApp() }
        //   binding.ivOpenPlaystore.setOnClickListener { openInAppMarket() }
        //   binding.ivOpenYoutube.setOnClickListener { openInYoutube() }
        //   binding.ivOpenLink.setOnClickListener { openLink() }
        //  binding.ivCallPhone.setOnClickListener { dialPhone(barcode.phone) }
        //  binding.ivSendSms.setOnClickListener { sendSms(barcode.phone) }
        //  binding.ivSendEmail.setOnClickListener { sendEmail(barcode.email) }
        binding.liShare.setOnClickListener { shareCode() }
        binding.ivCopy.setOnClickListener { copyBarcodeTextToClipboard() }
        //  binding.ivSearch.setOnClickListener { searchBarcodeTextOnInternet() }
        binding.liSave.setOnClickListener {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                requestPermissions()
            } else {
                saveImage()
            }
        }
    }

    private fun toggleIsFavorite() {
        try {
            val newBarcode = originalCode.copy(isFavorite = barcode.isFavorite.not())
            barcodeDatabase.insertCode(newBarcode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        barcode.isFavorite = newBarcode.isFavorite
                        showIsFavorite(newBarcode.isFavorite)
                    }, {}
                )
                .addTo(compositeDisposable)
        } catch (e: java.lang.Exception) {
            Log.e("TAG", "toggleIsFavorite: ${e.message}")
        }
    }

    private fun updateBarcodeName(name: String) {
        if (name.isBlank()) {
            return
        }

        val newBarcode = originalCode.copy(
            id = barcode.id,
            name = name
        )

        barcodeDatabase.insertCode(newBarcode)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    barcode.name = name
                    showBarcodeName(name)
                },
                ::showError
            )
            .addTo(compositeDisposable)
    }

    fun drawableToBitmap(drawable: Drawable): Bitmap? {
        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }
        var width = drawable.intrinsicWidth
        width = if (width > 0) width else 1
        var height = drawable.intrinsicHeight
        height = if (height > 0) height else 1
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight())
        drawable.draw(canvas)
        return bitmap
    }

    private fun saveAppIcon() {

        val bitmap = ContextCompat.getDrawable(this, R.mipmap.ic_launcher)
            ?.let { drawableToBitmap(it) }

        val root: String =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString()
        val myDir = File(
            root,
            "YourDirectoryName"
        ) // Replace "YourDirectoryName" with your desired directory name


        if (!myDir.exists()) {
            myDir.mkdirs()
        }

        val fileName = "your_image_name.jpg" // Replace with your desired file name

        val file = File(myDir, fileName)

        try {
            val outputStream = FileOutputStream(file)
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            Log.e("TAG", "saveAppIcon: ${e.message}")
        }
    }

    private fun saveImage() {

        val saveFunc = BarcodeGeneratorImage
            .generateBitmapAsync(originalCode, 640, 640, 2)
            .flatMapCompletable {
                DBImageSaver.savePngImageToPublicDirectory(
                    this,
                    it,
                    originalCode
                )
            }

        saveFunc
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    Toast.makeText(
                        this,
                        R.string.file_name_saved,
                        Toast.LENGTH_LONG
                    ).show()
                },
                { error ->
                    showLoading(false)
                    showError(error)
                }
            )
            .addTo(compositeDisposable)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (MainPermissionsHelper.areAllPermissionsGranted(grantResults)) {
            saveImage()
        }
    }

    private fun deleteBarcode() {
        showLoading(true)

        barcodeDatabase.deleteCode(barcode.id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { finish() },
                { error ->
                    showLoading(false)
                    showError(error)
                }
            )
            .addTo(compositeDisposable)
    }

    private fun addToCalendar() {
        val intent = Intent(Intent.ACTION_INSERT).apply {
            data = CalendarContract.Events.CONTENT_URI
            putExtra(CalendarContract.Events.TITLE, barcode.eventSummary)
            putExtra(CalendarContract.Events.DESCRIPTION, barcode.eventDescription)
            putExtra(CalendarContract.Events.EVENT_LOCATION, barcode.eventLocation)
            putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, barcode.eventStartDate)
            putExtra(CalendarContract.EXTRA_EVENT_END_TIME, barcode.eventEndDate)
        }
        startActivityIfExists(intent)
    }

    private fun addToContacts() {
        val intent = Intent(ContactsContract.Intents.Insert.ACTION).apply {
            type = ContactsContract.Contacts.CONTENT_TYPE

            val fullName = "${barcode.firstName.orEmpty()} ${barcode.lastName.orEmpty()}"
            putExtra(ContactsContract.Intents.Insert.NAME, fullName)
            putExtra(ContactsContract.Intents.Insert.COMPANY, barcode.organization.orEmpty())
            putExtra(ContactsContract.Intents.Insert.JOB_TITLE, barcode.jobTitle.orEmpty())

            putExtra(ContactsContract.Intents.Insert.PHONE, barcode.phone.orEmpty())
            putExtra(
                ContactsContract.Intents.Insert.PHONE_TYPE,
                barcode.phoneType.orEmpty().toPhoneType()
            )

            putExtra(
                ContactsContract.Intents.Insert.SECONDARY_PHONE,
                barcode.secondaryPhone.orEmpty()
            )
            putExtra(
                ContactsContract.Intents.Insert.SECONDARY_PHONE_TYPE,
                barcode.secondaryPhoneType.orEmpty().toPhoneType()
            )

            putExtra(
                ContactsContract.Intents.Insert.TERTIARY_PHONE,
                barcode.tertiaryPhone.orEmpty()
            )
            putExtra(
                ContactsContract.Intents.Insert.TERTIARY_PHONE_TYPE,
                barcode.tertiaryPhoneType.orEmpty().toPhoneType()
            )

            putExtra(ContactsContract.Intents.Insert.EMAIL, barcode.email.orEmpty())
            putExtra(
                ContactsContract.Intents.Insert.EMAIL_TYPE,
                barcode.emailType.orEmpty().toEmailType()
            )

            putExtra(
                ContactsContract.Intents.Insert.SECONDARY_EMAIL,
                barcode.secondaryEmail.orEmpty()
            )
            putExtra(
                ContactsContract.Intents.Insert.SECONDARY_EMAIL_TYPE,
                barcode.secondaryEmailType.orEmpty().toEmailType()
            )

            putExtra(
                ContactsContract.Intents.Insert.TERTIARY_EMAIL,
                barcode.tertiaryEmail.orEmpty()
            )
            putExtra(
                ContactsContract.Intents.Insert.TERTIARY_EMAIL_TYPE,
                barcode.tertiaryEmailType.orEmpty().toEmailType()
            )

            putExtra(ContactsContract.Intents.Insert.NOTES, barcode.note.orEmpty())
        }
        startActivityIfExists(intent)
    }

    private fun dialPhone(phone: String?) {
        try {
            val callIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${phone.orEmpty()}"))
            startActivity(callIntent)
        } catch (e: java.lang.Exception) {
            showToast(R.string.code_no_app)
        }
    }

    private fun sendSms(phone: String?) {
        try {
            val uri = Uri.parse("sms:${phone.orEmpty()}")
            val smsIntent = Intent(Intent.ACTION_SENDTO, uri).apply {
                putExtra("sms_body", barcode.smsBody.orEmpty())
            }
            startActivity(smsIntent)
        } catch (e: java.lang.Exception) {
            showToast(R.string.code_no_app)
        }
    }

    private fun sendEmail(email: String?) {
        try {
            val uri = Uri.parse("mailto:${email.orEmpty()}")
            val emailIntent = Intent(Intent.ACTION_SEND, uri).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_EMAIL, arrayOf(email.orEmpty()))
                putExtra(Intent.EXTRA_SUBJECT, barcode.emailSubject.orEmpty())
                putExtra(Intent.EXTRA_TEXT, barcode.emailBody.orEmpty())
            }
            startActivity(emailIntent)
        } catch (e: java.lang.Exception) {
            showToast(R.string.code_no_app)
        }
    }

    private fun showLocation() {
        startActivityIfExists(Intent.ACTION_VIEW, barcode.geoUri.orEmpty())
    }

    private fun connectToWifi() {
        showConnectToWifiButtonEnabled(false)

        ConnectorWifi.connect(
            this,
            barcode.networkAuthType.orEmpty(),
            barcode.networkName.orEmpty(),
            barcode.networkPassword.orEmpty(),
            barcode.isHidden.orFalse(),
            barcode.anonymousIdentity.orEmpty(),
            barcode.identity.orEmpty(),
            barcode.eapMethod.orEmpty(),
            barcode.phase2Method.orEmpty()
        )
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    showConnectToWifiButtonEnabled(true)
                    showToast(R.string.connecting_to_wifi)
                },
                { error ->
                    showConnectToWifiButtonEnabled(true)
                    showError(error)
                }
            )
            .addTo(compositeDisposable)
    }

    private fun openWifiSettings() {
        val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
        startActivityIfExists(intent)
    }

    private fun copyNetworkPasswordToClipboard() {
        copyToClipboard(barcode.networkPassword.orEmpty())
        showToast(R.string.code_copied)
    }

    private fun openApp() {
        val intent = packageManager?.getLaunchIntentForPackage(barcode.appPackage.orEmpty())
        if (intent != null) {
            startActivityIfExists(intent)
        }
    }

    private fun openInAppMarket() {
        startActivityIfExists(Intent.ACTION_VIEW, barcode.appMarketUrl.orEmpty())
    }

    private fun openInYoutube() {
        startActivityIfExists(Intent.ACTION_VIEW, barcode.youtubeUrl.orEmpty())
    }

    private fun openOtpInOtherApp() {
        startActivityIfExists(Intent.ACTION_VIEW, barcode.otpUrl.orEmpty())
    }

    private fun openBitcoinUrl() {
        startActivityIfExists(Intent.ACTION_VIEW, barcode.bitcoinUri.orEmpty())
    }

    private fun openLink() {
        startActivityIfExists(Intent.ACTION_VIEW, barcode.url.orEmpty())
    }

    private fun saveBookmark() {
        val intent = Intent(Intent.ACTION_INSERT, Uri.parse("content://browser/bookmarks")).apply {
            putExtra("title", barcode.bookmarkTitle.orEmpty())
            putExtra("url", barcode.url.orEmpty())
        }
        startActivityIfExists(intent)
    }

    private fun shareCode() {
        if (isCreated) {
            shareBarcodeAsImage()
        } else {
            shareBarcodeAsText()
        }
    }

    private fun shareBarcodeAsText() {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            Log.d("kjhkjsfsf89fsidjfls", "here is text -> ${barcode.text}")
            putExtra(Intent.EXTRA_TEXT, barcode.text)
        }
        startActivityIfExists(intent)
    }

    private fun copyBarcodeTextToClipboard() {
        copyToClipboard(barcode.text)
        showToast(R.string.code_copied)
    }

    private fun searchBarcodeTextOnInternet() {
        val searchEngine = mainSettings.searchEngine
   //     when (searchEngine) {
         /*   SearchEngineMain.NONE -> performWebSearch()
            SearchEngineMain.ASK_EVERY_TIME -> showSearchEnginesDialog()*/
            /*else ->*/
    /*        performWebSearchUsingSearchEngine(searchEngine)*/
    //    }
    }

    private fun performWebSearch() {
        val intent = Intent(Intent.ACTION_WEB_SEARCH).apply {
            putExtra(SearchManager.QUERY, barcode.text)
        }
        startActivityIfExists(intent)
    }

    private fun performWebSearchUsingSearchEngine(searchEngine: SearchEngineMain) {
        val url = searchEngine.templateUrl + barcode.text
        startActivityIfExists(Intent.ACTION_VIEW, url)
    }

    private fun shareBarcodeAsImage() {
        val imageUri = try {
            val image = BarcodeGeneratorImage.generateBitmap(originalCode, 200, 200, 1)
            DBImageSaver.saveImageToCache(this, image, barcode)
        } catch (ex: Exception) {
            // MainLogger.log(ex)
            showError(ex)
            return
        }

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, imageUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        startActivityIfExists(intent)
    }

    private fun printBarcode() {
        try {
            primaryBaseActivity?.let {
                val barcodeImage = BarcodeGeneratorImage.generateBitmap(originalCode, 1000, 1000, 3)
                PrintHelper(it).apply {
                    scaleMode = PrintHelper.SCALE_MODE_FIT
                    printBitmap("${barcode.format}_${barcode.schema}_${barcode.date}", barcodeImage)
                }
            }
        } catch (ex: Exception) {
            // MainLogger.log(ex)
            showError(ex)
        } catch (exception: java.lang.IllegalStateException) {
            //   MainLogger.log(exception)
        }
    }

    private fun showIsFavorite(isFavorite: Boolean) {
        val iconId = if (isFavorite) {
            R.drawable.ic_favorite_qr_checked
        } else {
            R.drawable.ic_favorite_qr_unchecked
        }
        binding.ivFavoriteQr?.setImageDrawable(ContextCompat.getDrawable(this, iconId))
    }

    private fun showBarcodeName(name: String?) {
        binding.tvQrName.isVisible = name.isNullOrBlank().not()
        binding.tvQrName.text = name.orEmpty()
    }

    private fun setupActionButtons() {
        //binding.ivSearch.isVisible = true
//      button_edit_name.isVisible = barcode.isInDb

        if (isCreated) {
            return
        }

//        binding.ivSearch.isVisible = true
//        binding.ivAddToCalendar.isVisible = barcode.schema == BarcodeSchema.VEVENT
//        binding.ivAddToContacts.isVisible = barcode.schema == BarcodeSchema.VCARD || barcode.schema == BarcodeSchema.MECARD
//        binding.ivCallPhone.isVisible = barcode.phone.isNullOrEmpty().not()
//        binding.ivSendSms.isVisible = barcode.phone.isNullOrEmpty().not() || barcode.smsBody.isNullOrEmpty().not()
//        binding.ivSendEmail.isVisible = barcode.email.isNullOrEmpty().not() || barcode.emailSubject.isNullOrEmpty().not() || barcode.emailBody.isNullOrEmpty().not()
//        binding.ivShowLocation.isVisible = barcode.geoUri.isNullOrEmpty().not()
//        binding.ivConnectToWifi.isVisible = barcode.schema == BarcodeSchema.WIFI
//        binding.ivCopyPassword.isVisible = barcode.networkPassword.isNullOrEmpty().not()
//        binding.ivOpenPlaystore.isVisible = barcode.appMarketUrl.isNullOrEmpty().not()
//        binding.ivOpenYoutube.isVisible = barcode.youtubeUrl.isNullOrEmpty().not()
    }

    private fun showConnectToWifiButtonEnabled(isEnabled: Boolean) {
        //    binding.ivConnectToWifi.isEnabled = isEnabled
    }

    private fun showDeleteBarcodeConfirmationDialog() {
        /*val dialog =
            DeleteConfirmationDialogFragment.newInstance(R.string.dialog_delete_barcode_message)
        dialog.show(supportFragmentManager, "")*/
        ShowMainDeleteHistoryDialog(this, false, onDelete = {
            if (it)
                deleteBarcode()
        })
    }


    private fun showLoading(isLoading: Boolean) {
        binding.pbLoading.isVisible = isLoading
        binding.scrollView.isVisible = isLoading.not()
    }

    private fun startActivityIfExists(action: String, uri: String) {
        val intent = Intent(action, Uri.parse(uri))
        startActivityIfExists(intent)
    }

    private fun startActivityIfExists(intent: Intent) {
        intent.apply {
            flags = flags or Intent.FLAG_ACTIVITY_NEW_TASK
        }

        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            showToast(R.string.code_no_app)
        }
    }

    private fun copyToClipboard(text: String) {
        val clipData = ClipData.newPlainText("", text)
        clipboardManager.setPrimaryClip(clipData)
    }

    private fun showToast(stringId: Int) {
        Toast.makeText(this, stringId, Toast.LENGTH_SHORT).show()
    }
}
