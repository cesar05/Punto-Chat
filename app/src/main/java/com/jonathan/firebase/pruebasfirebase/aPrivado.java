package com.jonathan.firebase.pruebasfirebase;


import android.content.Intent;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;


public class aPrivado extends AppCompatActivity {


    private static final String ACTION_CUSTOM = "co.edu.udea.compumovil.custombroadcast.action.CUSTOM";
    private FirebaseAuth mAuth;
    private DatabaseReference referencia;
    private DatabaseReference refPrivados;
    private DatabaseReference refCoordenadas;
    private DatabaseReference refContactos;
    private ArrayList<Usuarios> contactos = new ArrayList<Usuarios>();
    private AdaptadorPrivados adaptador;
    ListView lista;
    int cont;


    private double lat1;
    private double lon1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a_privado);

        mAuth = FirebaseAuth.getInstance();
        mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                updateUI();
            }
        });
        referencia = FirebaseDatabase.getInstance().getReference();
        refPrivados = referencia.child("Privados").child(mAuth.getCurrentUser().getUid()).child("contactos");

        refContactos = referencia.child("Coordenadas");
        refCoordenadas = referencia.child("Coordenadas").child(mAuth.getCurrentUser().getUid());

        refCoordenadas.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Usuarios post = postSnapshot.getValue(Usuarios.class);
                    lon1 = post.getLon();
                    lat1 = post.getLat();
                    Log.d("USUARIO", "Las coordenadas de este usuario son: "+post.getNombreContacto()+" "+post.getLat()+". "+post.getLon());
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        /*
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Usuarios post = postSnapshot.getValue(Usuarios.class);
                    lon1 = post.getLon();
                    lat1 = post.getLat();
                    Log.d("USUARIO", "Las coordenadas de este usuario son: "+post.getNombreContacto()+" "+post.getLat()+". "+post.getLon());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        */
        /*refCoordenadas.getParent().addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                Log.d("CONTACTO", postSnapshot.getKey());
                for (DataSnapshot postSnapshot2 : postSnapshot.getChildren()) {
                    Log.d("CONTACTO", postSnapshot2.getKey());
                    for (DataSnapshot postSnapshot3 : postSnapshot2.getChildren()) {

                        //Usuarios post = postSnapshot3.getValue(Usuarios.class);

                        //Log.d("CONTACTO", postSnapshot3.getKey());
                        //Usuarios post = postSnapshot3.getValue(Usuarios.class);
                        //Log.d("CONTACTO", "el nombre es: " + post.getNombreContacto() + " y las coordenadas: " + post.getLat() + " y " + post.getLon());
                        /*
                        Log.d("CONTACTO", "el nombre es: " + post.getNombreContacto() + " y las coordenadas: " + post.getLat() + " y " + post.getLon());
                        if (mAuth.getCurrentUser().getUid().equals(post.getUserID())) {
                            if (distance(lat1, lon1, post.getLat(), post.getLon())) {
                                contactos.add(post);
                                lista.deferNotifyDataSetChanged();
                                adaptador.notifyDataSetChanged();
                            }
                        }

                        break;
                    }
                    break;
                }
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    });*/

        //Query cercanos = refContactos.startAt()

        /*refPrivados.push().setValue(new Usuarios("Jonathan", "eFSftuxpwqXovScflX61xf3faEj1", "ozzhed@hotmail.com",0.0, 0.0, "https://lh5.googleusercontent.com/-5dNyypjR2-8/AAAAAAAAAAI/AAAAAAAAAC4/yflidw-rSkk/s96-c/photo.jpg"));
        refPrivados.push().setValue(new Usuarios("Jorge", "fK59nygvqsf3BErSaA3t8M9kPHs1", "jorgepaya108@gmail.com", 0.0, 0.0, "https://lh3.googleusercontent.com/-au6OPdaI3l0/AAAAAAAAAAI/AAAAAAAAAAA/AEMOYSA_cvp4V6_zxUFGni5jw6Mc30RcJg/s96-c/photo.jpg"));
        refPrivados.push().setValue(new Usuarios("Blanca", "lFwZAIOF8gg9BBOMaANNHwEnrFT2", "biancabien13@gmail.com",0.0, 0.0, "null"));*/

        /*Query query = refPrivados.limitToLast(1);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    listaPrivados post = postSnapshot.getValue(listaPrivados.class);

                    Intent service = new Intent(getApplicationContext(), MyService.class);
                    service.putExtra("nombre", post.getNombreContacto());
                    service.putExtra("mensaje", post.getEmail());
                    startService(service);
                    //Notificacion(post.getNombreContacto(), post.getEmail());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });*/
        refCoordenadas.getParent().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                cont = 0;
                contactos.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    cont++;
                    for (DataSnapshot postSnapshot2 : postSnapshot.getChildren()) {
                        Usuarios post = postSnapshot2.getValue(Usuarios.class);
                        Log.d("CONTACTO", "el nombre es: " + post.getNombreContacto() + " y las coordenadas: " + post.getLat() + " y " + post.getLon());
                        if (!mAuth.getCurrentUser().getUid().equals(post.getUserID())) {
                            if (distance(lat1, lon1, post.getLat(), post.getLon())) {
                                contactos.add(post);
                                lista.deferNotifyDataSetChanged();
                                adaptador.notifyDataSetChanged();
                            }
                        }
                        break;
                    }
                    Intent service = new Intent(getApplicationContext(), MyService.class);
                    service.putExtra("cantidad", cont);
                    startService(service);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
       /*refPrivados.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                cont = 0;
                contactos.clear();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    cont++;
                    Usuarios post = postSnapshot.getValue(Usuarios.class);
                    Log.d("contacto", "el nombre es: "+post.getNombreContacto()+" y la id: "+post.getUserID());
                    contactos.add(post);
                    adaptador.notifyDataSetChanged();
                    lista.deferNotifyDataSetChanged();
                }
                Intent service = new Intent(getApplicationContext(), MyService.class);
                service.putExtra("cantidad", cont);
                startService(service);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("Privado", "algo salio mal: "+databaseError.toString());
            }
        });*/
        adaptador = new AdaptadorPrivados(getApplicationContext(), contactos);
        lista = (ListView) findViewById(R.id.listView);
        lista.setAdapter(adaptador);

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView v = (TextView) view.findViewById(R.id.nombreContacto);
                TextView vv = (TextView) view.findViewById(R.id.emailContacto);
                TextView vvv = (TextView) view.findViewById(R.id.idContacto);
                TextView w = (TextView) view.findViewById(R.id.urlContacto);
                TextView ww = (TextView) view.findViewById(R.id.tokenContacto);
                Intent intent = new Intent(getApplicationContext(), ventanaPrivado.class);
                intent.putExtra("nombreContacto", v.getText());
                intent.putExtra("emailContacto", vv.getText());
                intent.putExtra("idContacto", vvv.getText());
                intent.putExtra("imagenContacto", w.getText());
                intent.putExtra("tokenContacto", ww.getText());
                startActivity(intent);
            }
        });
    }

    public void updateUI() {

    }

    public boolean isSignedIn() {
        return (mAuth.getCurrentUser() != null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.privado, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_Chat:
                finish();
                startActivity(new Intent(this, Principal.class));
                return true;

            case R.id.action_Map:
                startActivity(new Intent(this, mapa.class));
                return true;
            case R.id.pref:
                finish();
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
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