package com.project.stealmenot;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

import static com.project.stealmenot.TelephonyInfo.getInstance;

public class RegisterActivity extends AppCompatActivity {

    EditText etName, etEmail, etPassword, etConfirmPassword, etCity, etMobileNo;
    Button btnRegister;
    ProgressBar progressBarRegister;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    String imeiNumber;
    TelephonyManager tel;
    SharedPreferences simsharedPreferences;
    String simFile = "sim_data";
    Context context;
    String iccidnum1;
    String iccidnum2;
    protected static String icnum1,icnum2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        etCity = findViewById(R.id.etCity);
        etMobileNo = findViewById(R.id.etMobileNo);
        btnRegister = findViewById(R.id.btnRegister);
        progressBarRegister = findViewById(R.id.progressBar);
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        tel = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        TelephonyInfo telephonyInfo = getInstance(this);
        getIMEI();
       SubscriptionManager sManager = (SubscriptionManager) getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
        if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        SubscriptionInfo infoSim1 = Objects.requireNonNull(sManager).getActiveSubscriptionInfoForSimSlotIndex(0);
        SubscriptionInfo infoSim2 = sManager.getActiveSubscriptionInfoForSimSlotIndex(1);
        //context.getSharedPreferences(
        //        getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        //prefFile=prefFile+etNumber1.getText().toString();
        //        sharedPreferences = getSharedPreferences(prefFile,MODE_PRIVATE);
        simsharedPreferences = getSharedPreferences(simFile,MODE_PRIVATE);
        SharedPreferences.Editor simprefEdit = simsharedPreferences.edit();
        if(telephonyInfo.isSIM1Ready()) {
             iccidnum1 = infoSim1.getIccId();
             icnum1=iccidnum1;
            simprefEdit.putString("iccid1",iccidnum1);
            Log.e("ICCID1",iccidnum1);
        }
        if(telephonyInfo.isSIM2Ready()) {
             iccidnum2 = infoSim2.getIccId();
             icnum2=iccidnum2;
            simprefEdit.putString("iccid2",iccidnum2);
            Log.e("ICCID2",iccidnum2);
        }
       // simprefEdit.putString("iccid1",iccidnum1);
        //simprefEdit.putString("iccid2",iccidnum2);
        simprefEdit.apply();
    } // onCreate close

    // GET IMEI NO
    public void getIMEI() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            if(Build.VERSION.SDK_INT<= Build.VERSION_CODES.P)
                imeiNumber=tel.getDeviceId().toString();
            else
                imeiNumber = "NULL";
//                imeiNumber = tel.getImei();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
        }

    }

    // FOR REGISTRATION
    public void btnRegister(View view) {
        final String name = etName.getText().toString().trim();
        final String email = etEmail.getText().toString().trim();
        final String password = etPassword.getText().toString().trim();
        final String confirmPassword = etConfirmPassword.getText().toString().trim();
        final String city = etCity.getText().toString().trim();
        final String mobile = etMobileNo.getText().toString().trim();
        final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
//      final String passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{4,}$";
//      final String mobilePattern = "^[6-9]\d{9}$"
        progressBarRegister.setVisibility(View.VISIBLE);
        try {

            if (name.length() == 0 && email.length() == 0 && password.length() == 0 && confirmPassword.length() == 0 && city.length() == 0
                    && mobile.length() == 0)
            {
                progressBarRegister.setVisibility(View.INVISIBLE);
                etName.requestFocus();
                etName.setError("Required");
                etEmail.requestFocus();
                etEmail.setError("Required");
                etPassword.requestFocus();
                etPassword.setError("Required");
                etConfirmPassword.requestFocus();
                etConfirmPassword.setError("Required");
                etCity.requestFocus();
                etCity.setError("Required");
                etMobileNo.requestFocus();
                etMobileNo.setError("Required");

                Toast.makeText(RegisterActivity.this, "Please enter all details!!", Toast.LENGTH_SHORT).show();
                return;

            }
if (name.length() == 0 || email.length() == 0 || password.length() == 0 || confirmPassword.length() == 0 || city.length() == 0
    || mobile.length() == 0)
{
    progressBarRegister.setVisibility(View.INVISIBLE);

        if (name.length() == 0) {
            etName.requestFocus();
            etName.setError("Required");
        }
        if (email.length() == 0) {
            etEmail.requestFocus();
            etEmail.setError("Required");
        }
        if (password.length() == 0) {
            etPassword.requestFocus();
            etPassword.setError("Required");
        }
        if (confirmPassword.length() == 0) {
            etConfirmPassword.requestFocus();
            etConfirmPassword.setError("Required");
        }
        if (city.length() == 0) {
            etCity.requestFocus();
            etCity.setError("Required");
        }
        if (mobile.length() == 0) {
            etMobileNo.requestFocus();
            etMobileNo.setError("Required");
        }
        return;
    }
            if (password.length() < 6) {
                etPassword.requestFocus();
                etPassword.setError("Password length too small");
                return;
            }
            if (!email.matches(emailPattern)) {
                etEmail.requestFocus();
                etEmail.setError("Enter valid email address");
                return;
            }
            if (mobile.length()!=10 || (mobile.charAt(0)!='9' && mobile.charAt(0)!='8' && mobile.charAt(0)!='7' && mobile.charAt(0)!='6' ) ) {
                etMobileNo.requestFocus();
                etMobileNo.setError("Enter valid mobile no");
                return;
            }

            if (password.equals(confirmPassword)) {
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    progressBarRegister.setVisibility(View.INVISIBLE);
                                    finish();

//                                    Users users = new Users();
                                    Users users = new Users(name, email, password, city, mobile,imeiNumber);
                                    users.setName(name);
                                    users.setEmail(email);
                                    users.setPassword(password);
                                    users.setCity(city);
                                    users.setMobile(mobile);
                                    users.setImeiNumber(imeiNumber);

                                    FirebaseDatabase.getInstance().getReference("Users")
                                            .child(FirebaseAuth.getInstance().getUid())
                                            .setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(RegisterActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(getApplicationContext(), PasswordType.class));
                                        }
                                    });

                                } else {
                                    progressBarRegister.setVisibility(View.INVISIBLE);
                                    Toast.makeText(RegisterActivity.this, "Registration failed(User already exists)", Toast.LENGTH_SHORT).show();
                                }

                                //
                            }
                        });
            }
            else
            {
                progressBarRegister.setVisibility(View.INVISIBLE);
                etConfirmPassword.requestFocus();
                etConfirmPassword.setError("Password doesn't match");
                Toast.makeText(RegisterActivity.this, "Please enter same Password and Confirm Password!", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Exception occured!", Toast.LENGTH_SHORT).show();
//            Log.d("exception","Exception occured!!");
        }

    }

} // class close

