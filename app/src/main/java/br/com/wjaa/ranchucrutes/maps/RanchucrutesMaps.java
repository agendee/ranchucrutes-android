package br.com.wjaa.ranchucrutes.maps;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import br.com.wjaa.ranchucrutes.R;
import br.com.wjaa.ranchucrutes.activity.AgendamentoActivity;
import br.com.wjaa.ranchucrutes.activity.ProfissionalListActivity;
import br.com.wjaa.ranchucrutes.buffer.RanchucrutesSession;
import br.com.wjaa.ranchucrutes.listener.OnMapReadyFinish;
import br.com.wjaa.ranchucrutes.service.RanchucrutesConstants;
import br.com.wjaa.ranchucrutes.utils.AndroidUtils;
import br.com.wjaa.ranchucrutes.utils.CollectionUtils;
import br.com.wjaa.ranchucrutes.utils.ImageUtils;
import br.com.wjaa.ranchucrutes.utils.StringUtils;
import br.com.wjaa.ranchucrutes.vo.ClinicaVo;
import br.com.wjaa.ranchucrutes.vo.MapTipoLocalidade;
import br.com.wjaa.ranchucrutes.vo.ProfissionalBasicoVo;
import br.com.wjaa.ranchucrutes.vo.ResultadoBuscaClinicaVo;

/**
 * Created by wagner on 31/07/15.
 */
public class RanchucrutesMaps implements GoogleMap.OnMarkerClickListener,
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnMarkerDragListener,
        OnMapReadyCallback {

    private static final String TAG = RanchucrutesMaps.class.getSimpleName();
    private static final LatLng CENTER = new LatLng(-23.545616, -46.629299);


    private final static Map<Integer, Double> scaleZoom = new HashMap<>(20);

    private FragmentActivity context;
    private GoogleMap mMap;
    private Boolean reloadImage = true;
    private Marker mLastSelectedMarker;
    private Map<String, ClinicaVo> profissionais = new HashMap<String, ClinicaVo>();
    private GoogleMap.OnMyLocationButtonClickListener onMyLocationButtonClickListener;
    private Fragment fragment;
    private View mapView;
    private Marker you;
    private OnMapReadyFinish onMapReadyFinish;

    public RanchucrutesMaps(Context context, Fragment fragment,
                            GoogleMap.OnMyLocationButtonClickListener onMyLocationButtonClickListener,
                            OnMapReadyFinish onMapReadyFinish) {
        this.context = (FragmentActivity) context;
        this.fragment = fragment;
        this.onMyLocationButtonClickListener = onMyLocationButtonClickListener;
        this.onMapReadyFinish = onMapReadyFinish;
        mapView = fragment.getChildFragmentManager().findFragmentById(R.id.map).getView();
        View zoomControl = mapView.findViewById(0x1);
        View myLocationControl = mapView.findViewById(0x2);

        this.alignView(zoomControl, true);
        this.alignView(myLocationControl, false);

    }

    //
    // Marker related listeners.
    //

    @Override
    public boolean onMarkerClick(final Marker marker) {
        reloadImage = true;
        mLastSelectedMarker = marker;
        return false;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {

        try {
            ClinicaVo c = profissionais.get(marker.getId());
            if (MapTipoLocalidade.PARTICULAR.equals(c.getMapTipoLocalidade())) {
                ProfissionalBasicoVo p = c.getProfissionais().get(0);
                if (StringUtils.isNotBlank(c.getTelefone())) {
                    p.setTelefone(c.getTelefone());
                }
                Bundle b = new Bundle();
                b.putSerializable(RanchucrutesConstants.PARAM_PROFISSIONAL, p);
                AndroidUtils.openActivity(context, AgendamentoActivity.class, b, RanchucrutesConstants.FINISH_CONFIRME_AGENDAMENTO_OPEN_LIST);
            } else {
                Bundle b = new Bundle();
                b.putSerializable(RanchucrutesConstants.PARAM_CLINICA, c);
                AndroidUtils.openActivity(context, ProfissionalListActivity.class, b);
            }
        } catch (ActivityNotFoundException act) {
            Log.e("Exemplo de chamada", "falha", act);
        }

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


    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        // Hide the zoom controls as the button panel will cover it.
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) {
            fragment.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationButtonClickListener(this.onMyLocationButtonClickListener);
        // Setting an info window adapter allows us to change the both the contents and look of the
        // info window.
        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());

        // Set listeners for marker events.  See the bottom of this class for their behavior.
        mMap.setOnMarkerClickListener(this);
        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnMarkerDragListener(this);

        // Override the default content description on the view, for accessibility mode.
        // Ideally this string would be localised.
        map.setContentDescription("Profissionais localizados");

        // Pan to see all markers in view.
        // Cannot zoom to bounds until the map has a size.
        View mapView = null;

        try{
            mapView = fragment.getChildFragmentManager().findFragmentById(R.id.map).getView();

            final View mapViewf = mapView;
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
                            mapViewf.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        } else {
                            mapViewf.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(CENTER, 11));
                    }
                });
            }
        }catch(Exception ex){
            Log.e("RanchucrutesMaps","Erro no onmapsready",ex);
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M ) {
            onMapReadyFinish.mapReadyFinish();
        }

    }



    private void alignView(View view, boolean left) {
        if (view != null && view.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
            // ZoomControl is inside of RelativeLayout
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view.getLayoutParams();
            // Align it to - parent TOP|LEFT
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,0);
            final int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10,
                    context.getResources().getDisplayMetrics());


            if (!left){
                params.addRule(RelativeLayout.ALIGN_PARENT_END, 0);

                // Update margins, set to 10dp
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    params.setMarginStart(margin);
                }

            }else{
                params.setMargins(margin, margin, margin, margin);
            }
            view.setLayoutParams(params);
        }
    }

    public void realoadMarker(ResultadoBuscaClinicaVo resultado) {
        ReloadMarkerThread r = new ReloadMarkerThread(resultado);
        context.runOnUiThread(r);
    }

    public void setMyLocation(Location myLocation) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(myLocation.getLatitude(),myLocation.getLongitude()), 11));
    }


    /** Demonstrates customizing the info window and/or its contents. */
    class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
        // These a both viewgroups containing an ImageView with id "badge" and two TextViews with id
        // "title" and "snippet".
        private final View mWindow;
        private final View pacienteWindow;

        CustomInfoWindowAdapter() {
            mWindow = context.getLayoutInflater().inflate(R.layout.custom_info_window, null);
            pacienteWindow = context.getLayoutInflater().inflate(R.layout.paciente_info_window, null);
        }

        @Override
        public View getInfoWindow(Marker marker) {
            if (you!= null && !marker.getId().equals(you.getId())){
                render(marker, mWindow);
            }else{
                renderYou(pacienteWindow);
                return pacienteWindow;
            }
            return mWindow;
        }

        private void renderYou(View pacienteWindow) {
            ImageView fotoUser = (ImageView) pacienteWindow.findViewById(R.id.imgPaciente);
            if (RanchucrutesSession.isUsuarioLogado() && StringUtils.isNotBlank(RanchucrutesSession.getUsuario().getUrlFoto())){
                ImageUtils.loadImage(context,fotoUser,RanchucrutesSession.getUsuario().getUrlFoto());
            }else{
                fotoUser.setImageDrawable(context.getResources().getDrawable(R.drawable.unknow));
            }

        }

        @Override
        public View getInfoContents(Marker marker) {
            if (marker != null && marker.isInfoWindowShown()) {
                marker.hideInfoWindow();
                marker.showInfoWindow();
            }
            return null;
        }

        private void render(Marker marker, View view) {
            //
            ClinicaVo clinicaVo = profissionais.get(marker.getId());

            if (clinicaVo != null){

                if (CollectionUtils.isNotEmpty(clinicaVo.getProfissionais())){
                    ProfissionalBasicoVo profissional = clinicaVo.getProfissionais().get(0);
                    if (MapTipoLocalidade.PARTICULAR.equals(clinicaVo.getMapTipoLocalidade())){
                        LoadImage li = new LoadImage(view, profissional, marker, this);
                        li.start();
                        TextView nomeProfissional = ((TextView) view.findViewById(R.id.nome));
                        nomeProfissional.setText(profissional.getNome());
                        TextView crmProfissional = ((TextView) view.findViewById(R.id.crm));
                        String crm = profissional.getNumeroRegistro() != null ? profissional.getNumeroRegistro().toString() : "";
                        crmProfissional.setVisibility(View.VISIBLE);
                        crmProfissional.setText("CRM: " + crm);

                    }else {
                        ImageView i = (ImageView) view.findViewById(R.id.badge);
                        TextView nome = ((TextView) view.findViewById(R.id.nome));
                        if ( MapTipoLocalidade.CLINICA.equals(clinicaVo.getMapTipoLocalidade())){
                            i.setImageDrawable(view.getResources().getDrawable(R.drawable.ic_clinica));
                            i.invalidate();
                            if (StringUtils.isBlank(clinicaVo.getNome())){
                                nome.setText("Clinica sem nome");
                            }else{
                                nome.setText(clinicaVo.getNome());
                            }
                        }else{
                            nome.setText("Clínica(s)");
                            i.setImageDrawable(view.getResources().getDrawable(R.drawable.ic_edificio));
                            i.invalidate();
                        }
                        TextView crmProfissional = ((TextView) view.findViewById(R.id.crm));
                        crmProfissional.setVisibility(View.GONE);

                    }
                    TextView especProfissional = ((TextView) view.findViewById(R.id.espec));
                    especProfissional.setText(profissional.getEspec());
                    TextView endProfissional = ((TextView) view.findViewById(R.id.endereco));
                    endProfissional.setText(profissional.getEndereco());
                    TextView telProfissional = ((TextView) view.findViewById(R.id.telefone));
                    if (StringUtils.isNotBlank(clinicaVo.getTelefone())) {
                        telProfissional.setText("Telefone: " + clinicaVo.getTelefone());
                    } else if (StringUtils.isNotBlank(profissional.getTelefone())){
                        telProfissional.setText("Telefone: " + profissional.getTelefone());
                    }else{
                        telProfissional.setText("Telefone: --");
                    }


                }
            }else{

            }


        }
    }

    class LoadImage extends Thread{
        private View view;
        private ProfissionalBasicoVo profissional;
        private Bitmap bitmap = null;
        private Marker marker;
        private CustomInfoWindowAdapter customInfoWindowAdapter;

        LoadImage(View view, ProfissionalBasicoVo profissional, Marker marker, CustomInfoWindowAdapter customInfoWindowAdapter){
            this.view = view;
            this.profissional = profissional;
            this.marker = marker;
            this.customInfoWindowAdapter = customInfoWindowAdapter;

        }

        @Override
        public void run() {
            if( reloadImage ) {

                if (StringUtils.isNotBlank(profissional.getFoto())){
                    String imageUrl = "http://agendee.com.br/f/" + profissional.getFoto();
                    try {
                        bitmap = BitmapFactory.decodeStream((InputStream) new URL(imageUrl).getContent());
                        context.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ImageView i = (ImageView) view.findViewById(R.id.badge);
                                i.setImageBitmap(bitmap);
                                i.invalidate();
                                customInfoWindowAdapter.getInfoContents(marker);
                                reloadImage = false;
                            }
                        });
                    } catch (Exception e) {
                        Log.e(TAG, "Erro ao buscar a imagem", e);
                        loadDefaultImage();

                    }
                }else{
                    loadDefaultImage();
                }


            }
        }

        private void loadDefaultImage() {
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ImageView i = (ImageView) view.findViewById(R.id.badge);
                    i.setImageResource(R.drawable.unknow);
                    i.invalidate();
                    customInfoWindowAdapter.getInfoContents(marker);
                    reloadImage = false;
                }
            });
        }
    }

    class ReloadMarkerThread extends Thread{

        private ResultadoBuscaClinicaVo resultado;
        ReloadMarkerThread(ResultadoBuscaClinicaVo resultado){
            this.resultado = resultado;
        }

        @Override
        public void run() {

            try {
                int countProfissionais = 0;
                mMap.clear();
                profissionais.clear();

                you = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(resultado.getLatitude(), resultado.getLongitude()))
                        .title(context.getString(R.string.msg_voceAqui))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_person_black_36dp)));//defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

                try{

                    for (ClinicaVo clinicaVo : resultado.getClinicas()) {

                        //verificando se
                        if (MapTipoLocalidade.PARTICULAR.equals(clinicaVo.getMapTipoLocalidade())){
                            ProfissionalBasicoVo p = clinicaVo.getProfissionais().get(0);
                            if (p != null){
                                Marker m = mMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(p.getLatitude(), p.getLongitude()))
                                        .icon(BitmapDescriptorFactory.defaultMarker(
                                                clinicaVo.getTemAgenda() ?
                                                        BitmapDescriptorFactory.HUE_BLUE
                                                        : BitmapDescriptorFactory.HUE_RED)));

                                countProfissionais ++;
                                profissionais.put(m.getId(), clinicaVo);
                            }
                        }else{
                            Marker m = mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(clinicaVo.getLatitude(), clinicaVo.getLongitude()))
                                    .icon(BitmapDescriptorFactory.defaultMarker(
                                            MapTipoLocalidade.CLINICA.equals(clinicaVo.getMapTipoLocalidade()) ?
                                                    BitmapDescriptorFactory.HUE_GREEN : BitmapDescriptorFactory.HUE_YELLOW)));
                            countProfissionais += clinicaVo.getProfissionais().size();
                            profissionais.put(m.getId(), clinicaVo);
                        }


                    }
                }catch(Exception ex){
                    ex.printStackTrace();
                }

                if (CollectionUtils.isEmpty(resultado.getClinicas() )){
                    AndroidUtils.showMessageErroDlg(context.getString(R.string.msg_nenhumProfissionalEncontrado), context);
                    AndroidUtils.closeWaitDlg();
                    return;
                }



                final LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(you.getPosition());
                builder.include(new LatLng(resultado.getClinicaMaisProxima().getLatitude(),resultado.getClinicaMaisProxima().getLongitude()));
                LatLngBounds bounds = builder.build();
                LatLng ne = bounds.northeast;
                LatLng sw = bounds.southwest;

                int width = mapView.getWidth();
                int height = mapView.getHeight();

                int zoom = getBoundsZoomLevel(ne,sw,width,height);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(you.getPosition(), zoom -2));
                //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(resultado.getClinicaMaisProxima().getLatitude(),resultado.getClinicaMaisProxima().getLongitude()), getZoom(resultado.getDistanceInKm())));
                AndroidUtils.closeWaitDlg();
                Toast.makeText(context, "Encontramos " + countProfissionais + " profissionais!\nPara visualizar mais profissionais, arraste o mapa ou aumente o zoom.", Toast.LENGTH_LONG).show();

            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }
    }

    public GoogleMap getmMap() {
        return mMap;
    }



    final static int GLOBE_WIDTH = 256; // a constant in Google's map projection
    final static int ZOOM_MAX = 21;

    private int getBoundsZoomLevel(LatLng northeast,LatLng southwest,
                                         int width, int height) {

        double latFraction = (latRad(northeast.latitude) - latRad(southwest.latitude)) / Math.PI;
        double lngDiff = northeast.longitude - southwest.longitude;
        double lngFraction = ((lngDiff < 0) ? (lngDiff + 360) : lngDiff) / 360;
        double latZoom = zoom(height, GLOBE_WIDTH, latFraction);
        double lngZoom = zoom(width, GLOBE_WIDTH, lngFraction);
        double zoom = Math.min(Math.min(latZoom, lngZoom),ZOOM_MAX);
        return (int)(zoom-1);
    }
    private static double latRad(double lat) {
        double sin = Math.sin(lat * Math.PI / 180);
        double radX2 = Math.log((1 + sin) / (1 - sin)) / 2;
        return Math.max(Math.min(radX2, Math.PI), -Math.PI) / 2;
    }
    private static double zoom(double mapPx, double worldPx, double fraction) {
        final double LN2 = .693147180559945309417;
        return (Math.log(mapPx / worldPx / fraction) / LN2);
    }

}
