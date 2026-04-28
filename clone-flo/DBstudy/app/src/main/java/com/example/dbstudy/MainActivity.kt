package com.example.dbstudy

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.dbstudy.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    var list = ArrayList<Profile>()
    lateinit var customAdapter: CustomAdapter
    lateinit var db: ProfileDatabase

    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //getinstance를 이용해 db불러옴
        db = ProfileDatabase.getInstance(this)!!
        Thread{
            val savedContacts = db.profileDao().getAll()
            if(savedContacts.isNotEmpty()){
                list.addAll(savedContacts)
                //메인스레드에 맡기는게 아니라 스레드에서 비동기처리해야함

            }
        }.start()
        //그리고 db에 저장된 데이터를 불러온다



        //버튼을 누르면 데이터를 추가해주는 로직
        binding.button.setOnClickListener{
            Thread{
                list.add(Profile("티지","25","0000"))
                val list = db.profileDao().getAll()
/*
                Log.d("inserted primary key",list[list.size-1].id.toString())
*/
            }.start()
            customAdapter.notifyDataSetChanged()
        }
        customAdapter = CustomAdapter(list,this)

        binding.mainProfileLv.adapter = customAdapter
    }

}