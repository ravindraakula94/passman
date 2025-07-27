package com.ravault.passwordmanager

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.google.android.material.button.MaterialButton
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.textfield.TextInputEditText
import com.ravault.passwordmanager.data.models.PasswordGeneratorSettings
import com.ravault.passwordmanager.data.models.PasswordStrength
import com.ravault.passwordmanager.utils.PasswordGenerator
import com.ravault.passwordmanager.utils.GeneratorPreferences

class PasswordGeneratorActivity : AppCompatActivity() {
    
    private lateinit var passwordGenerator: PasswordGenerator
    private lateinit var generatorPreferences: GeneratorPreferences
    private var currentSettings = PasswordGeneratorSettings()
    
    private lateinit var seekBarLength: SeekBar
    private lateinit var tvLengthValue: TextView
    private lateinit var etGeneratedPassword: TextInputEditText
    private lateinit var checkLowercase: MaterialCheckBox
    private lateinit var checkUppercase: MaterialCheckBox
    private lateinit var checkNumbers: MaterialCheckBox
    private lateinit var checkSymbols: MaterialCheckBox
    private lateinit var checkExcludeSimilar: MaterialCheckBox
    private lateinit var etCustomSymbols: TextInputEditText
    private lateinit var progressStrength: LinearProgressIndicator
    private lateinit var tvStrengthLabel: TextView
    private lateinit var tvStrengthFeedback: TextView
    private lateinit var btnGenerate: MaterialButton
    private lateinit var btnCopy: MaterialButton
    private lateinit var btnUse: MaterialButton
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_generator)
        
        // Set up toolbar
        supportActionBar?.title = "Password Generator"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        
        passwordGenerator = PasswordGenerator()
        generatorPreferences = GeneratorPreferences(this)
        
        // Load saved settings
        currentSettings = generatorPreferences.loadSettings()
        
        initializeViews()
        setupListeners()
        generateInitialPassword()
    }
    
    private fun initializeViews() {
        seekBarLength = findViewById(R.id.seekbar_length)
        tvLengthValue = findViewById(R.id.tv_length_value)
        etGeneratedPassword = findViewById(R.id.et_generated_password)
        checkLowercase = findViewById(R.id.check_lowercase)
        checkUppercase = findViewById(R.id.check_uppercase)
        checkNumbers = findViewById(R.id.check_numbers)
        checkSymbols = findViewById(R.id.check_symbols)
        checkExcludeSimilar = findViewById(R.id.check_exclude_similar)
        etCustomSymbols = findViewById(R.id.et_custom_symbols)
        progressStrength = findViewById(R.id.progress_strength)
        tvStrengthLabel = findViewById(R.id.tv_strength_label)
        tvStrengthFeedback = findViewById(R.id.tv_strength_feedback)
        btnGenerate = findViewById(R.id.btn_generate)
        btnCopy = findViewById(R.id.btn_copy)
        btnUse = findViewById(R.id.btn_use)
        
        // Initialize with default settings
        seekBarLength.max = 124 // 128 - 4 (minimum length is 4)
        seekBarLength.progress = currentSettings.length - 4
        tvLengthValue.text = currentSettings.length.toString()
        
        checkLowercase.isChecked = currentSettings.includeLowercase
        checkUppercase.isChecked = currentSettings.includeUppercase
        checkNumbers.isChecked = currentSettings.includeNumbers
        checkSymbols.isChecked = currentSettings.includeSymbols
        checkExcludeSimilar.isChecked = currentSettings.excludeSimilar
        etCustomSymbols.setText(currentSettings.customSymbols)
    }
    
    private fun setupListeners() {
        seekBarLength.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val length = progress + 4 // Minimum length is 4
                tvLengthValue.text = length.toString()
                currentSettings = currentSettings.copy(length = length)
                if (fromUser) {
                    generatorPreferences.saveSettings(currentSettings)
                    generatePassword()
                }
            }
            
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        
        checkLowercase.setOnCheckedChangeListener { _, isChecked ->
            currentSettings = currentSettings.copy(includeLowercase = isChecked)
            generatorPreferences.saveSettings(currentSettings)
            generatePassword()
        }
        
        checkUppercase.setOnCheckedChangeListener { _, isChecked ->
            currentSettings = currentSettings.copy(includeUppercase = isChecked)
            generatorPreferences.saveSettings(currentSettings)
            generatePassword()
        }
        
        checkNumbers.setOnCheckedChangeListener { _, isChecked ->
            currentSettings = currentSettings.copy(includeNumbers = isChecked)
            generatorPreferences.saveSettings(currentSettings)
            generatePassword()
        }
        
        checkSymbols.setOnCheckedChangeListener { _, isChecked ->
            currentSettings = currentSettings.copy(includeSymbols = isChecked)
            generatorPreferences.saveSettings(currentSettings)
            generatePassword()
        }
        
        checkExcludeSimilar.setOnCheckedChangeListener { _, isChecked ->
            currentSettings = currentSettings.copy(excludeSimilar = isChecked)
            generatorPreferences.saveSettings(currentSettings)
            generatePassword()
        }
        
        etCustomSymbols.addTextChangedListener { text ->
            currentSettings = currentSettings.copy(customSymbols = text.toString())
            generatorPreferences.saveSettings(currentSettings)
            generatePassword()
        }
        
        etGeneratedPassword.addTextChangedListener { text ->
            updatePasswordStrength(text.toString())
        }
        
        btnGenerate.setOnClickListener {
            generatePassword()
        }
        
        btnCopy.setOnClickListener {
            copyPasswordToClipboard()
        }
        
        btnUse.setOnClickListener {
            usePassword()
        }
    }
    
    private fun generateInitialPassword() {
        generatePassword()
    }
    
    private fun generatePassword() {
        // Ensure at least one character type is selected
        if (!currentSettings.includeLowercase && !currentSettings.includeUppercase && 
            !currentSettings.includeNumbers && !currentSettings.includeSymbols) {
            etGeneratedPassword.setText("")
            return
        }
        
        val password = passwordGenerator.generatePassword(currentSettings)
        etGeneratedPassword.setText(password)
    }
    
    private fun updatePasswordStrength(password: String) {
        if (password.isEmpty()) {
            progressStrength.progress = 0
            tvStrengthLabel.text = "No Password"
            tvStrengthFeedback.text = ""
            return
        }
        
        val strengthResult = passwordGenerator.calculateStrength(password)
        
        progressStrength.progress = strengthResult.score
        tvStrengthLabel.text = when (strengthResult.strength) {
            PasswordStrength.VERY_WEAK -> "Very Weak"
            PasswordStrength.WEAK -> "Weak"
            PasswordStrength.FAIR -> "Fair"
            PasswordStrength.GOOD -> "Good"
            PasswordStrength.STRONG -> "Strong"
            PasswordStrength.VERY_STRONG -> "Very Strong"
        }
        
        tvStrengthFeedback.text = strengthResult.feedback.joinToString("\n• ", "• ")
        
        // Update progress indicator color based on strength
        val colorRes = when (strengthResult.strength) {
            PasswordStrength.VERY_WEAK, PasswordStrength.WEAK -> android.R.color.holo_red_dark
            PasswordStrength.FAIR -> android.R.color.holo_orange_dark
            PasswordStrength.GOOD -> android.R.color.holo_blue_dark
            PasswordStrength.STRONG, PasswordStrength.VERY_STRONG -> android.R.color.holo_green_dark
        }
        progressStrength.setIndicatorColor(getColor(colorRes))
    }
    
    private fun copyPasswordToClipboard() {
        val password = etGeneratedPassword.text.toString()
        if (password.isEmpty()) {
            Toast.makeText(this, "No password to copy", Toast.LENGTH_SHORT).show()
            return
        }
        
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Generated Password", password)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this, "Password copied to clipboard", Toast.LENGTH_SHORT).show()
    }
    
    private fun usePassword() {
        val password = etGeneratedPassword.text.toString()
        if (password.isEmpty()) {
            Toast.makeText(this, "No password to use", Toast.LENGTH_SHORT).show()
            return
        }
        
        val resultIntent = Intent().apply {
            putExtra("GENERATED_PASSWORD", password)
        }
        setResult(RESULT_OK, resultIntent)
        finish()
    }
    
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
