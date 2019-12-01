package com.track24x7.kuk.service;
 
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.track24x7.kuk.R;
import com.track24x7.kuk.activity.BirthdaysActivity;
import com.track24x7.kuk.activity.ChatActivity;
import com.track24x7.kuk.activity.ChatMessagesActivity;
import com.track24x7.kuk.activity.HomeActivity;
import com.track24x7.kuk.activity.MessageActivity;
import com.track24x7.kuk.activity.MyPostedBusinessActivity;
import com.track24x7.kuk.activity.MyPostedJobsActivity;
import com.track24x7.kuk.activity.NewsActivity;
import com.track24x7.kuk.activity.PostedBusinessActivity;
import com.track24x7.kuk.activity.PostedJobsActivity;
import com.track24x7.kuk.activity.PostedRequirementActivity;
import com.track24x7.kuk.activity.UserDetailActivity;
import com.track24x7.kuk.activity.UserMessagesActivity;
import com.track24x7.kuk.app.config;
import com.track24x7.kuk.util.NotificationUtils;
import com.track24x7.kuk.util.Pref;
import com.track24x7.kuk.util.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.atomic.AtomicInteger;


/**
 * Created by Ravi Tamada on 08/08/16.
 * www.androidhive.info
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {
 
    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();
 
    private NotificationUtils notificationUtils;
    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Log.d("NEW_TOKEN",token);
        // Saving reg id to shared preferences
        storeRegIdInPref(token);

        // sending reg id to your server
        sendRegistrationToServer(token);

        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(config.REGISTRATION_COMPLETE);
        registrationComplete.putExtra("token", token);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);

    }

    private void sendRegistrationToServer(final String token) {
        // sending gcm token to server
        Log.e(TAG, "sendRegistrationToServer: " + token);
    }

    private void storeRegIdInPref(String token) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(config.SHARED_PREF, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("regId", token);
        editor.commit();
    }
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "From: " + remoteMessage.getFrom());
 
        if (remoteMessage == null)
            return;
 
        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Notification Title: " + remoteMessage.getNotification().getTitle());
            Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
            handleNotification(remoteMessage);
        }
 
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());
 
            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                handleDataMessage(json);
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }
    }
 
    private void handleNotification(RemoteMessage remoteMessage) {
        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
            // app is in foreground, broadcast the push message
          /*  Intent pushNotification = new Intent(config.PUSH_NOTIFICATION);
            pushNotification.putExtra("title", remoteMessage.getNotification().getTitle());
            pushNotification.putExtra("message",remoteMessage.getNotification().getBody());
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);*/
 
            // play notification sound
            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
            notificationUtils.playNotificationSound();
            createNotification(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody());
            }else{
            // If the app is in background, firebase itself handles the notification
        }
    }


    public void createNotification(String title, String message ) {
        // Prepare intent which is triggered if the
        // notification is selected
        Intent intent;
        try {
            if(TextUtils.isEmpty(title))
            {
                title="Hello!!!";
            }
            if (!TextUtils.isEmpty(message)) {
                if (title.equalsIgnoreCase("registration")) {
                    String userId=message.split("#")[2];
                    intent = new Intent(getApplicationContext(),UserDetailActivity.class);
                    intent.putExtra("userID",userId);
                }  else if (title.equalsIgnoreCase("update")) {
                    String userId = message.split("#")[2];
                    intent = new Intent(getApplicationContext(), UserDetailActivity.class);
                    intent.putExtra("userID", userId);
                }
                else if (title.equalsIgnoreCase("login")) {
                    String userId = message.split("#")[2];
                    intent = new Intent(getApplicationContext(), UserDetailActivity.class);
                    intent.putExtra("userID", userId);
                }
                else if (title.equalsIgnoreCase("chat")) {
                    String roomName=message.split(":")[0];
                    intent = new Intent(getApplicationContext(),ChatMessagesActivity.class);
                    String userName=Pref.GetStringPref(getApplicationContext(), StringUtils.USERNAME,"") +"_"+ Pref.GetStringPref(getApplicationContext(), StringUtils.SCHOOL,"")+"_"+ Pref.GetStringPref(getApplicationContext(), StringUtils.Batch,"");
                    intent.putExtra("user_name",userName);
                    intent.putExtra("room_name",roomName );
                    intent.putExtra("Screen", "Chat");
                    //startActivity(intent);
                } else if (title.equalsIgnoreCase("message")) {
                    intent = new Intent(this, UserMessagesActivity.class);
                    Bundle b = new Bundle();
                    b.putString("Screen", "My Messages"); //Your id
                    intent.putExtras(b);
                } else if (title.equalsIgnoreCase("job")) {
                    intent = new Intent(this, PostedJobsActivity.class);
                    Bundle b = new Bundle();
                    b.putString("Screen", "Posted Jobs"); //Your id
                    intent.putExtras(b);
                } else if (title.equalsIgnoreCase("Requirement")) {
                    intent = new Intent(this, PostedRequirementActivity.class);
                    Bundle b = new Bundle();
                    b.putString("Screen", "Posted Requirement"); //Your id
                    intent.putExtras(b);
                }
                else if (title.equalsIgnoreCase("business")) {
                    intent = new Intent(this, PostedBusinessActivity.class);
                    Bundle b = new Bundle();
                    b.putString("Screen", "Business"); //Your id
                    intent.putExtras(b);
                } else if (title.equalsIgnoreCase("news")) {
                    intent = new Intent(this, NewsActivity.class);
                    Bundle b = new Bundle();
                    b.putString("Screen", "News"); //Your id
                    intent.putExtras(b);
                } else if (title.equalsIgnoreCase("birthday")) {
                    intent = new Intent(this, BirthdaysActivity.class);
                    Bundle b = new Bundle();
                    b.putString("Screen", "Birthday"); //Your id
                    intent.putExtras(b);
                } else {
                    intent = new Intent(this, HomeActivity.class);
                }
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                Intent backIntent = new Intent(getApplicationContext(), BirthdaysActivity.class);
                backIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                PendingIntent pIntent = PendingIntent.getActivities(this, (int) System.currentTimeMillis(), new Intent[] {backIntent, intent}, PendingIntent.FLAG_ONE_SHOT);
                String channelId = "Default";
                NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(getNotificationIcon())
                        .setContentTitle(title)
                        .setContentText(message).setAutoCancel(true).setContentIntent(pIntent);

                NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel(channelId, "Default channel", NotificationManager.IMPORTANCE_HIGH);
                    manager.createNotificationChannel(channel);
                }
                manager.notify(NotificationID.getID(), builder.build());
            }
        }
        catch(Exception ex)
        {

        }
    }

    public static class NotificationID {
        private static final AtomicInteger c = new AtomicInteger(0);

        public static int getID() {
            return c.incrementAndGet();
        }
    }
    private int getNotificationIcon() {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        //If the build version is higher than kitkat we need to create Silhouette icon.
        return useWhiteIcon ? R.drawable.notification_icon : R.mipmap.ic_launcher;
    }
    private void handleDataMessage(JSONObject json) {
        Log.e(TAG, "push json: " + json.toString());
 
        try {
            JSONObject data = json.getJSONObject("data");
 
            String title = data.getString("title");
            String message = data.getString("body");
           /* boolean isBackground = data.getBoolean("is_background");
            String imageUrl = data.getString("image");
            String timestamp = data.getString("timestamp");
            JSONObject payload = data.getJSONObject("payload");*/
 
            Log.e(TAG, "title: " + title);
            Log.e(TAG, "message: " + message);
/*            Log.e(TAG, "isBackground: " + isBackground);
            Log.e(TAG, "payload: " + payload.toString());
            Log.e(TAG, "imageUrl: " + imageUrl);
            Log.e(TAG, "timestamp: " + timestamp);*/


            if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
              /*  // app is in foreground, broadcast the push message
                Intent pushNotification = new Intent(config.PUSH_NOTIFICATION);
                pushNotification.putExtra("message", message);
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
 */
                // play notification sound
                NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                notificationUtils.playNotificationSound();
                createNotification(title,message);
            } else {
                // app is in background, show the notification in notification tray
                /*Intent resultIntent = new Intent(getApplicationContext(), HomeActivity.class);
                resultIntent.putExtra("message", message);

                // check for image attachment
                if (TextUtils.isEmpty(imageUrl)) {
 '                   showNotificationMessage(getApplicationContext(), title, message, timestamp, resultIntent);
                } else {
                    // image is present, show notification with image
                    showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp, resultIntent, imageUrl);
                }*/
                createNotification(title,message);
            }
        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }
 
    /**
     * Showing notification with text only
     */
    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
    }
 
    /**
     * Showing notification with text and image
     */
    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
    }
}