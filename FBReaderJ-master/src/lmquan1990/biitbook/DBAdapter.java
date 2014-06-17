package lmquan1990.biitbook;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBAdapter {

	public static final String STT = "stt";
	public static final String ID = "id";
	public static final String NAME = "name";
	public static final String AUTHOR = "author";
	public static final String IMAGE_VIEW = "image_view";
	public static final String URL = "url";
	public static final String POINT = "point";	
	public static final String CAT = "cat";
	public static final String BOOK_PATH = "book_path";	
	public static final String TYPE = "type";
	public static final String ONOFF = "onoff";
	public static final String PASS = "pass";
	public static final String USERID = "userid";
	private static final String TAG = "DBAdapter";
	private static final String DATABASE_NAME = "book";
	private static final String DATABASE_TABLE = "paths";
	private static final int DATABASE_VERSION = 1;
	private final Context context;
	private DatabaseHelper DBHelper;
	private SQLiteDatabase db;
	
	public DBAdapter(Context ctx){
		this.context = ctx;
		DBHelper = new DatabaseHelper(context);
	}

	private static class DatabaseHelper extends SQLiteOpenHelper
	{
		DatabaseHelper(Context context)
		{
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}
	
		@Override
		public void onCreate(SQLiteDatabase db){
			String CREATE_TABLE = "CREATE TABLE " + DATABASE_TABLE + "("
					+ STT + " INTEGER PRIMARY KEY," 
					+ ID + " TEXT,"
					+ NAME + " TEXT,"
					+ AUTHOR + " TEXT,"
					+ IMAGE_VIEW + " TEXT,"
					+ URL + " TEXT,"
					+ POINT + " TEXT,"
					+ CAT + " TEXT,"
					+ BOOK_PATH + " TEXT,"
					+ TYPE + " TEXT,"
					+ ONOFF + " TEXT,"
					+ PASS + " TEXT,"
					+ USERID + " TEXT"
					+ ")";
			db.execSQL(CREATE_TABLE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion,
				int newVersion){
			Log.w(TAG, "Upgrading database from version " + oldVersion
					+ " to "
					+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS titles");
			onCreate(db);
		}
	}

	//---opens the database---
	public DBAdapter open() throws SQLException{
		db = DBHelper.getWritableDatabase();
		return this;
	}

	//---closes the database---
	public void close(){
		DBHelper.close();
	}

	//---insert a title into the database---
	public long insertTitle(String id, String name, String author, String image_view, String url, 
			String point, String cat, String book_path, String type, String onoff,
			String pass, String userid){
		ContentValues initialValues = new ContentValues();
		initialValues.put(ID, id);  //0
		initialValues.put(NAME, name);//1
		initialValues.put(AUTHOR, author);//2
		initialValues.put(IMAGE_VIEW, image_view);//3
		initialValues.put(URL, url);//4
		initialValues.put(POINT, point);//5
		initialValues.put(CAT, cat);// 000 tren may, 001 da tao, 002 mua ve , 003 tap chi  //6
		initialValues.put(BOOK_PATH, book_path);//7
		initialValues.put(TYPE, type);//8
		initialValues.put(ONOFF, onoff);// 9
		initialValues.put(PASS, pass);// 10
		initialValues.put(USERID, userid); //11	
		return db.insert(DATABASE_TABLE, null, initialValues);
	}

	//---deletes a particular title---
	public boolean deleteTitle(String Id){
		return db.delete(DATABASE_TABLE, ID +
				"=" + Id, null) > 0;
	}

	//---retrieves all the titles---
	public Cursor getAllTitles(){
		return db.query(DATABASE_TABLE, new String[] {
				STT,
				ID,
				NAME,
				AUTHOR,
				IMAGE_VIEW,
				URL,
				POINT,
				CAT,
				BOOK_PATH,
				TYPE,				
				ONOFF,
				PASS,
				USERID
				},
				null,null,null,null,null);
	}
	
	
	public ArrayList<String> getData(){
	        String[] columns = new String[] {ID};
	        Cursor c = db.query(DATABASE_TABLE, columns, null, null, null, null, null);
	        //Cursor  c = db.rawQuery("select * from paths",null);
	        ArrayList<String> list = new ArrayList<String>();
	        	        
	        int iId = c.getColumnIndex(ID);	      	 
	        for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()){
	        	list.add(c.getString(iId)+"");	            
	        }
	        c.close();	 
	        return list;
	    }
	
	//---retrieves a particular title---
	public Cursor getTitle(String Id) throws SQLException{
		Cursor mCursor =
				db.query(true, DATABASE_TABLE, new String[] {
						STT, //0
						ID, //1
						NAME,//2
						AUTHOR,//3
						IMAGE_VIEW,//4
						URL,//5
						POINT,//6
						CAT,//7
						BOOK_PATH,//8
						TYPE,//9
						ONOFF,//10
						PASS,//11
						USERID//12
						},
				ID + "=" + Id,
				null,null,null,null,null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}
	
	//----------get book----------------------
	 // Getting All book
    public List<EBook> getAllEBook() {
        List<EBook> ebookList = new ArrayList<EBook>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + DATABASE_TABLE;
        
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.getColumnCount();
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                EBook eBook = new EBook();
                eBook.setStt(Integer.parseInt(cursor.getString(0)));
                eBook.setID(cursor.getString(1));
                eBook.setName(cursor.getString(2));
                eBook.setAuthor(cursor.getString(3));
                eBook.setImage_view(cursor.getString(4));
                eBook.setUrl(cursor.getString(5));
                eBook.setPoint(cursor.getString(6));
                eBook.setCat(cursor.getString(7));
                eBook.setBook_path(cursor.getString(8));
                eBook.setType(cursor.getString(9));
                eBook.setOnoff(cursor.getString(10));
                eBook.setPass(cursor.getString(11));
                eBook.setUserid(cursor.getString(12));
                ebookList.add(eBook);
            } while (cursor.moveToNext());
        }
        return ebookList;
    }	
	
    public int getCountColumn() {        
    	String selectQuery = "SELECT  * FROM " + DATABASE_TABLE;        
        Cursor cursor = db.rawQuery(selectQuery, null);
        return cursor.getColumnCount();
    }
    
    //--cap nhat pass--
  		public boolean updatePass(String id, String pass){			
  			ContentValues args = new ContentValues();
  			args.put(ID, id);
  			args.put(PASS, pass);					
  			return db.update(DATABASE_TABLE, args,
  					ID + "=" + id, null) > 0;
  		}
  		
  		
  	    //--cap nhat bookpath--
  	  		/**public boolean updateBookpath(String id, String bookpath){			
  	  			ContentValues args = new ContentValues();
  	  			args.put(ID, id);
  	  			args.put(BOOK_PATH, bookpath);					
  	  			return db.update(DATABASE_TABLE, args,
  	  					ID + "=" + id, null) > 0;
  	  		}*/
  	  		
  	  		
  	//delete multi book
  	public void deleteBooks(String cat, String userid){
  	    db.delete(DATABASE_TABLE, 
  	    		CAT + " = ? AND " + USERID + " = ?",  
  	            new String[] {cat, userid});
  	}
    
	//---updates a title---
	public boolean updateTitle(long stt, String id, String name,String author,
			String image_view,String url, String point, String cat,String book_path, 
			String type, String onoff, String pass, String user){
		ContentValues args = new ContentValues();
		args.put(ID, id);
		args.put(NAME, name);
		args.put(AUTHOR, author);
		args.put(IMAGE_VIEW, image_view);
		args.put(URL, url);
		args.put(POINT, point);
		args.put(CAT, cat);
		args.put(BOOK_PATH, book_path);
		args.put(TYPE, type);
		args.put(ONOFF, onoff);
		args.put(PASS, pass);
		args.put(USERID, user);
		return db.update(DATABASE_TABLE, args,
				ID + "=" + id, null) > 0;
	}
}
