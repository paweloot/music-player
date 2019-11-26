package com.paweloot.musicplayer

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.paweloot.musicplayer.dummy.DummyContent
import com.paweloot.musicplayer.dummy.DummyContent.DummyItem


class SongListFragment : Fragment() {

    private lateinit var onSongSelectedListener: OnSongSelectedListener

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =
            inflater.inflate(R.layout.fragment_song_list, container, false)

        with(view as RecyclerView) {
            layoutManager = LinearLayoutManager(context)
            adapter = SongListAdapter(DummyContent.ITEMS, onSongSelectedListener)
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
        fun onSongSelected(item: DummyItem?)
    }

    companion object {
        fun newInstance() = SongListFragment()
    }
}