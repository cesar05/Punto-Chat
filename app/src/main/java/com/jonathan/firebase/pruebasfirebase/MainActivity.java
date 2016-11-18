package com.jonathan.firebase.pruebasfirebase;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ui.email.SignInActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private DatabaseReference refMain;
    private DatabaseReference refUser;
    private DatabaseReference refCoor;
    private Intent intent;
    private CircleImageView img_UserPhoto;
    private static final int RC_SIGN_IN = 0;
    FirebaseAuth auth;
    TextView nombre;
    TextView email;
    Query query;
    /*Localizacion*/
    private TextView tvLat,tvLon;
    private Boolean bandera;
    public CircleImageView fotoUser;
    String imageUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("PuntoChat");

        fotoUser = (CircleImageView) findViewById(R.id.imagenUser);

        String token = FirebaseInstanceId.getInstance().getToken();


        auth = FirebaseAuth.getInstance();
        nombre = (TextView) findViewById(R.id.lblnombre);
        email = (TextView) findViewById(R.id.lblemail);
        bandera = false;

        if (auth.getCurrentUser() != null) { //el usuario ya esta conectado
            //startActivity(new Intent(this, Principal.class));
            startService(new Intent(this,ServicioLocalizacion.class));
            nombre.setText(auth.getCurrentUser().getDisplayName());
            email.setText(auth.getCurrentUser().getEmail());

            if(auth.getCurrentUser().getPhotoUrl() != null) {
                Glide.with(this).load(auth.getCurrentUser().getPhotoUrl()).into(fotoUser);
            }
            Log.d("FOTO", "La url es: "+auth.getCurrentUser().getPhotoUrl());
        } else {
            startActivityForResult(AuthUI.getInstance()
                    .createSignInIntentBuilder().setTheme(R.style.PuntoChatTema).setIsSmartLockEnabled(false)
                    .setProviders(AuthUI.EMAIL_PROVIDER,
                            AuthUI.GOOGLE_PROVIDER).build(), RC_SIGN_IN);
        }
        findViewById(R.id.btncerrar).setOnClickListener(this);
    }
    public boolean usuarioExistente(){
        query = refUser.orderByChild("userID").equalTo(auth.getCurrentUser().getUid());

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("USUARIO","entro");
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    bandera = true;
                    Usuarios user = postSnapshot.getValue(Usuarios.class);
                    Log.d("USUARIO", "El nombre del usuario es: " + user.getNombreContacto());
                }
                if (!bandera) {
                    if (auth.getCurrentUser().getPhotoUrl() != null) {
                        String token = FirebaseInstanceId.getInstance().getToken();
                        Usuarios usuario = new Usuarios(auth.getCurrentUser().getDisplayName(), auth.getCurrentUser().getUid(), auth.getCurrentUser().getEmail(), 0, 0, auth.getCurrentUser().getPhotoUrl().toString(), token);
                        refUser.push().setValue(usuario);
                        Log.d("USUARIO", "El usuario no existe, agregado");
                        bandera = true;
                    }else{
                        String token = FirebaseInstanceId.getInstance().getToken();
                        Usuarios usuario = new Usuarios(auth.getCurrentUser().getDisplayName(), auth.getCurrentUser().getUid(), auth.getCurrentUser().getEmail(), 0, 0, "null", token);
                        refUser.push().setValue(usuario);
                        Log.d("USUARIO", "El usuario no existe, agregado");
                        bandera = true;
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        if (query == null) {
            return false;
        }else {
            return true;
        }
    }
        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == RC_SIGN_IN){
                if (resultCode == RESULT_OK){//el usuario entró
                    refMain = FirebaseDatabase.getInstance().getReference();
                    refUser = refMain.child("Usuarios").child(auth.getCurrentUser().getUid());
                    usuarioExistente();
                    startService(new Intent(this,ServicioLocalizacion.class));
                    nombre.setText(auth.getCurrentUser().getDisplayName());
                    if(auth.getCurrentUser().getPhotoUrl() != null) {
                        Glide.with(this).load(auth.getCurrentUser().getPhotoUrl()).into(fotoUser);
                    }
                    //startActivity(new Intent(this, Principal.class));
                }else{ //el usuario no ha entrado
                    finish();
                }
            }
    }
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btncerrar){
            refMain = FirebaseDatabase.getInstance().getReference();
            refUser = refMain.child("Usuarios").child(auth.getCurrentUser().getUid());
            refCoor = refMain.child("Coordenadas").child(auth.getCurrentUser().getUid());

            refCoor.removeValue();
            refUser.removeValue();
            stopService(new Intent(getApplicationContext(), MyService.class));
            stopService(new Intent(getApplicationContext(), ServicioLocalizacion.class));
            AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    finish();
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_Privado:
                startActivity(new Intent(this, aPrivado.class));
                return true;

            case R.id.action_Chat:
                startActivity(new Intent(this, Principal.class));
                return true;
            case R.id.cerrar:
                this.onClick(findViewById(R.id.btncerrar));
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void clickChat(View view) {
        startActivity(new Intent(this, Principal.class));
    }


    public void clickPrivado(View view) {
        startActivity(new Intent(this, aPrivado.class));
    }

    public void clickAgregar(View view) {
        startActivity(new Intent(this, agregarContactos.class));
    }

    public void clickMapa(View view) {
        startActivity(new Intent(this, mapa.class));
    }

    @Override
    protected void onPause() {
        Log.d("CICLO", "onpause");
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
