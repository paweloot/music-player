package com.paweloot.musicplayer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainActivityViewModel : ViewModel() {

    val currentSong: LiveData<Song> = MutableLiveData<Song>()
}