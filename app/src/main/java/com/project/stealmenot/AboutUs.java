package com.project.stealmenot;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class AboutUs extends NavigationDrawer {

    TextView aboutUs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_about_us);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View ContentView = inflater.inflate(R.layout.activity_about_us,null,false);
        mDrawerLayout.addView(ContentView,0);
        
        aboutUs = findViewById(R.id.tvAboutUs);
        aboutUs.setText("Steal Me Not is created by Shivam, Himanshu, Farhan, Vishal.");

    }
}
