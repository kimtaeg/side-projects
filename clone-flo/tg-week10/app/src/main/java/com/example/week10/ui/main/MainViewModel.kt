package com.example.week10.ui.main
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.week10.data.Song
import com.example.week10.data.SongDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(private val songDao: SongDao) : ViewModel() {

    // 현재 재생 중인 곡 정보를 관찰 가능한 LiveData로 관리
    private val _currentSong = MutableLiveData<Song>()
    val currentSong: LiveData<Song> = _currentSong

    // 노래 데이터 로드
    fun loadSong(songId: Int, defaultId: Int = 1) {
        viewModelScope.launch {
            val id = if (songId == 0) defaultId else songId

            val song = withContext(Dispatchers.IO) {
                songDao.getSongs(id)
            }
            _currentSong.postValue(song)
        }
    }

    // 재생 상태 업데이트 로직
    fun updatePlayingStatus(isPlaying: Boolean) {
        _currentSong.value?.let {
            it.isPlaying = isPlaying
            _currentSong.value = it
        }
    }

    // 재생 위치 업데이트
    fun updateSongSecond(second: Int) {
        _currentSong.value?.let {
            it.second = second
            _currentSong.value = it
        }
    }
}