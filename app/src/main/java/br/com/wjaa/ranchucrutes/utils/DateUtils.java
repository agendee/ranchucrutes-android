package br.com.wjaa.ranchucrutes.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 *
 * @author Wagner Araujo
 *
 */
public class DateUtils {

    private static final SimpleDateFormat sdfddmmyyyy =
            new SimpleDateFormat("dd/mm/yyyy", Locale.getDefault());

    private static final SimpleDateFormat HHmm =
            new SimpleDateFormat("HH:mm", Locale.getDefault());

    public static String formatddmmyyyy(Date date){
        return sdfddmmyyyy.format(date);
    }

    public static String formatHHmm(Date date){
        return HHmm.format(date);
    }

}