package UI.locker

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.semantics.text
import androidx.fragment.app.Fragment
import com.example.week10.AuthManager
import com.example.week10.ui.login.LoginActivity
import com.example.week10.ui.main.MainActivity
import com.example.week10.databinding.FragmentLockerBinding
import com.example.week10.ui.vpadapter.LockerVPAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.kakao.sdk.user.UserApiClient

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
        //initViews()
        checkLoginStatus()
    }


    private fun getJwt():String?{
        val spf = activity?.getSharedPreferences("auth", AppCompatActivity.MODE_PRIVATE)
        //jwt가 저장되지 않았을 경우 빈 문자열을 반환하도록 한다.
        return spf?.getString("jwt",null)
    }
    private fun checkLoginStatus() {
        // AuthManager를 사용해 로그인 상태 확인
        if (AuthManager.isLoggedIn(requireContext())) {
            // 로그인 상태일 때
            binding.logInTv.text = "로그아웃"
            binding.logInTv.setOnClickListener {
                // 로그아웃 처리
                AuthManager.logout(requireContext())
                // 카카오 SDK에서도 로그아웃 처리
                UserApiClient.instance.logout { error ->
                    if (error != null) {
                        Log.e("LOGOUT", "카카오 로그아웃 실패", error)
                    } else {
                        Log.i("LOGOUT", "카카오 로그아웃 성공")
                    }
                }
                // UI 업데이트
                checkLoginStatus()
            }
        } else {
            // 로그아웃 상태일 때
            binding.logInTv.text = "로그인"
            binding.logInTv.setOnClickListener {
                startActivity(Intent(requireContext(), LoginActivity::class.java))
            }
        }
    }
    //로그인 로그아웃
    /*private fun initViews(){
        val jwt : String ?= getJwt()
        Log.d("LockerFragment", "")

        if(jwt==null){
            binding.logInTv.text = "로그인"
            binding.logInTv.setOnClickListener {
                startActivity(Intent(activity, LoginActivity::class.java))
                Log.d("LockerFragment", "현재 상태: 로그아웃")
            }
        }else{
            binding.logInTv.text = "로그아웃"
            binding.logInTv.setOnClickListener {
                Toast.makeText(activity, "로그아웃하셨습니다.", Toast.LENGTH_SHORT).show()
                Log.d("LockerFragment", "현재 상태: 로그인")

                //로그아웃 진행
                logout()
                startActivity(Intent(activity, MainActivity::class.java))
            }
        }
    }*/
    //로그아웃 진행하는 함수
    private fun logout(){
        val spf = activity?.getSharedPreferences("auth", AppCompatActivity.MODE_PRIVATE)
        val editor = spf!!.edit()

        editor.remove("userId")
        editor.apply()
    }

}
