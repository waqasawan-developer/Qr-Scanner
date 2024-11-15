package com.utilitytoolbox.qrscanner.barcodescanner.userInterface.activities

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.CalendarContract
import android.provider.ContactsContract
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.print.PrintHelper
import androidx.recyclerview.widget.GridLayoutManager
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
import com.utilitytoolbox.qrscanner.barcodescanner.utils.createFile
import com.utilitytoolbox.qrscanner.barcodescanner.utils.orFalse
import com.utilitytoolbox.qrscanner.barcodescanner.utils.showError
import com.utilitytoolbox.qrscanner.barcodescanner.utils.toEmailType
import com.utilitytoolbox.qrscanner.barcodescanner.utils.toPhoneType
import com.utilitytoolbox.qrscanner.barcodescanner.utils.toStringId
import com.utilitytoolbox.qrscanner.barcodescanner.utils.unsafeLazy
import com.google.zxing.BarcodeFormat
import com.utilitytoolbox.qrscanner.barcodescanner.R
import com.utilitytoolbox.qrscanner.barcodescanner.adapter.MyRcAdapter
import com.utilitytoolbox.qrscanner.barcodescanner.adapter.TypeOfResult
import com.utilitytoolbox.qrscanner.barcodescanner.databinding
.ActivityResultScanedBinding
import com.utilitytoolbox.qrscanner.barcodescanner.dialogs.ConfirmationDeleteFragDialog
import com.utilitytoolbox.qrscanner.barcodescanner.model.MineBarCode
import com.utilitytoolbox.qrscanner.barcodescanner.model
.SearchEngineMain
import com.utilitytoolbox.qrscanner.barcodescanner.model.schema
.BarcodeSchema
import com.utilitytoolbox.qrscanner.barcodescanner.usecase
.SettingsMainNew
import com.utilitytoolbox.qrscanner.barcodescanner.userInterface.activities.fragments.barcode.AztecCreateFrag
import com.utilitytoolbox.qrscanner.barcodescanner.userInterface.activities.fragments.barcode.CodabarCreateFrag
import com.utilitytoolbox.qrscanner.barcodescanner.userInterface.activities.fragments.barcode.Code128CreateFrag
import com.utilitytoolbox.qrscanner.barcodescanner.userInterface.activities.fragments.barcode.Code39CreateFrag
import com.utilitytoolbox.qrscanner.barcodescanner.userInterface.activities.fragments.barcode.Code93CreateFrag
import com.utilitytoolbox.qrscanner.barcodescanner.userInterface.activities.fragments.barcode.DataMatrixCreateFrag
import com.utilitytoolbox.qrscanner.barcodescanner.userInterface.activities.fragments.barcode.Ean13CreateFrag
import com.utilitytoolbox.qrscanner.barcodescanner.userInterface.activities.fragments.barcode.Ean8CreateFrag
import com.utilitytoolbox.qrscanner.barcodescanner.userInterface.activities.fragments.barcode.Itf14CreateFrag
import com.utilitytoolbox.qrscanner.barcodescanner.userInterface.activities.fragments.barcode.Pdf417CreateFrag
import com.utilitytoolbox.qrscanner.barcodescanner.userInterface.activities.fragments.barcode.UpcACreateFrag
import com.utilitytoolbox.qrscanner.barcodescanner.userInterface.activities.fragments.barcode.UpcECreateFrag
import com.utilitytoolbox.qrscanner.barcodescanner.userInterface.activities.fragments.qrcode.AppQrCodeCreateFrag
import com.utilitytoolbox.qrscanner.barcodescanner.userInterface.activities.fragments.qrcode.EmailQrCodeCreateFrag
import com.utilitytoolbox.qrscanner.barcodescanner.userInterface.activities.fragments.qrcode.QrCodeLocationCreateFrag
import com.utilitytoolbox.qrscanner.barcodescanner.userInterface.activities.fragments.qrcode.MeCardQrCodeCreateFrag
import com.utilitytoolbox.qrscanner.barcodescanner.userInterface.activities.fragments.qrcode.PhoneQrCodeCreateFrag
import com.utilitytoolbox.qrscanner.barcodescanner.userInterface.activities.fragments.qrcode.SmsQrCodeCreateFrag
import com.utilitytoolbox.qrscanner.barcodescanner.userInterface.activities.fragments.qrcode.TextQrCodeCreateFrag
import com.utilitytoolbox.qrscanner.barcodescanner.userInterface.activities.fragments.qrcode.UrlQrCodeCreateFrag
import com.utilitytoolbox.qrscanner.barcodescanner.userInterface.activities.fragments.qrcode.WifiQrCodeCreateFrag
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.net.URI
import java.net.URISyntaxException
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class QRResultActivity : AppCompatActivity(), ConfirmationDeleteFragDialog.Listener {
    private lateinit var binding: ActivityResultScanedBinding
    private val ivOpen = 0
    private val ivSave = 2
    private val ivShare = 3
    private val ivAddToCalendar = 4
    private val ivAddToContacts = 5
    private val ivCallPhone = 6
    private val ivSendSms = 7
    private val ivSendEmail = 8
    private val ivShowLocation = 9
    private val ivConnectToWifi = 10
    private val ivCopyPassword = 11
    private val ivOpenPlaystore = 12
    private val ivOpenYoutube = 13
    lateinit var mOptionSubMenuDialog: PopupWindow
    lateinit var mOptionMenuDialog: PopupWindow
    private var primaryBaseActivity: Context? = null
    var type = ""
    var myDir: File? = null

    @Inject
    lateinit var SettingsMainNew: SettingsMainNew

    @Inject
    lateinit var barcodeDatabase: BarcodeDatabase

    /* override fun attachBaseContext(base: Context) {
         super.attachBaseContext(AppLevel.localeManager.setLocale(base))
         primaryBaseActivity = base
     }*/

    companion object {
        private const val REQUEST_PERMISSIONS_CODE = 101
        private val PERMISSIONS = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)

        private const val QRCODE_KEY = "QRCODE_KEY"
        private const val IS_CREATED_QR = "IS_CREATED_QR"

        fun start(context: Context, mMineBarCode: MineBarCode, isCreatedQr: Boolean = false) {
            val intent = Intent(context, QRResultActivity::class.java).apply {
                putExtra(QRCODE_KEY, mMineBarCode)
                putExtra(IS_CREATED_QR, isCreatedQr)
//                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
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
        binding = ActivityResultScanedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        applyUIChanges()
        onClickListeners()
        showQrDetails()
        setupActionButtons()

        CoroutineScope(Dispatchers.IO).launch {
            val root = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS
            ).toString()
            myDir = File("$root/QRScannerBYWAQAS")
        }
    }

    private fun applyUIChanges() {
        changeStatusBarClr(isTransparent = false)
        binding.rootView.applySystemWindowInsets(applyTop = true, applyBottom = true)

        if (SettingsMainNew.copyToClipboard) {
            copyToClipboard(barcode.text)
        }

        if (SettingsMainNew.openLinksAutomatically.not() || isCreated) {
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
            binding.ivQrcode.setBackgroundColor(SettingsMainNew.codeBackgroundColor)
            binding.flQrImageBg.setBackgroundColor(SettingsMainNew.codeBackgroundColor)
            if (SettingsMainNew.isDarkThemeEnabled.not()) {
                binding.flQrImageBg.setPadding(0, 0, 0, 0)
            }
        } catch (ex: Exception) {
            // MainLogger.log(ex)
            binding.ivQrcode.isVisible = false
        }

        binding.tvDate.text = dateTimeFormatter.format(barcode.date)

        val format = barcode.format.toStringId()
        binding.itemTxt.setText(format)

        binding.tvQrName.isVisible = true
        binding.tvQrName.setText(barcode.format.toStringId())
        binding.tvQrText.text = if (isCreated) {
            barcode.text
        } else {
          /*  Log.d("ksjfs8sfjshf", " firstName -> ${barcode.firstName} \n")
            Log.d("ksjfs8sfjshf", " lastName ->  ${barcode.lastName} \n")
            Log.d("ksjfs8sfjshf", " phone ->  ${barcode.phone} \n")
            Log.d("ksjfs8sfjshf", " smsBody ->  ${barcode.smsBody} \n")
            Log.d("ksjfs8sfjshf", " address ->  ${barcode.address} \n")
            Log.d("ksjfs8sfjshf", " anonymousIdentity ->  ${barcode.anonymousIdentity} \n")
            Log.d("ksjfs8sfjshf", " appMarketUrl ->  ${barcode.appMarketUrl} \n")
            Log.d("ksjfs8sfjshf", " appPackage ->  ${barcode.appPackage} \n")
            Log.d("ksjfs8sfjshf", " date ->  ${barcode.date} \n")
            Log.d("ksjfs8sfjshf", " email ->  ${barcode.email} \n")
            Log.d("ksjfs8sfjshf", " bitcoinUri ->  ${barcode.bitcoinUri} \n")
            Log.d("ksjfs8sfjshf", " bookmarkTitle ->  ${barcode.bookmarkTitle} \n")
            Log.d("ksjfs8sfjshf", " eapMethod ->  ${barcode.eapMethod} \n")
            Log.d("ksjfs8sfjshf", " eventEndDate ->  ${barcode.eventEndDate} \n")
            Log.d("ksjfs8sfjshf", " emailBody ->  ${barcode.emailBody} \n")
            Log.d("ksjfs8sfjshf", " emailSubject ->  ${barcode.emailSubject} \n")
            Log.d("ksjfs8sfjshf", " emailType ->   ${barcode.emailType} \n")
            Log.d("ksjfs8sfjshf", " secondaryEmail ->  ${barcode.secondaryEmail} \n")
            Log.d("ksjfs8sfjshf", " geoUri ->  ${barcode.geoUri} \n")
            Log.d("ksjfs8sfjshf", " networkPassword ->  ${barcode.networkPassword} \n")
            Log.d("ksjfs8sfjshf", " format ->  ${barcode.format} \n")
            Log.d("ksjfs8sfjshf", " formattedText ->  ${barcode.formattedText} \n")
            Log.d("ksjfs8sfjshf", " name ->  ${barcode.name} \n")
            Log.d("ksjfs8sfjshf", " url ->  ${barcode.url} \n")
            Log.d("ksjfs8sfjshf", " youtubeUrl ->  ${barcode.youtubeUrl} \n")
            Log.d("ksjfs8sfjshf", " text ->  ${barcode.text} \n")
            Log.d("ksjfs8sfjshf", " text ->  ${barcode.schema} \n")*/

            barcode.formattedText
        }

        if (barcode.url != null || barcode.youtubeUrl != null) {
            binding.ivType.setImageResource(R.drawable.url)
            type = "URL"
        } else if (barcode.phone != null) {
            binding.ivType.setImageResource(R.drawable.phone)
            type = "Phone"
        } else {
            binding.ivType.setImageResource(R.drawable.ic_text_new)
            type = "Text"
        }



    }

    override fun onDeleteConfirmed() {
        deleteBarcode()
    }


    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent: Intent = Intent(
            this,
            MainActivityNew::class.java
        )
        //intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        startActivity(intent)
        finishAffinity()
    }

    private fun onClickListeners() {

        binding.ivDelete.setOnClickListener {
            showDeleteBarcodeConfirmationDialog()
        }

        binding.ivSave.setOnClickListener {
            toggleIsFavorite()
        }

        binding.ivCopy.setOnClickListener { copyBarcodeTextToClipboard() }
        binding.backBtn.setOnClickListener {
            val intent: Intent = Intent(
                this,
                MainActivityNew::class.java
            )
            //intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivity(intent)
            finishAffinity()
        }

        binding.ivOptions.setOnClickListener(View.OnClickListener { view: View? ->
            mOptionMenuDialog = optionsMenu()
            if (!mOptionMenuDialog.isShowing()) {
                mOptionMenuDialog.setOutsideTouchable(true)
                mOptionMenuDialog.setFocusable(true)
                mOptionMenuDialog.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                //                mOptionMenuDialog.showAsDropDown(view, -100, 0);
                var pointX = getResources().displayMetrics.density.toInt()
                /*    if (TinyDB.getInstance(this).getString("dl_language").equalsIgnoreCase("ar")) {
                        pointX = -100
                    }*/
                mOptionMenuDialog.showAsDropDown(view, pointX - 200, 0)
            } else {
                if (mOptionMenuDialog.isShowing()) {
                    mOptionMenuDialog.dismiss()
                }
                //  mOptionMenuDialog = null
            }
        })
        /* binding.ivAddToCalendar.setOnClickListener { addToCalendar() }
      binding.ivAddToContacts.setOnClickListener { addToContacts() }
      binding.ivShowLocation.setOnClickListener { showLocation() }
      binding.ivConnectToWifi.setOnClickListener { connectToWifi() }
      binding.ivOpenWifiSettings.setOnClickListener { openWifiSettings() }
      binding.ivCopyPassword.setOnClickListener { copyNetworkPasswordToClipboard() }
      binding.ivOpenApp.setOnClickListener { openApp() }
      binding.ivOpenPlaystore.setOnClickListener { openInAppMarket() }
      binding.ivOpenYoutube.setOnClickListener { openInYoutube() }
      binding.ivOpenLink.setOnClickListener { openLink() }
      binding.ivCallPhone.setOnClickListener { dialPhone(barcode.phone) }
      binding.ivSendSms.setOnClickListener { sendSms(barcode.phone) }
      binding.ivSendEmail.setOnClickListener { sendEmail(barcode.email) }*/

        /* binding.ivSave.setOnClickListener {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                requestPermissions()
            } else {
                saveImage()
            }
         }*/
    }

    private fun optionsMenu(): PopupWindow {
        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = inflater.inflate(R.layout.layout_options_sub_menu_main, null)
        val editContent = view.findViewById<LinearLayout>(R.id.liEdit)
        val exportCSV = view.findViewById<LinearLayout>(R.id.liCSV)
        val exportTXT = view.findViewById<LinearLayout>(R.id.liText)
        /*  if (TinyDB.getInstance(this).getBoolean(Constants.SETTING_HISTORY)) {
              editContent.visibility = View.VISIBLE
          } else {
              editContent.visibility = View.GONE
          }*/

        val selectedColor: Int =
            Color.parseColor("#1362BE")//TinyDB.getInstance(this).getInt(Constants.SELECTED_COLOR_SCHEME)

        exportCSV.setOnClickListener { view ->
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    mOptionSubMenuDialog = optionsSubMenu(true, false)
                    if (!mOptionSubMenuDialog.isShowing()) {
                        mOptionSubMenuDialog.setOutsideTouchable(true)
                        mOptionSubMenuDialog.setFocusable(true)
                        mOptionSubMenuDialog.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                        mOptionSubMenuDialog.showAsDropDown(view, -125, -20)
                    } else {
                        if (mOptionSubMenuDialog.isShowing()) {
                            mOptionSubMenuDialog.dismiss()
                        }
                        // mOptionSubMenuDialog = null
                    }
                }
            } catch (e: java.lang.Exception) {
                Log.e("TAG", "exportCSV:" + e.message)
            }
        }

        exportTXT.setOnClickListener { view ->
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    mOptionSubMenuDialog = optionsSubMenu(true, true)
                    if (!mOptionSubMenuDialog.isShowing()) {
                        mOptionSubMenuDialog.setOutsideTouchable(true)
                        mOptionSubMenuDialog.setFocusable(true)
                        mOptionSubMenuDialog.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                        mOptionSubMenuDialog.showAsDropDown(view, -125, -20)
                    } else {
                        if (mOptionSubMenuDialog.isShowing()) {
                            mOptionSubMenuDialog.dismiss()
                        }
                    }
                }
            } catch (e: java.lang.Exception) {
                Log.e("TAG", "exportCSV:" + e.message)
            }
        }

        editContent.setOnClickListener {
            mOptionMenuDialog.dismiss()
            fragment()

        }

        return PopupWindow(
            view,
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            true
        )
    }

    private fun fragment() {
        var barcodeFormater = originalCode.format
        var schema1 = originalCode.schema

        val fragment = when {
            barcodeFormater == BarcodeFormat.QR_CODE && schema1 == BarcodeSchema.OTHER -> {

                binding.ivSchema.setImageResource(R.drawable.ic_text_c)
                val tintColor = ContextCompat.getColor(this, R.color.qrtextBg)
                binding.frameLayout11.background.setColorFilter(
                    tintColor,
                    PorterDuff.Mode.SRC_IN
                )

                TextQrCodeCreateFrag.newInstance(originalCode)
            }

            barcodeFormater == BarcodeFormat.QR_CODE && schema1 == BarcodeSchema.Clipboard -> {


                binding.ivSchema.setImageResource(R.drawable.ic_clipbord_c)
                val tintColor = ContextCompat.getColor(this, R.color.qrClipbordBg)
                binding.frameLayout11.background.setColorFilter(
                    tintColor,
                    PorterDuff.Mode.SRC_IN
                )

                TextQrCodeCreateFrag.newInstance(originalCode)
            }


            barcodeFormater == BarcodeFormat.QR_CODE && schema1 == BarcodeSchema.URL -> {
                binding.ivSchema.setImageResource(R.drawable.ic_url_c)
                val tintColor = ContextCompat.getColor(this, R.color.qrurlBg)
                binding.frameLayout11.background.setColorFilter(
                    tintColor,
                    PorterDuff.Mode.SRC_IN
                )
                UrlQrCodeCreateFrag.newInstance(originalCode)
            }

            barcodeFormater == BarcodeFormat.QR_CODE && schema1 == BarcodeSchema.PHONE -> {
                binding.ivSchema.setImageResource(R.drawable.ic_phone_c)
                val tintColor = ContextCompat.getColor(this, R.color.qrphoneBg)
                binding.frameLayout11.background.setColorFilter(
                    tintColor,
                    PorterDuff.Mode.SRC_IN
                )
                PhoneQrCodeCreateFrag.newInstance(originalCode)
            }

            barcodeFormater == BarcodeFormat.QR_CODE && schema1 == BarcodeSchema.WIFI -> {
                binding.ivSchema.setImageResource(R.drawable.ic_wifi_c)
                val tintColor = ContextCompat.getColor(this, R.color.qrwifiBg)
                binding.frameLayout11.background.setColorFilter(
                    tintColor,
                    PorterDuff.Mode.SRC_IN
                )

                WifiQrCodeCreateFrag.newInstance(originalCode)

            }

            barcodeFormater == BarcodeFormat.QR_CODE && schema1 == BarcodeSchema.EMAIL -> {
                binding.ivSchema.setImageResource(R.drawable.ic_email_c)
                val tintColor = ContextCompat.getColor(this, R.color.qremailBg)
                binding.frameLayout11.background.setColorFilter(
                    tintColor,
                    PorterDuff.Mode.SRC_IN
                )
                EmailQrCodeCreateFrag.newInstance(originalCode)
            }

            barcodeFormater == BarcodeFormat.QR_CODE && schema1 == BarcodeSchema.SMS -> {
                binding.ivSchema.setImageResource(R.drawable.ic_sms_c)
                val tintColor = ContextCompat.getColor(this, R.color.qrsmsBg)
                binding.frameLayout11.background.setColorFilter(
                    tintColor,
                    PorterDuff.Mode.SRC_IN
                )
                SmsQrCodeCreateFrag.newInstance(originalCode)
            }

            barcodeFormater == BarcodeFormat.QR_CODE && schema1 == BarcodeSchema.MMS -> {
                binding.ivSchema.setImageResource(R.drawable.ic_sms_c)
                val tintColor = ContextCompat.getColor(this, R.color.qrsmsBg)
                binding.frameLayout11.background.setColorFilter(
                    tintColor,
                    PorterDuff.Mode.SRC_IN
                )
                SmsQrCodeCreateFrag.newInstance(originalCode)
            }

            barcodeFormater == BarcodeFormat.QR_CODE && schema1 == BarcodeSchema.CRYPTOCURRENCY -> {
                SmsQrCodeCreateFrag.newInstance(originalCode)
            }

            barcodeFormater == BarcodeFormat.QR_CODE && schema1 == BarcodeSchema.GEO -> {
                binding.ivSchema.setImageResource(R.drawable.ic_location_c)
                val tintColor = ContextCompat.getColor(this, R.color.qrlocationBg)
                binding.frameLayout11.background.setColorFilter(
                    tintColor,
                    PorterDuff.Mode.SRC_IN
                )
                QrCodeLocationCreateFrag.newInstance(originalCode)
            }

            barcodeFormater == BarcodeFormat.QR_CODE && schema1 == BarcodeSchema.APP -> {
                AppQrCodeCreateFrag.newInstance(originalCode)
            }

            barcodeFormater == BarcodeFormat.QR_CODE && schema1 == BarcodeSchema.VCARD -> {
                binding.ivSchema.setImageResource(R.drawable.ic_contact_c)
                val tintColor = ContextCompat.getColor(this, R.color.qrcontactBg)
                binding.frameLayout11.background.setColorFilter(
                    tintColor,
                    PorterDuff.Mode.SRC_IN
                )
                MeCardQrCodeCreateFrag.newInstance(originalCode)
            }

            barcodeFormater == BarcodeFormat.QR_CODE && schema1 == BarcodeSchema.MECARD -> {
                binding.ivSchema.setImageResource(R.drawable.ic_contact_c)
                val tintColor = ContextCompat.getColor(this, R.color.qrcontactBg)
                binding.frameLayout11.background.setColorFilter(
                    tintColor,
                    PorterDuff.Mode.SRC_IN
                )

                MeCardQrCodeCreateFrag.newInstance(originalCode)
            }

            barcodeFormater == BarcodeFormat.DATA_MATRIX -> {
                binding.ivSchema.setImageResource(R.drawable.ic_barcode_matrix)
                val tintColor = ContextCompat.getColor(this, R.color.barcode_matrix)
                binding.frameLayout11.background.setColorFilter(
                    tintColor,
                    PorterDuff.Mode.SRC_IN
                )
                DataMatrixCreateFrag.newInstance(originalCode)
            }

            barcodeFormater == BarcodeFormat.AZTEC -> {
                binding.ivSchema.setImageResource(R.drawable.ic_barcode_aztec)
                val tintColor = ContextCompat.getColor(this, R.color.barcode_aztec)
                binding.frameLayout11.background.setColorFilter(
                    tintColor,
                    PorterDuff.Mode.SRC_IN
                )
                AztecCreateFrag.newInstance(originalCode)
            }

            barcodeFormater == BarcodeFormat.PDF_417 -> {
                binding.ivSchema.setImageResource(R.drawable.ic_barcode_pdf417)
                val tintColor = ContextCompat.getColor(this, R.color.barcode_pdf417)
                binding.frameLayout11.background.setColorFilter(
                    tintColor,
                    PorterDuff.Mode.SRC_IN
                )
                Pdf417CreateFrag.newInstance(originalCode)
            }

            barcodeFormater == BarcodeFormat.CODABAR -> {
                binding.ivSchema.setImageResource(R.drawable.ic_barcode_codebar)
                val tintColor = ContextCompat.getColor(this, R.color.barcode_codabar)
                binding.frameLayout11.background.setColorFilter(
                    tintColor,
                    PorterDuff.Mode.SRC_IN
                )
                CodabarCreateFrag.newInstance(originalCode)
            }

            barcodeFormater == BarcodeFormat.CODE_39 -> {
                binding.ivSchema.setImageResource(R.drawable.ic_barcode_code39)
                val tintColor = ContextCompat.getColor(this, R.color.barcode_code39)
                binding.frameLayout11.background.setColorFilter(
                    tintColor,
                    PorterDuff.Mode.SRC_IN
                )

                Code39CreateFrag.newInstance(originalCode)
            }

            barcodeFormater == BarcodeFormat.CODE_93 -> {
                binding.ivSchema.setImageResource(R.drawable.ic_barcode_ran13)
                val tintColor = ContextCompat.getColor(this, R.color.barcode_ean13)
                binding.frameLayout11.background.setColorFilter(
                    tintColor,
                    PorterDuff.Mode.SRC_IN
                )
                Code93CreateFrag.newInstance(originalCode)
            }

            barcodeFormater == BarcodeFormat.CODE_128 -> {
                binding.ivSchema.setImageResource(R.drawable.ic_barcode_code129)
                val tintColor = ContextCompat.getColor(this, R.color.barcode_code128)
                binding.frameLayout11.background.setColorFilter(
                    tintColor,
                    PorterDuff.Mode.SRC_IN
                )

                Code128CreateFrag.newInstance(originalCode)
            }

            barcodeFormater == BarcodeFormat.EAN_8 -> {
                binding.ivSchema.setImageResource(R.drawable.ic_barcode_ean8)
                val tintColor = ContextCompat.getColor(this, R.color.barcode_ean8)
                binding.frameLayout11.background.setColorFilter(
                    tintColor,
                    PorterDuff.Mode.SRC_IN
                )

                Ean8CreateFrag.newInstance(originalCode)
            }

            barcodeFormater == BarcodeFormat.EAN_13 -> {
                binding.ivSchema.setImageResource(R.drawable.ic_barcode_ran13)
                val tintColor = ContextCompat.getColor(this, R.color.barcode_ean13)
                binding.frameLayout11.background.setColorFilter(
                    tintColor,
                    PorterDuff.Mode.SRC_IN
                )

                Ean13CreateFrag.newInstance(originalCode)
            }

            barcodeFormater == BarcodeFormat.ITF -> {
                binding.ivSchema.setImageResource(R.drawable.ic_barcode_itf)
                val tintColor = ContextCompat.getColor(this, R.color.barcode_itf)
                binding.frameLayout11.background.setColorFilter(
                    tintColor,
                    PorterDuff.Mode.SRC_IN
                )

                Itf14CreateFrag.newInstance(originalCode)
            }

            barcodeFormater == BarcodeFormat.UPC_A -> {
                binding.ivSchema.setImageResource(R.drawable.ic_barcode_upc_a)
                val tintColor = ContextCompat.getColor(this, R.color.barcode_upca)
                binding.frameLayout11.background.setColorFilter(
                    tintColor,
                    PorterDuff.Mode.SRC_IN
                )

                UpcACreateFrag.newInstance(originalCode)
            }

            barcodeFormater == BarcodeFormat.UPC_E -> {
                binding.ivSchema.setImageResource(R.drawable.ic_barcode_upc_e)
                val tintColor = ContextCompat.getColor(this, R.color.barcode_upce)
                binding.frameLayout11.background.setColorFilter(
                    tintColor,
                    PorterDuff.Mode.SRC_IN
                )

                UpcECreateFrag.newInstance(originalCode)
            }

            else -> return
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.fl_fragment_container, fragment as Fragment)
            .commit()

        binding.clEdit.visibility = View.VISIBLE
        binding.clScanResult.visibility = View.GONE

    }

    private fun optionsSubMenu(
        isScan: Boolean,
        isTXTExport: Boolean
    ): PopupWindow {
        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.layout_options_sub_menu, null)
        val shareQRData = view.findViewById<FrameLayout>(R.id.shareQRData)
        val saveCSV = view.findViewById<FrameLayout>(R.id.saveCSV)

        shareQRData.setOnClickListener {
            try {
                if (mOptionSubMenuDialog != null) {
                    mOptionSubMenuDialog.dismiss()
                }
                mOptionMenuDialog.dismiss()

                var list: ArrayList<MainBarcodeParsedNew > = arrayListOf()
                list.add(barcode)
                createFile(
                    list, type,
                    isScan,
                    true,
                    isTXTExport
                )
            } catch (e: java.lang.Exception) {
                Log.e("TAG", "shareQRData:" + e.message)
            }
        }

        saveCSV.setOnClickListener {

            try {
                /*if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
                    requestStoragePermission(this@ResultQRActivity) { permissionGranted: Boolean ->
                        try {
                            if (permissionGranted) {
                                if (mOptionSubMenuDialog != null) {
                                    mOptionSubMenuDialog.dismiss()
                                    mOptionSubMenuDialog = null
                                }
                                mOptionMenuDialog.dismiss()
                                val result: Boolean = ExtFunctionsKt.createFile(
                                    this@GenerateResultActivity,
                                    historyModel,
                                    isScan,
                                    false,
                                    isTXTExport
                                )
                                if (result) {
                                    if (isTXTExport) {
                                        Toast.makeText(
                                            this@GenerateResultActivity,
                                            getString(R.string.txt_data_saved) + "in " + myDir.getAbsolutePath(),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        Toast.makeText(
                                            this@GenerateResultActivity,
                                            getString(R.string.csv_data_saved) + "in " + myDir.getAbsolutePath(),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                } else {
                                    Toast.makeText(
                                        this@GenerateResultActivity,
                                        "Something went wrong",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        } catch (e: java.lang.Exception) {
                            Log.e("TAG", "saveCSV:" + e.message)
                        }
                        null
                    }
                } else {*/
                if (mOptionSubMenuDialog != null) {
                    mOptionSubMenuDialog.dismiss()
                }

                mOptionMenuDialog.dismiss()

                var list: ArrayList<MainBarcodeParsedNew > = arrayListOf()
                list.add(barcode)
                val result: Boolean = createFile(
                    list, type,
                    isScan,
                    false,
                    isTXTExport
                )
                if (result) {
                    if (isTXTExport) {
                        Toast.makeText(
                            this,
                            getString(R.string.txt_data_saved) + "in " + myDir?.getAbsolutePath(),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            this,
                            getString(R.string.csv_data_saved) + "in " + myDir?.getAbsolutePath(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this,
                        "Something went wrong",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: java.lang.Exception) {
                Log.e("TAG", "saveCSV:" + e.message)
            }
            //  }
        }
        return PopupWindow(
            view,
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            true
        )
    }

    private fun toggleIsFavorite() {
        try {
            val newBarcode = originalCode.copy(isFavorite = barcode.isFavorite.not())
            Log.d("fsfusof8sf798sf9s", "$newBarcode")

            barcodeDatabase.insertCode(newBarcode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        barcode.isFavorite = newBarcode.isFavorite
                        showIsFavorite(newBarcode.isFavorite)
                        Log.d("fsfusof8sf798sf9s", "${newBarcode.isFavorite}")

                    }, {}
                )
                .addTo(compositeDisposable)
        } catch (e: java.lang.Exception) {
            Log.e("foidfoud0d8g0dg", "toggleIsFavorite: ${e.message}")
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
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                .toString()
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
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
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

    /*private fun addToContacts() {
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
    }*/

    private fun addToContacts() {
        // Create the Intent to add a new contact
        val intent = Intent(ContactsContract.Intents.Insert.ACTION).apply {
            type = ContactsContract.Contacts.CONTENT_TYPE

            // Construct the full name
            val fullName = "${barcode.firstName.orEmpty()} ${barcode.lastName.orEmpty()}"
            putExtra(ContactsContract.Intents.Insert.NAME, fullName)
            putExtra(ContactsContract.Intents.Insert.COMPANY, barcode.organization.orEmpty())
            putExtra(ContactsContract.Intents.Insert.JOB_TITLE, barcode.jobTitle.orEmpty())

            // Add phone numbers if they exist
            barcode.phone?.let {
                putExtra(ContactsContract.Intents.Insert.PHONE, it)
                putExtra(
                    ContactsContract.Intents.Insert.PHONE_TYPE,
                    barcode.phoneType.orEmpty().toPhoneType()
                )
            }

            barcode.secondaryPhone?.let {
                putExtra(ContactsContract.Intents.Insert.SECONDARY_PHONE, it)
                putExtra(
                    ContactsContract.Intents.Insert.SECONDARY_PHONE_TYPE,
                    barcode.secondaryPhoneType.orEmpty().toPhoneType()
                )
            }

            barcode.tertiaryPhone?.let {
                putExtra(ContactsContract.Intents.Insert.TERTIARY_PHONE, it)
                putExtra(
                    ContactsContract.Intents.Insert.TERTIARY_PHONE_TYPE,
                    barcode.tertiaryPhoneType.orEmpty().toPhoneType()
                )
            }

            // Add emails if they exist
            barcode.email?.let {
                putExtra(ContactsContract.Intents.Insert.EMAIL, it)
                putExtra(
                    ContactsContract.Intents.Insert.EMAIL_TYPE,
                    barcode.emailType.orEmpty().toEmailType()
                )
            }

            barcode.secondaryEmail?.let {
                putExtra(ContactsContract.Intents.Insert.SECONDARY_EMAIL, it)
                putExtra(
                    ContactsContract.Intents.Insert.SECONDARY_EMAIL_TYPE,
                    barcode.secondaryEmailType.orEmpty().toEmailType()
                )
            }

            barcode.tertiaryEmail?.let {
                putExtra(ContactsContract.Intents.Insert.TERTIARY_EMAIL, it)
                putExtra(
                    ContactsContract.Intents.Insert.TERTIARY_EMAIL_TYPE,
                    barcode.tertiaryEmailType.orEmpty().toEmailType()
                )
            }

            // Add notes if they exist
            putExtra(ContactsContract.Intents.Insert.NOTES, barcode.note.orEmpty())
        }

        // Check if there's an app that can handle the intent
        //if (intent.resolveActivity(packageManager) != null) {

        try {
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(
                this@QRResultActivity,
                "No App found For this Action to perform",
                Toast.LENGTH_SHORT
            ).show()
        }
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
        // showConnectToWifiButtonEnabled(false)

        ConnectorWifi
            .connect(
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
                    //  showConnectToWifiButtonEnabled(true)
                    showToast(R.string.connecting_to_wifi)
                },
                { error ->
                    //  showConnectToWifiButtonEnabled(true)
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
        val intent =
            Intent(Intent.ACTION_INSERT, Uri.parse("content://browser/bookmarks")).apply {
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

        val stringBuilder = StringBuilder()

        if (barcode.phone.isNullOrEmpty().not()) {
            Log.d("lkjdlksjlfs", "barcode.phone -> ${barcode.phone}\n")
            stringBuilder.append(barcode.phone + "\n")
        }

        if (barcode.smsBody.isNullOrEmpty().not()) {
            Log.d("lkjdlksjlfs", "barcode.smsBody -> ${barcode.smsBody}\n")
            stringBuilder.append(barcode.smsBody + "\n")
        }


        if (barcode.youtubeUrl.isNullOrEmpty().not()) {
            Log.d("lkjdlksjlfs", "barcode.youtubeUrl -> ${barcode.youtubeUrl}\n")
            stringBuilder.append(barcode.youtubeUrl + "\n")
        }


        if (barcode.appMarketUrl.isNullOrEmpty().not()) {
            Log.d("lkjdlksjlfs", "barcode.appMarketUrl -> ${barcode.appMarketUrl}\n")
            stringBuilder.append(barcode.appMarketUrl + "\n")
        }

        if (barcode.email.isNullOrEmpty().not()) {
            Log.d("lkjdlksjlfs", "barcode.email -> ${barcode.email}\n")
            stringBuilder.append(barcode.email + "\n")
        }

        if (barcode.emailSubject.isNullOrEmpty().not()) {
            Log.d("lkjdlksjlfs", "barcode.emailSubject -> ${barcode.emailSubject}\n")
            stringBuilder.append(barcode.emailSubject + "\n")
        }

        if (barcode.emailBody.isNullOrEmpty().not()) {
            Log.d("lkjdlksjlfs", "barcode.emailBody -> ${barcode.emailBody}\n")
            stringBuilder.append(barcode.emailBody + "\n")
        }

        if (barcode.geoUri.isNullOrEmpty().not()) {
            Log.d("lkjdlksjlfs", "barcode.geoUri -> ${barcode.geoUri}\n")
            stringBuilder.append(barcode.geoUri + "\n")
        }
        if (barcode.networkPassword.isNullOrEmpty().not()) {
            Log.d("lkjdlksjlfs", "barcode.networkPassword -> ${barcode.networkPassword}\n")
            stringBuilder.append(barcode.networkPassword + "\n")
        }
        if (barcode.address.isNullOrEmpty().not()) {
            Log.d("lkjdlksjlfs", "barcode.address -> ${barcode.address}\n")
            stringBuilder.append(barcode.address + "\n")
        }

        if (barcode.url.isNullOrEmpty().not()) {
            Log.d("lkjdlksjlfs", "barcode.url -> ${barcode.url}\n")
            stringBuilder.append(barcode.url + "\n")
        }


        val finalString = stringBuilder.toString()

        Log.d("lkjdlksjlfs", "sharing this -> ${finalString}")

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, finalString)
        }
        startActivityIfExists(intent)
    }

    private fun copyBarcodeTextToClipboard() {
        copyToClipboard(barcode.text)
        showToast(R.string.code_copied)
    }

    private fun searchBarcodeTextOnInternet() {
        openCustomTabView(
            this@QRResultActivity, barcode.text, SettingsMainNew.searchEngine
        )
    }


    fun validateEmail(context: Context, text: String): String? {
        val emailRegex = Regex("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}")
        val matchResult = emailRegex.find(text)

        if (matchResult != null) {
            return matchResult.value
        } else {
            Toast.makeText(context, "Invalid email address", Toast.LENGTH_SHORT).show()
            return null
        }
    }

    /*  fun openCustomTabView(context: Context, url: String) {
          try {
              val customTabsIntent = CustomTabsIntent.Builder().build()

              customTabsIntent.launchUrl(context, Uri.parse(url))
          } catch (e: Exception) {
              Log.e(
                  "TAG",
                  "openCustomTabView: ${e.message}"
              ) // Use string template for cleaner formatting
          }
      }*/

    fun openCustomTabView(context: Context, input: String, searchEngine: SearchEngineMain) {
        try {
            val uri = Uri.parse(input)
            if (isValidUrl(uri)) {
                // Launch URL in Custom Tabs
                val customTabsIntent = CustomTabsIntent.Builder()
                    .setToolbarColor(context.getColor(android.R.color.holo_blue_light)) // Example customization
                    .build()

                customTabsIntent.launchUrl(context, uri)
            } else {
                // Launch Google search query
                Log.d("jhjdfd7fdjfjh", "here is tempate-> ${searchEngine.templateUrl}")
                val searchUri = Uri.parse("${searchEngine.templateUrl}${Uri.encode(input)}")
                val intent = Intent(Intent.ACTION_VIEW, searchUri)
                context.startActivity(intent)
            }
        } catch (e: Exception) {
            Log.e(
                "CustomTabError",
                "Failed to open URL or perform search: ${e.localizedMessage}", // Improved error message
                e // Include the throwable in the log for stack trace information
            )
        }
    }

    private fun isValidUrl(uri: Uri): Boolean {
        return try {
            val url = URI(uri.toString())
            url.scheme == "http" || url.scheme == "https"
        } catch (e: URISyntaxException) {
            false
        }
    }

    private fun performWebSearchUsingSearchEngine() {
        val url = SettingsMainNew.searchEngine.templateUrl + barcode.text
        startActivityIfExists(Intent.ACTION_VIEW, url)
    }

    private fun shareBarcodeAsImage() {
        val imageUri = try {
            val image = BarcodeGeneratorImage.generateBitmap(originalCode, 200, 200, 1)
            DBImageSaver.saveImageToCache(this, image, barcode)
        } catch (ex: Exception) {
            //  MainLogger.log(ex)
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
                val barcodeImage =
                    BarcodeGeneratorImage.generateBitmap(originalCode, 1000, 1000, 3)
                PrintHelper(it).apply {
                    scaleMode = PrintHelper.SCALE_MODE_FIT
                    printBitmap(
                        "${barcode.format}_${barcode.schema}_${barcode.date}",
                        barcodeImage
                    )
                }
            }
        } catch (ex: Exception) {
            //   MainLogger.log(ex)
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
        binding.ivSave?.setImageDrawable(ContextCompat.getDrawable(this, iconId))
    }

    private fun showBarcodeName(name: String?) {
        binding.tvQrName.isVisible = name.isNullOrBlank().not()
        binding.tvQrName.text = name.orEmpty()
    }

    private fun setupActionButtons() {

        var list: ArrayList<TypeOfResult> = arrayListOf()





        if (barcode.schema == BarcodeSchema.VEVENT) {
            list.add(
                TypeOfResult(
                    getString(R.string.add_to_calendar),
                    R.drawable.calender,
                    ivAddToCalendar
                )
            )
        }

        if (barcode.schema == BarcodeSchema.VCARD || barcode.schema == BarcodeSchema.MECARD || barcode.phone.isNullOrEmpty()
                .not()
        ) {
            list.add(
                TypeOfResult(
                    getString(R.string.add_to_contacts),
                    R.drawable.contact,
                    ivAddToContacts
                )
            )
        }

        if (barcode.phone.isNullOrEmpty().not()) {
            list.add(TypeOfResult(getString(R.string.call_phone), R.drawable.phone, ivCallPhone))
        }

        if (barcode.phone.isNullOrEmpty().not() || barcode.smsBody.isNullOrEmpty().not()) {
            list.add(TypeOfResult(getString(R.string.send_sms), R.drawable.sms, ivSendSms))
        }

        if (barcode.email.isNullOrEmpty().not() || barcode.emailSubject.isNullOrEmpty()
                .not() || barcode.emailBody.isNullOrEmpty().not()
        ) {
            list.add(TypeOfResult(getString(R.string.send_email), R.drawable.email, ivSendEmail))
        }

        if (barcode.geoUri.isNullOrEmpty().not()) {
            list.add(
                TypeOfResult(
                    getString(R.string.show_location),
                    R.drawable.location,
                    ivShowLocation
                )
            )
        }

        if (barcode.schema == BarcodeSchema.WIFI) {
            list.add(
                TypeOfResult(
                    getString(R.string.connect_to_wifi),
                    R.drawable.wifi,
                    ivConnectToWifi
                )
            )
        }
        if (barcode.networkPassword.isNullOrEmpty().not()) {
            list.add(
                TypeOfResult(
                    getString(R.string.copy_network_password),
                    R.drawable.ic_clip_blue,
                    ivCopyPassword
                )
            )
        }
        if (barcode.appMarketUrl.isNullOrEmpty().not()) {
            list.add(
                TypeOfResult(
                    getString(R.string.open_in_play_store),
                    R.drawable.playstore,
                    ivOpenPlaystore
                )
            )
        }
        if (barcode.youtubeUrl.isNullOrEmpty().not()) {
            list.add(
                TypeOfResult(
                    getString(R.string.open_in_youtube),
                    R.drawable.youtube,
                    ivOpenYoutube
                )
            )
        }


        if (barcode.phone == null &&  barcode.schema != BarcodeSchema.WIFI) {
            list.add(TypeOfResult("Open", R.drawable.search, ivOpen))
        }

        list.add(TypeOfResult("Share", R.drawable.share, ivShare))

        list.add(TypeOfResult(getString(R.string.save_as_image), R.drawable.download, ivSave))

        Log.d("skjfsf8sfsf", "heren is data listt -> ${list.size}")
        binding.rcResultTypes.layoutManager = GridLayoutManager(this, 3) // 3 columns
        binding.rcResultTypes.adapter = MyRcAdapter(list) { result_type ->

            when (result_type.type) {

                ivShare -> {
                    shareCode()
                }

                ivSave -> {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                        requestPermissions()
                    } else {
                        saveImage()
                    }
                }

                ivOpen -> {
                    searchBarcodeTextOnInternet()
                }

                ivAddToCalendar -> {
                    addToCalendar()
                }

                ivAddToContacts -> {
                    addToContacts()
                }

                ivCallPhone -> {
                    dialPhone(barcode.phone)
                }

                ivSendSms -> {
                    sendSms(barcode.phone)
                }

                ivSendEmail -> {
                    sendEmail(barcode.email)
                }

                ivShowLocation -> {
                    showLocation()
                }

                ivConnectToWifi -> {
                    connectToWifi()
                }

                ivCopyPassword -> {
                    copyNetworkPasswordToClipboard()
                }

                ivOpenPlaystore -> {
                    openInAppMarket()
                }

                ivOpenYoutube -> {
                    openInYoutube()
                }
            }
        }
        binding.rcResultTypes.setHasFixedSize(true)


    }

    private fun showDeleteBarcodeConfirmationDialog() {
        /*val dialog =
            DeleteConfirmationDialogFragment.newInstance(R.string.dialog_delete_barcode_message)
        dialog.show(supportFragmentManager, "")*/
        /* ShowDeleteHistoryMainDialog(this, false, onDelete = {
             if (it)
                 deleteBarcode()
         })*/

        deleteBarcode()
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

        Log.d("sljfljskfs8sdfsj", "here is pacakge manager -> ${intent}")

        try {
            startActivity(intent)
        } catch (e: Exception) {
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
