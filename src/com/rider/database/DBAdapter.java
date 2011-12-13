package com.rider.database;


import android.content.ContentValues;
import android.content.Context; 
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DBAdapter {

	public static final String KEY_ROWID = "_id";
	public static final String KEY_EMAIL = "_email";
	public static final String KEY_PASSWORD = "_password";
	public static final String KEY_USER_ID = "_userID";
	public static final String KEY_LINE_NUMBER ="_lineNumber";
	public static final String KEY_LINE_ID ="_lineID";
	

	private static final String TAG = "DBAdapter";
	private static final String DATABASE_NAME = "RiderLocal";

	
	public static int DATABASE_VERSION = 666;

	public enum Tabels {
		USERS , LINES
	}
	
	private static final String DATABASE_USERS_CREATE =
		"create table " + Tabels.USERS.toString() + " (" 
		+ KEY_ROWID + " integer primary key autoincrement, "
		+ KEY_EMAIL + " VARCHAR(50) not null, "
		+ KEY_PASSWORD + " VARCHAR(50) not null, "
		+ KEY_USER_ID + " integer not null);" ;
	
	private static final String DATABASE_LINES_CREATE =
		"create table " + Tabels.LINES.toString() + " (" 
		+ KEY_ROWID + " integer primary key autoincrement, "
		+ KEY_LINE_NUMBER + " VARCHAR(50) not null, "
		+ KEY_LINE_ID + " VARCHAR (50));" ;

	private final Context context;   
	private DatabaseHelper DBHelper;
	private SQLiteDatabase db;

	public DBAdapter(Context ctx) 
	{
		this.context = ctx;
		DBHelper = new DatabaseHelper(context);
	}

	//---opens the database---
	public DBAdapter open() throws SQLException 
	{
		db = DBHelper.getWritableDatabase();
		return this;
	}

	//---closes the database---    
	public void close() 
	{
		DBHelper.close();
	}

	//---deletes a particular item---
	public boolean deleteItem(String table,long rowId) 
	{
		return db.delete(table, KEY_ROWID + "=" + rowId, null) > 0;
	}

	//---updates a user---
	public boolean updateUserTable(long rowId,String name, String password, int userID) {
		
		ContentValues args = new ContentValues();

		args.put(KEY_EMAIL, name);
		args.put(KEY_PASSWORD, password);
		args.put(KEY_USER_ID, userID);

		return db.update(Tabels.USERS.toString(), args, 
				KEY_ROWID + "=" + rowId, null) > 0;
	}

	//---insert an item into the database---
	public long insertNewUser(String email, String password, int userID)	{
		ContentValues initialValues = new ContentValues();

		initialValues.put(KEY_EMAIL, email);
		initialValues.put(KEY_PASSWORD, password);
		initialValues.put(KEY_USER_ID, userID);
		
		return db.insert(Tabels.USERS.toString(), null, initialValues);
	}
	
	//---insert an item into the database---
	public long insertNewLine(String line, String lineID)	{
		ContentValues initialValues = new ContentValues();

		initialValues.put(KEY_LINE_ID, lineID);
		initialValues.put(KEY_LINE_NUMBER, line);
		
		return db.insert(Tabels.LINES.toString(), null, initialValues);
	}

	//-- delete all rows of the given table
	public int deleteTable(String table){
		return db.delete(table, null, null);
	}
	
	//---retrieves all the items by table---
	public Cursor getAllUsers() 
	{

		return db.query(Tabels.USERS.toString(), new String[] {
				KEY_ROWID, 
				KEY_EMAIL,
				KEY_PASSWORD,
				KEY_USER_ID
				
		},
		null, 
		null, 
		null, 
		null, 
		null);

	}

	//---retrieves all the lines number 
	public Cursor getAllLines() 
	{

		return db.query(Tabels.LINES.toString(), new String[] {
				KEY_ROWID,
				KEY_LINE_ID,
				KEY_LINE_NUMBER
				
		},
		null, 
		null, 
		null, 
		null, 
		null);

	}
	
	
	private static class DatabaseHelper extends SQLiteOpenHelper 
	{
		public DatabaseHelper(Context context) 
		{
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) 
		{
			db.execSQL(DATABASE_USERS_CREATE);
			db.execSQL(DATABASE_LINES_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, 
				int newVersion) 
		{

			db.execSQL("DROP TABLE IF EXISTS " + Tabels.USERS.toString());
			db.execSQL("DROP TABLE IF EXISTS " + Tabels.LINES.toString());
			onCreate(db);
		}
	}    

}
