package com.ravault.passwordmanager.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_settings")
data class AppSettingsEntity(
    @PrimaryKey
    @ColumnInfo(name = "key")
    val key: String,
    
    @ColumnInfo(name = "value")
    val value: String
)
