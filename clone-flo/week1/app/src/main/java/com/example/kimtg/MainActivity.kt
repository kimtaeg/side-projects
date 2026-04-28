package com.example.kimtg

import android.graphics.Color
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)//xml을 연결해 화면에 나오게함
        // xml에 있는 imageview 연결
        val angryImage = findViewById<ImageView>(R.id.angry)
        val nerveImage = findViewById<ImageView>(R.id.nerve)
        val sosoImage = findViewById<ImageView>(R.id.soso)
        val coolImage = findViewById<ImageView>(R.id.cool)
        val happyImage = findViewById<ImageView>(R.id.happy)

        // 커스텀 Toast 함수
        fun CustomToast(message: String, bgColor: Int) {
            val textView = TextView(this)
            textView.text = message //텍스트 설정
            textView.setTextColor(Color.WHITE)
            textView.textSize = 16f
            textView.setBackgroundColor(bgColor)  // 배경색만 적용

            val toast = Toast(this)
            toast.duration = Toast.LENGTH_SHORT //지속시간
            toast.view = textView
            toast.show()
        }

        // 클릭 이벤트
        angryImage.setOnClickListener {
            CustomToast("Angry 이미지를 클릭했습니다!", Color.RED)
        }

        nerveImage.setOnClickListener {
            CustomToast("Nerve 이미지를 클릭했습니다!", Color.GREEN)
        }

        sosoImage.setOnClickListener {
            CustomToast("Soso 이미지를 클릭했습니다!", Color.MAGENTA)
        }

        coolImage.setOnClickListener {
            CustomToast("Cool 이미지를 클릭했습니다!", Color.BLUE)
        }

        happyImage.setOnClickListener {
            CustomToast("Happy 이미지를 클릭했습니다!", Color.YELLOW)
        }
    }
}
