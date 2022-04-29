package com.experis.smartrac.as.ReimbursementModule;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.experis.smartrac.as.CommonUtils;
import com.experis.smartrac.as.Constants;
import com.experis.smartrac.as.R;
import com.experis.smartrac.as.ReimbursementModule.Adapters.ReimbursementHeaderAdapter;
import com.experis.smartrac.as.ReimbursementModule.Models.ReimbursementHeaderModel;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import fr.arnaudguyon.xmltojsonlib.XmlToJson;

/**
 * Created by RajPrudhviMarella on 08/Dec/2021.
 */

public class ReimbursementDashBoardActivity
        extends AppCompatActivity {
    private ImageView btnBack;
    private TextView txtHeading;
    private RecyclerView lstReimbursement;
    private SharedPreferences prefs;
    private String baseURL;
    private String SOAPRequestXML;
    private HttpResponse httpResponse = null;
    private ProgressDialog progressDialog;
    private SharedPreferences.Editor prefsEditor;
    private ReimbursementHeaderModel headerModel;
    private Button btnViewAll;
    List<ReimbursementHeaderModel> lstHeaders = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reimbersement_dash_board);

        //shared preference
        prefs = getSharedPreferences(CommonUtils.PREFERENCE_NAME, MODE_PRIVATE);
        prefsEditor = prefs.edit();
        btnBack = findViewById(R.id.employeeinfotopbarbackImageViewID);
        txtHeading = findViewById(R.id.mytext);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
            }
        });
        lstReimbursement = findViewById(R.id.lst_sub_header_list);
        lstReimbursement.setLayoutManager(new LinearLayoutManager(this));
        progressDialog = new ProgressDialog(this);
        btnViewAll = findViewById(R.id.btn_assign);
        btnViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ReimbursementDashBoardActivity.this, ReimbursementViewAllClaims.class);
                startActivity(intent);
                overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
            }
        });
        getExpenseTypeDetails();
    }

    private void getExpenseTypeDetails() {
        lstHeaders = new ArrayList<>();
        progressDialog.setMessage("Requesting... Please wait!");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String EmpID = prefs.getString("USERISDCODE", "");
        System.out.println("EmpID: " + EmpID);

        baseURL = Constants.base_url_default2;
        SOAPRequestXML = Constants.soapRequestHeader +
                "<soapenv:Header/>"
                + "<soapenv:Body>"
                + "<tem:getExpenseType xmlns=\"http://tempuri.org/\" />"
                + "</soapenv:Body>"
                + "</soapenv:Envelope>";

        //String msgLength = String.format("%1$d", SOAPRequestXML.length());
        System.out.println("Request== " + SOAPRequestXML);

        new Thread(new Runnable() {

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
                    Log.e("response", "run: " + Response);
                    XmlToJson xmlToJson = new XmlToJson.Builder(Response).build();
                    // convert to a Json String
                    String jsonString = xmlToJson.toString();
                    JSONObject jsonObject = new JSONObject(jsonString);
                    JSONObject Body = jsonObject.getJSONObject("soap:Envelope");
                    JSONObject soapBody = Body.getJSONObject("soap:Body");
                    JSONObject getShowSaveDetailsResponse = soapBody.getJSONObject("getExpenseTypeResponse");
                    JSONObject getShowSaveDetailsResult = getShowSaveDetailsResponse.getJSONObject("getExpenseTypeResult");
                    JSONObject diffgram = getShowSaveDetailsResult.getJSONObject("diffgr:diffgram");
                    JSONObject NewDataSet = diffgram.getJSONObject("NewDataSet");

                    Log.e("response json", "run: " + jsonString);

                    if (NewDataSet.get("Table") instanceof JSONObject) {
                        JSONObject object = NewDataSet.getJSONObject("Table");
                        ReimbursementHeaderModel reimbursementHeaderModel = new ReimbursementHeaderModel();
                        reimbursementHeaderModel.setName(object.getString("HeaderName"));
                        lstHeaders.add(reimbursementHeaderModel);
                    } else {
                        JSONArray Table = NewDataSet.getJSONArray("Table");
                        for (int i = 1; i < Table.length(); i++) {
                            JSONObject object = Table.getJSONObject(i);
                            ReimbursementHeaderModel reimbursementHeaderModel = new ReimbursementHeaderModel();
                            reimbursementHeaderModel.setName(object.getString("HeaderName"));
                            lstHeaders.add(reimbursementHeaderModel);
                        }
                    }
                } catch (HttpResponseException e) {
                    Log.e("response", "run: " + e.getMessage());
                } catch (Exception e) {
                    Log.e("response", "run: " + e.getMessage());
                    e.printStackTrace();
                }
                handler.sendEmptyMessage(0);
            }
        }).start();

    }

    Handler handler = new Handler() {

        public void handleMessage(Message msg) {
            try {
                if ((progressDialog != null) && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                lstReimbursement.setAdapter(new ReimbursementHeaderAdapter(ReimbursementDashBoardActivity.this, lstHeaders));
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }//handleMessage(Message msg)

    };

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
    }
}