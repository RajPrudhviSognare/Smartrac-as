package com.experis.smartrac.as;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.Calendar;

public class HRMSPayslipFragment extends Fragment {

    private PowerManager.WakeLock mWakeLock;
    private SharedPreferences prefs;
    private SharedPreferences.Editor prefsEditor;
    private ProgressDialog progressDialog;

    private Spinner hrmsPayslipMonthSelectionSpinnerID;
    private Spinner hrmsPayslipYearSelectionSpinnerID;
    private AutoCompleteTextView hrmsPayslipYearSelectionAutoCompleteTextViewID;
    private TextView hrmsPayslipPagePaySlipLinkTextViewID;
    private TextView hrmsPayslipOpenMessageTagTextViewID;
    private Button hrmsPayslipBtnID;

    private LinearLayout hrmsPayslipDownloadLinkLayoutID;
    private TextView hrmsPayslipDownloadLinkTagTextViewID;
    private ImageView hrmsPayslipPagePaySlipLinkPDF_DownloadImageViewID;

    private String MONTH[] = {"Select Month","Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
    String[] YEAR = { "2015","2016","2017","2018","2019","2020","2021","2022","2023","2024","2025","2026","2027"};
    String[] YEAR1 = new String[3];

    private String yearSelected = "";
    private String monthSelected = "";
    private boolean validYear = false;
    private boolean validMonth = false;

    private int CURRENT_MONTH;

    private String baseURL;
    private String SOAPRequestXML;
    private HttpResponse httpResponse = null;

    private String TAG_PayslipResult = "PayslipResult";

    private String PayslipResult = "";

    private Calendar calendar;

    public HRMSPayslipFragment() {
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
        View view = inflater.inflate(R.layout.hrms_payslip_fragment, container, false);

        hrmsPayslipMonthSelectionSpinnerID = (Spinner)view.findViewById(R.id.hrmsPayslipMonthSelectionSpinnerID);
        hrmsPayslipYearSelectionSpinnerID = (Spinner)view.findViewById(R.id.hrmsPayslipYearSelectionSpinnerID);
        hrmsPayslipYearSelectionAutoCompleteTextViewID = (AutoCompleteTextView)view.findViewById(R.id.hrmsPayslipYearSelectionAutoCompleteTextViewID);
        hrmsPayslipPagePaySlipLinkTextViewID = (TextView)view.findViewById(R.id.hrmsPayslipPagePaySlipLinkTextViewID);
        hrmsPayslipBtnID = (Button)view.findViewById(R.id.hrmsPayslipBtnID);

        hrmsPayslipDownloadLinkLayoutID = (LinearLayout)view.findViewById(R.id.hrmsPayslipDownloadLinkLayoutID);
        hrmsPayslipDownloadLinkTagTextViewID = (TextView)view.findViewById(R.id.hrmsPayslipDownloadLinkTagTextViewID);
        hrmsPayslipOpenMessageTagTextViewID = (TextView)view.findViewById(R.id.hrmsPayslipOpenMessageTagTextViewID);
        hrmsPayslipPagePaySlipLinkPDF_DownloadImageViewID = (ImageView)view.findViewById(R.id.hrmsPayslipPagePaySlipLinkPDF_DownloadImageViewID);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        YEAR1[0] = String.valueOf(calendar.get(Calendar.YEAR));
        System.out.println("YEAR1[0]: "+ YEAR1[0]);
        YEAR1[1] = String.valueOf(calendar.get(Calendar.YEAR)-1);
        System.out.println("YEAR1[1]: "+ YEAR1[1]);
        YEAR1[2] = String.valueOf(calendar.get(Calendar.YEAR)-2);
        System.out.println("YEAR1[2]: "+ YEAR1[2]);

        yearSelected = YEAR1[0];
        validYear = true;

        CURRENT_MONTH = calendar.get(Calendar.MONTH)+1;
        System.out.println("CURRENT_MONTH: "+ CURRENT_MONTH);

        /*ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.select_dialog_item,YEAR1);
        hrmsPayslipYearSelectionAutoCompleteTextViewID.setThreshold(1);
        hrmsPayslipYearSelectionAutoCompleteTextViewID.setAdapter(adapter);
        hrmsPayslipYearSelectionAutoCompleteTextViewID.setTextColor(Color.BLACK);*/

        ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, MONTH);
        dataAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        hrmsPayslipMonthSelectionSpinnerID.setAdapter(dataAdapter1);

        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, YEAR1);
        dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        hrmsPayslipYearSelectionSpinnerID.setAdapter(dataAdapter2);

        //Month
        hrmsPayslipMonthSelectionSpinnerID.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                monthSelected = parent.getItemAtPosition(position).toString();
                System.out.println("Payslip monthSelected: "+ monthSelected);
                if(position==0){
                    validMonth = false;
                }
                else{
                      if(yearSelected.equalsIgnoreCase(String.valueOf(calendar.get(Calendar.YEAR)))){
                          if(position<CURRENT_MONTH){
                              validMonth = true;
                          }
                          else{
                              Toast.makeText(getActivity(),"Please Select a Valid Month!",Toast.LENGTH_LONG).show();
                              hrmsPayslipMonthSelectionSpinnerID.setSelection(0);
                              validMonth = false;
                          }
                      }
                      else{
                          validMonth = true;
                      }

                }
                System.out.println("Payslip validMonth: "+ validMonth);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //Year
        hrmsPayslipYearSelectionSpinnerID.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                yearSelected = parent.getItemAtPosition(position).toString();
                System.out.println("Payslip yearSelected: "+ yearSelected);
                validYear = true;
                System.out.println("Payslip validYear: "+ validYear);

                if(yearSelected.equalsIgnoreCase(String.valueOf(calendar.get(Calendar.YEAR)))){
                    validMonth = false;
                    hrmsPayslipMonthSelectionSpinnerID.setSelection(0);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        hrmsPayslipBtnID.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                //yearSelected = hrmsPayslipYearSelectionAutoCompleteTextViewID.getText().toString();
                if (yearSelected.equalsIgnoreCase("")){
                    validYear = false;
                    Toast.makeText(getActivity(),"Please select a valid Year",Toast.LENGTH_SHORT).show();
                }
                if(!yearSelected.equalsIgnoreCase("")){
                    validYear = true;
                }
                System.out.println("Payslip validYear: "+ validYear);
                System.out.println("Payslip validMonth: "+ validMonth);

                if(validYear&&validMonth){
                      validYear = false;
                      validMonth = false;
                      hrmsPayslipMonthSelectionSpinnerID.setSelection(0);
                      hrmsPayslipYearSelectionSpinnerID.setSelection(0);

                      getPaySlipLink();
                }
                else{
                    Toast.makeText(getActivity(),"Please Select a valid Year/Month",Toast.LENGTH_SHORT).show();
                }
            }
        });

        //PDF download link click; Used Earlier with TextView link
        /*hrmsPayslipPagePaySlipLinkTextViewID.setOnClickListener(new TextView.OnClickListener(){
            @Override
            public void onClick(View v) {
                  if(hrmsPayslipPagePaySlipLinkTextViewID.getText().toString().startsWith("http")){
                      Intent i = new Intent(Intent.ACTION_VIEW);
                      i.setData(Uri.parse(hrmsPayslipPagePaySlipLinkTextViewID.getText().toString()));
                      startActivity(i);
                  }
                else{
                      Toast.makeText(getActivity(),"Invalid Download Link!",Toast.LENGTH_LONG).show();
                  }
            }
        });*/

        //PDF Icon Click
        hrmsPayslipPagePaySlipLinkPDF_DownloadImageViewID.setOnClickListener(new TextView.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(PayslipResult.toString().startsWith("http")){
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(PayslipResult.toString()));
                    getActivity().startActivity(i);
                }
                else{
                    Toast.makeText(getActivity(),"Invalid Download Link!",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void getPaySlipLink(){
        progressDialog.setMessage("Requesting... Please wait!");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String EmpID = prefs.getString("USERISDCODE","");
        System.out.println("EmpID: "+EmpID);

        baseURL = Constants.base_url_default;
        SOAPRequestXML = Constants.soapRequestHeader+
                "<soapenv:Header/>"
                +"<soapenv:Body>"
                +"<tem:Payslip>"
                //+"<tem:emp_code>"+100172254+"</tem:emp_code>"
                +"<tem:emp_code>"+EmpID+"</tem:emp_code>"
                +"<tem:month_name>"+monthSelected+"</tem:month_name>"
                +"<tem:year>"+yearSelected+"</tem:year>"
                +"</tem:Payslip>"
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

                    getParsingElementsForPaySlip(xpp);

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

    //getParsingElementsForPaySlip(xpp);
    public void getParsingElementsForPaySlip(XmlPullParser xpp){
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

                        if(tagname.equalsIgnoreCase(TAG_PayslipResult)){
                            PayslipResult = text;
                            text = "";
                            System.out.println("PayslipResult: "+PayslipResult);
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

            setPaySlipLink();
        }//handleMessage(Message msg)

    };
    private void setPaySlipLink(){
        hrmsPayslipDownloadLinkLayoutID.setVisibility(View.VISIBLE);
        hrmsPayslipPagePaySlipLinkTextViewID.setText("");
        if(PayslipResult.startsWith("http")){
            //hrmsPayslipDownloadLinkTagTextViewID.setVisibility(View.VISIBLE); //Used Earlier with Textview link
            //hrmsPayslipPagePaySlipLinkTextViewID.setPaintFlags(hrmsPayslipPagePaySlipLinkTextViewID.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);//Adds Underline;//Used Earlier with Textview link
            //hrmsPayslipPagePaySlipLinkTextViewID.setText(PayslipResult);//Used Earlier with Textview link
            //hrmsPayslipPagePaySlipLinkTextViewID.setTextColor(Color.BLUE);//Used Earlier with Textview link
            //hrmsPayslipPagePaySlipLinkTextViewID.setClickable(true);//Used Earlier with Textview link

            hrmsPayslipDownloadLinkTagTextViewID.setVisibility(View.GONE);
            hrmsPayslipPagePaySlipLinkTextViewID.setClickable(false);
            hrmsPayslipPagePaySlipLinkPDF_DownloadImageViewID.setVisibility(View.VISIBLE);
            hrmsPayslipOpenMessageTagTextViewID.setText("");
            hrmsPayslipOpenMessageTagTextViewID.setText("Note: "+prefs.getString("PAYSLIP_MESSAGE","Please Use Your Employee Code to Open The Payslip"));
            showPayslipStatusDialog("Payslip is available!");
        }
        else{
             hrmsPayslipPagePaySlipLinkPDF_DownloadImageViewID.setVisibility(View.GONE);//Added later for PDF Icon Click
             hrmsPayslipDownloadLinkTagTextViewID.setVisibility(View.GONE);
             hrmsPayslipPagePaySlipLinkTextViewID.setPaintFlags(hrmsPayslipPagePaySlipLinkTextViewID.getPaintFlags() & (~Paint.UNDERLINE_TEXT_FLAG));//Removes Underline
             if(!PayslipResult.equalsIgnoreCase("")){
                 hrmsPayslipPagePaySlipLinkTextViewID.setText(PayslipResult);
                 hrmsPayslipOpenMessageTagTextViewID.setText("");
                 showPayslipStatusDialog(PayslipResult);
             }
            else{
                 hrmsPayslipPagePaySlipLinkTextViewID.setText("Payslip is not available, try again!");
                 hrmsPayslipOpenMessageTagTextViewID.setText("");
                 showPayslipStatusDialog("Payslip is not available!");
             }
             hrmsPayslipPagePaySlipLinkTextViewID.setTextColor(Color.RED);
             hrmsPayslipPagePaySlipLinkTextViewID.setClickable(false);
        }

    }

    private void initAllViews(){
        //shared preference
        prefs = getActivity().getSharedPreferences(CommonUtils.PREFERENCE_NAME,getActivity().MODE_PRIVATE);
        prefsEditor = prefs.edit();

        //Progress Dialog
        progressDialog = new ProgressDialog(getActivity());

        calendar = Calendar.getInstance();
    }

    //Show Payslip Status Dialog
    private void showPayslipStatusDialog(String msg){
        //Alert Dialog Builder
        final AlertDialog.Builder aldb = new AlertDialog.Builder(getActivity());
        aldb.setMessage(msg);
        aldb.setPositiveButton("OK", null);
        aldb.show();
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
