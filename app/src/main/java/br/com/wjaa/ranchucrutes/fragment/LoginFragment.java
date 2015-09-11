package br.com.wjaa.ranchucrutes.fragment;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.inject.Inject;

import br.com.wjaa.ranchucrutes.R;
import br.com.wjaa.ranchucrutes.service.FacebookService;
import roboguice.fragment.provided.RoboFragment;
import roboguice.inject.InjectView;

/**
 *
 */
public class LoginFragment extends RoboFragment {

    @Inject
    private FacebookService facebookService;

    @InjectView(R.id.btnTesteFragment)
    private Button button;

    @Inject
    private NovoPacienteFragment novoPacienteFragment;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        facebookService.onCreate(this);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view,savedInstanceState);
        facebookService.onViewCreated(view, savedInstanceState);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // consider using Java coding conventions (upper first char class names!!!)
                FragmentTransaction transaction = getFragmentManager().beginTransaction();

                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack
                transaction.setCustomAnimations(R.anim.fadein, R.anim.fadeout, R.anim.fadein, R.anim.fadeout)
                        .replace(R.id.main_frame, novoPacienteFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        facebookService.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
        facebookService.onStop();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        facebookService.onActivityResult(requestCode,resultCode,data);
    }






}
