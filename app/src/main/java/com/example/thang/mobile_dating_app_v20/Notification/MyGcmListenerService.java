/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.thang.mobile_dating_app_v20.Notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.thang.mobile_dating_app_v20.Activity.ChatActivity;
import com.example.thang.mobile_dating_app_v20.Activity.MainActivity;
import com.example.thang.mobile_dating_app_v20.Activity.NearbyMapActivity;
import com.example.thang.mobile_dating_app_v20.Classes.DBHelper;
import com.example.thang.mobile_dating_app_v20.Classes.Message;
import com.example.thang.mobile_dating_app_v20.Classes.Utils;
import com.example.thang.mobile_dating_app_v20.R;
import com.google.android.gms.gcm.GcmListenerService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MyGcmListenerService extends GcmListenerService {
    private static final String FLAG_NOTIFICATION = "notification";
    private static final String FLAG_LOCATION = "location";
    private static final String FLAG_CHAT = "chat";

    private static final String TAG = "MyGcmListenerService";

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");
        String title = data.getString("title");
        String flag = data.getString("flag");
        String email = data.getString("email");
        double longitude = Double.valueOf(data.getString("longitude"));
        double latitude = Double.valueOf(data.getString("latitude"));
        Log.i(TAG, "From: " + from);
        Log.i(TAG, "Message: " + message);
        Log.i(TAG, "flag: " + flag);
        Log.i(TAG, "email: " + email);
        Log.i(TAG, "latitude: " + latitude);
        Log.i(TAG, "longitude: " + longitude);

        /**
         * Production applications would usually process the message here.
         * Eg: - Syncing with server.
         *     - Store message in local database.
         *     - Update UI.
         */

        /**
         * In some cases it may be useful to show a notification indicating to the user
         * that a message was received.
         */
        sendNotification(message, title, flag, email, longitude, latitude);
    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     */
    private void sendNotification(String message, String title, String flag, String email,
                                  double longitude, double latitude) {
        Intent intent;
        Bundle bundle = new Bundle();
        bundle.putString("Email", email);
        if (flag.equals(FLAG_CHAT)) {
            //check for conversation history
            String currentUserEmail = DBHelper.getInstance(this).getCurrentUser().getEmail();
            int conversationId = DBHelper.getInstance(this).getConservationId(currentUserEmail, email);

            if (conversationId == -1) {
                //insert new conversation
                DBHelper.getInstance(this).insertConversation(currentUserEmail, email);
                conversationId = DBHelper.getInstance(this).getConservationId(currentUserEmail, email);
            }

            //insert chat message to SQLite
            DBHelper.getInstance(getApplicationContext())
                    .insertConversationMessage(email, message, conversationId, getTime());

            intent = new Intent(this, ChatActivity.class);
            intent.putExtras(bundle);
        } else if (flag.equals(FLAG_LOCATION)) {
            bundle.putDouble("longitude", longitude);
            bundle.putDouble("latitude", latitude);
            intent = new Intent(this, NearbyMapActivity.class);
            intent.putExtras(bundle);
        } else {
            intent = new Intent(this, MainActivity.class);
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.notification_logo)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
//                .setLargeIcon(avatar.isEmpty() ? BitmapFactory.decodeResource(getResources(),
//                        R.drawable.no_avatar) : Bitmap.createScaledBitmap(Utils.decodeBase64StringToBitmap(avatar),8,8,false))
                        //Bitmap.createScaledBitmap(Utils.decodeBase64StringToBitmap(avatar),16,16,false)
                .setContentIntent(pendingIntent);

        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        bigTextStyle.setBigContentTitle(title);
        bigTextStyle.bigText(message);

//        NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
//        bigPictureStyle.bigPicture(avatar.isEmpty() ? BitmapFactory.decodeResource(getResources(),
//                R.drawable.no_avatar) : Utils.decodeBase64StringToBitmap(avatar));

        notificationBuilder.setStyle(bigTextStyle);
//        notificationBuilder.setStyle(bigPictureStyle);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    private String getTime(){
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date date = new Date();
        return dateFormat.format(date);
    }
}
