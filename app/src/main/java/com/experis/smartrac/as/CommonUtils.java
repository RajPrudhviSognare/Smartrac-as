package com.experis.smartrac.as;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonUtils{

	public static String PREFERENCE_NAME = "SmartracGMRPreference";
	public static String TAG = "CommonUtils: ";

	//Internet Connection checker
	public static boolean isInternelAvailable(Context mContext) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }
	//End

	//Device location enabled or disabled checker
	public static boolean locationServicesEnabled(Context context) {
		LocationManager lm = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
		boolean gps_enabled = false;
		boolean net_enabled = false;
		try {
			gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
		} catch (Exception ex) {
			Log.e(TAG,"Exception in gps_enabled");
		}

		try {
			net_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		} catch (Exception ex) {
			Log.e(TAG,"Exception in network_enabled");
		}
		return gps_enabled || net_enabled;
	}
	
	public static boolean mobileNumberPatternMatcher(String mobNumber)	
	  {
		  String str = "^[2-9][0-9]{9}$";	  
		  Pattern pttrn = Pattern.compile(str);
		  Matcher mtch = pttrn.matcher(mobNumber);
		  return mtch.matches();
	  }
	
	public static int randInt(int min, int max) {
	    Random rand = new Random();
	    int randomNum = rand.nextInt((max - min) + 1) + min;
	    return randomNum;
	}

	public static String getIMEI(Context context){

		TelephonyManager mngr = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
		String imei = mngr.getDeviceId();
		System.out.println("IMEI: "+imei);
		return imei;

	}


	public static String md5(final String password) {
		try {

			MessageDigest digest = MessageDigest
					.getInstance("MD5");
			digest.update(password.getBytes());
			byte messageDigest[] = digest.digest();

			StringBuffer hexString = new StringBuffer();
			for (int i = 0; i < messageDigest.length; i++) {
				String h = Integer.toHexString(0xFF & messageDigest[i]);
				while (h.length() < 2)
					h = "0" + h;
				hexString.append(h);
			}
			System.out.println("MD5: "+hexString.toString());
			return hexString.toString();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";
	}

	public static String imageToString(String filePath){

		String encodedImage = null;

		Bitmap bm = BitmapFactory.decodeFile(filePath);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.JPEG, 50, baos); //bm is the bitmap object
		byte[] byteArrayImage = baos.toByteArray();
		encodedImage = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
		System.out.println("Base64 Encoded Image: "+encodedImage);

     return encodedImage;
	}


	public static int calculateDistanceInKilometer(double userLat, double userLng, double venueLat, double venueLng) {

		double AVERAGE_RADIUS_OF_EARTH_KM = 6371;

		double latDistance = Math.toRadians(userLat - venueLat);
		double lngDistance = Math.toRadians(userLng - venueLng);

		double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
				+ Math.cos(Math.toRadians(userLat)) * Math.cos(Math.toRadians(venueLat))
				* Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);

		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

		return (int) (Math.round(AVERAGE_RADIUS_OF_EARTH_KM * c));
	}

	public static int calculateDistanceInMeter(double userLat, double userLng, double venueLat, double venueLng) {

		double AVERAGE_RADIUS_OF_EARTH_M = 6371*1000;

		double latDistance = Math.toRadians(userLat - venueLat);
		double lngDistance = Math.toRadians(userLng - venueLng);

		double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
				+ Math.cos(Math.toRadians(userLat)) * Math.cos(Math.toRadians(venueLat))
				* Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);

		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

		return (int) (Math.round(AVERAGE_RADIUS_OF_EARTH_M * c));
	}

}//Main Class


