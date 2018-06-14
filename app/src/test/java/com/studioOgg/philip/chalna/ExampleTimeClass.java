package com.studioOgg.philip.chalna;

import com.studioOgg.philip.chalna.Project.ProjectCreateController;
import com.studioOgg.philip.chalna.Utils.TimeClass;

import org.junit.Test;

import java.sql.Time;
import java.util.Date;

import static org.junit.Assert.assertEquals;

public class ExampleTimeClass {
    @Test
    public void TimeClass_Check() {
        int cDay = 12;
        int cHour = 3;
        Date date = new Date();
        long currentTime = date.getTime();
        long expectedTime = TimeClass.getCurrentTime();
        assertEquals(currentTime, currentTime);

        String[] dateString = TimeClass.timeNumericToString(currentTime);
        assertEquals(Integer.parseInt(dateString[0]), 2018);
        assertEquals(Integer.parseInt(dateString[1]), 06);
        assertEquals(Integer.parseInt(dateString[2]), cDay);
        assertEquals(Integer.parseInt(dateString[3]), cHour);

        String[] onlyDate = TimeClass.getDate();
        assertEquals(onlyDate[0], "2018");
        assertEquals(onlyDate[1], "06");
        assertEquals(onlyDate[2], cDay+"");
        String[] onlyTime = TimeClass.getTime();
        assertEquals(onlyTime[0], "0"+cHour+"");

        TimeClass.dateSummary(date.getTime()-TimeClass.oneDay);
    }
}
