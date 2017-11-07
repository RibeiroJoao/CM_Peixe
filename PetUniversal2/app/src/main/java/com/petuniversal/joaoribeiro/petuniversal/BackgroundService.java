package com.petuniversal.joaoribeiro.petuniversal;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;

import me.tatarka.support.job.JobParameters;
import me.tatarka.support.job.JobService;

/**
 * Created by jgpri on 28/10/2017.
 */

public class BackgroundService extends JobService {

    @Override
    public boolean onStartJob(JobParameters params) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(this, MainActivity.class);

        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(this);
        taskStackBuilder.addParentStack(MainActivity.class);
        taskStackBuilder.addNextIntent(intent);

        PendingIntent pendingIntent = taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notification = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.petuniversal230230)
                .setContentTitle("Pet Universal Notification")
                .setContentText("Verifique se tem tarefas!")
                .setAutoCancel(true) //remove when swiped
                .setTicker("Pet hourly!")
                .setDefaults(NotificationCompat.DEFAULT_SOUND);

        notificationManager.notify(112, notification.build());

        jobFinished(params, false);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }
}
