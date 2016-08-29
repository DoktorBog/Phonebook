package com.phonebook.app.ui;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.phonebook.app.MyApplication;
import com.phonebook.app.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import hu.aut.utillib.circular.animation.CircularAnimationUtils;
import hu.aut.utillib.circular.widget.CircularFrameLayout;

public class SplashActivity extends AppCompatActivity {


    @Bind(R.id.view) RelativeLayout view;
    @Bind(R.id.viewCenter) TextView viewCenter;
    @Bind(R.id.reveal) CircularFrameLayout reveal;
    private int screenWidth;
    private int screenHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);

        Display display = getWindowManager().getDefaultDisplay();

        if (Build.VERSION.SDK_INT >= 13) {
            Point size = new Point();
            display.getSize(size);
            screenWidth = size.x;
            screenHeight = size.y;
        } else {
            screenWidth = display.getWidth();
            screenHeight = display.getHeight();
        }

        int[] myViewLocation = new int[2];
        view.getLocationInWindow(myViewLocation);

        float finalRadius = CircularAnimationUtils.hypo(screenWidth - myViewLocation[0], screenHeight - myViewLocation[1]);

        int[] center = CircularAnimationUtils.getCenter(viewCenter, view);

        ObjectAnimator animator = CircularAnimationUtils.createCircularReveal(view, center[0], center[1], 0, finalRadius);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(1000);
        reveal.setVisibility(View.VISIBLE);
        animator.start();

        new Handler().postDelayed(() -> {
            Intent intent;
            if(MyApplication.getInstance().getPrefManager().getUser()!=null)
                intent = new Intent(SplashActivity.this, ContactsActivity.class);
            else
                intent = new Intent(SplashActivity.this, LoginActivity.class);

            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
        }, 2000);
    }
}
