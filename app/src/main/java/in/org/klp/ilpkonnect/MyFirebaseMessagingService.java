package in.org.klp.ilpkonnect;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

/**
 * Created by Shridhar.s
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    String t1 = "";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //Displaying data in log
        //It is optional 
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());


        //  String imageUri = remoteMessage.getData().get("image");
        //Calling method to generate notification
        sendNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody(), getApplicationContext());
    }













    //This method is only generating push notification
    //It is same as we did in earlier posts 
    private void sendNotification(String Title, String messageBody, Context appContext) {



    NotificationCompat.Builder builder = new NotificationCompat.Builder(appContext); //(icon, msg, when);

        builder.setContentTitle(getResources().getString(R.string.app_name));
        builder.setSmallIcon(R.drawable.ilp_logo);
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody));
        builder.setContentText(messageBody);
       builder.setContentIntent(PendingIntent.getActivity(this, 0, new Intent(),PendingIntent.FLAG_UPDATE_CURRENT));

        builder.setAutoCancel(true);
        builder.setDefaults(Notification.DEFAULT_LIGHTS);
        builder.setDefaults(Notification.DEFAULT_SOUND);

        NotificationManager notificationManager = (NotificationManager) appContext.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification=builder.build();
    //    builder.getNotification().flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(getNumber(), notification);


    }

   public int getNumber()
   {
       Random rand = new Random();
       return  rand.nextInt(9999);
   }


}