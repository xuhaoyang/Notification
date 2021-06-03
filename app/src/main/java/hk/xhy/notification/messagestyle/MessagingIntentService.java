/*
Copyright 2016 The Android Open Source Project

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package hk.xhy.notification.messagestyle;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationCompat.Action;
import androidx.core.app.NotificationCompat.MessagingStyle;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.Person;
import androidx.core.app.RemoteInput;
import androidx.core.app.TaskStackBuilder;
import androidx.core.content.ContextCompat;

import hk.xhy.notification.R;
import hk.xhy.notification.mock.MockDatabase;
import hk.xhy.notification.utils.GlobalNotificationBuilder;


/**
 * Asynchronously handles updating messaging app posts (and active Notification) with replies from
 * user in a conversation. Notification for social app use MessagingStyle.
 */
public class MessagingIntentService extends IntentService {

    private static final String TAG = "MessagingIntentService";

    public static final String ACTION_REPLY =
            "com.example.android.wearable.wear.wearnotifications.handlers.action.REPLY";

    public static final String EXTRA_REPLY =
            "com.example.android.wearable.wear.wearnotifications.handlers.extra.REPLY";

    public MessagingIntentService() {
        super("MessagingIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent(): " + intent);

        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_REPLY.equals(action)) {
                handleActionReply(getMessage(intent));
            }
        }
    }

    /**
     * Handles action for replying to messages from the notification.
     */
    private void handleActionReply(CharSequence replyCharSequence) {
        Log.d(TAG, "handleActionReply(): " + replyCharSequence);

        if (replyCharSequence != null) {

            // TODO: Asynchronously save your message to Database and servers.

            /*
             * You have two options for updating your notification (this class uses approach #2):
             *
             *  1. Use a new NotificationCompatBuilder to create the Notification. This approach
             *  requires you to get *ALL* the information that existed in the previous
             *  Notification (and updates) and pass it to the builder. This is the approach used in
             *  the MainActivity.
             *
             *  2. Use the original NotificationCompatBuilder to create the Notification. This
             *  approach requires you to store a reference to the original builder. The benefit is
             *  you only need the new/updated information. In our case, the reply from the user
             *  which we already have here.
             *
             *  IMPORTANT NOTE: You shouldn't save/modify the resulting Notification object using
             *  its member variables and/or legacy APIs. If you want to retain anything from update
             *  to update, retain the Builder as option 2 outlines.
             */

            // Retrieves NotificationCompat.Builder used to create initial Notification
            NotificationCompat.Builder notificationCompatBuilder =
                    GlobalNotificationBuilder.getNotificationCompatBuilderInstance(MessageStyleNotification.NOTIFICATION_ID);

            // Recreate builder from persistent state if app process is killed
            if (notificationCompatBuilder == null) {
                // Note: New builder set globally in the method
                notificationCompatBuilder = new MessageStyleNotification(this, MockDatabase.getMessagingStyleData(this)).getBuilder();
            }

            // Since we are adding to the MessagingStyle, we need to first retrieve the
            // current MessagingStyle from the Notification itself.
            Notification notification = notificationCompatBuilder.build();
            MessagingStyle messagingStyle =
                    MessagingStyle.extractMessagingStyleFromNotification(
                            notification);

            // Add new message to the MessagingStyle. Set last parameter to null for responses
            // from user.
            messagingStyle.addMessage(replyCharSequence, System.currentTimeMillis(), (Person) null);

            // Updates the Notification
            notification = notificationCompatBuilder.setStyle(messagingStyle).build();

            // Pushes out the updated Notification
            NotificationManagerCompat notificationManagerCompat =
                    NotificationManagerCompat.from(getApplicationContext());
            notificationManagerCompat.notify(MessageStyleNotification.NOTIFICATION_ID, notification);
        }
    }

    /*
     * Extracts CharSequence created from the RemoteInput associated with the Notification.
     */
    private CharSequence getMessage(Intent intent) {
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        if (remoteInput != null) {
            return remoteInput.getCharSequence(EXTRA_REPLY);
        }
        return null;
    }

}
