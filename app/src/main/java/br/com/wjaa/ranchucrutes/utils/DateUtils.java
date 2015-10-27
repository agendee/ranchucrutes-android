package br.com.wjaa.ranchucrutes.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
}