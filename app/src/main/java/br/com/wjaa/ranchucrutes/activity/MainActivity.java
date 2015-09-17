package br.com.wjaa.ranchucrutes.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.inject.Inject;

import br.com.wjaa.ranchucrutes.R;
import br.com.wjaa.ranchucrutes.buffer.RanchucrutesSession;
import br.com.wjaa.ranchucrutes.fragment.BuscaFragment;
import br.com.wjaa.ranchucrutes.fragment.ConsultasFragment;
import br.com.wjaa.ranchucrutes.fragment.DadosUsuarioFragment;
import br.com.wjaa.ranchucrutes.fragment.FavoritosFragment;
import br.com.wjaa.ranchucrutes.fragment.LoginFragment;
import br.com.wjaa.ranchucrutes.listener.SessionChangedListener;
import br.com.wjaa.ranchucrutes.vo.PacienteVo;
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
    private String[] menuSlider = {"Pesquisar Médicos","Meus Dados", "Minhas Consultas", "Médicos Favoritos", "Fazer Login"};

    @Inject
    private BuscaFragment buscaFragment;

    @Inject
    private FavoritosFragment favoritosFragment;

    @Inject
    private DadosUsuarioFragment dadosUsuarioFragment;

    @Inject
    private ConsultasFragment consultasFragment;

    @Inject
    private LoginFragment loginFragment;

    @InjectView(R.id.headerView)
    private TextView headerView;

    @InjectView(R.id.footerView)
    private TextView footerView;

    private Fragment fragmentAtual;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        RanchucrutesSession.addSessionChangedListener(this);
        RanchucrutesSession.addSessionChangedListener(dadosUsuarioFragment);
        initView();
        if (toolbar != null) {
            toolbar.setTitle("MarcMed");
            setSupportActionBar(toolbar);
        }
        initDrawer();
        displayView(0);
    }

    private void initView() {
        navigationDrawerAdapter=new ArrayAdapter<String>( MainActivity.this, android.R.layout.simple_list_item_1, menuSlider);
        leftDrawerList.setAdapter(navigationDrawerAdapter);
        leftDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                displayView(position);
            }
        });
        getFragmentManager().addOnBackStackChangedListener(
                new FragmentManager.OnBackStackChangedListener() {
                    @Override
                    public void onBackStackChanged() {

                        syncActionBarArrowState();
                    }
                }
        );

        footerView.setText(Html.fromHtml("<u>" + footerView.getText() + "</u>"));
        footerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "http://www.marcmed.com.br/medico/cadastro";
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
        // update the main content by replacing fragments
        Fragment fragment = null;

        switch (position){
            case 0:
                fragment = this.buscaFragment;
                toolbar.setTitle("Procurar Médico");
                break;
            case 1:
                fragment = this.dadosUsuarioFragment;
                toolbar.setTitle("Meus Dados");
                break;
            case 2:
                fragment = this.consultasFragment;
                toolbar.setTitle("Minhas Consultas");
                break;
            case 3:
                fragment = this.favoritosFragment;
                toolbar.setTitle("Médicos Favoritos");
                break;
            case 4:
                fragment = this.loginFragment;
                toolbar.setTitle("Fazer Login");
                break;
            default:
                break;
        }

        if (fragment != null){
            FragmentManager fragmentManager = getFragmentManager();

            //removendo o fragmento atual do gerenciador.
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2){
                fragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.fadein, R.anim.fadeout, R.anim.fadein, R.anim.fadeout)
                        .replace(R.id.main_frame, fragment,fragment.getClass().getSimpleName())
                        .commit();
            }
            else{
                fragmentManager.beginTransaction()
                        .replace(R.id.main_frame, fragment,fragment.getClass().getSimpleName())
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

    private void syncActionBarArrowState() {
        int backStackEntryCount = getSupportFragmentManager().getBackStackEntryCount();
        drawerToggle.setDrawerIndicatorEnabled(backStackEntryCount == 0);
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
    }

    @Override
    protected void onPause() {
        super.onPause();

    }
    @Override
    public void onBackPressed() {
        final BuscaFragment fragment = (BuscaFragment) getFragmentManager().findFragmentByTag(BuscaFragment.class.getSimpleName());

        /*if (fragment.allowBackPressed()) { // and then you define a method allowBackPressed with the logic to allow back pressed or not
            super.onBackPressed();
        }*/
        Toast.makeText(this,"sair sair sair",Toast.LENGTH_SHORT);
    }

    @Override
    public void pacienteChange(PacienteVo paciente) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                headerView.setText("Olá " + RanchucrutesSession.getPaciente().getNome());
                menuSlider = new String[]{"Pesquisar Médicos", "Meus Dados", "Minhas Consultas", "Médicos Favoritos"};
                navigationDrawerAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, menuSlider);
                leftDrawerList.setAdapter(navigationDrawerAdapter);
                leftDrawerList.refreshDrawableState();
            }
        });

    }
}
