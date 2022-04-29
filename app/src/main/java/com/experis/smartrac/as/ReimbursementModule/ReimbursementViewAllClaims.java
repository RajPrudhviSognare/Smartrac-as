package com.experis.smartrac.as.ReimbursementModule;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.experis.smartrac.as.Constants;
import com.experis.smartrac.as.CommonUtils;
import com.experis.smartrac.as.R;
import com.experis.smartrac.as.ReimbursementModule.Adapters.CartItemsListAdapter;
import com.experis.smartrac.as.ReimbursementModule.Models.ClaimData;
import com.experis.smartrac.as.Utils;

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
 * Created by RajPrudhviMarella on 14/Mar/2022.
 */

public class ReimbursementViewAllClaims extends AppCompatActivity {

    private ImageView btnBack;
    private TextView txtHeading;
    private RecyclerView lstReimbursement;
    private SharedPreferences prefs;
    private String baseURL;
    private String SOAPRequestXML;
    private HttpResponse httpResponse = null;
    private ProgressDialog progressDialog;
    private SharedPreferences.Editor prefsEditor;
    private List<ClaimData> lstClaims;
    private Button btnSubmit;
    private LinearLayout lytNoResult;
    private LinearLayout lytSubmit;

    private int STATUS_CODE = 0;
    private String STATUS_MESSAGE = "";
    private String STATUS_MESSAGE1 = "";
    private static final int SWIPE_BUTTON_WIDTH = Utils.dpToPx(70);
    private static final int SWIPE_BUTTON_TEXT_SIZE = Utils.dpToPx(14);
    private static final int SWIPE_BUTTON_PADDING_BETWEEN_TEXT_IMAGE = Utils.dpToPx(11);

    @Override
    protected void onResume() {
        callCartitemsApi();
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reimbursement_view_all_claims);
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
        btnSubmit = findViewById(R.id.btn_assign);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitClaims();
            }
        });
        lytNoResult = findViewById(R.id.no_result);
        lytNoResult.setVisibility(View.GONE);
        lytSubmit = findViewById(R.id.lyt_submit_button);
    }

    private void submitClaims() {
        progressDialog.setMessage("Submitting... Please wait!");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String EmpID = prefs.getString("USERISDCODE", "");
        String EmpName = prefs.getString("USERNAME", "");
        baseURL = Constants.base_url_default2;
        SOAPRequestXML = Constants.soapRequestHeader +
                "<soapenv:Header/>"
                + "<soapenv:Body>"
                + "<tem:setSubmitDetails xmlns=\"http://tempuri.org/\">"
                + "<tem:EmpCode>" + EmpID + "</tem:EmpCode>"
                + "<tem:EmpName>" + EmpName + "</tem:EmpName>"
                + "</tem:setSubmitDetails>"
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
                    JSONObject getShowSaveDetailsResponse = soapBody.getJSONObject("setSubmitDetailsResponse");
                    JSONObject getShowSaveDetailsResult = getShowSaveDetailsResponse.getJSONObject("setSubmitDetailsResult");
                    JSONArray stringList = getShowSaveDetailsResult.getJSONArray("string");
                    for (int i = 0; i < stringList.length(); i++) {
                        JSONObject object = stringList.getJSONObject(i);
                        STATUS_MESSAGE = object.getString("content");
                    }
                } catch (
                        HttpResponseException e) {
                    Log.e("exception", "run: " + e.getMessage());
                } catch (
                        Exception e) {
                    Log.e("exception", "run: " + e.getMessage());
                    e.printStackTrace();
                }
                handler2.sendEmptyMessage(0);
            }

        }).start();
    }


    Handler handler2 = new Handler() {

        public void handleMessage(Message msg) {
            try {
                if ((progressDialog != null) && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                if (httpResponse.getStatusLine().getStatusCode() == 200) {
                    showSuccessDialog(STATUS_MESSAGE, false);
                } else {
                    showFailureDialog(STATUS_MESSAGE);
                }
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }//handleMessage(Message msg)

    };


    private void callCartitemsApi() {
        lytNoResult.setVisibility(View.GONE);
        lstClaims = new ArrayList<>();
        progressDialog.setMessage("Requesting... Please wait!");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String EmpID = prefs.getString("USERISDCODE", "");
        baseURL = Constants.base_url_default2;
        SOAPRequestXML = Constants.soapRequestHeader +
                "<soapenv:Header/>"
                + "<soapenv:Body>"
                + "<tem:getShowSaveDetails xmlns=\"http://tempuri.org/\">"
                + "<tem:EmpCode>" + EmpID + "</tem:EmpCode>"
                + "</tem:getShowSaveDetails>"
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
                    JSONObject getShowSaveDetailsResponse = soapBody.getJSONObject("getShowSaveDetailsResponse");
                    JSONObject getShowSaveDetailsResult = getShowSaveDetailsResponse.getJSONObject("getShowSaveDetailsResult");
                    JSONObject diffgram = getShowSaveDetailsResult.getJSONObject("diffgr:diffgram");
                    JSONObject NewDataSet = diffgram.getJSONObject("NewDataSet");

                    if (NewDataSet.get("Table") instanceof JSONObject) {
                        JSONObject object = NewDataSet.getJSONObject("Table");
                        ClaimData claimData = new ClaimData();
                        if (object.has("RequestID"))
                            claimData.setRequestID(object.getString("RequestID"));
                        if (object.has("RowID"))
                            claimData.setRowID(object.getString("RowID"));
                        if (object.has("Head"))
                            claimData.setHead(object.getString("Head"));
                        if (object.has("HotelCity"))
                            claimData.setHotelCity(object.getString("HotelCity"));
                        if (object.has("Limit"))
                            claimData.setLimit(object.getString("Limit"));
                        if (object.has("Amount"))
                            claimData.setAmount(object.getString("Amount"));
                        if (object.has("Mode"))
                            claimData.setMode(object.getString("Mode"));
                        if (object.has("TollTax"))
                            claimData.setTollTax(object.getString("TollTax"));
                        if (object.has("CityName"))
                            claimData.setCityName(object.getString("CityName"));
                        if (object.has("StartTime"))
                            claimData.setStartTime(object.getString("StartTime"));
                        if (object.has("EndTime"))
                            claimData.setEndTime(object.getString("EndTime"));
                        if (object.has("BillFrom"))
                            claimData.setBillFrom(object.getString("BillFrom"));
                        if (object.has("BillTo"))
                            claimData.setBillTo(object.getString("BillTo"));
                        if (object.has("BillNumber"))
                            claimData.setBillNumber(object.getString("BillNumber"));
                        if (object.has("JourneyFrom"))
                            claimData.setJourneyFrom(object.getString("JourneyFrom"));
                        if (object.has("JourneyTo"))
                            claimData.setJourneyTo(object.getString("JourneyTo"));
                        if (object.has("JourneyKM"))
                            claimData.setJourneyKM(object.getString("JourneyKM"));
                        if (object.has("BillDate"))
                            claimData.setBillDate(object.getString("BillDate"));
                        if (object.has("HotelName"))
                            claimData.setHotelName(object.getString("HotelName"));
                        if (object.has("Remarks"))
                            claimData.setRemarks(object.getString("Remarks"));
                        if (object.has("TicketDate"))
                            claimData.setTicketDate(object.getString("TicketDate"));
                        if (object.has("StartKM"))
                            claimData.setStartKM(object.getString("StartKM"));
                        if (object.has("EndKM"))
                            claimData.setEndKM(object.getString("EndKM"));
                        if (object.has("OthersHead"))
                            claimData.setOthersHead(object.getString("OthersHead"));
                        if (object.has("ProofAttach"))
                            claimData.setProofAttach(object.getString("ProofAttach"));
                        if (object.has("ProofAttach2"))
                            claimData.setProofAttach2(object.getString("ProofAttach2"));
                        if (object.has("ProofAttach3"))
                            claimData.setProofAttach3(object.getString("ProofAttach3"));
                        lstClaims.add(claimData);
                    } else if (NewDataSet.get("Table") instanceof JSONArray) {
                        JSONArray Table = NewDataSet.getJSONArray("Table");
                        for (int i = 0; i < Table.length(); i++) {
                            JSONObject object = Table.getJSONObject(i);
                            ClaimData claimData = new ClaimData();
                            if (object.has("RequestID"))
                                claimData.setRequestID(object.getString("RequestID"));
                            if (object.has("RowID"))
                                claimData.setRowID(object.getString("RowID"));
                            if (object.has("Head"))
                                claimData.setHead(object.getString("Head"));
                            if (object.has("HotelCity"))
                                claimData.setHotelCity(object.getString("HotelCity"));
                            if (object.has("Limit"))
                                claimData.setLimit(object.getString("Limit"));
                            if (object.has("Amount"))
                                claimData.setAmount(object.getString("Amount"));
                            if (object.has("Mode"))
                                claimData.setMode(object.getString("Mode"));
                            if (object.has("TollTax"))
                                claimData.setTollTax(object.getString("TollTax"));
                            if (object.has("CityName"))
                                claimData.setCityName(object.getString("CityName"));
                            if (object.has("StartTime"))
                                claimData.setStartTime(object.getString("StartTime"));
                            if (object.has("EndTime"))
                                claimData.setEndTime(object.getString("EndTime"));
                            if (object.has("BillFrom"))
                                claimData.setBillFrom(object.getString("BillFrom"));
                            if (object.has("BillTo"))
                                claimData.setBillTo(object.getString("BillTo"));
                            if (object.has("BillNumber"))
                                claimData.setBillNumber(object.getString("BillNumber"));
                            if (object.has("JourneyFrom"))
                                claimData.setJourneyFrom(object.getString("JourneyFrom"));
                            if (object.has("JourneyTo"))
                                claimData.setJourneyTo(object.getString("JourneyTo"));
                            if (object.has("JourneyKM"))
                                claimData.setJourneyKM(object.getString("JourneyKM"));
                            if (object.has("BillDate"))
                                claimData.setBillDate(object.getString("BillDate"));
                            if (object.has("HotelName"))
                                claimData.setHotelName(object.getString("HotelName"));
                            if (object.has("Remarks"))
                                claimData.setRemarks(object.getString("Remarks"));
                            if (object.has("TicketDate"))
                                claimData.setTicketDate(object.getString("TicketDate"));
                            if (object.has("StartKM"))
                                claimData.setStartKM(object.getString("StartKM"));
                            if (object.has("EndKM"))
                                claimData.setEndKM(object.getString("EndKM"));
                            if (object.has("OthersHead"))
                                claimData.setOthersHead(object.getString("OthersHead"));
                            if (object.has("ProofAttach"))
                                claimData.setProofAttach(object.getString("ProofAttach"));
                            if (object.has("ProofAttach2"))
                                claimData.setProofAttach2(object.getString("ProofAttach2"));
                            if (object.has("ProofAttach3"))
                                claimData.setProofAttach3(object.getString("ProofAttach3"));
                            lstClaims.add(claimData);
                        }
                    }
                } catch (HttpResponseException e) {
                    Log.e("exception", "run: " + e.getMessage());

                } catch (Exception e) {
                    Log.e("exception", "run: " + e.getMessage());
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
                    setAdapter();

                }
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }//handleMessage(Message msg)

    };

    private void setAdapter() {
        if (lstClaims != null && lstClaims.size() > 0) {
            lstReimbursement.setAdapter(new CartItemsListAdapter(this, lstClaims));
            lytSubmit.setVisibility(View.VISIBLE);
            lstReimbursement.setVisibility(View.VISIBLE);
            lytNoResult.setVisibility(View.GONE);
            initUploadInvoiceSwipe();
        } else {
            lytSubmit.setVisibility(View.GONE);
            lstReimbursement.setVisibility(View.GONE);
            lytNoResult.setVisibility(View.VISIBLE);
        }

    }

    private void initUploadInvoiceSwipe() {
        ItemTouchHelper.SimpleCallback uploadedInvoiceSwipeHelper = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            private ColorDrawable background = new ColorDrawable();

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                if (lstReimbursement.getAdapter() != null && lstReimbursement.getAdapter() instanceof CartItemsListAdapter) {
                    final CartItemsListAdapter mAdapter = (CartItemsListAdapter) lstReimbursement.getAdapter();
                    if (mAdapter != null) {
                        final int deleteItemPosition = viewHolder.getAdapterPosition();
                        createDeleteUploadedInvoiceDialog(mAdapter, deleteItemPosition);
                    }
                }
            }

            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                if (lstReimbursement.getAdapter() != null && lstReimbursement.getAdapter() instanceof CartItemsListAdapter) {
                    CartItemsListAdapter mAdapter = (CartItemsListAdapter) lstReimbursement.getAdapter();
                    if (mAdapter != null) {
                        return ItemTouchHelper.LEFT;
                    } else {
                        return 0;
                    }
                } else {
                    return 0;
                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView
                    recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                    int actionState, boolean isCurrentlyActive) {
                if (lstReimbursement.getAdapter() != null && lstReimbursement.getAdapter() instanceof CartItemsListAdapter) {

                    String text = getString(R.string.txt_delete);
                    //InvoiceUploadsAdapter.ViewHolderItem mViewHolder = (InvoiceUploadsAdapter.ViewHolderItem) viewHolder;
                    int color = getResources().getColor(R.color.pinky_red);
                    background.setColor(color);
                    background.setBounds(viewHolder.itemView.getRight() + new Float(dX).intValue(), viewHolder.itemView.getTop(), viewHolder.itemView.getRight(), viewHolder.itemView.getBottom());
                    background.draw(c);
                    Paint p = new Paint();
                    int buttonWidth = viewHolder.itemView.getRight() - SWIPE_BUTTON_WIDTH;
                    RectF rightButton = new RectF(buttonWidth, viewHolder.itemView.getTop(), viewHolder.itemView.getRight(), viewHolder.itemView.getBottom());
                    p.setColor(color);
                    c.drawRect(rightButton, p);

                    p.setColor(Color.WHITE);
                    p.setAntiAlias(true);
                    p.setTextSize(SWIPE_BUTTON_TEXT_SIZE);
                    float textWidth = p.measureText(text);
                    Bitmap bmp = BitmapFactory.decodeResource(getResources(),
                            R.drawable.deletedraft);
                    Rect bounds = new Rect();
                    p.getTextBounds(text, 0, text.length(), bounds);
                    float combinedHeight = bmp.getHeight() + SWIPE_BUTTON_PADDING_BETWEEN_TEXT_IMAGE + bounds.height();
                    c.drawBitmap(bmp, rightButton.centerX() - (bmp.getWidth() / 2), rightButton.centerY() - (combinedHeight / 2), null);
                    c.drawText(text, rightButton.centerX() - (textWidth / 2), rightButton.centerY() + (combinedHeight / 2), p);
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }
            }
        };

        ItemTouchHelper helperUploadInvoice = new ItemTouchHelper(uploadedInvoiceSwipeHelper);
        helperUploadInvoice.attachToRecyclerView(lstReimbursement);
    }


    public void createDeleteUploadedInvoiceDialog(
            final CartItemsListAdapter mAdapter, final int deleteItemPosition) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ReimbursementViewAllClaims.this);
        builder.setPositiveButton(getString(R.string.txt_yes_delete), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteRecord(deleteItemPosition);
                //Remove from the list and update the adapter

                mAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }

        });
        builder.setNegativeButton(getString(R.string.txt_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mAdapter.notifyDataSetChanged();
            }
        });
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setTitle(getString(R.string.txt_delete_claim));
        dialog.setMessage(getString(R.string.txt_want_to_delete));
        dialog.show();
        Button deleteBtn = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        deleteBtn.setTextColor(getResources().getColor(R.color.chart_red));
        deleteBtn.setAllCaps(false);
        Button cancelBtn = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        cancelBtn.setAllCaps(false);
    }

    private void deleteRecord(final int deleteItemPosition) {
        progressDialog.setMessage("Deleting... Please wait!");
        progressDialog.setCancelable(false);
        progressDialog.show();
        baseURL = Constants.base_url_default2;
        SOAPRequestXML = Constants.soapRequestHeader +
                "<soapenv:Header/>"
                + "<soapenv:Body>"
                + "<tem:setDeleteDetails xmlns=\"http://tempuri.org/\">"
                + "<tem:TaskID>" + lstClaims.get(deleteItemPosition).getRequestID() + "</tem:TaskID>"
                + "<tem:RowID>" + lstClaims.get(deleteItemPosition).getRowID() + "</tem:RowID>"
                + "</tem:setDeleteDetails>"
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
                    JSONObject getShowSaveDetailsResponse = soapBody.getJSONObject("setDeleteDetailsResponse");
                    JSONObject getShowSaveDetailsResult = getShowSaveDetailsResponse.getJSONObject("setDeleteDetailsResult");
                    JSONObject diffgram = getShowSaveDetailsResult.getJSONObject("diffgr:diffgram");
                    JSONObject NewDataSet = diffgram.getJSONObject("NewDataSet");
                    JSONObject object = NewDataSet.getJSONObject("Table");
                    if (object.has("Description"))
                        STATUS_MESSAGE1 = object.getString("Description");
                    if (httpResponse.getStatusLine().getStatusCode() == 200) {
                        lstClaims.remove(deleteItemPosition);
                        lstReimbursement.getAdapter().notifyDataSetChanged();
                    } else {
                    }
                } catch (
                        HttpResponseException e) {
                    Log.e("exception", "run: " + e.getMessage());

                } catch (
                        Exception e) {
                    Log.e("exception", "run: " + e.getMessage());
                    e.printStackTrace();
                }
                handler3.sendEmptyMessage(0);
            }

        }).start();
    }

    private void showFailureDialog(String msg) {
        //Alert Dialog Builder
        final AlertDialog.Builder aldb = new AlertDialog.Builder(this);
        aldb.setTitle("Failed!");
        aldb.setMessage("\nReason: " + msg);
        aldb.setPositiveButton("OK", null);
        aldb.show();
    }

    private void showSuccessDialog(String msg, final boolean isFromDelete) {
        //Alert Dialog Builder
        final AlertDialog.Builder aldb = new AlertDialog.Builder(this);
        aldb.setMessage(msg);
        aldb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (!isFromDelete)
                    finish();
            }
        });
        aldb.show();
    }

    Handler handler3 = new Handler() {

        public void handleMessage(Message msg) {
            try {
                if ((progressDialog != null) && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                if (httpResponse.getStatusLine().getStatusCode() == 200) {
                    showSuccessDialog(STATUS_MESSAGE1, true);
                } else {
                    showFailureDialog(STATUS_MESSAGE1);
                }
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
