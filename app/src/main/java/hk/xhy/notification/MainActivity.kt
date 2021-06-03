package hk.xhy.notification

import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import hk.xhy.notification.channelmanager.ChannelManagerActivity
import hk.xhy.notification.utils.intent
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    companion object {
        const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnGoogleExample.setOnClickListener {
            startActivity(GoogleExampleActivity::class.intent)
        }

        btnGroupExample.setOnClickListener {
            startActivity(GroupActivity::class.intent)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            btnChannelManager.visibility = View.VISIBLE
            btnChannelManager.setOnClickListener {
                openChannelManager()
            }
        } else {
            btnChannelManager.visibility = View.GONE
        }


        btnTest.setOnClickListener {
            val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
            }
            startActivity(intent)
            test()
        }

    }

    private fun openChannelManager() {
        startActivity(ChannelManagerActivity::class.intent)
    }

    fun test() {
        val manager = getSystemService(
            NOTIFICATION_SERVICE
        ) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            for (channel in manager.notificationChannels) {
                Log.i(TAG, "test:  ${channel.toJson()}")
            }
        }
    }

}