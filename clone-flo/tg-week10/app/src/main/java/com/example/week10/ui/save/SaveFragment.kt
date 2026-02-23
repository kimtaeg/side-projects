package UI.save

import androidx.lifecycle.lifecycleScope
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.week10.R
import com.example.week10.ui.rvadapter.SaveRVAdapter
import com.example.week10.data.Song
import com.example.week10.data.SongDatabase
import com.example.week10.databinding.FragmentSaveBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SaveFragment : Fragment() {
    lateinit var binding: FragmentSaveBinding

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
        viewLifecycleOwner.lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                songDB.songDao().updateAllLikedSongs(false)
            }

            // 메인 스레드로 돌아와 UI 갱신
            initRecyclerview()
        }
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
            override fun onRemoveSong(songId: Int) {
                viewLifecycleOwner.lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        songDB.songDao().updateIsLikeById(false, songId)
                    }
                    // 삭제 후 리스트 새로고침
                    initRecyclerview()
                }
            }
        })
    }
}
