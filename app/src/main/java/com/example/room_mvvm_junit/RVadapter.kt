package com.example.room_mvvm_junit

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.room_mvvm_junit.databinding.ItemListBinding
import com.example.room_mvvm_junit.db.ImageDataModel
import javax.inject.Inject

class RVadapter @Inject constructor(
    private val imageList: List<ImageDataModel>,
    private val onItemClick: (ImageDataModel) -> Unit
) : RecyclerView.Adapter<MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val bindView: ItemListBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context), R.layout.item_list, parent, false    )
        return MyViewHolder(bindView)
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(imageList[position], onItemClick)
    }
    override fun getItemCount() = imageList.size
}

class MyViewHolder(private val binding: ItemListBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: ImageDataModel, onItemClick: (ImageDataModel) -> Unit) {
        Glide.with(itemView.context)
            .load(item.imageURL)
            .into(binding.ivImage)
        binding.tvTitle.text = item.title
        binding.rvItemListLayout.setOnClickListener { onItemClick(item) }
    }
}