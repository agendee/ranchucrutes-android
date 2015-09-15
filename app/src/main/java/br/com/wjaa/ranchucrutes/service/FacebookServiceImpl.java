package br.com.wjaa.ranchucrutes.service;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.inject.Inject;

import org.json.JSONException;
import org.json.JSONObject;

import javax.security.auth.login.LoginException;

import br.com.wjaa.ranchucrutes.R;
import br.com.wjaa.ranchucrutes.exception.RanchucrutesWSException;
import br.com.wjaa.ranchucrutes.form.LoginForm;
import br.com.wjaa.ranchucrutes.activity.NovoPacienteActivity;
import br.com.wjaa.ranchucrutes.utils.AndroidUtils;
import br.com.wjaa.ranchucrutes.wrapper.NativeFragmentWrapper;

/**
 * Created by wagner on 10/09/15.
 */
public class FacebookServiceImpl implements FacebookService {

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
        Profile profile = Profile.getCurrentProfile();
        mTextDetails.setText(constructWelcomeMessage(profile));
    }

    @Override
    public void onStop() {
        mTokenTracker.stopTracking();
        mProfileTracker.stopTracking();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
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
                Log.d("VIVZ", "" + currentAccessToken);
            }
        };
    }

    private void setupProfileTracker() {
        mProfileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                Log.d("VIVZ", "" + currentProfile);
                mTextDetails.setText(constructWelcomeMessage(currentProfile));
            }
        };
    }

    private void setupLoginButton(View view) {
        LoginButton mButtonLogin = (LoginButton) view.findViewById(R.id.login_button);
        mButtonLogin.setFragment(new NativeFragmentWrapper(this.fragment));
//        if (Build.VERSION.SDK_INT >= 16)
//            mButtonLogin.setBackground(null);
//        else
//            mButtonLogin.setBackgroundDrawable(null);
        mButtonLogin.setCompoundDrawables(null, null, null, null);
        mButtonLogin.setReadPermissions("email", "public_profile", "user_friends");
        mButtonLogin.registerCallback(mCallbackManager, mFacebookCallback);
    }

    private String constructWelcomeMessage(Profile profile) {
        StringBuffer stringBuffer = new StringBuffer();
        if (profile != null) {
            stringBuffer.append("Welcome " + profile.getName());
        }
        return stringBuffer.toString();
    }


    private FacebookCallback<LoginResult> mFacebookCallback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {

            AccessToken accessToken = loginResult.getAccessToken();
            login(accessToken);

        }

        @Override
        public void onCancel() {
            Log.d("VIVZ", "onCancel");
        }

        @Override
        public void onError(FacebookException e) {
            Log.d("VIVZ", "onError " + e);
        }
    };

    private void criarPaciente(AccessToken accessToken) {
        GraphRequest request = GraphRequest.newMeRequest(accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        try {

                            AndroidUtils.closeWaitDlg();
                            String nome = object.get("name").toString();
                            String email = object.get("email").toString();
                            AndroidUtils.openActivity(fragment.getActivity(),NovoPacienteActivity.class);




                            //loginService.criarPacienteFacebook(profile.getName(), email, telefone, profile.getId())
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,link,email");
        request.setParameters(parameters);
        request.executeAsync();
    }


    private void login(final AccessToken accessToken) {

        GraphRequest request = GraphRequest.newMeRequest(accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        String id = "";
                        try {
                            AndroidUtils.showWaitDlg("Autenticando, aguarde...",fragment.getActivity());
                            id = object.get("id").toString();
                            //se estiver authenticado ele já envia o pacientevo para o buffer

                            loginService.auth(id, LoginForm.AuthType.AUTH_FACEBOOK);

                            AndroidUtils.showMessageDlg("Sucesso!", "Usuário logado com sucesso!", fragment.getActivity());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (LoginException e) {
                            AndroidUtils.closeWaitDlg();
                            //se paciente nao existe na base do ranchucrutes. Vamos cria-lo
                            AndroidUtils.showWaitDlg("Registrando paciente, aguarde...",fragment.getActivity());
                            criarPaciente(accessToken);
                        } catch (RanchucrutesWSException e) {
                            AndroidUtils.closeWaitDlg();
                            AndroidUtils.showMessageDlg("Erro!", e.getMessage(), fragment.getActivity());
                            //tirar isso daqui...é apenas um testes.
                            criarPaciente(accessToken);
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,link,email");
        request.setParameters(parameters);
        request.executeAsync();


    }


}
