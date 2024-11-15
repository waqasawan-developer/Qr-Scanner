package com.utilitytoolbox.qrscanner.barcodescanner.userInterface.activities.fragments.qrcode

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import com.utilitytoolbox.qrscanner.barcodescanner.utils.createOrCancelQr
import com.utilitytoolbox.qrscanner.barcodescanner.utils.textString
import com.google.zxing.BarcodeFormat
import com.utilitytoolbox.qrscanner.barcodescanner.databinding.LayoutFragmentQrCodeTextBinding
import com.utilitytoolbox.qrscanner.barcodescanner.model.MineBarCode
import com.utilitytoolbox.qrscanner.barcodescanner.model.schema
.Schema
import com.utilitytoolbox.qrscanner.barcodescanner.usecase
.BarcodeParserNew
import com.utilitytoolbox.qrscanner.barcodescanner.userInterface.activities.fragments.BaseFragment

class TextQrCodeCreateFrag : BaseFragment() {

    lateinit var binding: LayoutFragmentQrCodeTextBinding
    companion object {
        private const val DEFAULT_TEXT_KEY = "DEFAULT_TEXT_KEY"
        private const val DEFAULT_TEXT_Barcode = "DEFAULT_TEXT_Barcode"

        fun newInstance(defaultText: String): TextQrCodeCreateFrag {
            return TextQrCodeCreateFrag().apply {
                arguments = Bundle().apply {
                    putString(DEFAULT_TEXT_KEY, defaultText)
                }
            }
        }



        fun newInstance(barcodemain: MineBarCode): TextQrCodeCreateFrag {
            return TextQrCodeCreateFrag().apply {
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
        binding = LayoutFragmentQrCodeTextBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleTextChanged()
        initEditText()

        binding.tvCancel.setOnClickListener {
            createOrCancelQr?.invoke(false)
        }

        binding.tvCreate.setOnClickListener {
            createOrCancelQr?.invoke(true)

        }
    }

    override fun getBarcodeSchema(): Schema {
        return BarcodeParserNew.parseSchema(BarcodeFormat.QR_CODE, binding.etText.textString)
    }

    private fun initEditText() {
        val defaultText = arguments?.getString(DEFAULT_TEXT_KEY).orEmpty()
        binding.etText.apply {
            setText(defaultText)
            setSelection(defaultText.length)
            requestFocus()
        }
    }

    private fun handleTextChanged() {
        binding.etText.addTextChangedListener {
        //    parentActivity.buttonEnabled = binding.etText.isNotBlank()
        }
    }
}