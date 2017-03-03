package br.com.agendee.activity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.inject.Inject;

import br.com.agendee.buffer.RanchucrutesSession;
import br.com.agendee.R;
import br.com.agendee.buffer.RanchucrutesBuffer;
import br.com.agendee.service.RanchucrutesConstants;
import br.com.agendee.utils.AndroidUtils;
import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;

/**
 * Created by wagner on 31/07/15.
 */
@ContentView(R.layout.splash)
public class FacadeActivity extends RoboActivity implements Runnable {


    @Inject
    private RanchucrutesBuffer buffer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (RanchucrutesSession.isUsuarioLogado()) {
            Log.i("FacadeActivity", "ranchucrutes está rodando sim.....");
            Bundle bundle = new Bundle();
            bundle.putInt(RanchucrutesConstants.PARAM_OPEN_FRAGMENT_MAIN_ACTIVITY, R.id.navAppointment);
            AndroidUtils.openActivity(this, HomeActivity.class, bundle);
            finish();
            return;
        }else{
            Log.i("FacadeActivity", "ranchucrutes NAO ESTÁ rodando.....");
            runAutoLogin();
        }

    }

    private void runAutoLogin() {
        buffer.initializer();

        //intervalo rapido depois de executar o pos.
        Handler handAutoLogin = new Handler();
        handAutoLogin.postDelayed(new Runnable() {
            @Override
            public void run() {
                buffer.posInitializer(FacadeActivity.this);
            }
        }, 500);

        Handler handler = new Handler();
        handler.postDelayed(this, 3000);
    }

    public void run(){
        Bundle bundle = new Bundle();
        bundle.putInt(RanchucrutesConstants.PARAM_OPEN_FRAGMENT_MAIN_ACTIVITY, R.id.navAppointment);
        AndroidUtils.openActivity(this, HomeActivity.class, bundle);
        finish();
    }
}
