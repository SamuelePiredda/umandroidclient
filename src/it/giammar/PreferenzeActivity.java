package it.giammar;

import static org.fusesource.stomp.client.Constants.DESTINATION;
import static org.fusesource.stomp.client.Constants.ID;
import static org.fusesource.stomp.client.Constants.SEND;
import static org.fusesource.stomp.client.Constants.SUBSCRIBE;
import it.giammar.pratomodel.QueryPermissions;
import it.giammar.pratomodel.QueryReply.Database;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.fusesource.hawtbuf.AsciiBuffer;
import org.fusesource.stomp.client.Future;
import org.fusesource.stomp.client.FutureConnection;
import org.fusesource.stomp.client.Stomp;
import org.fusesource.stomp.codec.StompFrame;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.thoughtworks.xstream.XStream;

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
		useSSL.setChecked(sp.getBoolean("usessl", true));
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

			aggiornaBancaDati();

			Intent main = new Intent(this, CustomizeAutoActivity.class);
			this.startActivity(main);
		}
	}

	public void aggiornaBancaDati() {

		// String elencoBD=sp.getString("elencobd", "");
		// messageShow("BD estratte" + elencoBD);

		Boolean ssl =  sp.getBoolean("usessl", false);
		String server = sp.getString("host", "");
		String utenteCompleto = sp.getString("utente", "");
		String password = sp.getString("password", "");

		

		//Scarica il logo del comune
//		scaricaLogo(server,ssl,comune);
		
		//Scarica le banche dati
		List<Database> arrBancheDati = scaricaBancheDati(utenteCompleto,
				password);
		String strBancheDati = joinDb(arrBancheDati, ", ");

		GsonBuilder gsonb = new GsonBuilder();
		Gson gson = gsonb.create();
		Editor edit = sp.edit();
		edit.putString("elencobd", strBancheDati);
		edit.putString("autobd", gson.toJson(arrBancheDati) );
		edit.commit();

		//Intent main = new Intent(this, UMAndroidClientActivity.class);
		//this.startActivity(main);

	}

	public List<Database> scaricaBancheDati(String utente, String password) {
		try {

			sp = this.getSharedPreferences("UM", Context.MODE_PRIVATE);
			Stomp stomp = Utilities.connectTcpOrSsl(getResources(), sp);
			Future<FutureConnection> future = stomp.connectFuture();
			FutureConnection connection = future.await();
			Log.d(TAG, "connesso a " + sp);
			// Lets setup a receive.. this does not block until you await it..
			Future<StompFrame> receiveFuture = connection.receive();

			StompFrame frame = new StompFrame(SUBSCRIBE);
			frame.addHeader(
					DESTINATION,
					StompFrame.encodeHeader("/temp-queue/qp"
							+ Utilities
									.imei(getSystemService(Context.TELEPHONY_SERVICE))));
			frame.addHeader(ID, connection.nextId());
			Future<StompFrame> response = connection.request(frame);

			// This unblocks once the response frame is received.
			response.await();

			frame = new StompFrame(SEND);
			frame.addHeader(DESTINATION,
					StompFrame.encodeHeader("/queue/queryPermissions"));
			XStream xstream = new XStream();
			QueryPermissions qp = new QueryPermissions();
			qp.setUserName(utente);
			qp.setPassword(password);
			frame.addHeader(
					StompFrame.encodeHeader("reply-to"),
					StompFrame.encodeHeader("/temp-queue/qp"
							+ Utilities
									.imei(getSystemService(Context.TELEPHONY_SERVICE))));
			frame.content(new AsciiBuffer(xstream.toXML(qp)));
			Future<Void> sendFuture = connection.send(frame);

			// This unblocks once the frame is accepted by the socket. Use it
			// to avoid flow control issues.
			sendFuture.await();

			StompFrame received = receiveFuture.await(15, TimeUnit.SECONDS);
			if (received == null) {
				// errore non ci Ã¨ arrivato niente
				// NB: non si puÃ² attendere all'infinito, Android ci blocca
				// l'app
				throw new FileNotFoundException(
						"non arrivata risposta dal server");
			}
			QueryPermissions qpr = (QueryPermissions) xstream.fromXML(received
					.contentAsString());

			EnumSet<Database> risultato = qpr.getDbAmmessi();
			List<Database> bancheDati = new ArrayList<Database>();
			for (Database db : risultato) {
				bancheDati.add(db);
			}
			return bancheDati;
		} catch (Exception e) {
			Log.e(TAG, "errore in richiesta acl", e);
			// TODO: anche in questo caso non ci Ã¨ arrivato niente, cosa
			// facciamo?
			List<Database> bancheDati = new ArrayList<Database>();
			// bancheDati.add(Database.ANIA);
			// bancheDati.add(Database.ANAGRAFE);
			// bancheDati.add(Database.CARRABILI); Database.MCTC, Database.OTV,
			// Database.PRA,
			// Database.RUBATI, Database.SIVES, Database.ZTL };
			return bancheDati;
		} finally {
			// TODO: chiudere tutte le connessioni
		}

	}

	public static String joinDb(List<Database> r, String d) {
		if (r.size() == 0)
			return "";
		StringBuilder sb = new StringBuilder();
		int i;
		boolean primo = true;
		for (Database db : r)
			if (primo) {
				sb.append(db.name());
				primo = false;
			} else
				sb.append(d + db.name());
		return sb.toString();
		// return sb.toString() + r[i].toString();
	}
}
