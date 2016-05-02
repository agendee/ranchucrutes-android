package br.com.wjaa.ranchucrutes.activity;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import br.com.wjaa.ranchucrutes.R;
import br.com.wjaa.ranchucrutes.service.RanchucrutesConstants;
import br.com.wjaa.ranchucrutes.utils.StringUtils;
import br.com.wjaa.ranchucrutes.vo.PlacesVo;

/**
 * Created by wagner on 15/03/16.
 */
public class SearchPlacesListActivity extends SearchListActivity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks{

    private GoogleApiClient googleApiClient;


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

    }

    public void filter( String q ){

        if (q.length() < 5 ){
            mListFilter.clear();
            return;
        }

        mListFilter.clear();
        LatLngBounds bounds = new LatLngBounds( new LatLng(-23.769084, -45.458059 ), new LatLng( -23.231440, -47.219174 ) );
        List<Integer> filterTypes = new ArrayList<Integer>();
        filterTypes.add(Place.TYPE_STREET_ADDRESS);
        filterTypes.add(Place.TYPE_NEIGHBORHOOD);
        filterTypes.add(Place.TYPE_POSTAL_CODE);


        Places.GeoDataApi.getAutocompletePredictions(googleApiClient, q,
                bounds, AutocompleteFilter.create(filterTypes)).setResultCallback(
                new ResultCallback<AutocompletePredictionBuffer>() {
                    @Override
                    public void onResult(AutocompletePredictionBuffer buffer) {

                        if (buffer == null)
                            return;

                        Log.w("SearchPlaces",buffer.getStatus().getStatusMessage());

                        if (buffer.getStatus().isSuccess()) {
                            for (AutocompletePrediction prediction : buffer) {
                                mListFilter.add(new PlacesVo(prediction.getPlaceId(),prediction.getDescription()));
                            }
                        }

                        mRecyclerView.setVisibility(mListFilter.isEmpty() ? View.GONE : View.VISIBLE);

                        if( mListFilter.isEmpty() ){

                            if (clContainer.findViewById(1) == null){
                                TextView tv = new TextView( SearchPlacesListActivity.this );
                                tv.setText( "Nenhum resultado encontrado." );
                                tv.setTextColor( getResources().getColor( android.R.color.black) );
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
        if( googleApiClient != null )
            googleApiClient.connect();
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
                setResult(RanchucrutesConstants.FINISH_MY_LOCATION);
                finish();
                return false;
            }
        });
        return true;
    }

    @Override
    protected CharSequence getQueryHint() {
        return "Pesquise um endereço";
    }
}
