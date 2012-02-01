package com.sqlite.management;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public abstract class TableManager {
	public SQLiteDatabase bdd;
	protected TableModel model;
	
	/**
	 * Constructor
	 * @param model to mange
	 */
	public TableManager(TableModel model){
		bdd = null;
		this.model = model;
	}
	
	/**
	 * Insert datas to database
	 * @param o object containing datas to insert
	 * @return the row ID of the newly inserted row, or -1 if an error occurred
	 */
	public long insert(TableModel o){
		return bdd.insert(model.tableName, null, getValues(o));
	}
	
	/**
	 * Update datas to database
	 * @param id : base_id of the record to update
	 * @param o : Object with the datas to update
	 * @return 1 if the record was removed, 0 otherwise.
	 */
	public int update(int id, TableModel o){
		return bdd.update(model.tableName, getValues(o), model.listFields[1] + "=" + id, null);
	}
	
	/**
	 * Remove the record selected with id
	 * @param id : base_id of the record to delete
	 * @return 1 if the record was removed, 0 otherwise. 
	 */
	public int remove(int id){
		return bdd.delete(model.tableName, model.listFields[1] + "=" + id , null);
	}
	
	private ContentValues getValues(TableModel o) {
		ContentValues values = new ContentValues();
		List<Object> ModelValues = o.toArray();
		
		for(int i=1; i<model.listFields.length; i++){
			if(ModelValues.get(i) != null)
				if(ModelValues.get(i).getClass().equals(byte[].class))
					values.put(model.listFields[i], (byte[])ModelValues.get(i));
				else
					values.put(model.listFields[i], ModelValues.get(i).toString());
			
		}
		
		return values;
	}
	
	/**
	 * Search records witch have this base_id
	 * @param Id : base_id to search
	 * @return List of record founded
	 */
	public List<TableModel> getWithBaseId(Integer Id){
		Cursor c = bdd.query(model.tableName, model.listFields, model.listFields[1] + "=" + Id, null, null, null, null);
		return cursorToTable(c);
	}
	
	/**
	 * Get all record in database.
	 * @return list of records.
	 */
	public List<TableModel> getAll(){
		Cursor c = bdd.query(model.tableName, model.listFields, null, null, null, null, null);
		return cursorToTable(c);
	}
	
	/**
	 * Get list of all base_ids in the table
	 * @return liste of base_id
	 */
	public Object getAllIds(){
		List<Object> list = new ArrayList<Object>();
		List<TableModel> records = getAll();
		
		for (TableModel rec : records) {
			list.add((Integer)rec.getBaseId());
		}
		
		return list;
	}
	
	protected abstract List<TableModel> cursorToTable(Cursor c);
}
