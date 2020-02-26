package com.telefon.ufanet.MVP.VOIP;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import android.telephony.TelephonyManager;

import java.lang.reflect.Method;

import static android.content.Context.NOTIFICATION_SERVICE;

public class IncomingCallReceiver extends BroadcastReceiver {
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onReceive(Context context, Intent intent) {

        ITelephony telephonyService;
        try {
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            String number = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);

            if(state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_RINGING)){

                TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                try {
                    Method m = tm.getClass().getDeclaredMethod("getITelephony");

                    m.setAccessible(true);
                    telephonyService = (ITelephony) m.invoke(tm);

                    if ((number != null)) {
                        telephonyService.endCall();

                        Intent ResultIntent = new Intent(Intent.ACTION_DIAL);
                        ResultIntent.setData(Uri.parse("tel:"+ number));
                        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, ResultIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT);

                        NotificationCompat.Builder builder =
                                (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                                        .setSmallIcon(android.R.drawable.stat_notify_missed_call)
                                        .setContentTitle("Пропущенный вызов")
                                        .setContentText(number)
                                        .setContentIntent(resultPendingIntent);

                        Notification notification = builder.build();

                        NotificationManager notificationManager =
                                (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
                        notificationManager.notify(1, notification);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }



            }
            if(state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_OFFHOOK)){

            }
            if(state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_IDLE)){

            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
