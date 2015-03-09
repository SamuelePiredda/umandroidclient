package it.giammar;

import it.giammar.pratomodel.QueryReply;
import it.giammar.pratomodel.QueryReply.Database;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class CustomizeAutoActivity extends ListActivity {
	private static final String TAG = "CustomizeAuto";
	private SharedPreferences sp;
	private List<Database> bancheDati;
//	private List<Database> bancheDati2;
	private EnumSet<Database> bdScelte = EnumSet.noneOf(Database.class);
	private ListView listView;
	private GsonBuilder gsonb;
	private Gson gson;
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.customizeauto);
		sp = this.getSharedPreferences("UM", Context.MODE_PRIVATE);

		gsonb = new GsonBuilder();
		gson = gsonb.create();

		String jBancheDati = sp.getString("autobd", "");
		Type type = new TypeToken<List<Database>>(){}.getType();
		bancheDati = gson.fromJson(jBancheDati, type);
//		bancheDati2 = new ArrayList<QueryReply.Database>(bancheDati);
		Log.i(TAG,bancheDati.toString());
//		Log.i(TAG,bancheDati2.toString());
//		Collections.copy(bancheDati, bancheDati2);
		// edit.putString("elencobd", );
		// edit.putString("autobd", gson.toJson(arrBancheDati) );
		//
		setListAdapter(new ArrayAdapter<Database>(this,
				android.R.layout.simple_list_item_multiple_choice, bancheDati));

		listView = getListView();

		listView.setItemsCanFocus(false);
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

	}

	public void onOkClicked(View v) {
		SparseBooleanArray checked = listView.getCheckedItemPositions();
		Log.i(TAG,bancheDati.toString());
//		Log.i(TAG,bancheDati2.toString());
		for (int i = 0; i < listView.getAdapter().getCount(); i++) {
		    if (checked.get(i)) {
		    	Database s=bancheDati.get(i);
		        bdScelte.add(s);
		    }
		}
		Editor edit = sp.edit();
		Log.i(TAG,bdScelte.toString());
	    edit.putString("autoBdScelte",gson.toJson(bdScelte) );
	    edit.commit();
		Intent main = new Intent(this, UMAndroidClientActivity.class);
		this.startActivity(main);
	}
}
