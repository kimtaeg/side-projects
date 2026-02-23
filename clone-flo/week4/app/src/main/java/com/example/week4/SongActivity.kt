package com.example.week4

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.week4.databinding.ActivitySongBinding

class SongActivity : AppCompatActivity() {

    //소괄호 : 클래스를 다른 클래스로 상속을 진행할 때는 소괄호를 넣어줘야 한다.
    //xml연결
    private lateinit var binding : ActivitySongBinding
    //데이터 클래스 연결
    private lateinit var song : Song
    //putextra를이용
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySongBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val title = intent.getStringExtra("title") ?: "제목 없음"
        val singer = intent.getStringExtra("singer") ?: "가수 없음"
/*
        val coverImg = intent.getStringExtra("coverImg", binding.songAlbumIv)
*/


        song = Song(title, singer,)

        binding.songMusicTitleTv.text = song.title
        binding.songSingerNameTv.text = song.singer
       /* binding.songAlbumIv.setImageResource(coverimg)*/

        initClickListener()

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
    //클릭 이벤트
    private fun initClickListener() {
        // 닫기 버튼 클릭 시 → MainActivity로 데이터 반환
        binding.songDownIb.setOnClickListener {
            val resultIntent = Intent().apply {
                putExtra("albumTitle", song.title)
                putExtra("singer", song.singer)
            }
            setResult(RESULT_OK, resultIntent)
            val toastMessage = "제목 : ${song.title}, 가수 : ${song.singer}"
            Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show()
            finish()
        }
    }
        //일시정지 재생버튼 변환
        fun setPlayerStatus(isPlaying: Boolean) {
            if (isPlaying) {
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