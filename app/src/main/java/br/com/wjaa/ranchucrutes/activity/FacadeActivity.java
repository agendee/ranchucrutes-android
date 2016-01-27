package br.com.wjaa.ranchucrutes.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.inject.Inject;

import br.com.wjaa.ranchucrutes.R;
import br.com.wjaa.ranchucrutes.buffer.RanchucrutesBuffer;
import br.com.wjaa.ranchucrutes.service.RanchucrutesConstants;
import br.com.wjaa.ranchucrutes.utils.AndroidUtils;
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
        buffer.initializer();

        //intervalo rapido depois de executar o pos.
        Handler handAutoLogin = new Handler();
        handAutoLogin.postDelayed(new Runnable() {
            @Override
            public void run() {
                buffer.posInitializer(FacadeActivity.this);
            }
        }, 300);

        Handler handler = new Handler();
        handler.postDelayed(this, 1500);
    }

    public void run(){
        Bundle bundle = new Bundle();
        bundle.putInt(RanchucrutesConstants.PARAM_OPEN_FRAGMENT_MAIN_ACTIVITY, 2);
        AndroidUtils.openActivity(this, MainActivity.class, bundle);
        //chamando novamente porque precisa atualizar o menu com o usuario logado.
        buffer.posInitializer(FacadeActivity.this);

        finish();
    }
}
