package com.xmlrpc.access;

import java.util.ArrayList;
import java.util.List;

import org.xmlrpc.android.XMLRPCClient;
import org.xmlrpc.android.XMLRPCException;

public class xmlrpcAccess {
	
	public XMLRPCClient rpcClient;
	public static final String URL_COMMON = "/xmlrpc/common";
	public static final String URL_OBJECT = "/xmlrpc/object";
	public static final String URL_DB = "/xmlrpc/db";
	
	public String URL = "";
	public String DB_NAME = "";
	public String USERNAME = "";
	public String PASSWORD = "";
	public String user = "";
	public int uid = 0;
	private Boolean connected = false;

	/**
	 * Connect to the spécified OpenERP data base
	 * @param strURL URL of the serveur with port e.g. : http://dataserver.org:8069
	 * @param Base OpenERP Data Base name
	 * @param Login Login of the user to connect
	 * @param Pass Password of the user to connect
	 * @return 0 if connected, -1 if not, -2 for bad user or pasword and -3 for bad server or data base name
	 */
	public int Connect(String strURL, String Base, String Login, String Pass){
		URL = strURL;
		DB_NAME = Base;
		USERNAME = Login;
		PASSWORD = Pass;
		
		try {
			rpcClient = new XMLRPCClient(URL+URL_COMMON);
			user = (String) rpcClient.call("login", DB_NAME, USERNAME, PASSWORD).toString();
			if(user != "false"){
				Object uid1 = Integer.parseInt(user);
				if (uid1 instanceof Integer){
					uid = (Integer) uid1;
					connected = true;
					return 0;
				}
			}
			return -1;
		} catch (XMLRPCException e) {
			e.printStackTrace();
			return -2;
		} catch (Exception e) {
			e.printStackTrace();
			return -3;
		}
		
	}
	
	/**
	 * Search ids in OpenERP model
	 * @param Model OpenERP Model
	 * @param Query Data filter representing a list of tupple
	 * @return List of ids if succeed, integer -2 or -3 not
	 */
	@SuppressWarnings("unchecked")
	public Object Search(String Model, Object Query){
		if (connected){
			try{
				rpcClient = new XMLRPCClient(URL+URL_OBJECT);
				Object res = rpcClient.call("execute", DB_NAME, uid, PASSWORD, Model, "search", Query);
				
				Object ERPIds = new ArrayList<Object>();
				for (Object id : (Object[])res){
					((List<Object>)ERPIds).add((Integer)id);
				}
				
				return ERPIds;
			} catch (XMLRPCException e) {
				e.printStackTrace();
				Integer res = -2;
				return res;
			} catch (Exception e) {
				e.printStackTrace();
				Integer res = -3;
				return res;
			}
		}
		return -1;
	}
	
	/**
	 * Read datas in OpenERP Model from a list of ids
	 * @param Model OpenERP Model
	 * @param ids list of ids to read
	 * @param Fields list of fields to read
	 * @return List of HashMap Containing datas if succed, -2 or -3 if not
	 */
	public Object Read(String Model, Object ids, String[] Fields){
		if(connected){
			try{
				rpcClient = new XMLRPCClient(URL+URL_OBJECT);
				Object res = rpcClient.call("execute", DB_NAME, uid, PASSWORD, Model, "read", ids, Fields);
				return res;
			} catch (XMLRPCException e) {
				e.printStackTrace();
				return -2;
			} catch (Exception e) {
				e.printStackTrace();
				return -3;
			}
		}
		return -1;
	}

	/**
	 * Write values in OpenERP model
	 * @param Model OpenERP model
	 * @param id id of the record to update
	 * @param values HashMap<Object, Object> containing datas
	 * @return true if succed, -2 or -3 if not
	 */
	public Object Write(String Model, Integer id, Object values){
		if(connected){
			try{
				rpcClient = new XMLRPCClient(URL+URL_OBJECT);
				Object res = rpcClient.call("execute", DB_NAME, uid, PASSWORD, Model, "write",new Integer[] {id}, values);
				return res;
			} catch (XMLRPCException e){
				e.printStackTrace();
				return -2;
			} catch (Exception e){
				e.printStackTrace();
				return -3;
			}
		}
		return -1;
	}
}
