package br.com.wjaa.ranchucrutes.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.inject.Inject;

import br.com.wjaa.ranchucrutes.R;
import br.com.wjaa.ranchucrutes.buffer.RanchucrutesBuffer;
import br.com.wjaa.ranchucrutes.maps.RanchucrutesMaps;
import br.com.wjaa.ranchucrutes.service.MedicoService;
import br.com.wjaa.ranchucrutes.utils.AndroidUtils;
import br.com.wjaa.ranchucrutes.vo.EspecialidadeVo;
import br.com.wjaa.ranchucrutes.vo.LocationVo;
import br.com.wjaa.ranchucrutes.vo.ResultadoBuscaMedicoVo;
import roboguice.RoboGuice;
import roboguice.activity.RoboActionBarActivity;
import roboguice.fragment.provided.RoboFragment;
import roboguice.inject.InjectView;
import roboguice.util.RoboAsyncTask;

public class BuscaFragment extends RoboFragment implements GoogleMap.OnMyLocationButtonClickListener {

    private static final String TAG = BuscaFragment.class.getSimpleName();
    static {
        RoboGuice.setUseAnnotationDatabases(false);
    }

    private EspecialidadeVo especSelecionada;
    @Inject
    private MedicoService medicoService;
    @InjectView(R.id.btnSelectEspec)
    private Button btnEspecilidade;
    @InjectView(R.id.edtCep)
    private EditText edtCep;
    @InjectView(R.id.btnProcurar)
    private Button btnProcurarMedicos;
    private EspecialidadeVo [] especialidades;
    private RanchucrutesMaps ranchucrutesMaps;
    private Location myLocation;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ranchucrutesMaps = new RanchucrutesMaps(getActivity(), this);
        this.initActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_home, container, false);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        FragmentManager fm = ((FragmentActivity)getActivity()).getSupportFragmentManager();
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
        SupportMapFragment mapFragment =
                (SupportMapFragment) ((RoboActionBarActivity)getActivity()).getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this.ranchucrutesMaps);
    }

    private void createBtnProcurar() {
        btnProcurarMedicos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (especSelecionada == null) {
                    AndroidUtils.showMessageDlg(getString(R.string.msg_warning), getString(R.string.msg_informeEspeciliade), getActivity());
                    return;
                }

                if (myLocation == null && (edtCep.getText() == null || edtCep.getText().toString().trim().equals(""))) {
                    AndroidUtils.showMessageDlg(getString(R.string.msg_warning), getString(R.string.msg_informeCep), getActivity());
                    return;
                }
                AndroidUtils.showWaitDlg(getString(R.string.msg_aguarde), getActivity());
                ProcurarMedicosTask t = new ProcurarMedicosTask(view);
                t.execute();
            }

        });
    }

    private void createBtnEspec() {
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
                try {

                    btnEspecilidade.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            especialidades = RanchucrutesBuffer.getEspecialidades();

                            if (especialidades != null){
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                                ListView modeList = new ListView(getActivity());
                                modeList.setItemsCanFocus(true);
                                View v = getActivity().getLayoutInflater().inflate(R.layout.custom_title,null);
                                TextView tv = (TextView) v.findViewById(R.id.titleDefault);
                                tv.setText("Selecione uma especilidade");
                                builder.setCustomTitle(v);
                                builder.setView(modeList);
                                modeList.setBackgroundColor(getResources().getColor(android.support.v7.appcompat.R.color.primary_material_dark));
                                final Dialog dialogEspecs = builder.create();
                                ArrayAdapter<EspecialidadeVo> modeAdapter = new ArrayAdapter<EspecialidadeVo>(getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, especialidades){
                                    @Override
                                    public View getView(int position, View convertView, ViewGroup parent) {
                                        View v = getActivity().getLayoutInflater().inflate(R.layout.custom_item, null);
                                        TextView t = (TextView) v.findViewById(R.id.txtViewItem);
                                        t.setOnClickListener(new EspecOnClickListener(especialidades[position],dialogEspecs));
                                        t.setText(especialidades[position].getNome());
                                        return t;
                                    }
                                };
                                modeList.setAdapter(modeAdapter);
                                dialogEspecs.show();
                            }else{
                                AndroidUtils.showMessageDlg("Ops!", "Ocorreu algum problema na comunicação com o servidor", getActivity());
                                especialidades = RanchucrutesBuffer.getEspecialidades();
                            }

                        }
                    });
                    AndroidUtils.closeWaitDlg();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }

    @Override
    public boolean onMyLocationButtonClick() {
        this.myLocation = this.ranchucrutesMaps.getmMap().getMyLocation();

        if (this.myLocation == null){
            AndroidUtils.showMessageDlg(getString(R.string.msg_warning),
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
     * Task para procurar médicos
     */
    class ProcurarMedicosTask extends RoboAsyncTask<Void>{
        ProcurarMedicosTask(View v){
            super(v.getContext());
        }
        @Override
        public Void call() throws Exception {

            ResultadoBuscaMedicoVo resultado = null;
            if ((edtCep.getText() == null || "".equals(edtCep.getText().toString())) && myLocation != null){
                resultado = medicoService.find(especSelecionada.getId(), new LocationVo(myLocation.getLatitude(),myLocation.getLongitude()));
            }else{
                resultado = medicoService.find(especSelecionada.getId(), edtCep.getText().toString());
            }

            if (resultado != null) {
                ranchucrutesMaps.realoadMarker(resultado);
            }
           return null;
        }
    }


}