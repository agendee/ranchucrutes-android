package br.com.wjaa.ranchucrutes.gcm;

import br.com.wjaa.ranchucrutes.utils.NotificationCustomUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;


public class GcmIntentService extends IntentService {
	public static final String TAG = "Script";
	
	public GcmIntentService(){
		super("GcmIntentService");
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(GcmIntentService.this);
		String title, author, message, messageType = gcm.getMessageType(intent);
		
		
		if(extras != null){
			if(GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)){
				Log.i(TAG, "Error: "+extras.toString());
			}
			else if(GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)){
				Log.i(TAG, "Deleted: "+extras.toString());
			}
			else if(GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)){
				title = extras.getString("title");
				author = extras.getString("author");
				message = extras.getString("message");
				
				NotificationCustomUtil.sendNotification(GcmIntentService.this,GcmIntentService.class,
						title, author, message);
			}
		}
		
		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}
	
}
