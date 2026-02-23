package com.example.week7

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class LockerVPAdapter (fragment : LockerFragment) : FragmentStateAdapter(fragment){
    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> FileFragment()
            1 -> SaveFragment()
            2 -> FileFragment()
            else -> FileFragment()
        }
    }
}