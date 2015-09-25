package br.com.wjaa.ranchucrutes.module;

import android.content.Context;

import com.google.inject.AbstractModule;

import br.com.wjaa.ranchucrutes.buffer.RanchucrutesBuffer;
import br.com.wjaa.ranchucrutes.service.DataService;
import br.com.wjaa.ranchucrutes.service.DataServiceImpl;
import br.com.wjaa.ranchucrutes.service.FacebookService;
import br.com.wjaa.ranchucrutes.service.FacebookServiceImpl;
import br.com.wjaa.ranchucrutes.service.GPlusService;
import br.com.wjaa.ranchucrutes.service.GPlusServiceImpl;
import br.com.wjaa.ranchucrutes.service.LoginService;
import br.com.wjaa.ranchucrutes.service.LoginServiceImpl;
import br.com.wjaa.ranchucrutes.service.MedicoService;
import br.com.wjaa.ranchucrutes.service.MedicoServiceImpl;
import br.com.wjaa.ranchucrutes.service.RanchucrutesService;
import br.com.wjaa.ranchucrutes.service.RanchucrutesServiceImpl;


/**
 * Created by wagner on 25/07/15.
 */
public class FindClassInjectableModule extends AbstractModule {

    private Context context;

    public FindClassInjectableModule(Context context){
        this.context = context;
    }

    @Override
    public void configure() {
        superbind(MedicoService.class).to(MedicoServiceImpl.class);
        superbind(RanchucrutesService.class).to(RanchucrutesServiceImpl.class);
        superbind(LoginService.class).to(LoginServiceImpl.class);
        superbind(FacebookService.class).to(FacebookServiceImpl.class);
        superbind(GPlusService.class).to(GPlusServiceImpl.class);
        superbind(RanchucrutesBuffer.class);
        superbind(DataService.class).toInstance(new DataServiceImpl(this.context));
    }

}