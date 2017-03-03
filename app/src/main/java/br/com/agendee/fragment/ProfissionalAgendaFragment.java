package br.com.agendee.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ScrollView;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import br.com.agendee.activity.LoginActivity;
import br.com.agendee.activity.MeusDadosActivity;
import br.com.agendee.activity.callback.DialogCallback;
import br.com.agendee.buffer.RanchucrutesSession;
import br.com.agendee.entity.PacienteEntity;
import br.com.agendee.exception.AgendamentoServiceException;
import br.com.agendee.service.RanchucrutesConstants;
import br.com.agendee.utils.AndroidUtils;
import br.com.agendee.utils.DateUtils;
import br.com.agendee.utils.StringUtils;
import br.com.agendee.vo.ConfirmarAgendamentoVo;
import br.com.agendee.R;
import br.com.agendee.activity.ConfirmeAgendamentoActivity;
import br.com.agendee.service.AgendamentoService;
import br.com.agendee.vo.ProfissionalBasicoVo;
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

    @SuppressLint("ValidFragment")
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
        this.initButtons((ScrollView) view);
    }

    private void initButtons(ScrollView scrollView) {
        GridLayout gl = (GridLayout) scrollView.findViewById(R.id.grLyButtons);
        for (Date date : horarios){
            View v = LayoutInflater.from(this.getActivity()).inflate(R.layout.button_agendamento, null);
            Button b = (Button) v.findViewById(R.id.btnAgendamento);
            b.setText(DateUtils.formatHHmm(date));
            b.setOnClickListener(new BtnAgendarClickListener(date));
            gl.addView(v);
        }
        gl.requestLayout();
        scrollView.requestLayout();

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

            //verificando se paciente esta logado
            if (!RanchucrutesSession.isUsuarioLogado()){
                pacienteNaoLogado();
                return;
            }else{
                PacienteEntity paciente = RanchucrutesSession.getUsuario();
                if (StringUtils.isBlank(paciente.getSexo()) ||
                    StringUtils.isBlank(paciente.getCpf()) ||
                    StringUtils.isBlank(paciente.getDataAniversario()) ||
                    StringUtils.isBlank(paciente.getEmail()) ||
                    StringUtils.isBlank(paciente.getNome()) ||
                    StringUtils.isBlank(paciente.getNome()) ){
                    pacienteCadastroIncompleto();
                    return;
                }
            }


            confirmarAgendamento();

        }

        private void confirmarAgendamento() {
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

        private void pacienteNaoLogado() {
            AndroidUtils.showConfirmDlgOnUiThread("Usuário não autenticado",
                    "Para agendar uma consulta você precisa estar autenticado. \n Deseja se autenticar agora?",
                    getActivity(), new DialogCallback() {
                @Override
                public void confirm() {
                    AndroidUtils.openActivity(getActivity(), LoginActivity.class);
                }

                @Override
                public void cancel() {

                }
            });
        }

        private void pacienteCadastroIncompleto() {
            AndroidUtils.showConfirmDlgOnUiThread("Cadastro incompleto",
                    "Para agendar essa consulta você precisa completar o seu cadastro. \n Deseja completar agora?",
                    getActivity(), new DialogCallback() {
                        @Override
                        public void confirm() {
                            AndroidUtils.openActivity(getActivity(), MeusDadosActivity.class);
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
        AndroidUtils.openActivity(getActivity(),ConfirmeAgendamentoActivity.class, bundle, RanchucrutesConstants.FINISH_CONFIRME_AGENDAMENTO);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getActivity().finish();
    }
}
