package br.com.wjaa.ranchucrutes.listener;

import br.com.wjaa.ranchucrutes.entity.PacienteEntity;

/**
 * Created by wagner on 17/09/15.
 */
public interface SessionChangedListener {

    void usuarioChange(PacienteEntity usuario);

}
