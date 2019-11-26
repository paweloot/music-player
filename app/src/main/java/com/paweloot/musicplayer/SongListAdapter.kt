package com.paweloot.musicplayer

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView


import com.paweloot.musicplayer.SongListFragment.OnSongSelectedListener


class SongListAdapter(
    private val songs: List<Song>,
    private val onSongSelectedListener: OnSongSelectedListener
) : RecyclerView.Adapter<SongListAdapter.SongHolder>() {

    private val onClickListener: View.OnClickListener

    init {
        onClickListener = View.OnClickListener { v ->
            val song = v.tag as Song
            onSongSelectedListener.onSongSelected(song)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_song, parent, false)

        return SongHolder(view)
    }

    override fun onBindViewHolder(holder: SongHolder, position: Int) {
        val song = songs[position]

        holder.titleTextView.text = song.title
        holder.artistTextView.text = song.artist

        with(holder.view) {
            tag = song
            setOnClickListener(onClickListener)
        }
    }

    override fun getItemCount(): Int = songs.size

    inner class SongHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView = view.findViewById(R.id.title)
        val artistTextView: TextView = view.findViewById(R.id.artist)

    }
}
