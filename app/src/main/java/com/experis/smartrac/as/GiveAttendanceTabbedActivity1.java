package com.experis.smartrac.as;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.experis.smartrac.as.R;

import java.util.ArrayList;
import java.util.List;

public class GiveAttendanceTabbedActivity1 extends AppCompatActivity {

    private PowerManager.WakeLock mWakeLock;
    private SharedPreferences prefs;
    private SharedPreferences.Editor prefsEditor;
    private ProgressDialog progressDialog;

    //private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private ViewPagerAdapter adapter;

    private ImageView attendancetopbarbackImageViewID;
    private ImageView attendancetopbarusericonImageViewID;

    private NotificationManager notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_give_attendance_tabbed);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.attendance_topbar_title);

        initAllViews();

        //checkUpdatedGooglePlayVersion();

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        viewPager.setOffscreenPageLimit(0);

        attendancetopbarbackImageViewID.setOnClickListener(new ImageView.OnClickListener(){
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //User Icon Click Event
        attendancetopbarusericonImageViewID.setOnClickListener(new ImageView.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent i = new Intent(GiveAttendanceTabbedActivity1.this, ChangePasswordActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
            }
        });


        startGeolocationService(getApplicationContext());

        //Added Later//////////////////
        if(Build.VERSION.SDK_INT>Build.VERSION_CODES.LOLLIPOP){
            System.out.println("Inside GiveAttendanceTabbedActivity Page checkAllPermissions() is called Above Lallipop: ");
            try{
                checkAllPermissions();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        //End Of Added Later//////////

        if(CommonUtils.locationServicesEnabled(GiveAttendanceTabbedActivity1.this)){
        }
        else{
             //Toast.makeText(GiveAttendanceTabbedActivity.this, "Please TURN ON Your Mobile's 'GPS' (Location Settings)", Toast.LENGTH_LONG).show();
            final AlertDialog.Builder aldb =  new AlertDialog.Builder(GiveAttendanceTabbedActivity1.this);
            aldb.setTitle("Location Error!");
            aldb.setMessage("Please TURN ON Your Mobile's 'GPS' (Location Settings)");
            aldb.setCancelable(false);
            aldb.setPositiveButton("OK",new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    onBackPressed();
                }
            });
        }

    }//onCreate()

    private void checkAllPermissions(){
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(GiveAttendanceTabbedActivity1.this,
                android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(GiveAttendanceTabbedActivity1.this,
                    android.Manifest.permission.CAMERA)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(GiveAttendanceTabbedActivity1.this,
                        new String[]{android.Manifest.permission.CAMERA,
                                     android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                     android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1991);

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
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED
                        && grantResults[2] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // tasks you need to do.
                    System.out.println("CAMERA, WRITE_EXTERNAL_STORAGE & READ_EXTERNAL_STORAGE ARE GRANTED!");

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    System.out.println("CAMERA, WRITE_EXTERNAL_STORAGE & READ_EXTERNAL_STORAGE ARE REJECTED!");
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }

    }

    public boolean isServiceRunning(String serviceClassName){
        final ActivityManager activityManager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);

        for (ActivityManager.RunningServiceInfo runningServiceInfo : services) {
            if (runningServiceInfo.service.getClassName().equals(serviceClassName)){
                return true;
            }
        }
        return false;
    }

    static public void startGeolocationService(Context context) {
        Intent geolocationService = new Intent(context,
                GeolocationService.class);
        context.startService(geolocationService);
    }

    static public void stopGeolocationService(Context context) {
        Intent geolocationService = new Intent(context,
                GeolocationService.class);
        context.stopService(geolocationService);
    }

    private void initAllViews(){
        //shared preference
        prefs = getSharedPreferences(CommonUtils.PREFERENCE_NAME,MODE_PRIVATE);
        prefsEditor = prefs.edit();

        attendancetopbarbackImageViewID = (ImageView) findViewById(R.id.attendancetopbarbackImageViewID);
        attendancetopbarusericonImageViewID = (ImageView) findViewById(R.id.attendancetopbarusericonImageViewID);

        //Progress Dialog
        progressDialog = new ProgressDialog(GiveAttendanceTabbedActivity1.this);
    }

    private void setupViewPager(ViewPager viewPager) {

        //ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
       /* adapter.addFragment(new AttendanceIntimeFragmet(), "In TIme");
        adapter.addFragment(new AttendanceOuttimeFragment(), "Out TIme");*/
        //adapter.addFragment(new AttendanceWeeklyoffFragment(), "Weekly Off");
        adapter.addFragment(new AttendanceLeaveFragment1(), "Attendance Regularization");
       // adapter.addFragment(new AttendanceWeeklyoffFragment(), "Weekly Off");
      //  adapter.addFragment(new AttendanceMeetingFragment(), "OD");
       // adapter.addFragment(new AttendanceLWPFragment(), "LWP");
        /*adapter.addFragment(new AttendanceBreakFragment(), "Break");*/
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed()
    {
        stopGeolocationService(getApplicationContext());
        /*if(isServiceRunning("GeolocationService")){
            System.out.println("GeolocationService is running!");
            stopGeolocationService(getApplicationContext());
        }
        else{
            System.out.println("GeolocationService is already destroyed!");
        }*/

        GiveAttendanceTabbedActivity1.this.finish();
        overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
    }

    private void clearAllNotification(){
        notificationManager = (NotificationManager) GiveAttendanceTabbedActivity1.this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    @Override
    public void onResume(){
        super.onResume();

        //Starting Geolocation Service
        /*if(!isServiceRunning("GeolocationService")){
            System.out.println("GeolocationService is not running!");
            startGeolocationService(getApplicationContext());
        }
        else{
            System.out.println("GeolocationService is already running!");
        }*/
        if(ImageCapture.ATTENDANCE_TYPE.equalsIgnoreCase("in")){
            //newCameraInterface.onClickPictureBack();
            int position = tabLayout.getSelectedTabPosition();
            System.out.println("onResume(): Tab position: "+position);
            Fragment fragment = adapter.getItem(tabLayout.getSelectedTabPosition());
            if (fragment != null) {
                switch (position) {
                    case 0:
                        try {
                            ((AttendanceIntimeFragmet) fragment).onClickPictureBack2();
                        }
                        catch(NullPointerException e){
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                            alertDialogBuilder.setMessage(e.getMessage());
                            alertDialogBuilder.setPositiveButton("Cancel",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    });
                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();

                        } catch (Exception e){

                        }
                        break;
                    case 1:

                        break;

                }
            }
        }//if

        System.out.println("onResume() is called inside GiveAttendanceTabbedActivity: ");
        if(ImageCapture.ATTENDANCE_TYPE.equalsIgnoreCase("out")){
            //newCameraInterface.onClickPictureBack();
            int position = tabLayout.getSelectedTabPosition();
            System.out.println("onResume(): Tab position: "+position);
            Fragment fragment = adapter.getItem(tabLayout.getSelectedTabPosition());
            if (fragment != null) {
                switch (position) {
                    case 0:
                        break;
                    case 1:
                        ((AttendanceOuttimeFragment)fragment).onClickPictureBack1();
                        break;
                }
            }
        }//if
    }

    /*private void checkUpdatedGooglePlayVersion(){
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable( getApplicationContext() );
        System.out.println("checkUpdatedGooglePlayVersion() Status: "+status);
        if(status == ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED){
            AlertDialog.Builder aldb = new AlertDialog.Builder(GiveAttendanceTabbedActivity.this);
            aldb.setCancelable(false);
            aldb.setTitle("Error!");
            aldb.setMessage("Google Play services out of date. Please update it before using Attendance features.");
            aldb.setNegativeButton("Later", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                       onBackPressed();
                }
            });
            aldb.setPositiveButton("Update Now", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse("market://details?id=com.google.android.gms"));
                    stopGeolocationService(getApplicationContext());
                    clearAllNotification();
                    GiveAttendanceTabbedActivity.this.finish();
                    overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
                    startActivity(i);
                }
            });

            aldb.show();
        }
    }*/

    @Override
    protected void onDestroy() {
        if(isServiceRunning("GeolocationService")){
            System.out.println("GeolocationService is running!");
            stopGeolocationService(getApplicationContext());
        }
        else{
            System.out.println("GeolocationService is already destroyed!");
        }

        try{
            if((progressDialog != null) && progressDialog.isShowing() ){
                progressDialog.dismiss();
            }
        }catch (Exception e){e.printStackTrace();}
        finally {
            progressDialog = null;
        }

        super.onDestroy();
    }

}//Main Class
