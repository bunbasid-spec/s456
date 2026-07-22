package com.example.iptvplayer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView

class LiveActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_live)

        val rvCategories = findViewById<RecyclerView>(R.id.rvCategories)
        val rvChannels = findViewById<RecyclerView>(R.id.rvChannels)

        // 1. Ortadan Odaklı Kaydırma Mantıklarını Atıyoruz
        rvCategories.layoutManager = FocusCenteredLinearLayoutManager(this)
        rvChannels.layoutManager = FocusCenteredLinearLayoutManager(this)

        // 2. Test İçin Örnek Veriler
        val dummyCategories = listOf("Ulusal", "Spor", "Sinema", "Belgesel", "Haber", "Çocuk", "Müzik", "Eğlence", "Dizi", "Uluslararası", "Yerel")
        val dummyChannels = listOf("TRT 1 HD", "ATV HD", "Kanal D HD", "Show TV HD", "Star TV HD", "TV8 HD", "NOW TV HD", "Kanal 7 HD", "A Haber HD", "TRT Spor HD", "BeIN Sports 1", "S Sport HD", "National Geographic")

        // 3. Adapter'ları Bağlıyoruz
        rvCategories.adapter = LiveRowAdapter(dummyCategories) { selectedCategory ->
            // Kategoriye tıklandığında yapılacak işlem
        }

        rvChannels.adapter = LiveRowAdapter(dummyChannels) { selectedChannel ->
            // Kanala tıklandığında (Tam ekran yapma vb.)
        }
    }
}
