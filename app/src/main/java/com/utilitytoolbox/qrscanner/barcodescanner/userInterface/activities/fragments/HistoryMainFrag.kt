package com.utilitytoolbox.qrscanner.barcodescanneruserInterface.activities.fragments

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.utilitytoolbox.qrscanner.barcodescanner.model
.MainBarcodeParsedNew
import com.utilitytoolbox.qrscanner.barcodescanner.usecase
.BarcodeDatabase
import com.utilitytoolbox.qrscanner.barcodescanner.model.MineBarCode
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.utilitytoolbox.qrscanner.barcodescanner.R
import com.utilitytoolbox.qrscanner.barcodescanner.adapter.HistoryAdapter
import com.utilitytoolbox.qrscanner.barcodescanner.databinding.FragFirstTabScanBinding
import com.utilitytoolbox.qrscanner.barcodescanner.databinding.FragSecondTabCreateBinding
import com.utilitytoolbox.qrscanner.barcodescanner.databinding.LayoutFragmentHistoryBinding
import com.utilitytoolbox.qrscanner.barcodescanner.dialogs.ConfirmationDeleteFragDialog
import com.utilitytoolbox.qrscanner.barcodescanner.utils.changeStatusBarClr
import com.utilitytoolbox.qrscanner.barcodescanner.utils.createFile
import com.utilitytoolbox.qrscanner.barcodescanner.utils.getHeaderListScan
import com.utilitytoolbox.qrscanner.barcodescanner.utils.orZero
import com.utilitytoolbox.qrscanner.barcodescanner.utils.showError
import com.utilitytoolbox.qrscanner.barcodescanner.utils.startScanFrag
import com.utilitytoolbox.qrscanner.barcodescanner.userInterface.activities.QRResultActivity
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
class HistoryMainFrag() : Fragment(), ConfirmationDeleteFragDialog.Listener {
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    lateinit var binding: LayoutFragmentHistoryBinding
    lateinit var mOptionSubMenuDialog: PopupWindow
    lateinit var mOptionMenuDialog: PopupWindow
    var listFragemt: ArrayList<Fragment> = arrayListOf()
    var touchableList: ArrayList<View>? = ArrayList()
    private val disposable = CompositeDisposable()

    @Inject
    lateinit var barcodeDatabase: BarcodeDatabase


    override fun onDestroyView() {
        super.onDestroyView()
        disposable.clear()
    }

    override fun onDeleteConfirmed() {
        clearHistory()
    }

    companion object {
        private const val PAGE_SIZE = 5000
        private const val TYPE_ALL = 0
        private const val TYPE_FAVORITES = 1
        private const val TYPE_KEY = "TYPE_KEY"
    }

    private fun showDeleteHistoryConfirmationDialog() {
        val dialog =
            ConfirmationDeleteFragDialog.newInstance(R.string.dlg_delete_clear_history_title)
        dialog.show(childFragmentManager, "")
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


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LayoutFragmentHistoryBinding.inflate(layoutInflater)
        return binding.root
    }

    private inner class ViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                1 -> {
                    val first = FirstTabFragment()
                    listFragemt.add(first)
                    return first
                }

                0 -> {
                    val second = SecondTabFragment()
                    listFragemt.add(second)
                    return second
                }

                else -> throw IllegalStateException("Unexpected position: $position")
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().changeStatusBarClr(isTransparent = false)

        binding.tvTitle.text = getString(R.string.history_title)
        val root = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DOWNLOADS
        ).toString()


        //supportEdgeToEdge()
        onClickListeners()

        viewPager = view.findViewById(R.id.viewPager)
        tabLayout = view.findViewById(R.id.tabLayout)

        val adapter = ViewPagerAdapter(this)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                1 -> "Create"
                0 -> "Scan"
                else -> null
            }


        }.attach()

        touchableList = tabLayout.touchables


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


        binding.ivSearch.setOnClickListener {
            binding.clSearch.visibility = View.VISIBLE
            viewPager.setUserInputEnabled(false);
            touchableList?.forEach { it.isEnabled = false }
        }

        binding.backBtn.setOnClickListener {
            binding.clSearch.visibility = View.GONE
            viewPager.setUserInputEnabled(true);
            touchableList?.forEach { it.isEnabled = true }
        }

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                when (viewPager.currentItem) {
                    0 -> {
                        (listFragemt.get(0) as FirstTabFragment).searchOnRC(s.toString())
                    }

                    1 -> {
                        (listFragemt.get(1) as SecondTabFragment).searchOnRC(s.toString())
                    }
                }


            }

            override fun afterTextChanged(s: Editable?) {}
        })


        binding.ivDelete.setOnClickListener {
            showDeleteHistoryConfirmationDialog()
        }

    }


    /* private fun showDeleteConfirmationDialog() {
         val builder = AlertDialog.Builder(requireContext(), R.style.DialogTheme) // Use 'this' for Activity or 'requireContext()' for Fragment
         builder.setTitle("Delete History")
             .setMessage("Are you sure you want to delete all history data?")
             .setPositiveButton("Delete") { dialog, which ->
                 // Call your delete method here
                 deleteHistoryData()
             }
             .setNegativeButton("Cancel") { dialog, which ->
                 dialog.dismiss() // Dismiss the dialog
             }
             .setCancelable(true) // Make the dialog cancelable

         val dialog = builder.create()
         dialog.show()
     }*/

    private fun deleteHistoryData() {
        barcodeDatabase.clearAll()
    }
    /* private fun init() {


    //     adapterOfHistory = AdapterOfHistory(arguments?.getInt(TYPE_KEY).orZero(), this)

 */

    /*
        when (arguments?.getInt(TYPE_KEY).orZero()) {
            TYPE_ALL -> {
                barcodeDatabase.getAllHistory().observe(viewLifecycleOwner, barcodeObserver!!)
            }

            TYPE_FAVORITES -> {
                barcodeDatabase.getFavoritesHistory().observe(viewLifecycleOwner, barcodeObserver!!)
            }

            else -> return
        }*/
    /*
        */
    /*val config = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPageSize(PAGE_SIZE)
            .build()

        val dataSource = when (arguments?.getInt(TYPE_KEY).orZero()) {
            TYPE_ALL -> barcodeDatabase.getAllCodes()
            TYPE_FAVORITES -> barcodeDatabase.getFavoritesCodes()
            else -> return
        }

        historyAdapter!!::submitList.let {
            RxPagedListBuilder(dataSource, config)
                .buildFlowable(BackpressureStrategy.LATEST)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    it,
                    ::showError
                )
                .addTo(disposable)
        }*/
    /*
    }
*/

    private fun optionsMenu(): PopupWindow {
        val inflater =
            activity?.getSystemService(AppCompatActivity.LAYOUT_INFLATER_SERVICE) as LayoutInflater
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

        /*editContent.setOnClickListener {
            mOptionMenuDialog.dismiss()
            fragment()

        }*/

        return PopupWindow(
            view,
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            true
        )
    }


    private fun optionsSubMenu(
        isScan: Boolean,
        isTXTExport: Boolean
    ): PopupWindow {
        val inflater =
            activity?.getSystemService(AppCompatActivity.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.layout_options_sub_menu, null)
        val shareQRData = view.findViewById<FrameLayout>(R.id.shareQRData)
        val saveCSV = view.findViewById<FrameLayout>(R.id.saveCSV)

        shareQRData.setOnClickListener {

            if (mOptionSubMenuDialog != null) {
                mOptionSubMenuDialog.dismiss()
            }
            mOptionMenuDialog.dismiss()

            /* createFile(
                 barcode, type,
                 isScan,
                 true,
                 isTXTExport
             )*/


            when (viewPager.currentItem) {
                0 -> {
                    (listFragemt.get(0) as FirstTabFragment).exportCSV(isTXTExport, true)
                }

                1 -> {
                    (listFragemt.get(1) as SecondTabFragment).exportCSV(isTXTExport, true)
                }
            }
        }

        saveCSV.setOnClickListener {

            if (mOptionSubMenuDialog != null) {
                mOptionSubMenuDialog.dismiss()
            }

            mOptionMenuDialog.dismiss()

            when (viewPager.currentItem) {
                0 -> {
                    (listFragemt.get(0) as FirstTabFragment).exportCSV(isTXTExport, false)
                }

                1 -> {
                    (listFragemt.get(1) as SecondTabFragment).exportCSV(isTXTExport, false)
                }
            }
        }

        return PopupWindow(
            view,
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            true
        )
    }

    override fun onDestroy() {
        super.onDestroy()
    }


    private fun onClickListeners() {
        /*binding.toolbar.setOnMenuItemClickListener { item ->
             when (item.itemId) {
                 R.id.item_clear_qr_history -> {
                     if (adapterOfHistory?.getData()?.isNotEmpty() == true) {
                         showDeleteHistoryConfirmationDialog()
                     } else {
                         Toast.makeText(
                             requireContext(),
                             getString(R.string.no_data),
                             Toast.LENGTH_SHORT
                         ).show()
                     }
                 }
             }
             return@setOnMenuItemClickListener true
         }*/


    }


    @AndroidEntryPoint
    class FirstTabFragment : Fragment(R.layout.frag_second_tab_create),
        HistoryAdapter.Listener {
        // Your code here
        private var historyAdapter: HistoryAdapter? = null
        private var MineBarCodeObserver: Observer<MutableList<MineBarCode>>? = null
        lateinit var binding: FragSecondTabCreateBinding
        private var mHistoryData: MutableList<MineBarCode> = mutableListOf()
        private val disposable = CompositeDisposable()
        var myDir: File? = null

        @Inject
        lateinit var barcodeDatabase: BarcodeDatabase

        open fun searchOnRC(s: String) {
            val filteredList =
                mHistoryData.filter { it.formattedText.contains(s, ignoreCase = true) }
            setDataToADapter(filteredList.toMutableList())
        }


        open fun exportCSV(isTXTExport: Boolean, isShare: Boolean) {

            if (mHistoryData.size > 0) {
                var list: ArrayList<MainBarcodeParsedNew> = arrayListOf()
                mHistoryData.forEach {
                    list.add(MainBarcodeParsedNew(it))
                }

                val result: Boolean = requireActivity().createFile(
                    list, "",
                    true,
                    isShare,
                    isTXTExport
                )
                if (result) {
                    if (isTXTExport) {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.txt_data_saved) + "in " + myDir?.getAbsolutePath(),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.csv_data_saved) + "in " + myDir?.getAbsolutePath(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Something went wrong",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } else {
                Toast.makeText(requireContext(), "No Data Found", Toast.LENGTH_SHORT).show()
            }

        }


        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            binding = FragSecondTabCreateBinding.inflate(layoutInflater)
            return binding.root
        }


        override fun onDestroy() {
            super.onDestroy()
            MineBarCodeObserver?.let {
                when (arguments?.getInt(TYPE_KEY).orZero()) {
                    TYPE_ALL -> {
                        barcodeDatabase.getAllHistory().removeObserver(MineBarCodeObserver!!)
                    }

                    TYPE_FAVORITES -> {
                        barcodeDatabase.getFavoritesHistory().removeObserver(MineBarCodeObserver!!)
                    }

                    else -> return
                }
            }
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            CoroutineScope(Dispatchers.IO).launch {
                val root = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS
                ).toString()
                myDir = File("$root/QRScannerBYWAQAS")
            }


            historyAdapter = HistoryAdapter(true, this)


            binding.btnScan.setOnClickListener {
                startScanFrag?.invoke(true)
            }

            binding.rvHistory.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = historyAdapter
            }


            barcodeDatabase.getAllHistory().observe(viewLifecycleOwner) { data ->
                Log.d("fdsjfls987sflsfljs", "here s called and size-> ${data.size}")
                mHistoryData = data.filter { !it.isScanned }.toMutableList()
                Log.d("fdsjfls987sflsfljs", "here s called and size after-> ${mHistoryData.size}")
                setDataToADapter(mHistoryData.toMutableList())

            }

        }

        fun setDataToADapter(lsit: MutableList<MineBarCode>) {
            if (lsit.isNotEmpty()) {
                historyAdapter?.setData(getHeaderListScan(lsit).toMutableList())
                binding.liNoData.isVisible = false
            } else {
                historyAdapter?.setData(getHeaderListScan(lsit).toMutableList())
                //show No data placeHolder
                binding.liNoData.isVisible = true
            }
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
            QRResultActivity.start(requireActivity(), MineBarCode)
        }

        override fun onDeleteClicked(MineBarCode: MineBarCode) {
            onDelete(MineBarCode)
        }

        override fun onFavoriteClicked(MineBarCode: MineBarCode) {
            toggleIsFavorite(MineBarCode)
        }


        override fun onDestroyView() {
            super.onDestroyView()
            disposable.clear()
        }
    }


    @AndroidEntryPoint
    class SecondTabFragment : Fragment(R.layout.frag_first_tab_scan),
        HistoryAdapter.Listener {
        // Your code here
        private var historyAdapter: HistoryAdapter? = null
        private val disposable = CompositeDisposable()

        private var mHistoryData: MutableList<MineBarCode> = mutableListOf()

        @Inject
        lateinit var barcodeDatabase: BarcodeDatabase

        private var MineBarCodeObserver: Observer<MutableList<MineBarCode>>? = null
        lateinit var binding: FragFirstTabScanBinding
        var myDir: File? = null


        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            binding = FragFirstTabScanBinding.inflate(layoutInflater)
            return binding.root
        }


        override fun onDestroy() {
            super.onDestroy()
            MineBarCodeObserver?.let {
                when (arguments?.getInt(TYPE_KEY).orZero()) {
                    TYPE_ALL -> {
                        barcodeDatabase.getAllHistory().removeObserver(MineBarCodeObserver!!)
                    }

                    TYPE_FAVORITES -> {
                        barcodeDatabase.getFavoritesHistory().removeObserver(MineBarCodeObserver!!)
                    }

                    else -> return
                }
            }
        }

        open fun searchOnRC(s: String) {
            val filteredList =
                mHistoryData.filter { it.formattedText.contains(s, ignoreCase = true) }
            setDataToAdapter(filteredList.toMutableList())
        }

        open fun exportCSV(isTXTExport: Boolean, isShare: Boolean) {

            if (mHistoryData.size > 0) {
                var list: ArrayList<MainBarcodeParsedNew> = arrayListOf()
                mHistoryData.forEach {
                    list.add(MainBarcodeParsedNew(it))
                }

                val result: Boolean = requireActivity().createFile(
                    list, "",
                    true,
                    isShare,
                    isTXTExport
                )
                if (result) {
                    if (isTXTExport) {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.txt_data_saved) + "in " + myDir?.getAbsolutePath(),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.csv_data_saved) + "in " + myDir?.getAbsolutePath(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Something went wrong",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } else {
                Toast.makeText(requireContext(), "No Data Found", Toast.LENGTH_SHORT).show()
            }
        }


        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            CoroutineScope(Dispatchers.IO).launch {
                val root = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS
                ).toString()
                myDir = File("$root/QRScannerBYWAQAS")
            }

            historyAdapter = HistoryAdapter(true, this)

            binding.toolbarSettingLayout.visibility = View.GONE

            binding.rvHistory.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = historyAdapter
            }

            barcodeDatabase.getAllHistory().observe(viewLifecycleOwner) { data ->
                Log.d("fdsjfls987sflsfljs", "here s called and size-> ${data.size}")
                mHistoryData.clear()
                mHistoryData = data.filter { it.isScanned }.toMutableList()

                Log.d("fdsjfls987sflsfljs", "here s called and size after-> ${mHistoryData.size}")
                setDataToAdapter(mHistoryData)

            }

            binding.btnScan.setOnClickListener {
                startScanFrag?.invoke(true)
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
            QRResultActivity.start(requireActivity(), MineBarCode)
        }

        override fun onDeleteClicked(MineBarCode: MineBarCode) {
            onDelete(MineBarCode)
        }


        override fun onFavoriteClicked(MineBarCode: MineBarCode) {
            toggleIsFavorite(MineBarCode)
        }


        override fun onDestroyView() {
            super.onDestroyView()
            disposable.clear()
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


}