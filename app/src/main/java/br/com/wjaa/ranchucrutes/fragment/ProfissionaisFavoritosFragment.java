package br.com.wjaa.ranchucrutes.fragment;

import android.content.ActivityNotFoundException;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import br.com.wjaa.ranchucrutes.R;
import br.com.wjaa.ranchucrutes.activity.AgendamentoActivity;
import br.com.wjaa.ranchucrutes.adapter.ProfissionaisFavoritosListAdapter;
import br.com.wjaa.ranchucrutes.entity.ProfissionalFavoritoEntity;
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

    private List<ProfissionalFavoritoEntity> profissionaisFavoritos;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ProfissionaisFavoritosFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        ProfissionalFavoritoEntity pfe =  profissionaisFavoritos.get(position);
        AndroidUtils.showWaitDlg("Aguarde abrindo agenda do profissional...",getActivity());
        new FindProfissional(pfe.getIdProfissional().longValue(), pfe.getIdClinica().longValue()).start();
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

    @Override
    public void onStart() {
        super.onStart();
        setEmptyText(getResources().getString(R.string.emptyProfissionais));
        profissionaisFavoritos = profissionalService.listProfissionalFavorito();
        setListAdapter(new ProfissionaisFavoritosListAdapter(profissionaisFavoritos, getActivity()));
    }

}
