package br.com.wjaa.ranchucrutes.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import br.com.wjaa.ranchucrutes.R;
import br.com.wjaa.ranchucrutes.buffer.RanchucrutesSession;
import br.com.wjaa.ranchucrutes.entity.UsuarioEntity;
import br.com.wjaa.ranchucrutes.listener.SessionChangedListener;
import br.com.wjaa.ranchucrutes.vo.PacienteVo;
import roboguice.fragment.provided.RoboFragment;
import roboguice.inject.InjectView;

/**
 *
 */
public class DadosUsuarioFragment extends RoboFragment implements SessionChangedListener{


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
        return inflater.inflate(R.layout.fragment_dados_usuario, container, false);
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        initView();
    }

    public void initView() {
        UsuarioEntity usuario = RanchucrutesSession.getUsuario();
        if (usuario != null){
            this.atualizarCampos(usuario);
        }else{
            this.zerarCampos();
        }
    }


    @Override
    public void usuarioChange(UsuarioEntity usuario) {
        if (usuario == null){
            this.zerarCampos();
        }else{
            this.atualizarCampos(usuario);
        }
    }

    private void atualizarCampos(UsuarioEntity usuario) {
        if (edtEmail != null) {
            edtCelular.setText(usuario.getTelefone());
            edtEmail.setText(usuario.getEmail());
            edtNome.setText(usuario.getNome());
        }

    }

    private void zerarCampos() {
        //verificando se o campo nao está null, porque o fragmento pode nao ter sido criado ainda.
        if (edtEmail != null){
            edtCelular.setText("");
            edtEmail.setText("");
            edtNome.setText("");
        }
    }
}
