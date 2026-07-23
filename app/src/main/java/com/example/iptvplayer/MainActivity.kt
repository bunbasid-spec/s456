package com.example.iptvplayer

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Home ekranındaki Live/Canlı TV butonu (Varsayılan layout buton ID'sine göre düzenlendi)
        val btnLive = findViewById<Button>(R.id.btnLive)
        btnLive?.setOnClickListener {
            val intent = Intent(this, LiveActivity::class.java)
            startActivity(intent)
        }
    }
}
