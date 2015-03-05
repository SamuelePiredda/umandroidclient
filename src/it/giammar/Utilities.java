package it.giammar;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Random;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import org.fusesource.stomp.client.Stomp;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.telephony.TelephonyManager;
import android.util.Log;

public class Utilities {
	
	private static final String TAG = "Utilities";
	private static String imei="";
	private static Random randomGenerator = new Random();
	private static String fakeImei = "test"
			+ Integer.valueOf(randomGenerator.nextInt()).toString();
	public static String imei(Object systemService) {
		if (!imei.equals(""))
			return imei;
		else {
			TelephonyManager tm = (TelephonyManager) systemService;
			String possibleImei = tm.getDeviceId();
			if (possibleImei==null || possibleImei.contains("0000000")) {
				imei = fakeImei;

			} else {
				imei = possibleImei;

			}
			Log.i(TAG, "IMEI--------------" + imei);
			return imei;
		}
	}
	public static Stomp connectTcpOrSsl(Resources r, SharedPreferences sp) throws KeyStoreException, IOException,
			NoSuchAlgorithmException, CertificateException,
			KeyManagementException, URISyntaxException {
		Stomp stomp;
		Log.i(TAG,
				"CONNESSIONE A:    "
						+ sp.getString("host", "ufficiomobile.comune.prato.it"));
		InputStream clientTruststoreIs = r.openRawResource(
				R.raw.truststore);
		KeyStore trustStore = null;
		trustStore = KeyStore.getInstance("BKS");
		trustStore.load(clientTruststoreIs, "prato1.".toCharArray());

		Log.d(TAG, "Loaded server certificates: " + trustStore.size());

		// initialize trust manager factory with the read truststore
		TrustManagerFactory tmf = null;
		tmf = TrustManagerFactory.getInstance(TrustManagerFactory
				.getDefaultAlgorithm());
		tmf.init(trustStore);

		KeyManagerFactory kmf = null;
		kmf = KeyManagerFactory.getInstance(KeyManagerFactory
				.getDefaultAlgorithm());

		SSLContext ctx = SSLContext.getInstance("TLS");

		ctx.init(null, tmf.getTrustManagers(), null);
		Log.d(TAG, "prima di new stomp ");
		String stompProtocol;
		if (sp.getBoolean("usessl", true))
			stompProtocol = "ssl";
		else
			stompProtocol = "tcp";

		stomp = new Stomp(stompProtocol + "://"
				+ sp.getString("host", "ufficiomobile.comune.prato.it") + ":"
				+ sp.getString("port", "61614"));

		Log.d(TAG, "prima di setsslcontext");

		stomp.setSslContext(ctx);
		Log.d(TAG, "prima di connectBlocking");
		return stomp;
	}

}
