package br.com.wjaa.ranchucrutes.service;

import android.support.annotation.Nullable;
import android.util.Log;

import javax.security.auth.login.LoginException;
import br.com.wjaa.ranchucrutes.buffer.RanchucrutesBuffer;
import br.com.wjaa.ranchucrutes.buffer.RanchucrutesSession;
import br.com.wjaa.ranchucrutes.exception.NovoPacienteException;
import br.com.wjaa.ranchucrutes.exception.RanchucrutesWSException;
import br.com.wjaa.ranchucrutes.exception.RestException;
import br.com.wjaa.ranchucrutes.exception.RestRequestUnstable;
import br.com.wjaa.ranchucrutes.exception.RestResponseUnsatisfiedException;
import br.com.wjaa.ranchucrutes.rest.RestUtils;
import br.com.wjaa.ranchucrutes.utils.ObjectUtils;
import br.com.wjaa.ranchucrutes.form.LoginForm;
import br.com.wjaa.ranchucrutes.utils.StringUtils;
import br.com.wjaa.ranchucrutes.vo.PacienteVo;
import br.com.wjaa.ranchucrutes.vo.ResultadoLoginVo;

/**
 * Created by wagner on 10/09/15.
 */
public class LoginServiceImpl implements LoginService {

    @Override
    public PacienteVo criarPacienteFacebook(String nome, String email, String telefone, String idFacebook)
            throws NovoPacienteException {

        PacienteVo paciente = new PacienteVo(nome,email,telefone,idFacebook, LoginForm.AuthType.AUTH_FACEBOOK);
        return criarPaciente(paciente);
    }
    @Override
    public PacienteVo criarPacienteGPlus(String nome, String email, String telefone, String idGPlus) throws NovoPacienteException {
        PacienteVo paciente = new PacienteVo(nome,email,telefone,idGPlus, LoginForm.AuthType.AUTH_GPLUS);
        return criarPaciente(paciente);
    }

    @Override
    public PacienteVo criarPaciente(String email, String senha, String nome, String telefone) throws NovoPacienteException {
        PacienteVo paciente = new PacienteVo(nome,email,telefone,null, LoginForm.AuthType.AUTH_RANCHUCRUTES);
        return criarPaciente(paciente);
    }

    @Override
    public PacienteVo auth(LoginForm auth) throws LoginException, RanchucrutesWSException {

        if (auth.getType() == null){
            throw new IllegalArgumentException("Tipo de login não pode ser vazio");
        }

        if ((LoginForm.AuthType.AUTH_GPLUS.equals(auth.getType()) ||
             LoginForm.AuthType.AUTH_FACEBOOK.equals(auth.getType()) ) &&
                StringUtils.isBlank(auth.getKeySocial()) ){
            throw new IllegalArgumentException("Chave da redesocial não está preenchida.");
        }

        //se for autenticacao pelo ranchucrutes o email e senha tem que estar preenchidos
        if (LoginForm.AuthType.AUTH_RANCHUCRUTES.equals(auth.getType()) &&
                (StringUtils.isBlank(auth.getLogin()) || StringUtils.isBlank(auth.getSenha())) ) {
            throw new IllegalArgumentException("Email ou senha não estão preenchidos");
        }

        String authJson = ObjectUtils.toJson(auth);
        try {
            ResultadoLoginVo resultadoLogin = RestUtils.postJson(ResultadoLoginVo.class,
                    RanchucrutesConstants.WS_HOST,RanchucrutesConstants.END_POINT_AUTH_PACIENTE,authJson);

            if (ResultadoLoginVo.StatusLogin.SUCESSO.equals(resultadoLogin.getStatus())){
                RanchucrutesSession.setPaciente(resultadoLogin.getPaciente());
                return resultadoLogin.getPaciente();
            }else{
                throw new LoginException(resultadoLogin.getStatus().getMsg());
            }

        } catch (RestResponseUnsatisfiedException | RestRequestUnstable e ) {
            Log.e(LoginServiceImpl.class.getSimpleName(), e.getMessage(), e);
            throw new RanchucrutesWSException(e.getMessage());
        } catch (RestException e) {
            Log.e(LoginServiceImpl.class.getSimpleName(), e.getMessage(), e);
            throw new RanchucrutesWSException(e.getErrorMessage().getErrorMessage());
        }

    }

    @Override
    public PacienteVo auth(String login, String senha) throws LoginException, RanchucrutesWSException {
        LoginForm loginForm = new LoginForm();
        loginForm.setLogin(login);
        loginForm.setSenha(senha);
        loginForm.setType(LoginForm.AuthType.AUTH_RANCHUCRUTES);
        return this.auth(loginForm);
    }

    @Override
    public PacienteVo auth(String key, LoginForm.AuthType type) throws LoginException, RanchucrutesWSException {
        LoginForm loginForm = new LoginForm();
        loginForm.setKeySocial(key);
        loginForm.setType(type);
        return this.auth(loginForm);
    }

    @Nullable
    private PacienteVo criarPaciente(PacienteVo paciente) throws NovoPacienteException {
        String pacienteJson = ObjectUtils.toJson(paciente);
        try {
            PacienteVo pVo = RestUtils.postJson(PacienteVo.class, RanchucrutesConstants.WS_HOST,
                    RanchucrutesConstants.END_POINT_CRIAR_PACIENTE, pacienteJson);
            RanchucrutesSession.setPaciente(pVo);
            return pVo;
        } catch (RestResponseUnsatisfiedException | RestRequestUnstable e) {
            throw new NovoPacienteException("Erro na comunicação com o servidor, tente criar seu login mais tarde!");
        } catch (RestException e) {
            throw new NovoPacienteException(e.getErrorMessage().getErrorMessage());
        }
    }
}
