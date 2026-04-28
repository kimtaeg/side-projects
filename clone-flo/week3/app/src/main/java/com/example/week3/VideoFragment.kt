package com.example.week3

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.week3.databinding.FragmentDetailBinding
import com.example.week3.databinding.FragmentVideoBinding

class VideoFragment : Fragment() {
    lateinit var binding : FragmentVideoBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentVideoBinding.inflate(inflater, container,false)
        return binding.root
    }


}