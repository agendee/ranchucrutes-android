package br.com.agendee.utils;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by wagner on 20/09/15.
 */
public class NumberUtils {

    public static final Integer getInteger(String value) {
        if ( value == null )
            return null;
        return Integer.valueOf(value);
    }

    public static final long longValue(Object value) {
        if (value == null)
            return 0L;
        ;
        return Long.valueOf(value.toString());
    }


    /**
     * Verifica se um numero e positivo.
     * @param v Valor
     * @return true caso positivo e false caso contrario
     */
    public static boolean isPositive(Long v){
        return v != null && v > 0;
    }


    /**
     * Verifica se um numero e positivo.
     * @param v Valor
     * @return true caso positivo e false caso contrario
     */
    public static boolean isPositive(Integer v){
        return v != null && isPositive(v.longValue());
    }


    /**
     * Verifica se um numero e positivo.
     * @param v Valor
     * @return true caso positivo e false caso contrario
     */
    public static boolean isPositive(Short s){
        return s != null && isPositive(s.longValue());
    }

    public static String formatMoney(Double d){
    Locale ptBr = new Locale("pt", "BR");
    NumberFormat nf = NumberFormat.getCurrencyInstance(ptBr);
    String formatado = nf.format (d);
    return formatado;
    }
}
