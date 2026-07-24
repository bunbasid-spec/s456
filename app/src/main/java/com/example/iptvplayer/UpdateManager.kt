package com.example.iptvplayer

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.core.content.FileProvider
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

class UpdateManager(private val context: Context) {

    private val githubUser = "bunbasid-spec"
    private val repoName = "s456"
    private val apiUrl = "https://api.github.com/repos/$githubUser/$repoName/releases/latest"

    fun checkForUpdates(silent: Boolean = false) {
        thread {
            try {
                val url = URL(apiUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.setRequestProperty("Accept", "application/vnd.github.v3+json")
                connection.connectTimeout = 5000
                connection.readTimeout = 5000

                if (connection.responseCode == 200) {
                    val response = connection.inputStream.bufferedReader().use { it.readText() }
                    val json = JSONObject(response)

                    val latestVersion = json.getString("tag_name").replace("v", "").trim()
                    val currentVersion = context.packageManager
                        .getPackageInfo(context.packageName, 0).versionName.trim()

                    val assets = json.getJSONArray("assets")
                    var downloadUrl = ""
                    for (i in 0 until assets.length()) {
                        val asset = assets.getJSONObject(i)
                        if (asset.getString("name").endsWith(".apk")) {
                            downloadUrl = asset.getString("browser_download_url")
                            break
                        }
                    }

                    if (isNewerVersion(currentVersion, latestVersion) && downloadUrl.isNotEmpty()) {
                        (context as? androidx.appcompat.app.AppCompatActivity)?.runOnUiThread {
                            showUpdateDialog(latestVersion, downloadUrl)
                        }
                    } else if (!silent) {
                        (context as? androidx.appcompat.app.AppCompatActivity)?.runOnUiThread {
                            Toast.makeText(context, "Uygulama güncel (v$currentVersion)", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun isNewerVersion(current: String, latest: String): Boolean {
        val currentParts = current.split(".").mapNotNull { it.toIntOrNull() }
        val latestParts = latest.split(".").mapNotNull { it.toIntOrNull() }

        val length = maxOf(currentParts.size, latestParts.size)
        for (i in 0 until length) {
            val curr = currentParts.getOrElse(i) { 0 }
            val lat = latestParts.getOrElse(i) { 0 }
            if (lat > curr) return true
            if (lat < curr) return false
        }
        return false
    }

    private fun showUpdateDialog(newVersion: String, downloadUrl: String) {
        AlertDialog.Builder(context)
            .setTitle("Yeni Güncelleme Mevcut!")
            .setMessage("Yeni bir sürüm bulundu (v$newVersion). Şimdi güncellemek ister misiniz?")
            .setPositiveButton("Güncelle") { _, _ ->
                checkInstallPermissionAndDownload(downloadUrl)
            }
            .setNegativeButton("Daha Sonra", null)
            .show()
    }

    private fun checkInstallPermissionAndDownload(downloadUrl: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!context.packageManager.canRequestPackageInstalls()) {
                Toast.makeText(context, "Lütfen bilinmeyen kaynaklardan yüklemeye izin verin.", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).apply {
                    data = Uri.parse("package:${context.packageName}")
                }
                context.startActivity(intent)
                return
            }
        }
        downloadAndInstallApk(downloadUrl)
    }

    private fun downloadAndInstallApk(downloadUrl: String) {
        Toast.makeText(context, "Güncelleme indiriliyor...", Toast.LENGTH_SHORT).show()

        thread {
            try {
                val url = URL(downloadUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.connect()

                // Dosyayı doğrudan cache klasörüne indiriyoruz (Fire OS uyumu için)
                val file = File(context.cacheDir, "update.apk")
                if (file.exists()) file.delete()

                val inputStream = connection.inputStream
                val outputStream = FileOutputStream(file)

                val buffer = ByteArray(4096)
                var bytesRead: Int
                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                    outputStream.write(buffer, 0, bytesRead)
                }

                outputStream.close()
                inputStream.close()

                (context as? androidx.appcompat.app.AppCompatActivity)?.runOnUiThread {
                    installApk(file)
                }

            } catch (e: Exception) {
                e.printStackTrace()
                (context as? androidx.appcompat.app.AppCompatActivity)?.runOnUiThread {
                    Toast.makeText(context, "İndirme başarısız oldu!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun installApk(file: File) {
        try {
            val apkUri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(apkUri, "application/vnd.android.package-archive")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
            }

            // Paket yükleyiciye açık izin veriyoruz
            val resInfoList = context.packageManager.queryIntentActivities(intent, android.content.pm.PackageManager.MATCH_DEFAULT_ONLY)
            for (resolveInfo in resInfoList) {
                val packageName = resolveInfo.activityInfo.packageName
                context.grantUriPermission(packageName, apkUri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Kurulum hatası: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }
}