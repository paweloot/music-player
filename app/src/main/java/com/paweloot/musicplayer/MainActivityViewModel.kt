package com.paweloot.musicplayer

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainActivityViewModel : ViewModel() {

    val currentSong: MutableLiveData<Song> = MutableLiveData()
}