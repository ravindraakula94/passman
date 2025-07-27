package com.ravault.passwordmanager.data

import android.content.Context
import androidx.room.*
import com.ravault.passwordmanager.data.dao.AppSettingsDao
import com.ravault.passwordmanager.data.dao.PasswordDao
import com.ravault.passwordmanager.data.entities.AppSettingsEntity
import com.ravault.passwordmanager.data.entities.PasswordEntity

@Database(
    entities = [PasswordEntity::class, AppSettingsEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class PasswordDatabase : RoomDatabase() {
    
    abstract fun passwordDao(): PasswordDao
    abstract fun appSettingsDao(): AppSettingsDao
    
    companion object {
        @Volatile
        private var INSTANCE: PasswordDatabase? = null
        
        fun getDatabase(context: Context): PasswordDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PasswordDatabase::class.java,
                    "ra_vault_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
