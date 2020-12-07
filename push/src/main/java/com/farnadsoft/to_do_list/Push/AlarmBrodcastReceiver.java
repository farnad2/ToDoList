package com.farnadsoft.to_do_list.Push;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by BLiveInHack on 10-09-2016.
 */
public class AlarmBrodcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service = new Intent(context, AlarmReceiver.class);
        context.startService(service);
    }
}
