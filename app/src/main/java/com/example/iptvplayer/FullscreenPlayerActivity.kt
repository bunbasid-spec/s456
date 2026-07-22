package com.example.iptvplayer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

class FullscreenPlayerActivity : AppCompatActivity() {

    private var player: ExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // Tam ekran PlayerView tasarımı

        val streamUrl = intent.getStringExtra("STREAM_URL") ?: "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8"
        val playerView = findViewById<PlayerView>(R.id.playerView)

        player = ExoPlayer.Builder(this).build().apply {
            playerView.player = this
            setMediaItem(MediaItem.fromUri(streamUrl))
            prepare()
            playWhenReady = true
        }
    }

    override fun onStop() {
        super.onStop()
        player?.release()
        player = null
    }
}
