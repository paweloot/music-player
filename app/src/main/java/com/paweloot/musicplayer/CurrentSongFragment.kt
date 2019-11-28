package com.paweloot.musicplayer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer


class CurrentSongFragment : Fragment() {

    private lateinit var titleTextView: TextView
    private lateinit var artistTextView: TextView
    private lateinit var playPauseButton: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =
            inflater.inflate(R.layout.fragment_current_song, container, false)

        titleTextView = view.findViewById(R.id.song_title)
        artistTextView = view.findViewById(R.id.song_artist)
        playPauseButton = view.findViewById(R.id.play_pause_button)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        SongDataManager.get().currentSong.observe(
            viewLifecycleOwner,
            Observer { song ->
                titleTextView.text = song.title
                artistTextView.text = song.artist
            }
        )
    }

    fun setOnPlayPauseButtonListener(onClickListener: View.OnClickListener) {
        playPauseButton.setOnClickListener(onClickListener)
    }

    companion object {
        fun newInstance() = CurrentSongFragment()
    }
}
