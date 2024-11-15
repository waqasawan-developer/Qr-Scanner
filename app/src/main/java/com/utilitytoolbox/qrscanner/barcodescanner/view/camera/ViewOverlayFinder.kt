
package com.utilitytoolbox.qrscanner.barcodescanner.view.camera

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.utilitytoolbox.qrscanner.barcodescanner.R


class ViewOverlayFinder(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val boxPaint: Paint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.barcode_reticle_stroke)
        style = Paint.Style.STROKE
        strokeWidth =
            context.resources.getDimensionPixelOffset(R.dimen.barcode_reticle_stroke_width)
                .toFloat()
    }

    private val scrimPaint: Paint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.barcode_reticle_background)
    }

    private val eraserPaint: Paint = Paint().apply {
        strokeWidth = boxPaint.strokeWidth
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }

    private val boxCornerRadius: Float = context.resources.getDimensionPixelOffset(R.dimen.barcode_reticle_corner_radius).toFloat()
    private val boxCornerStrokeRadius: Float = context.resources.getDimensionPixelOffset(R.dimen.barcode_stroke_corner_radius).toFloat()

    private var boxRect: RectF? = null

    fun setViewFinder() {
        val overlayWidth = width.toFloat()
        val overlayHeight = height.toFloat()
        val boxWidth = overlayWidth * 80 / 100
        val boxHeight = overlayHeight * 36 / 100
        val cx = overlayWidth / 2
        val cy = overlayHeight / 2
        boxRect =
            RectF(cx - boxWidth / 2, cy - boxHeight / 2, cx + boxWidth / 2, cy + boxHeight / 2)

        invalidate()
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        boxRect?.let {
            // Draws the dark background scrim and leaves the box area clear.
            canvas.drawRect(0f, 0f, canvas.width.toFloat(), canvas.height.toFloat(), scrimPaint)
            // As the stroke is always centered, so erase twice with FILL and STROKE respectively to clear
            // all area that the box rect would occupy.
            eraserPaint.style = Paint.Style.FILL
            canvas.drawRoundRect(it, boxCornerRadius, boxCornerRadius, eraserPaint)
            eraserPaint.style = Paint.Style.STROKE
            canvas.drawRoundRect(it, boxCornerRadius, boxCornerRadius, eraserPaint)
            // Draws the box.
            //canvas.drawRoundRect(it, boxCornerRadius, boxCornerRadius, boxPaint)


            // Draw the box without corners
            //boxPaint.style = Paint.Style.STROKE
            //boxPaint.strokeJoin = Paint.Join.ROUND // Use round joins for the corners
            boxPaint.strokeCap = Paint.Cap.SQUARE // Use square caps for the corners
            //canvas.drawRect(it, boxPaint)

            // Draw the corners with a thicker stroke
            //boxPaint.strokeWidth = boxPaint.strokeWidth * 2 // Adjust the stroke width as needed
            val cornerRadius = boxCornerStrokeRadius // Adjust the corner radius as needed

            // Draw top-left corner
            canvas.drawLine(it.left, it.top, it.left + cornerRadius, it.top, boxPaint)
            canvas.drawLine(it.left, it.top, it.left, it.top + cornerRadius, boxPaint)

            // Draw top-right corner
            canvas.drawLine(it.right, it.top, it.right - cornerRadius, it.top, boxPaint)
            canvas.drawLine(it.right, it.top, it.right, it.top + cornerRadius, boxPaint)

            // Draw bottom-left corner
            canvas.drawLine(it.left, it.bottom, it.left + cornerRadius, it.bottom, boxPaint)
            canvas.drawLine(it.left, it.bottom, it.left, it.bottom - cornerRadius, boxPaint)

            // Draw bottom-right corner
            canvas.drawLine(it.right, it.bottom, it.right - cornerRadius, it.bottom, boxPaint)
            canvas.drawLine(it.right, it.bottom, it.right, it.bottom - cornerRadius, boxPaint)
        }
    }

}