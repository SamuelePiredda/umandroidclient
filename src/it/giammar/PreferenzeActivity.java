package it.giammar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class PreferenzeActivity extends Activity implements OnClickListener {
	private SharedPreferences sp;
	private EditText utente;
	private EditText password;
	private EditText host;
	private EditText port;
	private Button   salva;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.preferenze);
		sp=this.getSharedPreferences("UM",Context.MODE_PRIVATE);
		salva = (Button) findViewById(R.id.salva);
		utente = (EditText) findViewById(R.id.utente);
		password = (EditText) findViewById(R.id.password);
		host = (EditText) findViewById(R.id.host);
		port = (EditText) findViewById(R.id.port);
		utente.setText(sp.getString("utente", ""));
		password.setText(sp.getString("password", ""));
		host.setText(sp.getString("host", "ufficiomobile.comune.prato.it"));
		port.setText(sp.getString("port", "61613"));
		salva.setOnClickListener(this);

	}
	@Override
	public void onClick(View v) {
		Editor edit=sp.edit();
		edit.putString("utente", utente.getText().toString());
		edit.putString("password", password.getText().toString());
		edit.putString("host", host.getText().toString());
		edit.putString("port", port.getText().toString());
		edit.commit();
		Intent main = new Intent(this, UMAndroidClientActivity.class);
		this.startActivity(main);
	}

}
