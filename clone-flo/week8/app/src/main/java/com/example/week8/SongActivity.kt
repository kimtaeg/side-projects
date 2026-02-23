package com.example.week8

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Toast
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.week8.databinding.ActivitySongBinding
import com.google.gson.Gson

class SongActivity : AppCompatActivity() {

    //소괄호 : 클래스를 다른 클래스로 상속을 진행할 때는 소괄호를 넣어줘야 한다.
    //전역 변수
    private lateinit var binding : ActivitySongBinding
    //데이터 클래스 연결 데이터클래스를 초기화해주는 함수
    private lateinit var timer : Timer
    private var gson: Gson = Gson()
    //음악을 쉽게 재생시켜주는 클래스
    private var mediaPlayer: MediaPlayer? = null
    //노래 관리해주는 전역변수
    val songs = arrayListOf<Song>()
    lateinit var songDB : SongDatabase
    var nowPos = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySongBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initPlayList()
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

        //종아요
        binding.songLikeIv.setOnClickListener {
            setLike(songs[nowPos].isLike)
            showCustomLikeToast(songs[nowPos].isLike)
        }



    }
    //일시정지 재생버튼 변환
    fun setPlayerStatus(isPlaying: Boolean) {
        songs[nowPos].isPlaying = isPlaying
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
        timer.interrupt()
        startTimer()
        mediaPlayer?.release()
        mediaPlayer = null

        setPlayer(songs[nowPos])
    }
    private fun initSong(){
        // sharedPreferences에서 아이디값을 받아 온 다음에 이 아이디를 통해서 songs와 비교해서 인덱스값을 구하는 함수
        val spf = getSharedPreferences("song", MODE_PRIVATE)
        val songId = spf.getInt("songId", 0)
        // 저장된 재생 초 불러오기
        val playSecond = spf.getInt("playsecond", 0)


        nowPos = getPlayingSongPosition(songId)

        //song에 저장된 재생 초 설정
        songs[nowPos].second = playSecond

        Log.d("now Song ID",songs[nowPos].id.toString())
        startTimer()
        setPlayer(songs[nowPos])
    }

    private fun getPlayingSongPosition(songId: Int): Int{
        for(i in 0 until songs.size){
            if (songs[i].id == songId){
                return i
            }
        }
        return 0
    }
    private fun setPlayer(song:Song){
        binding.songMusicTitleTv.text = song.title
        binding.songSingerNameTv.text = song.singer
        binding.songStartTimeTv.text = String.format("%02d:%02d",song.second / 60, song.second % 60)
        binding.songEndTimeTv.text = String.format("%02d:%02d",song.playTime / 60, song.playTime % 60)
        binding.songAlbumIv.setImageResource(song.coverImg!!)
        binding.songProgressSb.progress = ((song.second.toFloat() / song.playTime) * 100).toInt()

        Log.d("IMG_LOG", "SongActivity Album Cover ID: ${song.coverImg}")

        val music = resources.getIdentifier(song.music,"raw",this.packageName)
        mediaPlayer = MediaPlayer.create(this, music)
        mediaPlayer?.seekTo(song.second * 1000)

        setPlayerStatus(song.isPlaying)
        // 피드백 반영
        val initialRemainingTime = song.playTime - song.second
        binding.songEndTimeTv.text = String.format("-%02d:%02d", initialRemainingTime / 60, initialRemainingTime % 60)

        mediaPlayer?.setOnCompletionListener {
            //반복 활성화 상태확인
            if(binding.songRepeatColorIv.visibility == View.VISIBLE){
                //반복재생 상태일 때
                mediaPlayer?.seekTo(0)
                mediaPlayer?.start()
                //타이머 재시작
                startTimer(0) // 0초에서 다시 시작 타이머 재시작
            }
            else{
                // 반복재생 상태가 아닐때
                setPlayerStatus(false)
                // 타이머를 멈추고 재생 시간 초기화
                timer.interrupt()
                song.second = 0
                binding.songStartTimeTv.text = "00:00"
                binding.songProgressSb.progress = 0
            }
        }

        //좋아요
        if(song.isLike){
            binding.songLikeIv.setImageResource(R.drawable.ic_my_like_on)
        }else{
            binding.songLikeIv.setImageResource(R.drawable.ic_my_like_off)
        }
    }

    private fun startTimer(initialSecond: Int = songs[nowPos].second){
        /*if (::timer.isInitialized && timer.isAlive){
            timer.interrupt()
        }*/
        timer = Timer(songs[nowPos].playTime,songs[nowPos].isPlaying,songs[nowPos].second)
        timer.start()
    }

    inner class Timer(private val playTime: Int, var isPlaying: Boolean = true,initialSecond: Int = 0):Thread(){

        private var second : Int = initialSecond
        private var mills : Float = (initialSecond*1000).toFloat()

        override fun run(){
            super.run()
            try{
                while(true){
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
                    //ui 업데이트
                    runOnUiThread {
                        val currentSecond = (mills / 1000).toInt()
                        // 1. 남은 시간 계산 (초 단위)
                        val remainingTime = playTime - currentSecond
                        //현재 시간
                        binding.songProgressSb.progress = ((mills / playTime) * 100).toInt()
                        val displaySecond = (mills / 1000).toInt()
                        binding.songStartTimeTv.text =
                            String.format("%02d:%02d", displaySecond / 60, displaySecond % 60)
                        //남은 시간
                        binding.songEndTimeTv.text =
                            String.format("%02d:%02d", remainingTime / 60, remainingTime % 60)
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
        songs[nowPos].second = mediaPlayer?.currentPosition?.div(1000) ?: 0
        //songs[nowPos].second = ((binding.songProgressSb.progress * songs[nowPos].playTime) / 100)/1000
        val sharedPreferences = getSharedPreferences("song", MODE_PRIVATE)
        val editor = sharedPreferences.edit() //에디터

        editor.putInt("songId",songs[nowPos].id)
        editor.putBoolean("isPlaying", false)
        editor.putInt("playSecond", songs[nowPos].second)
        editor.apply()

    }

    override fun onDestroy() {
        super.onDestroy()
        timer.interrupt()
        mediaPlayer?.release()
        mediaPlayer = null//미디어 플레이어 해제
    }

    private fun initPlayList(){
        songDB = SongDatabase.getInstance(this)!!
        songs.addAll(songDB.songDao().getSongs())
    }
}