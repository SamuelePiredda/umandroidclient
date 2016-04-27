	package it.giammar;

	import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

	import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

	public class BancheDatiDataSource {

	  // Database fields
	  private SQLiteDatabase database;
	  private MySQLiteHelper dbHelper;
	  private String[] allColumns = { MySQLiteHelper.COLUMN_IDBANCHEDATI, MySQLiteHelper.COLUMN_NAME, MySQLiteHelper.COLUMN_ENABLED};

	  public BancheDatiDataSource(Context context) {
	    dbHelper = new MySQLiteHelper(context);
	  }

	  public void open() throws SQLException {
	    database = dbHelper.getWritableDatabase();
	  }

	  public void close() {
	    dbHelper.close();
	  }

	  public BancheDati createBancaDati(String stName, int stEnabled) {
	    ContentValues values = new ContentValues();
	    values.put(MySQLiteHelper.COLUMN_NAME, stName);
	    values.put(MySQLiteHelper.COLUMN_ENABLED, stEnabled);

	    long insertId = database.insert(MySQLiteHelper.TABLE_BANCHEDATI, null,
	        values);
	    
	    Cursor cursor = database.query(MySQLiteHelper.TABLE_BANCHEDATI,
	        allColumns, MySQLiteHelper.COLUMN_IDBANCHEDATI + " = " + insertId, null,
	        null, null, null);
	    cursor.moveToFirst();
	    BancheDati newBancheDati = cursorToBancheDati(cursor);
	    cursor.close();
	    return newBancheDati;
	  }

	  public void deleteBancheDati(long id) {
	    //long id = preferenze.getId();
	    System.out.println("BancheDati deleted with id: " + id);
	    database.delete(MySQLiteHelper.TABLE_BANCHEDATI, MySQLiteHelper.COLUMN_IDBANCHEDATI
	        + " = " + id, null);
	  }
	  
	  public void deleteAllBancheDati(long id) {
		    //long id = preferenze.getId();
		    System.out.println("BancheDati deleted");
		    database.delete(MySQLiteHelper.TABLE_BANCHEDATI,null, null);
		  }

	  public List<BancheDati> getAllBancheDati() {
	    List<BancheDati> BancheDatiColl = new ArrayList<BancheDati>();

	    Cursor cursor = database.query(MySQLiteHelper.TABLE_BANCHEDATI,
	        allColumns, null, null, null, null, null);

	    cursor.moveToFirst();
	    while (!cursor.isAfterLast()) {
	    	BancheDati banchedati = cursorToBancheDati(cursor);
	    	BancheDatiColl.add(banchedati);
	      cursor.moveToNext();
	    }
	    // make sure to close the cursor
	    cursor.close();
	    return BancheDatiColl;
	  }
	  
	  public BancheDati getAllBancheDatiByName(String stName) {
		    //List<Preferenze> preferenzeColl = new ArrayList<Preferenze>();
		  	BancheDati banchedati = null;
		  			
		  	String newString = stName.replace("'", "''");
		  			
		  	Cursor cursor = database.query(MySQLiteHelper.TABLE_BANCHEDATI,
			        allColumns, MySQLiteHelper.COLUMN_NAME + " = '" + newString + "'", null,
			        null, null, null);
			    cursor.moveToFirst();
			    
			    if(cursor!=null && cursor.getCount() > 0)
			    {                   
			         //... stuff
				    banchedati = cursorToBancheDati(cursor);
				    //preferenzeColl.add(preferenze);
				    
			    }
			cursor.close();

		    return banchedati;
		  }

	  private BancheDati cursorToBancheDati(Cursor cursor) {
		  BancheDati banchedati = new BancheDati();
		  banchedati.setId(cursor.getLong(0));
		  banchedati.setNome(cursor.getString(1));
		  banchedati.setEnabled(cursor.getInt(2));
	    return banchedati;
	  }
	} 

