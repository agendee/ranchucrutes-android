package br.com.wjaa.ranchucrutes.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.ThemedSpinnerAdapter;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
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

@ContentView(R.layout.activity_home)
public class HomeActivity extends RoboActionBarActivity implements SessionChangedListener,
        NavigationView.OnNavigationItemSelectedListener {


    @InjectView(R.id.navToolbar)
    private Toolbar navToolbar;

    @InjectView(R.id.appbar)
    private AppBarLayout appbarLayout;

    @InjectView(R.id.main_frame)
    private FrameLayout mainFrame;

    @InjectView(R.id.spinner)
    private Spinner spinner;

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


    @Inject
    private LoginService loginService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        RanchucrutesSession.addSessionChangedListener(this);
        RanchucrutesSession.addSessionChangedListener(meusDadosFragment);
        initView();

        //verificando se o play service está ativado.
        GcmUtils.checkPlayServices(this);

        if (navToolbar != null) {
            setSupportActionBar(navToolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        //initDrawer();

        initMenu();

        if (getIntent() != null && getIntent().getExtras() !=null){
            Integer openFragment = getIntent().getExtras().getInt(RanchucrutesConstants.PARAM_OPEN_FRAGMENT_MAIN_ACTIVITY,R.id.navSearch);
            displayView(openFragment);
        }else{
            displayView(R.id.navSearch);
        }

        // Setup spinner

        spinner.setAdapter(new MyAdapter(
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
               /* getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                        .commit();*/
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });




        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, navToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        navToolbar.invalidate();
        appbarLayout.invalidate();


    }


    private void initMenu() {
        if (RanchucrutesSession.isUsuarioLogado()){
            RanchucrutesSession.setUsuario(RanchucrutesSession.getUsuario());
        }
    }

    private void initView() {
       /* navigationDrawerAdapter = new MenuDefaultArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1);
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
        });*/
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        DrawerLayout.LayoutParams l = (DrawerLayout.LayoutParams) mainFrame.getLayoutParams();

        l.setMargins(0, appbarLayout.getHeight(), 0, 0);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
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



    private static class MyAdapter extends ArrayAdapter<String> implements ThemedSpinnerAdapter {
        private final ThemedSpinnerAdapter.Helper mDropDownHelper;

        public MyAdapter(Context context, String[] objects) {
            super(context, android.R.layout.simple_list_item_1, objects);
            mDropDownHelper = new ThemedSpinnerAdapter.Helper(context);
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            View view;

            if (convertView == null) {
                // Inflate the drop down using the helper's LayoutInflater
                LayoutInflater inflater = mDropDownHelper.getDropDownViewInflater();
                view = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
            } else {
                view = convertView;
            }

            TextView textView = (TextView) view.findViewById(android.R.id.text1);
            textView.setText(getItem(position));

            return view;
        }

        @Override
        public Resources.Theme getDropDownViewTheme() {
            return mDropDownHelper.getDropDownViewTheme();
        }

        @Override
        public void setDropDownViewTheme(Resources.Theme theme) {
            mDropDownHelper.setDropDownViewTheme(theme);
        }
    }


    /**
     * Diplaying fragment view for selected nav drawer list item
     * */
    public void displayView(int id) {

        //TODO ARRUMAR ESSA GAMBIARRA
        // update the main content by replacing fragments
        switch (id){
            case R.id.navSearch:
                navToolbar.setTitle("");
                spinner.setVisibility(View.VISIBLE);
                openFragment(this.pesquisaProfissionalFragment);
                break;
            case R.id.navData:
                spinner.setVisibility(View.INVISIBLE);
                navToolbar.setTitle("Meus Dados");
                openFragment(this.meusDadosFragment);
                break;
            case R.id.navAppointment:
                spinner.setVisibility(View.INVISIBLE);
                openFragment(this.minhasConsultasFragment);
                navToolbar.setTitle("Minhas Consultas");
                break;
            case R.id.navBookmark:
                spinner.setVisibility(View.INVISIBLE);
                openFragment(this.profissionaisFavoritosFragment);
                navToolbar.setTitle("Profissionais Favoritos");
                break;
            case R.id.navExit:
                spinner.setVisibility(View.INVISIBLE);
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
                spinner.setVisibility(View.INVISIBLE);
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



            // update selected item and title, then close the drawer
            //mDrawerList.setItemChecked(position, true);
            //mDrawerList.setSelection(position);
            //setTitle(navMenuTitles[position]);
            //mDrawerLayout.closeDrawer(mDrawerList);
            //this.drawer.closeDrawers();
            this.fragmentAtual = fragment;
        }
    }


    @Override
    public void usuarioChange(UsuarioEntity usuario) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (RanchucrutesSession.isUsuarioLogado()){
                    ((TextView)findViewById(R.id.navUsername)).setText(RanchucrutesSession.getUsuario().getNome());
                    ((TextView)findViewById(R.id.navEmail)).setText(RanchucrutesSession.getUsuario().getEmail());
                 //   findViewById(R.id.navExit).setVisibility(View.VISIBLE);
                  //  findViewById(R.id.navEnter).setVisibility(View.INVISIBLE);
                  // findViewById(R.id.navGroupAuth).setVisibility(View.VISIBLE);


                }else{
                    ((TextView)findViewById(R.id.navUsername)).setText("Paciente não autenticado");
                    ((TextView)findViewById(R.id.navEmail)).setText("");
                   // findViewById(R.id.navExit).setVisibility(View.INVISIBLE);
                   // findViewById(R.id.navEnter).setVisibility(View.VISIBLE);
                   // findViewById(R.id.navGroupAuth).setVisibility(View.INVISIBLE);
                }
            }
        });

    }


    /*class MenuLogadoArrayAdapter extends ArrayAdapter<String> implements AdapterView.OnItemClickListener {

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
    }*/


}
