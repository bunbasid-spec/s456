package com.example.iptvplayer

import android.content.Context
import android.content.SharedPreferences

class FavoriteManager(context: Context) {

    // Enum yapısını sınıfın içine aldık (Böylece dosya ikonu MAVİ "C" kalacak)
    enum class FavoriteType {
        NONE, FAV_1, FAV_2, BOTH
    }

    private val prefs: SharedPreferences = context.getSharedPreferences("IPTV_PREFS", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_DUAL_MODE = "key_dual_favorite_mode"
        private const val KEY_FAV_1 = "key_fav_1_ids"
        private const val KEY_FAV_2 = "key_fav_2_ids"
    }

    // Ayarlardaki Çift Favori Modu Durumu
    var isDualMode: Boolean
        get() = prefs.getBoolean(KEY_DUAL_MODE, true)
        set(value) = prefs.edit().putBoolean(KEY_DUAL_MODE, value).apply()

    fun getFav1List(): Set<String> {
        return prefs.getStringSet(KEY_FAV_1, emptySet()) ?: emptySet()
    }

    fun getFav2List(): Set<String> {
        return prefs.getStringSet(KEY_FAV_2, emptySet()) ?: emptySet()
    }

    fun getFavoriteStatus(channelId: String): FavoriteType {
        val inFav1 = getFav1List().contains(channelId)
        val inFav2 = isDualMode && getFav2List().contains(channelId)

        return when {
            inFav1 && inFav2 -> FavoriteType.BOTH
            inFav1 -> FavoriteType.FAV_1
            inFav2 -> FavoriteType.FAV_2
            else -> FavoriteType.NONE
        }
    }

    fun toggleFavorite(channelId: String, favType: Int) {
        val key = if (favType == 1) KEY_FAV_1 else KEY_FAV_2
        val currentSet = prefs.getStringSet(key, emptySet())?.toMutableSet() ?: mutableSetOf()

        if (currentSet.contains(channelId)) {
            currentSet.remove(channelId)
        } else {
            currentSet.add(channelId)
        }

        prefs.edit().putStringSet(key, currentSet).apply()
    }
}