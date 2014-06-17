package lmquan1990.biitbook;

import com.biitbook.android.R;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.geometerplus.android.fbreader.FBReader;
import org.holoeverywhere.widget.Toast;
import org.json.JSONObject;

import com.actionbarsherlock.app.SherlockFragment;
import com.artifex.mupdfdemo.MuPDFActivity;
import org.holoeverywhere.app.AlertDialog;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

@SuppressLint("NewApi")
public class AllLibrary extends SherlockFragment {
	
	 	static final String KEY_TAG = "bookdata";
	    static final String KEY_ID = "id";
	    static final String KEY_NAME = "name";
	    static final String KEY_AUTHOR = "author";
	    static final String KEY_IMAGE_VIEW = "image_view";
	    static final String KEY_URL = "url";
	    static final String KEY_CATO = "category_document";
	    static final String KEY_TYPE = "type";
	    static final String KEY_DOW = "dow";
	    static final String KEY_PASS = "pass";
	    static final String KEY_BOOKPATH = "bookpath";	    
	    public String book_id ="";
	    public String image_view ="";
	    public String url ="";
	    public String name ="";
	    public String author ="";
	    public String cato ="";
	    public String file_name ="";  
	    public String point ="";  
	    public String book_path ="";  
	    public String type ="";
	    public String dbuserid ="";
	    public String image_path = "";
	    public String passread = "";
	    List<EBook> ebooks;
	    public BinderGrid bindingGrid = null;
	    List<HashMap<String,String>> gbookDataCollection;
		HashMap<String,String> gmap = null;
		GridView gridview;		
		public ImageView plus;
		TextView text;
	    ProgressBar progressLoad;
	    BindTask bindTask;
	    DBAdapter db;
	    DowningDB dowingDB;
	    UserDB userdb;
	    String userid = "";
	    GolbalFunction golbal = new GolbalFunction(); 
	    public String urlDownload = "";
	    public String idbook = "";
	    public String fileext = "";
	    String bookPath = "";    
	    boolean down = false;	    
	    public static final String ACTION_OPEN_BOOK = "android.fbreader.action.VIEW";		
	    public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
	    int lenghtOfFile = 0;	  
	    private MyWebRequestReceiver receiver;
	    
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View v = inflater.inflate(R.layout.libs, container, false);
        plus = (ImageView)v.findViewById(R.id.plus);
		gridview = (GridView)v.findViewById(R.id.gridview);
		progressLoad = (ProgressBar)v.findViewById(R.id.progressLoad);
		text = (TextView)v.findViewById(R.id.txtAll);
		userid = golbal.loadSavedPreferences("KEY_IDEND");
        bookPath = golbal.loadSavedPreferences("KEY_PATHBOOK");        
        
        IntentFilter filter = new IntentFilter(MyWebRequestReceiver.PROCESS_RESPONSE);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        receiver = new MyWebRequestReceiver();
        getActivity().registerReceiver(receiver, filter);
		bindTask = new BindTask();
		bindTask.execute();
				
		gridview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {				
				idbook = gbookDataCollection.get(position).get(KEY_ID);
				name = gbookDataCollection.get(position).get(KEY_NAME);
				author = gbookDataCollection.get(position).get(KEY_AUTHOR);
				urlDownload = gbookDataCollection.get(position).get(KEY_URL);
				if(((LibsActivity)getActivity()).del == false&&down==false){
					down = true;
					System.out.println("url:  " + urlDownload);
					if(!urlDownload.equals("null") && !urlDownload.equals("")){
						fileext = urlDownload.substring(urlDownload.lastIndexOf("."), urlDownload.length());			
						
						passread = returnPassread(idbook);
						String onoff = returnOnoff(idbook);
						golbal.savePreferences("KEY_PATHOFFBOOK", gbookDataCollection.get(position).get(KEY_BOOKPATH));						
						File direct = new File(gbookDataCollection.get(position).get(KEY_BOOKPATH));						
			    		golbal.savePreferences("KEY_EXTBOOK", fileext);
			    		golbal.savePreferences("KEY_ONOFF", onoff);
						if(direct.exists()){
							golbal.savePreferences("KEY_IDBOOK", idbook);
							if(fileext.endsWith("pdf")){
								String path = "";
								if(onoff.equals("1"))
									path = golbal.loadSavedPreferences("KEY_PATHTMP")+idbook+fileext;
								else
									path = gbookDataCollection.get(position).get(KEY_BOOKPATH);
								Uri uri = Uri.parse(path);
				    			Intent intent = new Intent(getActivity(), MuPDFActivity.class);
				    			intent.setAction(Intent.ACTION_VIEW);
				    			intent.setData(uri);
				    			getActivity().startActivity(intent);
				    			getActivity().finish();
				    		}else{						         	
					    		((LibsActivity)getActivity()).startActivity(
							       		new Intent((LibsActivity)getActivity(), FBReader.class)
							       				.setAction(ACTION_OPEN_BOOK));					    		
					    		getActivity().finish();
				    		}
							down = false;
						}else{				
							if(!returnDowing(idbook, "1").equals("")){
								if(!returnDowing("1", 2).equals("")){
									down = false;
									Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.pleasewait), Toast.LENGTH_SHORT).show();
								}else{
									updateDowing(idbook, "1");
									down = false;
									AlertDialog.Builder alertDialog = new AlertDialog.Builder(((LibsActivity)getActivity()));							
									alertDialog.setTitle(getResources().getString(R.string.notice));
									alertDialog.setMessage(getResources().getString(R.string.dontexist));
									alertDialog.setPositiveButton(getResources().getString(R.string.agree),
											new DialogInterface.OnClickListener() {
												public void onClick(DialogInterface dialog,int which) {
													
									try
									{
										if(golbal.isOnline()){
											//1 dang down, 2 cho down, 0 da xong									
											if(!isMyServiceRunning()){	
												Intent intent =new Intent(getActivity(), MyIntentService.class);
												getActivity().startService(intent);
											}
										}else
											Toast.makeText(((LibsActivity)getActivity()), getResources().getString(R.string.networkerror), Toast.LENGTH_LONG).show();										
									}catch(Exception ex){
										Toast.makeText(((LibsActivity)getActivity()), getResources().getString(R.string.downerror), Toast.LENGTH_LONG).show();
										updateDowing(idbook, "2");
									}
										}
									});
				
								alertDialog.setNegativeButton(getResources().getString(R.string.cancel),
										new DialogInterface.OnClickListener() {
											public void onClick(DialogInterface dialog,	int which) {
												dialog.cancel();
											}
								});
								alertDialog.show();
								}
							}else{
								down = false;
								AlertDialog.Builder alertDialog = new AlertDialog.Builder(((LibsActivity)getActivity()));							
								alertDialog.setTitle(getResources().getString(R.string.notice));
								alertDialog.setMessage(getResources().getString(R.string.dontexist));
								alertDialog.setPositiveButton(getResources().getString(R.string.agree),
										new DialogInterface.OnClickListener() {
											public void onClick(DialogInterface dialog,int which) {
												
								try
								{
									if(golbal.isOnline()){
										//1 dang down, 2 cho down, 0 da xong									
										if(!isMyServiceRunning()){
											insertDowing(userid, idbook, urlDownload, name, author, "1");	
											Intent intent =new Intent(getActivity(), MyIntentService.class);
											getActivity().startService(intent);
										}else
											insertDowing(userid, idbook, urlDownload, name, author, "2");	
									}else
										Toast.makeText(((LibsActivity)getActivity()), getResources().getString(R.string.networkerror), Toast.LENGTH_LONG).show();										
								}catch(Exception ex){
									Toast.makeText(((LibsActivity)getActivity()), getResources().getString(R.string.downerror), Toast.LENGTH_LONG).show();
									updateDowing(idbook, "2");
								}
									}
								});
			
							alertDialog.setNegativeButton(getResources().getString(R.string.cancel),
									new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog,	int which) {
											dialog.cancel();
										}
							});
							alertDialog.show();
							}
						}			
					}else{
						down = false;
						Toast.makeText(((LibsActivity)getActivity()),getResources().getString(R.string.booknotfound), Toast.LENGTH_LONG).show();
					}
			
			}else if(((LibsActivity)getActivity()).del == true){
				((LibsActivity)getActivity()).del = false;
				AlertDialog.Builder alertDialog = new AlertDialog.Builder(((LibsActivity)getActivity()));
				alertDialog.setTitle(getResources().getString(R.string.notice));
				alertDialog.setMessage(getResources().getString(R.string.bookdelete));

				alertDialog.setPositiveButton(getResources().getString(R.string.agree),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,int which) {
								try
								{	
									String getJson = golbal.getJSONFromUrl("http://biitbook.com/api/ios/api_deletebookdown.php?userid="+userid+"&bookid="+idbook);									
									JSONObject jsonObject;										
									try {						
										if(!getJson.equals("")&&getJson!=null){
											jsonObject = new JSONObject(getJson);
											String mes = jsonObject.getString("mes");
											if(mes.equals("Thanh Cong!")==true){
												try {
													String json = golbal.getJSONFromUrl("http://biitbook.com/api/ios/api_getuserdown.php?userid=" + userid);
														if(!json.equals("")&&json!=null){
															updateJson(userid, json);		
															db.open();
															db.deleteTitle(idbook);
															db.close();
															gbookDataCollection.clear();	
															bindTask = new BindTask();
															bindTask.execute();
														}else{
															bindTask = new BindTask();
															bindTask.execute();
														}
												} catch (Exception e1) {														
													bindTask = new BindTask();
													bindTask.execute();
												}
											}
											else{            	
												Toast.makeText(((LibsActivity)getActivity()), getResources().getString(R.string.bookfail), Toast.LENGTH_LONG).show();
											}
										}else{
											Toast.makeText(((LibsActivity)getActivity()), getResources().getString(R.string.bookfail), Toast.LENGTH_LONG).show();
										}
									}catch(Exception ex){										
										Toast.makeText(((LibsActivity)getActivity()), getResources().getString(R.string.bookfail), Toast.LENGTH_LONG).show();
									}
							
								}catch(Exception ex){
									Toast.makeText(((LibsActivity)getActivity()), getResources().getString(R.string.bookfail), Toast.LENGTH_LONG).show();
								}
							}
						});
				alertDialog.setNegativeButton(getResources().getString(R.string.cancel),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,	int which) {
								dialog.cancel();
							}
						});					
				alertDialog.show();		

			}
		}		
		});		
        return v;
	}
		
	private String returnDowing(String downing, int num){    	
	     try{   
	    	dowingDB.open();
	        String path = "";
	        Cursor c = dowingDB.getDowning(downing);
	        if (c.moveToFirst())
	            path = returnDowing(c, num);
	        c.close();
	        dowingDB.close();
	        return path;
	     }catch(Exception ex){
	    	 ex.printStackTrace();
	    	 return null;
	     }
		}      
	 
		public String returnDowing(Cursor c, int num){
			return c.getString(num);	                
		}	
	
	 private String returnDowing(String idbook, String dowing){    	
	     try{   
	    	 dowingDB.open();
	    	 String path = "";
	    	 Cursor c = dowingDB.getTitle(idbook, dowing);
	    	 if (c.moveToFirst())
	            path = returnDowing(c);
	    	 c.close();
	    	 dowingDB.close();
	    	 return path;
	     }catch(Exception ex){
	    	 ex.printStackTrace();
	    	 return null;
	     }
		}      
	 
		public String returnDowing(Cursor c){
			return c.getString(6);
		}			
		
	private boolean isMyServiceRunning() {
	    @SuppressWarnings("static-access")	    
		ActivityManager manager = (ActivityManager)getActivity().getSystemService(getActivity().ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {	    	
	        if ("lmquan1990.biitbook.MyIntentService".equals(service.service.getClassName())) {
	            return true;
	        }
	    }
	    return false;
	}
	
	  private void updateDowing(String idbook, String dowing){ 
	    	try{
	    		dowingDB.open();
		        @SuppressWarnings("unused")
				boolean idBooks;
		        idBooks = dowingDB.updateDowing(idbook, dowing);  
		        dowingDB.close();	        
	    	}catch(Exception e){
	    		e.printStackTrace();
	    	}
	    }
			
	 private void insertDowing(String userid, String idbook, String url, String name, String author, String dowing){
			dowingDB.open();
	        @SuppressWarnings("unused")
			long idBooks;
	        idBooks = dowingDB.insertDowing(userid, idbook, url, name, author, dowing);  
	        dowingDB.close();
		}
	
	public class MyWebRequestReceiver extends BroadcastReceiver{	
		public static final String PROCESS_RESPONSE = "done.PROCESS_RESPONSE";		
		@Override
		public void onReceive(Context context, Intent intent) {
			try{				
				int position = 0;				
				for(int i = 0; i < ebooks.size(); i++){
		        	if(ebooks.get(i).getID().equals(intent.getStringExtra(MyIntentService.DONE_ID))){
		        		position = i;
		        	}
		        }
				System.out.println("id: " + intent.getStringExtra(MyIntentService.DONE_ID) + " position: " + position);
				View v = gridview.getChildAt(position - gridview.getFirstVisiblePosition());
		        if(v == null)
		        	return;
		        ImageView img = (ImageView)v.findViewById(R.id.imgDownload);
		        img.setVisibility(View.GONE);
		        dowingDB.open();
		        if(dowingDB.getAllEBook(userid, "2").size()<=0)
		        	getActivity().unregisterReceiver(this);
		        dowingDB.close();
			}catch(Exception ex){
				ex.printStackTrace();
				getActivity().unregisterReceiver(this);
			}
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
    		System.out.println(e.toString());
    	}
    }
	private String returnPassread(String id){
    	
        db.open();
        String path = "";
        Cursor c = db.getTitle(id);
        if (c.moveToFirst())
            path = returnPassread(c);
        c.close();
        db.close();
        return path;
	}        
	public String returnPassread(Cursor c){
		return c.getString(11);	                
	}
	
	 private String returnOnoff(String id){    	
	        db.open();
	        String path = "";
	        Cursor c = db.getTitle(id);
	        if (c.moveToFirst())
	            path = returnOnoff(c);
	        c.close();
	        db.close();
	        return path;
		}        
		public String returnOnoff(Cursor c)
		{
			return c.getString(10);	                
		}	
	
	  public class BindTask extends AsyncTask<String, String, String>{
		  @Override
          protected void onPreExecute() {			
			 progressLoad.setVisibility(View.VISIBLE);
             super.onPreExecute();
          }
		  
			@Override
			protected String doInBackground(String... params) {
				// TODO Auto-generated method stub
				String x = "";
				try {
					gbookDataCollection = new ArrayList<HashMap<String,String>>();
					db = new DBAdapter((LibsActivity)getActivity());
			        userdb = new UserDB((LibsActivity)getActivity());   
			        dowingDB = new DowningDB((LibsActivity)getActivity());
			        try{
						db.open();
						ebooks = db.getAllEBook();		
						db.close();
			        }catch(Exception ex){
			        	File book  = new File(Environment.getDataDirectory() + "/data/com.biitbook.android/databases/books");
			        	if(book.exists()){	        	
			        		book.delete();
			        	}	
			        	db = new DBAdapter(getActivity());
			        	db.open();
						ebooks = db.getAllEBook();		
						db.close();
			        }
					if(ebooks.size()>0){
						gbookDataCollection.clear();
				        for (EBook eb : ebooks) {
				        	gmap = new HashMap<String,String>();
							book_id = eb.getID();
							url = eb.getUrl();
							image_view = eb.getImage_view();
							name = eb.getName();
							author = eb.getAuthor();
							cato = eb.getCat(); 
							type = eb.getType();
							passread = eb.getPass();
							dbuserid = eb.getUserid();		
							book_path = eb.getBook_path();
								
							////////////////////////all
							if(!userid.equals("")){
								String dow = "0";
								if(eb.getOnoff().equals("1")){																	
									File f = new File(bookPath+book_id);
									if(f.exists())
										dow = "0";
									else
										dow = "1";			
								}
									
								if((dbuserid.equals(userid)||(dbuserid.equals("")||dbuserid==null))){
									gmap.put(KEY_ID, book_id);	                
				                    gmap.put(KEY_URL, url);
				                    gmap.put(KEY_IMAGE_VIEW, image_view);
				                    gmap.put(KEY_NAME, name);
				                    gmap.put(KEY_AUTHOR, author);
				                    gmap.put(KEY_CATO, cato);
				                    gmap.put(KEY_TYPE, type);
				                    gmap.put(KEY_DOW, dow);
				                    gmap.put(KEY_PASS, passread);
				                    gmap.put(KEY_BOOKPATH, book_path);
				                    gbookDataCollection.add(gmap);
								}
					        }else{
					        	if(cato.equals("000")){
					        		gmap.put(KEY_ID, book_id);	                
				                    gmap.put(KEY_URL, url);
				                    gmap.put(KEY_IMAGE_VIEW, image_view);
				                    gmap.put(KEY_NAME, name);
				                    gmap.put(KEY_AUTHOR, author);
				                    gmap.put(KEY_CATO, cato);
				                    gmap.put(KEY_TYPE, type);
				                    gmap.put(KEY_DOW, "0");
				                    gmap.put(KEY_PASS, passread);
				                    gmap.put(KEY_BOOKPATH, book_path);
				                    gbookDataCollection.add(gmap);		       
				        		}
							}							
							///////////////////
					     }
				        bindingGrid = new BinderGrid((LibsActivity)getActivity(), gbookDataCollection);
			    		x = "0";		    		
					}else if(ebooks.size()<=0){	
						x = "1";
					}
				} catch (Exception e1) {				
					x = "1";
					e1.printStackTrace();
					//System.out.println(e1.toString());
				}				
				return x;
			}
			
			public void onPostExecute(String result){	
				progressLoad.setVisibility(View.GONE);				
				if(result.equals("0")){
					if(gbookDataCollection.size()>0){
						gridview.setAdapter(bindingGrid);
						gridview.setVisibility(View.VISIBLE);
						text.setVisibility(View.GONE);
					}else{					
						gridview.setVisibility(View.GONE);
						text.setVisibility(View.VISIBLE);
					}					
				 }else{					 				 
					plus.setVisibility(View.VISIBLE);
					 plus.setOnClickListener(new View.OnClickListener() {
							public void onClick(View v) {
								// TODO Auto-generated method stub
								//Intent i = new Intent((LibsActivity)getActivity(), DiscoveryActivity.class);
								//startActivity(i);
							}
					});
				 }
			}
	    }
	  public String returnBook(String id){	    	
	        db.open();
	        String path = "";
	        Cursor c = db.getTitle(id);
	        if (c.moveToFirst())
	            path = returnPath(c);
	        c.close();
	        db.close();
	        return path;
		}        
		public String returnPath(Cursor c){
			return c.getString(8);	                
		}
}