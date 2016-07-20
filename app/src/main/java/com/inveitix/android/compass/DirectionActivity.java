package com.inveitix.android.compass;

import android.Manifest;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.math.BigDecimal;
import java.math.RoundingMode;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DirectionActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    public static final int PERMISSIONS_REQUEST_LOCATION = 0;
    public static final int PERMISSIONS_REQUEST_FINE_LOCATION = 1;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    @BindView(R.id.txt_direction)
    TextView txtDirection;

    private double latitude;
    private double longitude;
    private double currentLat;
    private double currentLong;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direction);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    protected void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        }
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestFineLocationPermission();
        }

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission();
        }
        startLocationUpdate();
        Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

        setCurrentLatLng(location);

    }

    private void startLocationUpdate() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestFineLocationPermission();
        }

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission();
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = currentLat;
        longitude = currentLong;
        setCurrentLatLng(location);
        updateUI();
    }

    private void setCurrentLatLng(Location location) {
        currentLat = location.getLatitude();
        currentLat = round(currentLat, 2);
        currentLong = location.getLongitude();
        currentLong = round(currentLong, 2);
    }

    private void updateUI() {
        txtDirection.setText(getDirectionByLocation());
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.e("Error", "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    private void requestLocationPermission() {
        ActivityCompat
                .requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        PERMISSIONS_REQUEST_LOCATION);
    }

    private void requestFineLocationPermission() {
        ActivityCompat
                .requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_REQUEST_FINE_LOCATION);
    }

    private double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public String getDirectionByLocation() {
        if (currentLat > latitude && currentLong == longitude) {
            return "Your direction:\n North";
        } else if (currentLat > latitude && currentLong > longitude) {
            return "Your direction:\n" +
                    " North East";
        } else if (currentLat > latitude && currentLong < longitude) {
            return "Your direction:\n" +
                    " North West";
        } else if (currentLat < latitude && currentLong > longitude) {
            return "Your direction:\n" +
                    " South East";
        } else if (currentLat < latitude && currentLong < longitude) {
            return "Your direction:\n" +
                    " South West";
        } else if (currentLat < latitude && currentLong == longitude) {
            return "Your direction:\n" +
                    " South";
        } else if (currentLat == latitude && currentLong > longitude) {
            return "Your direction:\n" +
                    " East";
        } else if (currentLat == latitude && currentLong > longitude) {
            return "Your direction:\n" +
                    " West";
        }
        return "Your direction:\n" +
                " Heading";
    }
}
