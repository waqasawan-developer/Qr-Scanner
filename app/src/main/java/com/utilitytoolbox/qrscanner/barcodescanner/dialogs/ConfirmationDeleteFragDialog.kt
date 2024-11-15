package com.utilitytoolbox.qrscanner.barcodescanner.dialogs

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.utilitytoolbox.qrscanner.barcodescanner.R

class ConfirmationDeleteFragDialog : DialogFragment() {
    var cameraPermissionDialog: android.app.AlertDialog? = null

    companion object {
        private const val MESSAGE_ID_KEY = "MESSAGE_ID_KEY"

        fun newInstance(messageId: Int): ConfirmationDeleteFragDialog {
            return ConfirmationDeleteFragDialog().apply {
                arguments = Bundle().apply {
                    putInt(MESSAGE_ID_KEY, messageId)
                }
                isCancelable = false
            }
        }
    }

    interface Listener {
        fun onDeleteConfirmed()
    }


    /* override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
         val listener = requireActivity() as? Listener ?: parentFragment as? Listener
         val messageId = arguments?.getInt(MESSAGE_ID_KEY).orZero()

         val dialog = AlertDialog.Builder(requireActivity(), R.style.DialogTheme)
             .setMessage(messageId)
             .setPositiveButton(R.string.dlg_delete_positive_btn) { _, _ -> listener?.onDeleteConfirmed() }
             .setNegativeButton(R.string.dlg_delete_negative_btn, null)
             .create()

         dialog.setOnShowListener {
             dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
             dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.blue))
         }

         return dialog
     }*/

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val listener = (requireActivity() as? Listener ?: parentFragment as? Listener)
        val messageId = arguments?.getInt(MESSAGE_ID_KEY) ?: 0

        // Inflate the custom layout
        // val messageTextView = view.findViewById<TextView>(R.id.dialog_message)

        // Set the message from the resource
        //   messageTextView.setText(messageId)

        val layout = View.inflate(context, R.layout.dialog_custm, null)
        layout.run {
            findViewById<TextView>(R.id.tvCreate).setOnClickListener {
                cameraPermissionDialog?.dismiss()
                listener?.onDeleteConfirmed()
            }
            findViewById<TextView>(R.id.tvCancel).setOnClickListener {
                cameraPermissionDialog?.dismiss()
            }
        }
        val builder = android.app.AlertDialog.Builder(context,R.style.CustomDialog)
        builder.setView(layout)
        cameraPermissionDialog = builder.create()
        cameraPermissionDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        cameraPermissionDialog?.setCancelable(false)

        return cameraPermissionDialog!!
    }
}