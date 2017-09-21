package com.aman.freechat.ui.activities;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.aman.freechat.R;
import com.aman.freechat.db.Db;
import com.aman.freechat.model.User;
import com.aman.freechat.utils.AppUtility;
import com.aman.freechat.utils.Constants;
import com.aman.freechat.utils.GetPermissions;
import com.aman.freechat.utils.SharedPrefHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = LoginActivity.class.getName();

    FloatingActionButton fab_register;
    TextInputEditText edit_email, edit_password;
    CardView login;
    TextInputLayout email, password;

    TextView reset_password;

    CredentialsHelper helper;
    FirebaseUser user;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initFirebase();
        initView();
    }

    private void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
        helper = new CredentialsHelper();
        user = mAuth.getCurrentUser();

        //initializing the database
        if (GetPermissions.hasPermissions(this, GetPermissions.STORAGE_PERMISSIONS)) {
            Db.getInstance().init(this);
        } else {
            GetPermissions.askAllPermissions(this);
        }


        if (user != null) {
            //user is already signed in
            startActivity(new Intent(this, TabsActivity.class));
            finish();
        }
    }

    private void initView() {
        fab_register = (FloatingActionButton) findViewById(R.id.fab_register);
        edit_email = (TextInputEditText) findViewById(R.id.edit_email);
        edit_password = (TextInputEditText) findViewById(R.id.edit_password);
        login = (CardView) findViewById(R.id.login);
        email = (TextInputLayout) findViewById(R.id.email);
        password = (TextInputLayout) findViewById(R.id.password);
        reset_password = (TextView) findViewById(R.id.reset_password);

        fab_register.setOnClickListener(this);
        login.setOnClickListener(this);
        reset_password.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_register:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    getWindow().setExitTransition(null);
                    getWindow().setEnterTransition(null);

                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, fab_register, fab_register.getTransitionName());
                    startActivityForResult(new Intent(this, RegisterActivity.class), Constants.REGISTER_CODE, options.toBundle());
                } else {
                    startActivityForResult(new Intent(this, RegisterActivity.class), Constants.REGISTER_CODE);
                }
                break;
            case R.id.login:
                if (AppUtility.validate(edit_email) == Constants.INVALID_EMAIL) {
                    email.setError("Invalid Email And/Or Password");
                } else {
                    helper.login(edit_email.getText().toString(), edit_password.getText().toString());
                }
                break;
            case R.id.reset_password:
                if (AppUtility.validate(edit_email) == Constants.INVALID_EMAIL) {
                    email.setError("Invalid email");
                } else {
                    AppUtility.showDialog(LoginActivity.this);
                    helper.resetPassword(edit_email.getText().toString());
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == Constants.REGISTER_CODE) {
            helper.createUser(data.getStringExtra("email"), data.getStringExtra("password"));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == GetPermissions.ALL_PERMISSIONS) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Db.getInstance().init(this);
            } else {
                AppUtility.showAlertDialog(this, "Required", "This permission is required for app to work properly." +
                        "\n" + "Please go to settings and grant the permission", new AppUtility.onAlertButtonClick() {
                    @Override
                    public void okClick() {

                    }

                    @Override
                    public void onCancelClick() {

                    }
                });
            }
        }
    }

    private class CredentialsHelper {

        private FirebaseUser getUser() {
            if (user == null) {
                user = mAuth.getCurrentUser();
                return user;
            }
            return user;
        }

        void createUser(String email, String password) {
            AppUtility.showDialog(LoginActivity.this);

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            AppUtility.dismissDialog();

                            if (task.isSuccessful()) {
                                createNewUser();
                                Toast.makeText(LoginActivity.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                                if (GetPermissions.hasPermissions(LoginActivity.this, GetPermissions.STORAGE_PERMISSIONS))
                                    startActivity(new Intent(LoginActivity.this, TabsActivity.class));
                                finish();
                            } else {
                                try {
                                    throw task.getException();
                                } catch (FirebaseAuthWeakPasswordException e) {
                                    Toast.makeText(LoginActivity.this, "Error\nWeak password", Toast.LENGTH_SHORT).show();
                                } catch (FirebaseAuthInvalidCredentialsException e) {
                                    Toast.makeText(LoginActivity.this, "Error\nInvalid Email", Toast.LENGTH_SHORT).show();
                                } catch (FirebaseAuthUserCollisionException e) {
                                    Toast.makeText(LoginActivity.this, "Error\nUser already exists", Toast.LENGTH_SHORT).show();
                                } catch (Exception e) {
                                    Toast.makeText(LoginActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
        }

        void login(String email, String password) {
            AppUtility.showDialog(LoginActivity.this);

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                saveUserDetailsLocal();
                            } else {
                                Toast.makeText(LoginActivity.this, "Login Error", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

        void createNewUser() {
            if (getUser() != null) {
                User newUser = new User();
                newUser.email = user.getEmail();
                newUser.userName = user.getEmail().split("@")[0];
                newUser.image = "";
                newUser.id = user.getUid();
                newUser.token = SharedPrefHelper.getInstance(LoginActivity.this).getSharedPreferenceToken("");
                FirebaseDatabase.getInstance().getReference().child("user/" + user.getUid()).setValue(newUser);
                saveUserDetailsLocal();
            }
        }

        void saveUserDetailsLocal() {
            if (getUser() != null) {
                FirebaseDatabase.getInstance().getReference().child("user/" + user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        HashMap user = (HashMap) dataSnapshot.getValue();
                        User userInfo = new User();
                        userInfo.email = (String) user.get("email");
                        userInfo.userName = (String) user.get("userName");
                        userInfo.image = "";
                        userInfo.id = (String) user.get("id");

                        saveTokenToFCM(userInfo);

                        SharedPrefHelper.getInstance(LoginActivity.this).saveUserInfo(userInfo);

                        AppUtility.dismissDialog();

                        if (GetPermissions.hasPermissions(LoginActivity.this, GetPermissions.STORAGE_PERMISSIONS)) {


                            Toast.makeText(LoginActivity.this, "Login Success", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this, TabsActivity.class));
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }

        void saveTokenToFCM(User user) {
            user.token = SharedPrefHelper.getInstance(LoginActivity.this).getSharedPreferenceToken("");
            FirebaseDatabase.getInstance().getReference().child("user/" + user.id).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (!task.isSuccessful()) {
                        Log.e(TAG, "onComplete: " + task.getException());
                    }
                }
            });
        }

        void resetPassword(final String email) {
            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            AppUtility.dismissDialog();
                            AppUtility.showAlertDialog(LoginActivity.this, "Password reset", "Password recovery email sent to " +
                                    email, null);
                        }
                    })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    AppUtility.dismissDialog();
                    AppUtility.showAlertDialog(LoginActivity.this, "Password reset", "Cannot send email to  " +
                            email, null);
                }
            });
        }
    }
}
