package com.project.stealmenot;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

public class DeviceAdminActivity extends NavigationDrawer {

    static final int RESULT_ENABLE = 1;
    DevicePolicyManager devicePolicyManager;
    ComponentName compName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_protection);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View ContentView = inflater.inflate(R.layout.activity_device_admin2,null,false);
        mDrawerLayout.addView(ContentView,0);

        devicePolicyManager= (DevicePolicyManager) getSystemService(Context. DEVICE_POLICY_SERVICE);
        compName = new ComponentName( this, DeviceAdmin. class);

        boolean active = devicePolicyManager .isAdminActive( compName);

        if (active) {
            Toast.makeText(this,"Permission Already enabled",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this,ProtectionModesActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();

        }
        else {
            Intent intent = new Intent(DevicePolicyManager. ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager. EXTRA_DEVICE_ADMIN, compName);
            intent.putExtra(DevicePolicyManager. EXTRA_ADD_EXPLANATION, "You should enable this to secure your device!");
            startActivityForResult(intent , RESULT_ENABLE);
            finish();
        }
    }

    /*public void wipeData(View view) {
        try {
            devicePolicyManager.wipeData(0);
//            devicePolicyManager.setStorageEncryption(compName,true);
            Toast.makeText(this,"Data wiped success",Toast.LENGTH_SHORT).show();
        }
        catch (Exception e)
        {
            Toast.makeText(this,"Exception occurred: "+e,Toast.LENGTH_SHORT).show();
        }
    }*/


 /*   public void lockPhone (View view) {
        devicePolicyManager .lockNow();
    }
*/
}


