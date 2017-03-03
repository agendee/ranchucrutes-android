package br.com.agendee.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

import br.com.agendee.adapter.ProfissionalListAdapter;
import br.com.agendee.service.RanchucrutesConstants;
import br.com.agendee.utils.AndroidUtils;
import br.com.agendee.utils.StringUtils;
import br.com.agendee.vo.ClinicaVo;
import br.com.agendee.R;
import br.com.agendee.vo.MapTipoLocalidade;
import br.com.agendee.vo.ProfissionalBasicoVo;
import roboguice.activity.RoboActionBarActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_profissional_list)
public class ProfissionalListActivity extends RoboActionBarActivity {

    private List<ProfissionalBasicoVo> profissionaisClinica;
    private ClinicaVo clinicaVo;
    @InjectView(R.id.toolbar)
    private Toolbar toolbar;

    @InjectView(R.id.listProfissional)
    private ListView listProfissional;

    @Override
    public void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
        Bundle b = getIntent().getExtras();
        clinicaVo = (ClinicaVo) b.getSerializable(RanchucrutesConstants.PARAM_CLINICA);
        if (clinicaVo != null){
            profissionaisClinica = clinicaVo.getProfissionais();
            if (MapTipoLocalidade.EDIFICIO.equals(clinicaVo.getMapTipoLocalidade())){
                toolbar.setTitle("Profissionais no Edifício");
            }else{
                toolbar.setTitle("Profissionais da Clínica");
            }
        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        init();
    }

    private void init(){

        listProfissional.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ProfissionalBasicoVo profissional =  profissionaisClinica.get(position);
                if (StringUtils.isNotBlank(clinicaVo.getTelefone())){
                    profissional.setTelefone(clinicaVo.getTelefone());
                }
                profissional.setIdClinicaAtual(clinicaVo.getId());
                Bundle b = new Bundle();
                b.putSerializable(RanchucrutesConstants.PARAM_PROFISSIONAL, profissional);
                AndroidUtils.openActivity(ProfissionalListActivity.this, AgendamentoActivity.class, b, RanchucrutesConstants.FINISH_CONFIRME_AGENDAMENTO_OPEN_LIST);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        listProfissional.setAdapter(new ProfissionalListAdapter(profissionaisClinica, clinicaVo, this));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == android.R.id.home){
            finish();
        }
        return true;
    }
}
