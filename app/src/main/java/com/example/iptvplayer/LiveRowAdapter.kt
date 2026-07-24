package com.example.iptvplayer

import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class LiveRowAdapter(
    private var items: List<String>,
    private val favoriteManager: FavoriteManager? = null,
    private val onItemClick: (String) -> Unit,
    private val onItemLongClick: ((String) -> Unit)? = null
) : RecyclerView.Adapter<LiveRowAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tvRowTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_live_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val title = items[position]

        // Favori durumuna göre yıldız rozeti gösterimi (★1, ★2, ★1+2)
        var displayText = title
        if (favoriteManager != null) {
            val status = favoriteManager.getFavoriteStatus(title)
            when (status) {
                FavoriteManager.FavoriteType.FAV_1 -> displayText = "$title   ★1"
                FavoriteManager.FavoriteType.FAV_2 -> displayText = "$title   ★2"
                FavoriteManager.FavoriteType.BOTH -> displayText = "$title   ★1+2"
                FavoriteManager.FavoriteType.NONE -> { }
            }
        }

        holder.tvTitle.text = displayText

        // Normal Fare / Dokunmatik Tıklama
        holder.itemView.setOnClickListener {
            onItemClick(title)
        }

        // Normal Fare / Dokunmatik Basılı Tutma
        holder.itemView.setOnLongClickListener {
            onItemLongClick?.invoke(title)
            true
        }

        // TV Kumandası Özel Tuş Yönetimi (OK Tuşu Basılma Süresi Kontrolü)
        var keyDownTime = 0L

        holder.itemView.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER) {
                if (event.action == KeyEvent.ACTION_DOWN) {
                    if (event.repeatCount == 0) {
                        keyDownTime = System.currentTimeMillis()
                    } else if (System.currentTimeMillis() - keyDownTime >= 500) {
                        // 0.5 saniyeden uzun basıldıysa Favori Ekranı açılır
                        onItemLongClick?.invoke(title)
                        keyDownTime = Long.MAX_VALUE // Tekrar tetiklenmeyi engelle
                        return@setOnKeyListener true
                    }
                } else if (event.action == KeyEvent.ACTION_UP) {
                    val pressDuration = System.currentTimeMillis() - keyDownTime
                    if (pressDuration < 500) {
                        // Kısa basıldıysa normal tıklama (Kanal Oynat)
                        onItemClick(title)
                    }
                    return@setOnKeyListener true
                }
            }

            // Kumanda Sınırlamaları (Listenin en altındayken AŞAĞI basılırsa odağı koru)
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                if (position == items.size - 1) {
                    return@setOnKeyListener true
                }
            }
            false
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<String>) {
        this.items = newItems
        notifyDataSetChanged()
    }
}