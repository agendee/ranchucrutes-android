package br.com.wjaa.ranchucrutes.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


/**
 *
 * @author Wagner Araujo
 *
 */
public class DateUtils {

    private static final SimpleDateFormat sdfddMMyyyy =
            new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    private static final SimpleDateFormat HHmm =
            new SimpleDateFormat("HH:mm", Locale.getDefault());


    private static final SimpleDateFormat sdfddMMyyyyHHmm =
            new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

    public static String formatddMMyyyy(Date date){
        return sdfddMMyyyy.format(date);
    }

    public static String formatHHmm(Date date){
        return HHmm.format(date);
    }

    public static Date parseddMMyyyy(String dateStr) {
        try {
            return sdfddMMyyyy.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String formatddMMyyyyHHmm(Date date) {
        return sdfddMMyyyyHHmm.format(date);
    }

    public static int diffInDays(Date date1, Date date2) {
        long time1 = date1.getTime();
        long time2 = date2.getTime();
        Long result = (time2-time1)/(1000*60*60*24);
        return result.intValue();

    }

}