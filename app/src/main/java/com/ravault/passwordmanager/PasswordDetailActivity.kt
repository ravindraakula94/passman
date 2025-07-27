package com.ravault.passwordmanager

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.ravault.passwordmanager.data.models.PasswordEntry
import com.ravault.passwordmanager.ui.viewmodels.PasswordViewModel
import kotlinx.coroutines.launch

class PasswordDetailActivity : AppCompatActivity() {
    
    private lateinit var passwordViewModel: PasswordViewModel
    private var passwordEntry: PasswordEntry? = null
    private var isPasswordVisible = false
    
    private lateinit var tvTitleValue: TextView
    private lateinit var tvUsernameValue: TextView
    private lateinit var tvPasswordValue: TextView
    private lateinit var tvWebsiteValue: TextView
    private lateinit var tvNotesValue: TextView
    private lateinit var btnCopyUsername: MaterialButton
    private lateinit var btnTogglePassword: MaterialButton
    private lateinit var btnCopyPassword: MaterialButton
    private lateinit var btnEdit: MaterialButton
    private lateinit var btnDelete: MaterialButton
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_detail)
        
        // Set up toolbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        
        // Initialize views
        tvTitleValue = findViewById(R.id.tv_title_value)
        tvUsernameValue = findViewById(R.id.tv_username_value)
        tvPasswordValue = findViewById(R.id.tv_password_value)
        tvWebsiteValue = findViewById(R.id.tv_website_value)
        tvNotesValue = findViewById(R.id.tv_notes_value)
        btnCopyUsername = findViewById(R.id.btn_copy_username)
        btnTogglePassword = findViewById(R.id.btn_toggle_password)
        btnCopyPassword = findViewById(R.id.btn_copy_password)
        btnEdit = findViewById(R.id.btn_edit)
        btnDelete = findViewById(R.id.btn_delete)
        
        // Set up ViewModel
        passwordViewModel = ViewModelProvider(this)[PasswordViewModel::class.java]
        
        // Get password ID from intent
        val passwordId = intent.getLongExtra("PASSWORD_ID", -1)
        if (passwordId != -1L) {
            loadPasswordData(passwordId)
        }
        
        // Set up click listeners
        btnCopyUsername.setOnClickListener { copyToClipboard("username", tvUsernameValue.text.toString()) }
        btnTogglePassword.setOnClickListener { togglePasswordVisibility() }
        btnCopyPassword.setOnClickListener { 
            passwordEntry?.let { copyToClipboard("password", it.password) }
        }
        btnEdit.setOnClickListener { editPassword() }
        btnDelete.setOnClickListener { showDeleteConfirmation() }
    }
    
    private fun loadPasswordData(passwordId: Long) {
        lifecycleScope.launch {
            passwordEntry = passwordViewModel.getPasswordById(passwordId)
            passwordEntry?.let { displayPasswordData(it) }
        }
    }
    
    private fun displayPasswordData(password: PasswordEntry) {
        supportActionBar?.title = password.title
        
        tvTitleValue.text = password.title
        
        if (!password.username.isNullOrEmpty()) {
            tvUsernameValue.text = password.username
            tvUsernameValue.visibility = View.VISIBLE
            btnCopyUsername.visibility = View.VISIBLE
            findViewById<TextView>(R.id.tv_username_label).visibility = View.VISIBLE
        } else {
            tvUsernameValue.visibility = View.GONE
            btnCopyUsername.visibility = View.GONE
            findViewById<TextView>(R.id.tv_username_label).visibility = View.GONE
        }
        
        updatePasswordDisplay()
        
        if (!password.websiteUrl.isNullOrEmpty()) {
            tvWebsiteValue.text = password.websiteUrl
            tvWebsiteValue.visibility = View.VISIBLE
            findViewById<TextView>(R.id.tv_website_label).visibility = View.VISIBLE
        } else {
            tvWebsiteValue.visibility = View.GONE
            findViewById<TextView>(R.id.tv_website_label).visibility = View.GONE
        }
        
        if (!password.notes.isNullOrEmpty()) {
            tvNotesValue.text = password.notes
            tvNotesValue.visibility = View.VISIBLE
            findViewById<TextView>(R.id.tv_notes_label).visibility = View.VISIBLE
        } else {
            tvNotesValue.visibility = View.GONE
            findViewById<TextView>(R.id.tv_notes_label).visibility = View.GONE
        }
    }
    
    private fun togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible
        updatePasswordDisplay()
    }
    
    private fun updatePasswordDisplay() {
        passwordEntry?.let { password ->
            if (isPasswordVisible) {
                tvPasswordValue.text = password.password
                btnTogglePassword.setIconResource(R.drawable.ic_visibility_off)
            } else {
                tvPasswordValue.text = "•".repeat(password.password.length)
                btnTogglePassword.setIconResource(R.drawable.ic_visibility)
            }
        }
    }
    
    private fun copyToClipboard(label: String, text: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this, getString(R.string.copied_to_clipboard), Toast.LENGTH_SHORT).show()
    }
    
    private fun editPassword() {
        passwordEntry?.let {
            val intent = Intent(this, AddEditPasswordActivity::class.java)
            intent.putExtra("PASSWORD_ID", it.id)
            startActivity(intent)
        }
    }
    
    private fun showDeleteConfirmation() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.delete_password_title))
            .setMessage(getString(R.string.delete_password_message))
            .setPositiveButton(getString(R.string.confirm_delete)) { _, _ ->
                deletePassword()
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }
    
    private fun deletePassword() {
        passwordEntry?.let {
            passwordViewModel.deletePassword(it)
            Toast.makeText(this, getString(R.string.password_deleted), Toast.LENGTH_SHORT).show()
            finish()
        }
    }
    
    override fun onResume() {
        super.onResume()
        // Reload data when returning from edit
        val passwordId = intent.getLongExtra("PASSWORD_ID", -1)
        if (passwordId != -1L) {
            loadPasswordData(passwordId)
        }
    }
    
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
