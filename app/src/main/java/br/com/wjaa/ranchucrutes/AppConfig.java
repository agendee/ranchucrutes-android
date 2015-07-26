package br.com.wjaa.ranchucrutes;

import android.app.Application;
import android.content.Intent;

import br.com.wjaa.ranchucrutes.module.FindClassInjectableModule;
import roboguice.RoboGuice;

/**
 * Created by wagner on 25/07/15.
 */
public class AppConfig extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        RoboGuice.getOrCreateBaseApplicationInjector(this, RoboGuice.DEFAULT_STAGE,
                RoboGuice.newDefaultRoboModule(this), new FindClassInjectableModule(this));

        //BufferBuilder bufferBuilder = RoboGuice.getBaseApplicationInjector(this).getInstance(BufferBuilder.class);
        //bufferBuilder.load();

        //Intent mServiceIntent = new Intent(this.getApplicationContext(), JokePullService.class);
        //mServiceIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        //this.getApplicationContext().startService(mServiceIntent);

    }



}