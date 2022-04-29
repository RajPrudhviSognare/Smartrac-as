package com.experis.smartrac.as;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.experis.smartrac.as.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MessagesForAssociateActivity extends AppCompatActivity {

    private PowerManager.WakeLock mWakeLock;
    private SharedPreferences prefs;
    private SharedPreferences.Editor prefsEditor;
    private ProgressDialog progressDialog;

    private ListView messagesForAssociatesListViewID;
    private TextView messagesForAssociatesNoDataTextViewID;

    private ImageView messagesAssociatetopbarbackImageViewID;
    private ImageView messagesAssociatetopbarusericonImageViewID;

    private RestFullClient client;

    private String TAG_STATUS = "status";
    private String TAG_ERROR = "error";
    private String TAG_MESSAGE = "message";

    private String STATUS = "";
    private String ERROR = "";
    private String MESSAGE = "";

    private String TAG_JOBJECT_DATA = "data";
    private String TAG_JARRAY_ASSOCIATEMESSAGELIST = "message_list";

    private JSONObject JOBJECT_DATA = null;

    //ASSOCIATE MESSAGE DETAIL TAGS
    private String TAG_ASSOCIATEMESSAGE = "message";
    private String TAG_ASSOCIATEMESSAGE_DATETIME = "sub_time";

    private List<String> associate_messageList = null;
    private List<String> associate_sub_timeList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages_for_associate);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.messageassociate_topbar_layout);

        /*PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "AttendanceTracking-DoNotDimScreen");
        mWakeLock.acquire();*/

        initAllViews();

        if(CommonUtils.isInternelAvailable(MessagesForAssociateActivity.this)){
            requestAssociateMessageList();
        }
        else{
            Toast.makeText(MessagesForAssociateActivity.this, "No internet connection!", Toast.LENGTH_SHORT).show();
        }

        //Back Button
        messagesAssociatetopbarbackImageViewID.setOnClickListener(new ImageView.OnClickListener(){

            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //User Icon Click Event
        messagesAssociatetopbarusericonImageViewID.setOnClickListener(new ImageView.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent i = new Intent(MessagesForAssociateActivity.this,ChangePasswordActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
            }
        });

    }//onCreate()

    private void initAllViews() {
        //shared preference
        prefs = getSharedPreferences(CommonUtils.PREFERENCE_NAME, MODE_PRIVATE);
        prefsEditor = prefs.edit();

        messagesForAssociatesListViewID = (ListView)findViewById(R.id.messagesForAssociatesListViewID);
        messagesForAssociatesNoDataTextViewID = (TextView)findViewById(R.id.messagesForAssociatesNoDataTextViewID);

        messagesAssociatetopbarbackImageViewID = (ImageView)findViewById(R.id.messagesAssociatetopbarbackImageViewID);
        messagesAssociatetopbarusericonImageViewID = (ImageView)findViewById(R.id.messagesAssociatetopbarusericonImageViewID);

        associate_messageList = new ArrayList<String>(0);
        associate_sub_timeList = new ArrayList<String>(0);

        //Progress Dialog
        progressDialog = new ProgressDialog(MessagesForAssociateActivity.this);
    }

    //All Associate Messages List
    private void requestAssociateMessageList(){
        associate_messageList.clear();
        associate_sub_timeList.clear();

        //progressDialog.setTitle("Associate Details");
        progressDialog.setMessage("Loading... Please wait!");
        progressDialog.setCancelable(false);
        progressDialog.show();
        client = new RestFullClient(Constants.BASE_URL+Constants.GETMESSAGE_BY_ASSOCIATE_RELATIVE_URI);
        client.AddParam("associate_code",prefs.getString("USERISDCODE",""));
        System.out.println("associate_id:"+prefs.getString("USERISDCODE",""));
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

                        JSONArray pendingDateArray = JOBJECT_DATA.getJSONArray(TAG_JARRAY_ASSOCIATEMESSAGELIST);
                        JSONObject c = null;
                        String message = null;
                        String sub_time = null;

                        System.out.println("##########All ASSOCIATE Messages List details###################");

                        associate_messageList.clear();
                        associate_sub_timeList.clear();

                        if(pendingDateArray.length()==0){
                        }
                        if(pendingDateArray.length()!=0){

                            for(int i = 0; i < pendingDateArray.length(); i++) {

                                try {
                                    c = pendingDateArray.getJSONObject(i);
                                    System.out.println("C is : " + c);
                                    if (c != null) {
                                        message = c.getString(TAG_ASSOCIATEMESSAGE);
                                        sub_time = c.getString(TAG_ASSOCIATEMESSAGE_DATETIME);

                                        associate_messageList.add(message);
                                        associate_sub_timeList.add(sub_time);
                                    }
                                } catch (Exception e) {
                                }

                            }//for

                        }//if(pendingDateArray.length()!=0)

                        System.out.println("Total associate_messageList: " + associate_messageList.size());
                        System.out.println("Total associate_sub_timeList: " + associate_sub_timeList.size());
                        System.out.println("##########End Of All ASSOCIATE Messages List details###################");


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
                    showAssociateMessageDetails();
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

    private void showAssociateMessageDetails(){
        if(associate_messageList.size()!=0){
            messagesForAssociatesListViewID.setVisibility(View.VISIBLE);
            messagesForAssociatesNoDataTextViewID.setVisibility(View.GONE);
            setAssociateMessageListView();
        }
        if(associate_messageList.size()==0){
            messagesForAssociatesListViewID.setVisibility(View.GONE);
            messagesForAssociatesNoDataTextViewID.setVisibility(View.VISIBLE);
        }
    }
    private void setAssociateMessageListView(){
        messagesForAssociatesListViewID.setAdapter(new CustomAdapterForAssociatesMessageList(MessagesForAssociateActivity.this));
    }

    /*
    * CustomAdapterForAssociatesMessageList
    */
    public class CustomAdapterForAssociatesMessageList extends BaseAdapter {

        public Context cntx;

        public CustomAdapterForAssociatesMessageList(Context context){
            cntx = context;
        }

        @Override
        public int getCount() {
            return associate_messageList.size();
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
                view = inflater.inflate(R.layout.customlayout_for_associate_messages, null);
            }
            else{
                view = convertView;
            }

            final int pos1 = position;

            final TextView msgTextView = (TextView)view.findViewById(R.id.customlayout_messagesForAssociates_MESSAGETextViewID);
            final TextView dateTextView = (TextView)view.findViewById(R.id.customlayout_messagesForAssociates_DATETextViewID);

            msgTextView.setText(associate_messageList.get(position));
            dateTextView.setText(associate_sub_timeList.get(position));

            return view;
        }

    }//CustomAdapterForAssociatesMessageList Class

    //Show failure Dialog
    private void showFailureDialog(){
        //Alert Dialog Builder
        final AlertDialog.Builder aldb = new AlertDialog.Builder(MessagesForAssociateActivity.this);
        aldb.setTitle("Failed!");
        aldb.setMessage("\nReason: "+MESSAGE);
        aldb.setPositiveButton("OK", null);
        aldb.show();
    }

    @Override
    public void onBackPressed()
    {
        MessagesForAssociateActivity.this.finish();
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
