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
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
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
import java.util.GregorianCalendar;

public class AttendanceLeaveFragment1 extends Fragment implements DatePickerDialog.OnDateSetListener{


    private ArrayList<String> leavetype=new ArrayList<String>();
    private  ArrayList<String> leavetypeid=new ArrayList<String>();
    private String TAG_ATTENDANCE_TYPE = "leave"; //"Leave"
    private String date = null,todate=null;
    private String reason = null;

    private EditText attendancepagetodateEditTextID1,attendancepagefromdateEditTextID1;
    private EditText attendancepagereasonEditTextID1,attendancepagetodateEditTextID_birthday;
    private ImageView attendancePageSubmitImageViewID3;
    private Spinner spinner;
    private LinearLayout birthdayleave,normalleave;

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
    private String flag="";

    private String LEAVE_TYPE1 = "", LEAVE_TYPE_NAME = "On Duty"; //casual, sick
    private Date date1,date2;
    private String baseURL;
    private String SOAPRequestXML;
    private HttpResponse httpResponse = null;
    private String TAG_OpeningLeaveBalance = "leavebalance";

    private String TAG_OpeningLeaveBalanceType = "leavetypeid";
    private String OpeningLeaveBalance = "0";
    private String OpeningLeaveBalance1 = "0";
    private String OpeningLeaveBalance7 = "0";
    private String OpeningLeaveBalance3="0";
    private String OpeningLeaveBalance4="0";
    private String OpeningLeaveBalance5="0";
    private String OpeningLeaveBalance6="5";

    private String OpeningLeaveBalancetype = "";
    private String flag_check="1";

    private String TAG_MESSAGE_ID="MessageID";
    private String TAG_MESSAGE_VALUE="";
    private String TAG_DESCRIPTION_ID="Description";
    private String TAG_DESCRIPTION_VALUE="";
    private int STATUS_CODE = 0;
    double totaldays=0;
    double numofday=0.0;
    String mDateTime = null,mDateTime1 = null;

    private String TAG_LoginAuthenticateResult = "Submit_LeaveRequestResult";
    public AttendanceLeaveFragment1() {
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
         View view = inflater.inflate(R.layout.attendance_leave_fragment1, container, false);

        attendancepagetodateEditTextID1 = (EditText)view.findViewById(R.id.attendancepagetodateEditTextID1);
        attendancepagefromdateEditTextID1=(EditText)view.findViewById(R.id.attendancepagefromdateEditTextID1);
        attendancepagereasonEditTextID1 = (EditText)view.findViewById(R.id.attendancepagereasonEditTextID1);
        attendancePageSubmitImageViewID3 = (ImageView)view.findViewById(R.id.attendancePageSubmitImageViewID3);
        attendancepagetodateEditTextID_birthday=(EditText)view.findViewById(R.id.attendancepagetodateEditTextID_birthday);
        normalleave=(LinearLayout)view.findViewById(R.id.normalleave);
        birthdayleave=(LinearLayout)view.findViewById(R.id.birthdayleave);
        spinner = (Spinner)view.findViewById(R.id.spinner);

        return view;
    }

    private void validateleavebalance() {
       /* double annualleave=0;
        if(!OpeningLeaveBalance7.equalsIgnoreCase("0")) {
            annualleave = Double.valueOf(OpeningLeaveBalance7);
        }
        double casualleave=0;
        if(!OpeningLeaveBalance1.equalsIgnoreCase("0")) {
            casualleave = Double.valueOf(OpeningLeaveBalance1);
        }
        double sickleave=0;
       *//* if(!OpeningLeaveBalance2.equalsIgnoreCase("0")) {
            sickleave = Double.valueOf(OpeningLeaveBalance2);
        }*//*
        double maternityleave=0;
        if(!OpeningLeaveBalance3.equalsIgnoreCase("0")) {
            maternityleave = Double.valueOf(OpeningLeaveBalance3);
        }
        double optionalleave=0;
        if(!OpeningLeaveBalance4.equalsIgnoreCase("0")) {
            optionalleave = Double.valueOf(OpeningLeaveBalance4);
        }
        double birthdayleave=0;
        if(!OpeningLeaveBalance5.equalsIgnoreCase("0")) {
            birthdayleave = Double.valueOf(OpeningLeaveBalance5);
        }
        double paternityleave=0;
        if(!OpeningLeaveBalance6.equalsIgnoreCase("0")) {
            paternityleave = Double.valueOf(OpeningLeaveBalance6);
        }

*/
        int numberofdays=getCountOfDays(date,todate);
        numofday=Double.valueOf(numberofdays);
        if(flag_check.equalsIgnoreCase("1")){
            totaldays=numofday;
        }
        else{
            totaldays=numofday/2;
        }

        //sendDataForLeave();
        if(LEAVE_TYPE1.equalsIgnoreCase("1")){

            sendDataForLeave();
            /*if(annualleave>totaldays ){
                //showdateDialog("You have enough leave balance");
                sendDataForLeave();
            }
            else{
                showdateDialog("You don't have enough paid leave balance");
            }*/

        }
        else if(LEAVE_TYPE1.equalsIgnoreCase("10")){
            sendDataForLeave();
           /* if(casualleave>totaldays){
                 //showdateDialog("You have enough leave balance");
                sendDataForLeave();
            }
            else{
                showdateDialog("You don't have enough casual &amp; sick leave balance");
            }*/
/*1 earned, 2 casual 3 sick */
        }
        /*else if(LEAVE_TYPE1.equalsIgnoreCase("3")){
            // showdateDialog("You have enough leave balance");
            //sendDataForLeave();
            if(sickleave>totaldays){
               // showdateDialog("You have enough leave balance");
                sendDataForLeave();
            }
            else{
                showdateDialog("You don't have enough sick leave balance");
            }

        }*/

        else if(LEAVE_TYPE1.equalsIgnoreCase("4")){
            // showdateDialog("You have enough leave balance");
            //sendDataForLeave();

               // showdateDialog("You have enough leave balance");
                sendDataForLeave();

           /* else{
                showdateDialog("You don't have enough maternity leave balance");
            }*/

        }

      /*  else if(LEAVE_TYPE1.equalsIgnoreCase("6")){
            // showdateDialog("You have enough leave balance");
            //sendDataForLeave();
            if(optionalleave>totaldays){
                //showdateDialog("You have enough leave balance");
                sendDataForLeave();
            }
            else{
                showdateDialog("You don't have enough optional leave balance");
            }

        }
*/
        else if(LEAVE_TYPE1.equalsIgnoreCase("7")){
            // showdateDialog("You have enough leave balance");
            //sendDataForLeave();
            String dob=prefs.getString("USERDOB","");
            String day="",month="",daynew="",monthnew="",dayneww="",monthneww="";
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
           Date dobfromdatabase = null,applieddob = null;
            try {
                dobfromdatabase=sdf.parse(dob);
                String [] dateParts = dob.split("-");
                 day = dateParts[2];
                 month = dateParts[1];
                //String year = dateParts[2];

                applieddob=sdf.parse(attendancepagetodateEditTextID_birthday.getText().toString());

                String [] datePartsnew = attendancepagetodateEditTextID_birthday.getText().toString().split("-");

                dayneww = datePartsnew[2];
                monthneww = datePartsnew[1];
               /* if(dayneww.length()==1)
                    daynew="0"+dayneww;
                else*/
                    daynew=dayneww;

                if(monthneww.length()==1)
                    monthnew="0"+monthneww;
                else
                    monthnew=monthneww;
            } catch (ParseException e) {
                showdateDialog("You are applied for wrong birthday date");
                e.printStackTrace();
            }




            if(day.equals(daynew) || month.equals(monthnew)){
                //showdateDialog("You have enough leave balance");
                sendDataForLeave();
            }
            else{
                showdateDialog("You are applied for wrong birthday date");
            }




        }



    }

    public int getCountOfDays(String createdDateString, String expireDateString) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        //SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MMM/yyyy");


        Date createdConvertedDate = null;
        Date expireCovertedDate = null;
        try {
            createdConvertedDate = dateFormat.parse(createdDateString);
            expireCovertedDate = dateFormat.parse(expireDateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        Calendar start = new GregorianCalendar();
        start.setTime(createdConvertedDate);

        Calendar end = new GregorianCalendar();
        end.setTime(expireCovertedDate);

        long diff = end.getTimeInMillis() - start.getTimeInMillis();

        float dayCount = (float) diff / (24 * 60 * 60 * 1000);


        return (int) (dayCount+1);
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

        prefs = getActivity().getSharedPreferences(CommonUtils.PREFERENCE_NAME,getActivity().MODE_PRIVATE);
        prefsEditor = prefs.edit();

        getLeavetype();
        /*getLeaveDetails();*/
        attendancepagetodateEditTextID1.setText(new StringBuilder().append(year).append("-")
                .append(month+1).append("-").append(day));

        attendancepagetodateEditTextID1.setOnClickListener(new EditText.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getActivity().showDialog(DATE_DIALOG_ID);
                flag="fromdate";
                DatePickerDialog dialog = new DatePickerDialog(getActivity(), AttendanceLeaveFragment1.this, year, month, day);
                dialog.show();
            }
        });
        attendancepagetodateEditTextID1.setOnFocusChangeListener(new EditText.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                //getActivity().showDialog(DATE_DIALOG_ID);
                flag="fromdate";
                DatePickerDialog dialog = new DatePickerDialog(getActivity(), AttendanceLeaveFragment1.this, year, month, day);
                dialog.show();
            }
        });


        attendancepagefromdateEditTextID1.setText(new StringBuilder().append(year).append("-")
                .append(month+1).append("-").append(day));

        attendancepagefromdateEditTextID1.setOnClickListener(new EditText.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getActivity().showDialog(DATE_DIALOG_ID);


                flag="todate";
                DatePickerDialog dialog = new DatePickerDialog(getActivity(), AttendanceLeaveFragment1.this, year, month, day);
                dialog.show();
            }
        });
        attendancepagefromdateEditTextID1.setOnFocusChangeListener(new EditText.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                //getActivity().showDialog(DATE_DIALOG_ID);
                flag="todate";
                DatePickerDialog dialog = new DatePickerDialog(getActivity(), AttendanceLeaveFragment1.this, year, month, day);
                dialog.show();
            }
        });



        attendancepagetodateEditTextID_birthday.setText(new StringBuilder().append(year).append("-")
                .append(month+1).append("-").append(day));

        attendancepagetodateEditTextID_birthday.setOnClickListener(new EditText.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getActivity().showDialog(DATE_DIALOG_ID);


                flag="birthdate";
                DatePickerDialog dialog = new DatePickerDialog(getActivity(), AttendanceLeaveFragment1.this, year, month, day);
                dialog.show();
            }
        });
        attendancepagetodateEditTextID_birthday.setOnFocusChangeListener(new EditText.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                //getActivity().showDialog(DATE_DIALOG_ID);
                flag="birthdate";
                DatePickerDialog dialog = new DatePickerDialog(getActivity(), AttendanceLeaveFragment1.this, year, month, day);
                dialog.show();
            }
        });



        attendancePageSubmitImageViewID3.setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(CommonUtils.isInternelAvailable(getActivity())){

                        if(LEAVE_TYPE1.equalsIgnoreCase("7"))
                            validateData1();
                        else
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

        if(flag.equalsIgnoreCase("todate")) {

          try{

              attendancepagefromdateEditTextID1.setText(new StringBuilder().append(year).append("-")
                      .append(monthOfYear + 1).append("-").append(dayOfMonth));

              /*Date date2=sdf.parse(String.valueOf(new StringBuilder().append(year).append("-")
                      .append(monthOfYear + 1).append("-").append(dayOfMonth)));*/



             /* if(date1.equals(date2)){
                  attendancepagefromdateEditTextID1.setText(new StringBuilder().append(year).append("-")
                          .append(monthOfYear + 1).append("-").append(dayOfMonth));
              }
              else if(date1.before(date2)){
                  attendancepagefromdateEditTextID1.setText(new StringBuilder().append(year).append("-")
                          .append(monthOfYear + 1).append("-").append(dayOfMonth));
              }
              else{
                  attendancepagefromdateEditTextID1.setText("");
                  showdateDialog("To Date can not be before From Date");
              }*/

          }
          catch(Exception e){

            }





        }
        else if(flag.equalsIgnoreCase("fromdate")){


            try{

                attendancepagetodateEditTextID1.setText(new StringBuilder().append(year).append("-")
                        .append(monthOfYear + 1).append("-").append(dayOfMonth));

                /*Date date2=sdf.parse(String.valueOf(new StringBuilder().append(year).append("-")
                        .append(monthOfYear + 1).append("-").append(dayOfMonth)));*/

                /*if(date1.equals(date2)){
                    attendancepagetodateEditTextID1.setText(new StringBuilder().append(year).append("-")
                            .append(monthOfYear + 1).append("-").append(dayOfMonth));
                }
                else if(date1.after(date2)){
                    attendancepagetodateEditTextID1.setText(new StringBuilder().append(year).append("-")
                            .append(monthOfYear + 1).append("-").append(dayOfMonth));
                }
                else{
                    attendancepagetodateEditTextID1.setText("");
                    showdateDialog("From Date can not be after To Date");
                }*/

            }
            catch(Exception e){

            }



        }

        else if(flag.equalsIgnoreCase("birthdate")){


            try{

                attendancepagetodateEditTextID_birthday.setText(new StringBuilder().append(year).append("-")
                        .append(monthOfYear + 1).append("-").append(dayOfMonth));


                /*Date date2=sdf.parse(String.valueOf(new StringBuilder().append(year).append("-")
                        .append(monthOfYear + 1).append("-").append(dayOfMonth)));*/

                /*if(date1.equals(date2)){
                    attendancepagetodateEditTextID1.setText(new StringBuilder().append(year).append("-")
                            .append(monthOfYear + 1).append("-").append(dayOfMonth));
                }
                else if(date1.after(date2)){
                    attendancepagetodateEditTextID1.setText(new StringBuilder().append(year).append("-")
                            .append(monthOfYear + 1).append("-").append(dayOfMonth));
                }
                else{
                    attendancepagetodateEditTextID1.setText("");
                    showdateDialog("From Date can not be after To Date");
                }*/

            }
            catch(Exception e){

            }



        }

    }

    //Validate Data locally(Checks whether the fields are empty or not)
    private void validateData() {

        boolean cancel = false;
        View focusView = null;

        if(TextUtils.isEmpty(attendancepagetodateEditTextID1.getText().toString()))
        {
            attendancepagetodateEditTextID1.setError("Required field!");
            focusView = attendancepagetodateEditTextID1;
            cancel = true;
        }
        if(TextUtils.isEmpty(attendancepagefromdateEditTextID1.getText().toString()))
        {
            attendancepagefromdateEditTextID1.setError("Required field!");
            focusView = attendancepagefromdateEditTextID1;
            cancel = true;
        }
        if(TextUtils.isEmpty(attendancepagereasonEditTextID1.getText().toString()))
        {
            attendancepagereasonEditTextID1.setError("Required field!");
            focusView = attendancepagereasonEditTextID1;
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


    private void validateData1() {

        boolean cancel = false;
        View focusView = null;

        if(TextUtils.isEmpty(attendancepagetodateEditTextID_birthday.getText().toString()))
        {
            attendancepagetodateEditTextID_birthday.setError("Required field!");
            focusView = attendancepagetodateEditTextID1;
            cancel = true;
        }

        if(TextUtils.isEmpty(attendancepagereasonEditTextID1.getText().toString()))
        {
            attendancepagereasonEditTextID1.setError("Required field!");
            focusView = attendancepagereasonEditTextID1;
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

   // GetLeaveType

    //Get the values from EditText
    private void getTextValues() {

        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
         String DATE_INPUT_FORMAT = "yyyy-MM-dd";

        String DATE_OUTPUT_FORMAT = "dd/MMM/yyyy";

        if(LEAVE_TYPE1.equalsIgnoreCase("7")){
            date = attendancepagetodateEditTextID_birthday.getText().toString();
            todate = attendancepagetodateEditTextID_birthday.getText().toString();
        }
        else{
            date = attendancepagetodateEditTextID1.getText().toString();
            todate = attendancepagefromdateEditTextID1.getText().toString();
        }

        try {
            mDateTime = DateUtils.formatDateFromDateString(DATE_INPUT_FORMAT, DATE_OUTPUT_FORMAT, attendancepagetodateEditTextID1.getText().toString());
            mDateTime1 = DateUtils.formatDateFromDateString(DATE_INPUT_FORMAT, DATE_OUTPUT_FORMAT, attendancepagefromdateEditTextID1.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        reason = attendancepagereasonEditTextID1.getText().toString();

        try {
            date1=sdf.parse(attendancepagefromdateEditTextID1.getText().toString());
            date2=sdf.parse(attendancepagetodateEditTextID1.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(LEAVE_TYPE1.equalsIgnoreCase("7")){
            validateleavebalance();
        }
else {
            if (!date.equalsIgnoreCase("") && !reason.equalsIgnoreCase("") && !todate.equalsIgnoreCase("")) {

                if (date1.equals(date2)) {
               /* attendancepagefromdateEditTextID1.setText(new StringBuilder().append(year).append("-")
                        .append(monthOfYear + 1).append("-").append(dayOfMonth));*/
                    // sendDataForLeave();
                    validateleavebalance();
                } else if (date1.after(date2)) {
              /*  attendancepagefromdateEditTextID1.setText(new StringBuilder().append(year).append("-")
                        .append(monthOfYear + 1).append("-").append(dayOfMonth));*/
                    // sendDataForLeave();

                    validateleavebalance();
                } else {
                    //attendancepagefromdateEditTextID1.setText("");
                    showdateDialog("To Date cannot be before From Date");
                }


            } else {
                Toast.makeText(getActivity(), "All Fields Are Mandatory!", Toast.LENGTH_SHORT).show();
            }

        }


    }

    public static int getDaysDifference(Date fromDate,Date toDate)
    {
        if(fromDate==null||toDate==null)
            return 0;

        return (int)( (toDate.getTime() - fromDate.getTime()) / (1000 * 60 * 60 * 24));
    }


    private void sendDataForLeave(){

        Constants.ASSOCIATE_ID = prefs.getString("USERISDCODE","");
        Constants.TL_ID = prefs.getString("TLID", "");
        Constants.ATTENDANCE_TYPE = TAG_ATTENDANCE_TYPE;
        Constants.ATTENDANCE_DATE = mDateTime;
        Constants.ATTENDANCE_TO_DATE=mDateTime1;
        Constants.CURRENT_LAT = Constants.UNIV_LAT;
        Constants.CURRENT_LONG = Constants.UNIV_LONG;
        Constants.REASON = reason;
        Constants.REMARKS = "";
        Constants.LEAVE_TYPE = LEAVE_TYPE1;
        String employeename=prefs.getString("USERNAME","");
        LEAVE_TYPE_NAME = "On Duty";
        progressDialog.setMessage("Submitting Your Leave... Please wait!");
        progressDialog.setCancelable(false);
        progressDialog.show();



        baseURL = Constants.base_url_default;
        SOAPRequestXML = Constants.soapRequestHeader+
                "<soapenv:Header/>"
                +"<soapenv:Body>"
                +"<tem:Submit_LeaveRequest>"
                +"<tem:emp_code>"+Constants.ASSOCIATE_ID+"</tem:emp_code>"
                +"<tem:emp_name>"+employeename+"</tem:emp_name>"
                +"<tem:leave_type_id>"+Constants.LEAVE_TYPE+"</tem:leave_type_id>"
                +"<tem:leave_type>"+LEAVE_TYPE_NAME+"</tem:leave_type>"
                +"<tem:from_date>"+Constants.ATTENDANCE_DATE+"</tem:from_date>"
                +"<tem:to_date>"+Constants.ATTENDANCE_TO_DATE+"</tem:to_date>"
                +"<tem:days>"+numofday+"</tem:days>"
                +"<tem:reason>"+Constants.REASON+"</tem:reason>"
                +"<tem:contact_no>"+""+"</tem:contact_no>"
                +"</tem:Submit_LeaveRequest>"
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

                    System.out.print("Server Response = "+Response);
                    StatusLine status = httpResponse.getStatusLine();
                    STATUS_CODE = status.getStatusCode();
                    System.out.println("Server status code = "+STATUS_CODE);
                    System.out.println("Server httpResponse.getStatusLine() = "+httpResponse.getStatusLine().toString());
                    System.out.println("Server Staus = "+httpResponse.getEntity().toString());

                    getParsingElementsForLoginDetails(xpp);

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

    //getParsingElementsForLoginDetails(xpp);
    public void getParsingElementsForLoginDetails(XmlPullParser xpp){
        String text = "";
        try {
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagname = xpp.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG:

                        tagname=xpp.getName();
                        if(tagname.equalsIgnoreCase(TAG_LoginAuthenticateResult)) {

           /*to handle nested tags.
           "xpp.nextTag()" goes to the next starting tag  immediately following "ItemArray",
           and "itemID = xpp.nextText()" assigns the text within that tag
            to the string itemID*/

                            xpp.nextTag();
                            STATUS = xpp.nextText();
                            Log.d("Listing ", STATUS);


                            xpp.nextTag();
                            TAG_MESSAGE_VALUE = xpp.nextText();
                            Log.d("Listing ", TAG_MESSAGE_VALUE);



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

                       /* if(tagname.equalsIgnoreCase(TAG_LoginAuthenticateResult)){
                            STATUS = text;
                            text = "";
                            System.out.println("STATUS: "+STATUS);
                        }
                        if(tagname.equalsIgnoreCase(TAG_MESSAGE_ID)){
                            TAG_MESSAGE_VALUE = text;
                            text = "";
                            System.out.println("STATUS: "+TAG_MESSAGE_VALUE);
                            // Toast.makeText(LoginActivity.this,"Successful",Toast.LENGTH_SHORT).show();
                        }
                        if(tagname.equalsIgnoreCase(TAG_DESCRIPTION_ID)){
                            TAG_DESCRIPTION_VALUE = text;
                            text = "";
                            System.out.println("STATUS: "+TAG_MESSAGE_VALUE);
                            // Toast.makeText(LoginActivity.this,"Successful",Toast.LENGTH_SHORT).show();
                        }*/

                       /* else{
                            Toast.makeText(LoginActivity.this,"Unsuccessful",Toast.LENGTH_SHORT).show();
                        }*/
                        /*else if(tagname.equalsIgnoreCase(TAG_EmpName)){
                            EmpName = text;
                            text = "";
                            System.out.println("EmpName: "+EmpName);
                        }
                        else if(tagname.equalsIgnoreCase(TAG_FatherName)){
                            FatherName = text;
                            text = "";
                            System.out.println("FatherName: "+FatherName);
                        }
                        else if(tagname.equalsIgnoreCase(TAG_DOB)){
                            DOB = text;
                            text = "";
                            System.out.println("DOB: "+DOB);
                        }
                        else if(tagname.equalsIgnoreCase(TAG_DOJ)){
                            DOJ = text;
                            text = "";
                            System.out.println("DOJ: "+DOJ);
                        }
                        else if(tagname.equalsIgnoreCase(TAG_PFNumber)){
                            PFNumber = text;
                            text = "";
                            System.out.println("PFNumber: "+PFNumber);
                        }
                        else if(tagname.equalsIgnoreCase(TAG_PFUAN)){
                            PFUAN = text;
                            text = "";
                            System.out.println("PFUAN: "+PFUAN);
                        }
                        else if(tagname.equalsIgnoreCase(TAG_ESICNumber)){
                            ESICNumber = text;
                            text = "";
                            System.out.println("ESICNumber: "+ESICNumber);
                        }
                        else if(tagname.equalsIgnoreCase(TAG_PAN)){
                            PAN = text;
                            text = "";
                            System.out.println("PAN: "+PAN);
                        }
                        else if(tagname.equalsIgnoreCase(TAG_ClientName)){
                            ClientName = text;
                            text = "";
                            System.out.println("ClientName: "+ClientName);
                        }
                        else if(tagname.equalsIgnoreCase(TAG_MailAddress)){
                            MailAddress = text;
                            text = "";
                            System.out.println("MailAddress: "+MailAddress);
                        }
                        else if(tagname.equalsIgnoreCase(TAG_PermAddress)){
                            PermAddress = text;
                            text = "";
                            System.out.println("PermAddress: "+PermAddress);
                        }
                        else if(tagname.equalsIgnoreCase(TAG_Mobile1)){
                            Mobile1 = text;
                            text = "";
                            System.out.println("Mobile1: "+Mobile1);
                        }
                        else if(tagname.equalsIgnoreCase(TAG_Mobile2)){
                            Mobile2 = text;
                            text = "";
                            System.out.println("Mobile2: "+Mobile2);
                        }
                        else if(tagname.equalsIgnoreCase(TAG_EmployeeStatus)){
                            EmployeeStatus = text;
                            text = "";
                            System.out.println("EmployeeStatus: "+EmployeeStatus);
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
        } catch (IOException e) {
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

            //Success
            if(STATUS_CODE==200){
                //Login success
                if(!STATUS.equalsIgnoreCase("")){
                    //Toast.makeText(LoginActivity.this, MESSAGE, Toast.LENGTH_SHORT).show();

                    if(STATUS.equalsIgnoreCase("NA")){
                        Constants.ASSOCIATE_ID = "";
                        Constants.TL_ID = "";
                        Constants.OUTLET_ID = "";
                        Constants.ATTENDANCE_TYPE = "";
                        Constants.ATTENDANCE_IMAGE = "";
                        //Constants.CURRENT_LAT = "0.0";
                        //Constants.CURRENT_LONG = "0.0";
                        Constants.ATTENDANCE_DATE = "0000-00-00";
                        Constants.REASON = "";
                        Constants.LEAVE_TYPE = "";
                        Constants.DISTANCE = "0";
                       // TAG_MESSAGE_VALUE = "Please check leave dates (holiday / Sun)";
                        showFailureDialog();
                    }
                  /*  else  if(STATUS.equalsIgnoreCase("3")){
                        Constants.ASSOCIATE_ID = "";
                        Constants.TL_ID = "";
                        Constants.OUTLET_ID = "";
                        Constants.ATTENDANCE_TYPE = "";
                        Constants.ATTENDANCE_IMAGE = "";
                        //Constants.CURRENT_LAT = "0.0";
                        //Constants.CURRENT_LONG = "0.0";
                        Constants.ATTENDANCE_DATE = "0000-00-00";
                        Constants.REASON = "";
                        Constants.LEAVE_TYPE = "";
                        Constants.DISTANCE = "0";
                        TAG_MESSAGE_VALUE = "Leave is already applied for mentioned period / dates";
                        showSuccessDialog();
                    }*/
                    else {

                        Constants.ASSOCIATE_ID = "";
                        Constants.TL_ID = "";
                        Constants.OUTLET_ID = "";
                        Constants.ATTENDANCE_TYPE = "";
                        Constants.ATTENDANCE_IMAGE = "";
                        //Constants.CURRENT_LAT = "0.0";
                        //Constants.CURRENT_LONG = "0.0";
                        Constants.ATTENDANCE_DATE = "0000-00-00";
                        Constants.REASON = "";
                        Constants.LEAVE_TYPE = "";
                        Constants.DISTANCE = "0";
                       // TAG_MESSAGE_VALUE = "Leave applied successfully";
                        showSuccessDialog();
                    }
                }

               /* else{
                    TAG_MESSAGE_VALUE="Leave applied failed";
                    showFailureDialog();
                }*/

                //Login failed
                /*if(STATUS.equalsIgnoreCase("0")){
                    showFailureDialog();
                }*/
            }

            //Login failed
            if(STATUS_CODE!=200){
                //Toast.makeText(LoginActivity.this, MESSAGE, Toast.LENGTH_SHORT).show();
                showFailureDialog();

            }

        }//handleMessage(Message msg)

    };




   /* private void sendDataForLeave(){

        Constants.ASSOCIATE_ID = prefs.getString("USERISDCODE","");
        Constants.TL_ID = prefs.getString("TLID", "");
        Constants.ATTENDANCE_TYPE = TAG_ATTENDANCE_TYPE;
        Constants.ATTENDANCE_DATE = date;
        Constants.ATTENDANCE_TO_DATE=todate;
        Constants.CURRENT_LAT = Constants.UNIV_LAT;
        Constants.CURRENT_LONG = Constants.UNIV_LONG;
        Constants.REASON = reason;
        Constants.REMARKS = "";
        Constants.LEAVE_TYPE = LEAVE_TYPE1;
        Constants.ATTENDANCE_IMAGE = "";
        Constants.od_from_time="";
        Constants.od_to_time="";
        Constants.client_name="";
        Constants.client_address="";
        //progressDialog.setTitle("Attendance");
        progressDialog.setMessage("Submitting Your Leave... Please wait!");
        progressDialog.setCancelable(false);
        progressDialog.show();
        client = new RestFullClient(Constants.BASE_URL+Constants.ATTENDANCE_RELATIVE_URI);

        //client.AddParam("associate_code", Constants.ASSOCIATE_ID);
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

        client.AddParam("attendance_to_date", Constants.ATTENDANCE_TO_DATE);

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
                    //Constants.CURRENT_LAT = "0.0";
                    //Constants.CURRENT_LONG = "0.0";
                    Constants.ATTENDANCE_DATE = "0000-00-00";
                    Constants.REASON = "";
                    Constants.LEAVE_TYPE = "";
                    Constants.DISTANCE = "0";

                    showSuccessDialog();

                   *//* getActivity().finish();
                    getActivity().overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);*//*

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

    };*/

    //Show Success Dialog
    private void showSuccessDialog(){
        //Alert Dialog Builder
        final AlertDialog.Builder aldb = new AlertDialog.Builder(getActivity());
        aldb.setTitle("Success!");
        aldb.setMessage("\n"+TAG_MESSAGE_VALUE);
        aldb.setCancelable(false);
        aldb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                clearData();
            }
        });
        aldb.show();

       /* final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // dialog.setTitle("Details");
        dialog.setCancelable(false);

        dialog.setContentView(R.layout.details_list_layout_d);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final Button close=dialog.findViewById(R.id.close);
        final TextView txt=dialog.findViewById(R.id.textmsg);
        final TextView title=dialog.findViewById(R.id.txttitle);
        title.setText("Success!");
        txt.setText(MESSAGE);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();*/
    }

    //Show Failure Dialog
    private void showFailureDialog(){
        //Alert Dialog Builder
        final AlertDialog.Builder aldb = new AlertDialog.Builder(getActivity());
        aldb.setTitle("Failed!");
        aldb.setMessage("\n"+TAG_MESSAGE_VALUE);
        aldb.setPositiveButton("OK", null);
        aldb.show();
        /*final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // dialog.setTitle("Details");
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.details_list_layout_d);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final Button close=dialog.findViewById(R.id.close);
        final TextView txt=dialog.findViewById(R.id.textmsg);
        final TextView title=dialog.findViewById(R.id.txttitle);
        title.setText("Failed!");
        txt.setText(MESSAGE);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();*/

    }
    //Show Failure Dialog
    private void showdateDialog(String msg){
        //Alert Dialog Builder
        final AlertDialog.Builder aldb = new AlertDialog.Builder(getActivity());
        aldb.setTitle("Failed!");
        aldb.setMessage("\n"+msg);
        aldb.setPositiveButton("OK", null);
        aldb.show();
    }

    private void clearData(){

        attendancepagetodateEditTextID1.setText("");
        attendancepagetodateEditTextID1.setText(new StringBuilder().append(year).append("-")
                .append(month+1).append("-").append(day));

        attendancepagereasonEditTextID1.setText("");
        attendancepagefromdateEditTextID1.setText("");
        attendancepagefromdateEditTextID1.setText(new StringBuilder().append(year).append("-")
                .append(month+1).append("-").append(day));

        ////Added Later
        spinner.setSelection(0);
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

    private void getLeaveDetails(){
        progressDialog.setMessage("Requesting... Please wait!");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String EmpID = prefs.getString("USERISDCODE","");
        System.out.println("EmpID: "+EmpID);

        baseURL = Constants.base_url_default;
        SOAPRequestXML = Constants.soapRequestHeader+
                "<soapenv:Header/>"
                +"<soapenv:Body>"
                +"<tem:GetMyLeaveBalance>"
                //+"<tem:emp_code>"+100172254+"</tem:emp_code>"
                +"<tem:emp_code>"+EmpID+"</tem:emp_code>"
                +"</tem:GetMyLeaveBalance>"
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

                    getParsingElementsForEmployeeDetails(xpp);

                } catch (HttpResponseException e) {
                    Log.i("httpResponse Error = ", e.getMessage());
                }
                catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                handler1.sendEmptyMessage(0);

            }

        }).start();

    }

    //getParsingElementsForEmployeeDetails(xpp);
    public void getParsingElementsForEmployeeDetails(XmlPullParser xpp){
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
                        System.out.println("Text data: "+text);
                        break;

                    case XmlPullParser.END_TAG:


                        if(tagname.equalsIgnoreCase("PLAvailed")){
                            OpeningLeaveBalance7 = text;
                            text = "";
                            System.out.println("OpeningLeaveBalance: "+OpeningLeaveBalance7);
                        }
                        if(tagname.equalsIgnoreCase("PLClosing")){
                            OpeningLeaveBalance1 = text;
                            text = "";
                            System.out.println("OpeningLeaveBalance: "+OpeningLeaveBalance1);
                        }

                       /* if(tagname.equalsIgnoreCase(TAG_OpeningLeaveBalanceType)){
                            OpeningLeaveBalancetype = text;
                            text = "";
                            System.out.println("OpeningLeaveBalance: "+OpeningLeaveBalancetype);
                        }
                        if(tagname.equalsIgnoreCase(TAG_OpeningLeaveBalance)){
                            if(OpeningLeaveBalancetype.equalsIgnoreCase("1"))
                                OpeningLeaveBalance7 = text;
                            else if(OpeningLeaveBalancetype.equalsIgnoreCase("10"))
                                OpeningLeaveBalance1 = text;
                           *//* else if(OpeningLeaveBalancetype.equalsIgnoreCase("3"))
                                OpeningLeaveBalance2 = text;*//*
                            else if(OpeningLeaveBalancetype.equalsIgnoreCase("4"))
                                OpeningLeaveBalance3 = text;
                           *//* else if(OpeningLeaveBalancetype.equalsIgnoreCase("6"))
                                OpeningLeaveBalance4 = text;
                            else if(OpeningLeaveBalancetype.equalsIgnoreCase("7"))
                                OpeningLeaveBalance5 = text;
                            else if(OpeningLeaveBalancetype.equalsIgnoreCase("8"))
                                OpeningLeaveBalance6 = text;*//*

                            text = "";
                            System.out.println("OpeningLeaveBalance: "+OpeningLeaveBalance);
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
        } catch (IOException e) {
            e.printStackTrace();
        }

    }//getParsingElementsForLogin(xpp);

    Handler handler1 = new Handler(){

        public void handleMessage(Message msg){
            try {
                if((progressDialog != null) && progressDialog.isShowing() ){
                    progressDialog.dismiss();
                }
            }catch (final Exception e) {
                e.printStackTrace();
            }

            setLeaveDetails();
        }//handleMessage(Message msg)

    };

    private void setLeaveDetails() {


    }




    private void getLeavetype(){
        leavetype=new ArrayList<String>();
        progressDialog.setMessage("Requesting... Please wait!");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String clientid = Constants.CLIENT_ID;


        baseURL = Constants.base_url_default;
        SOAPRequestXML = Constants.soapRequestHeader+
                "<soapenv:Header/>"
                +"<soapenv:Body>"
                +"<tem:GetLeaveType>"
                //+"<tem:emp_code>"+100172254+"</tem:emp_code>"
                +"<tem:client_id>"+clientid+"</tem:client_id>"
                +"</tem:GetLeaveType>"
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

                    getParsingElementsForLeaveDetails(xpp);

                } catch (HttpResponseException e) {
                    Log.i("httpResponse Error = ", e.getMessage());
                }
                catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                handler2.sendEmptyMessage(0);

            }

        }).start();

    }

    //getParsingElementsForEmployeeDetails(xpp);
    public void getParsingElementsForLeaveDetails(XmlPullParser xpp){
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
                        System.out.println("Text data: "+text);
                        break;

                    case XmlPullParser.END_TAG:

                        if(tagname.equalsIgnoreCase("LeaveTypeID")){
                            OpeningLeaveBalancetype = text;
                            text = "";
                            System.out.println("OpeningLeaveBalance: "+OpeningLeaveBalancetype);
                            if(OpeningLeaveBalancetype.equalsIgnoreCase("10"))
                            leavetypeid.add(OpeningLeaveBalancetype);
                        }
                        if(tagname.equalsIgnoreCase("LeaveType")){

                                OpeningLeaveBalance = text;

                            text = "";
                            System.out.println("OpeningLeaveBalance: "+OpeningLeaveBalance);
                            if(OpeningLeaveBalance.equalsIgnoreCase("On Duty"))
                            leavetype.add(OpeningLeaveBalance);
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

    Handler handler2 = new Handler(){

        public void handleMessage(Message msg){
            try {
                if((progressDialog != null) && progressDialog.isShowing() ){
                    progressDialog.dismiss();
                }
            }catch (final Exception e) {
                e.printStackTrace();
            }

            setLeaveType();
        }//handleMessage(Message msg)

    };

    private void setLeaveType() {
        if (leavetype != null) {

           /* if(prefs.getString("USERGENDER","").equalsIgnoreCase("Male")){
                leavetype.remove(3);
                leavetypeid.remove(3);

            }

            else{
                leavetype.remove(5);
                leavetypeid.remove(5);
            }*/
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, leavetype);

            spinner.setAdapter(adapter);

            // Spinner click listener
            ////Added Later
            spinner.setSelection(0);
            ////

            spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){

                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    LEAVE_TYPE1=leavetypeid.get(position);
                    if(parent!=null){
                        //((TextView) view).setTextColor(Color.parseColor("#e1e1e1"));
                        View v = spinner.getSelectedView();
                        if(v!=null){
                            ((TextView)v).setTextColor(Color.parseColor("#e1e1e1"));
                        }

                        LEAVE_TYPE_NAME=parent.getSelectedItem().toString();
                    }

                    if(LEAVE_TYPE1.equalsIgnoreCase("7")){
                        birthdayleave.setVisibility(View.VISIBLE);
                        normalleave.setVisibility(View.GONE);
                    }
                    else{
                        birthdayleave.setVisibility(View.GONE);
                        normalleave.setVisibility(View.VISIBLE);
                    }


                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

        }


        getLeaveDetails();
    }


}
