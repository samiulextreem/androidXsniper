package Xnotifier

import android.content.Intent
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class NotificationCaptureService : NotificationListenerService() {

    private var lastNotificationText: String? = null

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val packageName = sbn.packageName
        Log.d("NotificationCapture", "Notification captured from package: $packageName")

        if (packageName == "com.twitter.android") {
            val notification = sbn.notification
            val extras = notification.extras
            val title = extras.getString("android.title")
            val text = extras.getCharSequence("android.text")?.toString()

            // Filter for notifications from Elon Musk
            if (title != null && title.contains("Elon Musk", ignoreCase = true)) {
                // If the new notification text is the same as the last one, ignore it.
                if (text != null && text == lastNotificationText) {
                    Log.d("NotificationCapture", "Duplicate notification detected. Ignoring.")
                    return
                }

                lastNotificationText = text

                Log.d("NotificationCapture", "Elon Musk notification captured:")
                Log.d("NotificationCapture", "Title: $title")
                Log.d("NotificationCapture", "Text: $text")

                // Send a broadcast to MainActivity
                val intent = Intent("com.example.notificationrelay.NOTIFICATION_LISTENER")
                intent.putExtra("title", title)
                intent.putExtra("text", text)
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent)

                // Send the notification text to the server
                if (text != null) {
                    sendPayload(text)
                }
            }
        }
    }

    private fun sendPayload(payload: String) {
        Thread {
            try {
                val encodedPayload = URLEncoder.encode(payload, "UTF-8")
                val url = URL("https://unmethodized-eryn-unprocured.ngrok-free.dev/ping?text=$encodedPayload")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connect()

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    Log.d("NotificationCapture", "Successfully sent payload to server")
                } else {
                    Log.e("NotificationCapture", "Failed to send payload. Response code: $responseCode")
                }
            } catch (e: IOException) {
                Log.e("NotificationCapture", "Error sending payload: ${e.message}")
            }
        }.start()
    }
}
