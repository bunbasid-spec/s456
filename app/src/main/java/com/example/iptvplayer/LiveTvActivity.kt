package com.example.iptvplayer

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

class LiveTvActivity : AppCompatActivity() {

    private var player: ExoPlayer? = null
    private lateinit var miniPlayerView: PlayerView
    private var currentlyPlayingUrl: String = "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8"
    private var selectedChannelUrl: String = "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_live_tv)

        miniPlayerView = findViewById(R.id.miniPlayerView)

        setupNavigation()
        initializeMiniPlayer()
    }

    private fun setupNavigation() {
        findViewById<TextView>(R.id.navHome).setOnClickListener {
            finish() // Ana ekrana döner
        }
    }

    private fun initializeMiniPlayer() {
        player = ExoPlayer.Builder(this).build().apply {
            miniPlayerView.player = this
            val mediaItem = MediaItem.fromUri(currentlyPlayingUrl)
            setMediaItem(mediaItem)
            prepare()
            playWhenReady = true
        }
    }

    // Kumandada OK tuşuna basıldığında çağrılan mantık
    fun onChannelClick(clickedChannelUrl: String) {
        if (currentlyPlayingUrl == clickedChannelUrl) {
            // Eğer aynı kanala tekrar OK basıldıysa TAM EKRAN yap
            val intent = Intent(this, FullscreenPlayerActivity::class.java).apply {
                putExtra("STREAM_URL", clickedChannelUrl)
            }
            startActivity(intent)
        } else {
            // İlk tıklamada sağdaki küçük oynatıcıda çalıştır
            currentlyPlayingUrl = clickedChannelUrl
            player?.setMediaItem(MediaItem.fromUri(currentlyPlayingUrl))
            player?.prepare()
            player?.playWhenReady = true
        }
    }

    override fun onStop() {
        super.onStop()
        player?.release()
        player = null
    }
}
