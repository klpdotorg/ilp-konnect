package in.org.klp.ilpkonnect;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.concurrent.TimeUnit;

import in.org.klp.ilpkonnect.utils.Constants;

public class QuestionActivity extends AppCompatActivity {

    private FusedLocationProviderClient fusedLocationProviderClient = null;
    Double latitude = 0d, longitude = 0d;


    private LocationManager locManager;
    private LocationListener locListener = new MyLocationListener();
    private boolean gps_enabled = false;
    private boolean network_enabled = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        this.setTitle(getResources().getString(R.string.title_activity_question));

        if (Constants.surveyType == 2) {

            registerForLocationUpdates();



        }


    }


    @SuppressLint("MissingPermission")
    void registerForLocationUpdates() {
        FusedLocationProviderClient locationProviderClient = getFusedLocationProviderClient();
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(TimeUnit.SECONDS.toMillis(3000));
        locationRequest.setFastestInterval(TimeUnit.SECONDS.toMillis(1000));
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        Looper looper = Looper.myLooper();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationProviderClient.requestLocationUpdates(locationRequest, locationCallback, looper);
    }

    public void l1()
    {
        locManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);


        try {
            gps_enabled = locManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            network_enabled = locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        if (gps_enabled) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locListener);
        }
        if (network_enabled) {
            locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locListener);
        }
    }


    @NonNull
    private FusedLocationProviderClient getFusedLocationProviderClient() {
        if (fusedLocationProviderClient == null) {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
        }
        return fusedLocationProviderClient;
    }


    void unregisterForLocationUpdates() {
        if (fusedLocationProviderClient != null) {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        }
    }

    private LocationCallback locationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            Location lastLocation = locationResult.getLastLocation();
            updatePosition(lastLocation);
        }
    };

    void updatePosition(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
       /* String accuracyString = createAccuracyString(location.getAccuracy());
        latitudeValue.setText(latitudeString);
        longitudeValue.setText(longitudeString);
        accuracyValue.setText(accuracyString);*/

       // Toast.makeText(getApplicationContext(), latitude + ":" + longitude, Toast.LENGTH_SHORT).show();
    }


    public Double getLati() {
        return latitude;
    }

    public Double getLong() {

        return longitude;
    }


    class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                // This needs to stop getting the location data and save the battery power.
                 locManager.removeUpdates(locListener);

                longitude=location.getLongitude();
                latitude=location.getLatitude();
                String altitiude = "Altitiude: " + location.getAltitude();
                String accuracy = "Accuracy: " + location.getAccuracy();
                String time = "Time: " + location.getTime();
              /*  editTextShowLocation.setText(londitude + "\n" + latitude + "\n" + altitiude + "\n" + accuracy + "\n" + time);
                progress.setVisibility(View.GONE); */
             // Toast.makeText(getApplicationContext(),latitude+"ss"+londitude,Toast.LENGTH_SHORT).show();
            }         }         @Override
        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub
            }
             @Override
            public void onProviderEnabled(String provider) {
                 // TODO Auto-generated method stub
                 }
                 @Override
                 public void onStatusChanged(String provider, int status, Bundle extras) {
                     // TODO Auto-generated method stub
                     }
    }


            }