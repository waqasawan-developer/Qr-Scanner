package com.utilitytoolbox.qrscanner.barcodescanner.userInterface.activities

import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.utilitytoolbox.qrscanner.barcodescanner.R
import com.utilitytoolbox.qrscanner.barcodescanner.model
.MainBarcodeParsedNew
import com.utilitytoolbox.qrscanner.barcodescanner.utils.createFile
import com.utilitytoolbox.qrscanner.barcodescanner.utils.getHeaderListScan
import com.utilitytoolbox.qrscanner.barcodescanner.utils.orZero
import com.utilitytoolbox.qrscanner.barcodescanner.utils.showError
import com.utilitytoolbox.qrscanner.barcodescanner.adapter.HistoryAdapter
import com.utilitytoolbox.qrscanner.barcodescanner.databinding
.FragFirstTabScanBinding
import com.utilitytoolbox.qrscanner.barcodescanner.model.MineBarCode
import com.utilitytoolbox.qrscanner.barcodescanner.usecase
.BarcodeDatabase
import com.utilitytoolbox.qrscanner.barcodescanner.usecase
.SettingsMainNew
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint

class BatchScanningFavHistory : AppCompatActivity(),
    HistoryAdapter.Listener {
    // Your code here
    private var historyAdapter: HistoryAdapter? = null
    private val disposable = CompositeDisposable()

    private var mHistoryData: MutableList<MineBarCode> = mutableListOf()
    @Inject
    lateinit var SettingsMainNew: SettingsMainNew
    @Inject
    lateinit var barcodeDatabase: BarcodeDatabase

    lateinit var binding: FragFirstTabScanBinding
    var myDir: File? = null

    companion object {
        private const val PAGE_SIZE = 5000
        private const val TYPE_ALL = 0
        private const val TYPE_FAVORITES = 1
        private const val TYPE_KEY = "TYPE_KEY"
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }

    open fun searchOnRC(s: String) {
        val filteredList =
            mHistoryData.filter { it.formattedText.contains(s, ignoreCase = true) }
        setDataToAdapter(filteredList.toMutableList())
    }

    open fun exportCSV(isTXTExport: Boolean, isShare: Boolean) {

        if (mHistoryData.size > 0) {
            var list: ArrayList<MainBarcodeParsedNew > = arrayListOf()
            mHistoryData.forEach {
                list.add(MainBarcodeParsedNew(it))
            }

            val result: Boolean = createFile(
                list, "",
                true,
                isShare,
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

        } else {
            Toast.makeText(this, "No Data Found", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = FragFirstTabScanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        CoroutineScope(Dispatchers.IO).launch {
            val root = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS
            ).toString()
            myDir = File("$root/QRScannerBYWAQAS")
        }

        historyAdapter = HistoryAdapter(SettingsMainNew.saveQrCodesToHistory, this)

        binding.toolbarSettingLayout.visibility = View.VISIBLE

        binding.rvHistory.apply {
            layoutManager = LinearLayoutManager(this@BatchScanningFavHistory)
            adapter = historyAdapter
        }

        binding.backBtn.setOnClickListener {
            finish()
        }


        when (intent.getIntExtra(TYPE_KEY, TYPE_FAVORITES).orZero()) {
            TYPE_ALL -> {
                binding.itemTxt.text = "Batch Scan Results"


                val  barcodeList = intent.getSerializableExtra("BARCODE_LIST") as? MutableList<MineBarCode>

                barcodeList?.let {
                    setDataToAdapter(it)
                }

            }

            TYPE_FAVORITES -> {
                binding.itemTxt.text = "FAVORITES"
                barcodeDatabase.getFavoritesHistory().observe(this) { it2 ->
                    Log.d("fdsjfls987sflsfljs", "here s called and size-> ${it2.size}")
                    mHistoryData.clear()
                    mHistoryData = it2 /* it2.filter { it.isScanned }.toMutableList()*/

                    setDataToAdapter(mHistoryData)
                }
            }

            else -> return
        }


        binding.btnScan.setOnClickListener {
            finish()
        }
    }

    fun setDataToAdapter(list: MutableList<MineBarCode>) {
        if (list.isNotEmpty()) {
            historyAdapter?.setData(getHeaderListScan(list).toMutableList())
            binding.liNoData.isVisible = false
        } else {
            historyAdapter?.setData(getHeaderListScan(list).toMutableList())
            //show No data placeHolder
            binding.liNoData.isVisible = true
        }
    }

    private fun toggleIsFavorite(MineBarCode: MineBarCode) {
        try {
            val newBarcode = MineBarCode.copy(isFavorite = MineBarCode.isFavorite.not())
            barcodeDatabase.insertCode(newBarcode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {

                    }, {}
                )
                .addTo(disposable)
        } catch (e: java.lang.Exception) {
            Log.e("TAG", "toggleIsFavorite: ${e.message}")
        }
    }

    override fun onBarcodeClicked(MineBarCode: MineBarCode) {
        QRResultActivity.start(this, MineBarCode)
    }

    override fun onDeleteClicked(MineBarCode: MineBarCode) {
        onDelete(MineBarCode)
    }

    override fun onFavoriteClicked(MineBarCode: MineBarCode) {
        toggleIsFavorite(MineBarCode)
    }

    private fun onDelete(code: MineBarCode) {
        barcodeDatabase.deleteCode(code.id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { },
                { error ->

                    showError(error)
                }
            )
            .addTo(disposable)
    }

}
