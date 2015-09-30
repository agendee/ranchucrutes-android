package br.com.wjaa.ranchucrutes.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.inject.Inject;

import br.com.wjaa.ranchucrutes.R;
import br.com.wjaa.ranchucrutes.buffer.RanchucrutesBuffer;
import br.com.wjaa.ranchucrutes.buffer.RanchucrutesSession;
import br.com.wjaa.ranchucrutes.entity.UsuarioEntity;
import br.com.wjaa.ranchucrutes.listener.SessionChangedListener;
import br.com.wjaa.ranchucrutes.service.RanchucrutesService;
import br.com.wjaa.ranchucrutes.view.SearchableListDialog;
import br.com.wjaa.ranchucrutes.view.SearchableListDialogCallback;
import br.com.wjaa.ranchucrutes.vo.ConvenioCategoriaVo;
import br.com.wjaa.ranchucrutes.vo.ConvenioVo;
import roboguice.fragment.RoboFragment;
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

    @InjectView(R.id.btnSelectPlano)
    private Button btnConvenio;

    @InjectView(R.id.btnSelectCategoria)
    private Button btnCategoria;

    @InjectView(R.id.btnDadosSave)
    private Button btnSave;

    @Inject
    private RanchucrutesService ranchucrutesService;

    private ConvenioCategoriaVo [] categorias;

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
        initButtons();
    }

    private void initButtons() {
        btnConvenio.setOnClickListener(new DialogConvenio());
        btnCategoria.setOnClickListener(new DialogCategoria());
        btnCategoria.setEnabled(false);

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


    class DialogConvenio implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            SearchableListDialog<ConvenioVo> dialog = new SearchableListDialog<>(new SearchableListDialogCallback<ConvenioVo>() {
                @Override
                public void onResult(ConvenioVo result) {
                    btnConvenio.setText(result.getNome());
                    new FindCategoria(result.getId()).start();
                }
            }, getContext());
            dialog.addTitle("Selecione um plano").openDialog(RanchucrutesBuffer.getConvenios());
        }
    }


    class DialogCategoria implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            SearchableListDialog<ConvenioCategoriaVo> dialog = new SearchableListDialog<>(new SearchableListDialogCallback<ConvenioCategoriaVo>() {
                @Override
                public void onResult(ConvenioCategoriaVo result) {
                    btnCategoria.setText(result.getNome());
                }
            }, getContext());
            dialog.addTitle("Selecione um Plano").openDialog(categorias);
        }
    }

    class FindCategoria extends Thread{
        private Integer idConvenio;
        public FindCategoria(Integer idConvenio){
            this.idConvenio = idConvenio;
        }
        @Override
        public void run() {
            categorias = ranchucrutesService.getConvenioCategoriasByIdConvenio(idConvenio);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    btnCategoria.setText("Selecione uma Categoria");
                    btnCategoria.setEnabled(true);
                }
            });

        }
    }

}
