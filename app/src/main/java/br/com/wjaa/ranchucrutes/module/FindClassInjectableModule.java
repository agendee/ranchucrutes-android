package br.com.wjaa.ranchucrutes.module;

import android.content.Context;

import com.google.inject.Binder;
import com.google.inject.Module;

import br.com.wjaa.ranchucrutes.service.MedicoService;
import br.com.wjaa.ranchucrutes.service.MedicoServiceImpl;
import br.com.wjaa.ranchucrutes.service.RanchucrutesService;
import br.com.wjaa.ranchucrutes.service.RanchucrutesServiceImpl;

/**
 * Created by wagner on 25/07/15.
 */
public class FindClassInjectableModule implements Module {

    private Context context;

    public FindClassInjectableModule(Context context){
        this.context = context;
    }

    @Override
    public void configure(Binder binder) {
        binder.bind(MedicoService.class).to(MedicoServiceImpl.class);
        binder.bind(RanchucrutesService.class).to(RanchucrutesServiceImpl.class);

        //binder.bind(DataService.class).toInstance(new DataServiceImpl(this.context));
    }

}