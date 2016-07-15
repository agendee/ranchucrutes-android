package br.com.wjaa.ranchucrutes.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.inject.Inject;

import java.util.Date;

import br.com.wjaa.ranchucrutes.R;
import br.com.wjaa.ranchucrutes.activity.callback.DialogCallback;
import br.com.wjaa.ranchucrutes.exception.AgendamentoServiceException;
import br.com.wjaa.ranchucrutes.helper.DetalhesProfissionalHelper;
import br.com.wjaa.ranchucrutes.service.AgendamentoService;
import br.com.wjaa.ranchucrutes.service.RanchucrutesConstants;
import br.com.wjaa.ranchucrutes.utils.AndroidUtils;
import br.com.wjaa.ranchucrutes.utils.DateUtils;
import br.com.wjaa.ranchucrutes.view.SlidingTabLayout;
import br.com.wjaa.ranchucrutes.vo.AgendamentoVo;
import br.com.wjaa.ranchucrutes.vo.ConfirmarAgendamentoVo;
import roboguice.activity.RoboActionBarActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

/**
 * @author Wagner Jeronimo
 */
@ContentView(R.layout.activity_detalhes_profissional)
public class DetalhesConsultaActivity extends RoboActionBarActivity {

    @InjectView(R.id.toolbar)
    private Toolbar toolbar;

    @InjectView(R.id.collapsing_toolbar)
    private CollapsingToolbarLayout mCollapsingToolbarLayout;

    @InjectView(R.id.tabLayout)
    private SlidingTabLayout tabLayout;

    @InjectView(R.id.frameInfoBotom)
    private FrameLayout frameInfoBotom;

    @InjectView(R.id.vpTabs)
    private ViewPager viewPager;

    @InjectView(R.id.fab)
    private FloatingActionButton fab;

    private AgendamentoVo agendamentoVo;

    @Inject
    private AgendamentoService agendamentoService;

    private boolean pause = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Fresco.initialize(this);
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if (extras != null){
            agendamentoVo = (AgendamentoVo) extras.getSerializable(RanchucrutesConstants.PARAM_AGENDAMENTO);
        }
        this.updateView();
    }

    private void updateView() {
        if (agendamentoVo != null){
            DetalhesProfissionalHelper.build(this,"Detalhes",mCollapsingToolbarLayout,toolbar,agendamentoVo.getProfissional());
            updateDetalhesConsulta();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        pause = true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        //só entrará aqui se a activity foi parada para abrir outra activity
        if (pause){
            new Thread(){
                @Override
                public void run() {
                    try {
                        AndroidUtils.showWaitDlgOnUiThread("Atualizando dados...",DetalhesConsultaActivity.this);
                        agendamentoVo = agendamentoService.getAgendamentoById(agendamentoVo.getId());
                        updateDetalhesConsulta();
                        AndroidUtils.closeWaitDlg();
                    } catch (AgendamentoServiceException e) {
                        AndroidUtils.closeWaitDlg();
                        e.printStackTrace();
                    }
                }
            }.start();

        }


        pause = false;
    }

    private void updateDetalhesConsulta() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                View v = LayoutInflater.from(DetalhesConsultaActivity.this).inflate(R.layout.fragment_detalhes_consulta,frameInfoBotom, false);
                fab.setVisibility(View.INVISIBLE);
                tabLayout.setVisibility(View.INVISIBLE);
                viewPager.setVisibility(View.INVISIBLE);
                frameInfoBotom.setVisibility(View.VISIBLE);
                frameInfoBotom.removeAllViews();
                frameInfoBotom.addView(v);


                TextView dcTxtDataConsulta = (TextView) v.findViewById(R.id.dcTxtDataConsulta);
                dcTxtDataConsulta.setText(DateUtils.formatddMMyyyyHHmm(agendamentoVo.getDataAgendamento()));

                TextView dcTxtStatus = (TextView) v.findViewById(R.id.dcTxtStatus);


                Button dcBtnReagendar = (Button) v.findViewById(R.id.dcBtnReagendar);
                Button dcBtnCancelar = (Button) v.findViewById(R.id.dcBtnCancelar);
                Button dcBtnConfirmarConsulta = (Button) v.findViewById(R.id.dcBtnConfirmarConsulta);
                Button dcBtnConfirmarSolicitacao = (Button) v.findViewById(R.id.dcBtnConfirmarSolicitacao);

                addActionListener(dcBtnCancelar,dcBtnConfirmarConsulta,dcBtnConfirmarSolicitacao,dcBtnReagendar);


                LinearLayout dcLayoutStatus = (LinearLayout) v.findViewById(R.id.dcLayoutStatus);

                String statusConsulta = "está aguardando a sua confirmação.";
                int colorStatus = R.color.warningTextColorLigth;
                if (agendamentoVo.getCancelado()){
                    statusConsulta = "foi cancelada!";
                    colorStatus = R.color.errorTextColorLigth;

                    dcBtnReagendar.setVisibility(View.VISIBLE);
                }else{
                    if (agendamentoVo.getFinalizado()){
                        statusConsulta = "foi realizada!";
                        colorStatus = R.color.successTextColorLigth;

                        dcBtnReagendar.setVisibility(View.VISIBLE);
                    }else if (agendamentoVo.getDataConfirmacaoConsulta() != null){
                        statusConsulta = "foi confirmada!";
                        colorStatus = R.color.successTextColorLigth;
                        dcBtnCancelar.setVisibility(View.VISIBLE);
                    }else  if (agendamentoVo.getDataConfirmacaoProfissional() != null){
                        statusConsulta = "foi confirmada pelo profissional.";
                        colorStatus = R.color.successTextColorLigth;
                        dcBtnCancelar.setVisibility(View.VISIBLE);
                        /*if (DateUtils.diffInDays(new Date(), agendamentoVo.getDataAgendamento()) <= 2 ){
                            dcBtnConfirmarConsulta.setVisibility(View.VISIBLE);
                        }*/
                    }else  if (agendamentoVo.getDataConfirmacao() != null){
                        statusConsulta = "está aguardando a confirmação do profissional.";
                        colorStatus = R.color.warningTextColorLigth;
                        dcBtnCancelar.setVisibility(View.VISIBLE);
                        //dcBtnConfirmarConsulta.setVisibility(View.INVISIBLE);

                    }else{
                        dcBtnConfirmarSolicitacao.setVisibility(View.VISIBLE);
                    }
                }
                dcTxtStatus.setText(statusConsulta);
                dcLayoutStatus.setBackgroundColor(v.getResources().getColor(colorStatus));


            }

        });
    }


    private void addActionListener(Button ... btn) {
        for(Button b : btn){

            switch (b.getId()){
                case R.id.dcBtnReagendar : {
                    b.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Bundle bundle = new Bundle();
                            bundle.putSerializable(RanchucrutesConstants.PARAM_PROFISSIONAL,agendamentoVo.getProfissional());
                            AndroidUtils.openActivity(DetalhesConsultaActivity.this,AgendamentoActivity.class, bundle, RanchucrutesConstants.FINISH_CONFIRME_AGENDAMENTO_OPEN_LIST);
                        }
                    });
                    break;
                }
                case R.id.dcBtnCancelar : {
                    b.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AndroidUtils.showConfirmDlg("Confirmação", "Deseja realmente cancelar esse agendamento?", DetalhesConsultaActivity.this, new DialogCallback() {
                                @Override
                                public void confirm() {
                                    new Thread(){
                                        @Override
                                        public void run() {
                                            try {
                                                agendamentoVo = agendamentoService.cancelarAgendamento(agendamentoVo.getId());
                                                updateDetalhesConsulta();
                                                AndroidUtils.showMessageSuccessDlgOnUiThread("Agendamento cancelado com sucesso!",DetalhesConsultaActivity.this, new DialogCallback() {
                                                    @Override
                                                    public void confirm() {
                                                        finish();
                                                    }

                                                    @Override
                                                    public void cancel() {

                                                    }
                                                });


                                            } catch (AgendamentoServiceException e) {
                                                AndroidUtils.showMessageErroDlgOnUiThread(e.getMessage(),DetalhesConsultaActivity.this);
                                            }
                                        }
                                    }.start();
                                }

                                @Override
                                public void cancel() {

                                }
                            });

                        }
                    });
                    break;
                }
                case R.id.dcBtnConfirmarConsulta : {
                    b.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });
                    break;
                }
                case R.id.dcBtnConfirmarSolicitacao : {
                    b.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ConfirmarAgendamentoVo confirmar = new ConfirmarAgendamentoVo();
                            confirmar.setAgendamentoVo(agendamentoVo);
                            confirmar.setCodigoConfirmacao(agendamentoVo.getCodigoConfirmacao());
                            Bundle bundle = new Bundle();
                            bundle.putSerializable(RanchucrutesConstants.PARAM_CONFIMAR_AGENDAMENTO,confirmar);
                            AndroidUtils.openActivity(DetalhesConsultaActivity.this,ConfirmeAgendamentoActivity.class, bundle, RanchucrutesConstants.FINISH_CONFIRME_AGENDAMENTO);
                        }
                    });
                    break;
                }
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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RanchucrutesConstants.FINISH_CONFIRME_AGENDAMENTO ||
                requestCode == RanchucrutesConstants.FINISH_CONFIRME_AGENDAMENTO_OPEN_LIST){
            finish();
        }


    }


}
