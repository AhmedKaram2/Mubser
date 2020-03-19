

package com.samebits.beacon.mubser.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;

public final class PreferencesUtil {

    private PreferencesUtil() {
    }

    public static SharedPreferences getSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static String getDefaultRegionName(Context context) {
        return getSharedPreferences(context).getString("scan_default_region_text", Constants.DEFAULT_PROJECT_NAME);
    }

    public static int getScanBeaconSort(Context context) {
        return Integer.parseInt(getSharedPreferences(context).getString("scan_sorting_order_list", Integer.toString(Constants.SORT_DISTANCE_NEAREST_FIRST)));
    }

    public static void setScanBeaconSort(Context context, int sort) {
        getSharedPreferences(context).edit().putString("scan_sorting_order_list", Integer.toString(sort)).apply();
    }

    public static int getManualScanTimeout(Context context) {
        return Integer.parseInt(getSharedPreferences(context).getString("scan_manual_timeout_list", "30000"));
    }

    public static void clear(Context context) {
        getSharedPreferences(context).edit().clear().apply();
    }

    public static boolean isBackgroundScan(Context context) {
        return getSharedPreferences(context).getBoolean("scan_background_switch", true);
    }

    public static boolean isForegroundScan(Context context) {
        if  (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return getSharedPreferences(context).getBoolean("scan_foreground_switch", true);
        }
        return false;
    }

    public static int getBackgroundScanInterval(Context context) {
        return Integer.parseInt(getSharedPreferences(context).getString("scan_background_timeout_list", "120000"));
    }

    public static boolean isEddystoneLayoutUID(Context context) {
        return getSharedPreferences(context).getBoolean("scan_parser_layout_eddystone_uid", true);
    }

    public static boolean isEddystoneLayoutURL(Context context) {
        return getSharedPreferences(context).getBoolean("scan_parser_layout_eddystone_url", true);
    }

    public static boolean isEddystoneLayoutTLM(Context context) {
        return getSharedPreferences(context).getBoolean("scan_parser_layout_eddystone_tlm", false);
    }
}