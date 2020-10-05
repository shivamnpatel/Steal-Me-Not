package com.project.stealmenot;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class DeviceAdmin extends DeviceAdminReceiver {

    @Override
    public void onEnabled(Context context, Intent intent) {
        super.onEnabled(context, intent);
        Toast.makeText(context, "Device Admin permission enabled", Toast.LENGTH_SHORT).show();
        //context.startActivity(new Intent(context, ProtectionModesActivity.class));
        Intent intent1 = new Intent(context,ProtectionModesActivity.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent1);

    }

    @Override
    public void onDisabled(Context context, Intent intent) {
        super.onDisabled(context, intent);
        Toast.makeText(context, "Device Admin permission disabled", Toast.LENGTH_SHORT).show();
        //context.startActivity(new Intent(context, ProtectionModesActivity.class));
        /*Intent intent1 = new Intent(context,ProtectionModesActivity.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent1);*/
    }


}
