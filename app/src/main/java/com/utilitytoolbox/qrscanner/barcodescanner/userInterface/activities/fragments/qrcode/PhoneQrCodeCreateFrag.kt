package com.utilitytoolbox.qrscanner.barcodescanner.userInterface.activities.fragments.qrcode

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import com.utilitytoolbox.qrscanner.barcodescanner.utils.createOrCancelQr
import com.utilitytoolbox.qrscanner.barcodescanner.utils.textString
import com.utilitytoolbox.qrscanner.barcodescanner.databinding.LayoutFragmentQrCodePhoneBinding
import com.utilitytoolbox.qrscanner.barcodescanner.model.MineBarCode
import com.utilitytoolbox.qrscanner.barcodescanner.model.schema
.PhoneModel
import com.utilitytoolbox.qrscanner.barcodescanner.model.schema
.Schema
import com.utilitytoolbox.qrscanner.barcodescanner.userInterface.activities.fragments.BaseFragment

class PhoneQrCodeCreateFrag : BaseFragment() {

    companion object{
        private const val DEFAULT_TEXT_Barcode = "DEFAULT_TEXT_Barcode"


        fun newInstance(barcodemain: MineBarCode): PhoneQrCodeCreateFrag {
            return PhoneQrCodeCreateFrag().apply {
                arguments = Bundle().apply {
                    putSerializable(DEFAULT_TEXT_Barcode, barcodemain)
                }
            }
        }
    }

    lateinit var binding: LayoutFragmentQrCodePhoneBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LayoutFragmentQrCodePhoneBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEditText()
        handleTextChanged()

        binding.tvCancel.setOnClickListener {
            createOrCancelQr?.invoke(false)
        }

        binding.tvCreate.setOnClickListener {
            createOrCancelQr?.invoke(true)

        }
    }

    override fun showPhone(phone: String) {
        binding.etPhone.apply {
            setText(phone)
            setSelection(phone.length)
        }
    }

    override fun getBarcodeSchema(): Schema {
        return PhoneModel(binding.etPhone.textString)
    }

    private fun initEditText() {
        binding.etPhone.requestFocus()
    }

    private fun handleTextChanged() {
        binding.etPhone.addTextChangedListener {
          //  parentActivity.buttonEnabled = binding.etPhone.isNotBlank()
        }
    }
}