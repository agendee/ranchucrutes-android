package br.com.wjaa.ranchucrutes.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import br.com.wjaa.ranchucrutes.R;
import br.com.wjaa.ranchucrutes.activity.callback.DialogCallback;
import br.com.wjaa.ranchucrutes.exception.RestException;
import br.com.wjaa.ranchucrutes.exception.RestRequestUnstable;
import br.com.wjaa.ranchucrutes.exception.RestResponseUnsatisfiedException;
import br.com.wjaa.ranchucrutes.form.LoginForm;
import br.com.wjaa.ranchucrutes.rest.RestUtils;
import br.com.wjaa.ranchucrutes.service.RanchucrutesConstants;
import br.com.wjaa.ranchucrutes.utils.AndroidUtils;
import br.com.wjaa.ranchucrutes.utils.ObjectUtils;
import br.com.wjaa.ranchucrutes.utils.StringUtils;
import br.com.wjaa.ranchucrutes.vo.PacienteVo;
import roboguice.activity.RoboActionBarActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

/**
 * Created by wagner on 10/09/15.
 */
@ContentView(R.layout.activity_novo_paciente)
public class NovoPacienteActivity extends RoboActionBarActivity {



    @InjectView(R.id.toolbar)
    private Toolbar toolbar;

    @InjectView(R.id.edtCadEmail)
    private EditText edtEmail;

    @InjectView(R.id.edtCadCelular)
    private EditText edtCelular;

    @InjectView(R.id.edtCadNome)
    private EditText edtNome;

    @InjectView(R.id.edtCadSenha)
    private EditText edtSenha;

    @InjectView(R.id.edtCadConfSenha)
    private EditText edtConfSenha;

    @InjectView(R.id.btnCadSave)
    private Button btnSave;

    private PacienteVo pacienteRedeSocial;
    private boolean cadastroRedeSocial = false;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            this.pacienteRedeSocial = (PacienteVo) bundle.get("paciente");
        }

        this.init();

        if (this.pacienteRedeSocial != null){
            this.cadastroRedeSocial = true;
            this.initFacebook();
        }

    }

    private void initFacebook() {
        this.edtSenha.setVisibility(View.GONE);
        this.edtConfSenha.setVisibility(View.GONE);
        this.edtNome.setText(pacienteRedeSocial.getNome());
        this.edtEmail.setText(pacienteRedeSocial.getEmail());
    }

    private void init() {
        this.initToolbar();
        this.initEvents();
    }

    private void initToolbar() {

        if (toolbar != null) {
            this.toolbar.setTitle("Novo Paciente");
            setSupportActionBar(toolbar);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void initEvents() {
        this.btnSave.setOnClickListener(new BtnSaveClickListener());
    }


    private boolean validateForm() {
        if (StringUtils.isBlank(edtNome.getText().toString())){
            AndroidUtils.showMessageDlg("Ops!","O nome é obrigatório!",this);
            edtNome.requestFocus();
            return false;
        }
        if (StringUtils.isBlank(edtCelular.getText().toString())){
            AndroidUtils.showMessageDlg("Ops!","O celular é obrigatório!",this);
            edtCelular.requestFocus();
            return false;
        }
        if (StringUtils.isBlank(edtEmail.getText().toString())){
            AndroidUtils.showMessageDlg("Ops!","O email é obrigatório!",this);
            edtEmail.requestFocus();
            return false;
        }

        /* Entra aqui apenas quando for um cadastro direto. */
        if (pacienteRedeSocial == null){
            if (StringUtils.isBlank(edtSenha.getText().toString())){
                AndroidUtils.showMessageDlg("Ops!","A senha é obrigatória!",this);
                edtSenha.requestFocus();
                return false;
            }

            if (StringUtils.isBlank(edtConfSenha.getText().toString())){
                AndroidUtils.showMessageDlg("Ops!","Confirme sua senha",this);
                edtConfSenha.requestFocus();
                return false;
            }

            if (edtConfSenha.getText().toString().equals(edtSenha.getText())){
                AndroidUtils.showMessageDlg("Ops!", "Confirmação de senha inválida!", this);
                edtConfSenha.requestFocus();
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        /*if (id == R.id.action_settings) {
            return true;
        }
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }*/
        return super.onOptionsItemSelected(item);
    }

    class BtnSaveClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            if (validateForm()){
                PacienteVo pacienteVo = getPacienteVo();
                new SubmitterFormThread(pacienteVo).start();
            }
        }

    }

    private PacienteVo getPacienteVo() {
        PacienteVo pacienteVo = cadastroRedeSocial ? this.pacienteRedeSocial : new PacienteVo();

        pacienteVo.setEmail(edtEmail.getText().toString());
        pacienteVo.setNome(edtNome.getText().toString());
        pacienteVo.setTelefone(edtCelular.getText().toString());

        if (!cadastroRedeSocial){
            pacienteVo.setAuthType(LoginForm.AuthType.AUTH_RANCHUCRUTES);
            pacienteVo.setSenha(edtSenha.getText().toString());
        }

        return pacienteVo;
    }

    class SubmitterFormThread extends Thread{

        private PacienteVo pacienteVo;
        private SubmitterFormThread(PacienteVo pacienteVo){
            this.pacienteVo = pacienteVo;
        }

        @Override
        public void run() {
            AndroidUtils.showWaitDlgOnUiThread("Aguarde enviando dados",  NovoPacienteActivity.this);
            String json = ObjectUtils.toJson(pacienteVo);
            try {
                RestUtils.postJson(PacienteVo.class, RanchucrutesConstants.WS_HOST, RanchucrutesConstants.END_POINT_SALVAR_PACIENTE,json);
                AndroidUtils.closeWaitDlg();
                AndroidUtils.showMessageDlgOnUiThread("Sucesso!", "Cadastro criado com sucesso!", NovoPacienteActivity.this, new DialogCallback() {
                    @Override
                    public void confirm() {
                        //TODO ARRUMAR ESSA CHADA AQUI ESTÁ MUITO FEIA
                        ((MainActivity)getApplicationContext()).displayView(0);
                    }

                    @Override
                    public void cancel() {

                    }
                });

                //TODO AQUI REDIRECIONAR PARA O ACTIVITY QUE CHAMOU O CADASTRO OU DAR UM BACK
                //CRIAR UM SHOWMESSAGE COM PARAMETRO DE CALLBACK...PARA RECEBER O CLICK DO BOTAO.

            } catch (RestResponseUnsatisfiedException | RestRequestUnstable e) {
                Log.e(NovoPacienteActivity.class.getSimpleName(), "Erro ao criar um novo paciente", e);
                AndroidUtils.closeWaitDlg();
                AndroidUtils.showMessageDlgOnUiThread("Ops!", e.getMessage(), NovoPacienteActivity.this);
            } catch (RestException e) {
                Log.e(NovoPacienteActivity.class.getSimpleName(),"Erro ao criar um novo paciente", e);
                AndroidUtils.closeWaitDlg();
                AndroidUtils.showMessageDlgOnUiThread("Ops!",e.getErrorMessage().getErrorMessage(),NovoPacienteActivity.this);
            }
        }
    }

}
