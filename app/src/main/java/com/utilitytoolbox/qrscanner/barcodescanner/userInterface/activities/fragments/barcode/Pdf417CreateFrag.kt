package com.utilitytoolbox.qrscanner.barcodescanner.userInterface.activities.fragments.barcode



import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import com.utilitytoolbox.qrscanner.barcodescanner.utils.createOrCancelQr
import com.utilitytoolbox.qrscanner.barcodescanner.utils.textString
import com.utilitytoolbox.qrscanner.barcodescanner.databinding.LayoutFragmentPdf417Binding
import com.utilitytoolbox.qrscanner.barcodescanner.model.MineBarCode
import com.utilitytoolbox.qrscanner.barcodescanner.model.schema
.OtherModel
import com.utilitytoolbox.qrscanner.barcodescanner.model.schema
.Schema
import com.utilitytoolbox.qrscanner.barcodescanner.userInterface.activities.fragments.BaseFragment

class Pdf417CreateFrag : BaseFragment() {
    companion object{
        private const val DEFAULT_TEXT_Barcode = "DEFAULT_TEXT_Barcode"

        fun newInstance(barcodemain: MineBarCode): Pdf417CreateFrag {
            return Pdf417CreateFrag().apply {
                arguments = Bundle().apply {
                    putSerializable(DEFAULT_TEXT_Barcode, barcodemain)
                }
            }
        }
    }

    lateinit var binding: LayoutFragmentPdf417Binding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LayoutFragmentPdf417Binding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.etPdf417.requestFocus()
        binding.etPdf417 .addTextChangedListener {
          //  parentActivity.buttonEnabled = binding.etPdf417 .isNotBlank()
        }
        binding.tvCancel.setOnClickListener {
            createOrCancelQr?.invoke(false)
        }

        binding.tvCreate.setOnClickListener {
            createOrCancelQr?.invoke(true)
        }
    }

    override fun getBarcodeSchema(): Schema {
        return OtherModel(binding.etPdf417 .textString)
    }
}