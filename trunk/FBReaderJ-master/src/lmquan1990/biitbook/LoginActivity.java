package lmquan1990.biitbook;

import com.artifex.mupdfdemo.MuPDFCore;
import com.biitbook.android.R;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;

import org.geometerplus.android.fbreader.libraryService.BookCollectionShadow;
import org.geometerplus.fbreader.book.Author;
import org.geometerplus.fbreader.book.Book;
import org.geometerplus.fbreader.book.BookUtil;
import org.geometerplus.fbreader.bookmodel.BookReadingException;
import org.geometerplus.fbreader.fbreader.FBReaderApp;
import org.geometerplus.fbreader.formats.FormatPlugin;
import org.geometerplus.fbreader.formats.PluginCollection;
import org.geometerplus.zlibrary.core.filesystem.ZLFile;
import org.geometerplus.zlibrary.core.filesystem.ZLPhysicalFile;
import org.geometerplus.zlibrary.core.image.ZLImage;
import org.geometerplus.zlibrary.core.image.ZLLoadableImage;
import org.geometerplus.zlibrary.ui.android.image.ZLAndroidImageData;
import org.geometerplus.zlibrary.ui.android.image.ZLAndroidImageManager;
import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.app.Dialog;
import org.holoeverywhere.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;

import org.holoeverywhere.widget.Button;
import org.holoeverywhere.widget.EditText;
import org.holoeverywhere.widget.TextView;
import org.holoeverywhere.widget.Toast;


@TargetApi(Build.VERSION_CODES.GINGERBREAD)
@SuppressLint("NewApi")
public class LoginActivity extends Activity{
		
	public GolbalFunction golbalFunc;
	public String userid = "";
	public String pass = "";	
	public String json = "";
	public String email = "";
	public EditText editEmail;
	public EditText editPass;
	public Button btnLogin;	
	public TextView btnSignup;	
	public TextView txtForgot;
	public TextView txtContinue;
	UserDB db;	
	JSONArray arrayBc;
	public JSONObject job;
	JSONArray array;
    private ProgressDialog pd = null;    
    public static Activity fa;    
    String run = "0";
    String bookPathAll = "";
    String inPath = "";
	boolean isSD = false;
	DBAdapter bookdb;
	String bookPath = "";
	String nameauthor = "";
	String err = "";
    FristBloodTask firstTask;
	DoubleKillTask doubleKillTask;
	Dialog dialog;
	boolean done = false;
	boolean isRun = false;
	boolean isSplash = false;
	boolean running = true;
	private FBReaderApp myFBReaderApp;	
	private volatile Book myBook;
	ArrayList<File> result;
	private MuPDFCore core;
	DowningDB dowingDB;
		
    @Override
    public void onCreate(Bundle savedInstanceState) {    	
        super.onCreate(savedInstanceState);        				
		if (android.os.Build.VERSION.SDK_INT > 9) {
		    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		   StrictMode.setThreadPolicy(policy);
		}        
		golbalFunc = new GolbalFunction();		
		if(golbalFunc.loadSavedPreferences("KEY_RUN")!=null&&!golbalFunc.loadSavedPreferences("KEY_RUN").equals(""))
	    	run = golbalFunc.loadSavedPreferences("KEY_RUN");
	    if(!run.equals("2")){
	    	setContentView(R.layout.splash);        	
			TextView scan = (TextView)this.findViewById(R.id.scan);
		    TextView tv=(TextView)findViewById(R.id.txtLogo);
		    Typeface face=Typeface.createFromAsset(getAssets(), "fonts/lobster.otf");
		    tv.setTypeface(face);
		    tv.setText("Biitbook.com");

			if(!golbalFunc.loadSavedPreferences("KEY_SCAN").equals("1")){
				result = new ArrayList<File>();
				scan.setVisibility(View.VISIBLE);					
			}			
			String lang = Locale.getDefault().getDisplayLanguage();
			if(lang.equals(getResources().getString(R.string.vi)))				
				golbalFunc.savePreferences("KEY_LANG","vi");
			else				
				golbalFunc.savePreferences("KEY_LANG","en");			
			isSplash = true;
	    }else{
	    	golbalFunc.savePreferences("KEY_CANCELLOGIN", "1");
	    }
	    firstTask = new FristBloodTask();
	    firstTask.execute();
    }        
   
    private Bitmap getCachedBitmap(String name) {
		String mCachedBitmapFilePath = bookPath+"images/"+ name.hashCode();
		File mCachedBitmapFile = new File(mCachedBitmapFilePath);
		Bitmap lq = null;
		try {
			if (mCachedBitmapFile.exists() && mCachedBitmapFile.canRead()) {
				lq = BitmapFactory.decodeFile(mCachedBitmapFilePath);
				return lq;
			}
		} catch (Exception e) {
			e.printStackTrace();
			// some error with cached file,
			// delete the file and get rid of bitmap
			mCachedBitmapFile.delete();
			lq = null;
		}
		if (lq == null) {
			lq = Bitmap.createBitmap(200, 300,
					Bitmap.Config.ARGB_8888);
			core.drawSinglePage(-2, lq, 200, 300);
			try {
				lq.compress(CompressFormat.JPEG, 50, new FileOutputStream(
						mCachedBitmapFile));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				mCachedBitmapFile.delete();
			}
		}
		return lq;
	}
    
    private static void copyAssetFiles(InputStream in, OutputStream out) {
		try {

			byte[] buffer = new byte[1024];
			int read;

			while ((read = in.read(buffer)) != -1) {
				out.write(buffer, 0, read);
			}

			in.close();
			in = null;
			out.flush();
			out.close();
			out = null;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
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
		
    public ArrayList<File> walkdir(String dir) {    	
    	File directory = new File(dir);    	    	
    	File listFile[] = directory.listFiles();    	
    	if (listFile != null) {
    		int i = 0;
    		while(i < listFile.length && running == true){
    	        if (listFile[i].isDirectory()) {
    	            walkdir(listFile[i].getPath());
    	        } else {
    	          if (listFile[i].getName().endsWith(".epub")||listFile[i].getName().endsWith(".doc")||listFile[i].getName().endsWith(".rtf")||listFile[i].getName().endsWith(".pdf")){
    	        	  result.add(listFile[i]);    	        	  
    	          }
    	        }
    	        i = i+1;
    		}
    	}
    	return result;
    }
       
	
    public Book getBookByFile(ZLFile bookFile) {
		if (bookFile == null) {
			return null;
		}
		final FormatPlugin plugin = PluginCollection.Instance().getPlugin(bookFile);
		if (plugin == null) {
			return null;
		}
		try {
			bookFile = plugin.realBookFile(bookFile);
		} catch (BookReadingException e) {
			return null;			
		}

		Map<ZLFile,Book> myBooksByFile =
				Collections.synchronizedMap(new LinkedHashMap<ZLFile,Book>());
		Book book = myBooksByFile.get(bookFile);
		if (book != null) {
			return book;
		}

		final ZLPhysicalFile physicalFile = bookFile.getPhysicalFile();
		if (physicalFile != null && !physicalFile.exists()) {
			return null;
		}

	
		try {
			book = new Book(bookFile);
		} catch (BookReadingException e) {
			return null;
		}
		
		return book;
	}

    
    private String returnPass(String id){
    	try{
	        db.open();
	        String pass = "";
	        Cursor c = db.getTitle(id);
	        if (c.moveToFirst())
	            pass = getPass(c);
	        
	        db.close();
	        c.close();
	        return pass;
    	}catch(Exception e){
    		e.printStackTrace();	
    		return null;
    	}
	}
        
	
    public String getPass(Cursor c){
		return c.getString(2);	                
	}
    

    private void insert(String userid, String email, String pass, String log, String api, String jsondatao){ 
    	try{
    		db.open();
	        @SuppressWarnings("unused")
			long idBooks;
	        idBooks = db.insertUser(userid,email,pass,log,api, jsondatao);  
	        db.close();	        
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
  
 
	@Override
	protected void onDestroy(){	    
	    if(db != null){
	    	db.close();
	    }
	    super.onDestroy();
	    
	}
  
	
	private void update(String userid, String log){ 
    	try{
    		db.open();
	        @SuppressWarnings("unused")
			boolean idBooks;
	        idBooks = db.updateUser(userid, log);  
	        db.close();	        
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
		
	private void updateJson(String userid, String json){ 
    	try{
    		db.open();
	        @SuppressWarnings("unused")
			boolean idBooks;
	        idBooks = db.updateJson(userid, json);  
	        db.close();
    	}catch(Exception e){
    		e.printStackTrace(); 		
    	}
    }	
	
	private void updateJsondatao(String userid, String jsondatao){ 
    	try{
    		db.open();
	        @SuppressWarnings("unused")
			boolean idBooks;
	        idBooks = db.updateDatao(userid, jsondatao);  
	        db.close();
    	}catch(Exception e){
    		e.printStackTrace(); 		
    	}
    }		
				
	private void insertBook(String id, String name, String author, String image_view, String file_name, String point, 
			String cat, String book_path, String type, String onoff, String pass, String userid){
		bookdb.open();
        @SuppressWarnings("unused")
		long idBooks;
        idBooks = bookdb.insertTitle(id,name,author,image_view,file_name,point,cat,book_path, type, onoff, pass, userid);  
        bookdb.close();
	}
		
	private String checkUser(String log){
		try{
	        db.open();
	        String id = "";
	        Cursor c = db.getLog(log);
	        if (c.moveToFirst())
	            id = returnId(c);
	        c.close();
	        db.close();
	        return id;
		}catch(Exception e){
			e.printStackTrace();
    		return null;
    	}
	}        
	public String returnId(Cursor c)
	{
		return c.getString(1);	                
	}
	
	private String returnUser(String id){
		try{
	        db.open();
	        String pass = "";
	        Cursor c = db.getTitle(id);
	        if (c.moveToFirst())
	            pass = getUser(c);
	        c.close();
	        db.close();
	        return pass;
		}catch(Exception e){
			e.printStackTrace();	
    		return null;
    	}
	}
	
	public String getUser(Cursor c)
	{
		return c.getString(3);	                
	}
		
	private String returnDatao(String id){
		try{
	        db.open();
	        String pass = "";
	        Cursor c = db.getTitle(id);
	        if (c.moveToFirst())
	            pass = getDatao(c);
	        c.close();
	        db.close();
	        return pass;
		}catch(Exception e){
			e.printStackTrace();	
    		return null;
    	}
	}
	
	public String getDatao(Cursor c)
	{
		return c.getString(6);	                
	}
		
	public String getApi(Cursor c)
	{
		return c.getString(5);	                
	}
		
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(event.getAction() == KeyEvent.ACTION_DOWN){
            switch(keyCode){
            case KeyEvent.KEYCODE_BACK:            	
            	if(isSplash==false){
    	            isRun = true;
    				doubleKillTask = new DoubleKillTask();
    				doubleKillTask.execute();                	
            	}
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
	
	private class MyTask extends AsyncTask<Uri, Integer, String> {
		
		 @Override
	       protected void onPreExecute() {	                          
	           super.onPreExecute();
	       }
		 
		 @Override
	 		protected String doInBackground(Uri... params) {        	
			String result  = "";
		 	json = golbalFunc.getJSONFromUrl(params[0].toString());        	
		 	if(golbalFunc.loadSavedPreferences("KEY_CANCELLOGIN").equals("0")){
		 		golbalFunc.savePreferences("KEY_CANCELLOGIN", "1");
		 		result = "4";
		 	}else{
		 	if(json.equals("")|| (json == null))
					json = "{\"err\":\"1\"}";	
				JSONObject jsonObject;
				try{
					jsonObject = new JSONObject(json);
					err = jsonObject.getString("err");						
					if(err.equals("0")==true){   	
						JSONObject data = jsonObject.getJSONObject("data");
						userid = data.getString("userid");							
						if(golbalFunc.loadSavedPreferences("KEY_NAME").equals("")){
							nameauthor = data.getString("first_name") + " " + data.getString("last_name");
							golbalFunc.savePreferences("KEY_NAME", nameauthor);
						}			        	 	
		        	 	golbalFunc.savePreferences("KEY_BC", data.getString("point"));
						
		        	 	json = golbalFunc.getJSONFromUrl("http://biitbook.com/api/ios/api_getuserdown.php?userid=" + userid);
		        	 	if(!json.equals("")){					            		
				         	result = "0";
				         	if(returnPass(userid).length()==0){			            		
								insert(userid, email, pass,"yes", json,"");									
					        }else{									
								update(userid,"yes");
								updateJson(userid, json);
					        }
				        }else{
				            result = "1";					            
				        }
				        if(result.equals("0")){
				        	job = new JSONObject(json);				
							array = job.getJSONArray("result");				
							golbalFunc.savePreferences("KEY_BOOK", array.length()+"");
				        	golbalFunc.savePreferences("KEY_IDEND", userid);
							golbalFunc.savePreferences("KEY_EMAILEND", email);
							golbalFunc.savePreferences("KEY_PASSEND", pass);
							golbalFunc.savePreferences("KEY_RUN", "1");
				        }					       
					}else if(err.equals("1")==true){
						result = "1";
					}else if(err.equals("2")==true)
							result = "2";
				}catch (Exception e1) {
					if(err.equals("0"))
						result = "0";
					else
						result = "1";
					e1.printStackTrace();
				}	
				try {
					
					String jsonmag = "";
	    			jsonmag = golbalFunc.getJSONFromUrlGate("http://biitbook.com/api/ios/api_getuserdown_mag.php?userid=" + userid); //cai nay cung dua qua lib
	    			
	    			if(!jsonmag.equals("")&&jsonmag.length()>90){		
	    				updateJsondatao(userid, jsonmag);			    				
	    				jsonmag = returnDatao(userid);			    				
	    				job = new JSONObject(jsonmag);			    				
	    				try{
	    					bookdb.open();
		    			 	bookdb.deleteBooks("003",userid);		 	
		    			 	bookdb.close();
							array = job.getJSONArray("result");						
		    			}catch(Exception ex){
		    				ex.printStackTrace();
		    				array = null;
		    			}
						
						if(array!=null){
							if(array.length()>0){		    				
								for(int j = 0; j <array.length(); j++)
								{
									JSONObject json_data1 = array.getJSONObject(j);		
									String namemag = "";
									if(json_data1.getString("author").equals(""))
										namemag = getResources().getString(R.string.updating);
									else
										namemag = json_data1.getString("author"); 
										insertBook(json_data1.getString("id"), 
													json_data1.getString("title"),
													namemag, 
													json_data1.getString("image_view"),
													json_data1.getString("url_file"),
													json_data1.getString("point"),  
													"003", 
													bookPath + "books/"+json_data1.getString("id"), 
													json_data1.getString("url_file").substring(json_data1.getString("url_file").lastIndexOf(".")+1,json_data1.getString("url_file").length()), 
													"1",
													json_data1.getString("pass"),
													userid);							    			                   		
								}		        		
							}
							golbalFunc.savePreferences("KEY_MAG", array.length()+"");
						}
				}
					
					String jsondatao = "";
		    		jsondatao = golbalFunc.getJSONFromUrlGate("http://biitbook.com/api/ios/api_userdatao.php?user_id=" + userid);
		    		
		    		if(!jsondatao.equals("")&&jsondatao.length()>90){	
		    			updateJsondatao(userid, jsondatao);			    				
		    			jsondatao = returnDatao(userid);
		    			job = new JSONObject(jsondatao);	
		    			array = job.getJSONArray("result");
						bookdb.open();
			    		bookdb.deleteBooks("001",userid);		 	
			    		bookdb.close();
						if(array!=null){									
				   			if(array.length()>0){	
								for(int j = 0; j <array.length(); j++){				
									JSONObject json_data1 = array.getJSONObject(j);
									insertBook(json_data1.getString("ten_sach").hashCode()+"", 
											json_data1.getString("ten_sach"), 
											nameauthor, 
											json_data1.getString("image_url"), 
											json_data1.getString("file_url"), 
											"Free with me", 
											"001",  
											bookPath+"books/"+json_data1.getString("ten_sach").hashCode()+"",
											json_data1.getString("file_url").substring(json_data1.getString("file_url").lastIndexOf(".")+1,json_data1.getString("file_url").length()),
											"1",
											"1",
											userid);																    			                   		
								}		        		
							}
						}
		    		}
				}catch (Exception e1) {												
					e1.printStackTrace();
				}
		 	}
			return result;
		 }

		 @Override
		 protected void onPostExecute(String result) {		   
			 if(result.equals("0")){						
				 if (pd.isShowing())
	               pd.dismiss();
				 Toast.makeText(LoginActivity.this, getResources().getString(R.string.loginsuccess), Toast.LENGTH_LONG).show();				 
				 Intent i = new Intent(LoginActivity.this, LibsActivity.class);      						
				 startActivity(i);								
				 finish();
			 }if(result.equals("1")==true){
				if (pd.isShowing()){
	               pd.dismiss();
				}
				AlertDialog.Builder builder1 = new AlertDialog.Builder(LoginActivity.this);
			    builder1.setTitle(getResources().getString(R.string.notice));
			    builder1.setMessage(getResources().getString(R.string.networkerror));
			    builder1.setCancelable(true);
			    builder1.setNeutralButton(android.R.string.ok,
			            new DialogInterface.OnClickListener() {
			        public void onClick(DialogInterface dialog, int id) {
			            dialog.cancel();
			        }
			    });

			    AlertDialog alert11 = builder1.create();
			    if(alert11 != null)
			    	alert11.dismiss();
			    alert11.show();
			 }else if(result.equals("2")==true){
				if (pd.isShowing())
	               pd.dismiss();
				AlertDialog.Builder builder1 = new AlertDialog.Builder(LoginActivity.this);
			    builder1.setTitle(getResources().getString(R.string.notice));
			    builder1.setMessage(getResources().getString(R.string.incorrect));
			    builder1.setCancelable(true);
			    
			    builder1.setNeutralButton(android.R.string.ok,
			            new DialogInterface.OnClickListener() {
			        public void onClick(DialogInterface dialog, int id) {
			            dialog.cancel();
			        }
			    });
			    AlertDialog alert11 = builder1.create();
			    if(alert11 != null)
			    	alert11.dismiss();
			    alert11.show();
	      	}else if(result.equals("4")){
	      		if (pd.isShowing())
		               pd.dismiss();
	      	}
			 btnLogin.setEnabled(true);
	   }
	}
	
	private class DoubleKillTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			
			if(isRun == false){
				if(!golbalFunc.loadSavedPreferences("KEY_SCAN").equals("1")){
					try{
			    		bookdb.open();
			    		bookdb.deleteBooks("000", "");
					 	bookdb.getAllEBook();					 						 	
					 	db.open();
					 	checkUser("yes");
					 	db.close();
					 	//File book  = new File(Environment.getDataDirectory() + "/data/com.biitbook.android/databases/books");
			        	//if(book.exists()){	        	
			        		//book.delete();
			        	//}					        	
					 	bookdb.close();	
					 }catch(Exception ex){
						 ex.printStackTrace();
						 	File user  = new File(Environment.getDataDirectory() + "/data/com.biitbook.android/databases/login");
				        	if(user.exists()){	        	
				        		user.delete();
				        	}
				        	db = new UserDB(LoginActivity.this);
				        	File book  = new File(Environment.getDataDirectory() + "/data/com.biitbook.android/databases/book");
				        	if(book.exists()){	        	
				        		book.delete();
				        	}
				        	bookdb = new DBAdapter(LoginActivity.this);
				    }
					ArrayList<String> listbook = new ArrayList<String>();
					ArrayList<String> listname = new ArrayList<String>();
					if(isSD){
						ArrayList<File> listfiles = walkdir(bookPathAll);
						for(int i = 0; i < listfiles.size(); i++){					
							if(!listname.contains(listfiles.get(i).getName().toString())){
								listbook.add(listfiles.get(i).toString());
								listname.add(listfiles.get(i).getName().toString());						
							}							
						}							
						ArrayList<File> inlistfiles = walkdir(inPath);
						for(int i = 0; i < inlistfiles.size(); i++){
							if(!listname.contains(inlistfiles.get(i).getName().toString())){
								listbook.add(inlistfiles.get(i).toString());
								listname.add(inlistfiles.get(i).getName().toString());
							}
						}			
					}else{				
						ArrayList<File> inlistfiles = walkdir(inPath);
						for(int i = 0; i < inlistfiles.size(); i++){		
							if(!listname.contains(inlistfiles.get(i).getName().toString())){
								listbook.add(inlistfiles.get(i).toString());
								listname.add(inlistfiles.get(i).getName().toString());
							}
						}
					}							 		 
						 	 
						for(int i = 0; i < listbook.size(); i++){					
								String name = listname.get(i).toString();								
								String ext = name.substring(name.lastIndexOf(".")+1, name.length());
								String book_path = listbook.get(i).toString();				
								if(ext.equals("epub")){							
									try{
									myBook = getBookByFile(ZLFile.createFileByPath(book_path));					
									final StringBuilder buffer = new StringBuilder();
									final List<Author> authors = myBook.authors();
									for (Author a : authors) {
										if (buffer.length() > 0) {
											buffer.append(", ");
										}
										buffer.append(a.DisplayName);
									}			
									final DisplayMetrics metrics = new DisplayMetrics();
									getWindowManager().getDefaultDisplay().getMetrics(metrics);
			
									final int maxHeight = metrics.heightPixels * 2 / 3;
									final int maxWidth = maxHeight * 2 / 3;
									final ZLImage image = BookUtil.getCover(myBook);
			
									if (image == null) {
										return null;
									}
			
									if (image instanceof ZLLoadableImage) {
										final ZLLoadableImage loadableImage = (ZLLoadableImage)image;
										if (!loadableImage.isSynchronized()) {
											loadableImage.synchronize();
										}
									}
									final ZLAndroidImageData data =
										((ZLAndroidImageManager)ZLAndroidImageManager.Instance()).getImageData(image);
									if (data == null) {
										return null;
									}
			
									final Bitmap coverBitmap = data.getBitmap(2 * maxWidth, 2 * maxHeight);
									if (coverBitmap != null) {
										try {
										       FileOutputStream out = new FileOutputStream(bookPath+"images/"+name.hashCode());
										       coverBitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
										       out.close();
										} catch (Exception e) {
										       e.printStackTrace();
										}
										bookdb.open();
										insertBook(name.hashCode() + "", 
												myBook.getTitle(), 
												buffer.toString(),
												bookPath+"images/"+name.hashCode(), 
												name, 
												"0",
												"000",
												book_path,
												ext, "0", "1",
												userid);
										bookdb.close();
									}else{
										bookdb.open();
										insertBook(name.hashCode() + "",
												myBook.getTitle(),
												buffer.toString(),
												"NOIMAGEBOOK" + name.hashCode(),
												name, "0", "000",
												book_path, 
												ext, "0", "1",
												userid);
										bookdb.close();
									}	
									}catch(Exception ex){
										ex.printStackTrace();								
									}
									
								}if(ext.equals("pdf")){
									bookdb.open();
									try {
										core = new MuPDFCore(LoginActivity.this, book_path);
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									Bitmap bit = getCachedBitmap(name);
									if(bit!=null){
										insertBook(name.hashCode() + "", 
												name.substring(0, name.lastIndexOf(".")), 
												"No name", 
												bookPath+"images/"+name.hashCode(),
												name, "0", 
												"000", book_path, 
												ext, "0", "1",userid);
									}
									else{
										insertBook(name.hashCode() + "", myBook.getTitle(), "No name",
												"NOIMAGEBOOK" + name.hashCode(), name, "0",
												"000", book_path,  ext, "0", "1",userid);
									}
									bookdb.close();
								}else if(ext.equals("doc")||ext.equals("rtf")){	
									bookdb.open();
									insertBook(name.hashCode() + "",
											name.substring(0, name.lastIndexOf(".")), 
											"No name", "NOIMAGEBOOK" + name.hashCode(), name, "0", 
											"000", book_path, 
											ext, "0", "1",userid);
									bookdb.close();
								}
							}
						if(golbalFunc.loadSavedPreferences("KEY_SCAN").equals("")){
							golbalFunc.savePreferences("KEY_SCAN", "1");
						}
				}
				}			
			isRun = false;
			return null;        	
		}

		protected void onPostExecute(Void result) {	
			if(run.equals("1")){
				new MyTaskGate().execute();
			}if(run.equals("0")){
				Intent i = new Intent(LoginActivity.this, LibsActivity.class);       		
				startActivity(i);
				finish();	
			} 
			done = true;						 
		}

	}
	
	private class FristBloodTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			if(run.equals("2")){
				pd = new ProgressDialog(LoginActivity.this);
				pd.setMessage(getResources().getString(R.string.signining));
				pd.setIndeterminate(false);
				pd.setCancelable(true);
				pd.setCanceledOnTouchOutside(false);
				pd.setOnCancelListener(new DialogInterface.OnCancelListener(){
					public void onCancel(DialogInterface dialog) {
						golbalFunc.savePreferences("KEY_CANCELLOGIN", "0");			                   		
					}
				});
			}		
		}

		@Override
		protected Void doInBackground(Void... params) {
			db = new UserDB(LoginActivity.this);
			bookdb = new DBAdapter(LoginActivity.this);
			dowingDB = new DowningDB(LoginActivity.this);
			if(userid==null)
				userid = "";
	        fa = LoginActivity.this;
	        golbalFunc.actionFinish();  
	        myFBReaderApp = (FBReaderApp)FBReaderApp.Instance();
			if (myFBReaderApp == null) {
				myFBReaderApp = new FBReaderApp(new BookCollectionShadow());
			}
			myBook = null;
	        isSD = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
	        String tmpPath = "";
	        tmpPath = Environment.getDataDirectory() + "/data/com.biitbook.android/tmps/";
			golbalFunc.savePreferences("KEY_PATHTMP", tmpPath);
			if(!run.equals("2"))
				updateDowing(returnDowing("1", 2), "2");
			
				if(isSD){
					bookPath = Environment.getExternalStorageDirectory() + "/Android/data/com.biitbook.android/";
					golbalFunc.savePreferences("KEY_PATHBOOK", bookPath + "books/");
					bookPathAll = Environment.getExternalStorageDirectory()+"";
					inPath = Environment.getDataDirectory()+"/data/com.biitbook.android/";
				}
				else{
					bookPath = Environment.getDataDirectory() + "/data/com.biitbook.android/";
					golbalFunc.savePreferences("KEY_PATHBOOK", bookPath + "books/");
					inPath = Environment.getDataDirectory()+"/data/com.biitbook.android/";
				}
				
		    	File bookSD = new File(bookPath + "books/");
		    	if(!bookSD.exists()){
		        	bookSD.mkdirs();            	
		        }
		    	File imageSD = new File(bookPath+"images/");
		    	if(!imageSD.exists()){
		        	imageSD.mkdirs();            	
		        }
		    	File tmpSD = new File(tmpPath);
		    	if(!tmpSD.exists()){
		    		tmpSD.mkdirs();            	
		        }
				
		    	if(tmpSD.list().length>0){  
					 try {
						 DeleteRecursive(tmpSD);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
				 }   
				 /**
				 File f = new File(bookPath + "books/Sample.epub");
				 if(!f.exists()){
					 try {
							AssetManager assetFiles = getAssets();
							String[] files = assetFiles.list("samples");
		
							InputStream in = null;
							OutputStream out = null;
		
							for (int i = 0; i < files.length; i++) {
		
								if (files[i].toString().equalsIgnoreCase("images")
										|| files[i].toString().equalsIgnoreCase("js")) {
								
								}else{								
										in = assetFiles.open("samples/" + files[i]);							
										out = new FileOutputStream(bookPath + "books/" + files[i]);						
										copyAssetFiles(in, out);
								}
							}
					} catch (Exception e) {
						e.printStackTrace();
					} 	
				 }*/
				 
				File f = new File(bookPath + "images/" + "NOIMAGEBOOK".hashCode());				
				 if(!f.exists()){
					 try {
							AssetManager assetFiles = getAssets();
							String[] files = assetFiles.list("thumbnail");		
							InputStream in = null;
							OutputStream out = null;
							for (int i = 0; i < files.length; i++) {								
								if (files[i].toString().equals("noimage.PNG")) {									
									in = assetFiles.open("thumbnail/" + files[i]);							
									out = new FileOutputStream(bookPath + "images/" + "NOIMAGEBOOK".hashCode());						
									copyAssetFiles(in, out);
								}
							}
					} catch (Exception e) {
						e.printStackTrace();
					} 	
				 }
	        //}
				 
			if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) { 
	            finish(); 
	            return null; 
	        }
			return null;        	
		}
		
		protected void onPostExecute(Void result) {
				if(run.equals("1")){
		        	editEmail = (EditText)findViewById(R.id.txtEmail);
		            editPass = (EditText)findViewById(R.id.txtPassword);
		        	doubleKillTask = new DoubleKillTask();
					doubleKillTask.execute();
				}else if(run.equals("0")){
					try{
						doubleKillTask = new DoubleKillTask();
						doubleKillTask.execute();
					}catch(Exception e){
						e.printStackTrace();
						Toast.makeText(LoginActivity.this, getResources().getString(R.string.apperror), Toast.LENGTH_LONG).show();
						golbalFunc.actionFinish();  
			            finish();
					}						
				}else if(run.equals("2")){					
					try{
						golbalFunc.savePreferences("KEY_RUN", "0");		
						run = golbalFunc.loadSavedPreferences("KEY_RUN");
						isRun = true;
						setContentView(R.layout.login);						
					}catch(Exception ex){						
						ex.printStackTrace();	
						golbalFunc.actionFinish();  
				        finish();
				}   
				
		    try{
		    	declareControl();
		    	editPass.setOnEditorActionListener(new EditText.OnEditorActionListener() {	      	

					public boolean onEditorAction(android.widget.TextView v,
							int actionId, KeyEvent event) {
						
						// TODO Auto-generated method stub
						if (actionId == EditorInfo.IME_ACTION_DONE) {
							performLogin();            				
						}
					return false;
				}});
		    	
		    	txtContinue.setOnClickListener(new View.OnClickListener() {					
					public void onClick(View v) {						
						// TODO Auto-generated method stub
						isRun = true;
						doubleKillTask = new DoubleKillTask();
						doubleKillTask.execute();
					}
				});
		    	
		    	btnLogin.setOnClickListener(new View.OnClickListener() {					
					public void onClick(View v) {	
						
						performLogin();
					}
				});
		        
		        txtForgot.setOnClickListener(new View.OnClickListener() {					
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent i = new Intent(LoginActivity.this, ForgotActivity.class);
						startActivity(i);
					}
				});
		        
		        btnSignup.setOnClickListener(new View.OnClickListener() {					
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent i = new Intent(LoginActivity.this, SignupActivity.class);
						startActivity(i);
					}
				});
		    }catch(Exception ex){		    	
		    	ex.printStackTrace();
		    	golbalFunc.actionFinish();  
	            finish();				
		    }		
		  }			
		}
	}
	
	private void declareControl(){
		txtForgot = (TextView)findViewById(R.id.txtForgot);					
		editEmail = (EditText)findViewById(R.id.txtEmail);
	    editPass = (EditText)findViewById(R.id.txtPassword);
	    btnLogin = (Button)findViewById(R.id.btnLogin);
	    btnSignup = (TextView)findViewById(R.id.btnSignup);
	    txtContinue = (TextView)findViewById(R.id.btnContinue);
	    editEmail.setText(golbalFunc.loadSavedPreferences("KEY_EMAILEND"));
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
		
	private void performLogin(){
		btnLogin.setEnabled(false);
		if(golbalFunc.isOnline()==true){			
			email = editEmail.getText().toString();
			pass = editPass.getText().toString();
			if(email.equals("")&& !pass.equals("")){
				Toast.makeText(LoginActivity.this,getResources().getString(R.string.enteremail) , Toast.LENGTH_LONG).show();
				btnLogin.setEnabled(true);
			}
			else if(!email.equals("") && pass.equals("")){
				Toast.makeText(LoginActivity.this, getResources().getString(R.string.enterpass), Toast.LENGTH_LONG).show();
				btnLogin.setEnabled(true);
			}
			else if(email.equals("") && pass.equals("")){
				Toast.makeText(LoginActivity.this, getResources().getString(R.string.enterboth), Toast.LENGTH_LONG).show();
				btnLogin.setEnabled(true);
			}
			else if(!email.equals("") && !pass.equals("")){
				boolean space = false;
				for(int i = 0; i < email.length(); i++){
					if(Character.isWhitespace(email.charAt(i))){
		                space = true;
		            }
				}    					
				for(int i = 0; i < pass.length(); i++){
					if(Character.isWhitespace(pass.charAt(i))){
		                space = true;
		            }
				}
				if(space == false){                                  
			        if (!pd.isShowing())
			            pd.show();			
			        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);		
					imm.hideSoftInputFromWindow(editPass.getWindowToken(), 0);
					imm.hideSoftInputFromWindow(editEmail.getWindowToken(), 0);	    							
					new MyTask().execute(Uri.parse("http://biitbook.com/users/api/login?email="+email+"&password="+pass));					
				}else{	
					Toast.makeText(LoginActivity.this,getResources().getString(R.string.space), Toast.LENGTH_LONG).show();
					btnLogin.setEnabled(true);
				}
			} 
		}else{
			Toast.makeText(LoginActivity.this, getResources().getString(R.string.networkerror), Toast.LENGTH_LONG).show();
			btnLogin.setEnabled(true);
		}
	}
			
	void DeleteRecursive(File fileOrDirectory) {
		 if (fileOrDirectory.isDirectory())
		    for (File child : fileOrDirectory.listFiles())
		        DeleteRecursive(child);
		    fileOrDirectory.delete();
	}
	
	private class MyTaskGate extends AsyncTask<Uri, Integer, String> {
        @Override
        protected String doInBackground(Uri... params) {
        	String result  = "";
        	try{        		
        		userid = golbalFunc.loadSavedPreferences("KEY_IDEND");
            	email = returnPass(userid);
            	pass = returnUser(userid);            		
            	if(golbalFunc.isOnline()==true){
            		json = golbalFunc.getJSONFromUrlGate("http://biitbook.com/users/api/login?email="+email+"&password="+pass);
            		
    				if(json.equals(""))
    					json = "{\"err\":\"1\"}";
    				JSONObject jsonObject;    
    				try {
    					jsonObject = new JSONObject(json);				
    					String err = jsonObject.getString("err");
    					
    					if(err.equals("0")==true)
    					{            	
    						JSONObject data = jsonObject.getJSONObject("data");
    						if(golbalFunc.loadSavedPreferences("KEY_NAME").equals("")){
								nameauthor = data.getString("first_name") + " " + data.getString("last_name");
								golbalFunc.savePreferences("KEY_NAME", nameauthor);
							}        						
							userid = data.getString("userid");
							golbalFunc.savePreferences("KEY_BC", data.getString("point"));
							json = golbalFunc.getJSONFromUrlGate("http://biitbook.com/api/ios/api_getuserdown.php?userid=" + userid);  //cai nay cung dua qua lib
							if(!json.equals("")){					
								updateJson(userid, json);	
								job = new JSONObject(json);				
								array = job.getJSONArray("result");				
								golbalFunc.savePreferences("KEY_BOOK", array.length()+"");
			    			}
							
							
							String jsonmag = "";
			    			jsonmag = golbalFunc.getJSONFromUrlGate("http://biitbook.com/api/ios/api_getuserdown_mag.php?userid=" + userid); //cai nay cung dua qua lib
			    			
			    			if(!jsonmag.equals("")&&jsonmag.length()>90){		
			    				updateJsondatao(userid, jsonmag);			    				
			    				jsonmag = returnDatao(userid);			    				
			    				job = new JSONObject(jsonmag);			    				
			    				try{
			    					bookdb.open();
				    			 	bookdb.deleteBooks("003",userid);		 	
				    			 	bookdb.close();
									array = job.getJSONArray("result");						
				    			}catch(Exception ex){
				    				ex.printStackTrace();
				    				array = null;
				    			}
								
								if(array!=null){
									if(array.length()>0){		    				
										for(int j = 0; j <array.length(); j++)
										{
											JSONObject json_data1 = array.getJSONObject(j);		
											String namemag = "";
											if(json_data1.getString("author").isEmpty())
												namemag = getResources().getString(R.string.updating);
											else
												namemag = json_data1.getString("author"); 
											insertBook(json_data1.getString("id"),
													json_data1.getString("title"), 
													namemag, 
													json_data1.getString("image_view"), 
													json_data1.getString("url_file"), 
													json_data1.getString("point"),													 
													"003", 
													bookPath + "books/"+json_data1.getString("id"),
													json_data1.getString("url_file").substring(json_data1.getString("url_file").lastIndexOf(".")+1,json_data1.getString("url_file").length()), 
													"1",
													json_data1.getString("pass"),
													userid);							    			                   		
										}		        		
									}
									golbalFunc.savePreferences("KEY_MAG", array.length()+"");									
								}
    					}
							
			    			
			    			String jsondatao = "";
			    			jsondatao = golbalFunc.getJSONFromUrlGate("http://biitbook.com/api/ios/api_userdatao.php?user_id=" + userid); //cai nay cung dua qua lib
			    			
			    			if(!jsondatao.equals("")&&jsondatao.length()>90){		
			    				updateJsondatao(userid, jsondatao);			    				
			    				jsondatao = returnDatao(userid);			    				
			    				job = new JSONObject(jsondatao);			    				
			    				try{
			    					bookdb.open();
				    			 	bookdb.deleteBooks("001",userid);		 	
				    			 	bookdb.close();
									array = job.getJSONArray("result");						
				    			}catch(Exception ex){
				    				ex.printStackTrace();
				    				array = null;
				    			}
								
								if(array!=null){
									if(array.length()>0){		    				
										for(int j = 0; j <array.length(); j++)
										{
											JSONObject json_data1 = array.getJSONObject(j);								
											insertBook(json_data1.getString("ten_sach").hashCode()+"",
													json_data1.getString("ten_sach"),
													nameauthor, json_data1.getString("image_url"), 
													json_data1.getString("file_url"), "Free with me",
													"001", bookPath + "books/"+json_data1.getString("ten_sach").hashCode()+"",
													json_data1.getString("file_url").substring(json_data1.getString("file_url").lastIndexOf(".")+1, json_data1.getString("file_url").length()), 
													"1","1",userid);							    			                   		
										}		        		
									}
								}
    					}
    						result = "0";
    					} else if(err.equals("1")==true){
    						result = "2";
    					} else if(err.equals("2")==true){
    						result = "4";
    					}
    				} catch (Exception e1) {
    					// TODO Auto-generated catch block
    					result = "2";
    				}    				
	    			}else{
	    				result = "2";
	    			}
        	}catch(Exception ex){
        		result = "3";        		
        	}
        	return result;
        }

		@Override
        protected void onPostExecute(String result) {			
        	if(result.equals("0")){        		
        		try{
	    			Intent i = new Intent(LoginActivity.this, LibsActivity.class);       		
	    			startActivity(i);
	    			finish();
        		}catch(Exception ex){
        			Toast.makeText(LoginActivity.this,getResources().getString(R.string.cantlogin) , Toast.LENGTH_LONG).show();        			
        		}
        	}else if(result.equals("2")){        		
	        	try{
	        		Intent i = new Intent(LoginActivity.this, LibsActivity.class);
					startActivity(i);
					finish();
	        	}catch(Exception ex){
	        		Toast.makeText(LoginActivity.this, getResources().getString(R.string.cantlogin), Toast.LENGTH_LONG).show();
	    		}
        	}else{
        		if(result.equals("4")){
        			Toast.makeText(LoginActivity.this,getResources().getString(R.string.cantlogin) , Toast.LENGTH_LONG).show();
        		}
        		
	        	try{
	        
				golbalFunc.savePreferences("KEY_RUN", "0");		
				run = golbalFunc.loadSavedPreferences("KEY_RUN");
				isRun = true;
				setContentView(R.layout.login);	
				declareControl();
		    	editPass.setOnEditorActionListener(new EditText.OnEditorActionListener() {	      	

					public boolean onEditorAction(android.widget.TextView v,
							int actionId, KeyEvent event) {
												// TODO Auto-generated method stub
						if (actionId == EditorInfo.IME_ACTION_DONE) {
							performLogin(); 				
					}
					return false;
				}});
		    	
		    	txtContinue.setOnClickListener(new View.OnClickListener() {					
					public void onClick(View v) {						
						// TODO Auto-generated method stub
						isRun = true;
						doubleKillTask = new DoubleKillTask();
						doubleKillTask.execute();
					}
				});
		    	
		    	btnLogin.setOnClickListener(new View.OnClickListener() {					
					public void onClick(View v) {	
													    			
		    			performLogin();
					}
				});
		        
		        txtForgot.setOnClickListener(new View.OnClickListener() {					
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent i = new Intent(LoginActivity.this, ForgotActivity.class);
						startActivity(i);
					}
				});
		        
		        btnSignup.setOnClickListener(new View.OnClickListener() {					
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent i = new Intent(LoginActivity.this, SignupActivity.class);
						startActivity(i);
					}
				});
		    
				}catch(Exception ex){
					ex.printStackTrace();
					Toast.makeText(LoginActivity.this, getResources().getString(R.string.apperror), Toast.LENGTH_LONG).show();
					golbalFunc.actionFinish();  
			        finish();					
				}
        }
      }
    }	
}