package br.com.wjaa.ranchucrutes.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.inject.Inject;

import br.com.wjaa.ranchucrutes.R;
import br.com.wjaa.ranchucrutes.fragment.MedicoFavoritoFragment;
import br.com.wjaa.ranchucrutes.fragment.MeusDadosFragment;
import roboguice.activity.RoboActionBarActivity;
import roboguice.activity.RoboFragmentActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

/**
 * Created by wagner on 04/08/15.
 */
@ContentView(R.layout.main)
public class MainActivity extends RoboActionBarActivity {

    @InjectView(R.id.toolbar)
    private Toolbar toolbar;
    @InjectView(R.id.drawerLayout)
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;

    @InjectView(R.id.left_drawer)
    private ListView leftDrawerList;

    private ArrayAdapter<String> navigationDrawerAdapter;
    private String[] leftSliderData = {"Meus Dados", "Minhas Consultas", "Médicos Favoritos", "Fazer Login"};

    @Inject
    private HomeActivity home;

    @Inject
    private MedicoFavoritoFragment medicoFavorito;

    @Inject
    private MeusDadosFragment meusDadosFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.main);
        nitView();
        if (toolbar != null) {
            toolbar.setTitle("MarcMed");
            setSupportActionBar(toolbar);
        }
        initDrawer();
        displayView(0);
    }

    private void nitView() {
        //leftDrawerList = (ListView) findViewById(R.id.left_drawer);
        //toolbar = (Toolbar) findViewById(R.id.toolbar);
        //drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        navigationDrawerAdapter=new ArrayAdapter<String>( MainActivity.this, android.R.layout.simple_list_item_1, leftSliderData);
        leftDrawerList.setAdapter(navigationDrawerAdapter);
        //leftDrawerList.setOnClickListener();
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
    private void displayView(int position) {
        // update the main content by replacing fragments
        Fragment fragment = null;
        switch (position){
            case 0:
                fragment = home;
                break;
            case 1:
                fragment = meusDadosFragment;
                break;
            case 2:
                fragment = medicoFavorito;
                break;
            /*
            case 3:
                fragment = new EventsFragment();
                break;
            case 4:
                fragment = new PlacesFragment();
                break;
            case 5:
                fragment = new AdsFragment();
                break;
            */
            default:
                break;
        }

        if (fragment != null){
            FragmentManager fragmentManager = getFragmentManager();

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2){
                fragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.fadein, R.anim.fadeout, R.anim.fadein, R.anim.fadeout)
                        .replace(R.id.main_frame, fragment).commit();
            }
            else{
                fragmentManager.beginTransaction().replace(R.id.main_frame, fragment).commit();
            }

            // update selected item and title, then close the drawer
            //mDrawerList.setItemChecked(position, true);
            //mDrawerList.setSelection(position);
            //setTitle(navMenuTitles[position]);
            //mDrawerLayout.closeDrawer(mDrawerList);
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
        if (id == R.id.action_settings) {
            return true;
        }
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

