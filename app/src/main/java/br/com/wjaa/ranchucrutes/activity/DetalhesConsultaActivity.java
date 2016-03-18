package br.com.wjaa.ranchucrutes.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.Marker;

import java.io.InputStream;
import java.net.URL;
import java.util.Date;

import br.com.wjaa.ranchucrutes.R;
import br.com.wjaa.ranchucrutes.service.RanchucrutesConstants;
import br.com.wjaa.ranchucrutes.utils.DateUtils;
import br.com.wjaa.ranchucrutes.vo.AgendamentoVo;
import br.com.wjaa.ranchucrutes.vo.ProfissionalBasicoVo;
import roboguice.activity.RoboActionBarActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

/**
 * @author Wagner Jeronimo
 */
@ContentView(R.layout.activity_detalhes_consulta)
public class DetalhesConsultaActivity extends RoboActionBarActivity {

    @InjectView(R.id.toolbar)
    private Toolbar toolbar;

    @InjectView(R.id.dcImgProfissional)
    private ImageView dcImgProfissional;

    @InjectView(R.id.dcLayoutStatus)
    private LinearLayout dcLayoutStatus;

    @InjectView(R.id.dcLayoutBtnCanCon)
    private LinearLayout dcLayoutBtnCanCon;

    @InjectView(R.id.dcTxtEsp)
    private TextView dcTxtEsp;

    @InjectView(R.id.dcTxtNome)
    private TextView dcTxtNome;

    @InjectView(R.id.dcTxtNumero)
    private TextView dcTxtNumero;

    @InjectView(R.id.dcTxtEnd)
    private TextView dcTxtEnd;

    @InjectView(R.id.dcTxtTel)
    private TextView dcTxtTel;

    @InjectView(R.id.dcTxtDataConsulta)
    private TextView dcTxtDataConsulta;

    @InjectView(R.id.dcTxtStatus)
    private TextView dcTxtStatus;

    @InjectView(R.id.dcBtnReagendar)
    private Button dcBtnReagendar;

    @InjectView(R.id.dcBtnConfirmarSolicitacao)
    private Button dcBtnConfirmarSolicitacao;

    @InjectView(R.id.dcBtnCancelar)
    private Button dcBtnCancelar;

    @InjectView(R.id.dcBtnConfirmarConsulta)
    private Button dcBtnConfirmarConsulta;

    private AgendamentoVo agendamentoVo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initToolbar();
        Bundle extras = getIntent().getExtras();
        if (extras != null){
            agendamentoVo = (AgendamentoVo) extras.getSerializable(RanchucrutesConstants.PARAM_AGENDAMENTO);
        }
        this.updateView();
    }

    private void updateView() {
        if (agendamentoVo != null){
            new LoadImage(agendamentoVo.getProfissional()).start();
            dcTxtDataConsulta.setText(DateUtils.formatddMMyyyyHHmm(agendamentoVo.getDataAgendamento()));
            dcTxtNome.setText(agendamentoVo.getProfissional().getNome());
            dcTxtEnd.setText(agendamentoVo.getProfissional().getEndereco());
            dcTxtNumero.setText("CRM: " + agendamentoVo.getProfissional().getCrm());
            dcTxtTel.setText(agendamentoVo.getProfissional().getTelefone());
            dcTxtEsp.setText(agendamentoVo.getProfissional().getEspec());
            String statusConsulta = "Você não confirmou essa solicitação.";
            int colorStatus = R.color.warningTextColor;
            if (agendamentoVo.getCancelado()){
                statusConsulta = "Essa consulta foi cancelada!";
                colorStatus = R.color.errorTextColor;
                dcBtnReagendar.setVisibility(View.VISIBLE);
            }else{
                if (agendamentoVo.getFinalizado()){
                    statusConsulta = "Consulta realizada!";
                    colorStatus = R.color.successTextColor;
                    dcBtnReagendar.setVisibility(View.VISIBLE);
                }else if (agendamentoVo.getDataConfirmacaoConsulta() != null){
                    statusConsulta = "Consulta confirmada!";
                    colorStatus = R.color.successTextColor;
                    dcBtnCancelar.setVisibility(View.VISIBLE);
                }else  if (agendamentoVo.getDataConfirmacaoProfissional() != null){
                    statusConsulta = "Solicitação confirmada pelo profissional.";
                    colorStatus = R.color.successTextColor;
                    dcLayoutBtnCanCon.setVisibility(View.VISIBLE);
                    dcBtnConfirmarConsulta.setVisibility(View.INVISIBLE);
                    if (DateUtils.diffInDays(new Date(), agendamentoVo.getDataAgendamento()) <= 2 ){
                        dcBtnConfirmarConsulta.setVisibility(View.VISIBLE);
                    }
                }else  if (agendamentoVo.getDataConfirmacao() != null){
                    statusConsulta = "Essa consulta está aguardando a confirmação do profissional.";
                    colorStatus = R.color.warningTextColor;
                    dcLayoutBtnCanCon.setVisibility(View.VISIBLE);
                    dcBtnCancelar.setVisibility(View.VISIBLE);
                    dcBtnConfirmarConsulta.setVisibility(View.INVISIBLE);

                }else{
                    dcBtnConfirmarSolicitacao.setVisibility(View.VISIBLE);
                }
            }
            dcTxtStatus.setText(statusConsulta);
            dcLayoutStatus.setBackgroundColor(this.getResources().getColor(colorStatus));


        } else {
            this.clearView(dcTxtDataConsulta,dcTxtEnd,dcTxtNome,dcTxtNumero,dcTxtStatus,dcTxtTel,dcTxtEsp);
        }

    }
    private void clearView(TextView ... t ){
        for (TextView tv : t) {
            tv.setText("-");
        }
    }

    private void initToolbar() {

        if (toolbar != null) {
            this.toolbar.setTitle("Destalhes da Consulta");
            setSupportActionBar(toolbar);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
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


    class LoadImage extends Thread{
        private ProfissionalBasicoVo profissional;
        private Bitmap bitmap = null;

        LoadImage(ProfissionalBasicoVo profissional){
            this.profissional = profissional;

        }

        @Override
        public void run() {

            String imageUrl = "http://agendee.com.br/f/" + profissional.getCrm() + ".jpg";
            try {
                bitmap = BitmapFactory.decodeStream((InputStream) new URL(imageUrl).getContent());
                DetalhesConsultaActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dcImgProfissional.setImageBitmap(bitmap);
                        dcImgProfissional.invalidate();

                    }
                });
            } catch (Exception e) {
                Log.e("DestalhesConsulta", "Erro ao buscar a imagem", e);
                DetalhesConsultaActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dcImgProfissional.setImageResource(R.drawable.unknow);
                        dcImgProfissional.invalidate();
                    }
                });

            }

        }
    }

}
