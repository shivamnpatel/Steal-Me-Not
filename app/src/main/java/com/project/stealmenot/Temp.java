package com.project.stealmenot;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Temp extends AppCompatActivity {
    int chargerFlag, chargerFlag1, chargerFlag2 = 0;
    BroadcastReceiver receiver,receiver1;
    IntentFilter filter,filter1;

    Switch switchcharge;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp);
        switchcharge = findViewById(R.id.switchCharge);

        chargingDetection();
        //switchcharge.setChecked(true);



    }

    public void chargingDetection() {

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
                if (plugged == BatteryManager.BATTERY_PLUGGED_AC || plugged == BatteryManager.BATTERY_PLUGGED_USB || plugged == BatteryManager.BATTERY_PLUGGED_WIRELESS) {
                    chargerFlag = 1;
                    switchcharge.setChecked(true);
                } else if (plugged == 0) {
                    chargerFlag1 = 1;
                    chargerFlag = 0;
                    func();
                }
                // unregisterReceiver(this);
            }
        };
        filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(receiver, filter);


        switchcharge.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if (isChecked) {
                    if (chargerFlag != 1) {
                        Toast.makeText(Temp.this, "Connect To Charger", Toast.LENGTH_SHORT).show();
                        switchcharge.setChecked(false);
                    } else {
                        //if(!isMasterModeOn)
                        Toast.makeText(Temp.this, "Charger Protection Mode On", Toast.LENGTH_SHORT).show();
                        chargerFlag2 = 1;
                        func();
                    }
                } else {
                    chargerFlag2 = 0;
                }
                SharedPreferences settings = getSharedPreferences("chargingSave", 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("chargingSwitchKey", isChecked);
                editor.apply();
            }
        });
    }
    public void func() {
//        Toast.makeText(MainActivity.this, "ChargerFlag"+chargerFlag+"Chargerflag1:"+chargerFlag1, Toast.LENGTH_SHORT).show();
        if (chargerFlag == 0 && chargerFlag1 == 1 && chargerFlag2 == 1) {
            unregisterReceiver(receiver);
            startActivity(new Intent(Temp.this, Security.class));
            chargerFlag2 = 0;
            finish();
            //masterSwitch.setChecked(true);
        }
    }
}
