package lift.maintenance.android;

import java.util.List;

import lift.maintenance.android.dal.CheckLineModel;
import lift.maintenance.android.dal.DataBaseManager;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.ListView;

public class ActivityCheckLine extends Activity {
	private Context context;
	private DataBaseManager manager;
	private ListView lvCheckLine;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.checkline);
        
        context = getApplicationContext();
        manager = new DataBaseManager(context);
        
        lvCheckLine = (ListView) findViewById(R.id.lvCheckLine);
        
        int list_id = this.getIntent().getExtras().getInt("list_id");
        List<CheckLineModel> lsts;
        manager.open();
        if(list_id>0)
        	lsts = manager.checkline.getWithListId(list_id);
        else
        	lsts = manager.checkline.getAll();        
        manager.close();
        
        if(lsts != null){
        	AdapterCheckLine adapter = new AdapterCheckLine(context, lsts);
	        lvCheckLine.setAdapter(adapter);        
	    }
	}
}
