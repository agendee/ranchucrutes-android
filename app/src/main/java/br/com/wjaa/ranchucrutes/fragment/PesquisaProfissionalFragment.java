package br.com.wjaa.ranchucrutes.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.inject.Inject;

import java.util.ArrayList;

import br.com.wjaa.ranchucrutes.R;
import br.com.wjaa.ranchucrutes.activity.SearchingListActivity;
import br.com.wjaa.ranchucrutes.buffer.RanchucrutesBuffer;
import br.com.wjaa.ranchucrutes.maps.RanchucrutesMaps;
import br.com.wjaa.ranchucrutes.service.ProfissionalService;
import br.com.wjaa.ranchucrutes.service.RanchucrutesConstants;
import br.com.wjaa.ranchucrutes.utils.AndroidUtils;
import br.com.wjaa.ranchucrutes.view.SearchingListModel;
import br.com.wjaa.ranchucrutes.vo.EspecialidadeVo;
import br.com.wjaa.ranchucrutes.vo.LocationVo;
import br.com.wjaa.ranchucrutes.vo.ResultadoBuscaProfissionalVo;
import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;
import roboguice.util.RoboAsyncTask;

public class PesquisaProfissionalFragment extends RoboFragment implements GoogleMap.OnMyLocationButtonClickListener {

    private static final String TAG = PesquisaProfissionalFragment.class.getSimpleName();
    private EspecialidadeVo especSelecionada;
    @Inject
    private ProfissionalService profissionalService;
    @InjectView(R.id.btnSelectEspec)
    private Button btnEspecilidade;
    @InjectView(R.id.edtCep)
    private EditText edtCep;
    @InjectView(R.id.btnProcurar)
    private Button btnProcurarProfissional;
    private EspecialidadeVo[] especialidades;
    private RanchucrutesMaps ranchucrutesMaps;
    private Location myLocation;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ranchucrutesMaps = new RanchucrutesMaps(getActivity(),this, this);
        this.initActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.activity_home, container, false);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        FragmentManager fm = getChildFragmentManager();
        Fragment fragment = (fm.findFragmentById(R.id.map));
        FragmentTransaction ft = fm.beginTransaction();
        ft.remove(fragment);
        ft.commitAllowingStateLoss();
    }


    private void initActivity() {
        this.initBuffers();
        this.initEvents();
    }

    private void initBuffers() {
        especialidades = RanchucrutesBuffer.getEspecialidades();
    }

    private void initEvents() {
        this.createBtnEspec();
        this.createBtnProcurar();
        this.createEdtCep();
        this.createMaps();

    }

    private void createEdtCep() {
        edtCep.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                myLocation = null;
                return false;
            }
        });
    }

    private void createMaps() {
        SupportMapFragment mapFragment;
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(this.ranchucrutesMaps);
    }

    private void createBtnProcurar() {
        btnProcurarProfissional.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (especSelecionada == null) {
                    AndroidUtils.showMessageErroDlg(getString(R.string.msg_informeEspeciliade), getActivity());
                    return;
                }

                if (myLocation == null && (edtCep.getText() == null || edtCep.getText().toString().trim().equals(""))) {
                    AndroidUtils.showMessageErroDlg(getString(R.string.msg_informeCep), getActivity());
                    return;
                }
                AndroidUtils.showWaitDlg(getString(R.string.msg_aguarde), getActivity());
                ProcurarProfissionalTask t = new ProcurarProfissionalTask(view);
                t.execute();
            }

        });
    }

    private void createBtnEspec() {
        btnEspecilidade.setOnClickListener(new DialogEspecialidade());
    }

    @Override
    public boolean onMyLocationButtonClick() {
        this.myLocation = this.ranchucrutesMaps.getmMap().getMyLocation();

        if (this.myLocation == null){
            AndroidUtils.showMessageErroDlg(
                    "Não foi possível pegar sua localização. \n Verique se o GPS está ativo.", getActivity());
        }else{
            this.edtCep.setText("");
            this.edtCep.setHint("Usando sua Localização");
            this.ranchucrutesMaps.getmMap().moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(this.myLocation.getLatitude(), this.myLocation.getLongitude()), 13));
        }
        return true;

    }

    /**
     * Onclick das especialidades
     */
    class EspecOnClickListener implements View.OnClickListener{

        private EspecialidadeVo especialidadeVo;
        private Dialog dialog;
        EspecOnClickListener(EspecialidadeVo especialidadeVo, Dialog dialog){
            this.especialidadeVo = especialidadeVo;
            this.dialog = dialog;
        }
        @Override
        public void onClick(View view) {
            especSelecionada = this.especialidadeVo;
            btnEspecilidade.setText(especSelecionada.getNome());
            dialog.dismiss();
        }
    }

    /**
     * Task para procurar profissionais
     *
     */
    class ProcurarProfissionalTask extends RoboAsyncTask<Void>{
        ProcurarProfissionalTask(View v){
            super(v.getContext());
        }
        @Override
        public Void call() throws Exception {

            ResultadoBuscaProfissionalVo resultado = null;
            if ((edtCep.getText() == null || "".equals(edtCep.getText().toString())) && myLocation != null){
                resultado = profissionalService.find(especSelecionada.getId(), new LocationVo(myLocation.getLatitude(),myLocation.getLongitude()));
            }else{
                resultado = profissionalService.find(especSelecionada.getId(), edtCep.getText().toString());
            }

            if (resultado != null) {
                ranchucrutesMaps.realoadMarker(resultado);
            }
           return null;
        }
    }


    class DialogEspecialidade implements View.OnClickListener{

        @Override
        public void onClick(View v) {

            Bundle b = new Bundle();
            ArrayList<Parcelable> parcelables = new ArrayList<>();

            if (especialidades == null){
                especialidades = RanchucrutesBuffer.getEspecialidades();
            }

            if (especialidades != null){
                for (EspecialidadeVo e : especialidades){
                    parcelables.add(e);
                }
                b.putParcelableArrayList(RanchucrutesConstants.PARAM_LIST_SEARCH, parcelables);
                AndroidUtils.openActivityFromFragment(PesquisaProfissionalFragment.this, SearchingListActivity.class, b);
            }else{
                AndroidUtils.showMessageErroDlg("Problemas na comunicação com o servidor.",getActivity());
            }


        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null){
            SearchingListModel model = data.getExtras().getParcelable(RanchucrutesConstants.PARAM_RESULT_SEARCH);

            if (model != null){
                especSelecionada = (EspecialidadeVo)model;
                btnEspecilidade.setText(especSelecionada.getNome());
            }
        }
    }

}