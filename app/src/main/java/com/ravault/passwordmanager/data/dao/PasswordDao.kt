package com.ravault.passwordmanager.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.ravault.passwordmanager.data.entities.PasswordEntity

@Dao
interface PasswordDao {
    
    @Query("SELECT * FROM passwords ORDER BY title ASC")
    fun getAllPasswords(): LiveData<List<PasswordEntity>>
    
    @Query("SELECT * FROM passwords ORDER BY updated_at DESC")
    fun getAllPasswordsByDate(): LiveData<List<PasswordEntity>>
    
    @Query("SELECT * FROM passwords WHERE id = :id")
    suspend fun getPasswordById(id: Long): PasswordEntity?
    
    @Query("SELECT * FROM passwords WHERE title LIKE :searchQuery OR username LIKE :searchQuery OR website_url LIKE :searchQuery ORDER BY title ASC")
    suspend fun searchPasswords(searchQuery: String): List<PasswordEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPassword(password: PasswordEntity): Long
    
    @Update
    suspend fun updatePassword(password: PasswordEntity)
    
    @Delete
    suspend fun deletePassword(password: PasswordEntity)
    
    @Query("DELETE FROM passwords WHERE id = :id")
    suspend fun deletePasswordById(id: Long)
    
    @Query("SELECT COUNT(*) FROM passwords")
    suspend fun getPasswordCount(): Int
}
