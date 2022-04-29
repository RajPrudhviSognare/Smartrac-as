/*


import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class ViewAttendanceActivity extends AppCompatActivity {

    private SharedPreferences prefs;
    private SharedPreferences.Editor prefsEditor;
    private ProgressDialog progressDialog;

    private ImageView viewAttendancetopbarbackImageViewID;

    private ListView viewAttendanceDetailsListViewID;
    private TextView viewAttendanceDetailsNoDataTextViewID;

    //AMARTYA
    private ListView associatereportsAttendanceDetailsListViewID;
    private TextView associatereportsNoDataTextViewID;

    private View viewTypeViewAssociatereportsID10023;
    private LinearLayout listview_header_associatereports_MonthlyDetails_for_table_LinearLayoutID;
    private ListView associatereportsAttendanceDetailsListViewID1;
    private View viewTypeViewAssociatereportsID10024;
    //END

    private String baseURL;
    private String SOAPRequestXML;
    private HttpResponse httpResponse = null;

    private RestFullClient client;

    */
/*private String TAG_EmpCode = "EmpCode";
    private String EmpCode = "";

    private String TAG_InDate = "InDate";
    private String InDate = "";

    private String TAG_InTime = "InTime";
    private String InTime = "";

    private String TAG_OutTime = "OutTime";
    private String OutTime = "";

    private String TAG_HoursWorked = "HoursWorked";
    private String HoursWorked = "";*//*

    private String TAG_STATUS = "status";
    private String TAG_ERROR = "error";
    private String TAG_MESSAGE = "message";

    private String STATUS = "";
    private String ERROR = "";
    private String MESSAGE = "";

    private String TAG_JOBJECT_DATA = "data";
    private JSONObject JOBJECT_DATA = null;

    private String TAG_JOBJECT_CURRENT_MONTH = "current_month_result";
    private JSONObject JOBJECT_CURRENT_MONTH = null;
    private String TAG_JOBJECT_PREVIOUS_MONTH = "previous_month_result";
    private JSONObject JOBJECT_PREVIOUS_MONTH = null;

    private String TAG_CURRENT_MONTH = "current_month";
    private String TAG_PREVIOUS_MONTH = "previous_month";
    private String TAG_IN = "in";
    private String TAG_LEAVE = "leave";
    private String TAG_MEETING = "meeting";
    private String TAG_OUT = "out";
    private String TAG_WEEKLY_OFF = "weekly_off";
    private String TAG_work_from_home = "work_from_home";
    private String TAG_work_from_client_office = "work_from_client_office";

    private String CURRENT_MONTH = "";
    private String PREVIOUS_MONTH = "";

    private String IN = "0";
    private String LEAVE = "0";
    private String MEETING = "0";
    private String OUT = "0";
    private String WEEKLY_OFF = "0";
    private String WFH = "0";
    private String WFCO = "0";

    private String IN1 = "0";
    private String LEAVE1 = "0";
    private String MEETING1 = "0";
    private String OUT1 = "0";
    private String WEEKLY_OFF1 = "0";
    private String WFH1 = "0";
    private String WFCO1 = "0";

    private String TAG_JARRAY_MONTHLYATTENDANCEDATALIST = "details";

    private String TAG_ATTENDANCE_DATE_SUB = "attendance_date_sub";
    private String TAG_ATTENDANCE_TYPE = "attendance_type";
    private String TAG_ATTENDANCE_APPLY_DATE = "attendance_date";
    private String TAG_REASON = "reason";
    private String TAG_LEAVE_TYPE = "leave_type";
    private String TAG_ATTENDANCE_TIME = "attendance_time";
    private String TAG_ATTENDANCE_STATUS = "status";

    private List<String> InDateList = null;
    private List<String> InTimeList = null;
    private List<String> OutTimeList = null;
    private List<String> HoursWorkedList = null;

    private List<String> attendance_date_subList = null;
    private List<String> attendance_typeList = null;
    private List<String> attendance_dateList = null;
    private List<String> reasonList = null;
    private List<String> leave_typeList = null;
    private List<String> attendance_timeList = null;
    private List<String> attendance_statusList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_attendance);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.view_attendance_topbar);

        initAllViews();

        if(CommonUtils.isInternelAvailable(ViewAttendanceActivity.this)){
            requestAssociateReportsAttendanceList();
        }
        else{
            Toast.makeText(ViewAttendanceActivity.this, "No internet connection!", Toast.LENGTH_SHORT).show();
        }
        viewAttendanceDetailsListViewID.setOnItemClickListener(new ListView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(position==0){
                    //Toast.makeText(getActivity(), "position:"+ String.valueOf(position), Toast.LENGTH_SHORT).show();
                    loadMonthlyRecords(CURRENT_MONTH);
                }
                if(position==1){
                    //Toast.makeText(getActivity(), "position:"+ String.valueOf(position), Toast.LENGTH_SHORT).show();
                    loadMonthlyRecords(PREVIOUS_MONTH);
                }
            }

        });

        //Back Button
        viewAttendancetopbarbackImageViewID.setOnClickListener(new ImageView.OnClickListener(){

            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }//onCreate()

    private void requestAssociateReportsAttendanceList() {
        progressDialog.setMessage("Loading... Please wait!");
        progressDialog.setCancelable(false);
        progressDialog.show();

        client = new RestFullClient(Constants.BASE_URL+Constants.ASSOCIATE_REPORTS_ATTENDANCE_RELATIVE_URI);
        client.AddParam("associate_id", prefs.getString("USERID",""));

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

    private void loadMonthlyRecords(String MONTH){

        attendance_date_subList.clear();
        attendance_typeList.clear();
        attendance_dateList.clear();
        reasonList.clear();
        leave_typeList.clear();
        attendance_timeList.clear();
        attendance_statusList.clear();

        viewTypeViewAssociatereportsID10023.setVisibility(View.GONE);
        listview_header_associatereports_MonthlyDetails_for_table_LinearLayoutID.setVisibility(View.GONE);
        associatereportsAttendanceDetailsListViewID1.setVisibility(View.GONE);
        viewTypeViewAssociatereportsID10024.setVisibility(View.GONE);

        associatereportsNoDataTextViewID.setVisibility(View.GONE);

        progressDialog.setMessage("Loading... Please wait!");
        progressDialog.setCancelable(false);
        progressDialog.show();

        client = new RestFullClient(Constants.BASE_URL+Constants.ASSOCIATE_REPORTS_ATTENDANCE_MONTHLY_RELATIVE_URI);
        client.AddParam("associate_id", prefs.getString("USERID",""));
        client.AddParam("req_month", MONTH);

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
                handler1.sendEmptyMessage(0);

            }

        }).start();

    }

    private void initAllViews(){

        //shared preference
        prefs = getSharedPreferences(CommonUtils.PREFERENCE_NAME,MODE_PRIVATE);
        prefsEditor = prefs.edit();

        viewAttendancetopbarbackImageViewID = (ImageView)findViewById(R.id.viewAttendancetopbarbackImageViewID);

        viewAttendanceDetailsListViewID = (ListView)findViewById(R.id.viewAttendanceDetailsListViewID);
        viewAttendanceDetailsNoDataTextViewID = (TextView)findViewById(R.id.viewAttendanceDetailsNoDataTextViewID);

        InDateList = new ArrayList<String>(0);
        InTimeList = new ArrayList<String>(0);
        OutTimeList = new ArrayList<String>(0);
        HoursWorkedList = new ArrayList<String>(0);

        attendance_date_subList = new ArrayList<>();
        attendance_typeList = new ArrayList<>();
        attendance_dateList = new ArrayList<>();
        reasonList = new ArrayList<>();
        leave_typeList = new ArrayList<>();
        attendance_timeList = new ArrayList<>();
        attendance_statusList = new ArrayList<>();

        //Progress Dialog
        progressDialog = new ProgressDialog(ViewAttendanceActivity.this);
    }

    private void getAttendanceDetails() {

    }

    private void receiveDataForServerResponse(JSONObject jobj){

        try{

            if(client.responseCode==200){

                STATUS =  jobj.getString(TAG_STATUS);
                MESSAGE =  jobj.getString(TAG_MESSAGE);

                System.out.println("STATUS: responseCode==200: "+ STATUS);
                System.out.println("MESSAGE: responseCode==200: "+ MESSAGE);

                if(STATUS.equalsIgnoreCase("true")) {

                    JOBJECT_DATA = jobj.getJSONObject(TAG_JOBJECT_DATA);
                    System.out.println("JOBJECT_DATA: " + JOBJECT_DATA.toString());

                    if (JOBJECT_DATA != null) {

                        if(JOBJECT_DATA.has(TAG_CURRENT_MONTH)){
                            CURRENT_MONTH = JOBJECT_DATA.getString(TAG_CURRENT_MONTH);
                            System.out.println("CURRENT_MONTH: " + CURRENT_MONTH);
                        }
                        if(JOBJECT_DATA.has(TAG_PREVIOUS_MONTH)){
                            PREVIOUS_MONTH = JOBJECT_DATA.getString(TAG_PREVIOUS_MONTH);
                            System.out.println("PREVIOUS_MONTH: " + PREVIOUS_MONTH);
                        }

                        if(JOBJECT_DATA.has(TAG_JOBJECT_CURRENT_MONTH)){
                            JOBJECT_CURRENT_MONTH = JOBJECT_DATA.getJSONObject(TAG_JOBJECT_CURRENT_MONTH);
                            System.out.println("JOBJECT_CURRENT_MONTH : " + JOBJECT_CURRENT_MONTH);

                            if(JOBJECT_CURRENT_MONTH!=null){
                                if(JOBJECT_CURRENT_MONTH.has(TAG_IN)){
                                    IN = JOBJECT_CURRENT_MONTH.getString(TAG_IN);
                                    System.out.println("IN: " + IN);
                                }
                                if(JOBJECT_CURRENT_MONTH.has(TAG_LEAVE)){
                                    LEAVE = JOBJECT_CURRENT_MONTH.getString(TAG_LEAVE);
                                    System.out.println("LEAVE: " + LEAVE);
                                }
                                if(JOBJECT_CURRENT_MONTH.has(TAG_MEETING)){
                                    MEETING = JOBJECT_CURRENT_MONTH.getString(TAG_MEETING);
                                    System.out.println("MEETING: " + MEETING);
                                }
                                if(JOBJECT_CURRENT_MONTH.has(TAG_OUT)){
                                    OUT = JOBJECT_CURRENT_MONTH.getString(TAG_OUT);
                                    System.out.println("OUT: " + OUT);
                                }
                                if(JOBJECT_CURRENT_MONTH.has(TAG_WEEKLY_OFF)){
                                    WEEKLY_OFF = JOBJECT_CURRENT_MONTH.getString(TAG_WEEKLY_OFF);
                                    System.out.println("WEEKLY_OFF: " + WEEKLY_OFF);
                                }
                                if(JOBJECT_CURRENT_MONTH.has(TAG_work_from_home)){
                                    WFH = JOBJECT_CURRENT_MONTH.getString(TAG_work_from_home);
                                    System.out.println("WFH: " + WFH);
                                }
                                if(JOBJECT_CURRENT_MONTH.has(TAG_work_from_client_office)){
                                    WFCO = JOBJECT_CURRENT_MONTH.getString(TAG_work_from_client_office);
                                    System.out.println("WFCO: " + WFCO);
                                }

                            }//if(JOBJECT_CURRENT_MONTH!=null)

                        }//if(JOBJECT_DATA.has(TAG_JOBJECT_CURRENT_MONTH))

                        if(JOBJECT_DATA.has(TAG_JOBJECT_PREVIOUS_MONTH)){
                            JOBJECT_PREVIOUS_MONTH = JOBJECT_DATA.getJSONObject(TAG_JOBJECT_PREVIOUS_MONTH);
                            System.out.println("JOBJECT_PREVIOUS_MONTH : " + JOBJECT_PREVIOUS_MONTH);

                            if(JOBJECT_PREVIOUS_MONTH!=null){
                                if(JOBJECT_PREVIOUS_MONTH.has(TAG_IN)){
                                    IN1 = JOBJECT_PREVIOUS_MONTH.getString(TAG_IN);
                                    System.out.println("IN1: " + IN1);
                                }
                                if(JOBJECT_PREVIOUS_MONTH.has(TAG_LEAVE)){
                                    LEAVE1 = JOBJECT_PREVIOUS_MONTH.getString(TAG_LEAVE);
                                    System.out.println("LEAVE1: " + LEAVE1);
                                }
                                if(JOBJECT_PREVIOUS_MONTH.has(TAG_MEETING)){
                                    MEETING1 = JOBJECT_PREVIOUS_MONTH.getString(TAG_MEETING);
                                    System.out.println("MEETING1: " + MEETING1);
                                }
                                if(JOBJECT_PREVIOUS_MONTH.has(TAG_OUT)){
                                    OUT1 = JOBJECT_PREVIOUS_MONTH.getString(TAG_OUT);
                                    System.out.println("OUT1: " + OUT1);
                                }
                                if(JOBJECT_PREVIOUS_MONTH.has(TAG_WEEKLY_OFF)){
                                    WEEKLY_OFF1 = JOBJECT_PREVIOUS_MONTH.getString(TAG_WEEKLY_OFF);
                                    System.out.println("WEEKLY_OFF1: " + WEEKLY_OFF1);
                                }
                                if(JOBJECT_PREVIOUS_MONTH.has(TAG_work_from_home)){
                                    WFH1 = JOBJECT_PREVIOUS_MONTH.getString(TAG_work_from_home);
                                    System.out.println("WFH1: " + WFH1);
                                }
                                if(JOBJECT_PREVIOUS_MONTH.has(TAG_work_from_client_office)){
                                    WFCO1 = JOBJECT_PREVIOUS_MONTH.getString(TAG_work_from_client_office);
                                    System.out.println("WFCO1: " + WFCO1);
                                }

                            }//if(JOBJECT_PREVIOUS_MONTH!=null)


                        }//if(JOBJECT_DATA.has(TAG_JOBJECT_PREVIOUS_MONTH))

                    }//if (JOBJECT_DATA != null)
                    else{
                        System.out.println("JOBJECT_DATA is Null");
                    }

                }//if(STATUS.equalsIgnoreCase("true")

            }//if(client.responseCode==200)
            if(client.responseCode!=200){

                STATUS =  jobj.getString(TAG_STATUS);
                MESSAGE =  jobj.getString(TAG_MESSAGE);

                System.out.println("STATUS: responseCode!=200: "+ STATUS);
                System.out.println("MESSAGE: responseCode!=200: "+ MESSAGE);

            }//if(client.responseCode!=200)

        }
        catch(Exception e){}

    }//receiveDataForServerResponse(JSONObject jobj)

    private void receiveDataForServerResponse1(JSONObject jobj){

        try{

            if(client.responseCode==200){

                STATUS =  jobj.getString(TAG_STATUS);
                MESSAGE =  jobj.getString(TAG_MESSAGE);

                System.out.println("STATUS: responseCode==200: "+ STATUS);
                System.out.println("MESSAGE: responseCode==200: "+ MESSAGE);

                if(STATUS.equalsIgnoreCase("true")){

                    if (jobj.has(TAG_JOBJECT_DATA)) {

                        System.out.println("jobj.has(TAG_JOBJECT_DATA): in STATUS_CODE = 1: "+ jobj.has(TAG_JOBJECT_DATA));
                        JOBJECT_DATA = jobj.getJSONObject(TAG_JOBJECT_DATA);
                        System.out.println("JOBJECT_DATA: "+JOBJECT_DATA.toString());

                        if(JOBJECT_DATA!=null){

                            JSONArray pendingDateArray = JOBJECT_DATA.getJSONArray(TAG_JARRAY_MONTHLYATTENDANCEDATALIST);
                            JSONObject c = null;

                            String attendance_date_sub = null;
                            String attendance_type = null;
                            String attendance_date = null;
                            String reason = null;
                            String leave_type = null;
                            String attendance_time = null;
                            String status = null;

                            System.out.println("##########All Monthly Attendance Details###################");

                            attendance_date_subList.clear();
                            attendance_typeList.clear();
                            attendance_dateList.clear();
                            reasonList.clear();
                            leave_typeList.clear();
                            attendance_timeList.clear();
                            attendance_statusList.clear();

                            if(pendingDateArray.length()==0){
                            }
                            if(pendingDateArray.length()!=0){

                                for(int i = 0; i < pendingDateArray.length(); i++) {

                                    try {
                                        c = pendingDateArray.getJSONObject(i);
                                        System.out.println("C is : " + c);
                                        if (c != null) {
                                            attendance_date_sub = c.getString(TAG_ATTENDANCE_DATE_SUB);
                                            System.out.println("attendance_date_sub: " + attendance_date_sub);

                                            attendance_type = c.getString(TAG_ATTENDANCE_TYPE);
                                            System.out.println("attendance_type: " + attendance_type);

                                            attendance_date = c.getString(TAG_ATTENDANCE_APPLY_DATE);
                                            System.out.println("attendance_date: " + attendance_date);

                                            reason = c.getString(TAG_REASON);
                                            System.out.println("reason: " + reason);

                                            leave_type = c.getString(TAG_LEAVE_TYPE);
                                            System.out.println("leave_type: " + leave_type);

                                            attendance_time = c.getString(TAG_ATTENDANCE_TIME);
                                            System.out.println("attendance_time: " + attendance_time);

                                            status = c.getString(TAG_ATTENDANCE_STATUS);
                                            System.out.println("status: " + status);

                                            attendance_date_subList.add(attendance_date_sub);
                                            attendance_typeList.add(attendance_type);
                                            if(attendance_date.equalsIgnoreCase("0000-00-00")){
                                                attendance_dateList.add("-");
                                            }
                                            else{
                                                attendance_dateList.add(attendance_date);
                                            }
                                            reasonList.add(reason);
                                            leave_typeList.add(leave_type);
                                            attendance_timeList.add(attendance_time);
                                            attendance_statusList.add(status);

                                        }
                                    } catch (Exception e) {
                                    }

                                }//for

                            }//if(pendingDateArray.length()!=0)

                            System.out.println("Total attendance_date_subList: " + attendance_date_subList.size());
                            System.out.println("Total attendance_typeList: " + attendance_typeList.size());
                            System.out.println("Total attendance_dateList: " + attendance_dateList.size());
                            System.out.println("Total reasonList: " + reasonList.size());
                            System.out.println("Total leave_typeList: " + leave_typeList.size());
                            System.out.println("Total attendance_timeList: " + attendance_timeList.size());
                            System.out.println("Total attendance_statusList: " + attendance_statusList.size());

                            System.out.println("##########End Of All Monthly Attendance Details###################");


                        }//if(JOBJECT_DATA!=null)

                    }//if(jobj.has(TAG_JOBJECT_DATA))

                }//if(STATUS.equalsIgnoreCase("true"))

            }//if(client.responseCode==200)

            if(client.responseCode!=200){

                STATUS =  jobj.getString(TAG_STATUS);
                MESSAGE =  jobj.getString(TAG_MESSAGE);

                System.out.println("STATUS: responseCode==200: "+ STATUS);
                System.out.println("MESSAGE: responseCode==200: "+ MESSAGE);

            }//if(client.responseCode!=200)

        }
        catch(Exception e){}

    }

    */
/*private void getAttendanceDetails(){
        progressDialog.setMessage("Requesting... Please wait!");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String EmpID = prefs.getString("USERISDCODE","");
        System.out.println("EmpID: "+EmpID);

        baseURL = Constants.base_url_default;
        SOAPRequestXML = Constants.soapRequestHeader+
                "<soapenv:Header/>"
                +"<soapenv:Body>"
                +"<tem:GetMyAttendance>"
                //+"<tem:emp_code>"+100400985+"</tem:emp_code>"
                +"<tem:emp_code>"+EmpID+"</tem:emp_code>"
                +"<tem:from_date>"+""+"</tem:from_date>"
                +"<tem:to_date>"+""+"</tem:to_date>"
                +"</tem:GetMyAttendance>"
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
                        else if(tagname.equalsIgnoreCase(TAG_HoursWorked)){
                            HoursWorked = text;
                            text = "";
                            System.out.println("HoursWorked: "+HoursWorked);
                            HoursWorkedList.add(HoursWorked);
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

    }//getParsingElementsForLogin(xpp);*//*


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

    Handler handler1 = new Handler(){

        public void handleMessage(Message msg){

            progressDialog.dismiss();

            //Success
            if(client.responseCode==200){

                //Toast.makeText(SignupActivity.this, MESSAGE, Toast.LENGTH_SHORT).show();

                //success
                if(STATUS.equalsIgnoreCase("true")){
                    //showSuccessDialog();
                    //Toast.makeText(getActivity(), MESSAGE, Toast.LENGTH_SHORT).show();
                    showMonthlyAttendanceDetails();
                }

                //failed
                if(STATUS.equalsIgnoreCase("false")){
                    showFailureDialog();
                }

            }

            //failed
            if(client.responseCode!=200){
                //Toast.makeText(getActivity(), MESSAGE, Toast.LENGTH_SHORT).show();
                showFailureDialog();
            }

        }//handleMessage(Message msg)

    };

    private void setAttendanceDetails(){
        viewAttendanceDetailsListViewID.setAdapter(new CustomAdapterForAttendanceDetails(ViewAttendanceActivity.this));
    }

    //Show failure Dialog
    private void showFailureDialog(){
        associatereportsNoDataTextViewID.setVisibility(View.VISIBLE);
        //Alert Dialog Builder
        AlertDialog.Builder aldb = new AlertDialog.Builder(ViewAttendanceActivity.this);
        aldb.setTitle("Failed!");
        aldb.setMessage("\nReason: "+MESSAGE);
        aldb.setPositiveButton("OK", null);
        aldb.show();

    }

    */
/*
    * CustomAdapterForAttendanceDetails
    *//*

    public class CustomAdapterForAttendanceDetails extends BaseAdapter {

        public Context cntx;

        public CustomAdapterForAttendanceDetails(Context context){
            cntx = context;
        }

        @Override
        public int getCount() {


            if(InDateList!=null) {
                if(InDateList.size()!=0) {

                    int[] numbers = {InDateList.size(),InTimeList.size(),OutTimeList.size(),HoursWorkedList.size()};
                    int smallest = Integer.MAX_VALUE;
                    for(int i =0;i<numbers.length;i++) {
                        if(smallest > numbers[i]) {
                            smallest = numbers[i];
                        }
                    }
                    System.out.println("Smallest size is : " +smallest);
                    return smallest;

                    ////return InDateList.size();

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
                view = inflater.inflate(R.layout.customlayout_for_viewattendance, null);
            }
            else{
                view = convertView;
            }

            final TextView date = (TextView)view.findViewById(R.id.customlayout_IndateTextViewID);
            final TextView intime = (TextView)view.findViewById(R.id.customlayout_InTimeTextViewID);
            final TextView outtime = (TextView)view.findViewById(R.id.customlayout_OuttimeTextViewID);
            final TextView hoursworked = (TextView)view.findViewById(R.id.customlayout_HoursWorkedTextViewID);

            date.setText(InDateList.get(position));
            intime.setText(InTimeList.get(position));
            outtime.setText(OutTimeList.get(position));
            hoursworked.setText(HoursWorkedList.get(position));

            return view;
        }

    }//CustomAdapterForAttendanceDetails Class

    @Override
    public void onBackPressed()
    {
        ViewAttendanceActivity.this.finish();
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


}//ViewAttendanceActivity
*/
package com.experis.smartrac.as;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;


import com.experis.smartrac.as.R;

import org.json.JSONObject;

import java.util.List;

;
/**
 * Class Name: AssociateReportsAttendanceFragment
 * Created by Rana Krishna Paul
 */


public class LeavepolicyActivity extends AppCompatActivity {

    private ListView associatereportsAttendanceDetailsListViewID;
    private TextView associatereportsNoDataTextViewID;

    private View viewTypeViewAssociatereportsID10023;
    private LinearLayout listview_header_associatereports_MonthlyDetails_for_table_LinearLayoutID;
    private ListView associatereportsAttendanceDetailsListViewID1;
    private View viewTypeViewAssociatereportsID10024;

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

    private String TAG_JOBJECT_DATA = "data";
    private JSONObject JOBJECT_DATA = null;

    private String TAG_JOBJECT_CURRENT_MONTH = "current_month_result";
    private JSONObject JOBJECT_CURRENT_MONTH = null;
    private String TAG_JOBJECT_PREVIOUS_MONTH = "previous_month_result";
    private JSONObject JOBJECT_PREVIOUS_MONTH = null;

    private String TAG_CURRENT_MONTH = "current_month";
    private String TAG_PREVIOUS_MONTH = "previous_month";
    private String TAG_IN = "in";
    private String TAG_LEAVE = "leave";
    private String TAG_MEETING = "od";
    private String TAG_OUT = "out";
    private String TAG_WEEKLY_OFF = "weekly_off";
    private String TAG_work_from_home = "work_from_home";
    private String TAG_work_from_client_office = "work_from_client_office";

    private String CURRENT_MONTH = "";
    private String PREVIOUS_MONTH = "";

    private String IN = "0";
    private String LEAVE = "0";
    private String MEETING = "0";
    private String OUT = "0";
    private String WEEKLY_OFF = "0";
    private String WFH = "0";
    private String WFCO = "0";

    private String IN1 = "0";
    private String LEAVE1 = "0";
    private String MEETING1 = "0";
    private String OUT1 = "0";
    private String WEEKLY_OFF1 = "0";
    private String WFH1 = "0";
    private String WFCO1 = "0";

    private String TAG_JARRAY_MONTHLYATTENDANCEDATALIST = "details";

    private String TAG_ATTENDANCE_DATE_SUB = "attendance_date_sub";
    private String TAG_ATTENDANCE_TYPE = "attendance_type";
    private String TAG_ATTENDANCE_APPLY_DATE = "attendance_date";
    private String TAG_REASON = "reason";
    private String TAG_LEAVE_TYPE = "leave_type";
    private String TAG_ATTENDANCE_TIME = "attendance_time";
    private String TAG_ATTENDANCE_STATUS = "status";

    private List<String> attendance_date_subList = null;
    private List<String> attendance_typeList = null;
    private List<String> attendance_dateList = null;
    private List<String> reasonList = null;
    private List<String> leave_typeList = null;
    private List<String> attendance_timeList = null;
    private List<String> attendance_statusList = null;
    private ImageView attendancetopbarbackImageViewID;
    WebView browser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_leavepolicy);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.leavepolicy_topbar_title);

        browser = (WebView) findViewById(R.id.webview);
        initAllViews();
    }

    private void initAllViews(){

        //shared preference
        prefs = getSharedPreferences(CommonUtils.PREFERENCE_NAME,MODE_PRIVATE);
        prefsEditor = prefs.edit();

        //attendance_dateList = new ArrayList<String>(0);

        //Progress Dialog
        progressDialog = new ProgressDialog(LeavepolicyActivity.this);
        browser.loadUrl("http://10.194.5.12/projects10/smartrac_ceragon/uploads/policy/Leave_policy_and_holiday.pdf");


    }



    @Override
    public void onBackPressed() {
        LeavepolicyActivity.this.finish();
        overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
    }
}//Main Class
