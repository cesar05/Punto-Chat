package com.jonathan.firebase.pruebasfirebase;

/**
 * Created by CesarM on 14/11/2016.
 */

public class Usuarios {

    String nombreContacto;
    String userID;
    String email;
    double lat;
    double lon;
    String url;
    String token;



    public Usuarios(String nombreContacto, String userID, String email, double lat, double lon, String url, String token){
        this.nombreContacto = nombreContacto;
        this.userID = userID;
        this.email = email;
        this.setLat(lat);
        this.setLon(lon);
        this.url = url;
        this.token = token;
    }


    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setNombreContacto(String nombreContacto) {
        this.nombreContacto = nombreContacto;
    }


    public void setEmail(String email) {
        this.email = email;
    }



    public Usuarios (){
    }

    public String getEmail() {
        return email;
    }

    public String getUserID() {
        return userID;
    }

    public String getNombreContacto() {
        return nombreContacto;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getUrl() {
        return url;
    }

    public String getToken() {
        return token;
    }
}
