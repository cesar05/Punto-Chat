package com.jonathan.firebase.pruebasfirebase;

import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class Localizacion extends AppCompatActivity implements LocationListener {

    private LocationManager locationManager;
    private Location location;
    private TextView tvLat,tvLon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_localizacion);
        this.locationManager=(LocationManager)getSystemService(LOCATION_SERVICE);
        this.tvLat=((TextView) findViewById(R.id.lat));
        this.tvLon=((TextView) findViewById(R.id.lon));

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            else {
                this.location=this.locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
            }
        }
        else {
            try {
                this.location = this.locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);

            }
            catch (Exception e){
                this.tvLat.setText("Error:" + e.getMessage());
            }
        }

        if(this.location!=null) {
            this.tvLat.setText("LAT:" + this.location.getLatitude());
            this.tvLon.setText("LON:" + this.location.getLongitude());
        }
        else{
            this.tvLat.setText("NULL LOCATION");
        }
        this.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,this);
    }

    @Override
    public void onLocationChanged(Location location) {
        this.tvLat.setText("LAT:"+location.getLatitude());
        this.tvLon.setText("LON:"+location.getLongitude());
        this.location=location;
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
