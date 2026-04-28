package com.example.week6

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.week6.databinding.ActivityMainBinding
import com.google.gson.Gson

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // 클래스 멤버 변수를 사용
    var song: Song = Song()
    private var gson: Gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.mainPlayerCl.setOnClickListener {
            val intent = Intent(this, SongActivity::class.java)
        }

        initBottomNavigation()

        //초기화해준것

        //val song = Song(binding.mainMiniplayerTitleTv.text.toString(),binding.mainMiniplayerSingerTv.text.toString(), null,0,180,false)

        binding.mainPlayerCl.setOnClickListener {
            val intent = Intent(this, SongActivity::class.java)

            intent.putExtra("title", song.title)
            intent.putExtra("singer", song.singer)
            intent.putExtra("second", song.second)
            intent.putExtra("coverImg", song.coverImg)
            intent.putExtra("playTime", song.playTime)
            intent.putExtra("isPlaying", song.isPlaying)
            intent.putExtra("music", song.music)

            startActivity(intent)
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
        binding.mainProgressSb.progress = (song.second * 100) / song.playTime


    }


    //미니플레이어 로직
    //songactivity에서 데이터를 반영하기 위해서 onstart에서 해준다, onresume에서 안하는 이유는 onstart가 사용자에게 액티비티가 보여주기 전에 호출하는 함수고, ui와 관련된걸 초기화하는게 좋다.
    override fun onStart() {
        super.onStart()
        //"song"이라는 파일에서 songData 키로 저장된 JSON 형식의 노래 데이터songJson를 불러온다.
        val sharedPreferences = getSharedPreferences("song", MODE_PRIVATE)
        val songJson = sharedPreferences.getString("songData", null)

        song = if (songJson == null) {
            Song("SPEED DEMON", "Justin bieber", 0, 210, false, "music_lilac", R.drawable.justin)
        } else {
            gson.fromJson(songJson, Song::class.java)
        }
        setMiniPlayer(song)
    }

    override fun onResume() {
        super.onResume()
        val sharedPreferences = getSharedPreferences("song", MODE_PRIVATE)
        val songJson = sharedPreferences.getString("songData", null)

        if (songJson != null) {
            song = gson.fromJson(songJson, Song::class.java)
            setMiniPlayer(song)
        }
    }
}