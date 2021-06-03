package hk.xhy.notification.group

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import hk.xhy.notification.R
import hk.xhy.notification.utils.BaseNotification

/**
 * User: xuhaoyang
 * mail: xuhaoyang3x@gmail.com
 * Date: 2021/6/3
 * Time: 11:38 上午
 * Description: No Description
 */
class GroupNotification(context: Context?, data: GroupData) :
    BaseNotification<GroupData>(context, data) {

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                var channel = manager.getNotificationChannel(currentChannelId())
                if (channel?.importance != IMPORTANCE_HIGH) {
                    manager.deleteNotificationChannel(currentChannelId())
                    channel = NotificationChannel(
                        currentChannelId(),
                        currentChannelName(),
                        NotificationManager.IMPORTANCE_DEFAULT
                    )
                    channel.group = currentGroupId()
                    configureChannel(channel)
                    manager.createNotificationChannel(channel)
                    Log.i(TAG(), "initChannel: reset")
                }
            } catch (e: Exception) {
                Log.e(TAG(), "init: ", e)
            }
        }
    }

    override fun TAG(): String {
        return "Group"
    }

    override fun currentGroupId(): String {
        return data.channelGroupId
    }

    override fun currentGroupName(): String {
        return data.channelGroupName
    }

    override fun currentChannelId(): String {
        return data.channelId
    }

    override fun currentChannelName(): String {
        return data.channelName
    }

    fun show2() {
        val notification1 = NotificationCompat.Builder(this, currentChannelId())
            .setSmallIcon(R.drawable.ic_mark_email_unread)
            .setContentTitle("email 3 ")
            .setContentText("66666...")
            .setGroup(data.groupKey)
            .build()
        manager.notify(103, notification1)
    }

    override fun show() {
        val notification1 = NotificationCompat.Builder(this, currentChannelId())
            .setSmallIcon(R.drawable.ic_mark_email_unread)
            .setContentTitle("email 1 ")
            .setContentText("You will not believe...")
            .setGroup(data.groupKey)
            .build()

        val notification2 = NotificationCompat.Builder(this, currentChannelId())
            .setSmallIcon(R.drawable.ic_mark_email_unread)
            .setContentTitle("email 2")
            .setContentText("Please join us to celebrate the...")
            .setGroup(data.groupKey)
            .build()

        val summaryNotification = NotificationCompat.Builder(this, currentChannelId())
            .setContentTitle("email all")
            //set content text to support devices running API level < 24
            .setContentText("Two new messages")
            .setSmallIcon(R.drawable.ic_mark_email_unread)
            //build summary info into InboxStyle template
            .setStyle(
                NotificationCompat.InboxStyle()
                    .addLine("Alex Faarborg Check this out")
                    .addLine("Jeff Chang Launch Party")
                    .setBigContentTitle("2 new messages")
                    .setSummaryText("janedoe@example.com")
            )
            //specify which group this notification belongs to
            .setGroup(data.groupKey)
            //set this notification as the summary for the group
            .setGroupSummary(true)
            .build()

        manager.apply {
            notify(100, notification1)
            notify(101, notification2)
            notify(data.summaryId, summaryNotification)
        }
    }

    override fun configureNotify(mBuilder: NotificationCompat.Builder) {

    }

    @SuppressLint("WrongConstant")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun configureChannel(channel: NotificationChannel) {
        channel.importance = IMPORTANCE_HIGH
        channel.enableVibration(true)
        channel.lightColor = Color.GREEN
        channel.enableLights(true)
        channel.lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
    }
}