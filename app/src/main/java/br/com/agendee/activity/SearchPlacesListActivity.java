package br.com.agendee.activity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.zip.Inflater;

import br.com.agendee.utils.CollectionUtils;
import br.com.agendee.view.SearchingListModel;
import br.com.agendee.R;
import br.com.agendee.activity.callback.DialogCallback;
import br.com.agendee.service.RanchucrutesConstants;
import br.com.agendee.utils.AndroidUtils;
import br.com.agendee.vo.PlacesVo;

/**
 * Created by wagner on 15/03/16.
 */
public class SearchPlacesListActivity extends SearchListActivity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks{

    private GoogleApiClient googleApiClient;
    private static List<SearchingListModel> listCache = new ArrayList<>();
    private final int idFake = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mListFilter = new ArrayList<>();
        mList = new ArrayList<>();
        super.onCreate(savedInstanceState);
        googleApiClient = new GoogleApiClient
                .Builder( this )
                .enableAutoManage( this, 0, this )
                .addApi( Places.GEO_DATA_API )
                .addApi( Places.PLACE_DETECTION_API )
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener( this )
                .build();

        createViewDefault();
    }

    private void createViewDefault() {
        if (CollectionUtils.isEmpty(getListCache())){
            View v = LayoutInflater.from(this).inflate(R.layout.title_view_searchplaces,null);
            clContainer.addView(v);
        }
    }

    @Override
    protected List<SearchingListModel> getListCache() {
        return listCache;
    }

    public void filter( String q ){

        if (q.length() == 0 ){
            pbSearch.setVisibility(View.INVISIBLE);
            return;
        }
        if (q.length() < 5 ){
            mListFilter.clear();
            return;
        }

        mListFilter.clear();
        pbSearch.setVisibility(View.VISIBLE);

        LatLngBounds bounds = new LatLngBounds( new LatLng(-23.769084, -45.458059 ), new LatLng( -23.231440, -47.219174 ) );

        AutocompleteFilter acf = new AutocompleteFilter.Builder()
                .setTypeFilter(Place.TYPE_STREET_ADDRESS)
                .setTypeFilter(Place.TYPE_NEIGHBORHOOD)
                .setTypeFilter(Place.TYPE_POSTAL_CODE)
                .build();


        Places.GeoDataApi.getAutocompletePredictions(googleApiClient, q,
                bounds, acf ).setResultCallback(
                new ResultCallback<AutocompletePredictionBuffer>() {
                    @Override
                    public void onResult(AutocompletePredictionBuffer buffer) {

                        if (buffer == null)
                            return;

                        Log.w("SearchPlaces",buffer.getStatus().getStatusMessage());

                        if (buffer.getStatus().isSuccess()) {
                            for (AutocompletePrediction prediction : buffer) {
                                mListFilter.add(new PlacesVo(prediction.getPlaceId(),prediction.getFullText(null).toString()));
                            }
                        }

                        //removendo a primeira tela
                        if (clContainer.findViewById(R.id.titleViewSearchPlace) != null){
                            clContainer.removeView( clContainer.findViewById(R.id.titleViewSearchPlace) );
                        }

                        mRecyclerView.setVisibility(mListFilter.isEmpty() ? View.GONE : View.VISIBLE);

                        if( mListFilter.isEmpty() ){

                            if (clContainer.findViewById(idFake) == null){
                                TextView tv = new TextView( SearchPlacesListActivity.this );
                                tv.setText( "Nenhum resultado encontrado." );
                                tv.setTextColor( getResources().getColor( android.R.color.black) );
                                tv.setId(idFake);
                                tv.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
                                tv.setGravity(Gravity.CENTER);
                                clContainer.addView(tv);
                            }
                            pbSearch.setVisibility(View.INVISIBLE);
                        }else if( clContainer.findViewById(idFake) != null ) {

                            clContainer.removeView( clContainer.findViewById(idFake) );
                        }

                        pbSearch.setVisibility(View.INVISIBLE);
                        adapter.notifyDataSetChanged();

                        //Prevent memory leak by releasing buffer
                        buffer.release();
                    }
                }, 60, TimeUnit.SECONDS);



    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        if( googleApiClient != null ){
            googleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        if( googleApiClient != null && googleApiClient.isConnected() ) {
            googleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuItem itemMenu = menu.findItem(R.id.searchMyLocation);
        itemMenu.setVisible(true);
        itemMenu.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                boolean isOn = AndroidUtils.gpsActive(SearchPlacesListActivity.this);

                if (!isOn){
                    AndroidUtils.showConfirmDlg("GPS desligado!", "Seu GPS está inativo, deseja ativa-lo agora ?", SearchPlacesListActivity.this, new DialogCallback() {
                        @Override
                        public void confirm() {
                            final Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        }

                        @Override
                        public void cancel() {

                        }
                    });

                }else{
                    setResult(RanchucrutesConstants.FINISH_MY_LOCATION);
                    finish();
                }

                return false;
            }
        });
        return true;
    }

    @Override
    protected boolean isExpandActionView() {
        return false;
    }

    @Override
    protected CharSequence getQueryHint() {
        return "Pesquise um endereço";
    }
}
