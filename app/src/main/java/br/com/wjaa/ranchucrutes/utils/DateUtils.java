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

    public static String formatddmmyyyy(Date date){
        return sdfddmmyyyy.format(date);
    }

}