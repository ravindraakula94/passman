package com.ravault.passwordmanager.utils

import com.ravault.passwordmanager.data.models.PasswordGeneratorSettings
import com.ravault.passwordmanager.data.models.PasswordStrength
import com.ravault.passwordmanager.data.models.PasswordStrengthResult
import java.security.SecureRandom
import kotlin.math.ln
import kotlin.math.pow

class PasswordGenerator {
    
    companion object {
        private const val LOWERCASE = "abcdefghijklmnopqrstuvwxyz"
        private const val UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        private const val NUMBERS = "0123456789"
        private const val SIMILAR_CHARS = "0O1lI|"
        
        // Common weak patterns to avoid
        private val COMMON_PATTERNS = listOf(
            "123", "321", "abc", "qwe", "asd", "zxc", "password", "admin"
        )
    }
    
    private val secureRandom = SecureRandom()
    
    fun generatePassword(settings: PasswordGeneratorSettings): String {
        if (settings.length < 1) return ""
        
        val characterSet = buildCharacterSet(settings)
        if (characterSet.isEmpty()) return ""
        
        // Ensure at least one character from each selected type
        val requiredChars = mutableListOf<Char>()
        if (settings.includeLowercase) {
            requiredChars.add(getRandomChar(getFilteredChars(LOWERCASE, settings.excludeSimilar)))
        }
        if (settings.includeUppercase) {
            requiredChars.add(getRandomChar(getFilteredChars(UPPERCASE, settings.excludeSimilar)))
        }
        if (settings.includeNumbers) {
            requiredChars.add(getRandomChar(getFilteredChars(NUMBERS, settings.excludeSimilar)))
        }
        if (settings.includeSymbols) {
            requiredChars.add(getRandomChar(getFilteredChars(settings.customSymbols, settings.excludeSimilar)))
        }
        
        // Fill remaining length with random characters
        val remainingLength = settings.length - requiredChars.size
        val randomChars = (0 until remainingLength).map {
            getRandomChar(characterSet)
        }
        
        // Combine and shuffle
        val allChars = (requiredChars + randomChars).toMutableList()
        allChars.shuffle(secureRandom)
        
        return allChars.joinToString("")
    }
    
    private fun buildCharacterSet(settings: PasswordGeneratorSettings): String {
        val characterSet = StringBuilder()
        
        if (settings.includeLowercase) {
            characterSet.append(getFilteredChars(LOWERCASE, settings.excludeSimilar))
        }
        if (settings.includeUppercase) {
            characterSet.append(getFilteredChars(UPPERCASE, settings.excludeSimilar))
        }
        if (settings.includeNumbers) {
            characterSet.append(getFilteredChars(NUMBERS, settings.excludeSimilar))
        }
        if (settings.includeSymbols) {
            characterSet.append(getFilteredChars(settings.customSymbols, settings.excludeSimilar))
        }
        
        return characterSet.toString()
    }
    
    private fun getFilteredChars(chars: String, excludeSimilar: Boolean): String {
        return if (excludeSimilar) {
            chars.filter { it !in SIMILAR_CHARS }
        } else {
            chars
        }
    }
    
    private fun getRandomChar(chars: String): Char {
        return chars[secureRandom.nextInt(chars.length)]
    }
    
    fun calculateStrength(password: String): PasswordStrengthResult {
        val feedback = mutableListOf<String>()
        var score = 0
        
        // Length scoring
        when {
            password.length < 8 -> {
                score += 0
                feedback.add("Password is too short (minimum 8 characters)")
            }
            password.length < 12 -> {
                score += 15
                feedback.add("Consider using at least 12 characters")
            }
            password.length >= 16 -> {
                score += 30
            }
            else -> {
                score += 25
            }
        }
        
        // Character variety scoring
        var characterSets = 0
        if (password.any { it.isLowerCase() }) {
            characterSets++
            score += 5
        } else {
            feedback.add("Add lowercase letters")
        }
        
        if (password.any { it.isUpperCase() }) {
            characterSets++
            score += 5
        } else {
            feedback.add("Add uppercase letters")
        }
        
        if (password.any { it.isDigit() }) {
            characterSets++
            score += 5
        } else {
            feedback.add("Add numbers")
        }
        
        if (password.any { !it.isLetterOrDigit() }) {
            characterSets++
            score += 10
        } else {
            feedback.add("Add special characters")
        }
        
        // Bonus for using all character sets
        if (characterSets >= 4) {
            score += 15
        }
        
        // Entropy calculation
        val entropy = calculateEntropy(password)
        when {
            entropy < 30 -> score += 0
            entropy < 50 -> score += 10
            entropy < 70 -> score += 20
            else -> score += 25
        }
        
        // Pattern detection penalties
        if (hasCommonPatterns(password)) {
            score -= 20
            feedback.add("Avoid common patterns and sequences")
        }
        
        if (hasRepeatedCharacters(password)) {
            score -= 10
            feedback.add("Avoid repeated characters")
        }
        
        // Ensure score is within bounds
        score = score.coerceIn(0, 100)
        
        val strength = when (score) {
            in 0..20 -> PasswordStrength.VERY_WEAK
            in 21..40 -> PasswordStrength.WEAK
            in 41..60 -> PasswordStrength.FAIR
            in 61..80 -> PasswordStrength.GOOD
            in 81..95 -> PasswordStrength.STRONG
            else -> PasswordStrength.VERY_STRONG
        }
        
        if (feedback.isEmpty()) {
            feedback.add("Excellent password!")
        }
        
        return PasswordStrengthResult(strength, score, feedback)
    }
    
    private fun calculateEntropy(password: String): Double {
        val charSet = mutableSetOf<Char>()
        password.forEach { charSet.add(it) }
        
        val possibleChars = when {
            password.any { it.isLowerCase() } && password.any { it.isUpperCase() } && 
            password.any { it.isDigit() } && password.any { !it.isLetterOrDigit() } -> 94
            password.any { it.isLowerCase() } && password.any { it.isUpperCase() } && 
            password.any { it.isDigit() } -> 62
            password.any { it.isLowerCase() } && password.any { it.isUpperCase() } -> 52
            password.any { it.isLowerCase() } && password.any { it.isDigit() } -> 36
            password.any { it.isLowerCase() } -> 26
            else -> charSet.size
        }
        
        return password.length * ln(possibleChars.toDouble()) / ln(2.0)
    }
    
    private fun hasCommonPatterns(password: String): Boolean {
        val lowercasePassword = password.lowercase()
        return COMMON_PATTERNS.any { pattern ->
            lowercasePassword.contains(pattern)
        } || hasSequentialCharacters(password)
    }
    
    private fun hasSequentialCharacters(password: String): Boolean {
        for (i in 0 until password.length - 2) {
            val char1 = password[i].code
            val char2 = password[i + 1].code
            val char3 = password[i + 2].code
            
            if ((char2 == char1 + 1 && char3 == char2 + 1) ||
                (char2 == char1 - 1 && char3 == char2 - 1)) {
                return true
            }
        }
        return false
    }
    
    private fun hasRepeatedCharacters(password: String): Boolean {
        for (i in 0 until password.length - 2) {
            if (password[i] == password[i + 1] && password[i + 1] == password[i + 2]) {
                return true
            }
        }
        return false
    }
}
