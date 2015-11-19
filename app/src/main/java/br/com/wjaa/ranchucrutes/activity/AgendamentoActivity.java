package br.com.wjaa.ranchucrutes.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.LayoutDirection;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import br.com.wjaa.ranchucrutes.R;
import br.com.wjaa.ranchucrutes.adapter.AgendamentoTabsAdapter;
import br.com.wjaa.ranchucrutes.exception.AgendamentoServiceException;
import br.com.wjaa.ranchucrutes.fragment.ProfissionalAgendaFragment;
import br.com.wjaa.ranchucrutes.service.AgendamentoService;
import br.com.wjaa.ranchucrutes.service.RanchucrutesConstants;
import br.com.wjaa.ranchucrutes.utils.AndroidUtils;
import br.com.wjaa.ranchucrutes.utils.DateUtils;
import br.com.wjaa.ranchucrutes.view.SlidingTabLayout;
import br.com.wjaa.ranchucrutes.vo.AgendaVo;
import br.com.wjaa.ranchucrutes.vo.ProfissionalBasicoVo;
import roboguice.activity.RoboActionBarActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_agendamento)
public class AgendamentoActivity extends RoboActionBarActivity {

    @InjectView(R.id.collapsing_toolbar)
    private CollapsingToolbarLayout mCollapsingToolbarLayout;

    @InjectView(R.id.toolbar)
    private Toolbar toolbar;

    @InjectView(R.id.tabLayout)
    private SlidingTabLayout tabLayout;

    @InjectView(R.id.vpTabs)
    private ViewPager viewPager;

    @InjectView(R.id.scrollBody)
    private NestedScrollView nestedScrollView;


    @Inject
    private AgendamentoService agendamentoService;

    private ProfissionalBasicoVo profissional;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Fresco.initialize(this);
        super.onCreate(savedInstanceState);
        Bundle b = getIntent().getExtras();
        profissional = b.getParcelable(RanchucrutesConstants.PARAM_PROFISSIONAL);
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

    private void initButtons() {
        criarBotaoLigar();

    }

    private void criarBotaoLigar() {
        // FAB
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (profissional != null && profissional.getTelefone() != null && !"".equals(profissional.getTelefone())){
                    Intent chamada = new Intent(Intent.ACTION_DIAL);
                    //pega a posição da pessoa
                    chamada.setData(Uri.parse("tel:" + profissional.getTelefone().trim()));
                    AgendamentoActivity.this.startActivity(chamada);

                }else{
                    AndroidUtils.showMessageErroDlg("Esse profissional não divulgou seu telefone.",
                            AgendamentoActivity.this);
                }

            }
        });
    }

    private void initMenu() {

        mCollapsingToolbarLayout.setTitle(profissional.getNome());

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setBackground(null);
        mToolbar.setTitle(profissional.getNome());
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(false);

        SimpleDraweeView sdvFotoProfissional = (SimpleDraweeView) findViewById(R.id.sdvFotoProfissional);

        Uri uri = Uri.parse("http://agendee.com.br/f/" + profissional.getCrm() + ".jpg");
        DraweeController dc = Fresco.newDraweeControllerBuilder()
                .setUri( uri )
                .setAutoPlayAnimations(true)
                .setOldController( sdvFotoProfissional.getController() )
                .build();

        sdvFotoProfissional.setController(dc);

        TextView crmProfissional = ((TextView) this.findViewById(R.id.crm));
        String crm = profissional.getCrm() != null ? profissional.getCrm().toString() : "";
        SpannableString crmText = new SpannableString(crm);
        crmText.setSpan(new ForegroundColorSpan(Color.BLUE), 0, crm.length(), 0);
        crmProfissional.setText("CRM: " + crmText);

        TextView especProfissional = ((TextView) this.findViewById(R.id.espec));
        especProfissional.setText(profissional.getEspec());

        TextView endProfissional = ((TextView) this.findViewById(R.id.endereco));
        endProfissional.setText(profissional.getEndereco());

        TextView telProfissional = ((TextView) this.findViewById(R.id.telefone));
        if (profissional.getTelefone() != null && !"".equals(profissional.getTelefone())){

            telProfissional.setText("Telefone: " + profissional.getTelefone());
        }else{
            telProfissional.setText("Telefone: --");
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == android.R.id.home){
            finish();
        }
        return true;
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
                        profissional.getId(), profissional.getClinicas()[0].getId());
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
                            tabLayout.setBackgroundColor(getResources().getColor(R.color.primaryColor));
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

                            tabLayout.setViewPager(viewPager);
                        }
                    });



                }else{
                    criarMsg("Profissional não possui agenda online.");

                }


            } catch (AgendamentoServiceException e) {
                criarMsg(e.getMessage());
            }


        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        finish();
    }

    private void criarMsg(String s) {

      /*  final FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);
        lp.setMargins(20, 20, 20, 20);
        final LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        final TextView tv = new TextView(this);
        //b.setLayoutParams(lp);
        tv.setText(s);
        tv.setTextColor(getResources().getColor(android.R.color.white));
        tv.setTextSize(30);
        linearLayout.addView(tv);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                nestedScrollView.addView(linearLayout, lp);

            }
        });
*/
    }
}
