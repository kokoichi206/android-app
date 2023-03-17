package com.example.audioplay.playback

import android.content.Context
import android.media.MediaPlayer
import androidx.core.net.toUri
import java.io.File

class AndroidAudioPlayer(
    private val context: Context,
) : AudioPlayer {

    private var _player: MediaPlayer? = null

    override fun playFile(file: File) {
        MediaPlayer.create(context, file.toUri()).apply {
            _player = this

            start()
        }
    }

    override fun stop() {
        _player?.stop()
        _player?.release()
        _player = null
    }
}