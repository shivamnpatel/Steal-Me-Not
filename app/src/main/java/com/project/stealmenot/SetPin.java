package com.project.stealmenot;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SetPin extends AppCompatActivity {
    EditText etSetPin, etConfirmPin, etEmail;
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String Email = "emailKey";
    public static final String Password = "passwordKey";

    SharedPreferences sharedpreferences;
    Button btnSetPin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_pin);
       etSetPin = (EditText) findViewById(R.id.edtSetPin);
       etConfirmPin = (EditText) findViewById(R.id.etConfirmPin);
        btnSetPin = (Button) findViewById(R.id.btnSetPin);
        etEmail = (EditText) findViewById(R.id.etEmail);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String email = sharedpreferences.getString("emailKey", "");
        if (email.length() > 0) {
            startActivity(new Intent(SetPin.this, MainActivity.class));
        }
        btnSetPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pin = etSetPin.getText().toString();
                String confirmPin = etConfirmPin.getText().toString();
                String email = etEmail.getText().toString();
                if (TextUtils.isEmpty(email)) {
                    etEmail.setError("Required");
                    etEmail.requestFocus();
                    return;
                } else if (TextUtils.isEmpty(pin) || pin.length() < 4) {
                    etSetPin.setError("Required! Minimum length 4 digit");
                    etSetPin.requestFocus();
                    return;
                } else if (TextUtils.isEmpty(confirmPin) || confirmPin.length() < 4) {
                    etConfirmPin.setError("Required! Minimum length 4 digit");
                    etConfirmPin.requestFocus();
                    return;
                }
                if (pin.equals(confirmPin)) {
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString(Email, email);
                    editor.putString(Password, confirmPin);
                    editor.commit();
                    Toast.makeText(getApplicationContext(), "Password Set", Toast.LENGTH_SHORT).show();
                    afterSettingPin();
                    startActivity(new Intent(SetPin.this, MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Password Does Not Match", Toast.LENGTH_SHORT).show();
                }

            }
        });



    }

    public void afterSettingPin()
    {
        SharedPreferences sharedPreferences=getSharedPreferences("Pin_Type_Check",0);
        SharedPreferences.Editor edit=sharedPreferences.edit();

        edit.putString("UserDefinedPin","Success");
        edit.apply();
    }
}