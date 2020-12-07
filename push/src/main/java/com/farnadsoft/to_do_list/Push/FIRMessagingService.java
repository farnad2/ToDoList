/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.farnadsoft.to_do_list.Push;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.farnadsoft.to_do_list.UI.PushActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.farnadsoft.to_do_list.UI.CustomeWebView;
import com.farnadsoft.to_do_list.UI.DailogeNotice;
import com.farnadsoft.to_do_list.Database.DatabaseHandler;
import com.farnadsoft.to_do_list.Database.News;
import com.farnadsoft.to_do_list.R;

import java.util.Map;

/**
 * This {@code IntentService} does the actual handling of the GCM message.
 * {@code GcmBroadcastReceiver} (a {@code WakefulBroadcastReceiver}) holds a
 * partial wake lock for this service while the service does its work. When the
 * service is finished, it calls {@code completeWakefulIntent()} to release the
 * wake lock.
 */

public class FIRMessagingService extends FirebaseMessagingService {
    public static int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    TaskStackBuilder stackBuilder;
    String mTitle;
    SharedPreferences pref;
    Editor edit;
    boolean is_noty = false;
    Handler mHandler;


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
		Log.e("Msg", remoteMessage.getData().size()+"");
        //sendNotification(remoteMessage.getData());

        try {
            sendNotification(remoteMessage.getData());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        mHandler = new Handler();
    }

    public static final String TAG = "FCM Demo";

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(Map<String, String> data) {

        // handle notification here
        /*
		 * types of notification 1. result update 2. circular update 3. student
		 * corner update 4. App custom update 5. Custom Message 6. Notice from
		 * College custom
		 */
        int num = ++NOTIFICATION_ID;
        Bundle msg = new Bundle();
        for (String key : data.keySet()) {
            Log.e(key, data.get(key));
            msg.putString(key, data.get(key));
        }


        pref = getSharedPreferences("UPDATE_INSTANCE", MODE_PRIVATE);
        edit = pref.edit();
        Intent backIntent;
        Intent intent = null;
        PendingIntent pendingIntent = null;
        backIntent = new Intent(getApplicationContext(), PushActivity.class);
        backIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        SharedPreferences sp;
        Editor editor;

        switch (Integer.parseInt(msg.getString("type"))) {

            case 1:
                break;
            case 2:

                backIntent = new Intent(getApplicationContext(),
                        DailogeNotice.class);
                backIntent.putExtras(msg);

                stackBuilder = TaskStackBuilder.create(this);
                // Adds the back stack for the Intent (but not the Intent itself)
                // stackBuilder.addParentStack(AppUpdate.class);
                // Adds the Intent that starts the Activity to the top of the stack
                stackBuilder.addNextIntent(backIntent);
                pendingIntent = stackBuilder.getPendingIntent(num,
                        PendingIntent.FLAG_UPDATE_CURRENT);

                break;
            case 3:
                backIntent = new Intent(getApplicationContext(),
                        CustomeWebView.class);

                backIntent.putExtras(msg);
                // The stack builder object will contain an artificial back stack
                // for the
                // started Activity.
                // This ensures that navigating backward from the Activity leads out
                // of
                // your application to the Home screen.
                stackBuilder = TaskStackBuilder.create(this);
                // Adds the back stack for the Intent (but not the Intent itself)
                // stackBuilder.addParentStack(AppUpdate.class);
                // Adds the Intent that starts the Activity to the top of the stack
                stackBuilder.addNextIntent(backIntent);
                pendingIntent = stackBuilder.getPendingIntent(num,
                        PendingIntent.FLAG_UPDATE_CURRENT);


                break;

            case 4:
                backIntent = new Intent(getApplicationContext(),
                        PushActivity.class);

                // The stack builder object will contain an artificial back stack
                // for the
                // started Activity.
                // This ensures that navigating backward from the Activity leads out
                // of
                // your application to the Home screen.
                stackBuilder = TaskStackBuilder.create(this);
                // Adds the back stack for the Intent (but not the Intent itself)
                // stackBuilder.addParentStack(AppUpdate.class);
                // Adds the Intent that starts the Activity to the top of the stack
                stackBuilder.addNextIntent(backIntent);
                pendingIntent = stackBuilder.getPendingIntent(num,
                        PendingIntent.FLAG_UPDATE_CURRENT);


                DatabaseHandler db = new DatabaseHandler(getApplicationContext());
                db.addContact(
                        new News(msg.getString("title"), msg.getString("msg"),
                                msg.getString("link"), msg.getString("image")),
                        DatabaseHandler.TABLE_NEWS);

                Intent intent_notify = new Intent(FCMActivity.NEW_NOTIFICATION);
                intent_notify.putExtra("DUMMY","MUST");
                sendBroadcast(intent_notify);
                break;
            default:
                break;
        }
        if (!is_noty) {
            mNotificationManager = (NotificationManager) this
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                    this);

            mBuilder.setSmallIcon(R.drawable.ic_stat_fcm)
                    .setContentTitle(msg.getString("title"))
                    .setStyle(
                            new NotificationCompat.BigTextStyle().bigText(msg
                                    .getString("msg").toString()))
                    .setAutoCancel(true)
                    .setContentText(msg.getString("msg"));

            if (Integer.parseInt(msg.getString("type")) != 1) {
                mBuilder.setContentIntent(pendingIntent);
            }

            mBuilder.setDefaults(Notification.DEFAULT_ALL);

            mNotificationManager.notify(++NOTIFICATION_ID, mBuilder.build());
        }
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

        this.stopForeground(true);
    }
}
