package com.example.iptvplayer

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var updateManager: UpdateManager
    private val currentVersion = "1.0.0" // Uygulamanın güncel versiyonu

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        updateManager = UpdateManager(this)

        // Versiyon Bilgisini Sağ Alt Köşeye Yazdır
        val tvVersion = findViewById<TextView>(R.id.tvVersion)
        tvVersion.text = "v$currentVersion"

        // Buton Tanımlamaları
        val btnLiveTv = findViewById<Button>(R.id.btnLiveTv)
        val btnMovies = findViewById<Button>(R.id.btnMovies)
        val btnSeries = findViewById<Button>(R.id.btnSeries)
        val btnAccount = findViewById<Button>(R.id.btnAccount)
        val btnUpdate = findViewById<Button>(R.id.btnUpdate)
        val btnSettings = findViewById<Button>(R.id.btnSettings)
        val btnExit = findViewById<Button>(R.id.btnExit)

        // Güncelleme Butonu Tıklaması
        btnUpdate.setOnClickListener {
            Toast.makeText(this, "Güncellemeler kontrol ediliyor...", Toast.LENGTH_SHORT).show()
            updateManager.checkForUpdates(currentVersion) { apkUrl ->
                runOnUiThread {
                    Toast.makeText(this, "Yeni sürüm indiriliyor...", Toast.LENGTH_LONG).show()
                    updateManager.downloadAndInstallApk(apkUrl)
                }
            }
        }

        // Çıkış Butonu
        btnExit.setOnClickListener {
            finishAffinity() // Uygulamayı tamamen kapatır
        }

        // Şimdilik boş olan buton bildirimleri
        btnLiveTv.setOnClickListener { showToast("Canlı TV modülü hazırlanıyor") }
        btnMovies.setOnClickListener { showToast("Filmler modülü hazırlanıyor") }
        btnSeries.setOnClickListener { showToast("Diziler modülü hazırlanıyor") }
        btnAccount.setOnClickListener { showToast("Hesap ayarları hazırlanıyor") }
        btnSettings.setOnClickListener { showToast("Ayarlar hazırlanıyor") }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
