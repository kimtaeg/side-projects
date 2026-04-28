package UI.home

import UI.banner.BannerFragment
import com.example.week9.ui.vpadapter.BannerVPAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.week9.data.Album
import com.example.week9.ui.album.AlbumFragment
import UI.album.AlbumRVAdapter
import com.example.week9.ui.main.MainActivity
import UI.potcast.Potcast
import com.example.week9.R
import com.example.week9.data.SongDatabase
import com.example.week9.databinding.FragmentHomeBinding
import com.example.week9.ui.rvadapter.potcastRVAdapter
import com.example.week9.ui.vpadapter.HomeSliderVPAdapter
import com.google.gson.Gson

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private var albumDatas = ArrayList<Album>()
    //DB인스턴스 선언
    private lateinit var songDB : SongDatabase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        //db초기화
        songDB = SongDatabase.Companion.getInstance(requireContext())!!

        //db에서 앨범 데이터 가져오기
        //inputDummyAlbums()

        initPotcastRecyclerView()
        albumDatas.clear()

        albumDatas.addAll(songDB.albumDao().getAlbums()as ArrayList<Album>)

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


                //miniplayer 빈영 구현
                override fun onPlayClick(album: Album) {
                    //db인스턴스 가져오기
                    val songDB = SongDatabase.Companion.getInstance(requireContext())!!
                    //db에서 해당 앨범 id에 속하는 노래 목록 조회
                    val albumSongs = songDB.songDao().getSongsByAlbumId(album.id)
                    //노래 목록 중 첫번째 곡
                    //val song = albumSongs.firstOrNull()
                    val song = songDB.songDao().getSongs().firstOrNull{it.albumIdx == album.id}

                    if(song!= null){
                        val editor = requireContext().getSharedPreferences("song",
                            AppCompatActivity.MODE_PRIVATE).edit()
                        editor.putInt("songId",song.id)
                        editor.putBoolean("isPlaying",true)
                        editor.apply()

                        song.isPlaying = true
                        (context as MainActivity).setMiniPlayer(song)
                        (context as MainActivity).song = song
                        (context as MainActivity).sendServiceCommand("play", song.music)
                    } else{
                        Toast.makeText(context, "앨범에 수록된 곡이 없음", Toast.LENGTH_SHORT).show()
                    }
                }

            })




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
    }


    // potcast rv연결
    private fun initPotcastRecyclerView(){
        val potcastData = arrayListOf<Potcast>(
            Potcast("제목", "가수", R.drawable.img_potcast_exp),
            Potcast("제목", "가수", R.drawable.img_potcast_exp),
            Potcast("제목", "가수", R.drawable.img_potcast_exp),
            Potcast("제목", "가수", R.drawable.img_potcast_exp),
            Potcast("제목", "가수", R.drawable.img_potcast_exp),
            Potcast("제목", "가수", R.drawable.img_potcast_exp),
        )

        val potcastAdapter = potcastRVAdapter(potcastData)
        binding.homePotcastRv.adapter = potcastAdapter
    }
}
