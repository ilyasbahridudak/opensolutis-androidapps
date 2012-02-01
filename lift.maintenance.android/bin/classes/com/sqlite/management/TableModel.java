package com.sqlite.management;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public abstract class TableModel {
	public String modelName = "";
	public String tableName = "";
	public String[] listFields;
	
	protected int id;
	protected int base_id;
	protected String name;
	
	public int getId(){
    	return id;
    }
    public int getBaseId(){
    	return base_id;
    }
    public String getName(){
    	return name;
    }
    
    public void setId(int id){
    	this.id = id;
    }
    public void setBaseId(int base_id){
    	this.base_id = base_id;
    }
    public void setName(String name){
    	this.name = name;
    }
    
    public String toString(){
    	String base = "id: " + id + ", base_id: " + base_id + ", name:" + name;
    	List<Object> list = toArray();
    	
    	for(int i=0; i< listFields.length; i++){
    		base += ", " + listFields[i] + ": " + list.get(i).toString();
    	}
    	return base;
    }
    
    public abstract List<Object> toArray();
    protected List<Object> toArrayBase(){
    	List<Object> list = new ArrayList<Object>();
    	list.add(id);
    	list.add(base_id);
    	list.add(name);
    	return list;
    }
    
	protected static String[] MakeFields(String[] fields) {
		String[] list = new String[fields.length+3];
		
		list[0] = "id";
		list[1] = "base_id";
		list[2] = "name";
		
		for(int i=0; i<fields.length; i++)
			list[i+3] = fields[i];
		
		return list;
	}
	
	public static Timestamp stringToDate(String date, Boolean millis){
		String format = "yyyy-MM-dd hh:mm:ss";
		if (millis)
			format += ".S";
		try {
			return new Timestamp((new SimpleDateFormat(format)).parse(date).getTime());
		} catch (ParseException e) {
			return null;
		} catch (NullPointerException e) {
			return null;
		}
	}
}
