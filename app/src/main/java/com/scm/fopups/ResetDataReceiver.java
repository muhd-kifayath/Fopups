package com.scm.fopups;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ResetDataReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        TrackedAppHelper dbHelper = new TrackedAppHelper(context);
        dbHelper.resetAllIsUsageExceeded();
        dbHelper.close();
    }
}