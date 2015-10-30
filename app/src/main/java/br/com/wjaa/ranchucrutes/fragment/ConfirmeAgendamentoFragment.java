package br.com.wjaa.ranchucrutes.fragment;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import javax.inject.Inject;

import br.com.wjaa.ranchucrutes.R;
import br.com.wjaa.ranchucrutes.activity.callback.DialogCallback;
import br.com.wjaa.ranchucrutes.exception.AgendamentoServiceException;
import br.com.wjaa.ranchucrutes.service.AgendamentoService;
import br.com.wjaa.ranchucrutes.utils.AndroidUtils;
import br.com.wjaa.ranchucrutes.vo.ConfirmarAgendamentoVo;
import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConfirmeAgendamentoFragment extends RoboFragment {


    @InjectView(R.id.txtVwCode)
    private TextView txtVwCode;

    @InjectView(R.id.btnConfirmar)
    private Button btnConfirmar;

    @Inject
    private AgendamentoService agendamentoService;

    private ConfirmarAgendamentoVo confirmarAgendamento;

    public ConfirmeAgendamentoFragment() {

    }

    public ConfirmeAgendamentoFragment(ConfirmarAgendamentoVo confirmarAgendamentoVo) {
        this.confirmarAgendamento = confirmarAgendamentoVo;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_confirme_agendamento, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        txtVwCode.setText(confirmarAgendamento.getCodigoConfirmacao());
        btnConfirmar.setOnClickListener(new ConfirmarAgendamentoClickListener());
    }

    class ConfirmarAgendamentoClickListener extends Thread implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            new ConfirmarAgendamentoClickListener().start();
        }

        @Override
        public void run() {
            try {
                agendamentoService.confirmarAgendamento(confirmarAgendamento);
                AndroidUtils.showMessageSuccessDlgOnUiThread("Agendamento solicitado com sucesso! \nVeja detalhes em Minhas Consultas.", getActivity(), new DialogCallback() {
                    @Override
                    public void confirm() {
                        getActivity().finish();
                    }

                    @Override
                    public void cancel() {

                    }
                });
            } catch (AgendamentoServiceException e) {
                AndroidUtils.showMessageErroDlgOnUiThread(e.getMessage(), getActivity());
            }
        }
    }

}
