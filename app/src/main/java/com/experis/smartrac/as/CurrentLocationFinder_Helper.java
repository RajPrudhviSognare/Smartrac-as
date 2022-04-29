package com.experis.smartrac.as;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;

public class CurrentLocationFinder_Helper {

    private boolean isGPSEnabled = false;
    private boolean isNetworkEnabled = false;
    private boolean canGetLocation = false;
    private Location location; // location
    private double latitude = 0.0d; // latitude
    private double longitude = 0.0d; // longitude

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1; // 1 meter
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1/2; // 1/2 minute
    private LocationManager locationManager;

    private Context context;


    public CurrentLocationFinder_Helper(Context context) {
       this.context = context;
    }

    public void getCurrentLatAndLong(){

        locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        System.out.println("isGPSEnabled: "+isGPSEnabled);
        System.out.println("isNetworkEnabled: "+isNetworkEnabled);
        if (!isGPSEnabled && !isNetworkEnabled) {
        }
        else{

            this.canGetLocation = true;
            if (isNetworkEnabled) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();

                        Constants.UNIV_LAT1 = String.valueOf(latitude);
                        Constants.UNIV_LONG1 = String.valueOf(longitude);

                        System.out.println("Constants.UNIV_LAT1: "+Constants.UNIV_LAT1);
                        System.out.println("Constants.UNIV_LONG1: "+Constants.UNIV_LONG1);
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
                                ContextCompat.checkSelfPermission( context, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                                ContextCompat.checkSelfPermission( context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                            return  ;
                        }
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    }
                    catch(Exception e){}
                }
                if (location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();

                    Constants.UNIV_LAT1 = String.valueOf(latitude);
                    Constants.UNIV_LONG1 = String.valueOf(longitude);

                    System.out.println("Constants.UNIV_LAT1: "+Constants.UNIV_LAT1);
                    System.out.println("Constants.UNIV_LONG1: "+Constants.UNIV_LONG1);
                }
            }//if (isNetworkEnabled)


            if (isGPSEnabled) {
                if (location == null) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();

                            Constants.UNIV_LAT1 = String.valueOf(latitude);
                            Constants.UNIV_LONG1 = String.valueOf(longitude);

                            System.out.println("Constants.UNIV_LAT1: "+Constants.UNIV_LAT1);
                            System.out.println("Constants.UNIV_LONG1: "+Constants.UNIV_LONG1);
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
                                    ContextCompat.checkSelfPermission( context, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                                    ContextCompat.checkSelfPermission( context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                                return  ;
                            }

                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        }
                        catch(Exception e){}
                    }
                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();

                        Constants.UNIV_LAT1 = String.valueOf(latitude);
                        Constants.UNIV_LONG1 = String.valueOf(longitude);

                        System.out.println("Constants.UNIV_LAT1: "+Constants.UNIV_LAT1);
                        System.out.println("Constants.UNIV_LONG1: "+Constants.UNIV_LONG1);
                    }
                }

            }//if (isGPSEnabled)

        }//else

    }//getCurrentLatAndLong()


}//CurrentLocationFinder_Helper
