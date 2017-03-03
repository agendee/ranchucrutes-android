package br.com.agendee.buffer;

import android.content.Context;
import android.util.Log;

import com.google.inject.Inject;

import br.com.agendee.service.RanchucrutesService;
import br.com.agendee.vo.ConvenioVo;
import br.com.agendee.service.LoginService;
import br.com.agendee.vo.EspecialidadeVo;
import br.com.agendee.vo.ProfissaoVo;

/**
 * Created by wagner on 31/07/15.
 */

public class RanchucrutesBuffer {

    private static String TAG = RanchucrutesBuffer.class.getSimpleName();

    private static EspecialidadeVo[] especialidades;
    private static ProfissaoVo [] profissoes;
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
        new FindProfissao().start();
        new FindEspecialidade().start();
        new FindConvenio().start();
    }

    public boolean empty(){
        return  especialidades == null || especialidades.length == 0 ||
                convenios == null || convenios.length == 0;
    }

    public void posInitializer(Context context){
        new AutoLogin(context).start();
    }


    class FindProfissao extends Thread{
        @Override
        public void run() {
            try {
                profissoes = ranchucrutesService.getProfissoes();
            } catch (Exception ex) {
                Log.e(TAG,ex.getMessage(),ex);
            }
        }
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
        private Context context;
        public AutoLogin(Context context) {
            this.context = context;
        }

        @Override
        public void run() {
            try {
                loginService.authLocal(this.context);
            } catch (Exception ex) {
                Log.e(TAG,ex.getMessage(),ex);
            }
        }
    }

    public static ProfissaoVo[] getProfissoes() {
        return profissoes;
    }

    public static void setProfissoes(ProfissaoVo[] profissoes) {
        RanchucrutesBuffer.profissoes = profissoes;
    }
}
