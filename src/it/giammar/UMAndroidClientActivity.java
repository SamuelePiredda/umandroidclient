package it.giammar;

import it.giammar.pratomodel.QueryReply.Database;
import it.giammar.pratomodel.QueryRequest;
import it.giammar.pratomodel.QueryRequest.Tipo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import android.widget.TextView;

import com.thoughtworks.xstream.XStream;

public class UMAndroidClientActivity extends Activity implements
		OnClickListener, OnItemSelectedListener {
	private static final String TAG = "AndroidClient";

	private Button cerca;
	private EditText query;
	private CheckBox aPagamento;
	private Spinner bancaDati;
	private Spinner tipoRicerca;
	private Map<Database, Tipo[]> opzDiRicerca;
	private boolean isAuto = false;
	private Database dbScelto;

	private EditText nascita;
	private EditText comune;
	private EditText provincia;
	private TextView textView1;
	private TextView textView2;
	private TextView textView3;

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
		opzDiRicerca.put(Database.ANAGRAFE, new Tipo[] { Tipo.NC, Tipo.CF, });
		opzDiRicerca.put(Database.ANIA, new Tipo[] { Tipo.TARGA });
		opzDiRicerca.put(Database.CACOMM, new Tipo[] { Tipo.NC, Tipo.CF,
				Tipo.IND, Tipo.GEN });
		opzDiRicerca.put(Database.CARRABILI, new Tipo[] { Tipo.NC, Tipo.IND,
				Tipo.CARR });
		opzDiRicerca.put(Database.MCTC, new Tipo[] { Tipo.CF, Tipo.TARGA,
				Tipo.PATENTE, Tipo.NC });
		opzDiRicerca.put(Database.OTV, new Tipo[] { Tipo.IND, Tipo.GEN, Tipo.NUMORD });
		opzDiRicerca.put(Database.PRA, new Tipo[] { Tipo.TARGA });
		opzDiRicerca.put(Database.RUBATI,
				new Tipo[] { Tipo.TARGA, Tipo.TELAIO });
		opzDiRicerca.put(Database.ZTL, new Tipo[] { Tipo.NC, Tipo.CF,
				Tipo.TARGA, Tipo.PERM });
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

	private void preparaWidgets() {
		cerca = (Button) findViewById(R.id.cerca);
		query = (EditText) findViewById(R.id.query);
		aPagamento = (CheckBox) findViewById(R.id.aPagamento);
		bancaDati = (Spinner) findViewById(R.id.bancaDati);
		tipoRicerca = (Spinner) findViewById(R.id.tipoRicerca);
		cerca.setOnClickListener(this);
		nascita = (EditText) findViewById(R.id.nascita);
		comune = (EditText) findViewById(R.id.comune);
		provincia = (EditText) findViewById(R.id.provincia);
		textView1 = (TextView) findViewById(R.id.textView1);
		textView2 = (TextView) findViewById(R.id.textView2);
		textView3 = (TextView) findViewById(R.id.textView3);

		Bundle nuovaRicerca = getIntent().getExtras();
		if (nuovaRicerca != null)
			query.setText(nuovaRicerca.getString("query"));

		ArrayList<String> dbs = new ArrayList<String>();
		dbs.add("Auto");
		for (Database d : Database.values())
			dbs.add(d.toString());
		bancaDati.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, dbs));
		bancaDati.setOnItemSelectedListener(this);
		tipoRicerca.setOnItemSelectedListener(this);
	}

	@Override
	public void onClick(View v) {
		QueryRequest qr = new QueryRequest();
		boolean ok = preparaQuery(qr);
		if (ok) {
			Intent sr = new Intent(this, SfogliaRisultatiActivity.class);
			XStream xstream = new XStream();
			sr.putExtra("query", xstream.toXML(qr));
			this.startActivity(sr);
		}

	}

	private boolean preparaQuery(QueryRequest qr) {
		boolean ok = true;
		Tipo tpScelto = (Tipo) tipoRicerca.getSelectedItem();
		// TODO finire validazioni campi
		if ("".equals(query.getText().toString())) {
			query.setError("testo obbligatorio");
			ok = false;
		}
		qr.setQuery(query.getText().toString());
		if (isAuto == false
				&& (dbScelto.equals(Database.MCTC) && tpScelto.equals(Tipo.NC))) {

			qr.setComune(comune.getText().toString());
			try {
				qr.setNascita(new SimpleDateFormat("dd/MM/yyyy").parse(nascita
						.getEditableText().toString()));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				nascita.setError("data non riconosciuta");
				ok = false;
			}
			qr.setProvincia(provincia.getText().toString());
		}
		qr.setAutomatic(isAuto);
		qr.setaPagamento(aPagamento.isChecked());
		if (isAuto == false) {
			qr.addDove(dbScelto, tpScelto);

		}
		return ok;
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		Tipo t=null;
		switch (arg0.getId()) {

		case R.id.bancaDati:
			if (arg2 == 0) {
				isAuto = true;
				tipoRicerca.setEnabled(false);
			} else {
				isAuto = false;
				tipoRicerca.setEnabled(true);
				dbScelto = Database.values()[arg2 - 1];
				tipoRicerca.setAdapter(new ArrayAdapter<Tipo>(this,
						android.R.layout.simple_spinner_item, opzDiRicerca
								.get(dbScelto)));
			}
			// visualizzaMCTC(isAuto == false
			// || (dbScelto != null && dbScelto.equals(Database.MCTC)));

//			System.out.println(isAuto);
			break;
		case R.id.tipoRicerca:
			t = (Tipo) tipoRicerca.getSelectedItem();
//			visualizzaMCTC(t.equals(Tipo.NC) && dbScelto.equals(Database.MCTC));
			break;
		}
		visualizzaMCTC(isAuto == false
				&& (Database.MCTC.equals(dbScelto) && Tipo.NC.equals(t)));

	}

	private void visualizzaMCTC(boolean b) {
		int view;
		if (b == true)
			view = View.VISIBLE;
		else
			view = View.INVISIBLE;
		nascita.setVisibility(view);
		comune.setVisibility(view);
		provincia.setVisibility(view);
		textView1.setVisibility(view);
		textView2.setVisibility(view);
		textView3.setVisibility(view);

	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}

}