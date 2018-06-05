package com.example.philip.chalna;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

public class AlarmBootBroadcastReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(intent.ACTION_BOOT_COMPLETED)) {
            DBSQLiteModel myDB = DBSQLiteModel.getInstance(context);

            List<AlarmData> alarmList = myDB.selectAllFromALARM();

            for(AlarmData alarmData : alarmList){
                PendingIntent pi = PendingIntent.getBroadcast(context.getApplicationContext(), alarmData.project_id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
                alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, alarmData.alram_next_time, alarmData.alarm_cycle*TimeClass.oneDay, pi);
            }
        }
    }
}
