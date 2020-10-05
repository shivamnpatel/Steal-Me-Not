package com.project.stealmenot;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends NavigationDrawer {

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    FirebaseUser firebaseUser;
    TextView profile_Name,profile_Email,profile_Password,profile_City,profile_MobileNo,profile_IMEI;
    ProgressBar progressBarProfile;

    SharedPreferences pref;
    TextView icci1,icci2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_profile);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View ContentView = inflater.inflate(R.layout.activity_profile,null,false);
        mDrawerLayout.addView(ContentView,0);

        profile_Name = findViewById(R.id.tvProfileName);
        profile_Email = findViewById(R.id.tvProfileEmail);
        profile_City = findViewById(R.id.tvProfileCity);
        profile_MobileNo = findViewById(R.id.tvProfileMobileNo);
        //icci1=findViewById(R.id.icc1);
        //icci2=findViewById(R.id.icc2);
        progressBarProfile = findViewById(R.id.progressBarProfile);

        //testing sim iccid
       // pref=getApplicationContext().getSharedPreferences("sim_data",0);
        //final String ob1=pref.getString("iccid1",null);
        //final String ob2=pref.getString("iccid2",null);


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        progressBarProfile.setVisibility(View.VISIBLE);

        if(firebaseUser!=null)
        {
            final String uid = firebaseUser.getUid();
            //Toast.makeText(ProfileActivity.this,uid,Toast.LENGTH_LONG).show();
            DatabaseReference databaseReference = firebaseDatabase.getReference("Users");
            databaseReference.child(uid).addValueEventListener(new ValueEventListener()
            {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    progressBarProfile.setVisibility(View.INVISIBLE);
                    Users userProfile = dataSnapshot.getValue(Users.class);
                    profile_Name.setText("Name: " + userProfile.getName());
                    profile_Email.setText("Email: " + userProfile.getEmail());
                    profile_City.setText("City: " + userProfile.getCity());
                    profile_MobileNo.setText("Mobile no: " + userProfile.getMobile());
                    // checking for Android P+
                    //testing 2
                   // icci1.setText("ICCI1: "+ ob1);
                    //icci2.setText("ICCI2: "+ob2);

//                    Toast.makeText(getApplicationContext(), "User id is:" + uid, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getApplicationContext(), "Database error occured", Toast.LENGTH_SHORT).show();
                }
            });


        }
        else
        {
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
            Toast.makeText(this,"User does not exists(null!)",Toast.LENGTH_SHORT).show();

        }


    }

    /*public void btnLogout(View view) {
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(getApplicationContext(),"Logout successful",Toast.LENGTH_SHORT).show();
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
        finish();
    }*/
}// class closed
