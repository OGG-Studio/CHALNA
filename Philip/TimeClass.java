package com.example.philip.testalarm;


import android.icu.util.Calendar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeClass {
    public static final int YEAR = 0;
    public static final int MONTH = 1;
    public static final int DAY = 2;

    public static final int HOUR = 3;
    public static final int MINUTE = 4;
    public static final int SECOND = 5;


    public static long oneDay = 24*60*60*1000;
    public static String[] getTime(){
        SimpleDateFormat formatter = new SimpleDateFormat( "HH/mm/ss", Locale.KOREA );
        Date currentTime = new Date ();
        String dTime = formatter.format ( currentTime );

        return dTime.split("/");
    }
    public static String[] getDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd", Locale.KOREA);
        Date currentTime = new Date();
        String dTime = formatter.format(currentTime);

        return dTime.split("/");
    }
    public static String[] timeNumericToString(long time){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd/HH/mm/ss", Locale.KOREA);
        Date settingTime = new Date (time);
        String dTime = formatter.format(settingTime);
        return dTime.split("/");
    }
    public static String[] timeDateToNumeric(Date date){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd/HH/mm/ss", Locale.KOREA);
        String dTime = formatter.format(date);
        return dTime.split("/");
    }
    public static int differenceDay(Date a, Date b){
        String[] currentTimeInfo = timeDateToNumeric(a);
        String[] targetTimeInfo = timeDateToNumeric(b);

        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.setTime(a);
        calendar.set(Integer.parseInt(currentTimeInfo[0]),Integer.parseInt(currentTimeInfo[1])-1,Integer.parseInt(currentTimeInfo[2]), 0,0,0);
        long aTime = calendar.getTime().getTime();

        calendar.setTime(b);
        calendar.set(Integer.parseInt(targetTimeInfo[0]),Integer.parseInt(targetTimeInfo[1])-1,Integer.parseInt(targetTimeInfo[2]), 0,0,0);
        long bTime = calendar.getTime().getTime();

        return (int)(Math.abs(aTime-bTime)/oneDay);
    }
    public static String dateSummary(long targetTime){
        Date currentTime = new Date();
        Date target = new Date(targetTime);
        int difTime = differenceDay(currentTime,target);
        if(difTime==0){
            String[] currentTimeInfo = timeNumericToString(targetTime);
            return ampm(targetTime)+" "+currentTimeInfo[HOUR]+":"+currentTimeInfo[MINUTE];
        }else if(difTime==1){
            return "어제";
        }else{
            String[] currentTimeInfo = timeNumericToString(targetTime);
            return currentTimeInfo[YEAR]+"-"+currentTimeInfo[MONTH]+"-"+currentTimeInfo[DAY];
        }
    }
    public static String ampm(long time){
        Date date = new Date(time);
        String[] current = timeDateToNumeric(date);
        if(Integer.parseInt(current[HOUR])<12)
            return "오전";
        else
            return "오후";
    }
}