package it.giammar;

import it.giammar.pratomodel.QueryReply.Database;
import it.giammar.pratomodel.QueryRequest;
import it.giammar.pratomodel.QueryRequest.Tipo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import com.thoughtworks.xstream.XStream;

public class UMAndroidClientActivity extends Activity implements
		OnClickListener, OnItemSelectedListener {
	private Button cerca;
	private EditText query;
	private CheckBox aPagamento;
	private Spinner bancaDati;
	private Spinner tipoRicerca;
	private Map<Database, Tipo[]> opzDiRicerca;
	private boolean isAuto = false;
	private Database scelto;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		riempiOpzioniDiRicerca();
		preparaWidgets();
		// setDefaultLogin();

	}

	private void riempiOpzioniDiRicerca() {
		opzDiRicerca = new HashMap<Database, Tipo[]>();
		opzDiRicerca.put(Database.ANAGRAFE, new Tipo[] { Tipo.NC, Tipo.CF,Tipo.IND });
		opzDiRicerca.put(Database.ANIA, new Tipo[] { Tipo.TARGA });
		opzDiRicerca.put(Database.CACOMM, new Tipo[] {Tipo.NC,Tipo.CF,Tipo.IND,Tipo.GEN});
		opzDiRicerca.put(Database.CARRABILI, new Tipo[] {Tipo.NC,Tipo.IND,Tipo.CARR});
		opzDiRicerca.put(Database.MCTC, new Tipo[] {Tipo.CF,Tipo.TARGA,Tipo.PATENTE});
		opzDiRicerca.put(Database.OTV, new Tipo[] {Tipo.IND,Tipo.GEN});
		opzDiRicerca.put(Database.PRA, new Tipo[] { Tipo.TARGA });
		opzDiRicerca.put(Database.RUBATI, new Tipo[] { Tipo.TARGA,Tipo.TELAIO });
		opzDiRicerca.put(Database.ZTL, new Tipo[] {Tipo.NC,Tipo.CF,Tipo.TARGA,Tipo.PERM});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.mainmenu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent pref = new Intent(this, PreferenzeActivity.class);
		this.startActivity(pref);
		return true;
	}

	// private void setDefaultLogin() {
	// SharedPreferences p=this.getPreferences(Context.MODE_PRIVATE);
	// Editor e = p.edit();
	// if (!p.contains("userName")) e.putString("userName", "test");
	// if (!p.contains("password")) e.putString("password", "test");
	// if (!p.contains("server")) e.putString("server",
	// "ufficiomobile.comune.prato.it");
	// if (!p.contains("port")) e.putString("port", "61613");
	// e.commit();
	// }

	private void preparaWidgets() {
		cerca = (Button) findViewById(R.id.cerca);
		query = (EditText) findViewById(R.id.query);
		aPagamento = (CheckBox) findViewById(R.id.aPagamento);
		bancaDati = (Spinner) findViewById(R.id.bancaDati);
		tipoRicerca = (Spinner) findViewById(R.id.tipoRicerca);
		cerca.setOnClickListener(this);
		Bundle nuovaRicerca=getIntent().getExtras();
		if (nuovaRicerca!=null) query.setText(nuovaRicerca.getString("query"));

		ArrayList<String> dbs = new ArrayList<String>();
		dbs.add("Auto");
		for (Database d : Database.values())
			dbs.add(d.toString());
		bancaDati.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, dbs));
		bancaDati.setOnItemSelectedListener(this);
	}

	@Override
	public void onClick(View v) {
		// switch (v.getId()) {
		// case R.id.cerca:
		QueryRequest qr = new QueryRequest();
		preparaQuery(qr);
		Intent sr = new Intent(this, SfogliaRisultatiActivity.class);
		XStream xstream = new XStream();
		sr.putExtra("query", xstream.toXML(qr));
		this.startActivity(sr);
		// break;
		// case R.id.bancaDati:
		//
		// break;
		// }
	}

	private void preparaQuery(QueryRequest qr) {
		qr.setQuery(query.getText().toString());
		qr.setAutomatic(isAuto);
		//TODO a pagamento!!!
		if (isAuto == false) {
			qr.addDove(scelto, (Tipo) tipoRicerca.getSelectedItem());
			
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		if (arg2 == 0) {
			isAuto = true;
			tipoRicerca.setEnabled(false);
		} else {
			isAuto = false;
			tipoRicerca.setEnabled(true);
			scelto = Database.values()[arg2 - 1];
			tipoRicerca.setAdapter(new ArrayAdapter<Tipo>(this,
					android.R.layout.simple_spinner_item, opzDiRicerca
							.get(scelto)));
		}
		System.out.println(isAuto);
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}

}