package com.experis.smartrac.as;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
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
import java.util.List;

public class ViewAttendanceActivity extends AppCompatActivity {

    private SharedPreferences prefs;
    private SharedPreferences.Editor prefsEditor;
    private ProgressDialog progressDialog;

    private ImageView viewAttendancetopbarbackImageViewID,viewAttendancetopbarusericonImageViewID;

    private ListView viewAttendanceDetailsListViewID;
    private TextView viewAttendanceDetailsNoDataTextViewID;

    private String baseURL;
    private String SOAPRequestXML;
    private HttpResponse httpResponse = null;

    private String TAG_EmpCode = "empcode";
    private String EmpCode = "";

    private String TAG_InDate = "indate";
    private String InDate = "";

    private String TAG_InTime = "intime";
    private String InTime = "";

    private String TAG_OutTime = "OutTime";
    private String OutTime = "";

    private String TAG_HoursWorked = "HoursWorked";
    private String HoursWorked = "";

    private List<String> InDateList = null;
    private List<String> InTimeList = null;
    private List<String> OutTimeList = null;
    private List<String> HoursWorkedList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_attendance);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.view_attendance_topbar);

        initAllViews();

        if(CommonUtils.isInternelAvailable(ViewAttendanceActivity.this)){
            getAttendanceDetails();
        }
        else{
            Toast.makeText(ViewAttendanceActivity.this, "No internet connection!", Toast.LENGTH_SHORT).show();
        }

        //Back Button
        viewAttendancetopbarbackImageViewID.setOnClickListener(new ImageView.OnClickListener(){

            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        viewAttendancetopbarusericonImageViewID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(ViewAttendanceActivity.this,ViewAttendanceActivity1.class);
                startActivity(i);
            }
        });

    }//onCreate()

    private void initAllViews(){

        //shared preference
        prefs = getSharedPreferences(CommonUtils.PREFERENCE_NAME,MODE_PRIVATE);
        prefsEditor = prefs.edit();

        viewAttendancetopbarbackImageViewID = (ImageView)findViewById(R.id.viewAttendancetopbarbackImageViewID);
        viewAttendancetopbarusericonImageViewID=(ImageView)findViewById(R.id.viewAttendancetopbarusericonImageViewID);
        viewAttendanceDetailsListViewID = (ListView)findViewById(R.id.viewAttendanceDetailsListViewID);
        viewAttendanceDetailsNoDataTextViewID = (TextView)findViewById(R.id.viewAttendanceDetailsNoDataTextViewID);

        InDateList = new ArrayList<String>(0);
        InTimeList = new ArrayList<String>(0);
        OutTimeList = new ArrayList<String>(0);
        HoursWorkedList = new ArrayList<String>(0);

        //Progress Dialog
        progressDialog = new ProgressDialog(ViewAttendanceActivity.this);
    }

    private void getAttendanceDetails(){
        progressDialog.setMessage("Requesting... Please wait!");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String EmpID = prefs.getString("USERISDCODE","");
        System.out.println("EmpID: "+EmpID);

        baseURL = Constants.base_url_default;
        SOAPRequestXML = Constants.soapRequestHeader+
                "<soapenv:Header/>"
                +"<soapenv:Body>"
                +"<tem:GetMyAttendance>"
                //+"<tem:emp_code>"+100400985+"</tem:emp_code>"
                +"<tem:emp_code>"+EmpID+"</tem:emp_code>"
                +"<tem:from_date>"+""+"</tem:from_date>"
                +"<tem:to_date>"+""+"</tem:to_date>"
                +"</tem:GetMyAttendance>"
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

                    getParsingElementsForAttendanceDetails(xpp);

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

    //getParsingElementsForAttendanceDetails(xpp);
    public void getParsingElementsForAttendanceDetails(XmlPullParser xpp){
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
                        else if(tagname.equalsIgnoreCase(TAG_InDate)){
                            InDate = text;
                            text = "";
                            System.out.println("InDate: "+InDate);
                            InDateList.add(InDate);
                        }
                        else if(tagname.equalsIgnoreCase(TAG_InTime)){
                            InTime = text;
                            text = "";
                            System.out.println("InTime: "+InTime);
                            InTimeList.add(InTime);
                        }
                       /* else if(tagname.equalsIgnoreCase(TAG_OutTime)){
                            OutTime = text;
                            text = "";
                            System.out.println("OutTime: "+OutTime);
                            OutTimeList.add(OutTime);
                        }
                        else if(tagname.equalsIgnoreCase(TAG_HoursWorked)){
                            HoursWorked = text;
                            text = "";
                            System.out.println("HoursWorked: "+HoursWorked);
                            HoursWorkedList.add(HoursWorked);
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
        } catch(IOException e) {
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
            System.out.println("InDateList: "+InDateList.toString());
            System.out.println("InTimeList: "+InTimeList.toString());


            if(InDateList!=null){
                if(InDateList.size()==0){
                    viewAttendanceDetailsListViewID.setVisibility(View.GONE);
                    viewAttendanceDetailsNoDataTextViewID.setVisibility(View.VISIBLE);
                }
                else{
                    viewAttendanceDetailsListViewID.setVisibility(View.VISIBLE);
                    viewAttendanceDetailsNoDataTextViewID.setVisibility(View.GONE);
                    setAttendanceDetails();
                }
            }

        }//handleMessage(Message msg)

    };

    private void setAttendanceDetails(){
        viewAttendanceDetailsListViewID.setAdapter(new CustomAdapterForAttendanceDetails(ViewAttendanceActivity.this));
    }

    /*
    * CustomAdapterForAttendanceDetails
    */
    public class CustomAdapterForAttendanceDetails extends BaseAdapter {

        public Context cntx;

        public CustomAdapterForAttendanceDetails(Context context){
            cntx = context;
        }

        @Override
        public int getCount() {


            if(InDateList!=null) {
                if(InDateList.size()!=0) {

                    int[] numbers = {InDateList.size(),InTimeList.size()};
                    int smallest = Integer.MAX_VALUE;
                    for(int i =0;i<numbers.length;i++) {
                        if(smallest > numbers[i]) {
                            smallest = numbers[i];
                        }
                    }
                    System.out.println("Smallest size is : " +smallest);
                    return smallest;

                    ////return InDateList.size();

                }//if
                else{
                    return 0;
                }
            }
            else{
                return 0;
            }
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
                view = inflater.inflate(R.layout.customlayout_for_viewattendance, null);
            }
            else{
                view = convertView;
            }

            final TextView date = (TextView)view.findViewById(R.id.customlayout_IndateTextViewID);
            final TextView intime = (TextView)view.findViewById(R.id.customlayout_InTimeTextViewID);
            final TextView outtime = (TextView)view.findViewById(R.id.customlayout_OuttimeTextViewID);
            final TextView hoursworked = (TextView)view.findViewById(R.id.customlayout_HoursWorkedTextViewID);

            date.setText(InDateList.get(position));
            intime.setText(InTimeList.get(position));
          /*  outtime.setText(OutTimeList.get(position));
            hoursworked.setText(HoursWorkedList.get(position));*/

            return view;
        }

    }//CustomAdapterForAttendanceDetails Class

    @Override
    public void onBackPressed()
    {
       // super.onBackPressed();
       ViewAttendanceActivity.this.finish();
        overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onResume(){
        super.onResume();
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


}//ViewAttendanceActivity
