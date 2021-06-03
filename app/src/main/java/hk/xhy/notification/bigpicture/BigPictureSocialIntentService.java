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
package hk.xhy.notification.bigpicture;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.app.Notification;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.Person;
import androidx.core.app.RemoteInput;

import java.util.ArrayList;

import hk.xhy.notification.R;
import hk.xhy.notification.mock.MockDatabase;
import hk.xhy.notification.utils.GlobalNotificationBuilder;


/**
 * Asynchronously handles updating social app posts (and active Notification) with comments from
 * user. Notification for social app use BigPictureStyle.
 */
public class BigPictureSocialIntentService extends IntentService {

    private static final String TAG = "BigPictureService";

    public static final String ACTION_COMMENT =
            "com.example.android.wearable.wear.wearnotifications.handlers.action.COMMENT";

    public static final String EXTRA_COMMENT =
            "com.example.android.wearable.wear.wearnotifications.handlers.extra.COMMENT";

    private static Person you = new Person.Builder().setName("You").build();

    public BigPictureSocialIntentService() {
        super("BigPictureSocialIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent(): " + intent);

        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_COMMENT.equals(action)) {
                handleActionComment(getMessage(intent));
            }
        }
    }

    /**
     * Handles action for adding a comment from the notification.
     */
    @SuppressLint("RestrictedApi")
    private void handleActionComment(CharSequence comment) {
        Log.d(TAG, "handleActionComment(): " + comment);

        if (comment != null) {

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
             *  you only need the new/updated information. In our case, the comment from the user
             *  regarding the post (which we already have here).
             *
             *  IMPORTANT NOTE: You shouldn't save/modify the resulting Notification object using
             *  its member variables and/or legacy APIs. If you want to retain anything from update
             *  to update, retain the Builder as option 2 outlines.
             */

            // Retrieves NotificationCompat.Builder used to create initial Notification
            NotificationCompat.Builder notificationCompatBuilder =
                    GlobalNotificationBuilder.getNotificationCompatBuilderInstance(BigPictureNotification.NOTIFICATION_ID);

            // Recreate builder from persistent state if app process is killed
            if (notificationCompatBuilder == null) {
                // Note: New builder set globally in the method
                notificationCompatBuilder = new BigPictureNotification(this, MockDatabase.getBigPictureStyleData()).getBuilder();
            }

            if (Build.VERSION.SDK_INT < 30) {
                // Updates active Notification
                Notification updatedNotification = notificationCompatBuilder
                        // Adds a line and comment below content in Notification
                        .setRemoteInputHistory(new CharSequence[]{comment.toString()})
                        .build();

                // Pushes out the updated Notification
                NotificationManagerCompat notificationManagerCompat =
                        NotificationManagerCompat.from(getApplicationContext());
                notificationManagerCompat.notify(BigPictureNotification.NOTIFICATION_ID, updatedNotification);
            } else {

                Notification                      notification   = notificationCompatBuilder.build();
                NotificationCompat.Style          big            = NotificationCompat.BigPictureStyle.extractStyleFromNotification(notification);
                NotificationCompat.MessagingStyle messagingStyle = null;
                if (big != null && big instanceof NotificationCompat.BigPictureStyle) {
                    MockDatabase.BigPictureStyleSocialAppData data = MockDatabase.getBigPictureStyleData();
                    messagingStyle = new NotificationCompat.MessagingStyle(you);
                    messagingStyle.addMessage(new NotificationCompat.MessagingStyle.Message(
                            data.getBigContentTitle(),
                            System.currentTimeMillis(),
                            new Person.Builder().setName(data.getParticipants().get(0)).build()
                    ).setData("image/jpg", MockDatabase.resourceToUri(this, R.drawable.earth)));
                } else {
                    messagingStyle =
                            NotificationCompat.MessagingStyle.extractMessagingStyleFromNotification(
                                    notification);
                }
                messagingStyle.addMessage(
                        new NotificationCompat.MessagingStyle.Message(
                                comment,
                                System.currentTimeMillis(),
                                you
                        ));

                Notification updatedNotification = notificationCompatBuilder
                        .setStyle(messagingStyle)
                        .setOnlyAlertOnce(true)
                        .build();
                NotificationManagerCompat notificationManagerCompat =
                        NotificationManagerCompat.from(getApplicationContext());
                notificationManagerCompat.notify(BigPictureNotification.NOTIFICATION_ID, updatedNotification);
            }
        }
    }

    /*
     * Extracts CharSequence created from the RemoteInput associated with the Notification.
     */
    private CharSequence getMessage(Intent intent) {
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        if (remoteInput != null) {
            return remoteInput.getCharSequence(EXTRA_COMMENT);
        }
        return null;
    }


}
