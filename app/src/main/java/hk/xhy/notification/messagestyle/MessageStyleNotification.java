package hk.xhy.notification.messagestyle;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.Person;
import androidx.core.app.RemoteInput;
import androidx.core.app.TaskStackBuilder;
import androidx.core.content.ContextCompat;

import hk.xhy.notification.R;
import hk.xhy.notification.inbox.InboxMainActivity;
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
public class MessageStyleNotification extends BaseNotification<MockDatabase.MessagingStyleCommsAppData> {

    public static final int NOTIFICATION_ID = 4;

    public MessageStyleNotification(Context context, MockDatabase.MessagingStyleCommsAppData data) {
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
        MockDatabase.MessagingStyleCommsAppData messagingStyleCommsAppData = getData();

        // 2. Build the NotificationCompat.Style (MESSAGING_STYLE).
        String contentTitle = messagingStyleCommsAppData.getContentTitle();
        NotificationCompat.MessagingStyle messagingStyle =
                new NotificationCompat.MessagingStyle(messagingStyleCommsAppData.getMe())
                        /*
                         * <p>This API's behavior was changed in SDK version
                         * {@link Build.VERSION_CODES#P}. If your application's target version is
                         * less than {@link Build.VERSION_CODES#P}, setting a conversation title to
                         * a non-null value will make {@link #isGroupConversation()} return
                         * {@code true} and passing {@code null} will make it return {@code false}.
                         * This behavior can be overridden by calling
                         * {@link #setGroupConversation(boolean)} regardless of SDK version.
                         * In {@code P} and above, this method does not affect group conversation
                         * settings.
                         *
                         * In our case, we use the same title.
                         */
                        .setConversationTitle(contentTitle);


        // Adds all Messages.
        // Note: Messages include the text, timestamp, and sender.
        for (NotificationCompat.MessagingStyle.Message message : messagingStyleCommsAppData.getMessages()) {
            messagingStyle.addMessage(message);
        }

        messagingStyle.setGroupConversation(messagingStyleCommsAppData.isGroupConversation());

// 3. Set up main Intent for notification.
        Intent notifyIntent = new Intent(this, MessagingMainActivity.class);

        // When creating your Intent, you need to take into account the back state, i.e., what
        // happens after your Activity launches and the user presses the back button.

        // There are two options:
        //      1. Regular activity - You're starting an Activity that's part of the application's
        //      normal workflow.

        //      2. Special activity - The user only sees this Activity if it's started from a
        //      notification. In a sense, the Activity extends the notification by providing
        //      information that would be hard to display in the notification itself.

        // Even though this sample's MainActivity doesn't link to the Activity this Notification
        // launches directly, i.e., it isn't part of the normal workflow, a chat app generally
        // always links to individual conversations as part of the app flow, so we will follow
        // option 1.

        // For an example of option 2, check out the BIG_TEXT_STYLE example.

        // For more information, check out our dev article:
        // https://developer.android.com/training/notify-user/navigation.html

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack
        stackBuilder.addParentStack(MessagingMainActivity.class);
        // Adds the Intent to the top of the stack
        stackBuilder.addNextIntent(notifyIntent);
        // Gets a PendingIntent containing the entire back stack
        PendingIntent mainPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
//                PendingIntent.getActivity(
//                        this,
//                        0,
//                        notifyIntent,
//                        PendingIntent.FLAG_UPDATE_CURRENT
//                );

        // 4. Set up RemoteInput, so users can input (keyboard and voice) from notification.

        // Note: For API <24 (M and below) we need to use an Activity, so the lock-screen present
        // the auth challenge. For API 24+ (N and above), we use a Service (could be a
        // BroadcastReceiver), so the user can input from Notification or lock-screen (they have
        // choice to allow) without leaving the notification.

        // Create the RemoteInput specifying this key.
        String replyLabel = getString(R.string.reply_label);
        RemoteInput remoteInput = new RemoteInput.Builder(MessagingIntentService.EXTRA_REPLY)
                .setLabel(replyLabel)
                // Use machine learning to create responses based on previous messages.
                .setChoices(messagingStyleCommsAppData.getReplyChoicesBasedOnLastMessage())
                .build();

        // Pending intent =
        //      API <24 (M and below): activity so the lock-screen presents the auth challenge.
        //      API 24+ (N and above): this should be a Service or BroadcastReceiver.
        PendingIntent replyActionPendingIntent;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Intent intent = new Intent(this, MessagingIntentService.class);
            intent.setAction(MessagingIntentService.ACTION_REPLY);
            replyActionPendingIntent = PendingIntent.getService(this, 0, intent, 0);

        } else {
            replyActionPendingIntent = mainPendingIntent;
        }

        NotificationCompat.Action replyAction =
                new NotificationCompat.Action.Builder(
                        R.drawable.ic_reply_white_18dp,
                        replyLabel,
                        replyActionPendingIntent)
                        .addRemoteInput(remoteInput)
                        // Informs system we aren't bringing up our own custom UI for a reply
                        // action.
                        .setShowsUserInterface(false)
                        // Allows system to generate replies by context of conversation.
                        .setAllowGeneratedReplies(true)
                        .setSemanticAction(NotificationCompat.Action.SEMANTIC_ACTION_REPLY)
                        .build();


        // 5. Build and issue the notification.

        // Because we want this to be a new notification (not updating current notification), we
        // create a new Builder. Later, we update this same notification, so we need to save this
        // Builder globally (as outlined earlier).


        GlobalNotificationBuilder.setNotificationCompatBuilderInstance(NOTIFICATION_ID, mBuilder);

        mBuilder
                // MESSAGING_STYLE sets title and content for API 16 and above devices.
                .setStyle(messagingStyle)
                // Title for API < 16 devices.
                .setContentTitle(contentTitle)
                // Content for API < 16 devices.
                .setContentText(messagingStyleCommsAppData.getContentText())
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

                // Number of new notifications for API <24 (M and below) devices.
                .setSubText(Integer.toString(messagingStyleCommsAppData.getNumberOfNewMessages()))

                .addAction(replyAction)
                .setCategory(Notification.CATEGORY_MESSAGE)

                // Sets priority for 25 and below. For 26 and above, 'priority' is deprecated for
                // 'importance' which is set in the NotificationChannel. The integers representing
                // 'priority' are different from 'importance', so make sure you don't mix them.
                .setPriority(messagingStyleCommsAppData.getPriority())

                // Sets lock-screen visibility for 25 and below. For 26 and above, lock screen
                // visibility is set in the NotificationChannel.
                .setVisibility(messagingStyleCommsAppData.getChannelLockscreenVisibility());

        // If the phone is in "Do not disturb" mode, the user may still be notified if the
        // sender(s) are in a group allowed through "Do not disturb" by the user.
        for (Person name : messagingStyleCommsAppData.getParticipants()) {
            mBuilder.addPerson(name.getUri());
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

}
