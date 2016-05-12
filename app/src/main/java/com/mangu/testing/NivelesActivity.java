package com.mangu.testing;

import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NivelesActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LocationManager locationManager;
    private List<Marker> markerList;
    private int contador = 0;

    private class MarkerTask extends AsyncTask<Void, Integer, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            Random rnd = new Random();
            LatLng malaga = new LatLng(36.721261, -4.421266);
            LatLng random = new LatLng(rnd.nextInt(50), rnd.nextInt(50));
            Marker random_marker = mMap.addMarker(new MarkerOptions().position(random).title("Random"));
            Marker example = mMap.addMarker(new MarkerOptions().position(malaga).title("Malaga").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_image)).snippet(getString(R.string.example)));
            markerList.add(random_marker);
            markerList.add(example);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(malaga));
            mMap.moveCamera(CameraUpdateFactory.zoomIn());
            return null;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        markerList = new ArrayList<>();
        locationManager = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);
        setContentView(R.layout.activity_niveles);
        FragmentManager fmanager = getSupportFragmentManager();
        Fragment fragment = fmanager.findFragmentById(R.id.mapview);
        SupportMapFragment mapFragment = (SupportMapFragment) fragment;
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(),15));
                mMap.animateCamera(CameraUpdateFactory.zoomIn());
                mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
                marker.showInfoWindow();
                return false; //Asi no sobreescribe el comportamiento del Listener original, pudiendo conseguir los botones de abajo
            }
        });
        /*MarkerTask markerTask = new MarkerTask();
        markerTask.execute();*/
        Random rnd = new Random();
        LatLng malaga = new LatLng(36.721261, -4.421266);
        LatLng random = new LatLng(rnd.nextInt(50), rnd.nextInt(50));
        Marker random_marker = mMap.addMarker(new MarkerOptions().position(random).title("Random"));
        Marker example = mMap.addMarker(new MarkerOptions().position(malaga).title("Malaga").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_image)).snippet(this.getString(R.string.example)));
        markerList.add(random_marker);
        markerList.add(example);
        /*mMap.moveCamera(CameraUpdateFactory.newLatLng(malaga));
        mMap.moveCamera(CameraUpdateFactory.zoomIn());*/
        moveCamera(example);

    }

    public void onClickFocus(View view) {
        try {
            List<String> providers = locationManager.getProviders(true);
            Location bestLocation = null;
            for (String provider : providers) {
                Location l = locationManager.getLastKnownLocation(provider);
                if (l == null) {
                    continue;
                }
                if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                    bestLocation = l;
                }
            }
            if(bestLocation!=null) {
                LatLng new_latlng = new LatLng(bestLocation.getLatitude(), bestLocation.getLongitude());
                Marker usted = mMap.addMarker(new MarkerOptions().position(new_latlng).title("Usted está aquí").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_localizar)));
                usted.showInfoWindow();
                mMap.moveCamera(CameraUpdateFactory.newLatLng(new_latlng));
            }
        }catch (SecurityException e) {
            Log.e("SecurityException", e.getLocalizedMessage());
        }catch (NullPointerException ex) {
            Log.e("NullPointerException", ex.getLocalizedMessage());
        }
    }

    public void onClickNext(View view) {
        int next = contador % markerList.size();
        if(next < markerList.size()) {
            /*Marker next_marker = markerList.get(next);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(next_marker.getPosition(),15));
            mMap.animateCamera(CameraUpdateFactory.zoomIn());
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
            next_marker.showInfoWindow();*/
            moveCamera(next);
            contador++;
        }
    }

    public void moveCamera(int next) {
        Marker next_marker = markerList.get(next);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(next_marker.getPosition(),15));
        mMap.animateCamera(CameraUpdateFactory.zoomIn());
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
        next_marker.showInfoWindow();
    }

    public void moveCamera(Marker marker) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(),15));
        mMap.animateCamera(CameraUpdateFactory.zoomIn());
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
        marker.showInfoWindow();
    }
}
