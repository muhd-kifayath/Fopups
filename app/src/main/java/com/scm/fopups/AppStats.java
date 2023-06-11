package com.scm.fopups;

import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class AppStats {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-M-d HH:mm:ss");
    private static final String TAG = AppStats.class.getSimpleName();

    @SuppressWarnings("ResourceType")
    public List<UsageStats> getDailyStatsList(Context context) {
        UsageStatsManager usm = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        Calendar calendar = Calendar.getInstance();
        long endTime = calendar.getTimeInMillis();
        calendar.add(Calendar.YEAR, -1);
        long startTime = calendar.getTimeInMillis();

        Log.d(TAG, "Range start:" + dateFormat.format(startTime));
        Log.d(TAG, "Range end:" + dateFormat.format(endTime));

        return usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime);
    }

    public List<UsageStats> getWeeklyStatsList(Context context) {
        UsageStatsManager usm = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        Calendar calendar = Calendar.getInstance();
        long endTime = calendar.getTimeInMillis();
        calendar.add(Calendar.YEAR, -1);
        long startTime = calendar.getTimeInMillis();

        Log.d(TAG, "Range start:" + dateFormat.format(startTime));
        Log.d(TAG, "Range end:" + dateFormat.format(endTime));

        return usm.queryUsageStats(UsageStatsManager.INTERVAL_WEEKLY, startTime, endTime);
    }

    public void printCurrentUsageStatus(Context context) {
        List<UsageStats> usageStatsList = getDailyStatsList(context);
        for (UsageStats u : usageStatsList) {
            Log.d(TAG, "Pkg: " + u.getPackageName() + "\t" + "ForegroundTime: "
                    + u.getTotalTimeInForeground());
        }
    }

    @SuppressWarnings("ResourceType")
    public static UsageEvents getEvents(Context context) {
        UsageStatsManager usm = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        Calendar calendar = Calendar.getInstance();
        long endTime = calendar.getTimeInMillis();
        calendar.add(Calendar.DATE, -1);
        long startTime = calendar.getTimeInMillis();

        Log.d(TAG, "Range start:" + dateFormat.format(startTime));
        Log.d(TAG, "Range end:" + dateFormat.format(endTime));

        return usm.queryEvents(startTime, endTime);
    }

    public static void printEvents(UsageEvents events) {
        UsageEvents.Event e = new UsageEvents.Event();
        while (events.hasNextEvent()) {
            events.getNextEvent(e);
            String time = dateFormat.format(e.getTimeStamp());
            Log.d(e.getPackageName(), time);
        }
    }

    @SuppressWarnings("ResourceType")
    public static void printUsageMap(Context context) {
        UsageStatsManager manager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        long startTime = calendar.getTimeInMillis();

        calendar.set(Calendar.HOUR_OF_DAY, 8);
        long endTime = calendar.getTimeInMillis();

        Log.d(TAG, "Range start:" + dateFormat.format(startTime));
        Log.d(TAG, "Range end:" + dateFormat.format(endTime));

        Map<String, UsageStats> map = manager.queryAndAggregateUsageStats(startTime, endTime);
        for (String packageName : map.keySet()) {
            UsageStats us = map.get(packageName);
            try {
                long timeMs = us.getTotalTimeInForeground();
                long timeMinutes = timeMs / (60 * 1000);
                if (timeMinutes > 0) {
                    Log.d(packageName, timeMinutes + " mins");
                }
            } catch (Exception e) {
            }
        }
    }
}