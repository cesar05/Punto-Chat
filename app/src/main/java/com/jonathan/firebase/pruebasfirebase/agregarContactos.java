package com.jonathan.firebase.pruebasfirebase;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class agregarContactos extends AppCompatActivity implements FirebaseAuth.AuthStateListener {


    private FirebaseAuth mAuth;
    private DatabaseReference dbref;
    private DatabaseReference contactoRef;
    private ArrayList<contacto> contactos =  new ArrayList<contacto>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_contactos);


        mAuth = FirebaseAuth.getInstance();
        mAuth.addAuthStateListener(this);



        dbref = FirebaseDatabase.getInstance().getReference();
        contactoRef = dbref.child("Personas").child(mAuth.getCurrentUser().getUid());



        Log.d("TAGUID", "El id es: "+mAuth.getCurrentUser().getUid());

        contactos.add(new contacto("Cesar", "esG4yID", "Usted es gay"));
        contactos.add(new contacto("Santiago", "esG4yID", "Usted también es gay"));
        contactos.add(new contacto("Hillary", "T34m0", "en serio"));

        usuario Jonathan = new usuario(mAuth.getCurrentUser().getDisplayName(), contactos);
        contactoRef.push().setValue(Jonathan);
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

        updateUI();
    }

    public void updateUI() {
        /* Sending only allowed when signed in
        mSendButton.setEnabled(isSignedIn());
        mMessageEdit.setEnabled(isSignedIn());*/
    }



    public static class usuario {
        String nombre;
        ArrayList<contacto> contacto;

        public usuario(){
        }


        public usuario (String nombre, ArrayList<contacto> contacto){
            this.nombre = nombre;
            this.contacto = contacto;
        }
        public String getNombre() {
            return nombre;
        }

        public ArrayList<agregarContactos.contacto> getContacto() {
            return contacto;
        }
    }

    public static class contacto {
        String nombre;
        String uid;
        String mensaje;

        public contacto(){
        }

        public contacto (String nombre, String uid, String mensaje){
            this.nombre = nombre;
            this.uid = uid;
            this.mensaje = mensaje;
        }
        public String getNombre() {
            return nombre;
        }

        public String getMensaje() {
            return mensaje;
        }

        public String getUid() {
            return uid;
        }
    }
}
