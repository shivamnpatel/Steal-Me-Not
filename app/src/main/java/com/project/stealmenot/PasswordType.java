package com.project.stealmenot;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


public class PasswordType extends AppCompatActivity {
    private KeyguardManager mKeyguardManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_type);
    }

    public void onRadioBtnPressed(View view) {

        boolean checked= ((RadioButton) view).isChecked();

        switch(view.getId())
        {
            case R.id.rbtnDevicePassword:
                if(checked)
                {
                    checkifDeviceisSecured();

                    //startActivity(new Intent(PasswordType.this,PasswordType.class));


                    //startActivity(new Intent(PasswordType.this,ProtectionModesActivity.class));
                }
                break;
            case R.id.rbtnNewPIN:
                if(checked)
                {
                    beforeSettingPin();
                    startActivity(new Intent(PasswordType.this,SetPin.class));
                }
                break;
        }

    }

    public void beforeSettingPin()
    {
        SharedPreferences sharedPreferences=getSharedPreferences("Pin_Type_Check",0);
        SharedPreferences.Editor edit=sharedPreferences.edit();

        edit.putString("UserDefinedPin","yes");
        edit.apply();
    }


    public void checkifDeviceisSecured()
    {
        mKeyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        if (!mKeyguardManager.isKeyguardSecure()) {
            // Show a message that the user hasn't set up a lock screen.

            Toast.makeText(this,
                    "Secure lock screen hasn't set up.\n"
                            + "Go to 'Settings -> Security -> Screenlock' to set up a lock screen",
                    Toast.LENGTH_LONG).show();
        }
        else if(mKeyguardManager.isKeyguardSecure())
        {
            startActivity(new Intent(PasswordType.this, ProtectionModesActivity.class));
        }

    }
}
