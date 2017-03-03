package br.com.agendee.listener;

import br.com.agendee.entity.PacienteEntity;

/**
 * Created by wagner on 17/09/15.
 */
public interface SessionChangedListener {

    void usuarioChange(PacienteEntity usuario);

}
