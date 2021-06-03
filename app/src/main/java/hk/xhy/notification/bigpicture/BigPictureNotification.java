package hk.xhy.notification.bigpicture;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.RemoteInput;
import androidx.core.app.TaskStackBuilder;
import androidx.core.content.ContextCompat;

import hk.xhy.notification.R;
import hk.xhy.notification.bigtext.BigTextIntentService;
import hk.xhy.notification.bigtext.BigTextMainActivity;
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
public class BigPictureNotification extends BaseNotification<MockDatabase.BigPictureStyleSocialAppData> {

    public static final int NOTIFICATION_ID = 2;

    public BigPictureNotification(Context context, MockDatabase.BigPictureStyleSocialAppData data) {
        super(context, data);
    }

    @Override
    protected String TAG() {
        return "BigPicture";
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
        Log.w("DEBUG", "configureNotify: " + Thread.currentThread().getName());
        // 2. Build the BIG_PICTURE_STYLE.
        NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle()
                // Provides the bitmap for the BigPicture notification.
                .bigPicture(
                        BitmapFactory.decodeResource(
                                getResources(),
                                getData().getBigImage()
                        ))
                // Overrides ContentTitle in the big form of the template.
                .setBigContentTitle(getData().getBigContentTitle())
                // Summary line after the detail section in the big form of the template.
                .setSummaryText(getData().getSummaryText());

        // 3. Set up main Intent for notification.
        Intent mainIntent = new Intent(this, BigPictureSocialMainActivity.class);

        // When creating your Intent, you need to take into account the back state, i.e., what
        // happens after your Activity launches and the user presses the back button.

        // There are two options:
        //      1. Regular activity - You're starting an Activity that's part of the application's
        //      normal workflow.

        //      2. Special activity - The user only sees this Activity if it's started from a
        //      notification. In a sense, the Activity extends the notification by providing
        //      information that would be hard to display in the notification itself.

        // Even though this sample's MainActivity doesn't link to the Activity this Notification
        // launches directly, i.e., it isn't part of the normal workflow, a social app generally
        // always links to individual posts as part of the app flow, so we will follow option 1.

        // For an example of option 2, check out the BIG_TEXT_STYLE example.

        // For more information, check out our dev article:
        // https://developer.android.com/training/notify-user/navigation.html

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack.
        stackBuilder.addParentStack(BigPictureSocialMainActivity.class);
        // Adds the Intent to the top of the stack.
        stackBuilder.addNextIntent(mainIntent);
        // Gets a PendingIntent containing the entire back stack.
        //todo 参考https://www.jianshu.com/p/678e2322fd41作用
        PendingIntent mainPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
//                PendingIntent.getActivity(
//                        this,
//                        0,
//                        mainIntent,
//                        PendingIntent.FLAG_UPDATE_CURRENT
//                );

        // 4. Set up RemoteInput, so users can input (keyboard and voice) from notification.

        // Note: For API <24 (M and below) we need to use an Activity, so the lock-screen presents
        // the auth challenge. For API 24+ (N and above), we use a Service (could be a
        // BroadcastReceiver), so the user can input from Notification or lock-screen (they have
        // choice to allow) without leaving the notification.

        // Create the RemoteInput.
        String replyLabel = getString(R.string.reply_label);
        RemoteInput remoteInput =
                new RemoteInput.Builder(BigPictureSocialIntentService.EXTRA_COMMENT)
                        .setLabel(replyLabel)
                        // List of quick response choices for any wearables paired with the phone
                        .setChoices(getData().getPossiblePostResponses())
                        .build();
        // Pending intent =
        //      API <24 (M and below): activity so the lock-screen presents the auth challenge
        //      API 24+ (N and above): this should be a Service or BroadcastReceiver
        PendingIntent replyActionPendingIntent;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Intent intent = new Intent(this, BigPictureSocialIntentService.class);
            intent.setAction(BigPictureSocialIntentService.ACTION_COMMENT);
            replyActionPendingIntent = PendingIntent.getService(this, 0, intent, 0);

        } else {
            replyActionPendingIntent = mainPendingIntent;
        }

        NotificationCompat.Action replyAction =
                new NotificationCompat.Action.Builder(
                        R.drawable.ic_reply_white_18dp,
                        replyLabel,
                        replyActionPendingIntent
                )
                        .addRemoteInput(remoteInput)
                        .build();
        // 5. Build and issue the notification.

        // Because we want this to be a new notification (not updating a previous notification), we
        // create a new Builder. Later, we use the same global builder to get back the notification
        // we built here for a comment on the post.

        GlobalNotificationBuilder.setNotificationCompatBuilderInstance(NOTIFICATION_ID, mBuilder);

        mBuilder
                // BIG_PICTURE_STYLE sets title and content for API 16 (4.1 and after).
                .setStyle(bigPictureStyle)
                // Title for API <16 (4.0 and below) devices.
                .setContentTitle(getData().getContentTitle())
                // Content for API <24 (7.0 and below) devices.
                .setContentText(getData().getContentText())
                .setSmallIcon(R.drawable.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(
                        getResources(),
                        R.drawable.ic_person_black_48dp
                ))
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

                .setSubText(Integer.toString(1))
                .addAction(replyAction)
                .setCategory(Notification.CATEGORY_SOCIAL)

                // Sets priority for 25 and below. For 26 and above, 'priority' is deprecated for
                // 'importance' which is set in the NotificationChannel. The integers representing
                // 'priority' are different from 'importance', so make sure you don't mix them.
                .setPriority(getData().getPriority())

                // Sets lock-screen visibility for 25 and below. For 26 and above, lock screen
                // visibility is set in the NotificationChannel.
                .setVisibility(getData().getChannelLockscreenVisibility());

        // If the phone is in "Do not disturb mode, the user will still be notified if
        // the sender(s) is starred as a favorite.
        for (String name : getData().getParticipants()) {
            mBuilder.addPerson(name);
        }
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

    public void cancel() {
        manager.cancel(NOTIFICATION_ID);
    }

}
