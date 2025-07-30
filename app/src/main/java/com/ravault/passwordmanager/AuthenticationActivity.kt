package com.ravault.passwordmanager

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import android.widget.TextView
import java.util.concurrent.Executor

class AuthenticationActivity : AppCompatActivity() {
    
    private var biometricPrompt: BiometricPrompt? = null
    private var promptInfo: BiometricPrompt.PromptInfo? = null
    private lateinit var executor: Executor
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)
        
        executor = ContextCompat.getMainExecutor(this)
        
        val btnAuthenticate = findViewById<MaterialButton>(R.id.btn_authenticate)
        
        setupBiometricAuthentication()
        
        btnAuthenticate.setOnClickListener {
            when (BiometricManager.from(this).canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK or BiometricManager.Authenticators.DEVICE_CREDENTIAL)) {
                BiometricManager.BIOMETRIC_SUCCESS -> {
                    // Biometric authentication is available
                    biometricPrompt?.authenticate(promptInfo!!)
                }
                BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                    // No biometric hardware available, proceed directly
                    proceedToMainActivity()
                }
                BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                    // Biometric hardware unavailable, proceed directly
                    proceedToMainActivity()
                }
                BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                    // No biometric enrolled, proceed directly
                    proceedToMainActivity()
                }
                else -> {
                    // For any other case, proceed directly for this minimal version
                    proceedToMainActivity()
                }
            }
        }
    }
    
    private fun setupBiometricAuthentication() {
        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    showStatus(getString(R.string.authentication_failed))
                }
                
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    showStatus(getString(R.string.authentication_success))
                    proceedToMainActivity()
                }
                
                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    showStatus(getString(R.string.authentication_failed))
                }
            })
        
        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(getString(R.string.biometric_prompt_title))
            .setSubtitle(getString(R.string.biometric_prompt_subtitle))
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_WEAK or BiometricManager.Authenticators.DEVICE_CREDENTIAL)
            .build()
    }
    
    private fun showStatus(message: String) {
        val tvStatus = findViewById<TextView>(R.id.tv_status)
        tvStatus.text = message
        tvStatus.visibility = TextView.VISIBLE
    }
    
    private fun proceedToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
