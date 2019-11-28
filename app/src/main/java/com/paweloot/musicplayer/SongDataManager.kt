package com.paweloot.musicplayer

import android.content.Context
import android.provider.MediaStore
import androidx.lifecycle.MutableLiveData

class SongDataManager private constructor(private val context: Context) {

    val songData: MutableList<Song> = mutableListOf()
    val currentSong: MutableLiveData<Song> = MutableLiveData()

    init {
        querySongData()
    }

    private fun querySongData() {
        val albumArtworks = loadAlbumArtworks()

        val projection = arrayOf(
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM_ID
        )
        val selection = MediaStore.Audio.Media.IS_MUSIC + " == 1"
        val sort = MediaStore.Audio.Media.TITLE + " ASC"

        with(context as MainActivity) {
            val cursor = contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection, selection, null, sort
            )

            if (cursor != null && cursor.count > 0) {
                while (cursor.moveToNext()) {
                    val data = cursor.getString(0)
                    val title = cursor.getString(1)
                    val album = cursor.getString(2)
                    val artist = cursor.getString(3)
                    val albumId = cursor.getString(4)

                    songData.add(
                        Song(
                            data, title, artist, album,
                            albumArtworks[albumId.toInt()]
                        )
                    )
                }
            }

            cursor?.close()
        }
    }

    private fun loadAlbumArtworks(): HashMap<Int, String?> {
        val artworks = HashMap<Int, String?>()

        val projection = arrayOf(
            MediaStore.Audio.Albums._ID,
            MediaStore.Audio.Albums.ALBUM_ART
        )

        with(context as MainActivity) {
            val cursor = contentResolver.query(
                MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                null
            )

            if (cursor != null && cursor.count > 0) {
                while (cursor.moveToNext()) {
                    val albumId = cursor.getString(0)
                    val albumArt = cursor.getString(1)

                    artworks[albumId.toInt()] = albumArt
                }
            }
            cursor?.close()
        }

        return artworks
    }

    companion object {
        private var INSTANCE: SongDataManager? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = SongDataManager(context)
            }
        }

        fun get(): SongDataManager {
            return INSTANCE ?: throw IllegalStateException("SongDataManager must be initialized!")
        }
    }
}