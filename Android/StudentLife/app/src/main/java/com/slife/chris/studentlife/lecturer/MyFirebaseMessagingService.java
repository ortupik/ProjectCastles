package com.slife.chris.studentlife.lecturer;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.slife.chris.studentlife.R;

/**
 * Created by prashant rane prashantr46@gmail.com on 11/26/16.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // creating notification

        Intent i= new Intent(this, LecturerUnitsActivity.class);
        PendingIntent pi= PendingIntent.getActivity(this,0,i,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder bn= new NotificationCompat.Builder( this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Lecturer Alert")
                .setContentText(remoteMessage.getData().get("msg").toString())
                .setAutoCancel( true )
                .setContentIntent(pi);
        NotificationManager nm= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.notify(0, bn.build());
    }
}
