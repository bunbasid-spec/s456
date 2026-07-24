package com.example.iptvplayer

import android.app.AlertDialog
import android.os.Bundle
import android.view.KeyEvent
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView

class LiveActivity : AppCompatActivity() {

    private lateinit var favoriteManager: FavoriteManager
    private lateinit var channelAdapter: LiveRowAdapter
    private lateinit var categoryAdapter: LiveRowAdapter

    // Test Kanalları (Mevcut listen korundu)
    private val dummyChannels = listOf(
        "1 TV  TR: TRT 1 HD M", "2 TV  TR: TRT 1 HD R", "3 TV  TR: TRT 1 FHD R",
        "4 TV  TR: TRT SPOR HD+", "5 TV  TR: TRT SPOR HD", "6 TV  DE: ZDF HD*",
        "7 TV  DE: Das Erste HD*", "8 TV  DE: Das Erste* HD", "9 TV  DE: Sportdigital",
        "10 TV  DEMG: Fussball.TV 1*", "11 TV  DEMG: FUSSBALL.TV 1"
    )

    private val channelHistory = mutableListOf<String>()
    private var currentSelectedCategory = "ALL"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_live)

        favoriteManager = FavoriteManager(this)

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

        // 1. Kanal Adaptörünün Hazırlanması
        channelAdapter = LiveRowAdapter(
            items = dummyChannels,
            favoriteManager = favoriteManager,
            onItemClick = { channel ->
                playChannel(channel)
            },
            onItemLongClick = { channel ->
                handleFavoriteRequest(channel)
            }
        )
        rvChannels.adapter = channelAdapter

        // 2. Dinamik Kategori Listesinin Hazırlanması
        val categories = buildCategoriesList()
        categoryAdapter = LiveRowAdapter(
            items = categories,
            favoriteManager = null,
            onItemClick = { category ->
                onCategorySelected(category)
            }
        )
        rvCategories.adapter = categoryAdapter
    }

    // En Üst Özel Kategorilerin Dinamik Oluşturulması
    private fun buildCategoriesList(): List<String> {
        val categories = mutableListOf<String>()

        // 1. En Üst: ALL
        categories.add("ALL")

        // 2. Ayarlardaki Moda Göre FAVORITE 1 ve FAVORITE 2 (veya TEK FAVORITE)
        if (favoriteManager.isDualMode) {
            categories.add("FAVORITE 1")
            categories.add("FAVORITE 2")
        } else {
            categories.add("FAVORITE")
        }

        // 3. CHANNEL HISTORY
        categories.add("CHANNEL HISTORY")

        // 4. Mevcut Test Kategorilerin (Aynen korundu)
        categories.addAll(
            listOf(
                "2026 FIFA World Cup (25)", "TR: ULUSAL (41)", "TR: B-SPORTS (30)",
                "TR: EX-XEN SPOR (26)", "TR: TABII SPOR (29)", "TR: TIVIBU SPOR (9)",
                "TR: S-SPORT (18)", "TR: D SMARTSPOR (7)", "TR: HABERLER (48)",
                "TR: CANLI BELGESEL (33)", "TR: COCUK LIVE (77)"
            )
        )

        return categories
    }

    // Kategori Seçildiğinde Kanal Listesini Filtreleme
    private fun onCategorySelected(category: String) {
        currentSelectedCategory = category
        val filteredList = when (category) {
            "ALL" -> dummyChannels
            "FAVORITE 1", "FAVORITE" -> {
                val fav1Ids = favoriteManager.getFav1List()
                dummyChannels.filter { fav1Ids.contains(it) }
            }
            "FAVORITE 2" -> {
                val fav2Ids = favoriteManager.getFav2List()
                dummyChannels.filter { fav2Ids.contains(it) }
            }
            "CHANNEL HISTORY" -> channelHistory
            else -> dummyChannels
        }

        channelAdapter.updateData(filteredList)
    }

    // Kumandadan Basılı Tutulunca Veya İkona Basılınca Çalışacak Favori İşleyicisi
    private fun handleFavoriteRequest(channel: String) {
        if (!favoriteManager.isDualMode) {
            // Tek Favori Modu: Doğrudan Ekle / Çıkar
            favoriteManager.toggleFavorite(channel, 1)
            refreshCurrentList()
            Toast.makeText(this, "$channel favori durumu güncellendi", Toast.LENGTH_SHORT).show()
        } else {
            // Çift Favori Modu: Ekrana Şık Dialog Çıkar
            showFavoriteSelectionDialog(channel)
        }
    }

    private fun showFavoriteSelectionDialog(channel: String) {
        val inFav1 = favoriteManager.getFav1List().contains(channel)
        val inFav2 = favoriteManager.getFav2List().contains(channel)

        val option1 = if (inFav1) "FAVORITE 1 Listesinden Çıkar" else "FAVORITE 1 Listesine Ekle"
        val option2 = if (inFav2) "FAVORITE 2 Listesinden Çıkar" else "FAVORITE 2 Listesine Ekle"

        val options = arrayOf(option1, option2)

        AlertDialog.Builder(this)
            .setTitle("$channel - Favori Seçimi")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> favoriteManager.toggleFavorite(channel, 1)
                    1 -> favoriteManager.toggleFavorite(channel, 2)
                }
                refreshCurrentList()
            }
            .setNegativeButton("Kapat", null)
            .show()
    }

    private fun refreshCurrentList() {
        onCategorySelected(currentSelectedCategory)
    }

    private fun playChannel(channel: String) {
        if (!channelHistory.contains(channel)) {
            channelHistory.add(0, channel)
        }
        Toast.makeText(this, "$channel oynatılıyor...", Toast.LENGTH_SHORT).show()
    }
}