package com.example.nzaidi.gpslocationapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.List;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener, View.OnClickListener, LocationListener {

    public static final String TAG = "TAG";
    private static final int REQUEST_CODE = 1000;
    private GoogleApiClient googleApiClient;
    //private Location location;
   // private TextView txtLocation;

    EditText edtAddress;
    EditText edtMilesPerHour;
    EditText edtMetersPerMile;
    TextView txtDistance;
    TextView txtTime;
    Button btnGetTheData;

    private String destinationLocationAddress = "";

    private TaxiManager taxiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //txtLocation = (TextView)findViewById(R.id.txtLocation);

        edtAddress = (EditText) findViewById(R.id.edtAddress);
        edtMilesPerHour = (EditText) findViewById(R.id.edtMilesPerHour);
        edtMetersPerMile = (EditText) findViewById(R.id.edtMetersPerMile);
        txtDistance = (TextView) findViewById(R.id.txtDistance);
        txtTime = (TextView) findViewById(R.id.txtTime);
        btnGetTheData = (Button) findViewById(R.id.btnGetTheData);

        btnGetTheData.setOnClickListener(MainActivity.this);

        taxiManager = new TaxiManager();

        googleApiClient = new GoogleApiClient.Builder(MainActivity.this)
                          .addConnectionCallbacks(MainActivity.this)
                          .addOnConnectionFailedListener(MainActivity.this)
                          .addApi(LocationServices.API).build();

    }

    //automatically notify user about the location

    @Override
    public void onLocationChanged(Location location) {

        onClick(null);

    }

    // to stop getting info about user location after app gets into pause state
    @Override
    protected void onPause() {
        super.onPause();

        FusedLocationProviderApi fusedLocationProviderApi = LocationServices.FusedLocationApi;
        fusedLocationProviderApi.removeLocationUpdates(googleApiClient, (com.google.android.gms.location.LocationListener) MainActivity.this);
    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onClick(View v) {

        String addressValue = edtAddress.getText().toString();

        boolean isGeoCoding = true;

        if (!addressValue.equals(destinationLocationAddress))
        {
           // addressValue = destinationLocationAddress;
            destinationLocationAddress = addressValue;

            Geocoder geocoder = new Geocoder(getApplicationContext());

            try
            {
                List<Address> myAddress = geocoder.getFromLocationName(destinationLocationAddress,4);

                if (myAddress != null)
                {
                    double latitude = myAddress.get(0).getLatitude();
                    double longitude = myAddress.get(0).getLongitude();

                    Location locationAddress = new Location("My Destination");
                    locationAddress.setLatitude(latitude);
                    locationAddress.setLongitude(longitude);

                    taxiManager.setDestinationLocation(locationAddress);
                }
            }
            catch (Exception e)
            {
                isGeoCoding = false;
                e.printStackTrace();
            }
        }

        int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.ACCESS_COARSE_LOCATION);

        if (permissionCheck == PackageManager.PERMISSION_GRANTED)
        {
            FusedLocationProviderApi fusedLocationProviderApi = LocationServices.FusedLocationApi;
            Location userCurrentLocation = fusedLocationProviderApi.getLastLocation(googleApiClient);

            if (userCurrentLocation != null && isGeoCoding)
            {
                txtDistance.setText(taxiManager.returnTheMilesBetweenCurrentLocationAndDestinationLocation
                                   (userCurrentLocation,Integer.parseInt(edtMetersPerMile.getText().toString())));
                txtTime.setText(taxiManager.returnTheTimeLeftToGetToDestinationLocation
                        (userCurrentLocation, Float.parseFloat(edtMilesPerHour.getText().toString()),
                                Integer.parseInt(edtMetersPerMile.getText().toString())));
            }
        }
        else
        {
            txtDistance.setText("This app is not allowed to access the location.");
            ActivityCompat.requestPermissions
                    (MainActivity.this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},1);


        }

    }

    @Override
    public void onConnected(Bundle bundle) {

        Log.d(TAG,"We are connected to the user's location");
       // showTheUserLocation();

        FusedLocationProviderApi fusedLocationProviderApi = LocationServices.FusedLocationApi;
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        locationRequest.setSmallestDisplacement(5);

        if (googleApiClient.isConnected())
        {
            fusedLocationProviderApi.requestLocationUpdates(googleApiClient,locationRequest, (com.google.android.gms.location.LocationListener) MainActivity.this);
        }
        else
        {
            googleApiClient.connect();
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

        Log.d(TAG,"Connection suspended");

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        Log.d(TAG,"the connection is failed");

        if (connectionResult.hasResolution())
        {
            try
            {
                connectionResult.startResolutionForResult(MainActivity.this,REQUEST_CODE);
            }
            catch (Exception e)
            {
                Log.d(TAG,e.getStackTrace().toString());
            }
        }

        else
        {
            Toast.makeText(MainActivity.this,"google play services is not working",Toast.LENGTH_LONG).show();
            finish();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK)
        {
            googleApiClient.connect();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (googleApiClient != null)
        {
            googleApiClient.connect();
        }
    }


}
