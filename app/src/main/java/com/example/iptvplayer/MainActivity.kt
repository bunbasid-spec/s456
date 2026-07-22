package com.example.iptvplayer

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var updateManager: UpdateManager
    private lateinit var currentVersion: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Versiyon numarasını Android sisteminden dinamik alıyoruz (Örn: 1.0.1)
        currentVersion = try {
            packageManager.getPackageInfo(packageName, 0).versionName ?: "1.0.1"
        } catch (e: Exception) {
            "1.0.1"
        }

        updateManager = UpdateManager(this)

        val tvVersion = findViewById<TextView>(R.id.tvVersion)
        tvVersion.text = "v$currentVersion"

        val btnLiveTv = findViewById<Button>(R.id.btnLiveTv)
        val btnMovies = findViewById<Button>(R.id.btnMovies)
        val btnSeries = findViewById<Button>(R.id.btnSeries)
        val btnAccount = findViewById<Button>(R.id.btnAccount)
        val btnUpdate = findViewById<Button>(R.id.btnUpdate)
        val btnSettings = findViewById<Button>(R.id.btnSettings)
        val btnExit = findViewById<Button>(R.id.btnExit)

        btnLiveTv.setOnClickListener {
            startActivity(Intent(this, LiveTvActivity::class.java))
        }

        btnUpdate.setOnClickListener {
            Toast.makeText(this, "Güncellemeler kontrol ediliyor...", Toast.LENGTH_SHORT).show()
            updateManager.checkForUpdates(currentVersion) { apkUrl ->
                runOnUiThread {
                    Toast.makeText(this, "Yeni sürüm indiriliyor...", Toast.LENGTH_LONG).show()
                    updateManager.downloadAndInstallApk(apkUrl)
                }
            }
        }

        btnExit.setOnClickListener {
            finishAffinity()
        }

        btnMovies.setOnClickListener { showToast("Movies modülü hazırlanıyor") }
        btnSeries.setOnClickListener { showToast("Series modülü hazırlanıyor") }
        btnAccount.setOnClickListener { showToast("Account ayarları hazırlanıyor") }
        btnSettings.setOnClickListener { showToast("Settings hazırlanıyor") }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
