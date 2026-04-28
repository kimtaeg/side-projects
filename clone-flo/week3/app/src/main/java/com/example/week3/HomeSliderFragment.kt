package com.example.week3

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.week3.databinding.FragmentHomeSilderBinding

//뷰페이저에 여러 이미지를 넣기위해 인자값을 넣어라
class HomeSliderFragment(val imgRes : Int) : Fragment() {

    lateinit var binding : FragmentHomeSilderBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //binding 초기화
        binding = FragmentHomeSilderBinding.inflate(inflater,container,false)

        binding.homeSilderIv.setImageResource(imgRes)

        return binding.root

    }
    /*override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.bannerImage.setImageResource(imgRes)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }*/
}