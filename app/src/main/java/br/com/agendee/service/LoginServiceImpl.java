package br.com.agendee.service;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.security.auth.login.LoginException;

import br.com.agendee.buffer.RanchucrutesSession;
import br.com.agendee.entity.PacienteConvenioEntity;
import br.com.agendee.entity.PacienteEntity;
import br.com.agendee.exception.RestResponseUnsatisfiedException;
import br.com.agendee.rest.RestUtils;
import br.com.agendee.utils.DateUtils;
import br.com.agendee.utils.ObjectUtils;
import br.com.agendee.utils.StringUtils;
import br.com.agendee.vo.PacienteVo;
import br.com.agendee.commons.AuthType;
import br.com.agendee.exception.NovoPacienteException;
import br.com.agendee.exception.RanchucrutesWSException;
import br.com.agendee.exception.RestException;
import br.com.agendee.exception.RestRequestUnstable;
import br.com.agendee.form.LoginForm;
import br.com.agendee.utils.AndroidSystemUtil;
import br.com.agendee.utils.CollectionUtils;
import br.com.agendee.utils.GcmUtils;
import br.com.agendee.vo.ConvenioCategoriaVo;
import br.com.agendee.vo.ResultadoLoginVo;

/**
 * Created by wagner on 10/09/15.
 */
public class LoginServiceImpl implements LoginService {

    private static String TAG = LoginService.class.getSimpleName();

    @Inject
    private DataService dataService;

    @Inject
    private RanchucrutesService ranchucrutesService;

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

                PacienteEntity usuario = this.registrarAtualizarUsuario(paciente);
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

    @Override
    public PacienteEntity registrarAtualizarUsuario(PacienteVo paciente) {
        //removendo os usuarios da tabela
        //TODO REVER ESSE DELETEALL
        this.deleteAll();
        //usuario autenticado com sucesso, inserindo usuario no banco como ativo.
        PacienteEntity pacienteEntity = dataService.getById(PacienteEntity.class, paciente.getId().intValue());
        boolean insert = false;
        if (pacienteEntity == null){
            pacienteEntity = new PacienteEntity();
            insert = true;
        }
        pacienteEntity.setId(paciente.getId().intValue());
        pacienteEntity.setEmail(paciente.getEmail());
        pacienteEntity.setNome(paciente.getNome());
        pacienteEntity.setTelefone(paciente.getTelefone());
        pacienteEntity.setAuthType(paciente.getAuthType());
        pacienteEntity.setIdCategoria(paciente.getIdCategoria());
        pacienteEntity.setDeviceKey(paciente.getKeyDeviceGcm());
        pacienteEntity.setUrlFoto(paciente.getUrlFoto());
        pacienteEntity.setCpf(paciente.getCpf());
        if ( paciente.getDataNascimento() != null ){
            pacienteEntity.setDataAniversario(DateUtils.formatddMMyyyy(paciente.getDataNascimento()));
        }
        pacienteEntity.setSexo(paciente.getSexo());
        if (insert){
            dataService.insert(pacienteEntity);
        }else{
            dataService.updateById(pacienteEntity);
        }

        if (CollectionUtils.isNotEmpty(paciente.getConveniosCategorias())){
            List<PacienteConvenioEntity> listPacienteConvenio = new ArrayList<>();
            this.deleteAllConvenios();
            for (ConvenioCategoriaVo convenioCategoriaVo : paciente.getConveniosCategorias()){
                PacienteConvenioEntity pc = new PacienteConvenioEntity(pacienteEntity.getId(),convenioCategoriaVo);
                dataService.insert(pc);
                listPacienteConvenio.add(pc);

            }
            pacienteEntity.setListPacienteConvenio(listPacienteConvenio);
        }

        return pacienteEntity;

    }

    private void deleteAllConvenios() {
        List<PacienteConvenioEntity> listPacienteConvenio = dataService.getList(PacienteConvenioEntity.class);
        if (CollectionUtils.isNotEmpty(listPacienteConvenio)){
            for (PacienteConvenioEntity u :listPacienteConvenio) {
                dataService.deleteById(u);
            }
        }
    }

    private void deleteAll() {
        List<PacienteEntity> listPaciente = dataService.getList(PacienteEntity.class);
        if (CollectionUtils.isNotEmpty(listPaciente)){
            for (PacienteEntity u :listPaciente) {
                dataService.deleteById(u);
            }
        }
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
    public void authLocal(Context context) {
        List<PacienteEntity> listPaciente = dataService.getList(PacienteEntity.class);
        if (CollectionUtils.isNotEmpty(listPaciente)){
            PacienteEntity paciente = listPaciente.get(0);

            List<PacienteConvenioEntity> listPacienteConvenio = this.getListCategoriaConvenioByIdPaciente(paciente.getId());
            paciente.setListPacienteConvenio(listPacienteConvenio);

            RanchucrutesSession.setUsuario(paciente);

            initGcm(context);
        }
    }

    private List<PacienteConvenioEntity> getListCategoriaConvenioByIdPaciente(Integer idPaciente) {
        return dataService.getList(PacienteConvenioEntity.class,"id_paciente = ?",new String[]{idPaciente.toString()});
    }

    @Override
    public void logoff() {
        this.deleteAll();
        RanchucrutesSession.logoff();
    }

    @Override
    public void atualizarPaciente(PacienteVo pacienteVo) throws NovoPacienteException {
        String pacienteJson = ObjectUtils.toJson(pacienteVo);
        try {
            PacienteVo pVo = RestUtils.postJson(PacienteVo.class, RanchucrutesConstants.WS_HOST,
                    RanchucrutesConstants.END_POINT_ATUALIZAR_PACIENTE, pacienteJson);
            PacienteEntity u = registrarAtualizarUsuario(pVo);
            RanchucrutesSession.setUsuario(u);
        } catch (RestResponseUnsatisfiedException | RestRequestUnstable e) {
            Log.e(TAG,e.getMessage(),e);
            throw new NovoPacienteException("Erro na comunicação com o servidor. Dados não foram atualizados!");
        } catch (RestException e) {
            Log.e(TAG,e.getMessage(),e);
            throw new NovoPacienteException(e.getErrorMessage().getErrorMessage());
        }
    }

    @Nullable
    public PacienteVo criarPaciente(PacienteVo paciente) throws NovoPacienteException {
        String pacienteJson = ObjectUtils.toJson(paciente);
        try {
            PacienteVo pVo = RestUtils.postJson(PacienteVo.class, RanchucrutesConstants.WS_HOST,
                    RanchucrutesConstants.END_POINT_CRIAR_PACIENTE, pacienteJson);
            PacienteEntity u = registrarAtualizarUsuario(pVo);
            RanchucrutesSession.setUsuario(u);
            return pVo;
        } catch (RestResponseUnsatisfiedException | RestRequestUnstable e) {
            Log.e(TAG,e.getMessage(),e);
            throw new NovoPacienteException("Erro na comunicação com o servidor, tente criar seu login mais tarde!");
        } catch (RestException e) {
            Log.e(TAG,e.getMessage(),e);
            throw new NovoPacienteException(e.getErrorMessage().getErrorMessage());
        }
    }


    private void initGcm(Context context) {
        PacienteEntity user = RanchucrutesSession.getUsuario();
        this.registerKeyDevice(context, user.getDeviceKey());
    }

    public void registerKeyDevice(Context context, String keyRegister) {
        Log.i("LoginServiceImpl","Tentando registrar o device do cliente");
        String regId = AndroidSystemUtil.getRegistrationId(context);

        if (StringUtils.isBlank(regId)){
            regId = GcmUtils.registerIdDevice(context);
        }

        if(StringUtils.isNotBlank(regId) && !regId.equals(keyRegister)){

            Log.i("LoginServiceImpl","DeviceKey = " + regId);
            try {
                Map<String,String> mapParam = new HashMap<String,String>();
                mapParam.put("idLogin", RanchucrutesSession.getUsuario().getId().toString());
                mapParam.put("keyDevice", regId);
                PacienteVo pacienteVo = RestUtils.post(PacienteVo.class, RanchucrutesConstants.WS_HOST, RanchucrutesConstants.END_POINT_REGISTRO_GCM, mapParam);
                Log.i("LoginServiceImpl","DeviceKey registrado com sucesso!! ");
                registrarAtualizarUsuario(pacienteVo);
                Log.i("GcmUtils", pacienteVo.toString());
            } catch (Exception e) {
                Log.e("LoginServiceImpl", "Erro ao registrar o id do device", e);
            }
        }
    }

    @Override
    public PacienteVo recuperarSenha(String email) throws RanchucrutesWSException, RestResponseUnsatisfiedException, RestRequestUnstable, RestException {
      PacienteVo pacienteVo = new PacienteVo();
      pacienteVo.setEmail(email);
      String pacienteJson = ObjectUtils.toJson(pacienteVo);
      return       RestUtils.postJson(PacienteVo.class, RanchucrutesConstants.WS_HOST,
                RanchucrutesConstants.END_POINT_RECUPERAR_SENHA_PACIENTE, pacienteJson);
    }


}

