package br.com.agendee.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.inject.Inject;

import br.com.agendee.R;
import br.com.agendee.activity.callback.DialogCallback;
import br.com.agendee.commons.AuthType;
import br.com.agendee.exception.NovoPacienteException;
import br.com.agendee.service.LoginService;
import br.com.agendee.utils.AndroidSystemUtil;
import br.com.agendee.utils.AndroidUtils;
import br.com.agendee.utils.StringUtils;
import br.com.agendee.vo.PacienteVo;
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

    @Inject
    private LoginService loginService;

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
            AndroidUtils.showMessageErroDlg("O nome é obrigatório!", this);
            edtNome.requestFocus();
            return false;
        }
        if (StringUtils.isBlank(edtEmail.getText().toString())){
            AndroidUtils.showMessageErroDlg("O email é obrigatório!", this);
            edtEmail.requestFocus();
            return false;
        }

        /* Entra aqui apenas quando for um cadastro direto. */
        if (pacienteRedeSocial == null){
            if (StringUtils.isBlank(edtSenha.getText().toString())){
                AndroidUtils.showMessageErroDlg("A senha é obrigatória!", this);
                edtSenha.requestFocus();
                return false;
            }

            if (StringUtils.isBlank(edtConfSenha.getText().toString())){
                AndroidUtils.showMessageErroDlg("Confirme sua senha", this);
                edtConfSenha.requestFocus();
                return false;
            }

            if (edtConfSenha.getText().toString().equals(edtSenha.getText())){
                AndroidUtils.showMessageErroDlg("Confirmação de senha inválida!", this);
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
            pacienteVo.setAuthType(AuthType.AUTH_RANCHUCRUTES);
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
            try {
                String regId = AndroidSystemUtil.getRegistrationId(NovoPacienteActivity.this);
                pacienteVo.setKeyDeviceGcm(regId);
                loginService.criarPaciente(pacienteVo);
                AndroidUtils.closeWaitDlg();
                AndroidUtils.showMessageSuccessDlgOnUiThread("Cadastro criado com sucesso!", NovoPacienteActivity.this, new DialogCallback() {
                    @Override
                    public void confirm() {
                        finish();
                    }

                    @Override
                    public void cancel() {

                    }
                });
            } catch (NovoPacienteException e) {
                Log.e(NovoPacienteActivity.class.getSimpleName(),"Erro ao criar um novo paciente", e);
                AndroidUtils.closeWaitDlg();
                AndroidUtils.showMessageErroDlgOnUiThread(e.getMessage(), NovoPacienteActivity.this);
            }
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
