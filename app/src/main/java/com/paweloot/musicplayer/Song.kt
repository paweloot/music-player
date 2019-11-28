package com.paweloot.musicplayer

data class Song(
    val dataPath: String,
    val title: String,
    val artist: String,
    val album: String,
    val albumArtwork: String?
) {

}