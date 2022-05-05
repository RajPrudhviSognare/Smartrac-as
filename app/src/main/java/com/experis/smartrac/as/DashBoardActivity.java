package com.experis.smartrac.as;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.experis.smartrac.as.ReimbursementModule.ReimbursementDashBoardActivity;
import com.experis.smartrac.as.R;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

public class DashBoardActivity extends AppCompatActivity {

    private PowerManager.WakeLock mWakeLock;
    private SharedPreferences prefs;
    private SharedPreferences.Editor prefsEditor;
    private ProgressDialog progressDialog;

    private ImageView dashboardTopbarCompanyLogoImageViewID;
    private TextView dashboardTopCompanyAddressTextViewID;
    private TextView dashboardTopUserNameTextViewID;

    private GridView dashBoardForAssociatesGridViewID;
    private GridView dashBoardForTLGridViewID;
    private GridView dashBoardForSMGridViewID, dashBoardForSOOGridViewID;

    private ImageView dashboardtopbarusericonImageViewID;
    private ImageView dashboardtopbarLocationImageViewID;
    private ImageView dashboardtopbarLogouticonImageViewID;

    private Button dashboardImageViewAttendanceNewID;
    private Button dashboardImageViewAttendanceNewID2;

    private String baseURL;
    private String SOAPRequestXML;
    private HttpResponse httpResponse = null;

    private String TAG_EmpName = "EmpName";
    private String EmpName = "", ClientName = "", Mobile1 = "", Mobile2 = "", Designation = "", UserTypeID = "";

    private String TAG_ATTENDANCE_TYPE = "in"; //"In Time"
    private RestFullClient client;
    private String TAG_STATUS = "status";
    private String TAG_ERROR = "error";
    private String TAG_MESSAGE = "message";
    private String TAG_LEAVESTATUS = "leave_status";

    private String STATUS = "";
    private String ERROR = "";
    private String MESSAGE = "";
    private String LEAVESTATUS = "false";
    private String remarks = "";
    private String encodedImageIntoString = "";

    private ImageLoader imageLoader = null;

    private String ROLE_TYPE = "E"; //Associates

    private int ROLE_ID = 1; //1=>Associate, 2=>Teamlead, 3=>Admin, 4=>Manager,
    private String COMPANY_ADDRESS = "";
    private String COMPANY_OUTLETNAME = "";
    private String COMPANY_LOGO_PATH = "";
    private String COMPANY_LOGO_NAME = "";

    private LinearLayout dashboardResponseLayoutID;
    private TextView textResponseID;
    private Button btnResponseID;

    private int dashIconsForAssociates[] = {
            R.drawable.attendanceicon,
            R.drawable.associatereporticon,
            R.drawable.hrmsicon,
            R.drawable.reimbursement,
    };
    private int dashIconsForTL[] = {
            R.drawable.attendanceapprovalicon,
            R.drawable.associatereporticon,
            R.drawable.exit_form,
    };


    private CurrentLocationFinder_Helper currentLocationFinder_helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);
        try {
            getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            getSupportActionBar().setCustomView(R.layout.dashboard_topbar_title);
        } catch (Exception e) {
            e.printStackTrace();
        }


        getCurrentVersion();


    }//onCreate()


    String currentVersion, latestVersion;
    Dialog dialog;

    private void getCurrentVersion() {
        PackageManager pm = this.getPackageManager();
        PackageInfo pInfo = null;

        try {
            pInfo = pm.getPackageInfo(this.getPackageName(), 0);

        } catch (PackageManager.NameNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        currentVersion = pInfo.versionName;

        new GetLatestVersion().execute();

    }

    private class GetLatestVersion extends AsyncTask<String, String, JSONObject> {

        //  private ProgressDialog progressDialog;
        private String urlOfAppFromPlayStore = "https://play.google.com/store/apps/details?id=com.experis.smartrac.nunhems" + "&hl=en";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(String... params) {

            try {
                latestVersion = Jsoup.connect("https://play.google.com/store/apps/details?id=" + DashBoardActivity.this.getPackageName() + "&hl=en")
                        .timeout(30000)
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("http://www.google.com")
                        .get()
                        //.getElementsByAttributeValue("itemprop","softwareVersion").first().text();
                        .select("div.hAyfc:nth-child(4) > span:nth-child(2) > div:nth-child(1) > span:nth-child(1)")
                        .first()
                        //.text();
                        .ownText();


                Log.e("latestversion", "---" + latestVersion);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return new JSONObject();
        }

       /* @Override
        protected JSONObject doInBackground(String... params) {
            try {
//It retrieves the latest version by scraping the content of current version from play store at runtime
                Document doc = Jsoup.connect(urlOfAppFromPlayStore)
                        .timeout(30000).get();
                latestVersion = doc.getElementsByAttributeValue
                        ("itemprop","softwareVersion").first().text();

            }catch (Exception e){
                e.printStackTrace();

            }

            return new JSONObject();
        }*/

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if (latestVersion != null) {
                if (!currentVersion.equalsIgnoreCase(latestVersion)) {
                    if (!isFinishing()) { //This would help to prevent Error : BinderProxy@45d459c0 is not valid; is your activity running? error
                        if ((progressDialog != null) && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        showUpdateDialog();
                    }
                } else {

                    startbackground();
                }
            } else {

                startbackground();
            }
            super.onPostExecute(jsonObject);
        }
    }


    private void showUpdateDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Newer version is available in store\nPlease Update");
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
               /* startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse
                        ("market://details?id="+ DashBoardActivity.this.getPackageName())));*/
                dialog.dismiss();
            }
        });

      /*  builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                background.start();
            }
        });
*/
        builder.setCancelable(false);
        dialog = builder.show();
    }

    private void startbackground() {
        //Initialize all views ID & variables
        initAllViews();

        startGeolocationService(DashBoardActivity.this);
        getEmployeeDetails();

        /***Set Username & Outlet Details***/
        //getAnsSetValuesFromPreference();

        /***Creating an Image Directory for storing Selfie Pictures Temporarily***/
        createImageDirectory();

        try {
            currentLocationFinder_helper = new CurrentLocationFinder_Helper(DashBoardActivity.this);
            currentLocationFinder_helper.getCurrentLatAndLong();
        } catch (Exception e) {
            e.printStackTrace();
        }

        dashboardImageViewAttendanceNewID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Attendance
                if (LEAVESTATUS.equalsIgnoreCase("true")) {
                    showOptionDisableDialog();
                }
                if (LEAVESTATUS.equalsIgnoreCase("false")) {
                        /*Intent i = new Intent(DashBoardActivity.this, GiveAttendanceTabbedActivity.class);
                        startActivity(i);
                        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);*/

                    if (CommonUtils.locationServicesEnabled(DashBoardActivity.this)) {
                        MESSAGE = "";
//                        giveAttendance();
                        startActivity(new Intent(DashBoardActivity.this, GiveAttendanceTabbedActivity.class));
                    } else {
                        //Toast.makeText(GiveAttendanceTabbedActivity.this, "Please TURN ON Your Mobile's 'GPS' (Location Settings)", Toast.LENGTH_LONG).show();
                        final AlertDialog.Builder aldb = new AlertDialog.Builder(DashBoardActivity.this);
                        aldb.setTitle("Location Error!");
                        aldb.setMessage("Please TURN ON Your Mobile's 'GPS' (Location Settings)");
                        aldb.setCancelable(false);
                        aldb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        });
                    }//else

                }//if
            }
        });

        dashboardImageViewAttendanceNewID2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(DashBoardActivity.this, ViewAttendanceActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
            }
        });


        Constants.CURRENT_LAT = Constants.UNIV_LAT;
        Constants.CURRENT_LONG = Constants.UNIV_LONG;
        /***Dash Board for Associates***/
        //if (ROLE_TYPE.equalsIgnoreCase("Associates")) {
       /* if (ROLE_TYPE.equalsIgnoreCase("E")) {
            dashBoardForAssociatesGridViewID.setVisibility(View.VISIBLE);
            dashBoardForTLGridViewID.setVisibility(View.GONE);
            dashBoardForSMGridViewID.setVisibility(View.GONE);
            dashBoardForSOOGridViewID.setVisibility(View.GONE);
            dashboardtopbarLocationImageViewID.setVisibility(View.VISIBLE);
            dashBoardForAssociatesGridViewID.setAdapter(new CustomAdapterForAssociates(DashBoardActivity.this));
        }
        else {
            dashBoardForAssociatesGridViewID.setVisibility(View.GONE);
            dashBoardForTLGridViewID.setVisibility(View.VISIBLE);
            dashBoardForSMGridViewID.setVisibility(View.GONE);
            dashBoardForSOOGridViewID.setVisibility(View.GONE);
            dashboardtopbarLocationImageViewID.setVisibility(View.VISIBLE);
            dashBoardForTLGridViewID.setAdapter(new CustomAdapterForTL(DashBoardActivity.this));
        }*/

        /***Dash Board for StoreManager***/
        //if (ROLE_TYPE.equalsIgnoreCase("StoreManager")) {
        /*if (ROLE_ID==2) {
            dashBoardForAssociatesGridViewID.setVisibility(View.GONE);
            dashBoardForTLGridViewID.setVisibility(View.GONE);
            dashBoardForSMGridViewID.setVisibility(View.VISIBLE);
            dashBoardForSOOGridViewID.setVisibility(View.GONE);
            dashboardtopbarLocationImageViewID.setVisibility(View.VISIBLE);
            dashBoardForSMGridViewID.setAdapter(new CustomAdapterForSM(DashBoardActivity.this));
        }
        if (ROLE_ID==4) {
            dashBoardForAssociatesGridViewID.setVisibility(View.GONE);
            dashBoardForTLGridViewID.setVisibility(View.GONE);
            dashBoardForSMGridViewID.setVisibility(View.GONE);
            dashBoardForSOOGridViewID.setVisibility(View.VISIBLE);
            dashboardtopbarLocationImageViewID.setVisibility(View.VISIBLE);
            dashBoardForSOOGridViewID.setAdapter(new CustomAdapterForSOO(DashBoardActivity.this));
        }*/


        /***GridView Options Click Listener For Associates***/
        dashBoardForAssociatesGridViewID.setOnItemClickListener(new GridView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {

                //Attendance
                if (pos == 0) {
                    if (LEAVESTATUS.equalsIgnoreCase("true")) {
                        showOptionDisableDialog();
                    }
                    if (LEAVESTATUS.equalsIgnoreCase("false")) {
                        Intent i = new Intent(DashBoardActivity.this, GiveAttendanceTabbedActivity.class);
                        startActivity(i);
                        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
                    }
                }
                //Associates Reports
                if (pos == 1) {
                    Intent i = new Intent(DashBoardActivity.this, ViewAttendanceActivity.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
                   /* Intent i = new Intent(DashBoardActivity.this,AssociateReportsTabbedActivity.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);*/
                }
                //HRMS
                if (pos == 2) {
                    Intent i = new Intent(DashBoardActivity.this, HRMSTabbedActivity.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
                }
                if (pos == 3) {
                    Intent i = new Intent(DashBoardActivity.this, ReimbursementDashBoardActivity.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
                }
            }

        });
        /***End Of GridView Options Click Listener For Associates***/


        /***GridView Options Click Listener For tl***/
        dashBoardForTLGridViewID.setOnItemClickListener(new GridView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {

                if (pos == 0) {
                    Intent i = new Intent(DashBoardActivity.this, AttendanceApprovalRevisedActivity1.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
                   /* Intent i = new Intent(DashBoardActivity.this,AssociateReportsTabbedActivity.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);*/
                }
                if (pos == 1) {
                    Intent i = new Intent(DashBoardActivity.this, ViewAttendanceActivity.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
                }
                if (pos == 2) {
                    Intent i = new Intent(DashBoardActivity.this, ExitActivity1.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
                }
            }
        });
        /***End Of GridView Options Click Listener For tl***/

        /***GridView Options Click Listener For manager***/
        dashBoardForSMGridViewID.setOnItemClickListener(new GridView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {

                if (pos == 0) {
                    Intent i = new Intent(DashBoardActivity.this, DashboardAttendanceCountActivity.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
                   /* Intent i = new Intent(DashBoardActivity.this,AssociateReportsTabbedActivity.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);*/
                }
                //Attendance
                if (pos == 1) {
                    if (LEAVESTATUS.equalsIgnoreCase("true")) {
                        showOptionDisableDialog();
                    }
                    if (LEAVESTATUS.equalsIgnoreCase("false")) {
                        Intent i = new Intent(DashBoardActivity.this, GiveAttendanceTabbedActivity.class);
                        startActivity(i);
                        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
                    }
                }
                //Associates Reports
                if (pos == 2) {
                    Intent i = new Intent(DashBoardActivity.this, ViewAttendanceActivity.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
                   /* Intent i = new Intent(DashBoardActivity.this,AssociateReportsTabbedActivity.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);*/
                }

                if (pos == 3) {
                    Intent i = new Intent(DashBoardActivity.this, AttendanceApprovalRevisedActivity1.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
                   /* Intent i = new Intent(DashBoardActivity.this,AssociateReportsTabbedActivity.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);*/
                }
                //HRMS
                if (pos == 4) {
                    Intent i = new Intent(DashBoardActivity.this, HRMSTabbedActivity.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
                }
                if (pos == 5) {
                    Intent i = new Intent(DashBoardActivity.this, AttendanceApprovalRevisedActivity2.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
                   /* Intent i = new Intent(DashBoardActivity.this,AssociateReportsTabbedActivity.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);*/
                }

            }

        });
        /***End Of GridView Options Click Listener For manager***/

        /***GridView Options Click Listener For OFC***/
        dashBoardForSOOGridViewID.setOnItemClickListener(new GridView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {

                if (pos == 0) {
                    Intent i = new Intent(DashBoardActivity.this, DashboardAttendanceCountActivity.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
                   /* Intent i = new Intent(DashBoardActivity.this,AssociateReportsTabbedActivity.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);*/
                }


                if (pos == 1) {
                    Intent i = new Intent(DashBoardActivity.this, AttendanceApprovalRevisedActivity1.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
                   /* Intent i = new Intent(DashBoardActivity.this,AssociateReportsTabbedActivity.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);*/
                }
                if (pos == 2) {
                    Intent i = new Intent(DashBoardActivity.this, AttendanceApprovalRevisedActivity2.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
                   /* Intent i = new Intent(DashBoardActivity.this,AssociateReportsTabbedActivity.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);*/
                }


            }

        });
        /***End Of GridView Options Click Listener For OFC***/


        //User Icon Click Event
        dashboardtopbarusericonImageViewID.setOnClickListener(new ImageView.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(DashBoardActivity.this, ChangePasswordActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
            }
        });

        //Topbar Map/Location Icon
        dashboardtopbarLocationImageViewID.setOnClickListener(new ImageView.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(DashBoardActivity.this, GetCurrentLocationActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
            }
        });

        //Logout Icon Click Logic
        dashboardtopbarLogouticonImageViewID.setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View v) {
                prefsEditor.putString("LOGGEDIN", "no");
                prefsEditor.commit();

                stopGeolocationService(getApplicationContext());
                deleteAllSelfies();

                Toast.makeText(DashBoardActivity.this, "You have been Logged out successfully!", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(DashBoardActivity.this, LoginActivity.class);
                DashBoardActivity.this.finish();
                startActivity(i);
                overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
            }
        });

        //Added Later//////////////////
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            System.out.println("Inside DashBoardActivity Page checkAllPermissions() is called Above Lallipop: ");
            try {
                checkAllPermissions();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //End Of Added Later//////////

        btnResponseID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dashboardResponseLayoutID.setVisibility(View.GONE);
                btnResponseID.setVisibility(View.GONE);
                dashboardImageViewAttendanceNewID.setVisibility(View.VISIBLE);
                dashboardImageViewAttendanceNewID2.setVisibility(View.VISIBLE);
                textResponseID.setText("");
            }
        });


    }


    private void initAllViews() {
        //shared preference
        prefs = getSharedPreferences(CommonUtils.PREFERENCE_NAME, MODE_PRIVATE);
        prefsEditor = prefs.edit();

        dashboardTopbarCompanyLogoImageViewID = (ImageView) findViewById(R.id.dashboardTopbarCompanyLogoImageViewID);
        dashboardTopCompanyAddressTextViewID = (TextView) findViewById(R.id.dashboardTopCompanyAddressTextViewID);
        dashboardTopUserNameTextViewID = (TextView) findViewById(R.id.dashboardTopUserNameTextViewID);

        dashBoardForAssociatesGridViewID = (GridView) findViewById(R.id.dashBoardForAssociatesGridViewID);
        dashBoardForTLGridViewID = (GridView) findViewById(R.id.dashBoardForTLGridViewID);
        dashBoardForSMGridViewID = (GridView) findViewById(R.id.dashBoardForSMGridViewID);
        dashBoardForSOOGridViewID = (GridView) findViewById(R.id.dashBoardForSOOGridViewID);

        dashboardtopbarusericonImageViewID = (ImageView) findViewById(R.id.dashboardtopbarusericonImageViewID);
        dashboardtopbarLocationImageViewID = (ImageView) findViewById(R.id.dashboardtopbarLocationImageViewID);

        dashboardtopbarLogouticonImageViewID = (ImageView) findViewById(R.id.dashboardtopbarLogouticonImageViewID);

        dashboardImageViewAttendanceNewID = (Button) findViewById(R.id.dashboardImageViewAttendanceNewID);
        dashboardImageViewAttendanceNewID2 = (Button) findViewById(R.id.dashboardImageViewAttendanceNewID2);

        dashboardResponseLayoutID = (LinearLayout) findViewById(R.id.dashboardResponseLayoutID);
        textResponseID = (TextView) findViewById(R.id.textResponseID);
        btnResponseID = (Button) findViewById(R.id.btnResponseID);

        //Progress Dialog
        progressDialog = new ProgressDialog(DashBoardActivity.this);

        imageLoader = new ImageLoader(DashBoardActivity.this);
        // setEmployeeDetails();
    }

    public boolean isServiceRunning(String serviceClassName) {
        final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);

        for (ActivityManager.RunningServiceInfo runningServiceInfo : services) {
            if (runningServiceInfo.service.getClassName().equals(serviceClassName)) {
                return true;
            }
        }
        return false;
    }

    public void startGeolocationService(Context context) {
        Intent geolocationService = new Intent(context,
                GeolocationService.class);
        context.startService(geolocationService);
    }

    public void stopGeolocationService(Context context) {
        Intent geolocationService = new Intent(context,
                GeolocationService.class);
        context.stopService(geolocationService);
    }

    private void giveAttendance() {
        Constants.ASSOCIATE_ID = prefs.getString("USERID", "");
        Constants.TL_ID = prefs.getString("TLID", "");
        Constants.OUTLET_ID = prefs.getString("USEROUTLETID", "");
        Constants.ATTENDANCE_TYPE = TAG_ATTENDANCE_TYPE;
        Constants.ATTENDANCE_IMAGE = "";
        //Constants.ATTENDANCE_DATE = "";
        //Constants.ATTENDANCE_DATE = "yyyy-mm-dd";
        Constants.CURRENT_LAT = Constants.UNIV_LAT;
        Constants.CURRENT_LONG = Constants.UNIV_LONG;

        if (Constants.UNIV_LAT.equals("0.0")) {
            Constants.CURRENT_LAT = Constants.UNIV_LAT1;
        }
        if (Constants.UNIV_LONG.equals("0.0")) {
            Constants.CURRENT_LONG = Constants.UNIV_LONG1;
        }
        Constants.ATTENDANCE_DATE = "";
        Constants.REASON = "";
        Constants.LEAVE_TYPE = "";

        //progressDialog.setTitle("Attendance");
        progressDialog.setMessage("Submitting Your Break... Please wait!");
        progressDialog.setCancelable(false);
        progressDialog.show();
        client = new RestFullClient(Constants.BASE_URL + Constants.ATTENDANCE_RELATIVE_URI);
        client.AddParam("associate_id", Constants.ASSOCIATE_ID);
        client.AddParam("tl_id", Constants.TL_ID);
        client.AddParam("outlet_id", Constants.OUTLET_ID);
        client.AddParam("attendance_type", Constants.ATTENDANCE_TYPE);
        client.AddParam("attendance_image", Constants.ATTENDANCE_IMAGE);
        client.AddParam("attendance_time", "");
        client.AddParam("attendance_date_sub", "");
        client.AddParam("latitude", Constants.CURRENT_LAT);
        client.AddParam("longitude", Constants.CURRENT_LONG);
//        client.AddParam("distance", Constants.DISTANCE);
//        System.out.println("distance: "+Constants.DISTANCE);
        client.AddParam("attendance_date", Constants.ATTENDANCE_DATE);
        client.AddParam("reason", Constants.REASON);
        client.AddParam("remarks", Constants.REMARKS);
        client.AddParam("leave_type", Constants.LEAVE_TYPE);

        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {
                    client.Execute(1); //POST Request
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                receiveDataForServerResponse(client.jObj);
                handler.sendEmptyMessage(0);

            }

        }).start();
    }

    /*private void giveAttendance(){
        Constants.ASSOCIATE_ID = prefs.getString("USERISDCODE","");
        Constants.ATTENDANCE_TYPE = TAG_ATTENDANCE_TYPE;
        Constants.ATTENDANCE_DATE = "0000-00-00";

        Constants.CURRENT_LAT = Constants.UNIV_LAT;
        Constants.CURRENT_LONG = Constants.UNIV_LONG;

        if(Constants.UNIV_LAT.equals("0.0")){
            Constants.CURRENT_LAT = Constants.UNIV_LAT1;
        }
        if(Constants.UNIV_LONG.equals("0.0")){
            Constants.CURRENT_LONG = Constants.UNIV_LONG1;
        }

        Constants.REASON = "";
        Constants.REMARKS = remarks;
        Constants.LEAVE_TYPE = "";
        Constants.ATTENDANCE_IMAGE = encodedImageIntoString;

        progressDialog.setMessage("Submitting... Please wait!");
        progressDialog.setCancelable(false);
        progressDialog.show();
        client = new RestFullClient(Constants.BASE_URL+Constants.ATTENDANCE_RELATIVE_URI);

        client.AddParam("associate_code", Constants.ASSOCIATE_ID);
        System.out.println("associate_code: "+Constants.ASSOCIATE_ID);

        client.AddParam("attendance_type", Constants.ATTENDANCE_TYPE);
        System.out.println("attendance_type: "+Constants.ATTENDANCE_TYPE);

        client.AddParam("attendance_date", Constants.ATTENDANCE_DATE);
        System.out.println("attendance_date: "+Constants.ATTENDANCE_DATE);

        client.AddParam("latitude", Constants.CURRENT_LAT);
        System.out.println("latitude: "+Constants.CURRENT_LAT);

        client.AddParam("longitude", Constants.CURRENT_LONG);
        System.out.println("longitude: "+Constants.CURRENT_LONG);

        client.AddParam("reason", Constants.REASON);
        System.out.println("reason: "+Constants.REASON);

        client.AddParam("remarks", Constants.REMARKS);
        System.out.println("remarks: "+Constants.REMARKS);

        client.AddParam("leave_type", Constants.LEAVE_TYPE);
        System.out.println("leave_type: "+Constants.LEAVE_TYPE);

        client.AddParam("attendance_image", Constants.ATTENDANCE_IMAGE);
        System.out.println("attendance_image: "+Constants.ATTENDANCE_IMAGE);

        new Thread(new Runnable(){
            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {
                    client.Execute(1); //POST Request
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                receiveDataForServerResponse1(client.jObj);
                handler2.sendEmptyMessage(0);
            }

        }).start();
    }*/
    private void receiveDataForServerResponse1(JSONObject jobj) {

        try {

            if (client.responseCode == 200) {

                STATUS = jobj.getString(TAG_STATUS);
                MESSAGE = jobj.getString(TAG_MESSAGE);

                System.out.println("STATUS: responseCode==200: " + STATUS);
                System.out.println("MESSAGE: responseCode==200: " + MESSAGE);

            }//if(client.responseCode==200)
            if (client.responseCode != 200) {

                STATUS = jobj.getString(TAG_STATUS);
                MESSAGE = jobj.getString(TAG_MESSAGE);

                System.out.println("STATUS: responseCode!=200: " + STATUS);
                System.out.println("MESSAGE: responseCode!=200: " + MESSAGE);

            }//if(client.responseCode!=200)

        } catch (Exception e) {
        }
    }

    Handler handler2 = new Handler() {

        public void handleMessage(Message msg) {
            try {
                if ((progressDialog != null) && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            } catch (final Exception e) {
                e.printStackTrace();
            }

            //Success
            if (client.responseCode == 200) {

                //Success
                if (STATUS.equalsIgnoreCase("true")) {
                    Constants.ASSOCIATE_ID = "";
                    Constants.ATTENDANCE_TYPE = "";
                    Constants.ATTENDANCE_IMAGE = "";
                    //Constants.CURRENT_LAT = "0.0";
                    //Constants.CURRENT_LONG = "0.0";
                    Constants.ATTENDANCE_DATE = "0000-00-00";
                    Constants.REASON = "";
                    Constants.LEAVE_TYPE = "";
                    Constants.REMARKS = "";

                    dashboardResponseLayoutID.setVisibility(View.VISIBLE);
                    btnResponseID.setVisibility(View.VISIBLE);
                    dashboardImageViewAttendanceNewID.setVisibility(View.GONE);
                    dashboardImageViewAttendanceNewID2.setVisibility(View.GONE);
                    textResponseID.setText("");
                    textResponseID.setText(MESSAGE);
                    //textResponseID.setText("CURRENT_LAT: "+Constants.CURRENT_LAT+"\nCURRENT_LONG: "+Constants.CURRENT_LONG+"\n"+MESSAGE);
                }

                //Failed
                if (STATUS.equalsIgnoreCase("false")) {
                    //showRetryDialog();
                    showFailureDialog();
                }
            }
            //Failed
            if (client.responseCode != 200) {
                //showRetryDialog();
                showFailureDialog();
            }

        }//handleMessage(Message msg)

    };

    //Show failure Dialog
    private void showFailureDialog() {
        dashboardResponseLayoutID.setVisibility(View.VISIBLE);
        btnResponseID.setVisibility(View.VISIBLE);
        dashboardImageViewAttendanceNewID.setVisibility(View.GONE);
        dashboardImageViewAttendanceNewID2.setVisibility(View.GONE);
        textResponseID.setText("");
        textResponseID.setText(MESSAGE);
        ////textResponseID.setText("CURRENT_LAT: "+Constants.CURRENT_LAT+"\nCURRENT_LONG: "+Constants.CURRENT_LONG+"\n"+MESSAGE);
    }

    private void getEmployeeDetails() {
        progressDialog.setMessage("Requesting... Please wait!");
        progressDialog.setCancelable(false);
        progressDialog.show();
        prefs = getSharedPreferences(CommonUtils.PREFERENCE_NAME, MODE_PRIVATE);
        prefsEditor = prefs.edit();
        String EmpID = prefs.getString("USERISDCODE", "");
        System.out.println("EmpID: " + EmpID);

        baseURL = Constants.base_url_default;
        SOAPRequestXML = Constants.soapRequestHeader +
                "<soapenv:Header/>"
                + "<soapenv:Body>"
                + "<tem:EmployeeInformation>"
                //+"<tem:emp_code>"+100172254+"</tem:emp_code>"
                + "<tem:emp_code>" + EmpID + "</tem:emp_code>"
                + "</tem:EmployeeInformation>"
                + "</soapenv:Body>"
                + "</soapenv:Envelope>";

        //String msgLength = String.format("%1$d", SOAPRequestXML.length());
        System.out.println("Request== " + SOAPRequestXML);

        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub

                try {

                    HttpPost httppost = new HttpPost(baseURL);
                    StringEntity se = new StringEntity(SOAPRequestXML, HTTP.UTF_8);
                    se.setContentType("text/xml");
                    httppost.setHeader("Content-Type", "text/xml;charset=UTF-8");
                    httppost.setEntity(se);
                    HttpClient httpclient = new DefaultHttpClient();
                    httpResponse = null;
                    httpResponse = (HttpResponse) httpclient.execute(httppost);
                    String Response = new BasicResponseHandler().handleResponse(httpResponse);

                    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                    factory.setNamespaceAware(true);
                    XmlPullParser xpp = factory.newPullParser();
                    xpp.setInput(new StringReader(Response));
                    //int eventType = xpp.getEventType();

                    System.out.println("Server Response = " + Response);
                    StatusLine status = httpResponse.getStatusLine();
                    System.out.println("Server status code = " + status.getStatusCode());
                    System.out.println("Server httpResponse.getStatusLine() = " + httpResponse.getStatusLine().toString());
                    System.out.println("Server Staus = " + httpResponse.getEntity().toString());

                    getParsingElementsForEmployeeDetails(xpp);

                } catch (HttpResponseException e) {
                    Log.i("httpResponse Error = ", e.getMessage());
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                handler1.sendEmptyMessage(0);

            }

        }).start();

    }

    //getParsingElementsForEmployeeDetails(xpp);
    public void getParsingElementsForEmployeeDetails(XmlPullParser xpp) {
        String text = "";
        try {
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagname = xpp.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        break;

                    case XmlPullParser.TEXT:
                        text = xpp.getText().trim().toString();
                        System.out.println("Text data: " + text);
                        break;

                    case XmlPullParser.END_TAG:
                        if (tagname.equalsIgnoreCase(TAG_EmpName)) {
                            EmpName = text;
                            text = "";
                            System.out.println("EmpName: " + EmpName);
                        } else if (tagname.equalsIgnoreCase("ClientName")) {
                            ClientName = text;
                            text = "";
                            System.out.println("EmpName: " + ClientName);
                        } else if (tagname.equalsIgnoreCase("UserTypeID")) {
                            UserTypeID = text;
                            text = "";
                            System.out.println("UserTypeID: " + UserTypeID);
                        } else if (tagname.equalsIgnoreCase("Mobile1")) {
                            Mobile1 = text;
                            text = "";
                            System.out.println("Mobile1: " + Mobile1);
                        } else if (tagname.equalsIgnoreCase("Mobile2")) {
                            Mobile2 = text;
                            text = "";
                            System.out.println("Mobile2: " + Mobile2);
                        } else if (tagname.equalsIgnoreCase("Designation")) {
                            Designation = text;
                            text = "";
                            System.out.println("Mobile2: " + Designation);
                        }
                        break;

                    default:
                        break;

                }//switch

                eventType = xpp.next();

            }//while()

        }//try
        catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }//getParsingElementsForLogin(xpp);

    Handler handler1 = new Handler() {

        public void handleMessage(Message msg) {
            try {
                if ((progressDialog != null) && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            } catch (final Exception e) {
                e.printStackTrace();
            }

            setEmployeeDetails();
        }//handleMessage(Message msg)
    };

    private void setEmployeeDetails() {

        prefsEditor.putString("USERNAME", EmpName);
        prefsEditor.putString("USER_DESIGNATION", Designation);
        prefsEditor.putString("USEROUTLETNAME", ClientName);
        prefsEditor.putString("UserTypeID", UserTypeID);
        prefsEditor.putString("Mobile1", Mobile1);
        prefsEditor.putString("Mobile2", Mobile2);
        prefsEditor.commit();

        getAnsSetValuesFromPreference();
    }

    private void getAnsSetValuesFromPreference() {
      /*  ROLE_TYPE = prefs.getString("USERROLEID", "");
        if (ROLE_TYPE != null || !ROLE_TYPE.equals("")) {
            try {
                ROLE_ID = Integer.parseInt(ROLE_TYPE);
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("ROLE_ID IN DASHBOARD: " + ROLE_ID);
        }
*/

        prefs = getSharedPreferences(CommonUtils.PREFERENCE_NAME, MODE_PRIVATE);
        prefsEditor = prefs.edit();


        ROLE_TYPE = prefs.getString("UserTypeID", "");
        COMPANY_OUTLETNAME = prefs.getString("USEROUTLETNAME", "");

        System.out.println("USER NAME IN DASHBOARD: " + prefs.getString("USERNAME", "Guest") + " (" + prefs.getString("USERISDCODE", "123456") + ")");
        dashboardTopUserNameTextViewID.setText("");
        dashboardTopUserNameTextViewID.setText("Hello, " + prefs.getString("USERNAME", "") + " (" + prefs.getString("USERISDCODE", "") + ")");

        if (COMPANY_OUTLETNAME.equalsIgnoreCase("null")) {
            COMPANY_OUTLETNAME = "";
        }

        if (!COMPANY_OUTLETNAME.equalsIgnoreCase("")) {
            dashboardTopCompanyAddressTextViewID.setText("");
            //dashboardTopCompanyAddressTextViewID.setText("Outlet: "+"Huawei Telecommunications (INDIA) Co. Pvt. Ltd.");
            dashboardTopCompanyAddressTextViewID.setText(prefs.getString("USEROUTLETNAME", ""));
        } else {
            dashboardTopCompanyAddressTextViewID.setText("");
            //dashboardTopCompanyAddressTextViewID.setText("Outlet: "+"Huawei Telecommunications (INDIA) Co. Pvt. Ltd.");
            dashboardTopCompanyAddressTextViewID.setText(prefs.getString("USEROUTLETNAME", ""));
        }

        COMPANY_LOGO_NAME = prefs.getString("LOGO", "");
        System.out.println("COMPANY_LOGO_NAME IN DASHBOARD: " + COMPANY_LOGO_NAME);

        COMPANY_LOGO_PATH = Constants.BASE_URL_CLIENT_LOGO + COMPANY_LOGO_NAME;
        System.out.println("COMPANY_LOGO_PATH IN DASHBOARD: " + COMPANY_LOGO_PATH);

        imageLoader.DisplayImage(COMPANY_LOGO_PATH, dashboardTopbarCompanyLogoImageViewID);

        if (ROLE_TYPE.equalsIgnoreCase("E")) {
            dashBoardForAssociatesGridViewID.setVisibility(View.VISIBLE);
            dashBoardForTLGridViewID.setVisibility(View.GONE);
            dashBoardForSMGridViewID.setVisibility(View.GONE);
            dashBoardForSOOGridViewID.setVisibility(View.GONE);
            dashboardtopbarLocationImageViewID.setVisibility(View.VISIBLE);
            dashBoardForAssociatesGridViewID.setAdapter(new CustomAdapterForAssociates(DashBoardActivity.this));
        } else {
            dashBoardForAssociatesGridViewID.setVisibility(View.GONE);
            dashBoardForTLGridViewID.setVisibility(View.VISIBLE);
            dashBoardForSMGridViewID.setVisibility(View.GONE);
            dashBoardForSOOGridViewID.setVisibility(View.GONE);
            dashboardtopbarLocationImageViewID.setVisibility(View.VISIBLE);
            dashBoardForTLGridViewID.setAdapter(new CustomAdapterForTL(DashBoardActivity.this));
        }

    }

    //Creating an Image Directory for storing Self Pictures
    private void createImageDirectory() {
        File file = new File(Environment.getExternalStorageDirectory() + "/Self_Pictures");
        if (!file.exists()) {
            file.mkdir();
        }
    }

    private void checkLeaveStatus() {

        progressDialog.setMessage("Updating Credentials... Please wait!");
        progressDialog.setCancelable(false);
        progressDialog.show();
        client = new RestFullClient(Constants.BASE_URL + Constants.LEAVESTATUS_RELATIVE_URI);
        client.AddParam("associate_id", prefs.getString("USERID", ""));

        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {
                    client.Execute(1); //POST Request
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                receiveDataForServerResponse(client.jObj);
                handler.sendEmptyMessage(0);

            }

        }).start();

    }

    private void receiveDataForServerResponse(JSONObject jobj) {

        try {

            if (client.responseCode == 200) {

                STATUS = jobj.getString(TAG_STATUS);
                MESSAGE = jobj.getString(TAG_MESSAGE);

                System.out.println("STATUS: responseCode==200: " + STATUS);
                System.out.println("MESSAGE: responseCode==200: " + MESSAGE);

                if (STATUS.equalsIgnoreCase("true")) {
                    LEAVESTATUS = jobj.getString(TAG_LEAVESTATUS);
                    System.out.println("LEAVESTATUS: responseCode==200: & STATUS==true: " + LEAVESTATUS);
                }//if(STATUS.equalsIgnoreCase("true"))

            }//if(client.responseCode==200)

            if (client.responseCode != 200) {

                STATUS = jobj.getString(TAG_STATUS);
                MESSAGE = jobj.getString(TAG_MESSAGE);

                System.out.println("STATUS: responseCode!=200: " + STATUS);
                System.out.println("MESSAGE: responseCode!=200: " + MESSAGE);

            }//if(client.responseCode!=200)
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Handler handler = new Handler() {

        public void handleMessage(Message msg) {
            try {
                if ((progressDialog != null) && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }//handleMessage(Message msg)

    };

    //CustomAdapterForAssociates
    public class CustomAdapterForAssociates extends BaseAdapter {

        public Context cntx;

        public CustomAdapterForAssociates(Context context) {
            cntx = context;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return dashIconsForAssociates.length;
            //return 8;
        }

        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return arg0;
        }

        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return arg0;
        }

        @Override
        public int getViewTypeCount() {

            if (getCount() != 0)
                return getCount();

            //return 1;
            return 2;
        }

        @Override
        public int getItemViewType(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int pos, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub

            View view;
            ViewHolder viewHolder = new ViewHolder();

            GridView grid = (GridView) parent;
            //int sizeH = grid.getMinimumHeight();
            //int sizeW = grid.getMinimumWidth();

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) cntx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.custom_layout_for_dashboard, null);
                //view.setLayoutParams(new GridView.LayoutParams(sizeW, sizeH));
            } else {
                view = convertView;
            }

            view.setTag(viewHolder);
            final int pos1 = pos;

            LinearLayout layout = (LinearLayout) view.findViewById(R.id.customlayout_dashboard_LinearLayoutID);
            if (pos1 == 0) {
                layout.setBackgroundResource(R.drawable.bg_1);
            }
            if (pos1 == 1) {
                layout.setBackgroundResource(R.drawable.bg_2);
            }
            if (pos1 == 2) {
                layout.setBackgroundResource(R.drawable.bg_3);
            }
            if (pos1 == 3) {
                layout.setBackgroundResource(R.drawable.bg_4);
            }
            if (pos1 == 4) {
                layout.setBackgroundResource(R.drawable.bg_1);
            }
            if (pos1 == 5) {
                layout.setBackgroundResource(R.drawable.bg_2);
            }
            if (pos1 == 6) {
                layout.setBackgroundResource(R.drawable.bg_3);
            }
            if (pos1 == 7) {
                layout.setBackgroundResource(R.drawable.bg_4);
            }
            if (pos1 == 8) {
                layout.setBackgroundResource(R.drawable.bg_1);
            }
            viewHolder.imageView = (ImageView) view.findViewById(R.id.customlayout_dashboard_ImageViewID);
            viewHolder.imageView.setImageResource(dashIconsForAssociates[pos]);

            return view;
        }

        class ViewHolder {
            ImageView imageView;
        }

    }//CustomAdapterForAssociates

    //CustomAdapterForTL
    public class CustomAdapterForTL extends BaseAdapter {

        public Context cntx;

        public CustomAdapterForTL(Context context) {
            cntx = context;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return dashIconsForTL.length;
            //return 8;
        }

        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return arg0;
        }

        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return arg0;
        }

        @Override
        public int getViewTypeCount() {

            if (getCount() != 0)
                return getCount();

            //return 1;
            return 2;
        }

        @Override
        public int getItemViewType(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int pos, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub

            View view;
            ViewHolder viewHolder = new ViewHolder();

            GridView grid = (GridView) parent;
            //int sizeH = grid.getMinimumHeight();
            //int sizeW = grid.getMinimumWidth();

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) cntx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.custom_layout_for_dashboard, null);
                //view.setLayoutParams(new GridView.LayoutParams(sizeW, sizeH));
            } else {
                view = convertView;
            }

            view.setTag(viewHolder);
            final int pos1 = pos;

            LinearLayout layout = (LinearLayout) view.findViewById(R.id.customlayout_dashboard_LinearLayoutID);
            if (pos1 == 0) {
                layout.setBackgroundResource(R.drawable.bg_1);
            }
            if (pos1 == 1) {
                layout.setBackgroundResource(R.drawable.bg_2);
            }
            if (pos1 == 2) {
                layout.setBackgroundResource(R.drawable.bg_3);
            }
            if (pos1 == 3) {
                layout.setBackgroundResource(R.drawable.bg_4);
            }
            if (pos1 == 4) {
                layout.setBackgroundResource(R.drawable.bg_1);
            }
            if (pos1 == 5) {
                layout.setBackgroundResource(R.drawable.bg_2);
            }
            if (pos1 == 6) {
                layout.setBackgroundResource(R.drawable.bg_3);
            }
            if (pos1 == 7) {
                layout.setBackgroundResource(R.drawable.bg_4);
            }
            if (pos1 == 8) {
                layout.setBackgroundResource(R.drawable.bg_1);
            }
            if (pos1 == 9) {
                layout.setBackgroundResource(R.drawable.bg_2);
            }
            if (pos1 == 10) {
                layout.setBackgroundResource(R.drawable.bg_3);
            }
            if (pos1 == 11) {
                layout.setBackgroundResource(R.drawable.bg_1);
            }

            viewHolder.imageView = (ImageView) view.findViewById(R.id.customlayout_dashboard_ImageViewID);
            viewHolder.imageView.setImageResource(dashIconsForTL[pos]);

            return view;
        }

        class ViewHolder {
            ImageView imageView;
        }

    }//CustomAdapterForTL

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        deleteAllSelfies();
        stopGeolocationService(DashBoardActivity.this);

        DashBoardActivity.this.finish();
    }

    //Delete All Selfies
    private void deleteAllSelfies() {
        File file = new File(Environment.getExternalStorageDirectory() + "/Self_Pictures");
        if (file.exists() && file.isDirectory()) {
            System.out.println("'Self_Pictures' Directory is found");
            File[] files = file.listFiles();
            if (files != null) {
                if (files.length != 0) {
                    System.out.println("Total Files Found: " + files.length);
                    //System.out.println("Inside if(files != null)");
                    System.out.println("Inside if(files.length!= 0)");
                    for (File f : files) {
                        try {
                            System.out.println("Deleted File is: " + f.getPath().toString());
                            f.delete();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } else {
            System.out.println("'Self_Pictures' Directory is not found");
        }
    }

    //Display option disabled dialog for 'Leave'
    private void showOptionDisableDialog() {
        final AlertDialog.Builder aldb = new AlertDialog.Builder(DashBoardActivity.this);
        aldb.setMessage("This option is temporarily blocked because you are on 'Leave' today!");
        aldb.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
            }
        });
        aldb.show();
    }

    //Logout Dialog
    private void showLogoutDialog() {

        final AlertDialog.Builder aldb = new AlertDialog.Builder(DashBoardActivity.this);
        //aldb.setTitle("Exiting the app!");
        aldb.setMessage("Would you like to Logout?");
        aldb.setNegativeButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                stopGeolocationService(getApplicationContext());

                prefsEditor.putString("LOGGEDIN", "no");
                prefsEditor.commit();

                Toast.makeText(DashBoardActivity.this, "You have been Logged out successfully!", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(DashBoardActivity.this, LoginActivity.class);
                DashBoardActivity.this.finish();
                startActivity(i);
                overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
            }
        });

        aldb.setPositiveButton("No", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
            }
        });

        aldb.show();
    }

    private void checkAllPermissions() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(DashBoardActivity.this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(DashBoardActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(DashBoardActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(DashBoardActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(DashBoardActivity.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(DashBoardActivity.this,
                    new String[]{android.Manifest.permission.READ_PHONE_STATE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.CAMERA}, 1991);

        }//if

    }//checkAllPermissions()

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[],
                                           int[] grantResults) {
        switch (requestCode) {
            case 1991: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED
                        && grantResults[2] == PackageManager.PERMISSION_GRANTED
                        && grantResults[3] == PackageManager.PERMISSION_GRANTED
                        && grantResults[4] == PackageManager.PERMISSION_GRANTED
                        && grantResults[5] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // tasks you need to do.
                    System.out.println("ACCESS_FINE_LOCATION & ACCESS_COARSE_LOCATION ARE GRANTED!");

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    System.out.println("ACCESS_FINE_LOCATION & ACCESS_COARSE_LOCATION ARE REJECTED!");
                }
            }

        }

    }

    @Override
    protected void onDestroy() {
        if (isServiceRunning("GeolocationService")) {
            System.out.println("GeolocationService is running!");
            stopGeolocationService(DashBoardActivity.this);
        } else {
            System.out.println("GeolocationService is already destroyed!");
        }

        try {
            if ((progressDialog != null) && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        } catch (final Exception e) {
            e.printStackTrace();
        } finally {
            progressDialog = null;
        }

        super.onDestroy();
    }


}//Main Class
