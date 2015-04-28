	package it.giammar;

	import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

	import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

	public class PreferenzeDataSource {

	  // Database fields
	  private SQLiteDatabase database;
	  private MySQLiteHelper dbHelper;
	  private String[] allColumns = { MySQLiteHelper.COLUMN_ID, MySQLiteHelper.COLUMN_UTENTE, MySQLiteHelper.COLUMN_PASSWORD, MySQLiteHelper.COLUMN_HOST, MySQLiteHelper.COLUMN_PORT, MySQLiteHelper.COLUMN_ATTPORT, MySQLiteHelper.COLUMN_USESSL};

	  public PreferenzeDataSource(Context context) {
	    dbHelper = new MySQLiteHelper(context);
	  }

	  public void open() throws SQLException {
	    database = dbHelper.getWritableDatabase();
	  }

	  public void close() {
	    dbHelper.close();
	  }

	  public Preferenze createPreferenza(String stUtente, String stPassword, String stHost, String stPort, String stAttport, int iUsessl) {
	    ContentValues values = new ContentValues();
	    values.put(MySQLiteHelper.COLUMN_UTENTE, stUtente);
	    values.put(MySQLiteHelper.COLUMN_PASSWORD, stPassword);
	    values.put(MySQLiteHelper.COLUMN_HOST, stHost);
	    values.put(MySQLiteHelper.COLUMN_PORT, stPort);
	    values.put(MySQLiteHelper.COLUMN_ATTPORT, stAttport);
	    values.put(MySQLiteHelper.COLUMN_USESSL, iUsessl);
	    
	    long insertId = database.insert(MySQLiteHelper.TABLE_PREFERENZE, null,
	        values);
	    
	    Cursor cursor = database.query(MySQLiteHelper.TABLE_PREFERENZE,
	        allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
	        null, null, null);
	    cursor.moveToFirst();
	    Preferenze newPreferenze = cursorToPreferenze(cursor);
	    cursor.close();
	    return newPreferenze;
	  }

	  public void deletePreferenze(long id) {
	    //long id = preferenze.getId();
	    System.out.println("Preferenze deleted with id: " + id);
	    database.delete(MySQLiteHelper.TABLE_PREFERENZE, MySQLiteHelper.COLUMN_ID
	        + " = " + id, null);
	  }

	  public List<Preferenze> getAllPreferenze() {
	    List<Preferenze> preferenzeColl = new ArrayList<Preferenze>();

	    Cursor cursor = database.query(MySQLiteHelper.TABLE_PREFERENZE,
	        allColumns, null, null, null, null, null);

	    cursor.moveToFirst();
	    while (!cursor.isAfterLast()) {
	    	Preferenze preferenze = cursorToPreferenze(cursor);
	      preferenzeColl.add(preferenze);
	      cursor.moveToNext();
	    }
	    // make sure to close the cursor
	    cursor.close();
	    return preferenzeColl;
	  }

	  public Preferenze getAllPreferenzeByHost(String stHost) {
		    //List<Preferenze> preferenzeColl = new ArrayList<Preferenze>();
		  	Preferenze preferenze = null;
		    
		    Cursor cursor = database.query(MySQLiteHelper.TABLE_PREFERENZE,
			        allColumns, MySQLiteHelper.COLUMN_HOST + " = '" + stHost + "'", null,
			        null, null, null);
			    cursor.moveToFirst();
			    
			    if(cursor!=null && cursor.getCount() > 0)
			    {                   
			         //... stuff
				    preferenze = cursorToPreferenze(cursor);
				    //preferenzeColl.add(preferenze);
				    
			    }
			cursor.close();

//		    Cursor cursor = database.query(MySQLiteHelper.TABLE_PREFERENZE,
//		        allColumns, null, null, null, null, null);
//
//		    cursor.moveToFirst();
//		    while (!cursor.isAfterLast()) {
//		     if (cursor.getString(3) == stHost) {
//		      Preferenze preferenze = cursorToPreferenze(cursor);
//		      preferenzeColl.add(preferenze);
//		     }
//		      cursor.moveToNext();
//		    }
//		    // make sure to close the cursor
//		    cursor.close();
		    return preferenze;
		  }
	  
	  public Integer getCountPreferenzeByHost(String stHost) {
		    Integer i = 0;
		    
		    Cursor cursor = database.query(MySQLiteHelper.TABLE_PREFERENZE,
			        allColumns, MySQLiteHelper.COLUMN_HOST + " = '" + stHost + "'", null,
			        null, null, null);
			    cursor.moveToFirst();
			    
			    if(cursor!=null && cursor.getCount() > 0)
			    {                   
				    while (!cursor.isAfterLast()) {
				     i=i+1; 
				     cursor.moveToNext();
				    }
			    }
			cursor.close();

		    return i;
		  }

	  private Preferenze cursorToPreferenze(Cursor cursor) {
		  Preferenze preferenze = new Preferenze();
		  preferenze.setId(cursor.getLong(0));
		  preferenze.setUtente(cursor.getString(1));
		  preferenze.setPassword(cursor.getString(2));
		  preferenze.setHost(cursor.getString(3));
		  preferenze.setPort(cursor.getString(4));
		  preferenze.setattPort(cursor.getString(5));
		  preferenze.setuseSSL(cursor.getInt(6));
	    return preferenze;
	  }
	} 

