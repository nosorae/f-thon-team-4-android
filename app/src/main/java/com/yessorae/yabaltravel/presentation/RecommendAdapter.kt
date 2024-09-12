package com.yessorae.yabaltravel.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Adapter
import androidx.recyclerview.widget.RecyclerView
import com.yessorae.yabaltravel.databinding.AdapterRecommendBinding
import com.yessorae.yabaltravel.presentation.model.RecommendItem

class RecommendAdapter() :
    RecyclerView.Adapter<RecommendAdapter.ViewHolder>() {
    var recommendList =  ArrayList<RecommendItem>()
        set(value){
            field = value
            notifyDataSetChanged()
        }
    inner class ViewHolder(private val binding: AdapterRecommendBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: RecommendItem) {
            binding.txtName.text = item.name
            binding.txtDescription.text = item.description
            binding.txtAddress.text = item.address
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            AdapterRecommendBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = recommendList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(recommendList[position])
    }

}