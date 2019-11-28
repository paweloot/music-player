package com.paweloot.musicplayer

import android.media.AudioAttributes
import android.media.MediaMetadata
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
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

        setSessionPlaybackState(
            PlaybackStateCompat.STATE_PLAYING,
            player.currentPosition.toLong()
        )
        setSessionMetadata(song)

        Log.d(TAG, "Changing song in Observer")
        Log.d(TAG, "Current player position: ${player.currentPosition}")
    }

    override fun onPrepared(mediaPlayer: MediaPlayer?) {
        player.start()
    }

    private val callback = object : MediaSessionCompat.Callback() {
        override fun onPlay() {
            super.onPlay()
            player.start()
            setSessionPlaybackState(
                PlaybackStateCompat.STATE_PLAYING,
                player.currentPosition.toLong()
            )
            Log.d(TAG, "onPlay from MediaSession")
            Log.d(TAG, "onPlay position: ${player.currentPosition}")
        }

        override fun onPause() {
            super.onPause()
            player.pause()
            setSessionPlaybackState(
                PlaybackStateCompat.STATE_PAUSED,
                player.currentPosition.toLong()
            )
            Log.d(TAG, "onPause from MediaSession")
            Log.d(TAG, "onPause position: ${player.currentPosition}")
        }

        override fun onStop() {
            super.onStop()
            player.stop()
            setSessionPlaybackState(
                PlaybackStateCompat.STATE_STOPPED,
                player.currentPosition.toLong()
            )
            Log.d(TAG, "onStop from MediaSession")
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
                            PlaybackStateCompat.ACTION_PLAY_PAUSE or
                            PlaybackStateCompat.ACTION_PAUSE
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

    private fun setSessionPlaybackState(state: Int, position: Long) {
        mediaSession?.setPlaybackState(
            playbackStateBuilder
                .setActions(
                    PlaybackStateCompat.ACTION_PLAY or
                            PlaybackStateCompat.ACTION_PLAY_PAUSE or
                            PlaybackStateCompat.ACTION_PAUSE
                )
                .setState(state, position, 1.0f)
                .build()
        )
    }

    private fun setSessionMetadata(song: Song) {
        mediaSession?.setMetadata(
            MediaMetadataCompat.Builder()
                .putString(MediaMetadata.METADATA_KEY_TITLE, song.title)
                .putString(MediaMetadata.METADATA_KEY_ARTIST, song.artist)
                .putString(MediaMetadata.METADATA_KEY_ALBUM, song.album)
                .build()
        )
    }
}