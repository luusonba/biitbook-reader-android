package lmquan1990.biitbook;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import lmquan1990.biitbook.AllLibrary.MyWebRequestReceiver;
import lmquan1990.biitbook.DownloadActivity.ProgressRequestReceiver;

import org.geometerplus.android.fbreader.FBReaderApplication;
import org.holoeverywhere.widget.Toast;

import com.biitbook.android.R;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.NotificationCompat;

public class MyIntentService extends IntentService{
	
	int lenghtOfFile = 0;	  
	private NotificationManager notificationManager;
	PendingIntent contentIntent;
	int lastPercent = 0;
	NotificationCompat.Builder builder;
	GolbalFunction golbal = new GolbalFunction();
	String bookPath = "";
	public String idbook = "";
    public String url = "";
    public String name = "";
    public String userid = "";
    DowningDB dowingDB;
    URL uri;
	public static final String DONE_ID = "done.ID";
	public static final String DOWN_PER = "down.PER";
	public static final String DOWN_LASTPER = "down.LASTPER";
	public static final String DOWN_TOTAL = "down.TOTAL";
	public static final String ACTION_OPEN_BOOK = "android.fbreader.action.VIEW";
	 
	public MyIntentService(){
	   super("MyIntentService");
	   bookPath = golbal.loadSavedPreferences("KEY_PATHBOOK");
	   userid = golbal.loadSavedPreferences("KEY_IDEND");
	   dowingDB = new DowningDB(FBReaderApplication.getAppContext());	   
	}
	
	public boolean getWait(){	   
		idbook = returnDowing("1", 2);
		url = returnDowing("1", 3);
		name = returnDowing("1", 4);		
		if(idbook.equals(""))
			return false;
		else
			return true;
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
		
		 private String returnTopDowing(String downing, int num){    	
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
		 
			public String returnTopDowing(Cursor c, int num){
				return c.getString(num);	                
			}	
		 
	@SuppressWarnings("static-access")
	@Override
	protected void onHandleIntent(Intent intent) {
		builder = new NotificationCompat.Builder(getApplicationContext());
		Intent notificationIntent = new Intent(getApplicationContext(),DownloadActivity.class);
		contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);

		builder.setContentTitle(getApplicationContext().getResources().getString(R.string.downing))
		.setContentText(getApplicationContext().getResources().getString(R.string.downing))
		.setSmallIcon(R.drawable.ic_download)
		.setContentIntent(contentIntent)		
		.setOngoing(true);
		//.setProgress(0, 0, true);
		//.addAction(R.drawable.ic_cancel, getApplicationContext().getResources().getString(R.string.cancel), contentIntent);
		//http://android-er.blogspot.com/2013/03/stop-intentservice.html
		
		//notification.flags = Notification.FLAG_NO_CLEAR;
		
		notificationManager = (NotificationManager) getApplicationContext().getSystemService(getApplicationContext().NOTIFICATION_SERVICE);		    		
		notificationManager.notify(1, builder.build());
	
		File file = new File(bookPath+"biit");    		
	    if(file.exists()){
	    	@SuppressWarnings("unused")
			boolean deleted = file.delete();
	    }
	       
	    Thread download = new Thread() {
			@Override
            public void run() {					
				boolean fail = false;
				while(getWait() && fail==false){
					System.out.println("id: " + idbook + " name: " + name + " url: " + url);
					builder.setContentTitle(name);
	 		    	notificationManager.notify(1, builder.build());
					try {		    		
		    			uri = new URL(url);
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
				    	URLConnection conexion = uri.openConnection();
				    	conexion.connect();	
				    	lenghtOfFile = conexion.getContentLength()/1024;					    	
			    	} catch (Exception e) {
			    		fail = true;
			    		e.printStackTrace();
			    		url = "";
			    	}
	            	
	            	int count=0;
		     		try {		 					    	
		 		    	InputStream input = new BufferedInputStream(uri.openStream());
		 		    	OutputStream output = new FileOutputStream(bookPath+ "biit");
		 		    	Intent broadcastIntent = new Intent();
			       		broadcastIntent.setAction(ProgressRequestReceiver.PROCESS_RESPONSE);
			       		broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
			       		broadcastIntent.putExtra(DOWN_TOTAL, lenghtOfFile);
			       		sendBroadcast(broadcastIntent);
		 		    	byte data[] = new byte[1024];
		 		    	long total = 0;
		 		    
		 		    	int check = 0;
		 		    	while ((count = input.read(data)) != -1) {		 		    	
		 	    			total += count;
		 	    			int percent = (int)(total/1024);				    	
		 	    			lastPercent = (percent*100)/(lenghtOfFile);		 	    			
		 	    			if(lastPercent > check){
			 	    			builder.setContentText(percent + "/"+(lenghtOfFile) + " KB")
			 	    			.setProgress(100, lastPercent, false);
			 		    		notificationManager.notify(1, builder.build());
			 		    		
			 		    		broadcastIntent.putExtra(DOWN_PER, percent);
			 		    		broadcastIntent.putExtra(DOWN_LASTPER, lastPercent);
			 	    			sendBroadcast(broadcastIntent);
			 		    		check = lastPercent;
		 	    			}				            
		 		    		output.write(data, 0, count);
		 		    	}
		 	    		check = 0;	
		 	    		output.flush();
		 	    		output.close();
		 	    		input.close();	
		 	    	} catch (Exception e) {
		 	    		fail = true;
		 	    		e.printStackTrace();
		 	   		}		    		     	
		     		if(fail){		  
		     			Intent broadcastIntent = new Intent();
			       		broadcastIntent.setAction(MyWebRequestReceiver.PROCESS_RESPONSE);
			       		broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);		       			       		
			       		broadcastIntent.putExtra(DONE_ID, 0);
			       		sendBroadcast(broadcastIntent);
		     			notificationManager.cancel(1);
		     			Toast.makeText(getApplicationContext(), getResources().getString(R.string.downerror), Toast.LENGTH_LONG).show();
		 			}else{			       			       		
		 				File from = new File(bookPath,"biit");
			       		File to = new File(bookPath,idbook);    			       		
			       		from.renameTo(to);	
			       		Intent broadcastIntent = new Intent();
			       		broadcastIntent.setAction(MyWebRequestReceiver.PROCESS_RESPONSE);
			       		broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);		       			       		
			       		broadcastIntent.putExtra(DONE_ID, idbook);
			       		sendBroadcast(broadcastIntent);
			       		updateDowing(idbook, "0");
			       		updateDowing(returnTopDowing("2", 2), "1");
			       		builder.setContentTitle(name)
			       		.setContentText(getApplicationContext().getResources().getString(R.string.done))
			       		.setProgress(0, 0, false);
			       		notificationManager.notify(1, builder.build());		
			       		notificationManager.cancel(1);
		 			}
            }
		}
        };
		download.run();
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
}