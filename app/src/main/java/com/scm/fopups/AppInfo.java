package com.scm.fopups;

import android.graphics.drawable.Drawable;
import android.util.Log;

public class AppInfo implements Comparable<AppInfo>{
    private String appName;
    private Drawable icon;
    private String packageName;
    private boolean isTracked;
    private boolean isUsageExceeded;

    public AppInfo(String appName, Drawable icon, String packageName, boolean isTracked, boolean isUsageExceeded) {
        this.appName = appName;
        this.icon = icon;
        this.packageName = packageName;
        this.isTracked = isTracked;
        this.isUsageExceeded = isUsageExceeded;
    }
    public String getAppName() {
        return appName;
    }
    Drawable getIcon() {
        return icon;
    }
    public String getPackageName() {
        return packageName;
    }
    boolean getIsTracked() {
        return isTracked;
    }
    boolean getIsUsageExceeded() {
        return isUsageExceeded;
    }

    @Override
    public int compareTo(AppInfo appInfo) {
        return appName.compareTo(appInfo.getAppName());
    }
}
