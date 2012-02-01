package lift.maintenance.android;

import java.util.List;

import lift.maintenance.android.dal.*;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;

public class ActivityListView extends Activity implements OnItemClickListener{
	
	private Context context;
	private DataBaseManager manager;
	private ListView lvCheckList;
	private ListAdapter adapter;
	private Integer view;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.listview);
        
        context = getApplicationContext();
        manager = new DataBaseManager(context);
        
        //On récupére la vue à afficher
        view = this.getIntent().getExtras().getInt("view");
        
        lvCheckList = (ListView) findViewById(R.id.lvCheckList);
        
        lvCheckList.setOnItemClickListener(this);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onResume() {
		super.onResume();
		List<?> lsts;
        manager.open();
        
        //on affiche le contenu attendu
        switch(view){
        //check list
		case 1:
			lsts = manager.checklist.getAll();
			adapter = new AdapterCheckList(context, (List<CheckListModel>)lsts);
			break;
		//check line
		case 2:
			int list_id = this.getIntent().getExtras().getInt("list_id");
			lsts = manager.checkline.getWithListId(list_id);
			adapter = new AdapterCheckLine(context, (List<CheckLineModel>) lsts);
			break;
		//incidents
		case 3:
			lsts = manager.intervention.getIncidents();
			adapter = new AdapterIntervention(context, (List<InterventionModel>) lsts);
			break;
		//maintenances
		case 4 :
			lsts = manager.intervention.getMaintenances();
			adapter = new AdapterIntervention(context, (List<InterventionModel>) lsts);
			break;
        }
        
        manager.close();
        lvCheckList.setAdapter(adapter);
	}
	
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Intent intent;
		//on agit en fonction du comportement attendu
		switch(this.view){
		//check list
		case 1:
	        CheckListModel chklst = (CheckListModel)adapter.getItem(position);
	        
	        intent = new Intent(ActivityListView.this, ActivityListView.class);
	        intent.putExtra("view", 2);
	        intent.putExtra("list_id", chklst.getBaseId());
			startActivity(intent);
			break;
		case 3:
		case 4:
			InterventionModel inter = (InterventionModel)adapter.getItem(position);
			intent = new Intent(ActivityListView.this, ActivityIntervention.class);
			intent.putExtra("inter_id", inter.getBaseId());
			startActivity(intent);
			break;
		}
	}
}
