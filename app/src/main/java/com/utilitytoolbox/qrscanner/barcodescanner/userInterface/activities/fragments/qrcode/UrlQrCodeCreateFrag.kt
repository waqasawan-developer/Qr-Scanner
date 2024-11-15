package com.utilitytoolbox.qrscanner.barcodescanner.userInterface.activities.fragments.qrcode

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import com.utilitytoolbox.qrscanner.barcodescanner.model.schema
.UrlModel
import com.utilitytoolbox.qrscanner.barcodescanner.utils.createOrCancelQr
import com.utilitytoolbox.qrscanner.barcodescanner.utils.textString
import com.utilitytoolbox.qrscanner.barcodescanner.databinding.LayoutFragmentQrCodeUrlBinding
import com.utilitytoolbox.qrscanner.barcodescanner.model.MineBarCode
import com.utilitytoolbox.qrscanner.barcodescanner.model.schema
.Schema
import com.utilitytoolbox.qrscanner.barcodescanner.userInterface.activities.fragments.BaseFragment

class UrlQrCodeCreateFrag : BaseFragment() {

    lateinit var binding: LayoutFragmentQrCodeUrlBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LayoutFragmentQrCodeUrlBinding.inflate(layoutInflater)
        return binding.root
    }

    companion object{
        private const val DEFAULT_TEXT_Barcode = "DEFAULT_TEXT_Barcode"


        fun newInstance(barcodemain: MineBarCode): UrlQrCodeCreateFrag {
            return UrlQrCodeCreateFrag().apply {
                arguments = Bundle().apply {
                    putSerializable(DEFAULT_TEXT_Barcode, barcodemain)
                }
            }
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showUrlPrefix()
        handleTextChanged()

        binding.tvCancel.setOnClickListener {
            createOrCancelQr?.invoke(false)
        }

        binding.tvCreate.setOnClickListener {
            createOrCancelQr?.invoke(true)

        }
    }

    override fun getBarcodeSchema(): Schema {
        return UrlModel(binding.etUrl.textString)
    }

    private fun showUrlPrefix() {
        val prefix = "https://"
        binding.etUrl.apply {
            setText(prefix)
            setSelection(prefix.length)
            requestFocus()
        }
    }

    private fun handleTextChanged() {
        binding.etUrl.addTextChangedListener {
          //  parentActivity.buttonEnabled = binding.etUrl.isNotBlank()
        }
    }
}