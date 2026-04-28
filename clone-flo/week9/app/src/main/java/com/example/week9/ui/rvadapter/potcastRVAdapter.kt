package com.example.week9.ui.rvadapter

import UI.potcast.Potcast
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.week9.databinding.ItemPotcastBinding

class potcastRVAdapter(private val potcastList: ArrayList<Potcast>) : RecyclerView.Adapter<potcastRVAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemPotcastBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(potcast: Potcast){
            binding.potcastTittleTv.text = potcast.title
            binding.potcastSingerTv.text = potcast.singer
            potcast.coverImg?.let {
                binding.potcastIv.setImageResource(it)
            }
    }
}
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPotcastBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(potcastList[position])
    }
    override fun getItemCount(): Int = potcastList.size
}