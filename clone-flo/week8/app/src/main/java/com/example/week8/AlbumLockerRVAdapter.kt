package com.example.week8

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.week8.databinding.ItemLokerAlbumBinding

class AlbumLockerRVAdapter (): RecyclerView.Adapter<AlbumLockerRVAdapter.ViewHolder>() {

    // 앨범 리스트
    private val albums = ArrayList<Album>()

    interface MyItemClickListener{
        fun onRemoveAlbum(albumId: Int)
        fun onItemClick(album: Album)
    }

    private lateinit var mItemClickListener: MyItemClickListener

    fun setMyItemClickListener(itemClickListener: MyItemClickListener){
        mItemClickListener = itemClickListener
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): AlbumLockerRVAdapter.ViewHolder {
        val binding: ItemLokerAlbumBinding = ItemLokerAlbumBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(albums[position])

        // 1. 앨범 전체 클릭 리스너
        holder.itemView.setOnClickListener {
            mItemClickListener.onItemClick(albums[position])
        }


        holder.binding.saveMoreIv.setOnClickListener {
            mItemClickListener.onRemoveAlbum(albums[position].id)
            removeAlbum(position)
        }
    }


    override fun getItemCount(): Int = albums.size

    @SuppressLint("NotifyDataSetChanged")
    fun addAlbums(albums: ArrayList<Album>) {
        this.albums.clear()
        this.albums.addAll(albums)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun removeAlbum(position: Int){
        albums.removeAt(position)
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: ItemLokerAlbumBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(album: Album){
            binding.itemSaveCoverImgIv.setImageResource(album.coverImg!!)
            binding.saveTitleTv.text = album.title
            binding.saveNameTv.text = album.singer
        }
    }
}