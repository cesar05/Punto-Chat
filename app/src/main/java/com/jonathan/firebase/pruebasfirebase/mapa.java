package com.jonathan.firebase.pruebasfirebase;

/**
 * Created by jonathan on 12/11/2016.
 */

import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

import de.hdodenhof.circleimageview.CircleImageView;

public class mapa extends AppCompatActivity  implements OnMapReadyCallback{

    private LocationManager locationManager;
    private Location location;
    private TextView tvLat,tvLon;
    private GoogleMap mMap;
    private MapFragment mMapFragment;
    //private List<Marker> listMarket;
    private ArrayList<Marker> listMarket;
    private ArrayList<MarkerOptions> optionMarket;
    public CircleImageView fotoMapa;

    private FirebaseAuth mAuth;
    private DatabaseReference referencia,refCoordenadas,refSelf;

    private double lat1=0;
    private double lon1=0;
    private int caso=0;

    private CircleOptions circleOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa);
        this.listMarket=new ArrayList<Marker>();
        this.optionMarket=new ArrayList<MarkerOptions>();

        fotoMapa = (CircleImageView) findViewById(R.id.fotoMapaUser);

        GoogleMapOptions options = new GoogleMapOptions();
        options.mapType(GoogleMap.MAP_TYPE_NORMAL)
                .compassEnabled(false)
                .rotateGesturesEnabled(false)
                .tiltGesturesEnabled(false)
                .camera(new CameraPosition(new LatLng(lat1,lon1),15,15,15));


        mMapFragment = MapFragment.newInstance(options);
        mMapFragment.getMapAsync(mapa.this);
        android.app.FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.activity_mapa, mMapFragment);
        fragmentTransaction.commit();

        mAuth = FirebaseAuth.getInstance();
        referencia = FirebaseDatabase.getInstance().getReference();

        mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                //updateUI();
            }
        });
        Log.d("pruebagetID",mAuth.getCurrentUser().getUid());

        refCoordenadas = referencia.child("Coordenadas");
        refSelf        = referencia.child("Coordenadas").child(mAuth.getCurrentUser().getUid());

        refSelf.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Usuarios post = postSnapshot.getValue(Usuarios.class);
                    lon1 = post.getLon();
                    lat1 = post.getLat();
                    Log.d("USUARIO", "Las coordenadas de este usuario son: "+post.getNombreContacto()+" "+post.getLat()+". "+post.getLon());
                    caso=1;
                    mMapFragment.getMapAsync(mapa.this);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        refCoordenadas.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                optionMarket.clear();
                if (mAuth.getCurrentUser() != null) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        for (DataSnapshot postSnapshot2 : postSnapshot.getChildren()) {
                            Usuarios post = postSnapshot2.getValue(Usuarios.class);
                            Log.d("CONTACTO", "el nombre es: " + post.getNombreContacto() + " y las coordenadas: " + post.getLat() + " y " + post.getLon());

                            if (mAuth.getCurrentUser().getUid().equals(post.getUserID())) {
                                //String foto=  post.getUrl()==null?"null":post.getUrl().toString();
                                optionMarket.add(new MarkerOptions()
                                        .position(new LatLng(post.getLat(), post.getLon()))
                                        .title("Yo")
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)).snippet(post.getUrl()));
                                circleOptions=new CircleOptions()
                                        .center(new LatLng(post.getLat(), post.getLon()))
                                        .radius(500);
                            } else {
                                if (lat1 != 0 && lon1 != 0) {

                                    //String foto=post.getUrl()==null?"null":post.getUrl().toString();
                                    if (distance(lat1, lon1, post.getLat(), post.getLon())) {
                                    optionMarket.add(new MarkerOptions()
                                            .position(new LatLng(post.getLat(), post.getLon()))
                                            .title(post.getNombreContacto()).snippet(post.getUrl())
                                    );
                                    }
                                }
                            }
                            break;
                        }
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap map) {
        map.clear();
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener()
        {
            @Override
            public boolean onMarkerClick(Marker arg0) {
//                if(arg0.getTitle().equals("MyHome")) // if marker source is clicked
                    Glide.with(mapa.this).load(arg0.getSnippet()).into(fotoMapa);

                //Toast.makeText(mapa.this,arg0.getTitle(),Toast.LENGTH_LONG).show();
                return false;
            }
        });
        int size=optionMarket.size();
        for(int k=0;k<size;k++) {
            map.addMarker(optionMarket.get(k));
        }
        if(caso==1){
            map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(lat1,lon1)));
            caso=0;
            map.addCircle(new CircleOptions()
                    .center(new LatLng(lat1, lon1))
                    .radius(500));
        }else{
            if(this.circleOptions==null){
                map.addCircle(new CircleOptions()
                        .center(new LatLng(lat1, lon1))
                        .radius(500));
            }else{
                map.addCircle(this.circleOptions);
            }

        }
    }

    private  boolean distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        dist = dist * 1.609344;
        Log.d("DISTANCIA", "La distancia entre los puntos es: "+dist);
        return (dist<=0.5);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }
    private  double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }
}

