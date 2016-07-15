package br.com.wjaa.ranchucrutes.activity;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import br.com.wjaa.ranchucrutes.R;
import br.com.wjaa.ranchucrutes.adapter.AgendamentoTabsAdapter;
import br.com.wjaa.ranchucrutes.exception.AgendamentoServiceException;
import br.com.wjaa.ranchucrutes.fragment.ProfissionalAgendaFragment;
import br.com.wjaa.ranchucrutes.helper.DetalhesProfissionalHelper;
import br.com.wjaa.ranchucrutes.service.AgendamentoService;
import br.com.wjaa.ranchucrutes.service.ProfissionalService;
import br.com.wjaa.ranchucrutes.service.RanchucrutesConstants;
import br.com.wjaa.ranchucrutes.utils.AndroidUtils;
import br.com.wjaa.ranchucrutes.utils.DateUtils;
import br.com.wjaa.ranchucrutes.utils.StringUtils;
import br.com.wjaa.ranchucrutes.view.SlidingTabLayout;
import br.com.wjaa.ranchucrutes.vo.AgendaVo;
import br.com.wjaa.ranchucrutes.vo.ProfissionalBasicoVo;
import roboguice.activity.RoboActionBarActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_detalhes_profissional)
public class AgendamentoActivity extends RoboActionBarActivity {

    @InjectView(R.id.collapsing_toolbar)
    private CollapsingToolbarLayout mCollapsingToolbarLayout;

    @InjectView(R.id.toolbar)
    private Toolbar toolbar;

    @InjectView(R.id.tabLayout)
    private SlidingTabLayout tabLayout;

    @InjectView(R.id.frameInfoBotom)
    private FrameLayout frameInfoBotom;

    @InjectView(R.id.vpTabs)
    private ViewPager viewPager;

    @InjectView(R.id.fab)
    private FloatingActionButton fab;

    @InjectView(R.id.progressBar)
    private LinearLayout progressBar;

    @Inject
    private AgendamentoService agendamentoService;

    @Inject
    private ProfissionalService profissionalService;

    private ProfissionalBasicoVo profissional;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Fresco.initialize(this);
        super.onCreate(savedInstanceState);
        Bundle b = getIntent().getExtras();
        profissional = (ProfissionalBasicoVo) b.getSerializable(RanchucrutesConstants.PARAM_PROFISSIONAL);
        if (profissional != null){
            this.init();
        }

    }

    private void init() {
        this.initMenu();
        this.initTabs();
        this.initButtons();
    }

    private void initTabs() {
        new FindAgendamento(profissional).start();
    }

    @Override
    protected void onStart() {
        super.onStart();
        profissional.setFavorito(profissionalService.isFavorito(profissional.getId().intValue(),profissional.getIdClinicaAtual().intValue()));
    }

    private void initButtons() {
        criarBotaoLigar();

    }

    private void criarBotaoLigar() {
        fab.setOnClickListener(new BtnLigarClickListerner());
    }

    private void initMenu() {
        DetalhesProfissionalHelper.build(this,"Agendamento", mCollapsingToolbarLayout,toolbar,profissional);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profissional_detalhes, menu);
        MenuItem item = menu.findItem(R.id.menuAddFavorito);

        if ( profissional.isFavorito() )
            changeColorMenuItemProfissionalFavorito(item);
        else
            changeColorMenuItemProfissionalDefault(item);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == android.R.id.home){
            finish();
        }
        if (id == R.id.menuAddFavorito){
            if (!profissional.isFavorito()){
                changeColorMenuItemProfissionalFavorito(item);
                profissionalService.addProfissionalFavorito(profissional);
                Toast.makeText(this.getApplicationContext(),"Profissional adicionado ao favorito", Toast.LENGTH_SHORT).show();
            }else{
                changeColorMenuItemProfissionalDefault(item);
                profissionalService.removeProfissionalFavorito(profissional);
                Toast.makeText(this.getApplicationContext(),"Profissional removido do favorito", Toast.LENGTH_SHORT).show();
            }
            profissional.setFavorito(!profissional.isFavorito());

        }

        return true;
    }

    private void changeColorMenuItem(MenuItem item, int color) {
        Drawable drawable = item.getIcon();
        if (drawable != null) {
            drawable.mutate();
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        }
    }

    private void changeColorMenuItemProfissionalFavorito(MenuItem item) {
        changeColorMenuItem(item,getResources().getColor(R.color.favoritoColor));
    }

    private void changeColorMenuItemProfissionalDefault(MenuItem item) {
        changeColorMenuItem(item,getResources().getColor(android.R.color.white));
    }


    class FindAgendamento extends Thread{
        private ProfissionalBasicoVo profissional;
        public FindAgendamento(ProfissionalBasicoVo profissional){
            this.profissional = profissional;
        }


        @Override
        public void run() {

            try {
                final List<ProfissionalAgendaFragment> profissionalAgendaFragments = new ArrayList<>();
                AgendaVo agendaVo = agendamentoService.getAgendamentoByIdProfissional(
                        profissional.getId(), profissional.getIdClinicaAtual());
                Map<Date,List<Date>> datasAgrupadas = new TreeMap<>();
                if (agendaVo != null){
                    Collections.sort(agendaVo.getHorariosDisponiveis());
                    for (Date date : agendaVo.getHorariosDisponiveis() ){
                        String dateStr = DateUtils.formatddMMyyyy(date);
                        Date key = DateUtils.parseddMMyyyy(dateStr);

                        List<Date> listDatas = datasAgrupadas.get(key);

                        if (listDatas == null){
                            listDatas = new ArrayList<>();
                            datasAgrupadas.put(key,listDatas);
                        }
                        listDatas.add(date);
                    }

                    for (Date key : datasAgrupadas.keySet()){
                        profissionalAgendaFragments.add(new ProfissionalAgendaFragment(DateUtils.formatddMMyyyy(key)
                                ,datasAgrupadas.get(key),AgendamentoActivity.this,profissional));
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            viewPager.setAdapter(new AgendamentoTabsAdapter(getSupportFragmentManager(), profissionalAgendaFragments, AgendamentoActivity.this));
                            tabLayout.setBackgroundColor(getResources().getColor(android.R.color.black));
                            tabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.primaryColorDark));
                            tabLayout.setCustomTabView(R.layout.tab_view, R.id.tv_tab);
                            //aqui pra deixar apenas uma data por pagina.
                            //tabLayout.setCustomTabView();
                            tabLayout.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                                @Override
                                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                                    ImageView iant = (ImageView) tabLayout.getRootView().findViewById(R.id.icAgendaAnterior);
                                    ImageView ipr = (ImageView) tabLayout.getRootView().findViewById(R.id.icAgendaProximo);
                                    if (position == 0){
                                       ipr.setVisibility(View.VISIBLE);
                                       iant.setVisibility(View.INVISIBLE);

                                    }else if (position == profissionalAgendaFragments.size() -1){
                                        iant.setVisibility(View.VISIBLE);
                                        ipr.setVisibility(View.INVISIBLE);
                                    }else{
                                        iant.setVisibility(View.VISIBLE);
                                        ipr.setVisibility(View.VISIBLE);
                                    }

                                }

                                @Override
                                public void onPageSelected(int position) {
                                   // viewPager.destroyDrawingCache();
                                    //viewPager.invalidate();
                                }

                                @Override
                                public void onPageScrollStateChanged(int state) {
                                }
                            });
                            progressBar.setVisibility(View.INVISIBLE);
                            tabLayout.setViewPager(viewPager);
                        }
                    });



                }else{
                    profissionalSemAgenda("No momento esse profissional não possui agenda.");

                }


            } catch (AgendamentoServiceException e) {
                profissionalSemAgenda(e.getMessage());
            }


        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RanchucrutesConstants.FINISH_CONFIRME_AGENDAMENTO ||
                requestCode == RanchucrutesConstants.FINISH_CONFIRME_AGENDAMENTO_OPEN_LIST){
            finish();
        }


    }

    private void profissionalSemAgenda(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                View v = LayoutInflater.from(AgendamentoActivity.this).inflate(R.layout.fragment_agendamento,frameInfoBotom, false);
                TextView txtMsg = (TextView) v.findViewById(R.id.txtMsgAgenda);

                ImageButton btnLigar = (ImageButton) v.findViewById(R.id.btnLigar);
                btnLigar.setOnClickListener(new BtnLigarClickListerner());

                txtMsg.setText(msg);

                if (!StringUtils.isNotBlank(profissional.getTelefone())){
                    btnLigar.setVisibility(View.INVISIBLE);
                }

                progressBar.setVisibility(View.INVISIBLE);
                fab.setVisibility(View.INVISIBLE);
                tabLayout.setVisibility(View.INVISIBLE);
                viewPager.setVisibility(View.INVISIBLE);
                frameInfoBotom.setVisibility(View.VISIBLE);
                frameInfoBotom.addView(v);
            }
        });
    }




    class BtnLigarClickListerner implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            if (profissional != null && profissional.getTelefone() != null && !"".equals(profissional.getTelefone())) {
                Intent chamada = new Intent(Intent.ACTION_DIAL);
                //pega a posição da pessoa
                String tel = profissional.getTelefone().trim();
                if (tel.length() > 9){
                    //com ddd
                    chamada.setData(Uri.parse("tel:+55" + tel));
                }else{
                    //sem ddd
                    chamada.setData(Uri.parse("tel:" + tel));
                }
                AgendamentoActivity.this.startActivity(chamada);

            } else {
                AndroidUtils.showMessageErroDlg("Esse profissional não divulgou seu telefone.",
                        AgendamentoActivity.this);
            }

        }
    }


    @Override
    public void onBackPressed() {
        //jogando o setresult como cancelado porque a activity anterior espera
        //que o paciente concretize o agendamento e irá mostrar a lista de agendamentos.
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }
}
