package com.example.philip.chalna;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);

        long now = System.currentTimeMillis() + (60*60*24*1000);
        Date date = new Date(now);
        System.out.println(now);
        System.out.println(date.getTime());

        Calendar calendar = Calendar.getInstance();
        System.out.println(calendar.getTimeInMillis());
        System.out.println(calendar.getTime().toString());

//        SimpleDateFormat formatter = new SimpleDateFormat( "yyyy.MM.dd HH:mm:ss", Locale.KOREA );
        SimpleDateFormat formatter = new SimpleDateFormat( "HH:mm:ss--", Locale.KOREA );
        Date currentTime = new Date ();
        String dTime = formatter.format ( currentTime );
        System.out.println(currentTime.getTime() + " " + date.getTime());
        System.out.println ( dTime );

        long next = System.currentTimeMillis() - (60*60*3*1000);
        now = System.currentTimeMillis();

        System.out.println(TimeClass.dateSummary(next));
    }
}