package com.experis.smartrac.as;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


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

public class HRMSEmpDetailsFragment extends Fragment {

    private PowerManager.WakeLock mWakeLock;
    private SharedPreferences prefs;
    private SharedPreferences.Editor prefsEditor;
    private ProgressDialog progressDialog;

    private TextView employeeinfoPageEmployeeCodeTextViewID;
    private TextView employeeinfoPageEmployeeNameTextViewID;
    private TextView employeeinfoPageFatherNameTextViewID;
    private TextView employeeinfoPageEmployeeDOBTextViewID;
    private TextView employeeinfoPageEmployeeDOJTextViewID;
    private TextView employeeinfoPagePFNumberTextViewID;
    private TextView employeeinfoPageEmployeePFUANTextViewID;
    private TextView employeeinfoPageEmployeeESICNumberTextViewID;
    private TextView employeeinfoPageEmployeePANTextViewID;
    private TextView employeeinfoPageEmployeeClientNameTextViewID;
    private TextView employeeinfoPageEmployeeEMailAddressTextViewID;
    private TextView employeeinfoPageEmployeeMailingAddressTextViewID;
    private TextView employeeinfoPageEmployeePermAddressTextViewID;
    private TextView employeeinfoPageEmployeeMobile1TextViewID;
    private TextView employeeinfoPageEmployeeMobile2TextViewID;
    private TextView employeeinfoPageEmployeeStatusTextViewID;

    private String baseURL;
    private String SOAPRequestXML;
    private HttpResponse httpResponse = null;

    private String TAG_EmpCode = "EmpCode";
    private String TAG_EmpName = "EmpName";
    private String TAG_FatherName = "FatherName";
    private String TAG_DOB = "DOB";
    private String TAG_DOJ = "DOJ";
    private String TAG_PFNumber = "PFNumber";
    private String TAG_PFUAN = "PFUAN";
    private String TAG_ESICNumber = "ESICNumber";
    private String TAG_PAN = "PAN";
    private String TAG_ClientName = "ClientName";
    private String TAG_EmailID = "EmailID";
    private String TAG_MailAddress = "MailAddress";
    private String TAG_PermAddress = "PermAddress";
    private String TAG_Mobile1 = "Mobile1";
    private String TAG_Mobile2 = "Mobile2";
    private String TAG_EmployeeStatus = "EmployeeStatus";

    private String EmpCode = "";
    private String EmpName = "";
    private String FatherName = "";
    private String DOB = "";
    private String DOJ = "";
    private String PFNumber = "";
    private String PFUAN = "";
    private String ESICNumber = "";
    private String PAN = "";
    private String ClientName = "";
    private String EmailID = "";
    private String MailAddress = "";
    private String PermAddress = "";
    private String Mobile1 = "";
    private String Mobile2 = "";
    private String EmployeeStatus = "";

    public HRMSEmpDetailsFragment() {
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
        //Toast.makeText(getActivity(),"In Time Fragment",Toast.LENGTH_LONG).show();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.hrms_employee_info_fragment, container, false);

        employeeinfoPageEmployeeCodeTextViewID = (TextView)view.findViewById(R.id.employeeinfoPageEmployeeCodeTextViewID);
        employeeinfoPageEmployeeNameTextViewID = (TextView)view.findViewById(R.id.employeeinfoPageEmployeeNameTextViewID);
        employeeinfoPageFatherNameTextViewID = (TextView)view.findViewById(R.id.employeeinfoPageFatherNameTextViewID);
        employeeinfoPageEmployeeDOBTextViewID = (TextView)view.findViewById(R.id.employeeinfoPageEmployeeDOBTextViewID);
        employeeinfoPageEmployeeDOJTextViewID = (TextView)view.findViewById(R.id.employeeinfoPageEmployeeDOJTextViewID);
        employeeinfoPagePFNumberTextViewID = (TextView)view.findViewById(R.id.employeeinfoPagePFNumberTextViewID);
        employeeinfoPageEmployeePFUANTextViewID = (TextView)view.findViewById(R.id.employeeinfoPageEmployeePFUANTextViewID);
        employeeinfoPageEmployeeESICNumberTextViewID = (TextView)view.findViewById(R.id.employeeinfoPageEmployeeESICNumberTextViewID);
        employeeinfoPageEmployeePANTextViewID = (TextView)view.findViewById(R.id.employeeinfoPageEmployeePANTextViewID);
        employeeinfoPageEmployeeClientNameTextViewID = (TextView)view.findViewById(R.id.employeeinfoPageEmployeeClientNameTextViewID);
        employeeinfoPageEmployeeEMailAddressTextViewID = (TextView)view.findViewById(R.id.employeeinfoPageEmployeeEMailAddressTextViewID);
        employeeinfoPageEmployeeMailingAddressTextViewID = (TextView)view.findViewById(R.id.employeeinfoPageEmployeeMailingAddressTextViewID);
        employeeinfoPageEmployeePermAddressTextViewID = (TextView)view.findViewById(R.id.employeeinfoPageEmployeePermAddressTextViewID);
        employeeinfoPageEmployeeMobile1TextViewID = (TextView)view.findViewById(R.id.employeeinfoPageEmployeeMobile1TextViewID);
        employeeinfoPageEmployeeMobile2TextViewID = (TextView)view.findViewById(R.id.employeeinfoPageEmployeeMobile2TextViewID);
        employeeinfoPageEmployeeStatusTextViewID = (TextView)view.findViewById(R.id.employeeinfoPageEmployeeStatusTextViewID);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getEmployeeDetails();
    }

    private void getEmployeeDetails(){
        progressDialog.setMessage("Requesting... Please wait!");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String EmpID = prefs.getString("USERISDCODE","");
        System.out.println("EmpID: "+EmpID);

        baseURL = Constants.base_url_default;
        SOAPRequestXML = Constants.soapRequestHeader+
                "<soapenv:Header/>"
                +"<soapenv:Body>"
                +"<tem:EmployeeInformation>"
                //+"<tem:emp_code>"+100172254+"</tem:emp_code>"
                +"<tem:emp_code>"+EmpID+"</tem:emp_code>"
                +"</tem:EmployeeInformation>"
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

                handler.sendEmptyMessage(0);

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

                        if(tagname.equalsIgnoreCase(TAG_EmpCode)){
                            EmpCode = text;
                            text = "";
                            System.out.println("EmpCode: "+EmpCode);
                        }
                        else if(tagname.equalsIgnoreCase(TAG_EmpName)){
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
                        else if(tagname.equalsIgnoreCase(TAG_EmailID)){
                            EmailID = text;
                            text = "";
                            System.out.println("EmailID: "+EmailID);
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

    Handler handler = new Handler(){

        public void handleMessage(Message msg){
            try {
                if((progressDialog != null) && progressDialog.isShowing() ){
                    progressDialog.dismiss();
                }
            }catch (final Exception e) {
                e.printStackTrace();
            }
            setEmployeeDetails();
        }//handleMessage(Message msg)

    };

    private void setEmployeeDetails(){

        employeeinfoPageEmployeeCodeTextViewID.setText("");
        employeeinfoPageEmployeeNameTextViewID.setText("");
        employeeinfoPageFatherNameTextViewID.setText("");
        employeeinfoPageEmployeeDOBTextViewID.setText("");
        employeeinfoPageEmployeeDOJTextViewID.setText("");
        employeeinfoPagePFNumberTextViewID.setText("");
        employeeinfoPageEmployeePFUANTextViewID.setText("");
        employeeinfoPageEmployeeESICNumberTextViewID.setText("");
        employeeinfoPageEmployeePANTextViewID.setText("");
        employeeinfoPageEmployeeClientNameTextViewID.setText("");
        employeeinfoPageEmployeeEMailAddressTextViewID.setText("");
        employeeinfoPageEmployeeMailingAddressTextViewID.setText("");
        employeeinfoPageEmployeePermAddressTextViewID.setText("");
        employeeinfoPageEmployeeMobile1TextViewID.setText("");
        employeeinfoPageEmployeeMobile2TextViewID.setText("");
        employeeinfoPageEmployeeStatusTextViewID.setText("");

        employeeinfoPageEmployeeCodeTextViewID.setText(EmpCode);
        employeeinfoPageEmployeeNameTextViewID.setText(EmpName);
        employeeinfoPageFatherNameTextViewID.setText(FatherName);
        employeeinfoPageEmployeeDOBTextViewID.setText(DOB);

        prefsEditor.putString("USERDOB", DOB);
        prefsEditor.commit();

        employeeinfoPageEmployeeDOJTextViewID.setText(DOJ);
        employeeinfoPagePFNumberTextViewID.setText(PFNumber);
        employeeinfoPageEmployeePFUANTextViewID.setText(PFUAN);
        employeeinfoPageEmployeeESICNumberTextViewID.setText(ESICNumber);
        employeeinfoPageEmployeePANTextViewID.setText(PAN);
        employeeinfoPageEmployeeClientNameTextViewID.setText(ClientName);
        employeeinfoPageEmployeeEMailAddressTextViewID.setText(EmailID);
        employeeinfoPageEmployeeMailingAddressTextViewID.setText(MailAddress);
        employeeinfoPageEmployeePermAddressTextViewID.setText(PermAddress);
        employeeinfoPageEmployeeMobile1TextViewID.setText(Mobile1);
        employeeinfoPageEmployeeMobile2TextViewID.setText(Mobile2);
        employeeinfoPageEmployeeStatusTextViewID.setText(EmployeeStatus);

    }

    private void initAllViews(){
        //shared preference
        prefs = getActivity().getSharedPreferences(CommonUtils.PREFERENCE_NAME,getActivity().MODE_PRIVATE);
        prefsEditor = prefs.edit();

        //Progress Dialog
        progressDialog = new ProgressDialog(getActivity());
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

}//Main Class
