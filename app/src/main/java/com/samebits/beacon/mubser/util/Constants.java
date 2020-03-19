package com.samebits.beacon.mubser.util;

public final class Constants {

    public static final String TAG = "BeaconLocator";
    public static final String LOG_TAG = "logTag";
    public static final String DEFAULT_PROJECT_NAME = "default";
    public static final int SORT_DISTANCE_FAR_FIRST = 1;
    public static final int SORT_DISTANCE_NEAREST_FIRST = 0;
    public static final int SORT_UUID_MAJOR_MINOR = 2;
    public static final String TAG_FRAGMENT_SCAN_LIST = "SCAN_LIST";
    public static final String TAG_FRAGMENT_SCAN_RADAR = "SCAN_RADAR";
    public static final String TAG_FRAGMENT_TRACKED_BEACON_LIST = "TRACKED_BEACON_LIST";

    public static final String ARG_BEACON = "ARG_BEACON";

    public static final int REQ_GLOBAL_SETTING = 10078;

    public static final String ALARM_NOTIFICATION_SHOW = "com.samebits.beacon.locator.action.ALARM_NOTIFICATION_SHOW";
    public static final String GET_CURRENT_LOCATION = "com.samebits.beacon.locator.action.GET_CURRENT_LOCATION";
    public static final int FOREGROUND_NOTIFICATION_ID = 11125;

    private Constants() {
    }


}
