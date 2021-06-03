package hk.xhy.notification.inbox;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.core.content.ContextCompat;

import hk.xhy.notification.R;
import hk.xhy.notification.mock.MockDatabase;
import hk.xhy.notification.utils.BaseNotification;
import hk.xhy.notification.utils.GlobalNotificationBuilder;

import static hk.xhy.notification.utils.Constants.GOOGLE_EXAMPLE_GROUP;
import static hk.xhy.notification.utils.Constants.GOOGLE_EXAMPLE_GROUP_NAME;

/**
 * User: xuhaoyang
 * mail: xuhaoyang3x@gmail.com
 * Date: 2021/6/2
 * Time: 3:33 下午
 * Description: No Description
 */
public class InboxNotification extends BaseNotification<MockDatabase.InboxStyleEmailAppData> {

    public static final int NOTIFICATION_ID = 3;

    public InboxNotification(Context context, MockDatabase.InboxStyleEmailAppData data) {
        super(context, data);
    }

    @Override
    protected String TAG() {
        return "Inbox";
    }

    @Override
    protected String currentGroupId() {
        return GOOGLE_EXAMPLE_GROUP;
    }

    @Override
    protected String currentGroupName() {
        return GOOGLE_EXAMPLE_GROUP_NAME;
    }

    @Override
    protected String currentChannelId() {
        return getData().getChannelId();
    }

    @Override
    protected String currentChannelName() {
        return getData().getChannelName().toString();
    }

    @Override
    public void show() {
        manager.notify(NOTIFICATION_ID, getBuilder().build());
    }

    @Override
    protected void configureNotify(NotificationCompat.Builder mBuilder) {
        // TODO: 2021/6/2

        // 2. Build the INBOX_STYLE.
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle()
                // This title is slightly different than regular title, since I know INBOX_STYLE is
                // available.
                .setBigContentTitle(getData().getBigContentTitle())
                .setSummaryText(getData().getSummaryText());

        // Add each summary line of the new emails, you can add up to 5.
        for (String summary : getData().getIndividualEmailSummary()) {
            inboxStyle.addLine(summary);
        }

        // 3. Set up main Intent for notification.
        Intent mainIntent = new Intent(this, InboxMainActivity.class);

        // When creating your Intent, you need to take into account the back state, i.e., what
        // happens after your Activity launches and the user presses the back button.

        // There are two options:
        //      1. Regular activity - You're starting an Activity that's part of the application's
        //      normal workflow.

        //      2. Special activity - The user only sees this Activity if it's started from a
        //      notification. In a sense, the Activity extends the notification by providing
        //      information that would be hard to display in the notification itself.

        // Even though this sample's MainActivity doesn't link to the Activity this Notification
        // launches directly, i.e., it isn't part of the normal workflow, a eamil app generally
        // always links to individual emails as part of the app flow, so we will follow option 1.

        // For an example of option 2, check out the BIG_TEXT_STYLE example.

        // For more information, check out our dev article:
        // https://developer.android.com/training/notify-user/navigation.html

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack.
        stackBuilder.addParentStack(InboxMainActivity.class);
        // Adds the Intent to the top of the stack.
        stackBuilder.addNextIntent(mainIntent);
        // Gets a PendingIntent containing the entire back stack.
        PendingIntent mainPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
//                PendingIntent.getActivity(
//                        this,
//                        0,
//                        mainIntent,
//                        PendingIntent.FLAG_UPDATE_CURRENT
//                );
        // 4. Build and issue the notification.

        // Because we want this to be a new notification (not updating a previous notification), we
        // create a new Builder. However, we don't need to update this notification later, so we
        // will not need to set a global builder for access to the notification later.

        GlobalNotificationBuilder.setNotificationCompatBuilderInstance(NOTIFICATION_ID, mBuilder);
        mBuilder
                // INBOX_STYLE sets title and content for API 16+ (4.1 and after) when the
                // notification is expanded.
                .setStyle(inboxStyle)

                // Title for API <16 (4.0 and below) devices and API 16+ (4.1 and after) when the
                // notification is collapsed.
                .setContentTitle(getData().getContentTitle())

                // Content for API <24 (7.0 and below) devices and API 16+ (4.1 and after) when the
                // notification is collapsed.
                .setContentText(getData().getContentText())
                .setSmallIcon(R.drawable.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(
                        getResources(),
                        R.drawable.ic_person_black_48dp))
                .setContentIntent(mainPendingIntent)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                // Set primary color (important for Wear 2.0 Notifications).
                .setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary))

                // SIDE NOTE: Auto-bundling is enabled for 4 or more notifications on API 24+ (N+)
                // devices and all Wear devices. If you have more than one notification and
                // you prefer a different summary notification, set a group key and create a
                // summary notification via
                // .setGroupSummary(true)
                // .setGroup(GROUP_KEY_YOUR_NAME_HERE)

                // Sets large number at the right-hand side of the notification for API <24 devices.
                .setSubText(Integer.toString(getData().getNumberOfNewEmails()))

                .setCategory(Notification.CATEGORY_EMAIL)

                // Sets priority for 25 and below. For 26 and above, 'priority' is deprecated for
                // 'importance' which is set in the NotificationChannel. The integers representing
                // 'priority' are different from 'importance', so make sure you don't mix them.
                .setPriority(getData().getPriority())

                // Sets lock-screen visibility for 25 and below. For 26 and above, lock screen
                // visibility is set in the NotificationChannel.
                .setVisibility(getData().getChannelLockscreenVisibility());

    }


    // 1. Create/Retrieve Notification Channel for O and beyond devices (26+).
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void configureChannel(NotificationChannel channel) {
        if (channel != null) {
            channel.setImportance(getData().getChannelImportance());
            channel.setDescription(getData().getChannelDescription());
            channel.enableVibration(getData().isChannelEnableVibrate());
            channel.setLockscreenVisibility(getData().getChannelLockscreenVisibility());
        }
    }

}
