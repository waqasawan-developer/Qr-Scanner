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
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.utilitytoolbox.qrscanner.barcodescanner.R


class DelimiterIconButton : FrameLayout {
    private val view: View

    lateinit var image_view_schema: ImageView
    lateinit var image_view_arrow: ImageView
    lateinit var layout_image: FrameLayout
    lateinit var text_view: TextView
    lateinit var delimiter: View

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, -1)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        view = LayoutInflater
            .from(context)
            .inflate(R.layout.layout_ic_btn_delimiter, this, true)
        image_view_schema = view.findViewById(R.id.image_view_schema)
        layout_image = view.findViewById(R.id.layout_image)
        image_view_arrow = view.findViewById(R.id.image_view_arrow)
        text_view = view.findViewById(R.id.text_view)
        delimiter = view.findViewById(R.id.delimiter)

        context.obtainStyledAttributes(attrs, R.styleable.IconButtonWithDelimiter).apply {
            showIcon(this)
            //showIconBackgroundColor(this)
            showText(this)
            showArrow(this)
            showDelimiter(this)
            recycle()
        }
    }

    private fun showIcon(attributes: TypedArray) {
        val iconResId = attributes.getResourceId(R.styleable.IconButtonWithDelimiter_icon, -1)
        val icon = AppCompatResources.getDrawable(context, iconResId)
        image_view_schema.setImageDrawable(icon)
    }

    private fun showIconBackgroundColor(attributes: TypedArray) {
        val color = attributes.getColor(
            R.styleable.IconButtonWithDelimiter_iconBackground,
            view.context.resources.getColor(R.color.green)
        )
        (layout_image.background.mutate() as GradientDrawable).setColor(color)
    }

    private fun showText(attributes: TypedArray) {
        text_view.text =
            attributes.getString(R.styleable.IconButtonWithDelimiter_text).orEmpty()
    }

    private fun showArrow(attributes: TypedArray) {
        image_view_arrow.isVisible =
            attributes.getBoolean(R.styleable.IconButtonWithDelimiter_isArrowVisible, false)
    }

    private fun showDelimiter(attributes: TypedArray) {
        delimiter.isInvisible =
            attributes.getBoolean(R.styleable.IconButtonWithDelimiter_isDelimiterVisible, true)
                .not()
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        image_view_schema.isEnabled = enabled
        text_view.isEnabled = enabled
    }
}