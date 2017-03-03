package br.com.agendee.utils;

/**
 * Created by wagner on 10/09/15.
 */
public class StringUtils {


    public static boolean isBlank(String str) {
        return str == null || "".equals(str.trim());
    }

    public static boolean isNotBlank(String str) {
        return str != null && !"".equals(str.trim());
    }
}
