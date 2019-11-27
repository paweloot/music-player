package com.paweloot.musicplayer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders


class CurrentSongFragment : Fragment() {

    private lateinit var mainActivityViewModel: MainActivityViewModel

    private lateinit var titleTextView: TextView
    private lateinit var artistTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainActivityViewModel = ViewModelProviders.of(requireActivity())
            .get(MainActivityViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =
            inflater.inflate(R.layout.fragment_current_song, container, false)

        titleTextView = view.findViewById(R.id.song_title)
        artistTextView = view.findViewById(R.id.song_artist)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainActivityViewModel.currentSong.observe(
            viewLifecycleOwner,
            Observer { song ->
                titleTextView.text = song.title
                artistTextView.text = song.artist
            }
        )
    }

    companion object {
        fun newInstance() = CurrentSongFragment()
    }
}
