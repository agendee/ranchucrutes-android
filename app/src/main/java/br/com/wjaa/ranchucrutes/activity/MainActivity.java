package br.com.wjaa.ranchucrutes.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import br.com.wjaa.ranchucrutes.service.RanchucrutesConstants;
import br.com.wjaa.ranchucrutes.utils.AndroidSystemUtil;
import br.com.wjaa.ranchucrutes.utils.GcmUtils;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.inject.Inject;

import br.com.wjaa.ranchucrutes.R;
import br.com.wjaa.ranchucrutes.activity.callback.DialogCallback;
import br.com.wjaa.ranchucrutes.buffer.RanchucrutesSession;
import br.com.wjaa.ranchucrutes.entity.UsuarioEntity;
import br.com.wjaa.ranchucrutes.fragment.PesquisaProfissionalFragment;
import br.com.wjaa.ranchucrutes.fragment.MinhasConsultasFragment;
import br.com.wjaa.ranchucrutes.fragment.MeusDadosFragment;
import br.com.wjaa.ranchucrutes.fragment.ProfissionaisFavoritosFragment;
import br.com.wjaa.ranchucrutes.listener.SessionChangedListener;
import br.com.wjaa.ranchucrutes.service.LoginService;
import br.com.wjaa.ranchucrutes.utils.AndroidUtils;
import br.com.wjaa.ranchucrutes.utils.StringUtils;
import roboguice.activity.RoboActionBarActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

/**
 * Created by wagner on 04/08/15.
 */
@ContentView(R.layout.main)
public class MainActivity extends RoboActionBarActivity implements SessionChangedListener{

    @InjectView(R.id.toolbar)
    private Toolbar toolbar;
    @InjectView(R.id.drawerLayout)
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;

    @InjectView(R.id.left_drawer)
    private ListView leftDrawerList;

    private ArrayAdapter<String> navigationDrawerAdapter;

    @Inject
    private PesquisaProfissionalFragment pesquisaProfissionalFragment;

    @Inject
    private ProfissionaisFavoritosFragment profissionaisFavoritosFragment;

    @Inject
    private MeusDadosFragment meusDadosFragment;

    @Inject
    private MinhasConsultasFragment minhasConsultasFragment;

    @InjectView(R.id.headerView)
    private TextView headerView;

    @InjectView(R.id.footerView)
    private TextView footerView;

    private Fragment fragmentAtual;

    @Inject
    private LoginService loginService;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);


        RanchucrutesSession.addSessionChangedListener(this);
        RanchucrutesSession.addSessionChangedListener(meusDadosFragment);
        initView();

        //verificando se o play service está ativado.
        GcmUtils.checkPlayServices(this);

        if (toolbar != null) {
            toolbar.setTitle("Agendee");
            setSupportActionBar(toolbar);
        }
        initDrawer();

        if (getIntent() != null && getIntent().getExtras() !=null){
            Integer openFragment = getIntent().getExtras().getInt(RanchucrutesConstants.PARAM_OPEN_FRAGMENT_MAIN_ACTIVITY,0);
            displayView(openFragment);
        }else{
            displayView(0);
        }
    }

    private void initView() {
        navigationDrawerAdapter = new MenuDefaultArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1);
        leftDrawerList.setAdapter(navigationDrawerAdapter);
        leftDrawerList.setOnItemClickListener((MenuDefaultArrayAdapter) navigationDrawerAdapter);
        footerView.setText(Html.fromHtml("<u>" + footerView.getText() + "</u>"));
        footerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "http://www.agendee.com.br/profissional/cadastro";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
    }

    private void initDrawer() {

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

            }
        };
        drawerLayout.setDrawerListener(drawerToggle);


    }

    /**
     * Diplaying fragment view for selected nav drawer list item
     * */
    public void displayView(int position) {

        //TODO ARRUMAR ESSA GAMBIARRA
        // update the main content by replacing fragments
        switch (position){
            case 0:
                toolbar.setTitle("Procurar Profissional");
                openFragment(this.pesquisaProfissionalFragment);
                break;
            case 1:
                toolbar.setTitle("Meus Dados");
                openFragment(this.meusDadosFragment);
                break;
            case 2:
                openFragment(this.minhasConsultasFragment);
                toolbar.setTitle("Minhas Consultas");
                break;
            case 3:
                openFragment(this.profissionaisFavoritosFragment);
                toolbar.setTitle("Profissionais Favoritos");
                break;
            case 4:

                AndroidUtils.openActivity(this,LoginActivity.class);
                break;
            default:
                break;
        }

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



            // update selected item and title, then close the drawer
            //mDrawerList.setItemChecked(position, true);
            //mDrawerList.setSelection(position);
            //setTitle(navMenuTitles[position]);
            //mDrawerLayout.closeDrawer(mDrawerList);
            this.drawerLayout.closeDrawers();
            this.fragmentAtual = fragment;
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        /*if (id == R.id.action_settings) {
            return true;
        }
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }*/
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onResume() {
        super.onResume();
        GcmUtils.checkPlayServices(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

    }
    @Override
    public void onBackPressed() {
        displayView(0);
    }

    @Override
    public void usuarioChange(UsuarioEntity usuario) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (RanchucrutesSession.isUsuarioLogado()){
                    headerView.setText("  Olá " + RanchucrutesSession.getUsuario().getNome());
                    navigationDrawerAdapter = new MenuLogadoArrayAdapter( MainActivity.this, android.R.layout.simple_list_item_1);

                }else{
                    headerView.setText("  Paciente não autenticado");
                    navigationDrawerAdapter = new MenuDefaultArrayAdapter( MainActivity.this, android.R.layout.simple_list_item_1);
                }
                leftDrawerList.setAdapter(navigationDrawerAdapter);
                leftDrawerList.setOnItemClickListener((AdapterView.OnItemClickListener) navigationDrawerAdapter);
                leftDrawerList.refreshDrawableState();
            }
        });

    }


    class MenuLogadoArrayAdapter extends ArrayAdapter<String> implements AdapterView.OnItemClickListener {

        public MenuLogadoArrayAdapter(Context context, int resource) {
            super(context, resource, new String[]{"Pesquisar Profissionais", "Meus Dados", "Minhas Consultas", "Profissionais Favoritos","Logout (Sair)"});
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View v = super.getView(position, convertView, parent);


            return v;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (id != 4){
                displayView(position);
            }else{

                AndroidUtils.showConfirmDlg("Sair", "Deseja realmente sair do aplicativo?",
                        MainActivity.this, new DialogCallback() {

                    @Override
                    public void confirm() {
                        loginService.logoff();
                        drawerLayout.closeDrawers();
                        displayView(0);
                    }

                    @Override
                    public void cancel() {
                        drawerLayout.closeDrawers();
                    }
                });


            }
        }
    }

    class MenuDefaultArrayAdapter extends ArrayAdapter<String> implements AdapterView.OnItemClickListener{

        public MenuDefaultArrayAdapter(Context context, int resource) {
            super(context, resource, new String[]{"Pesquisar Profissionais","Fazer Login"});
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //TODO ARRUMAR ESSA GAMBETA
            if (position == 1){
                displayView(4);
            }else{
                displayView(position);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,requestCode,data);
        //displayView(0);
    }


}

