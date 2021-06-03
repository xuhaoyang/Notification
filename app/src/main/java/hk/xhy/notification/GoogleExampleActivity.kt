package hk.xhy.notification

import android.os.Bundle
import android.util.Log
import hk.xhy.notification.bigpicture.BigPictureNotification
import hk.xhy.notification.bigpicture.BigPictureSocialIntentService
import hk.xhy.notification.bigtext.BigTextNotification
import hk.xhy.notification.inbox.InboxNotification
import hk.xhy.notification.messagestyle.MessageStyleNotification
import hk.xhy.notification.mock.MockDatabase
import hk.xhy.notification.ui.base.BaseActivity
import kotlinx.android.synthetic.main.activity_google_example.*

class GoogleExampleActivity : BaseActivity() {
    companion object {
        const val TAG = "GoogleExample"
    }

    override fun backDisplay(): Boolean {
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_google_example)

        btnBigTextStyle.setOnClickListener {
            generateBigTextStyleNotification()
        }

        btnBigPicStyle.setOnClickListener {
            generateBigPictureStyleNotification()
        }

        btnInboxStyle.setOnClickListener {
            generateInboxStyleNotification()
        }

        btnMessageStyle.setOnClickListener {
            generateMessagingStyleNotification()
        }
    }


    private fun generateBigTextStyleNotification() {
        Log.d(TAG, "generateBigTextStyleNotification()")

        // Main steps for building a BIG_TEXT_STYLE notification:
        //      0. Get your data
        //      1. Create/Retrieve Notification Channel for O and beyond devices (26+)
        //      2. Build the BIG_TEXT_STYLE
        //      3. Set up main Intent for notification
        //      4. Create additional Actions for the Notification
        //      5. Build and issue the notification

        // 0. Get your data (everything unique per Notification).

        // Main steps for building a BIG_TEXT_STYLE notification:
        //      0. Get your data
        //      1. Create/Retrieve Notification Channel for O and beyond devices (26+)
        //      2. Build the BIG_TEXT_STYLE
        //      3. Set up main Intent for notification
        //      4. Create additional Actions for the Notification
        //      5. Build and issue the notification

        // 0. Get your data (everything unique per Notification).
        val bigTextStyleReminderAppData: MockDatabase.BigTextStyleReminderAppData =
            MockDatabase.getBigTextStyleData()

        val notification = BigTextNotification(this, bigTextStyleReminderAppData)
        notification.show()
    }


    private fun generateBigPictureStyleNotification() {
        Log.d(TAG, "generateBigPictureStyleNotification()")
        // Main steps for building a BIG_PICTURE_STYLE notification:
        //      0. Get your data
        //      1. Create/Retrieve Notification Channel for O and beyond devices (26+)
        //      2. Build the BIG_PICTURE_STYLE
        //      3. Set up main Intent for notification
        //      4. Set up RemoteInput, so users can input (keyboard and voice) from notification
        //      5. Build and issue the notification

        // 0. Get your data (everything unique per Notification).
        val notification = BigPictureNotification(this, MockDatabase.getBigPictureStyleData())
        notification.show()
    }

    private fun generateInboxStyleNotification() {
        Log.d(TAG, "generateInboxStyleNotification()")

        // Main steps for building a INBOX_STYLE notification:
        //      0. Get your data
        //      1. Create/Retrieve Notification Channel for O and beyond devices (26+)
        //      2. Build the INBOX_STYLE
        //      3. Set up main Intent for notification
        //      4. Build and issue the notification

        val notification = InboxNotification(this, MockDatabase.getInboxStyleData())
        notification.show()
    }

    private fun generateMessagingStyleNotification() {

        Log.d(MainActivity.TAG, "generateMessagingStyleNotification()")

        // Main steps for building a MESSAGING_STYLE notification:
        //      0. Get your data
        //      1. Create/Retrieve Notification Channel for O and beyond devices (26+)
        //      2. Build the MESSAGING_STYLE
        //      3. Set up main Intent for notification
        //      4. Set up RemoteInput (users can input directly from notification)
        //      5. Build and issue the notification
        val notification = MessageStyleNotification(this, MockDatabase.getMessagingStyleData(this))
        notification.show()
    }
}