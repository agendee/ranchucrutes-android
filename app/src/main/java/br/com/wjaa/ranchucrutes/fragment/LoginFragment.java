package br.com.wjaa.ranchucrutes.fragment;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.inject.Inject;

import javax.security.auth.login.LoginException;

import br.com.wjaa.ranchucrutes.R;
import br.com.wjaa.ranchucrutes.exception.RanchucrutesWSException;
import br.com.wjaa.ranchucrutes.service.FacebookService;
import br.com.wjaa.ranchucrutes.service.LoginService;
import br.com.wjaa.ranchucrutes.utils.AndroidUtils;
import br.com.wjaa.ranchucrutes.utils.StringUtils;
import br.com.wjaa.ranchucrutes.vo.PacienteVo;
import roboguice.fragment.provided.RoboFragment;
import roboguice.inject.InjectView;

/**
 *
 */
public class LoginFragment extends RoboFragment {

    @Inject
    private FacebookService facebookService;

    @Inject
    private LoginService loginService;

    @InjectView(R.id.btnEntrar)
    private Button btnEntrar;


    @InjectView(R.id.txtNovoPaciente)
    private TextView txtNovoPaciente;

    @InjectView(R.id.edtLoginEmail)
    private EditText edtLoginEmail;

    @InjectView(R.id.edtLoginPass)
    private EditText edtLoginPass;


    @Inject
    private NovoPacienteFragment novoPacienteFragment;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        facebookService.onCreate(this);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        facebookService.onViewCreated(view, savedInstanceState);
        initButtons();

    }

    private void initButtons() {

        txtNovoPaciente.setText(Html.fromHtml("<u>" + getString(R.string.txtNovoPaciente) + "</u>"));
        txtNovoPaciente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // consider using Java coding conventions (upper first char class names!!!)
                FragmentTransaction transaction = getFragmentManager().beginTransaction();

                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack
                transaction.setCustomAnimations(R.anim.fadein, R.anim.fadeout, R.anim.fadein, R.anim.fadeout)
                        .replace(R.id.main_frame, novoPacienteFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        btnEntrar.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                String email = edtLoginEmail.getText().toString();
                String pass = edtLoginPass.getText().toString();
                if (StringUtils.isBlank(email)){
                    AndroidUtils.showMessageDlg("Erro","Preencha seu email.",getActivity());
                    return;
                }
                if (StringUtils.isBlank(pass)){
                    AndroidUtils.showMessageDlg("Erro","Preencha sua senha.",getActivity());
                    return;
                }

                new LogarUsuario(email,pass).start();

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        facebookService.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
        facebookService.onStop();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        facebookService.onActivityResult(requestCode,resultCode,data);
    }


    class LogarUsuario extends Thread{
        private String email;
        private String senha;
        LogarUsuario(String email, String senha){
            this.email = email;
            this.senha = senha;
        }

        @Override
        public void run() {

            try {
                AndroidUtils.showWaitDlg("Aguarde, autenticando usuário",getActivity());
                PacienteVo pacienteVo = loginService.auth(email, senha);
                if (pacienteVo != null){
                    AndroidUtils.closeWaitDlg();
                    AndroidUtils.showMessageDlg("Sucesso","Olá " + pacienteVo.getNome(),getActivity());
                }

            } catch (Exception e) {
                AndroidUtils.closeWaitDlg();
                AndroidUtils.showMessageDlg("Erro",e.getMessage(),getActivity());
            }
        }
    }



}
