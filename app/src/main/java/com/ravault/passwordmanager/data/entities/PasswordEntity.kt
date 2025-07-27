package com.ravault.passwordmanager.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "passwords")
data class PasswordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    @ColumnInfo(name = "title")
    val title: String,
    
    @ColumnInfo(name = "username")
    val username: String?,
    
    @ColumnInfo(name = "encrypted_password")
    val encryptedPassword: ByteArray,
    
    @ColumnInfo(name = "website_url")
    val websiteUrl: String?,
    
    @ColumnInfo(name = "notes")
    val notes: String?,
    
    @ColumnInfo(name = "created_at")
    val createdAt: Date,
    
    @ColumnInfo(name = "updated_at")
    val updatedAt: Date,
    
    @ColumnInfo(name = "salt")
    val salt: ByteArray
) {
    // Override equals and hashCode to handle ByteArray properly
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PasswordEntity

        if (id != other.id) return false
        if (title != other.title) return false
        if (username != other.username) return false
        if (!encryptedPassword.contentEquals(other.encryptedPassword)) return false
        if (websiteUrl != other.websiteUrl) return false
        if (notes != other.notes) return false
        if (createdAt != other.createdAt) return false
        if (updatedAt != other.updatedAt) return false
        if (!salt.contentEquals(other.salt)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + (username?.hashCode() ?: 0)
        result = 31 * result + encryptedPassword.contentHashCode()
        result = 31 * result + (websiteUrl?.hashCode() ?: 0)
        result = 31 * result + (notes?.hashCode() ?: 0)
        result = 31 * result + createdAt.hashCode()
        result = 31 * result + updatedAt.hashCode()
        result = 31 * result + salt.contentHashCode()
        return result
    }
}
