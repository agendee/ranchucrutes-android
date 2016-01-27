package br.com.wjaa.ranchucrutes.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.inject.Inject;

import br.com.wjaa.ranchucrutes.R;
import br.com.wjaa.ranchucrutes.buffer.RanchucrutesBuffer;
import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;

/**
 * Created by wagner on 31/07/15.
 */
@ContentView(R.layout.splash)
public class SplashActivity extends RoboActivity implements Runnable {


    @Inject
    private RanchucrutesBuffer buffer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        buffer.initializer();
        Handler handler = new Handler();
        handler.postDelayed(this, 2001);
    }

    public void run(){
        startActivity(new Intent(this, MainActivity.class));
        finish();

        //intervalo rapido depois de executar o pos.
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
               buffer.posInitializer(SplashActivity.this);
            }
        }, 2000);

    }
}
