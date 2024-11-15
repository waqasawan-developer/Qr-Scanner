package com.utilitytoolbox.qrscanner.barcodescanner.userInterface.activities.fragments.qrcode

import android.content.Intent
import android.content.pm.ResolveInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.utilitytoolbox.qrscanner.barcodescanner.utils.showError
import com.utilitytoolbox.qrscanner.barcodescanner.databinding.LayoutFragmentQrCodeAppBinding
import com.utilitytoolbox.qrscanner.barcodescanner.model.MineBarCode
import com.utilitytoolbox.qrscanner.barcodescanner.model.schema.MainApps
import com.utilitytoolbox.qrscanner.barcodescanner.model.schema
.Schema
import com.utilitytoolbox.qrscanner.barcodescanner.userInterface.activities.fragments.BaseFragment
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers

class AppQrCodeCreateFrag : BaseFragment() {
    private val disposable = CompositeDisposable()
  //  private val appplicationAdapter by unsafeLazy { AppplicationAdapter(parentActivity) }

    lateinit var binding: LayoutFragmentQrCodeAppBinding

    companion object{
        private const val DEFAULT_TEXT_Barcode = "DEFAULT_TEXT_Barcode"

        fun newInstance(barcodemain: MineBarCode): AppQrCodeCreateFrag {
            return AppQrCodeCreateFrag().apply {
                arguments = Bundle().apply {
                    putSerializable(DEFAULT_TEXT_Barcode, barcodemain)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LayoutFragmentQrCodeAppBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        loadApps()
    }

    override fun getBarcodeSchema(): Schema {
        return MainApps.fromPackage("")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        disposable.clear()
    }

    private fun initRecyclerView() {
        binding.rvApps.apply {
            layoutManager = LinearLayoutManager(requireContext())
           // adapter = appplicationAdapter
        }
    }

    private fun loadApps() {
        showLoading(true)

        Single.just(getApps())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { apps ->
                    showLoading(false)
                    showApps(apps)
                },
                { error ->
                    showLoading(false)
                    showError(error)
                }
            )
            .addTo(disposable)
    }

    private fun getApps(): List<ResolveInfo> {
        val mainIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        return requireContext().packageManager
            .queryIntentActivities(mainIntent, 0)
            .filter { it.activityInfo?.packageName != null }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.pbLoading.isVisible = isLoading
        binding.rvApps.isVisible = isLoading.not()
    }

    private fun showApps(apps: List<ResolveInfo>) {
       // appplicationAdapter.apps = apps
    }
}