package com.utilitytoolbox.qrscanner.barcodescanner.userInterface.activities

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.provider.MediaStore
import android.view.MotionEvent.ACTION_UP
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.utilitytoolbox.qrscanner.barcodescanner.usecase
.BarcodeDatabase
import com.utilitytoolbox.qrscanner.barcodescanner.usecase
.BarcodeScannerImage
import com.utilitytoolbox.qrscanner.barcodescanner.usecase
.BarcodeParserNew
import com.utilitytoolbox.qrscanner.barcodescanner.usecase
.MainPermissionsHelper
import com.utilitytoolbox.qrscanner.barcodescanner.usecase
.SettingsMainNew
import com.utilitytoolbox.qrscanner.barcodescanner.model.MineBarCode
import com.utilitytoolbox.qrscanner.barcodescanner.usecase
.insertCode
import com.utilitytoolbox.qrscanner.barcodescanner.utils.applySystemWindowInsets
import com.utilitytoolbox.qrscanner.barcodescanner.utils.showError
import com.google.zxing.Result
import com.utilitytoolbox.qrscanner.barcodescanner.R
import com.utilitytoolbox.qrscanner.barcodescanner.databinding.ActivityGalleryScanBinding

import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class GalleryScanBarcodeActivity : AppCompatActivity() {

    lateinit var binding: ActivityGalleryScanBinding

    @Inject
    lateinit var SettingsMainNew: SettingsMainNew

    @Inject
    lateinit var barcodeDatabase: BarcodeDatabase


    companion object {
        private const val CHOOSE_FILE_REQUEST_CODE_CAREFULLY = 12
        private const val CHOOSE_FILE_AGAIN_REQUEST_CODE_CAREFULLY = 13
        private const val PERMISSIONS_REQUEST_CODE_CAREFULLY = 14
        private val PERMISSIONSREQUIED = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)

        fun startUp(context: Context) {
            val intent = Intent(context, GalleryScanBarcodeActivity::class.java)
            context.startActivity(intent)
        }
    }

    private var imageUri1: Uri? = null
    private var scanResult1: Result? = null
    private val compositeDisposable = CompositeDisposable()
    private val disposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGalleryScanBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window?.let {
            it.statusBarColor = ContextCompat.getColor(this, R.color.color_accent)
        }
        edgeToEdge()
        backPressed()
        toolbarMenuItemClicked()
        imageCropAreaChanged()
        buttonClicked()

        if (fromIntent().not()) {
            imageActivity(savedInstanceState)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if ((requestCode == CHOOSE_FILE_REQUEST_CODE_CAREFULLY || requestCode == CHOOSE_FILE_AGAIN_REQUEST_CODE_CAREFULLY) && resultCode == RESULT_OK) {
            data?.data?.apply(::image)
            return
        }

        if (requestCode == CHOOSE_FILE_REQUEST_CODE_CAREFULLY) {
            finish()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSIONS_REQUEST_CODE_CAREFULLY && MainPermissionsHelper.areAllPermissionsGranted(
                grantResults
            )
        ) {
            imageUri1?.apply(::image)
        } else {
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
        compositeDisposable.clear()
    }

    private fun edgeToEdge() {
        binding.rootView.applySystemWindowInsets(applyTop = true, applyBottom = true)
    }

    private fun fromIntent(): Boolean {
        var uri: Uri? = null

        if (intent?.action == Intent.ACTION_SEND && intent.type.orEmpty().startsWith("image/")) {
            uri = intent.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) as? Uri
        }

        if (intent?.action == Intent.ACTION_VIEW && intent.type.orEmpty().startsWith("image/")) {
            uri = intent.data
        }

        if (uri == null) {
            return false
        }

        image(uri)
        return true
    }

    private fun imageActivity(savedInstanceState: Bundle?) {
        imageActivity(CHOOSE_FILE_REQUEST_CODE_CAREFULLY, savedInstanceState)
    }

    private fun activityAgain() {
        imageActivity(CHOOSE_FILE_AGAIN_REQUEST_CODE_CAREFULLY, null)
    }

    private fun imageActivity(requestCode: Int, savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            return
        }

        try {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*"))
            startActivityForResult(intent, requestCode)
        } catch (e: java.lang.Exception) {
            if (e is ActivityNotFoundException) {
                val intent = Intent()
                intent.type = "image/*"
                intent.action = Intent.ACTION_GET_CONTENT
                startActivityForResult(
                    Intent.createChooser(intent, "Select Picture"),
                    requestCode
                )
            }
        }
    }

    private fun backPressed() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun toolbarMenuItemClicked() {
        binding.toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                // R.id.iv_scan_image -> activityAgain()
            }
            return@setOnMenuItemClickListener true
        }
    }

    private fun imageCropAreaChanged() {
        binding.cropImageView.touches()
            .filter { it.action == ACTION_UP }
            .debounce(400, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { croppedImage1() }
            .addTo(compositeDisposable)
    }

    private fun buttonClicked() {
        binding.ivScan.setOnClickListener {
            scanResult()
        }
    }

    private fun image(imageUri: Uri) {
        this.imageUri1 = imageUri

        binding.cropImageView
            .load(imageUri)
            .executeAsCompletable()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { croppedImage1() },
                ::showErrorOrRequestPermissions
            )
            .addTo(compositeDisposable)
    }

    private fun showErrorOrRequestPermissions(error: Throwable) {
        when (error) {
            is SecurityException -> MainPermissionsHelper.requestPermissions(
                this,
                PERMISSIONSREQUIED,
                PERMISSIONS_REQUEST_CODE_CAREFULLY
            )

            else -> showError(error)
        }
    }

    private fun croppedImage1() {
        loading(true)
        buttonEnabled(false)

        disposable.clear()
        scanResult1 = null

        binding.cropImageView
            .cropAsSingle()
            .subscribeOn(Schedulers.io())
            .subscribe(::croppedImage1, ::showError)
            .addTo(disposable)
    }

    private fun croppedImage1(image: Bitmap) {
        BarcodeScannerImage
            .parse(image)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { scanResult ->
                    scanResult1 = scanResult
                    buttonEnabled(true)
                    loading(false)
                },
                { loading(false) }
            )
            .addTo(disposable)
    }

    private fun scanResult() {
        val barcode = scanResult1?.let(BarcodeParserNew::parseResult) ?: return
        if (SettingsMainNew.saveQrCodesToHistory.not()) {
            barcodeScreen(barcode)
            return
        }

        loading(true)

        barcode.isScanned=true
        barcodeDatabase.insertCode(barcode, SettingsMainNew.doNotSaveDuplicates)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { id ->
                    barcodeScreen(barcode.copy(id = id))
                },
                { error ->
                    loading(false)
                    showError(error)
                }
            )
            .addTo(compositeDisposable)
    }

    private fun loading(isLoading: Boolean) {
        binding.pbLoading.isVisible = isLoading
        binding.ivScan.isInvisible = isLoading
    }

    private fun buttonEnabled(isEnabled: Boolean) {
        binding.ivScan.isEnabled = isEnabled
    }

    private fun barcodeScreen(MineBarCode: MineBarCode) {
        QRResultActivity.start(this, MineBarCode)
        finish()
    }
}