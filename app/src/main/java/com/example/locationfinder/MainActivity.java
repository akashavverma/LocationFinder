package com.example.locationfinder;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.*;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,LocationListener {

    private GoogleMap gMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.mapframe, mapFragment).commit();
        mapFragment.getMapAsync(this);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getLocation();
        } else
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},135);
    }

    @SuppressLint("MissingPermission")
    private void getLocation() {
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 10, (LocationListener) this);
        lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 135) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            } else {
                Toast.makeText(this, "User Denied Permission", Toast.LENGTH_LONG).show();
            }
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        gMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Geocoder gcd = new Geocoder(MainActivity.this, Locale.getDefault());
                List<Address> addressList;

                try{
                    addressList = gcd.getFromLocation(latLng.latitude, latLng.longitude, 1);
                    String displayAddress = addressList.get(0).getAddressLine(0);

                    gMap.clear();
                    gMap.addMarker(new MarkerOptions()
                        .position(latLng).title(displayAddress)
                    ).showInfoWindow();

                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onLocationChanged(Location location) {
        if (gMap != null) {
            gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 12.0f));
            gMap.addMarker(new MarkerOptions()
                    .position(new LatLng(location.getLatitude(), location.getLongitude()))
                    .snippet("My Location")
            ).showInfoWindow();
        }
    }
}