package lift.maintenance.android.dal;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import lift.maintenance.android.R;

import com.sqlite.management.TableModel;
import com.xmlrpc.access.xmlrpcAccess;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

@SuppressWarnings("unchecked")
public class Synchro {
	
	public static String synchro(SharedPreferences prefs, Context context, DataBaseManager manager){
		xmlrpcAccess access = new xmlrpcAccess();
			
		HashMap<Integer, Integer> modifs = new HashMap<Integer, Integer>();
		for(int i = 1; i <= 15; i++)
			modifs.put(i, 0);
		
		int connected = access.Connect(prefs.getString("URL", ""), prefs.getString("base", ""), prefs.getString("login", ""), prefs.getString("pass", ""));
		
		if (connected == 0){
			Timestamp last;
			try{
				long lDate = prefs.getLong("last", 0);
				if(lDate != 0)
					last = new Timestamp(lDate);
				else 
					last = null;
			}
			catch(ClassCastException e){
				last = null;
			}
			Timestamp today = new Timestamp(new Date().getTime());
			manager.open();
			
			if(last == null || (last.getDate() != today.getDate() || last.getMonth() != today.getMonth())){
				modifs = synchroCheckList(access, manager, modifs);
				modifs = synchroCodification(access, manager, modifs);
				SharedPreferences.Editor editor=prefs.edit();
				editor.putLong("last", today.getTime());
				editor.commit();
			}
			modifs = synchroInterventions(access, manager, context, modifs);
			manager.close();
		}
		
		String message = context.getString(R.string.SyncOf);
		message += context.getString(R.string.SyncCheckList, modifs.get(1),  modifs.get(2),  modifs.get(3));
		message += context.getString(R.string.SyncCheckLine, modifs.get(4),  modifs.get(5),  modifs.get(6));
		message += context.getString(R.string.SyncCodif,     modifs.get(7),  modifs.get(8),  modifs.get(9));
		message += context.getString(R.string.SyncMachine,   modifs.get(10), modifs.get(11), modifs.get(12));
		message += context.getString(R.string.SyncInterv,    modifs.get(13), modifs.get(14), modifs.get(15));
		
		return message;
	}
	
	private static HashMap<Integer, Integer> synchroCheckList(xmlrpcAccess access, DataBaseManager manager, HashMap<Integer, Integer> modifs){
		
		
		//Construction de la condition de recherche [["id",">",0]]
		Object query = new ArrayList<Object>();
		((ArrayList<Object>)query).add(CreateTuple("id", ">", 0));
		
		//Recherche des check list dans OpenERP
		Object ERPIds = access.Search("contract.check.list", query);
			
		//Vérification du résultat de la recherche
		if(!ERPIds.getClass().equals(Integer.class)){
			
			//lecture des enregistrements existants dans Android
			Object AndroidIds = manager.checklist.getAllIds();
			
			//suppression des listes n'exitant plus dans openERP
			Object Ids2Delete = DiffLists((List<Object>)AndroidIds, (List<Object>)ERPIds);
			
			for(Integer Id : (List<Integer>)Ids2Delete){
				manager.checklist.remove(Id);
				modifs.put(1, modifs.get(1)+1);
				for(Integer line_id : (List<Integer>)manager.checkline.getAllIds(Id)){
					manager.checkline.remove(line_id);
					modifs.put(4, modifs.get(4)+1);
				}
			}
			
			//insertion des list n'exitant pas dans Android
			Object Ids2Insert = DiffLists((List<Object>)ERPIds, (List<Object>)AndroidIds);
			
			if(!((List<Integer>)Ids2Insert).isEmpty()) {
				//lecture des données correspondant à l'id
				Object ERPInsert = access.Read("contract.check.list", ((List<Object>)Ids2Insert), CheckListModel.listFields);
			    
				//vérification du resultat de la lecture
				if(!ERPInsert.getClass().equals(Integer.class)){
					for(Object valueObj : (Object[])ERPInsert){
						HashMap<Object, Object> values = (HashMap<Object, Object>)valueObj;
						
						//insertion de la check list
						CheckListModel list = new CheckListModel();
						list.setBaseId((Integer)values.get(CheckListModel.listFields[0]));
						list.setName((String)values.get(CheckListModel.listFields[2]));
						manager.checklist.insert(list);
						modifs.put(2, modifs.get(2)+1);
						
						//synchornisation des checklines correspondantes
						modifs = synchroCheckLine(access, manager, (Integer)values.get(CheckListModel.listFields[0]), modifs);
					}
				}
			}
			
			//mise a jour des list en cas de modification dans OpenERP
			Object Ids2Update = EqualLists((List<Object>)ERPIds, (List<Object>)AndroidIds);
			
			if(!((List<Integer>)Ids2Update).isEmpty()) {
				Object ERPUpdate = access.Read("contract.check.list", Ids2Update, CheckListModel.listFields);
				
				//vérification du resultat de la lecture
				if(!ERPUpdate.getClass().equals(Integer.class)){
					for(Object valueObj : (Object[])ERPUpdate){
						HashMap<Object, Object> valuesERP = (HashMap<Object, Object>)valueObj;
						CheckListModel valuesAndroid = manager.checklist.getWithBaseID((Integer)valuesERP.get(CheckListModel.listFields[0])).get(0);
						
						if(!valuesAndroid.getName().equals(valuesERP.get(CheckListModel.listFields[2]))){
							valuesAndroid.setName((String)valuesERP.get(CheckListModel.listFields[2]));
							manager.checklist.update((Integer)valuesERP.get(CheckListModel.listFields[0]), valuesAndroid);
							modifs.put(3, modifs.get(3)+1);
						}
						
						modifs = synchroCheckLine(access, manager, (Integer)valuesERP.get(CheckListModel.listFields[0]), modifs);
					}
				}
			}
		}
		
		return modifs;
	}
	
	private static HashMap<Integer, Integer> synchroCheckLine(xmlrpcAccess access, DataBaseManager manager, Integer chklstId, HashMap<Integer, Integer> modifs){
		
		//Création de la conddition de recherche [["list_id", "=", chklstId]]
		Object query = new ArrayList<Object>();
		((ArrayList<Object>)query).add(CreateTuple("list_id", "=", chklstId));
		
		Object ERPIds = access.Search("contract.check.line", query);
		
		//vérification du résultat de la recherche
		if(!ERPIds.getClass().equals(Integer.class)){
						
			//lecture des enregistrements existants dans Android
			Object AndroidIds = manager.checkline.getAllIds(chklstId);
			
			//suppression des lines qui n'existent plus dans OpenERP
			Object Ids2Delete = DiffLists((List<Object>)AndroidIds, (List<Object>)ERPIds);
			
			for(Integer Id : (List<Integer>)Ids2Delete){
				manager.checkline.remove(Id);
				modifs.put(4, modifs.get(4)+1);
			}
			
			//ajout des lines n'existant pas dans Android
			Object Ids2Insert = DiffLists((List<Object>)ERPIds, (List<Object>)AndroidIds);
			
			if(!((List<Integer>)Ids2Insert).isEmpty()){
				//lecture des données correspondant à l'id
				Object ERPInsert = access.Read("contract.check.line", Ids2Insert, CheckLineModel.listFields);
				
				//vérification du resultat de la lecture
				if(!ERPInsert.getClass().equals(Integer.class)){
					
					for(Object valueObj : (Object[])ERPInsert){
						HashMap<Object, Object> values = (HashMap<Object, Object>)valueObj;
					
						//insertion de la check line
						CheckLineModel line = new CheckLineModel();
						line.setBaseId((Integer)values.get(CheckLineModel.listFields[0]));
						line.setName((String)values.get(CheckLineModel.listFields[2]));
						line.setFrequency((String)values.get(CheckLineModel.listFields[3]));
						line.setListId(chklstId);
						manager.checkline.insert(line);
						modifs.put(5, modifs.get(5)+1);
					}
				}
			}
			
			//mise a jour des check lines déjà présents
			Object Ids2Update = EqualLists((List<Object>)AndroidIds, (List<Object>)ERPIds);
			
			if(!((List<Integer>)Ids2Update).isEmpty()){
				//lecture des données correspondant à l'id
				Object ERPUpdate = access.Read("contract.check.line", Ids2Update, CheckLineModel.listFields);
				
				//vérification du resultat de la lecture
				if(!ERPUpdate.getClass().equals(Integer.class)){
					for(Object valueObj : (Object[])ERPUpdate){
						HashMap<Object, Object> valuesERP = (HashMap<Object, Object>)valueObj;
					
						CheckLineModel valuesAndroid = manager.checkline.getWithBaseId((Integer) valuesERP.get(CheckLineModel.listFields[0])).get(0);
					
						//si des valeurs ont changé
						if(!valuesAndroid.getName().equals(valuesERP.get(CheckLineModel.listFields[2])) ||
								!valuesAndroid.getFrequency().equals(valuesERP.get(CheckLineModel.listFields[3]))){
							//mise à jour de la check line
							valuesAndroid.setName((String)valuesERP.get(CheckLineModel.listFields[2]));
							valuesAndroid.setFrequency((String)valuesERP.get(CheckLineModel.listFields[3]));
							manager.checkline.update((Integer) valuesERP.get(CheckLineModel.listFields[0]), valuesAndroid);
							modifs.put(6, modifs.get(6)+1);
						}
					}
				}
			}
		}
		return modifs;
	}

	private static HashMap<Integer, Integer> synchroCodification(xmlrpcAccess access, DataBaseManager manager, HashMap<Integer, Integer> modifs){

		//Construction de la condition de recherche [["id",">",0]]
		Object query = new  ArrayList<Object>();
		((ArrayList<Object>)query).add(CreateTuple("id", ">", 0));
		
		//Recherche des check list dans OpenERP
		Object ERPIds = access.Search("intervention.codification", query);
					
		//Vérification du résultat de la recherche
		if(!ERPIds.getClass().equals(Integer.class)){
			
			//lecture des enregistrements existants dans Android
			Object AndroidIds = manager.code.getAllIds();
			
			//suppression des listes n'exitant plus dans openERP
			Object Ids2Delete = DiffLists((List<Object>)AndroidIds, (List<Object>)ERPIds);
			
			for(Integer Id : (List<Integer>)Ids2Delete){
				manager.checkline.remove(Id);
				modifs.put(7, modifs.get(7)+1);
			}
			
			//insertion des list n'exitant pas dans Android
			Object Ids2Insert = DiffLists((List<Object>)ERPIds, (List<Object>)AndroidIds);
			
			if(!((List<Integer>)Ids2Insert).isEmpty()){
				//lecture des données correspondant à l'id
				Object ERPInsert = access.Read("intervention.codification", Ids2Insert, CodificationModel.listFields);
				
				//vérification du resultat de la lecture
				if(!ERPInsert.getClass().equals(Integer.class)){
					for(Object valueObj : (Object[])ERPInsert){
						HashMap<Object, Object> values = (HashMap<Object, Object>)valueObj;
						CodificationModel code  = new CodificationModel();
						code.setBaseId((Integer)values.get("id"));
						if(!values.get("parent_id").equals(false))
							code.setParentId((Integer)((Object[])values.get("parent_id"))[0]);
						code.setType((String)values.get("type"));
						code.setName((String)values.get("name"));
						//insertion de la codification
						manager.code.insert(code);
						modifs.put(8, modifs.get(8)+1);
					}
				}
			}
			
			//mise a jour des list en cas de modification dans OpenERP
			Object Ids2Update = EqualLists((List<Object>)ERPIds, (List<Object>)AndroidIds);
			
			if(!((List<Integer>)Ids2Update).isEmpty()){
				//lecture des données correspondant à l'id
				Object ERPUpdate = access.Read("intervention.codification", Ids2Update, CodificationModel.listFields);
				
				//vérification du resultat de la lecture
				if(!ERPUpdate.getClass().equals(Integer.class)){
					for(Object valueObj : (Object[])ERPUpdate){
						HashMap<Object, Object> valuesERP = (HashMap<Object, Object>)valueObj;
						CodificationModel valuesAndroid = manager.code.getWithBaseId((Integer) valuesERP.get(CodificationModel.listFields[0])).get(0);
					
						if(valuesERP.get("parent_id").equals(false))
							valuesERP.put("parent_id", new Object[] {0, ""});
						//si il y a eu des modifications
						if(!((Integer)valuesAndroid.getParentId()).equals(((Object[])valuesERP.get("parent_id"))[0]) ||
								!valuesAndroid.getType().equals(valuesERP.get("type")) ||
								!valuesAndroid.getName().equals(valuesERP.get("name"))){
							
							//modification de la codification
							valuesAndroid.setParentId((Integer)((Object[])valuesERP.get("parent_id"))[0]);
							valuesAndroid.setType((String)valuesERP.get("type"));
							valuesAndroid.setName((String)valuesERP.get("name"));
							manager.code.update((Integer) valuesERP.get(CodificationModel.listFields[0]), valuesAndroid);
							modifs.put(9, modifs.get(9)+1);
						}
					}
				}
			}
		}
		return modifs;
	}

	private static HashMap<Integer, Integer> synchroMachine(xmlrpcAccess access, DataBaseManager manager, Context context, List<Object> machine_ids, HashMap<Integer, Integer> modifs){
		
		//lecture des enregistrements existants dans Android
		Object AndroidIds = manager.machine.getAllIds();
		
		//suppression des listes n'exitant plus dans openERP
		Object Ids2Delete = DiffLists((List<Object>)AndroidIds, machine_ids);
		
		if(!((List<Object>)Ids2Delete).isEmpty()){
			for(Integer id : (List<Integer>)Ids2Delete )
			{
				manager.machine.remove(id);
				modifs.put(10, modifs.get(10));
			}
		}
		
		//insertion des list n'exitant pas dans Android
		Object Ids2Insert = DiffLists(machine_ids, (List<Object>)AndroidIds);
		
		if(!((List<Object>)Ids2Insert).isEmpty()){
			Object ERPInsert = access.Read("contract.machine", ((List<Object>)Ids2Insert), MachineModel.listFields);
		    
			//vérification du resultat de la lecture
			if(!ERPInsert.getClass().equals(Integer.class)){
				for(Object valueObj : (Object[])ERPInsert){
					HashMap<Object, Object> value = (HashMap<Object, Object>)valueObj;
					MachineModel machine = new MachineModel();
					
					machine.setBaseId((Integer)value.get(MachineModel.listFields[0]));
					machine.setName((String)value.get(MachineModel.listFields[2]));
					machine.setContractName((String)((Object[])value.get(MachineModel.listFields[3]))[1]);
					
					if(!value.get(MachineModel.listFields[4]).equals(false)){
						machine.setMachineAddress((String)((Object[])value.get(MachineModel.listFields[4]))[1]);
						Object address = access.Read("res.partner.address", (Integer)((Object[])value.get(MachineModel.listFields[4]))[0], new String[] {"zip", "city"});
						if(!address.getClass().equals(Integer.class)){
							if(!((HashMap<String, String>) address).get("zip").equals(false))
								machine.setZip(((HashMap<String, String>)address).get("zip"));
							if(!((HashMap<String, String>) address).get("city").equals(false))
								machine.setCity(((HashMap<String, String>)address).get("city"));
						}
					}
					else
						machine.setMachineAddress(context.getString(R.string.no_value_error));
					
					if(!value.get(MachineModel.listFields[5]).equals(false))
						machine.setCaretakerAddress((String)((Object[])value.get(MachineModel.listFields[4]))[1]);
					
					if(!value.get(MachineModel.listFields[6]).equals(false))
						machine.setCheckListId((Integer)((Object[])value.get(MachineModel.listFields[5]))[0]);
					
					if(!value.get(MachineModel.listFields[7]).equals(false))
						machine.setArea((String)((Object[])value.get(MachineModel.listFields[7]))[1]);
					
					if(!value.get(MachineModel.listFields[8]).equals(false))
						machine.setGenre((String)value.get(MachineModel.listFields[8]));
					
					Timestamp[] mnts = getMaintenances(machine.getBaseId(), access);
					
					if(mnts[0]!=null)
						machine.setLastInter(mnts[0]);
					
					if(mnts[1]!=null)
						machine.setNextInter(mnts[1]);
					
					machine.setBreakdownStop((Boolean)value.get(MachineModel.listFields[13]));
					
					manager.machine.insert(machine);
					modifs.put(11, modifs.get(11)+1);
				}
			}
		}
			
		//mise a jour des list en cas de modification dans OpenERP
		Object Ids2Update = EqualLists(machine_ids, (List<Object>)AndroidIds);
		
		if(!((List<Object>)Ids2Update).isEmpty()){
			Object ERPUpdate = access.Read("contract.machine", ((List<Object>)Ids2Update), MachineModel.listFields);
		    
			//vérification du resultat de la lecture
			if(!ERPUpdate.getClass().equals(Integer.class)){
				for(Object valueObj : (Object[])ERPUpdate){
					HashMap<Object, Object> value = (HashMap<Object, Object>)valueObj;
					
					String city = "";
					String zip = "";
					
					if(value.get(MachineModel.listFields[4]).equals(false))
						value.put(MachineModel.listFields[4], new Object[] {0, context.getString(R.string.no_value_error)});
					else{
						Object address = access.Read("res.partner.address", (Integer)((Object[])value.get(MachineModel.listFields[4]))[0], new String[] {"zip", "city"});
						if(!address.getClass().equals(Integer.class)){
							zip = ((HashMap<String, String>)address).get("zip");
							city = ((HashMap<String, String>)address).get("city");
						}
					}
					if(value.get(MachineModel.listFields[5]).equals(false))
						value.put(MachineModel.listFields[5], new Object[] {0, ""});
					if(value.get(MachineModel.listFields[6]).equals(false))
						value.put(MachineModel.listFields[6], new Object[] {0, ""});
					if(value.get(MachineModel.listFields[7]).equals(false))
						value.put(MachineModel.listFields[7], new Object[] {0, 0});
					if(value.get(MachineModel.listFields[8]).equals(false))
						value.put(MachineModel.listFields[8], "");
					
					MachineModel machine = (MachineModel) manager.machine.getWithBaseId((Integer)value.get(MachineModel.listFields[0])).get(0);
					
					Timestamp[] mnts = getMaintenances(machine.getBaseId(), access);
					
					//vérification de la valeur des champs pour ne mettre à jour que les valeurs qui ont changé
					if(!machine.getName().equals(value.get(MachineModel.listFields[2])) ||
							machine.getContactName().equals(((Object[])value.get(MachineModel.listFields[3]))[1]) ||
							!machine.getMachineAddress().equals(((Object[])value.get(MachineModel.listFields[4]))[1]) ||
							!machine.getCaretakerAddress().equals(((Object[])value.get(MachineModel.listFields[5]))[1]) ||
							machine.getCheckListId() != (Integer)((Object[])value.get(MachineModel.listFields[6]))[0] ||
							machine.getArea() != (String)((Object[])value.get(MachineModel.listFields[7]))[1] ||
							machine.getGenre() != (String)value.get(MachineModel.listFields[8]) ||
							!machine.getZip().equals(zip) || !machine.getCity().equals(city) || 
							(machine.getLastInter()!=null && mnts[0]!=null && !machine.getLastInter().equals(mnts[0])) ||
							(machine.getNextInter()!=null && mnts[1]!=null && !machine.getLastInter().equals(mnts[1])) ){
						
						//mise à jour des valeurs
						machine.setName((String)value.get(MachineModel.listFields[2]));
						machine.setContractName((String)((Object[])value.get(MachineModel.listFields[3]))[1]);
						machine.setMachineAddress((String)((Object[])value.get(MachineModel.listFields[4]))[1]);
						machine.setCaretakerAddress((String)((Object[])value.get(MachineModel.listFields[5]))[1]);
						machine.setCheckListId((Integer)((Object[])value.get(MachineModel.listFields[6]))[0]);
						machine.setArea((String)((Object[])value.get(MachineModel.listFields[7]))[1]);
						machine.setGenre((String)value.get(MachineModel.listFields[8]));
						machine.setZip(zip);
						machine.setCity(city);
						if(mnts[0]!=null)
							machine.setLastInter(mnts[0]);
						if(mnts[1]!=null)
							machine.setNextInter(mnts[1]);
						manager.machine.update(machine.getBaseId(), machine);
						modifs.put(12, modifs.get(12)+1);
					}
					
					if(!machine.getBreakdownStop().equals(value.get(MachineModel.listFields[13]))){
						HashMap<String, Object> newValue = new HashMap<String, Object>();
						
						newValue.put(MachineModel.listFields[13], machine.getBreakdownStop());
						
						@SuppressWarnings("unused")
						Object res = access.Write(MachineModel.modelName, machine.getBaseId(), newValue); 
					}
				}
			}
		}
		
		return modifs;
	}
	
	private static Timestamp[] getMaintenances(int machine_id, xmlrpcAccess access) {
		Timestamp last=null;
		Timestamp next=null;
		Object query = new  ArrayList<Object>();
		((ArrayList<Object>)query).add(CreateTuple("machine_id","=",machine_id));
		((ArrayList<Object>)query).add(CreateTuple("state","=","done"));
		((ArrayList<Object>)query).add(CreateTuple("type","=","maintenance"));
		
		Object last_ids = access.Search(InterventionModel.modelName, query);
		if(!last_ids.getClass().equals(Integer.class)){
			Object last_objs = access.Read(InterventionModel.modelName, last_ids, InterventionModel.listFields);
			if(!last_objs.getClass().equals(Integer.class)){
				for(Object inter : (Object[])last_objs){
					if(last == null || last.before(TableModel.stringToDate((String) ((HashMap<String, Object>)inter).get(InterventionModel.listFields[9]),false)))
						last = TableModel.stringToDate((String) ((HashMap<String, Object>)inter).get(InterventionModel.listFields[9]),false);
				}
			}
		}
		
		((ArrayList<Object>)query).set(1, CreateTuple("state","!=","done"));
		
		Object next_ids = access.Search(InterventionModel.modelName, query);
		if(!next_ids.getClass().equals(Integer.class)){
			Object next_objs = access.Read(InterventionModel.modelName, next_ids, InterventionModel.listFields);
			if(!next_objs.getClass().equals(Integer.class)){
				for(Object inter : (Object[])next_objs)
				if(next == null || next.after(TableModel.stringToDate((String) ((HashMap<String, Object>)inter).get(InterventionModel.listFields[22]), false))){
					next = TableModel.stringToDate((String) ((HashMap<String, Object>)inter).get(InterventionModel.listFields[22]), false);
				}
			}
		}
		
		return new Timestamp[] {last, next};
	}

	private static HashMap<Integer, Integer> synchroInterventions(xmlrpcAccess access, DataBaseManager manager,	Context context, HashMap<Integer, Integer> modifs) {
		List<Object> machine_ids = new ArrayList<Object>();
		Object query;
		Object empId = 0;
		
		//query = [["user_id","=", uid]]
		query = new  ArrayList<Object>();
		((ArrayList<Object>)query).add(CreateTuple("user_id","=",access.uid));
		
		//recherche de l'employee_id
		Object employeeIds = access.Search("hr.employee", query);
		
		//si on l'a trouvé
		if(!employeeIds.getClass().equals(Integer.class)){
			empId = ((List<Object>)employeeIds).get(0);
			
			//query = [["state","=","draft"],["employee_id", "=", empId]
			query = new ArrayList<Object>();
			((ArrayList<Object>)query).add(CreateTuple("state","!=","done"));
			((ArrayList<Object>)query).add(CreateTuple("employee_id", "=", empId));
			
			//recherche des interventions
			Object ERPIds = access.Search(InterventionModel.modelName, query);
			
			if(!ERPIds.getClass().equals(Integer.class)){
				
				//lecture des enregistrements existants dans Android
				Object AndroidIds = manager.intervention.getAllIds();
				
				//mise a jour des list en cas de modification dans OpenERP
				if(!((List<Object>)AndroidIds).isEmpty()){
					Object ERPUpdate = access.Read(InterventionModel.modelName, ((List<Object>)AndroidIds), InterventionModel.listFields);
					
					//vérification du resultat de la lecture
					if(!ERPUpdate.getClass().equals(Integer.class)){
						for(Object valueObj : (Object[])ERPUpdate){
							HashMap<Object, Object> value = (HashMap<Object, Object>)valueObj;
							
							InterventionModel inter = (InterventionModel)manager.intervention.getWithBaseId((Integer) value.get(InterventionModel.listFields[0])).get(0);
							
							if(value.get(InterventionModel.listFields[3]).equals(false))
								value.put(InterventionModel.listFields[3], new Object[] {0, ""});
							
							if(value.get(InterventionModel.listFields[4]).equals(false))
								value.put(InterventionModel.listFields[4], new Object[] {0, ""});
							
							if(value.get(InterventionModel.listFields[5]).equals(false))
								value.put(InterventionModel.listFields[5], new Object[] {0, ""});
							
							if(value.get(InterventionModel.listFields[6]).equals(false))
								value.put(InterventionModel.listFields[6], null);
								
							if(value.get(InterventionModel.listFields[8]).equals(false))
								value.put(InterventionModel.listFields[8], null);
							
							if(value.get(InterventionModel.listFields[9]).equals(false))
								value.put(InterventionModel.listFields[9], null);
							
							if(value.get(InterventionModel.listFields[10]).equals(false))
								value.put(InterventionModel.listFields[10], null);
							
							if(value.get(InterventionModel.listFields[11]).equals(false))
								value.put(InterventionModel.listFields[11], null);
							
							if(value.get(InterventionModel.listFields[15]).equals(false))
								value.put(InterventionModel.listFields[15], null);
							
							if(value.get(InterventionModel.listFields[16]).equals(false))
								value.put(InterventionModel.listFields[16], null);
							
							if(value.get(InterventionModel.listFields[23]).equals(false))
								value.put(InterventionModel.listFields[23], null);
							
							if(value.get(InterventionModel.listFields[24]).equals(false))
								value.put(InterventionModel.listFields[24], null);
							
							if(inter.getChapterId() != (Integer)((Object[])value.get(InterventionModel.listFields[3]))[0] ||
									inter.getLocalizationId() != (Integer)((Object[])value.get(InterventionModel.listFields[4]))[0] ||
									inter.getCauseId() != (Integer)((Object[])value.get(InterventionModel.listFields[5]))[0] ||
									!inter.getState().equals(value.get(InterventionModel.listFields[12])) ||
									!inter.getInvoiceable().equals(value.get(InterventionModel.listFields[17])) ||
									!inter.getParachute().equals(value.get(InterventionModel.listFields[20])) ||
									!inter.getCable().equals(value.get(InterventionModel.listFields[21])) ||
									(inter.getInterventionStart()!=null && !inter.getInterventionStart().equals(TableModel.stringToDate((String)value.get(InterventionModel.listFields[8]), false))) ||
									(inter.getInterventionEnd()!=null && !inter.getInterventionEnd().equals(TableModel.stringToDate((String)value.get(InterventionModel.listFields[9]), false))) ||
									(inter.getTakeIntoAccountDay()!=null && !inter.getTakeIntoAccountDay().equals(TableModel.stringToDate((String)value.get(InterventionModel.listFields[23]), false))) ||
									(inter.getSignatureName()!=null && !inter.getSignatureName().equals(value.get(InterventionModel.listFields[15]))) ||
									(inter.getSignaturePicture()!=null && !inter.getSignaturePicture().equals(value.get(InterventionModel.listFields[16])))){
								
								HashMap<String, Object> newValue = new HashMap<String, Object>();

								if(inter.getChapterId() != 0)
									newValue.put(InterventionModel.listFields[3],((Integer)inter.getChapterId()).toString());
								else
									newValue.put(InterventionModel.listFields[3], false);
								
								if(inter.getLocalizationId() != 0)
									newValue.put(InterventionModel.listFields[4],  ((Integer)inter.getLocalizationId()).toString());
								else
									newValue.put(InterventionModel.listFields[4], false);
								
								if(inter.getCauseId() != 0)
									newValue.put(InterventionModel.listFields[5], ((Integer)inter.getCauseId()));
								else
									newValue.put(InterventionModel.listFields[5], false);
								
								newValue.put(InterventionModel.listFields[12], inter.getState());
								newValue.put(InterventionModel.listFields[17], inter.getInvoiceable());
								newValue.put(InterventionModel.listFields[20], inter.getParachute());
								newValue.put(InterventionModel.listFields[21], inter.getCable());
								
								if(inter.getInterventionStart()!=null)
									newValue.put(InterventionModel.listFields[8], new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(inter.getInterventionStart()).toString());
								
								if(inter.getInterventionEnd()!=null)
									newValue.put(InterventionModel.listFields[9], new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(inter.getInterventionEnd()).toString());
								
								if(inter.getTakeIntoAccountDay()!=null)
									newValue.put(InterventionModel.listFields[23], new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(inter.getTakeIntoAccountDay()).toString());
								
								if(inter.getSignatureName()!=null)
									newValue.put(InterventionModel.listFields[15], inter.getSignatureName());
								
								if(inter.getSignaturePicture()!=null)
									newValue.put(InterventionModel.listFields[16], Base64.encodeToString(inter.getSignaturePicture(), Base64.DEFAULT));

								Object res = access.Write(InterventionModel.modelName, inter.getBaseId(), newValue); 
								
								if(res.toString().equals("true")){
																		
									if(inter.getState().equals(context.getResources().getResourceEntryName(R.string.done))){
										manager.intervention.remove(inter.getBaseId());
										modifs.put(13, modifs.get(13)+1);
									}
									else {
										modifs.put(15, modifs.get(15)+1);
										
										if(!machine_ids.contains(inter.getMachineId()))
											machine_ids.add(inter.getMachineId());
									}
								}
								else if(!machine_ids.contains(inter.getMachineId()))
									machine_ids.add(inter.getMachineId());
							}
							else if(!machine_ids.contains(inter.getMachineId()))
									machine_ids.add(inter.getMachineId());
							
							if(inter != null && (
									(value.get(InterventionModel.listFields[6]) != null && !value.get(InterventionModel.listFields[6]).equals(inter.getInformation())) ||
									(value.get(InterventionModel.listFields[10]) != null && !value.get(InterventionModel.listFields[10]).equals(inter.getRequestorName())) ||
									(value.get(InterventionModel.listFields[11]) != null && !value.get(InterventionModel.listFields[11]).equals(inter.getRequestorPhone())) ||
									(value.get(InterventionModel.listFields[24]) != null && !value.get(InterventionModel.listFields[24]).equals(inter.getLastIncidents())) ) ){
								
								inter.setInformation((String) value.get(InterventionModel.listFields[6]));
								inter.setRequestorName((String) value.get(InterventionModel.listFields[10]));
								inter.setRequestorPhone((String) value.get(InterventionModel.listFields[11]));
								inter.setLastIncidents((String) value.get(InterventionModel.listFields[24]));
								
								manager.intervention.update(inter.getBaseId(), inter);
							}
						}
					}
				}
				
				//insertion des interventions n'exitant pas dans Android
				Object Ids2Insert = DiffLists((List<Object>)ERPIds, (List<Object>)AndroidIds);
				
				if(!((List<Object>)Ids2Insert).isEmpty()){
					Object ERPInsert = access.Read(InterventionModel.modelName, ((List<Object>)Ids2Insert), InterventionModel.listFields);
					
					//vérification du resultat de la lecture
					if(!ERPInsert.getClass().equals(Integer.class)){
						for(Object valueObj : (Object[])ERPInsert){
							HashMap<Object, Object> value = (HashMap<Object, Object>)valueObj;
							
							InterventionModel inter = new InterventionModel();
							
							inter.setBaseId((Integer) value.get(InterventionModel.listFields[0]));
							inter.setName((String) value.get(InterventionModel.listFields[2]));
							
							if(!value.get(InterventionModel.listFields[3]).equals(false))
								inter.setChapterId((Integer)((Object[])value.get(InterventionModel.listFields[3]))[0]);
							
							if(!value.get(InterventionModel.listFields[4]).equals(false))
								inter.setLocalizationId((Integer)((Object[])value.get(InterventionModel.listFields[4]))[0]);
							
							if(!value.get(InterventionModel.listFields[5]).equals(false))
								inter.setCauseId((Integer)((Object[])value.get(InterventionModel.listFields[5]))[0]);
							
							if(!value.get(InterventionModel.listFields[6]).equals(false))
								inter.setInformation((String) value.get(InterventionModel.listFields[6]));
							
							if(!value.get(InterventionModel.listFields[7]).equals(false))
								inter.setCallDay(TableModel.stringToDate((String)value.get(InterventionModel.listFields[7]), false));
							
							if(!value.get(InterventionModel.listFields[8]).equals(false))
								inter.setInterventionStart(TableModel.stringToDate((String)value.get(InterventionModel.listFields[8]), false));
							
							if(!value.get(InterventionModel.listFields[9]).equals(false))
								inter.setInterventionEnd(TableModel.stringToDate((String)value.get(InterventionModel.listFields[9]), false));
							
							if(!value.get(InterventionModel.listFields[10]).equals(false))
								inter.setRequestorName((String) value.get(InterventionModel.listFields[10]));
							
							if(!value.get(InterventionModel.listFields[11]).equals(false))
								inter.setRequestorPhone((String) value.get(InterventionModel.listFields[11]));
							
							inter.setState((String) value.get(InterventionModel.listFields[12]));
							inter.setType((String) value.get(InterventionModel.listFields[13]));
							inter.setSomeone((Boolean) value.get(InterventionModel.listFields[14]));
							
							if(!value.get(InterventionModel.listFields[15]).equals(false))
								inter.setSignatureName((String) value.get(InterventionModel.listFields[15]));
							
							if(!value.get(InterventionModel.listFields[16]).equals(false))
								inter.setSignaturePicture(Base64.decode(value.get(InterventionModel.listFields[16]).toString(), Base64.DEFAULT));
							
							inter.setInvoiceable((Boolean) value.get(InterventionModel.listFields[17]));
							inter.setMachineId((Integer) ((Object[])value.get(InterventionModel.listFields[18]))[0]);
							inter.setMachineName((String) ((Object[])value.get(InterventionModel.listFields[18]))[1]);
							inter.setParachute((Boolean) value.get(InterventionModel.listFields[20]));
							inter.setCable((Boolean) value.get(InterventionModel.listFields[21]));
							
							if(!value.get(InterventionModel.listFields[22]).equals(false))
								inter.setMaintenanceDeadline(TableModel.stringToDate((String)value.get(InterventionModel.listFields[22]), false));
							
							if(!value.get(InterventionModel.listFields[23]).equals(false))
								inter.setTakeIntoAccountDay(TableModel.stringToDate((String)value.get(InterventionModel.listFields[23]), false));
							
							if(!value.get(InterventionModel.listFields[24]).equals(false))
								inter.setLastIncidents((String) value.get(InterventionModel.listFields[24]));
							
							manager.intervention.insert(inter);
							if(!machine_ids.contains(inter.getMachineId()))
								machine_ids.add(inter.getMachineId());
							modifs.put(14, modifs.get(14)+1);
							
						}
					}
				}
				
				modifs = synchroMachine(access, manager, context, machine_ids, modifs);
			}
		}
		return modifs;
	}
	
  	private static Object CreateTuple(Object field, Object comp, Object value) {
		ArrayList<Object> res = new ArrayList<Object>();
		res.add(field);
		res.add(comp);
		res.add(value);
		return res;
	}

	private static List<Object> DiffLists(List<Object> Ids2find, List<Object> Ids2search) {
		List<Object> res = new ArrayList<Object>();
		
		for (Object find  : Ids2find) {
			if(!Ids2search.contains(find))
				res.add(find);
		}
		
		return res;
	}
	
	private static List<Object> EqualLists(List<Object> Ids2find, List<Object>Ids2search){
		List<Object> res = new ArrayList<Object>();
		
		for (Object find  : Ids2find) {
			if(Ids2search.contains(find))
				res.add(find);
		}
		
		return res;
	}
}