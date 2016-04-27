package it.giammar;

import java.util.List;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

//public class PreferenzeActivity extends Activity implements OnClickListener {
public class ConfigurazioneActivity extends ListActivity  {
	private static final String TAG = "Preferenze";
	private SharedPreferences sp;

	private Button salva;
	private ListView listView;
	private TextView hostText;
	String strSelectedHost = "";
	String strSelectedUtente = "";	
	String strSelectedPassword = "";
	String strSelectedPort = "";
	String strSelectedAttPort = "";
	long blnSelectedUseSSL = 0;

	private PreferenzeDataSource datasource;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.customizeauto);
		sp = this.getSharedPreferences("UM", Context.MODE_PRIVATE);
		salva = (Button) findViewById(R.id.TestButton);
		//hostText = (TextView) findViewById(R.id.hostText);
		//listView = (ListView) findViewById(R.id.listView1);
		listView = getListView();
		
		listView.setCacheColorHint(Color.TRANSPARENT);
		listView.requestFocus(0);

	    datasource = new PreferenzeDataSource(this);
	    datasource.open();

	    //List<Preferenze> values = datasource.getAllPreferenzeByHost(host.getText().toString());
	    List<Preferenze> values = datasource.getAllPreferenze();
	    //setListAdapter((ListAdapter) values);

	    // use the SimpleCursorAdapter to show the
	    // elements in a ListView
	    ArrayAdapter<Preferenze> adapter = new ArrayAdapter<Preferenze>(this,android.R.layout.simple_list_item_multiple_choice, values);
	    setListAdapter((ListAdapter) adapter);
	    
	    listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> arg0, View v,
                    int index, long arg3) {

            	Toast.makeText(getApplicationContext(),listView.getItemAtPosition(index).toString(), Toast.LENGTH_LONG).show();
                return false;
            }
	    }); 

	    listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> myAdapter, View myView, int myItemInt, long mylng) {

				try {
					Preferenze pref = (Preferenze) listView.getItemAtPosition(myItemInt);
					strSelectedHost = pref.getHost().toString();
					strSelectedUtente = pref.getUtente().toString();
					strSelectedPassword = pref.getPassword().toString();
					strSelectedPort = pref.getPort().toString();
					strSelectedAttPort = pref.getAttport().toString();
					blnSelectedUseSSL = pref.getuseSSL();
					
	            	if (strSelectedHost.toString() != "") {
	                	Intent returnIntent = new Intent();
	                	returnIntent.putExtra("host",strSelectedHost.toString() + "");
	                	returnIntent.putExtra("utente",strSelectedUtente.toString() + "");
	                	returnIntent.putExtra("password",strSelectedPassword.toString() + "");
	                	returnIntent.putExtra("port",strSelectedPort.toString() + "");
	                	returnIntent.putExtra("attport",strSelectedAttPort.toString() + "");
	                	returnIntent.putExtra("usessl", String.valueOf(blnSelectedUseSSL) + "");

	                	setResult(RESULT_OK,returnIntent);
	                	finish();
	            	}
	            	else {
	            		Toast.makeText(getApplicationContext(), "Selezionare un elemento dalla lista", Toast.LENGTH_SHORT).show();
	            	}
					
					Log.d("tag", pref.getHost().toString());
				}
				catch(Exception e) {
					Log.d("tag", e.getMessage().toString());
					strSelectedHost = "";
				}
	
			}
	    	
		});

	    salva.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
            	if (strSelectedHost.toString() != "") {
                	Intent returnIntent = new Intent();
                	returnIntent.putExtra("host",strSelectedHost.toString() + "");
                	returnIntent.putExtra("utente",strSelectedUtente.toString() + "");
                	returnIntent.putExtra("password",strSelectedPassword.toString() + "");
                	returnIntent.putExtra("port",strSelectedPort.toString() + "");
                	returnIntent.putExtra("attport",strSelectedAttPort.toString() + "");
                	returnIntent.putExtra("usessl",blnSelectedUseSSL);

                	setResult(RESULT_OK,returnIntent);
                	finish();
            	}
            	else {
            		Toast.makeText(getApplicationContext(), "Selezionare un elemento dalla lista", Toast.LENGTH_SHORT).show();
            	}
            }
        });

	}


}
