package com.project.stealmenot;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class LogoutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(getApplicationContext(),"Logout successful",Toast.LENGTH_SHORT).show();

        // TO PREVENT APP FROM OPENING PREVIOUS ACTIVITIES AFTER LOGOUT....
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        // set the new task and clear flags
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);

    }

}
