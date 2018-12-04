package com.pro.ahmed.weather2.sevices;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, NotificationService.class));
        Toast.makeText(context, "Hello Service!!", Toast.LENGTH_SHORT).show();
    }
}
