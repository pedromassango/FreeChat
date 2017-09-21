package com.aman.freechat.ui.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateInterpolator;
import android.widget.EditText;

import com.aman.freechat.R;
import com.aman.freechat.utils.AppUtility;
import com.aman.freechat.utils.Constants;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    CardView cvAdd;
    FloatingActionButton fab;
    TextInputEditText edit_email, edit_password, edit_confirm_password;
    TextInputLayout email, password, confirm_password;
    CardView register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initView();

        enterTransitionAnimation();
    }

    private void initView() {
        cvAdd = (CardView) findViewById(R.id.card);
        fab = (FloatingActionButton) findViewById(R.id.fab_cancel);
        edit_email = (TextInputEditText) findViewById(R.id.edit_email);
        edit_password = (TextInputEditText) findViewById(R.id.edit_password);
        edit_confirm_password = (TextInputEditText) findViewById(R.id.edit_confirm_password);
        register = (CardView) findViewById(R.id.register);

        email = (TextInputLayout) findViewById(R.id.email);
        password = (TextInputLayout) findViewById(R.id.password);
        confirm_password = (TextInputLayout) findViewById(R.id.confirm_password);

        fab.setOnClickListener(this);
        register.setOnClickListener(this);
    }

    private void enterTransitionAnimation() {
        Transition transition = TransitionInflater.from(this).inflateTransition(R.transition.fab_click_transition);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setSharedElementEnterTransition(transition);
        }

        transition.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {
                cvAdd.setVisibility(View.GONE);
            }

            @Override
            public void onTransitionEnd(Transition transition) {
                transition.removeListener(this);
                animateRevealShow();
            }

            @Override
            public void onTransitionCancel(Transition transition) {

            }

            @Override
            public void onTransitionPause(Transition transition) {

            }

            @Override
            public void onTransitionResume(Transition transition) {

            }
        });
    }

    public void animateRevealShow() {
        Animator mAnimator = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            mAnimator = ViewAnimationUtils.createCircularReveal(cvAdd, cvAdd.getWidth() / 2, 0, fab.getWidth() / 2, cvAdd.getHeight());

            mAnimator.setDuration(500);
            mAnimator.setInterpolator(new AccelerateInterpolator());
            mAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                }

                @Override
                public void onAnimationStart(Animator animation) {
                    cvAdd.setVisibility(View.VISIBLE);
                    super.onAnimationStart(animation);
                }
            });
            mAnimator.start();
        }
    }

    public void animateRevealClose() {
        Animator mAnimator = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            mAnimator = ViewAnimationUtils.createCircularReveal(cvAdd, cvAdd.getWidth() / 2, 0, cvAdd.getHeight(), fab.getWidth() / 2);

            mAnimator.setDuration(500);
            mAnimator.setInterpolator(new AccelerateInterpolator());
            mAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    cvAdd.setVisibility(View.INVISIBLE);
                    super.onAnimationEnd(animation);
                    fab.setImageResource(R.drawable.sign_up);
                    RegisterActivity.super.onBackPressed();
                }

                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                }
            });
            mAnimator.start();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_cancel:
                animateRevealClose();
                break;
            case R.id.register:
                switch (AppUtility.validate(edit_email, edit_password, edit_confirm_password)) {
                    case Constants.INVALID_EMAIL:
                        email.setError("Please enter a valid email address");
                        break;
                    case Constants.PASSWORD_LENGTH:
                        password.setError("Password length should be at least 6 characters");
                        break;
                    case Constants.PASSWORD_MATCH:
                        confirm_password.setError("Both passwords don't match");
                        break;
                    case Constants.VALIDATED:
                        Intent intent = new Intent();
                        intent.putExtra("email", edit_email.getText().toString());
                        intent.putExtra("password", edit_password.getText().toString());
                        setResult(RESULT_OK, intent);
                        finish();
                        break;
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        animateRevealClose();
    }
}
