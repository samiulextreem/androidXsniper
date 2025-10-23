package com.androidxsniper

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.androidxsniper.service.XSniperService
import com.androidxsniper.utils.ConfigManager
import com.google.android.material.textfield.TextInputEditText

class MainActivity : AppCompatActivity() {
    
    private lateinit var configManager: ConfigManager
    private lateinit var statusText: TextView
    private lateinit var lastPostText: TextView
    private lateinit var targetUsernameInput: TextInputEditText
    private lateinit var bearerTokenInput: TextInputEditText
    private lateinit var serverUrlInput: TextInputEditText
    private lateinit var startButton: Button
    private lateinit var stopButton: Button
    
    private var isMonitoring = false
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        configManager = ConfigManager(this)
        
        initViews()
        loadConfig()
        setupListeners()
        updateUI()
    }
    
    private fun initViews() {
        statusText = findViewById(R.id.statusText)
        lastPostText = findViewById(R.id.lastPostText)
        targetUsernameInput = findViewById(R.id.targetUsernameInput)
        bearerTokenInput = findViewById(R.id.bearerTokenInput)
        serverUrlInput = findViewById(R.id.serverUrlInput)
        startButton = findViewById(R.id.startButton)
        stopButton = findViewById(R.id.stopButton)
    }
    
    private fun loadConfig() {
        targetUsernameInput.setText(configManager.targetUsername ?: "elonmusk")
        bearerTokenInput.setText(configManager.bearerToken ?: "")
        serverUrlInput.setText(configManager.serverUrl ?: "https://your-server.com/api/posts")
    }
    
    private fun setupListeners() {
        startButton.setOnClickListener {
            if (validateAndSaveConfig()) {
                startMonitoring()
            }
        }
        
        stopButton.setOnClickListener {
            stopMonitoring()
        }
    }
    
    private fun validateAndSaveConfig(): Boolean {
        val username = targetUsernameInput.text?.toString()?.trim()
        val token = bearerTokenInput.text?.toString()?.trim()
        val url = serverUrlInput.text?.toString()?.trim()
        
        if (username.isNullOrEmpty() || token.isNullOrEmpty() || url.isNullOrEmpty()) {
            Toast.makeText(this, R.string.config_incomplete, Toast.LENGTH_SHORT).show()
            return false
        }
        
        configManager.targetUsername = username
        configManager.bearerToken = token
        configManager.serverUrl = url
        
        Toast.makeText(this, R.string.config_saved, Toast.LENGTH_SHORT).show()
        return true
    }
    
    private fun startMonitoring() {
        val intent = Intent(this, XSniperService::class.java).apply {
            action = XSniperService.ACTION_START
        }
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
        
        isMonitoring = true
        updateUI()
    }
    
    private fun stopMonitoring() {
        val intent = Intent(this, XSniperService::class.java).apply {
            action = XSniperService.ACTION_STOP
        }
        startService(intent)
        
        isMonitoring = false
        updateUI()
    }
    
    private fun updateUI() {
        if (isMonitoring) {
            statusText.text = getString(R.string.status_monitoring)
            startButton.isEnabled = false
            stopButton.isEnabled = true
            targetUsernameInput.isEnabled = false
            bearerTokenInput.isEnabled = false
            serverUrlInput.isEnabled = false
        } else {
            statusText.text = getString(R.string.status_idle)
            startButton.isEnabled = true
            stopButton.isEnabled = false
            targetUsernameInput.isEnabled = true
            bearerTokenInput.isEnabled = true
            serverUrlInput.isEnabled = true
        }
    }
}
