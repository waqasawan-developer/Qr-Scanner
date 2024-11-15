package com.utilitytoolbox.qrscanner.barcodescanner.userInterface.activities.fragments.qrcode

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.utilitytoolbox.qrscanner.barcodescanner.utils.textString
import com.utilitytoolbox.qrscanner.barcodescanner.utils.createOrCancelQr
import com.utilitytoolbox.qrscanner.barcodescanner.databinding.LayoutFragmentQrCodeMecardBinding
import com.utilitytoolbox.qrscanner.barcodescanner.model.MainContactN
import com.utilitytoolbox.qrscanner.barcodescanner.model.MineBarCode
import com.utilitytoolbox.qrscanner.barcodescanner.model.schema
.MeCardModel
import com.utilitytoolbox.qrscanner.barcodescanner.model.schema
.Schema
import com.utilitytoolbox.qrscanner.barcodescanner.userInterface.activities.fragments.BaseFragment

class MeCardQrCodeCreateFrag : BaseFragment() {

    lateinit var binding: LayoutFragmentQrCodeMecardBinding

    companion object{
        private const val DEFAULT_TEXT_Barcode = "DEFAULT_TEXT_Barcode"

        fun newInstance(barcodemain: MineBarCode): MeCardQrCodeCreateFrag {
            return MeCardQrCodeCreateFrag().apply {
                arguments = Bundle().apply {
                    putSerializable(DEFAULT_TEXT_Barcode, barcodemain)
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = LayoutFragmentQrCodeMecardBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.etFirstName.requestFocus()
      //  parentActivity.buttonEnabled = true


        binding.tvCancel.setOnClickListener {
            createOrCancelQr?.invoke(false)
        }

        binding.tvCreate.setOnClickListener {
            createOrCancelQr?.invoke(true)

        }
    }

    override fun getBarcodeSchema(): Schema {
        return MeCardModel(
            firstName = binding.etFirstName.textString,
            lastName = binding.etLastName.textString,
            email = binding.etEmail.textString,
            phone = binding.etPhone.textString
        )
    }


    override fun showContact(mainContact: MainContactN) {
        binding.etFirstName.setText(mainContact.firstName)
        binding.etLastName.setText(mainContact.lastName)
        binding.etEmail.setText(mainContact.email)
        binding.etPhone.setText(mainContact.phone)
    }
}