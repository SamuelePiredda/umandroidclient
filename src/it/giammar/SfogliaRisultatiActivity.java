package it.giammar;

import static org.fusesource.stomp.client.Constants.DESTINATION;
import static org.fusesource.stomp.client.Constants.ID;
import static org.fusesource.stomp.client.Constants.MESSAGE_ID;
import static org.fusesource.stomp.client.Constants.SEND;
import static org.fusesource.stomp.client.Constants.SUBSCRIBE;
import it.giammar.pratomodel.QueryReply;
import it.giammar.pratomodel.QueryReply.Database;
import it.giammar.pratomodel.QueryRequest;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.fusesource.hawtbuf.AsciiBuffer;
import org.fusesource.stomp.client.BlockingConnection;
import org.fusesource.stomp.client.Stomp;
import org.fusesource.stomp.codec.StompFrame;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.thoughtworks.xstream.XStream;

public class SfogliaRisultatiActivity extends Activity implements
		OnItemClickListener {
	private static final int SWIPE_MIN_DISTANCE = 120;
	private static final int SWIPE_MAX_OFF_PATH = 250;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;
	private static Random randomGenerator = new Random();
	private static String fakeImei = Integer.valueOf(randomGenerator.nextInt())
			.toString();
	private static String imei = "";
	private GestureDetector gestureDetector;
	private View.OnTouchListener gestureListener;
	private Animation slideLeftIn;
	private Animation slideLeftOut;
	private Animation slideRightIn;
	private Animation slideRightOut;
	private ViewFlipper viewFlipper;
	private ProgressBar progress;
	private QueryRequest qr;
	LayoutInflater inflater;
	private Activity io = this;
	private Map<QueryReply.Database, LinearLayout> visBancheDati = new HashMap<QueryReply.Database, LinearLayout>();
	private Map<QueryReply.Database, List<String>> righeBancheDati = new HashMap<QueryReply.Database, List<String>>();
	private SharedPreferences sp;
	protected BlockingConnection connection;
	private EffettuaQuery eq;
	final byte[] passPhrase = { (byte) 0x08, (byte) 0x09, (byte) 0x0A,
			(byte) 0x0B, (byte) 0x0C, (byte) 0x0D, (byte) 0x0E, (byte) 0x0F,
			(byte) 0x10, (byte) 0x11, (byte) 0x12, (byte) 0x13, (byte) 0x14,
			(byte) 0x15, (byte) 0x16, (byte) 0x17 };
	private int totaleBD;
	private int bdArrivate;
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		eq.cancel(true);
		chiudiTutto();
	}

	private void chiudiTutto() {
		if (eq != null && eq.getStatus() != AsyncTask.Status.FINISHED) {
			eq.cancel(true);
			eq = null;
		}
		if (connection != null)
			try {
				connection.close();
				connection = null;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		chiudiTutto();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sfogliarisultati);
		XStream xstream = new XStream();
		System.out.println(getIntent().getExtras().getString("query"));
		inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		viewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper1);
		progress = (ProgressBar) findViewById(R.id.progressBar1);
		slideLeftIn = AnimationUtils.loadAnimation(this,
				android.R.anim.slide_in_left);
		slideLeftOut = AnimationUtils.loadAnimation(this,
				android.R.anim.slide_out_right);
		slideRightIn = AnimationUtils.loadAnimation(this,
				android.R.anim.slide_in_left);
		slideRightOut = AnimationUtils.loadAnimation(this,
				android.R.anim.slide_out_right);
		gestureDetector = new GestureDetector(new MyGestureDetector());
		gestureListener = new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (gestureDetector.onTouchEvent(event)) {
					return true;
				}
				return false;
			}
		};
		viewFlipper.setOnTouchListener(gestureListener);

		associaBancheDatiaViews();
		// riempiFlipperconBancheDati();

		sp = this.getSharedPreferences("UM", Context.MODE_PRIVATE);

		qr = (QueryRequest) xstream.fromXML(getIntent().getExtras().getString(
				"query"));
		eq = new EffettuaQuery();
		eq.execute(qr);

	}

	@Deprecated
	private void riempiFlipperconBancheDati() {
		for (Entry<Database, LinearLayout> es : visBancheDati.entrySet()) {
			viewFlipper.addView(es.getValue());
		}

	}

	private void associaBancheDatiaViews() {
		for (Database db : Database.values()) {
			LinearLayout l = (LinearLayout) inflater.inflate(
					R.layout.risultati, null);
			TextView t = (TextView) l.getChildAt(0);
			ListView lv = (ListView) l.getChildAt(1);
			lv.setOnTouchListener(gestureListener);
			lv.setOnItemClickListener(this);
			String[] from = new String[] { "k", "v" };
			int[] to = new int[] { R.id.k, R.id.v };

			SimpleAdapter adapter = new SimpleAdapter(this,
					new ArrayList<Map<String, String>>(),
					R.layout.rigarisultato, from, to);

			lv.setAdapter(adapter);
			t.setText(db.toString());
			visBancheDati.put(db, l);
		}

	}

	private class EffettuaQuery extends
			AsyncTask<QueryRequest, QueryReply, Void> {

		@Override
		protected Void doInBackground(QueryRequest... params) {
			Stomp stomp;
			try {
				System.out
						.println("CONNESSIONE A:    "
								+ sp.getString("host",
										"ufficiomobile.comune.prato.it"));
				stomp = new Stomp("tcp://"
						+ sp.getString("host", "ufficiomobile.comune.prato.it")
						+ ":" + sp.getString("port", "61613"));

				connection = stomp.connectBlocking();

				StompFrame frame = new StompFrame(SUBSCRIBE);
				frame.addHeader(DESTINATION,
						StompFrame.encodeHeader("/queue/" + imei()));
				frame.addHeader(ID, connection.nextId());
				StompFrame response = connection.request(frame);

				QueryRequest q = params[0];
				q.setVersione(randomGenerator.nextInt());
				q.setUserName(sp.getString("utente", ""));
				q.setPassword(sp.getString("password",""));
				XStream xstream = new XStream();

				frame = new StompFrame(SEND);
				frame.addHeader(DESTINATION,
						StompFrame.encodeHeader("/queue/queryServer"));
				frame.addHeader(MESSAGE_ID, StompFrame.encodeHeader("test"));
				frame.addHeader(StompFrame.encodeHeader("stomp"),
						StompFrame.encodeHeader("yes"));
//				autenticati(frame);
				frame.addHeader(
						StompFrame.encodeHeader("CamelJmsDestinationName"),
						StompFrame.encodeHeader(imei()));
				frame.content(new AsciiBuffer(xstream.toXML(q)));
				connection.send(frame);
				bdArrivate = 0;
				totaleBD=1; // il loop deve continuare finche' non arriva almeno un risultato valido
				do {
					StompFrame received = connection.receive();

					System.out.println(received.contentAsString());
					QueryReply qrep = (QueryReply) xstream.fromXML(received
							.contentAsString());
					System.out.println(qr.getVersione());
					System.out.println(qrep.getVersione());
					if (qr.getVersione() == qrep.getVersione()) {
						
						bdArrivate++;
						totaleBD=qrep.getTotRisult();
						System.out.println(bdArrivate+"/"+totaleBD);
						publishProgress(qrep);
					}

				} while (bdArrivate<totaleBD);
				connection.close();
				connection = null;
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

//		private void autenticati(StompFrame frame) throws Exception {
//			ShiroSecurityToken securityToken = new ShiroSecurityToken(
//					sp.getString("utente", ""), sp.getString("password",""));
//			CipherService cipherService = new AesCipherService();
//			ByteArrayOutputStream stream = new  ByteArrayOutputStream();
//	        ObjectOutput serialStream = new ObjectOutputStream(stream);
//	        serialStream.writeObject(securityToken);
//	        ByteSource byteSource = cipherService.encrypt(stream.toByteArray(), passPhrase);
//	        serialStream.close();
//	        stream.close();
//	        String encodedString = StringUtils.newStringUtf8((Base64.encodeBase64(byteSource.getBytes(),false)));
//	        String safeString = encodedString.replace('+','-').replace('/','_');
//
//			frame.addHeader(StompFrame.encodeHeader("SHIRO_SECURITY_TOKEN"),
//					new AsciiBuffer(encodedString));
//			System.out.println(StompFrame.encodeHeader(safeString).length);
//			System.out.println(StompFrame.encodeHeader(safeString));
//		}

		private String imei() {
			if (!imei.equals(""))
				return imei;
			else {
				TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
				String possibleImei = tm.getDeviceId();
				if (possibleImei.contains("0000000")) {
					imei = fakeImei;

				} else {
					imei = possibleImei;

				}
				System.out.println("IMEI--------------" + imei);
				return imei;
			}
		}

		@Override
		protected void onProgressUpdate(QueryReply... values) {
			QueryReply qr = values[0];
			int i = 0;
			LinearLayout rigaLayout = visBancheDati.get(qr.getDaQualeDB());
			viewFlipper.addView(rigaLayout);
			for (Entry<String, List<Map<String, String>>> unRisultato : qr
					.getRisultati().entrySet()) {

				if (i == 1)
					rigaLayout.setBackgroundColor(Color.BLUE);
				i = 1 - i;
				ListView lv = ((ListView) rigaLayout.getChildAt(1));

				String[] from = new String[] { "k", "v" };
				int[] to = new int[] { R.id.k, R.id.v };

				SimpleAdapter adapter = new SimpleAdapter(io,
						unRisultato.getValue(), R.layout.rigarisultato, from,
						to);

				lv.setAdapter(adapter);

			}
			progress.setProgress(100*bdArrivate/totaleBD);
			// super.onProgressUpdate(values);
		}

	}

	class MyGestureDetector extends SimpleOnGestureListener {

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			System.out.println("sono in onfling");
			try {
				if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
					return false;
				// right to left swipe
				if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
						&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					viewFlipper.setAnimation(slideRightIn);
					// viewFlipper.setOutAnimation(slideLeftOut);
					viewFlipper.showNext();
				} else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
						&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					viewFlipper.setAnimation(slideLeftIn);
					// viewFlipper.setOutAnimation(slideRightOut);
					viewFlipper.showPrevious();
				}
			} catch (Exception e) {
				System.out.println(e.toString());
			}
			return false;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> listView, View arg1, int position,
			long arg3) {
		Map<String, String> selection = (Map<String, String>) listView
				.getItemAtPosition(position);
		String nuovaRicerca = selection.get("v");
		Intent sr = new Intent(this, UMAndroidClientActivity.class);
		sr.putExtra("query", nuovaRicerca);
		this.startActivity(sr);

	}

}
