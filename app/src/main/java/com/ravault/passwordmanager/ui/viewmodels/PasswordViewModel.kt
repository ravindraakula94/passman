package com.ravault.passwordmanager.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.ravault.passwordmanager.data.PasswordDatabase
import com.ravault.passwordmanager.data.models.PasswordEntry
import com.ravault.passwordmanager.repository.PasswordRepository
import com.ravault.passwordmanager.security.EncryptionManager
import kotlinx.coroutines.launch

class PasswordViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: PasswordRepository
    val allPasswords: LiveData<List<PasswordEntry>>
    
    init {
        val database = PasswordDatabase.getDatabase(application)
        val encryptionManager = EncryptionManager(application)
        repository = PasswordRepository(database.passwordDao(), encryptionManager)
        allPasswords = repository.getAllPasswords()
    }
    
    fun insertPassword(passwordEntry: PasswordEntry) = viewModelScope.launch {
        repository.insertPassword(passwordEntry)
    }
    
    fun updatePassword(passwordEntry: PasswordEntry) = viewModelScope.launch {
        repository.updatePassword(passwordEntry)
    }
    
    fun deletePassword(passwordEntry: PasswordEntry) = viewModelScope.launch {
        repository.deletePassword(passwordEntry)
    }
    
    suspend fun getPasswordById(id: Long): PasswordEntry? {
        return repository.getPasswordById(id)
    }
    
    suspend fun searchPasswords(query: String): List<PasswordEntry> {
        return repository.searchPasswords(query)
    }
    
    suspend fun getPasswordCount(): Int {
        return repository.getPasswordCount()
    }
}
