package lmquan1990.biitbook;

import com.biitbook.android.R;
import java.util.Locale;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class PreferenceFragment extends SherlockFragment {
	GolbalFunction golbal = new GolbalFunction();
	CheckBox chkScan;
		
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
		View v = inflater.inflate(R.layout.properties, container, false);
		
		RadioGroup lang = (RadioGroup) v.findViewById(R.id.lang);
		if(golbal.loadSavedPreferences("KEY_LANG").equals("en"))			
			lang.check(R.id.lang_en);
		else
			lang.check(R.id.lang_vi);
		lang.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				
				switch (checkedId) {
				case R.id.lang_en:
					String languageToLoaden  = "en"; // your language 
					Locale localen = new Locale(languageToLoaden);  
					Locale.setDefault(localen); 
					Configuration configen = new Configuration(); 
					configen.locale = localen; 
					getActivity().getBaseContext().getResources().updateConfiguration(configen,  
					getActivity().getBaseContext().getResources().getDisplayMetrics());					
					golbal.savePreferences("KEY_LANG", "en");
					break;
				case R.id.lang_vi:
					String languageToLoad  = "vi"; // your language 
					Locale locale = new Locale(languageToLoad);  
					Locale.setDefault(locale); 
					Configuration config = new Configuration(); 
					config.locale = locale; 
					getActivity().getBaseContext().getResources().updateConfiguration(config,  
					getActivity().getBaseContext().getResources().getDisplayMetrics());					
					golbal.savePreferences("KEY_LANG", "vi");					
					break;
				}
				FragmentManager fm = getSherlockActivity().getSupportFragmentManager();
				DialogFragmentJr alert = new DialogFragmentJr();
				alert.show(fm, "title");
			}			
		});
		chkScan = (CheckBox) v.findViewById(R.id.scan_enabled);
		if(golbal.loadSavedPreferences("KEY_SCAN").equals("0"))
			chkScan.setChecked(true);
		else
			chkScan.setChecked(false);
		chkScan.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(chkScan.isChecked()){
					golbal.savePreferences("KEY_SCAN", "0");					
				}
				else{
					golbal.savePreferences("KEY_SCAN", "1");
				}
			}
		});
		((LibsActivity)getActivity()).getSlidingMenu().showContent();
		((LibsActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		return v;
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}
}
