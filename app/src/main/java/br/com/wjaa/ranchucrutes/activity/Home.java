package br.com.wjaa.ranchucrutes.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import br.com.wjaa.ranchucrutes.R;
import br.com.wjaa.ranchucrutes.service.MedicoService;
import br.com.wjaa.ranchucrutes.service.RanchucrutesService;
import br.com.wjaa.ranchucrutes.vo.EspecialidadeVo;
import br.com.wjaa.ranchucrutes.vo.MedicoBasicoVo;
import br.com.wjaa.ranchucrutes.vo.ResultadoBuscaMedicoVo;
import roboguice.RoboGuice;
import roboguice.activity.RoboFragmentActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import roboguice.util.RoboAsyncTask;

@ContentView(R.layout.activity_home)
public class Home extends RoboFragmentActivity implements
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnMarkerDragListener,
        SeekBar.OnSeekBarChangeListener,
        OnMapReadyCallback {

    private static final String TAG = Home.class.getSimpleName();
    private static final LatLng CENTER = new LatLng(-23.545616, -46.629299);


    static {
        RoboGuice.setUseAnnotationDatabases(false);
    }

    private EspecialidadeVo especSelecionada;
    private GoogleMap mMap;
    private MarkerOptions you;
    private Map<String,MedicoBasicoVo> medicos = new HashMap<String, MedicoBasicoVo>();

    /**
     * Keeps track of the last selected marker (though it may no longer be selected).  This is
     * useful for refreshing the info window.
     */
    private Marker mLastSelectedMarker;

    private final List<Marker> mMarkerRainbow = new ArrayList<Marker>();

    //@InjectView(R.id.top_text)
    //private TextView mTopText;

    @Inject
    private RanchucrutesService ranchucrutesService;

    @Inject
    private MedicoService medicoService;

    @InjectView(R.id.btnSelectEspec)
    private Button btnEspecilidade;

    @InjectView(R.id.btnProcurar)
    private Button btnProcurarMedicos;

    private EspecialidadeVo [] especialidades;

    private final Random mRandom = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.createBtnEspec();

        this.createBtnProcurar();
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    private void createBtnProcurar() {
        btnProcurarMedicos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (especSelecionada == null) {
                    Toast.makeText(Home.this, "Selecione uma especilidade", Toast.LENGTH_SHORT).show();
                    return;
                }

                ProcurarMedicosTask t = new ProcurarMedicosTask(view);
                t.execute();


            }

        });
    }

    private void createBtnEspec() {
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    especialidades = ranchucrutesService.getEspecialidades();

                    btnEspecilidade.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);

                            builder.setTitle("Selecione uma especilidade");
                            ListView modeList = new ListView(Home.this);
                            builder.setView(modeList);

                            final Dialog dialog = builder.create();
                            ArrayAdapter<EspecialidadeVo> modeAdapter = new ArrayAdapter<EspecialidadeVo>(Home.this, android.R.layout.simple_list_item_1, android.R.id.text1, especialidades){
                                @Override
                                public View getView(int position, View convertView, ViewGroup parent) {
                                    TextView t = new TextView(parent.getContext());
                                    t.setTextAppearance(parent.getContext(),R.style.listDefault);
                                    t.setOnClickListener(new EspecOnClickListener(especialidades[position],dialog));
                                    t.setText(especialidades[position].getNome());
                                    return t;
                                }
                            };
                            modeList.setAdapter(modeAdapter);


                            dialog.show();
                        }
                    });


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        // Hide the zoom controls as the button panel will cover it.
        mMap.getUiSettings().setZoomControlsEnabled(false);

        // Add lots of markers to the map.
        //addMarkersToMap();

        // Setting an info window adapter allows us to change the both the contents and look of the
        // info window.
        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());

        // Set listeners for marker events.  See the bottom of this class for their behavior.
        mMap.setOnMarkerClickListener(this);
        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnMarkerDragListener(this);

        // Override the default content description on the view, for accessibility mode.
        // Ideally this string would be localised.
        map.setContentDescription("medicos localizados");

        // Pan to see all markers in view.
        // Cannot zoom to bounds until the map has a size.
        final View mapView = getSupportFragmentManager().findFragmentById(R.id.map).getView();
        if (mapView.getViewTreeObserver().isAlive()) {
            mapView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @SuppressWarnings("deprecation") // We use the new method when supported
                @SuppressLint("NewApi") // We check which build version we are using.
                @Override
                public void onGlobalLayout() {
                    LatLngBounds bounds = new LatLngBounds.Builder()
                            .include(CENTER)
                            .build();
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                        mapView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    } else {
                        mapView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                    mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 10));
                }
            });
        }
    }

    private boolean checkReady() {
        if (mMap == null) {
            Toast.makeText(this, R.string.map_not_ready, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /** Called when the Clear button is clicked. */
    public void onClearMap(View view) {
        if (!checkReady()) {
            return;
        }
        mMap.clear();
    }

    /** Called when the Reset button is clicked. */
    public void onResetMap(View view) {
        if (!checkReady()) {
            return;
        }
        // Clear the map because we don't want duplicates of the markers.
        mMap.clear();
    }

    /** Called when the Reset button is clicked. */
   /* public void onToggleFlat(View view) {
        if (!checkReady()) {
            return;
        }
        boolean flat = mFlatBox.isChecked();
        for (Marker marker : mMarkerRainbow) {
            marker.setFlat(flat);
        }
    }*/

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (!checkReady()) {
            return;
        }
        float rotation = seekBar.getProgress();
        for (Marker marker : mMarkerRainbow) {
            marker.setRotation(rotation);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // Do nothing.
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // Do nothing.
    }

    //
    // Marker related listeners.
    //

    @Override
    public boolean onMarkerClick(final Marker marker) {
        /*if (marker.equals(mPerth)) {
            // This causes the marker at Perth to bounce into position when it is clicked.
            final Handler handler = new Handler();
            final long start = SystemClock.uptimeMillis();
            final long duration = 1500;

            final Interpolator interpolator = new BounceInterpolator();

            handler.post(new Runnable() {
                @Override
                public void run() {
                    long elapsed = SystemClock.uptimeMillis() - start;
                    float t = Math.max(
                            1 - interpolator.getInterpolation((float) elapsed / duration), 0);
                    marker.setAnchor(0.5f, 1.0f + 2 * t);

                    if (t > 0.0) {
                        // Post again 16ms later.
                        handler.postDelayed(this, 16);
                    }
                }
            });
        } else if (marker.equals(mAdelaide)) {
            // This causes the marker at Adelaide to change color and alpha.
            marker.setIcon(BitmapDescriptorFactory.defaultMarker(mRandom.nextFloat() * 360));
            marker.setAlpha(mRandom.nextFloat());
        }*/

        mLastSelectedMarker = marker;
        // We return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Toast.makeText(this, "Click Info Window", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

        //
        // mTopText.setText("onMarkerDragStart");
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

        //mTopText.setText("onMarkerDragEnd");
    }

    @Override
    public void onMarkerDrag(Marker marker) {
        //mTopText.setText("onMarkerDrag.  Current Position: " + marker.getPosition());
    }


    class EspecOnClickListener implements View.OnClickListener{

        private EspecialidadeVo especialidadeVo;
        private Dialog dialog;
        EspecOnClickListener(EspecialidadeVo especialidadeVo, Dialog dialog){
            this.especialidadeVo = especialidadeVo;
            this.dialog = dialog;
        }
        @Override
        public void onClick(View view) {
            especSelecionada = this.especialidadeVo;
            btnEspecilidade.setText(especSelecionada.getNome());
            dialog.dismiss();
        }
    }


    /** Demonstrates customizing the info window and/or its contents. */
    class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
        // These a both viewgroups containing an ImageView with id "badge" and two TextViews with id
        // "title" and "snippet".
        private final View mWindow;
        private final View mContents;

        CustomInfoWindowAdapter() {
            mWindow = getLayoutInflater().inflate(R.layout.custom_info_window, null);
            mContents = getLayoutInflater().inflate(R.layout.custom_info_contents, null);
        }

        @Override
        public View getInfoWindow(Marker marker) {
            render(marker, mWindow);
            return mWindow;
        }

        @Override
        public View getInfoContents(Marker marker) {
            //if (mOptions.getCheckedRadioButtonId() != R.id.custom_info_contents) {
            // This means that the default info contents will be used.
            return null;
            // }
            //render(marker, mContents);
            // return mContents;
        }

        private void render(Marker marker, View view) {
            int badge = 0;
            // Use the equals() method on a Marker to check for equals.  Do not use ==.
           /* if (marker.equals(mBrisbane)) {
                badge = R.drawable.badge_qld;
            } else if (marker.equals(mAdelaide)) {
                badge = R.drawable.badge_sa;
            } else if (marker.equals(mSydney)) {
                badge = R.drawable.badge_nsw;
            } else if (marker.equals(mMelbourne)) {
                badge = R.drawable.badge_victoria;
            } else if (marker.equals(mPerth)) {
                badge = R.drawable.badge_wa;
            } else {
                // Passing 0 to setImageResource will clear the image view.
                badge = 0;
            }*/
            ((ImageView) view.findViewById(R.id.badge)).setImageResource(badge);
            MedicoBasicoVo medico = medicos.get(marker.getId());
            TextView nomeMedico = ((TextView) view.findViewById(R.id.nome));
            SpannableString medicoText = new SpannableString(medico.getNome());
            medicoText.setSpan(new ForegroundColorSpan(Color.BLUE), 0, medicoText.length(), 0);
            nomeMedico.setText(medicoText);

            TextView crmMedico = ((TextView) view.findViewById(R.id.crm));
            String crm = medico.getCrm() != null ? medico.getCrm().toString() : "";
            SpannableString crmText = new SpannableString(crm);
            crmText.setSpan(new ForegroundColorSpan(Color.BLUE), 0, crm.length(), 0);
            crmMedico.setText(crmText);

            TextView especMedico = ((TextView) view.findViewById(R.id.espec));
            especMedico.setText(medico.getEspec());

            TextView endMedico = ((TextView) view.findViewById(R.id.endereco));
            endMedico.setText(medico.getEndereco());

            TextView telMedico = ((TextView) view.findViewById(R.id.telefone));
            telMedico.setText(medico.getTelefone());

        }
    }


    class ProcurarMedicosTask extends RoboAsyncTask<Void>{

        private View view;
        ProcurarMedicosTask(View v){
            super(v.getContext());
            this.view = v;
        }

        @Override
        public Void call() throws Exception {

            ResultadoBuscaMedicoVo resultado = medicoService.find(especSelecionada.getId(), "07020280");


            if (resultado != null) {
                ReloadMarkerThread r = new ReloadMarkerThread(resultado);
                ((Activity)view.getContext()).runOnUiThread(r);

            }

           return null;
        }
    }


    class ReloadMarkerThread extends Thread{

        private ResultadoBuscaMedicoVo resultado;
        ReloadMarkerThread(ResultadoBuscaMedicoVo resultado){
            this.resultado = resultado;
        }

        @Override
        public void run() {

            try {
                mMap.clear();
                medicos.clear();
                Marker you = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(resultado.getLatitude(), resultado.getLongitude()))
                        .title("Você está aqui")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

                for (MedicoBasicoVo medico : resultado.getMedicos()) {
                    Marker m = mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(medico.getLatitude(), medico.getLongitude()))
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                    medicos.put(m.getId(),medico);

                }

                LatLngBounds bounds = new LatLngBounds.Builder()
                        .include(you.getPosition())
                        .build();

                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 10));
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }
    }

}