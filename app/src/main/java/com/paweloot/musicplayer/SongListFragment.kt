package com.paweloot.musicplayer

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SongListFragment(private val songDataManager: SongDataManager) : Fragment() {

    private lateinit var onSongSelectedListener: OnSongSelectedListener

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =
            inflater.inflate(R.layout.fragment_song_list, container, false)

        with(view as RecyclerView) {
            layoutManager = LinearLayoutManager(context)

            val songs: List<Song> = songDataManager.songData
            adapter = SongListAdapter(songs, onSongSelectedListener)
        }

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnSongSelectedListener) {
            onSongSelectedListener = context
        } else {
            throw RuntimeException("$context must implement OnListFragmentInteractionListener")
        }
    }

    interface OnSongSelectedListener {
        fun onSongSelected(song: Song)
    }

    companion object {
        fun newInstance(songDataManager: SongDataManager) =
            SongListFragment(songDataManager)
    }
}
