package com.experis.smartrac.as;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.experis.smartrac.as.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MessagesForTLActivity extends AppCompatActivity {

    private PowerManager.WakeLock mWakeLock;
    private SharedPreferences prefs;
    private SharedPreferences.Editor prefsEditor;
    private ProgressDialog progressDialog;

    private CheckBox messagesTL_checkBox_listheaderID;
    private ListView messagesTLAssociatesListViewID;
    private TextView messagesTLNoDataTextViewID;
    private EditText messagesTLMsgBoxEdittextID;
    private ImageView messagesTLSubmitImageViewID;
    private LinearLayout messagesTLBottombarLinearLayoutID;

    private ImageView messagesTLtopbarbackImageViewID;
    private ImageView messagesTLtopbarusericonImageViewID;

    /**
     * To save checked items, and re-add while scrolling.
     */
    private SparseBooleanArray mChecked = new SparseBooleanArray();
    private boolean checkedItems[];
    private boolean isAllChecked = false;
    private Set<String> selectionSet = null;
    private List<String> selectionList = null;
    private JSONArray jsonArray = new JSONArray();

    private RestFullClient client;

    private String TAG_STATUS = "status";
    private String TAG_ERROR = "error";
    private String TAG_MESSAGE = "message";

    private String STATUS = "";
    private String ERROR = "";
    private String MESSAGE = "";

    private String TAG_JOBJECT_DATA = "data";
    private String TAG_JARRAY_ASSOCIATELIST = "associate_data";

    private JSONObject JOBJECT_DATA = null;

    //ASSOCIATE DETAILS TAGS
    private String TAG_ASSOCIATEID = "id";
    private String TAG_ASSOCIATEISDCODE = "isd_code";
    private String TAG_ASSOCIATEFIRSTNAME = "first_name";
    private String TAG_ASSOCIATELASTNAME = "last_name";
    private List<String> associate_idList = null;
    private List<String> associate_isdList = null;
    private List<String> associate_firstnameList = null;
    private List<String> associate_lastnameList = null;

    private String msg = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages_for_tl);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.messagetl_topbar_layout);

        /*PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "AttendanceTracking-DoNotDimScreen");
        mWakeLock.acquire();*/

        initAllViews();

        if(CommonUtils.isInternelAvailable(MessagesForTLActivity.this)){
            requestAssociateList();
        }
        else{
            Toast.makeText(MessagesForTLActivity.this, "No internet connection!", Toast.LENGTH_LONG).show();
        }

        messagesTL_checkBox_listheaderID.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){
                    for(int i=0;i<associate_isdList.size();i++){
                        checkedItems[i] = true;
                    }
                    selectionSet.clear();
                    selectionList.clear();
                    isAllChecked = true;
                    messagesTLAssociatesListViewID.setAdapter(null);
                    messagesTLAssociatesListViewID.setAdapter(new CustomAdapterForAssociatesList(MessagesForTLActivity.this));

                    for(int j=0;j<associate_isdList.size();j++){
                        selectionSet.add(associate_isdList.get(j));
                        selectionList.add(associate_isdList.get(j));
                    }
                    System.out.println("Selection ID set: "+ selectionSet.toString());
                    System.out.println("Selection ID set size: "+ selectionSet.size());
                    System.out.println("Selection ID list: "+ selectionList.toString());
                    System.out.println("Selection ID list size: "+ selectionList.size());
                }
                if(!isChecked){
                    selectionSet.clear();
                    selectionList.clear();
                    isAllChecked = false;
                    System.out.println("Selection ID set: "+ selectionSet.toString());
                    System.out.println("Selection ID set size: "+ selectionSet.size());
                    System.out.println("Selection ID list: "+ selectionList.toString());
                    System.out.println("Selection ID list size: "+ selectionList.size());
                    messagesTLAssociatesListViewID.setAdapter(null);
                    messagesTLAssociatesListViewID.setAdapter(new CustomAdapterForAssociatesList(MessagesForTLActivity.this));
                }

            }
        });

        messagesTLSubmitImageViewID.setOnClickListener(new ImageView.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(selectionList.size()==0){
                    //Toast.makeText(MessagesForTLActivity.this, "No Associate is selected!", Toast.LENGTH_LONG).show();
                    showNoAssociateDialog("Kindly select an associate!");
                }//selectionList.size()==0
                if(selectionList.size()!=0){
                    try {
                        if(CommonUtils.isInternelAvailable(MessagesForTLActivity.this)){
                            validateData();
                        }
                        else{
                            Toast.makeText(MessagesForTLActivity.this, "No internet connection!", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }//if(selectionList.size()!=0)
            }
        });

        //Back Button
        messagesTLtopbarbackImageViewID.setOnClickListener(new ImageView.OnClickListener(){

            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //User Icon Click Event
        messagesTLtopbarusericonImageViewID.setOnClickListener(new ImageView.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent i = new Intent(MessagesForTLActivity.this,ChangePasswordActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
            }
        });

    }//onCreate()

    private void initAllViews() {
        //shared preference
        prefs = getSharedPreferences(CommonUtils.PREFERENCE_NAME, MODE_PRIVATE);
        prefsEditor = prefs.edit();

        messagesTL_checkBox_listheaderID = (CheckBox)findViewById(R.id.messagesTL_checkBox_listheaderID);
        messagesTLAssociatesListViewID = (ListView)findViewById(R.id.messagesTLAssociatesListViewID);
        messagesTLNoDataTextViewID = (TextView)findViewById(R.id.messagesTLNoDataTextViewID);
        messagesTLMsgBoxEdittextID = (EditText)findViewById(R.id.messagesTLMsgBoxEdittextID);
        messagesTLSubmitImageViewID = (ImageView)findViewById(R.id.messagesTLSubmitImageViewID);
        messagesTLBottombarLinearLayoutID = (LinearLayout)findViewById(R.id.messagesTLBottombarLinearLayoutID);

        messagesTLtopbarbackImageViewID = (ImageView)findViewById(R.id.messagesTLtopbarbackImageViewID);
        messagesTLtopbarusericonImageViewID = (ImageView)findViewById(R.id.messagesTLtopbarusericonImageViewID);

        associate_idList = new ArrayList<String>(0);
        associate_isdList = new ArrayList<String>(0);
        associate_firstnameList = new ArrayList<String>(0);
        associate_lastnameList = new ArrayList<String>(0);

        selectionSet = new HashSet<String>(0);
        selectionList = new ArrayList<String>(0);

        //Progress Dialog
        progressDialog = new ProgressDialog(MessagesForTLActivity.this);
    }

    //Validate Data locally(Checks whether the fields are empty or not)
    private void validateData() {

        boolean cancel = false;
        View focusView = null;

        if(TextUtils.isEmpty(messagesTLMsgBoxEdittextID.getText().toString()))
        {
            messagesTLMsgBoxEdittextID.setError("Message Box is Empty!");
            focusView = messagesTLMsgBoxEdittextID;
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
    //Get the values from EditText
    private void getTextValues() {

        msg = messagesTLMsgBoxEdittextID.getText().toString();

        if(!msg.equalsIgnoreCase("")){
            sendMessageToServer();
        }
        else{
            //Toast.makeText(this, "Message Box is Empty!", Toast.LENGTH_LONG).show();
            showNoAssociateDialog("Kindly write a message!");
        }
    }

    //sendMessageToServer
    private void sendMessageToServer(){

        //progressDialog.setTitle("Login");
        progressDialog.setMessage("Sending... Please wait!");
        progressDialog.setCancelable(false);
        progressDialog.show();
        client = new RestFullClient(Constants.BASE_URL+Constants.SENDMESSAGE_BY_TL_RELATIVE_URI);
        client.AddParam("tl_code",prefs.getString("USERISDCODE",""));
        System.out.println("tl_id:"+prefs.getString("USERISDCODE",""));
        client.AddParam("associate_codes",selectionList.toString());
        System.out.println("associate_ids:"+selectionList.toString());
        client.AddParam("message",msg);
        System.out.println("message:"+msg);

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
                    showSuccessDialog();
                    //Toast.makeText(TargetSettingActivity.this, MESSAGE, Toast.LENGTH_SHORT).show();
                }
                //Failed
                if(STATUS.equalsIgnoreCase("false")){
                    //showRetryDialog();
                    showFailureDialog();
                }
            }
            //Failed
            if(client.responseCode!=200){
                //Toast.makeText(TargetSettingActivity.this, MESSAGE, Toast.LENGTH_SHORT).show();
                //showRetryDialog();
                showFailureDialog();
            }

        }//handleMessage(Message msg)

    };
    //Show Success Dialog
    private void showSuccessDialog(){
        //Alert Dialog Builder
        final AlertDialog.Builder aldb = new AlertDialog.Builder(MessagesForTLActivity.this);
        aldb.setTitle("Success!");
        aldb.setMessage(MESSAGE);
        aldb.setCancelable(false);
        aldb.setNegativeButton("Send Another", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent i = new Intent(MessagesForTLActivity.this, MessagesForTLActivity.class);
                MessagesForTLActivity.this.finish();
                startActivity(i);
                overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
            }
        });
        aldb.setNeutralButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MessagesForTLActivity.this.finish();
                overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
            }
        });

        aldb.show();
    }

    //All Associate List
    private void requestAssociateList(){
        associate_idList.clear();
        associate_isdList.clear();
        associate_firstnameList.clear();
        associate_lastnameList.clear();

        //progressDialog.setTitle("Associate Details");
        progressDialog.setMessage("Loading... Please wait!");
        progressDialog.setCancelable(false);
        progressDialog.show();
        client = new RestFullClient(Constants.BASE_URL+Constants.GETASSOCIATE_BY_TEAMLEAD_RELATIVE_URI);
        client.AddParam("tl_code",prefs.getString("USERISDCODE",""));
        System.out.println("tl_code:"+prefs.getString("USERISDCODE",""));
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

                        JSONArray pendingDateArray = JOBJECT_DATA.getJSONArray(TAG_JARRAY_ASSOCIATELIST);
                        JSONObject c = null;
                        String id = null;
                        String isd = null;
                        String fname = null;
                        String lname = null;

                        System.out.println("##########All ASSOCIATE List details###################");

                        associate_idList.clear();
                        associate_isdList.clear();
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

                                        associate_idList.add(id);
                                        associate_isdList.add(isd);
                                        associate_firstnameList.add(fname);
                                        associate_lastnameList.add(lname);
                                    }
                                } catch (Exception e) {
                                }

                            }//for

                        }//if(pendingDateArray.length()!=0)

                        System.out.println("Total associate_idList: " + associate_idList.size());
                        System.out.println("Total associate_isdList: " + associate_isdList.size());
                        System.out.println("Total associate_firstnameList: " + associate_firstnameList.size());
                        System.out.println("Total associate_lastnameList: " + associate_lastnameList.size());
                        System.out.println("##########End Of All ASSOCIATE List details###################");


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

            try {
                if((progressDialog != null) && progressDialog.isShowing() ){
                    progressDialog.dismiss();
                }
            }catch (final Exception e) {
                e.printStackTrace();
            }

            //Success
            if(client.responseCode==200){

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
            if(client.responseCode!=200){
                //Toast.makeText(TargetSettingActivity.this, MESSAGE, Toast.LENGTH_SHORT).show();
                showFailureDialog();
            }

        }//handleMessage(Message msg)

    };

    private void showAssociateDetails(){
        if(associate_idList.size()!=0){
            messagesTLAssociatesListViewID.setVisibility(View.VISIBLE);
            messagesTLNoDataTextViewID.setVisibility(View.GONE);
            messagesTLBottombarLinearLayoutID.setVisibility(View.VISIBLE);
            setAssociateListView();
        }
        if(associate_idList.size()==0){
            messagesTLAssociatesListViewID.setVisibility(View.GONE);
            messagesTLNoDataTextViewID.setVisibility(View.VISIBLE);
            messagesTLBottombarLinearLayoutID.setVisibility(View.GONE);
        }
    }

    private void setAssociateListView(){
        checkedItems = new boolean[associate_isdList.size()];
        messagesTLAssociatesListViewID.setAdapter(new CustomAdapterForAssociatesList(MessagesForTLActivity.this));
    }

    /*
    * CustomAdapterForAssociatesList
    */
    public class CustomAdapterForAssociatesList extends BaseAdapter {

        public Context cntx;

        public CustomAdapterForAssociatesList(Context context){
            cntx = context;
        }

        @Override
        public int getCount() {
            return associate_idList.size();
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
                view = inflater.inflate(R.layout.custom_layout_for_messages_tl, null);
            }
            else{
                view = convertView;
            }

            view.setTag(viewHolder);
            final int pos1 = position;

            final TextView nameTextView = (TextView)view.findViewById(R.id.custom_messagesTL_AssociateNameTextViewID);
            final TextView idTextView = (TextView)view.findViewById(R.id.custom_messagesTL_AssociateIDTextViewID);
            viewHolder.checkbox = (CheckBox)view.findViewById(R.id.custom_messagesTL_checkBoxID);

            final ViewHolder holder = (ViewHolder)view.getTag();

            nameTextView.setText(associate_firstnameList.get(position)+" "+associate_lastnameList.get(position));
            idTextView.setText(associate_isdList.get(position));

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
                        selectionSet.add(associate_isdList.get(pos1));
                        selectionList.add(associate_isdList.get(pos1));
                        System.out.println("Selection ID list: "+ selectionSet.toString());
                        System.out.println("Total Set size(After Add): "+selectionSet.size());
                        System.out.println("Selection ID list: "+ selectionList.toString());
                        System.out.println("Total List size(After Add): "+selectionList.size());
                    }
                    if(!isChecked){

                        //Toast.makeText(getActivity(), "UnChecked at "+String.valueOf(pos1), Toast.LENGTH_SHORT).show();
                        selectionSet.remove(associate_isdList.get(pos1));
                        selectionList.remove(associate_isdList.get(pos1));
                        System.out.println("Selection ID list: "+ selectionSet.toString());
                        System.out.println("Total Set size(After Remove): "+selectionSet.size());
                        System.out.println("Selection ID list: "+ selectionList.toString());
                        System.out.println("Total List size(After Remove): "+selectionList.size());

                    }//if

                }

            });

            return view;

        }

        class ViewHolder{
            CheckBox checkbox;
        }

    }//CustomAdapterForAssociatesList Class

    //Show failure Dialog
    private void showFailureDialog(){
        //Alert Dialog Builder
        final AlertDialog.Builder aldb = new AlertDialog.Builder(MessagesForTLActivity.this);
        aldb.setTitle("Failed!");
        aldb.setMessage("\nReason: "+MESSAGE);
        aldb.setPositiveButton("OK", null);
        aldb.show();
    }

    //Show No Associate Dialog
    private void showNoAssociateDialog(String msg){
        //Alert Dialog Builder
        final AlertDialog.Builder aldb = new AlertDialog.Builder(MessagesForTLActivity.this);
        aldb.setMessage(msg);
        aldb.setPositiveButton("OK", null);
        aldb.show();
    }

    @Override
    public void onBackPressed()
    {
        MessagesForTLActivity.this.finish();
        overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
    }
    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    protected void onDestroy() {
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

}//Main Class
