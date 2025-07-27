package com.ravault.passwordmanager.data.models

data class PasswordGeneratorSettings(
    val length: Int = 12,
    val includeLowercase: Boolean = true,
    val includeUppercase: Boolean = true,
    val includeNumbers: Boolean = true,
    val includeSymbols: Boolean = true,
    val excludeSimilar: Boolean = true,
    val customSymbols: String = "!@#$%^&*()_+-=[]{}|;:,.<>?"
)

enum class PasswordStrength {
    VERY_WEAK,
    WEAK,
    FAIR,
    GOOD,
    STRONG,
    VERY_STRONG
}

data class PasswordStrengthResult(
    val strength: PasswordStrength,
    val score: Int, // 0-100
    val feedback: List<String>
)
