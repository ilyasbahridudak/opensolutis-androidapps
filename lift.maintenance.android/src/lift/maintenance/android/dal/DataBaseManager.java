package lift.maintenance.android.dal;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DataBaseManager {
	
	private SQLiteDatabase bdd;
	private DataBaseCreator base;
	private static int version = 12;
	
	private static final String lift_mnt_bdd = "lift_maintenance.db";
	
	public CheckListManager checklist;
	public CheckLineManager checkline;
	public CodificationManager code;
	public MachineManager machine; 
	public InterventionManager intervention; 
	
	//création de la base (ou mise à jour)
  	public DataBaseManager(Context context){
		base = new DataBaseCreator(context, lift_mnt_bdd, null, version);
		checklist = new CheckListManager();
		checkline = new CheckLineManager();
		code = new CodificationManager();
		machine = new MachineManager();
		intervention = new InterventionManager();
	}
	
	//on ouvre la BDD en écriture
	public void open(){
		bdd = base.getWritableDatabase();
		checklist.bdd = bdd;
		checkline.bdd = bdd;
		code.bdd = bdd;
		machine.bdd = bdd;
		intervention.bdd = bdd;
	}
	
	//on ferme l'accès à la BDD
	public void close(){
		bdd.close();
		checklist.bdd = null;
		checkline.bdd = null;
		code.bdd = null;
		machine.bdd = null;
		intervention.bdd = null;
	}
 
	//renvoi de la base de donnée
	public SQLiteDatabase getBDD(){
		return bdd;
	}
	
	//gestion de la table check list
	public class CheckListManager{
		public SQLiteDatabase bdd;
		
		public CheckListManager(){
			bdd=null;
		}
		
		public long insert(CheckListModel chklst){
			ContentValues values = new ContentValues();
			//on lui ajoute une valeur associé à une clé (qui est le nom de la colonne dans laquelle on veut mettre la valeur)
			values.put(CheckListModel.listFields[1], chklst.getBaseId());
			values.put(CheckListModel.listFields[2], chklst.getName());
			//on insère l'objet dans la BDD via le ContentValues
			return bdd.insert(CheckListModel.tableName, null, values);
		}
		
		public int update(int id, CheckListModel chklst){
			//La mise à jour d'un livre dans la BDD fonctionne plus ou moins comme une insertion
			//il faut simple préciser quelle livre on doit mettre à jour grâce à l'ID
			ContentValues values = new ContentValues();
			values.put(CheckListModel.listFields[1], chklst.getBaseId());
			values.put(CheckListModel.listFields[2], chklst.getName());
			return bdd.update(CheckListModel.tableName, values, CheckListModel.listFields[1] + " = " +id, null);
		}
		
		public int remove(int id){
			//Suppression d'un livre de la BDD grâce à l'ID
			return bdd.delete(CheckListModel.tableName, CheckListModel.listFields[1] + " = " +id, null);
		}
		
		public List<CheckListModel> getWithBaseID(Integer base_id){
			Cursor c = bdd.query(CheckListModel.tableName, CheckListModel.listFields, CheckListModel.listFields[1] + " = " + base_id.toString(), null, null, null, null);
			return cursorToCheckList(c);
		}

		public List<CheckListModel> getAll(){
			Cursor c = bdd.query(CheckListModel.tableName, CheckListModel.listFields, null, null, null, null, null);
			return cursorToCheckList(c);
		}
		
		public Object getAllIds(){
			List<Object> list = new ArrayList<Object>();
			List<CheckListModel> models = getAll();
			
			for (CheckListModel chk : models) {
				list.add((Integer)chk.getBaseId());
			}
			
			return list;
		}

		private List<CheckListModel> cursorToCheckList(Cursor c) {
			List<CheckListModel> lsts = new ArrayList<CheckListModel>();
			c.moveToFirst();
			
			for(int i=0; i<c.getCount();i++){
				CheckListModel lst = new CheckListModel();
				lst.setId(c.getInt(0));
				lst.setBaseId(c.getInt(1));
				lst.setName(c.getString(2));
				lsts.add(lst);
				c.moveToNext();
			}
			c.close();
			return lsts;
		}
	}
	
	//gestion de la table check line
	public class CheckLineManager{
		public SQLiteDatabase bdd;
		
		public long insert(CheckLineModel chklne){
			ContentValues values = new ContentValues();
			values.put(CheckLineModel.listFields[1], chklne.getBaseId());
			values.put(CheckLineModel.listFields[2], chklne.getName());
			values.put(CheckLineModel.listFields[3], chklne.getFrequency());
			values.put(CheckLineModel.listFields[4], chklne.getListId());
			return bdd.insert(CheckLineModel.tableName, null, values);
		}
		
		public int update(int id, CheckLineModel chklne){
			ContentValues values = new ContentValues();
			values.put(CheckLineModel.listFields[1], chklne.getBaseId());
			values.put(CheckLineModel.listFields[2], chklne.getName());
			values.put(CheckLineModel.listFields[3], chklne.getFrequency());
			values.put(CheckLineModel.listFields[4], chklne.getListId());
			return bdd.update(CheckLineModel.tableName, values, CheckLineModel.listFields[1] + " = " + id, null);
		}
		
		public int remove(int id){
			return bdd.delete(CheckLineModel.tableName, CheckLineModel.listFields[1] + " = " + id, null);
		}
		
		public List<CheckLineModel> getWithBaseId(Integer id){
			Cursor c  = bdd.query(CheckLineModel.tableName, CheckLineModel.listFields, CheckLineModel.listFields[1] + " = " + id.toString(), null, null, null, null);
			return cursorToCheckLine(c);
		}

		public List<CheckLineModel> getWithListId(Integer id){
			Cursor c = bdd.query(CheckLineModel.tableName, CheckLineModel.listFields, CheckLineModel.listFields[4] + " = " + id.toString(), null, null, null, null);
			return cursorToCheckLine(c);
		}
		
		public List<CheckLineModel> getAll(){
			Cursor c = bdd.query(CheckLineModel.tableName, CheckLineModel.listFields, null, null, null, null, null);
			return cursorToCheckLine(c);
		}
		
		public Object getAllIds(Integer list_id){
			List<Object> list = new ArrayList<Object>();
			List<CheckLineModel> models;
			
			if(list_id > 0){
				models = getWithListId(list_id);
			}
			else{
				models = getAll();
			}
			
			for (CheckLineModel chk : models) {
				list.add((Integer)chk.getBaseId());
			}
			
			return list;
		}
		
		private List<CheckLineModel> cursorToCheckLine(Cursor c) {
			List<CheckLineModel> lnes = new ArrayList<CheckLineModel>();
			c.moveToFirst();
			
			for(int i=0; i<c.getCount();i++){
				CheckLineModel lne = new CheckLineModel();
				lne.setId(c.getInt(0));
				lne.setBaseId(c.getInt(1));
				lne.setName(c.getString(2));
				lne.setFrequency(c.getString(3));
				lne.setListId(c.getInt(4));
				lnes.add(lne);
				c.moveToNext();
			}
			c.close();
			return lnes;
		}
	}
	
	//gestion des codifications de pannes
	public class CodificationManager{
		public SQLiteDatabase bdd;
		
		public CodificationManager(){
			bdd = null;
		}
		
		public long insert(CodificationModel code){
			ContentValues values = new ContentValues();
			values.put(CodificationModel.listFields[1], code.getBaseId());
			values.put(CodificationModel.listFields[2], code.getParentId());
			values.put(CodificationModel.listFields[3], code.getType());
			values.put(CodificationModel.listFields[4], code.getName());
			return bdd.insert(CodificationModel.tableName, null, values);
		}
		
		public int update(int id, CodificationModel code){
			ContentValues values = new ContentValues();
			values.put(CodificationModel.listFields[1], code.getBaseId());
			values.put(CodificationModel.listFields[2], code.getParentId());
			values.put(CodificationModel.listFields[3], code.getType());
			values.put(CodificationModel.listFields[4], code.getName());
			return bdd.update(CodificationModel.tableName, values, CodificationModel.listFields[1] + "=" + id, null);
		}
		
		public int remove(int id){
			return bdd.delete(CodificationModel.tableName, CodificationModel.listFields[1] + "=" + id , null);
		}
		
		public List<CodificationModel> getWithBaseId(Integer Id){
			Cursor c = bdd.query(CodificationModel.tableName, CodificationModel.listFields, CodificationModel.listFields[1] + "=" + Id, null, null, null, null);
			return cursorToCodification(c);
		}
		
		public List<CodificationModel> getCauses(){
			Cursor c = bdd.query(CodificationModel.tableName, CodificationModel.listFields, CodificationModel.listFields[3] + "= 't1'", null, null, null, CodificationModel.listFields[4]);
			return cursorToCodification(c);
		}
		
		public List<CodificationModel> getLocalisation(int chapterId){
			Cursor c = bdd.query(CodificationModel.tableName, CodificationModel.listFields, CodificationModel.listFields[3] + "='t2' and " +
					   																			   CodificationModel.listFields[2] + "="+chapterId, null, null, null, CodificationModel.listFields[4]);
			return cursorToCodification(c);
		}
		
		public List<CodificationModel> getChapters(){
			Cursor c = bdd.query(CodificationModel.tableName, CodificationModel.listFields, CodificationModel.listFields[3] + "= 't2' and " +
																								   CodificationModel.listFields[2] + " = 0", null, null, null, CodificationModel.listFields[4]);
			return cursorToCodification(c);
		}
		
		public List<CodificationModel> getAll(){
			Cursor c = bdd.query(CodificationModel.tableName, CodificationModel.listFields, null, null, null, null, null);
			return cursorToCodification(c);
		}
		
		public Object getAllIds(){
			List<Object> list = new ArrayList<Object>();
			List<CodificationModel> codes = getAll();
			
			for (CodificationModel cde : codes) {
				list.add((Integer)cde.getBaseId());
			}
			
			return list;
		}
		
		private List<CodificationModel> cursorToCodification(Cursor c) {
			List<CodificationModel> codes = new ArrayList<CodificationModel>();
			c.moveToFirst();
			
			for(int i=0; i<c.getCount();i++){
				CodificationModel code = new CodificationModel();
				code.setId(c.getInt(0));
				code.setBaseId(c.getInt(1));
				code.setParentId(c.getInt(2));
				code.setType(c.getString(3));
				code.setName(c.getString(4));
				codes.add(code);
				c.moveToNext();
			}
			c.close();
			return codes;
		}
	}
}
