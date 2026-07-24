package com.example.iptvplayer

import android.os.Handler
import android.os.Looper
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

    private val handler = Handler(Looper.getMainLooper())

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

        // Fare / Dokunmatik Tıklama
        holder.itemView.setOnClickListener {
            onItemClick(title)
        }

        // Fare / Dokunmatik Basılı Tutma
        holder.itemView.setOnLongClickListener {
            onItemLongClick?.invoke(title)
            true
        }

        // TV Kumandası Zamanlayıcı Tabanlı Özel Tuş Yönetimi
        var longPressRunnable: Runnable? = null
        var isLongPressHandled = false

        holder.itemView.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER) {

                // TUŞA BASILDIĞINDA (ACTION_DOWN)
                if (event.action == KeyEvent.ACTION_DOWN) {
                    if (event.repeatCount == 0) {
                        isLongPressHandled = false

                        // 600 ms bekleyecek zamanlayıcıyı kuruyoruz
                        longPressRunnable = Runnable {
                            isLongPressHandled = true
                            onItemLongClick?.invoke(title)
                        }
                        handler.postDelayed(longPressRunnable!!, 600)
                    }
                    return@setOnKeyListener true
                }

                // TUŞ BIRAKILDIĞINDA (ACTION_UP)
                else if (event.action == KeyEvent.ACTION_UP) {
                    // Zamanlayıcıyı iptal et
                    longPressRunnable?.let { handler.removeCallbacks(it) }

                    // Eğer uzun basma henüz tetiklenmediyse, kısa basış olarak kabul et ve kanalı aç
                    if (!isLongPressHandled) {
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