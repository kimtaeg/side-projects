package com.example.week9.ui.vpadapter

import UI.locker.LockerFragment
import UI.save.SaveAlbumFragment
import UI.save.SaveFragment
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.week9.ui.fragment.FileFragment

class LockerVPAdapter (fragment : LockerFragment) : FragmentStateAdapter(fragment){
    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> FileFragment()
            1 -> SaveFragment()
            2 -> FileFragment()
            else -> SaveAlbumFragment()
        }
    }
}