package com.utilitytoolbox.qrscanner.barcodescanner.adapter

import android.text.format.DateUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.utilitytoolbox.qrscanner.barcodescanner.R
import com.utilitytoolbox.qrscanner.barcodescanner.model.MineBarCode
import com.utilitytoolbox.qrscanner.barcodescanner.utils.toImageId
import com.utilitytoolbox.qrscanner.barcodescanner.utils.toStringId
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class HistoryAdapter(private val dataType: Boolean, private val listener: Listener) :
    RecyclerView.Adapter<ViewHolder>() {

    private var mHistoryData: MutableList<MineBarCode> = mutableListOf()

    interface Listener {
        fun onBarcodeClicked(MineBarCode: MineBarCode)
        fun onDeleteClicked(MineBarCode: MineBarCode)
        fun onFavoriteClicked(MineBarCode: MineBarCode)
    }

    fun setData(list: MutableList<MineBarCode>) {
        mHistoryData = list
        notifyDataSetChanged()
    }

    fun getData(): MutableList<MineBarCode> {
        return mHistoryData
    }

    private val dateFormatter = SimpleDateFormat("MMM dd , yyyy hh:mm a", Locale.ENGLISH)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        if (viewType == 1) {
            val inflater = LayoutInflater.from(parent.context)
            val view = inflater.inflate(R.layout.li_header, parent, false)

            return viewHolderHeader(view)
        } else {
            val inflater = LayoutInflater.from(parent.context)
            val view = inflater.inflate(R.layout.item_barcode_history, parent, false)

            return ViewHolderold(view)
        }
    }


    override fun getItemViewType(position: Int): Int {
        Log.e("TAG", "getItemViewType: ${dataType}")


        if (mHistoryData[position]?.isHeader == true) {
            return 1
        } else {
            return 0
        }

    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var model = mHistoryData[position]
        Log.d("dkjfdkfd87dfkjdf", "here is ${model.isHeader}")
        if (model.isHeader) {
            (holder as viewHolderHeader).apply {
                try {
                    var time = "(${dateFormatter.format(model.datetime)})"
                    val headerTxt =
                        if (time?.contains(",") == true || time?.contains(
                                ":"
                            ) == true
                        ) {
                            DateUtils.getRelativeTimeSpanString(
                                model.datetime,
                                System.currentTimeMillis(),
                                DateUtils.DAY_IN_MILLIS,
                                DateUtils.FORMAT_SHOW_DATE
                            ).toString()
                        } else {
                            DateUtils.getRelativeTimeSpanString(
                                model.datetime,
                                System.currentTimeMillis(),
                                DateUtils.DAY_IN_MILLIS,
                                DateUtils.FORMAT_SHOW_DATE
                            ).toString()
                        }
                    mHeaderTxt.text = headerTxt
                } catch (e: ParseException) {
                    e.printStackTrace()
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }
        } else {
            model.also { barcode ->
                (holder as ViewHolderold).show(barcode, position == itemCount.dec())
            }
        }

    }

    inner class ViewHolderold(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun show(MineBarCode: MineBarCode, isLastItem: Boolean) {

            itemView.findViewById<TextView>(R.id.tv_date).text =
                "(${dateFormatter.format(MineBarCode.datetime)})"

            itemView.findViewById<TextView>(R.id.text_view_format)
                .setText(MineBarCode.format.toStringId())

            itemView.findViewById<TextView>(R.id.text_view_text).text =
                MineBarCode.name ?: MineBarCode.formattedText

            val imageId = MineBarCode.schema.toImageId() ?: MineBarCode.format.toImageId()
            val image = AppCompatResources.getDrawable(itemView.context, imageId)
            itemView.findViewById<ImageView>(R.id.image_view_schema).setImageDrawable(image)

            if (MineBarCode.isFavorite) {
                itemView.findViewById<ImageView>(R.id.image_view_favorite)
                    .setImageResource(R.drawable.ic_favorite_qr_checked)
            } else {
                itemView.findViewById<ImageView>(R.id.image_view_favorite)
                    .setImageResource(R.drawable.ic_favorite_qr_unchecked)
            }

            // itemView.findViewById<View>(R.id.delimiter).isInvisible = isLastItem

            itemView.findViewById<ImageView>(R.id.iv_delete_qr).isVisible = dataType
            itemView.findViewById<ImageView>(R.id.image_view_favorite).isVisible = dataType
            setClickListener(MineBarCode)
        }

        private fun setClickListener(MineBarCode: MineBarCode) {
            itemView.setOnClickListener {
                listener.onBarcodeClicked(MineBarCode)
            }

            itemView.findViewById<ImageView>(R.id.iv_delete_qr).setOnClickListener {
                listener.onDeleteClicked(MineBarCode)
            }

            itemView.findViewById<ImageView>(R.id.image_view_favorite).setOnClickListener {
                listener.onFavoriteClicked(MineBarCode)
            }
        }
    }


    inner class viewHolderHeader(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var mHeaderTxt: TextView

        init {
            mHeaderTxt = itemView.findViewById(R.id.tv_header_title)
        }
    }


    override fun getItemCount(): Int {
        return mHistoryData.size
    }
}