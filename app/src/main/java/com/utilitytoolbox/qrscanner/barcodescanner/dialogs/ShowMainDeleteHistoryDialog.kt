package com.utilitytoolbox.qrscanner.barcodescanner.dialogs

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.utilitytoolbox.qrscanner.barcodescanner.R

class ShowMainDeleteHistoryDialog(
    context: Context,
    isClearHistory: Boolean = false,
    val onDelete: (Boolean) -> Unit = { _ -> }
) {

    private lateinit var dialog: AlertDialog

    init {
        val layout = View.inflate(context, R.layout.dialog_del_histry, null)
        layout.run {
            var dialogTitle = findViewById<TextView>(R.id.deleteDialogTitle)
            var dialogDescription = findViewById<TextView>(R.id.deleteDialogDes)

            if (isClearHistory) {
                dialogTitle?.setText(context.getString(R.string.delete_history))
                dialogDescription?.setText(context.getString(R.string.are_you_sure_you_want_to_clear_your_history_data_this_action_will_not_be_undo))
            } else {
                dialogTitle?.setText(context.getString(R.string.delete))
                dialogDescription?.setText(context.getString(R.string.are_you_sure_you_want_to_delete))
            }

            findViewById<Button>(R.id.yesDelete).setOnClickListener {
                dialog.dismiss()
                onDelete.invoke(true)
            }

            findViewById<TextView>(R.id.noCancel).setOnClickListener {
                dialog.dismiss()
                onDelete.invoke(false)
            }

        }

        val builder = AlertDialog.Builder(context)
        builder.setView(layout)
        dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

}