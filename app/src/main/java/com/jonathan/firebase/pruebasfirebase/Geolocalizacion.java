package com.jonathan.firebase.pruebasfirebase;

/**
 * Created by jonathan on 16/11/2016.
 */

public class Geolocalizacion {

    /**
     * lat : 6.254913999999999
     * lng : -75.6149779
     */

    private LocationBean location;
    /**
     * location : {"lat":6.254913999999999,"lng":-75.6149779}
     * accuracy : 6082
     */

    private int accuracy;

    public LocationBean getLocation() {
        return location;
    }

    public void setLocation(LocationBean location) {
        this.location = location;
    }

    public int getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(int accuracy) {
        this.accuracy = accuracy;
    }

    public static class LocationBean {
        private double lat;
        private double lng;

        public double getLat() {
            return lat;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }

        public double getLng() {
            return lng;
        }

        public void setLng(double lng) {
            this.lng = lng;
        }
    }
}
