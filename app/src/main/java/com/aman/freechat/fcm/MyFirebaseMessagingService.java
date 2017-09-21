package com.aman.freechat.fcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.aman.freechat.R;
import com.aman.freechat.ui.activities.ChatActivity;
import com.aman.freechat.ui.activities.TabsActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
 
/**
 * Created by aman on 09/20/2017.
 */
 
public class MyFirebaseMessagingService extends FirebaseMessagingService {
 
    private static final String TAG = MyFirebaseMessagingService.class.getName();
 
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //Displaying data in log
        //It is optional 
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());

        Log.e(TAG, "onCreate: " + ChatActivity.isShown);

        if(!ChatActivity.isShown) {
            //Calling method to generate notification
            sendNotification(remoteMessage.getNotification().getBody(), remoteMessage.getNotification().getTitle());
        }
    }
 
    //This method is only generating push notification
    //It is same as we did in earlier posts 
    private void sendNotification(String messageBody, String title) {
        Intent intent = new Intent(this, TabsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);
 
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
 
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
 
        notificationManager.notify(0, notificationBuilder.build());
    }
}