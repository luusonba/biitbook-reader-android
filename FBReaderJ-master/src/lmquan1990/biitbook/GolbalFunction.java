package lmquan1990.biitbook;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.geometerplus.android.fbreader.FBReaderApplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

public class GolbalFunction {
	private static final int DNS_SLEEP_WAIT = 250;
	static InputStream is = null;    
    static String json = "null";
    /**	KEY_EMAILEND  
     * 	KEY_STTEND
		KEY_IDEND 
		KEY_BOOK 
		KEY_RUN  0 la moi vao 1 la gate 2 la login tu trong app 
		KEY_TITLE
		KEY_FAG
		KEY_BC 
		KEY_NAME
		KEY_TIME
		KEY_LIB
		KEY_IDBOOK
		KEY_PASSBOOK
		KEY_PASSEND
		KEY_EXTBOOK
		KEY_ENCBOOK
		KEY_PATHBOOK
		KEY_PATHTMP   
		KEY_PATHOFFBOOK
		KEY_AUTHOR		
		KEY_DOWN
		KEY_CANCELLOGIN
		KEY_SCAN
		0 true, co
		1 false, khong
     * @param key
     * @return
     */
	
    public void actionFinish(){
		if(LibsActivity.fa != null)			
			LibsActivity.fa.finish();
		if(BuyBookActivity.fa != null)
			BuyBookActivity.fa.finish();
		if(SignupActivity.fa != null)
			SignupActivity.fa.finish();
		if(TermActivity.fa != null)
			TermActivity.fa.finish();
		if(ForgotActivity.fa != null)
			ForgotActivity.fa.finish();
	}
    
    public String loadSavedPreferences(String key) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(FBReaderApplication.getAppContext());
		String value = "";
		value = sp.getString(key, "");
		return value;
	}
    
	public void savePreferences(String key, String value) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(FBReaderApplication.getAppContext());
		Editor edit = sp.edit();
		edit.putString(key, value);
		edit.commit();
	}
	
    @SuppressLint({ "NewApi", "NewApi" })
	public String getJSONFromUrl(String url){  
    	if(isOnline()){
	    	RemoteDnsCheck check = new RemoteDnsCheck();
	    	json = "null";
	    	if(android.os.Build.VERSION.SDK_INT<11)
	    		check.execute(url);
	    	if(android.os.Build.VERSION.SDK_INT>=11)
	    		check.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
	        try {
	            int timeSlept = 0;
	            while(json.equals("null") && timeSlept<40000 && loadSavedPreferences("KEY_CANCELLOGIN").equals("1")){
	                Thread.sleep(DNS_SLEEP_WAIT);
	                timeSlept+=DNS_SLEEP_WAIT;
	            }            
	        } catch (Exception e) {        	
	        	json = "";
	        }
	        check.cancel(true);
    	}else{
    		json = "";
    	}
    	
    	/**byte[] b;
		try {
			b = json.getBytes("UTF-8");
			json = new String(b, "US-ASCII");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}**/
		//System.out.println("Json:  " + json);
        return json;
    }
    
    @SuppressLint({ "NewApi", "NewApi" })
   	public String getJSONFromUrlGate(String url){  
       	if(isOnline()){
   	    	RemoteDnsCheck check = new RemoteDnsCheck();
   	    	json = "null";
   	    	if(android.os.Build.VERSION.SDK_INT<11)
   	    		check.execute(url);
   	    	if(android.os.Build.VERSION.SDK_INT>=11)
   	    		check.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
   	        try {
   	            int timeSlept = 0;
   	            while(json.equals("null") && timeSlept<5000){
   	                Thread.sleep(DNS_SLEEP_WAIT);
   	                timeSlept+=DNS_SLEEP_WAIT;   	                
   	            }            
   	        } catch (Exception e) {        	
   	        	json = "";
   	        }    
   	        check.cancel(true);
       	}else{
       		json = "";
       	}
       	/**byte[] b;
		try {
			b = json.getBytes("UTF-8");
			json = new String(b, "US-ASCII");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}**/
        return json;
    }
    
    public boolean isOnline() {
    	ConnectivityManager cm = (ConnectivityManager)FBReaderApplication.getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;            
        }
        return false;
    }
    
    public class RemoteDnsCheck extends AsyncTask<String, String, String> {
    	
    	private String convertStreamToString(InputStream is) {

    		
    		BufferedReader reader;
    		StringBuilder sb = new StringBuilder();
    		try {
				reader = new BufferedReader(new InputStreamReader(is, "utf-8"), 8);
			 
            
            //String x = "";
            String line ="";
            if(reader != null){
    	        try {
    	        	
    	      /**  	while ((line = reader.readLine()) != null){
    	        	    sb.append(line);
    	        	    x = sb.toString().substring(0, sb.toString().length()-1);
    	        	}**/

    	            while ((line = reader.readLine()) != null) {
    	                sb.append(line + "\n");
    	            }
    	        } catch (Exception e) {
    	            /////e.printStackTrace();
    	        } finally {
    	            try {
    	                is.close();
    	            } catch (Exception e) {
    	               ///// e.printStackTrace();
    	            }
    	        }
            }
    		} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				////////e1.printStackTrace();
			}
            return sb.toString();
        }	
 	   
        @Override
    	protected String doInBackground(String... aurl) {
        	try{
            	try {                    
    				@SuppressWarnings("unused")
					InetAddress i = InetAddress.getByName(aurl[0]);
                  } catch (UnknownHostException e1) {
                	 /////e1.printStackTrace();
                  }
            	HttpParams httpParameters = new BasicHttpParams(); 
            	int timeoutConnection = 20000;
            	HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);        	
            	int timeoutSocket = 15000;
            	HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
            	DefaultHttpClient httpclient = new DefaultHttpClient(httpParameters);
            	HttpPost httpPost = new HttpPost(aurl[0]);
            	HttpResponse response = (HttpResponse) httpclient.execute(httpPost);
                
                if(response != null){
    	            // Get hold of the response entity (-> the data):
    	            HttpEntity entity = response.getEntity();
    	
    	            if (entity != null) {
    	                // Read the content stream
    	                InputStream instream = entity.getContent();
    	
    	                // convert content stream to a String
    	                json= convertStreamToString(instream);
    	                instream.close();    	                                    
    	                //return resultString;
    	            }
                }else
                	json = "";
                }
            catch(Exception e){
            	///////e.printStackTrace();
            }
	        return null;
    	}
    }
}