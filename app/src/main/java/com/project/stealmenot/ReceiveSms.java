package com.project.stealmenot;

import android.annotation.SuppressLint;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.util.Log;
import android.widget.Toast;

import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;
import static com.project.stealmenot.RegisterActivity.icnum1;
import static com.project.stealmenot.RegisterActivity.icnum2;
import static com.project.stealmenot.TelephonyInfo.getInstance;

public class ReceiveSms extends BroadcastReceiver {
    private static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 100;
    SharedPreferences sharedPreferences;
    String prefFile = "app_data";
    String simFile = "sim_data";
    public static String savedNumber1 = "+91",savedNumber2 = "+91";
    DevicePolicyManager devicePolicyManager;
    ComponentName componentName;
    SharedPreferences simsharedPreferences;
    String iccid1, iccid2, iccidnum1, iccidnum2;
    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
//        Toast.makeText(context,"SMS Received",Toast.LENGTH_SHORT).show();

        sharedPreferences = context.getSharedPreferences(prefFile, MODE_PRIVATE);
        SharedPreferences.Editor prefEdit = sharedPreferences.edit();
        savedNumber1 += sharedPreferences.getString("Number1", "null");
        //savedNumber2 += sharedPreferences.getString("Number2", "null");
        prefEdit.apply();

        simsharedPreferences = context.getSharedPreferences(simFile, MODE_PRIVATE);
        icnum1+=simsharedPreferences.getString("iccid1", iccid1);
        icnum2+=simsharedPreferences.getString("iccid2", iccid2);

        devicePolicyManager = (DevicePolicyManager)
                context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        componentName = new ComponentName(context, DeviceAdmin.class);

        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            Bundle bundle = intent.getExtras();
            SmsMessage[] msgs;
            String msg_from = "", msg_body = "";
            if (bundle != null) {
                try {
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    msgs = new SmsMessage[pdus.length];
                    // Reading the content of the msg
                    for (int i = 0; i < msgs.length; i++) {
                        // for API >= 23
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            String format = bundle.getString("format");
                            // from PDU we get all object and SmsMessage object using below code
                            msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i], format);
                        } else {
                            // for API < 23
                            msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                        }
                        msg_from = msgs[i].getOriginatingAddress();
                        msg_body = msgs[i].getMessageBody();
                        //Toast.makeText(context,"From: "+msg_from+" , Body: "+msg_body,Toast.LENGTH_LONG).show();
                    }

                    // Receive SMS command for GPS location and sent the SMS back...
//                     String receivedFrom = EmergencyContacts.savedNumber;
//                    Double receivedFrom1 = Double.parseDouble(savedNumber1);
//                    Double receivedFrom2 = Double.parseDouble(savedNumber2);

                    if (msg_body.equalsIgnoreCase("get gps") && msg_from.equals(savedNumber1)) {
//                        Toast.makeText(context,"Emerg Msg from: "+EmergencyContacts.savedNumber,Toast.LENGTH_LONG).show();
//                        Toast.makeText(context,"Msg from: "+msg_from,Toast.LENGTH_LONG).show();
                        Toast.makeText(context, "GPS COMMAND RECEIVED!!", Toast.LENGTH_SHORT).show();
                        Intent newIntent = new Intent(context, GPSLocation.class);
//                        newIntent.setClassName("com.project.stealmenot","com.project.stealmenot.GPSLocation");
                        newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        context.startActivity(newIntent);
                    }
                    // extra
                    else if (msg_body.equalsIgnoreCase("lock device") && msg_from.equals(savedNumber1)) {
                        Toast.makeText(context, "LOCK DEVICE COMMAND RECEIVED!!", Toast.LENGTH_SHORT).show();

                        devicePolicyManager.lockNow();
                    } else if (msg_body.equalsIgnoreCase("wipe data") && msg_from.equals(savedNumber1)) {
                        Toast.makeText(context, "WIPE DATA COMMAND RECEIVED!!", Toast.LENGTH_SHORT).show();
//                        devicePolicyManager.wipeData(0);
                        try {
                            devicePolicyManager.wipeData(0);
                            Toast.makeText(context, "Data wiped success", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Toast.makeText(context, "Exception occurred: " + e, Toast.LENGTH_SHORT).show();
                        }
                    }
                     else if (msg_body.equalsIgnoreCase("new sim detect") && msg_from.equals(savedNumber1)) {
                        Toast.makeText(context, "SIM DETECT COMMAND RECEIVED!!", Toast.LENGTH_LONG).show();
                        /// Remaining /////
                        try {
                            SubscriptionManager sManager = (SubscriptionManager) context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
                            final int REQ_CODE = 100;

                            TelephonyInfo telephonyInfo = getInstance(context);


                            @SuppressLint("MissingPermission") SubscriptionInfo infoSim1 = (sManager).getActiveSubscriptionInfoForSimSlotIndex(0);
                            @SuppressLint("MissingPermission") SubscriptionInfo infoSim2 = sManager.getActiveSubscriptionInfoForSimSlotIndex(1);
                            if (telephonyInfo.isSIM1Ready()) {
                                iccidnum1 = infoSim1.getIccId();
                                if (Objects.equals(icnum1, iccidnum1)) {
//                                    Intent sendIntent = new Intent(Intent.ACTION_VIEW);
//                                    sendIntent.setData(Uri.parse("sms:"+number));
//                                    sendIntent.putExtra("sms_body", "Sim 1 is changed");
//                                    context.startActivity(sendIntent);

                                    Toast.makeText(context, "Sim 1 is changed: iccid1=" + icnum1 + ",,, (current)  iccidnum1="+ iccidnum1, Toast.LENGTH_LONG).show();
                                    SmsManager smsManager = SmsManager.getDefault();
                                    smsManager.sendTextMessage(msg_from.substring(3,13), null, "Sim 1 is changed: Your iccid1=" + icnum1 + " Present Sim iccidnum1="+ iccidnum1 +  " New inserted Sim Number is:  "  + telephonyInfo.getMobnumber() + " ", null, null);

                                } else {
//                                    Intent sendIntent = new Intent(Intent.ACTION_VIEW);
//                                    sendIntent.setData(Uri.parse("sms:"+number));
//                                    sendIntent.putExtra("sms_body", "Sim 1 is Working Fine");
//                                    context.startActivity(sendIntent);
                                    Toast.makeText(context, "Sim 1 is fine", Toast.LENGTH_LONG).show();
                                    SmsManager smsManager = SmsManager.getDefault();
                                    smsManager.sendTextMessage(msg_from.substring(3,13), null, "Sim 1 is working fine" , null, null);

                                }
                                // simprefEdit.putString("iccid1",iccidnum1);
                            }
                           if (telephonyInfo.isSIM2Ready()) {
                                iccidnum2 = infoSim2.getIccId();
                                if (Objects.equals(iccid2, iccidnum2)) {
//                                    Intent sendIntent = new Intent(Intent.ACTION_VIEW);
//                                    sendIntent.setData(Uri.parse("sms:"+number));
//                                    sendIntent.putExtra("sms_body", "Sim 2 is changed");
//                                    context.startActivity(sendIntent);
                                    Toast.makeText(context, "Sim 2 is changed", Toast.LENGTH_LONG).show();
                                    SmsManager smsManager = SmsManager.getDefault();
                                    smsManager.sendTextMessage(msg_from.substring(3,13), null, "Sim 2 is changed : Your iccid2=" + iccidnum2 + " Present Sim iccidnum2=" + iccidnum2 +  " New inserted Sim Number is:  "  + telephonyInfo.getMobnumber() + " ", null, null);

                                } else {
//                                    Intent sendIntent = new Intent(Intent.ACTION_VIEW);
//                                    sendIntent.setData(Uri.parse("sms:"+number));
//                                    sendIntent.putExtra("sms_body", "Sim 2 is Working Fine");
//                                    context.startActivity(sendIntent);
                                    Toast.makeText(context, "Sim 2 is fine", Toast.LENGTH_LONG).show();
                                    SmsManager smsManager = SmsManager.getDefault();
                                    smsManager.sendTextMessage(msg_from.substring(3,13), null, "Sim 2 is fine", null, null);

                                }
                                //simprefEdit.putString("iccid2",iccidnum2);

                           }
                        } catch (Exception e) {
                            Log.e("SMS error sim detect", "Sim detect");
                            e.printStackTrace();
                            Toast.makeText(context, "Exception occurred: " + e, Toast.LENGTH_SHORT).show();

                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}