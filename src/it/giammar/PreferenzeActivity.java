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
import android.widget.CheckBox;

public class PreferenzeActivity extends Activity implements OnClickListener {
	private static final String TAG = "Preferenze";
	private SharedPreferences sp;
	private EditText utente;
	private EditText password;
	private EditText host;
	private EditText port;
	private EditText attPort;
	private CheckBox useSSL;
	private Button salva;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.preferenze);
		sp = this.getSharedPreferences("UM", Context.MODE_PRIVATE);
		salva = (Button) findViewById(R.id.salva);
		utente = (EditText) findViewById(R.id.utente);
		password = (EditText) findViewById(R.id.password);
		host = (EditText) findViewById(R.id.host);
		port = (EditText) findViewById(R.id.port);
		attPort = (EditText) findViewById(R.id.AttPort);
		useSSL = (CheckBox) findViewById(R.id.UseSSL);
		utente.setText(sp.getString("utente", ""));
		password.setText(sp.getString("password", ""));
		host.setText(sp.getString("host", "ufficiomobile.comune.prato.it"));
		port.setText(sp.getString("port", "61613"));
		attPort.setText(sp.getString("attport", "18080"));
		useSSL.setChecked(sp.getBoolean("usessl",true));
		salva.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		boolean ok = true;
		if ("".equals(utente.getText().toString())) {
			utente.setError("utente obbligatorio");
			ok = false;
		}
		if ("".equals(password.getText().toString())) {
			password.setError("password obbligatorio");
			ok = false;
		}
		if ("".equals(host.getText().toString())) {
			host.setError("host obbligatorio");
			ok = false;
		}
		if ("".equals(port.getText().toString())) {
			port.setError("port obbligatorio");
			ok = false;
		}
		if ("".equals(attPort.getText().toString())) {
			attPort.setError("port obbligatorio");
			ok = false;
		}
		if (ok) {
			Editor edit = sp.edit();
			edit.putString("utente", utente.getText().toString());
			edit.putString("password", password.getText().toString());
			edit.putString("host", host.getText().toString());
			edit.putString("port", port.getText().toString());
			edit.putString("attport", attPort.getText().toString());
			edit.putBoolean("usessl", useSSL.isChecked());
			edit.commit();
			Intent main = new Intent(this, UMAndroidClientActivity.class);
			this.startActivity(main);
		}
	}
}
