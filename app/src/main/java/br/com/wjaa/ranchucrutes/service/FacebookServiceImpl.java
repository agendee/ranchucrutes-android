package br.com.wjaa.ranchucrutes.service;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.inject.Inject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

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
 * Created by wagner on 10/09/15.
 */
public class FacebookServiceImpl implements FacebookService {

    public static final String TAG = FacebookService.class.getSimpleName();


    private TextView mTextDetails;
    private CallbackManager mCallbackManager;
    private AccessTokenTracker mTokenTracker;
    private ProfileTracker mProfileTracker;
    private Activity context;

    @Inject
    private LoginService loginService;

    @Override
    public void onCreate(Activity context) {
        this.context = context;
        mCallbackManager = CallbackManager.Factory.create();
        setupTokenTracker();
        setupProfileTracker();
        mTokenTracker.startTracking();
        mProfileTracker.startTracking();
    }

    @Override
    public void onResume() {
        /*Profile profile = Profile.getCurrentProfile();
        if (profile != null){
            logarWS(profile.getId(), null);
        }*/
    }

    @Override
    public void onStop() {
        mTokenTracker.stopTracking();
        mProfileTracker.stopTracking();
        LoginManager.getInstance().logOut();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //TODO ISSO É UMA GABIARRA PQ O RESULTCODE ESTAVA VINDO COM NUMERO MAIOR  QUE ZERO (EX:254778) MAS O RESULTADO ERA OK E NAO ERRO.
        int resultC = resultCode > 0 ? -1 : resultCode;
        mCallbackManager.onActivityResult(requestCode, resultC, data);
    }

    @Override
    public void onStart() {
        setupLoginButton();
    }

    private void setupTokenTracker() {
        mTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                Log.d(TAG, "TROCA DE TOKEN: " + currentAccessToken);
            }
        };
    }

    private void setupProfileTracker() {
        mProfileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                Log.d(TAG, "trocando de profile aa: " + currentProfile);

            }
        };
    }

    private void setupLoginButton() {
        LoginManager.getInstance().registerCallback(mCallbackManager, mFacebookCallback);
    }



    private FacebookCallback<LoginResult> mFacebookCallback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            AndroidUtils.closeWaitDlg();
            Toast.makeText(context, "Paciente conectado!", Toast.LENGTH_LONG).show();
            AndroidUtils.showWaitDlgOnUiThread("Autenticando, aguarde...", context);
            AccessToken accessToken = loginResult.getAccessToken();
            loginFacebook(accessToken);
        }
        @Override
        public void onCancel() {
            AndroidUtils.closeWaitDlg();
            Log.e("VIVZ", "onCancel");
        }

        @Override
        public void onError(FacebookException e) {
            AndroidUtils.closeWaitDlg();
            Log.e("VIVZ", "onError " + e,e);
        }
    };

    private void criarPaciente(AccessToken accessToken) {
        GraphRequest request = GraphRequest.newMeRequest(accessToken,
            new GraphRequest.GraphJSONObjectCallback() {
                @Override
                public void onCompleted(
                        JSONObject object,
                        GraphResponse response) {
                    try{

                        String id = object.get("id").toString();
                        String nome = object.get("name").toString();
                        String email = object.get("email").toString();

                        //se nao tiverem vazio pode criar um novo usuario
                        if(StringUtils.isNotBlank(id) &&
                                StringUtils.isNotBlank(nome) &&
                                StringUtils.isNotBlank(email)){

                            new CriarUsuarioFacebook(id,nome,email
                            ).start();
                        }else{


                            //alguma informacao está vazia, entao usuário precisa completar os seus dados.
                            AndroidUtils.closeWaitDlg();
                            PacienteVo pacienteVo = new PacienteVo();
                            pacienteVo.setKeySocial(id);
                            pacienteVo.setAuthType(AuthType.AUTH_FACEBOOK);
                            pacienteVo.setNome(nome);
                            pacienteVo.setEmail(email);
                            Bundle b = new Bundle();
                            b.putSerializable("paciente",pacienteVo);
                            AndroidUtils.openActivity(context,NovoPacienteActivity.class,b);
                        }

                    }catch (JSONException e) {
                        Log.e(TAG,e.getMessage(),e);
                    }


                }
            });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,link,email");
        request.setParameters(parameters);
        request.executeAsync();
    }


    private void loginFacebook(final AccessToken accessToken) {

        GraphRequest request = GraphRequest.newMeRequest(accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        try {
                            String id = object.get("id").toString();
                            logarWS(id, accessToken);
                        } catch (JSONException e) {
                            Log.e(TAG,e.getMessage(),e);
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,link,email");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void logarWS(String id, AccessToken accessToken) {
        new LogarUsuarioFacebook(id,accessToken).start();
    }

    @Override
    public void onClick(View v) {
        AndroidUtils.showWaitDlg("Aguarde, comunicando com o facebook...", context);
        LoginManager.getInstance().logInWithReadPermissions(context, Arrays.asList("email", "public_profile", "user_friends"));
    }


    class LogarUsuarioFacebook extends Thread{
        private String id;
        private AccessToken accessToken;

        public LogarUsuarioFacebook(String id, AccessToken accessToken){
            this.id = id;
            this.accessToken = accessToken;
        }


        @Override
        public void run() {
            try {
                //se estiver authenticado ele já envia o pacientevo para o buffer
                PacienteVo pacienteVo = loginService.auth(id, AuthType.AUTH_FACEBOOK);
                AndroidUtils.closeWaitDlg();
                saudarSair(pacienteVo);

            } catch (LoginException e) {
                if (accessToken != null){
                    AndroidUtils.showWaitDlgOnUiThread("Registrando paciente, aguarde...", context);
                    //se paciente nao existe na base do ranchucrutes. Vamos cria-lo
                    criarPaciente(accessToken);
                }else{
                    AndroidUtils.showMessageErroDlgOnUiThread(e.getMessage(), context);
                }
            } catch (RanchucrutesWSException e) {

                AndroidUtils.showMessageErroDlgOnUiThread(e.getMessage(), context);
            }
        }
    }


    class CriarUsuarioFacebook extends Thread{
        private String id;
        private String nome;
        private String email;

        public CriarUsuarioFacebook(String id, String nome, String email){
            this.id = id;
            this.nome = nome;
            this.email = email;
        }


        @Override
        public void run() {
            try {
                AndroidUtils.closeWaitDlg();
                PacienteVo paciente = new PacienteVo();
                paciente.setNome(nome);
                paciente.setEmail(email);
                paciente.setAuthType(AuthType.AUTH_FACEBOOK);
                paciente.setKeySocial(id);

                AndroidUtils.showWaitDlgOnUiThread("Aguarde, criando paciente...", context);
                PacienteVo pacienteVo = loginService.criarPaciente(paciente);
                AndroidUtils.closeWaitDlg();
                Toast.makeText(context, "Paciente conectado!", Toast.LENGTH_LONG).show();
                saudarSair(pacienteVo);

            }  catch (NovoPacienteException e) {
                Log.e("LoginFrament",e.getMessage(),e);
                AndroidUtils.closeWaitDlg();
                AndroidUtils.showMessageErroDlgOnUiThread(e.getMessage(), context);
            }

        }
    }

    private void saudarSair(PacienteVo pacienteVo) {
        if (pacienteVo != null){
            AndroidUtils.showMessageSuccessDlgOnUiThread("Olá " + pacienteVo.getNome(), context, new DialogCallback() {

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
