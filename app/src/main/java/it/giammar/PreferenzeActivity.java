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
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.thoughtworks.xstream.XStream;

//public class PreferenzeActivity extends Activity implements OnClickListener {
public class PreferenzeActivity extends Activity {
	private static final String TAG = "Preferenze";
	private SharedPreferences sp;
	private EditText utente;
	private EditText password;
	private EditText host;
	private EditText port;
	private EditText attPort;
	private CheckBox useSSL;
	private Button salva;
	private Button configAuto;
	private TextView textView6;
	private TextView textView5;
	
	private PreferenzeDataSource datasource;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.preferenze);
		sp = this.getSharedPreferences("UM", Context.MODE_PRIVATE);
		salva = (Button) findViewById(R.id.salva);
		configAuto = (Button) findViewById(R.id.configAuto);
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
		textView6 = (TextView) findViewById(R.id.textView6);
		textView5 = (TextView) findViewById(R.id.textView5);
		
		textView5.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
    			Intent main = new Intent(PreferenzeActivity.this, ConfigurazioneActivity.class);
    			//main.putExtra("MODE_AUTO", 0);
    			//this.startActivity(main);
    			startActivityForResult(main, 123);
				return true;
			}
		});
		
        salva.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
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

        		    //ArrayAdapter<Preferenze> adapter = (ArrayAdapter<Preferenze>) getListAdapter();
        		    //Preferenze preferenze = null;

        		    Integer i = datasource.getCountPreferenzeByHost(host.getText().toString());

        		    int tmpUsessl=0;
        		    if (useSSL.isChecked()==true) {
        		     tmpUsessl=2;
        		    }
        		    Preferenze preferenza;
        		    if (i == 0) {
	        		    //String stUtente, String stPassword, String stHost, String stPort, String stAttport, int iUsessl
	        		    preferenza = datasource.createPreferenza(utente.getText().toString(),password.getText().toString(), host.getText().toString(),port.getText().toString(), attPort.getText().toString(),tmpUsessl);
	        		    //adapter.add(preferenza);
        		    }
        		    if (i == 1) {
        		    	preferenza = datasource.getAllPreferenzeByHost(host.getText().toString());
        		    	datasource.deletePreferenze(preferenza.getId());
        		    	preferenza = datasource.createPreferenza(utente.getText().toString(),password.getText().toString(), host.getText().toString(),port.getText().toString(), attPort.getText().toString(),tmpUsessl);
        		    }

        			aggiornaBancaDati();

//        			Intent main = new Intent(PreferenzeActivity.this, CustomizeAutoActivity.class);
//        			main.putExtra("MODE_AUTO", 0);
//        			//this.startActivity(main);
//        			startActivity(main);
        			
        			finish();
        		}
            }
        });
        configAuto.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
    			Intent main = new Intent(PreferenzeActivity.this, CustomizeAutoActivity.class);
    			main.putExtra("MODE_AUTO", 1);
    			//this.startActivity(main);
    			startActivity(main);
            }
        });
		//salva.setOnClickListener(this);
		//configAuto.setOnClickListener(this);

	    datasource = new PreferenzeDataSource(this);
	    datasource.open();

	    //List<Preferenze> values = datasource.getAllPreferenze();

	    // use the SimpleCursorAdapter to show the
	    // elements in a ListView
/*	    ArrayAdapter<Comment> adapter = new ArrayAdapter<Comment>(this,
	        android.R.layout.simple_list_item_1, values);
	    setListAdapter(adapter);*/

	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

	    if (requestCode == 123) {
	        if(resultCode == RESULT_OK){
	            //String result=data.getExtras()getStringExtra("result");
	        	
	        	Intent intent = data;
	        	if (data.getExtras() != null) {
	        	  String sthost = intent.getStringExtra("host");
	        	  String stutente = intent.getStringExtra("utente");
	        	  String stpassword = intent.getStringExtra("password");
	        	  String stport = intent.getStringExtra("port");
	        	  String stattport = intent.getStringExtra("attport");
	        	  String usessl = intent.getStringExtra("usessl");

	        	  long l = Long.parseLong(usessl);
	        	  
	      		  utente.setText(stutente);
	    		  password.setText(stpassword);
	    		  host.setText(sthost);
	    		  port.setText(stport);
	    		  attPort.setText(stattport);
	    		  if (l == 0) {
	    			  useSSL.setChecked(sp.getBoolean("usessl", false));  
	    		  }
	    		  else {
	    			  useSSL.setChecked(sp.getBoolean("usessl", true));
	    		  }
	        	  //Toast.makeText(getApplicationContext(), "hai selezionato " + host.toString() + " " + utente.toString(), Toast.LENGTH_SHORT).show();
	        	}


	            
	        }
	        if (resultCode == RESULT_CANCELED) {
	            //Write your code if there's no result
	        }
	    }
	}//onActivityResult

//	@Override
//	public void onClick(View v) {
//		boolean ok = true;
//		if ("".equals(utente.getText().toString())) {
//			utente.setError("utente obbligatorio");
//			ok = false;
//		}
//		if ("".equals(password.getText().toString())) {
//			password.setError("password obbligatorio");
//			ok = false;
//		}
//		if ("".equals(host.getText().toString())) {
//			host.setError("host obbligatorio");
//			ok = false;
//		}
//		if ("".equals(port.getText().toString())) {
//			port.setError("port obbligatorio");
//			ok = false;
//		}
//		if ("".equals(attPort.getText().toString())) {
//			attPort.setError("port obbligatorio");
//			ok = false;
//		}
//		if (ok) {
//			Editor edit = sp.edit();
//			edit.putString("utente", utente.getText().toString());
//			edit.putString("password", password.getText().toString());
//			edit.putString("host", host.getText().toString());
//			edit.putString("port", port.getText().toString());
//			edit.putString("attport", attPort.getText().toString());
//			edit.putBoolean("usessl", useSSL.isChecked());
//			edit.commit();
//			
////			//
//		    //ArrayAdapter<Preferenze> adapter = (ArrayAdapter<Preferenze>) getListAdapter();
//		    //Preferenze preferenze = null;
//
//		      String[] preferenze1 = new String[] { "Cool", "Very nice", "Hate it" };
//		      int nextInt = new Random().nextInt(3);
//		      // save the new comment to the database
//		      int tmpUsessl=0;
//		      if (useSSL.isChecked()) {
//		    	  tmpUsessl=2;
//		      }
//		      Preferenze preferenza = datasource.createPreferenza(utente.getText().toString(),password.getText().toString(), host.getText().toString(),port.getText().toString(), attPort.getText().toString(),tmpUsessl);
//		      //adapter.add(preferenza);
//
////			//
//
//			aggiornaBancaDati();
//
//			Intent main = new Intent(this, CustomizeAutoActivity.class);
//			this.startActivity(main);
//		}
//	}

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
//		List<Database> temp = new ArrayList<Database>();
//		temp.add(Database.MCTC);
//		return temp;
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
