/*
 *
 *  Copyright (c) 2016 SameBits UG. All rights reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.samebits.beacon.mubser.receiver;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.samebits.beacon.mubser.R;
import com.samebits.beacon.mubser.util.Constants;
import com.samebits.beacon.mubser.util.NotificationBuilder;


public class LocationReceiver extends BroadcastReceiver {

    private static final Criteria criteria = new Criteria();
    private static final long MAX_AGE_TIME = 10000;
    private static final int RETRY_COUNT_MAX = 2;
    private int retryCount = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        retryCount = intent.getIntExtra("RETRY_COUNT", 0);
        Location bestLocation = null;
        final LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        for (final String provider : locationManager.getAllProviders()) {
            if (provider != null) {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    Log.w(Constants.TAG, "No permissions to use GPS ");

                    return;
                }
                final Location location = locationManager.getLastKnownLocation(provider);
                final long now = System.currentTimeMillis();
                if (location != null
                        && (bestLocation == null || location.getTime() > bestLocation.getTime())
                        && location.getTime() > now - MAX_AGE_TIME) {
                    bestLocation = location;
                }
            }
        }

        if (bestLocation != null) {
            final String position = bestLocation.getLatitude() + "," + bestLocation.getLongitude();
            final Uri uri = Uri.parse("geo:" + position + "?z=16&q=" + position);
            final Intent mapIntent = new Intent(Intent.ACTION_VIEW, uri);

            PendingIntent notificationIntent = PendingIntent.getActivity(context, 0, mapIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationBuilder notificationBuilder = new NotificationBuilder(context);
            notificationBuilder.createNotification(context.getString(R.string.action_alarm_text_title), null,true, notificationIntent);
            notificationBuilder.setMessage(context.getString(R.string.notification_display_last_position));
            notificationBuilder.show(1);

        } else {
            if (retryCount < RETRY_COUNT_MAX) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                }
                retryCount++;
                intent.putExtra("RETRY_COUNT", retryCount);
                final PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    Log.w(Constants.TAG, "No permissions to use GPS ");

                    return;
                }
                locationManager.requestSingleUpdate(criteria, pendingIntent);
            }
        }
    }
}
