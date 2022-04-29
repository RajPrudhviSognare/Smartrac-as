package com.experis.smartrac.as;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AttendanceIntimeFragmet extends Fragment {

    private String TAG_ATTENDANCE_TYPE = "in"; //"In Time"
    private static final int REQ_IMAGE_CAPTURE = 1511;
    private ImageView attendancePageTakePicturePlusIconImageViewID;
    private ImageView attendancePagePhotoPreviewAreaImageViewID;
    private ImageView attendancePageSubmitImageViewID;
    private Button btnSubmitAttendance;

    private SharedPreferences prefs;
    private SharedPreferences.Editor prefsEditor;
    private ProgressDialog progressDialog;

    //private static Uri capturedImageUri = null;
    //private static String realpath = null;
    private Uri capturedImageUri = null;
    private String realpath = null;
    private int CAMERA_REQUEST  = 1111;
    private Bitmap bitmap = null;

    private String encodedImageIntoString = null;
    private RestFullClient client;
    private String TAG_STATUS = "status";
    private String TAG_ERROR = "error";
    private String TAG_MESSAGE = "message";
    private String TAG_LEAVESTATUS = "leave_status";

    private String STATUS = "",STATUS1 = "",STATUS2 = "";
    private String ERROR = "";
    private String MESSAGE = "";
    private String LEAVESTATUS = "false";

    private String remarks = "";
    private EditText attendancePageReasonValueEditTextID;
    private String Locationname="",LocationCity="",LocationAddress="";





    private int STATUS_CODE = 0;
    private String username = "";



    private String baseURL;
    private String SOAPRequestXML;
    private HttpResponse httpResponse = null;
    private String TAG_MESSAGE_ID="MessageID";
    private String TAG_MESSAGE_VALUE="",TAG_MESSAGE_VALUE2="";
    private String TAG_DESCRIPTION_ID="Description";
    private String TAG_DESCRIPTION_VALUE="";
    private String TAG_ChangepasswordResult = "CheckFirstPunchedRecordResult";
    private ImageView ivResumeimg,etResume;
    private int CAMERA = 2;


    public AttendanceIntimeFragmet() {
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
        View view = inflater.inflate(R.layout.attendance_intime_fragment, container, false);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        attendancePageTakePicturePlusIconImageViewID = (ImageView)view.findViewById(R.id.attendancePageTakePicturePlusIconImageViewID);
        attendancePagePhotoPreviewAreaImageViewID = (ImageView)view.findViewById(R.id.attendancePagePhotoPreviewAreaImageViewID);
        attendancePageSubmitImageViewID = (ImageView)view.findViewById(R.id.attendancePageSubmitImageViewID);
        /*ivResumeimg=(ImageView)view.findViewById(R.id.ivResumeimg);
        etResume=(ImageView)view.findViewById(R.id.etResume);*/
        btnSubmitAttendance = view.findViewById(R.id.btnSubmitAttendance);

        attendancePageReasonValueEditTextID = (EditText)view.findViewById(R.id.attendancePageReasonValueEditTextID);

        return view;
    }

    private void initAllViews(){
        //shared preference
        prefs = getActivity().getSharedPreferences(CommonUtils.PREFERENCE_NAME,getActivity().MODE_PRIVATE);
        prefsEditor = prefs.edit();


        //Progress Dialog
        progressDialog = new ProgressDialog(getActivity());
       /* ivResumeimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // takePhotoFromCamera();
            }
        });*/

    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

       // CheckAttendance();
        //Take Picture Plus Sign Button Click
        attendancePageTakePicturePlusIconImageViewID.setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View v) {
                callCamera2();
            }
        });

        //Submit Button Click
        btnSubmitAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {




                try {
                    if(CommonUtils.locationServicesEnabled(getActivity())){
                        try {
                            if(CommonUtils.isInternelAvailable(getActivity())){
                                validateData();
                            }
                            else{
                                Toast.makeText(getActivity(), "No internet connection!", Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }//if
                    else{
                        Toast.makeText(getActivity(), "'GPS' is OFF! Please TURN ON Your Mobile's 'GPS' (Location Settings)", Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });

       /* btnSubmitAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (LEAVESTATUS.equalsIgnoreCase("true")) {
                    showOptionDisableDialog();
                }
                if (LEAVESTATUS.equalsIgnoreCase("false")) {
                        *//*Intent i = new Intent(DashBoardActivity.this, GiveAttendanceTabbedActivity.class);
                        startActivity(i);
                        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);*//*

                    if (CommonUtils.locationServicesEnabled(getActivity())) {
                        MESSAGE = "";
                        giveAttendance();
                    } else {
                        //Toast.makeText(GiveAttendanceTabbedActivity.this, "Please TURN ON Your Mobile's 'GPS' (Location Settings)", Toast.LENGTH_LONG).show();
                    final AlertDialog.Builder aldb = new AlertDialog.Builder(getActivity()~~);
                        aldb.setTitle("Location Error!");
                        aldb.setMessage("Please TURN ON Your Mobile's 'GPS' (Location Settings)");
                        aldb.setCancelable(false);
                        aldb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        });
                    }//else

                }//if
            }
        });*/

    }//onActivityCreated(Bundle savedInstanceState)

    private void showOptionDisableDialog() {
        final AlertDialog.Builder aldb = new AlertDialog.Builder(getActivity());
        aldb.setMessage("This option is temporarily blocked because you are on 'Leave' today!");
        aldb.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
            }
        });
        aldb.show();
    }

    //Validate Data locally(Checks whether the image is taken or not)
    private void validateData() {

      /*  if(realpath!=null){
            encodedImageIntoString = CommonUtils.imageToString(realpath);
            if(encodedImageIntoString!=null){
                realpath = null;
                remarks = attendancePageReasonValueEditTextID.getText().toString();
                System.out.println("remarks in INTIME: "+remarks);
                sendDataForIntime();
            }

        }
        else{
            realpath = null;
            encodedImageIntoString="";
            remarks = attendancePageReasonValueEditTextID.getText().toString();
            System.out.println("remarks in INTIME: "+remarks);
            sendDataForIntime();

           // Toast.makeText(getActivity(), "Image is not valid, please try again!", Toast.LENGTH_LONG).show();
        }*/

       /* if(realpath!=null){
            encodedImageIntoString = CommonUtils.imageToString(realpath);
        }
        if(encodedImageIntoString!=null){
            realpath = null;
            remarks = attendancePageReasonValueEditTextID.getText().toString();
            System.out.println("remarks in INTIME: "+remarks);
            sendDataForIntime();
        }
        else{
            Toast.makeText(getActivity(), "Image is not valid, please try again!", Toast.LENGTH_LONG).show();
        }*/

        new GeocodeAsyncTask().execute();
       /* sendDataForIntime();*/

    }//validateData

    //For InTime
    private void sendDataForIntime(){

        Constants.ASSOCIATE_ID = prefs.getString("USERISDCODE","");
        Constants.TL_ID = prefs.getString("TLID", "");
        Constants.ATTENDANCE_TYPE = TAG_ATTENDANCE_TYPE;
        Constants.ATTENDANCE_DATE = "0000-00-00";
        Constants.CURRENT_LAT = Constants.UNIV_LAT;
        Constants.CURRENT_LONG = Constants.UNIV_LONG;
        Constants.REASON = "";
        Constants.REMARKS = remarks;
        Constants.LEAVE_TYPE = "";
       // Constants.ATTENDANCE_IMAGE = encodedImageIntoString;
        Constants.od_from_time="";
        Constants.od_to_time="";
        Constants.client_name="";
        Constants.client_address="";
        Constants.CURRENT_LOC=Locationname;
        Constants.CURRENT_LOCCITY=LocationCity;
        Constants.CURRENT_LOCADD=LocationAddress;

        //Added Later
        encodedImageIntoString = null;
        attendancePagePhotoPreviewAreaImageViewID.setImageBitmap(null);
        System.out.println("Constants.ATTENDANCE_IMAGE:Intime "+Constants.ATTENDANCE_IMAGE);
        ////////////

        //progressDialog.setTitle("Attendance");
        progressDialog.setMessage("Submitting Your Intime... Please wait!");
        progressDialog.setCancelable(false);
        progressDialog.show();
        client = new RestFullClient(Constants.BASE_URL+Constants.ATTENDANCE_RELATIVE_URI);

        //client.AddParam("associate_code", Constants.ASSOCIATE_ID);
        client.AddParam("associate_code", Constants.ASSOCIATE_ID);
        System.out.println("associate_code: "+Constants.ASSOCIATE_ID);

      /*  client.AddParam("attendance_type", Constants.ATTENDANCE_TYPE);
        System.out.println("attendance_type: "+Constants.ATTENDANCE_TYPE);

        client.AddParam("attendance_date", Constants.ATTENDANCE_DATE);
        System.out.println("attendance_date: "+Constants.ATTENDANCE_DATE);*/

        client.AddParam("latitude", Constants.CURRENT_LAT);
        System.out.println("latitude: "+Constants.CURRENT_LAT);

        client.AddParam("longitude", Constants.CURRENT_LONG);
        System.out.println("longitude: "+Constants.CURRENT_LONG);
        client.AddParam("location_name", Constants.CURRENT_LOC);
        client.AddParam("location_city", Constants.CURRENT_LOCCITY);
        client.AddParam("location_address", Constants.CURRENT_LOCADD);
        client.AddParam("punched_type", "I");

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

    private void giveAttendance() {
        Constants.ASSOCIATE_ID = prefs.getString("USERID", "");
        Constants.TL_ID = prefs.getString("TLID", "");
        Constants.OUTLET_ID = prefs.getString("USEROUTLETID", "");
        Constants.ATTENDANCE_TYPE = TAG_ATTENDANCE_TYPE;
        Constants.ATTENDANCE_IMAGE = "";
        //Constants.ATTENDANCE_DATE = "";
        //Constants.ATTENDANCE_DATE = "yyyy-mm-dd";
        Constants.CURRENT_LAT = Constants.UNIV_LAT;
        Constants.CURRENT_LONG = Constants.UNIV_LONG;
        Constants.od_from_time="";
        Constants.od_to_time="";
        Constants.client_name="";
        Constants.client_address="";

        if(Constants.UNIV_LAT.equals("0.0")){
            Constants.CURRENT_LAT = Constants.UNIV_LAT1;
        }
        if(Constants.UNIV_LONG.equals("0.0")){
            Constants.CURRENT_LONG = Constants.UNIV_LONG1;
        }
        Constants.ATTENDANCE_DATE = "";
        Constants.REASON = "";
        Constants.LEAVE_TYPE = "";

        //progressDialog.setTitle("Attendance");
        progressDialog.setMessage("Submitting Your Break... Please wait!");
        progressDialog.setCancelable(false);
        progressDialog.show();
        client = new RestFullClient(Constants.BASE_URL + Constants.ATTENDANCE_RELATIVE_URI);
        client.AddParam("associate_id", Constants.ASSOCIATE_ID);
        client.AddParam("tl_id", Constants.TL_ID);
       // client.AddParam("outlet_id", Constants.OUTLET_ID);
        client.AddParam("attendance_type", Constants.ATTENDANCE_TYPE);
        client.AddParam("attendance_image", Constants.ATTENDANCE_IMAGE);
       // client.AddParam("attendance_time", "");
       // client.AddParam("attendance_date_sub", "");
       /* client.AddParam("latitude", "17.24026200"*//*Constants.CURRENT_LAT*//*);
        client.AddParam("longitude", "78.42938100"*//*Constants.CURRENT_LONG*//*);*/
        client.AddParam("latitude", Constants.CURRENT_LAT/*"17.24026200"*/);
        client.AddParam("longitude", Constants.CURRENT_LONG/*"78.42938100"*/);
//        client.AddParam("distance", Constants.DISTANCE);
//        System.out.println("distance: "+Constants.DISTANCE);
        client.AddParam("attendance_date", Constants.ATTENDANCE_DATE);
        client.AddParam("attendance_to_date", Constants.ATTENDANCE_DATE);
        client.AddParam("reason", Constants.REASON);
        client.AddParam("remarks", Constants.REMARKS);
        client.AddParam("leave_type", Constants.LEAVE_TYPE);

        client.AddParam("od_from_time", Constants.od_from_time);
        System.out.println("od_from_time: "+Constants.od_from_time);

        client.AddParam("od_to_time", Constants.od_to_time);
        System.out.println("od_to_time: "+Constants.od_to_time);

        client.AddParam("client_name", Constants.client_name);
        System.out.println("client_name: "+Constants.client_name);

        client.AddParam("client_address", Constants.client_address);
        System.out.println("client_address: "+Constants.client_address);

        new Thread(new Runnable() {

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

    private void receiveDataForServerResponse(JSONObject jobj) {

        try {

            if (client.responseCode == 200) {

                STATUS = jobj.getString(TAG_STATUS);
                MESSAGE = jobj.getString(TAG_MESSAGE);

                System.out.println("STATUS: responseCode==200: " + STATUS);
                System.out.println("MESSAGE: responseCode==200: " + MESSAGE);

                if (STATUS.equalsIgnoreCase("true")) {
                    LEAVESTATUS = jobj.getString(TAG_LEAVESTATUS);
                    System.out.println("LEAVESTATUS: responseCode==200: & STATUS==true: " + LEAVESTATUS);
                }//if(STATUS.equalsIgnoreCase("true"))

            }//if(client.responseCode==200)

            if (client.responseCode != 200) {

                STATUS = jobj.getString(TAG_STATUS);
                MESSAGE = jobj.getString(TAG_MESSAGE);

                System.out.println("STATUS: responseCode!=200: " + STATUS);
                System.out.println("MESSAGE: responseCode!=200: " + MESSAGE);

            }//if(client.responseCode!=200)
        } catch (Exception e) {
            e.printStackTrace();
        }

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

            attendancePagePhotoPreviewAreaImageViewID.setImageDrawable(null);
            attendancePageReasonValueEditTextID.setText("");
            //Success
            if(client.responseCode==200){

                //Success
                if(STATUS.equalsIgnoreCase("true")){
                    Constants.ASSOCIATE_ID = "";
                    Constants.ATTENDANCE_TYPE = "";
                    Constants.ATTENDANCE_IMAGE = "";
                   // Constants.CURRENT_LAT = "0.0";
                    //Constants.CURRENT_LONG = "0.0";
                    Constants.ATTENDANCE_DATE = "0000-00-00";
                    Constants.REASON = "";
                    Constants.LEAVE_TYPE = "";
                    Constants.REMARKS = "";


                    //Alert Dialog Builder
                    final AlertDialog.Builder aldb = new AlertDialog.Builder(getActivity());
                    aldb.setTitle("Success!");
                    aldb.setMessage(MESSAGE);
                    aldb.setCancelable(false);
                    aldb.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                           // CheckAttendance();
                            getActivity().finish();
                            getActivity().overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
                        }
                    });
                    aldb.show();

                   /* final Dialog dialog = new Dialog(getContext());
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    // dialog.setTitle("Details");
                    dialog.setCancelable(false);

                    dialog.setContentView(R.layout.details_list_layout_d);
                    dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    final Button close=dialog.findViewById(R.id.close);
                    final TextView txt=dialog.findViewById(R.id.textmsg);
                    final TextView title=dialog.findViewById(R.id.txttitle);
                    title.setText("Success!");
                    txt.setText(MESSAGE);
                    close.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    dialog.show();*/

                }

                //Failed
                if(STATUS.equalsIgnoreCase("false")){
                    //showRetryDialog();
                    showFailureDialog();
                }
            }
            //Failed
            if(client.responseCode!=200){
                //Toast.makeText(getActivity(), MESSAGE, Toast.LENGTH_LONG).show();
                //showRetryDialog();
                showFailureDialog();
            }

        }//handleMessage(Message msg)

    };

    //Show failure Dialog
    private void showFailureDialog(){
        //Alert Dialog Builder
        final AlertDialog.Builder aldb = new AlertDialog.Builder(getActivity());
        aldb.setTitle("Attendance Failed!");
        aldb.setMessage("\nReason: "+MESSAGE);
        aldb.setNegativeButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                //Added later
                Constants.ASSOCIATE_ID = "";
                Constants.ATTENDANCE_TYPE = "";
                Constants.ATTENDANCE_IMAGE = "";
               // Constants.CURRENT_LAT = "0.0";
                //Constants.CURRENT_LONG = "0.0";
                Constants.ATTENDANCE_DATE = "0000-00-00";
                Constants.REASON = "";
                Constants.LEAVE_TYPE = "";
                Constants.REMARKS = "";
                /////////////////

                //getActivity().finish();
                //getActivity().overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);

            }
        });
        aldb.show();

       /* final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // dialog.setTitle("Details");
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.details_list_layout_d);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final Button close=dialog.findViewById(R.id.close);
        final TextView txt=dialog.findViewById(R.id.textmsg);
        final TextView title=dialog.findViewById(R.id.txttitle);
        title.setText("Failed!");
        txt.setText(MESSAGE);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Constants.ASSOCIATE_ID = "";
                Constants.ATTENDANCE_TYPE = "";
                Constants.ATTENDANCE_IMAGE = "";
                // Constants.CURRENT_LAT = "0.0";
                //Constants.CURRENT_LONG = "0.0";
                Constants.ATTENDANCE_DATE = "0000-00-00";
                Constants.REASON = "";
                Constants.LEAVE_TYPE = "";
                Constants.REMARKS = "";
                dialog.dismiss();
            }
        });

        dialog.show();*/

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode == getActivity().RESULT_OK){

          /*  if (requestCode == CAMERA_REQUEST) {
                try {

                   // if(data!=null){

                        if(capturedImageUri!=null){

                            Log.v("capturedImageUri:",capturedImageUri.toString());
                            System.out.println("data!=null: "+data);
                            //bitmap.recycle(); ////Added Later
                            bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), capturedImageUri);
                            //BitmapFactory.Options options = new BitmapFactory.Options();
                            //options.inSampleSize = 3;

                            //For MarshMallow(6.0) API 23//
                            if(bitmap==null){
                                //Log.v("realpath: ",realpath.toString());
                                File f = new File(realpath);
                                BitmapFactory.Options options = new BitmapFactory.Options();
                                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                                try {
                                    //bitmap.recycle(); ////Added Later
                                    bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, options);
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                            }
                            //For MarshMallow(6.0) API 23//

                            if(bitmap!=null){
                                attendancePagePhotoPreviewAreaImageViewID.setImageBitmap(bitmap);
                                System.out.println("Bitmap Image: "+bitmap.toString());
                            }

                            //imageSelectionType = "CAMERA";

                        }//if(capturedImageUri!=null)

                   // }//if(data!=null)

                }catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }*/
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            attendancePagePhotoPreviewAreaImageViewID.setImageBitmap(imageBitmap);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream .toByteArray();
            encodedImageIntoString = Base64.encodeToString(byteArray, Base64.DEFAULT);

        }
        if(resultCode == getActivity().RESULT_CANCELED){

            if (requestCode == CAMERA_REQUEST) {
                capturedImageUri = null;
                realpath = null;
                System.out.println("capturedImageUri: "+capturedImageUri);
                System.out.println("realpath: "+realpath);
            }

        }

    }

    private void callCamera1(){

        //Camera 1 Logic
        Calendar cal = Calendar.getInstance();
        File file = null;
        try{
            file = new File(Environment.getExternalStorageDirectory()+"/Self_Pictures", (cal.getTimeInMillis() + ".jpg"));
        }catch (Exception e) {
            System.out.println("e.printStackTrace():file: "+e.toString());
        }

        if(!file.exists()){
            try{
                file.createNewFile();
            }catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                System.out.println("e.printStackTrace(): "+e.toString());
            }
        }else{
            file.delete();
            try{
                file.createNewFile();
            }catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                System.out.println("e.printStackTrace(): "+e.toString());
            }
        }
        capturedImageUri = Uri.fromFile(file);
        System.out.println("capturedImageUri: "+capturedImageUri);
        realpath = file.getAbsolutePath().toString();
        System.out.println("realpath: "+realpath);
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        i.putExtra(MediaStore.EXTRA_OUTPUT, capturedImageUri);
        i.putExtra("android.intent.extras.CAMERA_FACING", 1);
        startActivityForResult(i, CAMERA_REQUEST);
        //End Of Camera 1 Logic
    }

    private void callCamera2(){
      /*  Intent intent = new Intent(getActivity(), ImageCapture.class);
        intent.putExtra("ATTENDANCE_TYPE","in");
        getActivity().startActivity(intent);*/
        //getActivity().startActivityForResult(intent,CAMERA_REQUEST1);
        int checkPermission = ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.CAMERA);
        if (checkPermission == PackageManager.PERMISSION_GRANTED) {
            Log.d("PERMISSION", "Permission Granted");
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            takePictureIntent.putExtra("android.intent.extras.CAMERA_FACING", android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT);
            takePictureIntent.putExtra("android.intent.extras.LENS_FACING_FRONT", 1);
            takePictureIntent.putExtra("android.intent.extra.USE_FRONT_CAMERA", true);

           /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                takePictureIntent.putExtra("android.intent.extras.LENS_FACING_FRONT", 1);
            } else {
                takePictureIntent.putExtra("android.intent.extras.CAMERA_FACING", 1);
            }
*/

            if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQ_IMAGE_CAPTURE);
            }
        } else {
            Log.d("PERMISSION", "Permission Denied");
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onResume(){
        super.onResume();

        if(ImageCapture.camera2_status){
            ImageCapture.camera2_status = false;

            if(ImageCapture.ATTENDANCE_TYPE.equalsIgnoreCase("in")){

                realpath = ImageCapture.CAMERA2_IMAGEPATH;
                System.out.println("realpath: "+realpath);
                if(realpath!=null){
                    attendancePagePhotoPreviewAreaImageViewID.setImageURI(Uri.parse(realpath));
                }
                ImageCapture.ATTENDANCE_TYPE = "";
            }

            ImageCapture.CAMERA2_IMAGEPATH = "";

        }//if

    }

    public void onClickPictureBack2() {

        if(ImageCapture.camera2_status){
            ImageCapture.camera2_status = false;

            if(ImageCapture.ATTENDANCE_TYPE.equalsIgnoreCase("in")){

                realpath = ImageCapture.CAMERA2_IMAGEPATH;
                System.out.println("realpath: "+realpath);
                if(realpath!=null){
                    attendancePagePhotoPreviewAreaImageViewID.setImageURI(Uri.parse(realpath));
                }
                ImageCapture.ATTENDANCE_TYPE = "";
            }

            ImageCapture.CAMERA2_IMAGEPATH = "";

        }//if
    }

    //For Checking attendance
    private void CheckAttendance(){

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MMM/yyyy");
       String cur_date = dateFormat.format(calendar.getTime());


        Constants.CURRENT_LAT = Constants.UNIV_LAT;
        Constants.CURRENT_LONG = Constants.UNIV_LONG;
        Constants.REASON = "";
        Constants.REMARKS = remarks;
        Constants.LEAVE_TYPE = "";
        // Constants.ATTENDANCE_IMAGE = encodedImageIntoString;
        Constants.od_from_time="";
        Constants.od_to_time="";
        Constants.client_name="";
        Constants.client_address="";
        Constants.CURRENT_LOC=Locationname;
        Constants.CURRENT_LOCCITY=LocationCity;
        Constants.CURRENT_LOCADD=LocationAddress;

        progressDialog.setMessage("Checking attendance... Please wait!");
        progressDialog.setCancelable(false);
        progressDialog.show();



        baseURL = Constants.base_url_default1;
    /*    SOAPRequestXML = Constants.soapRequestHeader+
                "<soapenv:Header/>"
                +"<soapenv:Body>"
                +"<tem:CheckFirstPunchedRecord >"
                +"<tem:emp_code>"+ prefs.getString("USERISDCODE","")+"</tem:emp_code>"
                //+"<tem:password>"+CommonUtils.md5(passwd)+"</tem:password>"
                +"<tem:attendance_date>"+cur_date+"</tem:attendance_date>"


                +"</tem:CheckFirstPunchedRecord >"
                +"</soapenv:Body>"
                +"</soapenv:Envelope>";*/

        SOAPRequestXML = Constants.soapRequestHeader+
                "<soapenv:Header/>"
                +"<soapenv:Body>"
                +"<tem:AttendanceMark_v2>"
                +"<tem:emp_code>"+ prefs.getString("USERISDCODE","")+"</tem:emp_code>"
                +"<tem:attendance_mark_date>"+""+"</tem:attendance_mark_date>"
                +"<tem:attendance_mark_time>"+""+"</tem:attendance_mark_time>"
                +"<tem:attendance_mark_as>"+"P"+"</tem:attendance_mark_as>"
                +"<tem:location_name>"+Constants.CURRENT_LOC+"</tem:location_name>"
                +"<tem:location_city>"+Constants.CURRENT_LOCCITY+"</tem:location_city>"
                +"<tem:location_address>"+Constants.CURRENT_LOCADD+"</tem:location_address>"
                +"<tem:latitude>"+Constants.CURRENT_LAT+"</tem:latitude>"
                +"<tem:longitude>"+Constants.CURRENT_LONG+"</tem:longitude>"
                +"<tem:punched_type>"+"I"+"</tem:punched_type>"
                +"<tem:selfie_image>"+encodedImageIntoString+"</tem:selfie_image>"
                +"</tem:AttendanceMark_v2>"
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

                handler5.sendEmptyMessage(0);

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

                        tagname=xpp.getName();
                        if(tagname.equalsIgnoreCase("AttendanceMark_v2Result")) {

           /*to handle nested tags.
           "xpp.nextTag()" goes to the next starting tag  immediately following "ItemArray",
           and "itemID = xpp.nextText()" assigns the text within that tag
            to the string itemID*/

                          //  xpp.nextTag();
                           // STATUS1 = xpp.nextText();
                            //Log.d("Listing ", STATUS1);


                           /* xpp.nextTag();
                            TAG_MESSAGE_VALUE2 = xpp.nextText();
                            Log.d("Listing ", TAG_MESSAGE_VALUE2);*/



                           /* xpp.nextTag();
                            listingType = xpp.nextText();
                            Log.d("Listing ", listingType);*/

                        }
                        break;

                    case XmlPullParser.TEXT:
                        text = xpp.getText().trim().toString();
                        STATUS1=text;
                        System.out.println("Text data: "+text);
                        break;

                    case XmlPullParser.END_TAG:

                      /*  if(tagname.equalsIgnoreCase(TAG_ChangepasswordResult)){
                            STATUS1 = text;
                            text = "";
                            System.out.println("STATUS: "+STATUS1);
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
                        }*/

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


    Handler handler5 = new Handler(){

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
                if(STATUS1.equalsIgnoreCase("0")){
                    //Toast.makeText(LoginActivity.this, MESSAGE, Toast.LENGTH_SHORT).show();
                  //  MESSAGE=                // 0: Not Punched | 1: First Punched (IN Time) | 2: Second Punched (OUT Time);
                   // showSuccessDialog();
                    showDialog("Something went wrong! Please try again");
                }

                else if(STATUS1.equalsIgnoreCase("1")){
                    //Toast.makeText(LoginActivity.this, MESSAGE, Toast.LENGTH_SHORT).show();
                    //  MESSAGE=                // 0: Not Punched | 1: First Punched (IN Time) | 2: Second Punched (OUT Time);
                   // showSuccessDialog();
                    showSuccessDialog("Attendance submitted successfully");
                }
                else if(STATUS1.equalsIgnoreCase("9")){
                    //Toast.makeText(LoginActivity.this, MESSAGE, Toast.LENGTH_SHORT).show();
                    //  MESSAGE=                // 0: Not Punched | 1: First Punched (IN Time) | 2: Second Punched (OUT Time);
                   // showSuccessDialog();
                    showDialog("After 12 you are not allowed to give attendance");
                }
                //Login failed
                /*if(STATUS.equalsIgnoreCase("0")){
                    showFailureDialog();
                }*/



            }

            //Login failed
            if(STATUS_CODE!=200){
                //Toast.makeText(LoginActivity.this, MESSAGE, Toast.LENGTH_SHORT).show();
              //  MESSAGE="Please provide correct information";
                showFailureDialog1("Something went wrong. Please try again later");

            }

        }//handleMessage(Message msg)

    };


    //////////////////////////////////////for checking holiday\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

    //For Checking attendance
    private void CheckAttendance1(){



        progressDialog.setMessage("Checking attendance... Please wait!");
        progressDialog.setCancelable(false);
        progressDialog.show();



        baseURL = Constants.base_url_default;
        SOAPRequestXML = Constants.soapRequestHeader+
                "<soapenv:Header/>"
                +"<soapenv:Body>"
                +"<tem:IsDisabled>"
                +"<tem:emp_code>"+ prefs.getString("USERISDCODE","")+"</tem:emp_code>"
                +"</tem:IsDisabled>"
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

                    getParsingElementsForLoginDetails1(xpp);

                } catch (HttpResponseException e) {
                    Log.i("httpResponse Error = ", e.getMessage());
                }
                catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                handler7.sendEmptyMessage(0);

            }

        }).start();

    }
    //getParsingElementsForLoginDetails(xpp);
    public void getParsingElementsForLoginDetails1(XmlPullParser xpp){
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

                       if(tagname.equalsIgnoreCase("IsDisabledResult")){
                            STATUS2 = text;
                            text = "";
                            System.out.println("STATUS: "+STATUS2);
                        }
                      /*   if(tagname.equalsIgnoreCase(TAG_MESSAGE_ID)){
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
                        }*/

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


    Handler handler7 = new Handler(){

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
                if(STATUS2.equalsIgnoreCase("0")){
                    //Toast.makeText(LoginActivity.this, MESSAGE, Toast.LENGTH_SHORT).show();
                    //  MESSAGE=                // 0: Not Punched | 1: First Punched (IN Time) | 2: Second Punched (OUT Time);
                     showDialog("You are not allowed to punch attendance today");
                    //showDialog(TAG_MESSAGE_VALUE2);

                }

                else if(STATUS2.equalsIgnoreCase("1")){
                    //Toast.makeText(LoginActivity.this, MESSAGE, Toast.LENGTH_SHORT).show();
                    //  MESSAGE=                // 0: Not Punched | 1: First Punched (IN Time) | 2: Second Punched (OUT Time);
                    // showSuccessDialog();
                    if(CommonUtils.locationServicesEnabled(getActivity())){
                        try {
                            if(CommonUtils.isInternelAvailable(getActivity())){
                                validateData();
                            }
                            else{
                                Toast.makeText(getActivity(), "No internet connection!", Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }//if
                    else{
                        Toast.makeText(getActivity(), "'GPS' is OFF! Please TURN ON Your Mobile's 'GPS' (Location Settings)", Toast.LENGTH_LONG).show();
                    }

                }


            }

            //Login failed
            if(STATUS_CODE!=200){
                //Toast.makeText(LoginActivity.this, MESSAGE, Toast.LENGTH_SHORT).show();
                //  MESSAGE="Please provide correct information";
                showFailureDialog1("Something went wrong. Please try again later");

            }

        }//handleMessage(Message msg)

    };



    /////////////////////////////////////////for checking holiday\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\


    //Show Success Dialog
    private void showDialog(String msg){
        //Alert Dialog Builder
        final AlertDialog.Builder aldb = new AlertDialog.Builder(getActivity());
        aldb.setTitle("Alert!");
        aldb.setMessage(msg);
        aldb.setCancelable(false);
        aldb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        aldb.show();
    }

    //Show Success Dialog
    private void showSuccessDialog(String msg){
        //Alert Dialog Builder
        final AlertDialog.Builder aldb = new AlertDialog.Builder(getActivity());
        aldb.setTitle("Success!");
        aldb.setMessage(msg);
        aldb.setCancelable(false);
        aldb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getActivity().finish();
            }
        });
        aldb.show();
    }
    //Show failure Dialog
    private void showFailureDialog1(String msg){
        //Alert Dialog Builder
        final AlertDialog.Builder aldb = new AlertDialog.Builder(getActivity());
        aldb.setTitle("Failed!");
        aldb.setMessage("\nReason: "+msg);
        aldb.setPositiveButton("OK", null);
        aldb.show();
    }

    class GeocodeAsyncTask extends AsyncTask<Void, Void, Address> {

        String errorMessage = "";

        @Override
        protected void onPreExecute() {
          //  infoText.setVisibility(View.INVISIBLE);
            //progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Address doInBackground(Void ... none) {
            Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
            List<Address> addresses = null;

           /* if(fetchType == USE_ADDRESS_NAME) {
                String name = addressEdit.getText().toString();
                try {
                    addresses = geocoder.getFromLocationName(name, 1);
                } catch (IOException e) {
                    errorMessage = "Service not available";
                    Log.e(TAG, errorMessage, e);
                }
            }
            else if(fetchType == USE_ADDRESS_LOCATION) {*/
            Constants.CURRENT_LAT = Constants.UNIV_LAT;
            Constants.CURRENT_LONG = Constants.UNIV_LONG;
                double latitude = Double.parseDouble(Constants.CURRENT_LAT);
                double longitude = Double.parseDouble(Constants.CURRENT_LONG);

                try {
                    addresses = geocoder.getFromLocation(latitude, longitude, 1);
                } catch (IOException ioException) {
                    errorMessage = "Service Not Available";
                    Log.e("TAG", errorMessage, ioException);
                } catch (IllegalArgumentException illegalArgumentException) {
                    errorMessage = "Invalid Latitude or Longitude Used";
                    Log.e("TAG", errorMessage + ". " +
                            "Latitude = " + latitude + ", Longitude = " +
                            longitude, illegalArgumentException);
                }
           /* }
            else {
                errorMessage = "Unknown Type";
                Log.e(TAG, errorMessage);
            }*/

            if(addresses != null && addresses.size() > 0)
                return addresses.get(0);

            return null;
        }

        protected void onPostExecute(Address address) {
            if(address == null) {
               /* progressBar.setVisibility(View.INVISIBLE);
                infoText.setVisibility(View.VISIBLE);
                infoText.setText(errorMessage);*/
            }
            else {
               int p= address.getMaxAddressLineIndex()+1;
                String addressName = "";
                for(int i = 0; i < p; i++) {
                    addressName =   address.getAddressLine(i);
                    Locationname=address.getSubAdminArea();LocationCity=address.getLocality();LocationAddress=address.getAddressLine(i);
                //  String  addressName1 = " --- " + address.getAddressLine(0).toString();
                }
                /*progressBar.setVisibility(View.INVISIBLE);
                infoText.setVisibility(View.VISIBLE);
                infoText.setText("Latitude: " + address.getLatitude() + "\n" +
                        "Longitude: " + address.getLongitude() + "\n" +
                        "Address: " + addressName);*/

               // Locationname=addressName; LocationCity=addressName; LocationAddress=addressName;
            }
           /* if(STATUS1.equalsIgnoreCase("0"))
            sendDataForIntime();
            else if(STATUS1.equalsIgnoreCase("1"))
                showFailureDialog1("Already Applied for In Time");
            else if(STATUS1.equalsIgnoreCase("2"))
                showFailureDialog1("Already Applied Attendance for today");*/
// sendDataForIntime();
           /* if(STATUS1.equalsIgnoreCase("1"))
                sendDataForIntime();
            else
                showFailureDialog1(TAG_MESSAGE_VALUE2);*/

           // sendDataForIntime();
            CheckAttendance();


        }
    }

}
