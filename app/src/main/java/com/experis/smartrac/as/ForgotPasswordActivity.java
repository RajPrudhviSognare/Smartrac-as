package com.experis.smartrac.as;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

public class ForgotPasswordActivity extends AppCompatActivity {

    private PowerManager.WakeLock mWakeLock;
    private SharedPreferences prefs;
    private SharedPreferences.Editor prefsEditor;
    private ProgressDialog progressDialog;

    private EditText forgotPasswordPageISDEditTextID;
    private EditText forgotPasswordPageEmailEditTextID;
    private Button forgotPasswordSubmitBtnID;

    private ImageView forgotPasswordtopbarbackImageViewID;

    private RestFullClient client;

    private String TAG_STATUS = "status";
    private String TAG_ERROR = "error";
    private String TAG_MESSAGE = "message";

    private String STATUS = "";
    private String ERROR = "";
    private String MESSAGE = "";

    private String ISD_CODE = "";
    private String EMAILID = "";
    private int STATUS_CODE = 0;
    private String username = "";
    private String baseURL;
    private String SOAPRequestXML;
    private HttpResponse httpResponse = null;
    private String TAG_MESSAGE_ID="MessageID";
    private String TAG_MESSAGE_VALUE="";
    private String TAG_DESCRIPTION_ID="Description";
    private String TAG_DESCRIPTION_VALUE="";
    private String TAG_ChangepasswordResult = "GetPasswordResult";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.forgot_password_topbar_title);

        /*PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "AttendanceTracking-DoNotDimScreen");
        mWakeLock.acquire();*/

        initAllViews();

        //Back Button
        forgotPasswordtopbarbackImageViewID.setOnClickListener(new ImageView.OnClickListener(){

            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //Forgot Password Button Click
        forgotPasswordSubmitBtnID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    if(CommonUtils.isInternelAvailable(ForgotPasswordActivity.this)){

                        validateData();
                    }
                    else{
                        Toast.makeText(ForgotPasswordActivity.this, "No internet connection!", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

    }//onCreate()

    private void initAllViews(){
        //shared preference
        prefs = getSharedPreferences(CommonUtils.PREFERENCE_NAME,MODE_PRIVATE);
        prefsEditor = prefs.edit();

        forgotPasswordPageISDEditTextID = (EditText)findViewById(R.id.forgotPasswordPageISDEditTextID);
        forgotPasswordPageEmailEditTextID = (EditText)findViewById(R.id.forgotPasswordPageEmailEditTextID);
        forgotPasswordSubmitBtnID = (Button)findViewById(R.id.forgotPasswordSubmitBtnID);

        forgotPasswordtopbarbackImageViewID = (ImageView)findViewById(R.id.forgotPasswordtopbarbackImageViewID);

        //Progress Dialog
        progressDialog = new ProgressDialog(ForgotPasswordActivity.this);
    }

    //Validate Data locally(Checks whether the fields are empty or not)
    private void validateData() {

        boolean cancel = false;
        View focusView = null;

        if(TextUtils.isEmpty(forgotPasswordPageISDEditTextID.getText().toString()))
        {
            forgotPasswordPageISDEditTextID.setError("Required field!");
            focusView = forgotPasswordPageISDEditTextID;
            cancel = true;
        }
        else if(TextUtils.isEmpty(forgotPasswordPageEmailEditTextID.getText().toString())){
            forgotPasswordPageEmailEditTextID.setError("Required field!");
            focusView = forgotPasswordPageEmailEditTextID;
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

        ISD_CODE = forgotPasswordPageISDEditTextID.getText().toString();
        EMAILID = forgotPasswordPageEmailEditTextID.getText().toString();

        if(!ISD_CODE.equalsIgnoreCase("")&&!EMAILID.equalsIgnoreCase("")){
            sendDataToRetrievePassword();
        }
        else{
            Toast.makeText(this, "Both the fields are mandatory!", Toast.LENGTH_SHORT).show();
        }
    }
    //For Retrieving Password
    private void sendDataToRetrievePassword(){

        progressDialog.setMessage("Resendind your password... Please wait!");
        progressDialog.setCancelable(false);
        progressDialog.show();



        baseURL = Constants.base_url_default;
        SOAPRequestXML = Constants.soapRequestHeader+
                "<soapenv:Header/>"
                +"<soapenv:Body>"
                +"<tem:GetPassword>"
                +"<tem:login_id>"+ISD_CODE+"</tem:login_id>"
                //+"<tem:password>"+CommonUtils.md5(passwd)+"</tem:password>"
                +"<tem:email_id>"+EMAILID+"</tem:email_id>"


                +"</tem:GetPassword>"
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

                        if(tagname.equalsIgnoreCase(TAG_ChangepasswordResult)){
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
                if(STATUS.equalsIgnoreCase("1")){
                    //Toast.makeText(LoginActivity.this, MESSAGE, Toast.LENGTH_SHORT).show();
                    MESSAGE="Check your email";
                    showSuccessDialog();
                }
                else{
                    MESSAGE="Please provide correct ISD Code/Email Id";
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
                MESSAGE="Please provide correct ISD Code/Email Id";
                showFailureDialog();

            }

        }//handleMessage(Message msg)

    };

    //Show Success Dialog
    private void showSuccessDialog(){
        //Alert Dialog Builder
        final AlertDialog.Builder aldb = new AlertDialog.Builder(ForgotPasswordActivity.this);
        aldb.setTitle("Success!");
        aldb.setMessage(MESSAGE);
        aldb.setCancelable(false);
        aldb.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub

                //Intent i = new Intent(ForgotPasswordActivity.this,LoginActivity.class);
                ForgotPasswordActivity.this.finish();
                //startActivity(i);
                overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
            }
        });

        aldb.show();

    }
    //Show failure Dialog
    private void showFailureDialog(){
        //Alert Dialog Builder
        final AlertDialog.Builder aldb = new AlertDialog.Builder(ForgotPasswordActivity.this);
        aldb.setTitle("Failed!");
        aldb.setMessage("\nReason: "+MESSAGE);
        aldb.setPositiveButton("OK", null);
        aldb.show();
    }

    @Override
    public void onBackPressed()
    {
        //Intent i = new Intent(ForgotPasswordActivity.this,LoginActivity.class);
        ForgotPasswordActivity.this.finish();
        //startActivity(i);
        overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
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
