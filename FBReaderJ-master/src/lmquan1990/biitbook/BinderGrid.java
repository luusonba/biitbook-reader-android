package lmquan1990.biitbook;

import com.biitbook.android.R;

import java.util.HashMap;
import java.util.List;
import org.holoeverywhere.widget.TextView;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class BinderGrid extends BaseAdapter {	    
	static final String KEY_TAG = "bookdata"; // parent node 
	static final String KEY_ID = "id"; 
	static final String KEY_NAME = "name"; 
	static final String KEY_AUTHOR = "author"; 
	static final String KEY_IMAGE_VIEW = "image_view"; 
	static final String KEY_URL = "url";
	static final String KEY_CATO = "category_document";
    static final String KEY_TYPE = "type";
    static final String KEY_DOW = "dow";
    static final String KEY_PASS = "pass";
	    
	LayoutInflater inflater;
	ImageLoader imageLoader;	
	List<HashMap<String,String>> bookDataCollection;
	ViewHolder holder;	
	String uri = "";
	public BinderGrid() {
		// TODO Auto-generated constructor stub
	}	
	    
	public BinderGrid(Activity act, List<HashMap<String,String>> map) {
		this.bookDataCollection = map;
		inflater = (LayoutInflater) act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);		
        this.imageLoader = new ImageLoader(act, R.drawable.load, R.drawable.load);
	}
	public int getCount() {
		// TODO Auto-generated method stub		
		return bookDataCollection.size();
	}
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub		
		return null;
	}
	public long getItemId(int position) {
		// TODO Auto-generated method stub			 
		return 0;
	}
	public View getView(int position, View convertView, ViewGroup parent) {
		View vi;	     
	    vi = inflater.inflate(R.layout.grid, null);	    
	    holder = new ViewHolder();	     
	    holder.tvTitle = (TextView)vi.findViewById(R.id.textBook);  
        holder.tvAuthor = (TextView)vi.findViewById(R.id.textAuthor);
        holder.tvSample = (TextView)vi.findViewById(R.id.txtSample);
        holder.imgDownload=(ImageView)vi.findViewById(R.id.imgDownload);
	    holder.gBookImage =(ImageView)vi.findViewById(R.id.grid_image);
	    vi.setTag(holder);
	    holder.tvTitle.setText(bookDataCollection.get(position).get(KEY_NAME)); 
        holder.tvAuthor.setText(bookDataCollection.get(position).get(KEY_AUTHOR));
        if(bookDataCollection.get(position).get(KEY_PASS).equals("2")){        	
        	holder.tvSample.setVisibility(View.VISIBLE);
        }        
        
        if(bookDataCollection.get(position).get(KEY_DOW).equals("1")){
            holder.imgDownload.setVisibility(View.VISIBLE);
        }
	    uri = bookDataCollection.get(position).get(KEY_IMAGE_VIEW);
		for(int i = 0; i < uri.length(); i++){
	 	    if(Character.isWhitespace(uri.charAt(i))){
	 	            String newName = uri.substring(0, i) + "%20" + uri.substring(i+1, uri.length());
	 	            uri = newName;	    	            
	 	    }
	 	}	 	
	 	imageLoader.displayImage(uri, holder.gBookImage, bookDataCollection.get(position).get(KEY_CATO), bookDataCollection.get(position).get(KEY_TYPE));
        return vi;
	}
		
	static class ViewHolder{
		TextView tvTitle;        
        TextView tvAuthor;
        TextView tvSample;
        ImageView imgDownload;
		ImageView gBookImage;
	}
}