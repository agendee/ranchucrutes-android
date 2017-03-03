package br.com.agendee.service;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.inject.Inject;

import javax.security.auth.login.LoginException;

import br.com.agendee.activity.NovoPacienteActivity;
import br.com.agendee.buffer.RanchucrutesSession;
import br.com.agendee.commons.AuthType;
import br.com.agendee.entity.PacienteEntity;
import br.com.agendee.exception.NovoPacienteException;
import br.com.agendee.exception.RanchucrutesWSException;
import br.com.agendee.utils.AndroidSystemUtil;
import br.com.agendee.utils.AndroidUtils;
import br.com.agendee.utils.StringUtils;
import br.com.agendee.vo.PacienteVo;

/**
 * Created by wagner on 25/09/15.
 */
public class GPlusServiceImpl implements GPlusService, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int RC_SIGN_IN = 9001;

    // Profile pic image size in pixels
    private static final int PROFILE_PIC_SIZE = 400;

    private GoogleApiClient gplusClient;
    private Activity context;
    private boolean mIntentInProgress;

    private boolean mSignInClicked;

    private ConnectionResult mConnectionResult;

    @Inject
    private LoginService loginService;

    @Override
    public void onCreate(Activity context) {
        this.context = context;
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // Builds single client object that connects to Drive and Google+
       /* gplusClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addScope(Plus.SCOPE_PLUS_LOGIN).build();*/

        gplusClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API, Plus.PlusOptions.builder().build())
                .addScope(Plus.SCOPE_PLUS_LOGIN).build();


        /*gplusClient = new GoogleApiClient.Builder(context)
                //.enableAutoManage(context, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();*/
    }


    @Override
    public void onStart(){

    }

    public void onStop(){
        if (gplusClient.isConnected()) {
            gplusClient.disconnect();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_SIGN_IN) {
            if (resultCode != Activity.RESULT_OK) {
                mSignInClicked = false;
            }

            mIntentInProgress = false;

            if (!gplusClient.isConnecting()) {
                gplusClient.connect();
            }
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        AndroidUtils.closeWaitDlg();
        mSignInClicked = false;
        Toast.makeText(context, "Paciente conectado!", Toast.LENGTH_LONG).show();
        getProfileInformation();
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        AndroidUtils.closeWaitDlg();
    }



    @Override
    public void onConnectionFailed(ConnectionResult result) {
        AndroidUtils.closeWaitDlg();
        if (!result.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), context,
                    0).show();
            return;
        }

        if (!mIntentInProgress) {
            // Store the ConnectionResult for later usage
            mConnectionResult = result;
            resolveSignInError();

        }

    }

    /**
     * Method to resolve any signin errors
     * */
    private void resolveSignInError() {
        if (mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                mConnectionResult.startResolutionForResult(context, RC_SIGN_IN);
            } catch (IntentSender.SendIntentException e) {
                mIntentInProgress = false;
                gplusClient.connect();
            }
        }
    }



    private void getProfileInformation() {

            if (Plus.PeopleApi.getCurrentPerson(gplusClient) != null) {
                Person currentPerson = Plus.PeopleApi
                        .getCurrentPerson(gplusClient);

                final String id = currentPerson.getId();
                new LogarUsuarioGPlus(id).start();
            } else {
                Toast.makeText(context,
                        "Person information is null", Toast.LENGTH_LONG).show();
            }

    }

    @Override
    public void onClick(View v) {
        AndroidUtils.showWaitDlg("Aguarde, comunicando com o GPlus...",context);
        gplusClient.connect();
    }



    class LogarUsuarioGPlus extends Thread{
        private String id;

        public LogarUsuarioGPlus(String id) {
            this.id = id;
        }


        @Override
        public void run() {
            try {
                AndroidUtils.showWaitDlgOnUiThread("Aguarde autenticando paciente.", context);
                //se estiver authenticado ele já envia o pacientevo para o buffer
                PacienteVo pacienteVo = loginService.auth(id, AuthType.AUTH_GPLUS);
                loginService.registerKeyDevice(context, pacienteVo.getKeyDeviceGcm());
                Person currentPerson = Plus.PeopleApi.getCurrentPerson(gplusClient);

                //ATUALIZANDO A FOTO DO USUARIO CASO TENHA MUDADO
                String urlImg = currentPerson.getImage().getUrl();
                pacienteVo.setUrlFoto(urlImg);
                PacienteEntity usuario = loginService.registrarAtualizarUsuario(pacienteVo);
                RanchucrutesSession.setUsuario(usuario);
                AndroidUtils.closeWaitDlg();
                context.finish();

            } catch (LoginException e) {

                Person currentPerson = Plus.PeopleApi
                        .getCurrentPerson(gplusClient);

                if (currentPerson != null){
                    AndroidUtils.showWaitDlgOnUiThread("Registrando paciente, aguarde...", context);
                    //se paciente nao existe na base do ranchucrutes. Vamos cria-lo
                    criarPaciente(currentPerson);
                    AndroidUtils.closeWaitDlg();
                }else{
                    AndroidUtils.showMessageErroDlgOnUiThread(e.getMessage(), context);
                }
            } catch (RanchucrutesWSException e) {

                AndroidUtils.showMessageErroDlgOnUiThread(e.getMessage(), context);
            }
        }
    }

    private void criarPaciente(Person currentPerson) {
        final String id = currentPerson.getId();
        final String nome = currentPerson.getDisplayName();
        final String email = Plus.AccountApi.getAccountName(gplusClient);
        final String urlFoto = currentPerson.getImage().getUrl();

        //se nao tiverem vazio pode criar um novo usuario
        if(StringUtils.isNotBlank(id) &&
                StringUtils.isNotBlank(nome) &&
                StringUtils.isNotBlank(email)){

            new CriarUsuarioGPlus(id,nome,email,urlFoto
            ).start();
        }else{
            //alguma informacao está vazia, entao usuário precisa completar os seus dados.
            PacienteVo pacienteVo = new PacienteVo();
            pacienteVo.setKeySocial(id);
            pacienteVo.setAuthType(AuthType.AUTH_GPLUS);
            pacienteVo.setNome(nome);
            pacienteVo.setEmail(email);
            pacienteVo.setUrlFoto(urlFoto);
            Bundle b = new Bundle();
            b.putSerializable("paciente",pacienteVo);
            AndroidUtils.openActivity(context,NovoPacienteActivity.class,b);
        }


    }


    class CriarUsuarioGPlus extends Thread{
        private String id;
        private String nome;
        private String email;
        private String urlFoto;

        public CriarUsuarioGPlus(String id, String nome, String email,String urlFoto){
            this.id = id;
            this.nome = nome;
            this.email = email;
            this.urlFoto = urlFoto;
        }


        @Override
        public void run() {
            try {
                PacienteVo paciente = new PacienteVo();
                paciente.setNome(nome);
                paciente.setEmail(email);
                paciente.setAuthType(AuthType.AUTH_GPLUS);
                paciente.setKeySocial(id);
                paciente.setUrlFoto(urlFoto);

                AndroidUtils.showWaitDlgOnUiThread("Aguarde, criando paciente...", context);
                String regId = AndroidSystemUtil.getRegistrationId(context);
                paciente.setKeyDeviceGcm(regId);
                PacienteVo pacienteVo = loginService.criarPaciente(paciente);
                AndroidUtils.closeWaitDlg();
                context.finish();
            }  catch (NovoPacienteException e) {
                Log.e("LoginFrament",e.getMessage(),e);
                AndroidUtils.closeWaitDlg();
                AndroidUtils.showMessageErroDlgOnUiThread(e.getMessage(), context);
            }

        }
    }

}
