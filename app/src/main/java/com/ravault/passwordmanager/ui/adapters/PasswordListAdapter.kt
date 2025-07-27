package com.ravault.passwordmanager.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ravault.passwordmanager.R
import com.ravault.passwordmanager.data.models.PasswordEntry

class PasswordListAdapter(
    private val onItemClick: (PasswordEntry) -> Unit
) : ListAdapter<PasswordEntry, PasswordListAdapter.PasswordViewHolder>(PasswordDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PasswordViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_password, parent, false)
        return PasswordViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: PasswordViewHolder, position: Int) {
        val passwordEntry = getItem(position)
        holder.bind(passwordEntry)
        holder.itemView.setOnClickListener {
            onItemClick(passwordEntry)
        }
    }
    
    class PasswordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.tv_title)
        private val usernameTextView: TextView = itemView.findViewById(R.id.tv_username)
        private val websiteTextView: TextView = itemView.findViewById(R.id.tv_website)
        
        fun bind(passwordEntry: PasswordEntry) {
            titleTextView.text = passwordEntry.title
            
            if (!passwordEntry.username.isNullOrBlank()) {
                usernameTextView.text = passwordEntry.username
                usernameTextView.visibility = View.VISIBLE
            } else {
                usernameTextView.visibility = View.GONE
            }
            
            if (!passwordEntry.websiteUrl.isNullOrBlank()) {
                websiteTextView.text = passwordEntry.websiteUrl
                websiteTextView.visibility = View.VISIBLE
            } else {
                websiteTextView.visibility = View.GONE
            }
        }
    }
    
    class PasswordDiffCallback : DiffUtil.ItemCallback<PasswordEntry>() {
        override fun areItemsTheSame(oldItem: PasswordEntry, newItem: PasswordEntry): Boolean {
            return oldItem.id == newItem.id
        }
        
        override fun areContentsTheSame(oldItem: PasswordEntry, newItem: PasswordEntry): Boolean {
            return oldItem == newItem
        }
    }
}
