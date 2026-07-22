package com.example.iptvplayer

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

class FullscreenPlayerActivity : AppCompatActivity() {

    private var player: ExoPlayer? = null
    private lateinit var osdContainer: LinearLayout
    private lateinit var osdSettingsMenu: LinearLayout
    private lateinit var layoutNavHints: RelativeLayout
    private lateinit var tvChannelTitle: TextView

    // Sabit Test Kanal Listesi
    private val channelList = listOf(
        "27 TR: TRT 1 HD" to "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8",
        "28 TR: TRT 1 FHD Ⓗ" to "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8",
        "29 TR: ATV HD" to "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8"
    )

    private var currentChannelIndex = 1
    private var lastChannelIndex = 0

    private val osdHandler = Handler(Looper.getMainLooper())
    private val hideOsdRunnable = Runnable { hideOsd() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fullscreen_player)

        val playerView = findViewById<PlayerView>(R.id.playerView)
        osdContainer = findViewById(R.id.osdContainer)
        osdSettingsMenu = findViewById(R.id.osdSettingsMenu)
        layoutNavHints = findViewById(R.id.layoutNavHints)
        tvChannelTitle = findViewById(R.id.tvChannelTitle)

        player = ExoPlayer.Builder(this).build().apply {
            playerView.player = this
        }

        playChannel(currentChannelIndex)
        showOsd()
    }

    private fun playChannel(index: Int) {
        if (index in channelList.indices) {
            currentChannelIndex = index
            val (title, url) = channelList[currentChannelIndex]
            tvChannelTitle.text = title

            player?.setMediaItem(MediaItem.fromUri(url))
            player?.prepare()
            player?.playWhenReady = true

            showOsd()
        }
    }

    // Kumanda Tuş Kontrolleri
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            // SAĞ TUŞ -> Sonraki Kanal
            KeyEvent.KEYCODE_DPAD_RIGHT -> {
                lastChannelIndex = currentChannelIndex
                val nextIndex = (currentChannelIndex + 1) % channelList.size
                playChannel(nextIndex)
                return true
            }
            // SOL TUŞ -> Önceki Kanal
            KeyEvent.KEYCODE_DPAD_LEFT -> {
                lastChannelIndex = currentChannelIndex
                val prevIndex = if (currentChannelIndex - 1 < 0) channelList.size - 1 else currentChannelIndex - 1
                playChannel(prevIndex)
                return true
            }
            // YUKARI TUŞ -> En Son Oynatılan 2 Kanal Arasında Git/Gel
            KeyEvent.KEYCODE_DPAD_UP -> {
                val temp = currentChannelIndex
                playChannel(lastChannelIndex)
                lastChannelIndex = temp
                return true
            }
            // AŞAĞI TUŞ -> OSD Aç / Ayar Menüsünü Göster
            KeyEvent.KEYCODE_DPAD_DOWN -> {
                if (osdContainer.visibility != View.VISIBLE) {
                    showOsd()
                } else if (osdSettingsMenu.visibility != View.VISIBLE) {
                    // 2. kez basıldığında alt hızlı ayarları aç
                    osdSettingsMenu.visibility = View.VISIBLE
                    layoutNavHints.visibility = View.GONE
                    resetOsdTimer()
                }
                return true
            }
            // OK / BACK TUŞLARI -> OSD Kapat
            KeyEvent.KEYCODE_BACK -> {
                if (osdContainer.visibility == View.VISIBLE) {
                    hideOsd()
                    return true
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun showOsd() {
        osdContainer.visibility = View.VISIBLE
        osdSettingsMenu.visibility = View.GONE
        layoutNavHints.visibility = View.VISIBLE
        resetOsdTimer()
    }

    private fun hideOsd() {
        osdContainer.visibility = View.GONE
        osdHandler.removeCallbacks(hideOsdRunnable)
    }

    private fun resetOsdTimer() {
        osdHandler.removeCallbacks(hideOsdRunnable)
        osdHandler.postDelayed(hideOsdRunnable, 5000) // 5 Saniye sonra kapat
    }

    override fun onStop() {
        super.onStop()
        osdHandler.removeCallbacks(hideOsdRunnable)
        player?.release()
        player = null
    }
}
