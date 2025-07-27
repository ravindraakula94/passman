package com.ravault.passwordmanager

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.ravault.passwordmanager.data.models.PasswordEntry
import com.ravault.passwordmanager.ui.viewmodels.PasswordViewModel
import kotlinx.coroutines.launch
import java.util.Date

class AddEditPasswordActivity : AppCompatActivity() {
    
    private lateinit var passwordViewModel: PasswordViewModel
    private var passwordId: Long = -1
    private var isEditMode = false
    
    private lateinit var etTitle: TextInputEditText
    private lateinit var etUsername: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var etWebsite: TextInputEditText
    private lateinit var etNotes: TextInputEditText
    private lateinit var btnGeneratePassword: MaterialButton
    private lateinit var btnSave: MaterialButton
    
    private val passwordGeneratorLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val generatedPassword = result.data?.getStringExtra("GENERATED_PASSWORD")
            generatedPassword?.let {
                etPassword.setText(it)
            }
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_password)
        
        // Initialize views
        etTitle = findViewById(R.id.et_title)
        etUsername = findViewById(R.id.et_username)
        etPassword = findViewById(R.id.et_password)
        etWebsite = findViewById(R.id.et_website)
        etNotes = findViewById(R.id.et_notes)
        btnGeneratePassword = findViewById(R.id.btn_generate_password)
        btnSave = findViewById(R.id.btn_save)
        
        // Check if editing existing password
        passwordId = intent.getLongExtra("PASSWORD_ID", -1)
        isEditMode = passwordId != -1L
        
        // Set up toolbar
        supportActionBar?.title = if (isEditMode) "Edit Password" else "Add Password"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        
        // Set up ViewModel
        passwordViewModel = ViewModelProvider(this)[PasswordViewModel::class.java]
        
        // Load existing password if editing
        if (isEditMode) {
            loadPasswordData()
        }
        
        // Set up save button
        btnSave.setOnClickListener {
            savePassword()
        }
        
        // Set up generate password button
        btnGeneratePassword.setOnClickListener {
            val intent = Intent(this, PasswordGeneratorActivity::class.java)
            passwordGeneratorLauncher.launch(intent)
        }
    }
    
    private fun loadPasswordData() {
        lifecycleScope.launch {
            val passwordEntry = passwordViewModel.getPasswordById(passwordId)
            passwordEntry?.let {
                etTitle.setText(it.title)
                etUsername.setText(it.username)
                etPassword.setText(it.password)
                etWebsite.setText(it.websiteUrl)
                etNotes.setText(it.notes)
            }
        }
    }
    
    private fun savePassword() {
        val title = etTitle.text.toString().trim()
        val username = etUsername.text.toString().trim()
        val password = etPassword.text.toString().trim()
        val website = etWebsite.text.toString().trim()
        val notes = etNotes.text.toString().trim()
        
        // Validation
        if (title.isEmpty()) {
            etTitle.error = getString(R.string.title_required)
            return
        }
        
        if (password.isEmpty()) {
            etPassword.error = getString(R.string.password_required)
            return
        }
        
        val passwordEntry = PasswordEntry(
            id = if (isEditMode) passwordId else 0,
            title = title,
            username = username.ifEmpty { null },
            password = password,
            websiteUrl = website.ifEmpty { null },
            notes = notes.ifEmpty { null },
            createdAt = Date(), // Will be set properly in repository
            updatedAt = Date()
        )
        
        if (isEditMode) {
            passwordViewModel.updatePassword(passwordEntry)
            Toast.makeText(this, getString(R.string.password_updated), Toast.LENGTH_SHORT).show()
        } else {
            passwordViewModel.insertPassword(passwordEntry)
            Toast.makeText(this, getString(R.string.password_saved), Toast.LENGTH_SHORT).show()
        }
        
        finish()
    }
    
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
