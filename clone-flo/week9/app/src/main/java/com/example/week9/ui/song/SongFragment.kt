package UI.song

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.week9.databinding.FragmentSongBinding

class SongFragment : Fragment() {
    lateinit var binding : FragmentSongBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSongBinding.inflate(inflater, container,false)

        //mix버튼 변환
        binding.songMixOffIv.setOnClickListener {
            setMixStatus(true)
        }
        binding.songMixOnIv.setOnClickListener {
            setMixStatus(false)
        }
        return binding.root
    }
    //mix버튼 뱐환
    fun setMixStatus(notmixing : Boolean){
        if(notmixing){
            binding.songMixOnIv.visibility = View.VISIBLE
            binding.songMixOffIv.visibility = View.GONE
        }
        else{
            binding.songMixOnIv.visibility = View.GONE
            binding.songMixOffIv.visibility = View.VISIBLE
        }
    }
}