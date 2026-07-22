package com.example.iptvplayer

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import org.json.JSONObject
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

class UpdateManager(private val context: Context) {

    // Kendi GitHub kullanıcı adını ve repo adını buraya yazabilirsin
    private val githubApiUrl = "https://api.github.com/repos/bunbasid-spec/s456/releases/latest"

    fun checkForUpdates(currentVersion: String, onUpdateAvailable: (apkUrl: String) -> Unit) {
        thread {
            try {
                val url = URL(githubApiUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.setRequestProperty("Accept", "application/vnd.github.v3+json")

                if (connection.responseCode == 200) {
                    val response = connection.inputStream.bufferedReader().readText()
                    val json = JSONObject(response)
                    val latestVersion = json.getString("tag_name").replace("v", "")

                    if (isNewerVersion(currentVersion, latestVersion)) {
                        val assets = json.getJSONArray("assets")
                        if (assets.length() > 0) {
                            val downloadUrl = assets.getJSONObject(0).getString("browser_download_url")
                            onUpdateAvailable(downloadUrl)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun isNewerVersion(current: String, latest: String): Boolean {
        // Basit versiyon karşılaştırması (Örn: 1.0.0 < 1.0.1)
        return latest != current
    }

    fun downloadAndInstallApk(apkUrl: String) {
        thread {
            try {
                val url = URL(apkUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.connect()

                val file = File(context.externalCacheDir, "update.apk")
                url.openStream().use { input ->
                    file.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }

                installApk(file)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun installApk(apkFile: File) {
        val intent = Intent(Intent.ACTION_VIEW)
        val apkUri: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(context, "${context.packageName}.provider", apkFile)
        } else {
            Uri.fromFile(apkFile)
        }

        intent.setDataAndType(apkUri, "application/vnd.android.package-archive")
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
        context.startActivity(intent)
    }
}
