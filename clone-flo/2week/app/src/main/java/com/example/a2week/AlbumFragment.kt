package com.example.a2week

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.a2week.databinding.FragmentAlbumBinding

class AlbumFragment : Fragment() {
    private lateinit var binding: FragmentAlbumBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAlbumBinding.inflate(inflater, container, false)

        binding.albumBackIv.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.home_album_container, HomeFragment())
                .commitAllowingStateLoss()
        }

        val title = arguments?.getString("title") ?: "제목 없음"
        val singer =arguments?.getString("singer") ?: "가수 없음"

        binding.albumMusicTitleTv.text = title
        binding.albumSingerNameTv.text = singer
        Toast.makeText(requireContext(), "$title", Toast.LENGTH_SHORT).show()

        binding.albumBackIv.setOnClickListener {
            (activity as MainActivity).supportFragmentManager.popBackStack()
        }

        return binding.root
    }
}