package com.petuniversal.joaoribeiro.petuniversal;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;

/**
 * Created by Joao Ribeiro on 03/10/2017.
 */

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        createNotification(context, "Times Up!", "5 seconds have passed!", "Alert!");
    }

    public void createNotification (Context context, String msg, String msgTxt, String msgAlert){
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), 0);

        NotificationCompat.Builder notificationRep = (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.petuniversal230230)
                .setContentTitle(msg)
                .setContentText(msgTxt)
                .setAutoCancel(true) //remove when swiped
                .setTicker(msgAlert)
                .setDefaults(NotificationCompat.DEFAULT_SOUND);
        //TODO will the the notification stop repeating?
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationRep.build());
    }
}