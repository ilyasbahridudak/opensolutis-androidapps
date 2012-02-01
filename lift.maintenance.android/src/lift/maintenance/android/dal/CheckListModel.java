package lift.maintenance.android.dal;

public class CheckListModel {
	 
	private int id;
	private int base_id;
	private String name;
	
	public static final String modelName = "contract.check.list";
	public static final String tableName = modelName.replace(".", "_");
	public static String[] listFields = new String[] {"id","base_id","name"};
 
	public CheckListModel(){}
 
	public CheckListModel(int base_id, String name){
		this.base_id = base_id;
		this.name = name;
	}
 
	public int getId() {
		return id;
	}
 
	public void setId(int id) {
		this.id = id;
	}
 
	public int getBaseId() {
		return base_id;
	}
 
	public void setBaseId(int base_id) {
		this.base_id = base_id;
	}
 
	public String getName() {
		return name;
	}
 
	public void setName(String name) {
		this.name = name;
	}
 
	public String toString(){
		return "id : "+id+", base_id : "+base_id+", name : "+name;
	}

}