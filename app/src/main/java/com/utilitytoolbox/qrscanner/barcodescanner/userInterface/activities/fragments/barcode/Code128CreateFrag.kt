package com.utilitytoolbox.qrscanner.barcodescanner.userInterface.activities.fragments.barcode



import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import com.utilitytoolbox.qrscanner.barcodescanner.utils.createOrCancelQr
import com.utilitytoolbox.qrscanner.barcodescanner.utils.textString
import com.utilitytoolbox.qrscanner.barcodescanner.databinding.LayoutFragmentCode128Binding
import com.utilitytoolbox.qrscanner.barcodescanner.model.MineBarCode
import com.utilitytoolbox.qrscanner.barcodescanner.model.schema
.OtherModel
import com.utilitytoolbox.qrscanner.barcodescanner.model.schema
.Schema
import com.utilitytoolbox.qrscanner.barcodescanner.userInterface.activities.fragments.BaseFragment


class Code128CreateFrag : BaseFragment() {
    lateinit var binding: LayoutFragmentCode128Binding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = LayoutFragmentCode128Binding.inflate(layoutInflater)
        return binding.root
    }

    companion object{
        private const val DEFAULT_TEXT_Barcode = "DEFAULT_TEXT_Barcode"

        fun newInstance(barcodemain: MineBarCode): Code128CreateFrag {
            return Code128CreateFrag().apply {
                arguments = Bundle().apply {
                    putSerializable(DEFAULT_TEXT_Barcode, barcodemain)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.etCode128 .requestFocus()
        binding.etCode128.addTextChangedListener {
        //    parentActivity.buttonEnabled = binding.etCode128.isNotBlank()
        }
        binding.tvCancel.setOnClickListener {
            createOrCancelQr?.invoke(false)
        }

        binding.tvCreate.setOnClickListener {
            createOrCancelQr?.invoke(true)
        }
    }

    override fun getBarcodeSchema(): Schema {
        return OtherModel(binding.etCode128.textString)
    }
}