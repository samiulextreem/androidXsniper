package com.androidxsniper.utils

import android.content.Context
import android.content.SharedPreferences

class ConfigManager(context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    companion object {
        private const val PREFS_NAME = "XSniperConfig"
        private const val KEY_BEARER_TOKEN = "bearer_token"
        private const val KEY_SERVER_URL = "server_url"
        private const val KEY_TARGET_USERNAME = "target_username"
        private const val KEY_LAST_POST_ID = "last_post_id"
    }
    
    var bearerToken: String?
        get() = prefs.getString(KEY_BEARER_TOKEN, null)
        set(value) = prefs.edit().putString(KEY_BEARER_TOKEN, value).apply()
    
    var serverUrl: String?
        get() = prefs.getString(KEY_SERVER_URL, null)
        set(value) = prefs.edit().putString(KEY_SERVER_URL, value).apply()
    
    var targetUsername: String?
        get() = prefs.getString(KEY_TARGET_USERNAME, "elonmusk")
        set(value) = prefs.edit().putString(KEY_TARGET_USERNAME, value).apply()
    
    var lastPostId: String?
        get() = prefs.getString(KEY_LAST_POST_ID, null)
        set(value) = prefs.edit().putString(KEY_LAST_POST_ID, value).apply()
    
    fun isConfigured(): Boolean {
        return !bearerToken.isNullOrEmpty() && !serverUrl.isNullOrEmpty() && !targetUsername.isNullOrEmpty()
    }
    
    fun clearConfig() {
        prefs.edit().clear().apply()
    }
}
