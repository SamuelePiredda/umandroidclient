package it.giammar;

import it.giammar.pratomodel.QueryRequest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

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
		cerca = (Button) findViewById(R.id.cerca);
		query = (EditText) findViewById(R.id.query);
		aPagamento = (CheckBox) findViewById(R.id.aPagamento);
		bancaDati = (Spinner) findViewById(R.id.bancaDati);
		cerca.setOnClickListener(this);
		connettiBackend();
	}

	private void connettiBackend() {
		// TODO Auto-generated method stub
		
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