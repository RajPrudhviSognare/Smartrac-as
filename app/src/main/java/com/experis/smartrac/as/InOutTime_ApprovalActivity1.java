package com.experis.smartrac.as;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.experis.smartrac.as.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class InOutTime_ApprovalActivity1 extends AppCompatActivity {

    private String TAG_ATTENDANCE_TYPE = "in"; //"In & out Time"

    private Spinner attendanceapproval_InOutTimeSpinner,attendanceapproval_InOutTimeSpinner1;
    private RelativeLayout listview_header_INOUT_RelativeLayoutID;
    private LinearLayout listview_header_InOutTime_for_table_LinearLayoutID;
    private ListView InOutTimeattendanceApprovalDetailsListViewID;
    private TextView InOutTimeattendanceApprovalNoDataTextViewID;

    private CheckBox approveattendance_INOUT_checkBox_header;
    private ImageView approveattendance_INOUT_headerApproveImageViewID;
    private ImageView rejectattendance_INOUT_headerRejectImageViewID;

    private ImageView inoutattendancetopbarbackImageViewID;

    private int count = 0;
    private static boolean isNotAdded = true;
    private String actionType1;

    /**
     * To save checked items, and re-add while scrolling.
     */
    private SparseBooleanArray mChecked = new SparseBooleanArray();
    private boolean checkedItems[];
    private boolean isAllChecked = false;
    private Set<String> selectionSet = null;
    private JSONArray jsonArray = new JSONArray();

    private SharedPreferences prefs;
    private SharedPreferences.Editor prefsEditor;
    private ProgressDialog progressDialog;

    private RestFullClient client,client1;

    private String TAG_STATUS = "status";
    private String TAG_ERROR = "error";
    private String TAG_MESSAGE = "message";

    private String STATUS = "";
    private String ERROR = "";
    private String MESSAGE = "";

    private String TAG_JOBJECT_DATA = "data";
    private String TAG_JARRAY_PENDINGDATELIST = "pending_date";
    private String TAG_JARRAY_ATTENDANCEDATALIST = "attendance_data";

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
    private String TAG_ASSOCIATE_ISD = "isd_code";
    private String TAG_ATTENDANCE_TYPE_PARSING = "attendance_type";
    private String TAG_ATTENDANCE_REMARKS = "remarks";
    private String TAG_ASSOCIATE_DISTANCE = "distance";

    ////private String TAG_ATTENDANCE_TIME = "in_time";
    ////private String TAG_ATTENDANCE_IMAGE = "in_img";
    ////private String TAG_ATTENDANCE_TIME1 = "out_time";
    ////private String TAG_ATTENDANCE_IMAGE1 = "out_img";

    private JSONObject JOBJECT_DATA = null;

    private List<String> attendance_dateList = null;
    private List<String> selectedList = null;

    private List<String> associate_idList1 = null;
    private List<String> associate_isdList1 = null;
    private List<String> associate_firstnameList = null;
    private List<String> associate_lastnameList = null;

    private List<String> attendanceidList = null;
    private List<String> associate_idList = null;
    private List<String> tl_idList = null;
    private List<String> first_nameList = null;
    private List<String> last_nameList = null;
    private List<String> attendance_timeList = null;
    private List<String> attendance_imageList = null;
    private List<String> associate_isdList = null;
    private List<String> attendance_typeList = null;
    private List<String> associate_remarksList = null;
   // private List<String> associate_distanceList = null;

    //private List<String> attendance_time1List = null;
    //private List<String> attendance_image1List = null;

    private List<String> tl_remarksList = null;
    private List<String> final_tl_remarksList = null;

    private List<String> disabled_CheckboxList = null;

    private ImageLoader imageLoader;
    //private ImageLoader imageLoader1;

    private String PARTICULAR_DATE = "";

    private int attendanceSelectedDatePosition = 0;

    private String selectedDateFromSpinner = null;

    private boolean userSelect = false;


    private  int attendanceSelectedassociatePosition=0;
    private String selecteduserFromSpinner=null;
    private String TAG_JARRAY_ASSOCIATELIST = "associate";
    //ASSOCIATE DETAILS TAGS
    private String TAG_ASSOCIATEID = "id";
    private String TAG_ASSOCIATEISDCODE = "isd_code";
    private String TAG_ASSOCIATEFIRSTNAME = "first_name";
    private String TAG_ASSOCIATELASTNAME = "last_name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_in_out_time_approval1);

        /*getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.inout_attendance_topbar_title);*/

        initAllViews();

        if(CommonUtils.isInternelAvailable(InOutTime_ApprovalActivity1.this)){
            System.out.println("requestAttendanceList() called inside InOutTime_ApprovalActivity");
            requestAssociateList();
        }
        else{
            Toast.makeText(InOutTime_ApprovalActivity1.this, "No internet connection!", Toast.LENGTH_LONG).show();
        }

        //Date Spinner Selection Logic
        attendanceapproval_InOutTimeSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                selectedDateFromSpinner = parent.getItemAtPosition(position).toString();
                System.out.println("selectedDateFromSpinner: "+ selectedDateFromSpinner.toString());
                //Toast.makeText(getActivity(), "Selected Item: " + selectedDateFromSpinner, Toast.LENGTH_SHORT).show();
                //Toast.makeText(getActivity(), "Selected Item Position: " + position, Toast.LENGTH_SHORT).show();

                if (userSelect) {
                    if(CommonUtils.isInternelAvailable(InOutTime_ApprovalActivity1.this)){
                        System.out.println("requestAttendanceList() called inside Spinner. userSelect = "+userSelect);
                        requestAttendanceList();
                    }
                    else{
                        Toast.makeText(InOutTime_ApprovalActivity1.this, "No internet connection!", Toast.LENGTH_LONG).show();
                    }
                    userSelect = false;
                }

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }

        });

        attendanceapproval_InOutTimeSpinner.setOnTouchListener(new View.OnTouchListener(){

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                userSelect = true;
                return false;
            }
        });


        //Date Spinner Selection Logic
        attendanceapproval_InOutTimeSpinner1.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                /*selecteduserFromSpinner = parent.getItemAtPosition(position).toString();*/
                selecteduserFromSpinner = associate_idList1.get(position);
                System.out.println("selecteduserFromSpinner: "+ selecteduserFromSpinner.toString());
                //Toast.makeText(getActivity(), "Selected Item: " + selectedDateFromSpinner, Toast.LENGTH_SHORT).show();
                //Toast.makeText(getActivity(), "Selected Item Position: " + position, Toast.LENGTH_SHORT).show();


                    if(CommonUtils.isInternelAvailable(InOutTime_ApprovalActivity1.this)){

                        requestAttendanceList();
                    }
                    else{
                        Toast.makeText(InOutTime_ApprovalActivity1.this, "No internet connection!", Toast.LENGTH_LONG).show();
                    }



            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }

        });

        attendanceapproval_InOutTimeSpinner1.setOnTouchListener(new View.OnTouchListener(){

            @Override
            public boolean onTouch(View v, MotionEvent event) {
             //   userSelect1 = true;
                return false;
            }
        });


        approveattendance_INOUT_checkBox_header.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){
                    for(int i=0;i<attendanceidList.size();i++){
                        if(disabled_CheckboxList.contains(String.valueOf(i))){
                            System.out.println("disabled_CheckboxList.contains(String.valueOf(i)): "+ i);
                            continue;
                        }
                        checkedItems[i] = true;
                    }
                    selectionSet.clear();
                    isAllChecked = true;
                    InOutTimeattendanceApprovalDetailsListViewID.setAdapter(null);
                    InOutTimeattendanceApprovalDetailsListViewID.setAdapter(new CustomAdapterForAttendanceDetails(InOutTime_ApprovalActivity1.this));

                    for(int j=0;j<attendanceidList.size();j++){
                        if(disabled_CheckboxList.contains(String.valueOf(j))){
                            System.out.println("disabled_CheckboxList.contains(String.valueOf(i)): "+ j);
                            continue;
                        }
                        selectionSet.add(attendanceidList.get(j));
                    }
                    System.out.println("Selection ID list: "+ selectionSet.toString());
                    System.out.println("Selection ID list size: "+ selectionSet.size());
                }
                if(!isChecked){
                    selectionSet.clear();
                    isAllChecked = false;
                    System.out.println("Selection ID list: "+ selectionSet.toString());
                    System.out.println("Selection ID list size: "+ selectionSet.size());
                    InOutTimeattendanceApprovalDetailsListViewID.setAdapter(null);
                    InOutTimeattendanceApprovalDetailsListViewID.setAdapter(new CustomAdapterForAttendanceDetails(InOutTime_ApprovalActivity1.this));
                }
            }
        });

        //Approve Button Click
        approveattendance_INOUT_headerApproveImageViewID.setOnClickListener(new ImageView.OnClickListener(){

            @Override
            public void onClick(View v) {

                if(selectionSet.size()==0){
                    Toast.makeText(InOutTime_ApprovalActivity1.this, "No Associate is selected!", Toast.LENGTH_LONG).show();
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
                        JSONArray jsonArrayFriends1 = new JSONArray();

                        for(int i=0;i<value1.length;i++){
                            jsonArrayFriends.put(String.valueOf(value1[i]));
                        }

                        System.out.println("jsonArrayFriends: "+jsonArrayFriends);
                        System.out.println("jsonArrayFriendsToString: "+jsonArrayFriends.toString());
                        System.out.println("selectionSet: "+selectionSet.toString());

                        //Added Later For TL_Comments
                        int pos1 = 0;
                        Iterator<String> iterator = selectionSet.iterator();
                        while(iterator.hasNext()){
                            String item = iterator.next().toString();
                            if(attendanceidList.contains(item)){
                                int pos = attendanceidList.indexOf(item);
                                final_tl_remarksList.add(pos1,tl_remarksList.get(pos).toString());
                                pos1++;
                            }

                        }//while
                        System.out.println("selectionSet: "+selectionSet.toString());
                        System.out.println("final_tl_remarksList: "+final_tl_remarksList.toString());
                        if(final_tl_remarksList.size()!=0){
                            for(int i=0;i<final_tl_remarksList.size();i++){
                                jsonArrayFriends1.put(final_tl_remarksList.get(i).toString());
                            }

                            System.out.println("jsonArrayFriends1: "+jsonArrayFriends1);
                            System.out.println("jsonArrayFriends1ToString: "+jsonArrayFriends1.toString());
                            System.out.println("final_tl_remarksList: "+final_tl_remarksList.toString());
                        }//if
                        //Added Later For TL_Comments

                        SendRequestForApproveOrReject(jsonArrayFriends,jsonArrayFriends1,"approved",selectedDateFromSpinner);

                    }//if(value1.length!=0)

                }//if(selectionSet.size()!=0)

            }//onClick(View v)
        });

        //Reject Button Click
        rejectattendance_INOUT_headerRejectImageViewID.setOnClickListener(new ImageView.OnClickListener(){

            @Override
            public void onClick(View v) {

                if(selectionSet.size()==0){
                    Toast.makeText(InOutTime_ApprovalActivity1.this, "No Associate is selected!", Toast.LENGTH_LONG).show();
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
                        JSONArray jsonArrayFriends1 = new JSONArray();

                        for(int i=0;i<value1.length;i++){
                            jsonArrayFriends.put(String.valueOf(value1[i]));
                        }

                        System.out.println("jsonArrayFriends: "+jsonArrayFriends);
                        System.out.println("jsonArrayFriendsToString: "+jsonArrayFriends.toString());

                        System.out.println("selectionSet: "+selectionSet.toString());

                        //Added Later For TL_Comments
                        int pos1 = 0;
                        Iterator<String> iterator = selectionSet.iterator();
                        while(iterator.hasNext()){
                            String item = iterator.next().toString();
                            if(attendanceidList.contains(item)){
                                int pos = attendanceidList.indexOf(item);
                                final_tl_remarksList.add(pos1,tl_remarksList.get(pos).toString());
                                pos1++;
                            }

                        }//while
                        System.out.println("selectionSet: "+selectionSet.toString());
                        System.out.println("final_tl_remarksList: "+final_tl_remarksList.toString());
                        if(final_tl_remarksList.size()!=0){
                            for(int i=0;i<final_tl_remarksList.size();i++){
                                jsonArrayFriends1.put(final_tl_remarksList.get(i).toString());
                            }

                            System.out.println("jsonArrayFriends1: "+jsonArrayFriends1);
                            System.out.println("jsonArrayFriends1ToString: "+jsonArrayFriends1.toString());
                            System.out.println("final_tl_remarksList: "+final_tl_remarksList.toString());
                        }//if
                        //Added Later For TL_Comments

                        SendRequestForApproveOrReject(jsonArrayFriends,jsonArrayFriends1,"rejected",selectedDateFromSpinner);

                    }//if(value1.length!=0)

                }//if(selectionSet.size()!=0)

            }//onClick(View v)
        });

        //Top Back Button Click
        inoutattendancetopbarbackImageViewID.setOnClickListener(new ImageView.OnClickListener(){
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });



    }//onCreate()

    private void initAllViews(){

        //shared preference
        prefs = InOutTime_ApprovalActivity1.this.getSharedPreferences(CommonUtils.PREFERENCE_NAME,MODE_PRIVATE);
        prefsEditor = prefs.edit();

        attendanceapproval_InOutTimeSpinner = (Spinner)findViewById(R.id.attendanceapproval_InOutTimeSpinner);
        listview_header_INOUT_RelativeLayoutID = (RelativeLayout)findViewById(R.id.listview_header_INOUT_RelativeLayoutID);
        listview_header_InOutTime_for_table_LinearLayoutID = (LinearLayout)findViewById(R.id.listview_header_InOutTime_for_table_LinearLayoutID);
        InOutTimeattendanceApprovalDetailsListViewID = (ListView)findViewById(R.id.InOutTimeattendanceApprovalDetailsListViewID);
        InOutTimeattendanceApprovalNoDataTextViewID = (TextView)findViewById(R.id.InOutTimeattendanceApprovalNoDataTextViewID);

        approveattendance_INOUT_checkBox_header = (CheckBox)findViewById(R.id.approveattendance_INOUT_checkBox_header);
        approveattendance_INOUT_headerApproveImageViewID = (ImageView)findViewById(R.id.approveattendance_INOUT_headerApproveImageViewID);
        rejectattendance_INOUT_headerRejectImageViewID = (ImageView)findViewById(R.id.rejectattendance_INOUT_headerRejectImageViewID);
        attendanceapproval_InOutTimeSpinner1=(Spinner)findViewById(R.id.attendanceapproval_InOutTimeSpinner1);

        inoutattendancetopbarbackImageViewID = (ImageView)findViewById(R.id.inoutattendancetopbarbackImageViewID);

        attendance_dateList = new ArrayList<String>(0);
        selectedList = new ArrayList<String>(0);

        attendanceidList = new ArrayList<String>(0);
        associate_idList = new ArrayList<String>(0);
        tl_idList = new ArrayList<String>(0);
        first_nameList = new ArrayList<String>(0);
        last_nameList = new ArrayList<String>(0);
        attendance_timeList = new ArrayList<String>(0);
        attendance_imageList = new ArrayList<String>(0);
        associate_isdList = new ArrayList<String>(0);
        attendance_typeList = new ArrayList<String>(0);
        associate_remarksList = new ArrayList<String>(0);
        tl_remarksList = new ArrayList<String>(0);
        final_tl_remarksList = new ArrayList<String>(0);
      //  associate_distanceList = new ArrayList<String>(0);
        associate_idList1=new ArrayList<String>(0);
        associate_isdList1=new ArrayList<String>(0);
        associate_firstnameList=new ArrayList<String>(0);
        associate_lastnameList=new ArrayList<String>(0);

        ////attendance_time1List = new ArrayList<String>(0);
        ////attendance_image1List = new ArrayList<String>(0);

        disabled_CheckboxList = new ArrayList<String>(0);

        selectionSet = new HashSet<String>(0);

        imageLoader = new ImageLoader(InOutTime_ApprovalActivity1.this);
        ////imageLoader1 = new ImageLoader(InOutTime_ApprovalActivity.this);

        //Progress Dialog
        progressDialog = new ProgressDialog(InOutTime_ApprovalActivity1.this);
    }

    private void SendRequestForApproveOrReject(JSONArray jsonArrayFriends,JSONArray jsonArrayFriends1,String actionType,String selectedDateFromSpinner){

        actionType1 = actionType;
        JSONObject jObj = null;
        JSONObject jObj1 = null;
        jObj = new JSONObject();
        jObj1 = new JSONObject();

        try {
            jObj.put("ids",jsonArrayFriends);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println("jObj: " + jObj);
        try {
            jObj1.put("remarks",jsonArrayFriends1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println("jObj1: " + jObj1);

        if(actionType.equalsIgnoreCase("approved")){
            //progressDialog.setTitle("InTime Attendance Approval");
        }
        if(actionType.equalsIgnoreCase("rejected")){
            //progressDialog.setTitle("InTime Attendance Reject");
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
        client.AddParam("attendance_remarks", jObj1.toString());
        client.AddParam("apporver_id", prefs.getString("USERID",""));

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
    Handler handler1 = new Handler(){

        public void handleMessage(Message msg){

            progressDialog.dismiss();

            //Success
            if(client.responseCode==200){

                //Success
                if(STATUS.equalsIgnoreCase("true")){
                    showSuccessDialog();
                    selectionSet.clear();
                    final_tl_remarksList.clear();
                    approveattendance_INOUT_checkBox_header.setChecked(false);
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
        android.app.AlertDialog.Builder aldb = new android.app.AlertDialog.Builder(InOutTime_ApprovalActivity1.this);
        aldb.setTitle("Success!");
        aldb.setCancelable(false);
        if(actionType1.equalsIgnoreCase("approved")){
            aldb.setMessage("You Have Successfully Approved Selected Associates In/out time. \n\nThank You");
        }
        if(actionType1.equalsIgnoreCase("rejected")){
            aldb.setMessage("You Have Successfully Rejected Selected Associates In/out time. \n\nThank You");
        }
        aldb.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(CommonUtils.isInternelAvailable(InOutTime_ApprovalActivity1.this)){
                    requestAttendanceList();
                }
                else{
                    Toast.makeText(InOutTime_ApprovalActivity1.this, "No internet connection!", Toast.LENGTH_LONG).show();
                }
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

    private void requestAttendanceList(){

        InOutTimeattendanceApprovalNoDataTextViewID.setVisibility(View.GONE);
        attendance_dateList.clear();
        selectedList.clear();

        attendanceidList.clear();
        associate_idList.clear();
        tl_idList.clear();
        first_nameList.clear();
        last_nameList.clear();
        attendance_timeList.clear();
        attendance_imageList.clear();
        associate_isdList.clear();
        attendance_typeList.clear();
        associate_remarksList.clear();
        tl_remarksList.clear();
        //associate_distanceList.clear();

        //attendance_time1List.clear();
        //attendance_image1List.clear();

        disabled_CheckboxList.clear();

        //progressDialog.setTitle("InTime Attendance Details");
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
        client.AddParam("tl_id",selecteduserFromSpinner );


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
                        String associate_isd = null;
                        String attendance_type = null;
                        String associate_remarks = null;
                        String tl_remarks = null;
                        String associate_distance = null;

                        //String attendance_time1 = null;
                        //String attendance_image1 = null;

                        System.out.println("##########All Pending Date List details###################");

                        attendance_dateList.clear();
                        selectedList.clear();

                        if(pendingDateArray.length()==0){
                            //attendanceApprovalNoDataTextViewID.setVisibility(View.VISIBLE);
                            System.out.println("Inside 'if(pendingDateArray.length()==0)': InOutTimeattendanceApprovalNoDataTextViewID.setVisibility(View.VISIBLE): "+InOutTimeattendanceApprovalNoDataTextViewID.getVisibility());
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
                        associate_isdList.clear();
                        attendance_typeList.clear();
                        associate_remarksList.clear();
                        tl_remarksList.clear();
                      //  associate_distanceList.clear();

                        //attendance_time1List.clear();
                        //attendance_image1List.clear();

                        disabled_CheckboxList.clear();

                        if(attendanceDataArray.length()==0){
                            //attendanceApprovalNoDataTextViewID.setVisibility(View.VISIBLE);
                            System.out.println("Inside 'if(attendanceDataArray.length()==0)': InOutTimeattendanceApprovalNoDataTextViewID.setVisibility(View.VISIBLE): "+InOutTimeattendanceApprovalNoDataTextViewID.getVisibility());
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
                                        associate_isd = c1.getString(TAG_ASSOCIATE_ISD);
                                        attendance_type = c1.getString(TAG_ATTENDANCE_TYPE_PARSING);
                                        associate_remarks = c1.getString(TAG_ATTENDANCE_REMARKS);
                                       // associate_distance = c1.getString(TAG_ASSOCIATE_DISTANCE);

                                        //attendance_time1 = c1.getString(TAG_ATTENDANCE_TIME1);
                                        //attendance_image1 = c1.getString(TAG_ATTENDANCE_IMAGE1);

                                        //attendanceidList.add("\""+attendanceid+"\"");
                                        attendanceidList.add(attendanceid);
                                        associate_idList.add(associate_id);
                                        tl_idList.add(tl_id);
                                        first_nameList.add(first_name);
                                        last_nameList.add(last_name);
                                        attendance_timeList.add(attendance_time);
                                        attendance_imageList.add(Constants.BASE_URL_ATTENDANCE_LOGO+attendance_image);
                                        associate_isdList.add(associate_isd);
                                        attendance_typeList.add(attendance_type);
                                        associate_remarksList.add(associate_remarks);
                                        tl_remarksList.add("");
                                      //  associate_distanceList.add(associate_distance);

                                        //attendance_time1List.add(attendance_time1);
                                        //ttendance_image1List.add(Constants.BASE_URL_ATTENDANCE_LOGO+attendance_image1);

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
                        System.out.println("Total associate_isdList: " + associate_isdList.size());
                        System.out.println("Total attendance_typeList: " + attendance_typeList.size());
                        System.out.println("Total associate_remarksList: " + associate_remarksList.size());
                        System.out.println("Total tl_remarksList: " + tl_remarksList.size());
                      //  System.out.println("Total associate_distanceList: " + associate_distanceList.size());

                        //System.out.println("Total attendance_time1List: " + attendance_time1List.size());
                        //System.out.println("Total attendance_image1List: " + attendance_image1List.size());

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
        AlertDialog.Builder aldb = new AlertDialog.Builder(InOutTime_ApprovalActivity1.this);
        aldb.setTitle("Failed!");
        aldb.setMessage("\nReason: "+MESSAGE);
        aldb.setPositiveButton("OK", null);
        aldb.show();
    }

    private void showAttendanceDetails(){

        if(attendanceidList.size()!=0){
            InOutTimeattendanceApprovalDetailsListViewID.setVisibility(View.VISIBLE);
            InOutTimeattendanceApprovalNoDataTextViewID.setVisibility(View.GONE);
            SetDropDownItems();
            setAttendanceListView();
        }
        if(attendanceidList.size()==0){
            InOutTimeattendanceApprovalDetailsListViewID.setVisibility(View.GONE);
            InOutTimeattendanceApprovalNoDataTextViewID.setVisibility(View.VISIBLE);
        }
    }

    private void SetDropDownItems(){
        if(attendance_dateList.size()!=0){
            ArrayAdapter dataAdapter = new ArrayAdapter (InOutTime_ApprovalActivity1.this, android.R.layout.simple_spinner_item, attendance_dateList);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            attendanceapproval_InOutTimeSpinner.setAdapter(dataAdapter);
            System.out.println("Selected Spinner Item Position: "+ attendanceSelectedDatePosition);
            attendanceapproval_InOutTimeSpinner.setSelection(attendanceSelectedDatePosition);
        }
        else{
             InOutTimeattendanceApprovalNoDataTextViewID.setVisibility(View.VISIBLE);
             System.out.println("Inside 'SetDropDownItems()': InOutTimeattendanceApprovalNoDataTextViewID.setVisibility(View.VISIBLE): "+InOutTimeattendanceApprovalNoDataTextViewID.getVisibility());
        }
    }

    private void setAttendanceListView(){
        checkedItems = new boolean[attendanceidList.size()];
        InOutTimeattendanceApprovalDetailsListViewID.setAdapter(new CustomAdapterForAttendanceDetails(InOutTime_ApprovalActivity1.this));
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
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub

            View view;
            ViewHolder viewHolder = new ViewHolder();

            if(convertView==null){

                LayoutInflater inflater = (LayoutInflater)cntx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.custom_layout_for_inout_attendance_approval, null);
            }
            else{

                view = convertView;
            }

            view.setTag(viewHolder);
            final int pos1 = position;

            final TextView nameTextView = (TextView)view.findViewById(R.id.custom_approveattendance_INOUTNameTextViewID);
            final TextView isdTextView = (TextView)view.findViewById(R.id.custom_approveattendance_INOUTISDTextViewID);
            final TextView timeTextView = (TextView)view.findViewById(R.id.custom_approveattendance_INOUT_INTimeTextViewID);
            //final TextView time1TextView = (TextView)view.findViewById(R.id.custom_approveattendance_INOUT_OUTTimeTextViewID);
            final TextView distanceTextView = (TextView)view.findViewById(R.id.custom_approveattendance_DistanceTextViewID);

            viewHolder.checkbox = (CheckBox)view.findViewById(R.id.custom_approveattendance_INOUTcheckBoxID);

            viewHolder.selfieImage = (ImageView)view.findViewById(R.id.custom_approveattendance_INOUT_INSelfieImageViewID);
            //viewHolder.selfieImage1 = (ImageView)view.findViewById(R.id.custom_approveattendance_INOUT_OUTSelfieImageViewID);

            final TextView commentsAssociateTextView = (TextView)view.findViewById(R.id.custom_approveattendance_CommentsTextViewID);
            viewHolder.editText = (EditText)view.findViewById(R.id.custom_approveattendance_CommentsTLEditTextID);

            final ViewHolder holder = (ViewHolder)view.getTag();

            nameTextView.setText(first_nameList.get(position)+" "+last_nameList.get(position));
            isdTextView.setText("("+associate_isdList.get(position)+")");
            timeTextView.setText(attendance_typeList.get(position)+"   "+attendance_timeList.get(position));
            //time1TextView.setText(attendance_time1List.get(position));
            imageLoader.DisplayImage(attendance_imageList.get(position), holder.selfieImage);
            //imageLoader1.DisplayImage(attendance_image1List.get(position), holder.selfieImage1);
            commentsAssociateTextView.setText(associate_remarksList.get(position));
            //distanceTextView.setText(associate_distanceList.get(position)+"");


            //if(attendance_timeList.get(pos1).toString().equalsIgnoreCase("null")||attendance_time1List.get(pos1).toString().equalsIgnoreCase("null")){
            if(attendance_timeList.get(pos1).toString().equalsIgnoreCase("null")){
                holder.checkbox.setEnabled(false);
                holder.checkbox.setChecked(false);
                holder.checkbox.setClickable(false);
                holder.checkbox.setFocusable(false);
                holder.checkbox.setFocusableInTouchMode(false);
                //holder.checkbox.setVisibility(View.INVISIBLE);

                disabled_CheckboxList.add(String.valueOf(pos1));

                System.out.println("attendance_timeList.get(pos1) Value: "+attendance_timeList.get(pos1));
                //System.out.println("attendance_time1List.get(pos1) Value: "+attendance_time1List.get(pos1));
                System.out.println("holder.checkbox status at (pos1): "+holder.checkbox.getVisibility());
            }

            if(isAllChecked){
                holder.checkbox.setChecked(checkedItems[position]);
            }

            //Checkbox is checked
            holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    // TODO Auto-generated method stub
                    if(isChecked){
                        //Toast.makeText(getActivity(), "Checked at "+String.valueOf(pos1), Toast.LENGTH_SHORT).show();
                        selectionSet.add(attendanceidList.get(pos1));
                        System.out.println("Selection ID list: "+ selectionSet.toString());
                        System.out.println("Total Set size(After Add): "+selectionSet.size());
                    }
                    if(!isChecked){
                        //Toast.makeText(getActivity(), "UnChecked at "+String.valueOf(pos1), Toast.LENGTH_SHORT).show();
                        selectionSet.remove(attendanceidList.get(pos1));
                        System.out.println("Selection ID list: "+ selectionSet.toString());
                        System.out.println("Total Set size(After Remove): "+selectionSet.size());

                    }//if

                }

            });

            //Selfie image is clicked
            holder.selfieImage.setOnClickListener(new ImageView.OnClickListener(){

                @Override
                public void onClick(View v) {
                    //Toast.makeText(getActivity(), "Selfie image is clieck! at position "+String.valueOf(pos1), Toast.LENGTH_SHORT).show();
                    displayLargeSelfie(attendance_imageList.get(pos1));
                }
            });
            /*//Selfie1 image is clicked
            holder.selfieImage1.setOnClickListener(new ImageView.OnClickListener(){

                @Override
                public void onClick(View v) {
                    //Toast.makeText(getActivity(), "Selfie image is clieck! at position "+String.valueOf(pos1), Toast.LENGTH_SHORT).show();
                    displayLargeSelfie(attendance_image1List.get(pos1));
                }
            });*/

            holder.editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }
                @Override
                public void afterTextChanged(Editable s) {
                    //holder.editText.setText(s.toString());
                    System.out.println("afterTextChanged(Editable s): " + s.toString());
                    tl_remarksList.set(pos1,s.toString()); //Updating with a new value
                }
            });


            return view;

        }

        class ViewHolder{
            CheckBox checkbox;
            ImageView selfieImage;
            //ImageView selfieImage1;
            EditText editText;
        }

    }//CustomAdapterForAttendanceDetails Class

    private void displayLargeSelfie(String imageURL){
        final String url = imageURL;
        Intent i = new Intent(InOutTime_ApprovalActivity1.this,LargerSelfieImageDisplayActivity.class);
        i.putExtra("IMAGEPATH",url);
        InOutTime_ApprovalActivity1.this.startActivity(i);
    }

    @Override
    public void onBackPressed()
    {
        InOutTime_ApprovalActivity1.this.finish();
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


    //All Associate List
    private void requestAssociateList(){
        associate_idList1.clear();
        associate_isdList1.clear();
        associate_firstnameList.clear();
        associate_lastnameList.clear();

        //progressDialog.setTitle("Associate Details");
        progressDialog.setMessage("Loading... Please wait!");
        progressDialog.setCancelable(false);
        progressDialog.show();
        client1 = new RestFullClient(Constants.BASE_URL+Constants.GETASSOCIATE_BY_TEAMLEAD_RELATIVE_URI);
        client1.AddParam("tl_id",prefs.getString("USERID",""));
        client1.AddParam("role_id",prefs.getString("USERROLEID",""));
        System.out.println("tl_id:"+prefs.getString("USERID",""));
        new Thread(new Runnable(){
            @Override
            public void run() {
                // TODO Auto-generated method stub

                try {
                    client1.Execute(1); //POST Request

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                receiveDataForServerResponse2(client1.jObj);
                handler11.sendEmptyMessage(0);

            }

        }).start();

    }
    private void receiveDataForServerResponse2(JSONObject jobj){

        try{

            if(client1.responseCode==200){

                STATUS =  jobj.getString(TAG_STATUS);
                MESSAGE =  jobj.getString(TAG_MESSAGE);

                System.out.println("STATUS: responseCode==200: "+ STATUS);
                System.out.println("MESSAGE: responseCode==200: "+ MESSAGE);

                if(STATUS.equalsIgnoreCase("true")) {

                    JOBJECT_DATA = jobj.getJSONObject(TAG_JOBJECT_DATA);
                    System.out.println("JOBJECT_DATA: " + JOBJECT_DATA.toString());

                    if (JOBJECT_DATA != null) {

                        JSONArray pendingDateArray = JOBJECT_DATA.getJSONArray(TAG_JARRAY_ASSOCIATELIST);
                        JSONObject c = null;
                        String id = null;
                        String isd = null;
                        String fname = null;
                        String lname = null;

                        System.out.println("##########All ASSOCIATE List details###################");

                        associate_idList1.clear();
                        associate_isdList1.clear();
                        associate_firstnameList.clear();
                        associate_lastnameList.clear();

                        if(pendingDateArray.length()==0){
                        }
                        if(pendingDateArray.length()!=0){

                            for(int i = 0; i < pendingDateArray.length(); i++) {

                                try {
                                    c = pendingDateArray.getJSONObject(i);
                                    System.out.println("C is : " + c);
                                    if (c != null) {
                                        id = c.getString(TAG_ASSOCIATEID);
                                        isd = c.getString(TAG_ASSOCIATEISDCODE);
                                        fname = c.getString(TAG_ASSOCIATEFIRSTNAME);
                                        lname = c.getString(TAG_ASSOCIATELASTNAME);

                                        associate_idList1.add(id);
                                        associate_isdList1.add(isd);
                                        associate_firstnameList.add(fname+" "+lname+"( "+isd+" )");
                                        associate_lastnameList.add(lname);
                                    }
                                } catch (Exception e) {
                                }

                            }//for

                        }//if(pendingDateArray.length()!=0)

                        System.out.println("Total associate_idList: " + associate_idList1.size());
                        System.out.println("Total associate_isdList: " + associate_isdList1.size());
                        System.out.println("Total associate_firstnameList: " + associate_firstnameList.size());
                        System.out.println("Total associate_lastnameList: " + associate_lastnameList.size());
                        System.out.println("##########End Of All ASSOCIATE List details###################");


                    }//if (JOBJECT_DATA != null)
                    else{
                        System.out.println("JOBJECT_DATA is Null");
                    }

                }//if(STATUS.equalsIgnoreCase("true")

            }//if(client.responseCode==200)
            if(client1.responseCode!=200){

                STATUS =  jobj.getString(TAG_STATUS);
                MESSAGE =  jobj.getString(TAG_MESSAGE);

                System.out.println("STATUS: responseCode!=200: "+ STATUS);
                System.out.println("MESSAGE: responseCode!=200: "+ MESSAGE);

            }//if(client.responseCode!=200)

        }
        catch(Exception e){}

    }
    Handler handler11 = new Handler(){

        public void handleMessage(Message msg){

            progressDialog.dismiss();

            //Success
            if(client1.responseCode==200){

                //Toast.makeText(SignupActivity.this, MESSAGE, Toast.LENGTH_SHORT).show();

                //Login success
                if(STATUS.equalsIgnoreCase("true")){
                    //Toast.makeText(TargetSettingActivity.this, MESSAGE, Toast.LENGTH_SHORT).show();
                    showAssociateDetails();
                }

                //Login failed
                if(STATUS.equalsIgnoreCase("false")){
                    showFailureDialog();
                }

            }

            //Login failed
            if(client1.responseCode!=200){
                //Toast.makeText(TargetSettingActivity.this, MESSAGE, Toast.LENGTH_SHORT).show();
                showFailureDialog();
            }

        }//handleMessage(Message msg)

    };

    private void showAssociateDetails(){
        if(associate_idList1.size()!=0){
            setAssociateListView();
        }
        if(associate_idList1.size()==0){

        }
    }

    private void setAssociateListView(){

        if(associate_idList1.size()!=0){
            InOutTimeattendanceApprovalDetailsListViewID.setVisibility(View.VISIBLE);
            InOutTimeattendanceApprovalNoDataTextViewID.setVisibility(View.GONE);
            SetDropDownItems1();

        }
        if(associate_idList1.size()==0){
            InOutTimeattendanceApprovalDetailsListViewID.setVisibility(View.GONE);
            InOutTimeattendanceApprovalNoDataTextViewID.setVisibility(View.VISIBLE);
        }
    }

    private void SetDropDownItems1(){
        if(associate_idList1.size()!=0){
            ArrayAdapter dataAdapter = new ArrayAdapter (InOutTime_ApprovalActivity1.this, android.R.layout.simple_spinner_item, associate_firstnameList);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            attendanceapproval_InOutTimeSpinner1.setAdapter(dataAdapter);
            System.out.println("Selected Spinner Item Position: "+ attendanceSelectedDatePosition);
            attendanceapproval_InOutTimeSpinner1.setSelection(attendanceSelectedassociatePosition);
        }
        else{
            InOutTimeattendanceApprovalNoDataTextViewID.setVisibility(View.VISIBLE);
            System.out.println("Inside 'SetDropDownItems()': InOutTimeattendanceApprovalNoDataTextViewID.setVisibility(View.VISIBLE): "+InOutTimeattendanceApprovalNoDataTextViewID.getVisibility());
        }
    }


}//Main Class
