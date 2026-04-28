package UI.album

import android.view.LayoutInflater
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import com.example.week10.R
import com.example.week10.data.Album
import com.example.week10.databinding.ItemAlbumBinding

class AlbumRVAdapter (private val albumList: ArrayList<Album>) : RecyclerView.Adapter<AlbumRVAdapter.ViewHolder>(){

    //데이터 관리는 어뎁터에서 해준다
    //데이터를 넘기는 작업
    interface MyItemClickListener{
        //데이터를 넘기는 작업
        fun onItemClick(album: Album)
        //데이터 삭제
        /*
                fun onRemoveAlbum(position: Int)
        */
        fun onPlayClick(album: Album)
    }
    // 외부에서 받는 함수랑 저장할 변수를 선언
    private lateinit var myItemClickListener: MyItemClickListener
    fun setMyItemClickListener(itemClickListener: MyItemClickListener){
        myItemClickListener = itemClickListener
    }

    // 리사이클어뎁터는 데이터가 바뀐것을 모르기때문에 notifyDataSetChanged()을 꼭 선언해줘야한다.
    fun addItem(album: Album){
        albumList.add(album)
        notifyDataSetChanged()
    }

    fun removeItem(position: Int){
        albumList.removeAt(position)
        notifyDataSetChanged()
    }



    //뷰홀더를 생성해줄때 호출되는 메서드, 아이템뷰객체를 만든뒤 이를 재활용하기위해 뷰홀더에 던져준다.
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding: ItemAlbumBinding = ItemAlbumBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    //onBindViewHolder는 뷰홀더에 데이터 바운딩할때마다 호출되는 함수. position은 리사이클뷰에 인덕스아이디를 말한다.
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(albumList[position])
        // 앨범프리그먼트로 이동 클릭이벤트, 클릭인터페이스
        holder.itemView.setOnClickListener {
            myItemClickListener.onItemClick(albumList[position])
        }

    }

    //데이터세트 크기를 알려주는 함수. 받아온 앨범리스트 크기를 넣어준다.
    override fun getItemCount(): Int = albumList.size
    // 아이템뷰객체들을 재활용하기 위해 날라가지 않도록 담고있는 그릇. 매개변수로 아이템객체를 받아야한다.
    inner class ViewHolder(var binding: ItemAlbumBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(album: Album){
            binding.itemAlbumTitleTv.text = album.title
            binding.itemAlbumSingerTv.text = album.singer

            binding.itemAlbumCoverImgIv.setImageResource(album.coverImg ?: R.drawable.img_album_exp2)
            binding.homeAlbumStart.setOnClickListener {
                myItemClickListener.onPlayClick(album)
            }
        }
    }
}