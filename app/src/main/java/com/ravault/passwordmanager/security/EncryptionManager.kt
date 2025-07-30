package com.ravault.passwordmanager.security

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import com.ravault.passwordmanager.data.models.EncryptedData
import java.security.KeyStore
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec
import javax.crypto.SecretKeyFactory

class EncryptionManager(private val context: Context) {
    
    companion object {
        private const val ANDROID_KEYSTORE = "AndroidKeyStore"
        private const val MASTER_KEY_ALIAS = "RAVaultMasterKey"
        private const val AES_MODE = "AES/GCM/NoPadding"
        private const val KEY_LENGTH = 256
        private const val IV_LENGTH = 12
        private const val TAG_LENGTH = 128
        private const val SALT_LENGTH = 32
        private const val PBKDF2_ITERATIONS = 100000
    }
    
    private val keyStore: KeyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply {
        load(null)
    }
    
    init {
        generateOrRetrieveMasterKey()
    }
    
    private fun generateOrRetrieveMasterKey(): SecretKey {
        return if (keyStore.containsAlias(MASTER_KEY_ALIAS)) {
            val secretKeyEntry = keyStore.getEntry(MASTER_KEY_ALIAS, null) as KeyStore.SecretKeyEntry
            secretKeyEntry.secretKey
        } else {
            generateMasterKey()
        }
    }
    
    private fun generateMasterKey(): SecretKey {
        val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)
        val keyGenParameterSpec = KeyGenParameterSpec.Builder(
            MASTER_KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(KEY_LENGTH)
            .setUserAuthenticationRequired(false) // For now, will be enhanced later
            .build()
        
        keyGenerator.init(keyGenParameterSpec)
        return keyGenerator.generateKey()
    }
    
    fun generateSalt(): ByteArray {
        val salt = ByteArray(SALT_LENGTH)
        SecureRandom().nextBytes(salt)
        return salt
    }
    
    private fun deriveKey(masterKey: SecretKey, salt: ByteArray): SecretKey {
        // Instead of using the Android Keystore key directly (which doesn't expose raw bytes),
        // we'll use a proper PBKDF2 approach with the salt as the primary input
        
        try {
            // Create a deterministic seed by hashing the salt with a fixed context
            val saltWithContext = ByteArray(salt.size + 16)
            System.arraycopy(salt, 0, saltWithContext, 0, salt.size)
            // Add a fixed context string to prevent collision with other uses
            System.arraycopy("RAVaultContext".toByteArray(), 0, saltWithContext, salt.size, 14)
            
            // Use PBKDF2 with the enhanced salt
            val spec = PBEKeySpec(
                "RAVaultMasterPassword".toCharArray(), // Fixed password for deterministic derivation
                saltWithContext,
                PBKDF2_ITERATIONS,
                KEY_LENGTH
            )
            
            val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
            val derivedKeyBytes = factory.generateSecret(spec).encoded
            
            spec.clearPassword()
            
            return SecretKeySpec(derivedKeyBytes, "AES")
        } catch (e: Exception) {
            // Fallback to simple salt-based derivation
            val spec = PBEKeySpec(
                "RAVaultFallback".toCharArray(),
                salt,
                PBKDF2_ITERATIONS,
                KEY_LENGTH
            )
            
            val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
            val derivedKeyBytes = factory.generateSecret(spec).encoded
            
            spec.clearPassword()
            
            return SecretKeySpec(derivedKeyBytes, "AES")
        }
    }
    
    fun encryptPassword(plaintext: String, salt: ByteArray): EncryptedData {
        val masterKey = generateOrRetrieveMasterKey()
        val derivedKey = deriveKey(masterKey, salt)
        
        val cipher = Cipher.getInstance(AES_MODE)
        cipher.init(Cipher.ENCRYPT_MODE, derivedKey)
        
        val iv = cipher.iv
        val ciphertext = cipher.doFinal(plaintext.toByteArray(Charsets.UTF_8))
        
        // Combine IV and ciphertext for storage
        val combined = ByteArray(iv.size + ciphertext.size)
        System.arraycopy(iv, 0, combined, 0, iv.size)
        System.arraycopy(ciphertext, 0, combined, iv.size, ciphertext.size)
        
        return EncryptedData(combined, iv, salt)
    }
    
    fun decryptPassword(encryptedData: EncryptedData): String {
        val masterKey = generateOrRetrieveMasterKey()
        val derivedKey = deriveKey(masterKey, encryptedData.salt)
        
        // Extract IV and ciphertext from combined data
        val combined = encryptedData.ciphertext
        
        if (combined.size < IV_LENGTH) {
            throw IllegalArgumentException("Invalid encrypted data - too short")
        }
        
        val iv = ByteArray(IV_LENGTH)
        val ciphertext = ByteArray(combined.size - IV_LENGTH)
        
        System.arraycopy(combined, 0, iv, 0, IV_LENGTH)
        System.arraycopy(combined, IV_LENGTH, ciphertext, 0, ciphertext.size)
        
        val cipher = Cipher.getInstance(AES_MODE)
        val gcmSpec = GCMParameterSpec(TAG_LENGTH, iv)
        cipher.init(Cipher.DECRYPT_MODE, derivedKey, gcmSpec)
        
        val plaintext = cipher.doFinal(ciphertext)
        return String(plaintext, Charsets.UTF_8)
    }
    
    fun clearSensitiveData(data: ByteArray) {
        data.fill(0)
    }
    
    fun clearSensitiveData(data: CharArray) {
        data.fill('\u0000')
    }
}
