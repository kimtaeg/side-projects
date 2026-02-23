package UI.save

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.week9.databinding.BottomsheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SaveBottomSheetFragment : BottomSheetDialogFragment() {
    lateinit var binding: BottomsheetBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = BottomsheetBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.sheetIv1.setOnClickListener {
        }
        binding.sheetIv2.setOnClickListener {
        }
        binding.sheetIv3.setOnClickListener {
        }
        binding.sheetIv4.setOnClickListener {
            val delete = Bundle().apply {
                putBoolean("delete", true)
            }
            parentFragmentManager.setFragmentResult("requestKey", delete)
            dismiss()
            }
        }
    }
