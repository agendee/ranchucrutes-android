package br.com.wjaa.ranchucrutes.fragment;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import br.com.wjaa.ranchucrutes.R;
import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

/**
 *
 */
public class ProfissionalAgendaFragment extends RoboFragment {

    @InjectView(R.id.textViewHorario)
    private TextView textView;

    public ProfissionalAgendaFragment() {
        // Required empty public constructor
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

    }
}
