package br.com.agendee.scheduled;

import android.content.Intent;

import roboguice.service.RoboIntentService;

/**
 * Created by wagner on 19/11/15.
 */
public class NotificacoesService extends RoboIntentService{


    public NotificacoesService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }
}
