package lmquan1990.biitbook;


import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.biitbook.android.R;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;

import org.geometerplus.android.fbreader.FBReaderApplication;
import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.app.ProgressDialog;
import org.holoeverywhere.widget.Button;
import org.holoeverywhere.widget.EditText;
import org.holoeverywhere.widget.TextView;
import org.holoeverywhere.widget.Toast;

@SuppressLint({ "NewApi", "NewApi", "NewApi", "NewApi", "NewApi" })
public class ContactFragment extends SherlockFragment {

	Button btnSend;
	EditText editNd;
	TextView txtHot;
	String nd = "";
	String email = "";
	String userid = "";
	private ProgressDialog pd = null;
	GolbalFunction golbal = new GolbalFunction();
	String isReturn ="";
	
	@Override
	public void onCreate(Bundle savedInstanceState) { 
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
	}	

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();
		inflater.inflate(R.menu.none, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View v = inflater.inflate(R.layout.contact, container, false);
		super.onCreate(savedInstanceState);	
		email = golbal.loadSavedPreferences("KEY_EMAILEND");
		userid = golbal.loadSavedPreferences("KEY_IDEND");
		editNd = (EditText) v.findViewById(R.id.conts);		
		btnSend = (Button) v.findViewById(R.id.btnSend);
		txtHot = (TextView)v.findViewById(R.id.txtHotline);
		btnSend.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
                // TODO Auto-generated method stub
				nd = editNd.getText().toString();
				if(!nd.equals("")){
					if(golbal.isOnline()){					
						SendEmailTask send = new SendEmailTask();
						send.execute();
					}else
						Toast.makeText((LibsActivity)getActivity(),getResources().getString(R.string.dontsent),Toast.LENGTH_SHORT).show();
				}else
					Toast.makeText((LibsActivity)getActivity(),getResources().getString(R.string.enter),Toast.LENGTH_SHORT).show();
			}
		});
		
		txtHot.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});		
		((LibsActivity)getActivity()).getSlidingMenu().showContent();
		((LibsActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		return v;
	}
	
	public class SendEmailTask extends AsyncTask<String, String, String> {
    	
		@Override
    	protected void onPreExecute() {
    		super.onPreExecute();    	
    		pd = new ProgressDialog((LibsActivity)getActivity());
   			pd.setMessage(FBReaderApplication.getAppContext().getString(R.string.processing));
            pd.setIndeterminate(false);
            pd.setCancelable(true);     
            pd.setOnCancelListener(new DialogInterface.OnCancelListener(){
                public void onCancel(DialogInterface dialog) {
                	if (pd.isShowing())
                        pd.dismiss();
                	isReturn = "2";
                }
            });   
           if (!pd.isShowing())
            pd.show();
    	}

    	@Override
    	protected String doInBackground(String... aurl) {
    		try {                   	
            	if(userid ==null || userid.equals("")){
            		email = "User chua dang nhap";
            	}
                GMailSender sender = new GMailSender("contactbiitbookandroid@gmail.com", "[11, 89, -119, -100, -23, -1, -113, -88, 94, -109, 34, -3, -38, 41, -2, -93, 59, 52, -9, -35, 54, 109, -94, -77, -115, 87, 58, -97, 70, -51, 5, -43]");
                sender.sendMail("Contact Biitbook Android",   
                		email + " __ " + nd,   
                        "contactbiitbookandroid@gmail.com",   
                        "biitbook@gmail.com");   
                isReturn = "0";
            } catch (Exception e) {   
                 e.printStackTrace();
                 isReturn = "1";
            }
    		return isReturn;
    	}
    	
		@Override
    	protected void onPostExecute(String unused) {
    		if (pd.isShowing())
                pd.dismiss();
    		if(isReturn.equals("1")){
				AlertDialog.Builder builder1 = new AlertDialog.Builder((LibsActivity)getActivity());
			    builder1.setTitle(getResources().getString(R.string.notice));
			    builder1.setMessage(getResources().getString(R.string.dontsent));
			    builder1.setCancelable(true);
			    builder1.setNeutralButton(android.R.string.ok,
			            new DialogInterface.OnClickListener() {
			        public void onClick(DialogInterface dialog, int id) {
			            dialog.cancel();			            
			        }
			    });

			    AlertDialog alert11 = builder1.create();
			    alert11.show();
    		}else if(isReturn.equals("0")){
    			Toast.makeText((LibsActivity)getActivity(), getResources().getString(R.string.sent), Toast.LENGTH_LONG).show();
    		}    		
		}
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);		
	}
}