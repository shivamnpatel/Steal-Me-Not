package com.project.stealmenot;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class NavigationDrawer extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    protected DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    protected NavigationView navigationView;
    DevicePolicyManager devicePolicyManager;
    ComponentName componentName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer);
        mToggle = new ActionBarDrawerToggle(this,mDrawerLayout,R.string.open,R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        if(getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        navigationView =  (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
        // to highlight the menu item selected or clicked
        //navigationView.getMenu().getItem(0).setChecked(true);
       //navigationView.setCheckedItem();
        devicePolicyManager= (DevicePolicyManager) getSystemService(Context. DEVICE_POLICY_SERVICE);
        componentName = new ComponentName( this, DeviceAdmin. class);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(mToggle.onOptionsItemSelected(item))
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        int id = menuItem.getItemId();

        if(id==R.id.nav_home)
            startActivity(new Intent(getApplicationContext(),ProtectionModesActivity.class));

        if(id==R.id.nav_profile)
            startActivity(new Intent(getApplicationContext(),ProfileActivity.class));

        if(id==R.id.nav_emergencyContacts)
            startActivity(new Intent(getApplicationContext(),EmergencyContacts.class));

        if(id==R.id.nav_tools)
            startActivity(new Intent(getApplicationContext(),SettingsActivity.class));

        if(id==R.id.nav_logout)
            startActivity(new Intent(getApplicationContext(),LogoutActivity.class));

        if(id==R.id.nav_deviceAdminEnable)
            startActivity(new Intent(getApplicationContext(),DeviceAdminActivity.class));

        if(id==R.id.nav_deviceAdminDisable) {
            devicePolicyManager.removeActiveAdmin(componentName);
        }

        if(id==R.id.nav_info)
            startActivity(new Intent(getApplicationContext(),AboutUs.class));

        if(id==R.id.postProtection)
            startActivity(new Intent(getApplicationContext(),PostProtectionModes.class));

        if(id==R.id.nav_share)
        {
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareBody = "Download Steal Me Not to protect your device from getting Stolen!";
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT,"Subject here");
            sharingIntent.putExtra(Intent.EXTRA_TEXT,shareBody);
            startActivity(Intent.createChooser(sharingIntent,"Share via"));
        }

        return true;
    }

}
