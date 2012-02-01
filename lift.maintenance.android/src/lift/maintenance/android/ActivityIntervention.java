package lift.maintenance.android;

import java.sql.Timestamp;
import java.util.Date;

import lift.maintenance.android.dal.DataBaseManager;
import lift.maintenance.android.dal.InterventionModel;
import lift.maintenance.android.dal.MachineModel;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class ActivityIntervention extends TabActivity {
	private DataBaseManager manager;
	private Resources res;
	private InterventionModel inter;
	private MachineModel machine;
	
	private int inter_id;
	private TabHost tabHost;
	private TabSpec tabInfo;
	private TabSpec tabInter;
	private TabSpec tabSign;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.intertabhost);
		
		inter_id = this.getIntent().getExtras().getInt("inter_id");
		res = getResources();
		
		manager = new DataBaseManager(getApplicationContext());
		manager.open();
		inter = (InterventionModel)(manager.intervention.getWithBaseId(inter_id).get(0));
		machine = manager.machine.getWithBaseId(inter.getMachineId()).get(0);
		if(inter.getState().equals(res.getResourceEntryName(R.string.draft))){
			inter.setState(res.getResourceEntryName(R.string.considering));
			if(inter.getTakeIntoAccountDay() == null)
			{
				inter.setTakeIntoAccountDay(new Timestamp(new Date().getTime()));
			}
			manager.intervention.update(inter_id, inter);
		}
		manager.close();
		
		tabHost = getTabHost();

		//tab info
		Intent intentInfo = new Intent(this, ActivityInterTabInfo.class);
		intentInfo.putExtra("inter_id", inter_id);
	
		tabInfo = tabHost.newTabSpec("info").setIndicator("Info", res.getDrawable(R.drawable.info)).setContent(intentInfo);
		tabHost.addTab(tabInfo);
		
		//tab incident
		if(inter.getType().equals("incident")){
			Intent intentIncident = new Intent(this, ActivityInterTabIncident.class);
			intentIncident.putExtra("inter_id", inter_id);
			
			tabInter = tabHost.newTabSpec("incident").setIndicator("Incident", res.getDrawable(R.drawable.incidents_small)).setContent(intentIncident);
		}
		//tab maintenance		
		else{
			Intent intentMaint = new Intent(this, ActivityListView.class);
			intentMaint.putExtra("view", 2);
			intentMaint.putExtra("list_id", machine.getCheckListId());
			
			tabInter = tabHost.newTabSpec("maint").setIndicator("Maintenance", res.getDrawable(R.drawable.maintenance_small)).setContent(intentMaint);
		}
		tabHost.addTab(tabInter);
			
		Intent intentSign = new Intent(this, ActivityInterTabSign.class);
		intentSign.putExtra("inter_id", inter_id);
		
		tabSign = tabHost.newTabSpec("signature").setIndicator("Signature", getResources().getDrawable(R.drawable.sign)).setContent(intentSign);
		tabHost.addTab(tabSign);
	}
}
