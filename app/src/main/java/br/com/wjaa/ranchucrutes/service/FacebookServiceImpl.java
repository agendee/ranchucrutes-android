package br.com.wjaa.ranchucrutes.service;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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

import br.com.wjaa.ranchucrutes.R;
import br.com.wjaa.ranchucrutes.activity.MainActivity;
import br.com.wjaa.ranchucrutes.activity.NovoPacienteActivity;
import br.com.wjaa.ranchucrutes.commons.AuthType;
import br.com.wjaa.ranchucrutes.exception.NovoPacienteException;
import br.com.wjaa.ranchucrutes.exception.RanchucrutesWSException;
import br.com.wjaa.ranchucrutes.utils.AndroidUtils;
import br.com.wjaa.ranchucrutes.utils.StringUtils;
import br.com.wjaa.ranchucrutes.vo.PacienteVo;
import br.com.wjaa.ranchucrutes.wrapper.NativeFragmentWrapper;

/**
 * Created by wagner on 10/09/15.
 */
public class FacebookServiceImpl implements FacebookService {

    public static final String TAG = FacebookService.class.getSimpleName();


    private TextView mTextDetails;
    private CallbackManager mCallbackManager;
    private AccessTokenTracker mTokenTracker;
    private ProfileTracker mProfileTracker;
    private Fragment fragment;

    @Inject
    private LoginService loginService;

    @Override
    public void onCreate(Fragment frament) {
        this.fragment = frament;
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
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        int resultC = resultCode > 0 ? -1 : resultCode;
        mCallbackManager.onActivityResult(requestCode, resultC, data);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        setupTextDetails(view);
        setupLoginButton(view);
    }


    private void setupTextDetails(View view) {
        mTextDetails = (TextView) view.findViewById(R.id.info);
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

    private void setupLoginButton(View view) {
        LoginManager.getInstance().registerCallback(mCallbackManager, mFacebookCallback);
        Button btn_fb_login = (Button)view.findViewById(R.id.btnFacebook);

        btn_fb_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logInWithReadPermissions(fragment, Arrays.asList("email", "public_profile", "user_friends"));
            }
        });

    }



    private FacebookCallback<LoginResult> mFacebookCallback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            AndroidUtils.showWaitDlgOnUiThread("Autenticando, aguarde...", fragment.getActivity());
            AccessToken accessToken = loginResult.getAccessToken();
            loginFacebook(accessToken);
        }
        @Override
        public void onCancel() {
            Log.e("VIVZ", "onCancel");
        }

        @Override
        public void onError(FacebookException e) {
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
                            AndroidUtils.openActivity(fragment.getActivity(),NovoPacienteActivity.class,b);
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
                loginService.auth(id, AuthType.AUTH_FACEBOOK);
                AndroidUtils.closeWaitDlg();

                //TODO MELHOR ISSO AQUI...TÁ MAIS FEIO QUE BATER NA MAE
                fragment.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((MainActivity) fragment.getActivity()).displayView(0);
                    }
                });


            } catch (LoginException e) {
                if (accessToken != null){
                    AndroidUtils.showWaitDlgOnUiThread("Registrando paciente, aguarde...", fragment.getActivity());
                    //se paciente nao existe na base do ranchucrutes. Vamos cria-lo
                    criarPaciente(accessToken);
                }else{
                    AndroidUtils.showMessageDlgOnUiThread("Erro!", e.getMessage(), fragment.getActivity());
                }
            } catch (RanchucrutesWSException e) {

                AndroidUtils.showMessageDlgOnUiThread("Erro!", e.getMessage(), fragment.getActivity());
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

                AndroidUtils.showWaitDlgOnUiThread("Aguarde, criando usuário...", fragment.getActivity());
                loginService.criarPaciente(paciente);

                AndroidUtils.closeWaitDlg();
                //TODO mais feio que bater na mae.
                fragment.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((MainActivity) fragment.getActivity()).displayView(0);
                    }
                });
            }  catch (NovoPacienteException e) {
                Log.e("LoginFrament",e.getMessage(),e);
                AndroidUtils.closeWaitDlg();
                AndroidUtils.showMessageDlgOnUiThread("Erro", e.getMessage(), fragment.getActivity());
            }

        }
    }

}
