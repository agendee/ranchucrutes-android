package br.com.wjaa.ranchucrutes.maps;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
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

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import br.com.wjaa.ranchucrutes.R;
import br.com.wjaa.ranchucrutes.activity.AgendamentoActivity;
import br.com.wjaa.ranchucrutes.activity.callback.DialogCallback;
import br.com.wjaa.ranchucrutes.service.RanchucrutesConstants;
import br.com.wjaa.ranchucrutes.utils.AndroidUtils;
import br.com.wjaa.ranchucrutes.vo.ProfissionalBasicoVo;
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
    private Map<String,ProfissionalBasicoVo> profissionais = new HashMap<String, ProfissionalBasicoVo>();
    private GoogleMap.OnMyLocationButtonClickListener onMyLocationButtonClickListener;
    private Fragment fragment;

    public RanchucrutesMaps(Context context, Fragment fragment, GoogleMap.OnMyLocationButtonClickListener onMyLocationButtonClickListener){
        this.context = (FragmentActivity)context;
        this.fragment = fragment;
        this.onMyLocationButtonClickListener = onMyLocationButtonClickListener;
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
            ProfissionalBasicoVo m = profissionais.get(marker.getId());
            Bundle b = new Bundle();
            b.putParcelable(RanchucrutesConstants.PARAM_PROFISSIONAL,m);
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
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(CENTER, 13));
                }
            });
        }

    }

    public void realoadMarker(ResultadoBuscaProfissionalVo resultado) {
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
            ProfissionalBasicoVo profissional = profissionais.get(marker.getId());

            if (profissional != null){


                LoadImage li = new LoadImage(view, profissional, marker, this);
                li.start();

                TextView nomeProfissional = ((TextView) view.findViewById(R.id.nome));
                SpannableString profissionalText = new SpannableString(profissional.getNome());
                profissionalText.setSpan(new ForegroundColorSpan(Color.BLUE), 0, profissionalText.length(), 0);
                nomeProfissional.setText(profissionalText);

                TextView crmProfissional = ((TextView) view.findViewById(R.id.crm));
                String crm = profissional.getCrm() != null ? profissional.getCrm().toString() : "";
                SpannableString crmText = new SpannableString(crm);
                crmText.setSpan(new ForegroundColorSpan(Color.BLUE), 0, crm.length(), 0);
                crmProfissional.setText("CRM: " + crmText);

                TextView especProfissional = ((TextView) view.findViewById(R.id.espec));
                especProfissional.setText(profissional.getEspec());

                TextView endProfissional = ((TextView) view.findViewById(R.id.endereco));
                endProfissional.setText(profissional.getEndereco());

                TextView telProfissional = ((TextView) view.findViewById(R.id.telefone));
                if (profissional.getTelefone() != null && !"".equals(profissional.getTelefone())){

                    telProfissional.setText("Telefone: " + profissional.getTelefone());
                }else{
                    telProfissional.setText("Telefone: --");
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
                String imageUrl = "http://agendee.com.br/f/" + profissional.getCrm() + ".jpg";
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

        private ResultadoBuscaProfissionalVo resultado;
        ReloadMarkerThread(ResultadoBuscaProfissionalVo resultado){
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

                    for (ProfissionalBasicoVo profissional : resultado.getProfissionais()) {

                        Marker m = mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(profissional.getLatitude(), profissional.getLongitude()))
                                .icon(BitmapDescriptorFactory.defaultMarker(
                                        profissional.getTemAgenda() ?
                                                BitmapDescriptorFactory.HUE_BLUE
                                            : BitmapDescriptorFactory.HUE_RED)));

                        profissionais.put(m.getId(), profissional);

                    }
                }catch(Exception ex){

                }
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(you.getPosition(), 13));
                AndroidUtils.closeWaitDlg();
                
                if (resultado.getProfissionais() == null || resultado.getProfissionais().size() == 0){
                    AndroidUtils.showMessageErroDlg(context.getString(R.string.msg_nenhumProfissionalEncontrado), context);
                }else{
                    Toast.makeText(context,resultado.getProfissionais().size() + " Profissionals foram encontrados", Toast.LENGTH_SHORT);
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
