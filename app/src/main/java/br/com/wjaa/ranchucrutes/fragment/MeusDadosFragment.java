package br.com.wjaa.ranchucrutes.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.inject.Inject;

import java.util.ArrayList;

import br.com.wjaa.ranchucrutes.R;
import br.com.wjaa.ranchucrutes.activity.SearchingListActivity;
import br.com.wjaa.ranchucrutes.buffer.RanchucrutesBuffer;
import br.com.wjaa.ranchucrutes.buffer.RanchucrutesSession;
import br.com.wjaa.ranchucrutes.entity.UsuarioEntity;
import br.com.wjaa.ranchucrutes.listener.SessionChangedListener;
import br.com.wjaa.ranchucrutes.service.LoginService;
import br.com.wjaa.ranchucrutes.service.RanchucrutesConstants;
import br.com.wjaa.ranchucrutes.service.RanchucrutesService;
import br.com.wjaa.ranchucrutes.utils.AndroidUtils;
import br.com.wjaa.ranchucrutes.utils.StringUtils;
import br.com.wjaa.ranchucrutes.view.SearchingListModel;
import br.com.wjaa.ranchucrutes.vo.ConvenioCategoriaVo;
import br.com.wjaa.ranchucrutes.vo.ConvenioVo;
import br.com.wjaa.ranchucrutes.vo.PacienteVo;
import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

/**
 *
 */
public class MeusDadosFragment extends RoboFragment implements SessionChangedListener{


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

    @Inject
    private LoginService loginService;

    private ConvenioCategoriaVo [] categorias;

    private ConvenioCategoriaVo categoriaSelected;

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
        initButtons();
        initView();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void initButtons() {
        btnConvenio.setOnClickListener(new DialogConvenioClickListener());
        btnCategoria.setOnClickListener(new DialogCategoriaClickListener());
        btnCategoria.setEnabled(false);
        btnSave.setOnClickListener(new SavePacienteClickListener());

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
    public void usuarioChange(final UsuarioEntity usuario) {
        Activity activity = (Activity)getContext();

        //pode ser que o contexto ainda nao esteja criado.
        if (activity != null){
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (usuario == null){
                        zerarCampos();
                    }else{
                        atualizarCampos(usuario);
                    }
                }
            });
        }

    }

    private void atualizarCampos(UsuarioEntity usuario) {
        if (edtEmail != null) {
            edtCelular.setText(usuario.getTelefone());
            edtEmail.setText(usuario.getEmail());
            edtNome.setText(usuario.getNome());
            if (usuario.getCategoriaVo() != null){
                btnConvenio.setText(usuario.getCategoriaVo().getConvenioVo().getNome());
                btnCategoria.setText(usuario.getCategoriaVo().getNome());
                btnCategoria.setEnabled(true);
                categoriaSelected = usuario.getCategoriaVo();
            }
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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null){
            SearchingListModel model = data.getExtras().getParcelable(RanchucrutesConstants.PARAM_RESULT_SEARCH);

            if (model != null){

                if (model instanceof  ConvenioCategoriaVo){
                    ConvenioCategoriaVo convenioCategoriaVo = (ConvenioCategoriaVo)model;
                    btnCategoria.setText(convenioCategoriaVo.getNome());
                    this.categoriaSelected = convenioCategoriaVo;
                }
                if (model instanceof  ConvenioVo){
                    ConvenioVo convenioVo = (ConvenioVo)model;
                    btnConvenio.setText(convenioVo.getNome());
                    new FindCategoria(convenioVo.getId()).start();
                }

            }
        }
    }


    class DialogConvenioClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            Bundle b = new Bundle();
            ArrayList<Parcelable> parcelables = new ArrayList<>();
            for (ConvenioVo c : RanchucrutesBuffer.getConvenios()){
                parcelables.add(c);
            }
            b.putParcelableArrayList(RanchucrutesConstants.PARAM_LIST_SEARCH, parcelables);
            AndroidUtils.openActivityFromFragment(MeusDadosFragment.this,SearchingListActivity.class, b);


        }
    }


    class DialogCategoriaClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            Bundle b = new Bundle();

            if (categorias != null){

                ArrayList<Parcelable> parcelables = new ArrayList<>();
                for (ConvenioCategoriaVo c : categorias){
                    parcelables.add(c);
                }
                b.putParcelableArrayList(RanchucrutesConstants.PARAM_LIST_SEARCH, parcelables);
                AndroidUtils.openActivityFromFragment(MeusDadosFragment.this,SearchingListActivity.class, b);
            }
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

    private class SavePacienteClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            UsuarioEntity usuarioEntity = RanchucrutesSession.getUsuario();

            if (usuarioEntity == null){
                AndroidUtils.showMessageErroDlg("Usuário não está autenticado!", getContext());
                return;
            }
            if (StringUtils.isBlank(edtEmail.getText().toString())){
                AndroidUtils.showMessageErroDlg("Campo email não pode ser vazio.",getContext());
                return;
            }

            if (StringUtils.isBlank(edtNome.getText().toString())){
                AndroidUtils.showMessageErroDlg("Campo nome não pode ser vazio.", getContext());
                return;
            }
            PacienteVo pacienteVo = new PacienteVo();
            pacienteVo.setNome(edtNome.getText().toString());
            pacienteVo.setTelefone(edtCelular.getText().toString());
            pacienteVo.setEmail(edtEmail.getText().toString());
            if (categoriaSelected != null){
                pacienteVo.setIdCategoria(categoriaSelected.getId());
            }

            pacienteVo.setAuthType(usuarioEntity.getAuthType());
            pacienteVo.setId(usuarioEntity.getId().longValue());

            AndroidUtils.showWaitDlgOnUiThread("Aguarde, atualizando dados...", (Activity) getContext());
            new UpdatePaciente(pacienteVo).start();

        }
    }

    private class UpdatePaciente extends Thread{
        private PacienteVo pacienteVo;
        public UpdatePaciente(PacienteVo pacienteVo){
            this.pacienteVo = pacienteVo;
        }
        @Override
        public void run() {
            try{
                loginService.atualizarPaciente(pacienteVo);
                AndroidUtils.closeWaitDlg();
                AndroidUtils.showMessageSuccessDlgOnUiThread("Dados atualizados.", (Activity) getContext());
            }catch (Exception ex){
                AndroidUtils.closeWaitDlg();
                AndroidUtils.showMessageErroDlgOnUiThread(ex.getMessage(), (Activity) getContext());
            }
        }
    }
}
