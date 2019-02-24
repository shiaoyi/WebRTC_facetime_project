package org.appspot.apprtc;

/**
 * Created by hsushiaoyi on 2018/5/26.
 */

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;


public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());
            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                sendPushNotification(json);
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }
    }

    //this method will display the notification
    //We are passing the JSONObject that is received from
    //firebase cloud messaging
    private void sendPushNotification(JSONObject json) throws JSONException {
        //optionally we can display the json into log
        Log.e(TAG, "Notification JSON " + json.toString());
            //getting the json data
            JSONObject data = json.getJSONObject("data");

                //parsing json data
                String title = data.getString("title");
                String comment = data.getString("comment");
                String image = data.getString("user_image");
                String startTime = data.getString("startTime");
                String finishTime = data.getString("finishTime");
                String date = data.getString("date");
                String name = data.getString("user_nickname");

                //creating MyNotificationManager object
                MyNotificationManager mNotificationManager = new MyNotificationManager(getApplicationContext());
                Bundle bundle = new Bundle();
                Intent intent;

            if(!date.equals("null")){
                //creating an intent for the notification
                Schedule schedule = new Schedule(image,name,comment, date, startTime, finishTime);
                intent = new Intent(getApplicationContext(), EmailActivity.class);
                bundle.putSerializable("schedule", schedule);
                intent.putExtras(bundle);

                mNotificationManager.showBigNotification(title, name, intent);
            }else {


                Call calling = new Call(image, name);
                intent = new Intent(getApplicationContext(), CallingActivity.class);
                bundle.putSerializable("calling", calling);
                intent.putExtras(bundle);

                mNotificationManager.showCallNotification(title, name, intent);

            }
    }

}
