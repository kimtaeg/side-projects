package com.example.week5

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.week5.databinding.ActivitySongBinding

class SongActivity : AppCompatActivity() {

    //소괄호 : 클래스를 다른 클래스로 상속을 진행할 때는 소괄호를 넣어줘야 한다.
    //xml연결
    private lateinit var binding : ActivitySongBinding
    //데이터 클래스 연결 데이터클래스를 초기화해주는 함수
    private lateinit var song : Song
    private lateinit var timer : Timer
    //putextra를이용
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySongBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 데이터를 받아오는
        initSong()
        setPlayer(song)

        /*val title = intent.getStringExtra("title") ?: "제목 없음"
        val singer = intent.getStringExtra("singer") ?: "가수 없음"
*//*
        val coverImg = intent.getStringExtra("coverImg", binding.songAlbumIv)
*//*


        song = Song(title, singer,)

        binding.songMusicTitleTv.text = song.title
        binding.songSingerNameTv.text = song.singer*/
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
            /*val resultIntent = Intent().apply {
                putExtra("albumTitle", song.title)
                putExtra("singer", song.singer)
            }
            setResult(RESULT_OK, resultIntent)
            val toastMessage = "제목 : ${song.title}, 가수 : ${song.singer}"
            Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show()*/
            finish()
        }
    }
    //일시정지 재생버튼 변환
    fun setPlayerStatus(isPlaying: Boolean) {
        song.isPlaying = isPlaying
        timer.isPlaying = isPlaying
        if (isPlaying) {
            //xml파일에서 이미지 바꾸기
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
    override fun onDestroy() {
        super.onDestroy()
        timer.interrupt()
    }

    private fun initSong(){
        if(intent.hasExtra("title")&& intent.hasExtra("singer")){
            song = Song(
                intent.getStringExtra("title")!!,
                intent.getStringExtra("singer")!!,
                null,
                intent.getIntExtra("second", 0),
                intent.getIntExtra("playtime",60),
                intent.getBooleanExtra("isPlaying",false)
            )
        }
        startTimer()
    }
    private fun setPlayer(song:Song){
        binding.songMusicTitleTv.text = intent.getStringExtra("title")!!
        binding.songSingerNameTv.text = intent.getStringExtra("singer")!!
        binding.songStartTimeTv.text = String.format("%02d:%02d",song.second / 60, song.second % 60)
        binding.songEndTimeTv.text = String.format("%02d:%02d",song.playTime / 60, song.playTime % 60)
        //노래 재생 진행 상태를 반영해주는 여기에서 오류가 뜸
        binding.songProgressSb.progress = (song.second * 1000 / song.playTime)

        setPlayerStatus(song.isPlaying)
    }
    private fun startTimer(){
        timer = Timer(song.playTime,song.isPlaying)
        timer.start()
    }
    //내부 클래스 내부의 변수에 접근 못함
    inner class Timer(private val playTime: Int, var isPlaying: Boolean = true):Thread(){

        private var second : Int =0
        private var mills : Float = 0f
        private var TIMER_LIMIT_MILLS = 15000f

        override fun run(){
            super.run()
            try{
                while(true){
                    /*if (isPlaying && mills >= TIMER_LIMIT_MILLS) {
                        runOnUiThread {
                            setPlayerStatus(true)
                            Toast.makeText(this@SongActivity, "15초 타이머 종료!", Toast.LENGTH_SHORT).show()
                        }
                        break
                    }*/

                    if(second >= playTime){
                        /*runOnUiThread {
                            setPlayerStatus(true) // 일시 정지 상태로 변경 (노래 끝)
                        }*/
                        break
                    }

                    if(isPlaying) {
                        sleep(50)
                        mills += 50
                        runOnUiThread {
                            binding.songProgressSb.progress = ((mills / playTime) * 100).toInt()
                        }

                        if (mills % 1000 == 0f) {
                            runOnUiThread {
                                binding.songStartTimeTv.text =
                                    String.format("%02d:%02d", second / 60, second % 60)
                            }
                            second++
                        }

                        //10초 타이머
                        /*if (mills >= 15){
                            runOnUiThread { setPlayerStatus(false)
                            break}
                        }*/
                    }

                }

            }catch(e: InterruptedException){
                Log.d("Song","스레드가 죽었습니다. ${e.message}")
            }

        }
    }
}
