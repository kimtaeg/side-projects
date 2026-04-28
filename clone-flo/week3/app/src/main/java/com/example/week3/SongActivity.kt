package com.example.week3

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.week3.databinding.ActivitySongBinding

class SongActivity : AppCompatActivity() {

    //소괄호 : 클래스를 다른 클래스로 상속을 진행할 때는 소괄호를 넣어줘야 한다.

    private lateinit var binding : ActivitySongBinding

    //putextra를이용
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySongBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //데이터가 올수도있고 안올수도있기때문에 if문사용, 구조는 songactivity에서 스트링타입으로 데이터를 mainactivty로 보내는것
        if(intent.hasExtra("title") && intent.hasExtra("singer")){
            //텍스트뷰에 텍스트를 바꿔줄건데 인텐트라는 상자에서 타이틀이라는 걸 스트링으로 바뀌준다는 의미
            binding.songMusicTitleTv.text = intent.getStringExtra("title")
            binding.songSingerNameTv.text = intent.getStringExtra("singer")
        }

        binding.songDownIb.setOnClickListener {
            finish()
        }
//재생 정지 변환
        binding.songMiniplayerIv.setOnClickListener {
            setPlayerStatus(false)
        }

        binding.songPauseIv.setOnClickListener {
            setPlayerStatus(true)
        }
//반복 변환
        binding.songRepeatIv.setOnClickListener {
            setReatStatus(false)
        }
        binding.songRepeatColorIv.setOnClickListener{
            setReatStatus(true)
        }
//랜덤 변환
        binding.songRandomIv.setOnClickListener {
            setRandomStatus(false)
        }
        binding.songRandomColorIv.setOnClickListener{
            setRandomStatus(true)
        }

    }
    //일시정지 재생버튼 변환
    fun setPlayerStatus (isPlaying : Boolean){
        if(isPlaying){
            binding.songMiniplayerIv.visibility = View.VISIBLE //보이기
            binding.songPauseIv.visibility = View.GONE //숨기기
        } else {
            binding.songMiniplayerIv.visibility = View.GONE //숨기기
            binding.songPauseIv.visibility = View.VISIBLE //보이기
        }
    }


//반복 변환
    fun setReatStatus(isPlaying : Boolean){
        if(isPlaying){
            binding.songRepeatIv.visibility = View.VISIBLE //보이기
            binding.songRepeatColorIv.visibility = View.GONE //숨기기
        } else {
            binding.songRepeatIv.visibility = View.GONE //숨기기
            binding.songRepeatColorIv.visibility = View.VISIBLE //보이기
        }
    }

    //랜덤 변환
    fun setRandomStatus(isPlaying : Boolean){
        if(isPlaying){
            binding.songRandomIv.visibility = View.VISIBLE //보이기
            binding.songRandomColorIv.visibility = View.GONE //숨기기
        } else {
            binding.songRandomIv.visibility = View.GONE //숨기기
            binding.songRandomColorIv.visibility = View.VISIBLE //보이기
        }
    }

}