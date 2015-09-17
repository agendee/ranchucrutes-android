package br.com.wjaa.ranchucrutes.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.plus.PlusOneButton;

import br.com.wjaa.ranchucrutes.R;
import br.com.wjaa.ranchucrutes.listener.SessionChangedListener;
import br.com.wjaa.ranchucrutes.vo.PacienteVo;
import roboguice.fragment.provided.RoboFragment;
import roboguice.inject.InjectView;

/**
 * A fragment with a Google +1 button.
 * Activities that contain this fragment must implement the
 * {@link DadosUsuarioFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DadosUsuarioFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DadosUsuarioFragment extends RoboFragment implements SessionChangedListener {


    @InjectView(R.id.edtDadosEmail)
    private EditText edtEmail;

    @InjectView(R.id.edtDadosCelular)
    private EditText edtCelular;

    @InjectView(R.id.edtDadosNome)
    private EditText edtNome;

    @InjectView(R.id.btnDadosSave)
    private Button btnSave;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dados_usuario, container, false);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void pacienteChange(PacienteVo paciente) {
        edtCelular.setText(paciente.getTelefone());
        edtEmail.setText(paciente.getEmail());
        edtNome.setText(paciente.getNome());
    }


}
