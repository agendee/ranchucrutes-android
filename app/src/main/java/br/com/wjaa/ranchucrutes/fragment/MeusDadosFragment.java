package br.com.wjaa.ranchucrutes.fragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.widget.ListViewCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.RadioGroup;

import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import br.com.wjaa.ranchucrutes.R;
import br.com.wjaa.ranchucrutes.activity.SearchGenericListActivity;
import br.com.wjaa.ranchucrutes.activity.callback.DialogCallback;
import br.com.wjaa.ranchucrutes.adapter.ConveniosSelectedListAdapter;
import br.com.wjaa.ranchucrutes.buffer.RanchucrutesBuffer;
import br.com.wjaa.ranchucrutes.buffer.RanchucrutesSession;
import br.com.wjaa.ranchucrutes.entity.PacienteConvenioEntity;
import br.com.wjaa.ranchucrutes.entity.PacienteEntity;
import br.com.wjaa.ranchucrutes.service.LoginService;
import br.com.wjaa.ranchucrutes.service.RanchucrutesConstants;
import br.com.wjaa.ranchucrutes.service.RanchucrutesService;
import br.com.wjaa.ranchucrutes.utils.AndroidUtils;
import br.com.wjaa.ranchucrutes.utils.CollectionUtils;
import br.com.wjaa.ranchucrutes.utils.DateUtils;
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
public class MeusDadosFragment extends RoboFragment{


    @InjectView(R.id.edtDadosEmail)
    private EditText edtEmail;

    @InjectView(R.id.edtDadosCelular)
    private EditText edtCelular;

    @InjectView(R.id.edtDadosNome)
    private EditText edtNome;

    @InjectView(R.id.edtDadosCpf)
    private EditText edtCpf;

    @InjectView(R.id.rgSexo)
    private RadioGroup rgSexo;

    @InjectView(R.id.edtDtAniversario)
    private EditText edtDtAniversario;

    @InjectView(R.id.btnSelectPlano)
    private Button btnConvenio;

    /*@InjectView(R.id.btnSelectCategoria)
    private Button btnCategoria;*/

    @InjectView(R.id.btnDadosSave)
    private Button btnSave;

    @Inject
    private RanchucrutesService ranchucrutesService;

    @Inject
    private LoginService loginService;

    @InjectView(R.id.lvConvenios)
    private ListViewCompat lvConvenios;

    private List<ConvenioCategoriaVo> categorias;

    private ConvenioCategoriaVo categoriaSelected;

    private PacienteEntity user;

    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            String data = String.format("%02d",dayOfMonth) + "/"
                    + String.format("%02d",monthOfYear+1) + "/" + String.valueOf(year);
            edtDtAniversario.setText(data);
            user.setDataAniversario(data);
        }
    };

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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        user = new PacienteEntity(RanchucrutesSession.getUsuario());
        categorias = new ArrayList<>();
        if (user == null){
            AndroidUtils.showMessageErroDlg("Usuário não está autenticado!", this.getActivity());
            return;
        }else{
            if (CollectionUtils.isNotEmpty(user.getListPacienteConvenio())){
                for (PacienteConvenioEntity pc : user.getListPacienteConvenio()){
                    categorias.add(new ConvenioCategoriaVo(pc));
                }

            }
        }


        lvConvenios.setAdapter(new ConveniosSelectedListAdapter(this.getContext(),categorias));
        LayoutInflater inflater = (LayoutInflater) this.getActivity()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View emptyView = inflater.inflate(R.layout.empty_list, null, false);
        lvConvenios.setEmptyView(emptyView);
        initButtons();
    }

    @Override
    public void onResume() {
        super.onResume();
        initView();
        this.adjustListViewHeightBasedOnChildren();
    }

    private void initButtons() {
        btnConvenio.setOnClickListener(new DialogConvenioClickListener());
        //btnCategoria.setOnClickListener(new DialogCategoriaClickListener());
        edtDtAniversario.setOnClickListener(new DialogDataAniversarioClickListener());
        //btnCategoria.setEnabled(false);
        btnSave.setOnClickListener(new SavePacienteClickListener());
    }

    public void initView() {
        if (user != null){
            this.atualizarCampos(user);
        }else{
            this.zerarCampos();
        }

        EditText [] edts = new EditText[4];
        edts[0] = edtNome;
        edts[1] = edtEmail;
        edts[2] = edtCelular;
        edts[3] = edtCpf;

        for(EditText e : edts){
            e.addTextChangedListener(new TextChanged(e.getId()));
        }

        rgSexo.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (user != null){
                    switch (checkedId){
                        case R.id.rbFeminino : user.setSexo("F"); break;
                        case R.id.rbMasculino : user.setSexo("M"); break;
                    }
                }
            }
        });
    }



    private void atualizarCampos(PacienteEntity usuario) {
        if (edtEmail != null) {
            edtCelular.setText(usuario.getTelefone());
            edtEmail.setText(usuario.getEmail());
            edtNome.setText(usuario.getNome());
            edtCpf.setText(usuario.getCpf());
            edtDtAniversario.setText(usuario.getDataAniversario());
            if ("M".equals(usuario.getSexo())){
                rgSexo.check(R.id.rbMasculino);
            }else if ("F".equals(usuario.getSexo())){
                rgSexo.check(R.id.rbFeminino);
            }
            if (usuario.getCategoriaVo() != null){
                btnConvenio.setText(usuario.getCategoriaVo().getConvenioVo().getNome());
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
            edtNome.setText("");
            edtCpf.setText("");
            edtDtAniversario.setText("dd/mm/aaaa");
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
                    if ( !this.categorias.contains(convenioCategoriaVo) ){
                        this.categorias.add(convenioCategoriaVo);
                    }

                    ((ArrayAdapter)this.lvConvenios.getAdapter()).notifyDataSetChanged();

                }
                if (model instanceof  ConvenioVo){
                    ConvenioVo convenioVo = (ConvenioVo)model;

                    new FindCategoria(convenioVo.getId()).start();
                }

            }
        }
    }

    protected void createDialogDataAniversario() {
        Calendar calendario = Calendar.getInstance();

        int ano = calendario.get(Calendar.YEAR);
        int mes = calendario.get(Calendar.MONTH);
        int dia = calendario.get(Calendar.DAY_OF_MONTH);
        new DatePickerDialog(this.getActivity(), mDateSetListener, ano, mes,
                        dia).show();

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
            AndroidUtils.openActivityFromFragment(MeusDadosFragment.this,SearchGenericListActivity.class, b);


        }
    }

    class DialogDataAniversarioClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            createDialogDataAniversario();
        }
    }

    class FindCategoria extends Thread{
        private Integer idConvenio;
        public FindCategoria(Integer idConvenio){
            this.idConvenio = idConvenio;
        }
        @Override
        public void run() {
            AndroidUtils.showWaitDlgOnUiThread("Aguarde...",getActivity());
            Bundle b = new Bundle();

            ConvenioCategoriaVo [] convenioCategorias = ranchucrutesService.getConvenioCategoriasByIdConvenio(idConvenio);
            if (convenioCategorias != null){

                ArrayList<Parcelable> parcelables = new ArrayList<>();
                for (ConvenioCategoriaVo c : convenioCategorias){
                    parcelables.add(c);
                }
                b.putParcelableArrayList(RanchucrutesConstants.PARAM_LIST_SEARCH, parcelables);
                AndroidUtils.openActivityFromFragment(MeusDadosFragment.this, SearchGenericListActivity.class, b);
                AndroidUtils.closeWaitDlg();
            }
        }
    }

    private class SavePacienteClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            PacienteEntity usuarioEntity = RanchucrutesSession.getUsuario();

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
            pacienteVo.setNome(user.getNome());
            pacienteVo.setTelefone(user.getTelefone());
            pacienteVo.setEmail(user.getEmail());
            if ( StringUtils.isNotBlank(user.getDataAniversario()) ){
                pacienteVo.setDataNascimento(DateUtils.parseddMMyyyy(user.getDataAniversario()));
            }
            pacienteVo.setSexo(user.getSexo());
            pacienteVo.setCpf(user.getCpf());
            pacienteVo.setAuthType(user.getAuthType());
            pacienteVo.setId(user.getId().longValue());
            pacienteVo.setUrlFoto(user.getUrlFoto());
            pacienteVo.setConveniosCategorias(categorias);

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
                AndroidUtils.showMessageSuccessDlgOnUiThread("Dados atualizados com sucesso!", (Activity) getContext());
            }catch (Exception ex){
                AndroidUtils.closeWaitDlg();
                AndroidUtils.showMessageErroDlgOnUiThread(ex.getMessage(), (Activity) getContext());
            }
        }
    }


    class TextChanged implements TextWatcher {

        private int idEdt;
        TextChanged(int idEdt){
            this.idEdt = idEdt;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            switch (idEdt){
                case R.id.edtDadosNome : {
                    user.setNome(s.toString());
                    break;
                }
                case R.id.edtDadosCelular : {
                    user.setTelefone(s.toString());
                    break;
                }
                case R.id.edtDadosEmail : {
                    user.setEmail(s.toString());
                    break;
                }
                case R.id.edtDadosCpf : {
                    user.setCpf(s.toString());
                    break;
                }

            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }


    private void adjustListViewHeightBasedOnChildren() {
        ListAdapter listAdapter = lvConvenios.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, lvConvenios);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = lvConvenios.getLayoutParams();
        params.height = totalHeight + (lvConvenios.getDividerHeight() * (listAdapter.getCount() - 1));
        lvConvenios.setLayoutParams(params);
        lvConvenios.requestLayout();
    }

}
