package com.chametla.localizacioncombitizayucachofer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationRequest;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;

import com.chametla.localizacioncombitizayucachofer.providers.GeofireProvider;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.security.AuthProvider;

public class UserMap extends AppCompatActivity implements OnMapReadyCallback {

    Button botonCerrar;
    FirebaseAuth mAtuh;
    FirebaseUser mFirebaseUser;


    private GeofireProvider mGeofireProvider;

    GoogleMap mMap;
    SupportMapFragment mMapFragment;

    com.google.android.gms.location.LocationRequest mLocationRequest;
    FusedLocationProviderClient mFusedLocation;
    private final static int LOCATION_REQUEST_CODE = 1;
    private final static int SETTINGS_REQUEST_CODE = 2;

    private Marker mMarker;

    private Button mbIniciar;
    private boolean isConnect = false;

    private LatLng mCurrentLatLng;

    //Callback lines
    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {
                if (getApplicationContext() != null) {
                    //Guardar ubicacion en Firebase
                    mCurrentLatLng = new LatLng(location.getLatitude(),location.getLongitude());

                    //Obtener locación en tiempo real
                    if(mMarker != null){
                        mMarker.remove();
                    }
                    mMarker = mMap.addMarker(new MarkerOptions().position(
                            new LatLng(location.getLatitude(), location.getLongitude())
                    )
                            .title("Tu Ubicación")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.combimark))

                    );
                    mMap.moveCamera(CameraUpdateFactory.newCameraPosition(
                            new CameraPosition.Builder()
                                    .target(new LatLng(location.getLatitude(), location.getLongitude()))
                                    .zoom(17f)
                                    .build()
                    ));

                    updateLocation();
                }
            }
        }
    };
    //Callback lines ends
///////////////////////////////////ON CREATE/////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_map);

        //Instancias GeofireProvider
        mGeofireProvider = new GeofireProvider();

        mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this::onMapReady);

        //Toolbar Instancia
        /*
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        */

        //Instancia boton Iniciar
        mbIniciar = findViewById(R.id.bIniciar);
        mbIniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isConnect){
                    disconect();
                }
                else{
                    startlocation();
                }
            }
        });

        //Instanciar propiedades de Firebase
        mAtuh = FirebaseAuth.getInstance();
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        //Instancias boton cerrar
        botonCerrar = findViewById(R.id.bCerrar);




        //Instancia Fused Location
        mFusedLocation = LocationServices.getFusedLocationProviderClient(this);


        ////////////////////Metodo de boton

        botonCerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cerrarSesion();
            } //Boton cerrar sesion

            private void cerrarSesion() {
                mAtuh.signOut(); // Cierra sesión en Firebase
                Intent intent = new Intent(UserMap.this, SelectOptionLogin.class);
                startActivity(intent);
            }
        });
        ///////////////////////Fin metodo boton
    }

    ///////////////////////////////////ON CREATE/////////////////////////////////////////

    ///////////////////////////////////Localizacion en tiempo real/////////////////
    private void updateLocation(){

        mGeofireProvider.saveLocation(FirebaseAuth.getInstance().getCurrentUser().getUid(),mCurrentLatLng);
    }


    public boolean existSession(){
        boolean exist = false;
        if(mAtuh.getCurrentUser() != null){
            exist= true;
        }
        return exist;
    }


    ///////////////////////////////////Localizacion en tiempo real/////////////////

    //Metodo OnMap sobreescrito
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }



        mLocationRequest = new com.google.android.gms.location.LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.QUALITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(5);

        startlocation();

    }
    //Fin metodo OnMap READY


    ///////////////////////////Cerrar Sesión
    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.action_logout){
            cerrarSesion();
        }
        return super.onOptionsItemSelected(item);
    }
    private void cerrarSesion() {
        mAtuh.signOut(); // Cierra sesión en Firebase
        Intent intent = new Intent(UserMap.this, SelectOptionLogin.class);
        startActivity(intent);
    }
    */
    ///////////////////////////////Fin Cerrar Sesión



    //Permisos ubicacion
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        mFusedLocation.requestLocationUpdates(mLocationRequest,mLocationCallback, Looper.myLooper());
                        mMap.setMyLocationEnabled(false);
                    }
                }
            else {
                    checkLocationPermission();
                }
        } else {
                checkLocationPermission();
                }
    }


    /*Revisar GPS activo

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SETTINGS_REQUEST_CODE && gpsActivated()) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mFusedLocation.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
        }
        else{
            showNoGps();
        }
    }

    private void showNoGps(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Activar ubicación para continuar")
                .setPositiveButton("Configuarción", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), SETTINGS_REQUEST_CODE );
                    }
                })
                .create()
                .show();
    }


    private boolean gpsActivated(){
        boolean isActive = false;
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            isActive = true;
        }
            return isActive;
    }*/
//FIn Permisos activos

    private void disconect(){

        if(mFusedLocation != null){
            mbIniciar.setText("Inicio");
            isConnect = false;
            mFusedLocation.removeLocationUpdates(mLocationCallback);

            mGeofireProvider.removeLocation(mAtuh.getUid());
        }
    }



    //Inciciar Ubicacion haciendo uso del Escuchador
    private void startlocation(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
                mbIniciar.setText("Fin");
                isConnect = true;
                mFusedLocation.requestLocationUpdates(mLocationRequest,mLocationCallback, Looper.myLooper());
                mMap.setMyLocationEnabled(false);
            }
            else {
                checkLocationPermission();
            }
        }
        else{
                mFusedLocation.requestLocationUpdates(mLocationRequest,mLocationCallback, Looper.myLooper());
                mMap.setMyLocationEnabled(false);
        }

    }



    private void  checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
                new AlertDialog.Builder(this).setTitle("Proporciona los permisos para continuar.").setMessage("Se requiere los permisos de ubicación")
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(UserMap.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_REQUEST_CODE);
                            }
                        })
                        .create()
                        .show();

            }
            else{
                ActivityCompat.requestPermissions(UserMap.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_REQUEST_CODE);
            }

        }
    }
    //Fin codigo para permisos
}