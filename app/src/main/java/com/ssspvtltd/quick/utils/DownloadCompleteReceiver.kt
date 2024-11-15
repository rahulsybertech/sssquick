package com.ssspvtltd.quick.utils

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import java.io.File

import java.lang.ref.WeakReference

class DownloadCompleteReceiver(
    private val downloadId: Long,
    private val fileName: String,
    private val fragmentReference: WeakReference<Fragment>,
    private val downloadManager: DownloadManager,
    private val openPdf: (File) -> Unit
) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        try {
            val fragment = fragmentReference.get() // Get a reference to the fragment

            // Check if the fragment is still attached
            if (fragment == null || !fragment.isAdded) {
                Log.e("BroadcastReceiverError", "Fragment is not attached to activity.")
                return
            }

            val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            Log.i("TaG", "======= check id ===> $id")

            if (downloadId == id) {
                // Unregister the receiver after download completes
                fragment.requireContext().unregisterReceiver(this)

                // Query the DownloadManager for the download status
                val query = DownloadManager.Query().setFilterById(downloadId)
                val cursor = downloadManager.query(query)

                if (cursor != null && cursor.moveToFirst()) {
                    val status = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS))
                    cursor.close()

                    if (status == DownloadManager.STATUS_SUCCESSFUL) {
                        val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName)
                        Log.i("TaG", "======= check file ===> ${file.exists()}")
                        if (file.exists()) {
                            openPdf(file) // Open the downloaded PDF
                        } else {
                            Log.e("DownloadError", "File does not exist after download.")
                            Toast.makeText(fragment.requireContext(), "Failed to download file", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Log.e("DownloadError", "Download failed with status: $status")
                        Toast.makeText(fragment.requireContext(), "Download failed", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e("DownloadError", "Cursor is null or empty")
                    Toast.makeText(fragment.requireContext(), "Failed to retrieve download status", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("BroadcastReceiverError", "Error in onDownloadComplete: ${e.message}")
            fragmentReference.get()?.let {
                if (it.isAdded) {
                    Toast.makeText(it.requireContext(), "Error opening PDF", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
