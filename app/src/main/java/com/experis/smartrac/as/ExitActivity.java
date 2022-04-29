package com.experis.smartrac.as;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

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
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ExitActivity extends AppCompatActivity {

    private SharedPreferences prefs;
    private SharedPreferences.Editor prefsEditor;
    private ProgressDialog progressDialog;

    private ImageView viewAttendancetopbarbackImageViewID,viewAttendancetopbarusericonImageViewID;
    private EditText emailidedt,noticeedt,reasonforleaveedt;

    private String baseURL;
    private String SOAPRequestXML;
    private HttpResponse httpResponse = null;

    private String TAG_EmpCode = "EmpCode";
    private String EmpCode = "";

    private String TAG_InDate = "LeaveType";
    private String InDate = "";


    private String TAG_InTime = "LeaveFrom1";
    private String InTime = "";

    private String TAG_OutTime = "LeaveTo1";
    private String OutTime = "";

    private String TAG_HoursWorked = "RequestStatus";
    private String HoursWorked = "";

    private List<String> InDateList = null;
    private List<String> InTimeList = null;
    private List<String> OutTimeList = null;
    private List<String> HoursWorkedList = null;
    private ImageView attendancePageSubmitImageViewID3;
    private EditText attendancepagetodateEditTextID1,regdt;
    private Calendar calendar;
    private int year, month, day;

    private String date = null,todate=null;
    String mDateTime = null,mDateTime1 = null;
    private Date date1,date2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exit);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.view_attendance_topbar2);

        initAllViews();


        attendancepagetodateEditTextID1.setText(new StringBuilder().append(year).append("-")
                .append(month+1).append("-").append(day+1));
        regdt.setText(new StringBuilder().append(year).append("-")
                .append(month+1).append("-").append(day));

        attendancepagetodateEditTextID1.setOnClickListener(new EditText.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getActivity().showDialog(DATE_DIALOG_ID);

              /*  DatePickerDialog dialog = new DatePickerDialog(ViewAttendanceActivity1.this, AttendanceLeaveFragment1.this, year, month, day);
                dialog.show();*/
                setDate(999);
            }
        });
        attendancepagetodateEditTextID1.setOnFocusChangeListener(new EditText.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                //getActivity().showDialog(DATE_DIALOG_ID);

                setDate(999);
               /* DatePickerDialog dialog = new DatePickerDialog(ViewAttendanceActivity1.this, AttendanceLeaveFragment1.this, year, month, day);
                dialog.show();*/
            }
        });

        regdt.setOnClickListener(new EditText.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getActivity().showDialog(DATE_DIALOG_ID);

              /*  DatePickerDialog dialog = new DatePickerDialog(ViewAttendanceActivity1.this, AttendanceLeaveFragment1.this, year, month, day);
                dialog.show();*/
                setDate(988);
            }
        });
        regdt.setOnFocusChangeListener(new EditText.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                //getActivity().showDialog(DATE_DIALOG_ID);

                setDate(988);
               /* DatePickerDialog dialog = new DatePickerDialog(ViewAttendanceActivity1.this, AttendanceLeaveFragment1.this, year, month, day);
                dialog.show();*/
            }
        });







        //Back Button
        viewAttendancetopbarbackImageViewID.setOnClickListener(new ImageView.OnClickListener(){

            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        viewAttendancetopbarusericonImageViewID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(ExitActivity.this, ExitActivity.class);
                startActivity(i);
            }
        });

        attendancePageSubmitImageViewID3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(CommonUtils.isInternelAvailable(ExitActivity.this)){

                            validateData();
                    }
                    else{
                        Toast.makeText(ExitActivity.this, "No internet connection!", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }//onCreate()



    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub

        if (id == 999) {
            DatePickerDialog datePickerDialog= new DatePickerDialog(this,
                    myDateListener, year, month, day);
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_YEAR, 1);
            datePickerDialog.getDatePicker().setMinDate(cal.getTimeInMillis()); // where  DatePicker dp


           // datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            return  datePickerDialog;
        }
        if (id == 988) {
            DatePickerDialog datePickerDialog= new DatePickerDialog(this,
                    myDateListener1, year, month, day);
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_YEAR, 1);
            //datePickerDialog.getDatePicker().setMinDate(cal.getTimeInMillis()); // where  DatePicker dp


            // datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            return  datePickerDialog;
        }

        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day
                    showDate(arg1, arg2, arg3);
                }
            };

    private void showDate(int year, int monthOfYear, int dayOfMonth) {



            try{

                attendancepagetodateEditTextID1.setText(new StringBuilder().append(year).append("-")
                        .append(monthOfYear + 1).append("-").append(dayOfMonth));


            }
            catch(Exception e){

            }



    }

    private DatePickerDialog.OnDateSetListener myDateListener1 = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day
                    showDate1(arg1, arg2, arg3);
                }
            };

    private void showDate1(int year, int monthOfYear, int dayOfMonth) {



        try{

            regdt.setText(new StringBuilder().append(year).append("-")
                    .append(monthOfYear + 1).append("-").append(dayOfMonth));


        }
        catch(Exception e){

        }



    }

    public void setDate(int d) {

        showDialog(d);

    }


    private void initAllViews(){

        //shared preference
        prefs = getSharedPreferences(CommonUtils.PREFERENCE_NAME,MODE_PRIVATE);
        prefsEditor = prefs.edit();

        viewAttendancetopbarbackImageViewID = (ImageView)findViewById(R.id.viewAttendancetopbarbackImageViewID);
        viewAttendancetopbarusericonImageViewID=(ImageView)findViewById(R.id.viewAttendancetopbarusericonImageViewID);
        attendancePageSubmitImageViewID3=(ImageView)findViewById(R.id.attendancePageSubmitImageViewID3);
        attendancepagetodateEditTextID1 = (EditText)findViewById(R.id.attendancepagetodateEditTextID1);
        emailidedt=(EditText)findViewById(R.id.emailidedt);
        noticeedt=(EditText)findViewById(R.id.noticeedt);
        reasonforleaveedt=(EditText)findViewById(R.id.reasonforleaveedt);
        regdt=(EditText)findViewById(R.id.regdt);

        InDateList = new ArrayList<String>(0);
        InTimeList = new ArrayList<String>(0);
        OutTimeList = new ArrayList<String>(0);
        HoursWorkedList = new ArrayList<String>(0);

        //Progress Dialog
        progressDialog = new ProgressDialog(ExitActivity.this);
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);

        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

    }

    private void getAttendanceDetails(){
        progressDialog.setMessage("Requesting... Please wait!");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String EmpID = prefs.getString("USERISDCODE","");
        System.out.println("EmpID: "+EmpID);
        mDateTime1=attendancepagetodateEditTextID1.getText().toString();

        baseURL = Constants.base_url_default;
        SOAPRequestXML = Constants.soapRequestHeader+
                "<soapenv:Header/>"
                +"<soapenv:Body>"
                +"<tem:ExitRequest>"
                //+"<tem:emp_code>"+100400985+"</tem:emp_code>"
                +"<tem:emp_code>"+EmpID+"</tem:emp_code>"
                +"<tem:notice_period>"+noticeedt.getText().toString()+"</tem:notice_period>"
                +"<tem:last_working_day>"+mDateTime1+"</tem:last_working_day>"
                +"<tem:personal_email_id>"+emailidedt.getText().toString()+"</tem:personal_email_id>"
                +"<tem:reason_for_leaving>"+reasonforleaveedt.getText().toString()+"</tem:reason_for_leaving>"
                +"</tem:ExitRequest>"
                +"</soapenv:Body>"
                +"</soapenv:Envelope>";

        //String msgLength = String.format("%1$d", SOAPRequestXML.length());
        System.out.println("Request== "+SOAPRequestXML);

        new Thread(new Runnable(){

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

                    System.out.println("Server Response = "+Response);
                    StatusLine status = httpResponse.getStatusLine();
                    System.out.println("Server status code = "+status.getStatusCode());
                    System.out.println("Server httpResponse.getStatusLine() = "+httpResponse.getStatusLine().toString());
                    System.out.println("Server Staus = "+httpResponse.getEntity().toString());

                    getParsingElementsForAttendanceDetails(xpp);

                } catch (HttpResponseException e) {
                    Log.i("httpResponse Error = ", e.getMessage());
                }
                catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                handler.sendEmptyMessage(0);

            }

        }).start();
    }

    //getParsingElementsForAttendanceDetails(xpp);
    public void getParsingElementsForAttendanceDetails(XmlPullParser xpp){
        String text = "";
        try {
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagname = xpp.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        tagname=xpp.getName();
                        if(tagname.equalsIgnoreCase("ExitRequestResult")) {



                            xpp.nextTag();
                            InDate = xpp.nextText();
                            Log.d("Listing ", InDate);


                            xpp.nextTag();
                            OutTime = xpp.nextText();
                            Log.d("Listing ", OutTime);



                           /* xpp.nextTag();
                            listingType = xpp.nextText();
                            Log.d("Listing ", listingType);*/

                        }



                        break;

                    case XmlPullParser.TEXT:
                        text = xpp.getText().trim().toString();
                        System.out.println("Text data: "+text);
                        break;

                    case XmlPullParser.END_TAG:


                        /*else if(tagname.equalsIgnoreCase(TAG_InDate)){
                            InDate = text;
                            text = "";
                            System.out.println("InDate: "+InDate);
                            InDateList.add(InDate);
                        }
                        else if(tagname.equalsIgnoreCase(TAG_InTime)){
                            InTime = text;
                            text = "";
                            System.out.println("InTime: "+InTime);
                            InTimeList.add(InTime);
                        }
                        else if(tagname.equalsIgnoreCase(TAG_OutTime)){
                            OutTime = text;
                            text = "";
                            System.out.println("OutTime: "+OutTime);
                            OutTimeList.add(OutTime);
                        }
                        else if(tagname.equalsIgnoreCase(TAG_HoursWorked)){
                            HoursWorked = text;
                            text = "";
                            System.out.println("HoursWorked: "+HoursWorked);
                            HoursWorkedList.add(HoursWorked);
                        }*/

                        break;

                    default:
                        break;

                }//switch

                eventType = xpp.next();

            }//while()

        }//try
        catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch(IOException e) {
            e.printStackTrace();
        }

    }//getParsingElementsForLogin(xpp);

    Handler handler = new Handler(){

        public void handleMessage(Message msg){
            try {
                if((progressDialog != null) && progressDialog.isShowing() ){
                    progressDialog.dismiss();
                }
            }catch (final Exception e) {
                e.printStackTrace();
            }


            showPayslipStatusDialog(OutTime);
           /* if(InDateList!=null){
                if(InDateList.size()==0){
                    viewAttendanceDetailsListViewID.setVisibility(View.GONE);
                    viewAttendanceDetailsNoDataTextViewID.setVisibility(View.VISIBLE);
                }
                else{
                    viewAttendanceDetailsListViewID.setVisibility(View.VISIBLE);
                    viewAttendanceDetailsNoDataTextViewID.setVisibility(View.GONE);
                    setAttendanceDetails();
                }
            }*/

        }//handleMessage(Message msg)

    };

   /* private void setAttendanceDetails(){
        viewAttendanceDetailsListViewID.setAdapter(new CustomAdapterForAttendanceDetails(ExitActivity.this));
    }*/

    //Show Payslip Status Dialog
    private void showPayslipStatusDialog(String msg){
        //Alert Dialog Builder
        final AlertDialog.Builder aldb = new AlertDialog.Builder(ExitActivity.this);
        aldb.setMessage(msg);
        aldb.setPositiveButton("OK", null);
        aldb.show();
    }


    @Override
    public void onBackPressed()
    {
       // super.onBackPressed();
       ExitActivity.this.finish();
        overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
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


    private void validateData() {

        boolean cancel = false;
        View focusView = null;

        if(TextUtils.isEmpty(reasonforleaveedt.getText().toString()))
        {
            reasonforleaveedt.setError("Required field!");
            focusView = reasonforleaveedt;
            cancel = true;
        }
        if(TextUtils.isEmpty(attendancepagetodateEditTextID1.getText().toString()))
        {
            attendancepagetodateEditTextID1.setError("Required field!");
            focusView = attendancepagetodateEditTextID1;
            cancel = true;
        }
        if(TextUtils.isEmpty(noticeedt.getText().toString()))
        {
            noticeedt.setError("Required field!");
            focusView = noticeedt;
            cancel = true;
        }


        if(cancel){
            focusView.requestFocus();
        }
        else
        {
            getTextValues();
        }

    }//validateData
    private void getTextValues() {

        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        String DATE_INPUT_FORMAT = "yyyy-MM-dd";

        String DATE_OUTPUT_FORMAT = "dd/MMM/yyyy";


            date = attendancepagetodateEditTextID1.getText().toString();


        try {
            mDateTime = DateUtils.formatDateFromDateString(DATE_INPUT_FORMAT, DATE_OUTPUT_FORMAT, attendancepagetodateEditTextID1.getText().toString());

        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {

            date2=sdf.parse(attendancepagetodateEditTextID1.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }




            if (!date.equalsIgnoreCase("") ) {


                    if(CommonUtils.isInternelAvailable(ExitActivity.this)){
                        getAttendanceDetails();
                    }
                    else{
                        Toast.makeText(ExitActivity.this, "No internet connection!", Toast.LENGTH_SHORT).show();
                    }


            } else {
                Toast.makeText(ExitActivity.this, "All Fields Are Mandatory!", Toast.LENGTH_SHORT).show();
            }




    }

    //Show Failure Dialog
    private void showdateDialog(String msg){
        //Alert Dialog Builder
        final AlertDialog.Builder aldb = new AlertDialog.Builder(ExitActivity.this);
        aldb.setTitle("Failed!");
        aldb.setMessage("\n"+msg);
        aldb.setPositiveButton("OK", null);
        aldb.show();
    }
}//ViewAttendanceActivity
