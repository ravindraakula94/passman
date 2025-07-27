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

class PasswordAdapter(
    private val onItemClick: (PasswordEntry) -> Unit
) : ListAdapter<PasswordEntry, PasswordAdapter.PasswordViewHolder>(PasswordDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PasswordViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_password, parent, false)
        return PasswordViewHolder(view)
    }

    override fun onBindViewHolder(holder: PasswordViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PasswordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle: TextView = itemView.findViewById(R.id.tv_title)
        private val tvUsername: TextView = itemView.findViewById(R.id.tv_username)
        private val tvWebsite: TextView = itemView.findViewById(R.id.tv_website)

        fun bind(passwordEntry: PasswordEntry) {
            tvTitle.text = passwordEntry.title
            
            if (!passwordEntry.username.isNullOrEmpty()) {
                tvUsername.text = passwordEntry.username
                tvUsername.visibility = View.VISIBLE
            } else {
                tvUsername.visibility = View.GONE
            }
            
            if (!passwordEntry.websiteUrl.isNullOrEmpty()) {
                tvWebsite.text = passwordEntry.websiteUrl
                tvWebsite.visibility = View.VISIBLE
            } else {
                tvWebsite.visibility = View.GONE
            }
            
            itemView.setOnClickListener {
                onItemClick(passwordEntry)
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
