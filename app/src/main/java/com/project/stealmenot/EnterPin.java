package com.project.stealmenot;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import static com.project.stealmenot.SetPin.MyPREFERENCES;

public class EnterPin extends AppCompatActivity {

    EditText etEnterPin;
    SharedPreferences sharedpreferences;
    MediaPlayer mPlayer;
    View view;

    //Disable Back Key
    @Override
    public void onBackPressed() {

        return;
    }

    //Disable Tasks Key
    @Override
    protected void onPause() {
        super.onPause();

        ActivityManager activityManager = (ActivityManager) getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE);

        activityManager.moveTaskToFront(getTaskId(), 0);
    }

    //Disable Volume Key
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            // Do your thing

            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                return true;
            default:
                return super.dispatchKeyEvent(event);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_pin);

        /*final Vibrator vb = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {0, 100, 1000};
        vb.vibrate(pattern, 0);*/
        mPlayer = MediaPlayer.create(com.project.stealmenot.EnterPin.this, R.raw.siren);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        final String password = sharedpreferences.getString("passwordKey", "");
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        // final MediaPlayer mPlayer = MediaPlayer.create(EnterPin.this, R.raw.siren);
        //AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        //audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 20, 0);
        //mPlayer.start();
        //mPlayer.setLooping(true);
        playMedia();
        etEnterPin = (EditText) findViewById(R.id.etEnterPin);
        etEnterPin.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    String pin = etEnterPin.getText().toString();
                    if (pin.equals(password)) {
                        mPlayer.stop();
                        //vb.cancel();
                        startActivity(new Intent(EnterPin.this, ProtectionModesActivity.class));
                        finish();
                        handled = true;
                    } else {
                        etEnterPin.getText().clear();
                        etEnterPin.setError("Wrong Pin!");
                        etEnterPin.requestFocus();
                    }

                }
                return handled;
            }
        });

    }


    public void playMedia() {
        // mPlayer = MediaPlayer.create(Security.this, R.raw.siren);
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 20, 0);
        //mPlayer.start();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // mPlayer = MediaPlayer.create(Security.this, R.raw.siren);
                mPlayer.setLooping(true);
                mPlayer.start();
            }
        }, 5000);
        //mPlayer.setLooping(true);

    }
}