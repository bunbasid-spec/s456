package com.example.iptvplayer

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tvVersion = findViewById<TextView>(R.id.tvVersion)
        try {
            val pInfo = packageManager.getPackageInfo(packageName, 0)
            tvVersion.text = "v${pInfo.versionName}"
        } catch (e: Exception) {
            tvVersion.text = "v1.0.4"
        }

        // Canlı TV Butonu
        findViewById<Button>(R.id.btnLiveTv)?.setOnClickListener {
            val intent = Intent(this, LiveActivity::class.java)
            startActivity(intent)
        }

        // Filmler Butonu
        findViewById<Button>(R.id.btnMovies)?.setOnClickListener {
            Toast.makeText(this, "Filmler modülü hazırlanıyor...", Toast.LENGTH_SHORT).show()
        }

        // Diziler Butonu
        findViewById<Button>(R.id.btnSeries)?.setOnClickListener {
            Toast.makeText(this, "Diziler modülü hazırlanıyor...", Toast.LENGTH_SHORT).show()
        }

        // Hesap Butonu
        findViewById<Button>(R.id.btnAccount)?.setOnClickListener {
            Toast.makeText(this, "Hesap bilgileri hazırlanıyor...", Toast.LENGTH_SHORT).show()
        }

        // Güncelleme Butonu
        findViewById<Button>(R.id.btnUpdate)?.setOnClickListener {
            Toast.makeText(this, "Güncellemeler kontrol ediliyor...", Toast.LENGTH_SHORT).show()
        }

        // Ayarlar Butonu
        findViewById<Button>(R.id.btnSettings)?.setOnClickListener {
            Toast.makeText(this, "Ayarlar hazırlanıyor...", Toast.LENGTH_SHORT).show()
        }

        // Çıkış Butonu
        findViewById<Button>(R.id.btnExit)?.setOnClickListener {
            finishAffinity()
        }
    }
}