package com.example.iptvplayer

import android.app.AlertDialog
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SettingsActivity : AppCompatActivity() {

    private lateinit var favoriteManager: FavoriteManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        favoriteManager = FavoriteManager(this)

        // Üst Navigasyon - Geri Butonu
        val btnBack = findViewById<TextView>(R.id.btnBack)
        btnBack?.setOnClickListener {
            finish()
        }

        val rvSettings = findViewById<RecyclerView>(R.id.rvSettings)

        // 3 Sütunlu Grid Düzeni (12 Kart / 3 = 4 Satır Düzeni)
        rvSettings.layoutManager = GridLayoutManager(this, 3)

        // 12 Adet Ayar Kartı
        val settingsList = listOf(
            SettingsAdapter.SettingItem("parental", "Ebeveyn Kontrolü", android.R.drawable.ic_lock_lock),
            SettingsAdapter.SettingItem("favorites", "Favori Ayarları", android.R.drawable.btn_star_big_on),
            SettingsAdapter.SettingItem("playlist", "Çalma Listesini Değiştir", android.R.drawable.ic_menu_agenda),
            SettingsAdapter.SettingItem("language", "Dil Değiştir", android.R.drawable.ic_menu_sort_alphabetically),
            SettingsAdapter.SettingItem("hide_live", "Live Gizle", android.R.drawable.ic_menu_close_clear_cancel),
            SettingsAdapter.SettingItem("hide_movies", "Movies Gizle", android.R.drawable.ic_menu_close_clear_cancel),
            SettingsAdapter.SettingItem("hide_series", "Series Gizle", android.R.drawable.ic_menu_close_clear_cancel),
            SettingsAdapter.SettingItem("clear_channels", "Geçmiş Kanalları Temizle", android.R.drawable.ic_menu_delete),
            SettingsAdapter.SettingItem("clear_movies", "Film Geçmişini Temizle", android.R.drawable.ic_menu_delete),
            SettingsAdapter.SettingItem("clear_series", "Dizi Geçmişini Temizle", android.R.drawable.ic_menu_delete),
            SettingsAdapter.SettingItem("subtitles", "Altyazı Ayarları", android.R.drawable.ic_menu_manage),
            SettingsAdapter.SettingItem("theme", "Thema Ayarları", android.R.drawable.ic_menu_gallery)
        )

        rvSettings.adapter = SettingsAdapter(settingsList) { item ->
            onSettingClicked(item)
        }
    }

    private fun onSettingClicked(item: SettingsAdapter.SettingItem) {
        when (item.id) {
            "favorites" -> showFavoriteSettingsDialog()
            "theme" -> Toast.makeText(this, "Tema ayarları yakında eklenecek", Toast.LENGTH_SHORT).show()
            else -> Toast.makeText(this, "${item.title} seçildi", Toast.LENGTH_SHORT).show()
        }
    }

    // Favori Modunu (Tek Favori / Çift Favori) Değiştirme Diyaloğu
    private fun showFavoriteSettingsDialog() {
        val currentModeText = if (favoriteManager.isDualMode) "Çift Favori Modu (FAV 1 / FAV 2)" else "Tek Favori Modu (FAV)"
        val options = arrayOf("Tek Favori Modu", "Çift Favori Modu (FAV 1 / FAV 2)")

        AlertDialog.Builder(this)
            .setTitle("Favori Ayarları (Mevcut: $currentModeText)")
            .setSingleChoiceItems(options, if (favoriteManager.isDualMode) 1 else 0) { dialog, which ->
                favoriteManager.isDualMode = (which == 1)
                val selectedText = if (which == 1) "Çift Favori Modu Aktif Edildi" else "Tek Favori Modu Aktif Edildi"
                Toast.makeText(this, selectedText, Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .setNegativeButton("Kapat", null)
            .show()
    }
}