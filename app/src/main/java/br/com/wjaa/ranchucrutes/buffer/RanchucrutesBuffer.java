package br.com.wjaa.ranchucrutes.buffer;

import android.util.Log;

import com.google.inject.Inject;

import br.com.wjaa.ranchucrutes.service.RanchucrutesService;
import br.com.wjaa.ranchucrutes.vo.EspecialidadeVo;

/**
 * Created by wagner on 31/07/15.
 */

public class RanchucrutesBuffer {

    private static String TAG = RanchucrutesBuffer.class.getSimpleName();
    private static EspecialidadeVo[] especialidades;

    @Inject
    private RanchucrutesService ranchucrutesService;

    public void initializer(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    especialidades = ranchucrutesService.getEspecialidades();
                } catch (Exception ex) {
                    Log.e(TAG,ex.getMessage(),ex);
                }
            }
        });
        thread.start();
    }

    public static EspecialidadeVo [] getEspecialidades(){
        return RanchucrutesBuffer.especialidades;
    }

}
