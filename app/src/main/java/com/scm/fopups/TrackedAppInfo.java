package com.scm.fopups;

public class TrackedAppInfo {
    private String packageName;
    private int timeAllowed;
    private int isUsageExceeded;

    TrackedAppInfo(String packageName, int timeAllowed, int isUsageExceeded) {
        this.packageName = packageName;
        this.timeAllowed = timeAllowed;
        this.isUsageExceeded = isUsageExceeded;
    }

    String getPackageName() {
        return packageName;
    }

    int getTimeAllowed() {
        return timeAllowed;
    }

    public int getIsUsageExceeded() {
        return isUsageExceeded;
    }
}
