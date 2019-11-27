package com.paweloot.musicplayer

import android.media.MediaPlayer
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.media.MediaBrowserServiceCompat

private const val TAG = "SongPlaybackService"
private const val MEDIA_ROOT_ID = "media_root_id"

class SongPlaybackService : MediaBrowserServiceCompat() {

    private val callback = object : MediaSessionCompat.Callback() {
        override fun onPlay() {
            super.onPlay()
        }

        override fun onPause() {
            super.onPause()
        }

        override fun onStop() {
            super.onStop()
        }
    }

    private var mediaSession: MediaSessionCompat? = null
    private lateinit var playbackStateBuilder: PlaybackStateCompat.Builder
    private lateinit var player: MediaPlayer

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

        player = MediaPlayer()
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
    }
}