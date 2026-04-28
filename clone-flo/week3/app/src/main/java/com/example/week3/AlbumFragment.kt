package com.example.week3

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.week3.databinding.FragmentAlbumBinding
import com.google.android.material.tabs.TabLayoutMediator

class AlbumFragment : Fragment() {
    private lateinit var binding: FragmentAlbumBinding
    //구성들의 내용을 넣어두기위해 쓴다
    private val information = arrayListOf("수록곡", "상세정보", "영상")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAlbumBinding.inflate(inflater, container, false)

        //프리그먼트 화면 되돌리는거
        /*binding.albumBackIv.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.home_album_container, HomeFragment())
                .commitAllowingStateLoss()
        }
*/
        val title = arguments?.getString("title") ?: "제목 없음"
        val singer =arguments?.getString("singer") ?: "가수 없음"

        binding.albumMusicTitleTv.text = title
        binding.albumSingerNameTv.text = singer
        Toast.makeText(requireContext(), "제목 : $title, 가수 : $singer", Toast.LENGTH_SHORT).show()

        binding.albumBackIv.setOnClickListener {
            (activity as MainActivity).supportFragmentManager.popBackStack()
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
}