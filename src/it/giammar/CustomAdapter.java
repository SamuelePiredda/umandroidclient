package it.giammar;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CustomAdapter extends ArrayAdapter<Modello>{

    public CustomAdapter(Context context, int textViewResourceId,
            List<Modello> objects) {
        super(context, textViewResourceId, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext()
             .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.row, null);
        TextView bancaDati = (TextView)convertView.findViewById(R.id.textViewBancaDati);
        TextView percorso = (TextView)convertView.findViewById(R.id.textViewDescrizione);
        ImageView image = (ImageView)convertView.findViewById(R.id.imageView1);
        LinearLayout layout = (LinearLayout)convertView.findViewById(R.id.layoutRow);
        Modello c = getItem(position);
        bancaDati.setText(c.getBancaDati());
        percorso.setText(c.getDescrizione());
        switch (c.getTipo()) {
        case 0:
        	//auto
        	layout.setBackgroundColor(Color.parseColor("#DDDDDD"));
        	break;
        case 1:
        	layout.setBackgroundColor(Color.parseColor("#FFFFFF"));
        	//image.setImageResource(R.drawable.pubblica);
        	image.setBackgroundColor(Color.parseColor("#ffba34"));
        	break;
        case 2:
        	layout.setBackgroundColor(Color.parseColor("#FFFFFF"));
        	//image.setImageResource(R.drawable.privata);
        	image.setBackgroundColor(Color.parseColor("#67d2ff"));
        	break;
        default:
        	//layout.setBackgroundColor(Color.parseColor("#FFFFFF"));
        	
        }
        
        //LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(45,45);
        image.getLayoutParams().width=5;
        image.getLayoutParams().height=45;
        
        //image.setMinimumWidth(45);
        //image.setMaxWidth(45);
        return convertView;
        
    }

}