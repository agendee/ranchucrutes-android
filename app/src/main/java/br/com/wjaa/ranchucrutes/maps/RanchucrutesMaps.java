package br.com.wjaa.ranchucrutes.maps;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
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

import java.util.HashMap;
import java.util.Map;

import br.com.wjaa.ranchucrutes.R;
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


    private static final LatLng CENTER = new LatLng(-23.545616, -46.629299);

    private FragmentActivity context;
    private GoogleMap mMap;
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

        mLastSelectedMarker = marker;
        // We return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Toast.makeText(context, "Click Info Window", Toast.LENGTH_SHORT).show();
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
        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                EditText editText = (EditText) context.findViewById(R.id.edtCep);
                Location l = mMap.getMyLocation();
                editText.setText("Minha localização");

                return true;
            }
        });
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
        private final View mContents;

        CustomInfoWindowAdapter() {
            mWindow = context.getLayoutInflater().inflate(R.layout.custom_info_window, null);
            mContents = context.getLayoutInflater().inflate(R.layout.custom_info_contents, null);
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
            //((ImageView) view.findViewById(R.id.badge)).setImageBitmap(ImageService.getBitmapFromURLWithScale();
            MedicoBasicoVo medico = medicos.get(marker.getId());

            if (medico != null){
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
            }else{
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

                for (MedicoBasicoVo medico : resultado.getMedicos()) {
                    Marker m = mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(medico.getLatitude(), medico.getLongitude()))
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                    medicos.put(m.getId(),medico);

                }
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(you.getPosition(), 13));

                AndroidUtils.closeWaitDlg();

            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }
    }
}
