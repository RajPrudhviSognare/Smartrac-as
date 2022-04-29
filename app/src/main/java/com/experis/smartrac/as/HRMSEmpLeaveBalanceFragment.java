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
import android.widget.LinearLayout;
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

public class HRMSEmpLeaveBalanceFragment extends Fragment {

    private PowerManager.WakeLock mWakeLock;
    private SharedPreferences prefs;
    private SharedPreferences.Editor prefsEditor;
    private ProgressDialog progressDialog;

    private TextView employeeinfoPageLeaveBalanceTextViewID;
    private TextView employeeinfoPageLeaveBalanceTextViewID1;
    private TextView employeeinfoPageLeaveBalanceTextViewID2,employeeinfoPageLeaveBalanceTextViewID3,
            employeeinfoPageLeaveBalanceTextViewID4,employeeinfoPageLeaveBalanceTextViewID5;

    private String baseURL;
    private String SOAPRequestXML;
    private HttpResponse httpResponse = null;

    private String TAG_OpeningLeaveBalance = "leavebalance";
    private String TAG_OpeningLeaveBalanceType = "leavetypeid";
    private String OpeningLeaveBalance = "0";
    private String OpeningLeaveBalance1 = "0";
    private String OpeningLeaveBalance7 = "0";
    private String OpeningLeaveBalance3="0";
    private String OpeningLeaveBalance4="0";
    private String OpeningLeaveBalance5="0";
    private String OpeningLeaveBalance6="5";

    private String OpeningLeaveBalancetype = "";
    private LinearLayout paternitylayout;


    public HRMSEmpLeaveBalanceFragment() {
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
        View view = inflater.inflate(R.layout.hrms_leave_balance_fragment, container, false);

        employeeinfoPageLeaveBalanceTextViewID = (TextView)view.findViewById(R.id.employeeinfoPageLeaveBalanceTextViewID);
        employeeinfoPageLeaveBalanceTextViewID1 = (TextView)view.findViewById(R.id.employeeinfoPageLeaveBalanceTextViewID1);
        employeeinfoPageLeaveBalanceTextViewID2 = (TextView)view.findViewById(R.id.employeeinfoPageLeaveBalanceTextViewID2);
        employeeinfoPageLeaveBalanceTextViewID3 = (TextView)view.findViewById(R.id.employeeinfoPageLeaveBalanceTextViewID3);
        employeeinfoPageLeaveBalanceTextViewID4 = (TextView)view.findViewById(R.id.employeeinfoPageLeaveBalanceTextViewID4);
        employeeinfoPageLeaveBalanceTextViewID5 = (TextView)view.findViewById(R.id.employeeinfoPageLeaveBalanceTextViewID5);
        paternitylayout=(LinearLayout)view.findViewById(R.id.paternitylayout);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getLeaveDetails();
    }

    private void getLeaveDetails(){
        progressDialog.setMessage("Requesting... Please wait!");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String EmpID = prefs.getString("USERISDCODE","");
        System.out.println("EmpID: "+EmpID);


        /*if(prefs.getString("USERGENDER","").equalsIgnoreCase("Male")){
            paternitylayout.setVisibility(View.VISIBLE);
        }
        else{
            paternitylayout.setVisibility(View.GONE);
        }*/



        baseURL = Constants.base_url_default;
        SOAPRequestXML = Constants.soapRequestHeader+
                "<soapenv:Header/>"
                +"<soapenv:Body>"
                +"<tem:GetMyLeaveBalance>"
                //+"<tem:emp_code>"+100172254+"</tem:emp_code>"
                +"<tem:emp_code>"+EmpID+"</tem:emp_code>"
                +"</tem:GetMyLeaveBalance>"
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

                        /*if(tagname.equalsIgnoreCase(TAG_OpeningLeaveBalanceType)){
                            OpeningLeaveBalancetype = text;
                            text = "";
                            System.out.println("OpeningLeaveBalance: "+OpeningLeaveBalancetype);
                        }*/
                        /*if(tagname.equalsIgnoreCase("Paid Leave")){
                            OpeningLeaveBalance7 = text;
                            text = "";
                            System.out.println("OpeningLeaveBalance: "+OpeningLeaveBalance7);
                        }*/
                        if(tagname.equalsIgnoreCase("PLClosing")){
                            OpeningLeaveBalance1 = text;
                            text = "";
                            System.out.println("OpeningLeaveBalance: "+OpeningLeaveBalance1);
                        }
                       /* if(tagname.equalsIgnoreCase(TAG_OpeningLeaveBalance)){
                            if(OpeningLeaveBalancetype.equalsIgnoreCase("1"))
                                OpeningLeaveBalance7 = text;
                           *//* else if(OpeningLeaveBalancetype.equalsIgnoreCase("2"))
                                OpeningLeaveBalance1 = text;
                           *//**//* else if(OpeningLeaveBalancetype.equalsIgnoreCase("3"))
                                OpeningLeaveBalance2 = text;*//**//*
                            else if(OpeningLeaveBalancetype.equalsIgnoreCase("4"))
                                OpeningLeaveBalance3 = text;
                            else if(OpeningLeaveBalancetype.equalsIgnoreCase("6"))
                                OpeningLeaveBalance4 = text;
                            else if(OpeningLeaveBalancetype.equalsIgnoreCase("7"))
                                OpeningLeaveBalance5 = text;
                            else if(OpeningLeaveBalancetype.equalsIgnoreCase("8"))
                                OpeningLeaveBalance6 = text;*//*

                            text = "";
                            System.out.println("OpeningLeaveBalance: "+OpeningLeaveBalance);
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

            setLeaveDetails();
        }//handleMessage(Message msg)

    };

    private void setLeaveDetails(){
        employeeinfoPageLeaveBalanceTextViewID.setText("");
        employeeinfoPageLeaveBalanceTextViewID.setText(OpeningLeaveBalance7);
        employeeinfoPageLeaveBalanceTextViewID1.setText("");
        employeeinfoPageLeaveBalanceTextViewID1.setText(OpeningLeaveBalance1);
       /* employeeinfoPageLeaveBalanceTextViewID2.setText("");
        employeeinfoPageLeaveBalanceTextViewID2.setText(OpeningLeaveBalance2);*/
        employeeinfoPageLeaveBalanceTextViewID3.setText("");
        employeeinfoPageLeaveBalanceTextViewID3.setText(OpeningLeaveBalance4);
        employeeinfoPageLeaveBalanceTextViewID4.setText("");
        employeeinfoPageLeaveBalanceTextViewID4.setText(OpeningLeaveBalance5);
        employeeinfoPageLeaveBalanceTextViewID5.setText("");
        employeeinfoPageLeaveBalanceTextViewID5.setText(OpeningLeaveBalance6);

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
