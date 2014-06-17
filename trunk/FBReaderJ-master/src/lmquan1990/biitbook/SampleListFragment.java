package lmquan1990.biitbook;

import com.biitbook.android.R;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;

import org.geometerplus.android.fbreader.FBReaderApplication;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.widget.TextView;

public class SampleListFragment extends ListFragment {
		   
		UserDB userdb = new UserDB(FBReaderApplication.getAppContext());
		GolbalFunction golbal = new GolbalFunction();
		String userid = golbal.loadSavedPreferences("KEY_IDEND");        
		public GolbalFunction postUrl = new GolbalFunction();
		Fragment newContent = null;
		String tag = "";
	   	    
	    String testJson = "";
	 	    
	    TextView txtAcc;
	    TextView txtBookc;
	    TextView txtCc;
	    TextView txtMag;
	    TextView txtDangnhap;
	    LinearLayout loginlay;
	    FrameLayout layoutTong;
	    ListView listView;
	    JSONArray arrayBc;	    
	    String mylib = FBReaderApplication.getAppContext().getString(R.string.mylib);
	    String dis = FBReaderApplication.getAppContext().getString(R.string.dis);
	    //String profile = FBReaderApplication.getAppContext().getString(R.string.profile);
	    String bcoin = FBReaderApplication.getAppContext().getString(R.string.bcoin);
	    String setting = FBReaderApplication.getAppContext().getString(R.string.setting);
	    String cont = FBReaderApplication.getAppContext().getString(R.string.cont);
	    String logout = FBReaderApplication.getAppContext().getString(R.string.logout);
	    String quit = FBReaderApplication.getAppContext().getString(R.string.quit);
	    String[] mStrings = new String[] {mylib,dis,bcoin,setting,cont,logout};
	    String[] stringNull = new String[] {mylib,dis,bcoin,setting,cont,quit};
	    //String[] mStrings = new String[] {mylib,dis,profile,bcoin,setting,cont,logout};
	    //String[] stringNull = new String[] {mylib,dis,profile,bcoin,setting,cont,quit};
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		LinearLayout view = (LinearLayout) inflater.inflate(R.layout.menu_frame, null);		
		txtAcc = (TextView)view.findViewById(R.id.txtAcc);
		txtBookc = (TextView)view.findViewById(R.id.txtBookscount);
		txtCc = (TextView)view.findViewById(R.id.txtCoinscount);
		txtMag = (TextView)view.findViewById(R.id.txtMagscount);
		txtDangnhap = (TextView)view.findViewById(R.id.txtDangnhap);		
		loginlay = (LinearLayout)view.findViewById(R.id.loginlay);
		layoutTong = (FrameLayout)view.findViewById(R.id.layouttong);		
		listView = (ListView)view.findViewById(R.id.list);		
		return view;
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);		
		SampleAdapter adapter = new SampleAdapter(getActivity());
		if(userid.equals("")){
			txtDangnhap.setVisibility(View.VISIBLE);
			
			for (int i = 0; i < 6; i++) {
				adapter.add(new SampleItem(stringNull[i]));
			}
			setListAdapter(adapter);
			
			txtDangnhap.setOnClickListener(new View.OnClickListener() {			
				public void onClick(View v) {
					// TODO Auto-generated method stub				
					postUrl.savePreferences("KEY_RUN", "2");
					Intent i = new Intent(getActivity(), LoginActivity.class);
					startActivity(i);				
				}
			});
			
			
		}else{
			loginlay.setVisibility(View.VISIBLE);
			int book = 0;
			int bc = 0;								
			int mag = 0;
			
			if(!postUrl.loadSavedPreferences("KEY_BOOK").isEmpty())
				book = Integer.parseInt(postUrl.loadSavedPreferences("KEY_BOOK"));
			
			if(!postUrl.loadSavedPreferences("KEY_MAG").isEmpty())
				mag = Integer.parseInt(postUrl.loadSavedPreferences("KEY_MAG"));					
									
			if(!postUrl.loadSavedPreferences("KEY_BC").isEmpty())				
				bc = Integer.parseInt(postUrl.loadSavedPreferences("KEY_BC"));
			
			if(book>1)
				txtBookc.setText(book + " "+FBReaderApplication.getAppContext().getString(R.string.books));
			else
				txtBookc.setText(book + " "+FBReaderApplication.getAppContext().getString(R.string.bookn));
				
			if(bc>1)
				txtCc.setText(bc + " "+FBReaderApplication.getAppContext().getString(R.string.bcoins));
			else
				txtCc.setText(bc + " "+FBReaderApplication.getAppContext().getString(R.string.bcoinn));
			
			if(mag>1)
				txtMag.setText(mag + " "+FBReaderApplication.getAppContext().getString(R.string.mags));
			else
				txtMag.setText(mag + " "+FBReaderApplication.getAppContext().getString(R.string.magn));
				
			txtAcc.setText(postUrl.loadSavedPreferences("KEY_NAME"));					
					
			for (int i = 0; i < 6; i++) {
				adapter.add(new SampleItem(mStrings[i]));
			}
		
			setListAdapter(adapter);
			txtAcc.setText(postUrl.loadSavedPreferences("KEY_NAME"));
		}		       
	        layoutTong.setVisibility(View.VISIBLE);
	}	
		
	
	@Override
	public void onListItemClick(ListView lv, View v, int position, long id) {
		switch (position) {	
			case 0:				
				((LibsActivity)getActivity()).getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff'>"+mylib+"</font>"));
				((LibsActivity)getActivity()).getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
				((LibsActivity)getActivity()).getSlidingMenu().showContent();
				((LibsActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
				FragmentManager manager = getActivity().getSupportFragmentManager();
				FragmentTransaction ft = manager.beginTransaction();
				if(newContent!=null){					
					ft.remove(newContent);
					newContent = null;
				}
				ft.commit();
				break;
			case 1:
				((LibsActivity)getActivity()).getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff'>"+dis+"</font>"));
				((LibsActivity)getActivity()).getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
				tag = "DiscoveryFragment";
				newContent = new DiscoveryFragment();
				break;
			/**case 2:
				((LibsActivity)getActivity()).getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff'>"+profile+"</font>"));
				((LibsActivity)getActivity()).getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
				tag = "ProfileFragment";
				newContent = new ProfileFragment();				
				break;*/
			case 2:
				((LibsActivity)getActivity()).getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff'>"+bcoin+"</font>"));
				((LibsActivity)getActivity()).getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
				tag = "BcoinsFragment";
				newContent = new BcoinsFragment();
				break;
			case 3:         
				((LibsActivity)getActivity()).getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff'>"+setting+"</font>"));
				((LibsActivity)getActivity()).getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
				tag = "PreferenceFragment";
				newContent = new PreferenceFragment();				
				break;
			case 4:
				((LibsActivity)getActivity()).getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff'>"+cont+"</font>"));
				((LibsActivity)getActivity()).getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
				tag = "ContactFragment";
				newContent = new ContactFragment();				
				break;
			case 5:			
				if(userid.equals("")){
					golbal.actionFinish();
					System.exit(0);
				}else{
			        Intent intent = new Intent(getActivity().getApplication(), LoginActivity.class);
			        startActivity(intent);
			        golbal.savePreferences("KEY_RUN", "2");
			        golbal.savePreferences("KEY_BC", "");
			        golbal.savePreferences("KEY_BOOK", "");	        
			        golbal.savePreferences("KEY_IDEND", "");
			        golbal.savePreferences("KEY_NAME", "");
					updateUser(userid, "no");
				}
				break;
		}
		if (newContent != null)	
			switchFragment(newContent, tag);
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
	
	public String getFag(Cursor c){		
		return c.getString(6);	                
	}

	private void switchFragment(Fragment fragment, String tag) {
		if (getActivity() == null)
			return;		
		if (getActivity() instanceof LibsActivity) {
			if(!userid.equals("")){
				if(golbal.loadSavedPreferences("KEY_FAG").equals("lmquan1990.biitbook.BcoinsFragment")){
					BcoinsTask bctask = new BcoinsTask();
					bctask.execute();
				}
			}
			golbal.savePreferences("KEY_FAG", fragment.getClass().getName());
			LibsActivity fca = (LibsActivity) getActivity();
			fca.switchContent(fragment, tag);
		}		
	}
	
	
	public class BcoinsTask extends AsyncTask<String, String, String> {    	
    	protected String doInBackground(String... aurl) {
    		if(golbal.isOnline()){	    		
				String jsonBc = golbal.getJSONFromUrl("http://biitbook.com/api/ios/api_usercoint.php?user_id="+userid);
		   		JSONObject jobc;
				try {
					jobc = new JSONObject(jsonBc);
					arrayBc = jobc.getJSONArray("result");
					JSONObject json_data1 = arrayBc.getJSONObject(0);
					golbal.savePreferences("KEY_BC", json_data1.getString("point"));				
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		}
			return null;
    	}
    	
		@Override
    	protected void onPostExecute(String unused) {
			int bc = 0;
			if(postUrl.loadSavedPreferences("KEY_BC").equals(""))
				bc = 0;
			else
				bc = Integer.parseInt(postUrl.loadSavedPreferences("KEY_BC"));
			
			txtCc.setText(bc + " bcoin");
		}
	}
	  	 
	private class SampleItem {
		public String tag;
		public SampleItem(String tag){
			this.tag = tag; 
		}
	}	

	public class SampleAdapter extends ArrayAdapter<SampleItem> {

		public SampleAdapter(Context context) {
			super(context, 0);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(R.layout.row, null);
			}
			TextView title = (TextView) convertView.findViewById(R.id.row_title);
			title.setText(getItem(position).tag);			
			return convertView;
		}
	}
}