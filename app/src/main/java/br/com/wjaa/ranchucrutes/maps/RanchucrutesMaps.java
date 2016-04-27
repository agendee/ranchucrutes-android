package br.com.wjaa.ranchucrutes.maps;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
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
import br.com.wjaa.ranchucrutes.activity.callback.DialogCallback;
import br.com.wjaa.ranchucrutes.service.RanchucrutesConstants;
import br.com.wjaa.ranchucrutes.utils.AndroidUtils;
import br.com.wjaa.ranchucrutes.utils.CollectionUtils;
import br.com.wjaa.ranchucrutes.utils.StringUtils;
import br.com.wjaa.ranchucrutes.vo.ClinicaVo;
import br.com.wjaa.ranchucrutes.vo.ProfissionalBasicoVo;
import br.com.wjaa.ranchucrutes.vo.ResultadoBuscaClinicaVo;
import br.com.wjaa.ranchucrutes.vo.ResultadoBuscaProfissionalVo;

/**
 * Created by wagner on 31/07/15.
 */
public class RanchucrutesMaps implements GoogleMap.OnMarkerClickListener,
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnMarkerDragListener,
        OnMapReadyCallback{

    private static final String TAG = RanchucrutesMaps.class.getSimpleName();
    private static final LatLng CENTER = new LatLng(-23.545616, -46.629299);
    private FragmentActivity context;
    private GoogleMap mMap;
    private Boolean reloadImage = true;
    private Marker mLastSelectedMarker;
    private Map<String,ClinicaVo> profissionais = new HashMap<String, ClinicaVo>();
    private GoogleMap.OnMyLocationButtonClickListener onMyLocationButtonClickListener;
    private Fragment fragment;

    public RanchucrutesMaps(Context context, Fragment fragment, GoogleMap.OnMyLocationButtonClickListener onMyLocationButtonClickListener){
        this.context = (FragmentActivity)context;
        this.fragment = fragment;
        this.onMyLocationButtonClickListener = onMyLocationButtonClickListener;
        View mapView = fragment.getChildFragmentManager().findFragmentById(R.id.map).getView();
        View zoomControl = mapView.findViewById(0x1);
        View myLocationControl = mapView.findViewById(0x2);

        this.alignView(zoomControl,true);
        this.alignView(myLocationControl,false);

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

        try{
            //TODO AQUI PRECISA SER DIFERENTE QUANDO TIVER MAIS DE UM PROFISSIONAL NO MESMO LOCAL.
            ClinicaVo c = profissionais.get(marker.getId());
            ProfissionalBasicoVo m = c.getProfissionais().get(0);
            if (StringUtils.isNotBlank(c.getTelefone())){
                m.setTelefone(c.getTelefone());
            }
            Bundle b = new Bundle();
            b.putSerializable(RanchucrutesConstants.PARAM_PROFISSIONAL,m);
            AndroidUtils.openActivity(context,AgendamentoActivity.class, b);
        }
        catch(ActivityNotFoundException act){
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
        //private final View mContents;

        CustomInfoWindowAdapter() {
            mWindow = context.getLayoutInflater().inflate(R.layout.custom_info_window, null);
           // mContents = context.getLayoutInflater().inflate(R.layout.custom_info_contents, null);
        }

        @Override
        public View getInfoWindow(Marker marker) {
            render(marker, mWindow);
            return mWindow;
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

                    for (ProfissionalBasicoVo profissional : clinicaVo.getProfissionais()){
                        LoadImage li = new LoadImage(view, profissional, marker, this);
                        li.start();

                        TextView nomeProfissional = ((TextView) view.findViewById(R.id.nome));
                        nomeProfissional.setText(profissional.getNome());

                        TextView crmProfissional = ((TextView) view.findViewById(R.id.crm));
                        String crm = profissional.getNumeroRegistro() != null ? profissional.getNumeroRegistro().toString() : "";
                        crmProfissional.setText("CRM: " + crm);

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
                }


            }else{

                ImageView i = (ImageView) view.findViewById(R.id.badge);
                i.setImageBitmap(null);
                TextView nomeProfissional = ((TextView) view.findViewById(R.id.nome));
                nomeProfissional.setText(R.string.msg_voceAqui);

                TextView crmProfissional = ((TextView) view.findViewById(R.id.crm));
                crmProfissional.setText("");

                TextView especProfissional = ((TextView) view.findViewById(R.id.espec));
                especProfissional.setText("");

                TextView endProfissional = ((TextView) view.findViewById(R.id.endereco));
                endProfissional.setText("");

                TextView telProfissional = ((TextView) view.findViewById(R.id.telefone));
                telProfissional.setText("");
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
                String imageUrl = "http://agendee.com.br/f/" + profissional.getNumeroRegistro() + ".jpg";
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
                mMap.clear();
                profissionais.clear();

                Marker you = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(resultado.getLatitude(), resultado.getLongitude()))
                        .title(context.getString(R.string.msg_voceAqui))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_person_black_36dp)));//defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

                try{

                    for (ClinicaVo clinicaVo : resultado.getClinicas()) {
                        ProfissionalBasicoVo p = clinicaVo.getProfissionais().get(0);

                        //TODO localidade tem que ser da clinica.
                        if (p != null){
                            Marker m = mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(p.getLatitude(), p.getLongitude()))
                                    .icon(BitmapDescriptorFactory.defaultMarker(
                                            clinicaVo.getTemAgenda() ?
                                                    BitmapDescriptorFactory.HUE_BLUE
                                                    : BitmapDescriptorFactory.HUE_RED)));

                            profissionais.put(m.getId(), clinicaVo);

                        }


                    }
                }catch(Exception ex){

                }
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(you.getPosition(), 11));
                AndroidUtils.closeWaitDlg();
                
                if (CollectionUtils.isEmpty(resultado.getClinicas() )){
                    AndroidUtils.showMessageErroDlg(context.getString(R.string.msg_nenhumProfissionalEncontrado), context);
                }else{
                    Toast.makeText(context,"Profissionals foram encontrados!", Toast.LENGTH_SHORT);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }
    }


    class DialogInfoWindowCallback implements DialogCallback{

        private ProfissionalBasicoVo profissional;
        DialogInfoWindowCallback(ProfissionalBasicoVo profissional){
            this.profissional = profissional;
        }

        @Override
        public void confirm() {
            Intent chamada = new Intent(Intent.ACTION_DIAL);
            //pega a posição da pessoa
            chamada.setData(Uri.parse("tel:" + profissional.getTelefone().trim()));
            context.startActivity(chamada);
        }

        @Override
        public void cancel() {

        }
    }

    public GoogleMap getmMap() {
        return mMap;
    }
}
