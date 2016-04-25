package br.com.wjaa.ranchucrutes.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.inject.Inject;

import br.com.wjaa.ranchucrutes.R;
import br.com.wjaa.ranchucrutes.activity.callback.DialogCallback;
import br.com.wjaa.ranchucrutes.service.FacebookService;
import br.com.wjaa.ranchucrutes.service.GPlusService;
import br.com.wjaa.ranchucrutes.service.LoginService;
import br.com.wjaa.ranchucrutes.utils.AndroidUtils;
import br.com.wjaa.ranchucrutes.utils.StringUtils;
import br.com.wjaa.ranchucrutes.vo.PacienteVo;
import roboguice.activity.RoboActionBarActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

/**
 *
 */
@ContentView(R.layout.fragment_login)
public class LoginActivity extends RoboActionBarActivity {

    @InjectView(R.id.toolbar)
    private Toolbar toolbar;

    @Inject
    private FacebookService facebookService;

    @Inject
    private GPlusService gPlusService;

    @Inject
    private LoginService loginService;

    @InjectView(R.id.btnEntrar)
    private Button btnEntrar;

    @InjectView(R.id.btnFacebook)
    private Button btnFacebook;

    @InjectView(R.id.btnGPlus)
    private Button btnGPlus;

    @InjectView(R.id.txtNovoPaciente)
    private TextView txtNovoPaciente;

    @InjectView(R.id.edtLoginEmail)
    private EditText edtLoginEmail;

    @InjectView(R.id.edtLoginPass)
    private EditText edtLoginPass;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();


    }

    private void init() {
        facebookService.onCreate(this);
        gPlusService.onCreate(this);
        initToolbar();
        initButtons();
    }


    @Override
    protected void onStart() {
        super.onStart();
        AndroidUtils.closeWaitDlg();
        facebookService.onStart();
        gPlusService.onStart();
    }


    private void initToolbar() {

        if (toolbar != null) {
            this.toolbar.setTitle("Fazer Login");
            setSupportActionBar(toolbar);
           // getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
           // getSupportActionBar().
//            getSupportActionBar().setDisplayShowHomeEnabled(true);

        }
    }



    private void initButtons() {

        txtNovoPaciente.setText(Html.fromHtml("<u>" + getString(R.string.txtNovoPaciente) + "</u>"));
        txtNovoPaciente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AndroidUtils.openActivity(LoginActivity.this, NovoPacienteActivity.class);
            }
        });

        btnEntrar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String email = edtLoginEmail.getText().toString();
                String pass = edtLoginPass.getText().toString();
                if (StringUtils.isBlank(email)) {
                    AndroidUtils.showMessageErroDlg("Preencha seu email.", LoginActivity.this);
                    return;
                }
                if (StringUtils.isBlank(pass)) {
                    AndroidUtils.showMessageErroDlg("Preencha sua senha.", LoginActivity.this);
                    return;
                }

                new LogarUsuario(email, pass).start();

            }
        });
        btnFacebook.setOnClickListener(facebookService);
        btnGPlus.setOnClickListener(gPlusService);
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
        gPlusService.onStop();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //TODO ENTENDER MELHOR O LANCE DO REQUESTCODE.
        if (requestCode == 64206 ){
            facebookService.onActivityResult(requestCode, resultCode, data);
        }else{
            gPlusService.onActivityResult(requestCode, resultCode, data);
        }
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
                AndroidUtils.showWaitDlgOnUiThread("Aguarde, autenticando usuário", LoginActivity.this);
                PacienteVo pacienteVo = loginService.auth(email, senha);
                loginService.registerKeyDevice(LoginActivity.this, pacienteVo.getKeyDeviceGcm());
                AndroidUtils.closeWaitDlg();
                saudarSair(pacienteVo);

            } catch (Exception e) {
                Log.e("LoginFrament",e.getMessage(),e);
                AndroidUtils.closeWaitDlg();
                AndroidUtils.showMessageErroDlgOnUiThread(e.getMessage(), LoginActivity.this);
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_actions, menu);
        return true;
    }

    private void saudarSair(PacienteVo pacienteVo) {
        if (pacienteVo != null){
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(LoginActivity.this, "Paciente conectado!", Toast.LENGTH_LONG).show();
                }
            });


            AndroidUtils.showMessageSuccessDlgOnUiThread("Olá " + pacienteVo.getNome(), this, new DialogCallback() {

                @Override
                public void confirm() {
                    LoginActivity.this.finish();
                }

                @Override
                public void cancel() {

                }
            });

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == android.R.id.home){
            finish();
        }
        return true;
    }
}
