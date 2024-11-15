package com.utilitytoolbox.qrscanner.barcodescanner.view

import android.content.Context
import android.content.res.TypedArray
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import com.utilitytoolbox.qrscanner.barcodescanner.R


class BtnIcon : FrameLayout {
    private val view: View
    lateinit var text_view: TextView
    lateinit var image_view_schema: ImageView
    lateinit var layout_image: FrameLayout

    var text: String
        get() = text_view.text.toString()
        set(value) {
            text_view.text = value
        }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, -1)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        view = LayoutInflater
            .from(context)
            .inflate(R.layout.layout_ic_btn, this, true)
        text_view = view.findViewById(R.id.text_view)
        image_view_schema = view.findViewById(R.id.image_view_schema)
        layout_image = view.findViewById(R.id.layout_image)
        context.obtainStyledAttributes(attrs, R.styleable.IconButton).apply {
            showIcon(this)
            //showIconBackgroundColor(this)
            showText(this)
            recycle()
        }
    }

    private fun showIcon(attributes: TypedArray) {
        val iconResId = attributes.getResourceId(R.styleable.IconButton_icon, -1)
        val icon = AppCompatResources.getDrawable(context, iconResId)
        image_view_schema.setImageDrawable(icon)
    }

    private fun showIconBackgroundColor(attributes: TypedArray) {
        val color = attributes.getColor(
            R.styleable.IconButton_iconBackground,
            ContextCompat.getColor(view.context, R.color.green)
        )
        (layout_image.background.mutate() as GradientDrawable).setColor(color)
    }

    private fun showText(attributes: TypedArray) {
        text_view.text = attributes.getString(R.styleable.IconButton_text).orEmpty()
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        image_view_schema.isEnabled = enabled
        text_view.isEnabled = enabled
    }
}