package com.experis.smartrac.as;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
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
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;

public class LoginActivity extends AppCompatActivity {

    private EditText loginpageUsernameEditTextID;
    private EditText loginpagePasswordEditTextID;
    private Button loginpageLoginBtnID;
    private TextView forgotpasswdTextViewID;

    private PowerManager.WakeLock mWakeLock;
    private SharedPreferences prefs;
    private SharedPreferences.Editor prefsEditor;
    private ProgressDialog progressDialog;

    private String GCMRegdID = "";
    private String IMEIID = "";
    private RestFullClient client;

    private String username = "";
    private String passwd = "";

    private String TAG_STATUS = "status";
    private String TAG_ERROR = "error";
    private String TAG_MESSAGE = "message";

    private String STATUS = "";
    private String ERROR = "";
    private String MESSAGE = "";

    private int STATUS_CODE = 0;

    private String TAG_JOBJECT_DATA = "data";
    private String TAG_JOBJECT_USER = "user";

    private JSONObject JOBJECT_DATA = null;
    private JSONObject JOBJECT_USER = null;

    //USER DETAILS TAGS
    private String TAG_USERID = "id";
    private String TAG_USERISDCODE = "isd_code";
    private String TAG_USERFIRSTNAME = "first_name";
    private String TAG_USERLASTNAME = "last_name";
    private String TAG_designation = "designation";
    private String TAG_department = "department";
    private String TAG_USEREMAILID = "email";
    private String TAG_USERGENDER = "gender";
    private String TAG_USERROLEID = "role_id";
    private String TAG_USERSTATUS = "status";
    private String TAG_TLID = "tl_id";
    private String TAG_USEROUTLETNAME = "outlet_name";
    private String TAG_USEROUTLETADDR = "site";
    private String TAG_LOGO = "logo";

    private String TAG_LoginAuthenticateResult = "LoginAuthenticateResult";

    //USER DETAILS
    private String USERID = "",TLID="",DOB="";
    private String USERISDCODE = "",USERFIRSTNAME="",USERLASTNAME="";
    private String USERNAME = "";
    private String USERDESIGNATION = "";
    private String USERDEPARTMENT = "";
    private String USEREMAILID = "";
    private String USERGENDER = "";
    private String USERROLEID = "1";
    private String USERSTATUS = "";
    private String USEROUTLETADDR = "";
    private String USEROUTLETNAME = "BASF India Limited";
    private String LOGO = "";

    private String baseURL;
    private String SOAPRequestXML;
    private HttpResponse httpResponse = null;
    private String TAG_MESSAGE_ID="MessageID";
    private String TAG_MESSAGE_VALUE="";
    private String TAG_DESCRIPTION_ID="Description";
    private String TAG_DESCRIPTION_VALUE="";
    private  int versioncode=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login2);

        initAllViews();

        if(Build.VERSION.SDK_INT>Build.VERSION_CODES.LOLLIPOP){
            System.out.println("Inside Login Page checkAllPermissions() is called Above Lallipop: ");
            try{
                checkAllPermissions();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        else{
            try{
                IMEIID = CommonUtils.getIMEI(LoginActivity.this);
                System.out.println("Inside Login Page 'IMEIID' below Lallipop: "+IMEIID);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }

        GCMRegdID = prefs.getString("GCMREGISTRATIONID","Push ID Not Found");
        System.out.println("Inside Login Page 'GCM/FCM Token': "+GCMRegdID);

        //Internet connection checker
        if(CommonUtils.isInternelAvailable(this)){
        }
        else{
            Toast.makeText(this, "No internet connection!", Toast.LENGTH_SHORT).show();
            showAlertDialog(LoginActivity.this, "Internet Connection Error!", "Please check your internet settings.\n", false);
        }
        //Internet connection checker

        //Login button click
        loginpageLoginBtnID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    if(CommonUtils.isInternelAvailable(LoginActivity.this)){
                        validateData();
                    }
                    else{
                        Toast.makeText(LoginActivity.this, "No internet connection!", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        //Forgot Password
        forgotpasswdTextViewID.setOnClickListener(new TextView.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
            }
        });

    }//onCreate()

    private void initAllViews(){
        //shared preference
        prefs = getSharedPreferences(CommonUtils.PREFERENCE_NAME,MODE_PRIVATE);
        prefsEditor = prefs.edit();

        loginpageUsernameEditTextID = (EditText)findViewById(R.id.loginpageUsernameEditTextID);
        loginpagePasswordEditTextID = (EditText)findViewById(R.id.loginpagePasswordEditTextID);
        loginpageLoginBtnID = (Button)findViewById(R.id.loginpageLoginBtnID);

        forgotpasswdTextViewID = (TextView)findViewById(R.id.forgotpasswdTextViewID);

        //Progress Dialog
        progressDialog = new ProgressDialog(LoginActivity.this);
    }

    private void checkAllPermissions(){
        if (ContextCompat.checkSelfPermission(LoginActivity.this,android.Manifest.permission.READ_PHONE_STATE)!= PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(LoginActivity.this,android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(LoginActivity.this,android.Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(LoginActivity.this,android.Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(LoginActivity.this,android.Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED
                ) {
            ActivityCompat.requestPermissions(LoginActivity.this,
                    new String[]{android.Manifest.permission.READ_PHONE_STATE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION,android.Manifest.permission.CAMERA}, 1991);

        }//if
        else{
            IMEIID = CommonUtils.getIMEI(LoginActivity.this);
            System.out.println("Inside Login Page 'Device_IMEI_ID': "+IMEIID);
            System.out.println("READ_PHONE_STATE IS ALREADY GRANTED! ==> getIMEI() is called");
        }

    }//checkAllPermissions()

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1991: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED
                        && grantResults[2] == PackageManager.PERMISSION_GRANTED
                        && grantResults[3] == PackageManager.PERMISSION_GRANTED
                        && grantResults[4] == PackageManager.PERMISSION_GRANTED
                        && grantResults[5] == PackageManager.PERMISSION_GRANTED) {

                    IMEIID = CommonUtils.getIMEI(LoginActivity.this);
                    System.out.println("Inside Login Page 'Device_IMEI_ID': "+IMEIID);
                    System.out.println("READ_PHONE_STATE IS GRANTED! ==> getIMEI() is called");

                } else {
                    // permission denied! Disable the
                    // functionality that depends on these permissions.
                    IMEIID = "READ_PHONE_STATE IS REJECTED!";
                    System.out.println("Inside Login Page 'Device_IMEI_ID': "+IMEIID);
                    System.out.println("READ_PHONE_STATE IS REJECTED!");
                }
            }
        }
    }

    public void showAlertDialog(Context context, String title, String message, Boolean status) {
        final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        // Setting Dialog Title
        alertDialog.setTitle(title);
        // Setting Dialog Message
        alertDialog.setMessage(message);
        // Setting OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //LoginActivity.this.finish();
            }
        });
        // Showing Alert Message
        alertDialog.show();
    }

    //Validate Data locally(Checks whether the fields are empty or not)
    private void validateData() {
        boolean cancel = false;
        View focusView = null;

        if(TextUtils.isEmpty(loginpageUsernameEditTextID.getText().toString()))
        {
            loginpageUsernameEditTextID.setError("Required field!");
            focusView = loginpageUsernameEditTextID;
            cancel = true;
        }
        else if(TextUtils.isEmpty(loginpagePasswordEditTextID.getText().toString()))
        {
            loginpagePasswordEditTextID.setError("Required field!");
            focusView = loginpagePasswordEditTextID;
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
        username = loginpageUsernameEditTextID.getText().toString();
        passwd = loginpagePasswordEditTextID.getText().toString();

        if(!username.equalsIgnoreCase("")&&!passwd.equalsIgnoreCase("")){
            //sendDataForLogin();
            sendDataForLogin1();
        }
        else{
            Toast.makeText(this, "Please Enter valid Credential!", Toast.LENGTH_SHORT).show();
        }
    }


    private void sendDataForLogin1(){

        progressDialog.setMessage("Checking login credentials... Please wait!");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String modelNumber = "";
        try {
            modelNumber = Build.MANUFACTURER + " " + Build.MODEL;
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("device_id:"+IMEIID);
        System.out.println("GCM registration_id:"+GCMRegdID);
        System.out.println("password:"+ CommonUtils.md5(passwd));
        System.out.println(modelNumber);

        baseURL = Constants.base_url_default;
        SOAPRequestXML = Constants.soapRequestHeader+
                "<soapenv:Header/>"
                +"<soapenv:Body>"
                +"<tem:LoginAuthenticate_v2>"
                +"<tem:login_id>"+username+"</tem:login_id>"
               // +"<tem:password>"+CommonUtils.md5(passwd)+"</tem:password>"
                +"<tem:password>"+passwd+"</tem:password>"
                +"<tem:device_id>"+IMEIID+"</tem:device_id>"
                +"<tem:device_type>"+"Android"+"</tem:device_type>"
                +"<tem:registration_id>"+GCMRegdID+"</tem:registration_id>"
                +"<tem:model_number>"+modelNumber+"</tem:model_number>"
                +"<tem:version_check>"+versioncode+"</tem:version_check>"
                +"</tem:LoginAuthenticate_v2>"
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
                        break;

                    case XmlPullParser.TEXT:
                        text = xpp.getText().trim().toString();
                        System.out.println("Text data: "+text);
                        break;

                    case XmlPullParser.END_TAG:

                        if(tagname.equalsIgnoreCase(TAG_LoginAuthenticateResult)){
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
                        }

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
                if(TAG_MESSAGE_VALUE.equalsIgnoreCase("1")){
                    //Toast.makeText(LoginActivity.this, MESSAGE, Toast.LENGTH_SHORT).show();
                    saveUserDetails();
                }

                else{
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

    private void saveUserDetails(){
        //USER DETAILS
        prefsEditor.putString("USERID", USERID);
        //prefsEditor.putString("USERISDCODE", USERISDCODE);
        prefsEditor.putString("USERISDCODE", username);
        prefsEditor.putString("USERNAME", USERNAME);
        prefsEditor.putString("USERDESIGNATION", USERDESIGNATION);
        prefsEditor.putString("USERDEPARTMENT", USERDEPARTMENT);
        prefsEditor.putString("USEREMAILID", USEREMAILID);
        prefsEditor.putString("USERGENDER", USERGENDER);
        prefsEditor.putString("USERROLEID", USERROLEID);
        prefsEditor.putString("USERSTATUS", USERSTATUS);
        prefsEditor.putString("USEROUTLETNAME", USEROUTLETNAME);
        prefsEditor.putString("LOGO", LOGO);

        prefsEditor.putString("IMEIID", IMEIID);
        prefsEditor.putString("LOGGEDIN", "yes");
        prefsEditor.commit();

        Intent i = new Intent(LoginActivity.this,DashBoardActivity.class);
        LoginActivity.this.finish();
        startActivity(i);
        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
    }

    //For Login
    /*private void sendDataForLogin(){

        progressDialog.setMessage("Checking login credentials... Please wait!");
        progressDialog.setCancelable(false);
        progressDialog.show();
        client = new RestFullClient(Constants.BASE_URL+Constants.LOGIN_RELATIVE_URI);
        client.AddParam("login_id", username);
        System.out.println("login_id:"+username);
        client.AddParam("password", CommonUtils.md5(passwd));
        System.out.println("password:"+ CommonUtils.md5(passwd));
        client.AddParam("device_id", IMEIID);
        System.out.println("device_id:"+IMEIID);
        client.AddParam("device_type", "Android");
        System.out.println("device_type:"+"Android");
        client.AddParam("registration_id", GCMRegdID);
        System.out.println("GCM registration_id:"+GCMRegdID);

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
                USEROUTLETNAME = jobj.getString(TAG_USEROUTLETNAME);
                if(jobj.has(TAG_LOGO)){
                    LOGO = jobj.getString(TAG_LOGO);
                }
                System.out.println("STATUS: responseCode==200: "+ STATUS);
                System.out.println("MESSAGE: responseCode==200: "+ MESSAGE);
                System.out.println("USEROUTLETNAME: responseCode==200: "+ USEROUTLETNAME);
                System.out.println("LOGO: responseCode==200: "+ LOGO);

                if(STATUS.equalsIgnoreCase("true")){

                    JOBJECT_DATA = jobj.getJSONObject(TAG_JOBJECT_DATA);
                    System.out.println("JOBJECT_DATA: "+JOBJECT_DATA.toString());

                    if(JOBJECT_DATA!=null){

                        if(JOBJECT_DATA.has(TAG_JOBJECT_USER)){
                            JOBJECT_USER = JOBJECT_DATA.getJSONObject(TAG_JOBJECT_USER);
                            System.out.println("JOBJECT_USER: "+JOBJECT_USER.toString());

                            //User Details
                            USERID = JOBJECT_USER.getString(TAG_USERID);
                            System.out.println("USERID: "+USERID);
                            USERISDCODE = JOBJECT_USER.getString(TAG_USERISDCODE);
                            System.out.println("USERISDCODE "+USERISDCODE);
                            USERNAME = JOBJECT_USER.getString(TAG_USERNAME);
                            System.out.println("USERNAME: "+USERNAME);
                            USERDESIGNATION = JOBJECT_USER.getString(TAG_designation);
                            System.out.println("USERDESIGNATION: "+USERDESIGNATION);
                            USERDEPARTMENT = JOBJECT_USER.getString(TAG_department);
                            System.out.println("USERDEPARTMENT: "+USERDEPARTMENT);
                            USEREMAILID = JOBJECT_USER.getString(TAG_USEREMAILID);
                            System.out.println("USEREMAILID: "+USEREMAILID);
                            USERGENDER = JOBJECT_USER.getString(TAG_USERGENDER);
                            System.out.println("USERGENDER: "+USERGENDER);
                            USERROLEID = JOBJECT_USER.getString(TAG_USERROLEID);
                            System.out.println("USERROLEID: "+USERROLEID);
                            USERSTATUS = JOBJECT_USER.getString(TAG_USERSTATUS);
                            System.out.println("USERSTATUS: "+USERSTATUS);

                        }//if(JOBJECT_DATA.has(TAG_JOBJECT_USER))

                    }//if(JOBJECT_DATA!=null){
                    else{
                        System.out.println("JOBJECT_DATA is Null");
                    }

                }//if(STATUS.equalsIgnoreCase("true"))


            }//if(client.responseCode==200)

            if(client.responseCode!=200){

                STATUS =  jobj.getString(TAG_STATUS);
                MESSAGE =  jobj.getString(TAG_MESSAGE);

                System.out.println("STATUS: responseCode!=200: "+ STATUS);
                System.out.println("MESSAGE: responseCode!=200: "+ MESSAGE);

            }//if(client.responseCode!=200)

        }
        catch(Exception e){e.printStackTrace();}

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
                //Login success
                if(STATUS.equalsIgnoreCase("true")){
                    Toast.makeText(LoginActivity.this, MESSAGE, Toast.LENGTH_SHORT).show();
                    saveUserDetails();
                }

                //Login failed
                if(STATUS.equalsIgnoreCase("false")){
                    showFailureDialog();
                }
            }

            //Login failed
            if(client.responseCode!=200){
                Toast.makeText(LoginActivity.this, MESSAGE, Toast.LENGTH_SHORT).show();
                showFailureDialog();

            }

        }//handleMessage(Message msg)

    };

    private void saveUserDetails(){
        //USER DETAILS
        prefsEditor.putString("USERID", USERID);
        prefsEditor.putString("USERISDCODE", USERISDCODE);
        prefsEditor.putString("USERNAME", USERNAME);
        prefsEditor.putString("USERDESIGNATION", USERDESIGNATION);
        prefsEditor.putString("USERDEPARTMENT", USERDEPARTMENT);
        prefsEditor.putString("USEREMAILID", USEREMAILID);
        prefsEditor.putString("USERGENDER", USERGENDER);
        prefsEditor.putString("USERROLEID", USERROLEID);
        prefsEditor.putString("USERSTATUS", USERSTATUS);
        prefsEditor.putString("USEROUTLETNAME", USEROUTLETNAME);
        prefsEditor.putString("LOGO", LOGO);

        prefsEditor.putString("IMEIID", IMEIID);
        prefsEditor.putString("LOGGEDIN", "yes");
        prefsEditor.commit();

        Intent i = new Intent(LoginActivity.this,DashBoardActivity.class);
        LoginActivity.this.finish();
        startActivity(i);
        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
    }
*/
    //Show failure Dialog
    private void showFailureDialog(){
        //Alert Dialog Builder
        final AlertDialog.Builder aldb = new AlertDialog.Builder(LoginActivity.this);
        aldb.setTitle("Login Failed!");
        aldb.setMessage("\nReason: "+TAG_DESCRIPTION_VALUE);
        aldb.setPositiveButton("OK", null);
        aldb.show();
    }

    @Override
    public void onBackPressed()
    {
        showExitDialog();
    }

    //Exit Dialog
    public void showExitDialog(){

        final AlertDialog.Builder aldb = new AlertDialog.Builder(LoginActivity.this);
        aldb.setTitle("Smartrac");
        aldb.setMessage("Would you like to Exit now?");
        aldb.setNegativeButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                LoginActivity.this.finish();
                overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
            }
        });

        aldb.setPositiveButton("No", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
            }
        });

        aldb.show();

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
