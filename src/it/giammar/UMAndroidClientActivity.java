package it.giammar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class UMAndroidClientActivity extends Activity implements OnClickListener {
	private Button cerca;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		cerca = (Button) findViewById(R.id.cerca);
		cerca.setOnClickListener(this);
		connettiBackend();
	}

	private void connettiBackend() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onClick(View v) {
		inviaQuery();
		puliciListe();
		Intent sr = new Intent(this, SfogliaRisultatiActivity.class);
		this.startActivity(sr);
	}

	private void puliciListe() {
		// TODO Auto-generated method stub
		
	}

	private void inviaQuery() {
		// TODO Auto-generated method stub
		
	}

	

	

}