package com.utilitytoolbox.qrscanner.barcodescanner.userInterface.activities

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.utilitytoolbox.qrscanner.barcodescanner.userInterface.activities.fragments.qrcode.AppQrCodeCreateFrag

import com.utilitytoolbox.qrscanner.barcodescanner.model.MineBarCode
import com.utilitytoolbox.qrscanner.barcodescanner.usecase.BarcodeDatabase
import com.utilitytoolbox.qrscanner.barcodescanner.usecase.ContactHelperNew
import com.utilitytoolbox.qrscanner.barcodescanner.usecase
.MainPermissionsHelper
import com.utilitytoolbox.qrscanner.barcodescanner.utils.applySystemWindowInsets
import com.utilitytoolbox.qrscanner.barcodescanner.utils.changeStatusBarClr
import com.utilitytoolbox.qrscanner.barcodescanner.utils.createOrCancelQr
import com.utilitytoolbox.qrscanner.barcodescanner.utils.showError
import com.utilitytoolbox.qrscanner.barcodescanner.utils.toStringId
import com.utilitytoolbox.qrscanner.barcodescanner.utils.unsafeLazy
import com.google.zxing.BarcodeFormat
import com.utilitytoolbox.qrscanner.barcodescanner.databinding.ActivityQrCreateBinding
import com.utilitytoolbox.qrscanner.barcodescanner.adapter.AppAdapter
import com.utilitytoolbox.qrscanner.barcodescanner.model.schema.MainApps
import com.utilitytoolbox.qrscanner.barcodescanner.model.schema
.BarcodeSchema
import com.utilitytoolbox.qrscanner.barcodescanner.model.schema
.Schema
import com.utilitytoolbox.qrscanner.barcodescanner.usecase
.BarcodeParserNew
import com.utilitytoolbox.qrscanner.barcodescanner.usecase
.SettingsMainNew
import com.utilitytoolbox.qrscanner.barcodescanner.usecase
.insertCode
import com.utilitytoolbox.qrscanner.barcodescanner.userInterface.activities.fragments.BaseFragment
import com.utilitytoolbox.qrscanner.barcodescanner.BaseActivity
import com.utilitytoolbox.qrscanner.barcodescanner.R
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
import javax.inject.Inject


@AndroidEntryPoint
class MainBarcodeCreateActivity : BaseActivity(), AppAdapter.Listener {
    @Inject
    lateinit var barcodeDatabase: BarcodeDatabase
    @Inject
    lateinit var mains: SettingsMainNew

    lateinit var binding: ActivityQrCreateBinding

    companion object {
        private const val BARCODE_FORMATER_KEYCONST = "BARCODE_FORMAT_KEY"
        private const val BARCODE_SCHEMA_KEYCONST = "BARCODE_SCHEMA_KEY"
        private const val DEFAULT_TEXT_KEYCONST = "DEFAULT_TEXT_KEY"

        private const val CHOOSE_PHONE_REQUEST_CODE_CAREFULLY = 1
        private const val CHOOSE_CONTACT_REQUEST_CODE_CAREFULLY = 2

        private const val CONTACTS_PERMISSION_REQUEST_CODE_CAREFULLY = 101
        private val CONTACTS_PERMISSIONS_CAREFULLY = arrayOf(Manifest.permission.READ_CONTACTS)

        fun startUp(
            context: Context,
            barcodeFormat: BarcodeFormat,
            barcodeSchema: BarcodeSchema? = null,
            defaultText: String? = null
        ) {
            val intent = Intent(context, MainBarcodeCreateActivity::class.java).apply {
                putExtra(BARCODE_FORMATER_KEYCONST, barcodeFormat.ordinal)
                putExtra(BARCODE_SCHEMA_KEYCONST, barcodeSchema?.ordinal ?: -1)
                putExtra(DEFAULT_TEXT_KEYCONST, defaultText)
            }
            context.startActivity(intent)
        }
    }

    private val disposableSpcificHmm = CompositeDisposable()

    private val barcodeFormater by unsafeLazy {
        BarcodeFormat.values().getOrNull(intent?.getIntExtra(BARCODE_FORMATER_KEYCONST, -1) ?: -1)
            ?: BarcodeFormat.QR_CODE
    }

    private val schema1 by unsafeLazy {
        BarcodeSchema.values().getOrNull(intent?.getIntExtra(BARCODE_SCHEMA_KEYCONST, -1) ?: -1)
    }

    private val text1 by unsafeLazy {
        intent?.getStringExtra(DEFAULT_TEXT_KEYCONST).orEmpty()
    }

    /*    var buttonEnabled: Boolean
            get() = false
            set(enabled) {
                val iconId = if (enabled) {
                    R.drawable.ic_done_enabled
                } else {
                    R.drawable.ic_done_disabled
                }

                */
    /*  binding.toolbar.menu?.findItem(R.id.item_create_qr_barcode)?.apply {
                  icon = ContextCompat.getDrawable(this@BarcodeCreateMainActivity, iconId)
                  isEnabled = enabled
              }*//*


        }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        changeStatusBarClr()

        if (createBarcodeImmediatelyIfNeeded()) {
            return
        }

        binding = ActivityQrCreateBinding.inflate(layoutInflater)
        setContentView(binding.root)
        edgeToEdge()
        onClickListeners()
        toolbarMenu()
        fragment()
        changeStatusBarClr(isTransparent = false)

        binding.ivBack.setOnClickListener { onBackPressed() }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) {
            return
        }

        when (requestCode) {
            CHOOSE_PHONE_REQUEST_CODE_CAREFULLY -> chosenPhone(data)
            CHOOSE_CONTACT_REQUEST_CODE_CAREFULLY -> contact(data)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == CONTACTS_PERMISSION_REQUEST_CODE_CAREFULLY && MainPermissionsHelper.areAllPermissionsGranted(
                grantResults
            )
        ) {
            chooseContact()
        }
    }

    override fun onAppClicked(packageName: String) {
        createBarcode1(MainApps.fromPackage(packageName))
    }

    override fun onDestroy() {
        super.onDestroy()
        disposableSpcificHmm.clear()
    }

    private fun edgeToEdge() {
        binding.rootView.applySystemWindowInsets(applyTop = true, applyBottom = true)
    }

    private fun createBarcodeImmediatelyIfNeeded(): Boolean {
        if (intent?.action != Intent.ACTION_SEND) {
            return false
        }

        return when (intent?.type) {
            "text/plain" -> {
                createBarcodeForPlainText()
                true
            }

            "text/x-vcard" -> {
                createBarcodeForVCard()
                true
            }

            else -> false
        }
    }

    private fun createBarcodeForPlainText() {
        val text = intent?.getStringExtra(Intent.EXTRA_TEXT).orEmpty()
        val schema = BarcodeParserNew.parseSchema(barcodeFormater, text)
        createBarcode1(schema, true)
    }

    private fun createBarcodeForVCard() {
        val uri = intent?.extras?.get(Intent.EXTRA_STREAM) as? Uri ?: return
        val text = readDataFromVCardUri(uri).orEmpty()
        val schema = BarcodeParserNew.parseSchema(barcodeFormater, text)
        createBarcode1(schema, true)
    }

    private fun readDataFromVCardUri(uri: Uri): String? {
        val stream = try {
            contentResolver.openInputStream(uri) ?: return null
        } catch (e: Exception) {
            //  MainLogger.log(e)
            return null
        }

        val fileContent = StringBuilder("")

        var ch: Int
        try {
            while (stream.read().also { ch = it } != -1) {
                fileContent.append(ch.toChar())
            }
        } catch (e: Exception) {
            //  MainLogger.log(e)
        }
        stream.close()

        return fileContent.toString()
    }


    private fun onClickListeners() {
        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.done.setOnClickListener {
            createBarcode1()
        }

        createOrCancelQr = { create ->
            if (create) {
                createBarcode1()
            } else {
                finish()
            }
        }

    }


    private fun toolbarMenu() {
        val titleId = schema1?.toStringId() ?: barcodeFormater.toStringId()
        binding.tvTitle.setText(titleId)

        /*    val menuId = when (schema1) {
                BarcodeSchema.APP -> return
    //            BarcodeSchema.PHONE, BarcodeSchema.SMS, BarcodeSchema.MMS -> R.menu.menu_create_qr_code_phone_import_option
    //            BarcodeSchema.VCARD, BarcodeSchema.MECARD -> R.menu.menu_create_qr_code_contacts_import_option
                else -> R.menu.menu_create_code
            }*/
        // binding.toolbar.inflateMenu(menuId)
    }

    private fun fragment() {
        val fragment = when {
            barcodeFormater == BarcodeFormat.QR_CODE && schema1 == BarcodeSchema.OTHER -> {

                binding.ivSchema.setImageResource(R.drawable.ic_text_c)
                val tintColor = ContextCompat.getColor(this, R.color.qrtextBg)
                binding.frameLayout11.background.setColorFilter(
                    tintColor,
                    PorterDuff.Mode.SRC_IN
                )

                TextQrCodeCreateFrag.newInstance(text1)
            }

            barcodeFormater == BarcodeFormat.QR_CODE && schema1 == BarcodeSchema.Clipboard -> {

                binding.ivSchema.setImageResource(R.drawable.ic_clipbord_c)
                val tintColor = ContextCompat.getColor(this, R.color.qrClipbordBg)
                binding.frameLayout11.background.setColorFilter(
                    tintColor,
                    PorterDuff.Mode.SRC_IN
                )

                TextQrCodeCreateFrag.newInstance(text1)
            }


            barcodeFormater == BarcodeFormat.QR_CODE && schema1 == BarcodeSchema.URL -> {
                binding.ivSchema.setImageResource(R.drawable.ic_url_c)
                val tintColor = ContextCompat.getColor(this, R.color.qrurlBg)
                binding.frameLayout11.background.setColorFilter(tintColor, PorterDuff.Mode.SRC_IN)
                UrlQrCodeCreateFrag()
            }

            barcodeFormater == BarcodeFormat.QR_CODE && schema1 == BarcodeSchema.PHONE -> {
                binding.ivSchema.setImageResource(R.drawable.ic_phone_c)
                val tintColor = ContextCompat.getColor(this, R.color.qrphoneBg)
                binding.frameLayout11.background.setColorFilter(tintColor, PorterDuff.Mode.SRC_IN)
                PhoneQrCodeCreateFrag()
            }

            barcodeFormater == BarcodeFormat.QR_CODE && schema1 == BarcodeSchema.WIFI -> {
                binding.ivSchema.setImageResource(R.drawable.ic_wifi_c)
                val tintColor = ContextCompat.getColor(this, R.color.qrwifiBg)
                binding.frameLayout11.background.setColorFilter(tintColor, PorterDuff.Mode.SRC_IN)
                WifiQrCodeCreateFrag()
            }

            barcodeFormater == BarcodeFormat.QR_CODE && schema1 == BarcodeSchema.EMAIL -> {
                binding.ivSchema.setImageResource(R.drawable.ic_email_c)
                val tintColor = ContextCompat.getColor(this, R.color.qremailBg)
                binding.frameLayout11.background.setColorFilter(tintColor, PorterDuff.Mode.SRC_IN)
                EmailQrCodeCreateFrag()
            }

            barcodeFormater == BarcodeFormat.QR_CODE && schema1 == BarcodeSchema.SMS -> {
                binding.ivSchema.setImageResource(R.drawable.ic_sms_c)
                val tintColor = ContextCompat.getColor(this, R.color.qrsmsBg)
                binding.frameLayout11.background.setColorFilter(tintColor, PorterDuff.Mode.SRC_IN)
                SmsQrCodeCreateFrag()
            }

            barcodeFormater == BarcodeFormat.QR_CODE && schema1 == BarcodeSchema.MMS -> {
                binding.ivSchema.setImageResource(R.drawable.ic_sms_c)
                val tintColor = ContextCompat.getColor(this, R.color.qrsmsBg)
                binding.frameLayout11.background.setColorFilter(tintColor, PorterDuff.Mode.SRC_IN)
                SmsQrCodeCreateFrag()
            }

            barcodeFormater == BarcodeFormat.QR_CODE && schema1 == BarcodeSchema.CRYPTOCURRENCY -> {
                SmsQrCodeCreateFrag()
            }

            barcodeFormater == BarcodeFormat.QR_CODE && schema1 == BarcodeSchema.GEO -> {
                binding.ivSchema.setImageResource(R.drawable.ic_location_c)
                val tintColor = ContextCompat.getColor(this, R.color.qrlocationBg)
                binding.frameLayout11.background.setColorFilter(tintColor, PorterDuff.Mode.SRC_IN)
                QrCodeLocationCreateFrag()
            }

            barcodeFormater == BarcodeFormat.QR_CODE && schema1 == BarcodeSchema.APP -> {
                AppQrCodeCreateFrag()
            }

            barcodeFormater == BarcodeFormat.QR_CODE && schema1 == BarcodeSchema.VCARD -> {
                binding.ivSchema.setImageResource(R.drawable.ic_contact_c)
                val tintColor = ContextCompat.getColor(this, R.color.qrcontactBg)
                binding.frameLayout11.background.setColorFilter(tintColor, PorterDuff.Mode.SRC_IN)
                MeCardQrCodeCreateFrag()
            }

            barcodeFormater == BarcodeFormat.QR_CODE && schema1 == BarcodeSchema.MECARD -> {
                binding.ivSchema.setImageResource(R.drawable.ic_contact_c)
                val tintColor = ContextCompat.getColor(this, R.color.qrcontactBg)
                binding.frameLayout11.background.setColorFilter(tintColor, PorterDuff.Mode.SRC_IN)

                MeCardQrCodeCreateFrag()
            }

            barcodeFormater == BarcodeFormat.DATA_MATRIX -> {
                binding.ivSchema.setImageResource(R.drawable.ic_barcode_matrix)
                val tintColor = ContextCompat.getColor(this, R.color.barcode_matrix)
                binding.frameLayout11.background.setColorFilter(tintColor, PorterDuff.Mode.SRC_IN)
                DataMatrixCreateFrag()
            }

            barcodeFormater == BarcodeFormat.AZTEC -> {
                binding.ivSchema.setImageResource(R.drawable.ic_barcode_aztec)
                val tintColor = ContextCompat.getColor(this, R.color.barcode_aztec)
                binding.frameLayout11.background.setColorFilter(tintColor, PorterDuff.Mode.SRC_IN)
                AztecCreateFrag()
            }

            barcodeFormater == BarcodeFormat.PDF_417 -> {
                binding.ivSchema.setImageResource(R.drawable.ic_barcode_pdf417)
                val tintColor = ContextCompat.getColor(this, R.color.barcode_pdf417)
                binding.frameLayout11.background.setColorFilter(tintColor, PorterDuff.Mode.SRC_IN)
                Pdf417CreateFrag()
            }

            barcodeFormater == BarcodeFormat.CODABAR -> {
                binding.ivSchema.setImageResource(R.drawable.ic_barcode_codebar)
                val tintColor = ContextCompat.getColor(this, R.color.barcode_codabar)
                binding.frameLayout11.background.setColorFilter(tintColor, PorterDuff.Mode.SRC_IN)
                CodabarCreateFrag()
            }

            barcodeFormater == BarcodeFormat.CODE_39 -> {
                binding.ivSchema.setImageResource(R.drawable.ic_barcode_code39)
                val tintColor = ContextCompat.getColor(this, R.color.barcode_code39)
                binding.frameLayout11.background.setColorFilter(tintColor, PorterDuff.Mode.SRC_IN)

                Code39CreateFrag()
            }

            barcodeFormater == BarcodeFormat.CODE_93 -> {
                binding.ivSchema.setImageResource(R.drawable.ic_barcode_ran13)
                val tintColor = ContextCompat.getColor(this, R.color.barcode_ean13)
                binding.frameLayout11.background.setColorFilter(tintColor, PorterDuff.Mode.SRC_IN)
                Code93CreateFrag()
            }

            barcodeFormater == BarcodeFormat.CODE_128 -> {
                binding.ivSchema.setImageResource(R.drawable.ic_barcode_code129)
                val tintColor = ContextCompat.getColor(this, R.color.barcode_code128)
                binding.frameLayout11.background.setColorFilter(tintColor, PorterDuff.Mode.SRC_IN)

                Code128CreateFrag()
            }

            barcodeFormater == BarcodeFormat.EAN_8 -> {
                binding.ivSchema.setImageResource(R.drawable.ic_barcode_ean8)
                val tintColor = ContextCompat.getColor(this, R.color.barcode_ean8)
                binding.frameLayout11.background.setColorFilter(tintColor, PorterDuff.Mode.SRC_IN)

                Ean8CreateFrag()
            }

            barcodeFormater == BarcodeFormat.EAN_13 -> {
                binding.ivSchema.setImageResource(R.drawable.ic_barcode_ran13)
                val tintColor = ContextCompat.getColor(this, R.color.barcode_ean13)
                binding.frameLayout11.background.setColorFilter(tintColor, PorterDuff.Mode.SRC_IN)

                Ean13CreateFrag()
            }

            barcodeFormater == BarcodeFormat.ITF -> {
                binding.ivSchema.setImageResource(R.drawable.ic_barcode_itf)
                val tintColor = ContextCompat.getColor(this, R.color.barcode_itf)
                binding.frameLayout11.background.setColorFilter(tintColor, PorterDuff.Mode.SRC_IN)

                Itf14CreateFrag()
            }

            barcodeFormater == BarcodeFormat.UPC_A -> {
                binding.ivSchema.setImageResource(R.drawable.ic_barcode_upc_a)
                val tintColor = ContextCompat.getColor(this, R.color.barcode_upca)
                binding.frameLayout11.background.setColorFilter(tintColor, PorterDuff.Mode.SRC_IN)

                UpcACreateFrag()
            }

            barcodeFormater == BarcodeFormat.UPC_E -> {
                binding.ivSchema.setImageResource(R.drawable.ic_barcode_upc_e)
                val tintColor = ContextCompat.getColor(this, R.color.barcode_upce)
                binding.frameLayout11.background.setColorFilter(tintColor, PorterDuff.Mode.SRC_IN)

                UpcECreateFrag()
            }

            else -> return
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment as Fragment)
            .commit()
    }

    private fun phone() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE
        }
        activityForResultIfExists(intent, CHOOSE_PHONE_REQUEST_CODE_CAREFULLY)
    }

    private fun chosenPhone(data: Intent?) {
        val phone = ContactHelperNew.getPhone(this, data) ?: return
        fragment1().showPhone(phone)
    }

    private fun contactsPermissions() {
        MainPermissionsHelper.requestPermissions(
            this,
            CONTACTS_PERMISSIONS_CAREFULLY,
            CONTACTS_PERMISSION_REQUEST_CODE_CAREFULLY
        )
    }

    private fun chooseContact() {
        val intent = Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
        activityForResultIfExists(intent, CHOOSE_CONTACT_REQUEST_CODE_CAREFULLY)
    }

    private fun contact(data: Intent?) {
        val contact = ContactHelperNew.getContact(this, data) ?: return
        fragment1().showContact(contact)
    }

    private fun activityForResultIfExists(intent: Intent, requestCode: Int) {
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, requestCode)
        } else {
            Toast.makeText(this, R.string.code_no_app, Toast.LENGTH_SHORT).show()
        }
    }

    private fun createBarcode1() {
        val schema = fragment1().getBarcodeSchema()
        createBarcode1(schema)
    }

    private fun createBarcode1(schema: Schema, finish: Boolean = false) {
        val MineBarCode = MineBarCode(
            text = schema.toBarcodeText(),
            formattedText = schema.toFormattedText(),
            format = barcodeFormater,
            schema = schema.schema,
            datetime = System.currentTimeMillis(),
            isGenerated = true
        )

        if (mains.saveQrCodesToHistory.not()) {
            toBarcodeScreen(MineBarCode, finish)
            return
        }

        MineBarCode.isScanned = false


        barcodeDatabase?.insertCode(MineBarCode, mains.doNotSaveDuplicates)
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe(
                { id ->
                    toBarcodeScreen(MineBarCode.copy(id = id), finish)                },
                { error ->

                    showError(error)
                }
            )
            ?.addTo(disposableSpcificHmm)



    }

    private fun fragment1(): BaseFragment {
        return supportFragmentManager.findFragmentById(R.id.container) as BaseFragment
    }

    private fun toBarcodeScreen(MineBarCode: MineBarCode, finish: Boolean) {
        MineBarCode.isScanned = false

        QRResultActivity.start(this, MineBarCode, false)
        //   it.finish()

        // ResultOfQRActivity.start(this, barcode, true)

        if (finish) {
            finish()
        }
    }
}