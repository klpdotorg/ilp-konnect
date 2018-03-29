package in.org.klp.ilpkonnect;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;

import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

/**
 * Created by Shridhar.s
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseIIDService";

    String t1 = "";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //Displaying data in log
        //It is optional 
    //    Log.d(TAG, "From: " + remoteMessage.getFrom());
      //  Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());


        //  String imageUri = remoteMessage.getData().get("image");
        //Calling method to generate notification
        Log.d(TAG, "Refreshed message: " + "messgae");



        if (remoteMessage.getData().size() > 0) {
            if(remoteMessage.getData().get("key")!=null) {
                showNotification(getApplicationContext(), remoteMessage.getData().get("key"), new Intent());
            }


        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            showNotification(getApplicationContext(),remoteMessage.getNotification().getBody(),new Intent());
        }













    }














    private void sendNotification(String Title, String messageBody, Context appContext) {



    NotificationCompat.Builder builder = new NotificationCompat.Builder(appContext); //(icon, msg, when);

        builder.setContentTitle(getResources().getString(R.string.app_name));
        builder.setSmallIcon(R.drawable.ilp_logo);
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody));
        builder.setContentText(messageBody);
       // builder.setOngoing(true);

        builder.setContentIntent(PendingIntent.getActivity(this,
                0, new Intent(),PendingIntent.FLAG_UPDATE_CURRENT));

      //  builder.setAutoCancel(true);
        builder.setDefaults(Notification.DEFAULT_LIGHTS);
        builder.setDefaults(Notification.DEFAULT_SOUND);

        NotificationManager notificationManager = (NotificationManager) appContext.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification=builder.build();
    //    builder.getNotification().flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(getNumber(), notification);


    }

    public void showNotification(Context context, String body, Intent intent)
    {



        String channelId = "ILP";
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ilp_logo)
                   .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                     .setContentText(body)
                        .setAutoCancel(false)
                        .setSound(defaultSoundUri)
                        .setContentIntent(PendingIntent.getActivity(this,
                                0, new Intent(),PendingIntent.FLAG_UPDATE_CURRENT));

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "ILP Konnect",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

   public int getNumber()
   {
       Random random = new Random();
       return  random.nextInt(9999);
   }


}