package com.example.week4

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.week4.databinding.FragmentSaveBinding
import com.google.gson.Gson

class SaveFragment : Fragment(){
    lateinit var binding : FragmentSaveBinding
    private var albumDatas = ArrayList<Album>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSaveBinding.inflate(inflater, container,false)

        //데이터 리스트 더미
        albumDatas.apply {
            add(Album("SPEED DEMON", "Justin Bieber",R.drawable.justin))
            add(Album("Open Hearts", "The weeknd", R.drawable.weekend2))
            add(Album("reincarnated", "Kendrickn Lamar", R.drawable.kendrick))
            add(Album("떨어지는 낙엽까지도", "헤이즈", R.drawable.hezie))
            add(Album("포커페이스", "C JAMM", R.drawable.cjam))
            add(Album("이때다", "기리보이", R.drawable.giriboy))
            add(Album("태지", "창모", R.drawable.changmo))
            add(Album("돌고래", "Zion.T",R.drawable.ziont))
            add(Album("bonnie & clyde", "DEAN", R.drawable.dean))
            add(Album("Better Now", "Post Malone", R.drawable.postmalone2))

        }

        val saveRVAdapter = SaveRVAdapter(albumDatas)
        binding.saveRv.adapter = saveRVAdapter
        binding.saveRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        saveRVAdapter.setMyItemClickListener(object: SaveRVAdapter.MyItemClickListener{

            override fun onItemClick(album: Album) {
                changeAlbumFragment(album)
            }

            //삭제하는거
            override fun onRemoveAlbum(position: Int){
                saveRVAdapter.removeItem(position)
            }
        })

        return binding.root
    }

    private fun changeAlbumFragment(album: Album) {
        (context as MainActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.main_frm, AlbumFragment().apply {
                arguments = Bundle().apply {
                    val gson = Gson()
                    val albumJson = gson.toJson(album)
                    putString("album", albumJson)
                }
            })
            .commitAllowingStateLoss()
    }
}