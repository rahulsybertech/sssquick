package com.ssspvtltd.quick_app.utils

import android.app.Activity
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.util.Log
import android.widget.Toast
import java.io.File

class DownloadCompleteReceiver(
    private val downloadId: Long,
    private val fileName: String,
    private val context: Context,
    private val downloadManager: DownloadManager,
    private val openPdf: (File) -> Unit
) : BroadcastReceiver() {
    private var isDownloadComplete: Boolean = false
    override fun onReceive(context: Context, intent: Intent) {
        try {
            val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            Log.i("TaG", "======= check id ===> $id")

            if ((downloadId == id)) {
                // Unregister receiver after download completes
                context.unregisterReceiver(this)

                val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName)
                Log.i("TaG", "======= check file exists ===> ${file.exists()}")
                downloadManager.remove(downloadId)
                isDownloadComplete = true
                if (file.exists()) {
                    openPdf(file)  // Open the downloaded PDF
                } else {
                    Log.e("DownloadError", "File does not exist after download.")
                    Toast.makeText(context, "Failed to download file", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("BroadcastReceiverError", "Error in onDownloadComplete: ${e.message}")
            if (isActivityAdded(context)) {
                Toast.makeText(context, "Error opening PDF", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isActivityAdded(context: Context): Boolean {
        return context is Activity && !context.isFinishing
    }
}
