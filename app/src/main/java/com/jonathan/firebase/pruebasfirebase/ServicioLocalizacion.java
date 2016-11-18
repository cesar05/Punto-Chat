package com.jonathan.firebase.pruebasfirebase;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.provider.ContactsContract.CommonDataKinds.Website.URL;


public class ServicioLocalizacion extends Service  {

    private LocationManager locationManager;
    private Location locationAUX;
    private FirebaseAuth mAuth;
    private DatabaseReference refer;
    private DatabaseReference refCoor;
    private static String TAG="LOCALIZACION";
    private LocationListener locationListener;

    public ServicioLocalizacion() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mAuth = FirebaseAuth.getInstance();
        mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

            }
        });
        this.locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Toast.makeText(getApplicationContext(), "ServicioLocalizacion servicio corriendo", Toast.LENGTH_SHORT).show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        }
        Criteria criteria=new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);

        String bestProvider=this.locationManager.getBestProvider(criteria,true);
        LocationProvider locationProvider=this.locationManager.getProvider(bestProvider);

        Log.d(TAG,bestProvider);
        Log.d(TAG," "+locationProvider.getAccuracy());;
        //Log.d("PROVEDOR",locationProvider.);
        this.locationAUX = this.locationManager.getLastKnownLocation(bestProvider);

        if(this.locationAUX==null){
            geolocalizacionApi();
        }
        //this.locationManager.requestLocationUpdates(bestProvider, 5000, 0, this);

        //return super.onStartCommand(intent, flags, startId);
        this.locationListener=new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                locationAUX=location;
                Log.d("ServicioLocalizacion","LAT:"+location.getLatitude());
                Log.d("ServicioLocalizacion","LON:"+location.getLongitude());
                Coordenada();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.d(TAG,provider);
                Log.d(TAG," "+status);
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        };
        this.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,60000, 0,this.locationListener);
        Coordenada();
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void Coordenada(){
        try {
            refer = FirebaseDatabase.getInstance().getReference();
            refCoor = refer.child("Coordenadas").child(mAuth.getCurrentUser().getUid());
            refCoor.removeValue();
            String token = FirebaseInstanceId.getInstance().getToken();

            String foto=  mAuth.getCurrentUser().getPhotoUrl()==null?"":mAuth.getCurrentUser().getPhotoUrl().toString();
            double lat, lon;
            lat = locationAUX == null ? 0 : this.locationAUX.getLatitude();
            lon = locationAUX == null ? 0 : this.locationAUX.getLongitude();
                Usuarios usuario = new Usuarios(mAuth.getCurrentUser().getDisplayName(), mAuth.getCurrentUser().getUid(), mAuth.getCurrentUser().getEmail(), lat, lon, foto, token);
            Log.d(TAG, "LAS COORDENADAS SON: " + lat + " y " + lon);
            refCoor.push().setValue(usuario);
        }
        catch (Exception e){
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.d(TAG,e.getMessage());
        }
    }

    public void geolocalizacionApi(){
        String url = "https://www.googleapis.com/geolocation/v1/geolocate?key=AIzaSyB5YBmSu_Wqzq-yx7sDZwPnkbZCalxoh0Y";

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST,
                        url,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d(TAG, response.toString());
                                Gson gson = new Gson();
                                Geolocalizacion geolocalizacion = gson.fromJson(response.toString(), Geolocalizacion.class);
                                Log.d(TAG, " " + geolocalizacion.getLocation().getLat());
                                Log.d(TAG, " " + geolocalizacion.getLocation().getLng());

                                locationAUX=new Location(locationManager.GPS_PROVIDER);
                                locationAUX.setLatitude(geolocalizacion.getLocation().getLat());
                                locationAUX.setLongitude(geolocalizacion.getLocation().getLng());
                                Coordenada();
                            }
                        }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "Error: " + error.toString());
                    }
                });
        queue.add(jsObjRequest);
    }
}
