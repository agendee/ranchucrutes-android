package br.com.wjaa.ranchucrutes.fragment;

import android.content.ActivityNotFoundException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.inject.Inject;

import br.com.wjaa.ranchucrutes.activity.AgendamentoActivity;
import br.com.wjaa.ranchucrutes.fragment.dummy.DummyContent;
import br.com.wjaa.ranchucrutes.service.ProfissionalService;
import br.com.wjaa.ranchucrutes.service.RanchucrutesConstants;
import br.com.wjaa.ranchucrutes.utils.AndroidUtils;
import br.com.wjaa.ranchucrutes.vo.ProfissionalBasicoVo;
import roboguice.fragment.RoboListFragment;

public class FavoritoFragment extends RoboListFragment {

    @Inject
    private ProfissionalService profissionalService;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FavoritoFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // TODO: Change Adapter to display your content
        setListAdapter(new ArrayAdapter<DummyContent.DummyItem>(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1, DummyContent.ITEMS));
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        AndroidUtils.showWaitDlg("Aguarde abrindo agenda",getActivity());
        new FindProfissional().start();
    }

    class FindProfissional extends Thread{
        @Override
        public void run() {
            try{
                //profissionalService.
                final ProfissionalBasicoVo profissional = profissionalService.getProfissionalById(18868l);

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
