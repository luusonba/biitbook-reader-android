package lmquan1990.biitbook;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.geometerplus.android.fbreader.FBReader;
import org.holoeverywhere.widget.Toast;
import org.json.JSONObject;

import com.actionbarsherlock.app.SherlockFragment;
import com.artifex.mupdfdemo.MuPDFActivity;
import com.biitbook.android.R;
import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.app.ProgressDialog;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RemoteViews;
import android.widget.TextView;

@SuppressLint("NewApi")
public class BiitLibrary extends SherlockFragment {
	
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
	    UserDB userdb;
	    String userid = "";
	    GolbalFunction golbal = new GolbalFunction(); 
	    public String urlDownload = "";
	    public String idbook = "";
	    public String fileext = "";
	    String bookPath = "";
	    PreDownload myTask = null;
	    DownloadTask downloadTask = null;
	    boolean down = false;
	    private ProgressDialog mProgressDialog;	    
	    public static final String ACTION_OPEN_BOOK = "android.fbreader.action.VIEW";		
	    public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
	    int lenghtOfFile = 0;	    	    
	    private NotificationManager notificationManager;
		private Notification notification;
		PendingIntent contentIntent;
		int lastPercent = 0;
	    //ProgressDialog progressBar;
	   
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View v = inflater.inflate(R.layout.libs, container, false);
        plus = (ImageView)v.findViewById(R.id.plus);
		gridview = (GridView)v.findViewById(R.id.gridview);
		progressLoad = (ProgressBar)v.findViewById(R.id.progressLoad);
		text = (TextView)v.findViewById(R.id.txtAll);
		//progressBar = new ProgressDialog(v.getContext());
		notificationManager = (NotificationManager) getActivity().getSystemService(getActivity().NOTIFICATION_SERVICE);
		userid = golbal.loadSavedPreferences("KEY_IDEND");
        bookPath = golbal.loadSavedPreferences("KEY_PATHBOOK");
		bindTask = new BindTask();
		bindTask.execute();
				
		gridview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {				
				idbook = gbookDataCollection.get(position).get(KEY_ID);
				urlDownload = gbookDataCollection.get(position).get(KEY_URL);	
				golbal.savePreferences("KEY_TITLE", gbookDataCollection.get(position).get(KEY_NAME));
				if(((LibsActivity)getActivity()).del == false){
					if(!urlDownload.equals("null") && !urlDownload.equals("")){
						fileext = urlDownload.substring(urlDownload.lastIndexOf("."), urlDownload.length());			
						
						passread = returnPassread(idbook);
						String onoff = returnOnoff(idbook);
						golbal.savePreferences("KEY_PATHOFFBOOK", gbookDataCollection.get(position).get(KEY_BOOKPATH));						
						File direct = new File(gbookDataCollection.get(position).get(KEY_BOOKPATH));	
						System.out.println("path11:  " + gbookDataCollection.get(position).get(KEY_BOOKPATH));
						if(direct.exists()){
							golbal.savePreferences("KEY_IDBOOK", idbook);
				    		golbal.savePreferences("KEY_EXTBOOK", fileext);
				    		golbal.savePreferences("KEY_ONOFF", onoff);
				    		System.out.println("id:  " + idbook + "  ext:  " + fileext + "  onoff:  " + onoff + "  path:  " + gbookDataCollection.get(position).get(KEY_BOOKPATH));
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
						}else{
							AlertDialog.Builder alertDialog = new AlertDialog.Builder(((LibsActivity)getActivity()));							
							alertDialog.setTitle(getResources().getString(R.string.notice));
							alertDialog.setMessage(getResources().getString(R.string.dontexist));
							alertDialog.setPositiveButton(getResources().getString(R.string.agree),
									new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog,int which) {
							try
							{
								if(golbal.isOnline()){								
									myTask = new PreDownload(); 
							    	myTask.execute(urlDownload);
								}else
									Toast.makeText(((LibsActivity)getActivity()), getResources().getString(R.string.networkerror), Toast.LENGTH_LONG).show();										
										
							}catch(Exception ex){
								Toast.makeText(((LibsActivity)getActivity()), getResources().getString(R.string.downerror), Toast.LENGTH_LONG).show();																								
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
					db.open();
					ebooks = db.getAllEBook();		
					db.close();
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
								
							////////////////biit
							if(!userid.equals("")){
								String dow = "0";
								if(eb.getOnoff().equals("1")){																	
									File f = new File(bookPath+book_id);
									if(f.exists())
										dow = "0";
									else
										dow = "1";			
								}								
								if(cato.equals("002")&&(dbuserid.equals(userid))||cato.equals("003")&&(dbuserid.equals(userid))){
									gmap.put(KEY_ID, book_id);	                
									gmap.put(KEY_URL, url);
									gmap.put(KEY_IMAGE_VIEW, image_view);
									gmap.put(KEY_NAME, name);
									gmap.put(KEY_AUTHOR, author);
									gmap.put(KEY_CATO, cato);
									gmap.put(KEY_TYPE, type);
									gmap.put(KEY_DOW, dow);
									gmap.put(KEY_BOOKPATH, book_path);
									gmap.put(KEY_PASS, passread);			                    
									gbookDataCollection.add(gmap);
								}
							}else{
												
							}
							///////////
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
					 //Toast.makeText((LibsActivity)getActivity(), "Không tìm thấy sách.", Toast.LENGTH_LONG).show();
					 				 
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
		
		   public class PreDownload extends AsyncTask<String, String, String> {  
		    	@Override
		    	protected void onPreExecute() {
		    		super.onPreExecute();
		    		mProgressDialog = new ProgressDialog(((LibsActivity)getActivity()));
		    		mProgressDialog.setMessage(getResources().getString(R.string.processing));
		    		mProgressDialog.setIndeterminate(false);
		    		mProgressDialog.setCancelable(true);
		    		mProgressDialog.setCanceledOnTouchOutside(false);
		    		mProgressDialog.show();
		    		mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener(){
		               public void onCancel(DialogInterface dialog) {
		            	   myTask.cancel(true);
							File file = new File(bookPath+"biit");    		
				    	    if(file.exists()){
				    	    	@SuppressWarnings("unused")
				    			boolean deleted = file.delete();
				    	    }
				    	    mProgressDialog.dismiss();			                   		
		               }
		           });					
		    		File file = new File(bookPath+"biit");    		
		    	    if(file.exists()){
		    	    	@SuppressWarnings("unused")
		    			boolean deleted = file.delete();
		    	    }
		    	}

		    	@Override
		    	protected String doInBackground(String... aurl) {		    		
		    		try {		    			
		    			URL url = new URL(aurl[0]);
						for(int i = 0; i < aurl[0].length(); i++){
							if(Character.isWhitespace(aurl[0].charAt(i))){
				                String newName = aurl[0].substring(0, i) + "%20" + aurl[0].substring(i+1, aurl[0].length());
				                aurl[0] = newName;				             
				            }
						}
				    	try {
			                @SuppressWarnings("unused")
			    			InetAddress i = InetAddress.getByName(aurl[0]);
			            } catch (Exception e1) {
			            	System.out.println("Error unknow url: "+e1.toString());
			            }
				    	URLConnection conexion = url.openConnection();
				    	conexion.connect();	
				    	lenghtOfFile = conexion.getContentLength();	
				    	down = true;
			    	} catch (Exception e) {
			    		e.printStackTrace();
			    		down = false;
			    	}		    		
			    	return aurl[0];
		    	}

				@Override
		    	protected void onPostExecute(String unused) {
		    		if(down==true){
		    			downloadTask = new DownloadTask();
		    			downloadTask.execute(unused);
		    		}else
		    			Toast.makeText(((LibsActivity)getActivity()), getResources().getString(R.string.downerror), Toast.LENGTH_LONG).show();	
		    		down = false;
		    	}
		    }		   
		   
		   @SuppressLint("SdCardPath")
		    public class DownloadTask extends AsyncTask<String, String, String> {

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
		 			progressBar.setCanceledOnTouchOutside(false);
					progressBar.setMessage(getResources().getString(R.string.downing));
					progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
					progressBar.setProgress(0);	
					progressBar.setIndeterminate(false);
					progressBar.setMax(lenghtOfFile/1024);			
					progressBar.setProgressNumberFormat("%1d/%2d KB");		 			
					progressBar.setOnCancelListener(new DialogInterface.OnCancelListener() {
						
						public void onCancel(DialogInterface dialog) {
							// TODO Auto-generated method stub
							try{
								progressBar.setProgress(0);
								progressBar.setMax(0);	
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
					if (mProgressDialog.isShowing())
	 					mProgressDialog.dismiss();
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
		     	
		 		@Override
		     	protected void onPostExecute(String unused) {
		     		if(unused.equals("1")){
		     			Toast.makeText(((LibsActivity)getActivity()), getResources().getString(R.string.downerror), Toast.LENGTH_LONG).show();
		 			}else{       		       		
		 				File from = new File(bookPath,"biit");
			       		File to = new File(bookPath,idbook);
			       		System.out.println("update:  " + idbook + "  " + bookPath+idbook);
			       		from.renameTo(to);
			       		notification.setLatestEventInfo(getActivity(), getActivity().getResources().getString(R.string.downsuccess), getActivity().getResources().getString(R.string.done), contentIntent);
						notification.flags |= Notification.FLAG_AUTO_CANCEL;
						notificationManager.notify(1, notification);
			    		//if(progressBar.isShowing())
			    			//progressBar.dismiss();
			    		Toast.makeText(((LibsActivity)getActivity()), getResources().getString(R.string.downsuccess), Toast.LENGTH_LONG).show();
			    		golbal.savePreferences("KEY_IDBOOK", idbook);
			    		golbal.savePreferences("KEY_EXTBOOK", fileext);
			    		golbal.savePreferences("KEY_ONOFF", "1");
			    		if(fileext.equals(".pdf")){			    			
			    			Uri uri = Uri.parse(golbal.loadSavedPreferences("KEY_PATHTMP")+idbook+fileext);
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
		     		}
		     	}
		    }	
}