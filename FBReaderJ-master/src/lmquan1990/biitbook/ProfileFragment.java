package lmquan1990.biitbook;

import java.util.Timer;
import java.util.TimerTask;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.biitbook.android.R;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.*;

import org.geometerplus.android.fbreader.FBReaderApplication;
import org.holoeverywhere.widget.Button;
import org.holoeverywhere.widget.ProgressBar;
import org.holoeverywhere.widget.TextView;

@SuppressLint({ "SetJavaScriptEnabled", "NewApi", "NewApi", "NewApi", "NewApi", "NewApi", "NewApi" })
public class ProfileFragment extends SherlockFragment {
  
	public WebView webView;
	public LinearLayout webLayout;
	public TextView title;
	UserDB userdb;
	public String userid = "";
	public String pass = "";
	public RelativeLayout errLayout;
	Button btnRetry;
	private ProgressBar progress;
	GolbalFunction golbal = new GolbalFunction();
	boolean done = true;
	private Timer timer;
	private TimerTask timerTask;
	
	
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
					webView.loadUrl("http://touch.biitbook.com/user/profile?userid="+userid+"&pass="+pass);					
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
	
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		userid = golbal.loadSavedPreferences("KEY_IDEND");
		pass = golbal.loadSavedPreferences("KEY_PASSEND");
		View v;
		super.onCreate(savedInstanceState);
		if(userid.equals("")){
			v = inflater.inflate(R.layout.nologin, container, false);
			Button btnLogin = (Button)v.findViewById(R.id.btnLogin_no);
        	btnLogin.setOnClickListener(new View.OnClickListener() {
				
				public void onClick(View v) {
					// TODO Auto-generated method stub
					golbal.savePreferences("KEY_RUN", "2");
					Intent i = new Intent(FBReaderApplication.getAppContext(), LoginActivity.class);
					startActivity(i);	
				}
			});
		}else{
			v = inflater.inflate(R.layout.main_ui, container, false);
			userdb = new UserDB((LibsActivity)getActivity());
			progress = (ProgressBar)v.findViewById(R.id.progressBar);
	        progress.setMax(100);
			webLayout = (LinearLayout)v.findViewById(R.id.layout_webview);
			errLayout = (RelativeLayout)v.findViewById(R.id.layout_error);
			btnRetry = (Button)v.findViewById(R.id.retry);
			webLayout.setVisibility(View.VISIBLE);      
	        
	        webView = (WebView) v.findViewById(R.id.webView);        
	        webView.getSettings().setJavaScriptEnabled(true);        
	        webView.setVerticalScrollBarEnabled(false);
	        webView.setHorizontalScrollBarEnabled(false);        
	        webView.requestFocus();
	       
	        webView.setWebViewClient(new WebViewClient(){            
	            public boolean shouldOverrideUrlLoading(final WebView view,final String url) {     
	            	progress.setVisibility(View.VISIBLE);
	            	return webView.post(new Runnable() {		            
			            public void run() {
			            	view.loadUrl(url);
			            }			         
			        });	   
	            }
	            @Override
	            public void onPageFinished(WebView view, String url) {
	            		CookieSyncManager.getInstance().sync();
	            		progress.setVisibility(View.GONE);
	                   	webLayout.setBackgroundColor(Color.parseColor("#F6F6F6"));
	                  }
	            
			            @Override
			        	public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
			                webLayout.setVisibility(View.GONE);
			                errLayout.setVisibility(View.VISIBLE);
			            }
	            
	                });
	        
	        webView.setWebChromeClient(new WebChromeClient(){
              	 @Override
                   public void onProgressChanged(WebView view, int newProgress) {
              		ProfileFragment.this.setValue(newProgress);
                       super.onProgressChanged(view, newProgress);
                   }
		});
       
               btnRetry.setOnClickListener(new View.OnClickListener() {
       			
       			public void onClick(View v) {
       				// TODO Auto-generated method stub
       				errLayout.setVisibility(View.GONE);
       				webLayout.setVisibility(View.VISIBLE);                
       				webView.loadUrl("http://touch.biitbook.com/user/profile?userid="+userid+"&pass="+pass);
       			}
       		});               
               webView.loadUrl("http://touch.biitbook.com/user/profile?userid="+userid+"&pass="+pass);		
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
	
	public void setValue(int progress) {
	    this.progress.setProgress(progress);        
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}
}