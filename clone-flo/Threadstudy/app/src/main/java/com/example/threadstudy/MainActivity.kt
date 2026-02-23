/*
package com.example.threadstudy

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

//스레드가 왔다갔다하는게 콘텍스트스위칭?
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState:Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val a = A()
        val b = B()

        a.start()
        // a스레드가 실행하는데 다른 스레드가 실행하지 못하게 하고싶을때 join사용
        a.join()
        b.start()
    }
    class A : Thread(){
        override fun run(){
            super.run()
            for(i in 1..1000){
                Log.d("test","first : $i")
            }
        }
    }
    class B : Thread(){
        override fun run(){
            super.run()
            for(i in 1000 downTo 1){
                Log.d("test","second : $i")
            }
        }
    }
}*/


package com.example.threadstudy

import android.os.Bundle
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.threadstudy.databinding.ActivityMainBinding
import android.os.Handler

//이미지를 2초마다 바꾸는거 원래는 oncreate함수에 대해서 짜면 안되는 이유 : 메인 스레드가 이미지를 바꾸는 일을 계속 수행하니 다른 버튼을 누르는 할수 없어워크 스레드에서 실행해줘야한다.
//메인 스레드가 사용자 입력에 즉시 반응하고 UI를 부드럽게 유지할 수 있도록, 시간이 걸리는 모든 작업은 워커 스레드로 분리해야 한다
class MainActivity : AppCompatActivity() {
    lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState:Bundle?){
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val handler = Handler(Looper.getMainLooper())

        val imageList = arrayListOf<Int>()

        imageList.add(R.drawable.ic_flo_logo)
        imageList.add(R.drawable.btn_miniplay_mvplay)
        imageList.add(R.drawable.btn_miniplay_mvpause)
        imageList.add(R.drawable.ic_flo_logo)
        imageList.add(R.drawable.btn_miniplay_mvplay)
        imageList.add(R.drawable.btn_miniplay_mvpause)

        //메인스레드만 뷰렌더링가능 지금은 워크 스레드에서 뷰렌더링할라고하다 오류 해결은 핸들러만든다 스레드끼리 통신을 도와주는 클래스
        Thread{
            for(image in imageList){
                //상속받은 액티비티에서 함수가 있는데 runOnuiTread도 있다. 내부에 코드는 메인스레드에서 돌아감.
                handler.post{// ✅ 이 부분이 워커 스레드(Thread)에서 메인 스레드로 UI 작업(setImageResource)을 전달하는 통신 수단입니다.
                    binding.iv.setImageResource(image)                }
                Thread.sleep(2000)
            }
        }.start()
    }
}
