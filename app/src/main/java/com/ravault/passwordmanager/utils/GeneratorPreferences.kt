package com.ravault.passwordmanager.utils

import android.content.Context
import android.content.SharedPreferences
import com.ravault.passwordmanager.data.models.PasswordGeneratorSettings

class GeneratorPreferences(context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences(
        "generator_preferences", Context.MODE_PRIVATE
    )
    
    companion object {
        private const val KEY_LENGTH = "length"
        private const val KEY_INCLUDE_LOWERCASE = "include_lowercase"
        private const val KEY_INCLUDE_UPPERCASE = "include_uppercase"
        private const val KEY_INCLUDE_NUMBERS = "include_numbers"
        private const val KEY_INCLUDE_SYMBOLS = "include_symbols"
        private const val KEY_EXCLUDE_SIMILAR = "exclude_similar"
        private const val KEY_CUSTOM_SYMBOLS = "custom_symbols"
    }
    
    fun saveSettings(settings: PasswordGeneratorSettings) {
        prefs.edit().apply {
            putInt(KEY_LENGTH, settings.length)
            putBoolean(KEY_INCLUDE_LOWERCASE, settings.includeLowercase)
            putBoolean(KEY_INCLUDE_UPPERCASE, settings.includeUppercase)
            putBoolean(KEY_INCLUDE_NUMBERS, settings.includeNumbers)
            putBoolean(KEY_INCLUDE_SYMBOLS, settings.includeSymbols)
            putBoolean(KEY_EXCLUDE_SIMILAR, settings.excludeSimilar)
            putString(KEY_CUSTOM_SYMBOLS, settings.customSymbols)
            apply()
        }
    }
    
    fun loadSettings(): PasswordGeneratorSettings {
        return PasswordGeneratorSettings(
            length = prefs.getInt(KEY_LENGTH, 12),
            includeLowercase = prefs.getBoolean(KEY_INCLUDE_LOWERCASE, true),
            includeUppercase = prefs.getBoolean(KEY_INCLUDE_UPPERCASE, true),
            includeNumbers = prefs.getBoolean(KEY_INCLUDE_NUMBERS, true),
            includeSymbols = prefs.getBoolean(KEY_INCLUDE_SYMBOLS, true),
            excludeSimilar = prefs.getBoolean(KEY_EXCLUDE_SIMILAR, true),
            customSymbols = prefs.getString(KEY_CUSTOM_SYMBOLS, "!@#$%^&*()_+-=[]{}|;:,.<>?") ?: "!@#$%^&*()_+-=[]{}|;:,.<>?"
        )
    }
}
