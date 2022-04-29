package com.experis.smartrac.as;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
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
import java.util.ArrayList;
import java.util.Calendar;

public class LetterFragment extends Fragment {

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
    private ImageView appointmentdownload,incrementdownload,incrementstructuredownload;

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

    private String TAG_PayslipResult = "GetLetterResult";

    private String AppointmentLetter = "";
    private String IncrementLetterC = "";
    private String IncrementLetterD = "";

    private Calendar calendar;
    private ArrayList<String> doc;
    private TextView apoointletter,incrementletter,incrementstructure;
    private String tag="0";

    public LetterFragment() {
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
        View view = inflater.inflate(R.layout.fragment_letter, container, false);

        apoointletter=(TextView)view.findViewById(R.id.apoointletter);
        incrementletter=(TextView)view.findViewById(R.id.incrementletter);
        incrementstructure=(TextView)view.findViewById(R.id.incrementstructure);
      /*  hrmsPayslipMonthSelectionSpinnerID = (Spinner)view.findViewById(R.id.hrmsPayslipMonthSelectionSpinnerID);
        hrmsPayslipYearSelectionSpinnerID = (Spinner)view.findViewById(R.id.hrmsPayslipYearSelectionSpinnerID);
        hrmsPayslipYearSelectionAutoCompleteTextViewID = (AutoCompleteTextView)view.findViewById(R.id.hrmsPayslipYearSelectionAutoCompleteTextViewID);

        hrmsPayslipBtnID = (Button)view.findViewById(R.id.hrmsPayslipBtnID);*/
       /* hrmsPayslipPagePaySlipLinkTextViewID = (TextView)view.findViewById(R.id.hrmsPayslipPagePaySlipLinkTextViewID);
        hrmsPayslipDownloadLinkLayoutID = (LinearLayout)view.findViewById(R.id.hrmsPayslipDownloadLinkLayoutID);
        hrmsPayslipDownloadLinkTagTextViewID = (TextView)view.findViewById(R.id.hrmsPayslipDownloadLinkTagTextViewID);
        hrmsPayslipOpenMessageTagTextViewID = (TextView)view.findViewById(R.id.hrmsPayslipOpenMessageTagTextViewID);*/
        appointmentdownload = (ImageView)view.findViewById(R.id.appointmentdownload);
        incrementdownload = (ImageView)view.findViewById(R.id.incrementdownload);
        incrementstructuredownload = (ImageView)view.findViewById(R.id.incrementstructuredownload);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getPaySlipLink();





        appointmentdownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(AppointmentLetter.toString().startsWith("http")){
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(AppointmentLetter.toString()));
                    getActivity().startActivity(i);
                }
                else{
                    Toast.makeText(getActivity(),"Invalid Download Link!",Toast.LENGTH_SHORT).show();
                }

            }
        });

        incrementdownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(IncrementLetterC.toString().startsWith("http")){
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(IncrementLetterC.toString()));
                    getActivity().startActivity(i);
                }
                else{
                    Toast.makeText(getActivity(),"Invalid Download Link!",Toast.LENGTH_SHORT).show();
                }
            }
        });

        incrementstructuredownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(IncrementLetterD.toString().startsWith("http")){
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(IncrementLetterD.toString()));
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
       /* String EmpID = "100407695";*/
        System.out.println("EmpID: "+EmpID);

        baseURL = Constants.base_url_default;
        SOAPRequestXML = Constants.soapRequestHeader+
                "<soapenv:Header/>"
                +"<soapenv:Body>"
                +"<tem:GetLetter>"
                //+"<tem:emp_code>"+100172254+"</tem:emp_code>"
                +"<tem:emp_code>"+EmpID+"</tem:emp_code>"
                +"</tem:GetLetter>"
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



   /* private ArrayList<String> parseJokes(XmlPullParser xpp) {
        ArrayList<String> jokes = new ArrayList<String>();
      //  XmlResourceParser xpp = getResources().getXml(R.xml.jokes);


        try {
            int eventType=xpp.getEventType();
            while (eventType!=XmlPullParser.END_DOCUMENT) {
                if(eventType==XmlPullParser.START_TAG){
                    if (xpp.getName().equals("joke")) {
                        jokes.add(xpp.nextText());
                    }
                }

                eventType= xpp.next();
            }
        } catch (XmlPullParserException | IOException e) {
            Log.e("XmlPullParserException", e.toString());
        }
        xpp.close();
        return jokes;
    }*/
    
    
    //getParsingElementsForPaySlip(xpp);
    public void getParsingElementsForPaySlip(XmlPullParser xpp){
        String text = "";
        try {
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {

                String tagname;
                // = xpp.getName()
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        tagname=xpp.getName();
                        if(tagname.equalsIgnoreCase(TAG_PayslipResult)) {

           /*to handle nested tags.
           "xpp.nextTag()" goes to the next starting tag  immediately following "ItemArray",
           and "itemID = xpp.nextText()" assigns the text within that tag
            to the string itemID*/

                            xpp.nextTag();
                            AppointmentLetter = xpp.nextText();
                            Log.d("Listing ", AppointmentLetter);


                            xpp.nextTag();
                            IncrementLetterC = xpp.nextText();
                            Log.d("Listing ", IncrementLetterC);

                            xpp.nextTag();
                            IncrementLetterD = xpp.nextText();
                            Log.d("Listing ", IncrementLetterD);

                           /* xpp.nextTag();
                            listingType = xpp.nextText();
                            Log.d("Listing ", listingType);*/

                        }


                        break;

                    case XmlPullParser.TEXT:
                      /*  text = xpp.getText().trim().toString();
                        System.out.println("Text data: "+text);*/
                        break;


                    case XmlPullParser.END_TAG:

                        /*if(tagname.equalsIgnoreCase(TAG_PayslipResult)){
                            PayslipResult = text;
                            text = "";
                            System.out.println("PayslipResult: "+PayslipResult);
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

            setPaySlipLink();
        }//handleMessage(Message msg)

    };
    private void setPaySlipLink(){

        if(AppointmentLetter.startsWith("http")){
            //hrmsPayslipDownloadLinkTagTextViewID.setVisibility(View.VISIBLE); //Used Earlier with Textview link
            //hrmsPayslipPagePaySlipLinkTextViewID.setPaintFlags(hrmsPayslipPagePaySlipLinkTextViewID.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);//Adds Underline;//Used Earlier with Textview link
            //hrmsPayslipPagePaySlipLinkTextViewID.setText(PayslipResult);//Used Earlier with Textview link
            //hrmsPayslipPagePaySlipLinkTextViewID.setTextColor(Color.BLUE);//Used Earlier with Textview link
            //hrmsPayslipPagePaySlipLinkTextViewID.setClickable(true);//Used Earlier with Textview link


            appointmentdownload.setClickable(true);
            appointmentdownload.setVisibility(View.VISIBLE);

        }
        else{
            appointmentdownload.setClickable(false);
            appointmentdownload.setVisibility(View.GONE);
            if(!AppointmentLetter.equalsIgnoreCase("")){

                showPayslipStatusDialog(AppointmentLetter);
            }
            else{

                showPayslipStatusDialog("Document is not available!");
            }

        }


//increment
        if(IncrementLetterC.startsWith("http")){
            //hrmsPayslipDownloadLinkTagTextViewID.setVisibility(View.VISIBLE); //Used Earlier with Textview link
            //hrmsPayslipPagePaySlipLinkTextViewID.setPaintFlags(hrmsPayslipPagePaySlipLinkTextViewID.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);//Adds Underline;//Used Earlier with Textview link
            //hrmsPayslipPagePaySlipLinkTextViewID.setText(PayslipResult);//Used Earlier with Textview link
            //hrmsPayslipPagePaySlipLinkTextViewID.setTextColor(Color.BLUE);//Used Earlier with Textview link
            //hrmsPayslipPagePaySlipLinkTextViewID.setClickable(true);//Used Earlier with Textview link


            incrementdownload.setClickable(true);
            incrementdownload.setVisibility(View.VISIBLE);

        }
        else{
            incrementdownload.setClickable(false);
            incrementdownload.setVisibility(View.GONE);
            if(!IncrementLetterC.equalsIgnoreCase("")){

                showPayslipStatusDialog(IncrementLetterC);
            }
            else{

                showPayslipStatusDialog("Document is not available!");
            }

        }


        ///structure
        if(IncrementLetterD.startsWith("http")){
            //hrmsPayslipDownloadLinkTagTextViewID.setVisibility(View.VISIBLE); //Used Earlier with Textview link
            //hrmsPayslipPagePaySlipLinkTextViewID.setPaintFlags(hrmsPayslipPagePaySlipLinkTextViewID.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);//Adds Underline;//Used Earlier with Textview link
            //hrmsPayslipPagePaySlipLinkTextViewID.setText(PayslipResult);//Used Earlier with Textview link
            //hrmsPayslipPagePaySlipLinkTextViewID.setTextColor(Color.BLUE);//Used Earlier with Textview link
            //hrmsPayslipPagePaySlipLinkTextViewID.setClickable(true);//Used Earlier with Textview link


            incrementstructuredownload.setClickable(true);
            incrementstructuredownload.setVisibility(View.VISIBLE);

        }
        else{
            incrementstructuredownload.setClickable(false);
            incrementstructuredownload.setVisibility(View.GONE);
            if(!IncrementLetterD.equalsIgnoreCase("")){

                showPayslipStatusDialog(IncrementLetterD);
            }
            else{

                showPayslipStatusDialog("Document is not available!");
            }

        }




        /* doc=new ArrayList<>();
        doc.add(AppointmentLetter);
        doc.add(IncrementLetterC);
        doc.add(IncrementLetterD);*/




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
