package br.com.wjaa.ranchucrutes.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.facebook.FacebookRequestError;
import com.google.inject.Inject;

import br.com.wjaa.ranchucrutes.R;
import br.com.wjaa.ranchucrutes.activity.SearchingListActivity;
import br.com.wjaa.ranchucrutes.buffer.RanchucrutesBuffer;
import br.com.wjaa.ranchucrutes.buffer.RanchucrutesSession;
import br.com.wjaa.ranchucrutes.entity.UsuarioEntity;
import br.com.wjaa.ranchucrutes.listener.SessionChangedListener;
import br.com.wjaa.ranchucrutes.service.LoginService;
import br.com.wjaa.ranchucrutes.service.RanchucrutesService;
import br.com.wjaa.ranchucrutes.utils.AndroidUtils;
import br.com.wjaa.ranchucrutes.utils.ObjectUtils;
import br.com.wjaa.ranchucrutes.utils.StringUtils;
import br.com.wjaa.ranchucrutes.view.SearchingListDialog;
import br.com.wjaa.ranchucrutes.view.SearchingListDialogCallback;
import br.com.wjaa.ranchucrutes.vo.ConvenioCategoriaVo;
import br.com.wjaa.ranchucrutes.vo.ConvenioVo;
import br.com.wjaa.ranchucrutes.vo.PacienteVo;
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
    }

    @Override
    public void onResume() {
        super.onResume();
        initView();

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


    class DialogConvenioClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            SearchingListDialog<ConvenioVo> dialog = new SearchingListDialog<>(new SearchingListDialogCallback<ConvenioVo>() {
                @Override
                public void onResult(ConvenioVo result) {
                    btnConvenio.setText(result.getNome());
                    new FindCategoria(result.getId()).start();
                }
            }, getContext());
            dialog.addTitle("Selecione um plano").openDialog(RanchucrutesBuffer.getConvenios());
        }
    }


    class DialogCategoriaClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            AndroidUtils.openActivity(getActivity(),SearchingListActivity.class);



            /*SearchingListDialog<ConvenioCategoriaVo> dialog = new SearchingListDialog<>(new SearchingListDialogCallback<ConvenioCategoriaVo>() {
                @Override
                public void onResult(ConvenioCategoriaVo result) {
                    btnCategoria.setText(result.getNome());
                    categoriaSelected = result;
                }
            }, getContext());
            dialog.addTitle("Selecione um Plano").openDialog(categorias);*/
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
                AndroidUtils.showMessageDlg("Ops!", "Usuário não está autenticado!",getContext());
                return;
            }
            if (StringUtils.isBlank(edtEmail.getText().toString())){
                AndroidUtils.showMessageDlg("Ops!", "Campo email não pode ser vazio.",getContext());
                return;
            }

            if (StringUtils.isBlank(edtNome.getText().toString())){
                AndroidUtils.showMessageDlg("Ops!", "Campo nome não pode ser vazio.",getContext());
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
                AndroidUtils.showMessageDlgOnUiThread("Sucesso!", "Dados atualizados.", (Activity) getContext());
            }catch (Exception ex){
                AndroidUtils.closeWaitDlg();
                AndroidUtils.showMessageDlgOnUiThread("Ops!", ex.getMessage(), (Activity) getContext());
            }
        }
    }
}
