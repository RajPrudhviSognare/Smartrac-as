package com.experis.smartrac.as;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.experis.smartrac.as.R;


public class GetCurrentLocationActivity extends AppCompatActivity {

    private TextView latLongTextViewID;

    private ImageView currentLocationtopbarbackImageViewID;

    private boolean isGPSEnabled = false;
    private boolean isNetworkEnabled = false;
    private boolean canGetLocation = false;
    private Location location; // location
    private double latitude = 0.0d; // latitude
    private double longitude = 0.0d; // longitude

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1; // 1 meter
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1 / 2; // 1/2 minute
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_current_location);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.current_location_topbar_title);

        latLongTextViewID = (TextView) findViewById(R.id.latLongTextViewID);
        currentLocationtopbarbackImageViewID = (ImageView) findViewById(R.id.currentLocationtopbarbackImageViewID);

        //Added Later//////////////////
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            System.out.println("Inside GetCurrentLocationActivity Page checkAllPermissions() is called Above Lallipop: ");
            try {
                checkAllPermissions();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //End Of Added Later//////////

        //Back Button
        currentLocationtopbarbackImageViewID.setOnClickListener(new ImageView.OnClickListener() {

            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        locationManager = (LocationManager) GetCurrentLocationActivity.this.getSystemService(Context.LOCATION_SERVICE);
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        System.out.println("isGPSEnabled: " + isGPSEnabled);
        System.out.println("isNetworkEnabled: " + isNetworkEnabled);
        if (!isGPSEnabled && !isNetworkEnabled) {
            showLocationSettings();
        } else {

            this.canGetLocation = true;
            if (isNetworkEnabled) {
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        latLongTextViewID.setText("");
                        latLongTextViewID.setText("Latitude: " + latitude + "\n\nLongitude: " + longitude);
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {

                    }

                    @Override
                    public void onProviderEnabled(String provider) {

                    }

                    @Override
                    public void onProviderDisabled(String provider) {

                    }
                });

                Log.v("Network Provider ", "Network Provider");
                if (locationManager != null) {
                    try{
                        if(Build.VERSION.SDK_INT >= 23 &&
                                ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                                ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(getApplicationContext(), "You have denied these permissions, so this app won't work as expected.",
                                    Toast.LENGTH_LONG).show();
                            return  ;
                        }
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    }
                    catch(Exception e){}
                    }
                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        latLongTextViewID.setText("");
                        latLongTextViewID.setText("Latitude: "+latitude+"\n\nLongitude: "+longitude);

                    }
                }//if (isNetworkEnabled)


            if (isGPSEnabled) {
                if (location == null) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            latLongTextViewID.setText("");
                            latLongTextViewID.setText("Latitude: "+latitude+"\n\nLongitude: "+longitude);
                        }

                        @Override
                        public void onStatusChanged(String provider, int status, Bundle extras) {

                        }

                        @Override
                        public void onProviderEnabled(String provider) {

                        }

                        @Override
                        public void onProviderDisabled(String provider) {

                        }
                    });

                    Log.v("GPS Enabled", "GPS Enabled");
                    if (locationManager != null) {
                        try{
                            if(Build.VERSION.SDK_INT >= 23 &&
                                    ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                                            ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                Toast.makeText(getApplicationContext(), "You have denied these permissions, so this app won't work as expected.",
                                        Toast.LENGTH_LONG).show();
                                return  ;
                            }

                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        }
                        catch(Exception e){}
                        }
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            latLongTextViewID.setText("");
                            latLongTextViewID.setText("Latitude: "+latitude+"\n\nLongitude: "+longitude);
                        }
                    }

            }//if (isGPSEnabled)

        }//else


    }//onCreate()

    private void showLocationSettings(){

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(GetCurrentLocationActivity.this);
        alertDialog.setTitle("GPS settings!");
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                GetCurrentLocationActivity.this.startActivity(intent);
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }

    private void checkAllPermissions(){
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(GetCurrentLocationActivity.this,android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(GetCurrentLocationActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(GetCurrentLocationActivity.this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(GetCurrentLocationActivity.this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 1991);

                // The callback method gets the result of the request.
            }

        }//if

    }//checkAllPermissions()

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1991: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // tasks you need to do.
                    System.out.println("ACCESS_FINE_LOCATION & ACCESS_COARSE_LOCATION ARE GRANTED!");

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    System.out.println("ACCESS_FINE_LOCATION & ACCESS_COARSE_LOCATION ARE REJECTED!");
                    Toast.makeText(getApplicationContext(),"You have denied Location Access permissions, So Location feature might not work properly!",Toast.LENGTH_LONG).show();
                }
                return;
            }

        }

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void onBackPressed()
    {
        GetCurrentLocationActivity.this.finish();
        overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

}//Main Class
