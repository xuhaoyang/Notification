package hk.xhy.notification.channelmanager

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import hk.xhy.notification.R
import hk.xhy.notification.ui.base.BaseActivity
import hk.xhy.notification.ui.base.rv.RecycleViewDivider
import kotlinx.android.synthetic.main.activity_channel_manager.*

@RequiresApi(Build.VERSION_CODES.O)
class ChannelManagerActivity : BaseActivity(), ChannelAdapter.Callback {
    private val manager: NotificationManager by lazy {
        getSystemService(
            NOTIFICATION_SERVICE
        ) as NotificationManager
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_channel_manager)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            list.layoutManager = LinearLayoutManager(this)
            list.addItemDecoration(
                RecycleViewDivider(
                    this,
                    RecycleViewDivider.VERTICAL,
                    R.drawable.common_item_divider
                )
            )

            val adapter = ChannelAdapter(this, this)
            list.adapter = adapter
            adapter.submitList(manager.notificationChannels)

        } else {
            finish()
        }
    }

    override fun remove(adapter: ChannelAdapter, channel: NotificationChannel) {
        manager.deleteNotificationChannel(channel.id)
        adapter.submitList(manager.notificationChannels)
    }
}