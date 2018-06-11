package com.studioOgg.philip.chalna.AlarmReceiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.studioOgg.philip.chalna.Database.AlarmData;
import com.studioOgg.philip.chalna.Database.DBSQLiteModel;
import com.studioOgg.philip.chalna.Utils.TimeClass;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AlarmBootBroadcastReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(intent.ACTION_BOOT_COMPLETED)) {
            DBSQLiteModel myDB = DBSQLiteModel.getInstance(context);

            List<AlarmData> alarmList = myDB.selectAllFromALARM();

            for(AlarmData alarmData : alarmList){
                Log.d("BOOT", "BOOT ON ALARM TIME : " + new Date(alarmData.alram_next_time).toString() + " " + alarmData.project_id);
                Intent targetIntent = new Intent(context, AlarmBroadcastController.class);
                targetIntent.putExtra(DBSQLiteModel.PROJECT_ID, alarmData.project_id);

                // Calendar의 알람주기를 설정
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(alarmData.alram_next_time);
                String[] date_info = TimeClass.timeDateToNumeric(calendar.getTime());
                calendar.set(Integer.parseInt(date_info[0]), Integer.parseInt(date_info[1])-1, Integer.parseInt(date_info[2]), Integer.parseInt(date_info[3]), Integer.parseInt(date_info[4]), 1);

                PendingIntent pi = PendingIntent.getBroadcast(context, alarmData.project_id, targetIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
                assert alarmMgr != null;
                alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, alarmData.alram_next_time, alarmData.alarm_cycle*TimeClass.oneDay, pi);
            }
        }
    }
}
