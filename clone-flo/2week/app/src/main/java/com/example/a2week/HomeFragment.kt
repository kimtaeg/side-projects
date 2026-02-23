package com.example.a2week

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.a2week.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)


        binding.homeTodayMusicAlbum.setOnClickListener {
            val bundle = Bundle().apply {
                putString("title", binding.homeTodayMusicTitle02Tv.text.toString())
                putString("singer", binding.homeTodayMusicSingerTv.text.toString())
            }
            val albumfragment = AlbumFragment()
            albumfragment.arguments = bundle

            (activity as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, albumfragment)
                .addToBackStack(null)
                .commit()
        }
        return binding.root
    }
}