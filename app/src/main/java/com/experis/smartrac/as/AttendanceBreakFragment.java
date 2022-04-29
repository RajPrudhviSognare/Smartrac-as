package com.experis.smartrac.as;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.experis.smartrac.as.R;

import org.json.JSONObject;

import java.util.Calendar;

public class AttendanceBreakFragment extends Fragment implements DatePickerDialog.OnDateSetListener{

    private String TAG_ATTENDANCE_TYPE = "break_start"; // "long_break","short_break"
    private String date = "0000-00-00";
    private String reason = null;

    private EditText attendancepagetodateEditTextID1;
    private EditText attendancepagereasonEditTextID1;
    private ImageView attendancePageSubmitImageViewID3;
    private Spinner spinner;

    private SharedPreferences prefs;
    private SharedPreferences.Editor prefsEditor;
    private ProgressDialog progressDialog;

    private RestFullClient client;
    private String TAG_STATUS = "status";
    private String TAG_ERROR = "error";
    private String TAG_MESSAGE = "message";

    private String STATUS = "";
    private String ERROR = "";
    private String MESSAGE = "";

    private Calendar calendar;
    private int year, month, day;


    public AttendanceBreakFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initAllViews();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_attendance_break, container, false);

        attendancepagetodateEditTextID1 = (EditText)view.findViewById(R.id.attendancepagetodateEditTextID12);
        attendancepagereasonEditTextID1 = (EditText)view.findViewById(R.id.attendancepagereasonEditTextID12);
        attendancePageSubmitImageViewID3 = (ImageView)view.findViewById(R.id.attendancePageSubmitImageViewID42);
        spinner = (Spinner)view.findViewById(R.id.spinner12);

        return view;
    }

    private void initAllViews(){

        //shared preference
        prefs = getActivity().getSharedPreferences(CommonUtils.PREFERENCE_NAME,getActivity().MODE_PRIVATE);
        prefsEditor = prefs.edit();

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);

        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        //Progress Dialog
        progressDialog = new ProgressDialog(getActivity());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        attendancepagetodateEditTextID1.setText(new StringBuilder().append(year).append("-")
                .append(month+1).append("-").append(day));

        attendancepagetodateEditTextID1.setOnClickListener(new EditText.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getActivity().showDialog(DATE_DIALOG_ID);
                DatePickerDialog dialog = new DatePickerDialog(getActivity(), AttendanceBreakFragment.this, year, month, day);
                dialog.show();
            }
        });
        attendancepagetodateEditTextID1.setOnFocusChangeListener(new EditText.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                //getActivity().showDialog(DATE_DIALOG_ID);
                DatePickerDialog dialog = new DatePickerDialog(getActivity(), AttendanceBreakFragment.this, year, month, day);
                dialog.show();
            }
        });

        attendancePageSubmitImageViewID3.setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(CommonUtils.isInternelAvailable(getActivity())){

                        validateData();
                    }
                    else{
                        Toast.makeText(getActivity(), "No internet connection!", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        ////Added Later
        spinner.setSelection(1);
        ////

        spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==0){
                    //((TextView) parent.getChildAt(0)).setTextColor(Color.parseColor("#e1e1e1"));

                    if(parent!=null){
                        //((TextView) view).setTextColor(Color.parseColor("#e1e1e1"));
                        View v = spinner.getSelectedView();
                        if(v!=null){
                            ((TextView)v).setTextColor(Color.parseColor("#e1e1e1"));
                        }
                    }

                    /*TextView spinnerText = (TextView)parent.getChildAt(position);
                    spinnerText.setTextColor(Color.parseColor("#e1e1e1"));*/

                    TAG_ATTENDANCE_TYPE = "break_end";
                    //Toast.makeText(getActivity(), LEAVE_TYPE1+" Selected", Toast.LENGTH_SHORT).show();
                }
                if(position==1){
                    //((TextView) parent.getChildAt(0)).setTextColor(Color.parseColor("#e1e1e1"));

                    if(parent!=null){
                        //((TextView) view).setTextColor(Color.parseColor("#e1e1e1"));
                        View v = spinner.getSelectedView();
                        if(v!=null){
                            ((TextView)v).setTextColor(Color.parseColor("#e1e1e1"));
                        }
                    }

                    /*TextView spinnerText = (TextView)parent.getChildAt(position);
                    spinnerText.setTextColor(Color.parseColor("#e1e1e1"));*/

                    TAG_ATTENDANCE_TYPE = "break_start";
                    //Toast.makeText(getActivity(), LEAVE_TYPE1+" Selected", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }//onActivityCreated(Bundle savedInstanceState)

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        // this.editText.setText();
        attendancepagetodateEditTextID1.setText(new StringBuilder().append(year).append("-")
                .append(monthOfYear+1).append("-").append(dayOfMonth));
    }

    //Validate Data locally(Checks whether the fields are empty or not)
    private void validateData() {

        boolean cancel = false;
        View focusView = null;

        /*if(TextUtils.isEmpty(attendancepagetodateEditTextID1.getText().toString()))
        {

            attendancepagetodateEditTextID1.setError("Required field!");
            focusView = attendancepagetodateEditTextID1;
            cancel = true;

        }*/
        /*else if(TextUtils.isEmpty(attendancepagereasonEditTextID1.getText().toString()))
        {

            attendancepagereasonEditTextID1.setError("Required field!");
            focusView = attendancepagereasonEditTextID1;
            cancel = true;

        }*/

        if(cancel){
            focusView.requestFocus();
        }
        else
        {
            getTextValues();
        }

    }//validateData

    //Get the values from EditText
    private void getTextValues() {

        date = attendancepagetodateEditTextID1.getText().toString();
        reason = attendancepagereasonEditTextID1.getText().toString();

        /*if(!date.equalsIgnoreCase("")&&!reason.equalsIgnoreCase("")){

            sendDataForLeave();
        }
        else{

            Toast.makeText(getActivity(), "All Fields Are Mandatory!", Toast.LENGTH_SHORT).show();
        }*/

        /*if(!date.equalsIgnoreCase("")){

            sendDataForBreak();
        }
        else{

            Toast.makeText(getActivity(), "Date is Mandatory!", Toast.LENGTH_SHORT).show();
        }*/

        sendDataForBreak();
    }

    private void sendDataForBreak(){

        Constants.ASSOCIATE_ID = prefs.getString("USERID","");
        Constants.TL_ID = prefs.getString("TLID","");
        Constants.OUTLET_ID = prefs.getString("USEROUTLETID","");
        Constants.ATTENDANCE_TYPE = TAG_ATTENDANCE_TYPE;
        Constants.ATTENDANCE_IMAGE = "";
        //Constants.ATTENDANCE_DATE = "";
        //Constants.ATTENDANCE_DATE = "yyyy-mm-dd";
        Constants.ATTENDANCE_DATE = date;
        Constants.REASON = reason;
        Constants.LEAVE_TYPE = "";

        //progressDialog.setTitle("Attendance");
        progressDialog.setMessage("Submitting Your Break... Please wait!");
        progressDialog.setCancelable(false);
        progressDialog.show();
        client = new RestFullClient(Constants.BASE_URL+Constants.ATTENDANCE_RELATIVE_URI);
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

    Handler handler = new Handler(){

        public void handleMessage(Message msg){

            try {
                if((progressDialog != null) && progressDialog.isShowing() ){
                    progressDialog.dismiss();
                }
            }catch (final Exception e) {
                e.printStackTrace();
            }

            //Success
            if(client.responseCode==200){

                //Success
                if(STATUS.equalsIgnoreCase("true")){
                    //showSuccessDialog();
                    //Toast.makeText(getActivity(), MESSAGE, Toast.LENGTH_SHORT).show();

                    Constants.ASSOCIATE_ID = "";
                    Constants.TL_ID = "";
                    Constants.OUTLET_ID = "";
                    Constants.ATTENDANCE_TYPE = "";
                    Constants.ATTENDANCE_IMAGE = "";
                    Constants.CURRENT_LAT = "0.0";
                    Constants.CURRENT_LONG = "0.0";
                    Constants.ATTENDANCE_DATE = "0000-00-00";
                    Constants.REASON = "";
                    Constants.LEAVE_TYPE = "";
                    Constants.DISTANCE = "0";

                    showSuccessDialog();

                   /* getActivity().finish();
                    getActivity().overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);*/

                }

                //Failed
                if(STATUS.equalsIgnoreCase("false")){
                    showFailureDialog();
                }
            }

            //Failed
            if(client.responseCode!=200){

                //Toast.makeText(getActivity(), MESSAGE, Toast.LENGTH_SHORT).show();
                showFailureDialog();

            }

        }//handleMessage(Message msg)

    };

    //Show Success Dialog
    private void showSuccessDialog(){
        //Alert Dialog Builder
        final AlertDialog.Builder aldb = new AlertDialog.Builder(getActivity());
        aldb.setTitle("Success!");
        aldb.setMessage("\n"+MESSAGE);
        aldb.setCancelable(false);
        aldb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                clearData();
            }
        });
        aldb.show();
    }

    //Show Failure Dialog
    private void showFailureDialog(){
        //Alert Dialog Builder
        final AlertDialog.Builder aldb = new AlertDialog.Builder(getActivity());
        aldb.setTitle("Failed!");
        aldb.setMessage("\nReason: "+MESSAGE);
        aldb.setPositiveButton("OK", null);
        aldb.show();
    }

    private void clearData(){

        attendancepagetodateEditTextID1.setText("");
        attendancepagetodateEditTextID1.setText(new StringBuilder().append(year).append("-")
                .append(month+1).append("-").append(day));
        attendancepagereasonEditTextID1.setText("");

        ////Added Later
        spinner.setSelection(1);
        ////
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        try {
            if((progressDialog != null) && progressDialog.isShowing() ){
                progressDialog.dismiss();
            }
        }catch (final Exception e) {
            e.printStackTrace();
        } finally {
            progressDialog = null;
        }

        super.onDestroy();

    }

}
