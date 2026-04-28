package com.example.week8

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class AlbumVPAdapter(fragment: AlbumFragment) : FragmentStateAdapter(fragment) {
    //뷰페이저 개수
    override fun getItemCount(): Int = 3
    //when은 스위치문법이라, 괄호안에 조건을 써주고 괄호안에 각각 조건에 따라 어떤 작업을 할건지
    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> SongFragment()
            1 -> DetailFragment()
            else -> VideoFragment()
        }
    }
}