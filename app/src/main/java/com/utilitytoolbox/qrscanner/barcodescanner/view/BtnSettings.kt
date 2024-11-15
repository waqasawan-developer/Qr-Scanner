package com.utilitytoolbox.qrscanner.barcodescanner.view

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.view.isVisible
import com.utilitytoolbox.qrscanner.barcodescanner.R


class BtnSettings : FrameLayout {
    private val view: View
    var switch: androidx.appcompat.widget.SwitchCompat
    var tv_hint: TextView
    var tv_title: TextView

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, -1)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {

        view = LayoutInflater
            .from(context)
            .inflate(R.layout.layout_settings_btn, this, true)

        switch = view.findViewById(R.id.sw_control)
        tv_hint = view.findViewById<TextView>(R.id.text_view_hint)
        tv_title = view.findViewById<TextView>(R.id.tv_title)


        context.obtainStyledAttributes(attrs, R.styleable.SettingsButton).apply {
            showText(this)
            showHint(this)
            showSwitch(this)
            recycle()
        }
    }

    var hint: String
        get() = tv_hint.text.toString()
        set(value) {
            tv_hint.apply {
                text = value
                isVisible = text.isNullOrEmpty().not()
            }
        }

    var isChecked: Boolean
        get() = switch.isChecked
        set(value) {
            switch.isChecked = value
        }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        tv_title.isEnabled = enabled
    }

    fun setCheckedChangedListener(listener: ((Boolean) -> Unit)?) {
        switch.setOnCheckedChangeListener { _, isChecked ->
            listener?.invoke(isChecked)
        }
    }

    private fun showText(attributes: TypedArray) {
        tv_title.text = attributes.getString(R.styleable.SettingsButton_text).orEmpty()
    }

    private fun showHint(attributes: TypedArray) {
        hint = attributes.getString(R.styleable.SettingsButton_hint).orEmpty()
    }

    private fun showSwitch(attributes: TypedArray) {
        switch.isVisible =
            attributes.getBoolean(R.styleable.SettingsButton_isSwitchVisible, true)
        if (switch.isVisible) {
            view.setOnClickListener {
                switch.toggle()
            }
        }
    }
}