package com.example.week9.ui.album

import UI.home.HomeFragment
import com.example.week9.ui.main.MainActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.week9.data.Album
import com.example.week9.data.Like
import com.example.week9.R
import com.example.week9.data.SongDatabase
import com.example.week9.databinding.FragmentAlbumBinding
import com.example.week9.ui.vpadapter.AlbumVPAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson

class AlbumFragment : Fragment() {
    private lateinit var binding: FragmentAlbumBinding
    private var gson: Gson = Gson()
    //구성들의 내용을 넣어두기위해 쓴다
    private val information = arrayListOf("수록곡", "상세정보", "영상")

    private var isLiked : Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAlbumBinding.inflate(inflater, container, false)


        val albumJson = arguments?.getString("album")
        val album = gson.fromJson(albumJson, Album::class.java)
        //isliked 초기 설정 좋아요를 눌렀을때 db처리
        isLiked = isLikedAlbum(album.id)
        setOnClickListeners(album)
        setInit(album)
        //프리그먼트 화면 되돌리는 버튼이벤트
        binding.albumBackIv.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, HomeFragment())
                .commitAllowingStateLoss()
        }


        //뷰와 연결
        val albumAdapter = AlbumVPAdapter(this)
        binding.albumContentVp.adapter = albumAdapter

        //탭레이아웃을 뷰페이즈2와 얀결하는 중재자, 중재자는 탭이 선택될때 뷰페이지2의 위치를 선택된 탭과 동기화 인자값은 먼저 연결할 탭 레이아웃쓰고 그다음 뷰페이저
        TabLayoutMediator(binding.albumContentTb, binding.albumContentVp){
            tab, position ->
            tab.text = information[position]
        }.attach()

        return binding.root
    }

    //뷰초기화
    private fun setInit(album: Album){
        binding.albumAlbumIv.setImageResource(album.coverImg!!)
        binding.albumMusicTitleTv.text = album.title.toString()
        binding.albumSingerNameTv.text = album.singer.toString()

        if(isLiked){
            binding.albumLikeIv.setImageResource(R.drawable.ic_my_like_on)
        }else{
            binding.albumLikeIv.setImageResource(R.drawable.ic_my_like_off)
        }
    }

    private fun getJwt():Int{
        val spf = activity?.getSharedPreferences("auth", AppCompatActivity.MODE_PRIVATE)
        return spf!!.getInt("jwt",0)
    }

    private fun likeAlbum(userId:Int,albumId:Int){
        val songDB = SongDatabase.Companion.getInstance(requireContext())!!
        //앨범을 좋아요눌렀을때 like테이블에 정보를 추가하기 위해
        val like = Like(userId, albumId)

        songDB.albumDao().likeAlbum(like)

    }
    //홈화면에서 좋아요를 눌렀는지 안눌렀는지 확인하기 위해
    private fun isLikedAlbum(albumId: Int):Boolean {
        val songDB = SongDatabase.Companion.getInstance(requireContext())!!
        val userId = getJwt()
        // 어떤 유저가 어떤 앨범을 좋아요 눌렀는지 확인하는 함수
        val likeId : Int? = songDB.albumDao().isLikedAlbum(userId, albumId)

        return likeId != null
    }

    private fun disLikedAlbum(albumId: Int) {
        val songDB = SongDatabase.Companion.getInstance(requireContext())!!
        val userId = getJwt()
        songDB.albumDao().disLikedAlbum(userId, albumId)
    }

    private fun setOnClickListeners(album: Album) {
        val userId = getJwt()
        //좋아요를 눌렀을때
        binding.albumLikeIv.setOnClickListener {
            if(isLiked) {//좋아요인 상태를 다시 안좋아요로 하는
                binding.albumLikeIv.setImageResource(R.drawable.ic_my_like_off)
                disLikedAlbum(album.id)
            }else{//좋아요를 다시 누르는
                binding.albumLikeIv.setImageResource(R.drawable.ic_my_like_on)
                likeAlbum(userId,album.id)
            }
        }
    }
}
