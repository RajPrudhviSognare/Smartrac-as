package com.experis.smartrac.as;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;


import com.experis.smartrac.as.R;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;

public class AttendanceMeetingFragment extends Fragment implements DatePickerDialog.OnDateSetListener{

    private String TAG_ATTENDANCE_TYPE = "od"; //"Meeting"
    private String date = null;
    private String reason = null;

    private EditText attendancepagetodateEditTextID2,attendancepagetodateEditTextID22,attendancepagetodateEditTextID24;
    private EditText attendancepagereasonEditTextID2;
    private ImageView attendancePageSubmitImageViewID4;

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
    private String fromtime="00:00", totime="00:00";
    private Date formdate,todate;

    public AttendanceMeetingFragment() {
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
        //Toast.makeText(getActivity(),"Out Time Fragment",Toast.LENGTH_LONG).show();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.attendance_meeting_fragment, container, false);

        attendancepagetodateEditTextID2 = (EditText)view.findViewById(R.id.attendancepagetodateEditTextID2);
        attendancepagereasonEditTextID2 = (EditText)view.findViewById(R.id.attendancepagereasonEditTextID2);
        attendancePageSubmitImageViewID4 = (ImageView)view.findViewById(R.id.attendancePageSubmitImageViewID4);
        attendancepagetodateEditTextID22=(EditText)view.findViewById(R.id.attendancepagetodateEditTextID22);
        attendancepagetodateEditTextID24=(EditText)view.findViewById(R.id.attendancepagetodateEditTextID24);

        attendancepagetodateEditTextID22.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {


                        fromtime=hourOfDay + ":" + minute;
                        attendancepagetodateEditTextID22.setText(fromtime);
                    }
                }, hour, minute, false);

                timePickerDialog.show();

            }
        });


            attendancepagetodateEditTextID24.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Calendar c = Calendar.getInstance();
                    int hour = c.get(Calendar.HOUR_OF_DAY);
                    int minute = c.get(Calendar.MINUTE);

                    TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            if(!fromtime.equalsIgnoreCase("")) {
                            totime = hourOfDay + ":" + minute;
                            attendancepagetodateEditTextID24.setText(totime);
                            }
                            else{
                                totime="";
                                attendancepagetodateEditTextID24.setText(totime);
                                Toast.makeText(getActivity(),"Please select form time first",Toast.LENGTH_SHORT).show();
                            }

                        }
                    }, hour, minute, false);

                    timePickerDialog.show();
                }
            });

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

        attendancepagetodateEditTextID2.setText(new StringBuilder().append(year).append("-")
                .append(month+1).append("-").append(day));

        attendancepagetodateEditTextID2.setOnClickListener(new EditText.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getActivity().showDialog(DATE_DIALOG_ID);
                DatePickerDialog dialog = new DatePickerDialog(getActivity(), AttendanceMeetingFragment.this, year, month, day);
                dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                dialog.show();
            }
        });
        attendancepagetodateEditTextID2.setOnFocusChangeListener(new EditText.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                //getActivity().showDialog(DATE_DIALOG_ID);
                DatePickerDialog dialog = new DatePickerDialog(getActivity(), AttendanceMeetingFragment.this, year, month, day);
                dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                dialog.show();
            }
        });

        attendancePageSubmitImageViewID4.setOnClickListener(new ImageView.OnClickListener() {
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


    }//onActivityCreated(Bundle savedInstanceState)

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        // this.editText.setText();
        attendancepagetodateEditTextID2.setText(new StringBuilder().append(year).append("-")
                .append(monthOfYear+1).append("-").append(dayOfMonth));
    }

    //Validate Data locally(Checks whether the fields are empty or not)
    private void validateData() {

        boolean cancel = false;
        View focusView = null;
         /*  SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
                        formdate = new Date();
                        todate=new Date();
       try {
            if(!fromtime.equalsIgnoreCase(""))
            formdate=dateFormat.parse(fromtime);
            if(!totime.equalsIgnoreCase(""))
            todate=dateFormat.parse(totime);
        } catch (ParseException e) {
            e.printStackTrace();
        }*/
        //System.out.println(dateFormat.format(date));

        if(TextUtils.isEmpty(attendancepagetodateEditTextID2.getText().toString()))
        {
            attendancepagetodateEditTextID2.setError("Required field!");
            focusView = attendancepagetodateEditTextID2;
            cancel = true;
        }
        if(TextUtils.isEmpty(attendancepagereasonEditTextID2.getText().toString()))
        {
            attendancepagereasonEditTextID2.setError("Required field!");
            focusView = attendancepagereasonEditTextID2;
            cancel = true;
        }
       /* if(fromtime.equalsIgnoreCase("")){
           // attendancepagetodateEditTextID22.setError("Required field!");
           // focusView = attendancepagetodateEditTextID22;
            cancel = true;
            Toast.makeText(getActivity(),"From time is required field!",Toast.LENGTH_SHORT).show();
        }
        if( totime.equalsIgnoreCase("")){
           // attendancepagetodateEditTextID24.setError("Required field!");
           // focusView = attendancepagetodateEditTextID24;
            cancel = true;
            Toast.makeText(getActivity(),"To time is Required field!",Toast.LENGTH_SHORT).show();
        }

        if(formdate.after(todate))
        {
            //attendancepagetodateEditTextID22.setError("From time should be lesser than To time");
            //focusView = attendancepagetodateEditTextID22;
            //attendancepagetodateEditTextID24.setError("From time should be lesser than To time");

            cancel = true;
            Toast.makeText(getActivity(),"From time cannot be before To time",Toast.LENGTH_SHORT).show();
        }
*/

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

        date = attendancepagetodateEditTextID2.getText().toString();
        reason = attendancepagereasonEditTextID2.getText().toString();

        if(!date.equalsIgnoreCase("")&&!reason.equalsIgnoreCase("")){
            sendDataForMeeting();
        }
        else{
            Toast.makeText(getActivity(), "All Fields Are Mandatory!", Toast.LENGTH_SHORT).show();
        }

    }

    private void sendDataForMeeting(){

        Constants.ASSOCIATE_ID = prefs.getString("USERISDCODE","");
        Constants.TL_ID = prefs.getString("TLID", "");
        Constants.ATTENDANCE_TYPE = TAG_ATTENDANCE_TYPE;
        Constants.ATTENDANCE_DATE = date;
        Constants.CURRENT_LAT = Constants.UNIV_LAT;
        Constants.CURRENT_LONG = Constants.UNIV_LONG;
        Constants.REASON = reason;
        Constants.REMARKS = "";
        Constants.LEAVE_TYPE = "";
        Constants.ATTENDANCE_IMAGE = "";
        Constants.od_from_time=fromtime;
        Constants.od_to_time=totime;
        Constants.client_name="";
        Constants.client_address="";


        //progressDialog.setTitle("Attendance");
        progressDialog.setMessage("Submitting OD Details... Please wait!");
        progressDialog.setCancelable(false);
        progressDialog.show();
        client = new RestFullClient(Constants.BASE_URL+Constants.ATTENDANCE_RELATIVE_URI);

       // client.AddParam("associate_code", Constants.ASSOCIATE_ID);
        client.AddParam("associate_id", prefs.getString("USERID",""));
        System.out.println("associate_code: "+Constants.ASSOCIATE_ID);

        client.AddParam("attendance_type", Constants.ATTENDANCE_TYPE);
        System.out.println("attendance_type: "+Constants.ATTENDANCE_TYPE);



        client.AddParam("tl_id", Constants.TL_ID);
        client.AddParam("od_from_time", Constants.od_from_time);
        System.out.println("od_from_time: "+Constants.od_from_time);

        client.AddParam("od_to_time", Constants.od_to_time);
        System.out.println("od_to_time: "+Constants.od_to_time);

        client.AddParam("client_name", Constants.client_name);
        System.out.println("client_name: "+Constants.client_name);

        client.AddParam("client_address", Constants.client_address);
        System.out.println("client_address: "+Constants.client_address);


        client.AddParam("attendance_date", Constants.ATTENDANCE_DATE);
        System.out.println("attendance_date: "+Constants.ATTENDANCE_DATE);
        client.AddParam("attendance_to_date", Constants.ATTENDANCE_DATE);

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

        attendancepagetodateEditTextID2.setText("");
        attendancepagetodateEditTextID2.setText(new StringBuilder().append(year).append("-")
                .append(month+1).append("-").append(day));
        attendancepagereasonEditTextID2.setText("");
        attendancepagetodateEditTextID22.setText("");
        attendancepagetodateEditTextID24.setText("");
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
