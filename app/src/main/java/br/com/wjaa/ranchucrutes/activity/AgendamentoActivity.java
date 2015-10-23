package br.com.wjaa.ranchucrutes.activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.inject.Inject;

import java.util.Date;

import br.com.wjaa.ranchucrutes.R;
import br.com.wjaa.ranchucrutes.adapter.TabsAdapter;
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

    @Inject
    private AgendamentoService agendamentoService;

    private ProfissionalBasicoVo profissional;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Fresco.initialize(this);
        super.onCreate(savedInstanceState);
        Bundle b = getIntent().getExtras();
        profissional = b.getParcelable(RanchucrutesConstants.PARAM_PROFISSIONAL);
        this.init();
        this.criarAgenda();

    }

    private void init() {
        this.initMenu();
        this.initButtons();
        this.initTabs();
    }

    private void initTabs() {
        viewPager.setAdapter(new TabsAdapter(getSupportFragmentManager(), this));

        tabLayout.setBackgroundColor(getResources().getColor(R.color.primaryColor));
        tabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.primaryColorDark));
        tabLayout.setCustomTabView(R.layout.tab_view, R.id.tv_tab);
        /*slidingTabLayout.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                navigationDrawerLeft.setSelection(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });*/
        tabLayout.setViewPager(viewPager);

    }

    private void initButtons() {

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
                    AndroidUtils.showMessageDlg("Ops!", "Esse profissional não divulgou seu telefone.",
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

    private void criarAgenda() {
       /* ProfissionalAgendaFragment fragment = new ProfissionalAgendaFragment();
        paneLayout.addView(fragment.getView());
        fragment = new ProfissionalAgendaFragment();
        paneLayout.addView(fragment.getView());
        fragment = new ProfissionalAgendaFragment();
        paneLayout.addView(fragment.getView());
*/


        //this.criarBotoesAgenda(child, profissional);
    }

    private void criarBotoesAgenda(GridLayout child, ProfissionalBasicoVo profissional) {
       new FindAgendamento(profissional,child).start();
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
        private GridLayout layout;

        public FindAgendamento(ProfissionalBasicoVo profissional, GridLayout layout){
            this.profissional = profissional;
            this.layout = layout;
        }


        @Override
        public void run() {

            try {
                AgendaVo agendaVo = agendamentoService.getAgendamentoByIdProfissional(
                        profissional.getId(), profissional.getClinicas()[0].getId());

                if (agendaVo != null){
                    String text = "";
                    for (Date date : agendaVo.getHorariosDisponiveis() ){
                        text += DateUtils.formatHHmm(date) + " | ";

                    }
                    criarMsg(text);
                }else{
                    criarMsg("Profissional não possui agenda em nosso cadastro");
                }


            } catch (AgendamentoServiceException e) {
                criarMsg(e.getMessage());

            }


        }

        private void criarMsg(String msg) {
            final TextView tv = new TextView( AgendamentoActivity.this );
            tv.setText(msg);
            tv.setTextColor(getResources().getColor(R.color.primaryColor));
            tv.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
            tv.setGravity(Gravity.CENTER);

            AgendamentoActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    layout.addView(tv);
                }
            });
        }
    }
}
