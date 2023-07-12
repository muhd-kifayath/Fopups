package com.scm.fopups;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.util.Calendar;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class NotificationReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "Fopup_Note";
    private static final String TAG = "BackgroundService";
    Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        TrackedAppHelper dbHelper = new TrackedAppHelper(context);
        List<TrackedAppInfo> trackedAppInfos = dbHelper.getAllRows();
        this.context = context;

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        long beginTime = calendar.getTimeInMillis();
        long endTime = beginTime + Utils.DAY_IN_MILLIS;
        HashMap<String, Integer> appUsageMap = Utils.getTimeSpent(context, null, beginTime, endTime);

        String currentRunningPackageName = null;
        List<String> list = appUsageMap.keySet().stream().filter(s -> s.startsWith("current")).collect(Collectors.toList());
        if(list.size() > 0) {currentRunningPackageName = list.get(0).replaceFirst("current", "");}

        for(int i = 0; i < trackedAppInfos.size(); i++) {
            TrackedAppInfo trackedAppInfo = trackedAppInfos.get(i);
            String packageName = trackedAppInfo.getPackageName();

            if(appUsageMap.containsKey(packageName)) {
                Integer usageTime = appUsageMap.get(packageName);
                if(usageTime == null) usageTime = 0;
                int allowedTime = trackedAppInfo.getTimeAllowed();
                int isUsageExceeded = trackedAppInfo.getIsUsageExceeded();

                if((usageTime > allowedTime && isUsageExceeded == 0) ||
                        (isUsageExceeded == 1 && packageName.equals(currentRunningPackageName))) {
                    try {
                        dbHelper.setIsUsageExceeded(packageName);
                        String appName = (String) context.getPackageManager()
                                .getApplicationLabel(context.getPackageManager().getApplicationInfo(packageName, 0));
                        showNotification(appName, i);
                    } catch (PackageManager.NameNotFoundException e) {
                        Log.e(TAG, "package name not found");
                    }
                }
            }
        }
    }

    private void createNotificationChannel(NotificationManager notificationManager) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is not in the Support Library.
        CharSequence name = "Limit";
        String description = "Time exceeded Task recommendation";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);
        channel.enableVibration(true);
        // Register the channel with the system. You can't change the importance
        // or other notification behaviors after this.
        notificationManager.createNotificationChannel(channel);
    }


    private void showNotification(String appName, int id) {
        Intent intent = new Intent(context, AppInfoActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                intent, PendingIntent.FLAG_MUTABLE);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.warning)
                        .setContentTitle("Exceeded Limit")
                        .setContentText("You have lot of pending work")
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setCategory(NotificationCompat.CATEGORY_CALL);

        // Use a full-screen intent only for the highest-priority alerts where you
        // have an associated activity that you would like to launch after the user
        // interacts with the notification. Also, if your app targets Android 10
        // or higher, you need to request the USE_FULL_SCREEN_INTENT permission in
        // order for the platform to invoke this notification.

        notificationBuilder.setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        createNotificationChannel(notificationManager);

        notificationManager.notify(0, notificationBuilder.build());

    }
}
