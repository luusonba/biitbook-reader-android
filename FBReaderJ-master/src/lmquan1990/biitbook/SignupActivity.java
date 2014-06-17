package lmquan1990.biitbook;

import com.biitbook.android.R;

import org.json.JSONException;
import org.json.JSONObject;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;

import org.geometerplus.android.fbreader.FBReaderApplication;
import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.app.ProgressDialog;
import org.holoeverywhere.widget.TextView;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import org.holoeverywhere.widget.AdapterView;
import org.holoeverywhere.widget.AdapterView.OnItemSelectedListener;
import org.holoeverywhere.widget.RadioButton;
import org.holoeverywhere.widget.Toast;
import org.holoeverywhere.widget.Spinner;;

public class SignupActivity extends Activity {
	
	public GolbalFunction postUrl = new GolbalFunction();
	public EditText txtEmail;
	public EditText txtPass;
	public EditText fName;
	public EditText lName;
	public TextView txtDieukhoan;
	public Button btnSignup;
	public Spinner quocgia;
	public RadioButton radionam;
	public RadioButton radionu;
    int gender = 0;
	String email ="";    
    String pass = "";    
    String fname = "";
    String lname = "";
    String userid = "";
    String json = "";
    int lang = 0;
    private ProgressDialog pd = null;    
    TextView txtSignin;
    Toast toast;
    TextView text;
    UserDB db;
    DBAdapter bookdb;
    public static Activity fa;
    String arr[]={
    		 FBReaderApplication.getAppContext().getString(R.string.vi),
    		 FBReaderApplication.getAppContext().getString(R.string.eng)};
	@SuppressLint({ "NewApi", "NewApi", "NewApi", "NewApi", "NewApi" })
	@Override
    public void onCreate(Bundle savedInstanceState) {
		fa = SignupActivity.this;
        super.onCreate(savedInstanceState);
        try{
			setContentView(R.layout.signup);
		}catch(Exception ex){
			AlertDialog.Builder builder1 = new AlertDialog.Builder(SignupActivity.this);
		    builder1.setTitle(getResources().getString(R.string.notice));
		    builder1.setMessage(getResources().getString(R.string.apperror));
		    builder1.setCancelable(true);
		    builder1.setNeutralButton(android.R.string.ok,
		            new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int id) {
		            dialog.cancel();
		        }
		    });

		    AlertDialog alert11 = builder1.create();
		    alert11.show();
		}		
                
        if (android.os.Build.VERSION.SDK_INT > 9) {
		    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		    StrictMode.setThreadPolicy(policy);
		}        
        db = new UserDB(this);
        bookdb = new DBAdapter(this);
        radionam = (RadioButton)findViewById(R.id.rb1);
		radionu = (RadioButton)findViewById(R.id.rb2);
        txtSignin = (TextView)findViewById(R.id.txtSignin);
        txtEmail = (EditText)findViewById(R.id.txtEmail);        
        txtPass = (EditText)findViewById(R.id.txtPassword);
        txtDieukhoan = (TextView)findViewById(R.id.txtTerms);
        fName  = (EditText)findViewById(R.id.txtFirst);
        lName  = (EditText)findViewById(R.id.txtLast);        
        btnSignup = (Button)findViewById(R.id.btnLogin); 
        quocgia = (Spinner)findViewById(R.id.lang);        
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.my_simple_item, arr);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        quocgia.setAdapter(adapter);
        quocgia.setOnItemSelectedListener(new MyProcessEvent());
        LayoutInflater inflater = getLayoutInflater();
		View layout = inflater.inflate(R.layout.custom_toast,
				(ViewGroup) findViewById(R.id.toast_custom));
		text = (TextView) layout.findViewById(R.id.tvtoast);
		toast = new Toast(getApplicationContext());
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setView(layout);
		
		txtDieukhoan.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(SignupActivity.this, TermActivity.class);        						
				startActivity(i);
			}
		});
                                      
        btnSignup.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				btnSignup.setEnabled(false);
				email = txtEmail.getText().toString();		        
		        pass = txtPass.getText().toString();		        
		        fname = fName.getText().toString();
		        lname = lName.getText().toString();
		        //addr = address.getText().toString();
		        if(email.equals("")==true || pass.equals("")==true || lname.equals("")==true|| fname.equals("")==true||(radionam.isChecked()==false&&radionu.isChecked()==false&&lang==0)){
					text.setText(getResources().getString(R.string.full));
                	toast.show();						
                	btnSignup.setEnabled(true);
				}else{
			        if(radionam.isChecked()==true)
						gender = 0;		    		
			        if(radionu.isChecked()==true)
			        	gender = 1;	
			        if (!email.matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+") || email.equals("")){
			        	text.setText(getResources().getString(R.string.emailincorrect));
	                	toast.show();
	                	btnSignup.setEnabled(true);
	                }else if(!email.equals("") && !pass.equals("")){
	                	if(pass.length()<6){
	                		text.setText(getResources().getString(R.string.passincorrect));
		                	toast.show();
		                	btnSignup.setEnabled(true);
	                	}else{
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
												
							String newLName = "";
							for(int i = 0; i < lname.length(); i++){
								if(Character.isWhitespace(lname.charAt(i))){
					                newLName = lname.substring(0, i) + "%20" + lname.substring(i+1, lname.length());			                
					            }
							}
							String newFName = "";
							for(int i = 0; i < fname.length(); i++){
								if(Character.isWhitespace(fname.charAt(i))){
									newFName = fname.substring(0, i) + "%20" + fname.substring(i+1, fname.length());
					            }
							}
							
							if(space == false){					
		    					if(postUrl.isOnline()==true){
		    						InputMethodManager imm = (InputMethodManager)getSystemService(
		    							      Context.INPUT_METHOD_SERVICE);
		    							imm.hideSoftInputFromWindow(txtEmail.getWindowToken(), 0);
		    							imm.hideSoftInputFromWindow(txtPass.getWindowToken(), 0);    							
		    							new MyTask().execute(Uri.parse("http://biitbook.com/users/api/register?email=" + email + "&password=" +pass + "&username=" + newFName + "%20" + newLName + "&first_name=" + newFName + "&last_name=" + newLName + "&gender="+gender+"&phone=0885858585&address_line1=vn&career=1&group_id=2&language_user=" +lang));
							}else{
								text.setText(getResources().getString(R.string.networkerror));
			                	toast.show();
			                	btnSignup.setEnabled(true);
							}		       
						}else{
							text.setText(getResources().getString(R.string.space));
		                	toast.show();					
		                	btnSignup.setEnabled(true);
						}
	                }
				}
				}
			}
		});
        
       txtSignin.setOnClickListener(new View.OnClickListener() {
		
		public void onClick(View v) {
			// TODO Auto-generated method stub
			//postUrl.savePreferences("KEY_RUN", "2");		
			//Intent i = new Intent(SignupActivity.this, LoginActivity.class);			
			//startActivity(i);
			finish();
		}
	}); 				
		
    }
	
	private class MyProcessEvent implements OnItemSelectedListener{
		 public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3){
		  lang = arg2 + 1;		  
		 }
	
		 public void onNothingSelected(AdapterView<?> arg0) {
		  lang = 0;
		 }
	
	 }
		
	private class MyTask extends AsyncTask<Uri, Integer, String> {		
		 @Override
           protected void onPreExecute() {
              pd = new ProgressDialog(SignupActivity.this);
       			pd.setMessage(getResources().getString(R.string.processing));
                   pd.setIndeterminate(false);
                   pd.setCancelable(false);
                   pd.setCanceledOnTouchOutside(false);
                   pd.show();
               if (!pd.isShowing())
                   pd.show();                
               super.onPreExecute();
           }

       @Override
       protected String doInBackground(Uri... params) {
       	String result  = "";
       	json = postUrl.getJSONFromUrl(params[0].toString());       	
        if(json.equals(""))
			json = "{\"err\":\"1\"}";
		try {
	    	JSONObject jsonObject = new JSONObject(json);  	  		
	    	String err = jsonObject.getString("err");
	    	if(err.equals("0")==true)
	        {            	
	    		JSONObject data = jsonObject.getJSONObject("data");
	        	userid = data.getString("user_id");	  
	        	
	        	postUrl.savePreferences("KEY_IDEND", userid);
	        	json = postUrl.getJSONFromUrl("http://biitbook.com/api/ios/api_getuserdown_web.php?userid=" + userid);
				result = "0";			
	        } else if(err.equals("4")==true)
	            result = "4";
	        else if(err.equals("1")==true)
	        	result = "1";
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			result = "1";
		}  
		   return result;
       }
       @Override
       protected void onPostExecute(String result) {
       	if(result.equals("0")){        		
				if (pd.isShowing())
                   pd.dismiss();
				text.setText(getResources().getString(R.string.signupsuc));
            	toast.show();				
				insert(userid, email, pass,"yes", json,"");	
				postUrl.savePreferences("KEY_EMAILEND", email);
				postUrl.savePreferences("KEY_IDEND", userid);		
				postUrl.savePreferences("KEY_PASSEND", pass);		
				Intent i = new Intent(SignupActivity.this, LibsActivity.class);			
				startActivity(i);
				postUrl.savePreferences("KEY_NAME", fname + " " + lname);
				postUrl.savePreferences("KEY_BC", "0");
				postUrl.savePreferences("KEY_BOOK", "0");				
				if(LoginActivity.fa != null)
					LoginActivity.fa.finish();
				postUrl.savePreferences("KEY_RUN", "1");
				finish();
       	}
       	if(result.equals("1")==true){
				if (pd.isShowing())
                   pd.dismiss();
				AlertDialog.Builder builder1 = new AlertDialog.Builder(SignupActivity.this);
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
			    alert11.show();
       	}
       	
       	else if(result.equals("4")==true){
				if (pd.isShowing())
                   pd.dismiss();
				AlertDialog.Builder builder1 = new AlertDialog.Builder(SignupActivity.this);
			    builder1.setTitle(getResources().getString(R.string.notice));
			    builder1.setMessage(getResources().getString(R.string.emailexist));
			    builder1.setCancelable(true);
			    builder1.setNeutralButton(android.R.string.ok,
			            new DialogInterface.OnClickListener() {
			        public void onClick(DialogInterface dialog, int id) {
			            dialog.cancel();
			        }
			    });

			    AlertDialog alert11 = builder1.create();
			    alert11.show();
          	}
       	btnSignup.setEnabled(true);
       }
   }
		
	private void insert(String userid, String email, String pass, String log, String api, String datao){ 
    	try{
    		db.open();
	        @SuppressWarnings("unused")
			long idBooks;
	        idBooks = db.insertUser(userid,email,pass,log,api,datao);  
	        db.close();
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
}