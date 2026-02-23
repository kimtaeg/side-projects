package com.example.week8

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.week8.databinding.ItemSaveBinding

class SaveRVAdapter() : RecyclerView.Adapter<SaveRVAdapter.ViewHolder>() {

    //재생 중인 아이템의 위치를 기억해주는 변수, -1은 현재 아무것도 재생 중이 아님을 말한다.
    //private var playingPosition = -1
    private val songs = ArrayList<Song>()

    // 클릭, 삭제 인터페이스
    interface MyItemClickListener{
        //fun onItemClick(album: Album)
        fun onRemoveSong(songId: Int)
    }
    private lateinit var myItemClickListener: MyItemClickListener
    fun setMyItemClickListener(itemClickListener: MyItemClickListener){
        myItemClickListener = itemClickListener
    }
    // 아이템 삭제,추가 함수
//    fun addItem(album: Album){
//        albumList.add(album)
//        notifyDataSetChanged()
//    }
    @SuppressLint("NOfityDataSetChanged")
    fun removeSong(position: Int){
        songs.removeAt(position)

        /*if (position == playingPosition)
            playingPosition = -1 // 재생 중인 아이템이 삭제된 경우
        else if (position < playingPosition){
            playingPosition -= 1 // 삭제된 아이템이 재생 중인 아이템보다 앞쪽이면 인덱스 보정
        }*/
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): SaveRVAdapter.ViewHolder {
        val binding: ItemSaveBinding = ItemSaveBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)

        return ViewHolder(binding)
    }
    // 매번 아이템의 상태를 알려주는 것
    override fun onBindViewHolder(holder: SaveRVAdapter.ViewHolder, position: Int) {
        holder.bind(songs[position])
        holder.binding.saveMoreIv.setOnClickListener{
            myItemClickListener.onRemoveSong(songs[position].id)
            removeSong(position)}
    }

    override fun getItemCount(): Int = songs.size

    @SuppressLint("NotifyDataSetChanged")
    fun addSongs(songs: ArrayList<Song>){
        this.songs.clear()
        this.songs.addAll(songs)
        notifyDataSetChanged()
    }


    inner class ViewHolder(val binding: ItemSaveBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(song : Song){
            binding.saveTitleTv.text = song.title
            binding.saveNameTv.text = song.singer
            binding.itemSaveCoverImgIv.setImageResource(song.coverImg!!)

            //플레이버튼상태
            /*if (position == playingPosition) {
                // 아이템 재생중
                binding.savePlayIv.visibility = View.GONE
                binding.savePauseIv.visibility = View.VISIBLE
            } else {
                // 재생 중 아님
                binding.savePlayIv.visibility = View.VISIBLE
                binding.savePauseIv.visibility = View.GONE
            }

            binding.savePlayIv.setOnClickListener {
                val previousPosition = playingPosition
                playingPosition = position
                notifyItemChanged(previousPosition) // 이전 재생 항목
                notifyItemChanged(playingPosition)  // 새 재생 항목
            }

            binding.savePauseIv.setOnClickListener {
                val previousPosition = playingPosition
                playingPosition = -1
                notifyItemChanged(previousPosition)
            }*/
        }
    }

}