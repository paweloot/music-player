package com.paweloot.musicplayer

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.Observer
import androidx.media.MediaBrowserServiceCompat

private const val TAG = "SongPlaybackService"
private const val MEDIA_ROOT_ID = "media_root_id"

class SongPlaybackService : MediaBrowserServiceCompat(), MediaPlayer.OnPreparedListener {

    private val currentSongObserver = Observer<Song> { song ->
        player.stop()
        player.reset()

        player.apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            setDataSource(applicationContext, Uri.parse(song.dataPath))
        }

        player.prepareAsync()
    }

    override fun onPrepared(mediaPlayer: MediaPlayer?) {
        player.start()
    }

    private val callback = object : MediaSessionCompat.Callback() {
        override fun onPlay() {
            super.onPlay()
            player.start()
        }

        override fun onPause() {
            super.onPause()
            player.pause()
        }

        override fun onStop() {
            super.onStop()
            player.stop()
        }
    }

    private var mediaSession: MediaSessionCompat? = null
    private lateinit var playbackStateBuilder: PlaybackStateCompat.Builder
    private val player: MediaPlayer = MediaPlayer()

    private val songDataManager: SongDataManager = SongDataManager.get()

    override fun onCreate() {
        super.onCreate()

        mediaSession = MediaSessionCompat(baseContext, TAG).apply {
            setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
            )

            playbackStateBuilder = PlaybackStateCompat.Builder()
                .setActions(
                    PlaybackStateCompat.ACTION_PLAY or
                            PlaybackStateCompat.ACTION_PLAY_PAUSE
                )

            setPlaybackState(playbackStateBuilder.build())
            setCallback(callback)
            setSessionToken(sessionToken)
            isActive = true
        }

        songDataManager.currentSong.observeForever(currentSongObserver)
        player.setOnPreparedListener(this)
    }

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
        val mediaItems: MutableList<MediaBrowserCompat.MediaItem> =
            mutableListOf()

        if (MEDIA_ROOT_ID == parentId) {
            // Build the MediaItem objects for the top level,
            // and put them in the mediaItems list...
        }

        result.sendResult(mediaItems)
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot? {
        return BrowserRoot(MEDIA_ROOT_ID, null)
    }

    override fun onDestroy() {
        super.onDestroy()

        player.stop()
        player.release()
        mediaSession?.isActive = false

        songDataManager.currentSong.removeObserver(currentSongObserver)
    }
}