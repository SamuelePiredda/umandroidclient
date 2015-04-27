package it.giammar;

import it.giammar.pratomodel.QueryReply;
import it.giammar.pratomodel.QueryReply.Database;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

import org.apache.log4j.Logger;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class CustomizeAutoActivity extends ListActivity {
	private static final String TAG = "CustomizeAuto";
	private SharedPreferences sp;
	private List<Database> bancheDati;
	private List<Database> bancheDatiscelte;
//	private List<Database> bancheDati2;
	private EnumSet<Database> bdScelte = EnumSet.noneOf(Database.class);
	private ListView listView;
	private GsonBuilder gsonb;
	private Gson gson;
	private int iMODE_AUTO = 0;
	private BancheDatiDataSource datasource;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.customizeauto);
		sp = this.getSharedPreferences("UM", Context.MODE_PRIVATE);

		gsonb = new GsonBuilder();
		gson = gsonb.create();

		Bundle bundle = getIntent().getExtras();
		String jBancheDati;

		iMODE_AUTO = bundle.getInt("MODE_AUTO");

		jBancheDati = sp.getString("autobd", "");

		Type type = new TypeToken<List<Database>>(){}.getType();
		bancheDati = gson.fromJson(jBancheDati, type);
		if (jBancheDati!="") {
			Log.i(TAG,bancheDati.toString());	
		}		
//		Collections.copy(bancheDati, bancheDati2);
		// edit.putString("elencobd", );
		// edit.putString("autobd", gson.toJson(arrBancheDati) );
		//
		
		////////////////////////////////////////////////////
		// Recupero la configurazione "AUTO" dalla tabella banchedati del db Sqlite creato all'avvio
	    datasource = new BancheDatiDataSource(this);
	    datasource.open();

	    List<BancheDati> valuesBD = datasource.getAllBancheDati();

	    int countBD = valuesBD.size();

	    for(int z = 0; z < countBD; z++) {
	    	Log.d("Elemento " + Integer.toString(z), "a");
	    	Log.d("Riga " + valuesBD.get(z).toString(), "b"); 
	    	
	    	BancheDati bancadati1;
	    	bancadati1 = datasource.getAllBancheDatiByName(valuesBD.get(z).toString());
        	if (bancadati1 != null) {
	        	if (bancadati1.getEnabled()==0) {
	    	    	Database s=bancheDati.get(z);
	    	        bdScelte.remove(s);
	        	}
        	}
	    }
		////////////////////////////////////////////////////
		
		
	    if (jBancheDati!="") {
	    	
		    setListAdapter(new ArrayAdapter<Database>(this,
					android.R.layout.simple_list_item_multiple_choice, bancheDati));
	
			listView = getListView();
			
			listView.setCacheColorHint(Color.TRANSPARENT);
			listView.requestFocus(0);
			listView.setItemsCanFocus(false);
			listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
	
		    List<BancheDati> values = datasource.getAllBancheDati();
	
		    BancheDati bancadati;
	
			if(bundle.getInt("MODE_AUTO")== 1) {
				int count = listView.getCount();
		        for(int i = 0; i < count; i++) {
		        	Log.i("Elemento " + Integer.toString(i) , listView.getItemAtPosition(i).toString());
		        	bancadati = datasource.getAllBancheDatiByName(listView.getItemAtPosition(i).toString());
		        	if (bancadati != null) {
			        	if (bancadati.getEnabled()==0) {
			        		listView.setItemChecked(i, false);
			        	}
			        	else {
			        		listView.setItemChecked(i, true);	
			        	}
		        	}
		        	else {
		        		listView.setItemChecked(i, false);
		        	}
		        }	
			}
	    }
	    else {
	    	Toast.makeText(getApplicationContext(), "E' necessario salvare PRIMA le PREFERENZE, poi CONFIG.AUTO", Toast.LENGTH_SHORT).show();
	    }

	}

	public void onOkClicked(View v) {
		
		try {
			
			BancheDati bancadati;
			
			//Svuoto la tabella BancheDati del db Sqlite
			datasource.deleteAllBancheDati(1);
			
			//Ririempio la tabella BancheDati del db Sqlite sulla base delle selezioni fatte sulla lista
			SparseBooleanArray checked = listView.getCheckedItemPositions();
			Log.i(TAG,bancheDati.toString());
			if(iMODE_AUTO== 1) {
				for (int i = 0; i < listView.getAdapter().getCount(); i++) {
				    if (checked.get(i)) {
				    	bancadati = datasource.createBancaDati(listView.getItemAtPosition(i).toString(), 1);
				    	Database s=bancheDati.get(i);
				        bdScelte.add(s);
				    }
				    else {
				    	bancadati = datasource.createBancaDati(listView.getItemAtPosition(i).toString(), 0);
				    	Database s=bancheDati.get(i);
				        bdScelte.add(s);
				    }
				}
			}
			else {
				for (int i = 0; i < listView.getAdapter().getCount(); i++) {
				    if (checked.get(i)) {
				    	bancadati = datasource.createBancaDati(listView.getItemAtPosition(i).toString(), 1);
				    	Database s=bancheDati.get(i);
				        bdScelte.add(s);
				    }
				    else {
				    	bancadati = datasource.createBancaDati(listView.getItemAtPosition(i).toString(), 0);
				    	Database s=bancheDati.get(i);
				        bdScelte.add(s);
				    }
				}
			}
			////////////////////////////////////////////////////
		    List<BancheDati> valuesBD = datasource.getAllBancheDati();
	
		    int countBD = valuesBD.size();
	
		    for(int z = 0; z < countBD; z++) {
		    	Log.d("Elemento " + Integer.toString(z), "a");
		    	Log.d("Riga " + valuesBD.get(z).toString(), "b"); 
		    	
		    	BancheDati bancadati1;
		    	bancadati1 = datasource.getAllBancheDatiByName(valuesBD.get(z).toString());
	        	if (bancadati1 != null) {
		        	if (bancadati1.getEnabled()==0) {
	
		    	    	Database s=bancheDati.get(z);
		    	        bdScelte.remove(s);
	
		        	}
	        	}
		    	
		    }
			////////////////////////////////////////////////////
	
			Editor edit = sp.edit();
			Log.i(TAG,bdScelte.toString());
		    edit.putString("autoBdScelte",gson.toJson(bdScelte) );
		    edit.commit();
			Intent main = new Intent(this, UMAndroidClientActivity.class);
			this.startActivity(main);
		}
	 catch (Exception e) {
		 Toast.makeText(getApplicationContext(), "E' necessario salvare PRIMA le PREFERENZE, poi CONFIG.AUTO", Toast.LENGTH_SHORT).show();
	 }

	}

}