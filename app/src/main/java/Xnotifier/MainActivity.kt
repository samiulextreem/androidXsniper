package Xnotifier

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.notificationrelay.R

class MainActivity : AppCompatActivity() {

    private lateinit var notificationTextView: TextView
    private lateinit var permissionButton: Button

    private val notificationReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val title = intent?.getStringExtra("title")
            val text = intent?.getStringExtra("text")

            val currentText = notificationTextView.text.toString()
            notificationTextView.text = "Title: $title\nText: $text\n\n$currentText"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        notificationTextView = findViewById(R.id.notification_textview)
        permissionButton = findViewById(R.id.permission_button)

        permissionButton.setOnClickListener {
            startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
        }
    }

    override fun onResume() {
        super.onResume()
        if (!isNotificationServiceEnabled()) {
            permissionButton.visibility = Button.VISIBLE
            notificationTextView.text = "Please grant notification access to the app."
        } else {
            permissionButton.visibility = Button.GONE
            if (notificationTextView.text.toString() == "Please grant notification access to the app.") {
                notificationTextView.text = "Waiting for notifications..."
            }
            LocalBroadcastManager.getInstance(this).registerReceiver(
                notificationReceiver,
                IntentFilter("com.example.notificationrelay.NOTIFICATION_LISTENER")
            )
        }
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(notificationReceiver)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun isNotificationServiceEnabled(): Boolean {
        val pkgName = packageName
        val flat = Settings.Secure.getString(contentResolver, "enabled_notification_listeners")
        if (!TextUtils.isEmpty(flat)) {
            val names = flat.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            for (i in names.indices) {
                val cn = ComponentName.unflattenFromString(names[i])
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.packageName)) {
                        return true
                    }
                }
            }
        }
        return false
    }
}
