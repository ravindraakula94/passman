package com.ravault.passwordmanager.data.models

import java.util.Date

data class PasswordEntry(
    val id: Long = 0,
    val title: String,
    val username: String?,
    val password: String, // Plain text (only in memory)
    val websiteUrl: String?,
    val notes: String?,
    val createdAt: Date,
    val updatedAt: Date
)
