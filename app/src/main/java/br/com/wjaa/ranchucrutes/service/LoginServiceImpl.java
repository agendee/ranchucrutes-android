package br.com.wjaa.ranchucrutes.service;

import android.support.annotation.Nullable;
import android.util.Log;

import com.google.inject.Inject;

import java.util.List;

import javax.security.auth.login.LoginException;

import br.com.wjaa.ranchucrutes.buffer.RanchucrutesSession;
import br.com.wjaa.ranchucrutes.commons.AuthType;
import br.com.wjaa.ranchucrutes.entity.UsuarioEntity;
import br.com.wjaa.ranchucrutes.exception.NovoPacienteException;
import br.com.wjaa.ranchucrutes.exception.RanchucrutesWSException;
import br.com.wjaa.ranchucrutes.exception.RestException;
import br.com.wjaa.ranchucrutes.exception.RestRequestUnstable;
import br.com.wjaa.ranchucrutes.exception.RestResponseUnsatisfiedException;
import br.com.wjaa.ranchucrutes.form.LoginForm;
import br.com.wjaa.ranchucrutes.rest.RestUtils;
import br.com.wjaa.ranchucrutes.utils.CollectionUtils;
import br.com.wjaa.ranchucrutes.utils.ObjectUtils;
import br.com.wjaa.ranchucrutes.utils.StringUtils;
import br.com.wjaa.ranchucrutes.vo.PacienteVo;
import br.com.wjaa.ranchucrutes.vo.ResultadoLoginVo;

/**
 * Created by wagner on 10/09/15.
 */
public class LoginServiceImpl implements LoginService {



    @Inject
    private DataService dataService;

    @Override
    public PacienteVo criarPacienteFacebook(String nome, String email, String telefone, String idFacebook)
            throws NovoPacienteException {

        PacienteVo paciente = new PacienteVo(nome,email,telefone,idFacebook, AuthType.AUTH_FACEBOOK);
        return criarPaciente(paciente);
    }
    @Override
    public PacienteVo criarPacienteGPlus(String nome, String email, String telefone, String idGPlus) throws NovoPacienteException {
        PacienteVo paciente = new PacienteVo(nome,email,telefone,idGPlus, AuthType.AUTH_GPLUS);
        return criarPaciente(paciente);
    }

    @Override
    public PacienteVo criarPaciente(String email, String senha, String nome, String telefone) throws NovoPacienteException {
        PacienteVo paciente = new PacienteVo(nome,email,telefone,null, AuthType.AUTH_RANCHUCRUTES);
        return criarPaciente(paciente);
    }

    @Override
    public PacienteVo auth(LoginForm auth) throws LoginException, RanchucrutesWSException {

        if (auth.getType() == null){
            throw new IllegalArgumentException("Tipo de login não pode ser vazio");
        }

        if ((AuthType.AUTH_GPLUS.equals(auth.getType()) ||
             AuthType.AUTH_FACEBOOK.equals(auth.getType()) ) &&
                StringUtils.isBlank(auth.getKeySocial()) ){
            throw new IllegalArgumentException("Chave da redesocial não está preenchida.");
        }

        //se for autenticacao pelo ranchucrutes o email e senha tem que estar preenchidos
        if (AuthType.AUTH_RANCHUCRUTES.equals(auth.getType()) &&
                (StringUtils.isBlank(auth.getLogin()) || StringUtils.isBlank(auth.getSenha())) ) {
            throw new IllegalArgumentException("Email ou senha não estão preenchidos");
        }

        String authJson = ObjectUtils.toJson(auth);
        try {
            ResultadoLoginVo resultadoLogin = RestUtils.postJson(ResultadoLoginVo.class,
                    RanchucrutesConstants.WS_HOST,RanchucrutesConstants.END_POINT_AUTH_PACIENTE,authJson);

            if (ResultadoLoginVo.StatusLogin.SUCESSO.equals(resultadoLogin.getStatus())){

                PacienteVo paciente = resultadoLogin.getPaciente();
                UsuarioEntity usuario = this.registrarUsuario(paciente);
                RanchucrutesSession.setUsuario(usuario);
                return paciente;
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

    private UsuarioEntity registrarUsuario(PacienteVo paciente) {
        //desativando todos os usuarios, para registrar um novo.
        List<UsuarioEntity> listUsuario = dataService.getList(UsuarioEntity.class);
        if (CollectionUtils.isNotEmpty(listUsuario)){
            for (UsuarioEntity u :listUsuario) {
                u.setAtivo(false);
                dataService.insertOrUpdate(u);
            }
        }
        //usuario autenticado com sucesso, inserindo usuario no banco como ativo.
        UsuarioEntity usuarioEntity = dataService.getById(UsuarioEntity.class, paciente.getId().intValue());
        if (usuarioEntity == null){
            usuarioEntity = new UsuarioEntity();
            usuarioEntity.setId(paciente.getId().intValue());
            usuarioEntity.setEmail(paciente.getEmail());
            usuarioEntity.setNome(paciente.getNome());
            usuarioEntity.setTelefone(paciente.getTelefone());
            usuarioEntity.setAuthType(paciente.getAuthType());
            usuarioEntity.setAtivo(true);
            dataService.insert(usuarioEntity);
        }else{
            usuarioEntity.setAtivo(true);
            dataService.updateById(usuarioEntity);
        }
        return usuarioEntity;

    }

    @Override
    public PacienteVo auth(String login, String senha) throws LoginException, RanchucrutesWSException {
        LoginForm loginForm = new LoginForm();
        loginForm.setLogin(login);
        loginForm.setSenha(senha);
        loginForm.setType(AuthType.AUTH_RANCHUCRUTES);
        return this.auth(loginForm);
    }

    @Override
    public PacienteVo auth(String key, AuthType type) throws LoginException, RanchucrutesWSException {
        LoginForm loginForm = new LoginForm();
        loginForm.setKeySocial(key);
        loginForm.setType(type);
        return this.auth(loginForm);
    }

    @Override
    public void authLocal() {
        UsuarioEntity usuarioAtivo = dataService.findUniqueResult(UsuarioEntity.class, "ativo=?", new String[]{"1"});
        if (usuarioAtivo != null){
            RanchucrutesSession.setUsuario(usuarioAtivo);
        }
    }

    @Nullable
    public PacienteVo criarPaciente(PacienteVo paciente) throws NovoPacienteException {
        String pacienteJson = ObjectUtils.toJson(paciente);
        try {
            PacienteVo pVo = RestUtils.postJson(PacienteVo.class, RanchucrutesConstants.WS_HOST,
                    RanchucrutesConstants.END_POINT_CRIAR_PACIENTE, pacienteJson);
            UsuarioEntity u = registrarUsuario(pVo);
            RanchucrutesSession.setUsuario(u);
            return pVo;
        } catch (RestResponseUnsatisfiedException | RestRequestUnstable e) {
            throw new NovoPacienteException("Erro na comunicação com o servidor, tente criar seu login mais tarde!");
        } catch (RestException e) {
            throw new NovoPacienteException(e.getErrorMessage().getErrorMessage());
        }
    }
}
