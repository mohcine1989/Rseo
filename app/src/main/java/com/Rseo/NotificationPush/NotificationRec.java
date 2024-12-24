package com.Rseo.NotificationPush;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class NotificationRec extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getBundleExtra("NOTIFICATION_BUNDLE");
        if (bundle != null) {
            NotificationAlarm.showNotification(context, bundle , context.getClass() );
        }

    }

}