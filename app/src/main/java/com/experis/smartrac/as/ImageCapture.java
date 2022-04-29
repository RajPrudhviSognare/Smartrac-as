package com.experis.smartrac.as;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.experis.smartrac.as.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class ImageCapture extends AppCompatActivity implements Callback{

	private SurfaceHolder surfaceHolder;
    private SurfaceView surfaceView;
    //private ZoomControls imageCapture_zoomControlsID;
    private FloatingActionButton fab_Camera;
    private ImageView imageCapturetopbarBackImageID;

    private PowerManager.WakeLock mWakeLock;
    private Parameters params;
    private Camera mCamera = null;
    File imagePath;
    MediaPlayer mp;
    
    private boolean hasFlash = false;
    private File finalImageFilePath = null;

	public static boolean camera2_status = false;
	public static String CAMERA2_IMAGEPATH = "";
	public static String ATTENDANCE_TYPE = "";
	
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState){		
		   super.onCreate(savedInstanceState);
		   getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		   setContentView(R.layout.imagecapture);

		   ATTENDANCE_TYPE = getIntent().getStringExtra("ATTENDANCE_TYPE");
		   System.out.println("ATTENDANCE_TYPE: "+ATTENDANCE_TYPE);

		   int cameraCount = Camera.getNumberOfCameras();
		   if(cameraCount<2){
			   showNoFrontCameraDialog();
		   }
		   else{
			   mCamera = getCameraInstance();
		   }
	       if(mCamera==null){
	        	Toast.makeText(ImageCapture.this,"No Front Camera Found",Toast.LENGTH_LONG).show();
	       }

		   imageCapturetopbarBackImageID = (ImageView)findViewById(R.id.imageCapturetopbarBackImageID);
		   fab_Camera = (FloatingActionButton)findViewById(R.id.fab_Camera);
	       
	       surfaceView = (SurfaceView)findViewById(R.id.imageCapture_surfaceview);
	       surfaceHolder = surfaceView.getHolder();
	       surfaceHolder.addCallback(this);
	       surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	       
	       hasFlash = getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
	       
	       //Image Capture Button Listener 
		   fab_Camera.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
					        
		    	/*
		    	params = mCamera.getParameters();
		    	//Added Later
		    	params.setJpegQuality(CameraProfile.getJpegEncodingQualityParameter(0, CameraProfile.QUALITY_HIGH));
		    	params.setAntibanding("auto");

		    	List<Size> sizes = params.getSupportedPictureSizes();
		    	System.out.println("Total Camera Picture Sizes are: "+sizes.size());
		    	System.out.println("Camera Picture Sizes are: "+sizes.toString());
		        Size size = sizes.get(0);

		        for (int i = 0; i <= sizes.size()/3; i++) {
		            if (sizes.get(i).width > size.width)
		                size = sizes.get(i);
		            
		            System.out.println("Picture Size: "+size.width+","+size.height);
		        }

		        System.out.println("Finally Setting Picture Size: "+size.width+","+size.height);
				params.setPreviewSize(size.width, size.height); ////Added Later
		        params.setPictureSize(size.width, size.height);
		        //Finish of Added Later
		        */

		    	if(mCamera!=null){
		    		/*if(hasFlash){
		    			params.setFlashMode(Parameters.FLASH_MODE_TORCH);
	    				//mCamera.setParameters(params);	
		    		}*/
		    		 
		    		////mCamera.setParameters(params);

    				playSound();
    				// get an image from the camera
					try{
						mCamera.takePicture(null, null, null, mPicture);
					}
					catch(Exception e){
						e.printStackTrace();
						Toast.makeText(ImageCapture.this,e.getMessage(),Toast.LENGTH_LONG).show();
					}

		    	}//if(mCamera!=null)
		    					
			}
	    	   
	       });
	       	       
	       //Exit Button Listener
		   imageCapturetopbarBackImageID.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				ImageCapture.this.finish();
			 }
	    	   
	       });

		//Added Later//////////////////
		if(Build.VERSION.SDK_INT>Build.VERSION_CODES.LOLLIPOP){
			System.out.println("Inside ImageCapture Page checkAllPermissions() is called Above Lallipop: ");
			try{
				checkAllPermissions();
			}
			catch (Exception e){
				e.printStackTrace();
			}
		}
		//End Of Added Later//////////
	       
	       
	}//onCreate()

	private void checkAllPermissions(){
		// Here, thisActivity is the current activity
		if (ContextCompat.checkSelfPermission(ImageCapture.this,
				android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

			// Should we show an explanation?
			if (ActivityCompat.shouldShowRequestPermissionRationale(ImageCapture.this,
					android.Manifest.permission.CAMERA)) {

				// Show an expanation to the user *asynchronously* -- don't block
				// this thread waiting for the user's response! After the user
				// sees the explanation, try again to request the permission.

			} else {

				// No explanation needed, we can request the permission.
				ActivityCompat.requestPermissions(ImageCapture.this,
						new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1991);

				// The callback method gets the result of the request.
			}

		}//if

	}//checkAllPermissions()

	@Override
	public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
		switch (requestCode) {
			case 1991: {
				// If request is cancelled, the result arrays are empty.
				if (grantResults.length > 0
						&& grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED
						&& grantResults[2] == PackageManager.PERMISSION_GRANTED) {

					// permission was granted, yay! Do the
					// tasks you need to do.
					System.out.println("CAMERA, WRITE_EXTERNAL_STORAGE & READ_EXTERNAL_STORAGE ARE GRANTED!");

				} else {

					// permission denied, boo! Disable the
					// functionality that depends on this permission.
					System.out.println("CAMERA, WRITE_EXTERNAL_STORAGE & READ_EXTERNAL_STORAGE ARE REJECTED!");
					ImageCapture.this.finish();
				}
				return;
			}

		}

	}

	@SuppressWarnings("deprecation")
	public PictureCallback mPicture = new PictureCallback(){

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			// TODO Auto-generated method stub
			
			/*if(hasFlash){
				params.setFlashMode(Parameters.FLASH_MODE_OFF);
				mCamera.setParameters(params);
			}*/

			Calendar cal = Calendar.getInstance();

			releaseCamera();

			finalImageFilePath = new File(Environment.getExternalStorageDirectory().toString()+"/Self_Pictures/Picture_"+cal.getTimeInMillis()+".jpg");
			System.out.println("finalImageFilePath: "+finalImageFilePath);

			if(finalImageFilePath == null){
				camera2_status = false;
				CAMERA2_IMAGEPATH = "";
				return;
			}
			else{
				CAMERA2_IMAGEPATH = finalImageFilePath.getAbsolutePath().toString();
				System.out.println("CAMERA2_IMAGEPATH: "+CAMERA2_IMAGEPATH);
			}

			try {
				FileOutputStream fos = new FileOutputStream(finalImageFilePath);
				fos.write(data);
				fos.close();
				Toast.makeText(getApplicationContext(), "Selfie Taken Successfully", Toast.LENGTH_SHORT).show();

				////showImagePreview(finalImageFilePath.toString());

				//showImagePreview1(finalImageFilePath.toString());

				camera2_status = true;
				ImageCapture.this.finish();
			} catch (FileNotFoundException e) {
				Log.e("ImageCapture", "File not found: " + e.getMessage());
			} catch (IOException e) {
				Log.e("ImageCapture", "Error accessing file: " + e.getMessage());
			}

		}

	};
	
	public void showImagePreview(String path){
		
		final String path1 = path;
		AlertDialog.Builder aldb = new AlertDialog.Builder(ImageCapture.this);
		aldb.setTitle("Preview");
		final ImageView imgView = new ImageView(ImageCapture.this);
		imgView.setImageURI(Uri.parse(path));
		aldb.setView(imgView);
		aldb.setCancelable(false);

		aldb.setPositiveButton("Save", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
				/*Intent intent = new Intent(ImageCapture.this,ImageCapture.class);
				overridePendingTransition(0, 0);
				Toast.makeText(getApplicationContext(), "Image is saved", Toast.LENGTH_SHORT).show();
				MediaScannerConnection.scanFile(ImageCapture.this, new String[] {finalImageFilePath.toString()}, null, null);
				finish();
				startActivity(intent);*/

				camera2_status = true;
				ImageCapture.this.finish();
				
			}
		});
		
		aldb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
				File file = new File(path1);
				if(file.exists()){
					file.delete();

					camera2_status = false;
					CAMERA2_IMAGEPATH = "";

					Intent intent = new Intent(ImageCapture.this,ImageCapture.class);
					intent.putExtra("ATTENDANCE_TYPE",ATTENDANCE_TYPE);
					overridePendingTransition(0, 0);
					//Toast.makeText(getApplicationContext(), "Picture is deleted", Toast.LENGTH_SHORT).show();
					ImageCapture.this.finish();
					startActivity(intent);
				}
				
			}
		});

		aldb.show();
		
	}


	/*public void showImagePreview1(final String path){

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		final AlertDialog dialog = builder.create();
		LayoutInflater inflater = getLayoutInflater();
		View dialogLayout = inflater.inflate(R.layout.camera2_image_preview, null);
		dialog.setView(dialogLayout);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

		dialog.show();

		dialog.setOnShowListener(new DialogInterface.OnShowListener() {
			@Override
			public void onShow(DialogInterface d) {
				ImageView image = (ImageView) dialog.findViewById(R.id.goProDialogImage);
				//Bitmap icon = BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.whygoprodialogimage);
				Bitmap icon = BitmapFactory.decodeFile(path);
				float imageWidthInPX = (float)image.getWidth();

				LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(Math.round(imageWidthInPX),
						Math.round(imageWidthInPX * (float)icon.getHeight() / (float)icon.getWidth()));
				image.setLayoutParams(layoutParams);


			}
		});

	}*/

	public Camera getCameraInstance(){
		  // TODO Auto-generated method stub
		       Camera c = null;
		       int cameraCount = 0;
		       Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
		       cameraCount = Camera.getNumberOfCameras();
		       Log.e("ImageCapture - ", "cameraCount: " + cameraCount);

		       for (int camIdx = 0; camIdx < cameraCount; camIdx++) {

				   Camera.getCameraInfo(camIdx, cameraInfo);
				   Log.e("ImageCapture - ", "camIdx: " + camIdx);
				   if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
					   try {
						     c = Camera.open(camIdx);
						    //break;
					   } catch (RuntimeException e) {
						   Log.e("ImageCapture - ", "camIdx: " + camIdx);
						   Log.e("ImageCapture - ", "Camera failed to open: " + e.getMessage());
					   }
				   }//if
				   else{
					   try {
						    //c = Camera.open(); // attempt to get a Camera instance  //0==Back; 1==Front
					   }
					   catch (Exception e){
						   // Camera is not available (in use or does not exist)
						   Log.e("Camera Failed to Open: ", e.getMessage());
					   }
				   }//else

		       }//for

		   /*//Added Later
		   try {
			   CameraManager mCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
			   String desiredCameraId = null;
			   String mCameraIDsList[] = mCameraManager.getCameraIdList();
			   for(String cameraId : mCameraIDsList) {
				CameraCharacteristics chars =  mCameraManager.getCameraCharacteristics(cameraId);
				List<CameraCharacteristics.Key<?>> keys = chars.getKeys();
				try {
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
						if(CameraCharacteristics.LENS_FACING_FRONT == chars.get(CameraCharacteristics.LENS_FACING)) {
                            // This is the one we want.
                            desiredCameraId = cameraId;
							Log.e("ImageCapture - ", "desiredCameraId: " + desiredCameraId);
                            break;
                        }
					}
				} catch(IllegalArgumentException e) {
					// This key not implemented, which is a bit of a pain. Either guess - assume the first one
					// is rear, second one is front, or give up.
				}
			}
		}
		catch (Exception e){e.printStackTrace();}
		//End Of Added Later*/


		return c; // returns null if camera is unavailable

    }

	private void playSound(){

		mp = MediaPlayer.create(ImageCapture.this, R.raw.light_switch_on);

		mp.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				// TODO Auto-generated method stub
				mp.release();
			}
		});

		if(mp!=null){
			mp.start();
		}
	}

	@Override  
    public void onPause() {
    	releaseCamera();  // release the camera immediately on pause event
		super.onPause();
    	finish();
    }
	
	@SuppressWarnings("deprecation")
	private void releaseCamera(){
        if (mCamera != null){
            mCamera.release();  // release the camera for other applications
            mCamera = null;
        }
    }
	
public void onBackPressed(){
	releaseCamera();
	camera2_status = false;
	CAMERA2_IMAGEPATH = "";
	ImageCapture.this.finish();
}

@Override
protected void onStart() {
	super.onStart();
	
}

@Override
protected void onStop() {
	super.onStop();
	
}

@Override
protected void onResume() {
	super.onResume();
	
}
	
@Override
protected void onRestart() {
	super.onRestart();
}

@Override
protected void onDestroy() {
	 releaseCamera();
     super.onDestroy();
}

@Override
public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
	// TODO Auto-generated method stub
	
	if(surfaceHolder.getSurface() == null){
        // preview surface does not exist
        return;
      }
	
	// stop preview before making changes
    try {
		if (mCamera != null) {
			mCamera.stopPreview();
		}
    } catch (Exception e){
		e.printStackTrace();
	}
    	
    // start preview with new settings
    try {
		if (mCamera != null) {
			mCamera.setPreviewDisplay(surfaceHolder);
			mCamera.startPreview();
		}
    } catch (IOException e) {
        Log.e("ImageCapture", "Error setting camera preview: " + e.getMessage());
    }

}

@Override
public void surfaceCreated(SurfaceHolder holder) {
	// TODO Auto-generated method stub
	
	if (mCamera != null){
		try {

			//Added Later
			/*params = mCamera.getParameters();
			params.setJpegQuality(CameraProfile.getJpegEncodingQualityParameter(0, CameraProfile.QUALITY_HIGH));
			params.setAntibanding("auto");

			List<Size> sizes = params.getSupportedPictureSizes();
			System.out.println("Total Camera Picture Sizes are: "+sizes.size());
			System.out.println("Camera Picture Sizes are: "+sizes.toString());
			Size size = sizes.get(0);

			for (int i = 0; i <= sizes.size()/3; i++) {
				if (sizes.get(i).width > size.width)
					size = sizes.get(i);

				System.out.println("Picture Size: "+size.width+","+size.height);
			}

			System.out.println("Finally Setting Picture Size: "+size.width+","+size.height);
			params.setPreviewSize(size.width, size.height); ////Added Later
			params.setPictureSize(size.width, size.height);
			mCamera.setParameters(params);*/
			//Finish of Added Later

            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();

        }//try
		catch (IOException e) {
            Log.e("ImageCapture", "Error setting camera preview: " + e.getMessage());
        }
    }
    else {
          finish();
    }

}

@Override
public void surfaceDestroyed(SurfaceHolder holder) {
	// TODO Auto-generated method stub
	releaseCamera();
}

private void showNoFrontCameraDialog(){
	final AlertDialog.Builder aldb = new AlertDialog.Builder(ImageCapture.this);
	aldb.setTitle("Camera Error!");
	aldb.setMessage("You need a front camera to take picture!");
	aldb.setCancelable(false);
	aldb.setPositiveButton("OK", new DialogInterface.OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			ImageCapture.this.finish();
		}
	});
	aldb.show();
}
	
}//Main Class