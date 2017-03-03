package br.com.agendee.activity;


import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import javax.inject.Inject;

import br.com.agendee.activity.callback.DialogCallback;
import br.com.agendee.exception.AgendamentoServiceException;
import br.com.agendee.service.RanchucrutesConstants;
import br.com.agendee.utils.AndroidUtils;
import br.com.agendee.vo.ConfirmarAgendamentoVo;
import br.com.agendee.R;
import br.com.agendee.service.AgendamentoService;
import roboguice.activity.RoboActionBarActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

/**
 * A simple {@link Fragment} subclass.
 */
@ContentView(R.layout.fragment_confirme_agendamento)
public class ConfirmeAgendamentoActivity extends RoboActionBarActivity {


    @InjectView(R.id.txtVwCode)
    private TextView txtVwCode;

    @InjectView(R.id.edtConfirmeCode)
    private EditText edtConfirmeCode;

    @InjectView(R.id.btnConfirmar)
    private Button btnConfirmar;

    @Inject
    private AgendamentoService agendamentoService;

    private ConfirmarAgendamentoVo confirmarAgendamento;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        confirmarAgendamento = (ConfirmarAgendamentoVo) getIntent().getExtras()
                .get(RanchucrutesConstants.PARAM_CONFIMAR_AGENDAMENTO);

        txtVwCode.setText(confirmarAgendamento.getCodigoConfirmacao());
        btnConfirmar.setOnClickListener(new ConfirmarAgendamentoClickListener());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_activity_confirme_agendamento);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

    }

    class ConfirmarAgendamentoClickListener extends Thread implements View.OnClickListener{

        @Override
        public void onClick(View v) {

            if ( !confirmarAgendamento.getCodigoConfirmacao().equalsIgnoreCase(edtConfirmeCode.getText().toString()) ){
                AndroidUtils.showMessageErroDlg("Código de confirmação inválido!", ConfirmeAgendamentoActivity.this, new DialogCallback() {
                    @Override
                    public void confirm() {
                        edtConfirmeCode.requestFocus();
                    }

                    @Override
                    public void cancel() {

                    }
                });

            }else{
                new ConfirmarAgendamentoClickListener().start();
            }

        }

        @Override
        public void run() {
            try {
                AndroidUtils.showWaitDlgOnUiThread("Aguarde, enviando confirmação...", ConfirmeAgendamentoActivity.this);
                agendamentoService.confirmarSolicitacao(confirmarAgendamento.getAgendamentoVo().getId(), edtConfirmeCode.getText().toString());
                AndroidUtils.closeWaitDlg();
                AndroidUtils.showMessageSuccessDlgOnUiThread("Agendamento solicitado com sucesso! \n Veja detalhes em Minhas Consultas.",
                        ConfirmeAgendamentoActivity.this, new DialogCallback() {
                    @Override
                    public void confirm() {
                        finish();
                    }

                    @Override
                    public void cancel() {

                    }
                });
            } catch (AgendamentoServiceException e) {
                AndroidUtils.closeWaitDlg();
                AndroidUtils.showMessageErroDlgOnUiThread(e.getMessage(), ConfirmeAgendamentoActivity.this);
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
