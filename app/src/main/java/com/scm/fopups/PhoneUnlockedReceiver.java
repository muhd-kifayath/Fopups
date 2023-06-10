package com.scm.fopups;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class PhoneUnlockedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
            Log.d("lockstate", "Phone unlocked");
        }
        if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)){
            Log.d("lockstate", "Phone unlocked");
        }
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)){
            Log.d("lockstate", "Phone locked");
        }
    }
}