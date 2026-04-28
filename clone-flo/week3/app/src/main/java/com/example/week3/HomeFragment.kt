package com.example.week3

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.week3.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.homeTodayMusicAlbum.setOnClickListener {
            val bundle = Bundle().apply {
                putString("title",binding.homeTodayMusicTitle02Tv.text.toString())
                putString("singer", binding.homeTodayMusicSingerTv.text.toString())
            }
            val albumfragment = AlbumFragment()
            albumfragment.arguments = bundle

            (activity as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, albumfragment)
                .addToBackStack(null)
                .commit()
        }
        //리스트안에 프리그먼트 추가 배너
        val bannerAdapter = BannerVPAdapter(this)
        //괄호안에 추가할 프리그먼트 추가
        bannerAdapter.addFragment(BannerFragment(R.drawable.img_home_viewpager_exp))
        bannerAdapter.addFragment(BannerFragment(R.drawable.img_home_viewpager_exp2))
        bannerAdapter.addFragment(BannerFragment(R.drawable.img_home_viewpager_exp))
        bannerAdapter.addFragment(BannerFragment(R.drawable.img_home_viewpager_exp2))

        binding.homeBannerVp.adapter = bannerAdapter
        //뷰페이저가 좌우로 스크롤해주는것
        binding.homeBannerVp.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        //홈 앨범
        val homeSliderAdapter = HomeSliderVPAdapter(this)

        homeSliderAdapter.addFragment(HomeSliderFragment(R.drawable.img_first_album_default))
        homeSliderAdapter.addFragment(HomeSliderFragment(R.drawable.img_second_album_default))
        homeSliderAdapter.addFragment(HomeSliderFragment(R.drawable.img_third_album_default))

        binding.homePannelBackgroundVp.adapter = homeSliderAdapter
        binding.homePannelBackgroundVp.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        // Indicator에 viewPager 설정
        binding.homePannelIndicator.setViewPager(binding.homePannelBackgroundVp)


        return binding.root
        }

/*//indicator구현? 모르겠음
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = BannerVPAdapter(this)
        adapter.addFragment(BannerFragment(R.drawable.banner1))
        adapter.addFragment(BannerFragment(R.drawable.banner2))
        adapter.addFragment(BannerFragment(R.drawable.banner3))

        binding.homeBannerVp.adapter = adapter

        // Indicator 연결 (TabLayout 사용)
        TabLayoutMediator(binding.homeBannerIndicator, binding.homeBannerVp) { _, _ -> }.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }*/
    }