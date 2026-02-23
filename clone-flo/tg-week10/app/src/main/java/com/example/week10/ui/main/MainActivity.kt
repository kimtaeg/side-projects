package com.example.week10.ui.main

import UI.home.HomeFragment
import UI.locker.LockerFragment
import com.example.week10.ui.song.SongActivity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.week10.data.Album
import com.example.week10.ui.look.LookFragment
import com.example.week10.service.MusicPlayer
import com.example.week10.R
import com.example.week10.ui.fragment.SearchFragment
import com.example.week10.data.Song
import com.example.week10.data.SongDatabase
import com.example.week10.databinding.ActivityMainBinding
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.IntentFilter
import android.content.ServiceConnection
import android.os.IBinder
import android.view.Gravity
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private var musicPlayerService: MusicPlayer? = null
    private var isServiceBound = false
    private var isReceiverRegistered = false
    private var nowPos = 0
    private val songs = arrayListOf<Song>()

    var song: Song = Song()

    // ViewModel 초기화 (MainViewModel 파일이 미리 생성되어 있어야 합니다)
    private val viewModel: MainViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val database = SongDatabase.getInstance(applicationContext)!!
                return MainViewModel(database.songDao()) as T
            }
        }
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MusicPlayer.MusicPlayerBinder
            musicPlayerService = binder.getService()
            isServiceBound = true
            updateServiceStatus()
        }
        override fun onServiceDisconnected(name: ComponentName?) {
            isServiceBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.currentSong.observe(this) { updatedSong ->
            this.song = updatedSong
            setMiniPlayer(updatedSong)
        }

        initBottomNavigation()
        initClickListener()
        loadSongs()
    }

    private fun initClickListener() {
        // 미니플레이어 클릭 시 재생 화면 이동
        binding.mainPlayerCl.setOnClickListener {
            val editor = getSharedPreferences("song", MODE_PRIVATE).edit()
            editor.putInt("songId", song.id)
            editor.apply()
            startActivity(Intent(this, SongActivity::class.java))
        }

        // 재생 버튼
        binding.mainMiniplayerBtn.setOnClickListener {
            if (isServiceBound) {
                musicPlayerService?.start()
                sendServiceCommand("play", song.music, song.second)
                viewModel.updatePlayingStatus(true) // 즉시 true로 변경
            }
        }

        // 일시정지 버튼
        binding.mainPauseBtn.setOnClickListener {
            if (isServiceBound) {
                musicPlayerService?.pause()
                sendServiceCommand("pause")
                viewModel.updatePlayingStatus(false) // 즉시 false로 변경
            }
        }

        // 다음 곡
        binding.mainMiniplayerNext.setOnClickListener {
            moveSong(1)
        }

        // 이전 곡
        binding.mainMiniplayerPrevious.setOnClickListener {
            moveSong(-1)
        }
    }

    private fun moveSong(direct: Int) {
        if (songs.isEmpty()) return

        val nextPos = nowPos + direct

        if (nextPos < 0) {
            showCustomToast("첫 번째 곡입니다.")
            return
        }

        if (nextPos >= songs.size) {
            showCustomToast("마지막 곡입니다.")
            return
        }

        nowPos = nextPos
        val nextSong = songs[nowPos]

        val wasPlaying = binding.mainPauseBtn.visibility == View.VISIBLE

        // 3. ViewModel을 통해 곡 정보 로드
        viewModel.loadSong(nextSong.id)


        if (isServiceBound) {
            if (wasPlaying) {
                sendServiceCommand("play", nextSong.music, 0)
                viewModel.updatePlayingStatus(true)
            } else {
                sendServiceCommand("stop")
                viewModel.updatePlayingStatus(false)
            }
        }
    }

    private fun loadSongs() {
        val songDB = SongDatabase.getInstance(this)!!
        Thread {
            val allSongs = songDB.songDao().getSongs()
            songs.clear()
            songs.addAll(allSongs)

            val spf = getSharedPreferences("song", MODE_PRIVATE)
            val songId = spf.getInt("songId", 0)
            nowPos = songs.indexOfFirst { it.id == songId }.takeIf { it != -1 } ?: 0
        }.start()
    }
    private fun saveSongToSpf() {
        val spf = getSharedPreferences("song", MODE_PRIVATE)
        val editor = spf.edit()
        editor.putInt("songId", song.id)
        editor.apply()
    }

    fun setMiniPlayer(song: Song) {
        binding.mainMiniplayerTitleTv.text = song.title
        binding.mainMiniplayerSingerTv.text = song.singer

        binding.mainProgressSb.max = song.playTime

        binding.mainProgressSb.progress = song.second

        if (song.isPlaying) {
            binding.mainMiniplayerBtn.visibility = View.GONE
            binding.mainPauseBtn.visibility = View.VISIBLE
        } else {
            binding.mainMiniplayerBtn.visibility = View.VISIBLE
            binding.mainPauseBtn.visibility = View.GONE
        }

    }

    private val musicPlayerReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == "TIMER_TICK") {
                val currentPosition = intent.getIntExtra("currentPosition", 0)
                viewModel.updateSongSecond(currentPosition / 1000)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val intent = Intent(this, MusicPlayer::class.java)
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)

        registerMusicReceiver()

        val spf = getSharedPreferences("song", MODE_PRIVATE)
        val songId = spf.getInt("songId", 0)
        viewModel.loadSong(songId) // 데이터 로직은 ViewModel이 담당
    }

    private fun showCustomToast(message: String) {
        val inflater = layoutInflater
        val layout = inflater.inflate(
            R.layout.custom_toast,
            findViewById(R.id.custom_toast_container)
        )

        // 텍스트 설정
        val textView = layout.findViewById<TextView>(R.id.toast_message_tv)
        textView.text = message


        // 토스트 객체 생성 및 표시
        val toast = Toast(applicationContext)
        toast.duration = Toast.LENGTH_SHORT
        // 토스트의 위치를 아래 중앙으로 설정
        toast.setGravity(Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, 0, 100)
        toast.view = layout
        toast.show()
    }


    override fun onStop() {
        super.onStop()
        if (isServiceBound) {
            unbindService(serviceConnection)
            isServiceBound = false
        }

        unregisterMusicReceiver()
    }
    //안전한 등록을 위한 보조 함수
    private fun registerMusicReceiver() {
        if (!isReceiverRegistered) {
            val filter = IntentFilter("TIMER_TICK")
            ContextCompat.registerReceiver(this, musicPlayerReceiver, filter, ContextCompat.RECEIVER_EXPORTED)
            isReceiverRegistered = true
        }
    }

    // 안전한 해제를 위한 보조 함수
    private fun unregisterMusicReceiver() {
        if (isReceiverRegistered) {
            try {
                unregisterReceiver(musicPlayerReceiver)
            } catch (e: IllegalArgumentException) {
                Log.d("MainActivity", "Receiver already unregistered")
            }
            isReceiverRegistered = false
        }
    }

    private fun updateServiceStatus() {
        musicPlayerService?.let {
            viewModel.updatePlayingStatus(it.isPlaying())
        }
    }

    fun sendServiceCommand(command: String, music: String? = null, second: Int = 0) {
        val intent = Intent(this, MusicPlayer::class.java)
        intent.putExtra("command", command)
        music?.let { intent.putExtra("music", it) }
        intent.putExtra("second", second)
        startService(intent)
    }
    // 네비게이션
    private fun initBottomNavigation() {

        supportFragmentManager.beginTransaction()
            .replace(R.id.main_frm, HomeFragment())
            .commitAllowingStateLoss()
//BottomNavigationView를 눌렀을 때 Fragment 변경하기
        binding.mainBnv.setOnItemSelectedListener { item ->
            when (item.itemId) {

                R.id.homeFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, HomeFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }

                R.id.lookFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, LookFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }

                R.id.searchFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, SearchFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }

                R.id.lockerFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, LockerFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }
            }
            false
        }
    }
}
