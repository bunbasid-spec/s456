package com.example.iptvplayer

import android.os.Bundle
import android.view.KeyEvent
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView

class LiveActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_live)

        // Üst Navigasyon - Home Tıklanınca Ana Ekrana Dönüş
        val navHome = findViewById<TextView>(R.id.navHome)
        navHome?.setOnClickListener {
            finish()
        }

        val etGlobalSearch = findViewById<EditText>(R.id.etGlobalSearch)
        val rvCategories = findViewById<RecyclerView>(R.id.rvCategories)
        val rvChannels = findViewById<RecyclerView>(R.id.rvChannels)

        // Ortadan Odaklı Kaydırma Motoru
        rvCategories.layoutManager = FocusCenteredLinearLayoutManager(this)
        rvChannels.layoutManager = FocusCenteredLinearLayoutManager(this)

        // KANAL LİSTESİ EN ÜSTTEYKEN YUKARI BASILDIĞINDA DOĞRUDAN SEARCH KUTUSUNA ODAKLAN
        rvChannels.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                val layoutManager = rvChannels.layoutManager as? FocusCenteredLinearLayoutManager
                if (layoutManager?.findFirstCompletelyVisibleItemPosition() == 0 ||
                    layoutManager?.findFirstVisibleItemPosition() == 0) {
                    etGlobalSearch?.requestFocus()
                    return@setOnKeyListener true
                }
            }
            false
        }

        // Test Kategorileri
        val dummyCategories = listOf(
            "Son Görüntülenenler (0)", "Tümü (7560)", "Favori (0)", "2026 FIFA World Cup (25)",
            "TR: ULUSAL (41)", "TR: B-SPORTS (30)", "TR: EX-XEN SPOR (26)", "TR: TABII SPOR (29)",
            "TR: TIVIBU SPOR (9)", "TR: S-SPORT (18)", "TR: D SMARTSPOR (7)", "TR: HABERLER (48)",
            "TR: CANLI BELGESEL (33)", "TR: COCUK LIVE (77)"
        )

        // Test Kanalları
        val dummyChannels = listOf(
            "1 TV  TR: TRT 1 HD M", "2 TV  TR: TRT 1 HD R", "3 TV  TR: TRT 1 FHD R",
            "4 TV  TR: TRT SPOR HD+", "5 TV  TR: TRT SPOR HD", "6 TV  DE: ZDF HD*",
            "7 TV  DE: Das Erste HD*", "8 TV  DE: Das Erste* HD", "9 TV  DE: Sportdigital",
            "10 TV  DEMG: Fussball.TV 1*", "11 TV  DEMG: FUSSBALL.TV 1"
        )

        rvCategories.adapter = LiveRowAdapter(dummyCategories) { }
        rvChannels.adapter = LiveRowAdapter(dummyChannels) { }
    }
}