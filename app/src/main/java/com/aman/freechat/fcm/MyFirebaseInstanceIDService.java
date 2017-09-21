package com.aman.freechat.fcm;

import android.util.Log;

import com.aman.freechat.utils.SharedPrefHelper;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
 
/**
 * Created by aman on 09/20/2017.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
 
    private static final String TAG = MyFirebaseInstanceIDService.class.getName();
 
    @Override
    public void onTokenRefresh() {
        //Getting registration token
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        saveToken(refreshedToken);

        //Displaying token on logcat 
        Log.e(TAG, "Refreshed token: " + refreshedToken);
        
    }
 
    private void saveToken(String token) {
        SharedPrefHelper.getInstance(this).setSharedPreferenceStringToken(token);
    }
}