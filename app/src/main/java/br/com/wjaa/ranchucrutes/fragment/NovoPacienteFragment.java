package br.com.wjaa.ranchucrutes.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import br.com.wjaa.ranchucrutes.R;
import roboguice.activity.RoboActionBarActivity;
import roboguice.fragment.provided.RoboFragment;

/**
 * Created by wagner on 10/09/15.
 */
public class NovoPacienteFragment extends RoboFragment{



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_novo_paciente, container, false);
        ((RoboActionBarActivity)getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        ((RoboActionBarActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((RoboActionBarActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);

        return v;
    }


}
