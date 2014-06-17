package lmquan1990.biitbook;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.biitbook.android.R;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.util.Timer;
import java.util.TimerTask;

import org.geometerplus.android.fbreader.FBReader;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;

import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.app.ProgressDialog;
import org.holoeverywhere.widget.TextView;
import org.holoeverywhere.widget.Toast;
import org.holoeverywhere.widget.ProgressBar;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.*;
import android.view.LayoutInflater;
import com.actionbarsherlock.view.MenuInflater;
import com.artifex.mupdfdemo.MuPDFActivity;

import android.view.View;
import android.view.ViewGroup;

@SuppressLint({ "SetJavaScriptEnabled", "NewApi", "NewApi" })
public class DiscoveryFragment extends SherlockFragment {    
    String bookPath = "";
    String imagePath = "";
    ProgressBar progress;
    public GolbalFunction postUrl = new GolbalFunction();	
	public String userid ="";
	public String bookid="";
	public String pass="";
	public String lang="";
	public String path="";	 
	public String type="";
	boolean check = false;
	String u = "";
	JSONObject json_data = null;
    public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
    private ProgressDialog pd = null;
    //ProgressDialog progressBar;
    private NotificationManager notificationManager;
	private Notification notification;
	PendingIntent contentIntent;
	int lastPercent = 0;
	DBAdapter db;
	UserDB userdb;	
    boolean down = false;
	public WebView webView;
	public LinearLayout webLayout;
	public RelativeLayout errLayout;
	public static final String ACTION_OPEN_BOOK = "android.fbreader.action.VIEW";
	public static final String BOOK_KEY = "fbreader.book";
	public static final String BOOK_PATH = "fbreader.bookpath";
	public static final String BOOKMARK_KEY = "fbreader.bookmark";
	TextView txtCount;	
	Button btnRetry;
	public static Activity fa;
	String json = "";
	String url = "";
	String tmpPath = "";
	String passread = "";
	JSONArray arrayBc;	
	DownloadSDCard myTask = null;
	BuyTask buyTask = null;
	DownloadTask downloadTask = null;
	int lenghtOfFile = 0;
	private static File cacheDir = null;	
	boolean done = true;
	private Timer timer;
	private TimerTask timerTask;
	private boolean mag;
			
    @SuppressWarnings("static-access")
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){   
        View v;
        db = new DBAdapter((LibsActivity)getActivity());
        userdb = new UserDB((LibsActivity)getActivity());    
        userid = postUrl.loadSavedPreferences("KEY_IDEND");
        pass = postUrl.loadSavedPreferences("KEY_PASSEND");
        lang = postUrl.loadSavedPreferences("KEY_LANG");
	    if (cacheDir == null) cacheDir = Utils.createCacheDir((LibsActivity)getActivity());
	    if(userid.equals("")){
	    	v = inflater.inflate(R.layout.nologin, container, false);
        	Button btnLogin = (Button)v.findViewById(R.id.btnLogin_no);
        	btnLogin.setOnClickListener(new View.OnClickListener() {
				
				public void onClick(View v) {
					// TODO Auto-generated method stub
					postUrl.savePreferences("KEY_RUN", "2");
					Intent i = new Intent((LibsActivity)getActivity(), LoginActivity.class);
					startActivity(i);	
				}
			});
        }else{
        	v = inflater.inflate(R.layout.main_ui, container, false);
        	//progressBar = new ProgressDialog(v.getContext());
        	notificationManager = (NotificationManager) getActivity().getSystemService(getActivity().NOTIFICATION_SERVICE);
        	if (android.os.Build.VERSION.SDK_INT > 9) {
				StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
				StrictMode.setThreadPolicy(policy);
			}
						
			myTask = null;
			bookPath = postUrl.loadSavedPreferences("KEY_PATHBOOK");
			tmpPath = postUrl.loadSavedPreferences("KEY_PATHTMP"); 
			File tmp = new File(tmpPath);
			try {
				if(tmp.list().length>0){				
					DeleteRecursive(tmp);				 
				}	
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
			btnRetry = (Button)v.findViewById(R.id.retry);
			errLayout = (RelativeLayout)v.findViewById(R.id.layout_error);
		            
			progress = (ProgressBar)v.findViewById(R.id.progressBar);
			progress.setMax(100);
			webLayout = (LinearLayout)v.findViewById(R.id.layout_webview);
			webLayout.setVisibility(View.VISIBLE);
        
			webView = (WebView)v.findViewById(R.id.webView);        
			webView.getSettings().setJavaScriptEnabled(true);        
			webView.setVerticalScrollBarEnabled(false);
			webView.setHorizontalScrollBarEnabled(false);        
			webView.requestFocus();
       
			webView.setWebViewClient(new WebViewClient(){        	
        	@Override
            public boolean shouldOverrideUrlLoading(final WebView view,final String url) {
        		progress.setVisibility(View.VISIBLE);
            	webView.post(new Runnable() {		            
		            public void run() {
		            	view.loadUrl(url);
		            }
		        });     
            	if(lang.equals("vi")){	            	
            		if(url.length()>31){
            			if(url.startsWith("http://touch.biitbook.com/book/")==true){
		                	String tmp = url.substring(31, url.length()); 
		                	if(isInteger(tmp)){
		                		bookid = tmp;
		                		mag = false;
		                	}
            			}	
            			if(url.startsWith("http://touch.biitbook.com/book/download1/")==true){
		            		buyTask = new BuyTask(); 
							buyTask.execute();
		            	}
							
			            if(url.startsWith("http://touch.biitbook.com/magazine/detail/")==true){
			               	String tmp = url.substring(42, url.length()); 
			               	if(isInteger(tmp)){
			               		bookid = tmp;
			               		mag = true;
			               	}
			            }    	            	           		
		            	if(url.startsWith("http://touch.biitbook.com/book/read_trial/")==true){
		            		buyTask = new BuyTask(); 
							buyTask.execute();
		            	}
		            			            	
	            		if(url.startsWith("http://touch.biitbook.com/magazine/download/m/")==true){
	            			buyTask = new BuyTask(); 
							buyTask.execute();
	            		}
	            	
            		}else{            			
            			view.loadUrl(url);
            		}          		
            	}else{
            		if(url.length()>30){
            			if(url.startsWith("http://touch.biitbook.me/book/")==true){
		                	String tmp = url.substring(30, url.length()); 
		                	if(isInteger(tmp)){
		                		bookid = tmp;	
		                		mag = false;
		                	}
		                }            		
						if(url.startsWith("http://touch.biitbook.me/book/download/")==true){
	            			buyTask = new BuyTask(); 
							buyTask.execute();  
	            		}
		                if(url.startsWith("http://touch.biitbook.me/magazine/detail/")==true){		                	
		                	String tmp = url.substring(41, url.length()); 
		                	if(isInteger(tmp))
		                		bookid = tmp;
		                		mag = true;
		                }
						if(url.startsWith("http://touch.biitbook.me/book/read_trial/")==true){
	            			buyTask = new BuyTask(); 
							buyTask.execute();
	            		}
						
	            		if(url.startsWith("http://touch.biitbook.me/magazine/download/m/")==true){
	            			buyTask = new BuyTask(); 
							buyTask.execute();						
	            		}
	            	}            		
            		else{            			
            			view.loadUrl(url);
            		}
            	}
                return true;
            }
        	@Override
        	public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        		webLayout.setVisibility(View.GONE);
                errLayout.setVisibility(View.VISIBLE);
            }
            
		    @Override
		    public void onPageFinished(WebView view, String url) {
		    	CookieSyncManager.getInstance().sync();
	    		progress.setVisibility(View.GONE);
	           	webLayout.setBackgroundColor(Color.parseColor("#F6F6F6"));
	          }            
	        });
        webView.setWebChromeClient(new WebChromeClient(){
       	 @Override
            public void onProgressChanged(WebView view, int newProgress) {
       		 	DiscoveryFragment.this.setValue(newProgress);
                super.onProgressChanged(view, newProgress);
            }
       });
        if(lang.equals("vi"))
        	webView.loadUrl("http://touch.biitbook.com?userid="+userid+"&pass="+pass);
        else{
        	webView.loadUrl("http://touch.biitbook.me?userid="+userid+"&pass="+pass);
        }
        
        btnRetry.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				errLayout.setVisibility(View.GONE);
				webLayout.setVisibility(View.VISIBLE);  
				if(done==true)
					if(lang.equals("vi"))
			        	webView.loadUrl("http://touch.biitbook.com?userid="+userid+"&pass="+pass);
			        else{
			        	webView.loadUrl("http://touch.biitbook.me?userid="+userid+"&pass="+pass);
			        }
			}
		});
        }
	    ((LibsActivity)getActivity()).getSlidingMenu().showContent();
		((LibsActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		return v;
    }
        
    public boolean WebViewGoBack() {
    	if(webView !=null && webView.canGoBack()){
    		progress.setVisibility(View.VISIBLE);
    	   webView.goBack();
    	   return true;
    	}
    	return false;
    }    
           
    private void insert(String id, String name, String author, String image_view,
    		String file_name, String point, String cat, String book_path, String type, 
    		String onoff, String pass, String userid){
		db.open();
        @SuppressWarnings("unused")
		long idBooks;
        idBooks = db.insertTitle(id,name,author,image_view,file_name,point,cat,book_path,
        		type, onoff, pass, userid);  
        db.close();
	}
	
	void DeleteRecursive(File fileOrDirectory) {
		 if (fileOrDirectory.isDirectory())
		    for (File child : fileOrDirectory.listFiles())
		        DeleteRecursive(child);
		    fileOrDirectory.delete();
	}
	
	private void updatePass(String bookid, String pass){ 
    	try{
    		userdb.open();
	        @SuppressWarnings("unused")
			boolean idBooks;
	        idBooks = db.updatePass(bookid, pass);  
	        db.close();
    	}catch(Exception e){
    		e.printStackTrace(); 		
    	}
    }

	private void updateJson(String userid, String json){ 
    	try{
    		userdb.open();
	        @SuppressWarnings("unused")
			boolean idBooks;
	        idBooks = userdb.updateJson(userid, json);  
	        userdb.close();
    	}catch(Exception e){
    		e.printStackTrace(); 		
    	}
    }	
		
    public void updateUser(String userid, String log){ 
    	try{
    		userdb.open();	        
			userdb.updateUser(userid, log);  
	        userdb.close();
    	}catch(Exception e){
    		System.out.println(e.toString());
    	}
    }
    
    public void setValue(int progress) {
        this.progress.setProgress(progress);        
    }
        
    public boolean isInteger(String input){
       try{
          Integer.parseInt(input);
          return true;
       }
       catch(Exception ex){
          return false;
       }
    }   	
	
	private void getBitmap(String url){       	
    	String filename = "";
    	File f = null;
    	filename=String.valueOf(url.hashCode());
        f=new File(cacheDir, filename);
    	try {
    		InputStream is=new URL(url).openStream();
            OutputStream os = new FileOutputStream(f);
            Utils.copyStream(is, os);
            os.close();	
        } catch (Exception ex){        	
        	Bitmap bitmap = BitmapFactory.decodeResource(((LibsActivity)getActivity()).getResources(), R.drawable.load);
     	   	filename=String.valueOf(url.hashCode());
     	   	f=new File(cacheDir, filename);
     	   	OutputStream outStream;
     	   	try {
				outStream = new FileOutputStream(f);
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
	         	outStream.flush();
	         	outStream.close();
     	   	}catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
     	   	}            	   
        }
    }   
   
    
    private class BuyTask extends AsyncTask<String, Integer, String> {
    	
		 @Override
           protected void onPreExecute() {
              pd = new ProgressDialog((LibsActivity)getActivity());
       			pd.setMessage(getResources().getString(R.string.processing));
                   pd.setIndeterminate(false);
                   pd.setCancelable(false);  
                   pd.setCanceledOnTouchOutside(false);
               if (!pd.isShowing())
                   pd.show();      
               File file = new File(bookPath+"biit");    		
        	    if(file.exists()){
        	    	@SuppressWarnings("unused")
        			boolean deleted = file.delete();
        	    }
               super.onPreExecute();
           }

		 @Override
		 protected String doInBackground(String... params) {        	
			 try{
				 Thread.sleep(3000);
			 }catch(InterruptedException ex) {
				 Thread.currentThread().interrupt();
			 }       
			 return null;
       }
       @Override
       protected void onPostExecute(String result) {
			myTask = new DownloadSDCard(); 
			myTask.execute("0");
       	}
   }
    
    @SuppressLint("SdCardPath")
    public class DownloadSDCard extends AsyncTask<String, String, String> {     	
     	@Override
     	protected String doInBackground(String... aurl) {
     			String x = "0";
     			if(mag == false){
     				json = postUrl.getJSONFromUrl("http://biitbook.com/api/ios/api_getuserdown.php?userid=" + userid);
     				u = "url";
     			}else{
     				json = postUrl.getJSONFromUrl("http://biitbook.com/api/ios/api_getuserdown_mag.php?userid=" + userid);
     				u = "url_file";
     			}
           		if(!json.equals("")){           		
           			// 0 doc thu, 1 mua thanh cong, 2 k du mua
           			JSONObject job;
           			JSONArray array;        				     					
           			try {
						job = new JSONObject(json);
						array = job.getJSONArray("result");					
						postUrl.savePreferences("KEY_BOOK", array.length() + "");		    		        					
						if(array.length()>0){
							for(int j = 0; j <array.length(); j++){								
								json_data = array.getJSONObject(j);
								String id = json_data.getString("id");
								if(bookid.equals(id)){
									path = returnPath(bookid);
									if(path!=null&&!path.equals("")){										
										File direct = new File(path);
										if(direct.exists()){											
											check = false;
											passread = json_data.getString("pass");
											url = json_data.getString(u);
											type = json_data.getString("file_name").substring(json_data.getString("file_name").lastIndexOf(".")+1, json_data.getString("file_name").length());
											updatePass(bookid, passread);															
											getBitmap(json_data.getString("image_view"));																							
										}else{
											check = true;
											passread = json_data.getString("pass");
											url = json_data.getString(u);
											type = json_data.getString("file_name").substring(json_data.getString("file_name").lastIndexOf(".")+1, json_data.getString("file_name").length());
											updatePass(bookid, passread);
											getBitmap(json_data.getString("image_view"));
										}
									}else{
										check = true;										 
										passread = json_data.getString("pass");
										url = json_data.getString(u);
										type = json_data.getString("file_name").substring(json_data.getString("file_name").lastIndexOf(".")+1, json_data.getString("file_name").length());									
										insert(bookid, json_data.getString("name"), json_data.getString("author"),
												json_data.getString("image_view"), json_data.getString("file_name"), 
												json_data.getString("point"),"002", 
												bookPath+bookid, 
												type, "1",json_data.getString("pass"),userid);
										updateJson(userid, json);
										getBitmap(json_data.getString("image_view"));
									}
								}
							}
							if(check == false)
								x = "1";							
			        	}
           			} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
           		}else{           			
                	x = "1";
           		}			
           		if(x.equals("0")){
           			try {  
	           			URL url1 = new URL(url);		    	
	     				for(int i = 0; i < url.length(); i++){
	     					if(Character.isWhitespace(url.charAt(i))){
	     		                String newName = url.substring(0, i) + "%20" + url.substring(i+1, url.length());
	     		               url = newName;		             
	     		            }
	     				}
	     		    	try {
	     	                @SuppressWarnings("unused")
	     	    			InetAddress i = InetAddress.getByName(url);
	     	            } catch (Exception e1) {
	     	              	 System.out.println("Error unknow url: "+e1.toString());
	     	            }
	     		    	URLConnection conexion = url1.openConnection();
	     		    	conexion.connect();	
	     		    	lenghtOfFile = conexion.getContentLength();
	           		} catch (Exception e) {
	     	    		e.printStackTrace();
	     	    		x = "1";
	     	   		}
           		}     		
     		return x;
     	}
     	
 		@Override
     	protected void onPostExecute(String unused) { 
 			if (pd.isShowing())
	               pd.dismiss();
     		if(unused.equals("1")){    	
     			if(path!=null&&!path.equals("")){
     				postUrl.savePreferences("KEY_IDBOOK", bookid);
    	         	postUrl.savePreferences("KEY_EXTBOOK", "."+type);
    	         	postUrl.savePreferences("KEY_ONOFF", "1");    	    		
    	    		Toast.makeText(((LibsActivity)getActivity()), getResources().getString(R.string.downsuccess), Toast.LENGTH_LONG).show();	    		
    	         	if(!type.equals("pdf")){	         	
    		    		((LibsActivity)getActivity()).startActivity(
    				       		new Intent((LibsActivity)getActivity(), FBReader.class)
    				       				.setAction(ACTION_OPEN_BOOK));	    		
    		    		getActivity().finish();
    	         	}else{
    	         		Uri uri = Uri.parse(postUrl.loadSavedPreferences("KEY_PATHTMP")+bookid+"."+type);
    	    			Intent intent = new Intent(getActivity(), MuPDFActivity.class);
    	    			intent.setAction(Intent.ACTION_VIEW);
    	    			intent.setData(uri);
    	    			getActivity().startActivity(intent);
    	    			getActivity().finish();
    	         	}
     			}else if(path==null||path.equals("")){	     			
	       			AlertDialog.Builder builder1 = new AlertDialog.Builder((LibsActivity)getActivity());
	   			    builder1.setTitle(getResources().getString(R.string.notice));
	   			    builder1.setMessage(getResources().getString(R.string.buyerror));
	   			    builder1.setCancelable(true);
	   			    builder1.setNeutralButton(android.R.string.ok,
	   			            new DialogInterface.OnClickListener() {
	   			        public void onClick(DialogInterface dialog, int id) {
	   			            dialog.cancel();			            
	   			        }
	   			    });
	
	   			    AlertDialog alert11 = builder1.create();
	   			    alert11.show();
	   			    File file = new File(bookPath+"biit");    		
	   				if(file.exists()){
	   					@SuppressWarnings("unused")
	   					boolean deleted = file.delete();
	   				} 							
     			}     			
     		}else if(unused.equals("0")){       		       		
     			downloadTask = new DownloadTask();
     			downloadTask.execute(url);
     		}
 		}	
 	}       
    
    @Override
    public void onCreate(Bundle savedInstanceState) { 
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
	}	
    
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:							
			((LibsActivity)getActivity()).toggle();
			return true;	
		case R.id.refresh:		
			
			if(webView !=null){
				if(done==true){		
					progress.setVisibility(View.VISIBLE);
					if(lang.equals("vi"))
			        	webView.loadUrl("http://touch.biitbook.com?userid="+userid+"&pass="+pass);
			        else
			        	webView.loadUrl("http://touch.biitbook.me?userid="+userid+"&pass="+pass);					
					done = false;
				}
				 try {
				       timer = new Timer();
				       timerTask = new TimerTask() {
				          @Override
				          public void run() {
				        	  done = true;
				          }
				       };
				    timer.schedule(timerTask, 4000, 4000);
				    } catch (Exception e){
				       e.printStackTrace();
				    }
				return true;		
			}
		}
		return true;
	}
	
    @Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();
		inflater.inflate(R.menu.web, menu);		
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	@SuppressLint("SdCardPath")
    public class DownloadTask extends AsyncTask<String, String, String> {

 		@SuppressWarnings("deprecation")
		@Override
     	protected void onPreExecute() {		 			
 			Intent notificationIntent = new Intent();
			contentIntent = PendingIntent.getActivity(getActivity(), 0, notificationIntent, 0);
			notification = new Notification(R.drawable.ic_download,
					getActivity().getResources().getString(R.string.download), System.currentTimeMillis());
			notification.flags = notification.flags
					| Notification.FLAG_ONGOING_EVENT;
			notification.contentView = new RemoteViews(getActivity()
					.getPackageName(), R.layout.upload_progress_bar);
			notification.contentIntent = contentIntent;
			//notification.contentView.setProgressBar(R.id.progressBar1, 100,0, false);					
			notificationManager.notify(1, notification);
 			
 			/**progressBar.setCancelable(true);
			progressBar.setMessage(getResources().getString(R.string.downing));
			progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			progressBar.setProgress(0);	
			progressBar.setIndeterminate(false);
			progressBar.setMax(lenghtOfFile/1024);			
			progressBar.setProgressNumberFormat("%1d/%2d KB");
			progressBar.setCanceledOnTouchOutside(false);
			progressBar.setOnCancelListener(new DialogInterface.OnCancelListener() {
				
				public void onCancel(DialogInterface dialog) {
					// TODO Auto-generated method stub
					try{
						downloadTask.cancel(true);
						File file = new File(bookPath+"biit");    		
			    	    if(file.exists()){
			    	    	@SuppressWarnings("unused")
			    			boolean deleted = file.delete();
			    	    }
			    	    progressBar.dismiss();
					}catch(Exception ex){}
				}
			});*/
			if (pd.isShowing())
				pd.dismiss();
			//progressBar.show();
     		super.onPreExecute();    	    		
     	}

     	@Override
     	protected String doInBackground(String... aurl) {
     		int count=0;
     		try {    			
 		    	URL url = new URL(aurl[0]);
 					    	
 		    	InputStream input = new BufferedInputStream(url.openStream());
 		    	OutputStream output = new FileOutputStream(bookPath+ "biit");		    			    	
 		    	byte data[] = new byte[1024];		    	
 		    			
 		    	long total = 0;
 		    	while ((count = input.read(data)) != -1) {
 	    			total += count;
 	    			publishProgress(""+(int)(total/1024));
 	    			output.write(data, 0, count);
 	    			if(isCancelled()){
 	    				publishProgress(""+0);
 	    				break;
 	    			}
 	    		}
 	
 	    		output.flush();
 	    		output.close();
 	    		input.close();	
 	    		
 	    		
 	    		
 	    	} catch (Exception e) {
 	    		e.printStackTrace();
 	    		return "1";
 	   		}
     		return "0";


     	}
     	
    	protected void onProgressUpdate(String... progress) {
    		//progressBar.setProgress(Integer.parseInt(progress[0]));
    		int percent = Integer.parseInt(progress[0]);
    		if(percent > lastPercent) {
				//notification.contentView.setProgressBar(R.id.progressBar1, 100, percent, false);
				notificationManager.notify(1, notification);
				lastPercent = percent;
			}
    	}
     	    	    	
 		@SuppressWarnings("deprecation")
		@Override
     	protected void onPostExecute(String unused) {
     		if(unused.equals("1")){
     			Toast.makeText(((LibsActivity)getActivity()), getResources().getString(R.string.downerror), Toast.LENGTH_LONG).show();
 			}else{       		       		
 				File from = new File(bookPath,"biit");
	       		File to = new File(bookPath,bookid);
	       		from.renameTo(to);			       	
	    		postUrl.savePreferences("KEY_IDBOOK", bookid);
	         	postUrl.savePreferences("KEY_EXTBOOK", "."+type);
	         	postUrl.savePreferences("KEY_ONOFF", "1");
	         	notification.setLatestEventInfo(getActivity(), getActivity().getResources().getString(R.string.downsuccess), getActivity().getResources().getString(R.string.done), contentIntent);
				notification.flags |= Notification.FLAG_AUTO_CANCEL;
				notificationManager.notify(1, notification);
	    		Toast.makeText(((LibsActivity)getActivity()), getResources().getString(R.string.downsuccess), Toast.LENGTH_LONG).show();	    		
	         	if(!type.equals("pdf")){	         	
		    		((LibsActivity)getActivity()).startActivity(
				       		new Intent((LibsActivity)getActivity(), FBReader.class)
				       				.setAction(ACTION_OPEN_BOOK));	    		
		    		getActivity().finish();
	         	}else{
	         		Uri uri = Uri.parse(postUrl.loadSavedPreferences("KEY_PATHTMP")+bookid+"."+type);
	    			Intent intent = new Intent(getActivity(), MuPDFActivity.class);
	    			intent.setAction(Intent.ACTION_VIEW);
	    			intent.setData(uri);
	    			getActivity().startActivity(intent);
	    			getActivity().finish();
	         	}
     		}
     	}
     }
	
	private String returnPath(String idbook){
		db.open();
        String pass = "";
        Cursor c = db.getTitle(idbook);
        if (c.moveToFirst())
            pass = getPath(c);
        c.close();
        db.close();		
        return pass;
	}
	
	public String getPath(Cursor c){
		return c.getString(8);	                
	}
}