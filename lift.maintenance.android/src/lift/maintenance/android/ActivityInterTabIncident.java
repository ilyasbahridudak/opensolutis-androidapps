package lift.maintenance.android;

import java.util.ArrayList;
import java.util.List;

import lift.maintenance.android.dal.*;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

public class ActivityInterTabIncident extends Activity implements OnClickListener, OnItemSelectedListener{
	private DataBaseManager manager;
	private Context context;
	
	private int inter_id;
	
	private List<CodificationModel> Chapters;
	private List<CodificationModel> Localizations;
	private List<CodificationModel> Causes;
	private InterventionModel Intervention;
	
	private Spinner spChapter;
	private Spinner spLocalization;
	private Spinner spCauses;
	
	private TextView tvLastFailures;
	private TextView tvContact;
	
	private ImageButton bCall;
	
	private Boolean isResume;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		isResume = true;
		setContentView(R.layout.intertabincident);
		context = this;
		
		inter_id = this.getIntent().getExtras().getInt("inter_id");
		
		manager = new DataBaseManager(getApplicationContext());
		manager.open();
		Chapters = manager.code.getChapters();
		Localizations = new ArrayList<CodificationModel>();
		Causes = manager.code.getCauses();
		manager.close();
		
		spChapter = (Spinner)findViewById(R.id.spChapter);
		spLocalization = (Spinner)findViewById(R.id.spLocalization);
		spCauses = (Spinner)findViewById(R.id.spCause);
		tvLastFailures = (TextView)findViewById(R.id.tvLastFailures);
		tvContact = (TextView)findViewById(R.id.tvContact);
		bCall = (ImageButton)findViewById(R.id.bCall);
		
		spChapter.setOnItemSelectedListener(this);
		spLocalization.setOnItemSelectedListener(this);
		spCauses.setOnItemSelectedListener(this);
		bCall.setOnClickListener(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		isResume = true;
		manager.open();
		Intervention = (InterventionModel) manager.intervention.getWithBaseId(inter_id).get(0);
		manager.close();
		
		spLocalization.setEnabled(false);
		spCauses.setEnabled(false);
		
		spChapter.setAdapter(new AdapterCodification(this, Chapters, R.string.SelectChapter));
		spLocalization.setAdapter(new AdapterCodification(context, new ArrayList<CodificationModel>(), R.string.SelectLocalization));
		spCauses.setAdapter(new AdapterCodification(context, Causes, R.string.SelectCause));
		
		spChapter.setSelection(((AdapterCodification)spChapter.getAdapter()).getItemIdByBaseId(Intervention.getChapterId()));
		selectedChapter((CodificationModel)spChapter.getSelectedItem());
		
		if(spLocalization.isEnabled()){
			spLocalization.setSelection(((AdapterCodification)spLocalization.getAdapter()).getItemIdByBaseId(Intervention.getLocalizationId()));
			selectedLoc((CodificationModel)spLocalization.getSelectedItem());
			
			if(spCauses.isEnabled())
				spCauses.setSelection(((AdapterCodification)spCauses.getAdapter()).getItemIdByBaseId(Intervention.getCauseId()));
		}
		
		tvLastFailures.setText(Intervention.getLastIncidents());
		
		String Contact = "";
		if(Intervention.getRequestorName() != null)
			Contact = Intervention.getRequestorName();
		if(Intervention.getRequestorPhone() != null){
			if(Contact != "") Contact+=" - ";
			Contact += Intervention.getRequestorPhone();
		}
		
		if(Contact == "")
			Contact = context.getString(R.string.UnknownContact);
		
		tvContact.setText(Contact);
	}

	public void onClick(View v) {
		switch(v.getId()){
		case R.id.bCall:
			Intent intent;
			if(Intervention.getRequestorPhone() != null)
				intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+Intervention.getRequestorPhone()));
			else
				intent = new Intent(Intent.ACTION_DIAL);
			startActivity(intent);
			break;
		}
	}

	public void onItemSelected(AdapterView<?> parent, View view,
			int position, long id) {
		if(!isResume){
			CodificationModel item = (CodificationModel)parent.getItemAtPosition(position);
			switch(parent.getId()){
			case R.id.spChapter:
				selectedChapter(item);
				break;
			case R.id.spLocalization:
				selectedLoc(item);
				break;
			}
			Intervention.setChapterId(((CodificationModel)spChapter.getSelectedItem()).getBaseId());
			Intervention.setLocalizationId(((CodificationModel)spLocalization.getSelectedItem()).getBaseId());
			Intervention.setCauseId(((CodificationModel)spCauses.getSelectedItem()).getBaseId());
			
			manager.open();
			manager.intervention.update(inter_id, Intervention);
			manager.close();
		}
		else if(parent.getId() == R.id.spCause){
			isResume = false;
		}
	}
	
	public void onNothingSelected(AdapterView<?> parent) { }
	
	private void selectedChapter(CodificationModel item) {
		if(item.getBaseId() == 0){
			spLocalization.setEnabled(false);
			spLocalization.setSelection(0);
			spCauses.setEnabled(false);
			spCauses.setSelection(0);
		}
		else{
			manager.open();
			Localizations = manager.code.getLocalisation(item.getBaseId());
			manager.close();
			
			if(Localizations.size() > 0){
				
				spLocalization.setAdapter(new AdapterCodification(context, Localizations, R.string.SelectLocalization));
				spLocalization.setEnabled(true);
			}
			else
			{
				spLocalization.setEnabled(false);
			}
			spLocalization.setSelection(0);
		}
	}

	private void selectedLoc(CodificationModel item) {
		if(item.getBaseId() == 0){
			spCauses.setSelection(0);
			spCauses.setEnabled(false);
		}
		else{
			spCauses.setSelection(0);
			spCauses.setEnabled(true);
		}
	}

}
