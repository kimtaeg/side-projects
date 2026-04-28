package com.example.week10.service

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import com.example.week10.data.Song
import com.example.week10.data.SongDatabase

class MusicPlayer : Service() {

    private val binder = MusicPlayerBinder()
    private var mediaPlayer: MediaPlayer? = null
    private var currentMusic: String? = null
    private var currentIndex = 0
    private lateinit var playlist: List<Song>
    private var isRepeat = false

    var song: Song? = null

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

    fun getCurrentMusicName(): String? {
        return currentMusic
    }

    private fun play(song: Song) {
        if (mediaPlayer?.isPlaying == true && currentMusic == song.music) {
            Log.d("MUSIC_PLAYER", "이미 동일한 곡이 재생 중입니다. 무시합니다.")
            return
        }

        if (song.music != currentMusic || mediaPlayer == null) {
            currentMusic = song.music
            mediaPlayer?.release()
            mediaPlayer = null

            val resId = resources.getIdentifier(song.music, "raw", packageName)
            if (resId != 0) {
                mediaPlayer = MediaPlayer.create(this, resId)
            } else {
                Log.e("MUSIC_PLAYER", "파일을 찾을 수 없음: ${song.music}")
                return
            }

            // 새 곡인 경우에만 전달받은 second 지점으로 이동
            mediaPlayer?.seekTo(song.second * 1000)
        }

        // 3. 재생 시작
        mediaPlayer?.start()
        startTimer()

        // 상태 저장
        getSharedPreferences("song", MODE_PRIVATE).edit()
            .putBoolean("isPlaying", true)
            .apply()

        mediaPlayer?.setOnCompletionListener {
            if (isRepeat) {
                mediaPlayer?.seekTo(0)
                mediaPlayer?.start()
            } else {
                playNextSong()
            }
        }
    }

    private fun playNextSong() {
        if (playlist.isEmpty()) return

        currentIndex = (currentIndex + 1) % playlist.size
        val nextSong = playlist[currentIndex]

        getSharedPreferences("song", MODE_PRIVATE).edit()
            .putInt("songId", nextSong.id)
            .apply()

        // 다음 곡 실행
        play(nextSong)

        val intent = Intent("SONG_CHANGED")
        intent.putExtra("songId", nextSong.id)
        sendBroadcast(intent)
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

    // 서비스의 기능을 외부에서 호출할 수 있도록 제공하는 통로
    inner class MusicPlayerBinder : Binder(){
        fun getService(): MusicPlayer {
            return this@MusicPlayer
        }
    }

    override fun onBind(intent: Intent): IBinder{
        return binder
    }
    // 액티비티에서 곡 정보를 넘겨줄 때 사용할 메서드
    fun setPlayer(song: Song) {
        this.song = song
        play(song) // 서비스 내부의 play 호출
    }

    fun getTotalDuration(): Int {
        return mediaPlayer?.duration ?: 0
    }
    fun getDuration(): Int {
        return mediaPlayer?.duration ?: 0
    }

    fun start(){
        mediaPlayer?.start()
    }
    fun pause(){
        mediaPlayer?.pause()
    }
    fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying ?: false
    }
    fun getCurrentPosition(): Int{
        return mediaPlayer?.currentPosition ?: 0
    }
}