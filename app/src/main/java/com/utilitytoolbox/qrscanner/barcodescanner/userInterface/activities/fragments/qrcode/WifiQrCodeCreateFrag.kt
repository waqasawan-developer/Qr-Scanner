package com.utilitytoolbox.qrscanner.barcodescanner.userInterface.activities.fragments.qrcode

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.utilitytoolbox.qrscanner.barcodescanner.utils.createOrCancelQr
import com.utilitytoolbox.qrscanner.barcodescanner.utils.textString
import com.google.gson.Gson
import com.utilitytoolbox.qrscanner.barcodescanner.R
import com.utilitytoolbox.qrscanner.barcodescanner.databinding.LayoutFragmentQrCodeWifiBinding
import com.utilitytoolbox.qrscanner.barcodescanner.model.MineBarCode
import com.utilitytoolbox.qrscanner.barcodescanner.model.schema
.Schema
import com.utilitytoolbox.qrscanner.barcodescanner.model.schema
.WifiModel
import com.utilitytoolbox.qrscanner.barcodescanner.userInterface.activities.fragments.BaseFragment

class WifiQrCodeCreateFrag : BaseFragment() {

    lateinit var binding: LayoutFragmentQrCodeWifiBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LayoutFragmentQrCodeWifiBinding.inflate(layoutInflater)
        return binding.root
    }

    companion object {
        private const val DEFAULT_TEXT_Barcode = "DEFAULT_TEXT_Barcode"

        fun newInstance(barcodemain: MineBarCode): WifiQrCodeCreateFrag {
            return WifiQrCodeCreateFrag().apply {
                arguments = Bundle().apply {
                    putSerializable(DEFAULT_TEXT_Barcode, barcodemain)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEncryptionTypesSpinner()
        initNetworkNameEditText()
        handleTextChanged()


        val MineBarCode: MineBarCode? = arguments?.getSerializable(DEFAULT_TEXT_Barcode) as? MineBarCode


        Log.d("kjdfd8sd8fsfk","here is barcode-> ${Gson().toJson(MineBarCode)}")

        binding.tvCancel.setOnClickListener {
            createOrCancelQr?.invoke(false)
        }

        binding.tvCreate.setOnClickListener {
            createOrCancelQr?.invoke(true)
        }


    }

    override fun getBarcodeSchema(): Schema {
        val encryption = when (binding.spEncryption.selectedItemPosition) {
            0 -> "WPA"
            1 -> "WEP"
            2 -> "nopass"
            else -> "nopass"
        }
        return WifiModel(
            encryption = encryption,
            name = binding.etNetworkName.textString,
            password = binding.etPassword.textString,
            isHidden = binding.cbIsHidden.isChecked
        )
    }

    private fun initEncryptionTypesSpinner() {
        binding.spEncryption.adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.wifi_encryption,
            R.layout.item_spinner
        ).apply {
            setDropDownViewResource(R.layout.item_spinner_dropdown)
        }

        binding.spEncryption.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                binding.textInputLayoutPassword.isVisible = position != 2
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
    }

    private fun initNetworkNameEditText() {
        binding.etNetworkName.requestFocus()
    }

    private fun handleTextChanged() {
        binding.etNetworkName.addTextChangedListener { toggleCreateBarcodeButton() }
        binding.etPassword.addTextChangedListener { toggleCreateBarcodeButton() }
    }

    private fun toggleCreateBarcodeButton() {
        //   parentActivity.buttonEnabled = binding.etNetworkName.isNotBlank()
    }
}