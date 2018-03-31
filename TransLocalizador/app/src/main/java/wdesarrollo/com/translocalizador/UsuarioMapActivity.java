package wdesarrollo.com.translocalizador;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

public class UsuarioMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private ImageButton mMenu;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;

    private LatLng pickUpLocation;
    private Boolean requestBol = false;

    Location mLastLocation;
    LocationRequest mLocationRequest;
    private FusedLocationProviderClient mFusedLocationClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario_map);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



        mMenu = findViewById(R.id.btnMenu);

        mDrawerLayout = findViewById(R.id.drawer_layout);

        mNavigationView = findViewById(R.id.navi);



        mMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });

        MenuItem item = mNavigationView.getMenu().getItem(0);
        item.setChecked(true);

        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){

                    case R.id.menu_mapa:
                        item.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        break;

                    case R.id.menu_conf:
                        //Toast.makeText(UsuarioMapActivity.this,"Configuracion",Toast.LENGTH_LONG).show();
                        item.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        break;
                    case R.id.menu_acerca:
                        //Toast.makeText(UsuarioMapActivity.this,"Acerca de",Toast.LENGTH_LONG).show();
                        item.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        break;
                    case R.id.menu_cerrar:
                        //Toast.makeText(UsuarioMapActivity.this,"Cerrar",Toast.LENGTH_LONG).show();
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(UsuarioMapActivity.this,MainActivity.class);
                        startActivity(intent);
                        finish();

                        //item.setChecked(true);
                        //mDrawerLayout.closeDrawers();
                        break;

                    default:
                        break;
                }
                return true;
            }
        });


    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mLocationRequest = new LocationRequest();
        /*mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);*/
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        // Add a marker in Sydney and move the camera

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){

            }else{
                checkLocationPermission();
            }
        }
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
        mMap.setMyLocationEnabled(true);


    }

    private int radius = 1;
    private Boolean transporteFound = false;
    private String transporteFoundID;

    GeoQuery geoQuery;
    private void getTransportesCercanos() {

        DatabaseReference transporteTrabjando = FirebaseDatabase.getInstance().getReference().child("transporteTrabajando");
        GeoFire geoFire = new GeoFire(transporteTrabjando);

        geoQuery = geoFire.queryAtLocation(new GeoLocation(pickUpLocation.latitude,pickUpLocation.longitude ),radius);
        geoQuery.removeAllListeners();


        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                if(!transporteFound) {
                    transporteFound = true;
                    transporteFoundID = key;

                    DatabaseReference transporteRef = FirebaseDatabase.getInstance().getReference().child("UsuariosTransporte").child("Transportes").child(transporteFoundID);
                    String usuarioId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    /*HashMap map = new HashMap();
                    map.put("customerRideId",usuarioId);
                    transporteRef.updateChildren(map);*/

                    getTransporteLocation();
                }
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                if(!transporteFound){
                    radius++;
                    getTransportesCercanos();
                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });

    }
    private Marker mDriverMarker;

    private DatabaseReference driverLocationRef;
    private ValueEventListener driverLocationListener;
    public void getTransporteLocation(){

        driverLocationRef = FirebaseDatabase.getInstance().getReference().child("transporteTrabajando").child(transporteFoundID).child("l");


        driverLocationListener = driverLocationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    List<Object> map = (List<Object>) dataSnapshot.getValue();
                    double locationLat = 0;
                    double locationLng = 0;
                    if (map.get(0) != null) {
                        locationLat = Double.parseDouble(map.get(0).toString());
                    }
                    if (map.get(1) != null) {
                        locationLng = Double.parseDouble(map.get(1).toString());
                    }
                    LatLng driverLatLng = new LatLng(locationLat, locationLng);
                    if (mDriverMarker != null) {
                        mDriverMarker.remove();
                    }

                    mDriverMarker = mMap.addMarker(new MarkerOptions().position(driverLatLng).title("Aquí Estoy!!!").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_person)));
                }else{
                    mDriverMarker.remove();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    LocationCallback mLocationCallback = new LocationCallback(){
        @Override
        public void onLocationResult(LocationResult locationResult) {

            for (Location location: locationResult.getLocations()){

                if (getApplicationContext()!=null){
                    requestBol = true;
                    mLastLocation = location;
                    pickUpLocation = new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude());
                    LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());

                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(17));

                    getTransportesCercanos();
                }
            }
        }
    };

    private void checkLocationPermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){

                new AlertDialog.Builder(this)
                        .setTitle("Give permission")
                        .setMessage("Give permission message")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(UsuarioMapActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
                            }
                        }).create().show();

            }else{
                ActivityCompat.requestPermissions(UsuarioMapActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                        mMap.setMyLocationEnabled(true);
                    }
                }else{
                    Toast.makeText(getApplicationContext(),"Please provide the permission",Toast.LENGTH_LONG).show();
                }
                break;
        }
    }
}
