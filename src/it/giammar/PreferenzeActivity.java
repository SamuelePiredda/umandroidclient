package it.giammar;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

import it.giammar.pratomodel.QueryPermissions;
import it.giammar.pratomodel.QueryReply.CodErrore;
import it.giammar.pratomodel.QueryReply.Database;
import static org.fusesource.stomp.client.Constants.*;

import org.apache.http.util.ByteArrayBuffer;
import org.fusesource.hawtbuf.AsciiBuffer;
import org.fusesource.stomp.client.Future;
import org.fusesource.stomp.client.FutureConnection;
import org.fusesource.stomp.client.Stomp;
import org.fusesource.stomp.codec.StompFrame;

import com.thoughtworks.xstream.XStream;

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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.Toast;

public class PreferenzeActivity extends Activity implements OnClickListener {
	private static final String TAG = "Preferenze";
	private SharedPreferences sp;
	private EditText utente;
	private EditText password;
	private EditText host;
	private EditText port;
	private EditText attPort;
	private CheckBox useSSL;
	//private CheckBox abilitaStampa;
	//private EditText nomePackage;
	//private EditText nomeClass;
	private Button salva;
	private RelativeLayout layout;
	//private Button aggiorna;

	private String strErrore;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.preferenze);
		sp = this.getSharedPreferences("UM", Context.MODE_PRIVATE);
		layout=(RelativeLayout)findViewById(R.id.RelativeLayout1);
		salva = (Button) findViewById(R.id.salva);
		//aggiorna = (Button) findViewById(R.id.aggiorna);
		utente = (EditText) findViewById(R.id.utente);
		password = (EditText) findViewById(R.id.password);
		host = (EditText) findViewById(R.id.host);
		port = (EditText) findViewById(R.id.port);
		attPort = (EditText) findViewById(R.id.AttPort);
		useSSL = (CheckBox) findViewById(R.id.UseSSL);
		//abilitaStampa = (CheckBox) findViewById(R.id.abilitaStampa);
		//nomePackage = (EditText) findViewById(R.id.nomePackage);
		//nomeClass = (EditText) findViewById(R.id.nomeClass);
		utente.setText(sp.getString("utente", ""));
		password.setText(sp.getString("password", ""));
		host.setText(sp.getString("host", ""));
		port.setText(sp.getString("port", "61613"));
		attPort.setText(sp.getString("attport", "18080"));
		useSSL.setChecked(sp.getBoolean("usessl", true));
		//abilitaStampa.setChecked(sp.getBoolean("abilitastampa", false));
		//nomePackage.setText(sp.getString("nomepackage", ""));
		//nomeClass.setText(sp.getString("nomeclass", ""));
		salva.setOnClickListener(this);
		//abilitaStampa.setOnClickListener(new OnClickListener() {
		//	public void onClick(View v) {
		//		abilitaCampiStampa();
		//	}
		//});

		//tab  disabilitato temporaneamente
		
		
//		abilitaCampiStampa();
//		
//		TabHost tabHost=(TabHost)findViewById(R.id.tabHost);
//        tabHost.setup();
//      
//        TabSpec spec1=tabHost.newTabSpec("PRINCIPALE");
//        spec1.setContent(R.id.tab1);
//        spec1.setIndicator("PRINCIPALE");
//      
//        //tab  disabilitato temporaneamente
//        //TabSpec spec2=tabHost.newTabSpec("STAMPA");
//        //spec2.setIndicator("STAMPA");
//        //spec2.setContent(R.id.tab2);
//        LinearLayout tab2=(LinearLayout)findViewById(R.id.tab2);
//        tab2.setVisibility(View.INVISIBLE);
//      
//        LinearLayout tab1=(LinearLayout)findViewById(R.id.tab1);
//        tab1.setVisibility(View.VISIBLE);
//      
//        
//        tabHost.addTab(spec1);
//        //tabHost.addTab(spec2);
//
//        tabHost.setCurrentTab(0);

		
		
		
	}

//	private void abilitaCampiStampa() {
//		if (abilitaStampa.isChecked()) {
//			nomePackage.setEnabled(true);
//			nomeClass.setEnabled(true);
//		}
//		else {
//			nomePackage.setEnabled(false);
//			nomeClass.setEnabled(false);
//			//nomePackage.setText("");
//			//nomeClass.setText("");
//		}
//	}
	
	// public EnumSet<Database> getPermissions() {
	//
	// }
	@Override
	public void onClick(View v) {
		
		//Toast.makeText(layout.getContext(), "Connessione...",
		//		Toast.LENGTH_LONG).show();
		
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
			//cancella logo precedente
			String strDest=getFilesDir() + "/logo_pers.png";
			File file = new File(strDest);
			boolean deleted = file.delete();
			
			Editor edit = sp.edit();
			edit.putString("utente", utente.getText().toString());
			edit.putString("password", password.getText().toString());
			edit.putString("host", host.getText().toString());
			edit.putString("port", port.getText().toString());
			edit.putString("attport", attPort.getText().toString());
			edit.putBoolean("usessl", useSSL.isChecked());
//			edit.putBoolean("abilitastampa", abilitaStampa.isChecked());
//			edit.putString("nomepackage", nomePackage.getText().toString());
//			edit.putString("nomeclass", nomeClass.getText().toString());
			edit.commit();
			
			aggiornaBancaDati();
			
			Intent main = new Intent(this, UMAndroidClientActivity.class);
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

		String[] arrUtente = utenteCompleto.split("/");
		String utenteSpecifico ="";
		String comune = "";
		if (arrUtente.length > 1) {
			utenteSpecifico=arrUtente[1];
			comune = arrUtente[0];
		}

		//Scarica il logo del comune
//		scaricaLogo(server,ssl,comune);
		
		//Scarica le banche dati
		strErrore="";
		List<Database> arrBancheDati = scaricaBancheDati(comune, utenteSpecifico,
				password);
		String strBancheDati;
		if (!strErrore.equals("")) {
			strBancheDati = "Nessuna";
			Toast.makeText(layout.getContext(), strErrore,
					Toast.LENGTH_LONG).show();
		}
		else {
			strBancheDati = joinDb(arrBancheDati, ", ");
		}

		Editor edit = sp.edit();
		edit.putString("elencobd", strBancheDati);
		edit.commit();

		//Intent main = new Intent(this, UMAndroidClientActivity.class);
		//this.startActivity(main);

	}

	public void scaricaLogo(String server, Boolean ssl, String comune) {
		try {
			String str = "";
			if (ssl) str += "https://"; else str += "http://";
			str+=server + "/img/Logos/logo_" + comune + ".png";
			
			String strDest=getFilesDir() + "/logo_pers.png";
			
            URL url = new URL(str); //you can write here any link
            File file = new File(strDest);
            file.delete();
            
            file = new File(strDest);

            
            URLConnection ucon = url.openConnection();
            InputStream is = ucon.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            /*
             * Read bytes to the Buffer until there is nothing more to read(-1).
             */
            ByteArrayBuffer baf = new ByteArrayBuffer(50);
            int current = 0;
            while ((current = bis.read()) != -1) {
                    baf.append((byte) current);
            }
            
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(baf.toByteArray());
            fos.close();
            
	    } catch (IOException e) {
	    	String ex = e.getMessage();
	         
	    }
	}
	
	public List<Database> scaricaBancheDati(String comune, String utente,
			String password) {
		try {
			
			sp = this.getSharedPreferences("UM", Context.MODE_PRIVATE);
			Stomp stomp = Utilities.connectTcpOrSsl(getResources(),sp);
			Future<FutureConnection> future = stomp.connectFuture();
			FutureConnection connection = future.await();
			Log.d(TAG, "connesso a "+sp);
			// Lets setup a receive.. this does not block until you await it..
			Future<StompFrame> receiveFuture = connection.receive();

			StompFrame frame = new StompFrame(SUBSCRIBE);
			frame.addHeader(DESTINATION,
					StompFrame.encodeHeader("/temp-queue/qp"+Utilities.imei( getSystemService(Context.TELEPHONY_SERVICE))));
			frame.addHeader(ID, connection.nextId());
			Future<StompFrame> response = connection.request(frame);

			// This unblocks once the response frame is received.
			response.await();

			frame = new StompFrame(SEND);
			frame.addHeader(DESTINATION,
					StompFrame.encodeHeader("/queue/queryPermissions"));
			frame.addHeader(StompFrame.encodeHeader("stomp"),
					StompFrame.encodeHeader("yes"));
			XStream xstream = new XStream();
			QueryPermissions qp = new QueryPermissions();
			qp.setUserName(comune + "/" + utente);
			qp.setPassword(password);
			frame.addHeader(
					StompFrame.encodeHeader("reply-to"),
					StompFrame.encodeHeader("/temp-queue/qp"+Utilities.imei( getSystemService(Context.TELEPHONY_SERVICE))));
			frame.content(new AsciiBuffer(xstream.toXML(qp)));
			Future<Void> sendFuture = connection.send(frame);

			// This unblocks once the frame is accepted by the socket. Use it
			// to avoid flow control issues.
			sendFuture.await();

			StompFrame received = receiveFuture.await(15, TimeUnit.SECONDS);
			if (received == null) {
				// errore non ci è arrivato niente
				// NB: non si può attendere all'infinito, Android ci blocca
				// l'app
				throw new FileNotFoundException("non arrivata risposta dal server");
			}
			QueryPermissions qpr =  (QueryPermissions) xstream.fromXML(received
					.contentAsString());
			if (qpr.getRetCode().equals(CodErrore.NONAUTORIZZATO)) {
				strErrore="Utente non valido o password errata";
			}
			
			EnumSet<Database> risultato=qpr.getDbAmmessi();
			List<Database> bancheDati= new ArrayList<Database>();
			for (Database db:risultato) {
				bancheDati.add(db);
			}
			return bancheDati;
		} catch (Exception e) {
			Log.e(TAG, "errore in richiesta acl",e);
			// TODO: anche in questo caso non ci è arrivato niente, cosa facciamo?
			List<Database> bancheDati= new ArrayList<Database>();
			strErrore="Server non disponibile";
			//bancheDati.add(Database.ANIA);
			//bancheDati.add(Database.ANAGRAFE);
			//bancheDati.add(Database.CARRABILI); Database.MCTC, Database.OTV, Database.PRA,
			//		Database.RUBATI, Database.SIVES, Database.ZTL };
			return bancheDati;
		}
		finally {
			//TODO: chiudere tutte le connessioni
		}
		
	}

	public static String joinDb(List<Database> r, String d) {
		if (r.size() == 0)
			return "";
		StringBuilder sb = new StringBuilder();
		int i;
		boolean primo=true;
		for (Database db:r)
			if (primo) {
				sb.append(db.toString());
				primo=false;
			}
			else
				sb.append(d + db.toString());
		return sb.toString();
		//return sb.toString() + r[i].toString();
	}

}
