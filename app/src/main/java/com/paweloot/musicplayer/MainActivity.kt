package com.paweloot.musicplayer

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

private const val PERMISSION_READ_EXTERNAL_STORAGE = 0

class MainActivity : AppCompatActivity(), SongListFragment.OnSongSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestReadExternalStoragePermission()

        val songFragment = SongListFragment.newInstance()

        supportFragmentManager.beginTransaction()
            .add(R.id.activity_main_fragment_container, songFragment)
            .commit()
    }

    override fun onSongSelected(song: Song) {
        // TODO
    }

    private fun requestReadExternalStoragePermission() {
        val permission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSION_READ_EXTERNAL_STORAGE
            )
        }
    }
}
