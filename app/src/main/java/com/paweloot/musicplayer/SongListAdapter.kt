package com.paweloot.musicplayer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.paweloot.musicplayer.SongListFragment.OnSongSelectedListener


class SongListAdapter(
    private val songs: List<Song>,
    private val onSongSelectedListener: OnSongSelectedListener
) : RecyclerView.Adapter<SongListAdapter.SongHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_song, parent, false)

        return SongHolder(view)
    }

    override fun onBindViewHolder(holder: SongHolder, position: Int) {
        val song = songs[position]
        holder.bind(song)
    }

    override fun getItemCount(): Int = songs.size

    inner class SongHolder(view: View) : RecyclerView.ViewHolder(view),
        View.OnClickListener {
        private val titleTextView: TextView = view.findViewById(R.id.title)
        private val artistTextView: TextView = view.findViewById(R.id.artist)

        private lateinit var song: Song

        init {
            view.setOnClickListener(this)
        }

        fun bind(song: Song) {
            this.song = song

            titleTextView.text = song.title
            artistTextView.text = song.artist
        }

        override fun onClick(v: View) {
            onSongSelectedListener.onSongSelected(song)
        }
    }
}
