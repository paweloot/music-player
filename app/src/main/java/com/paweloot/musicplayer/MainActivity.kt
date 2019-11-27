package com.paweloot.musicplayer

import android.Manifest
import android.content.ComponentName
import android.content.pm.PackageManager
import android.media.AudioManager
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders

private const val PERMISSION_READ_EXTERNAL_STORAGE = 0

class MainActivity : AppCompatActivity(), SongListFragment.OnSongSelectedListener {

    private val connectionCallback =
        object : MediaBrowserCompat.ConnectionCallback() {
            override fun onConnected() {
                super.onConnected()

                mediaBrowser.sessionToken.also { token ->
                    val mediaController = MediaControllerCompat(
                        this@MainActivity,
                        token
                    )

                    MediaControllerCompat.setMediaController(
                        this@MainActivity,
                        mediaController
                    )
                }

                buildTransportControls()
            }
        }

    private val controllerCallback =
        object : MediaControllerCompat.Callback() {
            override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
                super.onPlaybackStateChanged(state)
            }

            override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
                super.onMetadataChanged(metadata)
            }
        }

    private lateinit var songDataManager: SongDataManager
    private lateinit var mediaBrowser: MediaBrowserCompat

    private lateinit var mainActivityViewModel: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainActivityViewModel = ViewModelProviders.of(this)
            .get(MainActivityViewModel::class.java)

        mainActivityViewModel.currentSong.observe(
            this,
            Observer<Song> { song ->
                Log.d("MainActivity", "Changed song to ${song.title}")
            }
        )

        requestReadExternalStoragePermission()

        SongDataManager.initialize(this)
        songDataManager = SongDataManager.get()

        val songFragment = SongListFragment.newInstance(songDataManager)
        val currentSongFragment = CurrentSongFragment.newInstance()

        supportFragmentManager.beginTransaction()
            .add(R.id.activity_main_song_list_container, songFragment)
            .add(R.id.activity_main_current_song_container, currentSongFragment)
            .commit()

        mediaBrowser = MediaBrowserCompat(
            this,
            ComponentName(this, SongPlaybackService::class.java),
            connectionCallback,
            null
        )
    }

    override fun onStart() {
        super.onStart()
        mediaBrowser.connect()
    }

    override fun onResume() {
        super.onResume()
        volumeControlStream = AudioManager.STREAM_MUSIC
    }

    override fun onStop() {
        super.onStop()
        MediaControllerCompat.getMediaController(this)?.unregisterCallback(controllerCallback)
        mediaBrowser.disconnect()
    }

    override fun onSongSelected(song: Song) {
        mainActivityViewModel.currentSong.value = song
    }

    private fun buildTransportControls() {
        val mediaController =
            MediaControllerCompat.getMediaController(this@MainActivity)

//        playPause = findViewById<ImageView>(R.id.play_pause).apply {
//            setOnClickListener {
//                // Since this is a play/pause button, you'll need to test the current state
//                // and choose the action accordingly
//
//                val pbState = mediaController.playbackState.state
//                if (pbState == PlaybackStateCompat.STATE_PLAYING) {
//                    mediaController.transportControls.pause()
//                } else {
//                    mediaController.transportControls.play()
//                }
//            }
//        }

        val metadata = mediaController.metadata
        val pbState = mediaController.playbackState

        mediaController.registerCallback(controllerCallback)
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
