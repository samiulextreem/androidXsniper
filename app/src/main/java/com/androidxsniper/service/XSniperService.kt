package com.androidxsniper.service

import android.app.*
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.androidxsniper.MainActivity
import com.androidxsniper.R
import com.androidxsniper.model.XPost
import com.androidxsniper.network.ServerRelayClient
import com.androidxsniper.network.XApiClient
import com.androidxsniper.utils.ConfigManager
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit

class XSniperService : Service() {
    
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var monitoringJob: Job? = null
    private lateinit var configManager: ConfigManager
    
    companion object {
        private const val TAG = "XSniperService"
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "XSniperChannel"
        private const val POLL_INTERVAL_MS = 60000L // 1 minute
        
        const val ACTION_START = "com.androidxsniper.ACTION_START"
        const val ACTION_STOP = "com.androidxsniper.ACTION_STOP"
    }
    
    override fun onCreate() {
        super.onCreate()
        configManager = ConfigManager(this)
        createNotificationChannel()
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> startMonitoring()
            ACTION_STOP -> stopMonitoring()
        }
        return START_STICKY
    }
    
    override fun onBind(intent: Intent?): IBinder? = null
    
    private fun startMonitoring() {
        if (!configManager.isConfigured()) {
            Log.e(TAG, "Service not configured properly")
            stopSelf()
            return
        }
        
        val notification = createNotification("Initializing...")
        startForeground(NOTIFICATION_ID, notification)
        
        monitoringJob?.cancel()
        monitoringJob = serviceScope.launch {
            while (isActive) {
                try {
                    checkForNewPosts()
                } catch (e: Exception) {
                    Log.e(TAG, "Error checking posts", e)
                }
                delay(POLL_INTERVAL_MS)
            }
        }
        
        updateNotification("Monitoring ${configManager.targetUsername}")
    }
    
    private fun stopMonitoring() {
        monitoringJob?.cancel()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }
    
    private suspend fun checkForNewPosts() {
        val bearerToken = configManager.bearerToken ?: return
        val username = configManager.targetUsername ?: return
        val serverUrl = configManager.serverUrl ?: return
        
        Log.d(TAG, "Checking for new posts from $username")
        
        val apiClient = XApiClient(bearerToken)
        val result = apiClient.getUserTweets(username, 5)
        
        result.onSuccess { response ->
            val posts = response.data
            val user = response.includes?.users?.firstOrNull()
            
            if (posts != null && posts.isNotEmpty()) {
                val lastPostId = configManager.lastPostId
                val newPosts = if (lastPostId != null) {
                    posts.takeWhile { it.id != lastPostId }
                } else {
                    listOf(posts.first()) // First run, only take the most recent
                }
                
                if (newPosts.isNotEmpty()) {
                    // Update last post ID
                    configManager.lastPostId = posts.first().id
                    
                    // Relay new posts
                    for (postData in newPosts) {
                        val post = XPost(
                            id = postData.id,
                            text = postData.text,
                            authorId = postData.author_id,
                            authorUsername = user?.username ?: username,
                            createdAt = postData.created_at,
                            url = "https://twitter.com/${user?.username ?: username}/status/${postData.id}"
                        )
                        
                        relayPost(post, serverUrl)
                    }
                    
                    showPostNotification(newPosts.size)
                }
            }
        }
        
        result.onFailure { error ->
            Log.e(TAG, "Failed to fetch posts", error)
        }
    }
    
    private suspend fun relayPost(post: XPost, serverUrl: String) {
        val relayClient = ServerRelayClient(serverUrl)
        val result = relayClient.relayPost(post)
        
        result.onSuccess {
            Log.d(TAG, "Successfully relayed post ${post.id}")
        }
        
        result.onFailure { error ->
            Log.e(TAG, "Failed to relay post ${post.id}", error)
        }
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = getString(R.string.notification_channel_description)
            }
            
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    private fun createNotification(text: String): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.notification_title))
            .setContentText(text)
            .setSmallIcon(android.R.drawable.ic_menu_info_details)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }
    
    private fun updateNotification(text: String) {
        val notification = createNotification(text)
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
    
    private fun showPostNotification(count: Int) {
        val text = if (count == 1) {
            getString(R.string.post_detected)
        } else {
            "$count new posts detected!"
        }
        updateNotification(text)
    }
    
    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }
}
