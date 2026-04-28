package com.example.week5

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.week5.databinding.FragmentBannerBinding

//뷰페이저에 여러 이미지를 넣기위해 인자값을 넣어라
class BannerFragment(val imgRes : Int) : Fragment() {

    lateinit var binding : FragmentBannerBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //binding 초기화
        binding = FragmentBannerBinding.inflate(inflater,container,false)

        binding.bannerImageIv.setImageResource(imgRes)
        return binding.root

    }
}