package com.utilitytoolbox.qrscanner.barcodescanner.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.utilitytoolbox.qrscanner.barcodescanner.databinding.ResultScreenTypeBinding

class MyRcAdapter(
    private val itemList: ArrayList<TypeOfResult>,
    private val onItemClick: (TypeOfResult) -> Unit
) : RecyclerView.Adapter<MyRcAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ResultScreenTypeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindData(typeOfResult: TypeOfResult) {
            binding.ivItem.setImageResource(typeOfResult.icon)
            binding.tvOpen.text = typeOfResult.name
            binding.liOpen.setOnClickListener {
                onItemClick(typeOfResult)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ResultScreenTypeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        Log.d("MyRecyclerViewAdapter", "onCreateViewHolder -> $binding")
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(itemList[position])
        Log.d("MyRecyclerViewAdapter", "onBindViewHolder -> $position")
    }

    override fun getItemCount(): Int {
        Log.d("MyRecyclerViewAdapter", "getItemCount -> ${itemList.size}")
        return itemList.size
    }
}
