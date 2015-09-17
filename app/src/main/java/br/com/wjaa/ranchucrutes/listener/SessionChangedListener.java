package br.com.wjaa.ranchucrutes.listener;

import br.com.wjaa.ranchucrutes.vo.PacienteVo;

/**
 * Created by wagner on 17/09/15.
 */
public interface SessionChangedListener {

    void pacienteChange(PacienteVo paciente);

}
