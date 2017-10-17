package com.example2.acer.memorableplace;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,GoogleMap.OnMapLongClickListener,LocationListener {

    private GoogleMap mMap;
    int location= -1;
    LocationManager locationManager;
    String provider;

    public void onMapLongClick(LatLng point) {
        String lable = new Date().toString();
        Geocoder geocoder=new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            List<Address> addressList=geocoder.getFromLocation(point.latitude, point.longitude,1);

            if (addressList != null && addressList.size()>0){
              lable=  addressList.get(0).getAddressLine(0);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        MainActivity.places.add(lable);
        MainActivity.arrayAdapter.notifyDataSetChanged();
        MainActivity.location.add(point);

        mMap.addMarker(new MarkerOptions().position(point).title(lable));


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        locationManager =(LocationManager)getSystemService(Context.LOCATION_SERVICE);
        provider=locationManager.getBestProvider(new Criteria(),false);
        Location userLocation=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        ActionBar actionBar= getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Intent i=getIntent() ;
        location= i.getIntExtra("LocationInfo",-1);

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
         // -1 mean error in point and 0 mean put a new point
        if (location != -1 && location != 0) {
            locationManager.removeUpdates(this);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(MainActivity.location.get(location), 10));
            mMap.addMarker(new MarkerOptions().position(MainActivity.location.get(location)).title(MainActivity.places.get(location)));
        }else{
            locationManager.requestLocationUpdates(provider,400,1,this);
        }
        mMap.setOnMapLongClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (location == -1 || location == 0) {

            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,400,1,this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                locationManager.removeUpdates(this);
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onLocationChanged(Location location) {

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),location.getLongitude()), 10));

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
