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
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

private const val PERMISSION_READ_EXTERNAL_STORAGE = 0
private const val TAG = "LifecyclePaweloot"

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
                Log.d(TAG, "MainActivity: onConnected to media browser")
            }
        }

    private val controllerCallback =
        object : MediaControllerCompat.Callback() {
            override fun onPlaybackStateChanged(state: PlaybackStateCompat) {
                super.onPlaybackStateChanged(state)

                when (state.state) {
                    PlaybackStateCompat.STATE_PLAYING -> {
                        currentSongFragment.setCurrentState(PlaybackStateCompat.STATE_PLAYING)
                    }
                    PlaybackStateCompat.STATE_PAUSED -> {
                        currentSongFragment.setCurrentState(PlaybackStateCompat.STATE_PAUSED)
                    }
                }

                Log.d(TAG, "MainActivity: onPlayBackStateChanged")
            }

            override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
                super.onMetadataChanged(metadata)
            }
        }

    private lateinit var songDataManager: SongDataManager
    private lateinit var mediaBrowser: MediaBrowserCompat

    private lateinit var songFragment: SongListFragment
    private lateinit var currentSongFragment: CurrentSongFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestReadExternalStoragePermission()

        SongDataManager.initialize(this)
        songDataManager = SongDataManager.get()

        songFragment = SongListFragment.newInstance(songDataManager)
        currentSongFragment = CurrentSongFragment.newInstance()

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

        if (!mediaBrowser.isConnected) {
            mediaBrowser.connect()
        }

        MediaControllerCompat.getMediaController(this)
            ?.registerCallback(controllerCallback)
    }

    override fun onResume() {
        super.onResume()
        volumeControlStream = AudioManager.STREAM_MUSIC
    }

    override fun onStop() {
        super.onStop()
        MediaControllerCompat.getMediaController(this)?.unregisterCallback(controllerCallback)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mediaBrowser.isConnected) {
            mediaBrowser.disconnect()
        }
    }

    override fun onSongSelected(song: Song) {
        SongDataManager.get().currentSong.postValue(song)
    }

    private fun buildTransportControls() {
        val mediaController =
            MediaControllerCompat.getMediaController(this@MainActivity)

        currentSongFragment.setOnPlayPauseButtonListener(View.OnClickListener {
            when (mediaController.playbackState.state) {
                PlaybackStateCompat.STATE_PLAYING ->
                    mediaController.transportControls.pause()
                PlaybackStateCompat.STATE_PAUSED ->
                    mediaController.transportControls.play()
            }
        })

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
