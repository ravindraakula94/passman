package com.ravault.passwordmanager.data.dao

import androidx.room.*
import com.ravault.passwordmanager.data.entities.AppSettingsEntity

@Dao
interface AppSettingsDao {
    
    @Query("SELECT * FROM app_settings WHERE key = :key")
    suspend fun getSetting(key: String): AppSettingsEntity?
    
    @Query("SELECT value FROM app_settings WHERE key = :key")
    suspend fun getSettingValue(key: String): String?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSetting(setting: AppSettingsEntity)
    
    @Update
    suspend fun updateSetting(setting: AppSettingsEntity)
    
    @Delete
    suspend fun deleteSetting(setting: AppSettingsEntity)
    
    @Query("DELETE FROM app_settings WHERE key = :key")
    suspend fun deleteSettingByKey(key: String)
}
