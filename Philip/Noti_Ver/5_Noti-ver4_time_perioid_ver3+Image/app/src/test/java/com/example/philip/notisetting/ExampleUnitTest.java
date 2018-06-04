package com.example.philip.notisetting;

import android.icu.util.Calendar;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        //assertEquals(4, 2 + 2);
        Date d = new Date();                // Date 객체 생성

        long currentTime = d.getTime();     // long type으로 현재 시간정보 받음
        currentTime += TimeClass.oneDay;    // long type으로 하루를 더해줌

        d = new Date(currentTime);          // d의 시간 업데이트 해줌_ long type형태로 업데이트! (하루 더해준거)
        System.out.println(d.toString());   // 출 력 ! 그냥 d를 to String으로 출력해주면 나옴.



        String[] arr=TimeClass.timeNumericToString(currentTime);
        System.out.println(arr[TimeClass.YEAR]);
        System.out.println(arr[TimeClass.DAY]);

        /*
        // 시간 설정하기
        int hour = 16;
        int minute = 30;
//        Calendar c = Calendar.getInstance();
        Date date = new Date();
//        String[] currentArray = TimeClass.timeNumericToString(date.getTime());
//        c.set(Integer.parseInt(currentArray[0]), Integer.parseInt(currentArray[1])-1, Integer.parseInt(currentArray[2]), hour, minute, 0);
        System.out.println(date);
        System.out.println(date.getTime());

        // 바꾸는 방법
        long currentTime2 = date.getTime() + TimeClass.oneDay*3;
        Date a = new Date(currentTime2);
        System.out.println(a);
        */
    }
}