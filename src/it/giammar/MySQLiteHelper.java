package it.giammar;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.EditText;

public class MySQLiteHelper extends SQLiteOpenHelper {

  public static final String TABLE_BANCHEDATI = "banchedati";
  public static final String COLUMN_IDBANCHEDATI = "id";
  public static final String COLUMN_NAME = "name";
  public static final String COLUMN_ENABLED = "enabled";
	
  public static final String TABLE_PREFERENZE = "preferenze";
  public static final String COLUMN_ID = "_id";
  public static final String COLUMN_UTENTE = "utente";
  public static final String COLUMN_PASSWORD = "password";
  public static final String COLUMN_HOST = "host";
  public static final String COLUMN_PORT = "port";
  public static final String COLUMN_ATTPORT = "attPort";
  public static final String COLUMN_USESSL = "useSSL";

  private static final String DATABASE_NAME = "umconfig.db";
  private static final int DATABASE_VERSION = 5;

  // Database creation sql statement
  private static final String BANCHEDATI_CREATE = "create table "
      + TABLE_BANCHEDATI + "(" + COLUMN_IDBANCHEDATI
      + " integer primary key autoincrement, " + COLUMN_NAME + " text not null, " + COLUMN_ENABLED + " integer"
      + ");";
  
  // Database creation sql statement
  private static final String PREFERENZE_CREATE = "create table "
      + TABLE_PREFERENZE + "(" + COLUMN_ID
      + " integer primary key autoincrement, " + COLUMN_UTENTE + " text not null, " + COLUMN_PASSWORD + " text not null, " + COLUMN_HOST + " text not null, " + COLUMN_PORT + " text not null, " + COLUMN_ATTPORT + " text not null, " + COLUMN_USESSL + " integer"
      + ");";

  public MySQLiteHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase database) {
    database.execSQL(PREFERENZE_CREATE);
    database.execSQL(BANCHEDATI_CREATE);
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    Log.w(MySQLiteHelper.class.getName(),
        "Upgrading database from version " + oldVersion + " to "
            + newVersion + ", which will destroy all old data");
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_PREFERENZE);
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_BANCHEDATI);
    onCreate(db);
  }

} 