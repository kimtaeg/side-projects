package com.example.week8

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.week8.databinding.FragmentLockerBinding
import com.google.android.material.tabs.TabLayoutMediator

class LockerFragment : Fragment() {


    private lateinit var binding: FragmentLockerBinding

    private val information = arrayListOf("내 리스트", "좋아요", "저장한 곡", "저장 앨범")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLockerBinding.inflate(inflater, container, false)

        val lockerAdapter = LockerVPAdapter(this)
        binding.lockerContentVp.adapter = lockerAdapter

        TabLayoutMediator(binding.lockerContentTb, binding.lockerContentVp){
                tab, position ->
            tab.text = information[position]
        }.attach()

        //로그인 프리그먼트로 이동
        binding.logInTv.setOnClickListener {
            startActivity(Intent(activity, LoginActivity::class.java))
        }

        return binding.root
    }

    override fun onStart(){
        super.onStart()
        initViews()
    }


    private fun getJwt():Int{
        val spf = activity?.getSharedPreferences("auth", AppCompatActivity.MODE_PRIVATE)
        return spf!!.getInt("jwt",0)
    }
    //로그인 로그아웃
    private fun initViews(){
        val jwt : Int = getJwt()

        if(jwt == 0){
            binding.logInTv.text = "로그인"
            binding.logInTv.setOnClickListener {
                startActivity(Intent(activity, LoginActivity::class.java))
            }
        }else{
            binding.logInTv.text = "로그아웃"
            binding.logInTv.setOnClickListener {
                Toast.makeText(activity, "로그아웃하셨습니다.", Toast.LENGTH_SHORT).show()

                //로그아웃 진행
                logout()
                startActivity(Intent(activity, MainActivity::class.java))
            }
        }
    }
    //로그아웃 진행하는 함수
    private fun logout(){
        val spf = activity?.getSharedPreferences("auth", AppCompatActivity.MODE_PRIVATE)
        val editor = spf!!.edit()

        editor.remove("jwt")
        editor.apply()
    }

}
