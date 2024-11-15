package com.utilitytoolbox.qrscanner.barcodescanner.userInterface.activities.fragments.qrcode

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import com.utilitytoolbox.qrscanner.barcodescanner.utils.createOrCancelQr
import com.utilitytoolbox.qrscanner.barcodescanner.utils.textString
import com.utilitytoolbox.qrscanner.barcodescanner.databinding.LayoutFragmentQrCodeSmsBinding
import com.utilitytoolbox.qrscanner.barcodescanner.model.MineBarCode
import com.utilitytoolbox.qrscanner.barcodescanner.model.schema
.Schema
import com.utilitytoolbox.qrscanner.barcodescanner.model.schema
.SmsModel
import com.utilitytoolbox.qrscanner.barcodescanner.userInterface.activities.fragments.BaseFragment

class SmsQrCodeCreateFrag : BaseFragment() {

    lateinit var binding: LayoutFragmentQrCodeSmsBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LayoutFragmentQrCodeSmsBinding.inflate(layoutInflater)
        return binding.root
    }


    companion object{
        private const val DEFAULT_TEXT_Barcode = "DEFAULT_TEXT_Barcode"

        fun newInstance(barcodemain: MineBarCode): SmsQrCodeCreateFrag {
            return SmsQrCodeCreateFrag().apply {
                arguments = Bundle().apply {
                    putSerializable(DEFAULT_TEXT_Barcode, barcodemain)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initTitleEditText()
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
        return SmsModel(
            phone = binding.etPhone.textString,
            message = binding.etMessage.textString
        )
    }

    private fun initTitleEditText() {
        binding.etPhone.requestFocus()
    }

    private fun handleTextChanged() {
        binding.etPhone.addTextChangedListener { toggleCreateBarcodeButton() }
        binding.etMessage.addTextChangedListener { toggleCreateBarcodeButton() }
    }

    private fun toggleCreateBarcodeButton() {
       // parentActivity.buttonEnabled = binding.etPhone.isNotBlank() || binding.etMessage.isNotBlank()
    }
}