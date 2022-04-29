package com.experis.smartrac.as;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.Toast;

import com.experis.smartrac.as.R;


public class SplashActivity extends AppCompatActivity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 6000; //3000
    // flag for Internet connection status
    private Boolean isInternetPresent = false;

    private PowerManager.WakeLock mWakeLock;
    private SharedPreferences prefs;
    private SharedPreferences.Editor prefsEditor;
    private ProgressDialog progressDialog;
    private String str = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        /*PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "AttendanceTracking-DoNotDimScreen");
        mWakeLock.acquire();*/

        initAllViews();

        str = prefs.getString("LOGGEDIN","");
        System.out.println("LOGGEDIN in Splash: "+ str);

        // check for Internet status
        if (CommonUtils.isInternelAvailable(getApplicationContext())||!CommonUtils.isInternelAvailable(getApplicationContext())) {
            // Internet Connection is Present
            new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

                @Override
                public void run() {
                    // This method will be executed once when the timer is over
                    if(str.equalsIgnoreCase("yes")){
                        Intent intent = new Intent(SplashActivity.this,DashBoardActivity.class);
                        SplashActivity.this.finish();
                        startActivity(intent);
                        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
                        Toast.makeText(getApplicationContext(),"Already Loggedin",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                        SplashActivity.this.finish();
                        startActivity(i);
                        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
                    }

                }
            }, SPLASH_TIME_OUT);

        } else {
            // Internet connection is not present
            // Ask user to connect to Internet
            showAlertDialog(SplashActivity.this, "Internet Connection Error!",
                    "Please check your internet settings.\n", false);
        }
    }//onCreate()

    private void initAllViews(){
        //shared preference
        prefs = getSharedPreferences(CommonUtils.PREFERENCE_NAME,MODE_PRIVATE);
        prefsEditor = prefs.edit();
    }

    /**
     * Function to display simple Alert Dialog
     * @param context - application context
     * @param title - alert dialog title
     * @param message - alert message
     * @param status - success/failure (used to set icon)
     * */
    public void showAlertDialog(Context context, String title, String message, Boolean status) {
        final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        // Setting Dialog Title
        alertDialog.setTitle(title);
        // Setting Dialog Message
        alertDialog.setMessage(message);
        // Setting OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                SplashActivity.this.finish();
                overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
            }
        });
        // Showing Alert Message
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        SplashActivity.this.finish();
        overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
    }

}//SplashActivity
