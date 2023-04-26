package com.paulmerchants.gold.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.paulmerchants.gold.databinding.ItemNotificatrionsBinding
import com.paulmerchants.gold.model.Notifications

class NotificationAdapter :
    ListAdapter<Notifications, NotificationAdapter.NotificationViewHolder>(DIFF_CALLBACK) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = NotificationViewHolder(
        ItemNotificatrionsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val DIFF_CALLBACK =
            object : DiffUtil.ItemCallback<Notifications>() {
                override fun areItemsTheSame(
                    oldItem: Notifications,
                    newItem: Notifications,
                ): Boolean =
                    oldItem.id == newItem.id

                override fun areContentsTheSame(
                    oldItem: Notifications,
                    newItem: Notifications,
                ): Boolean =
                    oldItem == newItem
            }
    }

    inner class NotificationViewHolder(private val binding: ItemNotificatrionsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(notifications: Notifications) {
            binding.messageTv.text = notifications.notificationMsg
        }

    }


}