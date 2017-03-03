package br.com.agendee.module;

import android.content.Context;

import com.google.inject.AbstractModule;

import br.com.agendee.buffer.RanchucrutesBuffer;
import br.com.agendee.service.AgendamentoService;
import br.com.agendee.service.AgendamentoServiceImpl;
import br.com.agendee.service.DataService;
import br.com.agendee.service.DataServiceImpl;
import br.com.agendee.service.FacebookService;
import br.com.agendee.service.FacebookServiceImpl;
import br.com.agendee.service.GPlusService;
import br.com.agendee.service.GPlusServiceImpl;
import br.com.agendee.service.LoginService;
import br.com.agendee.service.LoginServiceImpl;
import br.com.agendee.service.ProfissionalService;
import br.com.agendee.service.ProfissionalServiceImpl;
import br.com.agendee.service.RanchucrutesService;
import br.com.agendee.service.RanchucrutesServiceImpl;


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
        superbind(ProfissionalService.class).to(ProfissionalServiceImpl.class);
        superbind(RanchucrutesService.class).to(RanchucrutesServiceImpl.class);
        superbind(LoginService.class).to(LoginServiceImpl.class);
        superbind(FacebookService.class).to(FacebookServiceImpl.class);
        superbind(GPlusService.class).to(GPlusServiceImpl.class);
        superbind(AgendamentoService.class).to(AgendamentoServiceImpl.class);
        superbind(RanchucrutesBuffer.class);
        superbind(DataService.class).toInstance(new DataServiceImpl(this.context));
    }

}