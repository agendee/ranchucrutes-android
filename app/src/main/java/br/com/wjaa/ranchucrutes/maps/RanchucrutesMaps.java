package br.com.wjaa.ranchucrutes.maps;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;
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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import br.com.wjaa.ranchucrutes.R;
import br.com.wjaa.ranchucrutes.activity.callback.DialogCallback;
import br.com.wjaa.ranchucrutes.utils.AndroidUtils;
import br.com.wjaa.ranchucrutes.vo.MedicoBasicoVo;
import br.com.wjaa.ranchucrutes.vo.ResultadoBuscaMedicoVo;

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
    private Map<String,MedicoBasicoVo> medicos = new HashMap<String, MedicoBasicoVo>();


    public RanchucrutesMaps(Context context){
        this.context = (FragmentActivity)context;
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
        reloadImage = true;
        mLastSelectedMarker = marker;




        // We return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {

        try{

            MedicoBasicoVo m = medicos.get(marker.getId());

            if (m != null && m.getTelefone() != null && !"".equals(m.getTelefone())){

                AndroidUtils.showConfirmDlg("Ligar", "Esse médico não possui agenda online. \n Deseja ligar?",
                        context, new DialogInfoWindowCallback(m));

            }else{
                AndroidUtils.showMessageDlg("Ops!", "Esse médico não possui agenda e telefone.", context);
            }

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
        mMap.setOnMyLocationButtonClickListener((GoogleMap.OnMyLocationButtonClickListener)context);
        mMap.setPadding(0,0,0,220);
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
        final View mapView = context.getSupportFragmentManager().findFragmentById(R.id.map).getView();
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
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(CENTER, 13));
                }
            });
        }

    }

    public void realoadMarker(ResultadoBuscaMedicoVo resultado) {
        ReloadMarkerThread r = new ReloadMarkerThread(resultado);
        context.runOnUiThread(r);
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
            //
            MedicoBasicoVo medico = medicos.get(marker.getId());

            if (medico != null){


                LoadImage li = new LoadImage(view, medico, marker, this);
                li.start();

                TextView nomeMedico = ((TextView) view.findViewById(R.id.nome));
                SpannableString medicoText = new SpannableString(medico.getNome());
                medicoText.setSpan(new ForegroundColorSpan(Color.BLUE), 0, medicoText.length(), 0);
                nomeMedico.setText(medicoText);

                TextView crmMedico = ((TextView) view.findViewById(R.id.crm));
                String crm = medico.getCrm() != null ? medico.getCrm().toString() : "";
                SpannableString crmText = new SpannableString(crm);
                crmText.setSpan(new ForegroundColorSpan(Color.BLUE), 0, crm.length(), 0);
                crmMedico.setText("CRM: " + crmText);

                TextView especMedico = ((TextView) view.findViewById(R.id.espec));
                especMedico.setText(medico.getEspec());

                TextView endMedico = ((TextView) view.findViewById(R.id.endereco));
                endMedico.setText(medico.getEndereco());

                TextView telMedico = ((TextView) view.findViewById(R.id.telefone));
                if (medico.getTelefone() != null && !"".equals(medico.getTelefone())){

                    telMedico.setText("Telefone: " + medico.getTelefone());
                }else{
                    telMedico.setText("Telefone: --");
                }


            }else{

                ImageView i = (ImageView) view.findViewById(R.id.badge);
                i.setImageBitmap(null);
                TextView nomeMedico = ((TextView) view.findViewById(R.id.nome));
                nomeMedico.setText(R.string.msg_voceAqui);

                TextView crmMedico = ((TextView) view.findViewById(R.id.crm));
                crmMedico.setText("");

                TextView especMedico = ((TextView) view.findViewById(R.id.espec));
                especMedico.setText("");

                TextView endMedico = ((TextView) view.findViewById(R.id.endereco));
                endMedico.setText("");

                TextView telMedico = ((TextView) view.findViewById(R.id.telefone));
                telMedico.setText("");
            }


        }
    }

    class LoadImage extends Thread{
        private View view;
        private MedicoBasicoVo medico;
        private Bitmap bitmap = null;
        private Marker marker;
        private CustomInfoWindowAdapter customInfoWindowAdapter;

        LoadImage(View view, MedicoBasicoVo medico, Marker marker, CustomInfoWindowAdapter customInfoWindowAdapter){
            this.view = view;
            this.medico = medico;
            this.marker = marker;
            this.customInfoWindowAdapter = customInfoWindowAdapter;

        }

        @Override
        public void run() {

            String imageUrl = "http://marcmed.com.br/f/" + medico.getCrm() + ".jpg";
            try {
                bitmap = BitmapFactory.decodeStream((InputStream) new URL(imageUrl).getContent());
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if( reloadImage ){
                            ImageView i = (ImageView) view.findViewById(R.id.badge);
                            i.setImageBitmap(bitmap);
                            i.invalidate();
                            customInfoWindowAdapter.getInfoContents(marker);
                            reloadImage = false;
                        }

                    }
                });
            } catch (Exception e) {
                Log.e(TAG,"Erro ao buscar a imagem", e);
            }
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
                        .title(context.getString(R.string.msg_voceAqui))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

                try{

                    for (MedicoBasicoVo medico : resultado.getMedicos()) {
                        Marker m = mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(medico.getLatitude(), medico.getLongitude()))
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                        medicos.put(m.getId(),medico);

                    }
                }catch(Exception ex){

                }
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(you.getPosition(), 13));
                AndroidUtils.closeWaitDlg();
                
                if (resultado.getMedicos() == null || resultado.getMedicos().size() == 0){
                    AndroidUtils.showMessageDlg(context.getString(R.string.msg_warning),
                            context.getString(R.string.msg_nenhumMedicoEncontrado), context);
                }else{
                    Toast.makeText(context,resultado.getMedicos().size() + " Médicos foram encontrados", Toast.LENGTH_SHORT);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }
    }


    class DialogInfoWindowCallback implements DialogCallback{

        private MedicoBasicoVo medico;
        DialogInfoWindowCallback(MedicoBasicoVo medico){
            this.medico = medico;
        }

        @Override
        public void confirm() {
            Intent chamada = new Intent(Intent.ACTION_DIAL);
            //pega a posição da pessoa
            chamada.setData(Uri.parse("tel:" + medico.getTelefone().trim()));
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
