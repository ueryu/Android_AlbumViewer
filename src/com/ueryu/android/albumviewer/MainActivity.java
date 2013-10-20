
package com.ueryu.android.albumviewer;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;

import com.ueryu.android.albumviewer.utils.DefaultPreferences;
import com.ueryu.android.albumviewer.utils.SystemUiHider;

public class MainActivity extends FragmentActivity {
    /**
     * Whether or not the system UI should be auto-hidden after {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * If set, will toggle the system UI visibility upon interaction. Otherwise, will show the system UI visibility upon interaction.
     */
    private static final boolean TOGGLE_ON_CLICK = true;

    private static final String KEY_FULLSCREENED = "fullscreened";

    /**
     * The flags to pass to {@link SystemUiHider#getInstance}.
     */
    private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

    /**
     * The instance of the {@link SystemUiHider} for this activity.
     */
    private SystemUiHider mSystemUiHider;

    private boolean mFullscreened = false;
    private View mRoot = null;

    // Lifecycles.

    @Override
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRoot = findViewById(android.R.id.content);

        // Set up an instance of SystemUiHider to control the system UI for
        // this activity.
        mSystemUiHider = SystemUiHider.getInstance(this, mRoot, HIDER_FLAGS);
        mSystemUiHider.setup();
        mSystemUiHider.setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {

            @Override
            @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
            public void onVisibilityChange(boolean visible) {
                if (visible && AUTO_HIDE) {
                    // Schedule a hide().
                    delayedHide(AUTO_HIDE_DELAY_MILLIS);
                }
            }
        });

        // Set up the user interaction to manually show or hide the system UI.
        mRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TOGGLE_ON_CLICK) {
                    mSystemUiHider.toggle();
                } else {
                    mSystemUiHider.show();
                }
            }
        });
    }

    // overrides.

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(KEY_FULLSCREENED, mFullscreened);
    }

    @Override
    public void onBackPressed() {
        if (DefaultPreferences.getDefaultAddressLine(this) != null) {
            // リポジトリ設定画面を出す為に設定を消す.
            DefaultPreferences.setDefaultAddressLine(this, null);
            final Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    /**
     * Touch listener to use for in-layout UI controls to delay hiding the system UI. This is to prevent the jarring behavior of controls going away while
     * interacting with activity UI.
     */
    View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    Handler mHideHandler = new Handler();
    Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            mSystemUiHider.hide();
        }
    };

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    // others.
}
