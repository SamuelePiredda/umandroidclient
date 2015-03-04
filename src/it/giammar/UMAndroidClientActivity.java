package it.giammar;

import it.giammar.pratomodel.QueryReply.Database;
import it.giammar.pratomodel.QueryRequest;
import it.giammar.pratomodel.QueryRequest.Tipo;

import java.io.*;
import java.net.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.util.ByteArrayBuffer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;
import android.view.*;
import android.widget.RelativeLayout;

import com.thoughtworks.xstream.XStream;

public class UMAndroidClientActivity extends Activity implements
		OnClickListener, OnItemSelectedListener {
	private static final String TAG = "AndroidClient";

	private Button cerca;
	private Button btnBancaDati;
	private Button btnTipoRicerca;
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
	private RelativeLayout layout;
	private SharedPreferences sp;
	
	private Activity io = this;
	
	private String[] arrElencoBD;
	private String[] arrTipi;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sp = this.getSharedPreferences("UM", Context.MODE_PRIVATE);
		setContentView(R.layout.main);
		riempiOpzioniDiRicerca();
		preparaWidgets();
		
		caricaLogo();
		
		
		//DownloadFromUrl(getFilesDir() + "/prova.png");
		
		//Display getOrient = ((WindowManager)getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
		//if (getOrient.getOrientation()==Surface.ROTATION_90 | getOrient.getOrientation()==Surface.ROTATION_270)	
		//{
		//	RelativeLayout rl=(RelativeLayout)findViewById(R.id.RelativeLayout1);
		//	rl.setBackgroundResource(R.drawable.sfondo_app_oriz);
		//}
		// setDefaultLogin();

	}

	private void caricaLogo() {
		File f = new File(getFilesDir() + "/logo_pers.png");
		Bitmap bmImg;
		if (f.exists()) {
			bmImg = BitmapFactory.decodeFile(getFilesDir() + "/logo_pers.png");
		}
		else {
			bmImg = BitmapFactory.decodeResource(getResources(),R.drawable.lince);
		}
			
		ImageView imageView= (ImageView)findViewById(R.id.imageView1);
		imageView.setImageBitmap(bmImg);
		imageView.setAlpha(120);
		int w;
		int h;
		Display getOrient = ((WindowManager)getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
		if (getOrient.getOrientation()==Surface.ROTATION_90 | getOrient.getOrientation()==Surface.ROTATION_270)	{
			w = (int)(getOrient.getHeight()/1.5);
			h = (w * bmImg.getHeight())/bmImg.getWidth();
		}
		else {
			w = (int)(getOrient.getWidth()/1.5);
			h = (w * bmImg.getHeight())/bmImg.getWidth();
		}
		
		RelativeLayout.LayoutParams lp=new RelativeLayout.LayoutParams(w,h);
		lp.addRule(RelativeLayout.CENTER_VERTICAL);
		lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
		imageView.setLayoutParams(lp);
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
		opzDiRicerca.put(Database.SIVES, new Tipo[] { 
				Tipo.TARGA });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.mainmenu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()){
	        case R.id.preferenze:
	        	Intent pref = new Intent(this, PreferenzeActivity.class);
	    		this.startActivity(pref);
		        break;
	        case R.id.aggiornamenti:
	        	//scaricaApk();
	        	//Intent intent = new Intent(Intent.ACTION_VIEW);
	        	//intent.setDataAndType(Uri.fromFile(new File(getFilesDir() + "/ufficiomobileandroid.apk")), "application/vnd.android.package-archive");
	        	//startActivity(intent); 
	        	
				try {
					
					int versionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
					int newVersionCode = scaricaVers();
					if (newVersionCode==-1) {
						Toast.makeText(layout.getContext(), "Non è possibile collegarsi al server. Riprovare in un secondo momento.",
								Toast.LENGTH_LONG).show();
					}
					else if (newVersionCode>versionCode) {
						Intent promptInstall = new Intent(Intent.ACTION_VIEW,Uri.parse("http://mobilynx-bo.lansystems.it/files/ufficiomobileandroid.apk")); 
			        	startActivity(promptInstall);
					}
					else {
						Toast.makeText(layout.getContext(), "Nessun aggiornamento",
								Toast.LENGTH_LONG).show();
						
					}
				} catch (NameNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        	
	        	
	        	break;
	  }

		
		
		return true;
	}

	public int scaricaVers() {
		
		try {
			
			String str = "http://mobilynx-bo.lansystems.it/files/vers.txt";
			
		    // Create a URL for the desired page
		    URL url = new URL(str);

		    // Read all the text returned by the server
		    BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
		    String line;
		    line = in.readLine();
		    
		    
		    in.close();
		    return Integer.parseInt(line);
		    
		} catch (MalformedURLException e) {
			return -1;
		} catch (IOException e) {
			return -1;
		}
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
		layout =  (RelativeLayout) findViewById(R.id.RelativeLayout1);
		btnBancaDati = (Button) findViewById(R.id.btnBancaDati);
		btnTipoRicerca = (Button) findViewById(R.id.btnTipoRicerca);
		
		btnBancaDati.setText("Auto");
		btnTipoRicerca.setEnabled(false);
		
		bancaDati.setVisibility(View.INVISIBLE);
		tipoRicerca.setVisibility(View.INVISIBLE);
		btnBancaDati.setOnClickListener(new OnClickListener()
	    {
		      public void onClick(View v)
		      {
		    	  String elencoBD=sp.getString("elencobd", "");
		    	  if (elencoBD.equals("")|elencoBD.equals("Nessuna")) {
			    	  AlertDialog.Builder builder2 = new AlertDialog.Builder(io);
						builder2.setMessage("Utente non Impostato. Nessuna banca dati attiva. Si prega di adare in 'Preferenze', impostare l'utente e salvare.")
								.setCancelable(true)
								.setPositiveButton("OK",
									new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog,
												int id) {
											
										}
									});
						AlertDialog alert = builder2.create();
						alert.show();
		    	  }
		    	  else {
					
		    	  DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
	            	    @Override
	            	    public void onClick(DialogInterface dialog, int which) {
	            	    	btnBancaDati.setText(arrElencoBD[which]);
	            	    	
	            	    	if (which == 0) {
	            				isAuto = true;
	            				btnTipoRicerca.setEnabled(false);
	            			} else {
	            				isAuto = false;
	            				btnTipoRicerca.setEnabled(true);
	            				//dbScelto = Database.values()[arg2 - 1];
	            				dbScelto = Database.valueOf(arrElencoBD[which]);
	            				Tipo[] tipi= opzDiRicerca.get(dbScelto);
	            				String strTipi="";
	            				for (Tipo t:tipi) {
	            					strTipi+= t.toString() + ", ";
	            				}
	            				arrTipi=strTipi.split(", ");
	            				
	            				btnTipoRicerca.setText(arrTipi[0]);
	            				
	            				//tipoRicerca.setAdapter(new ArrayAdapter<Tipo>(this,
	            				//		android.R.layout.simple_spinner_item, opzDiRicerca
	            				//				.get(dbScelto)));
	            			}
	            	    	
	            	    	//Tipo t = (Tipo) btnTipoRicerca.getText().toString();
	            	    	visualizzaMCTC(isAuto == false
	            					&& (Database.MCTC.equals(dbScelto) && Tipo.NC.toString().equals(btnTipoRicerca.getText().toString().toUpperCase())));
	            	    }
	            	};

	            	AlertDialog.Builder builder = new AlertDialog.Builder(io);
	            	builder.setTitle("Seleziona la Banca Dati")
	            		.setItems(arrElencoBD, dialogClickListener)
	            		.show();
		    	  }
		      }
	    });
		
		btnTipoRicerca.setOnClickListener(new OnClickListener()
	    {
		      public void onClick(View v)
		      {
		    	  DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
	            	    @Override
	            	    public void onClick(DialogInterface dialog, int which) {
	            	    	btnTipoRicerca.setText(arrTipi[which]);
	            	    	
	            	    	
	            	    	visualizzaMCTC(isAuto == false
	            					&& (Database.MCTC.equals(dbScelto) && Tipo.NC.toString().equals(btnTipoRicerca.getText().toString().toUpperCase())));
	            	    }
	            	};

	            	AlertDialog.Builder builder = new AlertDialog.Builder(io);
	            	builder.setTitle("Ricercare per:")
	            		.setItems(arrTipi, dialogClickListener).show();
		      }
	    });
		
		Bundle nuovaRicerca = getIntent().getExtras();
		if (nuovaRicerca != null)
			query.setText(nuovaRicerca.getString("query"));

		ArrayList<String> dbs = new ArrayList<String>();
		//dbs.add("Auto");
		
		//gestione BD abilitate per utente
		try {
		
			String elencoBD="Auto, " + sp.getString("elencobd", "");
			arrElencoBD = elencoBD.split(", ");
			for(String str:arrElencoBD)
				dbs.add(str);
		}
		catch (Exception ex) {}
		
		//for (Database d : Database.values())
		//	dbs.add(d.toString());
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
		//Tipo tpScelto = (Tipo) tipoRicerca.getSelectedItem();
		 Tipo tpScelto=null;
		if (isAuto == false) tpScelto= Tipo.valueOf(btnTipoRicerca.getText().toString());
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
				//dbScelto = Database.values()[arg2 - 1];
				dbScelto = Database.valueOf(bancaDati.getSelectedItem().toString());
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