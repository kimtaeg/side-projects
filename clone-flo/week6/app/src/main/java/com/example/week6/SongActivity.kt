package com.example.week6

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.week6.databinding.ActivitySongBinding
import com.google.gson.Gson

class SongActivity : AppCompatActivity() {

    //소괄호 : 클래스를 다른 클래스로 상속을 진행할 때는 소괄호를 넣어줘야 한다.
    //전역 변수
    private lateinit var binding : ActivitySongBinding
    //데이터 클래스 연결 데이터클래스를 초기화해주는 함수
    private lateinit var song : Song
    private lateinit var timer : Timer
    private var gson: Gson = Gson()
    //음악을 쉽게 재생시켜주는 클래스
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySongBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 데이터를 받아오는
        initSong()
        setPlayer(song)
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
            binding.songMiniplayerIv.visibility = View.VISIBLE //보이기
            binding.songPauseIv.visibility = View.GONE //숨기기
            //음악 재생
            mediaPlayer?.start()
        } else {
            binding.songMiniplayerIv.visibility = View.GONE //숨기기
            binding.songPauseIv.visibility = View.VISIBLE //보이기
            //음악 정지
            if(mediaPlayer?.isPlaying == true){
                mediaPlayer?.pause()
            }
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

    private fun initSong(){
        val sharedPreferences = getSharedPreferences("song", MODE_PRIVATE)
        val songJason = sharedPreferences.getString("songData", null)
        song = if (songJason != null){
            gson.fromJson(songJason, Song::class.java)
        } else{
            Song(
                intent.getStringExtra("title")!!,
                intent.getStringExtra("singer")!!,
                intent.getIntExtra("second", 0),
                intent.getIntExtra("playTime", 0),
                intent.getBooleanExtra("isPlaying", false),
                intent.getStringExtra("music")!!
            )
        }
        startTimer()
    }
    private fun setPlayer(song:Song){
        binding.songMusicTitleTv.text = intent.getStringExtra("title")!!
        binding.songSingerNameTv.text = intent.getStringExtra("singer")!!
        binding.songStartTimeTv.text = String.format("%02d:%02d",song.second / 60, song.second % 60)
        binding.songEndTimeTv.text = String.format("%02d:%02d",song.playTime / 60, song.playTime % 60)
        binding.songProgressSb.progress = ((song.second.toFloat() / song.playTime) * 100).toInt()
        //Log.d("SongActivity", "Attempting to load music resource: '${song.music}'")
        //리소스파일에서 해당 string값을 찾아서 리소스를 받아서 내주는
        val music = resources.getIdentifier(song.music,"raw",this.packageName)
        //이 음악을 알려줄거라는
        //Log.d("SongActivity", "Resource ID found: $music")
        mediaPlayer = MediaPlayer.create(this, music)
        // 여기서 seekTo를 호출하여 재생 시작 위치를 설정
        // song.second는 초 단위이므로 밀리초로 변환
        mediaPlayer?.seekTo(song.second * 1000)
        setPlayerStatus(song.isPlaying)
    }

    private fun startTimer(){
        timer = Timer(song.playTime,song.isPlaying,song.second)
        timer.start()
    }
    //내부 클래스 내부의 변수에 접근 못함
    // initialSecond이라는 변수를 추가하여 Timer를 초기화
    inner class Timer(private val playTime: Int, var isPlaying: Boolean = true,initialSecond: Int = 0):Thread(){

        //원래는 0으로 초기화를 계속해주었는데 initalsecond로 초기화 해줌.
        private var second : Int = initialSecond
        private var mills : Float = (initialSecond*1000).toFloat()

        override fun run(){
            super.run()
            try{
                while(true){
                    //노래가 종료했는지 확인
                    if(second >= playTime){
                        break
                    }
                    sleep(50)

                    if(isPlaying) {
                        mills += 50
                        //1초 경과 확인
                        if (mills % 1000 == 0f) {
                            second++
                        }
                    }

                    //UI 업데이트를 isPlaying 밖으로 이동하여 즉시 동기화
                    runOnUiThread {
                        binding.songProgressSb.progress = ((mills / playTime) * 100).toInt()
                        // 시간 텍스트 변경 변수
                        val displaySecond = (mills / 1000).toInt()
                        binding.songStartTimeTv.text =
                            String.format("%02d:%02d", displaySecond / 60, displaySecond % 60)
                    }
                }
            }catch(e: InterruptedException){
                Log.d("Song","스레드가 죽었습니다. ${e.message}")
            }
        }
    }
    //사용자가 포커스를 잃었을때 음악 중지
    override fun onPause(){
        super.onPause()
        setPlayerStatus(false)
        //몇초까지 재생했는지 반영. 1000으로 나누어준이유는 mills랑 second단위를 맞추기위해
        song.second = ((binding.songProgressSb.progress * song.playTime) / 100)/1000
        //mode private으로 한다, 에디터를 사용해야한다
        val sharedPreferences = getSharedPreferences("song", MODE_PRIVATE)
        val editor = sharedPreferences.edit() //에디터
        //하나하나 더해줘야한다. 그래서 json포멧으로 한번에 보내줄것이다.
        /*editor.putString("title", song.title)*/
        val songJson = gson.toJson(song)
        editor.putString("songData", songJson)
        editor.apply()

    }

    override fun onDestroy() {
        super.onDestroy()
        timer.interrupt()
        // 불필요한 리소스 낭비를 방지하기 위해, 미디어플레이어가 갖고 있던 리소스 해제
        mediaPlayer?.release()
        mediaPlayer = null//미디어 플레이어 해제
    }

}