package com.example.week8

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.week8.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //회원가입 프리그먼트 이동
        binding.loginSignupTv.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        binding.loginInBtn.setOnClickListener {
            login()
        }
    }

    //로그인 진행 함수
    private fun login(){
        if(binding.loginIdEt.text.toString().isEmpty() || binding.loginInputEt.text.toString().isEmpty()){
            Toast.makeText(this,"이메일을 입력해주세요.",Toast.LENGTH_SHORT).show()
            return
        }
        if(binding.loginPasswordEt.text.toString().isEmpty()){
            Toast.makeText(this,"비밀번호를 입력해주세요.",Toast.LENGTH_SHORT).show()
            return
        }

        val email : String = binding.loginIdEt.text.toString() + "@" + binding.loginInputEt.text.toString()
        val pwd : String = binding.loginPasswordEt.text.toString()

        //db에 아이디가 있는지 확인
        val songDB = SongDatabase.getInstance(this)!!
        //일치하는 아이디가 있는지 확인
        val user = songDB.userDao().getUser(email, pwd)

        user?.let{
            Log.d("LOGINACT/GETUSER", "userId : ${user.id},$user")
            saveJwt(user.id)
            Toast.makeText(this, "로그인 성공", Toast.LENGTH_SHORT).show()
            // 로그인 완료하면 메인화면으로 넘어감
            startMainActivity()
        }
        //로그인실패
        if(user==null) {
            Toast.makeText(this, "회원 정보가 존재하지 않습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveJwt(jwt:Int){
        val spf = getSharedPreferences("auth", MODE_PRIVATE)
        val editor = spf.edit()

        editor.putInt("jwt",jwt)
        editor.apply()
    }

    private fun startMainActivity(){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}