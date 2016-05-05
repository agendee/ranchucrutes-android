package br.com.wjaa.ranchucrutes.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.LinearLayout;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import br.com.wjaa.ranchucrutes.R;
import br.com.wjaa.ranchucrutes.activity.ConfirmeAgendamentoActivity;
import br.com.wjaa.ranchucrutes.activity.callback.DialogCallback;
import br.com.wjaa.ranchucrutes.buffer.RanchucrutesSession;
import br.com.wjaa.ranchucrutes.exception.AgendamentoServiceException;
import br.com.wjaa.ranchucrutes.service.AgendamentoService;
import br.com.wjaa.ranchucrutes.service.RanchucrutesConstants;
import br.com.wjaa.ranchucrutes.utils.AndroidUtils;
import br.com.wjaa.ranchucrutes.utils.DateUtils;
import br.com.wjaa.ranchucrutes.vo.ConfirmarAgendamentoVo;
import br.com.wjaa.ranchucrutes.vo.ProfissionalBasicoVo;
import roboguice.RoboGuice;
import roboguice.fragment.RoboFragment;

/**
 *
 */
public class ProfissionalAgendaFragment extends RoboFragment {

    private String title;
    private List<Date> horarios;
    private ProfissionalBasicoVo profissional;
    private AgendamentoService agendamentoService;
    private Context context;

    public ProfissionalAgendaFragment(){

    }

    public ProfissionalAgendaFragment(String title, List<Date> hs, Context context, ProfissionalBasicoVo profissionalBasicoVo) {
        this.title = title;
        Collections.sort(hs);
        this.horarios = hs;
        this.context = context;
        this.profissional = profissionalBasicoVo;
        this.agendamentoService = RoboGuice.getInjector(this.context).getInstance(AgendamentoService.class);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profissional_agenda, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.initButtons((GridLayout) view);
    }

    private void initButtons(GridLayout gridLayout) {
        for (Date date : horarios){
            Button b = new Button(gridLayout.getContext());

           GridLayout.MarginLayoutParams lp = new GridLayout.MarginLayoutParams(GridLayout.MarginLayoutParams.WRAP_CONTENT,
                   GridLayout.MarginLayoutParams.WRAP_CONTENT);
            lp.setMargins(20, 20, 20, 20);
            /*LinearLayout l = new LinearLayout(gridLayout.getContext());
            LinearLayout.LayoutParams linearParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
            linearParam.setMargins(30,30,30,20);
            l.setLayoutParams(linearParam);*/
            //todo ver isso aqui se funciona nas outras versoes tirada por causa do tablet
            //b.setLayoutParams(lp);
            b.setText(DateUtils.formatHHmm(date));
            //b.setBackgroundColor(android.R.color.holo_green_dark);
            b.setOnClickListener(new BtnAgendarClickListener(date));
            b.requestLayout();
            b.setTextColor(getResources().getColor(android.R.color.background_dark));
            //l.addView(b);
            //l.requestLayout();
            gridLayout.addView(b);
        }
        gridLayout.requestLayout();

    }


    public String getTitle(){
        return this.title;
    }


    class BtnAgendarClickListener extends Thread implements View.OnClickListener{

        private Date horario;
        BtnAgendarClickListener(Date horario){
            this.horario = horario;
        }

        @Override
        public void onClick(View v) {
            AndroidUtils.showConfirmDlgOnUiThread("Confirmação", "Você quer agendar uma consulta com " +
                    profissional.getNome() + " no dia " + DateUtils.formatddMMyyyy(horario)  + " as " + DateUtils.formatHHmm(horario) + " ?",
                    getActivity(), new DialogCallback() {
                @Override
                public void confirm() {
                    new BtnAgendarClickListener(horario).start();
                }

                @Override
                public void cancel() {

                }
            });

        }


        @Override
        public void run() {
            try {

                AndroidUtils.showWaitDlgOnUiThread("Aguarde, tentando realizar o agendamento...", (Activity) context);
                final ConfirmarAgendamentoVo confirmarAgendamentoVo = agendamentoService.criarAgendamento(profissional.getId(), profissional.getIdClinicaAtual(),
                        new Long(RanchucrutesSession.getUsuario().getId()), horario, false);

                AndroidUtils.closeWaitDlg();

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        openConfirmeAgendamentoFragment(confirmarAgendamentoVo);
                    }
                });


            } catch (AgendamentoServiceException e) {
                AndroidUtils.closeWaitDlg();
                AndroidUtils.showMessageErroDlgOnUiThread(e.getMessage(), (Activity) context);
            }
        }
    }


    private void openConfirmeAgendamentoFragment(ConfirmarAgendamentoVo confirmarAgendamentoVo) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(RanchucrutesConstants.PARAM_CONFIMAR_AGENDAMENTO, confirmarAgendamentoVo);
        AndroidUtils.openActivity(getActivity(),ConfirmeAgendamentoActivity.class, bundle);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getActivity().finish();
    }
}
