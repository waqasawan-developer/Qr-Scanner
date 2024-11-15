package com.utilitytoolbox.qrscanner.barcodescanner.userInterface.activities.fragments

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaActionSound
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.util.Size
import android.view.LayoutInflater
import android.view.OrientationEventListener
import android.view.Surface
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil.isValidUrl
import android.widget.Button
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.content.res.AppCompatResources
import androidx.browser.customtabs.CustomTabsIntent
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.core.TorchState
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.utilitytoolbox.qrscanner.barcodescanner.model
.MainBarcodeParsedNew
import com.utilitytoolbox.qrscanner.barcodescanner.usecase
.BarcodeDatabase
import com.utilitytoolbox.qrscanner.barcodescanner.usecase
.BarcodeParserNew
import com.utilitytoolbox.qrscanner.barcodescanner.usecase
.MainPermissionsHelper
import com.utilitytoolbox.qrscanner.barcodescanner.usecase
.MainScannerCameraHelper
import com.utilitytoolbox.qrscanner.barcodescanner.usecase
.SettingsMainNew
import com.utilitytoolbox.qrscanner.barcodescanner.interfaces.MLKitAnalyzer
import com.utilitytoolbox.qrscanner.barcodescanner.interfaces
.ZXingAnalyzer
import com.utilitytoolbox.qrscanner.barcodescanner.interfaces
.ScanningResult
import com.utilitytoolbox.qrscanner.barcodescanner.model.MineBarCode
import com.utilitytoolbox.qrscanner.barcodescanner.usecase
.insertCode
import com.utilitytoolbox.qrscanner.barcodescanner.utils.applySystemWindowInsets
import com.utilitytoolbox.qrscanner.barcodescanner.utils.changeStatusBarClr
import com.utilitytoolbox.qrscanner.barcodescanner.utils.disableBatchScanningMode
import com.utilitytoolbox.qrscanner.barcodescanner.utils.getBarcodeFormat
import com.utilitytoolbox.qrscanner.barcodescanner.utils.onBatchScanningEnabled
import com.utilitytoolbox.qrscanner.barcodescanner.utils.onInAppSubscription
import com.utilitytoolbox.qrscanner.barcodescanner.utils.showBatchScanningFavHistory
import com.utilitytoolbox.qrscanner.barcodescanner.utils.showBatchScanningFromHistory
import com.utilitytoolbox.qrscanner.barcodescanner.utils.showError
import com.utilitytoolbox.qrscanner.barcodescanner.utils.toImageId
import com.utilitytoolbox.qrscanner.barcodescanner.utils.toStringId
import com.utilitytoolbox.qrscanner.barcodescanner.utils.vibrateOnce
import com.utilitytoolbox.qrscanner.barcodescanner.utils.vibrator
import com.google.common.util.concurrent.ListenableFuture
import com.google.zxing.BarcodeFormat
import com.google.zxing.Result
import com.permissionx.guolindev.PermissionX
import com.utilitytoolbox.qrscanner.barcodescanner.R
import com.utilitytoolbox.qrscanner.barcodescanner.databinding.LayoutFragmentCamBinding
import com.utilitytoolbox.qrscanner.barcodescanner.userInterface.activities.MainActivityNew
import com.utilitytoolbox.qrscanner.barcodescanner.model.SearchEngineMain
import com.utilitytoolbox.qrscanner.barcodescanner.model.schema.OtherModel
import com.utilitytoolbox.qrscanner.barcodescanner.model.schema.Schema
import com.utilitytoolbox.qrscanner.barcodescanner.userInterface.activities.GalleryScanBarcodeActivity
import com.utilitytoolbox.qrscanner.barcodescanner.userInterface.activities.QRResultActivity
import com.utilitytoolbox.qrscanner.barcodescanner.utils.textString
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Inject

@AndroidEntryPoint
class CameraFragment : Fragment() {
    private val permissions = arrayOf(Manifest.permission.CAMERA)
    private val permission_request_code = 101

    @Inject
    lateinit var SettingsMainNew: SettingsMainNew

    @Inject
    lateinit var barcodeDatabase: BarcodeDatabase

    enum class ScannerSDK {
        MLKIT,
        ZXING
    }

    lateinit var binding: LayoutFragmentCamBinding

    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>

    private lateinit var cameraExecutor: ExecutorService
    private var flashEnabled = false
    private var scannerSDK: ScannerSDK = ScannerSDK.MLKIT

    private val vibrationPattern = arrayOf<Long>(0, 80).toLongArray()
    private val disposable = CompositeDisposable()
    private var maxZoom: Int = 0
    private val zoomStep = 10
    private var currentZoom = 10
    private var toast: Toast? = null
    private var lastResult: MineBarCode? = null
    var camera: Camera? = null

    var cameraPermissionDialog: AlertDialog? = null

    private var previousScannedItems = arrayListOf<String>()
    private var batchScanningList = arrayListOf<MineBarCode>()

    lateinit var mContext: Context
    var mActivity: Activity? = null
    var shouldGoSetting = false


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LayoutFragmentCamBinding.inflate(layoutInflater)
        return binding.rootView
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        mActivity = activity
        mContext = activity
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == 88) {
            cameraPermissionDialog?.dismiss()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mActivity?.changeStatusBarClr(isTransparent = true)


        //supportEdgeToEdge()
        setDarkStatusBar()
        init()
        onClickListeners()

    }


    private fun init() {
        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    val zoomRatio = (progress.toFloat() / 100f).coerceIn(0f, 1f)
                    camera?.cameraControl?.setZoomRatio(zoomRatio * 10)
                }
            }
        })
        setupCamera()

        disableBatchScanningMode = {
            binding.batchScanningView.visibility = View.GONE
            previousScannedItems = arrayListOf<String>()
            batchScanningList = arrayListOf<MineBarCode>()
            MainActivityNew.isBatchScanningViewAdded = false
        }


        Log.d(
            "djfhdfd87fdhfjd",
            "$onBatchScanningEnabled -> ${MainActivityNew.isBatchScanningViewAdded}"
        )

        binding.liBarcode.setOnClickListener {
            showBarCodeDialog(mContext)
        }

        if (onBatchScanningEnabled) {
            binding.imageViewScanMode.setImageResource(R.drawable.ic_modes)
            if (MainActivityNew.isBatchScanningViewAdded) {
                disableBatchScanningMode?.invoke()
            }
            binding.imageViewScanMode.setColorFilter(
                ContextCompat.getColor(requireContext(), R.color.color_accent)
            )
            binding.tvModes.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.color_accent
                )
            )
        } else {
            binding.imageViewScanMode.setImageResource(R.drawable.ic_modes)
            if (MainActivityNew.isBatchScanningViewAdded) {
                disableBatchScanningMode?.invoke()
            }
            binding.imageViewScanMode.setColorFilter(
                ContextCompat.getColor(requireContext(), R.color.white)
            )
            binding.tvModes.setTextColor(
                ContextCompat.getColor(
                    mContext,
                    R.color.white
                )
            )
        }


    }


    public fun showBarCodeDialog(context: Context) {
        // Inflate the custom layout for the dialog
        binding.cameraPreview.visibility = View.GONE
        val dialogView = LayoutInflater.from(context).inflate(R.layout.barcode_dialog, null)

        // Find the views in the custom layout
        val yesButton = dialogView.findViewById<TextView>(R.id.tvCreate)
        val noButton = dialogView.findViewById<TextView>(R.id.tvCancel)

        val edittext = dialogView.findViewById<EditText>(R.id.et_code128)

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
                "here is clicked on ratting -> ${edittext.text} -> ${context.packageName}"
            )

            if (edittext.text.isNullOrEmpty()) {
                edittext.error = "Field cannot be empty"
            } else {
                createBarcode1(OtherModel(edittext.textString), false)
            }
        }

        //   rattingBar.setOnRatingChangeListener

        noButton.setOnClickListener {
            //  handleNoButtonClick() // Handle No button click
            dialog.dismiss() // Close the dialog
        }

        dialog.setOnDismissListener {
            binding.cameraPreview.visibility = View.VISIBLE
        }

        // Show the dialog
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.show()
    }


    private fun createBarcode1(schema: Schema, finish: Boolean = false) {
        val MineBarCode = MineBarCode(
            text = schema.toBarcodeText(),
            formattedText = schema.toFormattedText(),
            format = BarcodeFormat.CODE_128,
            schema = schema.schema,
            datetime = System.currentTimeMillis(),
            isGenerated = true
        )

        if (SettingsMainNew.saveQrCodesToHistory.not()) {
            toBarcodeScreen(MineBarCode, finish)
            return
        }

        MineBarCode.isScanned = false


        barcodeDatabase?.insertCode(MineBarCode, SettingsMainNew.doNotSaveDuplicates)
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe(
                { id ->
                    toBarcodeScreen(MineBarCode.copy(id = id), finish)
                },
                { error ->

                    showError(error)
                }
            )
            ?.addTo(disposableSpcificHmm)

    }

    private val disposableSpcificHmm = CompositeDisposable()

    private fun toBarcodeScreen(MineBarCode: MineBarCode, finish: Boolean) {
        MineBarCode.isScanned = false

        QRResultActivity.start(mContext, MineBarCode, false)
        //   it.finish()

        // ResultOfQRActivity.start(this, barcode, true)

    }

    private fun setupCamera() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(mContext)
        // Initialize our background executor
        cameraExecutor = Executors.newSingleThreadExecutor()

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            bindPreview(cameraProvider)
        }, ContextCompat.getMainExecutor(mContext))

        binding.overlay.post {
            binding.overlay.setViewFinder()
        }
    }

    private fun bindPreview(cameraProvider: ProcessCameraProvider?) {
        if ((mActivity as MainActivityNew).isDestroyed || (mActivity as MainActivityNew).isFinishing) {
            return
        }

        cameraProvider?.unbindAll()

        val preview: Preview = Preview.Builder()
            .build()
        var cameraSelector: CameraSelector? = null
        if (SettingsMainNew.isBackCamera) {
            cameraSelector = CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build()
        } else {
            cameraSelector = CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                .build()
        }

        val imageAnalysis = ImageAnalysis.Builder()
            .setTargetResolution(Size(binding.cameraPreview.width, binding.cameraPreview.height))
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()

        val orientationEventListener = object : OrientationEventListener(mContext) {
            override fun onOrientationChanged(orientation: Int) {
                // Monitors orientation values to determine the target rotation value
                val rotation: Int = when (orientation) {
                    in 45..134 -> Surface.ROTATION_270
                    in 135..224 -> Surface.ROTATION_180
                    in 225..314 -> Surface.ROTATION_90
                    else -> Surface.ROTATION_0
                }

                imageAnalysis.targetRotation = rotation
            }
        }
        orientationEventListener.enable()

        //switch the analyzers here, i.e. MLKitBarcodeAnalyzer, ZxingBarcodeAnalyzer
        class OfScanningResult : ScanningResult {
            override fun onScanned(result: Result) {
                mActivity?.runOnUiThread {
                    imageAnalysis.clearAnalyzer()
                    cameraProvider?.unbindAll()
                    //show result
                    handleScannedBarcode(result)
                }
            }

            override fun onBatchScanning(barcodes: MutableList<com.google.mlkit.vision.barcode.common.Barcode>) {
                barcodes.forEach { barcode ->
                    val qrCodeData: String? = barcode.rawValue
                    val qrCodeFormat: Int? = barcode.format
                    val rawBytes: ByteArray? = barcode.rawBytes

                    qrCodeFormat?.let {
                        // Create a ZXing Result object
                        val zxingResult = Result(
                            qrCodeData,
                            rawBytes,  // QR code raw bytes if available, can be obtained from mlKitBarcode.getRawBytes()
                            null,  // ResultPoint[] if available
                            getBarcodeFormat(it)
                        )

                        mActivity?.let { act ->
                            try {
                                if (!act.isDestroyed && !act.isFinishing)
                                    act.runOnUiThread {
                                        handleScannedBarcode(zxingResult)
                                    }
                            } catch (exception: java.lang.IllegalStateException) {
                                Log.e("TAG", "onBatchScanning: ")
                            }
                        }
                    }
                }
            }
        }

        var analyzer: ImageAnalysis.Analyzer = MLKitAnalyzer(OfScanningResult())

        if (scannerSDK == ScannerSDK.ZXING) {
            analyzer = ZXingAnalyzer(OfScanningResult())
        }

        imageAnalysis.setAnalyzer(cameraExecutor, analyzer)

        preview.setSurfaceProvider(binding.cameraPreview.surfaceProvider)

        camera = cameraProvider?.bindToLifecycle(this, cameraSelector, imageAnalysis, preview)

        if (camera?.cameraInfo?.hasFlashUnit() == true) {
            camera?.cameraInfo?.torchState?.observe(viewLifecycleOwner) {
                it?.let { torchState ->
                    flashEnabled = torchState == TorchState.ON
                    binding.imageViewFlash.isActivated = flashEnabled
                }
            }
        }
    }

    private fun showPermissionDialog() {
        val layout = View.inflate(context, R.layout.dialog_cam_permission, null)
        layout.run {
            findViewById<Button>(R.id.yesAllow).setOnClickListener {
                cameraPermissionDialog?.dismiss()
                if (shouldGoSetting) {
                    openAppSettings()
                } else
                    requestPermissions()
            }
        }

        val builder = AlertDialog.Builder(context)
        builder.setView(layout)
        cameraPermissionDialog = builder.create()
        cameraPermissionDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        cameraPermissionDialog?.setCancelable(false)
        if (cameraPermissionDialog?.isShowing == false)
            cameraPermissionDialog?.show()
    }

    private fun onClickListeners() {
        binding.imageViewScanFromFile.setOnClickListener {
            navigateToScanFromFileScreen()
        }

        binding.ivNextView.setOnClickListener {
            if (SettingsMainNew.saveQrCodesToHistory) {
                showBatchScanningFromHistory?.invoke()
            } else {
                showBatchScanningFavHistory?.invoke(false, batchScanningList)
            }
        }

        binding.imageViewFlash.setOnClickListener {
            camera?.cameraControl?.enableTorch(!flashEnabled)
            if (flashEnabled) {
                binding.imageViewFlash.setImageResource(R.drawable.torch_on)
            } else {
                binding.imageViewFlash.setImageResource(R.drawable.torch_off)
            }
        }

        binding.BatchScanning.setOnClickListener {
            if (onBatchScanningEnabled) {
                onBatchScanningEnabled = false
                binding.imageViewScanMode.setImageResource(R.drawable.ic_modes)
                if (MainActivityNew.isBatchScanningViewAdded) {
                    disableBatchScanningMode?.invoke()
                }
                binding.imageViewScanMode.setColorFilter(
                    ContextCompat.getColor(requireContext(), R.color.white)
                )
                binding.tvModes.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.white
                    )
                )
            } else {
                onBatchScanningEnabled = true
                binding.imageViewScanMode.setColorFilter(
                    ContextCompat.getColor(requireContext(), R.color.color_accent)
                )
                binding.tvModes.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.color_accent
                    )
                )
            }
        }

        binding.batchScanningView.setOnClickListener {
            //showBatchScanningFromHistory?.invoke()
        }

        /* binding.ivNextView.setOnClickListener {
         }*/

        /*binding.ivRemoveAds.setOnClickListener {
            (mActivity as MainActivity).iapConnector?.subscribe(
                requireActivity(),
                "remove_ad_subscription"
            )
        }*/

        binding.ivBtnZoomIn.setOnClickListener {
            increaseZoom()
        }

        binding.ivBtnZoomOut.setOnClickListener {
            decreaseZoom()
        }

        binding.ivRotateCamera.setOnClickListener {
            SettingsMainNew.isBackCamera = !SettingsMainNew.isBackCamera
            val cameraProvider = cameraProviderFuture.get()
            bindPreview(cameraProvider)
        }
    }

    override fun onResume() {
        super.onResume()

        if (areAllPermissionsGranted()) {
            cameraPermissionDialog?.dismiss()
            initZoomSeekBar()
            val cameraProvider = cameraProviderFuture.get()
            if (!isDetached)
                bindPreview(cameraProvider)

            /* if (!permissionsHelper.areAllPermissionsGranted(
                     mContext,
                     arrayOf(Manifest.permission.POST_NOTIFICATIONS)
                 )
             ) {
                 requestNotificationPermissions()
             }*/
        } else {
            cameraPermissionDialog?.dismiss()
            showPermissionDialog()
        }

        /* if (requireContext().checkIfPremium()) {
             binding.ivRemoveAds.isVisible = false
         }*/
    }

    /*override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == permission_request_code && areAllPermissionsGranted(grantResults)) {
            initZoomSeekBar()
            val cameraProvider = cameraProviderFuture.get()
            bindPreview(cameraProvider)
        }
    }*/

    override fun onPause() {
        val cameraProvider = cameraProviderFuture.get()
        cameraProvider?.unbindAll()
        super.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        setLightStatusBar()
        disposable.clear()
        onInAppSubscription = null
    }

    private fun supportEdgeToEdge() {
        binding.rootView.applySystemWindowInsets(applyTop = true, applyBottom = false)
    }

    private fun setDarkStatusBar() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return
        }

        if (SettingsMainNew.isDarkThemeEnabled) {
            return
        }

        mActivity?.window?.decorView?.apply {
            systemUiVisibility = systemUiVisibility xor View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }

    private fun setLightStatusBar() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return
        }

        if (SettingsMainNew.isDarkThemeEnabled) {
            return
        }

        mActivity?.window?.decorView?.apply {
            systemUiVisibility = systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }

    private fun initZoomSeekBar() {
        MainScannerCameraHelper.getCameraParameters(SettingsMainNew.isBackCamera)?.apply {
            this@CameraFragment.maxZoom = maxZoom
            binding.seekBar.max = maxZoom
            binding.seekBar.progress = zoom
        }
        currentZoom = 10
    }

    private fun decreaseZoom() {

        currentZoom -= zoomStep  // You can adjust this step size
        // Ensure the progress doesn't go below the minimum
        if (currentZoom < 0) {
            currentZoom = 0
        }
        // Update the ProgressBar's progress
        binding.seekBar.progress = currentZoom

        val zoomRatio = (currentZoom.toFloat() / 100f).coerceIn(0f, 1f)
        camera?.cameraControl?.setZoomRatio(zoomRatio * 10)
    }

    private fun increaseZoom() {

        currentZoom += zoomStep // You can adjust this step size
        // Ensure the progress doesn't exceed the maximum
        if (currentZoom > binding.seekBar.max) {
            currentZoom = binding.seekBar.max
        }
        // Update the ProgressBar's progress
        binding.seekBar.progress = currentZoom

        val zoomRatio = (currentZoom.toFloat() / 100f).coerceIn(0f, 1f)
        camera?.cameraControl?.setZoomRatio(zoomRatio * 10)

    }

    private fun handleScannedBarcode(result: Result) {


        val barcode = BarcodeParserNew.parseResult(result)

        if (onBatchScanningEnabled) {
            //visible batchScanningView
            binding.batchScanningView.visibility = View.VISIBLE

            if (!previousScannedItems.contains(result.text)) {
                vibrateIfNeeded()
                previousScannedItems.add(result.text)
                MainActivityNew.isBatchScanningViewAdded = true

                val format = barcode.format.toStringId()
                binding.scanTypeTxt.setText(format)
                binding.scanTxt.text = result.text

                val imageId = barcode.schema.toImageId() ?: barcode.format.toImageId()
                val image = AppCompatResources.getDrawable(mContext, imageId)

                binding.typeImg.setImageDrawable(image)
                batchScanningList.add(barcode)
                binding.batchScannedItemSize.text = "${batchScanningList.size}"
                when {
                    SettingsMainNew.saveQrCodesToHistory -> saveScannedBarcode(
                        barcode
                    )

                    else -> navigateToBarcodeScreen(barcode)
                }
            }
        } else {
            vibrateIfNeeded()
            when {
                SettingsMainNew.saveQrCodesToHistory -> saveScannedBarcode(
                    barcode
                )

                else -> navigateToBarcodeScreen(barcode)
            }
        }
    }

    private fun vibrateIfNeeded() {
        scanQrCode()
        if (SettingsMainNew.vibrate) {
            mActivity?.apply {
                runOnUiThread {
                    applicationContext.vibrator?.vibrateOnce(vibrationPattern)
                }
            }
        }
    }


    private fun scanQrCode() {
        // ... (your QR code scanning logic)

        if (SettingsMainNew.beep) {
            Log.d("dskff987sflkjsjf", "sound should came")
            val sound = MediaActionSound()
            try {
                sound.play(MediaActionSound.FOCUS_COMPLETE)
                Log.d("dskff987sflkjsjf", "Notification sound played successfully.")
            } catch (e: Exception) {
                Log.e("dskff987sflkjsjf", "Error playing notification sound: ${e.message}")
            } finally {
                Handler(Looper.getMainLooper()).postDelayed({
                    sound.release()
                }, 2000)

            }
        }
    }

    private fun saveScannedBarcode(MineBarCode: MineBarCode) {
        MineBarCode.isScanned = true
        barcodeDatabase.insertCode(MineBarCode, SettingsMainNew.doNotSaveDuplicates)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { id ->
                    lastResult = MineBarCode
                    navigateToBarcodeScreen(MineBarCode.copy(id = id))
                },
                ::showError
            )
            .addTo(disposable)
    }

    private fun requestPermissions() {
        PermissionX.init(requireActivity())
            .permissions(
                Manifest.permission.CAMERA
            ).onForwardToSettings { scope, deniedList ->
                /*scope.showForwardToSettingsDialog(
                    deniedList,
                    getString(R.string.you_need_to_allow_camera_permission),
                    getString(R.string.error_dlg_positive_button_text),
                    getString(R.string.dlg_delete_negative_btn)
                )*/
                shouldGoSetting = true
            }
            .request { allGranted, grantedList, deniedList ->
                if (!allGranted) {
                    Toast.makeText(
                        mContext,
                        "These permissions are denied: $deniedList",
                        Toast.LENGTH_LONG
                    ).show()
                }/* else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        requestNotificationPermissions()
                    }
                }*/
            }


    }

    private fun openAppSettings() {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", activity?.packageName, null)
            intent.data = uri
            startActivityForResult(intent, 88)
        } catch (e: Exception) {

        }

    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestNotificationPermissions() {
        PermissionX.init(requireActivity())
            .permissions(
                Manifest.permission.POST_NOTIFICATIONS
            ).onForwardToSettings { scope, deniedList ->
                scope.showForwardToSettingsDialog(
                    deniedList,
                    getString(R.string.you_need_to_allow_notification_permission),
                    getString(R.string.error_dlg_positive_button_text),
                    getString(R.string.dlg_delete_negative_btn)
                )
            }
            .request { allGranted, grantedList, deniedList ->
                if (!allGranted) {
                    Toast.makeText(
                        mContext,
                        "Notification permission are denied",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    //  FcmFireBaseMessagingService.subscribeToTopic()
                }
            }
    }

    private fun areAllPermissionsGranted(): Boolean {
        return MainPermissionsHelper.areAllPermissionsGranted(mContext, permissions)
    }

    private fun areAllPermissionsGranted(grantResults: IntArray): Boolean {
        return MainPermissionsHelper.areAllPermissionsGranted(grantResults)
    }

    private fun navigateToScanFromFileScreen() {
        GalleryScanBarcodeActivity.startUp(mContext)
    }


    private fun navigateToBarcodeScreen(MineBarCode: MineBarCode) {

        if (!onBatchScanningEnabled) {
            if (SettingsMainNew.autoSearch) {
                var barcode = MainBarcodeParsedNew(MineBarCode)

                if (barcode.url != null) {
                    mActivity?.let {
                        if (isValidUrl(barcode.text)) {
                            openCustomTabView(
                                it, barcode.text, SettingsMainNew.searchEngine
                            )
                        } else {
                            QRResultActivity.start(it, MineBarCode)
                            it.finish()
                        }
                    }
                } else {
                    startNextScanResult(MineBarCode)
                }
            } else {
                startNextScanResult(MineBarCode)
            }
        }
    }

    fun startNextScanResult(MineBarCode: MineBarCode) {
        mActivity?.let {
            QRResultActivity.start(it, MineBarCode)
            it.finish()
        }
    }

    fun openCustomTabView(context: Context, input: String, searchEngine: SearchEngineMain) {
        try {
            if (isValidUrl(input)) {
                // Launch URL in Custom Tabs
                val customTabsIntent = CustomTabsIntent.Builder()
                    .setToolbarColor(context.getColor(android.R.color.holo_blue_light)) // Example customization
                    .build()

                customTabsIntent.launchUrl(context, Uri.parse(input))
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

}
