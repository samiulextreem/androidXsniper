package com.androidxsniper.network

import com.androidxsniper.model.XPost
import com.androidxsniper.model.XPostResponse
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit

class XApiClient(private val bearerToken: String) {
    
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()
    
    private val gson = Gson()
    
    /**
     * Fetches recent tweets from a user by username
     * Uses X API v2 endpoint
     */
    fun getUserTweets(username: String, maxResults: Int = 10): Result<XPostResponse> {
        return try {
            // First, get user ID from username
            val userId = getUserId(username).getOrThrow()
            
            // Then fetch tweets using user ID
            val url = "https://api.twitter.com/2/users/$userId/tweets?max_results=$maxResults&tweet.fields=created_at,author_id&expansions=author_id"
            
            val request = Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer $bearerToken")
                .get()
                .build()
            
            val response = client.newCall(request).execute()
            
            if (response.isSuccessful) {
                val body = response.body?.string()
                if (body != null) {
                    val postResponse = gson.fromJson(body, XPostResponse::class.java)
                    Result.success(postResponse)
                } else {
                    Result.failure(IOException("Empty response body"))
                }
            } else {
                Result.failure(IOException("API request failed: ${response.code} - ${response.message}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Gets user ID from username
     */
    private fun getUserId(username: String): Result<String> {
        return try {
            val url = "https://api.twitter.com/2/users/by/username/$username"
            
            val request = Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer $bearerToken")
                .get()
                .build()
            
            val response = client.newCall(request).execute()
            
            if (response.isSuccessful) {
                val body = response.body?.string()
                if (body != null) {
                    val jsonObject = gson.fromJson(body, Map::class.java)
                    val data = jsonObject["data"] as? Map<*, *>
                    val id = data?.get("id") as? String
                    if (id != null) {
                        Result.success(id)
                    } else {
                        Result.failure(IOException("User ID not found in response"))
                    }
                } else {
                    Result.failure(IOException("Empty response body"))
                }
            } else {
                Result.failure(IOException("API request failed: ${response.code} - ${response.message}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
