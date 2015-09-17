package br.com.wjaa.ranchucrutes.service;

import javax.security.auth.login.LoginException;

import br.com.wjaa.ranchucrutes.exception.NovoPacienteException;
import br.com.wjaa.ranchucrutes.exception.RanchucrutesWSException;
import br.com.wjaa.ranchucrutes.form.LoginForm;
import br.com.wjaa.ranchucrutes.vo.PacienteVo;

/**
 * Created by wagner on 10/09/15.
 */
public interface LoginService {

    PacienteVo criarPacienteFacebook(String nome, String email, String telefone, String idFacebook) throws NovoPacienteException;

    PacienteVo criarPacienteGPlus(String nome, String email, String telefone, String idGPlus) throws NovoPacienteException;

    PacienteVo criarPaciente(String email, String senha, String nome, String telefone) throws NovoPacienteException;

    PacienteVo auth(LoginForm auth) throws LoginException, RanchucrutesWSException;

    PacienteVo auth(String login, String senha) throws LoginException, RanchucrutesWSException;

    PacienteVo auth(String key, LoginForm.AuthType type) throws LoginException, RanchucrutesWSException;

}