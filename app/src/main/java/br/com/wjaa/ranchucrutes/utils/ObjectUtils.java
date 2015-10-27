package br.com.wjaa.ranchucrutes.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import br.com.wjaa.ranchucrutes.vo.ErrorMessageVo;

/**
 * Created by wagner on 26/07/15.
 */
public class ObjectUtils {


    private static final Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();

    public static String toJson(Object o){
        return gson.toJson(o);
    }

    public static <T>T fromJson(String json, Class<T> clazz) {
        return gson.fromJson(json,clazz);
    }
}
