package it.giammar;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ViewFlipper;

public class SfogliaRisultatiActivity extends Activity {
	private static final int SWIPE_MIN_DISTANCE = 120;
	private static final int SWIPE_MAX_OFF_PATH = 250;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;
	private GestureDetector gestureDetector;
	View.OnTouchListener gestureListener;
	private Animation slideLeftIn;
	private Animation slideLeftOut;
	private Animation slideRightIn;
	private Animation slideRightOut;
	private ViewFlipper viewFlipper;

	// private Map<QueryReply.Database, ListView> tabelle = new
	// HashMap<QueryReply.Database, ListView>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ListView listView = (ListView) inflater.inflate(R.layout.risultati,
				null);
		viewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper1);
		String[] values = new String[] { "Android", "iPhone", "WindowsMobile",
				"Blackberry", "WebOS", "Ubuntu", "Windows7", "Max OS X",
				"Linux", "OS/2" };
		slideLeftIn = AnimationUtils.loadAnimation(this,
				android.R.anim.slide_in_left);
		slideLeftOut = AnimationUtils.loadAnimation(this,
				android.R.anim.slide_out_right);
		slideRightIn = AnimationUtils.loadAnimation(this,
				android.R.anim.slide_in_left);
		slideRightOut = AnimationUtils.loadAnimation(this,
				android.R.anim.slide_out_right);
		// First paramenter - Context
		// Second parameter - Layout for the row
		// Third parameter - ID of the View to which the data is written
		// Forth - the Array of data
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, android.R.id.text1, values);

		// Assign adapter to ListView
		listView.setAdapter(adapter);

		ListView listView2 = (ListView) inflater.inflate(R.layout.risultati,
				null);
		String[] values2 = new String[] { "asdf", "cccc", "ccczxcvzxcv",
				"zxcvz", "asdfasdfa", "asdfasdf", "adsfadsf",
				"Max zxcvzxcvzxcv X", "zxb fg", "OSxcvzxcvzxcvzxcvzxcv2" };

		// First paramenter - Context
		// Second parameter - Layout for the row
		// Third parameter - ID of the View to which the data is written
		// Forth - the Array of data
		ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, android.R.id.text1,
				values2);

		// Assign adapter to ListView
		listView2.setAdapter(adapter2);
		viewFlipper.addView(listView);

		viewFlipper.addView(listView2);
		gestureDetector = new GestureDetector(new MyGestureDetector());
		gestureListener = new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (gestureDetector.onTouchEvent(event)) {
					return true;
				}
				return false;
			}
		};
		listView.setOnTouchListener(gestureListener);
		listView2.setOnTouchListener(gestureListener);
		super.onCreate(savedInstanceState);
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
	// private void inizializzaTabelle() {
	// tabelle.put(Database.ANIA, ania);
	// // table_1.addContainerProperty("Risultato", RichTextArea.class, null);
	// tabelle.put(Database.PRA, pra);
	// tabelle.put(Database.RUBATI, rubati);
	// for (Table tabella : tabelle.values()) {
	// tabella.addContainerProperty("Chiave", RichTextArea.class, null);
	// tabella.addContainerProperty("Valore", RichTextArea.class, null);
	// }
	// }
}
