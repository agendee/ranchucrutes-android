package br.com.wjaa.ranchucrutes.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.google.inject.Inject;

import br.com.wjaa.ranchucrutes.adapter.MinhasConsultasListAdapter;
import br.com.wjaa.ranchucrutes.buffer.RanchucrutesSession;
import br.com.wjaa.ranchucrutes.exception.AgendamentoServiceException;
import br.com.wjaa.ranchucrutes.service.AgendamentoService;
import br.com.wjaa.ranchucrutes.utils.AndroidUtils;
import br.com.wjaa.ranchucrutes.vo.AgendamentoVo;
import roboguice.fragment.RoboListFragment;

/**
 *
 */
public class MinhasConsultasFragment extends RoboListFragment {


    @Inject
    private AgendamentoService agendamentoService;


    public MinhasConsultasFragment() {
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new FindAgendamentos().start();
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        AgendamentoVo vo = (AgendamentoVo) l.getItemAtPosition(position);
        //AndroidUtils.showWaitDlg("Detalhes do profissional " + vo.getProfissional().getNome(), getActivity());

    }

    class FindAgendamentos extends Thread{
        public FindAgendamentos() {
        }

        @Override
        public void run() {
            try{
                if (RanchucrutesSession.isUsuarioLogado()){
                    Integer idPaciente = RanchucrutesSession.getUsuario().getId();
                    AndroidUtils.showWaitDlgOnUiThread("Aguarde carregando agendamentos...", getActivity());
                    final AgendamentoVo[] agendamentoVos = agendamentoService.getAgendamentos(idPaciente.longValue());
                    if (agendamentoVos != null){
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setListAdapter(new MinhasConsultasListAdapter(getActivity(),agendamentoVos));
                            }
                        });

                    }
                    AndroidUtils.closeWaitDlg();
                }
            }catch (AgendamentoServiceException e) {
                AndroidUtils.closeWaitDlg();
                AndroidUtils.showMessageErroDlgOnUiThread(e.getMessage(),getActivity());
                Log.e("TAG erro","Erro ao abrir os agendamentos",e);
            }
        }
    }




}
