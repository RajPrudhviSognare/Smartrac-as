package com.experis.smartrac.as;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AttendanceApprovalLeaveFragment extends Fragment {

    private String TAG_ATTENDANCE_TYPE = "leave"; //"Leave"


    private Spinner attendanceapprovalIntimeSpinner;
    private ListView attendanceApprovalDetailsListViewID;
    private Button attendanceapprovalIntimeGetDetailsBtnID;
    private LinearLayout attendanceapprovalIntimeLayoutID;

    private  AutoCompleteTextView InOutTimetextTAGViewID199copy;
    private ImageView search;

    private View headerView = null;
    private CheckBox approveattendance_checkBox_header;
    private ImageView approveattendance_headerApproveImageViewID;
    private ImageView rejectattendance_headerRejectImageViewID;
    private int count = 0;
    private static boolean isNotAdded = true;
    private String actionType1;

    private SparseBooleanArray mChecked = new SparseBooleanArray();
    private boolean checkedItems[];
    private boolean isAllChecked = false;
    private Set<String> selectionSet = null;
    private JSONArray jsonArray = new JSONArray();

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
    private String TAG_JARRAY_PENDINGDATELIST = "pending_date";
    private String TAG_JARRAY_ATTENDANCEDATALIST = "attendance_data";
private String TAG_MESSAGE_VALUE="";
    private String TAG_ATTENDANCE_DATE = "attendance_date";
    private String TAG_DATE_SELECTED = "selected";

    private String TAG_PARTICULAR_DATE = "particular_date";

    private String TAG_ATTENDANCE_ID = "id";
    private String TAG_ASSOCIATE_ID = "associate_id";
    private String TAG_TL_ID = "tl_id";
    private String TAG_FIRST_NAME = "first_name";
    private String TAG_LAST_NAME = "last_name";
    private String TAG_ATTENDANCE_TIME = "attendance_time";
    private String TAG_ATTENDANCE_IMAGE = "attendance_image";
    private String TAG_ATTENDANCE_LEAVE_DATE = "attendance_date";
    private String TAG_ATTENDANCE_LEAVE_REASON = "reason";
    private String TAG_ATTENDANCE_LEAVE_TYPE = "leave_type";
    private String TAG_ASSOCIATE_ISD = "isd_code";

    private JSONObject JOBJECT_DATA = null;

    private List<String> attendance_dateList = null;
    private List<String> selectedList = null;

    private List<String> attendanceidList = null;
    private List<String> associate_idList = null;
    private List<String> tl_idList = null;
    private List<String> first_nameList = null;
    private List<String> last_nameList = null;
    private List<String> attendance_timeList = null;
    private List<String> attendance_imageList = null;
    private List<String> attendance_leavedateList = null;
    private List<String> attendance_leavereasonList = null;
    private List<String> attendance_leavetypeList = null;
    private List<String> associate_isdList = null;

    private ImageLoader imageLoader;
    private int STATUS_CODE = 0;
    private String PARTICULAR_DATE = "";

    private int attendanceSelectedDatePosition = 0;
    private String selectedDateFromSpinner = null;
    private boolean userSelect = false;

    private TextView attendanceApprovalNoDataTextViewID;

    private View view = null;
    private ArrayList<String> Employeelist=new ArrayList<String>();
    private  ArrayList<String> Employeelistid=new ArrayList<String>();
    private String baseURL;
    private String SOAPRequestXML;
    private HttpResponse httpResponse = null;
    private String TAG_OpeningLeaveBalance = "leavebalance";

    private String TAG_OpeningLeaveBalanceType = "Employeelistid";

    private String empname = "0";

    private String empcode = "";
    String attendanceid = null;
    String associate_id = null;
    String tl_id = null;
    String first_name = null;
    String last_name = null;
    String attendance_time = null;
    String attendance_image = null;
    String attendance_leavedate = null;
    String attendance_leavereason = null;
    String attendance_leavetype = null;
    String associate_isd = null;

    public AttendanceApprovalLeaveFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initAllViews();

       /* if(CommonUtils.isInternelAvailable(getActivity())){
            System.out.println("requestAttendanceList() called inside onCreate()");
            requestAttendanceList();
        }
        else{
            Toast.makeText(getActivity(), "No internet connection!", Toast.LENGTH_SHORT).show();
        }*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Toast.makeText(getActivity(),"In Time Fragment",Toast.LENGTH_LONG).show();
        // Inflate the layout for this fragment

        if(view==null){

            view = inflater.inflate(R.layout.attendance_approval_leave_fragment, container, false);

           // attendanceapprovalIntimeSpinner = (Spinner)view.findViewById(R.id.attendanceapprovalIntimeSpinner2);
            attendanceApprovalDetailsListViewID = (ListView)view.findViewById(R.id.attendanceApprovalDetailsListViewID2);

            approveattendance_checkBox_header = (CheckBox)view.findViewById(R.id.approveattendance_checkBox_header2);
            approveattendance_headerApproveImageViewID = (ImageView)view.findViewById(R.id.approveattendance_headerApproveImageViewID2);
            rejectattendance_headerRejectImageViewID = (ImageView)view.findViewById(R.id.rejectattendance_headerRejectImageViewID2);

            attendanceApprovalNoDataTextViewID = (TextView)view.findViewById(R.id.attendanceApprovalNoDataTextViewID2);

            attendanceapprovalIntimeGetDetailsBtnID = (Button)view.findViewById(R.id.attendanceapprovalIntimeGetDetailsBtnID2);
            attendanceapprovalIntimeLayoutID = (LinearLayout) view.findViewById(R.id.attendanceapprovalIntimeLayoutID2);
            InOutTimetextTAGViewID199copy=(AutoCompleteTextView)view.findViewById(R.id.InOutTimetextTAGViewID199copy);
            search=(ImageView)view.findViewById(R.id.search);

        }//if(view==null)
        else{
            //((ViewGroup) view.getParent()).removeView(view);
        }


      /* if(CommonUtils.isInternelAvailable(getActivity())){
            System.out.println("requestAttendanceList() called inside onCreateView()");
            requestAttendanceList();
        }
        else{
            Toast.makeText(getActivity(), "No internet connection!", Toast.LENGTH_SHORT).show();
        }*/

        return view;

    }

    private void initAllViews(){

        //shared preference
        prefs = getActivity().getSharedPreferences(CommonUtils.PREFERENCE_NAME,getActivity().MODE_PRIVATE);
        prefsEditor = prefs.edit();

        attendance_dateList = new ArrayList<String>(0);
        selectedList = new ArrayList<String>(0);

        attendanceidList = new ArrayList<String>(0);
        associate_idList = new ArrayList<String>(0);
        tl_idList = new ArrayList<String>(0);
        first_nameList = new ArrayList<String>(0);
        last_nameList = new ArrayList<String>(0);
        attendance_timeList = new ArrayList<String>(0);
        attendance_imageList = new ArrayList<String>(0);
        attendance_leavedateList = new ArrayList<String>(0);
        attendance_leavereasonList = new ArrayList<String>(0);
        attendance_leavetypeList = new ArrayList<String>(0);
        associate_isdList = new ArrayList<String>(0);

        selectionSet = new HashSet<String>(0);

        imageLoader = new ImageLoader(getActivity());
        //Progress Dialog
        progressDialog = new ProgressDialog(getActivity());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);



        if(CommonUtils.isInternelAvailable(getActivity())){
            System.out.println("requestEmployeeList() called inside onActivityCreated()of AttendanceApprovalLeaveFragment");
            requestEmployeeList();
        }
        else{
            Toast.makeText(getActivity(), "No internet connection!", Toast.LENGTH_SHORT).show();
        }


       /* if(CommonUtils.isInternelAvailable(getActivity())){
            System.out.println("requestAttendanceList() called inside onActivityCreated()of AttendanceApprovalLeaveFragment");
            requestAttendanceList();
        }
        else{
            Toast.makeText(getActivity(), "No internet connection!", Toast.LENGTH_SHORT).show();
        }*/

        /*attendanceapprovalIntimeGetDetailsBtnID.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(CommonUtils.isInternelAvailable(getActivity())){
                    System.out.println("requestAttendanceList() called inside onActivityCreated()of AttendanceApprovalLeaveFragment");
                    attendanceapprovalIntimeLayoutID.setVisibility(View.VISIBLE);
                    requestAttendanceList();
                }
                else{
                    Toast.makeText(getActivity(), "No internet connection!", Toast.LENGTH_SHORT).show();
                }
            }
        });*/


        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(CommonUtils.isInternelAvailable(getActivity())){
                    //  requestAttendanceList(Employeelistid.get(position));

                    getLeaveDetails(InOutTimetextTAGViewID199copy.getText().toString().trim());
                }
                else{
                    Toast.makeText(getActivity(), "No internet connection!", Toast.LENGTH_SHORT).show();
                }
            }
        });

     /*   attendanceapprovalIntimeSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                selectedDateFromSpinner = parent.getItemAtPosition(position).toString();
                System.out.println("selectedDateFromSpinner: "+ selectedDateFromSpinner.toString());
                //Toast.makeText(getActivity(), "Selected Item: " + selectedDateFromSpinner, Toast.LENGTH_SHORT).show();
                //Toast.makeText(getActivity(), "Selected Item Position: " + position, Toast.LENGTH_SHORT).show();

              //  if (userSelect) {
                    if(CommonUtils.isInternelAvailable(getActivity())){
                      //  requestAttendanceList(Employeelistid.get(position));

                        getLeaveDetails(Employeelistid.get(position));
                    }
                    else{
                        Toast.makeText(getActivity(), "No internet connection!", Toast.LENGTH_SHORT).show();
                    }
                  *//*  userSelect = false;
                }*//*

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }

        });

        attendanceapprovalIntimeSpinner.setOnTouchListener(new View.OnTouchListener(){

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                userSelect = true;
                return false;
            }
        });*/



            approveattendance_checkBox_header.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    if (isChecked) {
                        for (int i = 0; i < attendanceidList.size(); i++) {
                            checkedItems[i] = true;
                        }
                        selectionSet.clear();
                        isAllChecked = true;
                        attendanceApprovalDetailsListViewID.setAdapter(null);
                        attendanceApprovalDetailsListViewID.setAdapter(new CustomAdapterForAttendanceDetails(getActivity()));

                        for (int j = 0; j < attendanceidList.size(); j++) {
                            selectionSet.add(attendanceidList.get(j));
                        }
                        System.out.println("Selection ID list: " + selectionSet.toString());
                        System.out.println("Selection ID list size: " + selectionSet.size());
                    }
                    if (!isChecked) {
                        selectionSet.clear();
                        isAllChecked = false;
                        System.out.println("Selection ID list: " + selectionSet.toString());
                        System.out.println("Selection ID list size: " + selectionSet.size());
                        attendanceApprovalDetailsListViewID.setAdapter(null);
                        attendanceApprovalDetailsListViewID.setAdapter(new CustomAdapterForAttendanceDetails(getActivity()));
                    }

                }
            });


        //Approve Button Click
        approveattendance_headerApproveImageViewID.setOnClickListener(new ImageView.OnClickListener(){

            @Override
            public void onClick(View v) {

                if(selectionSet.size()==0){
                    Toast.makeText(getActivity(), "No Associate is selected!", Toast.LENGTH_SHORT).show();
                }//if(selectionSet.size()==0)

                if(selectionSet.size()!=0){
                    String value1[] = selectionSet.toArray(new String[selectionSet.size()]);
                    int size1 = value1.length;
                    if(size1!=0){
                        for(int i=0;i<value1.length;i++){
                            System.out.println("Set to String: "+value1[i]);
                        }
                        System.out.println("Set to String: "+value1);

                    }//if(size1!=0)
                    if(value1.length!=0){
                        JSONArray jsonArrayFriends = new JSONArray();
                        for(int i=0;i<value1.length;i++){
                            jsonArrayFriends.put(Integer.valueOf(value1[i]));
                        }

                        System.out.println("jsonArrayFriends: "+jsonArrayFriends);
                        System.out.println("jsonArrayFriendsToString: "+jsonArrayFriends.toString());

                     //   SendRequestForApproveOrReject(jsonArrayFriends,"Approved",selectedDateFromSpinner);
                        //sendApproveForLeave(jsonArrayFriends,"Approved",selectedDateFromSpinner);

                    }//if(value1.length!=0)

                }//if(selectionSet.size()!=0)

            }//onClick(View v)
        });

        //Reject Button Click
        rejectattendance_headerRejectImageViewID.setOnClickListener(new ImageView.OnClickListener(){

            @Override
            public void onClick(View v) {

                if(selectionSet.size()==0){
                    Toast.makeText(getActivity(), "No Associate is selected!", Toast.LENGTH_SHORT).show();
                }//if(selectionSet.size()==0)

                if(selectionSet.size()!=0){
                    String value1[] = selectionSet.toArray(new String[selectionSet.size()]);
                    int size1 = value1.length;
                    if(size1!=0){
                        for(int i=0;i<value1.length;i++){
                            System.out.println("Set to String: "+value1[i]);
                        }
                        System.out.println("Set to String: "+value1);

                    }//if(size1!=0)
                    if(value1.length!=0){
                        JSONArray jsonArrayFriends = new JSONArray();
                        for(int i=0;i<value1.length;i++){
                            jsonArrayFriends.put(Integer.valueOf(value1[i]));
                        }

                        System.out.println("jsonArrayFriends: "+jsonArrayFriends);
                        System.out.println("jsonArrayFriendsToString: "+jsonArrayFriends.toString());

                        SendRequestForApproveOrReject(jsonArrayFriends,"Rejected",selectedDateFromSpinner);

                    }//if(value1.length!=0)

                }//if(selectionSet.size()!=0)

            }//onClick(View v)
        });



    }//onActivityCreated(Bundle savedInstanceState)

    private void SendRequestForApproveOrReject(JSONArray jsonArrayFriends,String actionType,String selectedDateFromSpinner){

        actionType1 = actionType;
        JSONObject jObj = null;
        jObj = new JSONObject();
        try {
            jObj.put("ids",jsonArrayFriends);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println("jObj: " + jObj);
        if(actionType.equalsIgnoreCase("Approved")){
            //progressDialog.setTitle("Leave Attendance Approval");
        }
        if(actionType.equalsIgnoreCase("Rejected")){
            //progressDialog.setTitle("Leave Attendance Reject");
        }
        progressDialog.setMessage("Processing... Please wait!");
        progressDialog.setCancelable(false);
        progressDialog.show();
        client = new RestFullClient(Constants.BASE_URL+Constants.ATTENDANCE_APPROVALREJECTION_RELATIVE_URI);
        //client.AddParam("m_date", selectedDateFromSpinner);
        //client.AddParam("tl_id", prefs.getString("USERID",""));
        client.AddParam("action", actionType);
        //client.AddParam("type", TAG_ATTENDANCE_TYPE);
        client.AddParam("attendance_ids", jObj.toString());
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
    private void receiveDataForServerResponse1(JSONObject jobj){

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
    Handler handler1 = new Handler(){

        public void handleMessage(Message msg){

            progressDialog.dismiss();

            //Success
            if(client.responseCode==200){

                //Success
                if(STATUS.equalsIgnoreCase("true")){
                    showSuccessDialog();
                    selectionSet.clear();
                    approveattendance_checkBox_header.setChecked(false);
                    //Toast.makeText(getActivity(), MESSAGE, Toast.LENGTH_SHORT).show();

                }
                //Failed
                if(STATUS.equalsIgnoreCase("false")){
                    //showRetryDialog();
                    showFailureDialog();
                }
            }
            //Failed
            if(client.responseCode!=200){
                //Toast.makeText(getActivity(), MESSAGE, Toast.LENGTH_SHORT).show();
                //showRetryDialog();
                showFailureDialog();
            }

        }//handleMessage(Message msg)

    };
    //Show Success Dialog
    private void showSuccessDialog(){
        //Alert Dialog Builder
        android.app.AlertDialog.Builder aldb = new android.app.AlertDialog.Builder(getActivity());
        aldb.setTitle("Success!");
        aldb.setCancelable(false);
        if(actionType1.equalsIgnoreCase("Approved")){
            aldb.setMessage("You Have Successfully Approved Selected Associates' Leave. \n\nThank You");
        }
        if(actionType1.equalsIgnoreCase("Rejected")){
            aldb.setMessage("You Have Successfully Rejected Selected Associates' Leave. \n\nThank You");
        }
        aldb.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                getActivity().finish();
                /*if(CommonUtils.isInternelAvailable(getActivity())){
                    requestEmployeeList();
                }
                else{
                    Toast.makeText(getActivity(), "No internet connection!", Toast.LENGTH_SHORT).show();
                }*/
            }
        });
       /* aldb.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getActivity().finish();
                getActivity().overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
            }
        });
        aldb.setNegativeButton("My Location", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });*/
        aldb.show();

    }


    private void requestAttendanceList(String empid){

        attendanceApprovalNoDataTextViewID.setVisibility(View.GONE);
        attendance_dateList.clear();
        selectedList.clear();

        attendanceidList.clear();
        associate_idList.clear();
        tl_idList.clear();
        first_nameList.clear();
        last_nameList.clear();
        attendance_timeList.clear();
        attendance_imageList.clear();
        attendance_leavedateList.clear();
        attendance_leavereasonList.clear();
        attendance_leavetypeList.clear();
        associate_isdList.clear();

        //progressDialog.setTitle("Leave Attendance Details");
        progressDialog.setMessage("Loading... Please wait!");
        progressDialog.setCancelable(false);
        progressDialog.show();
        client = new RestFullClient(Constants.BASE_URL+Constants.ATTENDANCE_APPROVAL_RELATIVE_URI);
        if(selectedDateFromSpinner!=null){
            client.AddParam("m_date", selectedDateFromSpinner);
        }
        else{
            client.AddParam("m_date", "");
        }
        client.AddParam("type", TAG_ATTENDANCE_TYPE);
        client.AddParam("tl_id", prefs.getString("USERID",""));

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

                        JSONArray pendingDateArray = JOBJECT_DATA.getJSONArray(TAG_JARRAY_PENDINGDATELIST);
                        System.out.println("Pending Date List: " + pendingDateArray.toString());
                        System.out.println("Total Pending Date List: " + pendingDateArray.length());

                        PARTICULAR_DATE = JOBJECT_DATA.getString(TAG_PARTICULAR_DATE);
                        System.out.println("PARTICULAR_DATE: "+ PARTICULAR_DATE);

                        JSONArray attendanceDataArray = JOBJECT_DATA.getJSONArray(TAG_JARRAY_ATTENDANCEDATALIST);
                        System.out.println("Attendance Data List: " + attendanceDataArray.toString());
                        System.out.println("Total Attendance Data List: " + attendanceDataArray.length());

                        JSONObject c = null;
                        String attendance_date = null;
                        String selected = null;
                        //int selected;

                        JSONObject c1 = null;
                        String attendanceid = null;
                        String associate_id = null;
                        String tl_id = null;
                        String first_name = null;
                        String last_name = null;
                        String attendance_time = null;
                        String attendance_image = null;
                        String attendance_leavedate = null;
                        String attendance_leavereason = null;
                        String attendance_leavetype = null;
                        String associate_isd = null;

                        System.out.println("##########All Pending Date List details###################");

                        attendance_dateList.clear();
                        selectedList.clear();

                        if(pendingDateArray.length()==0){
                            //attendanceApprovalNoDataTextViewID.setVisibility(View.VISIBLE);
                            System.out.println("Inside 'if(pendingDateArray.length()==0)': attendanceApprovalNoDataTextViewID.setVisibility(View.VISIBLE): "+attendanceApprovalNoDataTextViewID.getVisibility());
                        }
                        if(pendingDateArray.length()!=0){

                            for(int i = 0; i < pendingDateArray.length(); i++) {

                                try {
                                    c = pendingDateArray.getJSONObject(i);
                                    System.out.println("C is : " + c);
                                    if (c != null) {
                                        attendance_date = c.getString(TAG_ATTENDANCE_DATE);
                                        selected = c.getString(TAG_DATE_SELECTED);
                                        //selected = c.getInt(TAG_DATE_SELECTED);

                                        attendance_dateList.add(attendance_date);
                                        selectedList.add(selected);
                                        //selectedList.add(String.valueOf(selected));

                                        if(Integer.parseInt(selected)==1){
                                            //if(selected==1){
                                            attendanceSelectedDatePosition = i;
                                        }

                                    }
                                } catch (Exception e) {
                                }

                            }//for

                        }//if(pendingDateArray.length()!=0)

                        System.out.println("Dropdown selected pos: " + attendanceSelectedDatePosition);
                        System.out.println("Total attendance_dateList: " + attendance_dateList.size());
                        System.out.println("Total selectedList: " + selectedList.size());
                        System.out.println("##########End Of All Pending Date List details###################");

                        System.out.println("##########All Attendance Data List details###################");

                        attendanceidList.clear();
                        associate_idList.clear();
                        tl_idList.clear();
                        first_nameList.clear();
                        last_nameList.clear();
                        attendance_timeList.clear();
                        attendance_imageList.clear();
                        attendance_leavedateList.clear();
                        attendance_leavereasonList.clear();
                        attendance_leavetypeList.clear();
                        associate_isdList.clear();

                        if(attendanceDataArray.length()==0){
                            //attendanceApprovalNoDataTextViewID.setVisibility(View.VISIBLE);
                            System.out.println("Inside 'if(pendingDateArray.length()==0)': attendanceApprovalNoDataTextViewID.setVisibility(View.VISIBLE): "+attendanceApprovalNoDataTextViewID.getVisibility());
                        }
                        if(attendanceDataArray.length()!=0){

                            for (int i = 0; i < attendanceDataArray.length(); i++) {

                                try {
                                    c1 = attendanceDataArray.getJSONObject(i);
                                    System.out.println("C1 is : " + c1);
                                    if (c1 != null) {
                                        attendanceid = c1.getString(TAG_ATTENDANCE_ID);
                                        associate_id = c1.getString(TAG_ASSOCIATE_ID);
                                        tl_id = c1.getString(TAG_TL_ID);
                                        first_name = c1.getString(TAG_FIRST_NAME);
                                        last_name = c1.getString(TAG_LAST_NAME);
                                        attendance_time = c1.getString(TAG_ATTENDANCE_TIME);
                                        attendance_image = c1.getString(TAG_ATTENDANCE_IMAGE);
                                        attendance_leavedate = c1.getString(TAG_ATTENDANCE_LEAVE_DATE);
                                        attendance_leavereason = c1.getString(TAG_ATTENDANCE_LEAVE_REASON);
                                        attendance_leavetype = c1.getString(TAG_ATTENDANCE_LEAVE_TYPE);
                                        associate_isd = c1.getString(TAG_ASSOCIATE_ISD);

                                        attendanceidList.add(attendanceid);
                                        associate_idList.add(associate_id);
                                        tl_idList.add(tl_id);
                                        first_nameList.add(first_name);
                                        last_nameList.add(last_name);
                                        attendance_timeList.add(attendance_time);
                                        attendance_imageList.add(Constants.BASE_URL_ATTENDANCE_LOGO+attendance_image);
                                        attendance_leavedateList.add(attendance_leavedate);
                                        attendance_leavereasonList.add(attendance_leavereason);
                                        attendance_leavetypeList.add(attendance_leavetype);
                                        associate_isdList.add(associate_isd);
                                    }
                                } catch (Exception e) {
                                }

                            }//for

                        }//if(attendanceDataArray.length()!=0)

                        System.out.println("Total attendanceidList: " + attendanceidList.size());
                        System.out.println("Total associate_idList: " + associate_idList.size());
                        System.out.println("Total tl_idList: " + tl_idList.size());
                        System.out.println("Total first_nameList: " + first_nameList.size());
                        System.out.println("Total last_nameList: " + last_nameList.size());
                        System.out.println("Total attendance_timeList: " + attendance_timeList.size());
                        System.out.println("Total attendance_imageList: " + attendance_imageList.size());
                        System.out.println("Total attendance_leavedateList: " + attendance_leavedateList.size());
                        System.out.println("Total attendance_leavereasonList: " + attendance_leavereasonList.size());
                        System.out.println("Total attendance_leavetypeList: " + attendance_leavetypeList.size());
                        System.out.println("Total associate_isdList: " + associate_isdList.size());
                        System.out.println("##########End Of All Attendance Data List details###################");



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

    }
    Handler handler = new Handler(){

        public void handleMessage(Message msg){

            progressDialog.dismiss();

            //Success
            if(client.responseCode==200){

                //Toast.makeText(SignupActivity.this, MESSAGE, Toast.LENGTH_SHORT).show();

                //Login success
                if(STATUS.equalsIgnoreCase("true")){
                    //showSuccessDialog();
                    //Toast.makeText(getActivity(), MESSAGE, Toast.LENGTH_SHORT).show();
                    showAttendanceDetails();
                }

                //Login failed
                if(STATUS.equalsIgnoreCase("false")){
                    showFailureDialog();
                }

            }

            //Login failed
            if(client.responseCode!=200){
                //Toast.makeText(getActivity(), MESSAGE, Toast.LENGTH_SHORT).show();
                showFailureDialog();
            }

        }//handleMessage(Message msg)

    };
    //Show failure Dialog
    private void showFailureDialog(){
        //Alert Dialog Builder
        AlertDialog.Builder aldb = new AlertDialog.Builder(getActivity());
        aldb.setTitle("Failed!");
        aldb.setMessage("\nReason: "+MESSAGE);
        aldb.setPositiveButton("OK", null);
        aldb.show();
    }

    private void showAttendanceDetails(){

      /*  SetDropDownItems();*/
        setAttendanceListView();
    }
    private void SetDropDownItems(){
        if(attendance_dateList.size()!=0){
            ArrayAdapter dataAdapter = new ArrayAdapter (getActivity(), android.R.layout.simple_spinner_item, attendance_dateList);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            attendanceapprovalIntimeSpinner.setAdapter(dataAdapter);
            System.out.println("Selected Spinner Item Position: "+ attendanceSelectedDatePosition);
            attendanceapprovalIntimeSpinner.setSelection(attendanceSelectedDatePosition);
        }
        else{
            attendanceApprovalNoDataTextViewID.setVisibility(View.VISIBLE);
            System.out.println("Inside 'SetDropDownItems()': attendanceApprovalNoDataTextViewID.setVisibility(View.VISIBLE): "+attendanceApprovalNoDataTextViewID.getVisibility());
        }
    }
    private void setAttendanceListView(){

        checkedItems = new boolean[attendanceidList.size()];
        attendanceApprovalDetailsListViewID.setAdapter(new CustomAdapterForAttendanceDetails(getActivity()));
    }

    /*
    * CustomAdapterForAttendanceDetails
    */
    public class CustomAdapterForAttendanceDetails extends BaseAdapter {

        public Context cntx;

        public CustomAdapterForAttendanceDetails(Context context){
            cntx = context;
        }

        public void updateAdapter(Context context) { //Does Not Work
            cntx = context;
            //and call notifyDataSetChanged
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            count = attendanceidList.size();
            return count;
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub

            View view;
            ViewHolder viewHolder = new ViewHolder();

            if(convertView==null){

                LayoutInflater inflater = (LayoutInflater)cntx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.custom_layout_for_attendance_approval_allleavetypes1, null);
            }
            else{

                view = convertView;
            }

            view.setTag(viewHolder);
            final int pos1 = position;

            final TextView nameTextView = (TextView)view.findViewById(R.id.custom_approveattendance_AllLeaveTypes_NameTextViewID);
            final TextView idTextView = (TextView)view.findViewById(R.id.custom_approveattendance_AllLeaveTypes_IDTextViewID);
            //final ImageView selfieImage = (ImageView)view.findViewById(R.id.custom_approveattendance_SelfieImageViewID);
            //final TextView timeTextView = (TextView)view.findViewById(R.id.custom_approveattendance_TimeTextViewID);
            final TextView dateTextView = (TextView)view.findViewById(R.id.custom_approveattendance_AllLeaveTypes_DateTextViewID);
            final TextView reasonTextView = (TextView)view.findViewById(R.id.custom_approveattendance_AllLeaveTypes_ReasonTextViewID);
            final TextView leaveTypeTextView = (TextView)view.findViewById(R.id.custom_approveattendance_AllLeaveTypes_TypeTextViewID);
            leaveTypeTextView.setVisibility(View.VISIBLE);

            viewHolder.checkbox = (CheckBox)view.findViewById(R.id.custom_approveattendance_AllLeaveTypes_checkBoxID);
            final  Button approve=(Button)view.findViewById(R.id.approve);
            final  Button reject=(Button)view.findViewById(R.id.reject);
            //viewHolder.selfieImage = (ImageView)view.findViewById(R.id.custom_approveattendance_SelfieImageViewID);

            final ViewHolder holder = (ViewHolder)view.getTag();

            nameTextView.setText(first_nameList.get(position));
            idTextView.setText("ISD: "+associate_idList.get(position));
            dateTextView.setText("Date: "+attendance_leavedateList.get(position));
            leaveTypeTextView.setText("Type: "+attendance_leavetypeList.get(position));
            reasonTextView.setText("Reason: "+attendance_leavereasonList.get(position));
            //timeTextView.setText(attendance_timeList.get(position));
            //imageLoader.DisplayImage(attendance_imageList.get(position), holder.selfieImage);


            //viewHolder.checkbox.setTag(pos1); // This line is important.
            //holder.checkbox.setChecked(checkedItems[pos1]);
            //view.setSelected(checkedItems[pos1]);

            approve.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendApproveForLeave(attendanceidList.get(pos1),"Approved",selectedDateFromSpinner);
                }
            });

            reject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendApproveForLeave(attendanceidList.get(pos1),"Rejected",selectedDateFromSpinner);
                }
            });

            if(isAllChecked){
                holder.checkbox.setChecked(checkedItems[position]);
            }

           // if(selectionSet.size()<1) {
                //Checkbox is checked
                holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        // TODO Auto-generated method stub


                       // isAllChecked = false;
                       // attendanceApprovalDetailsListViewID.setAdapter(null);
                       // attendanceApprovalDetailsListViewID.setAdapter(new CustomAdapterForAttendanceDetails(getActivity()));


                       // holder.checkbox.setChecked(checkedItems[position]);
                        if (isChecked) {

                           /* selectionSet.clear();
                            for (int i = 0; i < attendanceidList.size(); i++) {
                                if(i==position)
                                    checkedItems[i] = true;
                                else
                                    checkedItems[i] = false;
                            }

                            holder.checkbox.setChecked(checkedItems[position]);
*/

                            //Toast.makeText(getActivity(), "Checked at "+String.valueOf(pos1), Toast.LENGTH_SHORT).show();
                            selectionSet.add(attendanceidList.get(pos1));
                            System.out.println("Selection ID list: " + selectionSet.toString());
                            System.out.println("Total Set size(After Add): " + selectionSet.size());
                        }
                        if (!isChecked) {


                            //Toast.makeText(getActivity(), "UnChecked at "+String.valueOf(pos1), Toast.LENGTH_SHORT).show();
                            selectionSet.remove(attendanceidList.get(pos1));
                            System.out.println("Selection ID list: " + selectionSet.toString());
                            System.out.println("Total Set size(After Remove): " + selectionSet.size());

                        }//if

                    }

                });
         /*   }
            else{
                holder.checkbox.setClickable(false);
            }*/

            //Selfie image is clicked
        /*holder.selfieImage.setOnClickListener(new ImageView.OnClickListener(){

            @Override
            public void onClick(View v) {
                //Toast.makeText(getActivity(), "Selfie image is clieck! at position "+String.valueOf(pos1), Toast.LENGTH_SHORT).show();
                displayLargeSelfie(attendance_imageList.get(pos1));
            }
        });*/


            return view;

        }

        class ViewHolder{
            CheckBox checkbox;
            //ImageView selfieImage;
        }

    }//CustomAdapterForAttendanceDetails Class

    private void displayLargeSelfie(String imageURL){
        final String url = imageURL;
        Intent i = new Intent(getActivity(), LargerSelfieImageDisplayActivity.class);
        i.putExtra("IMAGEPATH",url);
        getActivity().startActivity(i);
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
    public void onResume(){
        super.onResume();
       /* if(CommonUtils.isInternelAvailable(getActivity())){
            System.out.println("requestAttendanceList() called inside onResume()");
            requestAttendanceList();
        }
        else{
            Toast.makeText(getActivity(), "No internet connection!", Toast.LENGTH_SHORT).show();
        }*/
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
    }


    private void requestEmployeeList(){
        Employeelist=new ArrayList<String>();
        Employeelistid=new ArrayList<String>();
        progressDialog.setMessage("Requesting... Please wait!");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String clientid = prefs.getString("USERISDCODE","");


        baseURL = Constants.base_url_default;
        SOAPRequestXML = Constants.soapRequestHeader+
                "<soapenv:Header/>"
                +"<soapenv:Body>"
                +"<tem:EmployeeImplantMapping>"
                //+"<tem:emp_code>"+100172254+"</tem:emp_code>"
                +"<tem:implant_userid>"+clientid+"</tem:implant_userid>"
                +"</tem:EmployeeImplantMapping>"
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

                        if(tagname.equalsIgnoreCase("EmpCode")){
                            empcode = text;
                            text = "";
                            System.out.println("empcode: "+empcode);
                            Employeelistid.add(empcode);
                        }
                        if(tagname.equalsIgnoreCase("EmpName")){

                            empname = text;

                            text = "";
                            System.out.println("empname: "+empname);
                            Employeelist.add(empname);
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

            setEmployeelist();
        }//handleMessage(Message msg)

    };

    private void setEmployeelist() {
        if (Employeelist != null) {

           // SetDropDownItems();
           /* if(prefs.getString("USERGENDER","").equalsIgnoreCase("Male")){
                Employeelist.remove(3);
                Employeelistid.remove(3);

            }

            else{
                Employeelist.remove(5);
                Employeelistid.remove(5);
            }*/



            if(Employeelist.size()!=0){
                ArrayAdapter dataAdapter = new ArrayAdapter (getActivity(), android.R.layout.simple_spinner_item, Employeelistid);
               /* dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);*/
                dataAdapter.setDropDownViewResource(R.layout.spinner_item);
                InOutTimetextTAGViewID199copy.setAdapter(dataAdapter);
               /* attendanceapprovalIntimeSpinner.setAdapter(dataAdapter);
                //System.out.println("Selected Spinner Item Position: "+ attendanceSelectedDatePosition);
                attendanceapprovalIntimeSpinner.setSelection(attendanceSelectedDatePosition);*/
            }
            else{
                attendanceApprovalNoDataTextViewID.setVisibility(View.VISIBLE);
                System.out.println("Inside 'SetDropDownItems()': attendanceApprovalNoDataTextViewID.setVisibility(View.VISIBLE): "+attendanceApprovalNoDataTextViewID.getVisibility());
            }





        }


       // getLeaveDetails();
    }


    private void getLeaveDetails(String EmpID){


        attendanceidList.clear();
        associate_idList.clear();
        tl_idList.clear();
        first_nameList.clear();
        last_nameList.clear();
        attendance_timeList.clear();
        attendance_imageList.clear();
        attendance_leavedateList.clear();
        attendance_leavereasonList.clear();
        attendance_leavetypeList.clear();
        associate_isdList.clear();

        progressDialog.setMessage("Requesting... Please wait!");
        progressDialog.setCancelable(false);
        progressDialog.show();

       // String EmpID = prefs.getString("USERISDCODE","");
        System.out.println("EmpID: "+EmpID);

        baseURL = Constants.base_url_default;
        SOAPRequestXML = Constants.soapRequestHeader+
                "<soapenv:Header/>"
                +"<soapenv:Body>"
                +"<tem:ApprovalPendingRequest >"
                //+"<tem:emp_code>"+100172254+"</tem:emp_code>"
                +"<tem:emp_code>"+EmpID+"</tem:emp_code>"
                +"</tem:ApprovalPendingRequest >"
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

                handler11.sendEmptyMessage(0);

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


                        if(tagname.equalsIgnoreCase("EmpCode")){
                            associate_id = text;
                            text = "";
                            System.out.println("OpeningLeaveBalance: "+associate_id);
                            associate_idList.add(associate_id);
                        }
                        if(tagname.equalsIgnoreCase("EmpName")){
                            first_name = text;
                            text = "";
                            System.out.println("OpeningLeaveBalance: "+first_name);
                            first_nameList.add(first_name);
                        }
                        if(tagname.equalsIgnoreCase("RequestID")){
                            attendanceid = text;
                            text = "";
                            System.out.println("OpeningLeaveBalance: "+attendanceid);
                            attendanceidList.add(attendanceid);
                        }
                        if(tagname.equalsIgnoreCase("LeaveType")){
                            attendance_leavetype = text;
                            text = "";
                            System.out.println("OpeningLeaveBalance: "+attendance_leavetype);
                            attendance_leavetypeList.add(attendance_leavetype);
                        }
                        if(tagname.equalsIgnoreCase("Start_x0020_Date")){
                            attendance_leavedate = text;
                            text = "";
                            System.out.println("OpeningLeaveBalance: "+attendance_leavedate);
                            attendance_leavedateList.add(attendance_leavedate);
                        }
                        if(tagname.equalsIgnoreCase("End_x0020_Date")){
                            attendance_time = text;
                            text = "";
                            System.out.println("OpeningLeaveBalance: "+attendance_time);
                            attendance_timeList.add(attendance_time);
                        }
                       /* if(tagname.equalsIgnoreCase("TotalDays")){
                            associate_id = text;
                            text = "";
                            System.out.println("OpeningLeaveBalance: "+associate_id);
                            associate_idList.add(associate_id);
                        }*/
                        if(tagname.equalsIgnoreCase("RequestStatus")){
                            attendance_leavereason = text;
                            text = "";
                            System.out.println("OpeningLeaveBalance: "+attendance_leavereason);
                            attendance_leavereasonList.add(attendance_leavereason);
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

    Handler handler11 = new Handler(){

        public void handleMessage(Message msg){
            try {
                if((progressDialog != null) && progressDialog.isShowing() ){
                    progressDialog.dismiss();
                }
            }catch (final Exception e) {
                e.printStackTrace();
            }
            showAttendanceDetails();
           // setLeaveDetails();
        }//handleMessage(Message msg)

    };

    private void sendApproveForLeave(String jsonArrayFriends,String actionType,String selectedDateFromSpinner){


        actionType1 = actionType;
        progressDialog.setMessage("Submitting Your Leave Approval... Please wait!");
        progressDialog.setCancelable(false);
        progressDialog.show();
        /*String s="";
        JSONArray ar=jsonArrayFriends;
        try {
            s=ar.getString(0);
        } catch (JSONException e) {
            e.printStackTrace();
        }*/


        baseURL = Constants.base_url_default;
        SOAPRequestXML = Constants.soapRequestHeader+
                "<soapenv:Header/>"
                +"<soapenv:Body>"
                +"<tem:LeaveApproval>"
                +"<tem:emp_code>"+selectedDateFromSpinner+"</tem:emp_code>"
                +"<tem:taskid>"+jsonArrayFriends+"</tem:taskid>"
                +"<tem:action_status>"+actionType+"</tem:action_status>"

                +"<tem:approver_remarks>"+""+"</tem:approver_remarks>"
                +"<tem:contact_no>"+""+"</tem:contact_no>"
                +"</tem:LeaveApproval>"
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

                handler20.sendEmptyMessage(0);

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
                        break;

                    case XmlPullParser.TEXT:
                        text = xpp.getText().trim().toString();
                        System.out.println("Text data: "+text);
                        break;

                    case XmlPullParser.END_TAG:

                        if(tagname.equalsIgnoreCase("LeaveApprovalResult")){
                            STATUS = text;
                            text = "";
                            System.out.println("STATUS: "+STATUS);
                        }
                       /* if(tagname.equalsIgnoreCase(TAG_MESSAGE_ID)){
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


    Handler handler20 = new Handler(){

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
                if(STATUS.equalsIgnoreCase("1")){
                    //Toast.makeText(LoginActivity.this, MESSAGE, Toast.LENGTH_SHORT).show();

                    TAG_MESSAGE_VALUE="Leave approval successfull";
                    showSuccessDialog();
                }

                else{
                    TAG_MESSAGE_VALUE="Leave approval failed";
                    showFailureDialog();
                }

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



}
