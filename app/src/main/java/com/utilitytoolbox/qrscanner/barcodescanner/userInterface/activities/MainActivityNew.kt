package com.utilitytoolbox.qrscanner.barcodescanner.userInterface.activities

import android.content.Intent
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.utilitytoolbox.qrscanner.barcodescanner.utils.disableBatchScanningMode
import com.utilitytoolbox.qrscanner.barcodescanner.utils.onBatchScanningEnabled
import com.utilitytoolbox.qrscanner.barcodescanner.utils.showBatchScanningFavHistory
import com.utilitytoolbox.qrscanner.barcodescanner.utils.showBatchScanningFromHistory
import com.utilitytoolbox.qrscanner.barcodescanner.utils.startScanFrag
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.utilitytoolbox.qrscanner.barcodescanner.R
import com.utilitytoolbox.qrscanner.barcodescanner.userInterface.activities.fragments.CameraFragment

import com.utilitytoolbox.qrscanner.barcodescanner.databinding.ActivityMainNewBinding
import com.utilitytoolbox.qrscanner.barcodescanner.userInterface.activities.fragments.CreateFrag
import com.utilitytoolbox.qrscanner.barcodescanneruserInterface.activities.fragments.HistoryMainFrag
import com.utilitytoolbox.qrscanner.barcodescanner.userInterface.activities.fragments.SettingsFrag
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivityNew : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener,
    Parcelable {

    // private lateinit var containerView: FrameLayout
    private var initLayoutComp = false
    lateinit var binding: ActivityMainNewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainNewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //supportEdgeToEdge()
        init()
        onClickListeners()
        initBottomNavigationView()

        showInitialFragment()

        highlightCurntSelection(2)

        startScanFrag = {
            //  showInitialFragment()
            highlightCurntSelection(2)
            navigateDestinationScreen(2)
        }

        showBatchScanningFromHistory = {
            showHistoryFragment()
        }

        showBatchScanningFavHistory = { fav,list ->
            if (fav) {
                val intent = Intent(this, BatchScanningFavHistory::class.java).apply {
                    putExtra("TYPE_KEY", 1)
                }
                startActivity(intent)
            } else {

                val intent = Intent(this, BatchScanningFavHistory::class.java).apply {
                    putExtra("TYPE_KEY",  0)
                    putExtra("BARCODE_LIST", list)
                }
                startActivity(intent)
            }

        }


    }

    private fun showHistoryFragment() {
        showFragment(R.id.item_history_view)
        highlightCurntSelection(1)
        binding.bnView.selectedItemId = R.id.item_history_view
        if (isBatchScanningViewAdded) {
            disableBatchScanningMode?.invoke()
        }
    }

    private fun highlightCurntSelection(selection: Int = 2) {
        when (selection) {
            0 -> {
                //  binding.clCreateSelectedView.setBackgroundResource(R.drawable.selected_view_bg)
                (binding.clCreateSelectedView.getChildAt(0) as ImageView).setColorFilter(
                    ContextCompat.getColor(this, R.color.color_accent)
                )
                binding.tvCreate.setTextColor(ContextCompat.getColor(this, R.color.color_accent))
                binding.clHistorySelectedView.setBackgroundResource(0)
                (binding.clHistorySelectedView.getChildAt(0) as ImageView).setColorFilter(
                    ContextCompat.getColor(this, R.color.unSelectedTextColor)
                )
                binding.tvHistory.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.unSelectedTextColor
                    )
                )


                binding.clScanSelectedView.setBackgroundResource(0)
                (binding.clScanSelectedView.getChildAt(0) as ImageView).setColorFilter(
                    ContextCompat.getColor(this, R.color.unSelectedTextColor)
                )
                binding.tvScan.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.unSelectedTextColor
                    )
                )

                /*   binding.clFavoriteSelectedView.setBackgroundResource(0)
                   (binding.clFavoriteSelectedView.getChildAt(0) as ImageView).setColorFilter(
                       Color.argb(255, 255, 255, 255)
                   )*/
                binding.clSettingSelectedView.setBackgroundResource(0)
                (binding.clSettingSelectedView.getChildAt(0) as ImageView).setColorFilter(
                    ContextCompat.getColor(this, R.color.black)
                )
                binding.tvSettings.setTextColor(ContextCompat.getColor(this, R.color.black))

            }

            1 -> {
                binding.clCreateSelectedView.setBackgroundResource(0)
                (binding.clCreateSelectedView.getChildAt(0) as ImageView).setColorFilter(
                    ContextCompat.getColor(this, R.color.unSelectedTextColor)
                )
                binding.tvCreate.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.unSelectedTextColor
                    )
                )

                //  binding.clHistorySelectedView.setBackgroundResource(R.drawable.selected_view_bg)
                (binding.clHistorySelectedView.getChildAt(0) as ImageView).setColorFilter(
                    ContextCompat.getColor(this, R.color.color_accent)
                )

                binding.tvHistory.setTextColor(ContextCompat.getColor(this, R.color.color_accent))

                binding.clScanSelectedView.setBackgroundResource(0)
                (binding.clScanSelectedView.getChildAt(0) as ImageView).setColorFilter(
                    ContextCompat.getColor(this, R.color.unSelectedTextColor)
                )

                binding.tvScan.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.unSelectedTextColor
                    )
                )

                /* binding.clFavoriteSelectedView.setBackgroundResource(0)
                 (binding.clFavoriteSelectedView.getChildAt(0) as ImageView).setColorFilter(
                     Color.argb(255, 255, 255, 255)
                 )*/
                binding.clSettingSelectedView.setBackgroundResource(0)
                (binding.clSettingSelectedView.getChildAt(0) as ImageView).setColorFilter(
                    ContextCompat.getColor(this, R.color.unSelectedTextColor)
                )
                binding.tvSettings.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.unSelectedTextColor
                    )
                )

            }

            2 -> {
                binding.clCreateSelectedView.setBackgroundResource(0)
                (binding.clCreateSelectedView.getChildAt(0) as ImageView).setColorFilter(
                    ContextCompat.getColor(this, R.color.unSelectedTextColor)
                )
                binding.tvCreate.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.unSelectedTextColor
                    )
                )

                binding.clHistorySelectedView.setBackgroundResource(0)
                (binding.clHistorySelectedView.getChildAt(0) as ImageView).setColorFilter(
                    ContextCompat.getColor(this, R.color.unSelectedTextColor)
                )
                binding.tvHistory.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.unSelectedTextColor
                    )
                )

                //  binding.clScanSelectedView.setBackgroundResource(R.drawable.selected_view_bg)
                (binding.clScanSelectedView.getChildAt(0) as ImageView).setColorFilter(
                    ContextCompat.getColor(this, R.color.color_accent)
                )
                binding.tvScan.setTextColor(ContextCompat.getColor(this, R.color.color_accent))
                binding.tvSettings.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.unSelectedTextColor
                    )
                )

                /*    binding.clFavoriteSelectedView.setBackgroundResource(0)
                    (binding.clFavoriteSelectedView.getChildAt(0) as ImageView).setColorFilter(
                        Color.argb(255, 255, 255, 255)
                    )*/
                binding.clSettingSelectedView.setBackgroundResource(0)
                (binding.clSettingSelectedView.getChildAt(0) as ImageView).setColorFilter(
                    ContextCompat.getColor(this, R.color.unSelectedTextColor)
                )
            }

            3 -> {
                binding.clCreateSelectedView.setBackgroundResource(0)
                (binding.clCreateSelectedView.getChildAt(0) as ImageView).setColorFilter(
                    ContextCompat.getColor(this, R.color.unSelectedTextColor)
                )
                binding.tvCreate.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.unSelectedTextColor
                    )
                )

                binding.clHistorySelectedView.setBackgroundResource(0)
                (binding.clHistorySelectedView.getChildAt(0) as ImageView).setColorFilter(
                    ContextCompat.getColor(this, R.color.unSelectedTextColor)
                )
                binding.tvHistory.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.unSelectedTextColor
                    )
                )

                binding.clScanSelectedView.setBackgroundResource(0)
                (binding.clScanSelectedView.getChildAt(0) as ImageView).setColorFilter(
                    ContextCompat.getColor(this, R.color.unSelectedTextColor)
                )

                binding.tvScan.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.unSelectedTextColor
                    )
                )

                /* binding.clFavoriteSelectedView.setBackgroundResource(R.drawable.selected_view_bg)
                 (binding.clFavoriteSelectedView.getChildAt(0) as ImageView).setColorFilter(
                     ContextCompat.getColor(this, R.color.color_accent)
                 )*/

                binding.clSettingSelectedView.setBackgroundResource(0)
                (binding.clSettingSelectedView.getChildAt(0) as ImageView).setColorFilter(
                    ContextCompat.getColor(this, R.color.unSelectedTextColor)
                )
            }

            4 -> {
                binding.clCreateSelectedView.setBackgroundResource(0)
                (binding.clCreateSelectedView.getChildAt(0) as ImageView).setColorFilter(
                    ContextCompat.getColor(this, R.color.unSelectedTextColor)
                )
                binding.tvCreate.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.unSelectedTextColor
                    )
                )


                binding.clHistorySelectedView.setBackgroundResource(0)
                (binding.clHistorySelectedView.getChildAt(0) as ImageView).setColorFilter(
                    ContextCompat.getColor(this, R.color.unSelectedTextColor)
                )
                binding.tvHistory.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.unSelectedTextColor
                    )
                )

                binding.clScanSelectedView.setBackgroundResource(0)
                (binding.clScanSelectedView.getChildAt(0) as ImageView).setColorFilter(
                    ContextCompat.getColor(this, R.color.unSelectedTextColor)
                )
                binding.tvScan.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.unSelectedTextColor
                    )
                )

                /* binding.clFavoriteSelectedView.setBackgroundResource(0)
                 (binding.clFavoriteSelectedView.getChildAt(0) as ImageView).setColorFilter(
                     Color.argb(255, 255, 255, 255)
                 )*/
                //   binding.clSettingSelectedView.setBackgroundResource(R.drawable.selected_view_bg)
                (binding.clSettingSelectedView.getChildAt(0) as ImageView).setColorFilter(
                    ContextCompat.getColor(this, R.color.color_accent)
                )
                binding.tvSettings.setTextColor(ContextCompat.getColor(this, R.color.color_accent))
            }
        }
    }

    private fun showInitialFragment() {
        showFragment(R.id.item_scan_qr)
    }

    private fun initBottomNavigationView() {
        binding.bnView.apply {
            setOnNavigationItemSelectedListener(this@MainActivityNew)
        }
    }

    private fun navigateDestinationScreen(destinationBit: Int) {
        when (destinationBit) {
            0 -> {
                highlightCurntSelection(destinationBit)
                binding.bnView.selectedItemId = R.id.item_create_qr
            }

            1 -> {
                highlightCurntSelection(destinationBit)
                binding.bnView.selectedItemId = R.id.item_history_view
            }

            2 -> {
                highlightCurntSelection(destinationBit)
                binding.bnView.selectedItemId = R.id.item_scan_qr
            }

            3 -> {
                highlightCurntSelection(destinationBit)
                binding.bnView.selectedItemId = R.id.item_favorite_history_view
            }

            4 -> {
                highlightCurntSelection(destinationBit)
                binding.bnView.selectedItemId = R.id.item_settings_view
            }
        }
    }

    private fun onClickListeners() {

        binding.clSettings.setOnClickListener {
            navigateDestinationScreen(4)
        }

        binding.clScan.setOnClickListener {
            navigateDestinationScreen(2)
        }

        binding.clHistory.setOnClickListener {
            navigateDestinationScreen(1)
        }

        binding.clCreate.setOnClickListener {
            navigateDestinationScreen(0)
        }
    }

    private fun init() {
        onBatchScanningEnabled = false
        isBatchScanningViewAdded = false
        //   containerView = findViewById(R.id.fl_bannerAdView)

//        onBannerAdViewAdded = { height, isAdLoadFailed ->
//            if (isAdLoadFailed) {
//                val params = binding.flFragmentContainer.layoutParams as MarginLayoutParams
//                params.setMargins(0, 0, 0, 0)
//                binding.flFragmentContainer.layoutParams = params
//            } else {
//                val params = binding.flFragmentContainer.layoutParams as MarginLayoutParams
//                params.setMargins(0, 0, 0, height)
//                binding.flFragmentContainer.layoutParams = params
//            }
//        }


    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        if (item.itemId == binding.bnView.selectedItemId) {
            return false
        }
        showFragment(item.itemId)
        // changeStatusBarColor(item.itemId)
        return true
    }

    companion object CREATOR : Parcelable.Creator<MainActivityNew> {
        override fun createFromParcel(parcel: Parcel): MainActivityNew {
            return MainActivityNew()
        }

        override fun newArray(size: Int): Array<MainActivityNew?> {
            return arrayOfNulls(size)
        }

        var isBatchScanningViewAdded = false

    }

    private fun showFragment(bottomItemId: Int) {
        if (isBatchScanningViewAdded) {
            disableBatchScanningMode?.invoke()
        }
        val fragment = when (bottomItemId) {
            R.id.item_scan_qr -> CameraFragment()
            R.id.item_create_qr -> CreateFrag()
            R.id.item_history_view -> HistoryMainFrag()
            R.id.item_settings_view -> SettingsFrag()
            else -> null
        }
        fragment?.apply(::replaceFragment)
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fl_fragment_container, fragment)
            .setReorderingAllowed(true)
            .commit()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeByte(if (initLayoutComp) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun onResume() {
        super.onResume()
        Log.e("test", "onResume")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("test", "destroy")
    }
}