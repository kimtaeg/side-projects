package com.example.week2

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.week2.databinding.ActivitySongBinding

class SongActivity : AppCompatActivity() {

    lateinit var binding : ActivitySongBinding

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        binding = ActivitySongBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.songDownIb.setOnClickListener{
            finish() //할 작업을 쓰면 된다.
        }
        binding.songMiniplayerIv.setOnClicklistener{
            setPlayerStatus(false)
        }
        binding.songPauseIv.setOnClicklistener{
            setPlayerStatus(true)
        }
    }

    // 정지버튼 플레이버튼 바뀌게하는
    fun setPlayerStatus(isPlaying : Boolean){
        if(isPlaying){
            binding.songMiniplayerIv.visivility = View.VISIBLE
            binding.songPauseIv.visibility = View.GONE
        }
        else{
            binding.songMiniplayerIv.visivility = View.GONE
            binding.songPauseIv.visibility = View.VISIBLE
        }
    }
}