package com.project.stealmenot;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PostProtectionModes extends NavigationDrawer {

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    public static String number;
//    String data="NULL",prefFile="app_data";
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_post_protection_modes);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View ContentView = inflater.inflate(R.layout.activity_post_protection_modes,null,false);
        mDrawerLayout.addView(ContentView,0);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
//        sharedPreferences = getSharedPreferences(prefFile,MODE_PRIVATE);
        // Fetch the number at the time of loading this activity.
        fetchNumber();
    }

    // Check for PERMISSION
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==1000)
        {
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED)
                Toast.makeText(this,"Permission granted!",Toast.LENGTH_SHORT).show();
            else {
                Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    // fetch the Number of the Owner (no which was used at the time of registration)
    public void fetchNumber()
    {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener()
        {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                Users userProfile = dataSnapshot.getValue(Users.class);
                number = userProfile.getMobile();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Database error occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //GET GPS LOCATION button onClick Method
    public void getLocation(View view) {

        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
        sendIntent.setData(Uri.parse("sms:"+number));
        sendIntent.putExtra("sms_body", "get gps");
        startActivity(sendIntent);
    }

    public void btnFormatDevice(View view) {
        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
        sendIntent.setData(Uri.parse("sms:"+number));
        sendIntent.putExtra("sms_body", "wipe data");
        startActivity(sendIntent);
    }

    public void lockDevice(View view) {
        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
        sendIntent.setData(Uri.parse("sms:"+number));
        sendIntent.putExtra("sms_body", "lock device");
        startActivity(sendIntent);
    }

    public void btnSimDetect(View view) {
        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
        sendIntent.setData(Uri.parse("sms:"+number));
        sendIntent.putExtra("sms_body", "new sim detect");
        startActivity(sendIntent);
    }
}