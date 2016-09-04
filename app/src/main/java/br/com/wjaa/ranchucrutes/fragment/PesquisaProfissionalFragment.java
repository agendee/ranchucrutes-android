package br.com.wjaa.ranchucrutes.fragment;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.inject.Inject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import br.com.wjaa.ranchucrutes.R;
import br.com.wjaa.ranchucrutes.activity.SearchGenericListActivity;
import br.com.wjaa.ranchucrutes.activity.SearchPlacesListActivity;
import br.com.wjaa.ranchucrutes.buffer.RanchucrutesBuffer;
import br.com.wjaa.ranchucrutes.exception.ProfissionalServiceException;
import br.com.wjaa.ranchucrutes.listener.OnMapReadyFinish;
import br.com.wjaa.ranchucrutes.maps.RanchucrutesMaps;
import br.com.wjaa.ranchucrutes.service.ProfissionalService;
import br.com.wjaa.ranchucrutes.service.RanchucrutesConstants;
import br.com.wjaa.ranchucrutes.utils.AndroidUtils;
import br.com.wjaa.ranchucrutes.utils.CollectionUtils;
import br.com.wjaa.ranchucrutes.view.SearchingListModel;
import br.com.wjaa.ranchucrutes.vo.EspecialidadeVo;
import br.com.wjaa.ranchucrutes.vo.LocationVo;
import br.com.wjaa.ranchucrutes.vo.PlacesVo;
import br.com.wjaa.ranchucrutes.vo.ResultadoBuscaClinicaVo;
import roboguice.fragment.RoboFragment;
import roboguice.util.RoboAsyncTask;

/**
 * @author Wagner Jeronimo
 */
public class PesquisaProfissionalFragment extends RoboFragment implements
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        OnMapReadyFinish {

    private static final String TAG = PesquisaProfissionalFragment.class.getSimpleName();
    private EspecialidadeVo especSelecionada;
    private PlacesVo placeVo;
    @Inject
    private ProfissionalService profissionalService;
    @Inject
    private RanchucrutesBuffer buffer;

    private EspecialidadeVo[] especialidades;
    private RanchucrutesMaps ranchucrutesMaps;
    private Location myLocation;
    private GoogleApiClient mGoogleApiClient;
    private ResultadoBuscaClinicaVo resultadoBuscaClinicaVo = null;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ranchucrutesMaps = new RanchucrutesMaps(getActivity(),this, this,this);
        this.initActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pesquisa_profissional, container, false);
    }

    @Override
    public void onDestroyView() {
        mGoogleApiClient.disconnect();
        super.onDestroyView();
        FragmentManager fm = getChildFragmentManager();
        Fragment fragment = (fm.findFragmentById(R.id.map));
        FragmentTransaction ft = fm.beginTransaction();
        ft.remove(fragment);
        ft.commitAllowingStateLoss();
    }


    private void initActivity() {
        this.initBuffers();
        this.initEvents();

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this.getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        mGoogleApiClient.connect();
    }

    private void initBuffers() {
        especialidades = RanchucrutesBuffer.getEspecialidades();
    }

    private void initEvents() {
        this.createMaps();
    }
    private void createMaps() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this.ranchucrutesMaps);
    }


    private void pesquisarProfissional(View view) {
        if (especSelecionada == null) {
            AndroidUtils.showMessageErroDlgOnUiThread(getString(R.string.msg_informeEspeciliade), getActivity());
            return;
        }

        if (myLocation == null) {
            AndroidUtils.showMessageErroDlgOnUiThread(getString(R.string.msg_informeLocalizacao), getActivity());
            return;
        }
        AndroidUtils.showWaitDlgOnUiThread(getString(R.string.msg_aguarde), getActivity());
        ProcurarProfissionalTask t = new ProcurarProfissionalTask(view);
        t.execute();
    }



    @Override
    public boolean onMyLocationButtonClick() {
        boolean isOn = AndroidUtils.gpsActive(this.getActivity());
        if (!isOn){
            AndroidUtils.alertGps(this.getActivity());
        }else{
            changeMyLocation();
            return true;
        }
        return false;
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //so chama o super caso alguma permissao foi autorizada.
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (resultadoBuscaClinicaVo != null){
            ranchucrutesMaps.setMyLocation(myLocation);
            ranchucrutesMaps.realoadMarker(resultadoBuscaClinicaVo);
        }
        boolean isOn = AndroidUtils.gpsActive(this.getActivity());
        if (!isOn){
            AndroidUtils.alertGps(this.getActivity());
        }

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Location location = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (location != null) {
            myLocation = location;
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void mapReadyFinish() {
        if (resultadoBuscaClinicaVo != null){
            ranchucrutesMaps.setMyLocation(myLocation);
            ranchucrutesMaps.realoadMarker(resultadoBuscaClinicaVo);
        }
    }


    /**
     * Task para procurar profissionais
     *
     */
    class ProcurarProfissionalTask extends RoboAsyncTask<Void>{
        ProcurarProfissionalTask(View v){
            super(v.getContext());
        }
        @Override
        public Void call() throws Exception {


            if (myLocation != null){
                try {
                    resultadoBuscaClinicaVo = profissionalService.find(especSelecionada.getId(), new LocationVo(myLocation.getLatitude(),myLocation.getLongitude()));
                } catch (ProfissionalServiceException e) {
                    AndroidUtils.closeWaitDlg();
                    AndroidUtils.showMessageErroDlgOnUiThread(e.getMessage(),getActivity());

                }
            }

            if (resultadoBuscaClinicaVo != null) {
                ranchucrutesMaps.realoadMarker(resultadoBuscaClinicaVo);
                if ( CollectionUtils.isNotEmpty(resultadoBuscaClinicaVo.getClinicas()) ){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
                            fab.setVisibility(View.VISIBLE);
                        }
                    });


                }
            }
           return null;
        }
    }


    class DialogEspecialidade implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            openDialogFindEspecialidade();
        }
    }

    public void openDialogFindEspecialidade() {
        Bundle b = new Bundle();
        ArrayList<Parcelable> parcelables = new ArrayList<>();

        if (especialidades == null){
            especialidades = RanchucrutesBuffer.getEspecialidades();
        }

        if (especialidades != null){
            for (EspecialidadeVo e : especialidades){
                parcelables.add(e);
            }
            b.putParcelableArrayList(RanchucrutesConstants.PARAM_LIST_SEARCH, parcelables);
            b.putSerializable(RanchucrutesConstants.PARAM_QUERY_TEXT, "Digite a especialidade");
            AndroidUtils.openActivityFromFragment(PesquisaProfissionalFragment.this, SearchGenericListActivity.class, b);
        }else{
            if (!AndroidUtils.internetActive(this.getContext())){
                AndroidUtils.showMessageErroDlg("Você está offline, impossível iniciar a pesquisa.",getActivity());
                return;
            }else{
                buffer.initializer();
            }
            AndroidUtils.showMessageErroDlg("Problemas na comunicação com o servidor.",getActivity());
        }
    }

    public void openDialogFindPlace() {
        AndroidUtils.openActivityFromFragment(PesquisaProfissionalFragment.this, SearchPlacesListActivity.class);
    }

    private boolean podeBuscar(){
        return myLocation != null && especSelecionada != null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null){
            SearchingListModel model = (SearchingListModel) data.getExtras().get(RanchucrutesConstants.PARAM_RESULT_SEARCH);

            if (model != null){
                if ( EspecialidadeVo.class.isInstance(model)){
                    especSelecionada = (EspecialidadeVo)model;
                    openDialogFindPlace();

                }else if ( PlacesVo.class.isInstance(model) ){
                    pesquisarProfissional((PlacesVo) model);
                }
            }
        }else if (resultCode == RanchucrutesConstants.FINISH_MY_LOCATION){
            changeMyLocation();
            if (podeBuscar()){
                pesquisarProfissional(getView());
            }
        }
    }

    private void changeMyLocation() {
        if (myLocation == null){
            myLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
        }

        if (myLocation == null){
            this.myLocation = this.ranchucrutesMaps.getmMap().getMyLocation();
        }

        if (this.myLocation == null){
                AndroidUtils.showMessageErroDlg(
                        "Não foi possível pegar sua localização. \n Verique se o GPS está ativo.", getActivity());
        }else{
            ranchucrutesMaps.setMyLocation(this.myLocation);
        }
    }

    private void pesquisarProfissional(PlacesVo model) {
        placeVo = model;
        final Geocoder geocoder = new Geocoder(getContext(), new Locale("pt","BR"));
        new Thread(){
            @Override
            public void run() {
                AndroidUtils.showWaitDlgOnUiThread("Aguarde, carregando a localização do paciente...",getActivity());

                try {
                    List<Address> addresses = geocoder.getFromLocationName(placeVo.getName(),1);
                    if (addresses != null) {
                        Address returnedAddress = addresses.get(0);
                        myLocation = new Location("geoCoder");
                        myLocation.setLatitude(returnedAddress.getLatitude()) ;
                        myLocation.setLongitude(returnedAddress.getLongitude());
                        if (podeBuscar()){
                            AndroidUtils.closeWaitDlg();
                            pesquisarProfissional(getView());
                        }

                    } else {
                        AndroidUtils.closeWaitDlg();
                        Log.w("PesquisaProfissional", "Nao achou nenhum location para o endereco:" + placeVo.getName());
                    }


                } catch (IOException e) {
                    AndroidUtils.closeWaitDlg();
                    Log.e("PesquisaProfissional","Erro ao buscar a geoLocation de um Place: ", e);
                }
            }
        }.start();

    }

}