

package com.samebits.beacon.mubser;

import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.samebits.beacon.mubser.injection.component.ApplicationComponent;
import com.samebits.beacon.mubser.injection.component.DaggerApplicationComponent;
import com.samebits.beacon.mubser.injection.module.ApplicationModule;
import com.samebits.beacon.mubser.receiver.BeaconAlertReceiver;
import com.samebits.beacon.mubser.receiver.LocationReceiver;
import com.samebits.beacon.mubser.ui.activity.MainNavigationActivity;
import com.samebits.beacon.mubser.util.BackgroundSwitchWatcher;
import com.samebits.beacon.mubser.util.Constants;
import com.samebits.beacon.mubser.util.NotificationBuilder;
import com.samebits.beacon.mubser.util.PreferencesUtil;

import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;

public class BeaconLocatorApp extends Application  {

    ApplicationComponent applicationComponent;
    BackgroundSwitchWatcher mBackgroundSwitchWatcher;
    private BeaconManager mBeaconManager;
    BeaconAlertReceiver mAlertReceiver;
    LocationReceiver mLocationReceiver;

    public static BeaconLocatorApp from(@NonNull Context context) {
        return (BeaconLocatorApp) context.getApplicationContext();
    }

    public ApplicationComponent getComponent() {
        return applicationComponent;
    }

    void registerReceivers() {

        mLocationReceiver = new LocationReceiver();
        mAlertReceiver = new BeaconAlertReceiver();

        LocalBroadcastManager.getInstance(this).registerReceiver(
                mLocationReceiver, new IntentFilter( Constants.GET_CURRENT_LOCATION));
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mAlertReceiver, new IntentFilter( Constants.ALARM_NOTIFICATION_SHOW));
    }

    void unregisterReceivers() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver( mLocationReceiver );
        LocalBroadcastManager.getInstance(this).unregisterReceiver( mAlertReceiver );
    }


    @Override
    public void onCreate() {
        super.onCreate();

        applicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();

        mBeaconManager = BeaconLocatorApp.from(this).getComponent().beaconManager();

        registerReceivers();

        initBeaconManager();

    }

    @Override
    public void onTerminate() {
        unregisterReceivers();
        super.onTerminate();
    }

    private void initBeaconManager() {

        if (PreferencesUtil.isEddystoneLayoutUID(this)) {
            mBeaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_UID_LAYOUT));
        }
        if (PreferencesUtil.isEddystoneLayoutURL(this)) {
            mBeaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_URL_LAYOUT));
        }
        if (PreferencesUtil.isEddystoneLayoutTLM(this)) {
            mBeaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_TLM_LAYOUT));
        }
        mBeaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.ALTBEACON_LAYOUT));

        mBeaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
        mBeaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        mBeaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:0-3=4c000215,i:4-19,i:20-21,i:22-23,p:24-24"));

        mBeaconManager.setAndroidLScanningDisabled(true);
        mBeaconManager.setBackgroundScanPeriod(15000L);
        mBeaconManager.setForegroundBetweenScanPeriod(0L);      // default is 0L
        mBeaconManager.setForegroundScanPeriod(1100L);          // Default is 1100L

        mBackgroundSwitchWatcher = new BackgroundSwitchWatcher(this);

        if (PreferencesUtil.isForegroundScan(this) && PreferencesUtil.isBackgroundScan(this)) {

            startScanAsForegroundService();

        } else {

            stopScanAsForegroundService();
        }

        enableBackgroundScan(true);

    }

    private void setScanSettings() {

        if (mBeaconManager == null) return;

        mBeaconManager.setBackgroundBetweenScanPeriod(PreferencesUtil.getBackgroundScanInterval(this));

        try {
            if (mBeaconManager.isAnyConsumerBound()) {
                mBeaconManager.updateScanPeriods();
            }
        } catch (RemoteException e) {
            Log.e(Constants.TAG, "Update scan periods error", e);
        }
    }


    /**
     * Here we switch between scan types when app in the foreground or background
     * @param enable yes or no
     */
    public void enableBackgroundScan(boolean enable) {

        if (mBeaconManager == null) return;

        setScanSettings();

        boolean backgroundScanEnabled = PreferencesUtil.isBackgroundScan(this);
        if (enable && backgroundScanEnabled) {
            Log.d(Constants.TAG, "Enable background scan");
        } else {
            Log.d(Constants.TAG, "Disable background scan");
            mBeaconManager.setBackgroundMode(false);
        }
    }

    public void startScanAsForegroundService() {

        Log.d(Constants.TAG, "Init: Enable as foreground service scan");

        if (mBeaconManager == null || mBeaconManager.isAnyConsumerBound()) {
            Log.w(Constants.TAG, "Cannot start scan in foreground mode, beacon manager is bound");
            return;
        }

        NotificationBuilder notificationBuilder = new NotificationBuilder(this);

        PendingIntent notificationIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainNavigationActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

        notificationBuilder.createNotificationService(getString(R.string.text_scan_foreground_service), notificationIntent);

        mBeaconManager.enableForegroundServiceScanning(notificationBuilder.getBuilder().build(), Constants.FOREGROUND_NOTIFICATION_ID);

    }

    public void stopScanAsForegroundService() {

        Log.d(Constants.TAG, "Init: Disable as foreground service scan");

        if (mBeaconManager == null || mBeaconManager.isAnyConsumerBound()) {
            Log.w(Constants.TAG, "Cannot stop scan in foreground mode, beacon manager is bound");
            return;
        }

        mBeaconManager.disableForegroundServiceScanning();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        enableBackgroundScan(false);
    }
}