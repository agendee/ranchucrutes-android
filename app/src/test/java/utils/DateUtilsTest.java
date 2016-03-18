package br.com.wjaa.ranchucrutes.utils;

import android.test.InstrumentationTestCase;

import java.util.Calendar;

/**
 * Created by wagner on 18/03/16.
 */
public class DateUtilsTest{

    public void testDiffInDays(){
        Calendar c1 = Calendar.getInstance();
        c1.set(2016,Calendar.MARCH,10);
        Calendar c2 = Calendar.getInstance();
        c1.set(2016, Calendar.MARCH, 12);
        System.out.println(String.valueOf(DateUtils.diffInDays(c1.getTime(), c2.getTime())));
    }



}