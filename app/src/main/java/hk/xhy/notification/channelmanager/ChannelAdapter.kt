package hk.xhy.notification.channelmanager

import android.app.Notification
import android.app.NotificationChannel
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import hk.xhy.notification.R

/**
 * User: xuhaoyang
 * mail: xuhaoyang3x@gmail.com
 * Date: 2021/6/3
 * Time: 4:02 下午
 * Description: No Description
 */
@RequiresApi(Build.VERSION_CODES.O)
class ChannelAdapter(private val context: Context, private val callback: Callback) :
    ListAdapter<NotificationChannel, RecyclerView.ViewHolder>(UserDiffCallback()) {

    interface Callback {
        fun remove(adapter: ChannelAdapter, channel: NotificationChannel)
    }

    class ChannelViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.name)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ChannelViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_channel,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        val channelViewHolder = holder as ChannelViewHolder
        channelViewHolder.name.text = item.name
        channelViewHolder.itemView.setOnLongClickListener {
            callback.remove(this, item)
            true
        }
    }

}

@RequiresApi(Build.VERSION_CODES.O)
private class UserDiffCallback : DiffUtil.ItemCallback<NotificationChannel>() {
    override fun areItemsTheSame(
        oldItem: NotificationChannel,
        newItem: NotificationChannel
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: NotificationChannel,
        newItem: NotificationChannel
    ): Boolean {
        return oldItem == newItem
    }
}