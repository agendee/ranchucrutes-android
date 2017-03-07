package br.com.agendee.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import br.com.agendee.adapter.SearchingListAdapter;
import br.com.agendee.listener.RecyclerViewOnClickListenerHack;
import br.com.agendee.service.RanchucrutesConstants;
import br.com.agendee.utils.StringUtils;
import br.com.agendee.view.SearchingListModel;
import br.com.agendee.R;

/**
 * Created by wagner on 02/10/15.
 */
public abstract class SearchListActivity extends AppCompatActivity implements RecyclerViewOnClickListenerHack {

    protected SearchingListAdapter adapter;
    protected CoordinatorLayout clContainer;
    protected LinearLayout pbSearch;
    protected RecyclerView mRecyclerView;
    protected List<SearchingListModel> mList;
    protected List<SearchingListModel> mListCache;
    protected List<SearchingListModel> mListFilter;
    protected String queryText;

    private Toolbar toolbar;

    private MenuItem itemMenu;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchable);
        //==========================

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            mList = bundle.getParcelableArrayList(RanchucrutesConstants.PARAM_LIST_SEARCH);
            if (mList != null){
                mListFilter = cloneList(mList);
            }

            queryText = (String) bundle.getSerializable(RanchucrutesConstants.PARAM_QUERY_TEXT);

            if (StringUtils.isBlank(queryText)){
                queryText = "Pesquise aqui";
            }

            String title = (String) bundle.getSerializable(RanchucrutesConstants.PARAM_TITLE);
            if (StringUtils.isNotBlank(title)){
                toolbar.setTitle(title);
            }

        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().set


        clContainer = (CoordinatorLayout) findViewById(R.id.cl_container);
        pbSearch = (LinearLayout) clContainer.findViewById(R.id.pbSearch);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_list);
        mRecyclerView.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager( this );
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(llm);
        adapter = new SearchingListAdapter(this, mListFilter, getListCache());
        mRecyclerView.setAdapter(adapter);


    }

    protected abstract List<SearchingListModel> getListCache();

    protected List<SearchingListModel> cloneList(List<SearchingListModel> mList) {
        List<SearchingListModel> clone = new ArrayList<>(mList.size());
        return cloneList(mList,clone);

    }

    protected List<SearchingListModel> cloneList(List<SearchingListModel> mList, List<SearchingListModel> clone) {
        for (SearchingListModel s : mList){
            clone.add(s);
        }
        return clone;

    }

    public abstract void filter( String q );



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        itemMenu = menu.findItem(R.id.search);

        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ){
            searchView = (SearchView) itemMenu.getActionView();
        }
        else{
            searchView = (SearchView) MenuItemCompat.getActionView( itemMenu );
        }

        ImageView closeButton = (ImageView) searchView.findViewById(android.support.v7.appcompat.R.id.search_close_btn);
        closeButton.setImageResource(R.drawable.ic_close_white_18dp);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint(getQueryHint());
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

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

        searchView.setIconifiedByDefault(false);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
        searchView.setLayoutParams(params);

        if(isExpandActionView()){
            itemMenu.expandActionView();
            searchView.onActionViewExpanded();
        }

        return true;
    }

    protected abstract boolean isExpandActionView();

    protected abstract CharSequence getQueryHint();

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == android.R.id.home){
            finish();
        }
        return true;
    }


    @Override
    public void onClickListener(View view, int position) {
        Intent intent = new Intent(this, SearchingListModel.class);
        intent.putExtra("car", mListFilter.get(position));

        // TRANSITIONS
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ){

        }
        else{
            startActivity(intent);
        }
    }

    @Override
    public void onLongPressClickListener(View view, int position) {}


}
