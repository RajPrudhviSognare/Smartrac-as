package com.experis.smartrac.as;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
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
import java.util.List;

public class ViewAttendanceActivity1 extends AppCompatActivity {

    private SharedPreferences prefs;
    private SharedPreferences.Editor prefsEditor;
    private ProgressDialog progressDialog;

    private ImageView viewAttendancetopbarbackImageViewID,viewAttendancetopbarusericonImageViewID;

    private ListView viewAttendanceDetailsListViewID;
    private TextView viewAttendanceDetailsNoDataTextViewID;

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
    private String HoursWorked = "",HoursWorked1 = "",HoursWorked2 = "",HoursWorked3 = "";

    private List<String> InDateList = null;
    private List<String> InTimeList = null;
    private List<String> OutTimeList = null;
    private List<String> HoursWorkedList = null;
    private List<String> TotalList = null;
    private List<String> PLList = null;
    private List<String> LWPList = null;

    private ImageView attendancePageSubmitImageViewID3;
    private EditText attendancepagetodateEditTextID1,attendancepagefromdateEditTextID1;
    private Calendar calendar;
    private int year, month, day;
    private String flag="";
    private String date = null,todate=null;
    String mDateTime = null,mDateTime1 = null;
    private Date date1,date2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_attendance1);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.view_attendance_topbar1);

        initAllViews();


        attendancepagetodateEditTextID1.setText(new StringBuilder().append(year).append("-")
                .append(month+1).append("-").append(day));

        attendancepagetodateEditTextID1.setOnClickListener(new EditText.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getActivity().showDialog(DATE_DIALOG_ID);
                flag="fromdate";
              /*  DatePickerDialog dialog = new DatePickerDialog(ViewAttendanceActivity1.this, AttendanceLeaveFragment1.this, year, month, day);
                dialog.show();*/
                setDate(flag);
            }
        });
        attendancepagetodateEditTextID1.setOnFocusChangeListener(new EditText.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                //getActivity().showDialog(DATE_DIALOG_ID);
                flag="fromdate";
                setDate(flag);
               /* DatePickerDialog dialog = new DatePickerDialog(ViewAttendanceActivity1.this, AttendanceLeaveFragment1.this, year, month, day);
                dialog.show();*/
            }
        });


        attendancepagefromdateEditTextID1.setText(new StringBuilder().append(year).append("-")
                .append(month+1).append("-").append(day));

        attendancepagefromdateEditTextID1.setOnClickListener(new EditText.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getActivity().showDialog(DATE_DIALOG_ID);


                flag="todate";
              /*  DatePickerDialog dialog = new DatePickerDialog(ViewAttendanceActivity1.this, AttendanceLeaveFragment1.this, year, month, day);
                dialog.show();*/
                setDate1(flag);
            }
        });
        attendancepagefromdateEditTextID1.setOnFocusChangeListener(new EditText.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                //getActivity().showDialog(DATE_DIALOG_ID);
                flag="todate";
                setDate1(flag);
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
                Intent i=new Intent(ViewAttendanceActivity1.this,ViewAttendanceActivity1.class);
                startActivity(i);
            }
        });

        attendancePageSubmitImageViewID3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(CommonUtils.isInternelAvailable(ViewAttendanceActivity1.this)){

                            validateData();
                    }
                    else{
                        Toast.makeText(ViewAttendanceActivity1.this, "No internet connection!", Toast.LENGTH_SHORT).show();
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
            return new DatePickerDialog(this,
                    myDateListener, year, month, day);
        }
        if (id == 998) {
            return new DatePickerDialog(this,
                    myDateListener, year, month, day);
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
        if(flag.equalsIgnoreCase("todate")) {

            try{

                attendancepagefromdateEditTextID1.setText(new StringBuilder().append(year).append("-")
                        .append(monthOfYear + 1).append("-").append(dayOfMonth));


            }
            catch(Exception e){

            }





        }
        else if(flag.equalsIgnoreCase("fromdate")){


            try{

                attendancepagetodateEditTextID1.setText(new StringBuilder().append(year).append("-")
                        .append(monthOfYear + 1).append("-").append(dayOfMonth));


            }
            catch(Exception e){

            }



        }

    }

    public void setDate(String view) {
        showDialog(999);

    }
    public void setDate1(String view) {
        showDialog(998);

    }

    private void initAllViews(){

        //shared preference
        prefs = getSharedPreferences(CommonUtils.PREFERENCE_NAME,MODE_PRIVATE);
        prefsEditor = prefs.edit();

        viewAttendancetopbarbackImageViewID = (ImageView)findViewById(R.id.viewAttendancetopbarbackImageViewID);
        viewAttendancetopbarusericonImageViewID=(ImageView)findViewById(R.id.viewAttendancetopbarusericonImageViewID);
        viewAttendanceDetailsListViewID = (ListView)findViewById(R.id.viewAttendanceDetailsListViewID);
        viewAttendanceDetailsNoDataTextViewID = (TextView)findViewById(R.id.viewAttendanceDetailsNoDataTextViewID);
        attendancePageSubmitImageViewID3=(ImageView)findViewById(R.id.attendancePageSubmitImageViewID3);
        attendancepagetodateEditTextID1 = (EditText)findViewById(R.id.attendancepagetodateEditTextID1);
        attendancepagefromdateEditTextID1=(EditText)findViewById(R.id.attendancepagefromdateEditTextID1);

        InDateList = new ArrayList<String>(0);
        InTimeList = new ArrayList<String>(0);
        OutTimeList = new ArrayList<String>(0);
        HoursWorkedList = new ArrayList<String>(0);
        TotalList=new ArrayList<String>(0);
        PLList=new ArrayList<String>(0);
        LWPList=new ArrayList<String>(0);

        //Progress Dialog
        progressDialog = new ProgressDialog(ViewAttendanceActivity1.this);
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

        baseURL = Constants.base_url_default;
        SOAPRequestXML = Constants.soapRequestHeader+
                "<soapenv:Header/>"
                +"<soapenv:Body>"
                +"<tem:GetLeaveReport>"
                //+"<tem:emp_code>"+100400985+"</tem:emp_code>"
                +"<tem:emp_code>"+EmpID+"</tem:emp_code>"
                +"<tem:from_date>"+mDateTime+"</tem:from_date>"
                +"<tem:to_date>"+mDateTime1+"</tem:to_date>"
                +"</tem:GetLeaveReport>"
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
                        break;

                    case XmlPullParser.TEXT:
                        text = xpp.getText().trim().toString();
                        System.out.println("Text data: "+text);
                        break;

                    case XmlPullParser.END_TAG:

                        if(tagname.equalsIgnoreCase(TAG_EmpCode)){
                            EmpCode = text;
                            text = "";
                            System.out.println("EmpCode: "+EmpCode);
                        }
                        else if(tagname.equalsIgnoreCase(TAG_InDate)){
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

                        else if(tagname.equalsIgnoreCase("TotalDays")){
                            HoursWorked1 = text;
                            text = "";
                            System.out.println("HoursWorked: "+HoursWorked1);
                            TotalList.add(HoursWorked1);
                        }
                        else if(tagname.equalsIgnoreCase(TAG_HoursWorked)){
                            HoursWorked = text;
                            text = "";
                            System.out.println("HoursWorked: "+HoursWorked);
                            HoursWorkedList.add(HoursWorked);
                        }
                        else if(tagname.equalsIgnoreCase("PL_Count")){
                            HoursWorked2 = text;
                            text = "";
                            System.out.println("HoursWorked: "+HoursWorked2);
                            PLList.add(HoursWorked2);
                        }
                        else if(tagname.equalsIgnoreCase("LWP_Count")){
                            HoursWorked3 = text;
                            text = "";
                            System.out.println("HoursWorked: "+HoursWorked3);
                            LWPList.add(HoursWorked3);
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
            System.out.println("InDateList: "+InDateList.toString());
            System.out.println("InTimeList: "+InTimeList.toString());
            System.out.println("OutTimeList: "+OutTimeList.toString());
            System.out.println("HoursWorkedList: "+HoursWorkedList.toString());

            if(InDateList!=null){
                if(InDateList.size()==0){
                    viewAttendanceDetailsListViewID.setVisibility(View.GONE);
                    viewAttendanceDetailsNoDataTextViewID.setVisibility(View.VISIBLE);
                }
                else{
                    viewAttendanceDetailsListViewID.setVisibility(View.VISIBLE);
                    viewAttendanceDetailsNoDataTextViewID.setVisibility(View.GONE);
                    setAttendanceDetails();
                }
            }

        }//handleMessage(Message msg)

    };

    private void setAttendanceDetails(){
        viewAttendanceDetailsListViewID.setAdapter(new CustomAdapterForAttendanceDetails(ViewAttendanceActivity1.this));
    }



    /*
    * CustomAdapterForAttendanceDetails
    */
    public class CustomAdapterForAttendanceDetails extends BaseAdapter {

        public Context cntx;

        public CustomAdapterForAttendanceDetails(Context context){
            cntx = context;
        }

        @Override
        public int getCount() {


            if(InDateList!=null) {
                if(InDateList.size()!=0) {

                   /* int[] numbers = {InDateList.size(),InTimeList.size(),OutTimeList.size(),HoursWorkedList.size()};
                    int smallest = Integer.MAX_VALUE;
                    for(int i =0;i<numbers.length;i++) {
                        if(smallest > numbers[i]) {
                            smallest = numbers[i];
                        }
                    }
                    System.out.println("Smallest size is : " +smallest);
                    return smallest;*/

                    return InDateList.size();

                }//if
                else{
                    return 0;
                }
            }
            else{
                return 0;
            }
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
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
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub

            View view;

            if(convertView==null){
                LayoutInflater inflater = (LayoutInflater)cntx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.customlayout_for_viewattendance1, null);
            }
            else{
                view = convertView;
            }

            final TextView date = (TextView)view.findViewById(R.id.customlayout_IndateTextViewID);
            final TextView intime = (TextView)view.findViewById(R.id.customlayout_InTimeTextViewID);
            final TextView outtime = (TextView)view.findViewById(R.id.customlayout_OuttimeTextViewID);
            final TextView hoursworked = (TextView)view.findViewById(R.id.customlayout_HoursWorkedTextViewID);
            final TextView PL = (TextView)view.findViewById(R.id.customlayout_PL);
            final TextView LWP = (TextView)view.findViewById(R.id.customlayout_LWP);

            date.setText(InDateList.get(position));
            intime.setText(InTimeList.get(position) +"\nto\n"+OutTimeList.get(position));
            outtime.setText(TotalList.get(position));
            hoursworked.setText(HoursWorkedList.get(position));
            PL.setText(PLList.get(position));
            LWP.setText(LWPList.get(position));

            return view;
        }

    }//CustomAdapterForAttendanceDetails Class

    @Override
    public void onBackPressed()
    {
       // super.onBackPressed();
       ViewAttendanceActivity1.this.finish();
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
            todate = attendancepagefromdateEditTextID1.getText().toString();

        try {
            mDateTime = DateUtils.formatDateFromDateString(DATE_INPUT_FORMAT, DATE_OUTPUT_FORMAT, attendancepagetodateEditTextID1.getText().toString());
            mDateTime1 = DateUtils.formatDateFromDateString(DATE_INPUT_FORMAT, DATE_OUTPUT_FORMAT, attendancepagefromdateEditTextID1.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            date1=sdf.parse(attendancepagefromdateEditTextID1.getText().toString());
            date2=sdf.parse(attendancepagetodateEditTextID1.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }




            if (!date.equalsIgnoreCase("") && !todate.equalsIgnoreCase("")) {

                if (date1.equals(date2)) {
               /* attendancepagefromdateEditTextID1.setText(new StringBuilder().append(year).append("-")
                        .append(monthOfYear + 1).append("-").append(dayOfMonth));*/
                    // sendDataForLeave();
                    if(CommonUtils.isInternelAvailable(ViewAttendanceActivity1.this)){
                        getAttendanceDetails();
                    }
                    else{
                        Toast.makeText(ViewAttendanceActivity1.this, "No internet connection!", Toast.LENGTH_SHORT).show();
                    }
                } else if (date1.after(date2)) {
              /*  attendancepagefromdateEditTextID1.setText(new StringBuilder().append(year).append("-")
                        .append(monthOfYear + 1).append("-").append(dayOfMonth));*/
                    // sendDataForLeave();

                    if(CommonUtils.isInternelAvailable(ViewAttendanceActivity1.this)){
                        getAttendanceDetails();
                    }
                    else{
                        Toast.makeText(ViewAttendanceActivity1.this, "No internet connection!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    //attendancepagefromdateEditTextID1.setText("");
                    showdateDialog("To Date cannot be before From Date");
                }


            } else {
                Toast.makeText(ViewAttendanceActivity1.this, "All Fields Are Mandatory!", Toast.LENGTH_SHORT).show();
            }




    }

    //Show Failure Dialog
    private void showdateDialog(String msg){
        //Alert Dialog Builder
        final AlertDialog.Builder aldb = new AlertDialog.Builder(ViewAttendanceActivity1.this);
        aldb.setTitle("Failed!");
        aldb.setMessage("\n"+msg);
        aldb.setPositiveButton("OK", null);
        aldb.show();
    }
}//ViewAttendanceActivity
