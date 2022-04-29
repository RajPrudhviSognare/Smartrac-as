package com.experis.smartrac.as;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;


import com.experis.smartrac.as.R;

import java.util.ArrayList;
import java.util.List;

public class HRMSTabbedActivity extends AppCompatActivity {

    private PowerManager.WakeLock mWakeLock;
    private SharedPreferences prefs;
    private SharedPreferences.Editor prefsEditor;
    private ProgressDialog progressDialog;

    private TabLayout tabLayout;
    private ViewPager viewPager;

    private ImageView employeeinfotopbarbackImageViewID;
    private ImageView employeeinfotopbarusericonImageViewID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hrms_tabbed);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.employee_info_topbar_title);

        /*PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "AttendanceTracking-DoNotDimScreen");
        mWakeLock.acquire();*/

        initAllViews();

        viewPager = (ViewPager) findViewById(R.id.viewpagerHRMS);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabsHRMS);
        tabLayout.setupWithViewPager(viewPager);

        viewPager.setOffscreenPageLimit(0);

        employeeinfotopbarbackImageViewID.setOnClickListener(new ImageView.OnClickListener(){
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //User Icon Click Event
        employeeinfotopbarusericonImageViewID.setOnClickListener(new ImageView.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent i = new Intent(HRMSTabbedActivity.this,ChangePasswordActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
            }
        });

    }

    private void initAllViews(){
        //shared preference
        prefs = getSharedPreferences(CommonUtils.PREFERENCE_NAME,MODE_PRIVATE);
        prefsEditor = prefs.edit();

        employeeinfotopbarbackImageViewID = (ImageView) findViewById(R.id.employeeinfotopbarbackImageViewID);
        employeeinfotopbarusericonImageViewID = (ImageView) findViewById(R.id.employeeinfotopbarusericonImageViewID);

        //Progress Dialog
        progressDialog = new ProgressDialog(HRMSTabbedActivity.this);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new HRMSEmpDetailsFragment(), "Information");
        adapter.addFragment(new HRMSEmpLeaveBalanceFragment(), "Leave Balance");
        adapter.addFragment(new HRMSPayslipFragment(), "Payslip");
        adapter.addFragment(new LetterFragment(),"Letter");
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
    public void onBackPressed()
    {
        HRMSTabbedActivity.this.finish();
        overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
    }

    @Override
    public void onResume(){
        super.onResume();
    }

}//Main Class
