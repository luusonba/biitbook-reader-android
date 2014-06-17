package lmquan1990.biitbook;

import java.util.List;

import com.biitbook.android.R;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.*;
import android.text.Html;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ProgressBar;

@SuppressLint("SetJavaScriptEnabled")
public class DownloadActivity extends BaseActivity {
    private ProgressBar progress;
    private ImageButton btnPause;
    private ImageButton btnCancel;
    private TextView txtName;
    private TextView txtPer;
    private TextView txtSize;
    private RelativeLayout realay;
    private ListView list;
    List<Dowing> listBook;
    DowningDB dowingDB;
    String userid = "";
    private ProgressRequestReceiver receiver;
	public static Activity fa;	
	GolbalFunction golbal = new GolbalFunction();
	public DownloadActivity() {
		super(R.string.downact);
	}
		
    @Override
    public void onCreate(Bundle savedInstanceState) { 
        super.onCreate(savedInstanceState);
        fa = this;
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff'>"+getResources().getString(R.string.downact)+"</font>"));
        setContentView(R.layout.download);
        getSlidingMenu().setSlidingEnabled(false);
        IntentFilter filter = new IntentFilter(ProgressRequestReceiver.PROCESS_RESPONSE);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        receiver = new ProgressRequestReceiver();
        DowningDB downingDB = new DowningDB(this);
        userid = golbal.loadSavedPreferences("KEY_IDEND");
        registerReceiver(receiver, filter);
		btnPause = (ImageButton)findViewById(R.id.pauseDown);
		btnCancel = (ImageButton)findViewById(R.id.cancelDown);
		realay = (RelativeLayout)findViewById(R.id.layreaproDown);
        progress = (ProgressBar)findViewById(R.id.progressDown);
		progress.setProgress(0);
		progress.setMax(100);
		realay.setVisibility(View.VISIBLE);
        list = (ListView)findViewById(R.id.listdowning);
        txtName = (TextView)findViewById(R.id.txtNameDown);
        txtPer = (TextView)findViewById(R.id.txtPerDown);
        txtSize = (TextView)findViewById(R.id.txtSizeDown);
        listBook = downingDB.getAllEBook(userid, "2");
        SampleAdapter adapter = new SampleAdapter(this);
        for (int i = 0; i < listBook.size(); i++) {
			adapter.add(new SampleItem(listBook.get(i).getName().toString(),listBook.get(i).getAuthor().toString()));
		}
		list.setAdapter(adapter);	
		txtName.setText(listBook.get(0).getName().toString());
    }
    
    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(event.getAction() == KeyEvent.ACTION_DOWN){
			switch(keyCode){            	
            case KeyEvent.KEYCODE_BACK:
            	finish();
			}
		}
		return true;
	}
    
    /**public void getWaitDown(){
    	String wait = golbal.loadSavedPreferences("KEY_WAITDOWN");
    	String[] array1 = wait.substring(1).split("|");
    	System.out.println("lenght: " + array1.length + "  array:  " + array1.toString());
    	for(int i = array1.length;i>=0;i++){
    		String[] array = array1[i].substring(1).split("\\$");
    		//idbook = array[0];
    		//url = array[1];
    		listName.add(array[2]);
    		//position = array[3];
    		listAuthor.add(array[5]);
    	}		
	}*/
    
    private class SampleItem {
		public String title;
		public String author;
		public SampleItem(String title, String author){
			this.title = title; 
			this.author = author;
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
			TextView title = (TextView) convertView.findViewById(R.id.rowTitleDown);
			TextView author = (TextView) convertView.findViewById(R.id.rowAuthorDown);
			ImageButton listcancel = (ImageButton)convertView.findViewById(R.id.rowCancelDown);
			title.setText(getItem(position).title);	
			author.setText(getItem(position).author);
			listcancel.setBackgroundResource(R.drawable.ic_menu_donwload);
			return convertView;
		}
	}
    
    public class ProgressRequestReceiver extends BroadcastReceiver{	
		public static final String PROCESS_RESPONSE = "down.PROCESS_RESPONSE";		
		@Override
		public void onReceive(Context context, Intent intent) {
			int progress = intent.getIntExtra(MyIntentService.DOWN_PER,0);
			int lenghtOfFile = intent.getIntExtra(MyIntentService.DOWN_TOTAL,0);
	        txtSize.setText(progress+"/"+lenghtOfFile+" KB");
	        txtPer.setText(intent.getIntExtra(MyIntentService.DOWN_LASTPER,0) + "%");
	        DownloadActivity.this.progress.setProgress((progress*100)/(lenghtOfFile));
	        if((progress*100)/(lenghtOfFile)==100)//va adapter bang 0 thi mat
	        	realay.setVisibility(View.GONE);
		}
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