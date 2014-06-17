package lmquan1990.biitbook;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import org.holoeverywhere.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.widget.RelativeLayout;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.astuetz.PagerSlidingTabStrip;
import com.biitbook.android.R;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
@SuppressLint("NewApi")
public class LibsActivity extends BaseActivity {
	public LibsActivity() {
		super(R.string.mylib);
	}

	DBAdapter db;
    UserDB userdb;    
	TestFragmentAdapter mAdapter;
	public static Activity fa;
    GolbalFunction postUrl;        
    String bookPath = "";
	String imagePath = "";
	String tmpPath ="";
	public String pass = "";    
    public String json= "";
    public String userid ="";
    JSONArray array;
    public JSONObject job;
    MyTask myLoad = null;
    FragmentManager manager;
	RelativeLayout layout;
	private PagerSlidingTabStrip tabs;
	private ViewPager pager;	
	private int currentColor = 0xFFF4842D;
	private boolean doubleBack = false;	
	private Timer timer;
	private TimerTask timerTask;	
	boolean del = false;
	    
		@Override
	    public boolean onOptionsItemSelected(MenuItem item) {
			switch (item.getItemId()) {
			case android.R.id.home:							
				toggle();
				return true;	
			case R.id.delete:
				Toast.makeText(this, getResources().getString(R.string.sedelete), Toast.LENGTH_SHORT).show();
				del = true;
				return true;
			}
			return true;
		}
		
		@Override
		public boolean onCreateOptionsMenu(Menu menu) {
			getSlidingMenu().showContent();
			getSupportMenuInflater().inflate(R.menu.main, menu);
			return true;
		}
	
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@SuppressLint("NewApi")
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		postUrl = new GolbalFunction();
		fa = this;		
		db = new DBAdapter(LibsActivity.this);
        userdb = new UserDB(LibsActivity.this);		
        setContentView(R.layout.activity_main);
		layout = (RelativeLayout)this.findViewById(R.id.content_frame);
		if (android.os.Build.VERSION.SDK_INT > 9) {
		    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		    StrictMode.setThreadPolicy(policy);
		}
		getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);		
		myLoad = new MyTask(); 
        myLoad.execute();
	}
		
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("currentColor", currentColor);
	}	
	
	public void switchContent(final Fragment fragment, String tag) {
		getSupportFragmentManager()
		.beginTransaction()
		.replace(R.id.content_frame, fragment,tag)
		.commit();    
	}
	
	public void updateUser(String userid, String log){ 
	    	try{
	    		userdb.open();	        
				userdb.updateUser(userid, log);  
		        userdb.close();
	    	}catch(Exception e){
	    		System.out.println("Update user: " + e.toString());
	    	}
	    }
	 
		void DeleteRecursive(File fileOrDirectory) {
			 if (fileOrDirectory.isDirectory())
			    for (File child : fileOrDirectory.listFiles())
			        DeleteRecursive(child);
			    fileOrDirectory.delete();
		}
					
		private String returnApi(String log){
			userdb.open();
	        String pass = "";
	        Cursor c = userdb.getLog(log);
	        if (c.moveToFirst())
	            pass = getApi(c);
	        c.close();
	        userdb.close();		
	        return pass;
		}
		
		public String getApi(Cursor c)
		{
			return c.getString(5);	                
		}
		
		private void insert(String id, String name, String author, String image_view,
				String file_name, String point, String cat, String book_path, String type,
				String onoff, String pass, String userid){    		
		        @SuppressWarnings("unused")
				long idBooks;
		        db.open();
		        idBooks = db.insertTitle(id,name,author,image_view,file_name,point,cat,book_path,
		        		type, onoff, pass, userid);
		        db.close();
	    }
		
	    private class MyTask extends AsyncTask<Uri, Integer, String> {
	    	
			 @Override
	           protected void onPreExecute() {				
	              super.onPreExecute();
	           }

			@Override
			protected String doInBackground(Uri... params) {			
				String x = "";						        		        
		        bookPath = postUrl.loadSavedPreferences("KEY_PATHBOOK");
				tmpPath = postUrl.loadSavedPreferences("KEY_PATHTMP"); 
				File tmp = new File(tmpPath);
				try {
					if(tmp.list().length>0)					 
						DeleteRecursive(tmp);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		        userid = postUrl.loadSavedPreferences("KEY_IDEND");	        
		        if(!userid.equals("")){
					try {	
						pass = postUrl.loadSavedPreferences("KEY_PASSEND");
				        json = returnApi("yes");
				        job = new JSONObject(json);				
						array = job.getJSONArray("result");				
						try{						
				    		db.open();
						 	db.deleteBooks("002",userid);		 	
						 	db.close();
						 }catch(Exception ex){
					        	ex.printStackTrace();
					     }
		
						if(array.length()>0){	
							for(int j = 0; j <array.length(); j++)
							{								
								JSONObject json_data = array.getJSONObject(j);
								String author ="";
				                if(json_data.getString("author").toString().equals("null"))
				                  	author =getResources().getString(R.string.updating);
				                else
				                   	author = json_data.getString("author");
				                	db.open();
				                	insert(json_data.getString("id"), 
				                			json_data.getString("name"),
				                			author, 
				                			json_data.getString("image_view"),
				                			json_data.getString("file_name"),
				                			json_data.getString("point"),
				                			"002",
				                			bookPath + json_data.getString("id"), 
				                			json_data.getString("file_name").substring(json_data.getString("file_name").lastIndexOf(".")+1, 
				             				json_data.getString("file_name").length()),
				             				"1",
				             				json_data.getString("pass"),userid);
				                	db.close();				                
							}		        		
						}	
							x = "0";
					} catch (JSONException e1) {
							x = "1";
							System.out.println("JSon: " + e1.toString());
							e1.printStackTrace();
					}
		        }
				return x;
			}
			
			
			 protected void onPostExecute(String result) {				 
				 setBehindContentView(R.layout.menu_frame); 
				 new Handler().post(new Runnable() {
			            public void run() {
			             if(!isFinishing()){
				            getSupportFragmentManager()
				   			 .beginTransaction()
				   			 .replace(R.id.menu_frame, new SampleListFragment())
				   			 .commit();
			             }
			            }
			        });
				 mAdapter = new TestFragmentAdapter(getSupportFragmentManager());
				 tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
				 pager = (ViewPager) findViewById(R.id.pager);		
				 pager.setAdapter(mAdapter);

				 final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
					.getDisplayMetrics());
				 pager.setPageMargin(pageMargin);
				 pager.setOffscreenPageLimit(3);
				 tabs.setViewPager(pager);
				 pager.setPageMarginDrawable(R.color.backforgot);
				 tabs.setDividerColor(0x00000000);					
			 }
	    }	        

	    @Override
		public boolean onKeyDown(int keyCode, KeyEvent event) {
			if(event.getAction() == KeyEvent.ACTION_DOWN){
				switch(keyCode){            	
	            case KeyEvent.KEYCODE_BACK:
	            	SlidingMenu sm = getSlidingMenu();
					if(!sm.isMenuShowing()){
		            	FragmentManager fm = getSupportFragmentManager();
		            	
		            	boolean goback = false;
		            	
		            	DiscoveryFragment disfragment = (DiscoveryFragment)fm.findFragmentByTag("DiscoveryFragment");
		            	if(disfragment!=null)
		            		goback = disfragment.WebViewGoBack();
		            	
		            	BcoinsFragment bcfragment = (BcoinsFragment)fm.findFragmentByTag("BcoinsFragment");
		            	if(bcfragment!=null)
		            		goback = bcfragment.WebViewGoBack();
		            	
		            	ProfileFragment profragment = (ProfileFragment)fm.findFragmentByTag("ProfileFragment");
		            	if(profragment!=null)
		            		goback = profragment.WebViewGoBack();
		            	
		            	if (!goback){
			            	Toast onBackToast = Toast.makeText(this, getResources().getString(R.string.backagain), Toast.LENGTH_SHORT);
		        			if (doubleBack) {
		        				onBackToast.cancel();
		        				fa = null;
		        			    finish();
		        			}else{
		        				this.doubleBack= true;			
		        				onBackToast.show();        				
		        			}      
		        			try {
		     			       timer = new Timer();
		     			       timerTask = new TimerTask() {
		     			          @Override
		     			          public void run() {
		     			        	  doubleBack = false;
		     			          }
		     			       };
		     			    timer.schedule(timerTask, 3000, 3000);
		     			    } catch (Exception e){
		     			       e.printStackTrace();
		     			    }	        			
						}
		            }
				}
	            return true;
	        }  
			return true;
		}
	    
		@Override
		protected void onRestoreInstanceState(Bundle savedInstanceState) {
			super.onRestoreInstanceState(savedInstanceState);
			currentColor = savedInstanceState.getInt("currentColor");			
		}	
		
}