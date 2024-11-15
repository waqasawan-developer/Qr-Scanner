package com.utilitytoolbox.qrscanner.barcodescanner.userInterface.activities.fragments

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.Fragment
import com.utilitytoolbox.qrscanner.barcodescanner.R
import com.utilitytoolbox.qrscanner.barcodescanner.usecase
.SettingsMainNew
import com.utilitytoolbox.qrscanner.barcodescanner.utils.changeStatusBarClr
import com.utilitytoolbox.qrscanner.barcodescanner.utils.onInAppSubscription
import com.utilitytoolbox.qrscanner.barcodescanner.utils.showBatchScanningFavHistory
import com.utilitytoolbox.qrscanner.barcodescanner.utils.showError
import com.utilitytoolbox.qrscanner.barcodescanner.databinding
.LayoutFragmentSettingsBinding

import com.utilitytoolbox.qrscanner.barcodescanner.dialogs.ConfirmationDeleteFragDialog
import com.utilitytoolbox.qrscanner.barcodescanner.dialogs.MainCustomDialog
import com.utilitytoolbox.qrscanner.barcodescanner.model
.SearchEngineMain
import com.utilitytoolbox.qrscanner.barcodescanner.usecase
.BarcodeDatabase
import com.utilitytoolbox.qrscanner.barcodescanner.utils.showConfirmationDialog
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject


@AndroidEntryPoint
class SettingsFrag : Fragment(), ConfirmationDeleteFragDialog.Listener {
    private val disposable = CompositeDisposable()
    lateinit var binding: LayoutFragmentSettingsBinding

    @Inject
    lateinit var SettingsMainNew: SettingsMainNew

    @Inject
    lateinit var barcodeDatabase: BarcodeDatabase

    var mActivity: Activity? = null
    var mContext: Context? = null

    /*  override fun onAttach(context: Context) {
          super.onAttach(context)
          mContext = context
          if (context is Activity) {
              mActivity = context as MainActivity
          }
      }*/

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LayoutFragmentSettingsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        mActivity = activity
        mContext = activity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().changeStatusBarClr(isTransparent = false)
        //supportEdgeToEdge()

        onClickListeners()

        SettingsMainNew.apply {
            binding.fmVibrate.isChecked = vibrate
            binding.fmBeep.isChecked = beep

            binding.fmSaveHistory.isChecked = saveQrCodesToHistory
            binding.fmAutoBrowse.isChecked = autoSearch
            binding.fmDuplicate.isChecked = doNotSaveDuplicates
            binding.ivBatchScanning.isChecked = batchScanning
            binding.ivBatchScanning.isChecked = batchScanning

            binding.tvSelectedCamera.text =
                resources.getStringArray(R.array.cameraSelection)[selectedCamera]
            binding.tvSelectedSearchEngine.text =
                resources.getStringArray(R.array.searchEngineSelectiom)[
                    getPostionseletedSearchEngine(
                        searchEngine
                    ),
                ]

            binding.tvSeletedTheme.text =
                resources.getStringArray(R.array.themeSelection)[getthemevalue(SettingsMainNew.theme)]


        }


    }

    override fun onResume() {
        super.onResume()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        disposable.clear()
        onInAppSubscription = null
    }


    private fun onClickListeners() {

        /* binding.ivClearHistory.setOnClickListener {
             ShowDeleteHistoryMainDialog(requireContext(), true, onDelete = {
                 if (it)
                     clearHistory()
             })
         }*/
        /*  binding.toolbar.setNavigationOnClickListener {
              (requireContext() as MainActivity).onBackPressed()
          }*/


        /* binding.ivRateUs.setOnClickListener {
             try {
                 startActivity(
                     Intent(
                         Intent.ACTION_VIEW,
                         Uri.parse("https://play.google.com/store/apps/details?id=${requireContext().packageName}")
                     )
                 )
             } catch (e: ActivityNotFoundException) {
                 startActivity(
                     Intent(
                         Intent.ACTION_VIEW,
                         Uri.parse("https://play.google.com/store/apps/details?id=${requireContext().packageName}")
                     )
                 )
             }
         }*/


        /* binding.ivOpenLinksAutomatically.setCheckedChangedListener {
             mainSettings.openLinksAutomatically = it
         }*/

        binding.fmVibrate.setCheckedChangedListener { SettingsMainNew.vibrate = it }

        binding.fmBeep.setCheckedChangedListener { SettingsMainNew.beep = it }

        binding.ivBatchScanning.setCheckedChangedListener {
            SettingsMainNew.batchScanning = it
        }

        binding.fmFavHistory.setOnClickListener {
            showBatchScanningFavHistory?.invoke(true, arrayListOf())
        }


        binding.fmDuplicate.setCheckedChangedListener {
            SettingsMainNew.doNotSaveDuplicates = it
        }

        binding.fmAutoBrowse.setCheckedChangedListener {
            SettingsMainNew.autoSearch = it
        }

        binding.fmSaveHistory.setCheckedChangedListener {
            SettingsMainNew.saveQrCodesToHistory = it
        }

        binding.clSelectCamera.setOnClickListener {
            showCameraSelectionDialog()
        }

        binding.fmRateUs.setOnClickListener {mContext?.let {
            showConfirmationDialog(it)
        }
        }

        binding.clSearchEngine.setOnClickListener {
            showSearchEnginesDialog()
        }

        binding.clTheme.setOnClickListener {
            showThemeSelectionDialog()
        }

        binding.fmDeleteHistory.setOnClickListener {
            /*ShowDeleteHistoryMainDialog(requireContext(), true, onDelete = {
                if (it)
                    clearHistory()
            })*/

            showDeleteHistoryConfirmationDialog()
        }


        binding.ivPrivacyPolicy.setOnClickListener {
            try {
                val builder: CustomTabsIntent.Builder = CustomTabsIntent.Builder()
                val customTabsIntent: CustomTabsIntent = builder.build()
                customTabsIntent.launchUrl(
                    requireContext(),
                    Uri.parse("https://utilitytoolboxapps.blogspot.com/2024/11/qr-scanner-privacy-policy.html")
                )
            } catch (e: Exception) {
                Log.e("TAG", "openCustomTabView:" + e.message)
            }
        }


    }

    fun showDeleteHistoryConfirmationDialog() {
        val dialog = ConfirmationDeleteFragDialog.newInstance(R.string.dlg_delete_clear_history_title)
        dialog.show(childFragmentManager, "")
    }

    private fun showSearchEnginesDialog() {
        showSearchEngineSelectionDialog()
    }



    private fun showCameraSelectionDialog() {
        MainCustomDialog(
            requireContext(),
            "Camera",
            SettingsMainNew.selectedCamera,
            addAgainSelection = null,
            R.layout.dialog_select_cam,
            R.array.cameraSelection
        ) { which, dismiss ->
            binding.tvSelectedCamera.text = resources.getStringArray(R.array.cameraSelection)[which]
            SettingsMainNew.selectedCamera = which
            SettingsMainNew.isBackCamera = which == 0
        }.show()
    }


    private val ITEMS = arrayOf(
        SearchEngineMain.BING,
        SearchEngineMain.DUCK_DUCK_GO,
        SearchEngineMain.GOOGLE,
        SearchEngineMain.QWANT,
        //  SearchEngineMain.STARTPAGE,
        SearchEngineMain.YAHOO,
        SearchEngineMain.YANDEX
    )

    private fun getPostionseletedSearchEngine(searchEngine: SearchEngineMain): Int {
        when (searchEngine) {
            SearchEngineMain.BING -> {
                return 0
            }

            SearchEngineMain.DUCK_DUCK_GO -> {
                return 1
            }

            SearchEngineMain.GOOGLE -> {
                return 2
            }

            SearchEngineMain.QWANT -> {
                return 3
            }

            /* SearchEngineMain.STARTPAGE -> {
                 return 4
             }*/

            SearchEngineMain.YAHOO -> {
                return 4
            }

            SearchEngineMain.YANDEX -> {
                return 5
            }

            else -> {
                return 2
            }
        }
    }


    private fun showSearchEngineSelectionDialog() {
        MainCustomDialog(
            requireContext(),
            "Search Engines",
            getPostionseletedSearchEngine(SettingsMainNew.searchEngine),
            addAgainSelection = null,
            R.layout.dialog_select_cam,
            R.array.searchEngineSelectiom
        ) { which, dismiss ->
            binding.tvSelectedSearchEngine.text =
                resources.getStringArray(R.array.searchEngineSelectiom)[which]
            SettingsMainNew.searchEngine = ITEMS[which]
        }.show()
    }


    private fun showThemeSelectionDialog() {
        MainCustomDialog(
            requireContext(),
            "Theme",
            getthemevalue(SettingsMainNew.theme),
            addAgainSelection = null,
            R.layout.dialog_select_cam,
            R.array.themeSelection
        ) { which, dismiss ->
            Log.d("kjdfjkdf87dfdfjkd", "here is which -> $which")
            SettingsMainNew.theme = setThemeVaue(which)
            binding.tvSeletedTheme.text = resources.getStringArray(R.array.themeSelection)[which]

        }.show()
    }


    private fun setThemeVaue(theme: Int): Int {
        when (theme) {
            0 -> {
                return AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            }

            1 -> {
                return AppCompatDelegate.MODE_NIGHT_NO
            }

            2 -> {
                return AppCompatDelegate.MODE_NIGHT_YES
            }

            else ->
                return AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }

    }

    private fun getthemevalue(theme: Int): Int {
        when (theme) {
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM -> {
                return 0
            }

            AppCompatDelegate.MODE_NIGHT_NO -> {
                return 1
            }

            AppCompatDelegate.MODE_NIGHT_YES -> {
                return 2
            }

            else ->
                return 0
        }

    }

    override fun onDeleteConfirmed() {
        clearHistory()
    }

    private fun clearHistory() {
        barcodeDatabase.clearAll()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { },
                ::showError
            )
            .addTo(disposable)
    }


    private fun performWebSearchUsingSearchEngine(searchEngine: SearchEngineMain) {
        SettingsMainNew.searchEngine = searchEngine
    }

}