package com.project.stealmenot;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class EmergencyContacts extends NavigationDrawer {

    EditText etNumber1,etNumber2,etSavedNumber1,etSavedNumber2;
    public static String savedNumber1,savedNumber2;
    String data="NULL",prefFile="app_data";
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_emergency_contacts);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View ContentView = inflater.inflate(R.layout.activity_emergency_contacts,null,false);
        mDrawerLayout.addView(ContentView,0);

        // Storing number
        etNumber1 = findViewById(R.id.etNumber1);
        //etNumber2 = findViewById(R.id.etNumber2);
        etSavedNumber1 = findViewById(R.id.etSavedNumber1);
        //etSavedNumber2 = findViewById(R.id.etSavedNumber2);
        prefFile=prefFile+etNumber1.getText().toString();
        //prefFile=prefFile+etNumber2.getText().toString();
        sharedPreferences = getSharedPreferences(prefFile,MODE_PRIVATE);

        fetchSavedContact();
    }

    //onClick of Save btn
    public void storeContactNumbers(View view) {
        String number1 = etNumber1.getText().toString();
       // String number2 = etNumber2.getText().toString();
        if(number1.length()<10)
            Toast.makeText(getApplicationContext(),"Please enter valid number!!",Toast.LENGTH_SHORT).show();

        else {
            SharedPreferences.Editor prefEdit = sharedPreferences.edit();
            prefEdit.putString("Number1", number1);
           // prefEdit.putString("Number2", number2);
            //etSavedNumber.setText(number1);
            prefEdit.commit();
            Toast.makeText(getApplicationContext(), "Number saved successfully", Toast.LENGTH_SHORT).show();
        }

    }
    public void fetchSavedContact()
    {
        SharedPreferences.Editor prefEdit = sharedPreferences.edit();
        //pref = getSharedPreferences(prefFile,MODE_PRIVATE);
        savedNumber1=sharedPreferences.getString("Number1","null");
        //savedNumber2=sharedPreferences.getString("Number2","null");
        etSavedNumber1.setText(savedNumber1);
        //etSavedNumber2.setText(savedNumber2);
       // Toast.makeText(getApplicationContext(),"Number is: "+savedNumber,Toast.LENGTH_SHORT).show();

        prefEdit.commit();
    }
}
