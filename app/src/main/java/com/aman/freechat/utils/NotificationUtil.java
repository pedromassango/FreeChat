package com.aman.freechat.utils;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by aman on 20/9/17.
 */

public class NotificationUtil {
    private final static String AUTH_KEY_FCM = "AAAAinPNl7g:APA91bH8xrnsibOUhtOAbXlpOEBO0M6ypH4_Z_S9UWBiniJmSzT4EA0tRGBPQcCWalsYw8C6aSlPpryRD757Ld87eMkTobJNbwELyrHw2Tg7NF1lf6STRLTeu_hc3zIu8jjcfYmYlmbW";
    private final static String API_URL_FCM = "https://fcm.googleapis.com/fcm/send";

    private final static String TAG = NotificationUtil.class.getName();

    private static NotificationUtil instance;

    private NotificationUtil() {

    }

    public static NotificationUtil getInstance() {
        if (instance == null) {
            instance = new NotificationUtil();
        }
        return instance;
    }

    public void sendNotification(final String id, final String message, final String title) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    pushFCMNotification(id, message, title);
                } catch (Exception e) {
                    Log.e(TAG, "doInBackground: " + e.getMessage());
                }
                return null;
            }
        }.execute();
    }

    private static void pushFCMNotification(String userDeviceIdKey, String chat, String receiverName) throws Exception {

        Log.e(TAG, "pushFCMNotification: " + receiverName);

        URL url = new URL(API_URL_FCM);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setUseCaches(false);
        conn.setDoInput(true);
        conn.setDoOutput(true);

        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", "key=" + AUTH_KEY_FCM);
        conn.setRequestProperty("Content-Type", "application/json");

        JSONObject json = new JSONObject();
        json.put("to", userDeviceIdKey.trim());
        JSONObject info = new JSONObject();
        info.put("title", receiverName); // Notification title
        info.put("body", chat); // Notification body
        json.put("notification", info);

        OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
        wr.write(json.toString());
        wr.flush();
        conn.getInputStream();
    }
}
