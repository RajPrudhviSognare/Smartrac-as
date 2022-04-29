package com.experis.smartrac.as;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;

public class RestFullClient {

	 public String url;
	 public String contentBody;
	 public int responseCode;
	 public String message;
	 public String response;
	 public ArrayList<NameValuePair> params;
	 public ArrayList<NameValuePair> headers;
	 
	 
	 public JSONObject jObj = null;

	 public RestFullClient(String url) {
		 
	      this.url = url;
	      params = new ArrayList<NameValuePair>();
	      headers = new ArrayList<NameValuePair>();
	      
	   }	  
	 
	 public RestFullClient() {
	   }

	   public String getResponse() {
	      return response;
	   }

	   public String getErrorMessage() {
	      return message;
	   }

	   public int getResponseCode() {
	      return responseCode;
	   }

	   public void AddParam(String name, String value) {
		      params.add(new BasicNameValuePair(name, value));
		   Log.d("PARAM", name + " : " + value);
	   }

	   public void AddHeader(String name, String value) {
		      headers.add(new BasicNameValuePair(name, value));
	   }
	   
	   public void Execute(int i) throws Exception {
		      switch (i) {
		      
		      case 0: {   //Get
		         // add parameters
		         String combinedParams = "";
		         if (!params.isEmpty()) {
		            combinedParams += "?";
		            for (NameValuePair p : params) {
		               String paramString = p.getName() + "=" + URLEncoder.encode(p.getValue(), "UTF-8");
		               if (combinedParams.length() > 1) {
		                  combinedParams += "&" + paramString;
		               } else {
		                  combinedParams += paramString;
		               }
		            }
		         }

		         HttpGet request = new HttpGet(url + combinedParams);

		         // add headers
		         for (NameValuePair h : headers) {
		            request.addHeader(h.getName(), h.getValue());
		         }

		         executeRequest(request, url);
		         break;
		      }
		      
		      case 1: {  //Post
		    	  
		         HttpPost request = new HttpPost(url);

		         // add headers
		         for (NameValuePair h : headers) {
		            request.addHeader(h.getName(), h.getValue());
		         }
		         if (contentBody != null) {
		            request.setEntity(new StringEntity(contentBody));
		            request.setHeader("Content-Type", "application/x-www-form-urlencoded");
		         } else if (!params.isEmpty()) {
		            request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
		            
		         }

		         executeRequest(request, url);
		         break;
		      }

		      case 2: {  //PUT
		    	  
			         HttpPut request = new HttpPut(url);

			         // add headers
			         for (NameValuePair h : headers) {
			            request.addHeader(h.getName(), h.getValue());
			         }
			         if (!params.isEmpty()) {
			        	//request.setHeader("Content-Type", "application/json");
			            request.setEntity(new UrlEncodedFormEntity(params,HTTP.UTF_8));
			         }

			         executeRequest(request, url);
			         break;
			      }

		      case 3: {   //DELETE
			         // add parameters
			         String combinedParams = "";
			         if (!params.isEmpty()) {
			            combinedParams += "?";
			            for (NameValuePair p : params) {
			               String paramString = p.getName() + "=" + URLEncoder.encode(p.getValue(), "UTF-8");
			               if (combinedParams.length() > 1) {
			                  combinedParams += "&" + paramString;
			               } else {
			                  combinedParams += paramString;
			               }
			            }
			         }

			         HttpDelete request = new HttpDelete(url + combinedParams);

			         // add headers
			         for (NameValuePair h : headers) {
			            request.addHeader(h.getName(), h.getValue());
			         }

			         executeRequest(request, url);
			         break;
			      }

		      case 4: {

		    	  break;
		      }

	}//Switch
		      
}//Execute(int i)
	   
	   
	   public void requestJSONArrayRestaurant(String tag, JSONArray jArray) throws JSONException{
		   
		   HttpClient httpClient = new DefaultHttpClient();
		   JSONObject json = new JSONObject();
		   json.put(tag, jArray.toString());

		   try {
		       HttpPost request = new HttpPost(url);
		       StringEntity params =new StringEntity(json.toString());
		       request.addHeader("content-type", "application/json");
		       request.addHeader("Accept","application/json");
		       request.setEntity(params);
		       HttpResponse httpResponse = httpClient.execute(request);
		       
		         responseCode = httpResponse.getStatusLine().getStatusCode();
		         System.out.println("Response code: "+ responseCode);
		         message = httpResponse.getStatusLine().getReasonPhrase();
		         System.out.println("Message: "+ message);

		         HttpEntity entity = httpResponse.getEntity();

		         if (entity != null) {
		            InputStream instream = entity.getContent();
		            response = convertStreamToString(instream);
		            System.out.println("Response: "+ response);
		            jObj = getJSONFromString(response);
		            System.out.println("JSONObject: "+ jObj);
		            
		            // Closing the input stream will trigger connection release
		            instream.close();
		         }
		       
		   }catch (Exception ex) {
		       // handle exception here
		   } finally {
		       httpClient.getConnectionManager().shutdown();
		   }
		   
	   }
	   
	   
	   private void executeRequest(HttpUriRequest request, String url) {
		      HttpClient client = new DefaultHttpClient();
		      // client.getParams().s
		      HttpResponse httpResponse;
		      try {
		         httpResponse = client.execute(request);
		         responseCode = httpResponse.getStatusLine().getStatusCode();
		         System.out.println("Response code: "+ responseCode);
		         message = httpResponse.getStatusLine().getReasonPhrase();
		         System.out.println("Message: "+ message);

		         HttpEntity entity = httpResponse.getEntity();

		         if (entity != null) {

		             InputStream instream = entity.getContent();
		             response = convertStreamToString(instream);
		             System.out.println("Response: "+ response);
					 Log.v("Response",response);
					 jObj = getJSONFromString(response);
		             System.out.println("JSONObject: "+ jObj);
		            
		            // Closing the input stream will trigger connection release
		            instream.close();
		            
		         }

		      } catch (ClientProtocolException e) {
		         client.getConnectionManager().shutdown();
		         e.printStackTrace();
		      } catch (IOException e) {
		         client.getConnectionManager().shutdown();
		         e.printStackTrace();
		      }
		      
		      
		   }//executeRequest(HttpUriRequest request, String url)
	   
	   
	   private static String convertStreamToString(InputStream is) {

		      BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		      StringBuilder sb = new StringBuilder();

		      String line = null;
		      try {
		         while ((line = reader.readLine()) != null) {
		            sb.append(line + "\n");
		         }
		      } catch (IOException e) {
		         e.printStackTrace();
		      } finally {
		         try {
		            is.close();
		         } catch (IOException e) {
		            e.printStackTrace();
		         }
		      }
		      return sb.toString();
		      
		      
		   }//convertStreamToString(InputStream is)
	   
	   
	   public JSONObject getJSONFromString(String str){
		   
		   JSONObject jObj = null;
		   
		    // try parse the string to a JSON object
	        try {
	            jObj = new JSONObject(str);
	        } catch (JSONException e) {
	            Log.e("JSON Parser", "Error parsing data " + e.toString());
	        }
	 
	        // return JSON String
	        return jObj;
	   }

	   public void setContentBody(String _body) {
		      contentBody = _body;
		   }

}//Main Class