package com.mangu.testing;

import android.content.Context;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NoiseActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LocationManager mLocationManager;
    private List<Marker> mMarkerList;
    private int mCounter = 0;
    private List<String> mStringMarkerList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mStringMarkerList = new ArrayList<>();
        mMarkerList = new ArrayList<>();
                /*MarkerTask markerTask = new MarkerTask();
        markerTask.execute();*/
        mLocationManager = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);
        setContentView(R.layout.activity_noise);
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
        for(String s : mStringMarkerList) {
            String[] splitted = s.split(";");
            String[] latlng = splitted[1].split(",");
            LatLng stringLatLng = new LatLng(Double.valueOf(latlng[0]),Double.valueOf(latlng[1]));
            Marker stringMarker = mMap.addMarker(new MarkerOptions().position(stringLatLng).title("Nivel de ruido:"+splitted[0]).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_image)));
            mMarkerList.add(stringMarker);
        }
        Random rnd = new Random();
        LatLng malaga = new LatLng(36.721261, -4.421266);
        LatLng random = new LatLng(rnd.nextInt(50), rnd.nextInt(50));
        Marker random_marker = mMap.addMarker(new MarkerOptions().position(random).title("Random"));
        Marker example = mMap.addMarker(new MarkerOptions().position(malaga).title("Malaga").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_image)).snippet(this.getString(R.string.example)));
        mMarkerList.add(random_marker);
        mMarkerList.add(example);
        /*mMap.moveCamera(CameraUpdateFactory.newLatLng(malaga));
        mMap.moveCamera(CameraUpdateFactory.zoomIn());*/
        moveCamera(example);

    }

    public void onClickFocus(View view) {
        try {
            List<String> providers = mLocationManager.getProviders(true);
            Location bestLocation = null;
            for (String provider : providers) {
                Location l = mLocationManager.getLastKnownLocation(provider);
                if (l == null) {
                    continue;
                }
                if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                    bestLocation = l;
                }
            }
            if(bestLocation!=null) {
                LatLng newLatLng = new LatLng(bestLocation.getLatitude(), bestLocation.getLongitude());
                Marker actualLocation = mMap.addMarker(new MarkerOptions().position(newLatLng).title("Usted está aquí").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_localizar)));
                actualLocation.showInfoWindow();
                mMap.moveCamera(CameraUpdateFactory.newLatLng(newLatLng));
            }
        }catch (SecurityException e) {
            Log.e("SecurityException", e.getLocalizedMessage());
        }catch (NullPointerException ex) {
            Log.e("NullPointerException", ex.getLocalizedMessage());
        }
    }

    public void onClickNext(View view) {
        /*  Marker next_marker = mMarkerList.get(next);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(next_marker.getPosition(),15));
            mMap.animateCamera(CameraUpdateFactory.zoomIn());
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
            next_marker.showInfoWindow();
            */
        int next = mCounter % mMarkerList.size();
        if(next < mMarkerList.size()) {
            moveCamera(next);
            mCounter++;
        }
    }

    public void moveCamera(int next) {
        Marker next_marker = mMarkerList.get(next);
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

    private class MarkerTask extends AsyncTask<Void, Integer, Void> {
        Context context;
        public MarkerTask(Context context) {
            this.context = context;
        }
        @Override
        protected Void doInBackground(Void... params) {
            //Suponiendo que ya tenemos el JSON por Volley, que lo mockeo aqui
            JSONObject jsonObject = new JSONObject();
            JSONObject jsonArray = new JSONObject();
            try {
                jsonObject.put("value",params[0]);
                jsonObject.put("localization",params[1]);
                jsonArray.put("marker",jsonObject);
            } catch (JSONException e) {
                Log.e("JSONException",e.getMessage());
            }catch(NullPointerException e) {
                Log.e("NullPointerException",e.getMessage());
            }
            @SuppressWarnings("UnusedAssignment") JSONObject otroObject = new JSONObject();
            try {
                otroObject = jsonArray.getJSONObject("marker");
                String toAdd = otroObject.getString("value")+ ";"+ otroObject.getString("localization");
                mStringMarkerList.add(toAdd);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

    }
}
