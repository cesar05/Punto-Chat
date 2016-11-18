package com.jonathan.firebase.pruebasfirebase;

/**
 * Created by jonathan on 06/11/2016.
 */
public class listaPrivados{
    String nombreContacto;
    String userID;
    String email;
    String urlFoto;

    //String ubicacion;
    //String foto;

    public listaPrivados (){
    }

    public listaPrivados(String nombreContacto, String userID, String email, String urlFoto){
            this.nombreContacto = nombreContacto;
            this.userID = userID;
            this.email = email;
            this.urlFoto = urlFoto;

    }

    public String getNombreContacto() {
        return nombreContacto;
    }

    public String getUserID() {
        return userID;
    }

    public String getEmail() {
        return email;
    }

    public String getUrlFoto() {
        return urlFoto;
    }
}
