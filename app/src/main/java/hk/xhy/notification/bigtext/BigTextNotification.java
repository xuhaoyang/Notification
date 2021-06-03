package hk.xhy.notification.bigtext;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import hk.xhy.notification.R;
import hk.xhy.notification.mock.MockDatabase;
import hk.xhy.notification.bigtext.BigTextMainActivity;
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
public class BigTextNotification extends BaseNotification<MockDatabase.BigTextStyleReminderAppData> {

    public static final int NOTIFICATION_ID = 1;

    public BigTextNotification(Context context, MockDatabase.BigTextStyleReminderAppData data) {
        super(context, data);
    }

    @Override
    protected String TAG() {
        return "BigText";
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
        // 2. Build the BIG_TEXT_STYLE.
        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle()
                // Overrides ContentText in the big form of the template.
                .bigText(getData().getBigText())
                // Overrides ContentTitle in the big form of the template.
                .setBigContentTitle(getData().getBigContentTitle())
                // Summary line after the detail section in the big form of the template.
                // Note: To improve readability, don't overload the user with info. If Summary Text
                // doesn't add critical information, you should skip it.
                .setSummaryText(getData().getSummaryText());

        // 3. Set up main Intent for notification.
        Intent notifyIntent = new Intent(this, BigTextMainActivity.class);

        // When creating your Intent, you need to take into account the back state, i.e., what
        // happens after your Activity launches and the user presses the back button.

        // There are two options:
        //      1. Regular activity - You're starting an Activity that's part of the application's
        //      normal workflow.

        //      2. Special activity - The user only sees this Activity if it's started from a
        //      notification. In a sense, the Activity extends the notification by providing
        //      information that would be hard to display in the notification itself.

        // For the BIG_TEXT_STYLE notification, we will consider the activity launched by the main
        // Intent as a special activity, so we will follow option 2.

        // For an example of option 1, check either the MESSAGING_STYLE or BIG_PICTURE_STYLE
        // examples.

        // For more information, check out our dev article:
        // https://developer.android.com/training/notify-user/navigation.html

        // Sets the Activity to start in a new, empty task
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent notifyPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        notifyIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        //4. Create additional Actions (Intents) for the Notification.

        // In our case, we create two additional actions: a Snooze action and a Dismiss action.
        // Snooze Action.
        Intent snoozeIntent = new Intent(this, BigTextIntentService.class);
        snoozeIntent.setAction(BigTextIntentService.ACTION_SNOOZE);

        PendingIntent snoozePendingIntent = PendingIntent.getService(this, 0, snoozeIntent, 0);
        NotificationCompat.Action snoozeAction =
                new NotificationCompat.Action.Builder(
                        R.drawable.ic_alarm_white_48dp,
                        "Snooze",
                        snoozePendingIntent
                )
                        .build();

        // Dismiss Action.
        Intent dismissIntent = new Intent(this, BigTextIntentService.class);
        dismissIntent.setAction(BigTextIntentService.ACTION_DISMISS);

        PendingIntent dismissPendingIntent = PendingIntent.getService(this, 0, dismissIntent, 0);
        NotificationCompat.Action dismissAction =
                new NotificationCompat.Action.Builder(
                        R.drawable.ic_cancel_white_48dp,
                        "Dismiss",
                        dismissPendingIntent
                )
                        .build();

        // 5. Build and issue the notification.

        // Because we want this to be a new notification (not updating a previous notification), we
        // create a new Builder. Later, we use the same global builder to get back the notification
        // we built here for the snooze action, that is, canceling the notification and relaunching
        // it several seconds later.

        // Notification Channel Id is ignored for Android pre O (26).

        GlobalNotificationBuilder.setNotificationCompatBuilderInstance(NOTIFICATION_ID, mBuilder);
        mBuilder
                // BIG_TEXT_STYLE sets title and content for API 16 (4.1 and after).
                .setStyle(bigTextStyle)
                // Title for API <16 (4.0 and below) devices.
                .setContentTitle(getData().getContentTitle())
                // Content for API <24 (7.0 and below) devices.
                .setContentText(getData().getContentText())
                .setSmallIcon(R.drawable.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(
                        getResources(),
                        R.drawable.ic_alarm_white_48dp
                ))
                .setContentIntent(notifyPendingIntent)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                // Set primary color (important for Wear 2.0 Notifications).
                .setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary))

                // SIDE NOTE: Auto-bundling is enabled for 4 or more notifications on API 24+ (N+)
                // devices and all Wear devices. If you have more than one notification and
                // you prefer a different summary notification, set a group key and create a
                // summary notification via
                // .setGroupSummary(true)
                // .setGroup(GROUP_KEY_YOUR_NAME_HERE)

                .setCategory(Notification.CATEGORY_REMINDER)

                // Sets priority for 25 and below. For 26 and above, 'priority' is deprecated for
                // 'importance' which is set in the NotificationChannel. The integers representing
                // 'priority' are different from 'importance', so make sure you don't mix them.
                .setPriority(getData().getPriority())

                // Sets lock-screen visibility for 25 and below. For 26 and above, lock screen
                // visibility is set in the NotificationChannel.
                .setVisibility(getData().getChannelLockscreenVisibility())

                // Adds additional actions specified above.
                .addAction(snoozeAction)
                .addAction(dismissAction);
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
