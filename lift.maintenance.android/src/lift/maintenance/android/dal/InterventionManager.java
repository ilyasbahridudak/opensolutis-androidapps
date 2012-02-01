package lift.maintenance.android.dal;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;

import com.sqlite.management.TableManager;
import com.sqlite.management.TableModel;

public class InterventionManager extends TableManager {
	
	public InterventionManager() {
		super(new InterventionModel());
	}
	
	public List<TableModel> getIncidents(){
		Cursor c  = bdd.query(model.tableName, model.listFields, "type = 'incident'", null, null, null, null);
		return cursorToTable(c);
	}
	
	public int getNbIncidents(Boolean all){
		String selection = "type = 'incident'";
		
		if(!all) selection += " and state = 'draft'";
		
		Cursor c = bdd.query(model.tableName, new String[] {"id"}, selection, null, null, null, null);
		
		int nb = c.getCount();
		c.close();
		return nb;
	}
	
	public List<TableModel> getMaintenances(){
		Cursor c  = bdd.query(model.tableName, model.listFields, "type = 'maintenance'", null, null, null, null);
		return cursorToTable(c);
	}
	
	public int getNbMaintenance(Boolean all){
		String selection = "type = 'maintenance'";
		
		if(!all) selection += " and state = 'draft'";
		
		Cursor c = bdd.query(model.tableName, new String[] {"id"}, selection, null, null, null, null);
		
		int nb = c.getCount();
		c.close();
		return nb;
	}
	
	@Override
  	public List<TableModel> cursorToTable(Cursor c) {
		List<TableModel> records = new ArrayList<TableModel>();
		c.moveToFirst();
		
		for(int i=0; i<c.getCount();i++){
			InterventionModel intervention = new InterventionModel();
			intervention.setId(c.getInt(0));
			intervention.setBaseId(c.getInt(1));
			intervention.setName(c.getString(2));
			intervention.setChapterId(c.getInt(3));
			intervention.setLocalizationId(c.getInt(4));
			intervention.setCauseId(c.getInt(5));
			intervention.setInformation(c.getString(6));
			intervention.setCallDay(TableModel.stringToDate(c.getString(7), true));
			intervention.setInterventionStart(TableModel.stringToDate(c.getString(8), true));
			intervention.setInterventionEnd(TableModel.stringToDate(c.getString(9), true));
			intervention.setRequestorName(c.getString(10));
			intervention.setRequestorPhone(c.getString(11));
			intervention.setState(c.getString(12));
			intervention.setType(c.getString(13));
			intervention.setSomeone(Boolean.valueOf(c.getString(14)));
			intervention.setSignatureName(c.getString(15));
			intervention.setSignaturePicture(c.getBlob(16));
			intervention.setInvoiceable(Boolean.valueOf(c.getString(17)));
			intervention.setMachineId(c.getInt(18));
			intervention.setMachineName(c.getString(19));
			intervention.setParachute(Boolean.valueOf(c.getString(20)));
			intervention.setCable(Boolean.valueOf(c.getString(21)));
			intervention.setMaintenanceDeadline(TableModel.stringToDate(c.getString(22), true));
			intervention.setTakeIntoAccountDay(TableModel.stringToDate(c.getString(23), true));
			intervention.setLastIncidents(c.getString(24));
			records.add(intervention);
			c.moveToNext();
		}
		c.close();
		return records;
	}

}
