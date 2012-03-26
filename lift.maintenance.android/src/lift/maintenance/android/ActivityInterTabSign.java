package lift.maintenance.android;

import lift.maintenance.android.dal.*;

import com.android.drawing.ImageDrawer;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class ActivityInterTabSign extends Activity implements OnClickListener {
	private DataBaseManager manager;
	
	private int inter_id;
	private InterventionModel inter;
	
	private ImageDrawer ivSign;
	private EditText etSignName;
	private Button bValSign;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.intertabsign);
		
		inter_id = getIntent().getExtras().getInt("inter_id");
		
		manager = new DataBaseManager(getApplicationContext());
		
		ivSign = (ImageDrawer)findViewById(R.id.iVSign);
		int width = getWindowManager().getDefaultDisplay().getWidth()-10;
		ivSign.setSize(width, width);
		
		etSignName = (EditText)findViewById(R.id.etSignName);
		
		bValSign = (Button)findViewById(R.id.bValSig);
		bValSign.setOnClickListener(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		manager.open();
		inter = (InterventionModel) manager.intervention.getWithBaseId(inter_id).get(0);
		manager.close();
		
		if(inter.getSignaturePicture() != null)
			ivSign.setByteArray(inter.getSignaturePicture());
		
		if(inter.getSignatureName() != null)
			etSignName.setText(inter.getSignatureName());
	}

	public void onClick(View v) {
		inter.setSignatureName(etSignName.getText().toString());
		inter.setSignaturePicture(ivSign.getByteArray());
		manager.open();
		manager.intervention.update(inter_id, inter);
		manager.close();
	}
}
