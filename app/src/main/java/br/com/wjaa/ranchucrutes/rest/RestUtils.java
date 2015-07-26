package br.com.wjaa.ranchucrutes.rest;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import br.com.wjaa.ranchucrutes.exception.RestException;
import br.com.wjaa.ranchucrutes.exception.RestRequestUnstable;
import br.com.wjaa.ranchucrutes.exception.RestResponseUnsatisfiedException;
import br.com.wjaa.ranchucrutes.vo.ErrorMessageVo;

/**
 * Created by wagner on 25/07/15.
 */
public class RestUtils {

    private static final String TAG = RestUtils.class.getSimpleName();
    private static final Gson gson = new GsonBuilder().create();


    public static <T>T getJsonWithParamPath(Class<T> clazzReturn, String targetUrl, String ... params) throws
            RestResponseUnsatisfiedException, RestException, RestRequestUnstable {


        try {
            URL url = new URL("http://" + targetUrl + "/" + RestUtils.createParamsPath(params));

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");


            Log.d(TAG, "Response Code: " + conn.getResponseCode());
            InputStream in = new BufferedInputStream(conn.getInputStream());
            String response = org.apache.commons.io.IOUtils.toString(in, "UTF-8");
            System.out.println(response);

            int statusCode = conn.getResponseCode();

            if ( statusCode >= 400 && statusCode < 500){
                throw new RestRequestUnstable("Servico está fora do ar.");
            }

            if (statusCode >= 500 && statusCode < 600){
                throw new RestException(gson.fromJson(response, ErrorMessageVo.class));
            }

            Log.d(TAG,"m=getJsonWithParamPath Response: " + response);

            return gson.fromJson(response, clazzReturn);

        }catch (JsonParseException e) {
            throw new RestResponseUnsatisfiedException(e.getMessage(), e);
        } catch (IOException e) {
            throw new RestRequestUnstable(e.getMessage(), e);
        }  catch (Exception e) {
            throw new RestException(new ErrorMessageVo(500, e.getMessage()));
        }
    }

    public static <T>T postJson(Class<T> clazzReturn, String targetUrl, String uri, String json) throws
            RestResponseUnsatisfiedException, RestException, RestRequestUnstable {


        try {

            URL url = new URL("http://" + targetUrl + "/" + uri);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("application/json","UTF-8");
            conn.setDoOutput(true);
            OutputStream os = conn.getOutputStream();
            os.write(json.getBytes());
            os.flush();
            os.close();

            Log.d(TAG,"Response Code: " + conn.getResponseCode());
            InputStream in = new BufferedInputStream(conn.getInputStream());
            String response = org.apache.commons.io.IOUtils.toString(in, "UTF-8");
            System.out.println(response);

            int statusCode = conn.getResponseCode();

            if ( statusCode >= 400 && statusCode < 500){
                throw new RestRequestUnstable("Servico está fora do ar.");
            }

            if (statusCode >= 500 && statusCode < 600){
                throw new RestException(gson.fromJson(response, ErrorMessageVo.class));
            }

            Log.d(TAG,"m=getJsonWithParamPath Response: " + conn.getResponseCode());

            return gson.fromJson(response, clazzReturn);

        }catch (JsonParseException e) {
            throw new RestResponseUnsatisfiedException(e.getMessage(), e);
        } catch (IOException  e) {
            throw new RestRequestUnstable(e.getMessage(), e);
        }  catch (Exception e) {
            throw new RestException(new ErrorMessageVo(500,e.getMessage()));
        }
    }

    private static String createParamsPath(String[] params) {
        String result = "";
        for(String p : params){
            result += "/" + p;
        }
        return result;

    }

}
