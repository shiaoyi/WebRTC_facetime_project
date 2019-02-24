package org.appspot.apprtc;

/**
 * Created by hsushiaoyi on 2018/5/26.
 */

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.text.Html;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * Created by Ravi on 31/03/15.
 */

public class MyNotificationManager {

    public static final int ID_BIG_NOTIFICATION = 234;
    public static final int ID_SMALL_NOTIFICATION = 235;

    private Context mCtx;

    public MyNotificationManager(Context mCtx) {
        this.mCtx = mCtx;
    }

    //the method will show a big notification with an image
    //parameters are title for message title, message for message text, url of the big image and an intent that will open
    //when you will tap on the notification
    public void showBigNotification(String title, String name,Intent intent) {

        long[] vibrate_effect =
                {1000, 500, 1000, 400, 1000, 300, 1000, 200, 1000, 100};

        PendingIntent pendingIntent =
                PendingIntent.getActivity(mCtx, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification.Builder(mCtx)
                .setTicker("You got a mail") // ticker text is no longer displayed in Android 5.0
                .setContentTitle(title)
                .setContentText("Reservation is from " + name)
                .setSmallIcon(android.R.drawable.ic_dialog_email)
                .setVibrate(vibrate_effect)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();
        NotificationManager notificationManager = (NotificationManager) mCtx.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(ID_BIG_NOTIFICATION, notification);
    }

    //the method will show a small notification
    //parameters are title for message title, message for message text and an intent that will open
    //when you will tap on the notification
    public void showCallNotification(String title,String name, Intent intent) {
        long[] vibrate_effect =
                {1000, 500, 1000, 400, 1000, 300, 1000, 200, 1000, 100};

        PendingIntent pendingIntent =
                PendingIntent.getActivity(mCtx, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification.Builder(mCtx)
                .setTicker("You got a call") // ticker text is no longer displayed in Android 5.0
                .setContentTitle(title)
                .setContentText(name + " is calling")
                .setSmallIcon(android.R.drawable.ic_menu_call)
                .setVibrate(vibrate_effect)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();
        NotificationManager notificationManager = (NotificationManager) mCtx.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(ID_SMALL_NOTIFICATION, notification);
    }

}

