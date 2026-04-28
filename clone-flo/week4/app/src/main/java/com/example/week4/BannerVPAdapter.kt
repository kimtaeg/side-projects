package com.example.week4

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

//뷰페이지에 이미지를 넣어야하는데 프래그먼트로 넣어야해서,FragmentStateAdapter라는 상속을 받는다.
class BannerVPAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    //여러개의 프래그먼트를 담아둘곳, 초기화를 꼭 해줘야한다
    //private은 클래스 안에서만 쓰는 변수라고 생각 안쓰면 해당 클래스에서만 써야하는 변수가 다른 클래스에서 사용하면 데이터가 변경이 일어나는걸 방지하기위해
    private val fragmentlist : ArrayList<Fragment> = ArrayList()


    // 상속을 받은 클래스가 실행될때 꼭 필요한 맴버 함수
    //클래스에서 연결된 뷰페이지에게 데이터를 전달할때 몇개를 전달하는지 알려주는 함수 getitemcount
    //add도 확인해보기 size는 리스트안에 있는 값에 개수를 가져오는것
    //  override fun getItemCount(): Int { return fragmentlist.size}
    override fun getItemCount(): Int = fragmentlist.size

    //프래그먼트들을 생성해주는 함수
    override fun createFragment(position: Int): Fragment = fragmentlist[position]//get이 4라면 0,1,2,3까지
    //이함수가 첨 실행될때 아무것도 없을때 추가할 프리그먼트를 위해 사용
    fun addFragment(fragment: Fragment){
        fragmentlist.add(fragment)
        //리스트 안에 새로운 값이 추가되었을때 뷰페이지에 추가된걸 알려주기 위해서
        notifyItemInserted(fragmentlist.size-1)
    }

}