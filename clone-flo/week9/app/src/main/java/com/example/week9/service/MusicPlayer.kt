package com.example.week9.service

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import com.example.week9.data.Song
import com.example.week9.data.SongDatabase

class MusicPlayer : Service() {

    private var mediaPlayer: MediaPlayer? = null
    private var currentMusic: String? = null
    private var currentIndex = 0
    private lateinit var playlist: List<Song>
    private var isRepeat = false

    override fun onCreate() {
        super.onCreate()
        // 앱 전체에서 사용할 곡 리스트를 DB에서 불러오기
        val songDB = SongDatabase.Companion.getInstance(this)!!
        playlist = songDB.songDao().getSongs()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val command = intent?.getStringExtra("command")
        val musicName = intent?.getStringExtra("music")
        isRepeat = intent?.getBooleanExtra("isRepeat", false) ?: false
        val second = intent?.getIntExtra("second", 0) ?: 0

        when (command) {
            "play" -> {
                val newIndex = playlist.indexOfFirst { it.music == musicName }.takeIf { it >= 0 } ?: 0

                if (mediaPlayer != null && currentMusic == musicName) {
                    //전달받은 second 값으로 시점을 재설정합니다.
                    if (second > 0) {
                        mediaPlayer?.seekTo(second * 1000)
                    }
                    playlist[currentIndex].second = second //동기화
                    mediaPlayer?.start()
                    startTimer()
                } else {
                    currentIndex = newIndex
                    playlist[currentIndex].second = second
                    play(playlist[currentIndex]) // 이건 새로 시작할 때만
                }
            }

            "pause" -> {
                mediaPlayer?.pause()
                stopTimer()
            }

            "next" -> {
                currentIndex = (currentIndex + 1) % playlist.size
                play(playlist[currentIndex])
            }

            "prev" -> {
                currentIndex = if (currentIndex - 1 < 0) playlist.lastIndex else currentIndex - 1
                play(playlist[currentIndex])
            }
        }

        return START_STICKY
    }

    private fun play(song: Song) {
        if (song.music != currentMusic || mediaPlayer == null) {
            currentMusic = song.music
            mediaPlayer?.release() // 기존 플레이어 해제
            mediaPlayer = null

            val resId = resources.getIdentifier(song.music, "raw", packageName)
            Log.d("MUSIC_PLAYER_DEBUG", "Music file name: ${song.music}, Resource ID: $resId")

            if (resId != 0) {
                mediaPlayer = MediaPlayer.create(this, resId) // ⭐ MediaPlayer 생성은 여기서 한 번만!
            } else {
                // 파일을 찾지 못했을 경우
                Log.e("MUSIC_PLAYER_ERROR", "Music file not found in raw folder: ${song.music}")
                getSharedPreferences("song", MODE_PRIVATE).edit()
                    .putBoolean("isPlaying", false)
                    .apply()
                return // 재생 중단
            }
        }


        mediaPlayer?.seekTo(song.second * 1000)  // 진행 시간 복원
        mediaPlayer?.start()
        startTimer()

        //main에서의 재생여부 체크
        getSharedPreferences("song", MODE_PRIVATE).edit()
            .putBoolean("isPlaying", true)
            .apply()

        //  반복 재생 처리
        mediaPlayer?.setOnCompletionListener {
            if (isRepeat) {
                mediaPlayer?.seekTo(0)
                mediaPlayer?.start()
            } else {
                stopSelf()
            }
        }
    }

    private val handler = Handler(Looper.getMainLooper())
    private val timerRunnable = object : Runnable {
        override fun run() {
            val currentPosition = mediaPlayer?.currentPosition ?: 0
            val intent = Intent("TIMER_TICK")
            intent.putExtra("currentPosition", currentPosition)
            sendBroadcast(intent)

            handler.postDelayed(this, 1000)

        }
    }

    private fun startTimer() {
        stopTimer()
        handler.post(timerRunnable)
    }

    private fun stopTimer() {
        handler.removeCallbacks(timerRunnable)
    }

    override fun onDestroy() {
        mediaPlayer?.release()
        mediaPlayer = null
        super.onDestroy()
    }
    //override fun onBind(intent: Intent?): IBinder? = null
    override fun onBind(intent: Intent): IBinder{
        return Binder()
    }
}