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
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AttendanceApprovalWeeklyOffFragment extends Fragment {

    private String TAG_ATTENDANCE_TYPE = "weekly_off"; //"Weekly Off"

    private Spinner attendanceapprovalIntimeSpinner;
    private ListView attendanceApprovalDetailsListViewID;
    private Button attendanceapprovalIntimeGetDetailsBtnID;
    private LinearLayout attendanceapprovalIntimeLayoutID;

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
    private List<String> associate_isdList = null;

    private ImageLoader imageLoader;

    private String PARTICULAR_DATE = "";

    private int attendanceSelectedDatePosition = 0;
    private String selectedDateFromSpinner = null;
    private boolean userSelect = false;

    private TextView attendanceApprovalNoDataTextViewID;

    private View view = null;

    public AttendanceApprovalWeeklyOffFragment() {
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

            view = inflater.inflate(R.layout.attendance_approval_weeklyoff_fragment, container, false);

            attendanceapprovalIntimeSpinner = (Spinner)view.findViewById(R.id.attendanceapprovalIntimeSpinner1);
            attendanceApprovalDetailsListViewID = (ListView)view.findViewById(R.id.attendanceApprovalDetailsListViewID1);

            approveattendance_checkBox_header = (CheckBox)view.findViewById(R.id.approveattendance_checkBox_header1);
            approveattendance_headerApproveImageViewID = (ImageView)view.findViewById(R.id.approveattendance_headerApproveImageViewID1);
            rejectattendance_headerRejectImageViewID = (ImageView)view.findViewById(R.id.rejectattendance_headerRejectImageViewID1);

            attendanceApprovalNoDataTextViewID = (TextView)view.findViewById(R.id.attendanceApprovalNoDataTextViewID1);

            attendanceapprovalIntimeGetDetailsBtnID = (Button)view.findViewById(R.id.attendanceapprovalIntimeGetDetailsBtnID1);
            attendanceapprovalIntimeLayoutID = (LinearLayout) view.findViewById(R.id.attendanceapprovalIntimeLayoutID1);

        }//if(view==null)
        else{
            //((ViewGroup) view.getParent()).removeView(view);
        }


       /*if(CommonUtils.isInternelAvailable(getActivity())){
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
            System.out.println("requestAttendanceList() called inside onActivityCreated() of AttendanceApprovalWeeklyOffFragment");
            requestAttendanceList();
        }
        else{
            Toast.makeText(getActivity(), "No internet connection!", Toast.LENGTH_SHORT).show();
        }

        /*attendanceapprovalIntimeGetDetailsBtnID.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(CommonUtils.isInternelAvailable(getActivity())){
                    System.out.println("requestAttendanceList() called inside onActivityCreated() of AttendanceApprovalWeeklyOffFragment");
                    attendanceapprovalIntimeLayoutID.setVisibility(View.VISIBLE);
                    requestAttendanceList();
                }
                else{
                    Toast.makeText(getActivity(), "No internet connection!", Toast.LENGTH_SHORT).show();
                }
            }
        });*/

        attendanceapprovalIntimeSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                selectedDateFromSpinner = parent.getItemAtPosition(position).toString();
                System.out.println("selectedDateFromSpinner: "+ selectedDateFromSpinner.toString());
                //Toast.makeText(getActivity(), "Selected Item: " + selectedDateFromSpinner, Toast.LENGTH_SHORT).show();
                //Toast.makeText(getActivity(), "Selected Item Position: " + position, Toast.LENGTH_SHORT).show();

                if (userSelect) {
                    if(CommonUtils.isInternelAvailable(getActivity())){
                        requestAttendanceList();
                    }
                    else{
                        Toast.makeText(getActivity(), "No internet connection!", Toast.LENGTH_SHORT).show();
                    }
                    userSelect = false;
                }

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
        });

        approveattendance_checkBox_header.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){
                    for(int i=0;i<attendanceidList.size();i++){
                        checkedItems[i] = true;
                    }
                    selectionSet.clear();
                    isAllChecked = true;
                    attendanceApprovalDetailsListViewID.setAdapter(null);
                    attendanceApprovalDetailsListViewID.setAdapter(new CustomAdapterForAttendanceDetails(getActivity()));

                    for(int j=0;j<attendanceidList.size();j++){
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

                        SendRequestForApproveOrReject(jsonArrayFriends,"approved",selectedDateFromSpinner);

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

                        SendRequestForApproveOrReject(jsonArrayFriends,"rejected",selectedDateFromSpinner);

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
        if(actionType.equalsIgnoreCase("approved")){
            //progressDialog.setTitle("Weekly Off Attendance Approval");
        }
        if(actionType.equalsIgnoreCase("rejected")){
            //progressDialog.setTitle("Weekly Off Attendance Reject");
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
        if(actionType1.equalsIgnoreCase("approved")){
            aldb.setMessage("You Have Successfully Approved Selected Associates' Weekly Off. \n\nThank You");
        }
        if(actionType1.equalsIgnoreCase("rejected")){
            aldb.setMessage("You Have Successfully Rejected Selected Associates' Weekly Off. \n\nThank You");
        }
        aldb.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(CommonUtils.isInternelAvailable(getActivity())){
                    requestAttendanceList();
                }
                else{
                    Toast.makeText(getActivity(), "No internet connection!", Toast.LENGTH_SHORT).show();
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
        associate_isdList.clear();

        //progressDialog.setTitle("Weekly Off Attendance Details");
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

                //success
                if(STATUS.equalsIgnoreCase("true")){
                    //showSuccessDialog();
                    //Toast.makeText(getActivity(), MESSAGE, Toast.LENGTH_SHORT).show();
                    showAttendanceDetails();
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

        SetDropDownItems();
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
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        View view;
        ViewHolder viewHolder = new ViewHolder();

        if(convertView==null){

            LayoutInflater inflater = (LayoutInflater)cntx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.custom_layout_for_attendance_approval_allleavetypes, null);
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

        viewHolder.checkbox = (CheckBox)view.findViewById(R.id.custom_approveattendance_AllLeaveTypes_checkBoxID);
        //viewHolder.selfieImage = (ImageView)view.findViewById(R.id.custom_approveattendance_SelfieImageViewID);

        final ViewHolder holder = (ViewHolder)view.getTag();

        nameTextView.setText(first_nameList.get(position)+" "+last_nameList.get(position));
        idTextView.setText("ISD: "+associate_isdList.get(position));
        dateTextView.setText("Date: "+attendance_leavedateList.get(position));
        reasonTextView.setText("Reason: "+attendance_leavereasonList.get(position));
        //timeTextView.setText(attendance_timeList.get(position));
        //imageLoader.DisplayImage(attendance_imageList.get(position), holder.selfieImage);


        //viewHolder.checkbox.setTag(pos1); // This line is important.
        //holder.checkbox.setChecked(checkedItems[pos1]);
        //view.setSelected(checkedItems[pos1]);

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
        /*if(CommonUtils.isInternelAvailable(getActivity())){
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


}
