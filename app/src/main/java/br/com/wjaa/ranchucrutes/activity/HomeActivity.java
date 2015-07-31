package br.com.wjaa.ranchucrutes.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.inject.Inject;

import java.util.Random;

import br.com.wjaa.ranchucrutes.R;
import br.com.wjaa.ranchucrutes.buffer.RanchucrutesBuffer;
import br.com.wjaa.ranchucrutes.maps.RanchucrutesMaps;
import br.com.wjaa.ranchucrutes.service.MedicoService;
import br.com.wjaa.ranchucrutes.utils.AndroidUtils;
import br.com.wjaa.ranchucrutes.vo.EspecialidadeVo;
import br.com.wjaa.ranchucrutes.vo.ResultadoBuscaMedicoVo;
import roboguice.RoboGuice;
import roboguice.activity.RoboFragmentActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import roboguice.util.RoboAsyncTask;

@ContentView(R.layout.activity_home)
public class HomeActivity extends RoboFragmentActivity {

    private static final String TAG = HomeActivity.class.getSimpleName();



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
    private RanchucrutesMaps ranchucrutesMaps = new RanchucrutesMaps(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.initActivity();
    }

    private void initActivity() {
        AndroidUtils.showWaitDlg(getString(R.string.msg_aguarde), HomeActivity.this);
        this.initBuffers();
        this.initEvents();
    }

    private void initBuffers() {
        especialidades = RanchucrutesBuffer.getEspecialidades();
    }

    private void initEvents() {
        this.createBtnEspec();
        this.createBtnProcurar();
        this.createMaps();

    }

    private void createMaps() {
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this.ranchucrutesMaps);
    }

    private void createBtnProcurar() {
        btnProcurarMedicos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (especSelecionada == null) {
                    AndroidUtils.showMessageDlg(getString(R.string.msg_warning), getString(R.string.msg_informeEspeciliade), HomeActivity.this);
                    return;
                }

                if (edtCep.getText() == null || edtCep.getText().toString().trim().equals("")) {
                    AndroidUtils.showMessageDlg(getString(R.string.msg_warning), getString(R.string.msg_informeCep), HomeActivity.this);
                    return;
                }
                AndroidUtils.showWaitDlg(getString(R.string.msg_aguarde), HomeActivity.this);
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
                            AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);

                            builder.setTitle("Selecione uma especilidade");
                            ListView modeList = new ListView(HomeActivity.this);
                            builder.setView(modeList);

                            final Dialog dialogEspecs = builder.create();

                            ArrayAdapter<EspecialidadeVo> modeAdapter = new ArrayAdapter<EspecialidadeVo>(HomeActivity.this, android.R.layout.simple_list_item_1, android.R.id.text1, especialidades){
                                @Override
                                public View getView(int position, View convertView, ViewGroup parent) {
                                    TextView t = new TextView(parent.getContext());
                                    t.setTextAppearance(parent.getContext(),R.style.listDefault);
                                    t.setOnClickListener(new EspecOnClickListener(especialidades[position],dialogEspecs));
                                    t.setText(especialidades[position].getNome());
                                    return t;
                                }
                            };
                            modeList.setAdapter(modeAdapter);
                            dialogEspecs.show();
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
            ResultadoBuscaMedicoVo resultado = medicoService.find(especSelecionada.getId(), edtCep.getText().toString());
            if (resultado != null) {
                ranchucrutesMaps.realoadMarker(resultado);
            }
           return null;
        }
    }


}