package lmquan1990.biitbook;

import com.biitbook.android.R;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.*;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import org.holoeverywhere.widget.ProgressBar;
@SuppressLint("SetJavaScriptEnabled")
public class ForgotActivity extends BaseActivity {
    public WebView webView;
	private ProgressBar progress;
	public LinearLayout webLayout;
	public RelativeLayout errLayout;
	Button btnRetry;
	public static Activity fa;	
	public ForgotActivity() {
		super(R.string.forgot);
	}
		
    @Override
    public void onCreate(Bundle savedInstanceState) { 
        super.onCreate(savedInstanceState);
        fa = this;
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff'>"+getResources().getString(R.string.forgot)+"</font>"));
        setContentView(R.layout.main_ui);        
        getSlidingMenu().setSlidingEnabled(false);
		                
        webLayout = (LinearLayout)findViewById(R.id.layout_webview);
        errLayout = (RelativeLayout)findViewById(R.id.layout_error);
		webLayout.setVisibility(View.VISIBLE);
        
        webLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
        
        btnRetry = (Button)findViewById(R.id.retry);
        webView = (WebView) findViewById(R.id.webView);        
        webView.getSettings().setJavaScriptEnabled(true);        
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);        
        webView.requestFocus();
        progress = (ProgressBar) findViewById(R.id.progressBar);
        progress.setMax(100);
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
                ForgotActivity.this.setValue(newProgress);
                super.onProgressChanged(view, newProgress);
            }
       });
        btnRetry.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				errLayout.setVisibility(View.GONE);
				webLayout.setVisibility(View.VISIBLE);                
				webView.loadUrl("http://id.biitbook.com/forgot_password");
			}
		});
       webView.loadUrl("http://id.biitbook.com/forgot_password");               
    }
       
    public void setValue(int progress) {
        this.progress.setProgress(progress);        
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
}