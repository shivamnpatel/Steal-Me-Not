package com.project.stealmenot;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

@SuppressLint(value = "Registered")
public class ProtectionModesActivity extends NavigationDrawer implements SensorEventListener {

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    FirebaseUser firebaseUser;
    static Switch switchcharge,motionSwitch,proximitySwitch,headphone;
    static public Switch masterSwitch;
    BroadcastReceiver receiver,receiver1;
    static boolean isMasterModeOn,isChargeModeOn,isheadphoneModeOn;
    CountDownTimer cdt;
    IntentFilter filter,filter1;
    private SensorManager sensorMan,mSensorManager;
    private Sensor mSensor,accelerometer;
    private float[] mGravity;
    private float mAccel,mAccelCurrent,mAccelLast;
    AlertDialog alertDialog;
    private static final int SENSOR_SENSITIVITY = 4;
    int chargerFlag, chargerFlag1, chargerFlag2 = 0;
    int headFlag=0,headFlag1,headFlag2=0;
    int mSwitchSet,pSwitchSet = 0;
    boolean  isHeadphoneConnected;
    /*public static final String[] HEADPHONE_ACTIONS = {
             Intent.ACTION_HEADSET_PLUG,
             "android.bluetooth.headset.action.STATE_CHANGED",
             "android.bluetooth.headset.profile.action.CONNECTION_STATE_CHANGED"
     };*/

    @Override
    public void onResume() {
        super.onResume();
        sensorMan.registerListener( this, accelerometer,
                SensorManager.SENSOR_DELAY_UI);
        //registerReceiver(receiver1,filter1);
        mSensorManager.registerListener( this, mSensor,
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {

        sensorMan.unregisterListener(this);
        mSensorManager.unregisterListener(this);
        // unregisterReceiver(receiver1);
        super.onPause();
        // unregisterReceiver(receiver);
    }

    @RequiresApi(api = Build.VERSION_CODES.N_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_protection);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View ContentView = inflater.inflate(R.layout.activity_protection,null,false);
        mDrawerLayout.addView(ContentView,0);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        if(firebaseUser==null)
        {
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }
        switchcharge = findViewById(R.id.switchCharge);
        masterSwitch = findViewById(R.id.mSwitch);
        motionSwitch = findViewById(R.id.sMotion);
        proximitySwitch = findViewById(R.id.sProximity);
        headphone = findViewById(R.id.headphoneDetector);
        //isMasterModeOn = false;
        isChargeModeOn=false;
        isheadphoneModeOn=false;
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        sensorMan = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorMan.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        alertDialog = new AlertDialog.Builder(this).create();

        // Check saved switch state in SharedPreference
        savedSwitchState();

        // Charging Detection
        chargingDetection();

        // Movement Detection
        movementDetection();

        // Pocket Detection
        pocketDetection();

        // Headphone Detection
        headphoneDetection();

        // Master Mode
        masterMode();
    }

    private void masterMode() {
        masterSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    isMasterModeOn=true;
                    switchcharge.setChecked(isChecked);
                    // registerReceiver(receiver, filter);
                    headphone.setChecked(isChecked);
                    //registerReceiver(receiver1, filter1);
                    Toast.makeText(ProtectionModesActivity.this,"Master Mode Started",Toast.LENGTH_LONG).show();
                    // proximitySwitch.setChecked(isChecked);
                    // motionSwitch.setChecked(isChecked);
                    // startTimer();

                }
                else {
                    isMasterModeOn=false;
                }
                SharedPreferences settings = getSharedPreferences("masterSave", 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("masterSwitchKey", isChecked);
                editor.apply();
            }
        });
    }

    private void headphoneDetection() {
        receiver1=new BroadcastReceiver() {
            //  boolean headsetConnected = false;
            @Override
            public void onReceive(Context context, Intent intent) {

                if (intent.getAction().equals(AudioManager.ACTION_HEADSET_PLUG)) {
                    int state = intent.getIntExtra("state", 0);
                    if (state == 0) {
                        headFlag1 = 1;
                        headFlag = 0;
                        headfun();
                    } else if (state == 1) {

                        headFlag = 1;
                        if(isMasterModeOn) {
                            headphone.setChecked(true);
                            isheadphoneModeOn = true;
                        }
                    }
                }
                // unregisterReceiver(this);
            }
        };

        filter1 = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        registerReceiver(receiver1, filter1);

        headphone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    if (headFlag != 1) {
                        Toast.makeText(ProtectionModesActivity.this, "Connect to Headphone", Toast.LENGTH_SHORT).show();
                        headphone.setChecked(false);
                    }
                    else {
                       // if(!isMasterModeOn)
                            Toast.makeText(ProtectionModesActivity.this, "Headphone Connected", Toast.LENGTH_SHORT).show();
                        headFlag2=1;
                        headfun();
                    }
                }
                else
                {
                    headFlag2=0;
                }
                SharedPreferences settings = getSharedPreferences("headphoneSave", 0);
                SharedPreferences.Editor editor = settings.edit();
               // editor.putBoolean("headphoneSwitchKey", isChecked);
                editor.apply();
            }
        });
    }

    private void pocketDetection() {
        proximitySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {

                if (isChecked) {
                    alertDialog.setTitle("Keep Phone In Your Pocket");
                    alertDialog.setMessage("00:10");
                    //Toast.makeText(MainActivity.this, "Motion Switch On", Toast.LENGTH_SHORT).show();

                    cdt = new CountDownTimer(10000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            alertDialog.setMessage("00:" + (millisUntilFinished / 1000));
                        }

                        @Override
                        public void onFinish() {
                            //info.setVisibility(View.GONE);
                            pSwitchSet = 1;
                            alertDialog.hide();
//                            Toast.makeText(MainActivity.this, "Motion Detection Mode Activated", Toast.LENGTH_SHORT).show();
                        }
                    }.start();
                    alertDialog.show();
                    alertDialog.setCancelable(false);
                } else {
                    Toast.makeText(ProtectionModesActivity.this, "Motion Switch Off", Toast.LENGTH_SHORT).show();
                    pSwitchSet = 0;
                }
                SharedPreferences settings = getSharedPreferences("pocketSave", 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("pocketSwitchKey", isChecked);
                editor.apply();
            }
        });
    }

    private void movementDetection() {
        mAccel = 0.00f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 20, 0);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        motionSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if (isChecked) {
                    alertDialog.setTitle("Will Be Activated In 10 Seconds");
                    alertDialog.setMessage("00:10");
                    //Toast.makeText(MainActivity.this, "Motion Switch On", Toast.LENGTH_SHORT).show();
                    cdt = new CountDownTimer(10000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            alertDialog.setMessage("00:" + (millisUntilFinished / 1000));
                        }
                        @Override
                        public void onFinish() {
                            //info.setVisibility(View.GONE);
                            mSwitchSet = 1;
                            alertDialog.hide();
                            if(!isMasterModeOn)
                                Toast.makeText(ProtectionModesActivity.this, "Motion Detection Mode Activated", Toast.LENGTH_SHORT).show();
                        }
                    }.start();
                    alertDialog.show();
                    alertDialog.setCancelable(false);
                } else {
                    Toast.makeText(ProtectionModesActivity.this, "Motion Switch Off", Toast.LENGTH_SHORT).show();
                    mSwitchSet = 0;
                }
                SharedPreferences settings = getSharedPreferences("motionSave", 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("motionSwitchKey", isChecked);
                editor.apply();
            }
        });
    }

    public void chargingDetection() {

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
                if (plugged == BatteryManager.BATTERY_PLUGGED_AC || plugged == BatteryManager.BATTERY_PLUGGED_USB || plugged == BatteryManager.BATTERY_PLUGGED_WIRELESS) {
                    chargerFlag = 1;
                    if(isMasterModeOn) {
                        switchcharge.setChecked(true);
                        isChargeModeOn = true;
                    }
                    isChargeModeOn=true;
                } else if (plugged == 0) {

                    chargerFlag1 = 1;
                    chargerFlag = 0;
                    isChargeModeOn=false;
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
                         Toast.makeText(ProtectionModesActivity.this, "Connect To Charger", Toast.LENGTH_SHORT).show();
                        switchcharge.setChecked(false);
                        isChargeModeOn=false;
                    } else {
                        //if(!isMasterModeOn)
                        Toast.makeText(ProtectionModesActivity.this, "Charger Protection Mode On", Toast.LENGTH_SHORT).show();
                        chargerFlag2 = 1;
                        isChargeModeOn=true;
                        func();
                    }
                } else {
                    chargerFlag2 = 0;
                }
                SharedPreferences settings = getSharedPreferences("chargingSave", 0);
                SharedPreferences.Editor editor = settings.edit();
               // editor.putBoolean("chargingSwitchKey", isChecked);
                editor.apply();
            }
        });
    }

    private void savedSwitchState() {
       SharedPreferences chargingSP = getSharedPreferences("chargingSave",0);
      // boolean chargingSwitchState = chargingSP.getBoolean("chargingSwitchKey",false);
       //switchcharge.setChecked(chargingSwitchState);

        SharedPreferences pocketSP = getSharedPreferences("pocketSave",0);
        boolean pocketSwitchState = pocketSP.getBoolean("pocketSwitchKey",false);
        proximitySwitch.setChecked(pocketSwitchState);

        SharedPreferences motionSP = getSharedPreferences("motionSave",0);
        boolean motionSwitchState = motionSP.getBoolean("motionSwitchKey",false);
        motionSwitch.setChecked(motionSwitchState);

        SharedPreferences headphoneSP = getSharedPreferences("headphoneSave",0);
        boolean headphoneSwitchState = headphoneSP.getBoolean("headphoneSwitchKey",false);
        //headphone.setChecked(headphoneSwitchState);

        SharedPreferences masterSP = getSharedPreferences("masterSave",0);
        boolean masterSwitchState = masterSP.getBoolean("masterSwitchKey",false);
        masterSwitch.setChecked(masterSwitchState);
    }

    public void startTimer() {
        alertDialog.setTitle("Master Mode Starting in");
        alertDialog.setMessage("00:10");
        cdt = new CountDownTimer(10000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                alertDialog.setMessage("00:" + (millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                //info.setVisibility(View.GONE);
               // pSwitchSet = 1;
                //mSwitchSet = 1;
                alertDialog.hide();
            }
        }.start();
        alertDialog.show();
        alertDialog.setCancelable(false);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                return true;
            default:
                return super.dispatchKeyEvent(event);
        }
    }
    public void func() {
//        Toast.makeText(MainActivity.this, "ChargerFlag"+chargerFlag+"Chargerflag1:"+chargerFlag1, Toast.LENGTH_SHORT).show();
        if (chargerFlag == 0 && chargerFlag1 == 1 && chargerFlag2 == 1) {
            unregisterReceiver(receiver);
            isChargeModeOn=false;
            checkPasswordType();
            //startActivity(new Intent(ProtectionModesActivity.this, Security.class));
            chargerFlag2 = 0;

            finish();
            //masterSwitch.setChecked(true);
        }
    }

    public void headfun() {
        if(headFlag==0 && headFlag1==1 && headFlag2==1)
        {
            unregisterReceiver(receiver1);
            isheadphoneModeOn=false;
            checkPasswordType();
            //startActivity(new Intent(ProtectionModesActivity.this,Security.class));
            headFlag2=0;

            finish();
            //masterSwitch.setChecked(true);
        }
    }

    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            mGravity = event.values.clone();
            // Shake detection
            float x = mGravity[0];
            float y = mGravity[1];
            float z = mGravity[2];
            mAccelLast = mAccelCurrent;
            mAccelCurrent = (float) Math.sqrt(x * x + y * y + z * z);
            float delta = mAccelCurrent - mAccelLast;
            mAccel = mAccel * 0.9f + delta;
            // Make this higher or lower according to how much
            // motion you want to detect
            if (mAccel > 0.5) {
                //Toast.makeText(MainActivity.this, "Sensor Run Hua Bc", Toast.LENGTH_SHORT).show();
                //MediaPlayer mPlayer = MediaPlayer.create(MainActivity.this, R.raw.siren);
                //mPlayer.start();
                if (mSwitchSet == 1) {
//                    wakeDevice();
                    checkPasswordType();
                    //startActivity(new Intent(ProtectionModesActivity.this, Security.class));
                    //----->>>
                    finish();
                    //masterSwitch.setChecked(true);
                }
            }
        }
        else if (event.sensor.getType()== Sensor.TYPE_PROXIMITY){
            if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
                if (event.values[0] >= -SENSOR_SENSITIVITY && event.values[0] <= SENSOR_SENSITIVITY) {
                    //near
//                    Toast.makeText(getApplicationContext(), "near", Toast.LENGTH_SHORT).show();
                } else if (pSwitchSet==1) {
                    checkPasswordType();
                    //startActivity(new Intent(ProtectionModesActivity.this, Security.class));
                    //----->>>
                    finish();
                    //masterSwitch.setChecked(true);
                    //far
//                    Toast.makeText(getApplicationContext(), "far", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void checkPasswordType()
    {
        SharedPreferences sharedPreferences=getSharedPreferences("Pin_Type_Check",0);
        //SharedPreferences.Editor edit=sharedPreferences.edit();
        //edit.putString("UserDefinedPin","Success");
        //edit.apply();

        String result=sharedPreferences.getString("UserDefinedPin",null);

        if(result.equals("Success"))
        {
            startActivity(new Intent(ProtectionModesActivity.this,EnterPin.class));

        }
        else
        {
            startActivity(new Intent(ProtectionModesActivity.this,Security.class));

        }
    }

}
