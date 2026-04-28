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
import com.google.gson.Gson
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.IntentFilter
import android.content.ServiceConnection
import android.os.IBinder
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var musicPlayerService: MusicPlayer? = null
    private var isServiceBound = false

    // 클래스 멤버 변수를 사용
    var song: Song = Song()

    //서비스 연결 관리 객체
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?){
            val binder = service as MusicPlayer.MusicPlayerBinder
            musicPlayerService = binder.getService()
            isServiceBound = true
            Log.d("SERVICE_CONN", "서비스 연결 성공")
            updateMiniPlayerUI()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isServiceBound = false
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        inputDummyAlbums()
        inputDummySongs()
        initBottomNavigation()

        //초기화해준것

        binding.mainPlayerCl.setOnClickListener {

            val editor = getSharedPreferences("song", MODE_PRIVATE).edit()
            editor.putInt("songId", song.id)
            editor.apply()

            val intent = Intent(this, SongActivity::class.java)
            startActivity(intent)
        }

        val songDB = SongDatabase.Companion.getInstance(this)!!

        //재생
        binding.mainMiniplayerBtn.setOnClickListener {
            song.isPlaying = true

            if(isServiceBound){
                if(musicPlayerService?.isPlaying() == true){
                    musicPlayerService?.pause()
                }else{
                    musicPlayerService?.start()
                }
                updateMiniPlayerUI()
            }

            val editor = getSharedPreferences("song", MODE_PRIVATE).edit()
            editor.putInt("songId", song.id)
            editor.putBoolean("isPlaying",true)
            editor.apply()

            saveAndSetMiniPlayer(song)
            sendServiceCommand("play", song.music, song.second)
        }
        //일시정지
        binding.mainPauseBtn.setOnClickListener {
            song.isPlaying = false

            val editor = getSharedPreferences("song", MODE_PRIVATE).edit()
            editor.putInt("songId", song.id)
            editor.putBoolean("isPlaying",false)
            editor.apply()

            saveAndSetMiniPlayer(song)
            sendServiceCommand("pause") // 일시정지 시 음악 파일 이름은 필요 없습니다.
        }

        //이전
        binding.mainMiniplayerPrevious.setOnClickListener {
            val prevSong = songDB.songDao().getSongs().filter{it.id<song.id}.maxByOrNull { it.id }
            if (prevSong != null) {
                song = prevSong
                song.isPlaying = true
                saveAndSetMiniPlayer(song)
                sendServiceCommand("play", song.music)
            }else{
                Toast.makeText(this, "첫번째 곡입니다.", Toast.LENGTH_SHORT).show()
            }
        }

        //다음
        binding.mainMiniplayerNext.setOnClickListener {
            val nextSong = songDB.songDao().getSongs().filter{it.id>song.id}.minByOrNull { it.id }
            if(nextSong != null){
                song = nextSong
                song.isPlaying = true
                saveAndSetMiniPlayer(song)
                sendServiceCommand("play", song.music)
            }else{
                Toast.makeText(this, "마지막 곡입니다.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.mainMiniplayerList.setOnClickListener {
            val intent = Intent(this, SongActivity::class.java)
            startActivity(intent)
        }

    }
    fun sendServiceCommand(command: String, music: String? = null, second: Int = 0) {
        val intent = Intent(this, MusicPlayer::class.java)
        intent.putExtra("command", command)
        music?.let{intent.putExtra("music", it)}
        intent.putExtra("second", second)
        startService(intent)
    }

    fun saveAndSetMiniPlayer(song: Song) {
        val editor = getSharedPreferences("song", MODE_PRIVATE).edit()
        editor.putInt("songId",song.id)
        editor.putBoolean("isPlaying",song.isPlaying)
        editor.apply()

        setMiniPlayer(song)

    }

    // 실시간 재생 위치를 받아 Seekbar를 업데이트하는 리시버
    private val musicPlayerReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                "TIMER_TICK" -> {
                    val currentPositionMillis = intent.getIntExtra("currentPosition", 0)
                    val currentSecond = currentPositionMillis / 1000

                    // 1. song 객체의 second를 서비스로부터 받은 최신 값으로 업데이트
                    song.second = currentSecond

                    // 2. 미니 플레이어 UI 업데이트 (Seekbar, 재생/일시정지 버튼)
                    setMiniPlayer(song)
                }
            }
        }
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


    fun setMiniPlayer(song: Song) {
        binding.mainMiniplayerTitleTv.text = song.title
        binding.mainMiniplayerSingerTv.text = song.singer

        if(song == null)return
        binding.mainMiniplayerBtn.visibility = if (song.isPlaying) View.GONE else View.VISIBLE
        binding.mainPauseBtn.visibility = if (song.isPlaying) View.VISIBLE else View.GONE


        // 피드백 수정
        if (song.playTime > 0) {
            // playTime이 0보다 클 때만 나눗셈 연산을 수행
            binding.mainProgressSb.progress = ((song.second.toFloat() / song.playTime) * 100).toInt()
        } else {
            binding.mainProgressSb.progress = 0
        }
    }




    //미니플레이어 로직
    //songactivity에서 데이터를 반영하기 위해서 onstart에서 해준다, onresume에서 안하는 이유는 onstart가 사용자에게 액티비티가 보여주기 전에 호출하는 함수고, ui와 관련된걸 초기화하는게 좋다.
    override fun onStart() {
        super.onStart()

        val intent = Intent(this, MusicPlayer::class.java)
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)

        val spf = getSharedPreferences("song", MODE_PRIVATE)
        val songId = spf.getInt("songId", 0)
        val playSecond = spf.getInt("playSecond", 0)
        val isPlaying = spf.getBoolean("isPlaying", false)
        //db에서 해당 id에 해당하는 song을 가져오는걸 dao에 쿼리문을 만듬
        val songDB = SongDatabase.Companion.getInstance(this)!!


        song = if (songId == 0){
            songDB.songDao().getSongs(1)
        }else{
            songDB.songDao().getSongs(songId)
        }
        Log.d("song ID", song.id.toString())

        song.second = playSecond
        song.isPlaying = isPlaying

        setMiniPlayer(song)
    }

    override fun onStop(){
        super.onStop()
        if(isServiceBound){
            unbindService(serviceConnection)
            isServiceBound = false
        }
    }

    private fun updateMiniPlayerUI(){
        if(!isServiceBound)return
        if(musicPlayerService?.isPlaying() == true){
            binding.mainMiniplayerBtn.setImageResource(R.drawable.btn_miniplay_pause)
        } else {
            // 현재 멈춰 있으면 -> 재생 아이콘(▶️) 표시
            binding.mainMiniplayerBtn.setImageResource(R.drawable.btn_miniplayer_play)
        }
    }
    override fun onResume() {
        super.onResume()
        val spf = getSharedPreferences("song", MODE_PRIVATE)
        val songId = spf.getInt("songId", 0)
//        val playSecond = spf.getInt("playSecond", 0)

        val songDB = SongDatabase.Companion.getInstance(this)!!
        val defaultId = songDB.songDao().getSongs().firstOrNull()?.id ?: return

        val filter = IntentFilter("TIMER_TICK")
        ContextCompat.registerReceiver(
            this, // mainActivity
            musicPlayerReceiver,
            filter,
            ContextCompat.RECEIVER_EXPORTED
        )
        song = if (songId == 0) {
            songDB.songDao().getSongs(defaultId)
        } else {
            songDB.songDao().getSongs(songId)
        }

        /*song.second = playSecond
        song.isPlaying = isPlaying  */// 미니플레이어 상태 정확히 반영
        setMiniPlayer(song)

    }
    override fun onPause() {
        super.onPause()
        //Broadcast Receiver 해제
        unregisterReceiver(musicPlayerReceiver)
    }

    private fun inputDummyAlbums() {
        val songDB = SongDatabase.Companion.getInstance(this)!!
        val albums = songDB.albumDao().getAlbums()

        if (albums.isNotEmpty()) return
        songDB.albumDao().insert(Album(1, "Lilac", "아이유 (IU)", R.drawable.img_album_exp2))
        songDB.albumDao().insert(Album(2, "Flu", "아이유 (IU)", R.drawable.img_album_exp2))
        songDB.albumDao().insert(Album(3, "Butter", "방탄소년단 (BTS)", R.drawable.img_album_exp))
        songDB.albumDao().insert(Album(4, "Boy with Luv", "방탄소년단 (BTS)", R.drawable.justin))
        songDB.albumDao().insert(Album(5, "Next Level", "에스파", R.drawable.hezie))
    }
    private fun inputDummySongs(){
        val songDB = SongDatabase.Companion.getInstance(this)!!
        val songs = songDB.songDao().getSongs()

        if (songs.any { it.title == "Lilac" && it.singer == "아이유 (IU)" }) return


        // 비어있다면 더미데이터넣어햐함
        if(songs.isNotEmpty()) return


        songDB.songDao().insert(
            Song(
                "Lilac",
                "아이유 (IU)",
                0,
                200,
                false,
                "music_lilac",
                R.drawable.img_album_exp2,
                false,
                1
            )
        )

        songDB.songDao().insert(
            Song(
                "Flu",
                "아이유 (IU)",
                0,
                200,
                false,
                "music_flu",
                R.drawable.img_album_exp2,
                false,
                2
            )
        )

        songDB.songDao().insert(
            Song(
                "Butter",
                "방탄소년단 (BTS)",
                0,
                190,
                false,
                "music_butter",
                R.drawable.img_album_exp,
                false,
                3
            )
        )

        songDB.songDao().insert(
            Song(
                "Boy with Luv",
                "방탄소년단",
                0,
                230,
                false,
                "music_boy",
                R.drawable.justin,
                false,
                4
            )
        )

        songDB.songDao().insert(
            Song(
                "Next Level",
                "에스파 (AESPA)",
                0,
                210,
                false,
                "music_next",
                R.drawable.hezie,
                false,
                5
            )
        )

        //데이터들이 잘 들어갔는데 확인
        val _songs = songDB.songDao().getSongs()
        Log.d("DB데이터 확인", _songs.toString())
    }
}
