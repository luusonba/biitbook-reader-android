package lmquan1990.biitbook;

import com.biitbook.android.R;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.holoeverywhere.app.ProgressDialog;
import org.holoeverywhere.widget.Button;
import org.holoeverywhere.widget.ProgressBar;
import org.holoeverywhere.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import org.holoeverywhere.widget.TextView;

import android.text.Html;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

@SuppressLint("SetJavaScriptEnabled")
public class BuyBookActivity extends BaseActivity {
    public RelativeLayout buyLayout;
	Button btnBuy;
	TextView txtName;
	TextView txtAuthor;
	ImageView imageBook;
	WebView webView;
	LinearLayout layoutBuy;
	LinearLayout layoutWeb;
	RelativeLayout layoutErr;
	Button btnRetry;
	Toast toast;
	TextView text;
	ProgressBar progress;
	public static Activity fa;
	String json = "";
	String url = "";
	public String lang="";
	JSONArray arrayBc;	
	public GolbalFunction postUrl = new GolbalFunction();	
	public String userid ="";
	public String bookid="";
	public String pass = "";
	DBAdapter db;
	boolean isDone = false;
    public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
    private ProgressDialog pd = null;
	BuyTask buyTask = null;
	private boolean mag;
	JSONObject json_data = null;
	public GolbalFunction golbalFunc = new GolbalFunction();
	private static File cacheDir = null;
	
	public BuyBookActivity() {
		super(R.string.buybook);
	}
		
    @Override
    public void onCreate(Bundle savedInstanceState) { 
        super.onCreate(savedInstanceState);
        fa = this;
        db = new DBAdapter(this);
        userid = golbalFunc.loadSavedPreferences("KEY_IDEND");
        pass = golbalFunc.loadSavedPreferences("KEY_PASSEND");
        bookid = golbalFunc.loadSavedPreferences("KEY_IDBOOK");
        lang = postUrl.loadSavedPreferences("KEY_LANG");
        url = returnImage(bookid);
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff'>"+getResources().getString(R.string.buybook)+"</font>"));
        getSlidingMenu().setSlidingEnabled(false);
        setContentView(R.layout.buybook);		
        
        btnBuy = (Button)findViewById(R.id.btn_buy_book);
        txtName = (TextView)findViewById(R.id.text_Buy_textNameBook);
        txtAuthor = (TextView)findViewById(R.id.text_Buy_textAuthor);
        imageBook = (ImageView)findViewById(R.id.image_buybook);
        webView = (WebView)findViewById(R.id.webView_Buybook);
        layoutBuy = (LinearLayout)findViewById(R.id.layout_Buy);
        layoutWeb= (LinearLayout)findViewById(R.id.layout_Buybook_Webview);
        progress = (ProgressBar)findViewById(R.id.progressBar_Buy_web);
        btnRetry = (Button)findViewById(R.id.btn_buy_retry);
        layoutErr = (RelativeLayout)findViewById(R.id.layout_buy_error);
   	 	LayoutInflater inflater = getLayoutInflater();
		View layout = inflater.inflate(R.layout.custom_toast,
				(ViewGroup) findViewById(R.id.toast_custom));
		text = (TextView) layout.findViewById(R.id.tvtoast);
		toast = new Toast(getApplicationContext());
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setView(layout);

        if (cacheDir == null) cacheDir = Utils.createCacheDir(this);
        txtName.setText(returnName(bookid));
        txtAuthor.setText(returnAuthor(bookid));
        Bitmap bitmap=null;
        String filename = "";
    	File f = null;
    	filename=String.valueOf(url.hashCode());
        f=new File(cacheDir, filename);
        bitmap = decodeFile(f);
    	imageBook.setImageBitmap(bitmap);
    	webView.getSettings().setJavaScriptEnabled(true);        
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);        
        webView.requestFocus();
    	    	
        btnBuy.setOnClickListener(new View.OnClickListener() {			
			public void onClick(View v) {
				// TODO Auto-generated method stub				
				layoutBuy.setVisibility(View.GONE);
				if(lang.equals("vi"))
					webView.loadUrl("http://touch.biitbook.com/book/"+ bookid);
				else
					webView.loadUrl("http://touch.biitbook.me/book/"+ bookid);
				layoutWeb.setVisibility(View.VISIBLE);
			}			
		});                      
        
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
            			if(url.startsWith("http://touch.biitbook.com/book/")==true)
		                	mag = false;
            			if(url.startsWith("http://touch.biitbook.com/book/download1/")==true){
		            		buyTask = new BuyTask(); 
							buyTask.execute("1");
		            	}
							
			            if(url.startsWith("http://touch.biitbook.com/magazine/detail/")==true)
			               	mag = true;
		            	if(url.startsWith("http://touch.biitbook.com/book/read_trial/")==true){
		            		buyTask = new BuyTask(); 
							buyTask.execute("0");
		            	}
		            			            	
	            		if(url.startsWith("http://touch.biitbook.com/magazine/download/m/")==true){
	            			buyTask = new BuyTask(); 
							buyTask.execute("1");
	            		}
	            	
            		}else{            			
            			view.loadUrl(url);
            		}          		
            	}else{
            		if(url.length()>30){
            			if(url.startsWith("http://touch.biitbook.me/book/")==true)
		                	mag = false;
		                			                         		
						if(url.startsWith("http://touch.biitbook.me/book/download/")==true){
	            			buyTask = new BuyTask(); 
							buyTask.execute("1");  
	            		}
		                if(url.startsWith("http://touch.biitbook.me/magazine/detail/")==true)
		                	mag = true;
						if(url.startsWith("http://touch.biitbook.me/book/read_trial/")==true){
	            			buyTask = new BuyTask(); 
							buyTask.execute("0");
	            		}
						
	            		if(url.startsWith("http://touch.biitbook.me/magazine/download/m/")==true){
	            			buyTask = new BuyTask(); 
							buyTask.execute("1");						
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
        		layoutWeb.setVisibility(View.GONE);
        		layoutErr.setVisibility(View.VISIBLE);
            }
            
		    @Override
		    public void onPageFinished(WebView view, String url) {
		    	CookieSyncManager.getInstance().sync();
	    		progress.setVisibility(View.GONE);
	    		layoutWeb.setBackgroundColor(Color.parseColor("#F6F6F6"));
	          }            
	        });
        webView.setWebChromeClient(new WebChromeClient(){
       	 @Override
            public void onProgressChanged(WebView view, int newProgress) {
                BuyBookActivity.this.setValue(newProgress);
                super.onProgressChanged(view, newProgress);
            }
       });
        if(lang.equals("vi"))
        	webView.loadUrl("http://touch.biitbook.com?userid="+userid+"&pass="+pass);
        else
        	webView.loadUrl("http://touch.biitbook.me?userid="+userid+"&pass="+pass);
        
        btnRetry.setOnClickListener(new View.OnClickListener() {			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				layoutErr.setVisibility(View.GONE);
				layoutWeb.setVisibility(View.VISIBLE);                
				if(lang.equals("vi"))
					webView.loadUrl("http://touch.biitbook.com/book/"+ bookid);
				else
					webView.loadUrl("http://touch.biitbook.me/book/"+ bookid);
			}
		});
    }
    
    //decodes image and scales it to reduce memory consumption
    private Bitmap decodeFile(File f){
        try {
            //decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f),null,o);
            
            //Find the correct scale value. It should be the power of 2.
            final int REQUIRED_SIZE=70;
            int width_tmp=o.outWidth, height_tmp=o.outHeight;
            int scale=1;
            while(true){
                if(width_tmp/2<REQUIRED_SIZE || height_tmp/2<REQUIRED_SIZE)
                    break;
                width_tmp/=2;
                height_tmp/=2;
                scale++;
            }
            
            //decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize=scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {
        	
        }
        return null;
    }
    
    public void setValue(int progress) {
        this.progress.setProgress(progress);        
    }
   
    private void updatePass(String userid, String pass){ 
    	try{
    		db.open();
	        @SuppressWarnings("unused")
			boolean idBooks;
	        idBooks = db.updatePass(userid, pass);  
	        db.close();	        
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
	
	private String returnImage(String id){
    	
        db.open();
        String path = "";
        Cursor c = db.getTitle(id);
        if (c.moveToFirst())
            path = returnImage(c);
        c.close();
        db.close();
        return path;
	}        
	public String returnImage(Cursor c){
		return c.getString(4);	                
	}
	
	private String returnAuthor(String id){    	
        db.open();
        String path = "";
        Cursor c = db.getTitle(id);
        if (c.moveToFirst())
            path = returnAuthor(c);
        c.close();
        db.close();
        return path;
	}        
	public String returnAuthor(Cursor c){
		return c.getString(3);	                
	}
	
	private String returnName(String id){
    	
        db.open();
        String path = "";
        Cursor c = db.getTitle(id);
        if (c.moveToFirst())
            path = returnName(c);
        c.close();
        db.close();
        return path;
	}        
	public String returnName(Cursor c){
		return c.getString(2);	                
	}
	
	
	 @Override
		public boolean onOptionsItemSelected(MenuItem item) {
	    	switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				return true;		
			}
			return super.onOptionsItemSelected(item);
		}
		
		@Override
		public boolean onCreateOptionsMenu(Menu menu) {
			getSupportMenuInflater().inflate(R.menu.none, menu);
			return true;
		}	
		
		@Override
		public boolean onKeyDown(int keyCode, KeyEvent event) {
			if(event.getAction() == KeyEvent.ACTION_DOWN){
				switch(keyCode){            	
	            case KeyEvent.KEYCODE_BACK:
	            	if(webView.canGoBack())
	            		webView.goBack();
	            	else
	            		finish();
				}
			}
			return true;
		}
	    
	
    private class BuyTask extends AsyncTask<String, Integer, String> {
    	
		 @Override
           protected void onPreExecute() {
              pd = new ProgressDialog(BuyBookActivity.this);
       			pd.setMessage(getResources().getString(R.string.processing));
                   pd.setIndeterminate(false);
                   pd.setCancelable(false);                                            
               if (!pd.isShowing())
                   pd.show();                
               super.onPreExecute();
           }

		 @Override
		 protected String doInBackground(String... params) {        	
       			String result  = "";
       			if(params[0].equals("0")){//0 la trial 1 la mua
       				try {
    				    Thread.sleep(1000);
    				} catch(InterruptedException ex) {
    				    Thread.currentThread().interrupt();
    				}       				       				
       				result = "0";       				
       			}else{       				
       				result = "1";
       			}
           return result;
       }
       @Override
       protected void onPostExecute(String result) {    	   
       	if(result.equals("0")){        		//0 trial
       		if (pd.isShowing())
                pd.dismiss();
       		text.setText(getResources().getString(R.string.downloaed));
        	toast.show();	
       	}
       	if(result.equals("1")==true){ //1 mua
       		if(mag == false){
 				json = postUrl.getJSONFromUrl("http://biitbook.com/api/ios/api_getuserdown.php?userid=" + userid);
 			}else{
 				json = postUrl.getJSONFromUrl("http://biitbook.com/api/ios/api_getuserdown_mag.php?userid=" + userid);
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
								if(!golbalFunc.loadSavedPreferences("KEY_PASSBOOK").equals(json_data.getString("id"))){
									golbalFunc.savePreferences("KEY_PASSBOOK", json_data.getString("id"));
									updatePass(bookid, json_data.getString("id"));
									if (pd.isShowing())
	    				                  pd.dismiss();
	    							text.setText(getResources().getString(R.string.buyed));
	    		                	toast.show();	
	    		                	finish();
								}else{
									if (pd.isShowing())
		        						   pd.dismiss();
									text.setText(getResources().getString(R.string.notbcoin));
							        toast.show();
								}
							}
						}												
		        	}else{
		        		if (pd.isShowing())
                            pd.dismiss();
               			text.setText(getResources().getString(R.string.buyerror));
	                	toast.show();
		        	}
       			} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					if (pd.isShowing())
                        pd.dismiss();
           			text.setText(getResources().getString(R.string.buyerror));
                	toast.show();
				}   				
   			}else{
   				if (pd.isShowing())
   	                pd.dismiss();
   				text.setText(getResources().getString(R.string.buyerror));
            	toast.show();
   			}
       	}       
       }
   }
}