package UI.save

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.week9.R
import com.example.week9.ui.rvadapter.SaveRVAdapter
import com.example.week9.data.Song
import com.example.week9.data.SongDatabase
import com.example.week9.databinding.FragmentSaveBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class SaveFragment : Fragment() {
    lateinit var binding: FragmentSaveBinding

    //private var albumDatas = ArrayList<Album>()
    lateinit var songDB: SongDatabase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSaveBinding.inflate(inflater, container, false)

        songDB = SongDatabase.Companion.getInstance(requireContext())!!

        initSelectAllListener()

        return binding.root


        /*val saveRVAdapter = SaveRVAdapter(albumDatas)
        binding.saveRv.adapter = saveRVAdapter
        binding.saveRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        saveRVAdapter.setMyItemClickListener(object: SaveRVAdapter.MyItemClickListener{

            override fun onItemClick(album: Album) {
                changeAlbumFragment(album)
            }

            //삭제하는거
            override fun onRemoveAlbum(position: Int){
                saveRVAdapter.removeItem(position)
            }
        })

        return binding.root*/
    }

    private fun initSelectAllListener(){
        val clickListener = View.OnClickListener{showDeleteConfirmDialog()}

        binding.lokerChoiceTv.setOnClickListener(clickListener)
        binding.lokerCheckIv.setOnClickListener(clickListener)
    }

    private fun showDeleteConfirmDialog(){
        val bottomSheetDialog = BottomSheetDialog(requireContext())

        val dialogView = layoutInflater.inflate(R.layout.bottomsheet,null)

        bottomSheetDialog.setContentView(dialogView)

        setSelectButtonState(true)

        bottomSheetDialog.setOnDismissListener {
            setSelectButtonState(false)
        }
        dialogView.findViewById<ImageView>(R.id.sheet_iv4).setOnClickListener{
            deleteAllLikedSongs()
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.show()
    }

    private fun setSelectButtonState(isDialogShowing: Boolean) {
        val context = requireContext()


        val color = if (isDialogShowing) {
            context.getColor(R.color.select_color)
        } else {
            // 기본 색상으로 변경
            context.getColor(android.R.color.darker_gray)
        }

        val text = if (isDialogShowing) "선택해제" else "전체선택"

        //  이미지 리소스 변경
        val imageResource = if (isDialogShowing) {
            R.drawable.btn_playlist_select_on
        } else {
            R.drawable.btn_playlist_select_off
        }

        binding.lokerChoiceTv.text = text
        binding.lokerChoiceTv.setTextColor(color)
        binding.lokerCheckIv.setImageResource(imageResource)
    }

    private fun deleteAllLikedSongs() {
        // 모든 좋아요 노래의 isLiked 상태를 false로 업데이트
        Thread {
            songDB.songDao().updateAllLikedSongs(false)

            // UI 갱신 (리스트 다시 로드)
            requireActivity().runOnUiThread {
                initRecyclerview()
            }
        }.start()
    }

    override fun onStart() {
        super.onStart()
        initRecyclerview()
    }


    private fun initRecyclerview() {
        binding.saveRv.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        val songRVAdapter = SaveRVAdapter()

        binding.saveRv.adapter = songRVAdapter

        songRVAdapter.addSongs(songDB.songDao().getLikedSongs(true) as ArrayList<Song>)

        songRVAdapter.setMyItemClickListener(object : SaveRVAdapter.MyItemClickListener {
            //삭제 기능
            override fun onRemoveSong(songId: Int) {
               //DB에서 해당 노래를 삭제하는 로직
                Thread {
                    songDB.songDao().updateIsLikeById(false, songId)
                    requireActivity().runOnUiThread {

                    }
                }.start()
            }

        })

        /*
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
    }*/
    }
}
