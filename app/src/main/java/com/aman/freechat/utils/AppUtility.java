package com.aman.freechat.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.design.widget.TextInputEditText;
import android.text.TextUtils;

import com.aman.freechat.R;
import com.aman.freechat.db.ChatDB;
import com.aman.freechat.db.FriendDB;
import com.aman.freechat.model.User;
import com.aman.freechat.ui.activities.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by aman on 3/9/17.
 */

public class AppUtility {
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;
    private static ProgressDialog dialog;
    private static onAlertButtonClick onClick;

    public static void showDialog(Context context) {
        dialog = ProgressDialog.show(context, null, null, true, false);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.layout_progress);
        dialog.show();
    }

    public static void dismissDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public static int validate(TextInputEditText email, TextInputEditText password, TextInputEditText confirmPassword) {
        if (password.getText().length() < 6) {
            return Constants.PASSWORD_LENGTH;
        } else if (!TextUtils.equals(password.getText(), confirmPassword.getText())) {
            return Constants.PASSWORD_MATCH;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
            return Constants.INVALID_EMAIL;
        }
        return Constants.VALIDATED;
    }

    public static int validate(TextInputEditText email) {
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
            return Constants.INVALID_EMAIL;
        }
        return Constants.VALIDATED;
    }

    public static void showAlertDialog(Context context, String title, String message, onAlertButtonClick click) {
        onClick = click;
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Light_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(context);
        }
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (onClick != null)
                            onClick.okClick();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (onClick != null)
                            onClick.onCancelClick();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public static void logout(Context context) {
        FirebaseAuth.getInstance().signOut();

        SharedPrefHelper.getInstance(context).removeUser(context);
        FriendDB.getInstance().dropDB();
        ChatDB.getInstance().dropDB();

        Intent intent = new Intent(context, LoginActivity.class);
        ((Activity) context).finish();
        context.startActivity(intent);
    }

    public static String getTimeAgo(long time) {
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }

        long now = System.currentTimeMillis();
        if (time > now || time <= 0) {
            return null;
        }

        // TODO: localize
        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "just now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "a minute ago";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " minutes ago";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "an hour ago";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " hours ago";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "yesterday";
        } else {
            return diff / DAY_MILLIS + " days ago";
        }
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return  activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

    }

    public interface onAlertButtonClick {
        void okClick();

        void onCancelClick();
    }

    public static void showAboutDialog(Context context, String title, String message) {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Light_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(context);
        }
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setIcon(R.mipmap.ic_launcher)
                .show();
    }
}