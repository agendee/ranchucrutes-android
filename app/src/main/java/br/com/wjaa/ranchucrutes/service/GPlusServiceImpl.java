package br.com.wjaa.ranchucrutes.service;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.inject.Inject;

import javax.security.auth.login.LoginException;

import br.com.wjaa.ranchucrutes.activity.NovoPacienteActivity;
import br.com.wjaa.ranchucrutes.activity.callback.DialogCallback;
import br.com.wjaa.ranchucrutes.commons.AuthType;
import br.com.wjaa.ranchucrutes.exception.NovoPacienteException;
import br.com.wjaa.ranchucrutes.exception.RanchucrutesWSException;
import br.com.wjaa.ranchucrutes.utils.AndroidUtils;
import br.com.wjaa.ranchucrutes.utils.StringUtils;
import br.com.wjaa.ranchucrutes.vo.PacienteVo;

/**
 * Created by wagner on 25/09/15.
 */
public class GPlusServiceImpl implements GPlusService, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int RC_SIGN_IN = 0;
    // Logcat tag
    private static final String TAG = "MainActivity";

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
        // Builds single client object that connects to Drive and Google+
        gplusClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN).build();
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

        //signInWithGplus();
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
                AndroidUtils.closeWaitDlg();
                saudarSair(pacienteVo);

            } catch (LoginException e) {

                Person currentPerson = Plus.PeopleApi
                        .getCurrentPerson(gplusClient);

                if (currentPerson != null){
                    AndroidUtils.showWaitDlgOnUiThread("Registrando paciente, aguarde...", context);
                    //se paciente nao existe na base do ranchucrutes. Vamos cria-lo
                    criarPaciente(currentPerson);
                    AndroidUtils.closeWaitDlg();
                }else{
                    AndroidUtils.showMessageDlgOnUiThread("Erro!", e.getMessage(), context);
                }
            } catch (RanchucrutesWSException e) {

                AndroidUtils.showMessageDlgOnUiThread("Erro!", e.getMessage(), context);
            }
        }
    }

    private void criarPaciente(Person currentPerson) {
        final String id = currentPerson.getId();
        final String nome = currentPerson.getDisplayName();
        final String email = Plus.AccountApi.getAccountName(gplusClient);

        //se nao tiverem vazio pode criar um novo usuario
        if(StringUtils.isNotBlank(id) &&
                StringUtils.isNotBlank(nome) &&
                StringUtils.isNotBlank(email)){

            new CriarUsuarioGPlus(id,nome,email
            ).start();
        }else{
            //alguma informacao está vazia, entao usuário precisa completar os seus dados.
            PacienteVo pacienteVo = new PacienteVo();
            pacienteVo.setKeySocial(id);
            pacienteVo.setAuthType(AuthType.AUTH_GPLUS);
            pacienteVo.setNome(nome);
            pacienteVo.setEmail(email);
            Bundle b = new Bundle();
            b.putSerializable("paciente",pacienteVo);
            AndroidUtils.openActivity(context,NovoPacienteActivity.class,b);
        }


    }


    class CriarUsuarioGPlus extends Thread{
        private String id;
        private String nome;
        private String email;

        public CriarUsuarioGPlus(String id, String nome, String email){
            this.id = id;
            this.nome = nome;
            this.email = email;
        }


        @Override
        public void run() {
            try {
                PacienteVo paciente = new PacienteVo();
                paciente.setNome(nome);
                paciente.setEmail(email);
                paciente.setAuthType(AuthType.AUTH_GPLUS);
                paciente.setKeySocial(id);

                AndroidUtils.showWaitDlgOnUiThread("Aguarde, criando paciente...", context);
                PacienteVo pacienteVo = loginService.criarPaciente(paciente);
                AndroidUtils.closeWaitDlg();
                saudarSair(pacienteVo);
            }  catch (NovoPacienteException e) {
                Log.e("LoginFrament",e.getMessage(),e);
                AndroidUtils.closeWaitDlg();
                AndroidUtils.showMessageDlgOnUiThread("Erro", e.getMessage(), context);
            }

        }
    }


    private void saudarSair(PacienteVo pacienteVo) {
        if (pacienteVo != null){
            AndroidUtils.showMessageDlgOnUiThread("Sucesso", "Olá " + pacienteVo.getNome(), context, new DialogCallback() {

                @Override
                public void confirm() {
                    context.finish();
                }

                @Override
                public void cancel() {

                }
            });

        }
    }
}
