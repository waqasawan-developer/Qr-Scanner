package com.utilitytoolbox.qrscanner.barcodescanner.view

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.RadioButton
import android.widget.TextView
import androidx.core.view.isInvisible
import com.utilitytoolbox.qrscanner.barcodescanner.R

class RadioSettingsBtn : FrameLayout {
    private val view: View
    lateinit var delimiter: View
    lateinit var text_view_text: TextView
    lateinit var radio_button: RadioButton

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, -1)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        view = LayoutInflater
            .from(context)
            .inflate(R.layout.layout_settings_radio_btn, this, true)

        text_view_text = view.findViewById(R.id.text_view_text)
        radio_button = view.findViewById(R.id.radio_button)
        delimiter = view.findViewById(R.id.delimiter)

        context.obtainStyledAttributes(attrs, R.styleable.SettingsRadioButton).apply {
            showText(this)
            showDelimiter(this)
            recycle()
        }

        view.setOnClickListener {
            radio_button.toggle()
        }
    }

    var isChecked: Boolean
        get() = radio_button.isChecked
        set(value) {
            radio_button.isChecked = value
        }

    fun setCheckedChangedListener(listener: ((Boolean) -> Unit)?) {
        radio_button.setOnCheckedChangeListener { _, isChecked ->
            listener?.invoke(isChecked)
        }
    }

    private fun showText(attributes: TypedArray) {
        text_view_text.text = attributes.getString(R.styleable.SettingsRadioButton_text).orEmpty()
    }

    private fun showDelimiter(attributes: TypedArray) {
        delimiter.isInvisible =
            attributes.getBoolean(R.styleable.SettingsRadioButton_isDelimiterVisible, true).not()
    }
}