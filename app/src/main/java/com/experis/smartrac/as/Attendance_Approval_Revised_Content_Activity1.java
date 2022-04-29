package com.experis.smartrac.as;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.experis.smartrac.as.R;

public class Attendance_Approval_Revised_Content_Activity1 extends AppCompatActivity {

    private PowerManager.WakeLock mWakeLock;
    private SharedPreferences prefs;
    private SharedPreferences.Editor prefsEditor;
    private ProgressDialog progressDialog;

    private ImageView attendanceapprovaltopbarbackImageViewID1;
    private TextView mytextID;

    private FrameLayout containerViewID;

    private Intent intent;
    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_approval_revised1_content);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.attendance_approval_conetent_topbar_title);

        /*PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "AttendanceTracking-DoNotDimScreen");
        mWakeLock.acquire();*/

        initAllViews();

        attendanceapprovaltopbarbackImageViewID1.setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        intent = getIntent();
        type = intent.getStringExtra("TYPE");
        System.out.println("TYPE: "+type);

        if(type.equalsIgnoreCase("INTIME")){

            mytextID.setText("");
            mytextID.setText("Intime - Attendance Approval");

            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction fmt = fm.beginTransaction();
            AttendanceApprovalIntimeFragment inTime = new AttendanceApprovalIntimeFragment();
            fmt.replace(R.id.containerViewID,inTime,"IntimeFragment");
            fmt.commit();
        }
        if(type.equalsIgnoreCase("OUTTIME")){
            mytextID.setText("");
            mytextID.setText("Outtime - Attendance Approval");

            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction fmt = fm.beginTransaction();
            AttendanceApprovalOuttimeFragment inTime1 = new AttendanceApprovalOuttimeFragment();
            fmt.replace(R.id.containerViewID,inTime1,"OuttimeFragment");
            fmt.commit();
        }
        if(type.equalsIgnoreCase("WEEKLYOFF")){
            mytextID.setText("");
            //mytextID.setText("Weeklyoff - Attendance Approval");
            mytextID.setText("Weekly off");

            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction fmt = fm.beginTransaction();
            AttendanceApprovalWeeklyOffFragment inTime2 = new AttendanceApprovalWeeklyOffFragment();
            fmt.replace(R.id.containerViewID,inTime2,"WeeklyoffFragment");
            fmt.commit();
        }
        if(type.equalsIgnoreCase("LEAVE")){
            mytextID.setText("");
            //mytextID.setText("Leave - Attendance Approval");
            mytextID.setText("Leave");

            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction fmt = fm.beginTransaction();
            AttendanceApprovalLeaveFragment inTime3 = new AttendanceApprovalLeaveFragment();
            fmt.replace(R.id.containerViewID,inTime3,"LeaveFragment");
            fmt.commit();
        }
        if(type.equalsIgnoreCase("LWP")){
            mytextID.setText("");
            //mytextID.setText("Leave - Attendance Approval");
            mytextID.setText("LWP");

            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction fmt = fm.beginTransaction();
            AttendanceApprovalLWPFragment inTime3 = new AttendanceApprovalLWPFragment();
            fmt.replace(R.id.containerViewID,inTime3,"LWPFragment");
            fmt.commit();
        }
      /* if(type.equalsIgnoreCase("HL")){
            mytextID.setText("");
            //mytextID.setText("Leave - Attendance Approval");
            mytextID.setText("Half-Leave");

            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction fmt = fm.beginTransaction();
            AttendanceApprovalHalfLeaveFragment inTime7 = new AttendanceApprovalHalfLeaveFragment();
            fmt.replace(R.id.containerViewID,inTime7,"HalfLeaveFragment");
            fmt.commit();
        }*/
        if(type.equalsIgnoreCase("OD")){
            mytextID.setText("");
            //mytextID.setText("Meeting - Attendance Approval");
            mytextID.setText("OD");

            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction fmt = fm.beginTransaction();
            AttendanceApprovalMeetingFragment inTime4 = new AttendanceApprovalMeetingFragment();
            fmt.replace(R.id.containerViewID,inTime4,"MeetingFragment");
            fmt.commit();
        }

        /* if(type.equalsIgnoreCase("WFH")){
            mytextID.setText("");
            //mytextID.setText("Meeting - Attendance Approval");
            mytextID.setText("Work from Home");

            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction fmt = fm.beginTransaction();
            AttendanceApprovalWFHFragment inTime5 = new AttendanceApprovalWFHFragment();
            fmt.replace(R.id.containerViewID,inTime5,"WFHFragment");
            fmt.commit();
        }
        if(type.equalsIgnoreCase("WFCO")){
            mytextID.setText("");
            //mytextID.setText("Meeting - Attendance Approval");
            mytextID.setText("Work from Client Office");

            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction fmt = fm.beginTransaction();
            AttendanceApprovalWFCOFragment inTime6 = new AttendanceApprovalWFCOFragment();
            fmt.replace(R.id.containerViewID,inTime6,"WFCOFragment");
            fmt.commit();
        }
        if(type.equalsIgnoreCase("HOLIDAY")){
            mytextID.setText("");
            //mytextID.setText("Leave - Attendance Approval");
            mytextID.setText("Holiday");

            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction fmt = fm.beginTransaction();
            AttendanceApprovalHolidayFragment inTime8 = new AttendanceApprovalHolidayFragment();
            fmt.replace(R.id.containerViewID,inTime8,"HolidayFragment");
            fmt.commit();
        }


*/


    }//onCreate()

    private void initAllViews() {
        //shared preference
        prefs = getSharedPreferences(CommonUtils.PREFERENCE_NAME, MODE_PRIVATE);
        prefsEditor = prefs.edit();

        containerViewID = (FrameLayout) findViewById(R.id.containerViewID);
        attendanceapprovaltopbarbackImageViewID1 = (ImageView)findViewById(R.id.attendanceapprovaltopbarbackImageViewID1);
        mytextID = (TextView)findViewById(R.id.mytextID);

        //Progress Dialog
        progressDialog = new ProgressDialog(Attendance_Approval_Revised_Content_Activity1.this);

    }

    @Override
    public void onBackPressed()
    {
        Attendance_Approval_Revised_Content_Activity1.this.finish();
        overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
    }
    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }



}//Main Class
