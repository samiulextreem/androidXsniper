package com.androidxsniper.network

import com.androidxsniper.model.XPost
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

class ServerRelayClient(private val serverUrl: String) {
    
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()
    
    private val gson = Gson()
    private val JSON = "application/json; charset=utf-8".toMediaType()
    
    /**
     * Relays a post to the configured server
     */
    fun relayPost(post: XPost): Result<String> {
        return try {
            val json = gson.toJson(post)
            val body = json.toRequestBody(JSON)
            
            val request = Request.Builder()
                .url(serverUrl)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build()
            
            val response = client.newCall(request).execute()
            
            if (response.isSuccessful) {
                val responseBody = response.body?.string() ?: "Success"
                Result.success(responseBody)
            } else {
                Result.failure(Exception("Server relay failed: ${response.code} - ${response.message}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
