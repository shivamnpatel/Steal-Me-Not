package com.project.stealmenot;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends NavigationDrawer {
   // SharedPreferences pref;
    public static String timer,newTime;
    EditText txttimer,getTxttimer;
    TextView selectAudioFile;
    String getCurrTime;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_settings);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View ContentView = inflater.inflate(R.layout.activity_settings, null, false);
        mDrawerLayout.addView(ContentView, 0);

        txttimer = findViewById(R.id.timer);
        selectAudioFile = findViewById(R.id.selectAudioFile);

        try {

            txttimer.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        //Log.e("newtime",timer);
                        getTimer();

                        return true;
                    }
                    return false;
                }
            });

           newTime = timer;
            Log.e("setter1", newTime);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public void getTimer()
    {
        timer=txttimer.getText().toString();
        txttimer.setText(timer);
        Log.e("setter0",timer);
    }
    public void selectAudioFile(View view){

        Intent intent_upload = new Intent();
        intent_upload.setType("audio/*");
        intent_upload.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent_upload,1);
    }
    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){

        if(requestCode == 1){

            if(resultCode == RESULT_OK){

                //the selected audio.
                Uri uri = data.getData();
                Security.mPlayer = MediaPlayer.create(this, uri);;
                Toast.makeText(this, "Audio: "+uri, Toast.LENGTH_SHORT).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
