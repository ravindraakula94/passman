package com.ravault.passwordmanager

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.ravault.passwordmanager.ui.adapters.PasswordAdapter
import com.ravault.passwordmanager.ui.viewmodels.PasswordViewModel
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    
    private lateinit var passwordViewModel: PasswordViewModel
    private lateinit var passwordAdapter: PasswordAdapter
    private lateinit var rvPasswords: RecyclerView
    private lateinit var tvEmptyState: android.widget.TextView
    private lateinit var fabAddPassword: FloatingActionButton
    private var isSearching = false
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // Set up toolbar
        setSupportActionBar(findViewById(R.id.toolbar))
        
        // Initialize views
        rvPasswords = findViewById(R.id.rv_passwords)
        tvEmptyState = findViewById(R.id.tv_empty_state)
        fabAddPassword = findViewById(R.id.fab_add_password)
        
        // Set up RecyclerView
        passwordAdapter = PasswordAdapter { passwordEntry ->
            val intent = Intent(this, PasswordDetailActivity::class.java)
            intent.putExtra("PASSWORD_ID", passwordEntry.id)
            startActivity(intent)
        }
        
        rvPasswords.apply {
            adapter = passwordAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }
        
        // Set up ViewModel
        passwordViewModel = ViewModelProvider(this)[PasswordViewModel::class.java]
        
        // Observe password list
        passwordViewModel.allPasswords.observe(this) { passwords ->
            passwords?.let { 
                passwordAdapter.submitList(it)
                updateEmptyState(it.isEmpty())
            }
        }
        
        // Set up FAB
        fabAddPassword.setOnClickListener {
            val intent = Intent(this, AddEditPasswordActivity::class.java)
            startActivity(intent)
        }
    }
    
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        
        // Set up search functionality
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as? androidx.appcompat.widget.SearchView
        
        searchView?.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
            
            override fun onQueryTextChange(newText: String?): Boolean {
                lifecycleScope.launch {
                    if (newText.isNullOrEmpty()) {
                        // Stop searching, return to normal live data observation
                        if (isSearching) {
                            isSearching = false
                            // Re-observe the live data when search is cleared
                            passwordViewModel.allPasswords.observe(this@MainActivity) { passwords ->
                                passwords?.let { 
                                    passwordAdapter.submitList(it)
                                    updateEmptyState(it.isEmpty())
                                }
                            }
                        }
                    } else {
                        // Search passwords - use suspend function
                        isSearching = true
                        val searchResults = passwordViewModel.searchPasswords(newText)
                        passwordAdapter.submitList(searchResults)
                        updateEmptyState(searchResults.isEmpty())
                    }
                }
                return true
            }
        })
        
        return true
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                // Return to authentication screen
                val intent = Intent(this, AuthenticationActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    
    private fun updateEmptyState(isEmpty: Boolean) {
        if (isEmpty) {
            tvEmptyState.visibility = View.VISIBLE
            rvPasswords.visibility = View.GONE
        } else {
            tvEmptyState.visibility = View.GONE
            rvPasswords.visibility = View.VISIBLE
        }
    }
}
