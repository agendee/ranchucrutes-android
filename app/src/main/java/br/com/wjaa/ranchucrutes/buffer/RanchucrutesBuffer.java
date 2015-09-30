package br.com.wjaa.ranchucrutes.buffer;

import android.util.Log;

import com.google.inject.Inject;

import br.com.wjaa.ranchucrutes.service.LoginService;
import br.com.wjaa.ranchucrutes.service.RanchucrutesService;
import br.com.wjaa.ranchucrutes.vo.ConvenioVo;
import br.com.wjaa.ranchucrutes.vo.EspecialidadeVo;

/**
 * Created by wagner on 31/07/15.
 */

public class RanchucrutesBuffer {

    private static String TAG = RanchucrutesBuffer.class.getSimpleName();

    private static EspecialidadeVo[] especialidades;
    private static ConvenioVo[] convenios;

    @Inject
    private RanchucrutesService ranchucrutesService;

    @Inject
    private LoginService loginService;

    public static EspecialidadeVo[] getEspecialidades(){
        return especialidades;
    }

    public static ConvenioVo[] getConvenios(){
        return convenios;
    }

    public void initializer(){
        new FindEspecialidade().start();
        new FindConvenio().start();
    }


    public void posInitializer(){
        new AutoLogin().start();
    }


    class FindEspecialidade extends Thread{
        @Override
        public void run() {
            try {
                especialidades = ranchucrutesService.getEspecialidades();
            } catch (Exception ex) {
                Log.e(TAG,ex.getMessage(),ex);
            }
        }
    }

    class FindConvenio extends Thread{
        @Override
        public void run() {
            try {
                convenios = ranchucrutesService.getConvenios();
            } catch (Exception ex) {
                Log.e(TAG,ex.getMessage(),ex);
            }
        }
    }

    class AutoLogin extends Thread{
        @Override
        public void run() {
            try {
                loginService.authLocal();
            } catch (Exception ex) {
                Log.e(TAG,ex.getMessage(),ex);
            }
        }
    }
}
