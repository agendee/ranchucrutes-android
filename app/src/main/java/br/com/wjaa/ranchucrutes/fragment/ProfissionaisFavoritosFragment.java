package br.com.wjaa.ranchucrutes.fragment;

import android.content.ActivityNotFoundException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.List;

import br.com.wjaa.ranchucrutes.R;
import br.com.wjaa.ranchucrutes.activity.AgendamentoActivity;
import br.com.wjaa.ranchucrutes.adapter.ProfissionaisFavoritosListAdapter;
import br.com.wjaa.ranchucrutes.fragment.dummy.DummyContent;
import br.com.wjaa.ranchucrutes.service.ProfissionalService;
import br.com.wjaa.ranchucrutes.service.RanchucrutesConstants;
import br.com.wjaa.ranchucrutes.utils.AndroidUtils;
import br.com.wjaa.ranchucrutes.vo.ItemListVo;
import br.com.wjaa.ranchucrutes.vo.ProfissionalBasicoVo;
import roboguice.fragment.RoboListFragment;

public class ProfissionaisFavoritosFragment extends RoboListFragment {

    @Inject
    private ProfissionalService profissionalService;

    private ProfissionalBasicoVo [] profissionaisFavoritos;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ProfissionaisFavoritosFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //TODO AQUI TRAZER OS FAVORITOS DA BASE
        ProfissionalBasicoVo p1 = new ProfissionalBasicoVo();
        p1.setId(18868l);
        p1.setNome("Dr. Wagner Jeronimo");
        p1.setEspec("Psiquiatria");
        p1.setIdClinicaAtual(19589l);
        ProfissionalBasicoVo p2 = new ProfissionalBasicoVo();
        p2.setId(18878l);
        p2.setNome("Dra. Luiza Donizetti");
        p2.setEspec("Dermatologista");
        p2.setIdClinicaAtual(19593l);
        profissionaisFavoritos = new ProfissionalBasicoVo[]{p1,p2};

        setListAdapter(new ProfissionaisFavoritosListAdapter(profissionaisFavoritos, getActivity()));
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        ProfissionalBasicoVo vo =  profissionaisFavoritos[position];
        AndroidUtils.showWaitDlg("Aguarde abrindo agenda do profissional...",getActivity());
        new FindProfissional(vo.getId(), vo.getIdClinicaAtual()).start();
    }

    class FindProfissional extends Thread{
        private Long idProfissional;
        private Long idClinica;
        public FindProfissional(Long idProfissional, Long idClinica) {
            this.idProfissional = idProfissional;
            this.idClinica = idClinica;
        }

        @Override
        public void run() {
            try{
                //TODO BUSCAR O PROFISSIONA E CLINICA
                final ProfissionalBasicoVo profissional = profissionalService.getProfissionalById(idProfissional);
                profissional.setIdClinicaAtual(idClinica);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Bundle b = new Bundle();
                        b.putSerializable(RanchucrutesConstants.PARAM_PROFISSIONAL, profissional);
                        AndroidUtils.closeWaitDlg();
                        AndroidUtils.openActivity(getActivity(), AgendamentoActivity.class, b);
                    }
                });

            }
            catch(ActivityNotFoundException act){
                AndroidUtils.closeWaitDlg();
                Log.e("Exemplo de chamada", "falha", act);
            }
        }
    }

}
