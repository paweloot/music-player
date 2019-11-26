package com.paweloot.musicplayer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.paweloot.musicplayer.dummy.DummyContent

class MainActivity : AppCompatActivity(), SongListFragment.OnSongSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val songFragment = SongListFragment.newInstance()

        supportFragmentManager.beginTransaction()
            .add(R.id.activity_main_fragment_container, songFragment)
            .commit()
    }

    override fun onSongSelected(song: Song) {
        // TODO
    }
}
