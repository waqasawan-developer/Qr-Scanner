package com.utilitytoolbox.qrscanner.barcodescanner.userInterface.activities.fragments.qrcode

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import com.utilitytoolbox.qrscanner.barcodescanner.utils.createOrCancelQr
import com.utilitytoolbox.qrscanner.barcodescanner.utils.textString
import com.utilitytoolbox.qrscanner.barcodescanner.databinding.LayoutFragmentQrCodeEmailBinding
import com.utilitytoolbox.qrscanner.barcodescanner.model.MineBarCode
import com.utilitytoolbox.qrscanner.barcodescanner.model.schema
.EmailModel
import com.utilitytoolbox.qrscanner.barcodescanner.model.schema
.Schema
import com.utilitytoolbox.qrscanner.barcodescanner.userInterface.activities.fragments.BaseFragment

class EmailQrCodeCreateFrag : BaseFragment() {

    lateinit var binding: LayoutFragmentQrCodeEmailBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LayoutFragmentQrCodeEmailBinding.inflate(layoutInflater)
        return binding.root
    }

    companion object{
        private const val DEFAULT_TEXT_Barcode = "DEFAULT_TEXT_Barcode"

        fun newInstance(barcodemain: MineBarCode): EmailQrCodeCreateFrag {
            return EmailQrCodeCreateFrag().apply {
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

    override fun getBarcodeSchema(): Schema {
        return EmailModel(
            email = binding.etEmail.textString,
            subject = binding.etSubject.textString,
            body = binding.etMessage.textString
        )
    }

    private fun initTitleEditText() {
        binding.etEmail.requestFocus()
    }

    private fun handleTextChanged() {
        binding.etEmail.addTextChangedListener { toggleCreateBarcodeButton() }
        binding.etSubject.addTextChangedListener { toggleCreateBarcodeButton() }
        binding.etMessage.addTextChangedListener { toggleCreateBarcodeButton() }
    }

    private fun toggleCreateBarcodeButton() {
      //  parentActivity.buttonEnabled = binding.etEmail.isNotBlank() || binding.etSubject.isNotBlank() || binding.etMessage.isNotBlank()
    }
}