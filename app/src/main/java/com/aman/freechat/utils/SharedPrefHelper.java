package com.aman.freechat.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.aman.freechat.model.User;

import java.lang.reflect.Field;

/**
 * Created by aman on 7/9/17.
 */

public class SharedPrefHelper {
    private final static String PREF_FILE = "com.freefirechat.sharedpref";
    private final static String TOKEN_FILE = "com.freefirechat.sharedpref.token";
    private final static String EMAIL = "EMAIL";
    private final static String USERNAME = "USERAME";
    private final static String IMAGE = "IMAGE";
    private final static String ID = "ID";
    private final static String TOKEN = "TOKEN";
    static private SharedPreferences settings;
    static private SharedPreferences settings_token;
    private static SharedPreferences.Editor editor;
    private static SharedPreferences.Editor editor_token;
    private static SharedPrefHelper helper;

    public static SharedPrefHelper getInstance(Context context) {
        if(helper == null) {
            helper = new SharedPrefHelper();
            settings = context.getSharedPreferences(PREF_FILE, 0);
            settings_token = context.getSharedPreferences(TOKEN_FILE, 0);
            editor = settings.edit();
            editor_token = settings_token.edit();
        }
        return helper;
    }

    public void setSharedPreferenceStringToken(String value) {
        editor_token.putString(TOKEN, value);
        editor_token.apply();
    }

    public String getSharedPreferenceToken(String defValue) {
        return settings_token.getString(TOKEN, defValue);
    }

    private static void setSharedPreferenceString(String key, String value) {
        editor.putString(key, value);
        editor.apply();
    }

    /**
     * Set a integer shared preference
     *
     * @param key   - Key to set shared preference
     * @param value - Value for the key
     */
    public static void setSharedPreferenceInt(String key, int value) {
        editor.putInt(key, value);
        editor.apply();
    }

    /**
     * Set a Boolean shared preference
     *
     * @param key   - Key to set shared preference
     * @param value - Value for the key
     */
    public static void setSharedPreferenceBoolean(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.apply();
    }

    /**
     * Get a string shared preference
     *
     * @param key      - Key to look up in shared preferences.
     * @param defValue - Default value to be returned if shared preference isn't found.
     * @return value - String containing value of the shared preference if found.
     */
    public static String getSharedPreferenceString(String key, String defValue) {
        return settings.getString(key, defValue);
    }

    /**
     * Get a integer shared preference
     *
     * @param key      - Key to look up in shared preferences.
     * @param defValue - Default value to be returned if shared preference isn't found.
     * @return value - String containing value of the shared preference if found.
     */
    public static int getSharedPreferenceInt(Context context, String key, int defValue) {
        SharedPreferences settings = context.getSharedPreferences(PREF_FILE, 0);
        return settings.getInt(key, defValue);
    }

    /**
     * Get a boolean shared preference
     *
     * @param key      - Key to look up in shared preferences.
     * @param defValue - Default value to be returned if shared preference isn't found.
     * @return value - String containing value of the shared preference if found.
     */
    public static boolean getSharedPreferenceBoolean(Context context, String key, boolean defValue) {
        SharedPreferences settings = context.getSharedPreferences(PREF_FILE, 0);
        return settings.getBoolean(key, defValue);
    }

    public void saveUserInfo(User user) {
        setSharedPreferenceString(EMAIL, user.email);
        setSharedPreferenceString(USERNAME, user.userName);
        setSharedPreferenceString(IMAGE, user.email);
        setSharedPreferenceString(ID, user.id);
    }

    public User getUserInfo() {
        User user = new User();
        user.email = getSharedPreferenceString(EMAIL, "");
        user.userName = getSharedPreferenceString(USERNAME, "");
        user.image = "";
        user.id = getSharedPreferenceString(ID, "");
        return user;
    }

    public void removeUser(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREF_FILE, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.clear().commit();
    }
}
