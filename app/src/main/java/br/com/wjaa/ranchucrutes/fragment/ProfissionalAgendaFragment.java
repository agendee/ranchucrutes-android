package br.com.wjaa.ranchucrutes.fragment;

import android.app.Activity;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.TextView;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import br.com.wjaa.ranchucrutes.R;
import br.com.wjaa.ranchucrutes.utils.DateUtils;
import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

/**
 *
 */
public class ProfissionalAgendaFragment extends RoboFragment {

    private String title;
    private List<Date> horarios;

    public ProfissionalAgendaFragment(String title, List<Date> hs) {
        this.title = title;
        Collections.sort(hs);
        this.horarios = hs;
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
            b.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT));
            b.setText(DateUtils.formatHHmm(date));
            //b.setTextAppearance(R.style.btn);

            gridLayout.addView(b);
        }
        gridLayout.requestLayout();


    }


    public String getTitle(){
        return this.title;
    }

}
