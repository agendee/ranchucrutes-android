package br.com.wjaa.ranchucrutes.fragment;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ToggleButton;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.inject.Inject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import br.com.wjaa.ranchucrutes.R;
import br.com.wjaa.ranchucrutes.activity.SearchGenericListActivity;
import br.com.wjaa.ranchucrutes.activity.SearchPlacesListActivity;
import br.com.wjaa.ranchucrutes.buffer.RanchucrutesBuffer;
import br.com.wjaa.ranchucrutes.maps.RanchucrutesMaps;
import br.com.wjaa.ranchucrutes.service.ProfissionalService;
import br.com.wjaa.ranchucrutes.service.RanchucrutesConstants;
import br.com.wjaa.ranchucrutes.utils.AndroidUtils;
import br.com.wjaa.ranchucrutes.utils.CollectionUtils;
import br.com.wjaa.ranchucrutes.view.SearchingListModel;
import br.com.wjaa.ranchucrutes.vo.EspecialidadeVo;
import br.com.wjaa.ranchucrutes.vo.LocationVo;
import br.com.wjaa.ranchucrutes.vo.PlacesVo;
import br.com.wjaa.ranchucrutes.vo.ResultadoBuscaProfissionalVo;
import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;
import roboguice.util.RoboAsyncTask;

/**
 * @author Wagner Jeronimo
 */
public class PesquisaProfissionalFragment extends RoboFragment implements GoogleMap.OnMyLocationButtonClickListener {

    private static final String TAG = PesquisaProfissionalFragment.class.getSimpleName();
    private EspecialidadeVo especSelecionada;
    private PlacesVo placeVo;
    @Inject
    private ProfissionalService profissionalService;
    @InjectView(R.id.btnSelectEspec)
    private Button btnEspecilidade;

    @InjectView(R.id.btnSelectPlace)
    private Button btnSelectPlace;

    @InjectView(R.id.btnMyLocation)
    private ToggleButton btnMyLocation;


    //@InjectView(R.id.edtCep)
    //private EditText edtCep;
    @InjectView(R.id.btnProcurar)
    private Button btnProcurarProfissional;
    @InjectView(R.id.btnFechar)
    private Button btnFechar;
    @InjectView(R.id.frameBusca)
    private View frameBusca;
    private EspecialidadeVo[] especialidades;
    private RanchucrutesMaps ranchucrutesMaps;
    private Location myLocation;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ranchucrutesMaps = new RanchucrutesMaps(getActivity(),this, this);
        this.initActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pesquisa_profissional, container, false);

    }

    @Override
    public void onDestroyView() {
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
    }

    private void initBuffers() {
        especialidades = RanchucrutesBuffer.getEspecialidades();
    }

    private void initEvents() {
        this.createBtnEspec();
        this.createBtnPlace();
        this.createBtnMyLocation();
        this.createBtnEvents();
        this.createEdtCep();
        this.createMaps();

    }

    private void createBtnMyLocation() {
        btnMyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                myLocation = ranchucrutesMaps.getmMap().getMyLocation();
                if (myLocation != null && btnMyLocation.isChecked()){
                    ranchucrutesMaps.setMyLocation(myLocation);
                    btnSelectPlace.setText("");
                }else{
                    myLocation = null;
                }
            }
        });
    }

    private void createBtnPlace() {

        btnSelectPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AndroidUtils.openActivityFromFragment(PesquisaProfissionalFragment.this, SearchPlacesListActivity.class);
            }
        });

    }

    private void createEdtCep() {
       /* edtCep.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                myLocation = null;
                return false;
            }
        });*/
    }

    private void createMaps() {
        SupportMapFragment mapFragment;
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this.ranchucrutesMaps);
    }

    private void createBtnEvents() {
        btnProcurarProfissional.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (especSelecionada == null) {
                    AndroidUtils.showMessageErroDlg(getString(R.string.msg_informeEspeciliade), getActivity());
                    return;
                }

                if (myLocation == null) {
                    AndroidUtils.showMessageErroDlg(getString(R.string.msg_informeLocalizacao), getActivity());
                    return;
                }
                AndroidUtils.showWaitDlg(getString(R.string.msg_aguarde), getActivity());
                ProcurarProfissionalTask t = new ProcurarProfissionalTask(view);
                t.execute();
            }

        });

        btnFechar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                frameBusca.setVisibility(View.GONE);
                FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
                fab.setVisibility(View.VISIBLE);
            }
        });
    }

    private void createBtnEspec() {
        btnEspecilidade.setOnClickListener(new DialogEspecialidade());
    }

    @Override
    public boolean onMyLocationButtonClick() {
        this.myLocation = this.ranchucrutesMaps.getmMap().getMyLocation();

        if (this.myLocation == null){
            btnMyLocation.setChecked(false);
            AndroidUtils.showMessageErroDlg(
                    "Não foi possível pegar sua localização. \n Verique se o GPS está ativo.", getActivity());
        }else{
            ranchucrutesMaps.setMyLocation(this.myLocation);
            btnMyLocation.setChecked(true);
        }
        return true;

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

            ResultadoBuscaProfissionalVo resultado = null;
            if (myLocation != null){
                resultado = profissionalService.find(especSelecionada.getId(), new LocationVo(myLocation.getLatitude(),myLocation.getLongitude()));
            }

            if (resultado != null) {
                ranchucrutesMaps.realoadMarker(resultado);
                if ( CollectionUtils.isNotEmpty(resultado.getProfissionais()) ){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            frameBusca.setVisibility(View.GONE);
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
                AndroidUtils.openActivityFromFragment(PesquisaProfissionalFragment.this, SearchGenericListActivity.class, b);
            }else{
                AndroidUtils.showMessageErroDlg("Problemas na comunicação com o servidor.",getActivity());
            }


        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null){
            SearchingListModel model = (SearchingListModel) data.getExtras().get(RanchucrutesConstants.PARAM_RESULT_SEARCH);

            if (model != null){
                if ( EspecialidadeVo.class.isInstance(model)){
                    especSelecionada = (EspecialidadeVo)model;
                    btnEspecilidade.setText(especSelecionada.getNome());
                }else if ( PlacesVo.class.isInstance(model) ){
                    placeVo = (PlacesVo)model;
                    btnSelectPlace.setText(placeVo.getName());
                    Geocoder geocoder = new Geocoder(getContext(), new Locale("pt","BR"));
                    try {
                        List<Address>  addresses = geocoder.getFromLocationName(placeVo.getName(),1);
                        if (addresses != null) {
                            Address returnedAddress = addresses.get(0);
                            myLocation = new Location("geoCoder");
                            myLocation.setLatitude(returnedAddress.getLatitude()) ;
                            myLocation.setLongitude(returnedAddress.getLongitude());
                            btnMyLocation.setChecked(false);

                        } else {
                            Log.w("PesquisaProfissional", "Nao achou nenhum location para o endereco:" + placeVo.getName());
                        }


                    } catch (IOException e) {
                        Log.e("PesquisaProfissional","Erro ao buscar a geoLocation de um Place: ", e);
                    }
                }
            }
        }
    }

    public View getFrameBusca() {
        return frameBusca;
    }


    public boolean isFrameBuscaVisible(){
        return frameBusca != null ? frameBusca.getVisibility() == View.VISIBLE : false;
    }

}