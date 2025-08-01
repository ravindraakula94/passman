package com.ravault.passwordmanager.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.ravault.passwordmanager.data.dao.PasswordDao
import com.ravault.passwordmanager.data.entities.PasswordEntity
import com.ravault.passwordmanager.data.models.PasswordEntry
import com.ravault.passwordmanager.security.EncryptionManager
import java.util.Date

class PasswordRepository(
    private val passwordDao: PasswordDao,
    private val encryptionManager: EncryptionManager
) {
    
    fun getAllPasswords(): LiveData<List<PasswordEntry>> {
        return passwordDao.getAllPasswords().map { entities ->
            entities.mapNotNull { entity ->
                try {
                    val encryptedData = com.ravault.passwordmanager.data.models.EncryptedData(
                        entity.encryptedPassword,
                        ByteArray(0), // IV is embedded in ciphertext
                        entity.salt
                    )
                    val decryptedPassword = encryptionManager.decryptPassword(encryptedData)
                    
                    PasswordEntry(
                        id = entity.id,
                        title = entity.title,
                        username = entity.username,
                        password = decryptedPassword,
                        websiteUrl = entity.websiteUrl,
                        notes = entity.notes,
                        createdAt = entity.createdAt,
                        updatedAt = entity.updatedAt
                    )
                } catch (e: Exception) {
                    // Log error and skip this entry
                    android.util.Log.e("PasswordRepository", "Failed to decrypt password for: ${entity.title}", e)
                    null
                }
            }
        }
    }
    
    suspend fun getPasswordById(id: Long): PasswordEntry? {
        val entity = passwordDao.getPasswordById(id) ?: return null
        return try {
            val encryptedData = com.ravault.passwordmanager.data.models.EncryptedData(
                entity.encryptedPassword,
                ByteArray(0), // IV is embedded in ciphertext
                entity.salt
            )
            val decryptedPassword = encryptionManager.decryptPassword(encryptedData)
            
            PasswordEntry(
                id = entity.id,
                title = entity.title,
                username = entity.username,
                password = decryptedPassword,
                websiteUrl = entity.websiteUrl,
                notes = entity.notes,
                createdAt = entity.createdAt,
                updatedAt = entity.updatedAt
            )
        } catch (e: Exception) {
            android.util.Log.e("PasswordRepository", "Failed to decrypt password by ID: $id", e)
            null
        }
    }
    
    suspend fun insertPassword(passwordEntry: PasswordEntry): Long {
        val salt = encryptionManager.generateSalt()
        val encryptedData = encryptionManager.encryptPassword(passwordEntry.password, salt)
        
        val entity = PasswordEntity(
            title = passwordEntry.title,
            username = passwordEntry.username,
            encryptedPassword = encryptedData.ciphertext, // This now contains IV + ciphertext
            websiteUrl = passwordEntry.websiteUrl,
            notes = passwordEntry.notes,
            createdAt = Date(),
            updatedAt = Date(),
            salt = salt
        )
        
        return passwordDao.insertPassword(entity)
    }
    
    suspend fun updatePassword(passwordEntry: PasswordEntry) {
        val existingEntity = passwordDao.getPasswordById(passwordEntry.id) ?: return
        val salt = existingEntity.salt // Reuse existing salt
        val encryptedData = encryptionManager.encryptPassword(passwordEntry.password, salt)
        
        val updatedEntity = existingEntity.copy(
            title = passwordEntry.title,
            username = passwordEntry.username,
            encryptedPassword = encryptedData.ciphertext, // This now contains IV + ciphertext
            websiteUrl = passwordEntry.websiteUrl,
            notes = passwordEntry.notes,
            updatedAt = Date()
        )
        
        passwordDao.updatePassword(updatedEntity)
    }
    
    suspend fun deletePassword(passwordEntry: PasswordEntry) {
        passwordDao.deletePasswordById(passwordEntry.id)
    }
    
    suspend fun searchPasswords(query: String): List<PasswordEntry> {
        val searchQuery = "%$query%"
        val entities = passwordDao.searchPasswords(searchQuery)
        
        return entities.mapNotNull { entity ->
            try {
                val encryptedData = com.ravault.passwordmanager.data.models.EncryptedData(
                    entity.encryptedPassword,
                    ByteArray(0), // IV is embedded in ciphertext
                    entity.salt
                )
                val decryptedPassword = encryptionManager.decryptPassword(encryptedData)
                
                PasswordEntry(
                    id = entity.id,
                    title = entity.title,
                    username = entity.username,
                    password = decryptedPassword,
                    websiteUrl = entity.websiteUrl,
                    notes = entity.notes,
                    createdAt = entity.createdAt,
                    updatedAt = entity.updatedAt
                )
            } catch (e: Exception) {
                android.util.Log.e("PasswordRepository", "Failed to decrypt password in search: ${entity.title}", e)
                null
            }
        }
    }
    
    suspend fun getPasswordCount(): Int {
        return passwordDao.getPasswordCount()
    }
}
