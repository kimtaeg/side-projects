package UI.save

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.week9.data.Album
import com.example.week9.ui.album.AlbumFragment
import UI.album.AlbumLockerRVAdapter
import com.example.week9.R
import com.example.week9.data.SongDatabase
import com.example.week9.databinding.FragmentSaveAlbumBinding
import com.google.gson.Gson

class SaveAlbumFragment : Fragment() {
    lateinit var binding: FragmentSaveAlbumBinding
    lateinit var albumDB: SongDatabase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSaveAlbumBinding.inflate(inflater, container, false)
        albumDB = SongDatabase.Companion.getInstance(requireContext())!!
        return binding.root
    }

    override fun onStart(){
        super.onStart()
        initRecyclerview()
    }

    private fun initRecyclerview(){
        binding.saveRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        val albumRVAdapter = AlbumLockerRVAdapter()

        albumRVAdapter.setMyItemClickListener(object : AlbumLockerRVAdapter.MyItemClickListener{
            override fun onRemoveAlbum(albumId: Int){
                SongDatabase.Companion.getInstance(requireContext())!!.albumDao().getAlbum(albumId)
            }

            override fun onItemClick(album: Album){
                val fragment = AlbumFragment().apply{
                    arguments = Bundle().apply{
                        putString("album",Gson().toJson(album))
                    }
                }
                parentFragmentManager.beginTransaction().replace(R.id.main_frm,fragment).commitAllowingStateLoss()
            }
        })

        binding.saveRv.adapter = albumRVAdapter
        albumRVAdapter.addAlbums(albumDB.albumDao().getLikedAlbums(getJwt())as ArrayList)
    }
    private fun getJwt(): Int{
        val spf = activity?.getSharedPreferences("auth", AppCompatActivity.MODE_PRIVATE)
        val jwt = spf!!.getInt("jwt",0)
        return jwt
    }
}