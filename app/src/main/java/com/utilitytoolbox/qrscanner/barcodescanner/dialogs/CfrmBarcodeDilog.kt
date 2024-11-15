package com.utilitytoolbox.qrscanner.barcodescanner.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.utilitytoolbox.qrscanner.barcodescanner.R
import com.utilitytoolbox.qrscanner.barcodescanner.utils.toStringId
import com.utilitytoolbox.qrscanner.barcodescanner.model.MineBarCode

class CfrmBarcodeDilog : DialogFragment() {

    interface Listener {
        fun onBarcodeConfirmed(MineBarCode: MineBarCode)
        fun onBarcodeDeclined()
    }

    companion object {
        private const val BARCODE_KEY = "BARCODE_FORMAT_MESSAGE_ID_KEY"

        fun newInstance(MineBarCode: MineBarCode): CfrmBarcodeDilog {
            return CfrmBarcodeDilog().apply {
                arguments = Bundle().apply {
                    putSerializable(BARCODE_KEY, MineBarCode)
                }
                isCancelable = false
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val listener = parentFragment as? Listener
        val MineBarCode = arguments?.getSerializable(BARCODE_KEY) as? MineBarCode ?: throw IllegalArgumentException("No barcode passed")
        val messageId = MineBarCode.format.toStringId()

        val dialog = AlertDialog.Builder(requireActivity(), R.style.DialogTheme)
            .setTitle(R.string.dlg_confirm_code_title)
            .setMessage(messageId)
            .setCancelable(false)
            .setPositiveButton(R.string.dlg_confirm_code_positive_btn) { _, _ ->
                listener?.onBarcodeConfirmed(MineBarCode)
            }
            .setNegativeButton(R.string.dlg_confirm_code_negative_btn) { _, _ ->
                listener?.onBarcodeDeclined()
            }
            .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.blue))
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
        }

        return dialog
    }
}