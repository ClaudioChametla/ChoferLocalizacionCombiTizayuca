package com.chametla.localizacioncombitizayucachofer.providers;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class GeofireProvider {
    private DatabaseReference mDatabase;
    private GeoFire mGeofire;

    public GeofireProvider(){
        mDatabase = FirebaseDatabase.getInstance().getReference().child("OperadorEnServicio");
        mGeofire = new GeoFire(mDatabase);

    }
    public void saveLocation(String id, LatLng latLng){
        mGeofire.setLocation(id, new GeoLocation(latLng.latitude, latLng.longitude));
    }
    public void removeLocation(String id){
        mGeofire.removeLocation(id);
    }

}
