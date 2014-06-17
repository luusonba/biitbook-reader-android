package lmquan1990.biitbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Stack;

import lmquan1990.biitbook.ImagesByUrlProjector;
import lmquan1990.biitbook.Utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

public class ImageLoader implements ImagesByUrlProjector {
    
	public static final String TAG = "ImageLoader";		
    private HashMap<String, Bitmap> cache=new HashMap<String, Bitmap>();
    
    private static File cacheDir = null;
    private final int progressDrawable;    
    private final int defaultDrawable;
    
    private GolbalFunction gol = new GolbalFunction();;
        
    @SuppressWarnings("unused")
    private final Context context;
    
    public ImageLoader(Context context, int defaultDrawable) {
        this(context, -1, defaultDrawable);
    }
    
    public ImageLoader(Context context, int progressDrawable, int defaultDrawable) {
        //Make the background thread low priority. This way it will not affect the UI performance
        photoLoaderThread.setPriority(Thread.NORM_PRIORITY-1);
        
        this.context = context;
        
        this.progressDrawable = progressDrawable;
        this.defaultDrawable = defaultDrawable;
        
        //get the dir to save cached images
        if (cacheDir == null) cacheDir = Utils.createCacheDir(context);
    }
    
    // sets imageView tag!
    public void displayImage(String url, ImageView imageView, String cato, String type)
    {
        if(cache.containsKey(url)) {
        	Log.d(TAG, "Image " + url + " exists in cache, loading it from there");
            imageView.setImageBitmap(cache.get(url));
        } else {
        	Log.d(TAG, "Image " + url + " not exists in cache, putting it in queue, setting view to default view");
        	imageView.setTag(url);
            queuePhoto(url, imageView, cato, type);
            imageView.setImageResource((progressDrawable != -1) ? progressDrawable : defaultDrawable);
        }    
    }

    private void queuePhoto(String url, ImageView imageView, String cato, String type)
    {
        photosQueue.clean(imageView);
        PhotoToLoad p=new PhotoToLoad(url, imageView);
        synchronized(photosQueue.photosToLoad){
            photosQueue.photosToLoad.push(p);
            photosQueue.photosToLoad.notifyAll();
        }
        
        //start thread if it's not started yet
        if(photoLoaderThread.getState()==Thread.State.NEW)
            photoLoaderThread.start();
    }
            
    private Bitmap getBitmap(String url){   
    	String filename = "";
    	File f = null;
    	Bitmap bitmap=null;    	
    	try {	    	    
	    	    filename=String.valueOf(url.hashCode());
	            f=new File(cacheDir, filename);
	            if(!f.exists()){
	            	@SuppressWarnings("unused")
					URL url1 = new URL(url);
	            	if(gol.isOnline()){ 	
		    		        try {
		    		            InputStream is=new URL(url).openStream();
		    		            OutputStream os = new FileOutputStream(f);
		    		            Utils.copyStream(is, os);
		    		            os.close();		    		            
		    		        } catch (Exception ex){
		    		        	ex.printStackTrace();
		    		        }        	
		            
		    	    }else		            
			            f = new File(cacheDir, "NOIMAGEBOOK".hashCode()+"");
	            }
	        }catch (Exception e) {
	    		if(url.startsWith("NOIMAGE")){
	    			f = new File(cacheDir,url.substring(0, 11).hashCode()+"");   				
	    		}else{
	    			f = new File(url);
	    		}	    		
	    	}	    			    
    	
    	bitmap = decodeFile(f);    	
    	return bitmap;
    }
    

    //decodes image and scales it to reduce memory consumption
    private Bitmap decodeFile(File f){
        try {
            //decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f),null,o);
            
            //Find the correct scale value. It should be the power of 2.
            final int REQUIRED_SIZE=70;
            int width_tmp=o.outWidth, height_tmp=o.outHeight;
            int scale=1;
            while(true){
                if(width_tmp/2<REQUIRED_SIZE || height_tmp/2<REQUIRED_SIZE)
                    break;
                width_tmp/=2;
                height_tmp/=2;
                scale++;
            }
            
            //decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize=scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {
        	
        }
        return null;
    }
    
    //Task for the queue
    private class PhotoToLoad
    {
        public String url;
        public ImageView imageView;
        public PhotoToLoad(String u, ImageView i){
            url=u; 
            imageView=i;
        }
    }
    
    PhotosQueue photosQueue=new PhotosQueue();
    
    public void stopThread()
    {
        photoLoaderThread.interrupt();
    }
    
    //stores list of photos to download
    class PhotosQueue
    {
        private Stack<PhotoToLoad> photosToLoad=new Stack<PhotoToLoad>();
        
        //removes all instances of this ImageView
        public void clean(ImageView image)
        {
            for(int j=0 ;j<photosToLoad.size();){
                if(photosToLoad.get(j).imageView==image)
                    photosToLoad.remove(j);
                else
                    ++j;
            }
        }
    }
    
    class PhotosLoader extends Thread {
        @Override public void run() {
            try {
                while(true)
                {
                    //thread waits until there are any images to load in the queue
                    if(photosQueue.photosToLoad.size()==0)
                        synchronized(photosQueue.photosToLoad){
                            photosQueue.photosToLoad.wait();
                        }
                    if(photosQueue.photosToLoad.size()!=0)
                    {
                        PhotoToLoad photoToLoad;
                        synchronized(photosQueue.photosToLoad){
                            photoToLoad=photosQueue.photosToLoad.pop();
                        }                        
                        Bitmap bmp=getBitmap(photoToLoad.url);
                        cache.put(photoToLoad.url, bmp);
                        if(((String)photoToLoad.imageView.getTag()).equals(photoToLoad.url)){
                            BitmapDisplayer bd=new BitmapDisplayer(bmp, photoToLoad.imageView);
                            Activity a=(Activity)photoToLoad.imageView.getContext();
                            a.runOnUiThread(bd);
                        }                                                
                    }
                    if(Thread.interrupted())
                        break;
                }
            } catch (Exception e) {
                //allow thread to exit
            	//System.out.println(e.toString());
            }
        }
    }
    
    PhotosLoader photoLoaderThread=new PhotosLoader();
    
    //Used to display bitmap in the UI thread
    class BitmapDisplayer implements Runnable
    {
        Bitmap bitmap;
        ImageView imageView;
        public BitmapDisplayer(Bitmap b, ImageView i){bitmap=b;imageView=i;}
        public void run()
        {
            if(bitmap!=null)
                imageView.setImageBitmap(bitmap);
            else
                imageView.setImageResource(defaultDrawable);
        }
    }

    public void clearCache() {
        //clear memory cache
        cache.clear();
        
        //clear SD cache
        File[] files=cacheDir.listFiles();
        for(File f:files)
            f.delete();
    }

}