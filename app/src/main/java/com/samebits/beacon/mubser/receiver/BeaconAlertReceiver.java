
package com.samebits.beacon.mubser.receiver;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.samebits.beacon.mubser.R;
import com.samebits.beacon.mubser.model.NotificationAction;
import com.samebits.beacon.mubser.ui.activity.MainNavigationActivity;
import com.samebits.beacon.mubser.util.Constants;
import com.samebits.beacon.mubser.util.NotificationBuilder;


public class BeaconAlertReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equalsIgnoreCase(Constants.ALARM_NOTIFICATION_SHOW)) {
            NotificationAction notificationAction = intent.getParcelableExtra("NOTIFICATION");
            createNotification(context, context.getString(R.string.action_alarm_text_title),
                    notificationAction.getMessage(), notificationAction.getMessage(),
                    notificationAction.getRingtone(), notificationAction.isVibrate());

        }
    }

    private void createNotification(Context context, String title, String msgText
            , String msgAlert, String ringtone, boolean isVibrate) {

        PendingIntent notificationIntent = PendingIntent.getActivity(context, 0, new Intent(context, MainNavigationActivity.class), 0);
        NotificationBuilder notificationBuilder = new NotificationBuilder(context);
        notificationBuilder.createNotification(title, ringtone, isVibrate, notificationIntent);

        notificationBuilder.setMessage(msgText);
        notificationBuilder.setTicker(msgAlert);


        notificationBuilder.show(1);

    }
}





