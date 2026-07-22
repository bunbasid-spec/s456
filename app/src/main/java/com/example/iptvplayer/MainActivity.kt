package com.example.iptvplayer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.ui.PlayerView

class MainActivity : AppCompatActivity() {

    private var player: ExoPlayer? = null
    private lateinit var playerView: PlayerView

    // Test için HLS (m3u8) canlı yayın adresi
    private val testStreamUrl = "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        playerView = findViewById(R.id.playerView)
        initializePlayer()
    }

    private fun initializePlayer() {
        // En yüksek video kalitesini zorlayan Track Selector
        val trackSelector = DefaultTrackSelector(this).apply {
            setParameters(
                buildUponParameters()
                    .setForceHighestSupportedBitrate(true) // Her zaman desteklenen en yüksek kaliteyi seç
            )
        }

        // Hızlı açılış için optimize edilmiş buffer ayarları
        val loadControl = DefaultLoadControl.Builder()
            .setBufferDurationsMs(
                1500,  // Min buffer (1.5 sn)
                8000,  // Max buffer (8 sn)
                1000,  // Oynatmaya başlamak için gereken buffer (1 sn)
                1500   // Re-buffer sonrası gereken süre (1.5 sn)
            )
            .build()

        player = ExoPlayer.Builder(this)
            .setTrackSelector(trackSelector)
            .setLoadControl(loadControl)
            .build()
            .apply {
                playerView.player = this
                val mediaItem = MediaItem.fromUri(testStreamUrl)
                setMediaItem(mediaItem)
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
