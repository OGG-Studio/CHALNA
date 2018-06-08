package com.example.philip.chalna.AlarmReceiver;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.philip.chalna.Database.AlarmData;
import com.example.philip.chalna.Database.DBSQLiteModel;
import com.example.philip.chalna.Utils.DescriptionManager;
import com.example.philip.chalna.Project.MainActivity;
import com.example.philip.chalna.Database.ProjectData;
import com.example.philip.chalna.Project.ProjectPreviewController;
import com.example.philip.chalna.R;
import com.example.philip.chalna.Utils.TimeClass;

import java.util.Date;

public class AlarmBroadcastController extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) { //알람시간이 되었을 때 onReceive를 호출함
        //NotificationManager 안드로이드 상태바에 메시지를 던지기 위한 서비스를 불러오고
        Log.d("ALARM", "START ALARM : ");
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        DBSQLiteModel myDB = DBSQLiteModel.getInstance(context);
        int project_id = intent.getIntExtra(DBSQLiteModel.PROJECT_ID,-1);

        ProjectData currentPorject = myDB.getDataByIdFromPROJECT(Integer.toString(project_id));
        AlarmData alarmData = myDB.getDataByNameFromALARM(Integer.toString(project_id));

        Log.d("ALARM", "START ALARM : " + project_id);

        if(currentPorject==null || alarmData==null){
            AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            PendingIntent pd = PendingIntent.getActivities(context, project_id, new Intent[]{new Intent(context, MainActivity.class)}, PendingIntent.FLAG_NO_CREATE);
            if(pd!=null)
                alarmMgr.cancel(pd);
            //아래 알람 울리는거 repeat으로 바꿔야해!
            return;
        }
        Log.d("ALARM", "START ALARM : " + project_id + " " + currentPorject.name);

        Notification notification = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            Intent targetIntent = new Intent(context, ProjectPreviewController.class);
            Log.d("ALARM",  "BROADCAST RECEIVER ALARM = " + currentPorject.name);
            targetIntent.putExtra("PROJECT_NAME", currentPorject.name);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, targetIntent,  PendingIntent.FLAG_UPDATE_CURRENT);

            notification = new Notification.Builder(context)
                    .setSmallIcon(R.drawable.icon)
                    .setTicker("HETT")
                    .setWhen(System.currentTimeMillis())
                    .setNumber(1)
                    .setContentTitle("CHALNA: " + currentPorject.name)
                    .setContentText("오늘의 '찰나'를 기록해보세요!")
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .build();
            notification.defaults |= Notification.DEFAULT_SOUND;    // 소리
            notification.defaults |= Notification.DEFAULT_VIBRATE;  // 진동
            notificationManager.notify(project_id, notification);

            alarmData.alram_next_time = alarmData.alram_next_time + alarmData.alarm_cycle * TimeClass.oneDay;
            currentPorject.description = DescriptionManager.getAlarmDescription();
            Date currentTime = new Date();
            currentPorject.modificationDate = currentTime.getTime();

            myDB.syncProjectData(currentPorject);
            myDB.syncAlarmData(alarmData);
        }else{
            return;
        }
    }
}
