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
        List<ItemListVo> items = new ArrayList<>();
        items.add(new ItemListVo("18868", "Dr. Wagner Jeronimo"));


        setListAdapter(new ArrayAdapter<ItemListVo>(getActivity(),
                R.layout.item_list_default, R.id.textItemList, items));
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        ItemListVo vo = (ItemListVo) l.getItemAtPosition(position);
        AndroidUtils.showWaitDlg("Aguarde abrindo agenda do profissional...",getActivity());
        new FindProfissional(new Long(vo.getId())).start();
    }

    class FindProfissional extends Thread{
        private Long idProfissional;
        public FindProfissional(Long idProfissional) {
            this.idProfissional = idProfissional;
        }

        @Override
        public void run() {
            try{
                //profissionalService.
                final ProfissionalBasicoVo profissional = profissionalService.getProfissionalById(idProfissional);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Bundle b = new Bundle();
                        b.putParcelable(RanchucrutesConstants.PARAM_PROFISSIONAL, profissional);
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
