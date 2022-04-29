package com.experis.smartrac.as.ReimbursementModule;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.experis.smartrac.as.Constants;
import com.experis.smartrac.as.CommonUtils;
import com.experis.smartrac.as.R;
import com.experis.smartrac.as.ReimbursementModule.Adapters.HomeWorkImageListAdapter;
import com.experis.smartrac.as.ReimbursementModule.Models.ClaimData;
import com.experis.smartrac.as.ReimbursementModule.Models.InQueueForUploadDataModel;
import com.experis.smartrac.as.ReimbursementModule.Models.InternalStorageFileDataModel;
import com.experis.smartrac.as.Utils;
import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import fr.arnaudguyon.xmltojsonlib.XmlToJson;

/**
 * Created by RajPrudhviMarella on 09/Dec/2021.
 */

public class ReimbursementLodgingFormActivity extends AppCompatActivity {
    private String reimbursementHeader = "";
    private ImageView btnBack;
    private TextView txtHeading;
    private SharedPreferences prefs;
    private SharedPreferences.Editor prefsEditor;
    private TextView billFromValue;
    private TextView billToValue;
    private EditText billNoValue;
    private TextView billDateValue;
    private EditText limitValue;
    private Spinner travelModeValue;
    private EditText cityValue;
    private EditText journeyFromValue;
    private EditText journeyToValue;
    private TextView startTimeValue;
    private TextView endTimeValue;
    private EditText tollTaxValue;
    private EditText amountValue;
    private EditText remarksValue;
    private EditText hotelNameValue;
    private Button btnSave;
    private Spinner spinnerStatusList;
    private RelativeLayout lytSelectPhotos;
    private RelativeLayout lytListPhotos;
    private RecyclerView lstPhotos;
    private TextView txtSelectPhoto;
    private TextView txtUploadsHeader;
    private ImageView imgCamera;
    public static final int RESULT_LOAD_IMAGE = 101;
    public static final int RESULT_LOAD_CAMERA = 102;
    Uri outPutfileUri;
    private Random rand = new Random();
    private ArrayList<InQueueForUploadDataModel> invoiceDataList = new ArrayList<>();
    private String startTime = "";
    private String endTime = "";
    private String billFromDate = "";
    private String billToDate = "";
    private String billDate = "";
    private String subHeader = "";
    private String travelMode = "";
    private ProgressDialog progressDialog;
    private String baseURL;
    private HttpResponse httpResponse = null;
    private String encodedImageIntoString1 = null;
    private String encodedImageIntoString2 = null;
    private String SOAPRequestXML;
    private int STATUS_CODE = 0;
    private ClaimData lstClaim;
    private String STATUS_MESSAGE = "";
    private String limit = "0";
    List<String> statusList = new ArrayList<>();
    List<String> travelModeList = new ArrayList<>();
    private TextView txtDateHeader;
    private TextView txtDateValue;
    private String dateValue = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reimbursement_lodging_form);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey("REIMBURSEMENT_HEADER")) {
            reimbursementHeader = bundle.getString("REIMBURSEMENT_HEADER");
        }
        if (bundle != null && bundle.containsKey("CLAIM_DATA")) {
            lstClaim = Utils.gsonExposeExclusive.fromJson(bundle.getString("CLAIM_DATA"), ClaimData.class);
        }
        //shared preference
        prefs = getSharedPreferences(CommonUtils.PREFERENCE_NAME, MODE_PRIVATE);
        prefsEditor = prefs.edit();
        progressDialog = new ProgressDialog(ReimbursementLodgingFormActivity.this);
        btnBack = findViewById(R.id.employeeinfotopbarbackImageViewID);
        txtHeading = findViewById(R.id.mytext);
        txtHeading.setText(reimbursementHeader);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        spinnerStatusList = findViewById(R.id.spin_status);
        billFromValue = findViewById(R.id.txt_bill_from_date_value);
        billToValue = findViewById(R.id.txt_bill_to_date_value);
        billNoValue = findViewById(R.id.edt_home_work);
        billDateValue = findViewById(R.id.txt_bill_date_value);
        limitValue = findViewById(R.id.edt_assignment);
        travelModeValue = findViewById(R.id.edt_assignment_description);
        travelModeList = new ArrayList<>();
        travelModeList.add("select");
        travelModeList.add("Car");
        travelModeList.add("Train");
        travelModeList.add("Bus");
        travelModeList.add("Others");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ReimbursementLodgingFormActivity.this, R.layout.spinner_item_selected, travelModeList);
        adapter.setDropDownViewResource(R.layout.spinner_drop_down_layout);
        travelModeValue.setAdapter(adapter);
        travelModeValue.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!travelModeValue.getSelectedItem().toString().equals("select")) {
                    travelMode = travelModeValue.getSelectedItem().toString();
                } else {
                    travelMode = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        cityValue = findViewById(R.id.edt_city);
        journeyFromValue = findViewById(R.id.edt_jurney_from);
        journeyToValue = findViewById(R.id.edt_journey_to);
        startTimeValue = findViewById(R.id.txt_assignment_due_date_header_value);
        endTimeValue = findViewById(R.id.txt_end_time_value);
        endTimeValue = findViewById(R.id.txt_end_time_value);
        tollTaxValue = findViewById(R.id.edt_time_duration);
        amountValue = findViewById(R.id.edt_amount);
        remarksValue = findViewById(R.id.edt_remarks);
        hotelNameValue = findViewById(R.id.edt_hotel_name);
        btnSave = findViewById(R.id.btn_assign);

        lytSelectPhotos = findViewById(R.id.lyt_add_images);
        txtSelectPhoto = findViewById(R.id.txt_select);
        lytSelectPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (verifyStoragePermissions() && verifyCameraPermissions()) {
                    imagePickerDialog();
                }
            }
        });
        imgCamera = findViewById(R.id.img_camera);
        imgCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (verifyStoragePermissions() && verifyCameraPermissions()) {
                    imagePickerDialog();
                }
            }
        });
        txtSelectPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (verifyStoragePermissions() && verifyCameraPermissions()) {
                    imagePickerDialog();
                }
            }
        });
        lstPhotos = findViewById(R.id.lst_images);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        lstPhotos.setLayoutManager(layoutManager);
        lytListPhotos = findViewById(R.id.lyt_image_list);
        lytListPhotos.setVisibility(View.GONE);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (CommonUtils.isInternelAvailable(ReimbursementLodgingFormActivity.this)) {
                        if (validate()) {
                            submitAttandance();
                        }
                    } else {
                        Toast.makeText(ReimbursementLodgingFormActivity.this, "No internet connection!", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        txtDateHeader = findViewById(R.id.txt_request_date_header);
        txtDateValue = findViewById(R.id.txt_request_date_value);
        txtDateValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SingleDateAndTimePickerDialog.Builder(ReimbursementLodgingFormActivity.this)
                        .titleTextColor(getResources().getColor(R.color.whitecolor))
                        .minutesStep(1)
                        .maxDateRange(new Date())
                        .listener(new SingleDateAndTimePickerDialog.Listener() {
                            @Override
                            public void onDateSelected(Date date) {
                                String DATE_FORMAT_NOW1 = "dd/MMM/YYYY";
                                String DATE_FORMAT_NOW2 = "dd/MMM/YYYY";
                                SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW1);
                                SimpleDateFormat sdf2 = new SimpleDateFormat(DATE_FORMAT_NOW2);
                                txtDateValue.setText(sdf.format(date));
                                txtDateValue.setError(null);
                                dateValue = sdf2.format(date);
                            }
                        }).display();
            }
        });
        startTimeValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SingleDateAndTimePickerDialog.Builder(ReimbursementLodgingFormActivity.this)
                        .titleTextColor(getResources().getColor(R.color.whitecolor))
                        .minutesStep(1)
                        .maxDateRange(new Date())
                        .listener(new SingleDateAndTimePickerDialog.Listener() {
                            @Override
                            public void onDateSelected(Date date) {
                                String DATE_FORMAT_NOW1 = "HH:mm";
                                String DATE_FORMAT_NOW2 = "HH:mm";
                                SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW1);
                                SimpleDateFormat sdf2 = new SimpleDateFormat(DATE_FORMAT_NOW2);
                                startTimeValue.setText(sdf.format(date));
                                startTimeValue.setError(null);
                                startTime = sdf2.format(date);
                            }
                        }).display();
            }
        });
        endTimeValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SingleDateAndTimePickerDialog.Builder(ReimbursementLodgingFormActivity.this)
                        .titleTextColor(getResources().getColor(R.color.whitecolor))
                        .minutesStep(1)
                        .maxDateRange(new Date())
                        .listener(new SingleDateAndTimePickerDialog.Listener() {
                            @Override
                            public void onDateSelected(Date date) {
                                String DATE_FORMAT_NOW1 = "HH:mm";
                                String DATE_FORMAT_NOW2 = "HH:mm";
                                SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW1);
                                SimpleDateFormat sdf2 = new SimpleDateFormat(DATE_FORMAT_NOW2);
                                endTimeValue.setText(sdf.format(date));
                                endTimeValue.setError(null);
                                endTime = sdf2.format(date);
                            }
                        }).display();
            }
        });
        billFromValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SingleDateAndTimePickerDialog.Builder(ReimbursementLodgingFormActivity.this)
                        .titleTextColor(getResources().getColor(R.color.whitecolor))
                        .minutesStep(1)
                        .maxDateRange(new Date())
                        .listener(new SingleDateAndTimePickerDialog.Listener() {
                            @Override
                            public void onDateSelected(Date date) {
                                String DATE_FORMAT_NOW1 = "dd/MMM/YYYY";
                                String DATE_FORMAT_NOW2 = "dd/MMM/YYYY";
                                SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW1);
                                SimpleDateFormat sdf2 = new SimpleDateFormat(DATE_FORMAT_NOW2);
                                billFromValue.setText(sdf.format(date));
                                billFromValue.setError(null);
                                billFromDate = sdf2.format(date);
                            }
                        }).display();
            }
        });
        billToValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SingleDateAndTimePickerDialog.Builder(ReimbursementLodgingFormActivity.this)
                        .titleTextColor(getResources().getColor(R.color.whitecolor))
                        .minutesStep(1)
                        .maxDateRange(new Date())
                        .listener(new SingleDateAndTimePickerDialog.Listener() {
                            @Override
                            public void onDateSelected(Date date) {
                                String DATE_FORMAT_NOW1 = "dd/MMM/YYYY";
                                String DATE_FORMAT_NOW2 = "dd/MMM/YYYY";
                                SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW1);
                                SimpleDateFormat sdf2 = new SimpleDateFormat(DATE_FORMAT_NOW2);
                                billToValue.setText(sdf.format(date));
                                billToValue.setError(null);
                                billToDate = sdf2.format(date);
                            }
                        }).display();
            }
        });
        billDateValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SingleDateAndTimePickerDialog.Builder(ReimbursementLodgingFormActivity.this)
                        .titleTextColor(getResources().getColor(R.color.whitecolor))
                        .minutesStep(1)
                        .listener(new SingleDateAndTimePickerDialog.Listener() {
                            @Override
                            public void onDateSelected(Date date) {
                                String DATE_FORMAT_NOW1 = "dd/MMM/YYYY";
                                String DATE_FORMAT_NOW2 = "dd/MMM/YYYY";
                                SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW1);
                                SimpleDateFormat sdf2 = new SimpleDateFormat(DATE_FORMAT_NOW2);
                                billDateValue.setText(sdf.format(date));
                                billDateValue.setError(null);
                                billDate = sdf2.format(date);
                            }
                        }).display();
            }
        });
        if (lstClaim != null) {

            for (int i = 0; i < travelModeList.size(); i++) {
                if (travelModeList.get(i).equalsIgnoreCase(lstClaim.getMode())) {
                    travelModeValue.setSelection(i);
                    break;
                }
            }
            cityValue.setText(lstClaim.getHotelCity());
            journeyFromValue.setText(lstClaim.getJourneyFrom());
            journeyToValue.setText(lstClaim.getJourneyTo());
            startTimeValue.setText(lstClaim.getStartTime());
            endTimeValue.setText(lstClaim.getEndTime());
            tollTaxValue.setText(lstClaim.getTollTax());
            hotelNameValue.setText(lstClaim.getHotelName());
            amountValue.setText(lstClaim.getAmount());
            remarksValue.setText(lstClaim.getRemarks());
            billDateValue.setText(lstClaim.getBillDate());
            billDate = lstClaim.getBillDate();
            billToDate = lstClaim.getBillTo();
            billFromDate = lstClaim.getBillFrom();
            endTime = lstClaim.getEndTime();
            startTime = lstClaim.getStartTime();
            billFromValue.setText(lstClaim.getBillFrom());
            billToValue.setText(lstClaim.getBillTo());
            billNoValue.setText(lstClaim.getBillNumber());
            if (lstClaim.getProofAttach() != null && !lstClaim.getProofAttach().isEmpty() && lstClaim.getProofAttach().length() > 0) {
                InQueueForUploadDataModel invoiceImageDataModel = new InQueueForUploadDataModel();
                invoiceImageDataModel.setByteCode(lstClaim.getProofAttach());
                if (invoiceDataList != null && invoiceDataList.size() < 3) {
                    invoiceDataList.add(invoiceImageDataModel);
                }
            }
            if (lstClaim.getProofAttach2() != null && !lstClaim.getProofAttach2().isEmpty() && lstClaim.getProofAttach2().length() > 0) {
                InQueueForUploadDataModel invoiceImageDataModel = new InQueueForUploadDataModel();
                invoiceImageDataModel.setByteCode(lstClaim.getProofAttach2());
                if (invoiceDataList != null && invoiceDataList.size() < 3) {
                    invoiceDataList.add(invoiceImageDataModel);
                }
            }
            if (lstClaim.getProofAttach3() != null && !lstClaim.getProofAttach3().isEmpty() && lstClaim.getProofAttach3().length() > 0) {
                InQueueForUploadDataModel invoiceImageDataModel = new InQueueForUploadDataModel();
                invoiceImageDataModel.setByteCode(lstClaim.getProofAttach3());
                if (invoiceDataList != null && invoiceDataList.size() < 3) {
                    invoiceDataList.add(invoiceImageDataModel);
                }
            }

            if (invoiceDataList != null && invoiceDataList.size() > 0) {
                InQueueForUploadDataModel inQueueForUploadDataModel = new InQueueForUploadDataModel();
                inQueueForUploadDataModel.setNewAdded(true);
                invoiceDataList.add(inQueueForUploadDataModel);
                lytListPhotos.setVisibility(View.VISIBLE);
                lytSelectPhotos.setVisibility(View.GONE);
                lstPhotos.setAdapter(new HomeWorkImageListAdapter(this, invoiceDataList, new HomeWorkImageListAdapter.onNewCameraClicked() {
                    @Override
                    public void onNewCameraClicked() {
                        if (verifyStoragePermissions() && verifyCameraPermissions()) {
                            imagePickerDialog();
                        }
                    }

                    @Override
                    public void onViewImageClicked(InQueueForUploadDataModel inQueueForUploadDataModel) {
                    }
                }));
            } else {
                lytListPhotos.setVisibility(View.GONE);
                lytSelectPhotos.setVisibility(View.VISIBLE);
            }
            txtDateValue.setText(lstClaim.getTicketDate());
            btnSave.setText("Update");
        }
        callSubheadApi();
    }

    private void callSubheadApi() {
        statusList = new ArrayList<>();
        statusList.add("select");
        progressDialog.setMessage("Requesting... Please wait!");
        progressDialog.setCancelable(false);
        progressDialog.show();

        baseURL = Constants.base_url_default2;
        SOAPRequestXML = Constants.soapRequestHeader +
                "<soapenv:Header/>"
                + "<soapenv:Body>"
                + "<tem:getSubHead xmlns=\"http://tempuri.org/\">"
                + "<tem:Head>" + reimbursementHeader + "</tem:Head>"
                + "</tem:getSubHead>"
                + "</soapenv:Body>"
                + "</soapenv:Envelope>";

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
                    JSONObject getShowSaveDetailsResponse = soapBody.getJSONObject("getSubHeadResponse");
                    JSONObject getShowSaveDetailsResult = getShowSaveDetailsResponse.getJSONObject("getSubHeadResult");
                    JSONObject diffgram = getShowSaveDetailsResult.getJSONObject("diffgr:diffgram");
                    JSONObject NewDataSet = diffgram.getJSONObject("NewDataSet");
                    JSONArray table = NewDataSet.getJSONArray("Table");
                    for (int i = 0; i < table.length(); i++) {
                        JSONObject object = table.getJSONObject(i);
                        String subHeader = object.getString("SubHead");
                        statusList.add(subHeader);
                    }
                    Log.e("response json", "run: " + jsonString);

                } catch (HttpResponseException e) {
                    Log.e("response", "run: " + e.getMessage());
                } catch (Exception e) {
                    Log.e("response", "run: " + e.getMessage());
                    e.printStackTrace();
                }
                handler4.sendEmptyMessage(0);
            }
        }).start();
    }

    Handler handler4 = new Handler() {

        public void handleMessage(Message msg) {
            try {
                if ((progressDialog != null) && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(ReimbursementLodgingFormActivity.this, R.layout.spinner_item_selected, statusList);
                adapter.setDropDownViewResource(R.layout.spinner_drop_down_layout);
                spinnerStatusList.setAdapter(adapter);
                spinnerStatusList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (!spinnerStatusList.getSelectedItem().toString().equals("select")) {
                            subHeader = spinnerStatusList.getSelectedItem().toString();
                            callLimitAPI();
                        } else {
                            subHeader = "";
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
            } catch (final Exception e) {
                e.printStackTrace();
            }
            if (lstClaim != null)
                for (int i = 0; i < statusList.size(); i++) {
                    if (statusList.get(i).equalsIgnoreCase(lstClaim.getOthersHead())) {
                        spinnerStatusList.setSelection(i);
                        break;
                    }
                }
        }//handleMessage(Message msg)

    };
//
//    private void callTravelModeApi() {
//        progressDialog.setMessage("Requesting... Please wait!");
//        progressDialog.setCancelable(false);
//        progressDialog.show();
//
//        baseURL = Constants.base_url_default2;
//        SOAPRequestXML = Constants.soapRequestHeader +
//                "<soapenv:Header/>"
//                + "<soapenv:Body>"
//                + "<tem:getSubHead xmlns=\"http://tempuri.org/\">"
//                + "<tem:Head>" + reimbursementHeader + "</tem:Head>"
//                + "</tem:getSubHead>"
//                + "</soapenv:Body>"
//                + "</soapenv:Envelope>";
//
//        new Thread(new Runnable() {
//
//            @Override
//            public void run() {
//                // TODO Auto-generated method stub
//
//                try {
//                    HttpPost httppost = new HttpPost(baseURL);
//                    StringEntity se = new StringEntity(SOAPRequestXML, HTTP.UTF_8);
//                    se.setContentType("text/xml");
//                    httppost.setHeader("Content-Type", "text/xml;charset=UTF-8");
//                    httppost.setEntity(se);
//                    HttpClient httpclient = new DefaultHttpClient();
//                    httpResponse = null;
//                    httpResponse = (HttpResponse) httpclient.execute(httppost);
//                    String Response = new BasicResponseHandler().handleResponse(httpResponse);
//
//                    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
//                    factory.setNamespaceAware(true);
//                    XmlPullParser xpp = factory.newPullParser();
//                    xpp.setInput(new StringReader(Response));
//                    Log.e("response", "run: " + Response);
//                    XmlToJson xmlToJson = new XmlToJson.Builder(Response).build();
//                    // convert to a Json String
//                    String jsonString = xmlToJson.toString();
//                    JSONObject jsonObject = new JSONObject(jsonString);
//                    JSONObject Body = jsonObject.getJSONObject("soap:Envelope");
//                    JSONObject soapBody = Body.getJSONObject("soap:Body");
//                    JSONObject getShowSaveDetailsResponse = soapBody.getJSONObject("getSubHeadResponse");
//                    JSONObject getShowSaveDetailsResult = getShowSaveDetailsResponse.getJSONObject("getSubHeadResult");
//                    JSONObject diffgram = getShowSaveDetailsResult.getJSONObject("diffgr:diffgram");
//                    JSONObject NewDataSet = diffgram.getJSONObject("NewDataSet");
//                    JSONArray table = NewDataSet.getJSONArray("Table");
//                    for (int i = 0; i < table.length(); i++) {
//                        JSONObject object = table.getJSONObject(i);
//                        String subHeader = object.getString("SubHead");
//                        travelModeList.add(subHeader);
//                    }
//                    Log.e("response json", "run: " + jsonString);
//
//                } catch (HttpResponseException e) {
//                    Log.e("response", "run: " + e.getMessage());
//                } catch (Exception e) {
//                    Log.e("response", "run: " + e.getMessage());
//                    e.printStackTrace();
//                }
//                handler5.sendEmptyMessage(0);
//            }
//        }).start();
//    }
//
//    Handler handler5 = new Handler() {
//
//        public void handleMessage(Message msg) {
//            try {
//                if ((progressDialog != null) && progressDialog.isShowing()) {
//                    progressDialog.dismiss();
//                }
//                ArrayAdapter<String> adapter = new ArrayAdapter<String>(ReimbursementLodgingFormActivity.this, R.layout.spinner_item_selected, travelModeList);
//                adapter.setDropDownViewResource(R.layout.spinner_drop_down_layout);
//                travelModeValue.setAdapter(adapter);
//                travelModeValue.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                    @Override
//                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                        travelMode = travelModeValue.getSelectedItem().toString();
//                    }
//
//                    @Override
//                    public void onNothingSelected(AdapterView<?> adapterView) {
//
//                    }
//                });
//            } catch (final Exception e) {
//                e.printStackTrace();
//            }
//        }//handleMessage(Message msg)
//
//    };

    private void callLimitAPI() {
        String EmpDesignation = prefs.getString("USER_DESIGNATION", "");
        Log.e("EmpDesignation", "callLimitAPI: " + EmpDesignation);
        final String baseURL = Constants.base_url_default2;
        final String SOAPRequestXML = Constants.soapRequestHeader +
                "<soapenv:Header/>"
                + "<soapenv:Body>"
                + "<tem:getLimit xmlns=\"http://tempuri.org/\">"
                + "<tem:Head>" + reimbursementHeader + "</tem:Head>"
                + "<tem:SubHead>" + spinnerStatusList.getSelectedItem() + "</tem:SubHead>"
                + "<tem:designation>" + EmpDesignation + "</tem:designation>"
                + "</tem:getLimit>"
                + "</soapenv:Body>"
                + "</soapenv:Envelope>";

        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    HttpPost httppost = new HttpPost(baseURL);
                    StringEntity se = new StringEntity(SOAPRequestXML, HTTP.UTF_8);
                    se.setContentType("text/xml");
                    httppost.setHeader("Content-Type", "text/xml");
                    httppost.setEntity(se);
                    HttpClient httpclient = new DefaultHttpClient();
                    httpResponse = null;
                    httpResponse = (HttpResponse) httpclient.execute(httppost);
                    String Response = new BasicResponseHandler().handleResponse(httpResponse);
                    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                    factory.setNamespaceAware(true);
                    XmlPullParser xpp = factory.newPullParser();
                    xpp.setInput(new StringReader(Response));
                    XmlToJson xmlToJson = new XmlToJson.Builder(Response).build();
                    String jsonString = xmlToJson.toString();
                    JSONObject jsonObject = new JSONObject(jsonString);
                    JSONObject Body = jsonObject.getJSONObject("soap:Envelope");
                    JSONObject soapBody = Body.getJSONObject("soap:Body");
                    JSONObject getShowSaveDetailsResponse = soapBody.getJSONObject("getLimitResponse");
                    JSONObject getShowSaveDetailsResult = getShowSaveDetailsResponse.getJSONObject("getLimitResult");
                    JSONObject diffgram = getShowSaveDetailsResult.getJSONObject("diffgr:diffgram");
                    JSONObject NewDataSet = diffgram.getJSONObject("NewDataSet");
                    JSONObject object = NewDataSet.getJSONObject("Table");
                    if (object.has("Limit"))
                        limit = object.getString("Limit");
                    Log.e("EmpDesignation", "callLimitAPI: " + limit);
                } catch (
                        HttpResponseException e) {
                    Log.e("exception", "run: " + e.getMessage());
                } catch (
                        Exception e) {
                    Log.e("exception", "run: " + e.getMessage());
                    e.printStackTrace();
                }
            }

        }).start();
    }

    private void submitAttandance() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MMM/yyyy");
        String cur_date = dateFormat.format(calendar.getTime());


        progressDialog.setMessage("Saving... Please wait!");
        progressDialog.setCancelable(false);
        progressDialog.show();
        baseURL = Constants.base_url_default2;
        String encodedImageIntoString1 = "";
        if (invoiceDataList != null && invoiceDataList.size() > 0 && invoiceDataList.get(0) != null && invoiceDataList.get(0).getByteCode() != null && !invoiceDataList.get(0).getByteCode().isEmpty()) {
            encodedImageIntoString1 = invoiceDataList.get(0).getByteCode();
        }
        String encodedImageIntoString2 = "";
        if (invoiceDataList != null && invoiceDataList.size() > 0 && invoiceDataList.get(1) != null && invoiceDataList.get(1).getByteCode() != null && !invoiceDataList.get(1).getByteCode().isEmpty()) {
            encodedImageIntoString2 = invoiceDataList.get(1).getByteCode();
        }
        if (lstClaim != null) {
            SOAPRequestXML = Constants.soapRequestHeader +
                    "<soapenv:Header/>"
                    + "<soapenv:Body>"
                    + "<tem:UpdateDetails xmlns=\"http://tempuri.org/\">"
                    + "<tem:TaskID>" + lstClaim.getRequestID() + "</tem:TaskID>"
                    + "<tem:RowID>" + lstClaim.getRowID() + "</tem:RowID>"
                    + "<tem:EmpCode>" + prefs.getString("USERISDCODE", "") + "</tem:EmpCode>"
                    + "<tem:head>" + reimbursementHeader + "</tem:head>"
                    + "<tem:OthersHead>" + subHeader + "</tem:OthersHead>"
                    + "<tem:DateRqst>" + dateValue + "</tem:DateRqst>"
                    + "<tem:bill_from_date>" + billFromDate + "</tem:bill_from_date>"
                    + "<tem:bill_to_date>" + billToDate + "</tem:bill_to_date>"
                    + "<tem:bill_number>" + billNoValue.getText().toString() + "</tem:bill_number>"
                    + "<tem:bill_date>" + billDate + "</tem:bill_date>"
                    + "<tem:TravelMode>" + travelMode + "</tem:TravelMode>"
                    + "<tem:ddlcity>" + cityValue.getText().toString() + "</tem:ddlcity>"
                    + "<tem:journey_to>" + journeyToValue.getText().toString() + "</tem:journey_to>"
                    + "<tem:journey_from>" + journeyFromValue.getText().toString() + "</tem:journey_from>"
                    + "<tem:JStartTime>" + startTime.toString() + "</tem:JStartTime>"
                    + "<tem:JEndTime>" + endTime + "</tem:JEndTime>"
                    + "<tem:TollTax>" + tollTaxValue.getText().toString() + "</tem:TollTax>"
                    + "<tem:txtStartKM>" + "0" + "</tem:txtStartKM>"
                    + "<tem:txtEndKM>" + "0" + "</tem:txtEndKM>"
                    + "<tem:journey_km>" + "0" + "</tem:journey_km>"
                    + "<tem:amount>" + amountValue.getText().toString() + "</tem:amount>"
                    + "<tem:remarks>" + remarksValue.getText().toString() + "</tem:remarks>"
                    + "<tem:HotelName>" + hotelNameValue.getText().toString() + "</tem:HotelName>"
                    + "<tem:limit>" + limit + "</tem:limit>"
                    + "<tem:proofattach1>" + encodedImageIntoString1 + "</tem:proofattach1>"
                    + "<tem:proofattach2>" + encodedImageIntoString2 + "</tem:proofattach2>"
                    + "</tem:UpdateDetails>"
                    + "</soapenv:Body>"
                    + "</soapenv:Envelope>";

        } else {
            SOAPRequestXML = Constants.soapRequestHeader +
                    "<soapenv:Header/>"
                    + "<soapenv:Body>"
                    + "<tem:setSaveDetails xmlns=\"http://tempuri.org/\">"
                    + "<tem:EmpCode>" + prefs.getString("USERISDCODE", "") + "</tem:EmpCode>"
                    + "<tem:head>" + reimbursementHeader + "</tem:head>"
                    + "<tem:OthersHead>" + subHeader + "</tem:OthersHead>"
                    + "<tem:DateRqst>" + dateValue + "</tem:DateRqst>"
                    + "<tem:bill_from_date>" + billFromDate + "</tem:bill_from_date>"
                    + "<tem:bill_to_date>" + billToDate + "</tem:bill_to_date>"
                    + "<tem:bill_number>" + billNoValue.getText().toString() + "</tem:bill_number>"
                    + "<tem:bill_date>" + billDate + "</tem:bill_date>"
                    + "<tem:TravelMode>" + travelMode + "</tem:TravelMode>"
                    + "<tem:ddlcity>" + cityValue.getText().toString() + "</tem:ddlcity>"
                    + "<tem:journey_to>" + journeyToValue.getText().toString() + "</tem:journey_to>"
                    + "<tem:journey_from>" + journeyFromValue.getText().toString() + "</tem:journey_from>"
                    + "<tem:JStartTime>" + startTime.toString() + "</tem:JStartTime>"
                    + "<tem:JEndTime>" + endTime + "</tem:JEndTime>"
                    + "<tem:TollTax>" + tollTaxValue.getText().toString() + "</tem:TollTax>"
                    + "<tem:txtStartKM>" + "0" + "</tem:txtStartKM>"
                    + "<tem:txtEndKM>" + "0" + "</tem:txtEndKM>"
                    + "<tem:journey_km>" + "0" + "</tem:journey_km>"
                    + "<tem:amount>" + amountValue.getText().toString() + "</tem:amount>"
                    + "<tem:remarks>" + remarksValue.getText().toString() + "</tem:remarks>"
                    + "<tem:HotelName>" + hotelNameValue.getText().toString() + "</tem:HotelName>"
                    + "<tem:limit>" + limit + "</tem:limit>"
                    + "<tem:proofattach1>" + encodedImageIntoString1 + "</tem:proofattach1>"
                    + "<tem:proofattach2>" + encodedImageIntoString2 + "</tem:proofattach2>"
                    + "</tem:setSaveDetails>"
                    + "</soapenv:Body>"
                    + "</soapenv:Envelope>";
        }

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
                    StatusLine status = httpResponse.getStatusLine();
                    STATUS_CODE = status.getStatusCode();
                    XmlToJson xmlToJson = new XmlToJson.Builder(Response).build();
                    String jsonString = xmlToJson.toString();
                    JSONObject jsonObject = new JSONObject(jsonString);
                    JSONObject Body = jsonObject.getJSONObject("soap:Envelope");
                    JSONObject soapBody = Body.getJSONObject("soap:Body");
                    JSONObject getShowSaveDetailsResponse = null;
                    JSONObject getShowSaveDetailsResult = null;
                    if (lstClaim != null) {
                        getShowSaveDetailsResponse = soapBody.getJSONObject("UpdateDetailsResponse");
                        getShowSaveDetailsResult = getShowSaveDetailsResponse.getJSONObject("UpdateDetailsResult");
                    } else {
                        getShowSaveDetailsResponse = soapBody.getJSONObject("setSaveDetailsResponse");
                        getShowSaveDetailsResult = getShowSaveDetailsResponse.getJSONObject("setSaveDetailsResult");
                    }
                    JSONObject diffgram = getShowSaveDetailsResult.getJSONObject("diffgr:diffgram");
                    JSONObject NewDataSet = diffgram.getJSONObject("NewDataSet");
                    JSONObject object = NewDataSet.getJSONObject("Table");
                    if (object.has("Description"))
                        STATUS_MESSAGE = object.getString("Description");
                } catch (HttpResponseException e) {
                    Log.i("httpResponse Error = ", e.getMessage());
                } catch (Exception e) {
                    // TODO Auto-generated catch block
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
                if (httpResponse.getStatusLine().getStatusCode() == 200) {
                    showSuccessDialog(STATUS_MESSAGE);
                } else {
                    showFailureDialog(STATUS_MESSAGE);
                }
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }//handleMessage(Message msg)

    };

    private boolean validate() {
        boolean isValid = true;
        if (billFromDate == null || billFromDate.length() == 0) {
            billFromValue.setError("Invalid");
            isValid = false;
        } else {
            billFromValue.setError(null);
        }
        if (travelMode.toString() == null || travelMode.length() == 0) {
            isValid = false;
        }
        if (subHeader.toString() == null || subHeader.length() == 0) {
            isValid = false;
        }
        if (billToDate == null || billToDate.length() == 0) {
            billToValue.setError("Invalid");
            isValid = false;
        } else {
            billToValue.setError(null);
        }
        if (billNoValue.getText().toString() == null || billNoValue.getText().toString().length() == 0) {
            billNoValue.setError("Invalid");
            isValid = false;
        } else {
            billNoValue.setError(null);
        }
        if (billDate == null || billDate.length() == 0) {
            billDateValue.setError("Invalid");
            isValid = false;
        } else {
            billDateValue.setError(null);
        }

//        if (travelModeValue.getText().toString() == null || travelModeValue.getText().toString().length() == 0) {
//            travelModeValue.setError("Invalid");
//            isValid = false;
//        } else {
//            travelModeValue.setError(null);
//        }
        if (journeyFromValue.getText().toString() == null || journeyFromValue.getText().toString().length() == 0) {
            journeyFromValue.setError("Invalid");
            isValid = false;
        } else {
            journeyFromValue.setError(null);
        }
        if (journeyToValue.getText().toString() == null || journeyToValue.getText().toString().length() == 0) {
            journeyToValue.setError("Invalid");
            isValid = false;
        } else {
            journeyToValue.setError(null);
        }
        if (tollTaxValue.getText().toString() == null || tollTaxValue.getText().toString().length() == 0) {
            tollTaxValue.setError("Invalid");
            isValid = false;
        } else {
            tollTaxValue.setError(null);
        }
        if (startTime == null || startTime.length() == 0) {
            startTimeValue.setError("Invalid");
            isValid = false;
        } else {
            startTimeValue.setError(null);
        }
        if (endTime == null || endTime.length() == 0) {
            endTimeValue.setError("Invalid");
            isValid = false;
        } else {
            endTimeValue.setError(null);
        }
        if (amountValue.getText().toString() == null || amountValue.getText().toString().length() == 0) {
            amountValue.setError("Invalid");
            isValid = false;
        } else {
            if (amountValue.getText().toString() != null && amountValue.getText().toString().equalsIgnoreCase("0")) {
                amountValue.setError("Invalid");
                isValid = false;
            } else {
                amountValue.setError(null);
            }
        }
        if (remarksValue.getText().toString() == null || remarksValue.getText().toString().length() == 0) {
            remarksValue.setError("Invalid");
            isValid = false;
        } else {
            remarksValue.setError(null);
        }
        return isValid;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Uri uri = null;
            Bitmap bitmap = null;
            if (requestCode == RESULT_LOAD_CAMERA) {
                uri = outPutfileUri;
                try {
                    bitmap = ImageRotationHelper.handleSamplingAndRotationBitmap(ReimbursementLodgingFormActivity.this, outPutfileUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                uri = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            AssetFileDescriptor fileDescriptor = null;
            try {
                fileDescriptor = getApplicationContext().getContentResolver().openAssetFileDescriptor(uri, "r");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            assert fileDescriptor != null;
            long fileSize = fileDescriptor.getLength();
            if (fileSize < 50000000) {
                Cursor returnCursor =
                        getContentResolver().query(uri, null, null, null, null);
                int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                returnCursor.moveToFirst();
                String fileName = randomString(7);
                InQueueForUploadDataModel invoiceImageDataModel = new InQueueForUploadDataModel();
                if (bitmap != null) {
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                    byte[] byteArray = byteArrayOutputStream.toByteArray();
                    invoiceImageDataModel.setByteCode(Base64.encodeToString(byteArray, Base64.DEFAULT));
                }
                if (invoiceDataList != null && invoiceDataList.size() > 0) {
                    invoiceDataList.remove(invoiceDataList.size() - 1);
                }
                if (invoiceDataList != null && invoiceDataList.size() < 3) {
                    invoiceDataList.add(invoiceImageDataModel);
                } else {
                    Toast.makeText(this, "Only 3files Can Upload", Toast.LENGTH_SHORT).show();

                }
                if (invoiceDataList != null && invoiceDataList.size() > 0) {
                    InQueueForUploadDataModel inQueueForUploadDataModel = new InQueueForUploadDataModel();
                    inQueueForUploadDataModel.setNewAdded(true);
                    invoiceDataList.add(inQueueForUploadDataModel);
                    lytListPhotos.setVisibility(View.VISIBLE);
                    lytSelectPhotos.setVisibility(View.GONE);
                    lstPhotos.setAdapter(new HomeWorkImageListAdapter(this, invoiceDataList, new HomeWorkImageListAdapter.onNewCameraClicked() {
                        @Override
                        public void onNewCameraClicked() {
                            if (verifyStoragePermissions() && verifyCameraPermissions()) {
                                imagePickerDialog();
                            }
                        }

                        @Override
                        public void onViewImageClicked(InQueueForUploadDataModel inQueueForUploadDataModel) {
                        }
                    }));
                } else {
                    lytListPhotos.setVisibility(View.GONE);
                    lytSelectPhotos.setVisibility(View.VISIBLE);
                }
            } else {
                Toast.makeText(this, "Please upload file below 5mb", Toast.LENGTH_SHORT).show();
            }


        }

    }

    private void imagePickerDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Select from:");
        String[] options = {"Camera", "Gallery"};
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0://Gallery selected
                        dialog.dismiss();
                        if (verifyCameraPermissions())
                            imagePicker(RESULT_LOAD_CAMERA);
                        break;
                    case 1://document selected
                        dialog.dismiss();
                        imagePicker(RESULT_LOAD_IMAGE);
                        break;
                    default:
                        break;
                }
            }
        });
        // Create the AlertDialog
        android.app.AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void imagePicker(int source) {
        if (source == RESULT_LOAD_IMAGE) {
            Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, RESULT_LOAD_IMAGE);
        } else {
            Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            String auth = getApplicationContext().getPackageName() + ".com.experis.smartrac.nh";
            try {
                outPutfileUri = FileProvider.getUriForFile(this, auth, createImageFile());
            } catch (IOException e) {
                e.printStackTrace();
            }

            captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, outPutfileUri);
            captureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivityForResult(captureIntent, RESULT_LOAD_CAMERA);
        }
    }

    private boolean verifyStoragePermissions() {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                this.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, RESULT_LOAD_IMAGE);
            }
            return false;
        }
        return true;
    }

    private boolean verifyCameraPermissions() {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                this.requestPermissions(new String[]{Manifest.permission.CAMERA}, RESULT_LOAD_CAMERA);
            }
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[],
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case RESULT_LOAD_IMAGE:
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
                break;
            case RESULT_LOAD_CAMERA:
                Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                String auth = getApplicationContext().getPackageName() + ".com.experis.smartrac.nh";
                try {
                    outPutfileUri = FileProvider.getUriForFile(this, auth, createImageFile());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, outPutfileUri);
                captureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                startActivityForResult(captureIntent, RESULT_LOAD_CAMERA);
                break;
            default:
                Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",        /* suffix */
                storageDir      /* directory */
        );
        return image;
    }

    /**
     * save the pdf file to internal storage in the specified folder
     *
     * @param uri
     * @param filename
     * @return
     */
    public InternalStorageFileDataModel savePdfInInternalStorage(String filename, Uri uri) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String directoryName = timeStamp + "_" + randomString(3);
        File folder = getFilesDir();
        File invoiceDirectory = new File(folder, "eHomeWork");
        invoiceDirectory.mkdir();
        File newInvoiceDirectory = new File(invoiceDirectory, directoryName);
        newInvoiceDirectory.mkdir();
        File file = new File(newInvoiceDirectory, filename);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        InputStream fis;
        try {
            fis = getContentResolver().openInputStream(uri);
            byte[] buf = new byte[1024];
            int n;
            while (-1 != (n = fis.read(buf)))
                baos.write(buf, 0, n);
        } catch (Exception e) {
            e.printStackTrace();
        }
        byte[] bytes = baos.toByteArray();
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(bytes);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        InternalStorageFileDataModel filePath = new InternalStorageFileDataModel();
        filePath.setDirectoryPath(newInvoiceDirectory.getAbsolutePath());
        filePath.setFilePath(file.getAbsolutePath());
        return filePath;
    }

    /**
     * generate a random string of specified length
     *
     * @param len
     * @return
     */
    public String randomString(int len) {
        final String DATA = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
        StringBuilder sb = new StringBuilder(len);

        for (int i = 0; i < len; i++) {
            sb.append(DATA.charAt(rand.nextInt(DATA.length())));
        }

        return sb.toString();
    }

    /**
     * save the bitmap to internal storage in the specified folder
     *
     * @param bitmap
     * @return
     */
    public InternalStorageFileDataModel saveInInternalStorage(Bitmap bitmap, String filename) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String directoryName = timeStamp + "_" + randomString(3);
        //String imageFileName = "JPEG_" + timeStamp + "_"+randomString(2);
        File folder = getFilesDir();
        File invoiceDirectory = new File(folder, "eHomeWork");
        invoiceDirectory.mkdir();
        File newInvoiceDirectory = new File(invoiceDirectory, directoryName);
        newInvoiceDirectory.mkdir();
        File file = new File(newInvoiceDirectory, filename);
        try {

            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            Log.d("", "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d("", "Error accessing file: " + e.getMessage());
        }
        InternalStorageFileDataModel filePath = new InternalStorageFileDataModel();

        filePath.setDirectoryPath(newInvoiceDirectory.getAbsolutePath());
        filePath.setFilePath(file.getAbsolutePath());
        return filePath;
    }

    private ByteArrayOutputStream getBytesData(Uri uri) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        InputStream fis;
        try {
            fis = getContentResolver().openInputStream(uri);
            byte[] buf = new byte[1024];
            int n;
            while (-1 != (n = fis.read(buf)))
                baos.write(buf, 0, n);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return baos;
    }

    /**
     * return the rotated bitmap as per the current orientaion of the selected image from the image Uri
     *
     * @param uri
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public Bitmap decodeSampledBitmapFromImageUri(Uri uri, int reqWidth, int reqHeight) {

        int rotationInDegrees = 0;
        try {
            ExifInterface exif = new ExifInterface(uri.getPath());
            int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            rotationInDegrees = exifToDegrees(rotation);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        try {
            InputStream ims = getContentResolver().openInputStream(uri);
            BitmapFactory.decodeStream(ims, null, options);

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
            Log.d("BITMAP SZ B4 UPLOADING", options.inSampleSize + "***");
            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            options.inDither = true;
            ims.close();
            InputStream imsNew = getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(imsNew, null, options);
            Bitmap rotatedBitmap = null;
            if (bitmap != null) {
                rotatedBitmap = rotateBitmap(bitmap, rotationInDegrees);
            }
            return rotatedBitmap;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * return the rotated bitmap as per the rotation angle
     *
     * @param source
     * @param angle
     * @return
     */
    public static Bitmap rotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    /**
     * return the current rotation of and image
     *
     * @param exifOrientation
     * @return
     */
    private static int exifToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        Log.d("B4 sample size", inSampleSize + "*********");
        return inSampleSize;
    }

    private void showFailureDialog(String msg) {
        //Alert Dialog Builder
        final AlertDialog.Builder aldb = new AlertDialog.Builder(ReimbursementLodgingFormActivity.this);
        aldb.setTitle("Failed!");
        aldb.setMessage("\nReason: " + msg);
        aldb.setPositiveButton("OK", null);
        aldb.show();
    }

    private void showSuccessDialog(final String msg) {
        //Alert Dialog Builder
        final AlertDialog.Builder aldb = new AlertDialog.Builder(ReimbursementLodgingFormActivity.this);
        aldb.setMessage(msg);
        aldb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (msg.equalsIgnoreCase("Details has been saved successfully") || msg.equalsIgnoreCase("Details has been updated successfully")) {
                    finish();
                }
            }
        });
        aldb.show();
    }
}