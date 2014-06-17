package lmquan1990.biitbook;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UserDB{

	private static final String DATABASE_NAME = "login";
	private static final String DATABASE_TABLE_USER = "users";
	private static final int DATABASE_VERSION = 1;	
	public static final String KEY_STT = "_stt";
	public static final String KEY_USERID = "userid";
	public static final String KEY_PASS = "pass";
	public static final String KEY_LOG = "log";
	public static final String KEY_EMAIL = "email";
	public static final String KEY_API = "api";
	public static final String KEY_DATAO = "datao";
	private final Context context;
	private DatabaseHelper DBHelper;
	private SQLiteDatabase db;
	
	public UserDB(Context ctx){
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
			String CREATE_TABLE_USER = "CREATE TABLE " + DATABASE_TABLE_USER + "("
						+ KEY_STT + " INTEGER PRIMARY KEY," + KEY_USERID + " TEXT," + KEY_EMAIL + " TEXT,"
						+ KEY_PASS + " TEXT," + KEY_LOG + " TEXT," + KEY_API + " TEXT," + KEY_DATAO + " TEXT" +")";
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
	public UserDB open() throws SQLException
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
	
	public long insertUser(String userid, String email, String pass, String log, String api, String datao)
	{
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_USERID, userid);
		initialValues.put(KEY_EMAIL, email);
		initialValues.put(KEY_PASS, pass);
		initialValues.put(KEY_LOG, log);
		initialValues.put(KEY_API, api);
		initialValues.put(KEY_DATAO, datao);
		return db.insert(DATABASE_TABLE_USER, null, initialValues);
	}
	
	//---deletes a particular title---
	public boolean deleteTitle(String Id)
	{
		return db.delete(DATABASE_TABLE_USER, KEY_USERID +
				"=" + Id, null) > 0;
	}	
	
	//---retrieves a particular title---
	public Cursor getTitle(String Id) 
	{
		Cursor mCursor =
				db.query(true, DATABASE_TABLE_USER, new String[] {
						KEY_STT,
						KEY_USERID,
						KEY_EMAIL,
						KEY_PASS,
						KEY_LOG,
						KEY_API,
						KEY_DATAO
				},
				KEY_USERID + "='" + Id+"'",
				null,null,null,null,null);
		if (mCursor != null && mCursor.getCount()>0) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}
	
	//Kiem tra coi co ai dang nhap
		public Cursor getLog(String log)
		{
			Cursor mCursor =
					db.query(true, DATABASE_TABLE_USER, new String[] {
							KEY_STT,
							KEY_USERID,
							KEY_EMAIL,
							KEY_PASS,
							KEY_LOG,
							KEY_API,
							KEY_DATAO
					},
					KEY_LOG + "=" + "'"+log+"'",
					null,null,null,null,null);
			if (mCursor != null && mCursor.getCount()>0) {
				mCursor.moveToFirst();
			}
			return mCursor;
		}
		
	//---updates a title---
	public boolean updateUser(String userid, String log)
	{		
		ContentValues args = new ContentValues();
		args.put(KEY_USERID, userid);
		args.put(KEY_LOG, log);
		return db.update(DATABASE_TABLE_USER, args,
				KEY_USERID + "='" + userid+"'", null) > 0;
	}
	
	//---updates a json---
	public boolean updateJson(String userid, String json){	
		ContentValues args = new ContentValues();
		args.put(KEY_USERID, userid);
		args.put(KEY_API, json);
		return db.update(DATABASE_TABLE_USER, args,
				KEY_USERID + "='" + userid+"'", null) > 0;
	}
	
	//---updates a json da tao---
	public boolean updateDatao(String userid, String datao)
	{	
		ContentValues args = new ContentValues();
		args.put(KEY_USERID, userid);
		args.put(KEY_DATAO, datao);
		return db.update(DATABASE_TABLE_USER, args,
				KEY_USERID + "='" + userid+"'", null) > 0;
	}

}