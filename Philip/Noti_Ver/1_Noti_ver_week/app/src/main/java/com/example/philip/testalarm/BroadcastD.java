package com.studioOgg.philip.testalarm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;

public class BroadcastD extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) { //알람시간이 되었을 때 onReceive를 호출함
        //NotificationManager 안드로이드 상태바에 메시지를 던지기 위한 서비스를 불러오고
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getActivities(context, 0, new Intent[]{new Intent(context, MainActivity.class)}, PendingIntent.FLAG_UPDATE_CURRENT);

        boolean[] week = intent.getBooleanArrayExtra("weekday");

        Calendar cal = Calendar.getInstance();

        int i=0;
        for(boolean b : week){
            Log.d("TEST_DEBUG", ""+i+" is " + Boolean.toString(b));
        }

        if (!week[cal.get(Calendar.DAY_OF_WEEK)])
            return;
            //Log.i("태그", "요일 : "+week[cal.get(Calendar.DAY_OF_WEEK)]);

        Notification notification = new Notification.Builder(context)
                .setSmallIcon(R.drawable.chalna)
                .setTicker("HETT")
                .setWhen(System.currentTimeMillis())
                .setNumber(1)
                .setContentTitle("CHALNA")
                .setContentText("오늘의 '찰나'를 기록하시겠어요!!!")
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();

        notification.defaults |= Notification.DEFAULT_SOUND;    // 소리
        notification.defaults |= Notification.DEFAULT_VIBRATE;  // 진동

        notificationManager.notify(1, notification);

    }
}
