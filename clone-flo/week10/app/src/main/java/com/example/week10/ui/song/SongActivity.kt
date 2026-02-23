package com.example.week10.ui.song

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.MediaPlayer
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.week10.R
import com.example.week10.data.Song
import com.example.week10.data.SongDatabase
import com.example.week10.databinding.ActivitySongBinding
import com.example.week10.service.MusicPlayer
import com.google.gson.Gson

class SongActivity : AppCompatActivity() {

    //소괄호 : 클래스를 다른 클래스로 상속을 진행할 때는 소괄호를 넣어줘야 한다.
    //전역 변수
    private lateinit var binding : ActivitySongBinding
    //데이터 클래스 연결 데이터클래스를 초기화해주는 함수
    //private lateinit var timer : Timer
    private var gson: Gson = Gson()
    //음악을 쉽게 재생시켜주는 클래스
    //private var mediaPlayer: MediaPlayer? = null
    private var musicPlayerService: MusicPlayer? = null
    private var isServiceBound = false
    //노래 관리해주는 전역변수
    val songs = arrayListOf<Song>()
    lateinit var songDB : SongDatabase
    var nowPos = 0


    private val handler = android.os.Handler(android.os.Looper.getMainLooper())
    private val updateProgressRunnable = object : Runnable {
        override fun run() {
            if (isServiceBound && musicPlayerService?.isPlaying() == true) {
                val currentPos = musicPlayerService?.getCurrentPosition() ?: 0

                // 프로그레스 바 업데이트
                binding.songProgressSb.progress = currentPos
                // 시간 텍스트 업데이트
                binding.songStartTimeTv.text = formatTime(currentPos)
            }
            // 1초마다 다시 실행
            handler.postDelayed(this, 1000)
        }
    }

    private fun formatTime(millis: Int): String {
        val second = millis / 1000
        return String.format("%02d:%02d", second / 60, second % 60)
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MusicPlayer.MusicPlayerBinder
            musicPlayerService = binder.getService()
            isServiceBound = true

            // 서비스 연결 후 초기 곡 설정
            initSong()

            val duration = musicPlayerService?.getDuration() ?: 0

            binding.songProgressSb.max = duration
            binding.songEndTimeTv.text = formatTime(duration)

            handler.post(updateProgressRunnable)
        }
        override fun onServiceDisconnected(name: ComponentName?) {
            isServiceBound = false
            // 서비스 연결 끊기면 루프 중단
            handler.removeCallbacks(updateProgressRunnable) // 중복 실행 방지
            handler.post(updateProgressRunnable)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySongBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //initPlayList()
        // 데이터를 받아오는
        initSong()
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

        songDB = SongDatabase.getInstance(this)!!
        songs.addAll(songDB.songDao().getSongs())

    }

    override fun onStart() {
        super.onStart()
        val intent = Intent(this, MusicPlayer::class.java)
        bindService(intent,serviceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onStop() {
        super.onStop()
        if (isServiceBound){
            unbindService(serviceConnection)
            isServiceBound = false
        }
    }
    //클릭 이벤트
    private fun initClickListener() {
        // 닫기 버튼 클릭 시 → MainActivity로 데이터 반환
        binding.songDownIb.setOnClickListener {
            finish()
        }
        binding.songNextIv.setOnClickListener {
            moveSong(+1)
        }
        binding.songPreviousIv.setOnClickListener {
            moveSong(-1)
        }

        binding.songMiniplayerIv.setOnClickListener { setPlayerStatus(false) }
        binding.songPauseIv.setOnClickListener { setPlayerStatus(true) }

        //종아요
        binding.songLikeIv.setOnClickListener {
            setLike(songs[nowPos].isLike)
            showCustomLikeToast(songs[nowPos].isLike)
        }



    }
    // 재생/일시정지 로직을 서비스 호출로 변경
    fun setPlayerStatus(isPlaying: Boolean) {
        if (!isServiceBound) return

        if (isPlaying) {
            musicPlayerService?.start()
            binding.songMiniplayerIv.visibility = View.VISIBLE
            binding.songPauseIv.visibility = View.GONE
        } else {
            musicPlayerService?.pause()
            binding.songMiniplayerIv.visibility = View.GONE
            binding.songPauseIv.visibility = View.VISIBLE
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
    //좋아요 토스트
    private fun showCustomLikeToast(isLike: Boolean) {
        val inflater = layoutInflater
        val layout = inflater.inflate(
            R.layout.custom_toast,
            findViewById(R.id.custom_toast_container) // XML의 최상위 LinearLayout ID
        )

        val message: String
        val iconRes: Int

        if (isLike) {
            message = "좋아요 한 곡에 담았습니다."
            iconRes = R.drawable.ic_my_like_on // 좋아요 켜진 아이콘 사용 가정
        } else {
            message = "좋아요 한 곡에서 삭제했습니다."
            iconRes = R.drawable.ic_my_like_off // 좋아요 꺼진 아이콘 사용 가정
        }

        // 텍스트와 아이콘 설정
        val textView = layout.findViewById<TextView>(R.id.toast_message_tv)

        textView.text = message

        // 토스트 객체 생성 및 표시
        val toast = Toast(applicationContext)
        toast.duration = Toast.LENGTH_SHORT
        // 토스트의 위치를 아래 중앙으로 설정 (필요 시 조정)
        toast.setGravity(Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, 0, 100)
        toast.view = layout
        toast.show()
    }
    //일반 커스텀 토스트
    private fun showCustomToast(message: String) {
        val inflater = layoutInflater
        val layout = inflater.inflate(
            R.layout.custom_toast,
            findViewById(R.id.custom_toast_container) // XML의 최상위 LinearLayout ID
        )

        // 텍스트 설정
        val textView = layout.findViewById<TextView>(R.id.toast_message_tv)
        textView.text = message


        // 토스트 객체 생성 및 표시
        val toast = Toast(applicationContext)
        toast.duration = Toast.LENGTH_SHORT
        // 토스트의 위치를 아래 중앙으로 설정 (필요 시 조정)
        toast.setGravity(Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, 0, 100)
        toast.view = layout
        toast.show()
    }



    // 좋아요 함수
    private fun setLike(isLike : Boolean){
        //주의 db의 값 업데이트안함 송다이오 만듬
        songs[nowPos].isLike = !isLike
        songDB.songDao().updateIsLikeById(!isLike,songs[nowPos].id)

        if (isLike) {
            songs[nowPos].isLike = false
            binding.songLikeIv.setImageResource(R.drawable.ic_my_like_off)
        } else {
            songs[nowPos].isLike = true
            binding.songLikeIv.setImageResource(R.drawable.ic_my_like_on)
        }
    }

    private fun moveSong(direct: Int){
        if(nowPos + direct < 0){
            showCustomToast("첫 곡입니다.")
            return
        }
        if(nowPos + direct >= songs.size){
            showCustomToast("마지막 곡입니다.")
            return
        }

        nowPos += direct

        setPlayer(songs[nowPos])
    }


    private fun initSong() {
        if (songs.isEmpty()) return
        if (!isServiceBound) return

        val spf = getSharedPreferences("song", MODE_PRIVATE)
        val songId = spf.getInt("songId", 0)

        // 1. 현재 곡 인덱스 찾기
        nowPos = getPlayingSongPosition(songId)

        // 2. 서비스에서 현재 정보 가져오기
        val duration = musicPlayerService?.getDuration() ?: 0
        val currentPos = musicPlayerService?.getCurrentPosition() ?: 0

        // 3. 진입하자마자 UI에 즉시 반영 (재생 여부와 상관없이)
        binding.songProgressSb.max = duration
        binding.songProgressSb.progress = currentPos
        binding.songEndTimeTv.text = formatTime(duration)
        binding.songStartTimeTv.text = formatTime(currentPos)

        // 4. 곡 정보(제목, 가수) 설정 및 버튼 상태 설정
        setPlayer(songs[nowPos])
    }
    // 주기적으로 서비스의 상태를 가져와 UI를 업데이트하는 함수
    private fun startUpdatingUI() {
        val handler = android.os.Handler(android.os.Looper.getMainLooper())
        handler.post(object : Runnable {
            override fun run() {
                if (isServiceBound && musicPlayerService?.isPlaying() == true) {
                    // 서비스에서 직접 현재 재생 위치와 전체 길이를 가져옴
                    val currentPos = musicPlayerService?.getCurrentPosition() ?: 0
                    val totalDuration = musicPlayerService?.getTotalDuration() ?: 1 // 서비스에 getTotalDuration() 추가 필요

                    if (totalDuration > 0) {
                        // ⭐ (현재시간 * 1000)을 먼저 하고 나누기를 해야 소수점 손실이 없습니다.
                        val progress = (currentPos.toLong() * 1000 / totalDuration).toInt()
                        binding.songProgressSb.progress = progress

                        // 시간 텍스트 업데이트
                        binding.songStartTimeTv.text = formatTime(currentPos)
                    }
                }

                // 1초마다 반복 호출
                handler.postDelayed(this, 1000)
            }
        })
    }

    private fun getPlayingSongPosition(songId: Int): Int{
        for(i in 0 until songs.size){
            if (songs[i].id == songId){
                return i
            }
        }
        return 0
    }
    // 새 곡을 재생할 때 서비스에 전달
    private fun setPlayer(song: Song) {
        if (!isServiceBound) return

        binding.songMusicTitleTv.text = song.title
        binding.songSingerNameTv.text = song.singer
        binding.songAlbumIv.setImageResource(song.coverImg!!)

        val isPlaying = musicPlayerService?.isPlaying() ?: false

        // 서비스에 곡 설정 및 재생 요청
        musicPlayerService?.setPlayer(song)
        setPlayerStatus(isPlaying)
    }

    //사용자가 포커스를 잃었을때 음악 중지
    override fun onPause(){

        super.onPause()
        if (isServiceBound) {
            val sharedPreferences = getSharedPreferences("song", MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putInt("songId", songs[nowPos].id)
            editor.putInt("playSecond", (musicPlayerService?.getCurrentPosition() ?: 0) / 1000)
            editor.apply()
        }
    }
    }



