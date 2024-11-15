package com.utilitytoolbox.qrscanner.barcodescanner.userInterface.activities.fragments

import androidx.fragment.app.Fragment
import com.utilitytoolbox.qrscanner.barcodescanner.model
.MainContactN
import com.utilitytoolbox.qrscanner.barcodescanner.model.schema
.OtherModel
import com.utilitytoolbox.qrscanner.barcodescanner.model.schema
.Schema

abstract class BaseFragment : Fragment() {

    /* protected val parentActivity by unsafeLazy {
           if (requireActivity() is BarcodeCreateMainActivity) {
               requireActivity() as BarcodeCreateMainActivity
           }else{
               requireActivity() as ResultQRActivity
           }
    } */

    open val latitude: Double? = null
    open val longitude: Double? = null

    open fun getBarcodeSchema(): Schema = OtherModel("")
    open fun showPhone(phone: String) {}
    open fun showContact(mainContact: MainContactN) {}
    open fun showLocation(latitude: Double?, longitude: Double?) {}
}