package br.com.wjaa.ranchucrutes.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.ThemedSpinnerAdapter;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.inject.Inject;

import br.com.wjaa.ranchucrutes.R;
import br.com.wjaa.ranchucrutes.activity.callback.DialogCallback;
import br.com.wjaa.ranchucrutes.buffer.RanchucrutesSession;
import br.com.wjaa.ranchucrutes.entity.UsuarioEntity;
import br.com.wjaa.ranchucrutes.fragment.MeusDadosFragment;
import br.com.wjaa.ranchucrutes.fragment.MinhasConsultasFragment;
import br.com.wjaa.ranchucrutes.fragment.PesquisaProfissionalFragment;
import br.com.wjaa.ranchucrutes.fragment.ProfissionaisFavoritosFragment;
import br.com.wjaa.ranchucrutes.listener.SessionChangedListener;
import br.com.wjaa.ranchucrutes.service.LoginService;
import br.com.wjaa.ranchucrutes.service.RanchucrutesConstants;
import br.com.wjaa.ranchucrutes.utils.AndroidUtils;
import br.com.wjaa.ranchucrutes.utils.GcmUtils;
import roboguice.activity.RoboActionBarActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

/**
 * @author Wagner Jeronimo
 */
@ContentView(R.layout.activity_home)
public class HomeActivity extends RoboActionBarActivity implements SessionChangedListener,
        NavigationView.OnNavigationItemSelectedListener {


    @InjectView(R.id.navToolbar)
    private Toolbar navToolbar;

    @InjectView(R.id.appbar)
    private AppBarLayout appbarLayout;

    @InjectView(R.id.main_frame)
    private FrameLayout mainFrame;

   /* @InjectView(R.id.spinner)
    private Spinner spinner;*/
    @InjectView(R.id.fab)
    private FloatingActionButton fab;

    @Inject
    private PesquisaProfissionalFragment pesquisaProfissionalFragment;

    @Inject
    private ProfissionaisFavoritosFragment profissionaisFavoritosFragment;

    @Inject
    private MeusDadosFragment meusDadosFragment;

    @Inject
    private MinhasConsultasFragment minhasConsultasFragment;

    private Fragment fragmentAtual;

    @InjectView(R.id.drawer_layout)
    private DrawerLayout drawer;

    @InjectView(R.id.navView)
    private NavigationView navView;

    @Inject
    private LoginService loginService;

    private boolean pausado = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //verificando se o play service está ativado.
        GcmUtils.checkPlayServices(this);

        //iniciando os listeners da Activity
        initListeners();

        //iniciando e configurando o menu
        initMenu();

        //Alguns ajustes na view
        initView();

        //Pós inicio

        initPos();
    }

    private void initPos() {
        if (getIntent() != null && getIntent().getExtras() !=null){
            Integer openFragment = getIntent().getExtras().getInt(RanchucrutesConstants.PARAM_OPEN_FRAGMENT_MAIN_ACTIVITY,R.id.navSearch);
            displayView(openFragment);
        }else{
            displayView(R.id.navSearch);
        }
    }

    private void initListeners() {

        //listeners do itens do menu
        navView.setNavigationItemSelectedListener(this);

        RanchucrutesSession.addSessionChangedListener(this);
        RanchucrutesSession.addSessionChangedListener(meusDadosFragment);

        if (RanchucrutesSession.isUsuarioLogado()){
            RanchucrutesSession.setUsuario(RanchucrutesSession.getUsuario());
        }
    }


    private void initMenu() {
        if (navToolbar != null) {
            setSupportActionBar(navToolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        //TODO MONTAR O SPINNER NO FUTURO. ELE IRA MUDAR A VISAO DO USARIO ENTRE MAPA E LISTA
       /* spinner.setAdapter(new MyAdapter(
                navToolbar.getContext(),
                new String[]{
                        "Mostrar Mapa",
                        "Mostrar Lista",
                }));

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // When the given dropdown item is selected, show its contents in the
                // container view.
               *//* getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                        .commit();*//*
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, navToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


    }

    private void initView() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.frameBusca).setVisibility(View.VISIBLE);
                fab.setVisibility(View.INVISIBLE);
            }
        });
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        //verificando se o menu está aberto
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);

        //verificando se o menu de busca de profissionais está aberto
        }else if (PesquisaProfissionalFragment.class.isInstance(fragmentAtual) &&
                ((PesquisaProfissionalFragment)fragmentAtual).getFrameBusca().getVisibility() == View.VISIBLE){
            ((PesquisaProfissionalFragment)fragmentAtual).getFrameBusca().setVisibility(View.GONE);
            fab.setVisibility(View.VISIBLE);

        //caso contrario chama fragmente principal (mapa).
        } else {
            displayView(R.id.navSearch);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.home, menu);

        onEndConstruct();
        return true;
    }

    private void onEndConstruct() {

        //ajustando a altura do mainFrame.
       /* RelativeLayout.LayoutParams l = (RelativeLayout.LayoutParams) mainFrame.getLayoutParams();
        l.setMargins(0, appbarLayout.getHeight(), 0, 0);
        mainFrame.setLayoutParams(l);*/


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        displayView(id);
        return true;
    }

    /**
     * Diplaying fragment view for selected nav drawer list item
     * */
    public void displayView(int id) {

        //ocultando o fab
        fab.setVisibility( (id == R.id.navSearch || id == R.id.navExit || id == R.id.navEnter) &&
                !this.pesquisaProfissionalFragment.isFrameBuscaVisible()  ? View.VISIBLE : View.INVISIBLE);
        //TODO INATIVO POR ENQUANTO.
        //spinner.setVisibility(id == R.id.navSearch || id == R.id.navExit || id == R.id.navEnter ? View.VISIBLE : View.INVISIBLE);


        MenuItem menuItem = navView.getMenu().findItem(id);
        if (menuItem != null && id != R.id.navExit && id != R.id.navEnter){
            navToolbar.setTitle(menuItem.getTitle());
        }

        switch (id){
            case R.id.navSearch:
                openFragment(this.pesquisaProfissionalFragment);
                break;
            case R.id.navData:
                openFragment(this.meusDadosFragment);
                break;
            case R.id.navAppointment:
                openFragment(this.minhasConsultasFragment);
                break;
            case R.id.navBookmark:
                openFragment(this.profissionaisFavoritosFragment);
                break;
            case R.id.navSetting:
                AndroidUtils.openActivity(this,SettingsActivity.class);
                break;
            case R.id.navExit:
                AndroidUtils.showConfirmDlg("Sair", "Deseja realmente sair do aplicativo?",
                        HomeActivity.this, new DialogCallback() {

                            @Override
                            public void confirm() {
                                loginService.logoff();
                                drawer.closeDrawers();
                                displayView(R.id.navSearch);
                            }

                            @Override
                            public void cancel() {
                                drawer.closeDrawers();
                            }
                        });
                break;
            case R.id.navEnter:
                AndroidUtils.openActivity(this, LoginActivity.class);
                break;
            default:
                break;
        }

        drawer.closeDrawer(GravityCompat.START);

    }

    private void openFragment(Fragment fragment) {
        if (fragment != null){
            FragmentManager fragmentManager = getSupportFragmentManager();

            //removendo o fragmento atual do gerenciador.
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2){

                fragment.setRetainInstance(true);
                fragmentManager.beginTransaction()
                        //.setCustomAnimations(R.anim.fadein, R.anim.fadeout, R.anim.fadein, R.anim.fadeout)
                        .replace(R.id.main_frame, fragment)
                        .commit();

            }
            else{

                fragment.setRetainInstance(true);
                fragmentManager.beginTransaction()
                        .replace(R.id.main_frame, fragment)
                        .commit();
            }
            this.fragmentAtual = fragment;
        }
    }


    @Override
    public void usuarioChange(UsuarioEntity usuario) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //ajustando o menu
                boolean isLogado = RanchucrutesSession.isUsuarioLogado();
                navView.getMenu().findItem(R.id.navExit).setVisible(isLogado);
                navView.getMenu().findItem(R.id.navEnter).setVisible(!isLogado);
                navView.getMenu().setGroupVisible(R.id.navGroupAuth, isLogado);


                TextView tvUserName = (TextView) navView.findViewById(R.id.navUsername);
                if (tvUserName != null) {
                    tvUserName.setText(isLogado ?
                            RanchucrutesSession.getUsuario().getNome() : "Paciente não autenticado");
                }

                TextView tvEmail = (TextView) navView.findViewById(R.id.navEmail);
                if (tvEmail != null) {
                    tvEmail.setText(isLogado ?
                            RanchucrutesSession.getUsuario().getEmail() : "");
                }

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        GcmUtils.checkPlayServices(this);
        if (pausado){
            pausado = false;
            //displayView(R.id.navSearch);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        pausado = true;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,requestCode,data);
        //displayView(0);
    }
}
