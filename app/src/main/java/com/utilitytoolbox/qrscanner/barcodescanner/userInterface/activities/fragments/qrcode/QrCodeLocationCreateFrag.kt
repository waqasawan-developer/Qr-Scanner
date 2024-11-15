package com.utilitytoolbox.qrscanner.barcodescanner.userInterface.activities.fragments.qrcode

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import com.utilitytoolbox.qrscanner.barcodescanner.utils.createOrCancelQr
import com.utilitytoolbox.qrscanner.barcodescanner.utils.textString
import com.utilitytoolbox.qrscanner.barcodescanner.databinding.LayoutFragmentQrCodeLocationBinding
import com.utilitytoolbox.qrscanner.barcodescanner.model.MineBarCode
import com.utilitytoolbox.qrscanner.barcodescanner.model.schema
.GeoModel
import com.utilitytoolbox.qrscanner.barcodescanner.model.schema
.Schema
import com.utilitytoolbox.qrscanner.barcodescanner.userInterface.activities.fragments.BaseFragment

class QrCodeLocationCreateFrag : BaseFragment() {

    lateinit var binding: LayoutFragmentQrCodeLocationBinding

    override val latitude: Double?
        get() = binding.etLatitude.textString.toDoubleOrNull()

    override val longitude: Double?
        get() = binding.etLongitude.textString.toDoubleOrNull()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LayoutFragmentQrCodeLocationBinding.inflate(layoutInflater)
        return binding.root
    }

    companion object{
        private const val DEFAULT_TEXT_Barcode = "DEFAULT_TEXT_Barcode"

        fun newInstance(barcodemain: MineBarCode): QrCodeLocationCreateFrag {
            return QrCodeLocationCreateFrag().apply {
                arguments = Bundle().apply {
                    putSerializable(DEFAULT_TEXT_Barcode, barcodemain)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initLatitudeEditText()
        handleTextChanged()

        binding.tvCancel.setOnClickListener {
            createOrCancelQr?.invoke(false)
        }

        binding.tvCreate.setOnClickListener {
            createOrCancelQr?.invoke(true)
        }
    }

    override fun getBarcodeSchema(): Schema {
        return GeoModel(
            latitude = binding.etLatitude.textString,
            longitude = binding.etLongitude.textString
        )
    }

    override fun showLocation(latitude: Double?, longitude: Double?) {
        latitude?.apply {
            binding.etLatitude.setText(latitude.toString())
        }
        longitude?.apply {
            binding.etLongitude.setText(longitude.toString())
        }
    }

    private fun initLatitudeEditText() {
        binding.etLatitude.requestFocus()
    }

    private fun handleTextChanged() {
        binding.etLatitude.addTextChangedListener { toggleCreateBarcodeButton() }
        binding.etLongitude.addTextChangedListener { toggleCreateBarcodeButton() }
    }

    private fun toggleCreateBarcodeButton() {
      //  parentActivity.buttonEnabled = binding.etLatitude.isNotBlank() && binding.etLongitude.isNotBlank()
    }
}