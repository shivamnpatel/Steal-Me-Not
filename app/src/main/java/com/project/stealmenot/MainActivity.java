package com.project.stealmenot;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    EditText etEmail,etPassword;
    Button btnLogin;
    ProgressBar progressBar;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    String resetEmail = "";
    public String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    //SharedPreferences simpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // FOR TESTING ACTIVITIES ONLY//////
//        Intent i=new Intent(this, NavigationDrawer.class);
//        startActivity(i);
        /////////////////////////

        // CHECK FOR PERMISSION AND REQUEST IF SOME ARE MISSING
        if(checkSelfPermission(Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.SEND_SMS)!=PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.READ_PHONE_STATE)!=PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                    Manifest.permission.RECEIVE_SMS,
                    Manifest.permission.SEND_SMS,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.READ_PHONE_STATE}, 1000);
        }

        etEmail = findViewById(R.id.etEmailLogin);
        etPassword = findViewById(R.id.etPasswordLogin);
        btnLogin = findViewById(R.id.btnLogin);
        progressBar = findViewById(R.id.progressBar);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();

        /// for checking if user is already logged in /////
        if(firebaseUser!=null) {
            startActivity(new Intent(MainActivity.this, ProtectionModesActivity.class));
            finish();
        }

    }

    public void btnLogin(View view) {

        progressBar.setVisibility(View.VISIBLE);
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        try
        {
            if(email.length()==0 && password.length()==0)
            {
                progressBar.setVisibility(View.INVISIBLE);
                etEmail.requestFocus();
                etEmail.setError("Required");
                etPassword.requestFocus();
                etPassword.setError("Required");
                Toast.makeText(MainActivity.this, "Please enter all details!!", Toast.LENGTH_SHORT).show();
                return;

            }
            if (email.length()==0) {
                progressBar.setVisibility(View.INVISIBLE);
                etEmail.requestFocus();
                etEmail.setError("Required");
                return;
            }
            if (password.length()==0) {
                progressBar.setVisibility(View.INVISIBLE);
                etPassword.requestFocus();
                etPassword.setError("Required");
                return;
            }

            if (!email.matches(emailPattern)) {
                progressBar.setVisibility(View.INVISIBLE);
                etEmail.requestFocus();
                etEmail.setError("Enter valid email id");
                return;
            }

            // Authenticating user
            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                finish();
                                progressBar.setVisibility(View.INVISIBLE);
                                startActivity(new Intent(getApplicationContext(), ProtectionModesActivity.class));
                                Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_SHORT).show();

                            } else {
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(getApplicationContext(), "Invalid Email id & Password", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
        catch(Exception e)
        {
            Toast.makeText(getApplicationContext(),"Exception occurred: "+e,Toast.LENGTH_SHORT).show();
        }

    }
    @Override
    public void onBackPressed() {

        AlertDialog.Builder exitBox = new AlertDialog.Builder(this);
        exitBox.setCancelable(false);
        exitBox.setTitle("Exit");
        exitBox.setMessage("Do you want to exit?");
        exitBox.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        exitBox.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        exitBox.create().show();
    }

    public void goToRegister(View view) {
        startActivity(new Intent(MainActivity.this,RegisterActivity.class));
    }

    public void forgotPassword(View view) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //builder.setCancelable(false);
        builder.setTitle("Enter your Email address");
       // builder.setMessage("Enter Email id for Password Reset Link");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        builder.setView(input);
        builder.setNegativeButton("Send", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                resetEmail = input.getText().toString().trim();
                if(TextUtils.isEmpty(resetEmail) || !resetEmail.matches(emailPattern))
                {
                    Toast.makeText(getApplicationContext(), "Enter valid email id", Toast.LENGTH_SHORT).show();
                    input.requestFocus();
                    input.setError("Please enter valid email");

//                    return;
                }
                else {
                    firebaseAuth.sendPasswordResetEmail(resetEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Password Reset Email Sent!\n" + "Please check your email",
                                        Toast.LENGTH_SHORT).show();
                            } else
                                Toast.makeText(getApplicationContext(), "Email address not registered!!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                //finish();
            }
        });
        builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.create().show();

    }

}   // class closed
