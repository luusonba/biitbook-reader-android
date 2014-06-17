package lmquan1990.biitbook;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DowningDB{

	private static final String DATABASE_NAME = "downing";
	private static final String DATABASE_TABLE = "downtable";
	private static final int DATABASE_VERSION = 1;	
	public static final String KEY_STT = "stt";
	public static final String KEY_USERID = "userid";
	public static final String KEY_IDBOOK = "idbook";
	public static final String KEY_URL = "url";
	public static final String KEY_NAME = "name";
	public static final String KEY_AUTHOR = "author";
	public static final String KEY_DOWNING = "downing";
	private final Context context;
	private DatabaseHelper DBHelper;
	private SQLiteDatabase db;
	
	public DowningDB(Context ctx){
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
			String CREATE_TABLE_USER = "CREATE TABLE " + DATABASE_TABLE + "("
						+ KEY_STT + " INTEGER PRIMARY KEY," 
						+ KEY_USERID + " TEXT," 
						+ KEY_IDBOOK + " TEXT," 
						+ KEY_URL + " TEXT,"
						+ KEY_NAME + " TEXT," 
						+ KEY_AUTHOR + " TEXT,"
						+ KEY_DOWNING + " TEXT" +")";
			db.execSQL(CREATE_TABLE_USER);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion,
				int newVersion){
			db.execSQL("DROP TABLE IF EXISTS titles");
			onCreate(db);
		}
	}

	//---opens the database---
	public DowningDB open() throws SQLException
	{
		db = DBHelper.getWritableDatabase();
		return this;
	}

	//---closes the database---
	public void close()
	{		
        if (db != null && db.isOpen())
            db.close();
	}
	
	public long insertDowing(String userid, String idbook, String url, String name, String author, String dowing)
	{
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_USERID, userid);
		initialValues.put(KEY_IDBOOK, idbook);
		initialValues.put(KEY_URL, url);
		initialValues.put(KEY_NAME, name);
		initialValues.put(KEY_AUTHOR, author);
		initialValues.put(KEY_DOWNING, dowing);
		return db.insert(DATABASE_TABLE, null, initialValues);
	}
	
	//---deletes a particular title---
	public boolean deleteTitle(String Id)
	{
		return db.delete(DATABASE_TABLE, KEY_IDBOOK +
				"=" + Id, null) > 0;
	}
	
	//---deletes all particular title---
		public boolean deleteAllDOwing(String userid)
		{
			return db.delete(DATABASE_TABLE, KEY_USERID +
					"=" + userid, null) > 0;
		}	
	
	//---retrieves a particular title---
	public Cursor getTitle(String Id, String dowing){
		Cursor mCursor =
				db.query(true, DATABASE_TABLE, new String[] {
						KEY_STT,//0
						KEY_USERID,//1
						KEY_IDBOOK,//2
						KEY_URL,//3
						KEY_NAME,//4
						KEY_AUTHOR,//5
						KEY_DOWNING//6
				},
				KEY_IDBOOK + "='" + Id+"' and " + KEY_DOWNING + "='" + dowing +"'",
				null,null,null,null,null);
		if (mCursor != null && mCursor.getCount()>0) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}
	
	//---retrieves a particular title---
		public Cursor getDowning(String downing){
			Cursor mCursor =
					db.query(true, DATABASE_TABLE, new String[] {
							KEY_STT,//0
							KEY_USERID,//1
							KEY_IDBOOK,//2
							KEY_URL,//3
							KEY_NAME,//4
							KEY_AUTHOR,//5
							KEY_DOWNING//6
					},
					KEY_DOWNING + "='" + downing +"'",
					null,null,null,null,null);
			if (mCursor != null && mCursor.getCount()>0) {
				mCursor.moveToFirst();
			}
			return mCursor;
		}
		
		
		public Cursor getTopDowning(String userid, String downg) {
			
			String selectQuery = "SELECT  * FROM " + DATABASE_TABLE + " WHERE " + 
			        KEY_USERID + " = '" + userid + "' and " + KEY_DOWNING + " ='" + downg + "' ORDER BY" +  KEY_STT + "DESC LIMIT 1";
			Cursor cursor = db.rawQuery(selectQuery, null);
			
			if (cursor != null && cursor.getCount()>0) {
				cursor.moveToFirst();
			}
			return cursor;
		}
	
	  public List<Dowing> getAllEBook(String userid, String downg) {
	        List<Dowing> dowingList = new ArrayList<Dowing>();
	        //String selectQuery = "SELECT  * FROM " + DATABASE_TABLE + " WHERE " + 
	        //KEY_USERID + " = '" + userid + "' and " + KEY_DOWNING + " = '" + downg + "'";
	        
	        String selectQuery = "SELECT  * FROM " + DATABASE_TABLE;
	        
	        Cursor cursor = db.rawQuery(selectQuery, null);
	        cursor.getColumnCount();
	        if (cursor.moveToFirst()) {
	            do {
	            	Dowing dowing = new Dowing();
	            	dowing.setStt(Integer.parseInt(cursor.getString(0)));
	            	dowing.setUserid(cursor.getString(1));
	            	dowing.setIdbook(cursor.getString(2));
	            	dowing.setUrl(cursor.getString(3));
	            	dowing.setName(cursor.getString(4));	
	            	dowing.setAuthor(cursor.getString(5));
	            	dowing.setDowing(cursor.getString(6));
	            	dowingList.add(dowing);
	            } while (cursor.moveToNext());
	        }
	        return dowingList;
	    }	
	  		
	//---updates a Dowing---
	public boolean updateDowing(String idbook, String dowing){		
		ContentValues args = new ContentValues();
		args.put(KEY_IDBOOK, idbook);
		args.put(KEY_DOWNING, dowing);
		return db.update(DATABASE_TABLE, args,
				KEY_IDBOOK + "='" + idbook+"'", null) > 0;
	}
}