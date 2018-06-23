package br.com.agendee.service;

import android.content.Context;

import javax.security.auth.login.LoginException;

import br.com.agendee.entity.PacienteEntity;
import br.com.agendee.exception.RestException;
import br.com.agendee.exception.RestRequestUnstable;
import br.com.agendee.exception.RestResponseUnsatisfiedException;
import br.com.agendee.vo.PacienteVo;
import br.com.agendee.commons.AuthType;
import br.com.agendee.exception.NovoPacienteException;
import br.com.agendee.exception.RanchucrutesWSException;
import br.com.agendee.form.LoginForm;

/**
 * Created by wagner on 10/09/15.
 */
public interface LoginService {

    PacienteVo criarPacienteFacebook(String nome, String email, String telefone, String idFacebook) throws NovoPacienteException;

    PacienteVo criarPacienteGPlus(String nome, String email, String telefone, String idGPlus) throws NovoPacienteException;

    PacienteVo criarPaciente(String email, String senha, String nome, String telefone) throws NovoPacienteException;

    PacienteVo criarPaciente(PacienteVo paciente) throws NovoPacienteException;

    PacienteVo auth(LoginForm auth) throws LoginException, RanchucrutesWSException;

    PacienteVo auth(String login, String senha) throws LoginException, RanchucrutesWSException;

    PacienteVo auth(String key, AuthType type) throws LoginException, RanchucrutesWSException;

    void authLocal(Context context);

    void logoff();

    void atualizarPaciente(PacienteVo pacienteVo) throws NovoPacienteException;

    PacienteEntity registrarAtualizarUsuario(PacienteVo paciente);

    void registerKeyDevice(Context context, String keyRegister);

    PacienteVo recuperarSenha(String email) throws RanchucrutesWSException, RestResponseUnsatisfiedException, RestRequestUnstable, RestException;
}
