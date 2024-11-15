package com.utilitytoolbox.qrscanner.barcodescanner.adapter

import android.content.Context
import android.graphics.PorterDuff
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.utilitytoolbox.qrscanner.barcodescanner.R
import com.utilitytoolbox.qrscanner.barcodescanner.model.QRMainType


class CreateQRGridAdapter(
    var context: Context,
    var list: MutableList<QRMainType>,
    private val listener: Listener
) :
    RecyclerView.Adapter<CreateQRGridAdapter.ViewHolder>() {

    private var createQRsData: MutableList<QRMainType> = mutableListOf()

    init {
        createQRsData = list
    }

    interface Listener {
        fun onCreateQrTypeClick(qrMainType: QRMainType)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_create_qr, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val current = createQRsData[position]

        Log.d("jhad89adjkhkd", "here is coming icon -> ${current.icon}")
        holder.apply {
            icon.setImageResource(current.icon)
            typeTitle.text = current.content

            mainItem.setOnClickListener {
                listener.onCreateQrTypeClick(current)
            }

            // Set a tint color
            val tintColor = ContextCompat.getColor(context, current.color)
            frameLayout.background.setColorFilter(tintColor, PorterDuff.Mode.SRC_IN)
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var icon: ImageView
        var typeTitle: TextView
        var mainItem: CardView
        var frameLayout: FrameLayout

        init {
            icon = itemView.findViewById(R.id.iv_schema)
            typeTitle = itemView.findViewById(R.id.tv_title)
            mainItem = itemView.findViewById(R.id.rootView)
            frameLayout = itemView.findViewById(R.id.frameLayout11)
        }

    }

    override fun getItemCount(): Int {
        return createQRsData.size
    }
}