package com.example.week4

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.week4.databinding.FragmentHomeBinding
import com.google.gson.Gson

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private var albumDatas = ArrayList<Album>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

            // 데이터 리스트 생성 더미 데이터
        albumDatas.apply {
            add(Album("SWAG II", "Justin Bieber",R.drawable.justin,arrayListOf(Song("SPEED DEMON", "Justin Bieber", R.drawable.justin))
            ))
            add(Album("Hurry Up Tommorrow", "The weeknd", R.drawable.weekend2,arrayListOf(Song("Open Hearts", "The weeknd", R.drawable.weekend2))))
            add(Album("GNX", "Kendrickn Lamar", R.drawable.kendrick,arrayListOf(Song("reincarnated", "Kendrickn Lamar", R.drawable.kendrick))))
            add(Album("만추", "헤이즈", R.drawable.hezie,arrayListOf(Song("떨어지는 낙엽까지도", "헤이즈", R.drawable.hezie))))
            add(Album("킁", "C JAMM", R.drawable.cjam,arrayListOf(Song("포커페이스", "C JAMM", R.drawable.cjam))))
            add(Album("치명적인 앨범 III", "기리보이", R.drawable.giriboy,arrayListOf(Song("이때다", "기리보이", R.drawable.giriboy))))
            add(Album("UNDERGROUND ROCKSTAR", "창모", R.drawable.changmo,arrayListOf(Song("태지", "창모", R.drawable.changmo))))
            add(Album("Zip", "Zion.T",R.drawable.ziont,arrayListOf(Song("돌고래", "Zion.T",R.drawable.ziont))))
            add(Album("130 Mood:TRBL", "DEAN", R.drawable.dean,arrayListOf(Song("bonnie & clyde", "DEAN", R.drawable.dean))))
        }

            //어뎁터 데이터리스트 연결
            val albumRVAdapter = AlbumRVAdapter(albumDatas)
            //너가 사용해야할 어뎁터는 이거다
            binding.homeTodayMusicAlbumRv.adapter = albumRVAdapter
            //레이아웃 매니저 설정
            binding.homeTodayMusicAlbumRv.layoutManager = LinearLayoutManager(
                context,
                LinearLayoutManager.HORIZONTAL, false
            )

            /*val albumfragment = AlbumFragment()
            albumfragment.arguments = bundle*/
            //화면 전환 및 앨범데이터를 프리그먼트전환이 될때 같이 넘겨줄때 여러방법있지만 버들사용
            albumRVAdapter.setMyItemClickListener(object : AlbumRVAdapter.MyItemClickListener {
                override fun onItemClick(album: Album) {
                    changeAlbumFragment(album)

                }
                //앨범 삭제해주는 것
               /*  override fun onRemoveAlbum(position: Int) {
                    albumRVAdapter.removeItem(position)
                }*/


                //miniplayer 빈영 구현
                override fun onPlayClick(album: Album) {
                    val song = album.songs?.getOrNull(0)
                    if (song != null) {
                        val songWithCover = song.copy(coverImg = album.coverImg)

                        val sharedPreferences = requireActivity().getSharedPreferences("song", AppCompatActivity.MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        val songJson = Gson().toJson(songWithCover)
                        editor.putString("songData", songJson)
                        editor.apply()

                        // ✅ MainActivity의 미니플레이어와 song 변수에 즉시 반영
                        (activity as MainActivity).setMiniPlayer(songWithCover)
                        (activity as MainActivity).song = songWithCover
                    } else {
                        Toast.makeText(context, "앨범에 곡 정보가 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                }

            })


            /*   (activity as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, albumfragment)
                .addToBackStack(null)
                .commit()*/

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

    private fun changeAlbumFragment(album: Album) {
        (context as MainActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.main_frm, AlbumFragment().apply {
                arguments = Bundle().apply {
                    val gson = Gson()
                    val albumJson = gson.toJson(album)
                    putString("album", albumJson)
                }
            })
            .commitAllowingStateLoss()
           /* .addToBackStack(null)
                    //.commit()*/
    }
}
