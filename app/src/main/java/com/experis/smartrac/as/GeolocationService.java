package com.experis.smartrac.as;

/**
 Class Name: GeolocationService
 Created by Rana Krishna Paul
 */

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;

/**
 * This is a Service class which handles(finds) current Lat & Long of the Device
 */
public class GeolocationService extends Service implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener{

    public GeolocationService() {
    }

    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10*1000;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    protected GoogleApiClient mGoogleApiClient;
    protected LocationRequest mLocationRequest;

    private String TAG = "GeolocationService";

    @Override
    public void onCreate(){
        super.onCreate();
        System.out.println("GeolocationService: onCreate()");
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent,startId);
        System.out.println("GeolocationService Started");
        buildGoogleApiClient();
        mGoogleApiClient.connect();
    }

    /*@Override
    public int onStartCommand(Intent intent,int flags,int startId){
        super.onStartCommand(intent,flags,startId);

        return Service.START_STICKY;
    }*/

    @Override
    public void onDestroy() {
        Log.v(TAG, "selfStop() is executed: onDestroy()");
//        super.onDestroy();
        try {
            if (mGoogleApiClient.isConnected()) {
                stopLocationUpdates();
                mGoogleApiClient.disconnect();
            }
        } catch (Exception e) {}
        super.onDestroy();
    }

    protected void startLocationUpdates() {
        try{
            if(Build.VERSION.SDK_INT>Build.VERSION_CODES.LOLLIPOP &&
                    ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                System.out.println("You have denied ACCESS_FINE_LOCATION/ACCESS_COARSE_LOCATION permissions, so this app can't fetch the current location");
                return  ;
            }
            if (mGoogleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.requestLocationUpdates(
                        mGoogleApiClient, mLocationRequest, this);
            }
        }
        catch(Exception e ){
           e.printStackTrace();
        }
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.v(TAG, "Connected to GoogleApiClient");
        startLocationUpdates();
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.v(TAG,
                "New location : " + location.getLatitude() + ", "
                        + location.getLongitude() + ", "
                        + location.getAccuracy());
        Constants.UNIV_LAT = String.valueOf(location.getLatitude());
        Constants.UNIV_LONG = String.valueOf(location.getLongitude());
        System.out.println("Constants.UNIV_LAT: "+Constants.UNIV_LAT);
        System.out.println("Constants.UNIV_LONG: "+Constants.UNIV_LONG);
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.v(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.v(TAG,
                "Connection failed: ConnectionResult.getErrorCode() = "
                        + result.getErrorCode());
    }

    protected synchronized void buildGoogleApiClient() {
        Log.v(TAG, "Building GoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        createLocationRequest();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
