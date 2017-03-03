package br.com.agendee.utils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

/**
 * Created by wagner on 21/01/16.
 */
public class GcmUtils {

    public static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String SENDER_ID = "618168161291";


    public static boolean checkPlayServices(final Activity context){
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);

        if(resultCode != ConnectionResult.SUCCESS){
            if(GooglePlayServicesUtil.isUserRecoverableError(resultCode)){
                GooglePlayServicesUtil.getErrorDialog(resultCode, context, PLAY_SERVICES_RESOLUTION_REQUEST);
            }
            else{
                Toast.makeText(context, "PlayServices sem suporte", Toast.LENGTH_SHORT).show();
                context.finish();
            }
            return(false);
        }
        return(true);
    }


    public static String registerIdDevice(final Context context){
        String msg = "";
        GoogleCloudMessaging gcm;
        String regId = "";
        try{

            gcm = GoogleCloudMessaging.getInstance(context);
            regId = gcm.register(SENDER_ID);
            msg = "Register Id: "+regId;
            Log.i("GcmUtils", regId);
            AndroidSystemUtil.storeRegistrationId(context, regId);
        }
        catch(IOException e){
            Log.i("GcmUtils", e.getMessage());
        }

        return regId;
    }

}
