package br.com.wjaa.ranchucrutes.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.com.wjaa.ranchucrutes.R;
import br.com.wjaa.ranchucrutes.adapter.SearchingListAdapter;
import br.com.wjaa.ranchucrutes.listener.RecyclerViewOnClickListenerHack;
import br.com.wjaa.ranchucrutes.service.RanchucrutesConstants;
import br.com.wjaa.ranchucrutes.utils.StringUtils;
import br.com.wjaa.ranchucrutes.view.SearchingListModel;

/**
 * Created by wagner on 02/10/15.
 */
public class SearchingListActivity extends AppCompatActivity implements RecyclerViewOnClickListenerHack {

    private SearchingListAdapter adapter;
    private CoordinatorLayout clContainer;
    private RecyclerView mRecyclerView;
    private List<SearchingListModel> mList;
    private List<SearchingListModel> mListFilter;

    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchable);
        //==========================

        toolbar = (Toolbar) findViewById(R.id.tb_main);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            mList = bundle.getParcelableArrayList(RanchucrutesConstants.PARAM_LIST_SEARCH);
            mListFilter = cloneList(mList);
        }

        clContainer = (CoordinatorLayout) findViewById(R.id.cl_container);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_list);
        mRecyclerView.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager( this );
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(llm);


        adapter = new SearchingListAdapter(this, mListFilter);
        //adapter.setRecyclerViewOnClickListenerHack(this);
        mRecyclerView.setAdapter(adapter);

    }

    private List<SearchingListModel> cloneList(List<SearchingListModel> mList) {
        List<SearchingListModel> clone = new ArrayList<>(mList.size());
        return cloneList(mList,clone);

    }

    private List<SearchingListModel> cloneList(List<SearchingListModel> mList, List<SearchingListModel> clone) {
        for (SearchingListModel s : mList){
            clone.add(s);
        }
        return clone;

    }

    public void filter( String q ){
        mListFilter.clear();
        if (StringUtils.isBlank(q)){
            cloneList(mList, mListFilter);

            adapter.notifyDataSetChanged();
            return;
        }

        for( int i = 0, tamI = mList.size(); i < tamI; i++ ){
            if(StringUtils.isNotBlank(mList.get(i).getName()) && mList.get(i).getName().toLowerCase().contains( q.toLowerCase() ) ){
                mListFilter.add( mList.get(i) );
            }
        }

        mRecyclerView.setVisibility(mListFilter.isEmpty() ? View.GONE : View.VISIBLE);
        if( mListFilter.isEmpty() ){

            if (clContainer.findViewById(1) == null){
                TextView tv = new TextView( this );
                tv.setText( "Nenhum resultado encontrado." );
                tv.setTextColor( getResources().getColor( R.color.primaryColor ) );
                tv.setId(1);
                tv.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
                tv.setGravity(Gravity.CENTER);

                clContainer.addView(tv);
            }
        }
        else if( clContainer.findViewById(1) != null ) {
            clContainer.removeView( clContainer.findViewById(1) );
        }

       adapter.notifyDataSetChanged();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView;
        MenuItem item = menu.findItem(R.id.search);

        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ){
            searchView = (SearchView) item.getActionView();
        }
        else{
            searchView = (SearchView) MenuItemCompat.getActionView( item );
        }

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint("Pesquise aqui");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return false;
            }
        });


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == android.R.id.home){
            finish();
        }
        /*else if( id == R.id.action_delete ){
            SearchRecentSuggestions searchRecentSuggestions = new SearchRecentSuggestions(this,
                    SearchableProvider.AUTHORITY,
                    SearchableProvider.MODE);

            searchRecentSuggestions.clearHistory();

            Toast.makeText(this, "Cookies removidos", Toast.LENGTH_SHORT).show();
        }*/

        return true;
    }


    @Override
    public void onClickListener(View view, int position) {
        Intent intent = new Intent(this, SearchingListModel.class);
        intent.putExtra("car", mListFilter.get(position));

        // TRANSITIONS
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ){

           /* View ivCar = view.findViewById(R.id.tviv_car);
            View tvModel = view.findViewById(R.id.tv_model);
            View tvBrand = view.findViewById(R.id.tv_brand);

            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                    Pair.create(ivCar, "element1"),
                    Pair.create( tvModel, "element2" ),
                    Pair.create( tvBrand, "element3" ));*/

            //*startActivity(intent, options.toBundle() );
        }
        else{
            startActivity(intent);
        }
    }

    @Override
    public void onLongPressClickListener(View view, int position) {}


}
