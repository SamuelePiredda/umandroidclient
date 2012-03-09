package it.giammar;

import it.giammar.pratomodel.QueryRequest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import com.thoughtworks.xstream.XStream;

public class UMAndroidClientActivity extends Activity implements OnClickListener {
	private Button cerca;
	private EditText query;
	private CheckBox aPagamento;
	private Spinner bancaDati;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		preparaWidgets();
//		setDefaultLogin();
		
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.mainmenu,menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return true;
	}

//	private void setDefaultLogin() {
//		SharedPreferences p=this.getPreferences(Context.MODE_PRIVATE);
//		Editor e = p.edit();
//		if (!p.contains("userName")) e.putString("userName", "test");
//		if (!p.contains("password")) e.putString("password", "test");
//		if (!p.contains("server")) e.putString("server", "ufficiomobile.comune.prato.it");
//		if (!p.contains("port")) e.putString("port", "61613");
//		e.commit();
//	}


	private void preparaWidgets() {
		cerca = (Button) findViewById(R.id.cerca);
		query = (EditText) findViewById(R.id.query);
		aPagamento = (CheckBox) findViewById(R.id.aPagamento);
		bancaDati = (Spinner) findViewById(R.id.bancaDati);
		cerca.setOnClickListener(this);
	}

	
	@Override
	public void onClick(View v) {
		QueryRequest qr= new QueryRequest();
		preparaQuery(qr);
		Intent sr = new Intent(this, SfogliaRisultatiActivity.class);
		XStream xstream = new XStream();
		sr.putExtra("query", xstream.toXML(qr));
		this.startActivity(sr);
	}

	private void preparaQuery(QueryRequest qr) {
		qr.setQuery(query.getText().toString());
		qr.setAutomatic(true);
	}

	

	

} 	