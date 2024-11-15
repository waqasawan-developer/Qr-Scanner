package com.utilitytoolbox.qrscanner.barcodescanner.dialogs


import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.annotation.ArrayRes
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import androidx.core.view.forEach
import androidx.core.view.get
import com.utilitytoolbox.qrscanner.barcodescanner.R

class MainCustomDialog(
    context: Context,
    private val title: String,
    private val selected: Int? = null,
    private val addAgainSelection: Int? = null,
    @LayoutRes val layoutRes: Int,
    @ArrayRes val dataRes: Int? = null,
    val dataList: Array<String>? = null,
    val onSelect: (Int, Boolean) -> Unit = { _, _ -> }
) {

    private var dialog: AlertDialog? = null
    private val builder = AlertDialog.Builder(context,R.style.CustomDialog)
    private val layout = View.inflate(context, layoutRes, null)
    private var selection = -1

    init {
        builder.setView(layout)
        initListDialog()
    }


    fun tintDrawable(drawable: Drawable?, color: Int): Drawable? {
        drawable?.setColorFilter(color, PorterDuff.Mode.SRC_IN)
        return drawable
    }

    private fun initListDialog() {
        fun onSelection(i: Int) {
            selection = i
        }

        layout.run {
            this.findViewById<TextView>(R.id.dialogTitle).text = title
            val dataArray =
                if (dataRes != null) context.resources.getStringArray(dataRes) else dataList
                    ?: arrayOf("")
            dataArray.forEachIndexed { i, s ->
                val radioButton =
                    (View.inflate(context, R.layout.dialog_radio_list, null) as RadioButton).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                    }
                radioButton.text = s
                if (i == selected) {
                    selection = i
                    radioButton.isChecked = true
                    radioButton.setTextColor(ContextCompat.getColor(context, R.color.color_accent))
                }

                radioButton.id = i

                if (dataArray.contentEquals(resources.getStringArray(R.array.cameraSelection))) {
                    if (i == 0) {
                        val drawable = ContextCompat.getDrawable(
                            context,
                            R.drawable.ic_back_camera
                        )
                        val drawable2 = ContextCompat.getDrawable(
                            context,
                            R.drawable.customcheckbox
                        )

                        if (i == selected) {

                            val tintedDrawable = tintDrawable(
                                drawable,
                                ContextCompat.getColor(context, R.color.color_accent)
                            )
                            val tintedDrawable2 = tintDrawable(
                                drawable2,
                                ContextCompat.getColor(context, R.color.color_accent)
                            )

                            radioButton.setCompoundDrawablesWithIntrinsicBounds(
                                tintedDrawable,
                                null,
                                tintedDrawable2,
                                null
                            )
                        } else {
                            radioButton.setCompoundDrawablesWithIntrinsicBounds(
                                drawable,
                                null,
                                drawable2,
                                null
                            )
                        }


                    } else {
                        val drawable = ContextCompat.getDrawable(
                            context,
                            R.drawable.ic_front_cam
                        )
                        val drawable2 = ContextCompat.getDrawable(
                            context,
                            R.drawable.customcheckbox
                        )

                        if (i == selected) {

                            val tintedDrawable = tintDrawable(
                                drawable,
                                ContextCompat.getColor(context, R.color.color_accent)
                            )
                            val tintedDrawable2 = tintDrawable(
                                drawable2,
                                ContextCompat.getColor(context, R.color.color_accent)
                            )

                            radioButton.setCompoundDrawablesWithIntrinsicBounds(
                                tintedDrawable,
                                null,
                                tintedDrawable2,
                                null
                            )
                        } else {
                            radioButton.setCompoundDrawablesWithIntrinsicBounds(
                                drawable,
                                null,
                                drawable2,
                                null
                            )
                        }
                    }
                } else
                    if (dataArray.contentEquals(resources.getStringArray(R.array.themeSelection))) {
                        if (i == 0) {
                            val drawable = ContextCompat.getDrawable(
                                context,
                                R.drawable.ic_default_mode
                            )
                            val drawable2 = ContextCompat.getDrawable(
                                context,
                                R.drawable.customcheckbox
                            )

                            if (i == selected) {

                                val tintedDrawable = tintDrawable(
                                    drawable,
                                    ContextCompat.getColor(context, R.color.color_accent)
                                )
                                val tintedDrawable2 = tintDrawable(
                                    drawable2,
                                    ContextCompat.getColor(context, R.color.color_accent)
                                )

                                radioButton.setCompoundDrawablesWithIntrinsicBounds(
                                    tintedDrawable,
                                    null,
                                    tintedDrawable2,
                                    null
                                )
                            } else {
                                radioButton.setCompoundDrawablesWithIntrinsicBounds(
                                    drawable,
                                    null,
                                    drawable2,
                                    null
                                )
                            }


                        } else if (i == 1) {
                            val drawable = ContextCompat.getDrawable(
                                context,
                                R.drawable.ic_light_mode
                            )
                            val drawable2 = ContextCompat.getDrawable(
                                context,
                                R.drawable.customcheckbox
                            )

                            if (i == selected) {

                                val tintedDrawable = tintDrawable(
                                    drawable,
                                    ContextCompat.getColor(context, R.color.color_accent)
                                )
                                val tintedDrawable2 = tintDrawable(
                                    drawable2,
                                    ContextCompat.getColor(context, R.color.color_accent)
                                )

                                radioButton.setCompoundDrawablesWithIntrinsicBounds(
                                    tintedDrawable,
                                    null,
                                    tintedDrawable2,
                                    null
                                )
                            } else {
                                radioButton.setCompoundDrawablesWithIntrinsicBounds(
                                    drawable,
                                    null,
                                    drawable2,
                                    null
                                )
                            }
                        } else {
                            val drawable = ContextCompat.getDrawable(
                                context,
                               R.drawable.ic_dark_mode
                            )
                            val drawable2 = ContextCompat.getDrawable(
                                context,
                                R.drawable.customcheckbox
                            )

                            if (i == selected) {

                                val tintedDrawable = tintDrawable(
                                    drawable,
                                    ContextCompat.getColor(context, R.color.color_accent)
                                )
                                val tintedDrawable2 = tintDrawable(
                                    drawable2,
                                    ContextCompat.getColor(context, R.color.color_accent)
                                )

                                radioButton.setCompoundDrawablesWithIntrinsicBounds(
                                    tintedDrawable,
                                    null,
                                    tintedDrawable2,
                                    null
                                )
                            } else {
                                radioButton.setCompoundDrawablesWithIntrinsicBounds(
                                    drawable,
                                    null,
                                    drawable2,
                                    null
                                )
                            }
                        }
                    } else {

                        if (i == 0) {

                            val drawable = ContextCompat.getDrawable(
                                context,
                                R.drawable.ic_bing
                            )
                            val drawable2 = ContextCompat.getDrawable(
                                context,
                                R.drawable.customcheckbox
                            )

                            radioButton.setCompoundDrawablesWithIntrinsicBounds(
                                drawable,
                                null,
                                drawable2,
                                null
                            )
                        } else if (i == 1) {

                            val drawable = ContextCompat.getDrawable(
                                context,
                               R.drawable.ic_duck
                            )
                            val drawable2 = ContextCompat.getDrawable(
                                context,
                                R.drawable.customcheckbox
                            )

                            radioButton.setCompoundDrawablesWithIntrinsicBounds(
                                drawable,
                                null,
                                drawable2,
                                null
                            )
                        } else if (i == 2) {
                            val drawable = resources.getDrawable(R.drawable.ic_google)
                            val drawable2 = ContextCompat.getDrawable(
                                context,
                                R.drawable.customcheckbox
                            )

                            radioButton.setCompoundDrawablesWithIntrinsicBounds(
                                drawable,
                                null,
                                drawable2,
                                null
                            )
                        } else if (i == 3) {
                            val drawable =
                                resources.getDrawable(R.drawable.ic_qwant)

                            val drawable2 = ContextCompat.getDrawable(
                                context,
                                R.drawable.customcheckbox
                            )

                            radioButton.setCompoundDrawablesWithIntrinsicBounds(
                                drawable,
                                null,
                                drawable2,
                                null
                            )
                        } else if (i == 4) {
                            val drawable =
                                resources.getDrawable(R.drawable.ic_yahoo)
                            val drawable2 = ContextCompat.getDrawable(
                                context,
                                R.drawable.customcheckbox
                            )

                            radioButton.setCompoundDrawablesWithIntrinsicBounds(
                                drawable,
                                null,
                                drawable2,
                                null
                            )
                        } else if (i == 5) {
                            val drawable =
                                resources.getDrawable(R.drawable.ic_yandes)

                            val drawable2 = ContextCompat.getDrawable(
                                context,
                                R.drawable.customcheckbox
                            )

                            radioButton.setCompoundDrawablesWithIntrinsicBounds(
                                drawable,
                                null,
                                drawable2,
                                null
                            )
                        }
                    }

                if (addAgainSelection != null) {
                    if (i == selected && i == addAgainSelection) {
                        radioButton.setOnClickListener {
                            onSelection(i)
                        }
                    }
                }


                this.findViewById<RadioGroup>(R.id.itemsLayout).addView(radioButton)
            }

            this.findViewById<RadioGroup>(R.id.itemsLayout).setOnCheckedChangeListener { aa, i ->
                onSelection(i)

                if (dataArray.contentEquals(resources.getStringArray(R.array.cameraSelection))) {
                    when (i) {
                        0 -> {
                            (aa.get(0) as RadioButton).setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.color_accent
                                )
                            )

                            (aa.get(1) as RadioButton).setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.black
                                )
                            )

                            val drawable = ContextCompat.getDrawable(
                                context,
                                R.drawable.ic_back_camera
                            )
                            val drawable2 = ContextCompat.getDrawable(
                                context,
                                R.drawable.customcheckbox
                            )

                            val tintedDrawable = tintDrawable(
                                drawable,
                                ContextCompat.getColor(context, R.color.color_accent)
                            )
                            val tintedDrawable2 = tintDrawable(
                                drawable2,
                                ContextCompat.getColor(context, R.color.color_accent)
                            )


                            (aa.get(0) as RadioButton).setCompoundDrawablesWithIntrinsicBounds(
                                tintedDrawable,
                                null,
                                tintedDrawable2,
                                null
                            )

                            val drawable12 = ContextCompat.getDrawable(
                                context,
                                R.drawable.ic_front_cam
                            )
                            val drawable22 = ContextCompat.getDrawable(
                                context,
                                R.drawable.customcheckbox
                            )
                            val tintedDrawable11 = tintDrawable(
                                drawable12,
                                ContextCompat.getColor(context, R.color.iconsColor)
                            )
                            val tintedDrawable222 = tintDrawable(
                                drawable22,
                                ContextCompat.getColor(context, R.color.iconsColor)
                            )


                            (aa.get(1) as RadioButton).setCompoundDrawablesWithIntrinsicBounds(
                                tintedDrawable11,
                                null,
                                tintedDrawable222,
                                null
                            )
                        }

                        1 -> {
                            (aa.get(0) as RadioButton).setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.black
                                )
                            )

                            (aa.get(1) as RadioButton).setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.color_accent
                                )
                            )
                            val drawable = ContextCompat.getDrawable(
                                context,
                                R.drawable.ic_back_camera
                            )
                            val drawable2 = ContextCompat.getDrawable(
                                context,
                                R.drawable.customcheckbox
                            )

                            val tintedDrawable = tintDrawable(
                                drawable,
                                ContextCompat.getColor(context, R.color.iconsColor)
                            )
                            val tintedDrawable2 = tintDrawable(
                                drawable2,
                                ContextCompat.getColor(context, R.color.iconsColor)
                            )


                            (aa.get(0) as RadioButton).setCompoundDrawablesWithIntrinsicBounds(
                                tintedDrawable,
                                null,
                                tintedDrawable2,
                                null
                            )

                            val drawable12 = ContextCompat.getDrawable(
                                context,
                                R.drawable.ic_front_cam
                            )
                            val drawable22 = ContextCompat.getDrawable(
                                context,
                                R.drawable.customcheckbox
                            )
                            val tintedDrawable11 = tintDrawable(
                                drawable12,
                                ContextCompat.getColor(context, R.color.color_accent)
                            )
                            val tintedDrawable222 = tintDrawable(
                                drawable22,
                                ContextCompat.getColor(context, R.color.color_accent)
                            )


                            (aa.get(1) as RadioButton).setCompoundDrawablesWithIntrinsicBounds(
                                tintedDrawable11,
                                null,
                                tintedDrawable222,
                                null
                            )

                        }
                    }
                } else if (dataArray.contentEquals(resources.getStringArray(R.array.themeSelection))) {
                    when (i) {
                        0 -> {
                            (aa.get(0) as RadioButton).setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.color_accent
                                )
                            )

                            (aa.get(1) as RadioButton).setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.black
                                )
                            )

                            (aa.get(2) as RadioButton).setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.black
                                )
                            )

                            val drawable = ContextCompat.getDrawable(
                                context,
                                R.drawable.ic_default_mode
                            )
                            val drawable2 = ContextCompat.getDrawable(
                                context,
                                R.drawable.customcheckbox
                            )

                            val tintedDrawable = tintDrawable(
                                drawable,
                                ContextCompat.getColor(context, R.color.color_accent)
                            )
                            val tintedDrawable2 = tintDrawable(
                                drawable2,
                                ContextCompat.getColor(context, R.color.color_accent)
                            )


                            (aa.get(0) as RadioButton).setCompoundDrawablesWithIntrinsicBounds(
                                tintedDrawable,
                                null,
                                tintedDrawable2,
                                null
                            )

                            val drawable12 = ContextCompat.getDrawable(
                                context,
                                R.drawable.ic_light_mode
                            )
                            val drawable22 = ContextCompat.getDrawable(
                                context,
                                R.drawable.customcheckbox
                            )
                            val tintedDrawable11 = tintDrawable(
                                drawable12,
                                ContextCompat.getColor(context, R.color.iconsColor)
                            )
                            val tintedDrawable22 = tintDrawable(
                                drawable22,
                                ContextCompat.getColor(context, R.color.iconsColor)
                            )


                            (aa.get(1) as RadioButton).setCompoundDrawablesWithIntrinsicBounds(
                                tintedDrawable11,
                                null,
                                tintedDrawable22,
                                null
                            )

                            val drawable111 = ContextCompat.getDrawable(
                                context,
                                R.drawable.ic_dark_mode
                            )
                            val drawable222 = ContextCompat.getDrawable(
                                context,
                                R.drawable.customcheckbox
                            )

                            val tintedDrawable111 = tintDrawable(
                                drawable111,
                                ContextCompat.getColor(context, R.color.iconsColor)
                            )
                            val tintedDrawable222 = tintDrawable(
                                drawable222,
                                ContextCompat.getColor(context, R.color.iconsColor)
                            )


                            (aa.get(2) as RadioButton).setCompoundDrawablesWithIntrinsicBounds(
                                tintedDrawable111,
                                null,
                                tintedDrawable222,
                                null
                            )
                        }

                        1 -> {
                            (aa.get(0) as RadioButton).setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.black
                                )
                            )

                            (aa.get(1) as RadioButton).setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.color_accent
                                )
                            )

                            (aa.get(2) as RadioButton).setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.black
                                )
                            )

                            val drawable = ContextCompat.getDrawable(
                                context,
                                R.drawable.ic_default_mode
                            )
                            val drawable2 = ContextCompat.getDrawable(
                                context,
                                R.drawable.customcheckbox
                            )

                            val tintedDrawable = tintDrawable(
                                drawable,
                                ContextCompat.getColor(context, R.color.iconsColor)
                            )
                            val tintedDrawable2 = tintDrawable(
                                drawable2,
                                ContextCompat.getColor(context, R.color.iconsColor)
                            )


                            (aa.get(0) as RadioButton).setCompoundDrawablesWithIntrinsicBounds(
                                tintedDrawable,
                                null,
                                tintedDrawable2,
                                null
                            )

                            val drawable12 = ContextCompat.getDrawable(
                                context,
                                R.drawable.ic_light_mode
                            )
                            val drawable22 = ContextCompat.getDrawable(
                                context,
                                R.drawable.customcheckbox
                            )
                            val tintedDrawable11 = tintDrawable(
                                drawable12,
                                ContextCompat.getColor(context, R.color.color_accent)
                            )
                            val tintedDrawable22 = tintDrawable(
                                drawable22,
                                ContextCompat.getColor(context, R.color.color_accent)
                            )


                            (aa.get(1) as RadioButton).setCompoundDrawablesWithIntrinsicBounds(
                                tintedDrawable11,
                                null,
                                tintedDrawable22,
                                null
                            )

                            val drawable111 = ContextCompat.getDrawable(
                                context,
                                R.drawable.ic_dark_mode
                            )
                            val drawable222 = ContextCompat.getDrawable(
                                context,
                                R.drawable.customcheckbox
                            )

                            val tintedDrawable111 = tintDrawable(
                                drawable111,
                                ContextCompat.getColor(context, R.color.iconsColor)
                            )
                            val tintedDrawable222 = tintDrawable(
                                drawable222,
                                ContextCompat.getColor(context, R.color.iconsColor)
                            )


                            (aa.get(2) as RadioButton).setCompoundDrawablesWithIntrinsicBounds(
                                tintedDrawable111,
                                null,
                                tintedDrawable222,
                                null
                            )
                        }

                        2 ->  {
                            (aa.get(0) as RadioButton).setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.black
                                )
                            )

                            (aa.get(1) as RadioButton).setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.black
                                )
                            )

                            (aa.get(2) as RadioButton).setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.color_accent
                                )
                            )

                            val drawable = ContextCompat.getDrawable(
                                context,
                                R.drawable.ic_default_mode
                            )
                            val drawable2 = ContextCompat.getDrawable(
                                context,
                                R.drawable.customcheckbox
                            )

                            val tintedDrawable = tintDrawable(
                                drawable,
                                ContextCompat.getColor(context, R.color.iconsColor)
                            )
                            val tintedDrawable2 = tintDrawable(
                                drawable2,
                                ContextCompat.getColor(context, R.color.iconsColor)
                            )


                            (aa.get(0) as RadioButton).setCompoundDrawablesWithIntrinsicBounds(
                                tintedDrawable,
                                null,
                                tintedDrawable2,
                                null
                            )

                            val drawable12 = ContextCompat.getDrawable(
                                context,
                                R.drawable.ic_light_mode
                            )
                            val drawable22 = ContextCompat.getDrawable(
                                context,
                                R.drawable.customcheckbox
                            )
                            val tintedDrawable11 = tintDrawable(
                                drawable12,
                                ContextCompat.getColor(context, R.color.iconsColor)
                            )
                            val tintedDrawable22 = tintDrawable(
                                drawable22,
                                ContextCompat.getColor(context, R.color.iconsColor)
                            )


                            (aa.get(1) as RadioButton).setCompoundDrawablesWithIntrinsicBounds(
                                tintedDrawable11,
                                null,
                                tintedDrawable22,
                                null
                            )

                            val drawable111 = ContextCompat.getDrawable(
                                context,
                                R.drawable.ic_dark_mode
                            )
                            val drawable222 = ContextCompat.getDrawable(
                                context,
                                R.drawable.customcheckbox
                            )

                            val tintedDrawable111 = tintDrawable(
                                drawable111,
                                ContextCompat.getColor(context, R.color.color_accent)
                            )
                            val tintedDrawable222 = tintDrawable(
                                drawable222,
                                ContextCompat.getColor(context, R.color.color_accent)
                            )


                            (aa.get(2) as RadioButton).setCompoundDrawablesWithIntrinsicBounds(
                                tintedDrawable111,
                                null,
                                tintedDrawable222,
                                null
                            )
                        }
                    }
                } else {
                    aa.forEach {
                        if (it.id != i) {
                            (it as RadioButton).setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.black
                                )
                            )
                        } else {
                            (it as RadioButton).setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.color_accent
                                )
                            )
                        }
                    }

                }


            }

            this.findViewById<TextView>(R.id.tvCreate).setOnClickListener {
                onSelect(selection, false)
                dialog?.dismiss()
            }
        }
    }

    fun show() {
        dialog = builder.create()
        dialog?.setOnDismissListener {
            onSelect.invoke(
                if (selection != -1) selection else selected ?: 0, true
            )
        }
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.show()
    }
}