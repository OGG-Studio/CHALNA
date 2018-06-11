package com.studioOgg.philip.testalarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ToggleButton;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private ToggleButton _toggleSun, _toggleMon, _toggleTue, _toggleWed, _toggleThu, _toggleFri, _toggleSat;
    private static final long A_WEEK = 1000 * 10;

    AlarmManager alarmMgr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(MainActivity.this, BroadcastD.class);

        _toggleSun = (ToggleButton) findViewById(R.id.toggle_sun);
        _toggleMon = (ToggleButton) findViewById(R.id.toggle_mon);
        _toggleTue = (ToggleButton) findViewById(R.id.toggle_tue);
        _toggleWed = (ToggleButton) findViewById(R.id.toggle_wed);
        _toggleThu = (ToggleButton) findViewById(R.id.toggle_thu);
        _toggleFri = (ToggleButton) findViewById(R.id.toggle_fri);
        _toggleSat = (ToggleButton) findViewById(R.id.toggle_sat);
        /*
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
        calendar.set(Calendar.HOUR_OF_DAY, 18);
        calendar.set(Calendar.MINUTE, 00);

        PendingIntent pi = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);
        */
        alarmMgr = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        //alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), A_WEEK, pi);
    }//end onCreate method


    public void onRegist(View v)
    {

        boolean[] week = { false, _toggleSun.isChecked(), _toggleMon.isChecked(), _toggleTue.isChecked(), _toggleWed.isChecked(),
                _toggleThu.isChecked(), _toggleFri.isChecked(), _toggleSat.isChecked() }; // sunday=1 이라서 0의 자리에는 아무 값이나 넣었음

        Intent intent = new Intent(MainActivity.this, BroadcastD.class);
        intent.putExtra("weekday", week);

        PendingIntent pIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.SECOND, cal.get(Calendar.SECOND) + 10); // 10초 뒤

        long tsetTime = 1000 * 10;
        long oneday = 24 * 60 * 60 * 1000;// 24시간

        // 10초 뒤에 시작해서 매일 같은 시간에 반복하기
        alarmMgr.cancel(pIntent);
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), tsetTime, pIntent);
    }

    public void onUnregist(View v)
    {

        Intent intent = new Intent(this, BroadcastD.class);
        PendingIntent pIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);

        alarmMgr.cancel(pIntent);
    }



}
