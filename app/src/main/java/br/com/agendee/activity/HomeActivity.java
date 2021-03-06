package br.com.agendee.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.inject.Inject;

import br.com.agendee.R;
import br.com.agendee.activity.callback.DialogCallback;
import br.com.agendee.buffer.RanchucrutesBuffer;
import br.com.agendee.buffer.RanchucrutesSession;
import br.com.agendee.entity.PacienteEntity;
import br.com.agendee.fragment.MeusDadosFragment;
import br.com.agendee.fragment.MinhasConsultasFragment;
import br.com.agendee.fragment.PesquisaProfissionalFragment;
import br.com.agendee.fragment.ProfissionaisFavoritosFragment;
import br.com.agendee.listener.SessionChangedListener;
import br.com.agendee.service.LoginService;
import br.com.agendee.service.RanchucrutesConstants;
import br.com.agendee.utils.AndroidUtils;
import br.com.agendee.utils.GcmUtils;
import br.com.agendee.utils.ImageUtils;
import br.com.agendee.utils.StringUtils;
import roboguice.activity.RoboActionBarActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

/**
 * @author Wagner Jeronimo
 */
@ContentView(R.layout.activity_home)
public class HomeActivity extends RoboActionBarActivity implements SessionChangedListener,
        NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "HomeActivity";
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

    @Inject
    private RanchucrutesBuffer buffer;

    private MenuItem menuItem;

    private boolean isNotActivityResult = true;

    @Override
    protected void onStart() {
        super.onStart();
        if ( AndroidUtils.internetNotActive(this) ) {
            AndroidUtils.showMessageErroDlg("Você está offline, alguns recursos podem não funcionar.",this);
        }

        if (RanchucrutesSession.isUsuarioLogado() && isNotActivityResult){
            AndroidUtils.closeWaitDlg();
            Log.i(TAG,"chamando o aguarde...");

            new Thread(){
                @Override
                public void run() {
                    try {
                        AndroidUtils.showWaitDlgOnUiThread("Aguarde...",HomeActivity.this);
                        Thread.sleep(2000);
                        runOnUiThread(new Runnable(){
                            @Override
                            public void run() {
                                usuarioChange(RanchucrutesSession.getUsuario());
                                if (getIntent() != null && getIntent().getExtras() !=null){
                                    Integer openFragment = getIntent().getExtras().getInt(RanchucrutesConstants.PARAM_OPEN_FRAGMENT_MAIN_ACTIVITY,R.id.navSearch);
                                    displayView(openFragment);
                                }else{
                                    displayView(R.id.navSearch);
                                }
                                Log.i(TAG,"chamando o close wait");
                                AndroidUtils.closeWaitDlg();
                            }
                        });


                    } catch (InterruptedException e) {
                        AndroidUtils.closeWaitDlg();
                        e.printStackTrace();
                    }
                }
            }.start();
        }if (isNotActivityResult){
            displayView(R.id.navSearch);
        }

        this.isNotActivityResult = true;
    }

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
        //TODO se o buffer estiver vazio eh pq o app saiu do estado de sleep e nao conseguiu carregar o buffer e nem logar.
        if ( buffer.empty() ){
            AndroidUtils.closeWaitDlg();
            AndroidUtils.showWaitDlg("Aguarde...",this);
            buffer.initializer();

            //tentando logar o usuário novamente.
            buffer.posInitializer(this);

            //aguardando 3s para tirar o aguarde. Tempo suficiente para as thread de carregamento do buffer
            //e de login terminarem o trabalho
            new Thread(){
                @Override
                public void run() {
                    try {
                        Thread.sleep(3000);
                        AndroidUtils.closeWaitDlg();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }


    }

    private void initListeners() {
        //listeners do itens do menu
        navView.setNavigationItemSelectedListener(this);
        RanchucrutesSession.addSessionChangedListener(this);
    }




    private void initMenu() {
        if (navToolbar != null) {
            setSupportActionBar(navToolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
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
                pesquisaProfissionalFragment.openDialogChooseProfissao();
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
        }else if (PesquisaProfissionalFragment.class.isInstance(fragmentAtual) ) {
            AndroidUtils.showConfirmDlg("Sair","Deseja realmente sair?",this, new DialogCallback(){
                @Override
                public void confirm() {
                    finish();
                }

                @Override
                public void cancel() {
                    fab.setVisibility(View.VISIBLE);
                }
            });

        //caso contrario chama fragmente principal (mapa).
        } else {
            displayView(R.id.navSearch);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.search) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        this.menuItem = menu.findItem(R.id.search);

        this.menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                pesquisaProfissionalFragment.openDialogFindEspecialidade();
                return true;
            }
        });
        this.menuItem.setVisible(false);


        return true;
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
        this.fab.setVisibility( (id == R.id.navSearch || id == R.id.navExit || id == R.id.navEnter || id == R.id.navShare) ? View.VISIBLE : View.INVISIBLE);
        if (this.menuItem != null){
            this.menuItem.setVisible(this.fab.getVisibility() == View.VISIBLE);
        }
        //TODO INATIVO POR ENQUANTO.
        //spinner.setVisibility(id == R.id.navSearch || id == R.id.navExit || id == R.id.navEnter ? View.VISIBLE : View.INVISIBLE);


        MenuItem navItem = navView.getMenu().findItem(id);
        if (navItem != null && id != R.id.navExit && id != R.id.navEnter && id != R.id.navSetting && id != R.id.navShare){
            navToolbar.setTitle(navItem.getTitle());
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
                AndroidUtils.openActivity(this,SettingsActivity.class, RanchucrutesConstants.FINISH_TO_OPEN_HOME);
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
            case R.id.navShare:
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=br.com.agendee");
                shareIntent.putExtra(Intent.EXTRA_TITLE, "Agendee - aplicativo de agendamento.");
                startActivity(Intent.createChooser(shareIntent, "Compartilhe o Agendee"));
                break;

            default:
                break;
        }

        drawer.closeDrawer(GravityCompat.START);

    }

    private void openFragment(Fragment fragment) {
        if (fragment != null){
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragment.setRetainInstance(true);
            fragmentManager.beginTransaction()
            .replace(R.id.main_frame, fragment)
            .commit();
            this.fragmentAtual = fragment;
        }
    }


    @Override
    public void usuarioChange(final PacienteEntity usuario) {
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


                ImageView fotoUser = (ImageView) navView.findViewById(R.id.navFotoUser);
                if (isLogado && StringUtils.isNotBlank(RanchucrutesSession.getUsuario().getUrlFoto())){
                    ImageUtils.loadImage(HomeActivity.this,fotoUser,usuario.getUrlFoto());
                }else{
                    fotoUser.setImageDrawable(getResources().getDrawable(R.drawable.unknow));
                }

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        GcmUtils.checkPlayServices(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        this.isNotActivityResult = false;

        if (resultCode == RESULT_CANCELED){
            return;
        }

        if (requestCode == RanchucrutesConstants.FINISH_TO_OPEN_HOME){
            displayView(R.id.navSearch);
        }else if (requestCode == RanchucrutesConstants.FINISH_CONFIRME_AGENDAMENTO_OPEN_LIST){
            displayView(R.id.navAppointment);
        }

    }
}
